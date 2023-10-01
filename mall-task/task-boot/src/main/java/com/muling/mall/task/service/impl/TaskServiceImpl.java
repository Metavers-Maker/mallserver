package com.muling.mall.task.service.impl;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.task.constant.TaskConstants;
import com.muling.mall.task.enums.TaskTypeEnum;
import com.muling.mall.task.event.TaskCheckSuccessEvent;
import com.muling.mall.task.pojo.entity.TaskConfig;
import com.muling.mall.task.pojo.entity.TaskMember;
import com.muling.mall.task.pojo.form.app.SubmitTaskItemForm;
import com.muling.mall.task.service.ITaskConfigService;
import com.muling.mall.task.service.ITaskMemberService;
import com.muling.mall.task.service.ITaskService;
import com.muling.mall.ums.api.MemberInviteFeignClient;
import com.muling.mall.ums.pojo.dto.MemberInviteDTO;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements ITaskService {

    private final RedissonClient redissonClient;

    private final RabbitTemplate rabbitTemplate;

    private final ITaskConfigService taskConfigService;

    private final ITaskMemberService taskMemberService;

    private final WalletFeignClient walletFeignClient;

    private final MemberInviteFeignClient memberInviteFeignClient;


    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean draw(Long taskId) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(TaskConstants.TASK_DRAW_PREFIX + taskId);

        try {
            lock.lock();
            TaskConfig taskConfig = taskConfigService.getById(taskId);
            if (taskConfig == null) {
                throw new BizException(ResultCode.REQUEST_INVALID, "任务不存在");
            }
//            if (taskConfig.getTaskType() != TaskTypeEnum.NOR_TYPE.getValue()) {
//                throw new BizException(ResultCode.REQUEST_INVALID, "子任务不能领取");
//            }
            Integer totalNum = taskConfig.getTotalNum();
            Integer usedNum = taskConfig.getUsedNum();
            if (totalNum.intValue() <= usedNum.intValue()) {
                throw new BizException(ResultCode.REQUEST_INVALID, "任务已领完");
            }

            if (taskConfig.getCostCoinValue().compareTo(BigDecimal.ZERO) > 0) {
                WalletDTO walletDTO = new WalletDTO();
                walletDTO.setMemberId(memberId);
                walletDTO.setCoinType(taskConfig.getCostCoinType());
                walletDTO.setBalance(taskConfig.getCostCoinValue().negate());
                walletDTO.setOpType(WalletOpTypeEnum.MISSION_COMPLETE_COST.getValue());
                walletDTO.setRemark(WalletOpTypeEnum.MISSION_COMPLETE_COST.getLabel());
                walletFeignClient.updateBalance(walletDTO);
            }

            TaskMember taskMember = new TaskMember();
            taskMember.setMemberId(memberId);
//            taskMember.setTaskType(TaskTypeEnum.NOR_TYPE.getValue());
            taskMember.setName(taskConfig.getName());
            boolean save = taskMemberService.save(taskMember);

            //加1
            usedNum = usedNum + 1;
            taskConfig.setUsedNum(usedNum);

            taskConfigService.updateById(taskConfig);

            log.info("领取任务:{}", JSONUtil.toJsonStr(taskMember));

            return save;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submit(SubmitTaskItemForm taskItemForm) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(TaskConstants.TASK_SUBMIT_PREFIX + memberId);

        try {
            lock.lock();
            Long taskId = taskItemForm.getTaskId();
            TaskConfig taskConfig = taskConfigService.getById(taskId);
            if (taskConfig == null) {
                throw new BizException(ResultCode.REQUEST_INVALID, "任务不存在");
            }
            if (taskConfig.getParentId() == 0) {
                throw new BizException(ResultCode.REQUEST_INVALID, "主任务不能提交");
            }
            Long parentId = taskConfig.getParentId();
            //判断是否已领取主任务
            boolean exist = taskMemberService.exist(memberId, parentId);

            if (!exist) {
                throw new BizException(ResultCode.REQUEST_INVALID, "主任务未领取");
            }

            TaskMember taskMember = taskMemberService.get(memberId, taskId);
            boolean success = false;
            //提交审核
            if (taskMember == null) {
                taskMember = new TaskMember();
                taskMember.setTaskId(taskId);
//                taskMember.setTaskType(TaskTypeEnum.NOR_TYPE.getValue());
                taskMember.setMemberId(memberId);
                taskMember.setExt(taskItemForm.getExt());
                success = taskMemberService.save(taskMember);
            } else {
                if (taskMember.getStatus().intValue() == 2) {
                    throw new BizException(ResultCode.REQUEST_INVALID, "任务已成功，不能重复提交");
                } else {
                    //重新提交审核
                    taskMember.setStatus(0);
                    taskMember.setExt(taskItemForm.getExt());
                    success = taskMemberService.updateById(taskMember);
                }
            }

            log.info("提交审核:{}", JSONUtil.toJsonStr(taskMember));

            return success;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean check(Long taskMemberId, Integer status) {
        RLock lock = redissonClient.getLock(TaskConstants.TASK_SUBMIT_PREFIX + taskMemberId);

        try {
            lock.lock();
            TaskMember taskMember = taskMemberService.getById(taskMemberId);
            if (taskMember == null) {
                throw new BizException(ResultCode.REQUEST_INVALID, "任务不存在");
            }
            if (taskMember.getStatus() == 2) {
                throw new BizException(ResultCode.REQUEST_INVALID, "任务已成功，不能重复提交");
            }

            taskMember.setStatus(status);
            boolean success = taskMemberService.updateById(taskMember);

            //审核成功
            if (status == 2) {
                TaskConfig taskConfig = taskConfigService.getById(taskMember.getTaskId());
                if (taskConfig.getRewardCoinValue().compareTo(BigDecimal.ZERO) > 0) {
                    WalletDTO walletDTO = new WalletDTO()
                            .setMemberId(taskMember.getMemberId())
                            .setBalance(taskConfig.getRewardCoinValue())
                            .setCoinType(taskConfig.getRewardCoinType())
                            .setOpType(WalletOpTypeEnum.MISSION_COMPLETE_REWARD.getValue())
                            .setRemark(WalletOpTypeEnum.MISSION_COMPLETE_REWARD.getLabel());
                    walletFeignClient.updateBalance(walletDTO);
                }
            }

            //后续逻辑
            TaskCheckSuccessEvent event = new TaskCheckSuccessEvent();
            event.setTaskMemberId(taskMemberId);
            event.setTaskId(taskMember.getTaskId());
            event.setMemberId(taskMember.getMemberId());
            rabbitTemplate.convertAndSend(GlobalConstants.MQ_TASK_CHECK_SUCCESS_EXCHANGE, GlobalConstants.MQ_TASK_CHECK_SUCCESS_KEY, event);

            log.info("审核:{}", JSONUtil.toJsonStr(taskMember));
            return success;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean checkSuccess(TaskCheckSuccessEvent event) {

        Result<List<MemberInviteDTO>> memberInvitesResult = memberInviteFeignClient.getMemberInviteDepthById(event.getMemberId());
        List<MemberInviteDTO> memberInviteList = memberInvitesResult.getData();
        TaskConfig taskConfig = taskConfigService.getById(event.getTaskId());
        return true;
    }
}

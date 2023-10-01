package com.muling.mall.farm.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.DateUtils;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.api.ItemFeignClient;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.farm.constant.FarmConstants;
import com.muling.mall.farm.converter.FarmMemberConverter;
import com.muling.mall.farm.converter.FarmMemberItemConverter;
import com.muling.mall.farm.enums.FarmOpEnum;
import com.muling.mall.farm.pojo.dto.FarmRakeDTO;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.query.app.FarmMemberItemPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmMemberItemVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;
import com.muling.mall.farm.service.*;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.api.MemberInviteFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberInviteDTO;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmServiceImpl implements IFarmService {

    private final ItemFeignClient itemFeignClient;

    private final IFarmConfigService farmConfigService;

    private final IFarmMemberItemService farmMemberItemService;

    private final IFarmBagConfigService farmBagConfigService;

    private final WalletFeignClient walletFeignClient;

    private final RedissonClient redissonClient;

    private final IFarmMemberService farmMemberService;

    private final IFarmMemberLogService memberLogService;

    private final IFarmRakeService farmRakeService;

    private final RabbitTemplate rabbitTemplate;

    private final MemberInviteFeignClient memberInviteFeignClient;

    private final MemberFeignClient memberFeignClient;

    private final IFarmAdService farmAdService;

    @Override
    public FarmMemberVO get(Long farmId) {
        Long memberId = MemberUtils.getMemberId();
        FarmMember farmMember = farmMemberService.getByMemberId(memberId, farmId);
        FarmMemberVO farmMemberVO = FarmMemberConverter.INSTANCE.po2vo(farmMember);
        return farmMemberVO;
    }

    //获取农场列表
    public Page<FarmMember> pageFarm(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FarmMember> wrapper = Wrappers.<FarmMember>lambdaQuery()
                .eq(FarmMember::getStatus,0)
                .orderByDesc(FarmMember::getCreated);
        Page<FarmMember> page = farmMemberService.page(new Page(pageNum, pageSize), wrapper);
        return page;
    }

    @Override
    public Page<FarmMemberItemVO> page(FarmMemberItemPageQuery queryParams) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<FarmMemberItem> wrapper = Wrappers.<FarmMemberItem>lambdaQuery()
                .eq(FarmMemberItem::getMemberId, memberId)
                .ne(FarmMemberItem::getStatus, 2)
                .orderByDesc(FarmMemberItem::getUpdated)
                .orderByDesc(FarmMemberItem::getCreated);
        Page<FarmMemberItem> page = farmMemberItemService.page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);
        Page<FarmMemberItemVO> result = FarmMemberItemConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Override
    public Page<FarmMemberItem> closeFarmBagPage(Integer pageNum, Integer pageSize) {
        //获取状态为0，且到期的工作包（未关闭的工作包）
        LambdaQueryWrapper<FarmMemberItem> queryWrapper = new LambdaQueryWrapper<FarmMemberItem>()
                .eq(FarmMemberItem::getStatus, 0)
                .le(FarmMemberItem::getClosed, LocalDateTime.now());
        Page<FarmMemberItem> page = farmMemberItemService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return page;
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void create(Long itemId) {

        FarmMemberItem farmMemberItem = farmMemberItemService.getByItemId(itemId);
        if (farmMemberItem != null) {
            return;
        }
        Result<MemberItemDTO> itemInfo = itemFeignClient.getItemInfo(itemId);
        if (Result.isFailed(itemInfo)) {
            return;
        }
        MemberItemDTO memberItemDTO = itemInfo.getData();
        FarmBagConfig farmBagConfig = farmBagConfigService.getBySpuId(memberItemDTO.getSpuId());
        if (farmBagConfig == null) {
            return;
        }

        FarmConfig farmConfig = farmConfigService.getById(farmBagConfig.getFarmId());

        farmMemberService.create(memberItemDTO, farmConfig, farmBagConfig);
        if (farmBagConfig.getClaimCoinValueExt().compareTo(BigDecimal.ZERO) > 0) {
            //荣誉值
            WalletDTO walletDTO = new WalletDTO()
                    .setMemberId(memberItemDTO.getMemberId())
                    .setBalance(farmBagConfig.getClaimCoinValueExt())
                    .setCoinType(farmConfig.getClaimCoinTypeExt())
                    .setOpType(WalletOpTypeEnum.FARM_BAG_BUY_REWARD.getValue())
                    .setRemark(WalletOpTypeEnum.FARM_BAG_BUY_REWARD.getLabel());
            walletFeignClient.updateBalance(walletDTO);
        }

        if (farmBagConfig.getActiveValue().compareTo(BigDecimal.ZERO) > 0) {
            //工作包购买奖励
            sendActiveValue(memberItemDTO.getMemberId(), farmBagConfig.getActiveValue(), farmBagConfig.getRakeBackActiveValue());
        }
    }

    public boolean open(Long farmId) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(FarmConstants.FARM_OPEN_PREFIX + memberId);
        try {
            lock.lock();
            FarmMember farmMember = farmMemberService.getByMemberId(memberId, farmId);
            if (farmMember == null) {
                throw new BizException(ResultCode.REQUEST_INVALID, "农场不存在");
            }
            Assert.isTrue(farmMember.getStatus().intValue() ==0,"农场已关闭");
            FarmConfig farmConfig = farmConfigService.getById(farmId);
//            Assert.isTrue(farmAdService.isComplete(farmConfig.getMaxNum()) == true,"广告任务未完成");
            //获取当前时间
            Date now = DateUtils.localDateTimeToDate(LocalDateTime.now());
            Date allowClaimed = DateUtils.localDateTimeToDate(farmMember.getAllowClaimed());
            if (allowClaimed != null) {
                boolean sameDay = DateUtil.isSameDay(allowClaimed, now);
                if (sameDay) {
                    log.info("工作包已经开启-异常", JSONUtil.toJsonStr(farmMember));
                    throw new BizException(ResultCode.REQUEST_INVALID, "工作包已经开启:");
                }
            }
            //开启农场
            boolean open = farmMemberService.open(farmMember, farmConfig);
            if (open) {
                log.info("开启农场:{}", JSONUtil.toJsonStr(farmMember));
            }
            return open;
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

    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean claim(Long farmId) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(FarmConstants.FARM_CLAIM_PREFIX + memberId);
        try {
            lock.lock();
            FarmMember farmMember = farmMemberService.getByMemberId(memberId, farmId);
            if (farmMember == null) {
                throw new BizException(ResultCode.REQUEST_INVALID, "工作包不存在");
            }
            LocalDateTime localDateTime = LocalDateTime.now();
            Date now = DateUtils.localDateTimeToDate(localDateTime);
            Date claim = DateUtils.localDateTimeToDate(farmMember.getClaimed());
            Date allowClaimed = DateUtils.localDateTimeToDate(farmMember.getAllowClaimed());
            Date end = DateUtils.localDateTimeToDate(LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX));
            if (allowClaimed == null) {
                throw new BizException(ResultCode.REQUEST_INVALID, "不允许领取奖励");
            }
            //激励任务没有完成，需要判断是否在有效时间内
            FarmConfig farmConfig = farmConfigService.getById(farmId);
            if(farmAdService.isComplete(farmConfig.getMaxNum()) == false) {
                //判断是否在领取时间
                boolean allow = DateUtil.isIn(now, allowClaimed, end);
                if (!allow) {
                    throw new BizException(ResultCode.REQUEST_INVALID, "不在领取时间");
                }
            }
            //
            BigDecimal claimCoinValue = farmMember.getClaimCoinValue();
            if (claimCoinValue.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException(ResultCode.REQUEST_INVALID, "奖励为0，不能领取");
            }
            //判断是否领取过奖励
            if (claim != null) {
                boolean sameDay = DateUtil.isSameDay(now, claim);
                if (sameDay) {
                    throw new BizException(ResultCode.REQUEST_INVALID, "已领取");
                }
            }
            //获取上级烧伤码
            Integer parentBurnCode = 0;
            Result<MemberInviteDTO> memberInviteDTOResult = memberInviteFeignClient.getMemberInviteById(farmMember.getMemberId());
            if (memberInviteDTOResult.getData()!=null) {
                FarmMember parentFarm = farmMemberService.getByMemberId(memberInviteDTOResult.getData().getInviteMemberId(),farmId);
                if (parentFarm!=null) {
                    parentBurnCode = parentFarm.getBurnCode();
                }
            }
            //更新
            boolean f = farmMemberService.claim(farmMember,parentBurnCode);
            if (f) {
                Result<MemberInviteDTO> memberInvite = memberInviteFeignClient.getMemberInviteById(memberId);
                MemberInviteDTO memberInviteDTO = memberInvite.getData();
                Long inviteMemberId = memberInviteDTO.getInviteMemberId();
                List<WalletDTO> list = Lists.newArrayList();
                if (inviteMemberId != -1 && farmMember.getRakeBackCoinValue().compareTo(BigDecimal.valueOf(0.0))>0) {
                    //记录返佣荣誉值和建设值
                    FarmRakeDTO farmRakeDTO0 = new FarmRakeDTO();
                    farmRakeDTO0.setMemberId(memberId);
                    farmRakeDTO0.setTargetId(inviteMemberId);
                    farmRakeDTO0.setCoinType(farmMember.getRakeBackCoinType());
                    farmRakeDTO0.setCoinValue(farmMember.getRakeBackCoinValue());
                    farmRakeService.create(farmRakeDTO0);
                    //
                    FarmRakeDTO farmRakeDTO1 = new FarmRakeDTO();
                    farmRakeDTO1.setMemberId(memberId);
                    farmRakeDTO1.setTargetId(inviteMemberId);
                    farmRakeDTO1.setCoinType(1);
                    farmRakeDTO1.setCoinValue(farmMember.getRakeBackCoinValue());
                    farmRakeService.create(farmRakeDTO1);

                    //工作包返佣奖励
                    WalletDTO walletDTO = new WalletDTO()
                            .setMemberId(inviteMemberId)
                            .setBalance(farmMember.getRakeBackCoinValue())
                            .setCoinType(farmMember.getRakeBackCoinType())
                            .setOpType(WalletOpTypeEnum.FARM_BAG_RAKE_BACK_CLAIM_REWARD.getValue())
                            .setRemark(WalletOpTypeEnum.FARM_BAG_RAKE_BACK_CLAIM_REWARD.getLabel());
                    list.add(walletDTO);

                    //工作包返荣誉值
                    WalletDTO walletDTO1 = new WalletDTO()
                            .setMemberId(inviteMemberId)
                            .setBalance(farmMember.getRakeBackCoinValue())
                            .setCoinType(1)
                            .setOpType(WalletOpTypeEnum.FARM_BAG_RAKE_BACK_CLAIM_REWARD.getValue())
                            .setRemark(WalletOpTypeEnum.FARM_BAG_RAKE_BACK_CLAIM_REWARD.getLabel());
                    list.add(walletDTO1);
                }

                //
                if (farmMember.getClaimCoinValue().compareTo(BigDecimal.valueOf(0.0)) > 0) {
                    //工作包收获奖励
                    WalletDTO walletDTO = new WalletDTO()
                            .setMemberId(farmMember.getMemberId())
                            .setBalance(farmMember.getClaimCoinValue())
                            .setCoinType(farmMember.getClaimCoinType())
                            .setOpType(WalletOpTypeEnum.FARM_BAG_CLAIM_REWARD.getValue())
                            .setRemark(WalletOpTypeEnum.FARM_BAG_CLAIM_REWARD.getLabel());
                    list.add(walletDTO);
                }
                if (!list.isEmpty()) {
                    walletFeignClient.updateBalances(list);
                }
                //领取昨日的返佣
                farmRakeService.claim();
                //
                if (farmMember.getActiveValueExt().compareTo(BigDecimal.valueOf(0.0)) > 0) {
                    //每次领取活跃度
                    sendActiveValue(memberId, farmMember.getActiveValueExt(), BigDecimal.ZERO);
                }
                log.info("领取农场奖励:{}.{}", JSONUtil.toJsonStr(farmMember));
            }
            return f;
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

    //激活工作包
    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean activate(Long farmItemId) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(FarmConstants.FARM_ACTIVATE_PREFIX + farmItemId);

        try {
            lock.lock();
            FarmMemberItem farmMemberItem = farmMemberItemService.getById(farmItemId);
            if (farmMemberItem == null) {
                throw new BizException(ResultCode.REQUEST_INVALID, "工作包不存在");
            }
            Assert.isTrue(farmMemberItem.getMemberId().compareTo(memberId) == 0, "非自己工作包");
            //获取配置信息
            FarmBagConfig farmBagConfig = farmBagConfigService.getBySpuId(farmMemberItem.getSpuId());
            FarmConfig farmConfig = farmConfigService.getById(farmBagConfig.getFarmId());
            boolean activate = false;
            if (farmConfig != null) {
                //检测金额
                Result<BigDecimal> coinBalance = walletFeignClient.getCoinValueByMemberIdAndCoinType(memberId, farmConfig.getActivateCoinType());
                if (coinBalance.getData() == null) {
                    return false;
                }
                Assert.isTrue(coinBalance.getData().compareTo(farmBagConfig.getActivateCoinValue()) >= 0, "余额不足");
                //激活
                activate = farmMemberService.activate(farmMemberItem, farmBagConfig);
                List<WalletDTO> list = org.apache.commons.compress.utils.Lists.newArrayList();
                //工作包激活消耗
                WalletDTO walletDTO = new WalletDTO()
                        .setMemberId(farmMemberItem.getMemberId())
                        .setBalance(farmBagConfig.getActivateCoinValue().negate())
                        .setCoinType(farmConfig.getActivateCoinType())
                        .setOpType(WalletOpTypeEnum.FARM_BAG_ACTIVATE_CONSUME.getValue())
                        .setRemark(WalletOpTypeEnum.FARM_BAG_ACTIVATE_CONSUME.getLabel());
                list.add(walletDTO);
                //激活奖励-荣誉值奖励
                if (farmBagConfig.getClaimCoinValueExt().compareTo(BigDecimal.ZERO) > 0) {
                    WalletDTO walletDTO1 = new WalletDTO()
                            .setMemberId(farmMemberItem.getMemberId())
                            .setBalance(farmBagConfig.getClaimCoinValueExt())
                            .setCoinType(farmConfig.getClaimCoinTypeExt())
                            .setOpType(WalletOpTypeEnum.FARM_BAG_ACTIVATE_CONSUME.getValue())
                            .setRemark(WalletOpTypeEnum.FARM_BAG_ACTIVATE_CONSUME.getLabel());
                    list.add(walletDTO1);
                }
                if (!list.isEmpty()) {
                    walletFeignClient.updateBalances(list);
                }
                log.info("激活工作包:{}", JSONUtil.toJsonStr(farmMemberItem));
            }
            return activate;
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
    public void close(List<FarmMemberItem> farmMemberItems) {
        if (!farmMemberItems.isEmpty()) {
            farmMemberService.close(farmMemberItems);
            List<Long> idLists = farmMemberItems.stream().map(FarmMemberItem::getId).collect(Collectors.toList());
            log.info("关闭工作包:{}", JSONUtil.toJsonStr(idLists));
        }
    }

    @Override
    public boolean checkUser() {
        Long memberId = MemberUtils.getMemberId();
        Result<MemberDTO> memberDTOResult = memberFeignClient.getMemberById(memberId);
        if (memberDTOResult.getData() != null) {
            Assert.isTrue(memberDTOResult.getData().getStatus().intValue() == 1, "账号被封禁");
        }
        return true;
    }

    /**
     * 重置
     *
     * @param memberIds
     */
    @Override
    public boolean reset(List<Long> memberIds, Long farmId) {
        memberIds.forEach(memberId -> {
            farmMemberService.reset(memberId, farmId);
        });
        return true;
    }

    /**
     * 开启农场
     */
    @Override
    public boolean openFarm(Long[] memberIds) {
        LambdaUpdateWrapper<FarmMember> updateWrapper = new LambdaUpdateWrapper<FarmMember>()
                .in(FarmMember::getMemberId,memberIds)
                .eq(FarmMember::getStatus,1)
                .set(FarmMember::getStatus,0);
        return farmMemberService.update(updateWrapper);
    }

    /**
     * 关闭农场
     */
    @Override
    public boolean closeFarm(Long[] memberIds) {
        LambdaUpdateWrapper<FarmMember> updateWrapper = new LambdaUpdateWrapper<FarmMember>()
                .in(FarmMember::getMemberId,memberIds)
                .eq(FarmMember::getStatus,0)
                .set(FarmMember::getStatus,1);
        return farmMemberService.update(updateWrapper);
    }

    /**
     * 重新领取农场
     */
    @Override
    public boolean reClaim(Long farmId) {
        FarmMember farmMember = farmMemberService.getById(farmId);
        if (farmMember!=null) {
            LocalDateTime tim = farmMember.getAllowClaimed();
            farmMember.setClaimed(tim.minusDays(1));
        }
        return farmMemberService.saveOrUpdate(farmMember);
    }

    //定时任务处理（关闭工作包）
    public boolean resetFarms(List<FarmMember> farmMembers) {
        farmMembers.stream().forEach(farmMember -> {
            //获取该农场下所有的任务包(正在执行的工作包)
            LocalDateTime now = LocalDateTime.now();
            LambdaQueryWrapper<FarmMemberItem> wrapper = Wrappers.<FarmMemberItem>lambdaQuery()
                    .eq(FarmMemberItem::getMemberId, farmMember.getMemberId())
                    .eq(FarmMemberItem::getStatus,0)
                    .orderByDesc(FarmMemberItem::getCreated);
            Page<FarmMemberItem> page = farmMemberItemService.page(new Page(1, 100), wrapper);
            if (!page.getRecords().isEmpty()) {
                BigDecimal claimCoinTotal = new BigDecimal(0.0);
                BigDecimal rakeCoinTotal = new BigDecimal(0.0);
                BigDecimal activeValueExTotal = new BigDecimal(0.0);
                Integer burnCode = 0;
                //根据时间戳来进行工作包关闭
                for (FarmMemberItem farmMemberItem : page.getRecords()) {
                    if (farmMemberItem.getClosed().compareTo(now) > 0) {
                        //未过期，走统计
                        //奖励（建设值）
                        claimCoinTotal = claimCoinTotal.add(farmMemberItem.getClaimCoinValue());
                        //返佣（建设值）
                        rakeCoinTotal = rakeCoinTotal.add(farmMemberItem.getRakeBackCoinValue());
                        //按周期返佣（活跃度）
                        activeValueExTotal = activeValueExTotal.add(farmMemberItem.getActiveValueExt());
                        if( farmMemberItem.getName().indexOf("实习")!=-1 ) {
                            burnCode = burnCode | 0x0001;
                        } else if( farmMemberItem.getName().indexOf("初级")!=-1 ) {
                            burnCode = burnCode | 0x0002;
                        } else if( farmMemberItem.getName().indexOf("中级")!=-1 ) {
                            burnCode = burnCode | 0x0004;
                        } else if( farmMemberItem.getName().indexOf("高级")!=-1 ) {
                            burnCode = burnCode | 0x0008;
                        } else if( farmMemberItem.getName().indexOf("专家")!=-1 ) {
                            burnCode = burnCode | 0x0010;
                        } else if( farmMemberItem.getName().indexOf("资深")!=-1 ) {
                            burnCode = burnCode | 0x0020;
                        } else if( farmMemberItem.getName().indexOf("体验")!=-1 ) {
                            burnCode = burnCode | 0x0040;
                        }
                    } else {
                        //过期，走关闭逻辑
                        farmMemberItemService.close(farmMemberItem);
                    }
                }
                //重置数据
                farmMember.setBurnCode(burnCode);
                farmMember.setClaimCoinValue(claimCoinTotal);
                farmMember.setRakeBackCoinValue(rakeCoinTotal);
                farmMember.setActiveValueExt(activeValueExTotal);
                boolean b = farmMemberService.updateById(farmMember);
                if (b) {
                    log.info("重置Farm：memberId{},farmId:{}",farmMember.getMemberId(),farmMember.getFarmId());
                    memberLogService.save(farmMember, FarmOpEnum.RESET);
                }
            }
        });
        return true;
    }


    private void sendActiveValue(Long memberId, BigDecimal activeValue, BigDecimal rakeBackActiveValue) {
        JSONObject obj = JSONUtil.createObj();
        obj.set("memberId", memberId);
        obj.set("activeValue", activeValue);
        obj.set("rakeBackActiveValue", rakeBackActiveValue);
        rabbitTemplate.convertAndSend(GlobalConstants.MQ_ACTIVE_VALUE_EXCHANGE, GlobalConstants.MQ_ACTIVE_VALUE_KEY, obj.toString());
    }
}

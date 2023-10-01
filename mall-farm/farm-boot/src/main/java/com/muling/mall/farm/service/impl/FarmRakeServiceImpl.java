package com.muling.mall.farm.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.muling.mall.farm.mapper.FarmMemberItemMapper;
import com.muling.mall.farm.mapper.FarmRakeMapper;
import com.muling.mall.farm.pojo.dto.FarmRakeDTO;
import com.muling.mall.farm.pojo.entity.*;
import com.muling.mall.farm.pojo.query.app.FarmMemberItemPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmMemberItemVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;
import com.muling.mall.farm.pojo.vo.app.FarmRakeVO;
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
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmRakeServiceImpl extends ServiceImpl<FarmRakeMapper, FarmRake> implements IFarmRakeService {

    private final WalletFeignClient walletFeignClient;

    private final RedissonClient redissonClient;

    public boolean create(FarmRakeDTO farmRakeDTO) {
        //一天只能创建一条数据
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime started = today.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime ended = today.withHour(23).withMinute(59).withSecond(59);
        LambdaQueryWrapper<FarmRake> queryWrapper = new LambdaQueryWrapper<FarmRake>()
                .eq(FarmRake::getMemberId, farmRakeDTO.getMemberId())
                .ge(FarmRake::getCreated, started)
                .le(FarmRake::getCreated, ended);
        FarmRake farmRake = this.baseMapper.selectOne(queryWrapper);
        if (farmRake == null) {
            farmRake = new FarmRake();
            farmRake.setMemberId(farmRakeDTO.getMemberId());
            farmRake.setTargetId(farmRakeDTO.getTargetId());
            farmRake.setCoinType(farmRakeDTO.getCoinType());
            farmRake.setCoinValue(farmRakeDTO.getCoinValue());
            farmRake.setStatus(1);  //0未领取，1已领取
            return this.save(farmRake);
        }
        return false;
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    public Map<Integer,FarmRakeVO> claim() {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(FarmConstants.FARM_CLAIM_RAKE_PREFIX + memberId);
        try {
            lock.lock();
            //获取前一天的时间
            LocalDateTime localDateTime = LocalDateTime.now();
            LocalDateTime preDay = localDateTime.minus(1, ChronoUnit.DAYS);
            LocalDateTime started = preDay.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime ended = preDay.withHour(23).withMinute(59).withSecond(59);
            LambdaQueryWrapper<FarmRake> queryWrapper = new LambdaQueryWrapper<FarmRake>();
            queryWrapper.eq(FarmRake::getTargetId, memberId);
            queryWrapper.eq(FarmRake::getStatus, 0);
            queryWrapper.ge(FarmRake::getCreated, started);
            queryWrapper.le(FarmRake::getCreated, ended);
            //
            Map<Integer,FarmRakeVO> farmRakeVOMap = new HashMap<>();
            Page<FarmRake> farmRakePage = this.page(new Page<>(1, 1000), queryWrapper);
            if (!farmRakePage.getRecords().isEmpty()) {
                //计算奖励
                LambdaUpdateWrapper<FarmRake> queryWrapperUpdate = new LambdaUpdateWrapper<FarmRake>()
                        .eq(FarmRake::getTargetId, memberId)
                        .eq(FarmRake::getStatus, 0)
                        .ge(FarmRake::getCreated, started)
                        .le(FarmRake::getCreated, ended)
                        .set(FarmRake::getStatus, 1);
                boolean f = this.update(queryWrapperUpdate);
                if (f) {
                    for (FarmRake farmRake : farmRakePage.getRecords()) {
                        FarmRakeVO farmRakeVO = farmRakeVOMap.get(farmRake.getCoinType());
                        if (farmRakeVO == null) {
                            farmRakeVO = new FarmRakeVO();
                            farmRakeVO.setStatus(1);
                            farmRakeVO.setRewardCoinType(farmRake.getCoinType());
                            farmRakeVO.setRewardCoinValue(farmRake.getCoinValue());
                            farmRakeVOMap.put(farmRake.getCoinType(),farmRakeVO);
                        } else {
                            farmRakeVO.setRewardCoinValue(farmRakeVO.getRewardCoinValue().add(farmRake.getCoinValue()));
                        }
                    }
                    //工作包返佣奖励(多积分)
                    farmRakeVOMap.forEach((coinType,farmRakeVO)->{
                        WalletDTO walletDTO = new WalletDTO()
                                .setMemberId(memberId)
                                .setBalance(farmRakeVO.getRewardCoinValue())
                                .setCoinType(coinType)
                                .setOpType(WalletOpTypeEnum.FARM_BAG_RAKE_BACK_CLAIM_REWARD.getValue())
                                .setRemark(WalletOpTypeEnum.FARM_BAG_RAKE_BACK_CLAIM_REWARD.getLabel());
                        walletFeignClient.updateBalance(walletDTO);
                    });
                }
            }
            return farmRakeVOMap;
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

}

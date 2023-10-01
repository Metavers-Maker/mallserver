package com.muling.mall.farm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.farm.constant.FarmConstants;
import com.muling.mall.farm.mapper.FarmAdItemMapper;
import com.muling.mall.farm.pojo.dto.FarmAdItemDTO;
import com.muling.mall.farm.pojo.entity.*;
import com.muling.mall.farm.service.*;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmAdItemServiceImpl extends ServiceImpl<FarmAdItemMapper, FarmAdItem> implements IFarmAdItemService {

    private final RedissonClient redissonClient;

    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean checkTrans(String transId) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(FarmConstants.FARM_AD_REWARD_PREFIX + memberId);
        try {
            lock.lock();
            QueryWrapper<FarmAdItem> queryWrapper = new QueryWrapper<FarmAdItem>()
                    .eq("transaction_id", transId);
            FarmAdItem farmAdItem = this.baseMapper.selectOne(queryWrapper);
            Assert.isTrue(farmAdItem != null, "未完成");
            return true;
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

    public boolean saveDTO(FarmAdItemDTO farmAdItemDTO) {
        QueryWrapper<FarmAdItem> queryWrapper = new QueryWrapper<FarmAdItem>()
                .eq("transaction_id", farmAdItemDTO.getTransId());
        FarmAdItem farmAdItem = this.baseMapper.selectOne(queryWrapper);
        if (farmAdItem == null) {
            farmAdItem = new FarmAdItem();
            farmAdItem.setMemberId(farmAdItemDTO.getMemberId());
            farmAdItem.setAdType(farmAdItemDTO.getAdType());
            farmAdItem.setAdSn(farmAdItemDTO.getAdSn());
            farmAdItem.setAdId(farmAdItemDTO.getAdId());
            farmAdItem.setTransactionId(farmAdItemDTO.getTransId());
            farmAdItem.setEcpm(farmAdItemDTO.getEcpm());
            farmAdItem.setRewardCount(farmAdItemDTO.getRewardCount());
            farmAdItem.setRewardName(farmAdItemDTO.getRewardName());
            return this.save(farmAdItem);
        }
        return false;
    }

}

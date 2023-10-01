package com.muling.mall.pms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.common.constant.PmsConstants;
import com.muling.mall.pms.converter.RndConverter;
import com.muling.mall.pms.mapper.PmsRndMapper;
import com.muling.mall.pms.pojo.dto.RndDTO;
import com.muling.mall.pms.pojo.entity.PmsRnd;
import com.muling.mall.pms.pojo.form.RndForm;
import com.muling.mall.pms.repository.PmsHotRepository;
import com.muling.mall.pms.service.IPmsRndService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PmsRndServiceImpl extends ServiceImpl<PmsRndMapper, PmsRnd> implements IPmsRndService {

    private final PmsHotRepository hotRepository;

    private final RedissonClient redissonClient;

    @Override
    public boolean save(RndForm rndForm) {
        PmsRnd rnd = RndConverter.INSTANCE.form2Po(rndForm);
        boolean b = this.save(rnd);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, RndForm rndForm) {
        PmsRnd rnd = getById(id);
        if (rnd == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        BeanUtil.copyProperties(rndForm, rnd);

        return updateById(rnd);
    }

    @GlobalTransactional
    @Override
    public List<RndDTO> lock(Long spuId) {
        RLock lock = redissonClient.getLock(PmsConstants.LOCK_RND_NUM_PREFIX + spuId);
        try {
            lock.lock();
            //原始列表
            List<PmsRnd> pmsRndList = this.list(Wrappers.<PmsRnd>lambdaQuery().eq(PmsRnd::getTarget, spuId));
            //更新列表
            List<PmsRnd> updateList = new ArrayList<>();
            //盲盒锁仓，返回可以开出的数据
            pmsRndList.forEach(item -> {
                boolean lock_success = false;
                if (item.getMaxCount() == 0) {
                    lock_success = true;
                } else if (item.getMaxCount() >= item.getAliveCount() + item.getSpuCount()) {
                    lock_success = true;
                }
                if (lock_success) {
                    item.setAliveCount(item.getAliveCount() + item.getSpuCount());
                    updateList.add(item);
                }
            });
            if (updateList.size() == 0) {
                List<RndDTO> rndDTOList = new ArrayList<>();
                return rndDTOList;
            } else {
                //更新锁仓
                boolean reUpdate = this.updateBatchById(updateList);
                Assert.isTrue(reUpdate, "盲盒库存锁仓失败");
                //返回可以开出的数据
                List<RndDTO> rndDTOList = RndConverter.INSTANCE.po2DTOs(updateList);
                return rndDTOList;
            }
        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    @GlobalTransactional
    @Override
    public boolean unlock(Long spuId, Long rndId) {
        RLock lock = redissonClient.getLock(PmsConstants.LOCK_RND_NUM_PREFIX + spuId);
        try {
            lock.lock();
            //原始列表
            List<PmsRnd> pmsRndList = this.list(Wrappers.<PmsRnd>lambdaQuery().eq(PmsRnd::getTarget, spuId));
            //更新列表
            List<PmsRnd> updateList = new ArrayList<>();
            pmsRndList.forEach(item -> {
                boolean unlock_success = false;
                if (item.getId().equals(rndId) == false) {
                    unlock_success = true;
                }
                if (unlock_success) {
                    item.setAliveCount(item.getAliveCount() - item.getSpuCount());
                    updateList.add(item);
                }
            });
            //更新锁仓
            if (updateList.size() == 0) {
                return true;
            }
            //
            boolean reUpdate = this.updateBatchById(updateList);
            Assert.isTrue(reUpdate, "盲盒库存解锁失败");
            return reUpdate;

        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    //
}

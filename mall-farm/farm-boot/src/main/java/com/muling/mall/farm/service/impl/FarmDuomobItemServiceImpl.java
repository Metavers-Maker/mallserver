package com.muling.mall.farm.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.DateUtils;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.api.ItemFeignClient;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.farm.constant.FarmConstants;
import com.muling.mall.farm.converter.*;
import com.muling.mall.farm.mapper.FarmAdItemMapper;
import com.muling.mall.farm.mapper.FarmDuomobItemMapper;
import com.muling.mall.farm.mapper.FarmMemberItemMapper;
import com.muling.mall.farm.pojo.dto.FarmAdItemDTO;
import com.muling.mall.farm.pojo.dto.FarmDuomobItemDTO;
import com.muling.mall.farm.pojo.entity.*;
import com.muling.mall.farm.pojo.query.app.FarmMemberItemPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmAdVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberItemVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;
import com.muling.mall.farm.service.*;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.api.MemberInviteFeignClient;
import com.muling.mall.ums.pojo.dto.AdFarmDispatchDTO;
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
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmDuomobItemServiceImpl extends ServiceImpl<FarmDuomobItemMapper, FarmDuomobItem> implements IFarmDuomobItemService {

    private final MemberInviteFeignClient memberInviteFeignClient;
    private final RedissonClient redissonClient;

    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean checkTrans(String orderId) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(FarmConstants.FARM_DUOMOB_REWARD_PREFIX + memberId);
        try {
            lock.lock();
            QueryWrapper<FarmDuomobItem> queryWrapper = new QueryWrapper<FarmDuomobItem>()
                    .eq("order_id", orderId);
            FarmDuomobItem farmDuomobItem = this.baseMapper.selectOne(queryWrapper);
            if (farmDuomobItem!=null) {
                return true;
            }
            return false;
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
    public boolean saveDTO(FarmDuomobItem farmDuomobItem) {
        QueryWrapper<FarmDuomobItem> queryWrapper = new QueryWrapper<FarmDuomobItem>()
                .eq("order_id", farmDuomobItem.getOrderId());
        FarmDuomobItem item = this.baseMapper.selectOne(queryWrapper);
        if (item == null) {
//            log.info("保存数据中...",farmDuomobItem.toString());
            farmDuomobItem.setDispatch(1);
            boolean f = this.save(farmDuomobItem);
            if (f) {
                //保存成功就分红了
                BigDecimal total = farmDuomobItem.getMemberIncome().add(farmDuomobItem.getMediaIncome());
                AdFarmDispatchDTO adFarmDispatchDTO = new AdFarmDispatchDTO();
                adFarmDispatchDTO.setMemberId(farmDuomobItem.getMemberId());
                adFarmDispatchDTO.setCoinType(5);
                adFarmDispatchDTO.setCoinValue(farmDuomobItem.getMemberIncome());
                adFarmDispatchDTO.setDirectValue(total.multiply(BigDecimal.valueOf(0.05)));
                adFarmDispatchDTO.setAdLevel1Value(total.multiply(BigDecimal.valueOf(0.05)));
                adFarmDispatchDTO.setAdLevel2Value(total.multiply(BigDecimal.valueOf(0.05)));
                adFarmDispatchDTO.setAdLevel3Value(total.multiply(BigDecimal.valueOf(0.05)));
                adFarmDispatchDTO.setAdLevel4Value(total.multiply(BigDecimal.valueOf(0.05)));
                memberInviteFeignClient.gameFarmDispatch(adFarmDispatchDTO);
            }
            return f;
        } else {
            //发现目标
            if (item.getDispatch().intValue() == 0) {
                //走分佣逻辑
                BigDecimal total = item.getMemberIncome().add(item.getMediaIncome());
                AdFarmDispatchDTO adFarmDispatchDTO = new AdFarmDispatchDTO();
                adFarmDispatchDTO.setMemberId(item.getMemberId());
                adFarmDispatchDTO.setCoinType(5);
                adFarmDispatchDTO.setCoinValue(item.getMemberIncome());
                adFarmDispatchDTO.setDirectValue(total.multiply(BigDecimal.valueOf(0.05)));
                adFarmDispatchDTO.setAdLevel1Value(total.multiply(BigDecimal.valueOf(0.05)));
                adFarmDispatchDTO.setAdLevel2Value(total.multiply(BigDecimal.valueOf(0.05)));
                adFarmDispatchDTO.setAdLevel3Value(total.multiply(BigDecimal.valueOf(0.05)));
                adFarmDispatchDTO.setAdLevel4Value(total.multiply(BigDecimal.valueOf(0.05)));
                memberInviteFeignClient.gameFarmDispatch(adFarmDispatchDTO);
                //
                item.setDispatch(1);
                boolean f = this.updateById(item);
                return f;
            }
        }
        return false;
    }

}

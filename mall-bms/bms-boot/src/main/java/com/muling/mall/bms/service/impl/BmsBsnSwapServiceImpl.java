package com.muling.mall.bms.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.cert.config.BSNConfig;
import com.muling.common.cert.service.HttpApiClientBSN;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.Result;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.converter.BsnSwapConverter;
import com.muling.mall.bms.enums.ChainSwapTypeEnum;
import com.muling.mall.bms.enums.ItemStatusEnum;
import com.muling.mall.bms.mapper.BmsBsnSwapMapper;
import com.muling.mall.bms.pojo.entity.BmsBsnSwap;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.query.admin.BsnSwapPageQuery;
import com.muling.mall.bms.pojo.vo.app.BsnSwapVO;
import com.muling.mall.bms.service.IBmsBsnSwapService;
import com.muling.mall.bms.service.IMemberItemService;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BmsBsnSwapServiceImpl extends ServiceImpl<BmsBsnSwapMapper, BmsBsnSwap> implements IBmsBsnSwapService {

//    private final BusinessNoGenerator businessNoGenerator;
//
//    private final RedissonClient redissonClient;
//
////    private final IMemberItemService memberItemService;
//
//    private final HttpApiClientBSN httpApiClientBSN;
//
//    private final MemberFeignClient memberFeignClient;
//
//    private final BSNConfig bsnConfig;

    @Override
    public IPage<BsnSwapVO> page(BsnSwapPageQuery queryParams) {
        LambdaQueryWrapper<BmsBsnSwap> queryWrapper = new LambdaQueryWrapper<BmsBsnSwap>()
                .orderByDesc(BmsBsnSwap::getUpdated);
        Page<BmsBsnSwap> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<BsnSwapVO> result = BsnSwapConverter.INSTANCE.po2PageVO(page);
        return result;
    }

    @Override
    public boolean save(Long fromMemberId, Long toMemberId, OmsMemberItem memberItem, ChainSwapTypeEnum swapTypeEnum) {
        BmsBsnSwap bsnSwap = new BmsBsnSwap();
        bsnSwap.setFromId(fromMemberId)
                .setToId(toMemberId)
                .setSpuId(memberItem.getSpuId())
                .setSpuName(memberItem.getName())
                .setItemId(memberItem.getId())
                .setItemNo(memberItem.getItemNo())
                .setPicUrl(memberItem.getPicUrl())
                .setSourceUrl(memberItem.getSourceUrl())
                .setTokenId(memberItem.getHexId())
                .setTransType(swapTypeEnum);
        boolean b = this.save(bsnSwap);
        return b;
    }

    @Override
    public boolean saveBatch(Long fromMemberId, Long toMemberId, List<OmsMemberItem> memberItems, ChainSwapTypeEnum swapTypeEnum) {
        List<BmsBsnSwap> bsnSwaps = new ArrayList<>();
        memberItems.forEach(item -> {
            BmsBsnSwap bsnSwap = new BmsBsnSwap();
            bsnSwap.setFromId(fromMemberId)
                    .setToId(toMemberId)
                    .setSpuId(item.getSpuId())
                    .setSpuName(item.getName())
                    .setItemId(item.getId())
                    .setItemNo(item.getItemNo())
                    .setPicUrl(item.getPicUrl())
                    .setSourceUrl(item.getSourceUrl())
                    .setTokenId(item.getHexId())
                    .setTransType(swapTypeEnum);
            bsnSwaps.add(bsnSwap);
        });
        return this.saveBatch(bsnSwaps);
    }



}

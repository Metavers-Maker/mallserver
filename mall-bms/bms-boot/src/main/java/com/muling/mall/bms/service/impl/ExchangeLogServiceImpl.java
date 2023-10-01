package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.converter.ExchangeLogConverter;
import com.muling.mall.bms.mapper.ExchangeLogMapper;
import com.muling.mall.bms.pojo.entity.OmsExchangeLog;
import com.muling.mall.bms.pojo.query.admin.ExchangeLogPageQuery;
import com.muling.mall.bms.pojo.vo.app.ExchangeLogVO;
import com.muling.mall.bms.service.IExchangeLogService;
import com.muling.mall.bms.service.IItemLogService;
import com.muling.mall.bms.service.IMemberItemService;
import com.muling.mall.pms.api.SkuFeignClient;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.wms.api.WalletFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ExchangeLogServiceImpl extends ServiceImpl<ExchangeLogMapper, OmsExchangeLog> implements IExchangeLogService {

    private final RedissonClient redissonClient;
    private final SkuFeignClient skuFeignClient;
    private final SpuFeignClient spuFeignClient;
    private final IItemLogService itemLogService;
    private final WalletFeignClient walletFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final IMemberItemService memberItemService;

    @Override
    public IPage<ExchangeLogVO> page(ExchangeLogPageQuery queryParams) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<OmsExchangeLog> wrapper = Wrappers.<OmsExchangeLog>lambdaQuery()
                .eq(OmsExchangeLog::getMemberId, memberId)
                .orderByDesc(OmsExchangeLog::getCreated);
        ;
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<ExchangeLogVO> list = ExchangeLogConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }


}

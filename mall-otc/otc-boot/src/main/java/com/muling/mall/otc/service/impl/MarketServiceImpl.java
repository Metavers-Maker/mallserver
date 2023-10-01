package com.muling.mall.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.otc.converter.MarketConverter;
import com.muling.mall.otc.mapper.MarketMapper;
import com.muling.mall.otc.pojo.entity.OtcMarket;
import com.muling.mall.otc.pojo.query.app.MarketPageQuery;
import com.muling.mall.otc.pojo.vo.MarketVO;
import com.muling.mall.otc.service.IMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MarketServiceImpl extends ServiceImpl<MarketMapper, OtcMarket> implements IMarketService {

    @Override
    public IPage<MarketVO> page(MarketPageQuery queryParams) {
        LambdaQueryWrapper<OtcMarket> wrapper = Wrappers.<OtcMarket>lambdaQuery()
                .orderByDesc(OtcMarket::getUpdated);
        ;
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<MarketVO> list = MarketConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }


}

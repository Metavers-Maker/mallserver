package com.muling.mall.otc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.otc.pojo.entity.OtcMarket;
import com.muling.mall.otc.pojo.query.app.MarketPageQuery;
import com.muling.mall.otc.pojo.vo.MarketVO;


public interface IMarketService extends IService<OtcMarket> {

    public IPage<MarketVO> page(MarketPageQuery queryParams);

}


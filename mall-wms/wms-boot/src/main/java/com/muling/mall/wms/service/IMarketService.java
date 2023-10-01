package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.wms.pojo.entity.WmsMarket;
import com.muling.mall.wms.pojo.form.app.MarketCreateForm;
import com.muling.mall.wms.pojo.form.app.MarketUpdateForm;
import com.muling.mall.wms.pojo.query.app.MarketBuyPageQueryApp;
import com.muling.mall.wms.pojo.query.app.MarketPageQueryApp;
import com.muling.mall.wms.pojo.query.app.MarketSellPageQueryApp;
import com.muling.mall.wms.pojo.vo.MarketDispatchVO;
import com.muling.mall.wms.pojo.vo.MarketVO;

import java.time.LocalDateTime;
import java.util.List;

public interface IMarketService extends IService<WmsMarket> {

    public IPage<MarketVO> page(MarketPageQueryApp queryParams);

    public IPage<MarketVO> pageMe(MarketPageQueryApp queryParams);

    public IPage<MarketVO> buyPageMe(MarketBuyPageQueryApp queryParams);

    public IPage<MarketVO> sellPageMe(MarketSellPageQueryApp queryParams);

    public boolean createBuy(MarketCreateForm marketCreateForm);

    public Integer buyStar();

    public boolean destroyBuy(Long marketId);

    public boolean updateById(Long id, MarketUpdateForm marketForm);

    public boolean lock(Long marketId);

    public boolean buyCancel(Long marketId);

    public boolean cancel(Long marketId);

    public boolean commit(Long marketId);

    public boolean confirm(Long marketId);

    public boolean close(List<Long> marketIds);

    //admin
    public boolean coinFreeze(Long marketId);

    public boolean coinReturn(Long marketId);

    public boolean coinPay(Long marketId);

    public boolean coinCancle(Long marketId);

    public MarketDispatchVO coinDispatch(LocalDateTime date);

}


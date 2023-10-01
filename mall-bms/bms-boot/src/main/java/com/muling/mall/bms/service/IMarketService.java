package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.event.TransMemberItemEvent;
import com.muling.mall.bms.pojo.entity.OmsMarket;
import com.muling.mall.bms.pojo.form.app.MarketCreateForm;
import com.muling.mall.bms.pojo.form.app.MarketUpdateForm;
import com.muling.mall.bms.pojo.query.app.MarketBuyPageQueryApp;
import com.muling.mall.bms.pojo.query.app.MarketPageQueryApp;
import com.muling.mall.bms.pojo.vo.app.MarketVO;

import java.util.List;

public interface IMarketService extends IService<OmsMarket> {

    public IPage<MarketVO> page(MarketPageQueryApp queryParams);

    public IPage<MarketVO> pageMe(MarketPageQueryApp queryParams);

    public IPage<MarketVO> buyPageMe(MarketBuyPageQueryApp queryParams);

    public List<MarketVO> getListByIds(List<Long> ids);

    public boolean save(MarketCreateForm marketCreateForm);

    public boolean updateById(Long id, MarketUpdateForm marketForm);

    public boolean close(List<Long> marketIds);

    public void buyItem(TransMemberItemEvent event);

    public boolean lockById(Long id, Long memberId);

    public boolean unlockById(Long id);

    /**
     * 后台系统撤销
     * */
    public boolean adminCancle(Long marketId);


}


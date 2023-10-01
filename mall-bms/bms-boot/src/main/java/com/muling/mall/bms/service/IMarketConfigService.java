package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsMarketConfig;
import com.muling.mall.bms.pojo.form.admin.MarketConfigForm;
import com.muling.mall.bms.pojo.query.app.MarketConfigPageQueryApp;
import com.muling.mall.bms.pojo.vo.app.MarketConfigVO;

public interface IMarketConfigService extends IService<OmsMarketConfig> {

    public IPage<MarketConfigVO> page(MarketConfigPageQueryApp queryParams);

    public boolean isOpen(Long spuId);

    public boolean save(MarketConfigForm configForm);

    public boolean updateById(Long id, MarketConfigForm configForm);

}


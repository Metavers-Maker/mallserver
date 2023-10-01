package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.wms.pojo.entity.WmsMarketConfig;
import com.muling.mall.wms.pojo.form.admin.MarketConfigForm;
import com.muling.mall.wms.pojo.query.app.MarketConfigPageQueryApp;
import com.muling.mall.wms.pojo.vo.MarketConfigVO;

public interface IMarketConfigService extends IService<WmsMarketConfig> {

    public IPage<MarketConfigVO> page(MarketConfigPageQueryApp queryParams);

    public boolean save(MarketConfigForm configForm);

    public WmsMarketConfig getByCoinType(Integer coinType,Integer opType);

    public boolean updateById(Long id, MarketConfigForm configForm);

}


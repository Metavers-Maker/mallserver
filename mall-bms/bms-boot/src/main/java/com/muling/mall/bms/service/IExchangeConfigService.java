package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsExchangeConfig;
import com.muling.mall.bms.pojo.form.admin.ExchangeConfigForm;
import com.muling.mall.bms.pojo.form.app.ExchangeForm;
import com.muling.mall.bms.pojo.query.admin.ExchangePageQuery;
import com.muling.mall.bms.pojo.vo.app.ExchangeVO;

public interface IExchangeConfigService extends IService<OmsExchangeConfig> {

    public IPage<ExchangeVO> page(ExchangePageQuery queryParams);

    public boolean save(ExchangeConfigForm configForm);

    public boolean updateById(Long id, ExchangeConfigForm configForm);

    public boolean exchange(ExchangeForm exchangeForm);

}

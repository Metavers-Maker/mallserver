package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsExchangeLog;
import com.muling.mall.bms.pojo.query.admin.ExchangeLogPageQuery;
import com.muling.mall.bms.pojo.vo.app.ExchangeLogVO;

public interface IExchangeLogService extends IService<OmsExchangeLog> {

    public IPage<ExchangeLogVO> page(ExchangeLogPageQuery queryParams);

}

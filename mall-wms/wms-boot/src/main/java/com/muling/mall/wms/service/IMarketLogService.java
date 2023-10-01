package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.base.BasePageQuery;
import com.muling.mall.wms.pojo.entity.WmsMarketLog;
import com.muling.mall.wms.pojo.query.app.TransferPageQuery;
import com.muling.mall.wms.pojo.vo.TransferLogVO;
import com.muling.mall.wms.pojo.vo.TransferVO;
import com.muling.mall.wms.pojo.vo.WmsMarketLogVO;

public interface IMarketLogService extends IService<WmsMarketLog> {

    public IPage<WmsMarketLogVO> page(BasePageQuery queryParams);
}

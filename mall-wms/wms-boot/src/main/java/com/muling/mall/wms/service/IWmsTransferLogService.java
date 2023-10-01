package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.base.BasePageQuery;
import com.muling.mall.wms.pojo.entity.WmsTransferLog;
import com.muling.mall.wms.pojo.query.app.TransferPageQuery;
import com.muling.mall.wms.pojo.vo.TransferLogVO;
import com.muling.mall.wms.pojo.vo.TransferVO;

public interface IWmsTransferLogService extends IService<WmsTransferLog> {

    public IPage<TransferLogVO> page(BasePageQuery queryParams);
}

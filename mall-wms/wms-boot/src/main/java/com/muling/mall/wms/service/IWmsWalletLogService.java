package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.base.BasePageQuery;
import com.muling.mall.wms.pojo.entity.WmsWalletLog;
import com.muling.mall.wms.pojo.query.app.WalletLogPageQuery;
import com.muling.mall.wms.pojo.vo.WalletLogVO;

public interface IWmsWalletLogService extends IService<WmsWalletLog> {

    public IPage<WalletLogVO> page(WalletLogPageQuery queryParams);
}

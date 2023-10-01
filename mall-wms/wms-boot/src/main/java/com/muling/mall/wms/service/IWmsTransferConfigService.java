package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.wms.pojo.entity.WmsTransferConfig;
import com.muling.mall.wms.pojo.form.admin.TransferConfigForm;

public interface IWmsTransferConfigService extends IService<WmsTransferConfig> {

    public boolean save(TransferConfigForm transferConfigForm);

    public boolean updateById(Long id, TransferConfigForm transferConfigForm);

    public WmsTransferConfig getByCoinType(Integer coinType);
}

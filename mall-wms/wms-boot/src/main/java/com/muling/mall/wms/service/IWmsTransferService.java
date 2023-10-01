package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.mall.wms.pojo.form.app.TransferForm;
import com.muling.mall.wms.pojo.query.app.TransferPageQuery;
import com.muling.mall.wms.pojo.vo.TransferVO;

public interface IWmsTransferService {

    public IPage<TransferVO> page(TransferPageQuery queryParams);

    public boolean transfer(TransferForm transferForm);
}

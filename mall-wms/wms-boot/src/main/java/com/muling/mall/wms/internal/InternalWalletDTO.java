package com.muling.mall.wms.internal;

import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.pojo.entity.WmsWalletLog;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InternalWalletDTO {
    private WmsWallet wallet;
    private WmsWalletLog walletLog;
    private boolean canUpdate;
}

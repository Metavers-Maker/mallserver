package com.muling.mall.bms.pojo.form.admin;

import com.muling.mall.bms.enums.ItemTransferEnum;
import lombok.Data;


@Data
public class ItemTransferAdminForm {

    private String hash;
    private ItemTransferEnum transfer;
}

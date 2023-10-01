package com.muling.mall.bms.pojo.form.app;

import com.muling.mall.bms.enums.ItemStatusEnum;
import lombok.Data;


@Data
public class ItemMintForm {

    private String hash;
    private ItemStatusEnum status;

}

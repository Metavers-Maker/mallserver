package com.muling.mall.bms.pojo.form.app;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class ItemTransferForm {

    //物品ID
    @NotNull
    private Long itermId[];

    //转移到会员ID
    @NotNull
    private String toUid;

}

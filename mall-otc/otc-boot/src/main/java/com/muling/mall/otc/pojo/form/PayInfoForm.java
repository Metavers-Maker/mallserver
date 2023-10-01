package com.muling.mall.otc.pojo.form;

import lombok.Data;

@Data
public class PayInfoForm {

    private Integer payType;

    private String name;

    private String qrCode;

    private String remark;
}

package com.muling.mall.oms.pojo.form;

import com.muling.mall.oms.enums.PayTypeEnum;
import lombok.Data;

@Data
public class ChannelForm {

    private String name;

    private PayTypeEnum payType;
}

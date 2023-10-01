package com.muling.mall.bms.pojo.form.app;

import com.muling.mall.bms.enums.PayTypeEnum;
import lombok.Data;

@Data
public class ChannelForm {

    private String name;

    private PayTypeEnum payType;
}

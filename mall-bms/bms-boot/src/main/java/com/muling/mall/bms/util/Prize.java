package com.muling.mall.bms.util;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Prize {
    /**
     * 概率
     * */
    private double prob;

    /**
     * 配置ID
     * */
    private long cfgId;
}

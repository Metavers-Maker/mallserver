package com.muling.global.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum OssServiceTypeEnum implements IBaseEnum<Integer> {

    LOCAL(0, "local"), //本地存储

    ALIYUN(1, "aliyun");  //阿里云oss

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    OssServiceTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}

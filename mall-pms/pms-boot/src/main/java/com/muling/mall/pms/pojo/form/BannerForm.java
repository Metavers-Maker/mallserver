package com.muling.mall.pms.pojo.form;

import com.muling.mall.pms.common.enums.LinkTypeEnum;
import lombok.Data;

@Data
public class BannerForm {

    private String name;

    private String link;

    private LinkTypeEnum linkType;

    private String source;

    private Integer sort;
}

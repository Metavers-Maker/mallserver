package com.muling.mall.pms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.pms.common.enums.LinkTypeEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PmsBanner extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;

    private LinkTypeEnum linkType;
    private String link;

    private ViewTypeEnum visible;

    private String source;

    private Integer sort;
}

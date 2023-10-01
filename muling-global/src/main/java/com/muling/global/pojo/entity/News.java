package com.muling.global.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.muling.common.base.BaseEntity;
import com.muling.common.enums.VisibleEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
public class News extends BaseEntity {

    private Long id;

    private Integer type;

    private String title;

    private String ext;

    private String content;

    private Integer sort;

    private VisibleEnum visible;

}

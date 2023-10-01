package com.muling.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;

@Data
public class SysPathRule extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String path;

    private Integer type;

    private String value;

    private String remark;

}

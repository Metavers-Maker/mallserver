package com.muling.mall.pms.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.common.base.BaseEntity;
import com.muling.common.mybatis.handler.JsonObjectSqlJsonHandler;
import com.muling.mall.pms.common.enums.StatusEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PmsSubject extends BaseEntity {

    @TableId(type = IdType.NONE)
    private Long id;
    private String name;
    private Long brandId;
    private String picUrl;
    @TableField(typeHandler = JsonObjectSqlJsonHandler.class)
    private JSONObject ext;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer sort;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private ViewTypeEnum visible;
}

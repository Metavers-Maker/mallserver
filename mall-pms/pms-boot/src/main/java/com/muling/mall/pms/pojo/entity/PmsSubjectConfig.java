package com.muling.mall.pms.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.common.base.BaseEntity;
import com.muling.mall.pms.enums.BindEnum;
import com.muling.mall.pms.common.enums.StatusEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class PmsSubjectConfig extends BaseEntity {
    /**
     * 系列配置ID
     * */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 系列Id
     * */
    private Long subjectId;
    /**
     * 商品Id
     * */
    private Long spuId;
    /**
     * 排序
     * */
    private Integer sort;
    /**
     * 是否可见
     * */
    private ViewTypeEnum visible;
}

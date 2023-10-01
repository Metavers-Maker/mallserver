package com.muling.mall.pms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 */
@Data
@Accessors(chain = true)
public class PmsGround extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 广场名称
     */
    private String name;
    /**
     * 广场类型
     */
    private Integer type;
    /**
     * 商品编号
     */
    private Long spuId;
    /**
     * 是否可见
     */
    private ViewTypeEnum visible;
    /**
     * 排序
     */
    private Integer sort;

}

package com.muling.mall.pms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class PmsRnd extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 目标ID
     * */
    private Long target;

    /**
     * 奖励藏品ID
     * */
    private Long spuId;

    /**
     * 奖励藏品数量
     * */
    private Integer spuCount;

    /**
     * 奖励藏品名称
     * */
    private String name;

    /**
     * 奖励藏品图片
     * */
    private String picUrl;

    /**
     * 奖励藏品最大数量
     * */
    private Integer maxCount;

    /**
     * 奖励藏品产出数量
     * */
    private Integer aliveCount;

    private Long skuId;

    private Integer coinType;

    private Integer coinCount;

    /**
     * 概率
     * */
    private Integer prod;

}

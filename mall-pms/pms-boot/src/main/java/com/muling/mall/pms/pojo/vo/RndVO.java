package com.muling.mall.pms.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 商品详情视图对象
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2021/8/8
 */
@Data
@ApiModel("盲盒内部详情")
public class RndVO {

    private Long id;

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

    private Integer coinType;

    private Integer coinCount;

    /**
     * 概率
     * */
    private Integer prod;


}

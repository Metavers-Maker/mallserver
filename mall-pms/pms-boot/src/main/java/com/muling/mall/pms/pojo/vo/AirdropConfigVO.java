package com.muling.mall.pms.pojo.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import software.amazon.ion.Decimal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("空投配置视图对象")
public class AirdropConfigVO {

    /**
     * 活动名称
     * */
    private String name;

    /**
     * 活动细节描述
     * */
    private JSONObject detail;

    /**
     * 绑定SpuId
     * */
    private Long spuId;

    /**
     * Spu数量
     * */
    private Integer spuCount;

    /**
     * 奖励积分类型
     * */
    private Integer coinType;

    /**
     * 奖励积分数量
     * */
    private BigDecimal coinCount;

    /**
     * 活动状态
     * */
    private Integer status;

    /**
     * 活动排序
     * */
    private Integer sort;

    /**
     * 活动开始时间
     * */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime started;

}

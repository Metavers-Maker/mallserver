package com.muling.mall.pms.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SpuInfoDTO {

    /**
     * spuId
     */
    private Long id;
    /**
     * subjctId
     */
    private Long subjectId;
    /**
     * 藏品名称
     */
    private String name;
    /**
     * 合约地址
     */
    private String contract;
    /**
     * 藏品类型
     */
    private Integer type;
    /**
     * 藏品图片
     */
    private String picUrl;
    /**
     * 资源类型
     */
    private Integer sourceType;
    /**
     * 资源链接
     */
    private String sourceUrl;
    /**
     * 产品苹果ID
     */
    private String productId;
    /**
     *
     */
    private Integer bind;
    /**
     * 藏品发行量
     */
    private Integer total;
    /**
     * 藏品发行价格
     */
    private Long price;
    /**
     * 最大限购数
     */
    private Integer buyMax;
    /**
     * 发行状态 0未发行，1已发行
     */
    private Integer publishStatus;
    /**
     * 藏品状态 0未铸造，1铸造中，2已铸造
     */
    private Integer mintStatus;
    /**
     * 开始时间
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime started;
}

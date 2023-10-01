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
public class PmsSpu extends BaseEntity {
    /**
     * 藏品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 藏品名称
     */
    private String name;
    /**
     * 藏品展示图
     */
    private String picUrl;
    /**
     * 藏品资源类型
     */
    private Integer sourceType;
    /**
     * 资源Url
     */
    private String sourceUrl;
    /**
     * 藏品类型（0-NFT，1-盲盒）
     */
    private Integer type;
    /**
     * 藏品细节描述
     */
    private JSONObject detail;
    /**
     * 藏品发行量
     */
    private Integer total;
    /**
     * 藏品销售量
     */
    private Integer sales;
    /**
     * 系列Id
     */
    private Long subjectId;
    /**
     * 藏品作者Id
     */
    private Long brandId;
    /**
     * 藏品发行方Id
     */
    private Long publishId;
    /**
     * 藏品合约地址
     */
    private String contract;
    /**
     * 合约快速标识
     */
    private String symbol;
    /**
     * 元数据链接
     */
    private String metadataUrl;
    /**
     * 铸造状态
     */
    private BindEnum bind;
    /**
     * 藏品发行价格
     */
    private Long price;
    /**
     * 藏品发行时间
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime started;
    /**
     * 最大限购数量
     */
    private Integer buyMax;
    /**
     * 是否为开发版
     */
    private Integer dev;
    /**
     * 是否可见
     */
    private ViewTypeEnum visible;
    /**
     * 藏品状态
     */
    private StatusEnum status;
    /**
     * 上链操作ID
     */
    private String operationId;
    /**
     * 发行状态 0未发行，1已发行
     */
    private Integer publishStatus;
    /**
     * 藏品状态 0未铸造，1铸造中，2已铸造
     */
    private Integer mintStatus;
    /**
     * 扩展字段
     */
    private JSONObject ext;
}

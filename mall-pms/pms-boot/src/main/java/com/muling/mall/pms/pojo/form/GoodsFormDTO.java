package com.muling.mall.pms.pojo.form;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.mall.pms.enums.BindEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 */
@Data
public class GoodsFormDTO {
    /**
     * 商品名称
     * */
    private String name;
    /**
     * 商品图片
     * */
    private String picUrl;
    /**
     *  资源类型，见
     *  @see com.muling.mall.pms.common.enums.SourceTypeEnum
     * */
    private Integer sourceType;
    /**
     *  资源链接
     * */
    private String sourceUrl;
    /**
     *  NFT类型 商品类型：0-NFT 1-盲盒
     * */
    private Integer type;
    /**
     *  细节描述
     * */
    private JSONObject detail;
    /**
     * 系列Id
     * */
    private Long subjectId;
    /**
     * 作者ID
     * */
    private Long brandId;
    /**
     * 发行方ID
     * */
    private Long publishId;
    /**
     * 合约地址
     * */
    private String contract;
    /**
     * 合约快速标识
     * */
    private String symbol;
    /**
     * 元数据链接
     * */
    private String metadataUrl;
    /**
     * 是否绑定合约
     * */
    private BindEnum bind;
    /**
     * 发行总量
     * */
    @ApiModelProperty("发行量【展示】")
    private Integer total;
    /**
     * 销售量
     * */
    @ApiModelProperty("销量【展示】")
    private Integer sales;
    /**
     * 价格
     * */
    private Long price;
    /**
     * 发售时间
     * */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime started;
    /**
     * 最大限购数量
     */
    private Integer buyMax;
//    /**
//     * 是否为开发版本 0 正式 1开发
//     * */
//    private Integer dev;
//    /**
//     * 是否可见 0 不可见，1可见
//     * */
//    private Integer visible;
//    /**
//     * 状态 0 下架，1上架，2关闭销售
//     * */
//    private Integer status;
    /**
     * 扩展信息
     * */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private JSONObject ext;
}

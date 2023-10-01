package com.muling.mall.pms.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品分页对象
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2021/8/8
 */
@ApiModel("商品分页对象")
@Data
public class GoodsPageVO {

    @ApiModelProperty("商品ID")
    private Long id;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("系列ID")
    private Long subjectId;

    @ApiModelProperty("作者ID")
    private Long brandId;

    @ApiModelProperty("发行方ID")
    private Long publishId;

    @ApiModelProperty("商品价格(单位：分)")
    private Long price;

    @ApiModelProperty("最大限购数")
    private Integer buyMax;

    @ApiModelProperty("资源类型(0-图片 1-视频 2-3D模型 3-音频)")
    private Integer sourceType;

    @ApiModelProperty("资源链接")
    private String sourceUrl;

    @ApiModelProperty("商品类型(0-NFT 1-盲盒)")
    private Integer type;

    @ApiModelProperty("详细内容")
    private Object detail;

    @ApiModelProperty("销量")
    private Integer sales;

    @ApiModelProperty("总量")
    private Integer total;

    @ApiModelProperty("图片地址")
    private String picUrl;

    @ApiModelProperty("合约地址")
    private String contract;

    @ApiModelProperty("合约快速标识")
    private String symbol;

    @ApiModelProperty("扩展字段")
    private Object ext;

    @ApiModelProperty("绑定")
    private Integer bind;

    @ApiModelProperty("开售时间")
    private Long started;

    @ApiModelProperty("更新时间")
    private Long updated;

    @ApiModelProperty("作者信息")
    private BrandInfo brandInfo;

    @ApiModelProperty("发行方信息")
    private PublishInfo publishInfo;

    @Data
    public static class BrandInfo {
        private String name;
        private String picUrl;
        private Integer spuCount;
        private Integer sellCount;
        private String simpleDsp;
        private String dsp;
    }

    @Data
    public static class PublishInfo {
        private String name;
        private String picUrl;
        private String simpleDsp;
        private String dsp;
    }
    //
}

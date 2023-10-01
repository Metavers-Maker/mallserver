package com.muling.mall.oms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 订单详情表（1级订单，2级订单，都在这里）
 */
@Data
@Accessors(chain = true)
public class OmsOrder extends BaseEntity {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单类型 0 1级订单，1 2级订单
     */
    private Integer orderType;
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 订单图片
     */
    private String picUrl;
    /**
     * 订单总额（分）
     */
    private Long totalAmount;
    /**
     * 商品总数
     */
    private Integer totalQuantity;
    /**
     * 订单来源[0->PC订单；1->app订单]
     */
    private Integer sourceType;
    /**
     * 订单状态【101->待付款；102->用户取消；103->系统取消；201->已付款；202->申请退款；203->已退款；301->待发货；401->已发货；501->用户收货；502->系统收货；901->已完成】
     */
    private Integer status;
    /**
     * 订单名称(商品名)
     */
    private String orderName;
    /**
     * 订单备注
     */
    private String remark;
    /**
     * 付款方会员id
     */
    private Long memberId;
    /**
     * 应付总额（分）
     */
    private Long payAmount;
    /**
     * 收款方会员id
     */
    private Long receiveId;
    /**
     * 手续费(数量)
     */
    private Long feeCount;
    /**
     * 支付时间
     */
    private Date payTime;
    /**
     * 支付方式【1->微信jsapi;2->支付宝;3->苹果;4->微信app;5->ADA支付;6->ADA微信预支付;7->杉德支付;8->;9->免费发放】
     */
    private Integer payType;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 微信支付订单号
     */
    private String transactionId;
    /**
     * 商户退款单号
     */
    private String outRefundNo;
    /**
     * 微信支付退款单号
     */
    private String refundId;
    /**
     * 运费金额（分）
     */
    private Long freightAmount;
    /**
     * 发货时间
     */
    private Date deliveryTime;
    /**
     * 确认收货时间
     */
    private Date receiveTime;
    /**
     * 评价时间
     */
    private Date commentTime;
    /**
     * 取消原因
     */
    private String reason;
    /**
     * 逻辑删除标识(1:已删除；0:正常)
     */
    private Integer deleted;

    @TableField(exist = false)
    private List<OmsOrderItem> orderItems;
}

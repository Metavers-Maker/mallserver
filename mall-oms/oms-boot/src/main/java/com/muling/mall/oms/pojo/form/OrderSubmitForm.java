package com.muling.mall.oms.pojo.form;

import com.muling.mall.oms.pojo.dto.OrderItemDTO;
import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 订单提交表单对象
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2021/1/16
 */
@Data
@ApiModel
public class OrderSubmitForm {

    /**
     * 订单类型 0-1级市场，1-2级市场
     */
    @ApiModelProperty("订单类型")
    private Integer orderType;

    /**
     * 提交订单确认页面签发的令牌
     */
    @ApiModelProperty("提交订单确认页面签发的令牌")
    private String orderSn;

    /**
     * 订单总金额-用于验价(单位：分)
     */
    @ApiModelProperty("订单总金额-用于验价(单位：分)")
    private Long totalAmount;

    /**
     * 支付金额(单位：分)
     */
    @ApiModelProperty("支付金额(单位：分)")
    private Long payAmount;

    /**
     * 订单的商品明细
     */
    @ApiModelProperty("订单的商品明细")
    private List<OrderItemDTO> orderItems;

    /**
     * 订单备注
     */
    @ApiModelProperty("订单备注")
    @Size(max = 500, message = "订单备注长度不能超过500")
    private String remark;

}

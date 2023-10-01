package com.muling.mall.oms.pojo.vo;

import com.muling.mall.oms.pojo.dto.OrderItemDTO;
import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@ApiModel("订单确认视图层对象")
@Data
public class OrderConfirmVO {

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("物品图片")
    private String picUrl;

    @ApiModelProperty("物品数量")
    private Integer count;

    @ApiModelProperty("订单明细")
    private List<OrderItemDTO> orderItems;

//    @ApiModelProperty("会员收获地址列表")
//    private List<MemberAddressDTO> addresses;

}

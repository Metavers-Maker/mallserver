package com.muling.mall.oms.event;

import com.muling.mall.oms.pojo.dto.OrderItemDTO;
import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OrderCreateEvent {
    private Long memberId;
    private String OrderSn;
    private Integer orderType;
    private Long skuId;
    private Integer status;
    private Integer sourceType;
    private String remark;
    private Long payAmount;
    private Integer totalQuantity;
    private Long totalAmount;
    List<OrderItemDTO> orderItems;
    private MemberAddressDTO deliveryAddress;
}

package com.muling.mall.oms.controller.admin;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.util.DateUtil;
import com.muling.common.util.ValidateUtil;
import com.muling.common.web.util.ExcelExportUtil;
import com.muling.mall.oms.constant.OrderHeader;
import com.muling.mall.oms.pojo.dto.OrderDTO;
import com.muling.mall.oms.pojo.dto.OrderExportDTO;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.entity.OmsOrderItem;
import com.muling.mall.oms.pojo.query.OrderPageQuery;
import com.muling.mall.oms.pojo.vo.OrderPageVO;
import com.muling.mall.oms.service.IOrderItemService;
import com.muling.mall.oms.service.IOrderService;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberListByIds;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.muling.common.util.DateUtil.YYYY_MM_DD_HH_MM_SS;

@Api(tags = "admin-订单管理")
@RestController("OrderController")
@RequestMapping("/api/v1/orders")
@Slf4j
@AllArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    private final IOrderItemService orderItemService;

    private final MemberFeignClient memberFeignClient;

    @ApiOperation("订单分页列表")
    @GetMapping
    public PageResult listOrderPages(OrderPageQuery queryParams) {
        IPage<OrderPageVO> result = orderService.listOrderPages(queryParams);
        return PageResult.success(result);
    }

    @ApiOperation(value = "订单详情")
    @GetMapping("/detail/{orderId}")
    public Result getOrderDetail(
            @ApiParam("订单ID") @PathVariable Long orderId
    ) {
        OrderDTO orderDTO = new OrderDTO();
        OmsOrder order = orderService.getById(orderId);
        List<OmsOrderItem> orderItems = orderItemService.list(
                new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, orderId)
        );
        orderItems = Optional.ofNullable(orderItems).orElse(new ArrayList<>());
        orderDTO.setOrder(order).setOrderItems(orderItems);
        return Result.success(orderDTO);
    }

    @ApiOperation(value = "根据ORDER_ID获取ORDER列表")
    @GetMapping("/ids")
    public Result getOrders(
            @ApiParam("ORDER IDS") @RequestParam(required = true) List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty() || orderIds.size() > 10) {
            return Result.failed("Order ID不能为空，且不能超过10个");
        }

        List<OrderDTO> orders = new ArrayList<>();
        for (int i = 0; i < orderIds.size(); i++) {
            OrderDTO orderDTO = new OrderDTO();
            // 订单
            OmsOrder order = orderService.getById(orderIds.get(i));

            // 订单明细
            List<OmsOrderItem> orderItems = orderItemService.list(
                    new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, order.getId())
            );
            orderItems = Optional.ofNullable(orderItems).orElse(new ArrayList<>());

            orderDTO.setOrder(order).setOrderItems(orderItems);
            orders.add(orderDTO);
        }
        return Result.success(orders);
    }

    @ApiOperation("订单退款")
    @PutMapping("/{orderId}/refund")
    public Result refund(@PathVariable Long orderId) {
        boolean result = orderService.refundOrder(orderId);
        return Result.judge(result);
    }

    @ApiOperation("拒绝申请退款")
    @PutMapping("/{orderId}/reject-apply-refund")
    public Result rejectApplyRefund(@PathVariable Long orderId) {
        boolean result = orderService.rejectApplyRefundOrder(orderId);
        return Result.judge(result);
    }

    @ApiOperation("模拟支付成功")
    @GetMapping("/paysuccess/mock")
    public Result test(@ApiParam("订单编号") @RequestParam String orderNo) {
        OmsOrder order = orderService.getOne(new QueryWrapper<OmsOrder>().eq(orderNo != null, "order_sn", orderNo));

        orderService.payOrderSuccess(order);
//        rabbitTemplate.convertAndSend("order.exchange", "order.create.routing.key", "4acd475a-c6aa-4d9a-a3a5-40da7472cbee");
        return Result.success();
    }

    /**
     * 导出订单
     *
     * @param response
     */
    @ApiOperation(value = "导出订单")
    @GetMapping("/export")
    public void export(HttpServletResponse response, OrderPageQuery queryParams) throws IOException {
        try {
            IPage<OrderPageVO> result = orderService.listOrderPages(queryParams);

            List<OrderExportDTO> list = new ArrayList<>();

            if (ValidateUtil.isNotEmpty(result.getRecords())) {
                List<Long> memberIds = result.getRecords().stream().map(OrderPageVO::getMemberId).collect(Collectors.toList());

                Result<List<MemberDTO>> memberResult = memberFeignClient.listByIds(new MemberListByIds().setMemberIds(memberIds));

                Assert.notEmpty(memberResult.getData());

                Map<Long, String> memberId2MobileMap = memberResult.getData().stream().collect(Collectors.toMap(o -> o.getId(), o -> o.getMobile()));

                for (OrderPageVO omsOrder : result.getRecords()) {
                    for (OrderPageVO.OrderItem item : omsOrder.getOrderItems()) {
                        list.add(new OrderExportDTO().setOrderSn(omsOrder.getOrderSn())
                                .setSpuName(item.getSpuName())
                                .setSpuId(item.getSpuId())
                                .setMemberId(omsOrder.getMemberId())
                                .setMemberMobile(memberId2MobileMap.get(omsOrder.getMemberId())));
                    }
                }
            }

            ExcelExportUtil.exportCsv(response, OrderHeader.headers(), list, "订单导出-" + DateUtil.format(new Date(), YYYY_MM_DD_HH_MM_SS) + ".csv");
        } catch (Exception e) {
            response.reset();
        }
    }

    //
}

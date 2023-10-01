package com.muling.mall.oms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.base.IBaseEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.common.web.util.RequestUtils;
import com.muling.mall.oms.enums.PayTypeEnum;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.form.*;
import com.muling.mall.oms.pojo.query.OrderPageQuery;
import com.muling.mall.oms.pojo.vo.OrderConfirmVO;
import com.muling.mall.oms.pojo.vo.OrderPageVO;
import com.muling.mall.oms.pojo.vo.OrderSubmitVO;
import com.muling.mall.oms.service.IOrderPayService;
import com.muling.mall.oms.service.IOrderService;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import io.swagger.annotations.*;
import jodd.util.ArraysUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Api(tags = "app-订单管理")
@RestController
@RequestMapping("/app-api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    final IOrderService orderService;

    final IOrderPayService payService;

    final MemberFeignClient memberFeignClient;

    @ApiOperation("分页列表")
    @GetMapping
    public PageResult listOrderPages(OrderPageQuery queryParams) {
        Long memberId = MemberUtils.getMemberId();
        queryParams.setMemberId(memberId);
        IPage<OrderPageVO> result = orderService.listOrderPages(queryParams);
        return PageResult.success(result);
    }

    @ApiOperation(value = "订单详情")
    @GetMapping("/{orderId}")
    public Result getOrderDetail(
            @ApiParam("订单Id") @PathVariable Long orderId
    ) {
        // 订单
        OmsOrder order = orderService.findById(orderId);
        if (order == null) {
            return Result.failed("订单不存在");
        }
        return Result.success(order);
    }

    @ApiOperation("1级申请订单")
    @PostMapping("/_confirm")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<OrderConfirmVO> confirm(@Valid @RequestBody OrderConfirmForm orderConfirmForm) {
        Long memberId = MemberUtils.getMemberId();
        Result<MemberDTO> member = memberFeignClient.getMemberById(memberId);
//        if (!ArrayUtil.contains(GlobalConstants.MOBILE_LOGIN_WHITE_LIST, member.getData().getMobile())) {
//            return Result.failed("您没有购买权限");
//        }
        OrderConfirmVO result = orderService.confirm(orderConfirmForm);
        return Result.success(result);
    }

    @ApiOperation("1级提交订单(秒杀)")
    @PostMapping("/_seckill")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result secKill(@Valid @RequestBody @ApiParam("订单秒杀实体") OrderSubmitForm orderSubmitForm) {
        OrderSubmitVO result = orderService.submit(orderSubmitForm);
        return Result.success(result);
    }

    @ApiOperation("2级申请订单")
    @PostMapping("/market/_confirm")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<OrderConfirmVO> confirmMarket(@Valid @RequestBody OrderM2ConfirmForm m2ConfirmForm) {
        Long memberId = MemberUtils.getMemberId();
        Result<MemberDTO> member = memberFeignClient.getMemberById(memberId);
//        if (!ArrayUtil.contains(GlobalConstants.MOBILE_LOGIN_WHITE_LIST, member.getData().getMobile())) {
//            return Result.failed("您没有购买权限");
//        }
        OrderConfirmVO result = orderService.confirmMarket(m2ConfirmForm);
        return Result.success(result);
    }

    @ApiOperation("2级提交订单")
    @PostMapping("/_submit")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result submitMarket(@Valid @RequestBody @ApiParam("订单实体") OrderSubmitForm orderSubmitForm) {
        OrderSubmitVO result = orderService.submitMarket(orderSubmitForm);
        return Result.success(result);
    }

    @ApiOperation("订单取消")
    @PutMapping("/cancel")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result cancel(@RequestBody OrderCancelForm orderCancelForm) {
        boolean result = payService.cancelOrder(orderCancelForm);
        return Result.judge(result);
    }

    @ApiOperation("订单支付")
    @PostMapping("/{orderId}/_pay")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "payType", value = "支付方式", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "appId", value = "小程序appId", paramType = "query", dataType = "String")
    })
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public <T> Result<T> pay(@PathVariable Long orderId, Integer payType, String appId, String channel) {
        PayTypeEnum payTypeEnum = IBaseEnum.getEnumByValue(payType, PayTypeEnum.class);
        Integer[] payTypes = {
                PayTypeEnum.WEIXIN_JSAPI.getValue(),
                PayTypeEnum.ALIPAY.getValue(),
                PayTypeEnum.APPLEPAY.getValue(),
                PayTypeEnum.ADA_PAY.getValue(),
                PayTypeEnum.ADA_PAY_WEI_XIN.getValue(),
                PayTypeEnum.FREE_PAY.getValue(),
                PayTypeEnum.SAND_PAY.getValue()
        };
        if (payTypeEnum == null || !ArraysUtil.contains(payTypes, payType)) {
            return Result.failed(ResultCode.NO_SUPPORT_PAY);
        }
        //获取请求IP参数
        String ip = RequestUtils.getIp();
        return Result.success(payService.pay(ip, orderId, appId, payTypeEnum, channel));
    }

    @ApiOperation("查询")
    @GetMapping("/{orderSn}/_query")
    public Result<OmsOrder> alipayQuery(@PathVariable String orderSn) {
        OmsOrder order = orderService.findByOrderSn(orderSn);
        if (order == null) {
            return Result.failed(ResultCode.ORDER_GET_WAITING);
        }
        return Result.success(order);
    }

    @ApiOperation("订单删除")
    @DeleteMapping("/delete")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result deleteOrder(@RequestBody OrderDeleteForm orderDeleteForm) {
        boolean result = orderService.deleteOrder(orderDeleteForm.getOrderId());
        return Result.judge(result);
    }

    @ApiOperation("申请退款")
    @PutMapping("/apply-refund")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result refund(@RequestBody OrderRefundForm orderRefundForm) {
        boolean result = orderService.applyRefundOrder(orderRefundForm);
        return Result.judge(result);
    }

    @ApiOperation("取消申请退款")
    @PutMapping("/{orderId}/cancel-apply-refund")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result cancelRefund(@PathVariable Long orderId) {
        boolean result = orderService.cancelApplyRefundOrder(orderId);
        return Result.judge(result);
    }

//    //订单成功
//    @ApiOperation("测试订单成功")
//    @PutMapping("/success")
//    public Result success(@RequestBody OrderSuccessForm orderSuccessForm) {
//        boolean result = orderService.successOrder(orderSuccessForm);
//        return Result.judge(result);
//    }

    @ApiOperation("测试")
    @GetMapping(value = "/success", produces = MediaType.TEXT_PLAIN_VALUE)
    public String success() {
        return "success";
    }
}

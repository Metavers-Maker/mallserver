package com.muling.mall.oms.controller.rpc;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.muling.common.result.Result;
import com.muling.mall.oms.converter.OrderConverter;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "rpc-订单管理")
@RestController("rpcOrderController")
@RequestMapping("/app-api/v1/rpc/order")
@Slf4j
@AllArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @ApiOperation("订单列表按时间查询")
    @GetMapping("/list")
    public Result list(@RequestParam String begin, @RequestParam String end) {
        Assert.notNull(begin);
        Assert.notNull(end);

        List<OmsOrder> orderList = orderService.list(new LambdaQueryWrapper<OmsOrder>().ge(OmsOrder::getCreated, begin).lt(OmsOrder::getCreated, end));

        if (orderList != null && !orderList.isEmpty()) {
            return Result.success(orderList.stream().map(OrderConverter.INSTANCE::eniity2Dto).collect(Collectors.toList()));
        }
        return Result.success();
    }

}

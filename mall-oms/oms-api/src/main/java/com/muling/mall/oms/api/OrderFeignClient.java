package com.muling.mall.oms.api;

import com.muling.common.result.Result;
import com.muling.mall.oms.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author haoxr
 * @description TODO
 * @createTime 2021/3/13 11:59
 */
@FeignClient("mall-oms")
public interface OrderFeignClient {

    @GetMapping("/app-api/v1/rpc/order/list")
    Result<List<OrderDTO>> list(@RequestParam String begin, @RequestParam String end);
}

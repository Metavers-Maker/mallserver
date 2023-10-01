package com.muling.mall.task.api;

import com.muling.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "mall-task", contextId = "task")
public interface TaskFeignClient {

    @PostMapping("/app-api/v1/v1/rpc/task/draw")
    Result getItemInfo(@RequestParam Long taskId);
}

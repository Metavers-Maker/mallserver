package com.muling.mall.chat.api;

import com.muling.mall.chat.dto.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "mall-im",contextId = "message")
public interface ImFeignClient {

    @PostMapping("/app-api/v1/im/send-message")
    public void sendMessage(@RequestBody final Message message);

    @PostMapping("/app-api/v1/im/send-private-message/{id}")
    public void sendPrivateMessage(@PathVariable final String id,
                                   @RequestBody final Message message);
}


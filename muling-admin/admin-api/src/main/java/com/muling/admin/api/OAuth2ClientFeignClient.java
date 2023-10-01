package com.muling.admin.api;

import com.muling.admin.dto.OAuth2ClientDTO;
import com.muling.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "muling-admin", contextId = "oauth-client")
public interface OAuth2ClientFeignClient {

    @GetMapping("/api/v1/oauth-clients/getOAuth2ClientById")
    Result<OAuth2ClientDTO> getOAuth2ClientById(@RequestParam String clientId);

}

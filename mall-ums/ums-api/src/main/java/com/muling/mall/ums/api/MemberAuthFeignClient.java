package com.muling.mall.ums.api;

import com.muling.common.result.Result;
import com.muling.mall.ums.pojo.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mall-ums", contextId = "auth")
public interface MemberAuthFeignClient {

    /**
     * 根据memberID获取会员认证信息(real)
     *
     * @param memberId
     * @return
     */
    @GetMapping("/app-api/v1/rpc/auth/real/{memberId}")
    Result<MemberRealDTO> getMemberRealById(@PathVariable Long memberId);

    /**
     * 根据memberID获取Sand账户
     *
     * @param memberId
     * @return
     */
    @GetMapping("/app-api/v1/rpc/auth/sand/{memberId}")
    Result<MemberSandDTO> getMemberSandAccount(@PathVariable Long memberId);
    //
}



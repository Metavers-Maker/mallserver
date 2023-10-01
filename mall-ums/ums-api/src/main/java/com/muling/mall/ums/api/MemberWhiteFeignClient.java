package com.muling.mall.ums.api;

import com.muling.common.result.Result;
import com.muling.mall.ums.pojo.dto.MemberWhiteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mall-ums", contextId = "white")
public interface MemberWhiteFeignClient {

    /**
     * 根据memberID获取会员白名单信息（权益）
     *
     * @param memberId
     * @return
     */
    @GetMapping("/app-api/v1/rpc/white/{memberId}")
    Result<MemberWhiteDTO> getMemberWhiteById(@PathVariable Long memberId);

}



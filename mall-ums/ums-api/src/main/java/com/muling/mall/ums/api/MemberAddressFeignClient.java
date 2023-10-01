package com.muling.mall.ums.api;

import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import com.muling.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 会员地址 Feign 客户端
 *
 * @author haoxr
 * @date 2022/2/12
 */
@FeignClient(name = "mall-ums", contextId = "address")
public interface MemberAddressFeignClient {

    /**
     * 获取当前会员地址列表
     *
     * @return
     */
    @GetMapping("/app-api/v1/rpc/addresses")
    Result<List<MemberAddressDTO>> listCurrMemberAddresses();

}



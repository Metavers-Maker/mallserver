package com.muling.mall.ums.api;

import com.muling.common.result.Result;
import com.muling.mall.ums.pojo.dto.MemberBankDTO;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 会员银行 Feign 客户端
 *
 */
@FeignClient(name = "mall-ums", contextId = "bank")
public interface MemberBankFeignClient {

    /**
     * 获取当前会员银行卡信息
     *
     * @return
     */
    @GetMapping("/app-api/v1/rpc/bank/{id}")
    Result<MemberBankDTO> getBankInfo(@PathVariable Long bankId);

}



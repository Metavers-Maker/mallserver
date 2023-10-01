package com.muling.mall.ums.controller.rpc;

import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import com.muling.mall.ums.service.IUmsAddressService;
import com.muling.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "rpc-会员地址")
@RestController("RpcAddressController")
@RequestMapping("/app-api/v1/rpc/addresses")
@Slf4j
@RequiredArgsConstructor
public class AddressController {

    private final IUmsAddressService addressService;

    @ApiOperation(value = "获取当前会员地址列表")
    @GetMapping
    public Result<List<MemberAddressDTO>> listCurrentMemberAddresses() {
        List<MemberAddressDTO> addressList = addressService.listCurrentMemberAddresses();
        return Result.success(addressList);
    }

}

package com.muling.mall.ums.controller.rpc;

import com.muling.mall.ums.converter.BankConverter;
import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import com.muling.mall.ums.pojo.dto.MemberBankDTO;
import com.muling.mall.ums.pojo.entity.UmsBank;
import com.muling.mall.ums.service.IUmsAddressService;
import com.muling.common.result.Result;
import com.muling.mall.ums.service.IUmsBankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "rpc-会员银行信息")
@RestController("RpcMemberBankController")
@RequestMapping("/app-api/v1/rpc/bank")
@Slf4j
@RequiredArgsConstructor
public class MemberBankController {

    private final IUmsBankService bankService;

    @ApiOperation(value = "获取当前会员银行信息")
    @GetMapping("/{id}")
    public Result<MemberBankDTO> getBankInfo(@PathVariable Long bankId) {
        UmsBank umsBank = bankService.getById(bankId);
        MemberBankDTO bankDTO = BankConverter.INSTANCE.pos2dto(umsBank);
        return Result.success(bankDTO);
    }

}

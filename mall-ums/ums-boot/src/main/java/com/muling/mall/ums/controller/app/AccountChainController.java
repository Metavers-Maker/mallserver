package com.muling.mall.ums.controller.app;

import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.enums.AccountChainEnum;
import com.muling.mall.ums.pojo.dto.MemberAccountChainDTO;
import com.muling.mall.ums.pojo.form.AddressChainForm;
import com.muling.mall.ums.pojo.form.AddressChainUnbindForm;
import com.muling.mall.ums.pojo.form.RegisterForm;
import com.muling.mall.ums.pojo.form.ResetPasswordForm;
import com.muling.mall.ums.service.IUmsAccountChainService;
import com.muling.mall.ums.service.IUmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "app-关联账户")
@RestController
@RequestMapping("/app-api/v1/chain/account")
@Slf4j
@RequiredArgsConstructor
public class AccountChainController {

    private final IUmsAccountChainService umsAccountChainService;

    @ApiOperation(value = "获取用户账户地址列表")
    @GetMapping("/list")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result<List<MemberAccountChainDTO>> list(
            @ApiParam(value = "三方账户类型", example = "4") Integer chainType
    ) {
        List<MemberAccountChainDTO> result = umsAccountChainService.listAccount(chainType);
        return Result.success(result);
    }

    @ApiOperation(value = "生成bsn账户")
    @PostMapping("/bsn/gen")
    @RequestLimit(count = 5, waits = 1, limitFiledType = RequestLimit.LimitFiledType.IP)
    public Result<String> genBSNAddr() {
        log.info("gen bsn acount");
        Result<String> ret = umsAccountChainService.genBsnAccount();
        return ret;
    }

    @ApiOperation(value = "绑定关联账户")
    @PostMapping("/third/bind")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result bind(@Validated @RequestBody AddressChainForm addressChainForm) {
//        if (addressChainForm.getChainType() != AccountChainEnum.ACCOUNT_ETH.getValue()) {
//            return Result.judge(false);
//        }
        Long memberId = MemberUtils.getMemberId();
        boolean result = umsAccountChainService.bindAccount(addressChainForm, memberId);
        return Result.judge(result);
    }

    @ApiOperation(value = "解绑关联账户")
    @PostMapping("/third/unbind")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result unbind(@Validated @RequestBody AddressChainUnbindForm chainUnbindForm) {
        Long memberId = MemberUtils.getMemberId();
        boolean result = umsAccountChainService.unbindAccount(chainUnbindForm, memberId);
        return Result.judge(result);
    }

    @ApiOperation(value = "更新关联账户")
    @PostMapping("/third/update/{id}")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result update(
            @PathVariable Long id,
            @Validated @RequestBody AddressChainForm addressChainForm) {
        if (addressChainForm.getChainType() != AccountChainEnum.ACCOUNT_ETH.getValue()) {
            return Result.judge(false);
        }
        Long memberId = MemberUtils.getMemberId();
        boolean result = umsAccountChainService.updateAccount(id, addressChainForm, memberId);
        return Result.judge(result);
    }
    //

}

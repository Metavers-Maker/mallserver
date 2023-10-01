package com.muling.mall.ums.controller.app;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ReUtil;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.es.service.IUmsMemberAuthEService;
import com.muling.mall.ums.pojo.form.MemberAuthCreateForm;
import com.muling.mall.ums.pojo.vo.MemberAuthVO;
import com.muling.mall.ums.service.IUmsMemberAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(tags = "app-会员认证")
@RestController
@RequestMapping("/app-api/v1/members/auth")
@Slf4j
public class MemberAuthController {

    @Resource
    private IUmsMemberAuthService umsMemberAuthService;

    @Resource
    private IUmsMemberAuthEService umsMemberAuthEService;

    @ApiOperation(value = "创建认证信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberAuthCreateForm", value = "memberAuthCreateForm", required = true, paramType = "body", dataType = "MemberAuthCreateForm", dataTypeClass = MemberAuthCreateForm.class)
    })
    @PostMapping
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result create(@Valid @RequestBody MemberAuthCreateForm memberAuthCreateForm) throws Exception {
        Long memberId = MemberUtils.getMemberId();
        // 判断身份证号是否合法
        if (!IdcardUtil.isValidCard(memberAuthCreateForm.getIdCard())) {
            return Result.failed(ResultCode.PARAM_ERROR, "身份证号不合法");
        }
        boolean chinese = isChineseName(memberAuthCreateForm.getRealName());
        if (!chinese) {
            return Result.failed(ResultCode.PARAM_ERROR, "姓名必须为中文");
        }
        boolean result = umsMemberAuthService.create(memberId, memberAuthCreateForm);
        return Result.success(result);
    }

//    @ApiOperation(value = "重新认证")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "memberAuthCreateForm", value = "memberAuthCreateForm", required = true, paramType = "body", dataType = "MemberAuthCreateForm", dataTypeClass = MemberAuthCreateForm.class)
//    })
//    @PostMapping("/re-cert")
//    @RequestLimit(count = 5, time = 24 * 3600, waits = 5, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
//    public Result reCert(@Valid @RequestBody MemberAuthCreateForm memberAuthCreateForm) {
//        Long memberId = MemberUtils.getMemberId();
//        // 判断身份证号是否合法
//        if (!IdcardUtil.isValidCard(memberAuthCreateForm.getIdCard())) {
//            return Result.failed(ResultCode.PARAM_ERROR, "身份证号不合法");
//        }
//        boolean chinese = isChineseName(memberAuthCreateForm.getRealName());
//        if (!chinese) {
//            return Result.failed(ResultCode.PARAM_ERROR, "姓名必须为中文");
//        }
//        boolean result = umsMemberAuthService.reCert(memberId, memberAuthCreateForm);
//        return Result.success(result);
//    }

    @ApiOperation("认证信息")
    @GetMapping
    public Result<MemberAuthVO> memberAuth() {

        Long memberId = MemberUtils.getMemberId();
        MemberAuthVO umsMemberAuth = umsMemberAuthService.queryByMemberId(memberId);
        if (umsMemberAuth == null) {
            return Result.failed(ResultCode.USER_AUTH_NOT_EXIST);
        } else {
            return Result.success(umsMemberAuth);
        }
    }

    public static void main(String[] args) {
        boolean match = ReUtil.isMatch("^((?![\\u3000-\\u303F])[\\u2E80-\\uFE4F]|\\·)*(?![\\u3000-\\u303F])[\\u2E80-\\uFE4F](\\·)*$", "阿里木江·买买提");
        System.out.println(match);

    }

    /**
     * 中文包括少数民族·字符
     *
     * @param name
     * @return
     */
    public boolean isChineseName(String name) {
        return ReUtil.isMatch("^((?![\\u3000-\\u303F])[\\u2E80-\\uFE4F]|\\·)*(?![\\u3000-\\u303F])[\\u2E80-\\uFE4F](\\·)*$", name);
    }
}

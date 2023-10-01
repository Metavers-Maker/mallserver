package com.muling.mall.ums.controller.rpc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.mall.ums.converter.MemberWhiteConverter;
import com.muling.mall.ums.pojo.dto.*;
import com.muling.mall.ums.pojo.entity.UmsWhite;
import com.muling.mall.ums.service.IUmsWhiteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "rpc-会员白名单（权益）")
@RestController("RpcMemberWhiteController")
@RequestMapping("/app-api/v1/rpc/white")
@Slf4j
public class MemberWhiteController {

    @Autowired
    private IUmsWhiteService whiteService;

    @ApiOperation(value = "根据会员ID获取白名单权益信息")
    @GetMapping("/{memberId}")
    Result<MemberWhiteDTO> getMemberRealById(@PathVariable Long memberId) {
        //
        QueryWrapper<UmsWhite> queryWrapper = new QueryWrapper<UmsWhite>()
                .eq("member_id", memberId);
        UmsWhite umsWhite = whiteService.getOne(queryWrapper);
        MemberWhiteDTO memberWhiteDTO = MemberWhiteConverter.INSTANCE.po2dto(umsWhite);
        if (memberWhiteDTO == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberWhiteDTO);
    }

}

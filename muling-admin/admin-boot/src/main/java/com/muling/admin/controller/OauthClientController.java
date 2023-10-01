package com.muling.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.admin.dto.OAuth2ClientDTO;
import com.muling.admin.pojo.entity.SysOauthClient;
import com.muling.admin.pojo.query.OauthClientPageQuery;
import com.muling.admin.service.ISysOauthClientService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Api(tags = "admin-auth2客户端")
@RestController
@RequestMapping("/api/v1/oauth-clients")
@Slf4j
@AllArgsConstructor
public class OauthClientController {

    private ISysOauthClientService oauthClientService;

    @ApiOperation(value = "列表分页")
    @GetMapping
    public PageResult<SysOauthClient> list(OauthClientPageQuery queryParams) {
        IPage<SysOauthClient> result = oauthClientService.page(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                new LambdaQueryWrapper<SysOauthClient>()
                        .like(StrUtil.isNotBlank(queryParams.getClientId()), SysOauthClient::getClientId, queryParams.getClientId()));
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{clientId}")
    public Result detail(@PathVariable String clientId) {
        SysOauthClient client = oauthClientService.getById(clientId);
        return Result.success(client);
    }

    @ApiOperation(value = "创建")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@RequestBody SysOauthClient client) {
        boolean status = oauthClientService.save(client);
        return Result.judge(status);
    }

    @ApiOperation(value = "更新")
    @PutMapping(value = "/{clientId}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result update(
            @PathVariable String clientId,
            @RequestBody SysOauthClient client) {
        boolean status = oauthClientService.updateById(client);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(@PathVariable("ids") String ids) {
        boolean status = oauthClientService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }

    @ApiOperation(hidden = true, value = "获取 OAuth2 客户端认证信息", notes = "Feign 调用")
    @GetMapping("/getOAuth2ClientById")
    public Result<OAuth2ClientDTO> getOAuth2ClientById(@RequestParam String clientId) {
        SysOauthClient client = oauthClientService.getById(clientId);
        Assert.isTrue(client != null, "OAuth2 客户端不存在");
        OAuth2ClientDTO authClientDTO = new OAuth2ClientDTO();
        BeanUtil.copyProperties(client, authClientDTO);
        return Result.success(authClientDTO);
    }
}

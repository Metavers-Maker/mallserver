package com.muling.global.controller.app;

import com.muling.common.result.Result;
import com.muling.global.pojo.vo.UpdateConfigVO;
import com.muling.global.service.IUpdateConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "app-更新配置")
@RestController
@RequestMapping("/app-api/v1/update-config")
@RequiredArgsConstructor
public class UpdateConfigController {

    private final IUpdateConfigService updateConfigService;

    @ApiOperation(value = "获取更新配置列表")
    @GetMapping
    public Result<Map<String, UpdateConfigVO>> list() {
        Map<String, UpdateConfigVO> map = updateConfigService.map();
        return Result.success(map);
    }

}

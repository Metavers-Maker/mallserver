package com.muling.mall.ums.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.Result;
import com.muling.mall.ums.pojo.dto.RelationDTO;
import com.muling.mall.ums.service.IUmsRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "admin-会员关注")
@RestController("RelationController")
@RequestMapping("/api/v1/relation")
@Slf4j
@AllArgsConstructor
public class RelationController {

    private IUmsRelationService followService;

    @ApiOperation(value = "follows")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", paramType = "query", dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "limit", value = "size", paramType = "query", dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "memberId", value = "memberId", paramType = "query", dataType = "Long", dataTypeClass = Long.class)
    })
    @GetMapping("/follows")
    public Result<List<RelationDTO>> follows(Integer page, Integer limit, Long memberId) {
        IPage<RelationDTO> result = followService.follows(new Page<>(page, limit), memberId);
        return Result.success(result.getRecords(), result.getTotal());
    }

    @ApiOperation(value = "fans")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", paramType = "query", dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "limit", value = "size", paramType = "query", dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "memberId", value = "memberId", paramType = "query", dataType = "Long", dataTypeClass = Long.class)
    })
    @GetMapping("/fans")
    public Result<List<RelationDTO>> fans(Integer page, Integer limit, Long memberId) {
        IPage<RelationDTO> result = followService.fans(new Page<>(page, limit), memberId);
        return Result.success(result.getRecords(), result.getTotal());
    }
}

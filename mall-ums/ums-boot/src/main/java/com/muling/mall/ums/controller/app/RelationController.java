package com.muling.mall.ums.controller.app;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.pojo.dto.RelationDTO;
import com.muling.mall.ums.pojo.vo.MemberCoinRank;
import com.muling.mall.ums.service.IUmsRelationService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "app-会员关注")
@RestController
@RequestMapping("/app-api/v1/relation")
@Slf4j
public class RelationController {

    @Autowired
    private IUmsRelationService relationService;

    @ApiOperation(value = "follows")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "size", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "memberId", value = "memberId", paramType = "query", dataType = "Long", dataTypeClass = Long.class)
    })
    @GetMapping
    public Result<Map<String, Object>> follows(Integer page, Integer limit, Long memberId) throws Exception {
        if (memberId == null) {
            memberId = MemberUtils.getMemberId();
        }
        IPage<RelationDTO> p = relationService.follows(new Page<>(page, limit), memberId);

        int followCount = relationService.getFollowCount(memberId);
        int fansCount = relationService.getFansCount(memberId);

        Map<String, Object> maps = Maps.newHashMap();
        maps.put("relations", p.getRecords());
        maps.put("total", p.getTotal());
        maps.put("follows", followCount);
        maps.put("fans", fansCount);

        return Result.success(maps);
    }

    @ApiOperation(value = "fans")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "size", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "memberId", value = "memberId", paramType = "query", dataType = "Long", dataTypeClass = Long.class)
    })
    @GetMapping("/fans")
    public Result<Map<String, Object>> fans(Integer page, Integer limit, Long memberId) throws Exception {
        if (memberId == null) {
            memberId = MemberUtils.getMemberId();
        }
        IPage<RelationDTO> p = relationService.fans(new Page<>(page, limit), memberId);

        int followCount = relationService.getFollowCount(memberId);
        int fansCount = relationService.getFansCount(memberId);

        Map<String, Object> maps = Maps.newHashMap();
        maps.put("relations", p.getRecords());
        maps.put("total", p.getTotal());
        maps.put("follows", followCount);
        maps.put("fans", fansCount);

        return Result.success(maps);
    }

    @ApiOperation(value = "follow")
    @ApiImplicitParam(name = "followId", value = "followId", required = true, paramType = "query", dataType = "Long", dataTypeClass = Long.class)
    @PutMapping("/follow")
    public Result follow(Long followId) {

        Long memberId = MemberUtils.getMemberId();
        if (followId == memberId.longValue()) {
            return Result.failed();
        }
        boolean status = relationService.follow(memberId, followId);
        return Result.judge(status);
    }

    @ApiOperation(value = "unFollow")
    @ApiImplicitParam(name = "followId", value = "followId", required = true, paramType = "query", dataType = "Long", dataTypeClass = Long.class)
    @PutMapping("/unFollow")
    public Result unFollow(Long followId) {

        Long memberId = MemberUtils.getMemberId();
        if (followId == memberId.longValue()) {
            return Result.failed();
        }
        boolean status = relationService.unFollow(memberId, followId);
        return Result.judge(status);
    }

    @ApiOperation(value = "count")
    @ApiImplicitParam(name = "memberId", value = "memberId", required = true, paramType = "query", dataType = "Long", dataTypeClass = Long.class)
    @GetMapping("/count")
    public Result<Map<String, Object>> count(Long memberId) {

        if (memberId == null) {
            memberId = MemberUtils.getMemberId();
        }
        int followCount = relationService.getFollowCount(memberId);
        int fansCount = relationService.getFansCount(memberId);

        Map<String, Object> maps = Maps.newHashMap();
        maps.put("follows", followCount);
        maps.put("fans", fansCount);

        return Result.success(maps);
    }

    @ApiOperation(value = "用户积分排行")
    @GetMapping("/rank")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<List<MemberCoinRank>> rankByCoin(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "积分类型", example = "0") Integer coinType
    ) {
        List<MemberCoinRank> ret = relationService.rankByCoin(pageNum, pageSize, coinType);
        return Result.success(ret);
    }
}

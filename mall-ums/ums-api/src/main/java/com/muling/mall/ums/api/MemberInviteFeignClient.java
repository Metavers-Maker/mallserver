package com.muling.mall.ums.api;

import com.muling.common.result.Result;
import com.muling.mall.ums.pojo.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "mall-ums", contextId = "invite")
public interface MemberInviteFeignClient {

    /**
     * 获取会员信息
     */
    @GetMapping("/app-api/v1/rpc/members/invite/{memberId}")
    Result<MemberInviteDTO> getMemberInviteById(@PathVariable Long memberId);

    /**
     * 获取目标会员上层所有人信息
     */
    @GetMapping("/app-api/v1/rpc/members/invite/depth/{memberId}")
    Result<List<MemberInviteDTO>> getMemberInviteDepthById(@PathVariable Long memberId);

    /**
     * 分红奖励
     */
    @PutMapping("/app-api/v1/rpc/members/invite/star-dispatch")
    Result<Boolean> starDispatch(@RequestBody StarDispatchDTO starDispatch);

    /**
     * 广告任务分红
     */
    @PutMapping("/app-api/v1/rpc/members/invite/ad-mission-dispatch")
    Result<Boolean> adMissionDispatch(@RequestBody AdMissionDispatchDTO adMissionDispatchDTO);

    /**
     * 广告农场分红
     */
    @PutMapping("/app-api/v1/rpc/members/invite/ad-farm-dispatch")
    Result<Boolean> adFarmDispatch(@RequestBody AdFarmDispatchDTO adFarmDispatchDTO);

    /**
     * 小游戏分红
     */
    @PutMapping("/app-api/v1/rpc/members/invite/game-farm-dispatch")
    Result<Boolean> gameFarmDispatch(@RequestBody AdFarmDispatchDTO adFarmDispatchDTO);

}



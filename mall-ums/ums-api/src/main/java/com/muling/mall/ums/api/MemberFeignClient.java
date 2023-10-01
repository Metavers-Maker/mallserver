package com.muling.mall.ums.api;

import com.muling.common.result.Result;
import com.muling.mall.ums.pojo.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-ums", contextId = "member")
public interface MemberFeignClient {

    /**
     * 新增会员
     *
     * @param member
     * @return
     */
    @PostMapping("/app-api/v1/rpc/members")
    Result<Long> addMember(@RequestBody MemberDTO member);

    /**
     * 新增杉德会员账号
     *
     * @return
     */
    @PostMapping("/app-api/v1/rpc/members/sand")
    Result<Long> addSandAccount(@RequestBody MemberSandDTO memberSandDTO);

    /**
     * 绑定Wxopen账号
     *
     * @return
     */
    @PostMapping("/app-api/v1/rpc/members/bind/wxopen")
    Result<Boolean> bindOpenId(@RequestBody BindWxopenDTO wxopenDTO);

    /**
     * 绑定Alipay账号
     *
     * @return
     */
    @PostMapping("/app-api/v1/rpc/members/bind/alipay")
    Result<Boolean> bindOpenId(@RequestBody BindAlipayDTO alipayDTO);

    /**
     * 获取会员的 openid
     *
     * @return
     */
    @PostMapping("/app-api/v1/rpc/members/{memberId}/openid")
    Result<String> getMemberOpenId(@PathVariable Long memberId);

    /**
     * 获取会员信息
     */
    @GetMapping("/app-api/v1/rpc/members/{memberId}")
    Result<MemberDTO> getMemberById(@PathVariable Long memberId);

    /**
     * 获取简单的会员信息
     */
    @GetMapping("/app-api/v1/rpc/members/{memberId}/simple")
    Result<MemberSimpleDTO> getSimpleUserById(@PathVariable Long memberId);

    /**
     * 根据openId获取会员认证信息
     *
     * @param openid
     * @return
     */
    @GetMapping("/app-api/v1/rpc/members/openid/{openid}")
    Result<MemberAuthDTO> loadUserByOpenId(@PathVariable String openid);

    /**
     * 根据alipayid获取会员认证信息
     *
     * @param alipayid
     * @return
     */
    @GetMapping("/app-api/v1/rpc/members/alipayid/{alipayid}")
    Result<MemberAuthDTO> loadUserByAlipayId(@PathVariable String alipayid);

    /**
     * 根据username获取会员认证信息
     *
     * @param username
     * @return
     */
    @GetMapping("/app-api/v1/rpc/members/username/{username}")
    Result<MemberAuthDTO> loadUserByUsername(@PathVariable String username);

    /**
     * 根据uid获取会员认证信息
     *
     * @param uid
     * @return
     */
    @GetMapping("/app-api/v1/rpc/members/uid/{uid}")
    Result<MemberAuthDTO> loadUserByUid(@PathVariable String uid);

    /**
     * 根据手机号获取会员认证信息
     *
     * @param mobile
     * @return
     */
    @GetMapping("/app-api/v1/rpc/members/mobile/{mobile}")
    Result<MemberAuthDTO> loadUserByMobile(@PathVariable String mobile);


    /**
     * 根据邮箱获取会员认证信息
     *
     * @param email
     * @return
     */
    @GetMapping("/app-api/v1/rpc/members/email/{email}")
    Result<MemberAuthDTO> loadUserByEmail(@PathVariable String email);

    /**
     * 根据时间获取用户列表
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/app-api/v1/rpc/members/list")
    Result<List<MemberDTO>> list(@RequestParam String begin, @RequestParam String end);


    /**
     * 绑定Wxopen账号
     *
     * @return
     */
    @PostMapping("/app-api/v1/rpc/members/listByIds")
    Result<List<MemberDTO>> listByIds(@RequestBody MemberListByIds memberIds);

}



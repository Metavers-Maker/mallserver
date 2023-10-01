package com.muling.mall.ums.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.ums.pojo.app.MemberSearchDTO;
import com.muling.mall.ums.pojo.dto.*;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.form.*;
import com.muling.mall.ums.pojo.vo.MemberSimpleVO;
import com.muling.mall.ums.pojo.vo.MemberVO;

public interface IUmsMemberService extends IService<UmsMember> {

    /**
     * 新增会员
     *
     * @param member
     * @return
     */
    Long addMember(MemberDTO member);

    /**
     * 绑定微信开放平台
     *
     * @param wxopenForm
     * @return
     */
    boolean bindWxopenDirect(BindWxopenForm wxopenForm);
    boolean unbindWxopenDirect(BindWxopenForm wxopenForm);

    /**
     * 绑定微信开放平台
     *
     * @param wxopenDTO
     * @return
     */
    boolean bindWxopen(BindWxopenDTO wxopenDTO);

    /**
     * 绑定支付宝
     *
     * @param alipayDTO
     * @return
     */
    boolean bindAlipay(BindAlipayDTO alipayDTO);

    public Long adminAddMember(AdminRegisterForm adminRegisterForm);

    public boolean register(RegisterForm registerForm);

    public boolean unregister(UnRegisterForm form);

    public boolean resetPassword(ResetPasswordForm resetPasswordForm);

    public boolean resetTradePassword(ResetTradePasswordForm tradePasswordForm);

    public boolean checkTradePassword(String password);

    IPage<UmsMember> list(Page<UmsMember> page, String nickname, String mobile);

    public IPage<MemberSearchDTO> search(Page<MemberSearchDTO> page, String name);

    public MemberSimpleDTO getSimpleById(Long userId);

    public MemberVO getCurrentMemberInfo();

    public MemberSimpleVO getMemberInfoByUid(String uid);

    public MemberSimpleVO getMemberInfoById(Long id);

    /**
     * 根据手机号获取会员认证信息
     *
     * @param mobile
     * @return
     */
    MemberAuthDTO getByMobile(String mobile);

    /**
     * 根据 微信openid 获取会员认证信息
     *
     * @param openid
     * @return
     */
    MemberAuthDTO getByOpenid(String openid);


    /**
     * 根据 支付宝alipayId 获取会员认证信息
     *
     * @param alipayId
     * @return
     */
    MemberAuthDTO getByAlipayId(String alipayId);

    /**
     * 根据 email 获取会员认证信息
     *
     * @param email
     * @return
     */
    MemberAuthDTO getByEmail(String email);

    /**
     * 根据 username 获取会员认证信息
     *
     * @param username
     * @return
     */
    MemberAuthDTO getByUsername(String username);

    /**
     * 根据 uid 获取会员认证信息
     *
     * @param uid
     * @return
     */
    public MemberAuthDTO getByUId(String uid);

    /**
     * 根据 inviteCode 获取会员信息
     *
     * @param inviteCode
     * @return
     */
    public UmsMember getByInviteCode(String inviteCode);

    /**
     * 封号
     *
     * @param status
     * @return
     */
    public boolean feng(Long memberId,Integer status);

}

package com.muling.mall.ums.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.ums.event.MemberAuthSuccessEvent;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsMemberAuth;
import com.muling.mall.ums.pojo.form.MemberAuthCreateForm;
import com.muling.mall.ums.pojo.vo.MemberAuthVO;
import com.muling.mall.ums.util.ResponseAuth;
import com.muling.mall.ums.util.ResponseDTO;

public interface IUmsMemberAuthService extends IService<UmsMemberAuth> {

    /**
     * 根据 memberAuthCreateForm创建会员认证信息
     *
     * @param memberAuthCreateForm
     * @return
     * @throws Exception
     * @author MuLin
     */
    public boolean create(Long memberId, MemberAuthCreateForm memberAuthCreateForm) throws Exception;

    /**
     * @param memberId
     * @return
     */
    public MemberAuthVO queryByMemberId(Long memberId);

    /**
     * @param memberId
     * @return
     */
    public MemberAuthVO queryFullByMemberId(Long memberId);

    /**
     * 重新认证
     *
     * @param memberId
     * @param memberAuthCreateForm
     * @return
     */
    public boolean reCert(Long memberId, MemberAuthCreateForm memberAuthCreateForm);


    /**
     * @param umsMember
     * @param memberAuth
     * @param data
     * @param response
     * @param responseDTO
     */
    public void updateAuth(UmsMember umsMember, UmsMemberAuth memberAuth, String data, String response, ResponseAuth responseAuth);


    public void authReward(MemberAuthSuccessEvent event);
}

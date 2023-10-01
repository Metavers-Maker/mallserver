package com.muling.mall.ums.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.base.BasePageQuery;
import com.muling.mall.ums.event.MemberRegisterEvent;
import com.muling.mall.ums.pojo.dto.AdFarmDispatchDTO;
import com.muling.mall.ums.pojo.dto.AdMissionDispatchDTO;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.pojo.form.MemberInviteForm;
import com.muling.mall.ums.pojo.vo.MemberInviteVO;

import java.math.BigDecimal;

public interface IUmsMemberInviteService extends IService<UmsMemberInvite> {

    public void addInvite(MemberRegisterEvent event);

    /**
     * 推荐人信息
     *
     * @param memberId
     * @return
     */
    public UmsMemberInvite getRefereeByMemberId(Long memberId);

    public UmsMemberInvite getInviteByMemberId(Long memberId);

    public IPage<MemberInviteVO> listInvitesByInviteMemberIdWithPage(BasePageQuery pageQuery);

    public boolean starDispatch(Integer star, BigDecimal fee);

    public boolean adMissionDispatch(AdMissionDispatchDTO adMissionDispatchDTO);

    public boolean adFarmDispatch(AdFarmDispatchDTO adFarmDispatchDTO);

    public boolean gameFarmDispatch(AdFarmDispatchDTO adFarmDispatchDTO);

    public boolean setInviteCode(Long memberId, String inviteCode);

    public UmsMemberInvite getByInviteCode(String inviteCode);

    public boolean update(Long id, MemberInviteForm memberInviteForm);



    /**
     * 封号
     *
     * @param status
     * @return
     */
    public boolean feng(Long memberId,Integer status);
}

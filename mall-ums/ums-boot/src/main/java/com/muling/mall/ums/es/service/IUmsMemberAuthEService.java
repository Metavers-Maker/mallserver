package com.muling.mall.ums.es.service;


import com.muling.mall.ums.pojo.vo.MemberAuthVO;

public interface IUmsMemberAuthEService {

    /**
     * @param memberId
     * @return
     */
    public MemberAuthVO queryByMemberId(Long memberId);

}

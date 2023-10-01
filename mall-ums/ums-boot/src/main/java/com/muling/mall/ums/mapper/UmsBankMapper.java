package com.muling.mall.ums.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.muling.mall.ums.pojo.entity.UmsBank;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UmsBankMapper extends MPJBaseMapper<UmsBank> {

//    @Select("SELECT * FROM ums_member_invite WHERE member_id =#{memberId}")
//    public UmsMemberInvite getByMemberId(Long memberId);
//
////    @Select("SELECT * FROM ums_member_invite WHERE member_id =#{memberId}")
////    public List<UmsMemberInvite> listByMemberIds(List<String> memberIds);
//
//    @Select("SELECT * FROM ums_member_invite WHERE invite_code =#{inviteCode}")
//    public UmsMemberInvite getOneByInviteCode(String inviteCode);

}

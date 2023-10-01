package com.muling.mall.ums.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.muling.mall.ums.pojo.entity.UmsMemberAuth;
import com.muling.mall.ums.pojo.vo.MemberAuthVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface UmsMemberAuthMapper extends MPJBaseMapper<UmsMemberAuth> {

    @Select("SELECT * FROM ums_member_auth WHERE member_id =#{memberId}")
    public MemberAuthVO simpleOne(Long memberId);
}

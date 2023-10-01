package com.muling.mall.farm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muling.mall.farm.pojo.entity.FarmMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FarmMemberMapper extends BaseMapper<FarmMember> {


    @Select("<script>" +
            " SELECT * from farm_member where member_id =#{memberId} and farm_id =#{farmId} limit 1" +
            "</script>")
    FarmMember getByMemberId(Long memberId, Long farmId);
}

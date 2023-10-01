package com.muling.mall.ums.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.muling.mall.ums.pojo.entity.UmsMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface UmsUserMapper extends MPJBaseMapper<UmsMember> {

    @Select("<script>" +
            " SELECT * from ums_member " +
            " <if test ='nickname !=null and nickname.trim() neq \"\" ' >" +
            "       WHERE nick_name like concat('%',#{nickname},'%')" +
            " </if>" +
            " <if test ='mobile !=null' >" +
            "       WHERE mobile =#{mobile}" +
            " </if>" +
            " ORDER BY updated DESC, created DESC" +
            "</script>")
    @Results({
            @Result(id = true, column = "id", property = "id")
    })
    List<UmsMember> list(Page<UmsMember> page, String nickname, String mobile);

}

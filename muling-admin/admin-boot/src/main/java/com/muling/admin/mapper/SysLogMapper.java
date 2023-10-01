package com.muling.admin.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.muling.admin.pojo.dto.SysLogDTO;
import com.muling.admin.pojo.entity.SysLog;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface SysLogMapper extends MPJBaseMapper<SysLog> {

    @Select("<script>" +
            " SELECT * from sys_log WHERE " +
            " DATE_FORMAT(created,'%Y-%m-%d') BETWEEN DATE_FORMAT(#{started},'%Y-%m-%d') AND DATE_FORMAT(#{ended},'%Y-%m-%d') " +
            " <if test ='username !=null and username.trim() neq \"\" ' >" +
            "  AND username like concat('%',#{username},'%')" +
            " </if>" +
            "</script>")
    List<SysLog> list(Page<SysLog> page, String username, String started, String ended);

    @Select("<script>" +
            " SELECT * from sys_log where " +
            " DATE_FORMAT(created,'%Y-%m-%d') BETWEEN DATE_FORMAT(#{started},'%Y-%m-%d') AND DATE_FORMAT(#{ended},'%Y-%m-%d') " +
            " <if test ='username !=null and username.trim() neq \"\" ' >" +
            "  AND username like concat('%',#{username},'%')" +
            " </if>" +
            "</script>")
    List<SysLogDTO> exportList(String username,String started, String ended);
}

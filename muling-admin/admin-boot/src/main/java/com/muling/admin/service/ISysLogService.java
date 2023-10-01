package com.muling.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.pojo.dto.SysLogDTO;
import com.muling.admin.pojo.entity.SysLog;

import java.util.List;

public interface ISysLogService extends IService<SysLog> {

    IPage<SysLog> list(Page<SysLog> page, String username, String started, String ended);

    List<SysLogDTO> exportList(String username,String started, String ended);

}

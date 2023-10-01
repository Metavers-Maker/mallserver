package com.muling.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.admin.mapper.SysLogMapper;
import com.muling.admin.pojo.dto.SysLogDTO;
import com.muling.admin.pojo.entity.SysLog;
import com.muling.admin.service.ISysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {

    @Override
    public IPage<SysLog> list(Page<SysLog> page, String username, String started, String ended) {
        List<SysLog> list = this.baseMapper.list(page, username, started, ended);
        page.setRecords(list);
        return page;
    }

    @Override
    public List<SysLogDTO> exportList(String username, String started, String ended) {
        return this.baseMapper.exportList(username, started, ended);
    }
}

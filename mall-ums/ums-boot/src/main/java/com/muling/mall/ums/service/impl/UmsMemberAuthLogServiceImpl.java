package com.muling.mall.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.ums.mapper.UmsMemberAuthLogMapper;
import com.muling.mall.ums.pojo.entity.UmsMemberAuthLog;
import com.muling.mall.ums.service.IUmsMemberAuthLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UmsMemberAuthLogServiceImpl extends ServiceImpl<UmsMemberAuthLogMapper, UmsMemberAuthLog> implements IUmsMemberAuthLogService {


}

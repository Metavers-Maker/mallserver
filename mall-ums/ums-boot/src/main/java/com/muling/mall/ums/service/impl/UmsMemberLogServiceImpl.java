package com.muling.mall.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.ums.mapper.UmsMemberLogMapper;
import com.muling.mall.ums.pojo.entity.UmsMemberLog;
import com.muling.mall.ums.service.IUmsMemberLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UmsMemberLogServiceImpl extends ServiceImpl<UmsMemberLogMapper, UmsMemberLog> implements IUmsMemberLogService {


}

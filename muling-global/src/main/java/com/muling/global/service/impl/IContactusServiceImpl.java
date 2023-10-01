package com.muling.global.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.global.mapper.ContactusMapper;
import com.muling.global.pojo.entity.Contactus;
import com.muling.global.service.IContactusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IContactusServiceImpl extends ServiceImpl<ContactusMapper, Contactus> implements IContactusService {

}

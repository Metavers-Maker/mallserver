package com.muling.mall.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.oms.mapper.OrderSettingMapper;
import com.muling.mall.oms.pojo.entity.OmsOrderSetting;
import com.muling.mall.oms.service.IOrderSettingService;
import org.springframework.stereotype.Service;


@Service
public class OrderSettingServiceImpl extends ServiceImpl<OrderSettingMapper, OmsOrderSetting> implements IOrderSettingService {

}

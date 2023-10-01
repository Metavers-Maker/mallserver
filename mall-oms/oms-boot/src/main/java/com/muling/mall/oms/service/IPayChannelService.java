package com.muling.mall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.oms.pojo.entity.OmsPayChannel;
import com.muling.mall.oms.pojo.form.ChannelForm;

/**
 * 订单支付接口
 */
public interface IPayChannelService extends IService<OmsPayChannel> {

    public boolean save(ChannelForm channelForm);

    public boolean updateById(Long id, ChannelForm channelForm);
}


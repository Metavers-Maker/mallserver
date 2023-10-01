package com.muling.mall.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.oms.converter.PayChannelConverter;
import com.muling.mall.oms.mapper.PayChannelMapper;
import com.muling.mall.oms.pojo.entity.OmsPayChannel;
import com.muling.mall.oms.pojo.form.ChannelForm;
import com.muling.mall.oms.service.IPayChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PayChannelServiceImpl extends ServiceImpl<PayChannelMapper, OmsPayChannel> implements IPayChannelService {

    @Override
    public boolean save(ChannelForm channelForm) {
        OmsPayChannel channel = PayChannelConverter.INSTANCE.form2po(channelForm);

        boolean b = this.save(channel);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, ChannelForm channelForm) {
        OmsPayChannel channel = getById(id);
        if (channel == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        PayChannelConverter.INSTANCE.updatePo(channelForm, channel);

        return updateById(channel);
    }
}

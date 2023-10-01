package com.muling.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muling.mall.oms.pojo.entity.OmsPayChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付渠道表
 */
@Mapper
public interface PayChannelMapper extends BaseMapper<OmsPayChannel> {

}

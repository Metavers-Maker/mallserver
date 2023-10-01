package com.muling.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muling.mall.oms.pojo.entity.OmsOrderPay;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
@Mapper
public interface OrderPayMapper extends BaseMapper<OmsOrderPay> {

}

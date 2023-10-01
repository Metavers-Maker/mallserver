package com.muling.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muling.mall.oms.pojo.entity.OmsOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单商品明细表
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OmsOrderItem> {

    @Select("<script>" +
            " SELECT * from oms_order_item where order_id =#{orderId} " +
            "</script>")
    List<OmsOrderItem> listByOrderId(Long orderId);
}

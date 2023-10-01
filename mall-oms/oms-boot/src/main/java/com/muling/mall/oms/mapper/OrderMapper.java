package com.muling.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.query.OrderPageQuery;
import com.muling.mall.oms.pojo.vo.OrderPageVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单详情表
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
@Mapper
public interface OrderMapper extends BaseMapper<OmsOrder> {

    /**
     * 订单分页列表
     *
     * @param page
     * @param queryParams
     * @return
     */
    List<OrderPageVO> listOrderPages(Page<OrderPageVO> page, OrderPageQuery queryParams);


    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(property = "orderItems", column = "id",
                    many = @Many(select = "com.muling.mall.oms.mapper.OrderItemMapper.listByOrderId"))
    })
    @Select("select * from oms_order where id = #{id}")
    OmsOrder findById(Long id);

    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(property = "orderItems", column = "id",
                    many = @Many(select = "com.muling.mall.oms.mapper.OrderItemMapper.listByOrderId"))
    })
    @Select("select * from oms_order where order_sn = #{orderSn}")
    OmsOrder findByOrderSn(String orderSn);

}

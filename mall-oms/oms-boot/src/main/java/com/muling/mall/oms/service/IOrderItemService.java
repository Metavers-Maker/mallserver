package com.muling.mall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.event.ItemNoSyncEvent;
import com.muling.mall.oms.pojo.entity.OmsOrderItem;

import java.util.List;


/**
 * 订单商品信息表
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
public interface IOrderItemService extends IService<OmsOrderItem> {


    public List<OmsOrderItem> getByOrderId(Long orderId);

    public boolean itemNoSync(ItemNoSyncEvent itemNoSyncEvent);
}


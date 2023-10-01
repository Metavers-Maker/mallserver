package com.muling.mall.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.bms.event.ItemNoSyncEvent;
import com.muling.mall.oms.mapper.OrderItemMapper;
import com.muling.mall.oms.pojo.entity.OmsOrderItem;
import com.muling.mall.oms.service.IOrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OmsOrderItem> implements IOrderItemService {

    @Override
    public List<OmsOrderItem> getByOrderId(Long orderId) {
        List<OmsOrderItem> omsOrderItems = baseMapper.selectList(Wrappers.<OmsOrderItem>lambdaQuery().eq(OmsOrderItem::getOrderId, orderId));
        return omsOrderItems;
    }

    @Override
    public boolean itemNoSync(ItemNoSyncEvent itemNoSyncEvent) {
        //
        List<ItemNoSyncEvent.ItemNoSync> itemNoSyncList = itemNoSyncEvent.getSyncDatas();
        for (ItemNoSyncEvent.ItemNoSync itemNoSync : itemNoSyncList) {
            //itemNoSync
            this.update(new LambdaUpdateWrapper<OmsOrderItem>()
                    .eq(OmsOrderItem::getId, itemNoSync.getOrderItemId())
                    .set(OmsOrderItem::getItemNo, itemNoSync.getItemNo()));
        }
        return true;
    }
}

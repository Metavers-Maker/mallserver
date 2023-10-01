package com.muling.mall.bms.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemNoSyncEvent implements Serializable {

    private String orderSn;

    private List<ItemNoSync> syncDatas;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemNoSync implements Serializable {
        private String itemNo;
        private Long orderItemId;
    }
}

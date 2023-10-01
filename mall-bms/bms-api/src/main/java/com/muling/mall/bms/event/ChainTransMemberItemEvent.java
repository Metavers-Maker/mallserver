package com.muling.mall.bms.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * 链上用户物品转移事件
 * */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChainTransMemberItemEvent implements Serializable {

    private Integer chainType;

    private Long itemContract;

    private Long fromAddress;

    private Long toAddress;

}

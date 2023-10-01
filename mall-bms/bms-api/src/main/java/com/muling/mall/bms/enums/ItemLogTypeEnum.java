package com.muling.mall.bms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huawei
 * @desc
 * @email huawei_code@163.com
 * @date 2021/1/16
 */
public enum ItemLogTypeEnum implements IBaseEnum<Integer> {


    BUY(0, "购买"),

    AIRDROP(1, "空投"),
    OPEN(2, "开盲盒"),

    MINTED(3, "铸造"),
    REFUND(4, "退款"),
    TRANSFER(5, "转赠"),
    TRANSFER_CONSUME(6, "转赠消耗"),
    TRANSFER_OUTSIDE(7, "转赠外链"),
    CHAIN_TRANSFER(8, "上链转移"),
    ADMIN_TRANSFER(9, "管理员转移物品"),
    COMPOUND(10, "合成"),
    EXCHANGE(11, "兑换"),
    COMPOUND_CONSUME(12, "合成消耗"),
    TRANSFER_RECEIVE(13, "转赠接收"),
    MARKET_SELL(14, "市场售卖"),
    MARKET_BUY(15, "市场购买"),
    EXCHANGE_CONSUME(16, "兑换消耗"),
    STAKE(17, "质押"),
    WITHDRAW(18, "提取"),
    MARKET_FREEZE(19, "市场冻结"),
    MARKET_UNFREEZE(20, "市场解冻"),
    PUBLISH(21, "首发"),
    MARKET_ADMIN_CANCLE(22, "市场后台撤销"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ItemLogTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}

package com.muling.mall.pms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.IterableMap;

public enum OhEnum implements IBaseEnum<Integer> {

    /**
     * 类型 0-未绑定 1-绑定
     */
    WEAPON_CREATE_SYSTEM(0, "武器创建(系统)"),
    WEAPON_CREATE_AIRDROP(1, "武器创建(空投)"),
    ITEM_CREATE_BATTLE(10, "战斗道具掉落"),
    ITEM_CREATE_NORMAL(11, "道具创建普通"),
    ITEM_CREATE_AIRDROP(12, "道具创建(空投)"),
    ITEM_DESTORY_TEST(13, "道具销毁(测试)"),
    ITEM_DESTORY_COMBINE(14, "道具合成销毁"),
    ITEM_DESTORY_NORMAL(15, "道具销毁(普通)"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    OhEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}

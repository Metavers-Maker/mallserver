package com.muling.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum StatusEnum implements IBaseEnum<Integer> {

    ENABLE(0, "可用"),

    DISABLED(1, "不可用");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    StatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value){
        StatusEnum[] statusEnum =  values();

        for(StatusEnum item: statusEnum){
            if(item.getValue().equals(value)) return item.getLabel();
        }

        return null;
    }
}

package com.muling.mall.pms.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum SourceTypeEnum implements IBaseEnum<Integer> {

    /**
     * 卡池来源类型 0-图片 1-视频 2-3D模型 3-音频
     */
    IMAGE(0, "图片"),
    VIDEO(1, "视频"),
    THREE_DIMENSIONAL_MODEL(2, "3D模型"),
    AUDIO(3, "音频");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    SourceTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}

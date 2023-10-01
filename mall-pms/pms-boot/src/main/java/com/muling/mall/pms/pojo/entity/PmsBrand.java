package com.muling.mall.pms.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.pms.common.enums.StatusEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PmsBrand extends BaseEntity {

    @TableId(type = IdType.NONE)
    private Long id;

    /**
     * 角色 0 作者，1发行发
     * */
    private Integer role;

    /**
     * 名称
     * */
    private String name;

    /**
     * 图片Logo
     * */
    private String picUrl;

    /**
     * 简单描述
     * */
    private String simpleDsp;

    /**
     * 简介
     * */
    private String dsp;

    /**
     * 扩展字段
     * */
    private JSONObject ext;

    /**
     * 可见性
     * */
    private ViewTypeEnum visible;

    /**
     * 状态
     * */
    private StatusEnum status;

    /**
     * 排序
     * */
    private Integer sort;
}

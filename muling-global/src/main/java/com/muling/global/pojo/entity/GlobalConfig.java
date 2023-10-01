package com.muling.global.pojo.entity;


import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
public class GlobalConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String content;

    private JSONObject ext;

}

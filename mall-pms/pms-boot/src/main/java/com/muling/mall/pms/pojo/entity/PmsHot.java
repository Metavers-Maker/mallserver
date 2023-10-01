package com.muling.mall.pms.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.pms.common.enums.ContentTypeEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 */
@Data
@Accessors(chain = true)
public class PmsHot extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private JSONObject ext;

    private ContentTypeEnum contentType;
    private ViewTypeEnum visible;

    private Long contentId;

}

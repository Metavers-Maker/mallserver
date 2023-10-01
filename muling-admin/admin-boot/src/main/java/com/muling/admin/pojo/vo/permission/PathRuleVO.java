package com.muling.admin.pojo.vo.permission;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 权限视图对象
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2021/10/30 10:54
 */
@Data
@ApiModel("权限视图对象")
public class PathRuleVO {

    private String name;

    private String path;

    private Integer type;

    private String value;

    private String remark;

}

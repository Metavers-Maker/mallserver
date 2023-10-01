package com.muling.admin.pojo.form;

import lombok.Data;

/**
 * 表单对象
 */
@Data
public class PathRuleForm {

    private String name;

    private String path;

    private Integer type;

    private String value;

    private String remark;

}

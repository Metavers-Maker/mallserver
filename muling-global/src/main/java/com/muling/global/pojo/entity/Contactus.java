package com.muling.global.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
public class Contactus extends BaseEntity {

    private Long id;

    private Long memberId;

    private String name;

    private String email;

    private String title;

    private String content;

//    private String result;
//
//    private Integer status;
}

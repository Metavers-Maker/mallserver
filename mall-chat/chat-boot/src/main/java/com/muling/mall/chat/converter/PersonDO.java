package com.muling.mall.chat.converter;

import lombok.Data;

import java.util.Date;

@Data
public class PersonDO {
    private Integer id;
    private String name;
    private int age;
    private Date birthday;
}

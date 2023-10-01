package com.muling.mall.chat.converter;

import lombok.Data;

import java.util.Date;

@Data
public class PersonDTO {
    private String userName;
    private Integer age;
    private Date birthday;
}

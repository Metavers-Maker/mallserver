package com.muling.global.pojo.vo;


import lombok.Data;

@Data
public class NewsVO {

    private Integer type;

    private String title;

    private String ext;

    private String content;

    private Long created;

}

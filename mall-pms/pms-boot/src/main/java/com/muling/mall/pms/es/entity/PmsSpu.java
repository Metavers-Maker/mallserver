package com.muling.mall.pms.es.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Data
@Document(indexName = "pms_spu", createIndex = false)
@Setting(shards = 5)
public class PmsSpu {

    @Field(type = FieldType.Long, store = true)
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;
    @Field(name = "subject_id")
    private Long subjectId;
    @Field(name = "brand_id")
    private Long brandId;
    private String contract;

    @Field(name = "product_id")
    private String productId;

    @Field(name = "source_type")
    private Integer sourceType;
    private Integer type;
    @Field(name = "rule_id")
    private Integer ruleId;
    private Long price;
    //发行量
    private Integer total;
    //销量
    private Integer sales;
    @Field(name = "pic_url")
    private String picUrl;

    @Field(type = FieldType.Object)
    private String[] album;
    private String[] icons;
    private String[] images;
    private Object ext;
    private Integer sort;

    private Integer bind;
    private Integer visible;
    private Integer status;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime started;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;
}

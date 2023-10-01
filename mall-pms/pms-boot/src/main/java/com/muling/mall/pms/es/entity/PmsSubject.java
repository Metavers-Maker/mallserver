package com.muling.mall.pms.es.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Data
@Document(indexName = "pms_subject", createIndex = false)
@Setting(shards = 5)
public class PmsSubject {

    @Field(type = FieldType.Long, store = true)
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;
    @Field(name = "brand_id", type = FieldType.Long)
    private Long brandId;
    @Field(name = "icon_url")
    private String iconUrl;
    private String[] images;
    private String[] icons;
    @Field(type = FieldType.Object)
    private Object ext;
    @Field(type = FieldType.Integer)
    private Integer sort;
    @Field(type = FieldType.Integer)
    private Integer visible;
    @Field(type = FieldType.Integer)
    private Integer status;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime started;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;
}

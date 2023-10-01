package com.muling.mall.pms.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Data
@Document(indexName = "pms_brand", createIndex = false)
@Setting(shards = 5)
public class PmsBrand {

    @Id
    private Long id;
    @Field(type = FieldType.Integer)
    private Integer role;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;
    @Field(name = "pic_url")
    private String picUrl;
    @Field(name = "dsp")
    private String dsp;
    @Field(type = FieldType.Object)
    private Object ext;
    @Field(type = FieldType.Integer)
    private Integer sort;
    @Field(type = FieldType.Integer)
    private Integer visible;
    @Field(type = FieldType.Integer)
    private Integer status;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;

}

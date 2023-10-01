package com.muling.mall.pms.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Data
@Document(indexName = "pms_hot", createIndex = false)
@Setting(shards = 5)
public class PmsHot {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;

    @Field(name = "content_type")
    private Integer contentType;

    @Field(type = FieldType.Object)
    private Object ext;

    private Integer visible;

    @Field(name = "content_id")
    private Long contentId;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;

}

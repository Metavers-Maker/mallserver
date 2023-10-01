package com.muling.mall.pms.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Data
@Document(indexName = "pms_banner", createIndex = false)
@Setting(shards = 5)
public class PmsBanner {

    @Id
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;

    @Field(name = "link_type")
    private Integer linkType;
    private String link;

    private String source;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;

}

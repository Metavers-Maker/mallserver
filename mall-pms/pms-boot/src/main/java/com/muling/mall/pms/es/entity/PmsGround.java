package com.muling.mall.pms.es.entity;

import com.muling.mall.pms.common.enums.ViewTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Data
@Document(indexName = "pms_ground", createIndex = false)
@Setting(shards = 5)
public class PmsGround {

    @Id
    private Long id;
    @Field(name = "product_id")
    private Long productId;
    private Integer type;

    private String icon;
    private Integer visible;
    private Integer sort;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;

}

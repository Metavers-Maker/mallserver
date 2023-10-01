package com.muling.mall.pms.es.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Data
@Document(indexName = "pms_sku", createIndex = false)
@Setting(shards = 5)
public class PmsSku {

    @Field(type = FieldType.Long, store = true)
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;

    @Field(name = "spu_id", type = FieldType.Long)
    private Long spuId;
    private Long price;

    @Field(name = "pic_url")
    private String picUrl;
    @Field(name = "stock_num")
    private Integer stockNum;

    private Integer close;

    @Field(name = "locked_stock_num")
    private Integer lockedStockNum;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;
}

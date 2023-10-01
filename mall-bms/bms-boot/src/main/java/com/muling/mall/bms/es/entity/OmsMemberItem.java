package com.muling.mall.bms.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Data
@Document(indexName = "oms_member_item", createIndex = false)
@Setting(shards = 5)
public class OmsMemberItem {

    @Id
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;

    @Field(name = "order_id")
    private Long orderId;
    @Field(name = "member_id")
    private Long memberId;

    @Field(name = "sku_id")
    private Long skuId;
    @Field(name = "spu_id")
    private Long spuId;

    private Integer type;
    @Field(name = "pic_url")
    private String picUrl;

    private String contract;
    @Field(name = "hex_id")
    private String hexId;

    private String hash;

    @Field(name = "item_no")
    private String itemNo;

    private Integer status;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;

}

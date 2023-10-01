package com.muling.mall.ums.es.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.muling.mall.ums.enums.AuthStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Data
@Document(indexName = "ums_member_auth", createIndex = false)
@Setting(shards = 5)
public class UmsMemberAuth {

    @Id
    private Long id;

    @Field(name = "real_name", type = FieldType.Text, analyzer = "ik_smart")
    private String realName;

    @Field(name = "member_id")
    private Long memberId;

    @Field(name = "id_card_type")
    private Integer idCardType;

    @Field(name = "id_card")
    private String idCard;

    private String mobile;

    private Integer status;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;

}

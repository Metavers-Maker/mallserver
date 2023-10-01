package com.muling.mall.ums.es.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Data
@Document(indexName = "ums_member", createIndex = false)
@Setting(shards = 5)
public class UmsMember {

    @Id
    private Long id;

    @Field(name = "nick_name", type = FieldType.Text, analyzer = "ik_smart")
    private String nickName;

    private Integer gender;

    private String uid;

    private String password;

    private String email;

    private String mobile;

    @Field(name = "avatar_url")
    private String avatarUrl;

    private String openid;

    private Integer status;

    @Field(name = "auth_status")
    private Integer authStatus;

    private String secret;

    @Field(name = "is_bind_google")
    private Integer isBindGoogle;

    @Field(name = "chain_address")
    private String chainAddress;

    private JSONObject ext;

    @TableLogic(delval = "1", value = "0")
    private Integer deleted;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime created;
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    private LocalDateTime updated;

}

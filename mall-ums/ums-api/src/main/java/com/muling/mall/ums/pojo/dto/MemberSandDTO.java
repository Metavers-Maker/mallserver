package com.muling.mall.ums.pojo.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberSandDTO {

    private Long memberId;

    private String userId;

    private String nickName;

    /**
     * 0 未生效 1生效
     * */
    private Integer status;

}

package com.muling.mall.ums.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
public class MemberWhiteDTO {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 权益登记
     */
    private Integer level;
}

package com.muling.mall.ums.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会员合约地址传输层对象
 *
 */

@Data
public class MemberAccountChainDTO {

    private Long id;

    private Long memberId;

    private String address;

    private Integer chainType;

    private String bankCardCode;

    private String bankName;

    private String bankUsername;

    private Integer status;

}




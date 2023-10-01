package com.muling.mall.ums.pojo.form;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 *
 */
@Data
public class MemberAuthCreateForm {

    @Size(min = 2, max = 15, message = "姓名长度需要在2-15个字之间")
    private String realName;
    private Integer idCardType = 0;
    @Size(min = 18, max = 18, message = "身份证号长度需要为18位")
    private String idCard;

}

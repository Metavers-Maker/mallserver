package com.muling.auth.security.extension.alipay;

import lombok.Data;

/**
 * @author freeze
 * @date 2022/9/25
 */
@Data
public class AlipayUserInfo {
    private String avatarUrl;

    private String city;

    private String country;

    private Integer gender;

    private String language;

    private String nickName;

    private String province;

}

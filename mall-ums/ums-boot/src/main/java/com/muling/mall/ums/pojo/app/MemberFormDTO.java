package com.muling.mall.ums.pojo.app;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class MemberFormDTO {

    private String nickName;

    private String avatarUrl;

    private String safeCode;
    
    private JSONObject ext;

}

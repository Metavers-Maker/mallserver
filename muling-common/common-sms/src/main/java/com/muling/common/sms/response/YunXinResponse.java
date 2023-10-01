package com.muling.common.sms.response;

import lombok.Data;

@Data
public class YunXinResponse {
    private String code;
    private String msg;
    private String desc;

}
//{"msg":"发送成功","code":"0","data":{"taskid":"042180811517071381887447040"}}

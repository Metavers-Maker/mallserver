package com.muling.common.sms.response;

import lombok.Data;

@Data
public class AllNetResponse {
    private String code;
    private String msg;
    private Content data;

    @Data
    public static class Content {
        private String taskid;
    }
}
//{"msg":"发送成功","code":"0","data":{"taskid":"042180811517071381887447040"}}

package com.muling.mall.ums.util;

import lombok.Data;

@Data
public class ResponseAuth {
    private int error_code;
    private String reason;
    private Result result;
    private String sn;

    @Data
    public static class Result {
        private String Name;
        private String CardNo;
        private String Mobile;
        private String VerificationResult;
    }

}

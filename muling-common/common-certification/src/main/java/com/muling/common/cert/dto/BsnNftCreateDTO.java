package com.muling.common.cert.dto;

import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class BsnNftCreateDTO {
    private int code;
    private String message;
    private Result data;
    private String seqNo;

    @lombok.Data
    public static class Result {
        private int result;
    }

}

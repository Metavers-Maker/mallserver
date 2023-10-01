package com.muling.common.exception;

import com.muling.common.result.ResultCode;
import lombok.Data;

@Data
public class GateWayException extends RuntimeException{

    private String code;

    private String message;

    public GateWayException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
    }
}

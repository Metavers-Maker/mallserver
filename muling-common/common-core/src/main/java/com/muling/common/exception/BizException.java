package com.muling.common.exception;

import com.muling.common.result.IResultCode;
import lombok.Getter;

import java.text.MessageFormat;


@Getter
public class BizException extends RuntimeException {

    public IResultCode resultCode;

    public BizException(IResultCode errorCode) {
        super(errorCode.getMsg());
        this.resultCode = errorCode;
    }

    public BizException(IResultCode errorCode, Object... args) {
        super(MessageFormat.format(errorCode.getMsg(), args));
        this.resultCode = errorCode;
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }
}

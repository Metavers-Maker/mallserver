package com.muling.common.web.encrypt.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseBasic<T> implements Serializable {
    private int code;
    private T data;
    private String msg;
    private int salt;
    private String signature;
    public ResponseBasic<T> fail(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public ResponseBasic<T> success(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }
}

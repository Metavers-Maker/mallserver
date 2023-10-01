package com.muling.mall.chat.dto;

import lombok.Data;

@Data
public class ResponseMessage extends Message {

    public ResponseMessage() {
    }

    public ResponseMessage(String data) {
        super.data = data;
    }
}


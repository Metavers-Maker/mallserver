package com.muling.mall.chat.entity;


import lombok.Data;

import java.security.Principal;

@Data
public final class User implements Principal {

    private final String name;

    private final Long id;

    private final String sessionId;

    public User(String sessionId, Long id, String name) {
        this.sessionId = sessionId;
        this.id = id;
        this.name = name;

    }

    @Override
    public String getName() {
        return name;
    }
}

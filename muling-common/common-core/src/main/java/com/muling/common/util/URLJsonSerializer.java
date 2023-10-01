package com.muling.common.util;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

import java.net.URL;

public class URLJsonSerializer implements TypeJsonSerializer<URL> {

    @Override
    public boolean serialize(JsonContext jsonContext, URL value) {
        jsonContext.writeString(value.toString());
        return true;
    }
}

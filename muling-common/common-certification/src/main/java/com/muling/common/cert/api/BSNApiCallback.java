package com.muling.common.cert.api;

import cn.hutool.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;

public interface BSNApiCallback {

    public void succ(String opId,CloseableHttpResponse response, JSONObject ret);

    public void fail(String opId,CloseableHttpResponse response, JSONObject ret);
}


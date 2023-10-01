//
//  Created by  fred on 2017/1/12.
//  Copyright © 2016年 Alibaba. All rights reserved.
//

package com.muling.common.cert.service;

import com.alibaba.cloudapi.sdk.client.ApacheHttpClient;
import com.alibaba.cloudapi.sdk.enums.HttpMethod;
import com.alibaba.cloudapi.sdk.enums.ParamPosition;
import com.alibaba.cloudapi.sdk.enums.Scheme;
import com.alibaba.cloudapi.sdk.model.ApiCallback;
import com.alibaba.cloudapi.sdk.model.ApiRequest;
import com.alibaba.cloudapi.sdk.model.ApiResponse;
import com.alibaba.cloudapi.sdk.model.HttpClientBuilderParams;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HttpApiClientCertification extends ApacheHttpClient {
    public final static String HOST = "checkone.market.alicloudapi.com";
    static HttpApiClientCertification instance = new HttpApiClientCertification();

    public static HttpApiClientCertification getInstance() {
        return instance;
    }

    public static final ObjectMapper mapper = new ObjectMapper();

    public void init(HttpClientBuilderParams httpClientBuilderParams) {
        httpClientBuilderParams.setScheme(Scheme.HTTP);
        httpClientBuilderParams.setHost(HOST);
        super.init(httpClientBuilderParams);
    }


    public void cert(String name, String idcard, ApiCallback callback) {
        String path = "/chinadatapay/1882";
        ApiRequest request = new ApiRequest(HttpMethod.POST_FORM, path);
        request.addParam("name", name, ParamPosition.BODY, true);
        request.addParam("idcard", idcard, ParamPosition.BODY, true);

        sendAsyncRequest(request, callback);
    }

    public ApiResponse certSyncMode(String name, String idcard) {
        String path = "/chinadatapay/1882";
        ApiRequest request = new ApiRequest(HttpMethod.POST_FORM, path);
        request.addParam("name", name, ParamPosition.BODY, true);
        request.addParam("idcard", idcard, ParamPosition.BODY, true);

        return sendSyncRequest(request);
    }

}

package com.muling.common.cert.service;

import com.alibaba.cloudapi.sdk.enums.HttpMethod;
import com.alibaba.cloudapi.sdk.enums.ParamPosition;
import com.alibaba.cloudapi.sdk.model.ApiCallback;
import com.alibaba.cloudapi.sdk.model.ApiRequest;
import com.alibaba.cloudapi.sdk.model.ApiResponse;
import com.alibaba.cloudapi.sdk.model.HttpClientBuilderParams;
import com.muling.common.cert.config.CertConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class CertService {

    private final CertConfig certConfig;

    @PostConstruct
    public HttpApiClientCertification httpApiClient() {

        HttpClientBuilderParams httpParam = new HttpClientBuilderParams();
        httpParam.setAppKey(certConfig.getAppKey());
        httpParam.setAppSecret(certConfig.getAppSecret());

        HttpApiClientCertification instance = HttpApiClientCertification.getInstance();
        instance.init(httpParam);
        return instance;
    }

    public void cert(String name, String idcard, ApiCallback callback) {
        String path = "/chinadatapay/1882";
        ApiRequest request = new ApiRequest(HttpMethod.POST_FORM, path);
        request.addParam("name", name, ParamPosition.BODY, true);
        request.addParam("idcard", idcard, ParamPosition.BODY, true);

        httpApiClient().sendAsyncRequest(request, callback);
    }

    public ApiResponse certSyncMode(String name, String idcard) {
        String path = "/chinadatapay/1882";
        ApiRequest request = new ApiRequest(HttpMethod.POST_FORM, path);
        request.addParam("name", name, ParamPosition.BODY, true);
        request.addParam("idcard", idcard, ParamPosition.BODY, true);

        return httpApiClient().sendSyncRequest(request);
    }
}

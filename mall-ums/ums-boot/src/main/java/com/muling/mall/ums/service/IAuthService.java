package com.muling.mall.ums.service;

import com.muling.mall.ums.util.ResponseAuth;

public interface IAuthService {

    public ResponseAuth auth(String name, String idCard, String mobile) throws Exception;
}

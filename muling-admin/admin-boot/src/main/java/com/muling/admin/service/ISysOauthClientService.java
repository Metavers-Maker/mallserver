package com.muling.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.pojo.entity.SysOauthClient;

public interface ISysOauthClientService extends IService<SysOauthClient> {
    void cleanCache();
}

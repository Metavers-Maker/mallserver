package com.muling.mall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.ums.pojo.entity.UmsSandAccount;

public interface ISandAccountService extends IService<UmsSandAccount> {

    /**
     * 创建sand账户
     *
     * @return
     */
    String createSandAccount(String ipAddr, Long testId);

}

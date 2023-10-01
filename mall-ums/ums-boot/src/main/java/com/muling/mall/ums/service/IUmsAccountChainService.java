package com.muling.mall.ums.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.result.Result;
import com.muling.mall.ums.pojo.dto.MemberAccountChainDTO;
import com.muling.mall.ums.pojo.dto.MemberSandDTO;
import com.muling.mall.ums.pojo.entity.UmsAccountChain;
import com.muling.mall.ums.pojo.form.AddressChainForm;
import com.muling.mall.ums.pojo.form.AddressChainUnbindForm;

import java.util.List;

/**
 * 会员链上地址业务接口
 *
 * @author haoxr
 * @date 2022/2/12
 */
public interface IUmsAccountChainService extends IService<UmsAccountChain> {

    public List<MemberAccountChainDTO> listAccount(Integer chainType);

    /**
     * 生成BSN地址
     */
    Result<String> genBsnAccount();

    /**
     * 根据用户ID生成BSN地址
     */
    Result<String> genBsnAccountByMemberId(Long memberId);

    /**
     * SyncBSN地址
     */
    boolean syncBsnAccount(Long memberId);

    /**
     * 绑定三方账户
     *
     * @param addressForm
     * @return
     */
    boolean bindAccount(AddressChainForm addressForm, Long memberId);

    /**
     * 解绑三方账户
     *
     * @param unbindForm
     * @return
     */
    boolean unbindAccount(AddressChainUnbindForm unbindForm, Long memberId);

    /**
     * 更新三方账户
     *
     * @param addressForm
     * @return
     */
    boolean updateAccount(Long accountId, AddressChainForm addressForm, Long memberId);

}

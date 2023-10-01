package com.muling.mall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.ums.pojo.entity.UmsBank;
import com.muling.mall.ums.pojo.form.AddressChainForm;
import com.muling.mall.ums.pojo.form.BankBindEnsureForm;
import com.muling.mall.ums.pojo.form.BankBindForm;
import com.muling.mall.ums.pojo.form.BankUnbindForm;
import com.muling.mall.ums.pojo.vo.BankBindVO;
import com.muling.mall.ums.pojo.vo.BankVO;

import java.util.List;

/**
 * 会员链银行卡业务接口
 */
public interface IUmsBankService extends IService<UmsBank> {

    public List<BankVO> listBank(Integer platType);

    /**
     * 申请绑定卡片
     *
     * @param bankForm
     * @return
     */
    BankBindVO bindCard(BankBindForm bankForm, Long memberId);

    /**
     * 绑定卡片确认
     *
     * @param bankForm
     * @return
     */
    boolean bindCardEnsure(BankBindEnsureForm bankForm, Long memberId);

    /**
     * 解绑卡片
     *
     * @param id
     * @return
     */
    boolean unbindCard(Long id, Long memberId);

    /**
     * 设置默认卡片
     *
     * @param id
     * @return
     */
    boolean usedBank(Long id, Long memberId);

}

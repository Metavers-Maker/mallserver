package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.wms.pojo.entity.WmsWithdraw;
import com.muling.mall.wms.pojo.query.app.WithdrawPageQuery;
import com.muling.mall.wms.pojo.vo.WithdrawVO;
import software.amazon.ion.Decimal;

public interface IWmsWithdrawService extends IService<WmsWithdraw> {

    public IPage<WithdrawVO> page(WithdrawPageQuery queryParams);

    /**
     * 用户发起Withdraw操作
     * */
    public WithdrawVO withdraw(Decimal balance,Integer coinType);

    /**
     * 用户发起取消操作
     * */
    public boolean cancle(Integer id);

    /**
     * 通过操作
     * */
    public boolean pass(Integer id);

    /**
     * 拒绝操作
     * */
    public boolean reject(Integer id,String reason);

}

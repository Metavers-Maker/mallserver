package com.muling.mall.otc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.otc.pojo.entity.OtcPayInfo;
import com.muling.mall.otc.pojo.form.PayInfoForm;
import com.muling.mall.otc.pojo.query.app.PayInfoPageQuery;
import com.muling.mall.otc.pojo.vo.PayInfoVO;


public interface IPayInfoService extends IService<OtcPayInfo> {

    public IPage<PayInfoVO> page(PayInfoPageQuery queryParams);

    public boolean save(PayInfoForm configForm);

    public boolean updateById(Long id, PayInfoForm configForm);
}


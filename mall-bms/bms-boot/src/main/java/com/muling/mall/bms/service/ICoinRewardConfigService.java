package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.BmsCoinConfig;
import com.muling.mall.bms.pojo.form.admin.CoinRewardConfigForm;
import com.muling.mall.bms.pojo.query.admin.CoinRewardPageQuery;
import com.muling.mall.bms.pojo.vo.app.CoinRewardVO;

public interface ICoinRewardConfigService extends IService<BmsCoinConfig> {

    public IPage<CoinRewardVO> page(CoinRewardPageQuery queryParams);

    public boolean save(CoinRewardConfigForm configForm);

    public boolean updateById(Long id, CoinRewardConfigForm configForm);

    public void calculate();

}

package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.pms.pojo.entity.PmsAirdropConfig;
import com.muling.mall.pms.pojo.form.AirdropConfigForm;
import com.muling.mall.pms.pojo.query.AirdropConfigPageQuery;
import com.muling.mall.pms.pojo.vo.AirdropConfigVO;

import java.util.List;

public interface IPmsAirdropConfigService extends IService<PmsAirdropConfig> {

//    public List<AirdropConfigVO> getAppBrandDetails(List<Long> brandIds);

    IPage<AirdropConfigVO> page(AirdropConfigPageQuery queryParams);

    public boolean add(AirdropConfigForm airdropConfigForm);

    public boolean updateById(Long id, AirdropConfigForm airdropConfigForm);

}

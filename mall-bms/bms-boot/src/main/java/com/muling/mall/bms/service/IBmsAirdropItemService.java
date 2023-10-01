package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.BmsAirdropItem;
import com.muling.mall.bms.pojo.form.AirdropItemForm;
import com.muling.mall.bms.pojo.query.AirdropItemPageQuery;
import com.muling.mall.bms.pojo.vo.AirdropItemVO;

public interface IBmsAirdropItemService extends IService<BmsAirdropItem> {

//    public List<AirdropItemVO> getAppBrandDetails(List<Long> brandIds);

    IPage<AirdropItemVO> page(AirdropItemPageQuery queryParams);

    public boolean save(AirdropItemForm airdropItemForm);

    public boolean updateById(Long id, AirdropItemForm airdropItemForm);

    public boolean exec(Long cfgId);
}

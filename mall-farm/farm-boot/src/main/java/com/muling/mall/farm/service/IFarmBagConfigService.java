package com.muling.mall.farm.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.enums.VisibleEnum;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import com.muling.mall.farm.pojo.form.admin.FarmBagConfigForm;
import com.muling.mall.farm.pojo.query.app.FarmBagConfigPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmBagConfigVO;

import java.util.Collection;

public interface IFarmBagConfigService extends IService<FarmBagConfig> {

    public IPage<FarmBagConfigVO> page(FarmBagConfigPageQuery queryParams);

    boolean add(FarmBagConfigForm form);

    boolean update(Long id, FarmBagConfigForm form);

    boolean update(Long id, VisibleEnum visible);

    public boolean delete(Collection<String> ids);

    public FarmBagConfig getBySpuId(Long spuId);
}

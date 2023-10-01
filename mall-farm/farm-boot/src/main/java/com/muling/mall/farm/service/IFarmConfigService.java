package com.muling.mall.farm.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.enums.VisibleEnum;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.form.admin.FarmConfigForm;
import com.muling.mall.farm.pojo.query.app.FarmConfigPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmConfigVO;

import java.util.Collection;

public interface IFarmConfigService extends IService<FarmConfig> {

    public IPage<FarmConfigVO> page(FarmConfigPageQuery queryParams);

    boolean add(FarmConfigForm form);

    boolean update(Long id, FarmConfigForm form);

    boolean update(Long id, VisibleEnum visible);

    public boolean delete(Collection<String> ids);

}

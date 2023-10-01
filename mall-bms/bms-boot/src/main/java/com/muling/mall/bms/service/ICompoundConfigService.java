package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsCompoundConfig;
import com.muling.mall.bms.pojo.form.admin.CompoundConfigForm;
import com.muling.mall.bms.pojo.query.admin.CompoundPageQuery;
import com.muling.mall.bms.pojo.vo.app.CompoundVO;

public interface ICompoundConfigService extends IService<OmsCompoundConfig> {

    public IPage<CompoundVO> page(CompoundPageQuery queryParams);


    public boolean save(CompoundConfigForm compoundConfigForm);

    public boolean updateById(Long id, CompoundConfigForm compoundConfigForm);

}

package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.base.BasePageQuery;
import com.muling.mall.pms.pojo.entity.PmsGround;
import com.muling.mall.pms.pojo.form.GroundForm;
import com.muling.mall.pms.pojo.query.GroundPageQuery;
import com.muling.mall.pms.pojo.vo.GroundVO;

public interface IPmsGroundService extends IService<PmsGround> {

    public IPage<GroundVO> page(GroundPageQuery queryParams);

    public boolean save(GroundForm groundForm);

    public boolean updateById(Long id, GroundForm groundForm);

}

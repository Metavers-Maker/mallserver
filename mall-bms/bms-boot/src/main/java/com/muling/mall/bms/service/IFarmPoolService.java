package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.pojo.entity.OmsFarmPool;
import com.muling.mall.bms.pojo.form.admin.StakeConfigForm;
import com.muling.mall.bms.pojo.query.admin.StakePageQuery;
import com.muling.mall.bms.pojo.vo.app.FarmPoolVO;

import java.util.List;

public interface IFarmPoolService extends IService<OmsFarmPool> {

    public IPage<FarmPoolVO> page(StakePageQuery queryParams);

    public boolean save(StakeConfigForm configForm);

    public boolean updateById(Long id, StakeConfigForm configForm);

    public List<OmsFarmPool> list(StatusEnum status);

}

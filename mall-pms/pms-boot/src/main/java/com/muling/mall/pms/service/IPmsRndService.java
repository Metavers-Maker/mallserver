package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.pms.pojo.dto.RndDTO;
import com.muling.mall.pms.pojo.entity.PmsRnd;
import com.muling.mall.pms.pojo.form.RndForm;

import java.util.List;

public interface IPmsRndService extends IService<PmsRnd> {

    public boolean save(RndForm rndForm);

    public boolean updateById(Long id, RndForm rndForm);

    public List<RndDTO> lock(Long spuId);

    public boolean unlock(Long spuId, Long rndId);
}

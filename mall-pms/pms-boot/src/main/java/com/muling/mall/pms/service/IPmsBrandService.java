package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.form.BrandForm;
import com.muling.mall.pms.pojo.query.BrandPageQuery;
import com.muling.mall.pms.pojo.vo.BrandVO;

import java.util.List;

public interface IPmsBrandService extends IService<PmsBrand> {

    public List<BrandVO> getAppBrandDetails(List<Long> brandIds);

    public PmsBrand getBrandDetails(Long brandId);

    IPage<BrandVO> page(BrandPageQuery queryParams);

    public boolean save(BrandForm brandForm);

    public boolean updateById(Long id, BrandForm brandForm);
}

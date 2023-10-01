package com.muling.mall.pms.es.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.mall.pms.pojo.query.BrandPageQuery;
import com.muling.mall.pms.pojo.vo.BrandVO;

import java.util.List;

public interface PmsBrandEService {

    List<BrandVO> getAppBrandDetails(List<Long> brandIds);

    IPage<BrandVO> page(BrandPageQuery queryParams);

}

package com.muling.mall.pms.es.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.base.BasePageQuery;
import com.muling.mall.pms.pojo.query.GroundPageQuery;
import com.muling.mall.pms.pojo.vo.GroundVO;

public interface PmsGroundEService {

    IPage<GroundVO> page(GroundPageQuery queryParams);

}

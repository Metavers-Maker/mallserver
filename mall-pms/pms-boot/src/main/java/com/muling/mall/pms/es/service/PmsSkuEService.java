package com.muling.mall.pms.es.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.base.BasePageQuery;
import com.muling.mall.pms.pojo.vo.SkuVO;
import com.muling.mall.pms.pojo.vo.SubjectVO;

import java.util.List;

public interface PmsSkuEService {

    public Integer getStockNum(Long skuId);

    public List<SkuVO> getAppSkuDetails(List<Long> spuIds);
}

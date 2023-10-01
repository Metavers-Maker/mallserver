package com.muling.mall.pms.es.service;

import com.muling.mall.pms.pojo.vo.GoodsPageVO;

import java.util.List;

public interface PmsSpuEService {

    public List<GoodsPageVO> listSpuBySubjectId(Long subjectId, Integer dev);

    public GoodsPageVO getAppSpuDetail(Long spuId);

    public List<GoodsPageVO> getAppSpuDetails(List<Long> spuIds);
}

package com.muling.mall.pms.es.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.base.BasePageQuery;
import com.muling.mall.pms.pojo.vo.SubjectVO;

import java.util.List;

public interface PmsSubjectEService {

    List<SubjectVO> getAppSubjectDetails(List<Long> subjectIds);

    IPage<SubjectVO> page(BasePageQuery queryParams);
}

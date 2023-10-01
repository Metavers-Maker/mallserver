package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.pms.pojo.entity.PmsSubject;
import com.muling.mall.pms.pojo.form.SubjectFormDTO;
import com.muling.mall.pms.pojo.query.SubjectPageQuery;
import com.muling.mall.pms.pojo.vo.SubjectVO;

import java.util.List;

public interface IPmsSubjectService extends IService<PmsSubject> {

    public IPage<SubjectVO> page(SubjectPageQuery queryParams);

    public PmsSubject save(SubjectFormDTO subjectForm);

    public boolean updateById(Long id, SubjectFormDTO subjectForm);

    /**
     * 「移动端」获取商品详情
     *
     * @param subjectIds
     * @return
     */
    public List<SubjectVO> getAppSubjectDetails(List<Long> subjectIds);
}

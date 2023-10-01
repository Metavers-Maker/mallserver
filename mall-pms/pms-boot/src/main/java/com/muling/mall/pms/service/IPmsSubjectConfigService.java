package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.pms.pojo.entity.PmsSubjectConfig;
import com.muling.mall.pms.pojo.form.SubjectConfigFormDTO;
import com.muling.mall.pms.pojo.query.SubjectConfigPageQuery;
import com.muling.mall.pms.pojo.vo.SubjectConfigVO;
import com.muling.mall.pms.pojo.vo.SubjectVO;

import java.util.List;

public interface IPmsSubjectConfigService extends IService<PmsSubjectConfig> {

    public IPage<SubjectConfigVO> page(SubjectConfigPageQuery queryParams);

    public PmsSubjectConfig save(SubjectConfigFormDTO subjectForm);

    public boolean updateById(Long id, SubjectConfigFormDTO subjectForm);

//    /**
//     * 「移动端」获取商品详情
//     *
//     * @param subjectIds
//     * @return
//     */
//    public List<SubjectVO> getAppSubjectDetails(List<Long> subjectIds);
}

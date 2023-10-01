package com.muling.mall.pms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.converter.SubjectConverter;
import com.muling.mall.pms.mapper.PmsSubjectMapper;
import com.muling.mall.pms.pojo.entity.PmsSubject;
import com.muling.mall.pms.pojo.form.SubjectFormDTO;
import com.muling.mall.pms.pojo.query.SubjectPageQuery;
import com.muling.mall.pms.pojo.vo.SubjectVO;
import com.muling.mall.pms.service.IPmsSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PmsSubjectServiceImpl extends ServiceImpl<PmsSubjectMapper, PmsSubject> implements IPmsSubjectService {

    private final BusinessNoGenerator businessNoGenerator;

    public IPage<SubjectVO> page(SubjectPageQuery queryParams) {

        LambdaQueryWrapper<PmsSubject> wrapper = Wrappers.<PmsSubject>lambdaQuery()
                .eq(PmsSubject::getVisible, GlobalConstants.STATUS_YES)
                .like(StrUtil.isNotBlank(queryParams.getName()), PmsSubject::getName, queryParams.getName())
                .orderByDesc(PmsSubject::getSort);
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<SubjectVO> list = SubjectConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public PmsSubject save(SubjectFormDTO subjectForm) {

        PmsSubject subject = SubjectConverter.INSTANCE.formToPo(subjectForm);
        Long id = businessNoGenerator.generateLong(BusinessTypeEnum.SUBJECT);
        subject.setId(id);
        boolean b = save(subject);
        if (b) {
            return subject;
        } else {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
    }

    @Override
    public boolean updateById(Long id, SubjectFormDTO subjectForm) {
        PmsSubject subject = getById(id);
        if (subject == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        SubjectConverter.INSTANCE.updatePo(subjectForm, subject);
        boolean b = updateById(subject);
        return b;
    }

    @Override
    public List<SubjectVO> getAppSubjectDetails(List<Long> subjectIds) {
        LambdaQueryWrapper<PmsSubject> wrapper = Wrappers.<PmsSubject>lambdaQuery()
                .eq(PmsSubject::getVisible, GlobalConstants.STATUS_YES)
                .in(PmsSubject::getId, subjectIds);
        List<PmsSubject> pmsSubjects = this.list(wrapper);
        List<SubjectVO> result = SubjectConverter.INSTANCE.po2voList(pmsSubjects);
        return result;
    }

}

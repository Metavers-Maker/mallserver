package com.muling.mall.pms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.converter.SubjectConfigConverter;
import com.muling.mall.pms.mapper.PmsSubjectConfigMapper;
import com.muling.mall.pms.pojo.entity.PmsSubjectConfig;
import com.muling.mall.pms.pojo.form.SubjectConfigFormDTO;
import com.muling.mall.pms.pojo.query.SubjectConfigPageQuery;
import com.muling.mall.pms.pojo.vo.SubjectConfigVO;
import com.muling.mall.pms.service.IPmsSubjectConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PmsSubjectConfigServiceImpl extends ServiceImpl<PmsSubjectConfigMapper, PmsSubjectConfig> implements IPmsSubjectConfigService {

//    private final BusinessNoGenerator businessNoGenerator;

    public IPage<SubjectConfigVO> page(SubjectConfigPageQuery queryParams) {

        LambdaQueryWrapper<PmsSubjectConfig> wrapper = Wrappers.<PmsSubjectConfig>lambdaQuery()
                .eq(queryParams.getSubject_id()!=null,PmsSubjectConfig::getSubjectId, queryParams.getSubject_id())
                .eq(queryParams.getSpu_id()!=null,PmsSubjectConfig::getSpuId, queryParams.getSpu_id())
                .orderByDesc(PmsSubjectConfig::getSort);
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);
        List<SubjectConfigVO> list = SubjectConfigConverter.INSTANCE.po2voList(page.getRecords());
        return page.setRecords(list);
    }

    @Override
    public PmsSubjectConfig save(SubjectConfigFormDTO subjectForm) {

        PmsSubjectConfig subject = SubjectConfigConverter.INSTANCE.formToPo(subjectForm);
//        Long id = businessNoGenerator.generateLong(BusinessTypeEnum.SUBJECT);
//        subject.setId(id);
        boolean b = save(subject);
        if (b) {
            return subject;
        } else {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
    }

    @Override
    public boolean updateById(Long id, SubjectConfigFormDTO subjectForm) {
        PmsSubjectConfig subject = getById(id);
        if (subject == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        SubjectConfigConverter.INSTANCE.updatePo(subjectForm, subject);
        boolean b = updateById(subject);
        return b;
    }

//    @Override
//    public List<SubjectVO> getAppSubjectDetails(List<Long> subjectIds) {
//        LambdaQueryWrapper<PmsSubject> wrapper = Wrappers.<PmsSubject>lambdaQuery()
//                .eq(PmsSubject::getVisible, GlobalConstants.STATUS_YES)
//                .in(PmsSubject::getId, subjectIds);
//        List<PmsSubject> pmsSubjects = this.list(wrapper);
//        List<SubjectVO> result = SubjectConverter.INSTANCE.po2voList(pmsSubjects);
//        return result;
//    }

}

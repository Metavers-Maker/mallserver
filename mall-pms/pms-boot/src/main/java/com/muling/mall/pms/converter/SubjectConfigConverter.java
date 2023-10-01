package com.muling.mall.pms.converter;

import com.muling.mall.pms.pojo.entity.PmsSubjectConfig;
import com.muling.mall.pms.pojo.form.SubjectConfigFormDTO;
import com.muling.mall.pms.pojo.vo.SubjectConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubjectConfigConverter {

    SubjectConfigConverter INSTANCE = Mappers.getMapper(SubjectConfigConverter.class);

    SubjectConfigVO po2vo(PmsSubjectConfig subjectConfig);

    List<SubjectConfigVO> po2voList(List<PmsSubjectConfig> subjectConfigs);

    PmsSubjectConfig formToPo(SubjectConfigFormDTO subjectConfigFormDTO);

    void updatePo(SubjectConfigFormDTO bannerForm, @MappingTarget PmsSubjectConfig subject);

//    List<SubjectVO> subjects2VoList(List<com.muling.mall.pms.es.entity.PmsSubject> subjects);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

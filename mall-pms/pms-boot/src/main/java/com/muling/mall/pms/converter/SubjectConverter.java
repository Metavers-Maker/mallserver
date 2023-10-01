package com.muling.mall.pms.converter;

import com.muling.mall.pms.pojo.entity.PmsSubject;
import com.muling.mall.pms.pojo.form.SubjectFormDTO;
import com.muling.mall.pms.pojo.vo.SubjectVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubjectConverter {

    SubjectConverter INSTANCE = Mappers.getMapper(SubjectConverter.class);

    SubjectVO po2vo(PmsSubject subject);

    List<SubjectVO> po2voList(List<PmsSubject> subjects);

    PmsSubject formToPo(SubjectFormDTO bannerForm);

    void updatePo(SubjectFormDTO bannerForm, @MappingTarget PmsSubject subject);

    List<SubjectVO> subjects2VoList(List<com.muling.mall.pms.es.entity.PmsSubject> subjects);

//    @Mapping(source = "subject.id", target = "id")
//    @Mapping(source = "subject.created", target = "created")
//    Subject po2esPo(PmsSubject subject, @Context ZoneId zoneId);
//
//    default Date fromLocalDateTime(LocalDateTime localDateTime, @Context ZoneId zoneId) {
//        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
//    }

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

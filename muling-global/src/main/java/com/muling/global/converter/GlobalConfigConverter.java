package com.muling.global.converter;

import com.muling.global.pojo.entity.GlobalConfig;
import com.muling.global.pojo.form.GlobalConfigForm;
import com.muling.global.pojo.vo.GlobalConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GlobalConfigConverter {

    GlobalConfigConverter INSTANCE = Mappers.getMapper(GlobalConfigConverter.class);

    GlobalConfig form2po(GlobalConfigForm configForm);

    void updatePo(GlobalConfigForm configForm, @MappingTarget GlobalConfig config);

    GlobalConfigVO po2vo(GlobalConfig globalConfig);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

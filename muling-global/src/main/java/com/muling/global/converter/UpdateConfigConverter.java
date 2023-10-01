package com.muling.global.converter;

import com.muling.global.pojo.entity.UpdateConfig;
import com.muling.global.pojo.form.UpdateConfigForm;
import com.muling.global.pojo.vo.UpdateConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateConfigConverter {

    UpdateConfigConverter INSTANCE = Mappers.getMapper(UpdateConfigConverter.class);

    UpdateConfig form2po(UpdateConfigForm configForm);

    void updatePo(UpdateConfigForm configForm, @MappingTarget UpdateConfig config);

    UpdateConfigVO po2vo(UpdateConfig globalConfig);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

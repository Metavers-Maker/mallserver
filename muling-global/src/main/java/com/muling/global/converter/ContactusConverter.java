package com.muling.global.converter;

import com.muling.global.pojo.entity.Contactus;
import com.muling.global.pojo.form.ContactusForm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactusConverter {

    ContactusConverter INSTANCE = Mappers.getMapper(ContactusConverter.class);

    Contactus form2po(ContactusForm contactusForm);

    void updatePo(ContactusForm contactusForm, @MappingTarget Contactus contactus);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

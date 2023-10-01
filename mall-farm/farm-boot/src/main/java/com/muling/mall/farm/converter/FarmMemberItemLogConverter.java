package com.muling.mall.farm.converter;

import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.entity.FarmMemberLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmMemberItemLogConverter {

    FarmMemberItemLogConverter INSTANCE = Mappers.getMapper(FarmMemberItemLogConverter.class);

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    FarmMemberLog po2log(FarmMember farmMember);

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    FarmMemberLog po2log(FarmMemberItem farmMemberItem);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

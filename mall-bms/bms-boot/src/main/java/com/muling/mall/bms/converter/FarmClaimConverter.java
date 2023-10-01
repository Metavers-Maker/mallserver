package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.OmsFarmClaim;
import com.muling.mall.bms.pojo.vo.app.FarmClaimVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmClaimConverter {

    FarmClaimConverter INSTANCE = Mappers.getMapper(FarmClaimConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    FarmClaimVO do2vo(OmsFarmClaim reward);

    List<FarmClaimVO> po2voList(List<OmsFarmClaim> rewards);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.OmsFarmLog;
import com.muling.mall.bms.pojo.vo.app.FarmLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmLogConverter {

    FarmLogConverter INSTANCE = Mappers.getMapper(FarmLogConverter.class);

    @Mappings({
            @Mapping(source = "logType.value", target = "logType")
    })
    FarmLogVO do2vo(OmsFarmLog farmLog);

    List<FarmLogVO> po2voList(List<OmsFarmLog> farmLogs);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

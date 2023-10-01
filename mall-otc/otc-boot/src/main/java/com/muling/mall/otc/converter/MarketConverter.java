package com.muling.mall.otc.converter;

import com.muling.mall.otc.pojo.entity.OtcMarket;
import com.muling.mall.otc.pojo.vo.MarketVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MarketConverter {

    MarketConverter INSTANCE = Mappers.getMapper(MarketConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    MarketVO do2vo(OtcMarket market);

    List<MarketVO> po2voList(List<OtcMarket> markets);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

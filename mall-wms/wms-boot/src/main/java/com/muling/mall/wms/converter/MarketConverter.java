package com.muling.mall.wms.converter;

import com.muling.mall.wms.pojo.entity.WmsMarket;
import com.muling.mall.wms.pojo.vo.MarketVO;
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
            @Mapping(source = "status.value", target = "status"),
            @Mapping(source = "step.value", target = "step")
    })
    MarketVO po2vo(WmsMarket market);

//    WmsMarket vo2po(MarketVO marketVO);

    List<MarketVO> po2voList(List<WmsMarket> markets);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

package com.muling.mall.bms.converter;

import com.muling.mall.bms.dto.MarketItemDTO;
import com.muling.mall.bms.pojo.entity.OmsMarket;
import com.muling.mall.bms.pojo.vo.app.MarketVO;
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
            @Mapping(source = "itemType.value", target = "itemType")
    })
    MarketVO po2vo(OmsMarket market);

    MarketItemDTO po2dto(OmsMarket market);

    List<MarketVO> po2voList(List<OmsMarket> markets);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

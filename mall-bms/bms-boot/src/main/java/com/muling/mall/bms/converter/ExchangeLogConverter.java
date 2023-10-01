package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.OmsExchangeLog;
import com.muling.mall.bms.pojo.vo.app.ExchangeLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExchangeLogConverter {

    ExchangeLogConverter INSTANCE = Mappers.getMapper(ExchangeLogConverter.class);

    ExchangeLogVO do2vo(OmsExchangeLog log);

    List<ExchangeLogVO> po2voList(List<OmsExchangeLog> logs);


    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

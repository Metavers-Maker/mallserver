package com.muling.mall.farm.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.farm.pojo.entity.FarmAd;
import com.muling.mall.farm.pojo.entity.FarmAdItem;
import com.muling.mall.farm.pojo.vo.app.FarmAdVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmAdConverter {

    FarmAdConverter INSTANCE = Mappers.getMapper(FarmAdConverter.class);

    FarmAdVO po2vo(FarmAd farmAd);

    List<FarmAdVO> po2voList(List<FarmAd> configs);

    Page<FarmAdVO> entity2PageVO(Page<FarmAd> adPage);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

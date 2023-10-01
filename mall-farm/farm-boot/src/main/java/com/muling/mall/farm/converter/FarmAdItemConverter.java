package com.muling.mall.farm.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.farm.pojo.dto.FarmAdItemDTO;
import com.muling.mall.farm.pojo.entity.FarmAdItem;
import com.muling.mall.farm.pojo.vo.app.FarmAdItemVO;
import com.muling.mall.farm.pojo.vo.app.FarmAdVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmAdItemConverter {

    FarmAdItemConverter INSTANCE = Mappers.getMapper(FarmAdItemConverter.class);

    FarmAdItem dtoToEntity(FarmAdItemDTO farmAdItemDTO);

    FarmAdItemVO po2vo(FarmAdItem farmAdItem);

    List<FarmAdItemVO> po2voList(List<FarmAdItem> configs);

    Page<FarmAdItemVO> entity2PageVO(Page<FarmAdItem> adPage);


    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

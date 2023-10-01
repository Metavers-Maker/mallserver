package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.OmsFarmPool;
import com.muling.mall.bms.pojo.form.admin.StakeConfigForm;
import com.muling.mall.bms.pojo.vo.app.FarmPoolVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmPoolConverter {

    FarmPoolConverter INSTANCE = Mappers.getMapper(FarmPoolConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    FarmPoolVO do2vo(OmsFarmPool config);

    List<FarmPoolVO> po2voList(List<OmsFarmPool> configs);

    OmsFarmPool form2po(StakeConfigForm configForm);

    void updatePo(StakeConfigForm configForm, @MappingTarget OmsFarmPool config);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

package com.muling.mall.pms.converter;

import com.muling.mall.pms.pojo.entity.PmsGround;
import com.muling.mall.pms.pojo.form.GroundForm;
import com.muling.mall.pms.pojo.vo.GroundVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroundConverter {

    GroundConverter INSTANCE = Mappers.getMapper(GroundConverter.class);

    PmsGround form2Po(GroundForm groundForm);

    List<GroundVO> po2Vo(List<PmsGround> pmsGrounds);

    List<GroundVO> grounds2VoList(List<com.muling.mall.pms.es.entity.PmsGround> pmsGrounds);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

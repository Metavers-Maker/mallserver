package com.muling.mall.pms.converter;

import com.muling.mall.pms.pojo.dto.RndDTO;
import com.muling.mall.pms.pojo.entity.PmsRnd;
import com.muling.mall.pms.pojo.form.RndForm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RndConverter {

    RndConverter INSTANCE = Mappers.getMapper(RndConverter.class);

    PmsRnd form2Po(RndForm rndForm);

    List<RndDTO> po2DTOs(List<PmsRnd> pmsRndList);

    RndDTO po2DTO(PmsRnd pmsRnd);

    void updatePo(RndForm rndForm, @MappingTarget PmsRnd rnd);
}

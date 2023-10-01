package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.OmsCompoundConfig;
import com.muling.mall.bms.pojo.form.admin.CompoundConfigForm;
import com.muling.mall.bms.pojo.vo.app.CompoundVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompoundConfigConverter {

    CompoundConfigConverter INSTANCE = Mappers.getMapper(CompoundConfigConverter.class);

//    @Mappings({
//            @Mapping(source = "status.value", target = "status")
//    })
    CompoundVO do2vo(OmsCompoundConfig config);

    List<CompoundVO> po2voList(List<OmsCompoundConfig> configs);

    OmsCompoundConfig form2po(CompoundConfigForm configForm);

    void updatePo(CompoundConfigForm configForm, @MappingTarget OmsCompoundConfig config);


}

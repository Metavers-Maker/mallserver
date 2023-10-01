package com.muling.mall.pms.converter;

import com.muling.mall.pms.pojo.entity.PmsHot;
import com.muling.mall.pms.pojo.form.HotForm;
import com.muling.mall.pms.pojo.vo.HotVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotConverter {

    HotConverter INSTANCE = Mappers.getMapper(HotConverter.class);

    @Mappings({
            @Mapping(source = "contentType.value", target = "contentType")
    })
    HotVO do2vo(PmsHot hot);

    List<HotVO> hotsToVOs(List<PmsHot> hots);


    PmsHot form2Po(HotForm hotForm);


    List<HotVO> hotsToVoList(List<com.muling.mall.pms.es.entity.PmsHot> hots);
}

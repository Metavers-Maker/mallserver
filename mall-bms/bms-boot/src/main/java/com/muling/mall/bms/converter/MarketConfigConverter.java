package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.OmsMarketConfig;
import com.muling.mall.bms.pojo.form.admin.MarketConfigForm;
import com.muling.mall.bms.pojo.vo.app.MarketConfigVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MarketConfigConverter {

    MarketConfigConverter INSTANCE = Mappers.getMapper(MarketConfigConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    MarketConfigVO do2vo(OmsMarketConfig config);

    List<MarketConfigVO> po2voList(List<OmsMarketConfig> configs);

    OmsMarketConfig form2po(MarketConfigForm configForm);

    void updatePo(MarketConfigForm configForm, @MappingTarget OmsMarketConfig config);


}

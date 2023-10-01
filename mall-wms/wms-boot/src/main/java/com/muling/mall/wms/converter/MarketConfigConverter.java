package com.muling.mall.wms.converter;

import com.muling.mall.wms.pojo.entity.WmsMarketConfig;
import com.muling.mall.wms.pojo.form.admin.MarketConfigForm;
import com.muling.mall.wms.pojo.vo.MarketConfigVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MarketConfigConverter {

    MarketConfigConverter INSTANCE = Mappers.getMapper(MarketConfigConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    MarketConfigVO do2vo(WmsMarketConfig config);

    List<MarketConfigVO> po2voList(List<WmsMarketConfig> configs);

    WmsMarketConfig form2po(MarketConfigForm configForm);

    void updatePo(MarketConfigForm configForm, @MappingTarget WmsMarketConfig config);


}

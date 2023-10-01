package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.OmsExchangeConfig;
import com.muling.mall.bms.pojo.form.admin.ExchangeConfigForm;
import com.muling.mall.bms.pojo.vo.app.ExchangeVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExchangeConfigConverter {

    ExchangeConfigConverter INSTANCE = Mappers.getMapper(ExchangeConfigConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status"),
            @Mapping(source = "exchangeType.value", target = "exchangeType")
    })
    ExchangeVO do2vo(OmsExchangeConfig config);

    List<ExchangeVO> po2voList(List<OmsExchangeConfig> configs);

    OmsExchangeConfig form2po(ExchangeConfigForm configForm);

    void updatePo(ExchangeConfigForm configForm, @MappingTarget OmsExchangeConfig config);


}

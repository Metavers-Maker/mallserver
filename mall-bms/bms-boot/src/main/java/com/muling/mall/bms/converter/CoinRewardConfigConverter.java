package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.BmsCoinConfig;
import com.muling.mall.bms.pojo.entity.OmsExchangeConfig;
import com.muling.mall.bms.pojo.form.admin.CoinRewardConfigForm;
import com.muling.mall.bms.pojo.form.admin.ExchangeConfigForm;
import com.muling.mall.bms.pojo.vo.app.CoinRewardVO;
import com.muling.mall.bms.pojo.vo.app.ExchangeVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoinRewardConfigConverter {

    CoinRewardConfigConverter INSTANCE = Mappers.getMapper(CoinRewardConfigConverter.class);

//    @Mappings({
//            @Mapping(source = "visible.value", target = "visible"),
//            @Mapping(source = "fromType.value", target = "fromType")
//    })
    CoinRewardVO do2vo(BmsCoinConfig config);

    List<CoinRewardVO> po2voList(List<BmsCoinConfig> configs);

    BmsCoinConfig form2po(CoinRewardConfigForm configForm);

    void updatePo(CoinRewardConfigForm configForm, @MappingTarget BmsCoinConfig config);
}

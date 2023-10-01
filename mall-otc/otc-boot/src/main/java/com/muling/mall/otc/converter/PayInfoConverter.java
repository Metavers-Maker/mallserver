package com.muling.mall.otc.converter;

import com.muling.mall.otc.pojo.entity.OtcPayInfo;
import com.muling.mall.otc.pojo.form.PayInfoForm;
import com.muling.mall.otc.pojo.vo.PayInfoVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PayInfoConverter {

    PayInfoConverter INSTANCE = Mappers.getMapper(PayInfoConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    PayInfoVO do2vo(OtcPayInfo config);

    List<PayInfoVO> po2voList(List<OtcPayInfo> configs);

    OtcPayInfo form2po(PayInfoForm configForm);

    void updatePo(PayInfoForm configForm, @MappingTarget OtcPayInfo config);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

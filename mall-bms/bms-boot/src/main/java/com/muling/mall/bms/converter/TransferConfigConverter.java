package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.OmsTransferConfig;
import com.muling.mall.bms.pojo.form.admin.TransferConfigForm;
import com.muling.mall.bms.pojo.vo.app.TransferVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferConfigConverter {

    TransferConfigConverter INSTANCE = Mappers.getMapper(TransferConfigConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    TransferVO do2vo(OmsTransferConfig config);

    List<TransferVO> po2voList(List<OmsTransferConfig> configs);

    OmsTransferConfig form2po(TransferConfigForm configForm);

    void updatePo(TransferConfigForm configForm, @MappingTarget OmsTransferConfig config);


}

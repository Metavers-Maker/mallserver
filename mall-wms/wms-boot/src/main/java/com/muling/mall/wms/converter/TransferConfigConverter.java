package com.muling.mall.wms.converter;

import com.muling.mall.wms.pojo.entity.WmsTransferConfig;
import com.muling.mall.wms.pojo.form.admin.TransferConfigForm;
import com.muling.mall.wms.pojo.vo.TransferVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferConfigConverter {

    TransferConfigConverter INSTANCE = Mappers.getMapper(TransferConfigConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    TransferVO do2vo(WmsTransferConfig config);

    List<TransferVO> po2voList(List<WmsTransferConfig> configs);

    WmsTransferConfig form2po(TransferConfigForm configForm);

    void updatePo(TransferConfigForm configForm, @MappingTarget WmsTransferConfig config);



}

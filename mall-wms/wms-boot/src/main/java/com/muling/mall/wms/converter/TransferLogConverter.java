package com.muling.mall.wms.converter;

import com.muling.mall.wms.pojo.entity.WmsTransferConfig;
import com.muling.mall.wms.pojo.entity.WmsTransferLog;
import com.muling.mall.wms.pojo.form.admin.TransferConfigForm;
import com.muling.mall.wms.pojo.vo.TransferLogVO;
import com.muling.mall.wms.pojo.vo.TransferVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferLogConverter {

    TransferLogConverter INSTANCE = Mappers.getMapper(TransferLogConverter.class);

    TransferLogVO do2vo(WmsTransferLog log);

    List<TransferLogVO> po2voList(List<WmsTransferLog> configs);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

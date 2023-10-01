package com.muling.mall.wms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.wms.pojo.entity.WmsWalletLog;
import com.muling.mall.wms.pojo.vo.WalletLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WalletLogConverter {

    WalletLogConverter INSTANCE = Mappers.getMapper(WalletLogConverter.class);

    @Mappings({
            @Mapping(source = "opType.value", target = "opType")
    })
    WalletLogVO do2vo(WmsWalletLog walletLog);

    Page<WalletLogVO> entity2PageVO(Page<WmsWalletLog> entityPage);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

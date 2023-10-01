package com.muling.mall.wms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.base.IBaseEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.pojo.form.admin.WalletForm;
import com.muling.mall.wms.pojo.vo.WalletVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WalletConverter {

    WalletConverter INSTANCE = Mappers.getMapper(WalletConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    WalletVO do2vo(WmsWallet wallet);

    Page<WalletVO> entity2PageVO(Page<WmsWallet> entityPage);

    WalletDTO form2dto(WalletForm walletForm);

    WmsWallet dto2po(WalletDTO walletDTO);

    void updatePo(WalletForm walletForm, @MappingTarget WmsWallet wallet);

}

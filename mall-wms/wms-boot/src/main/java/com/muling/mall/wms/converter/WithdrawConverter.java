package com.muling.mall.wms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.base.IBaseEnum;
import com.muling.mall.wms.pojo.entity.WmsWithdraw;
import com.muling.mall.wms.pojo.vo.WithdrawVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WithdrawConverter {

    WithdrawConverter INSTANCE = Mappers.getMapper(WithdrawConverter.class);

    WithdrawVO do2vo(WmsWithdraw wallet);

    Page<WithdrawVO> entity2PageVO(Page<WmsWithdraw> entityPage);

//    WalletDTO form2dto(WalletForm walletForm);
//
//    WmsWithdraw dto2po(WalletDTO walletDTO);
//
//    void updatePo(WalletForm walletForm, @MappingTarget WmsWithdraw wallet);

}

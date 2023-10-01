package com.muling.mall.wms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.wms.pojo.entity.WmsWithdrawCoin;
import com.muling.mall.wms.pojo.vo.WithdrawCoinVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WithdrawCoinConverter {

    WithdrawCoinConverter INSTANCE = Mappers.getMapper(WithdrawCoinConverter.class);

    WithdrawCoinVO do2vo(WmsWithdrawCoin wallet);

    Page<WithdrawCoinVO> entity2PageVO(Page<WmsWithdrawCoin> entityPage);

}

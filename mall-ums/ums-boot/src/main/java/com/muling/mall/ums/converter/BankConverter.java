package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberBankDTO;
import com.muling.mall.ums.pojo.entity.UmsBank;
import com.muling.mall.ums.pojo.vo.BankVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankConverter {

    BankConverter INSTANCE = Mappers.getMapper(BankConverter.class);

    MemberBankDTO pos2dto(UmsBank umsBank);

    List<BankVO> pos2vos(List<UmsBank> umsBanks);
}

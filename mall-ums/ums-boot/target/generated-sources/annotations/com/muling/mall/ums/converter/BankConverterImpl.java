package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberBankDTO;
import com.muling.mall.ums.pojo.entity.UmsBank;
import com.muling.mall.ums.pojo.vo.BankVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-01T17:21:13+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 14.0.1 (Oracle Corporation)"
)
@Component
public class BankConverterImpl implements BankConverter {

    @Override
    public MemberBankDTO pos2dto(UmsBank umsBank) {
        if ( umsBank == null ) {
            return null;
        }

        MemberBankDTO memberBankDTO = new MemberBankDTO();

        memberBankDTO.setId( umsBank.getId() );

        return memberBankDTO;
    }

    @Override
    public List<BankVO> pos2vos(List<UmsBank> umsBanks) {
        if ( umsBanks == null ) {
            return null;
        }

        List<BankVO> list = new ArrayList<BankVO>( umsBanks.size() );
        for ( UmsBank umsBank : umsBanks ) {
            list.add( umsBankToBankVO( umsBank ) );
        }

        return list;
    }

    protected BankVO umsBankToBankVO(UmsBank umsBank) {
        if ( umsBank == null ) {
            return null;
        }

        BankVO bankVO = new BankVO();

        bankVO.setId( umsBank.getId() );
        bankVO.setBankCardCode( umsBank.getBankCardCode() );
        bankVO.setBankName( umsBank.getBankName() );
        bankVO.setUsed( umsBank.getUsed() );

        return bankVO;
    }
}

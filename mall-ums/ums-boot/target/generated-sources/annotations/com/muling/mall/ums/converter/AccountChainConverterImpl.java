package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberAccountChainDTO;
import com.muling.mall.ums.pojo.entity.UmsAccountChain;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-01T17:21:14+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 14.0.1 (Oracle Corporation)"
)
@Component
public class AccountChainConverterImpl implements AccountChainConverter {

    @Override
    public List<MemberAccountChainDTO> pos2dtos(List<UmsAccountChain> umsAddresses) {
        if ( umsAddresses == null ) {
            return null;
        }

        List<MemberAccountChainDTO> list = new ArrayList<MemberAccountChainDTO>( umsAddresses.size() );
        for ( UmsAccountChain umsAccountChain : umsAddresses ) {
            list.add( umsAccountChainToMemberAccountChainDTO( umsAccountChain ) );
        }

        return list;
    }

    protected MemberAccountChainDTO umsAccountChainToMemberAccountChainDTO(UmsAccountChain umsAccountChain) {
        if ( umsAccountChain == null ) {
            return null;
        }

        MemberAccountChainDTO memberAccountChainDTO = new MemberAccountChainDTO();

        memberAccountChainDTO.setId( umsAccountChain.getId() );
        memberAccountChainDTO.setMemberId( umsAccountChain.getMemberId() );
        memberAccountChainDTO.setAddress( umsAccountChain.getAddress() );
        memberAccountChainDTO.setChainType( umsAccountChain.getChainType() );
        memberAccountChainDTO.setBankCardCode( umsAccountChain.getBankCardCode() );
        memberAccountChainDTO.setBankName( umsAccountChain.getBankName() );
        memberAccountChainDTO.setBankUsername( umsAccountChain.getBankUsername() );
        memberAccountChainDTO.setStatus( umsAccountChain.getStatus() );

        return memberAccountChainDTO;
    }
}

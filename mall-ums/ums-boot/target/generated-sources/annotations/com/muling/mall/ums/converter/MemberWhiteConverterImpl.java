package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberWhiteDTO;
import com.muling.mall.ums.pojo.entity.UmsWhite;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-01T17:21:14+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 14.0.1 (Oracle Corporation)"
)
@Component
public class MemberWhiteConverterImpl implements MemberWhiteConverter {

    @Override
    public MemberWhiteDTO po2dto(UmsWhite umsWhite) {
        if ( umsWhite == null ) {
            return null;
        }

        MemberWhiteDTO memberWhiteDTO = new MemberWhiteDTO();

        memberWhiteDTO.setMemberId( umsWhite.getMemberId() );
        memberWhiteDTO.setMobile( umsWhite.getMobile() );
        memberWhiteDTO.setLevel( umsWhite.getLevel() );

        return memberWhiteDTO;
    }
}

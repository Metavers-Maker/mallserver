package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberRealDTO;
import com.muling.mall.ums.pojo.entity.UmsMemberAuth;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-14T23:25:17+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 14.0.1 (Oracle Corporation)"
)
@Component
public class MemberAuthConverterImpl implements MemberAuthConverter {

    @Override
    public MemberRealDTO po2dto(UmsMemberAuth memberAuth) {
        if ( memberAuth == null ) {
            return null;
        }

        MemberRealDTO memberRealDTO = new MemberRealDTO();

        memberRealDTO.setRealName( memberAuth.getRealName() );
        memberRealDTO.setIdCard( memberAuth.getIdCard() );

        return memberRealDTO;
    }
}

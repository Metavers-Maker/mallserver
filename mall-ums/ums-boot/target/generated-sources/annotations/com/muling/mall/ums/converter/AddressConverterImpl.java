package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import com.muling.mall.ums.pojo.entity.UmsAddress;
import com.muling.mall.ums.pojo.form.AddressForm;
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
public class AddressConverterImpl implements AddressConverter {

    @Override
    public UmsAddress form2po(AddressForm addressForm) {
        if ( addressForm == null ) {
            return null;
        }

        UmsAddress umsAddress = new UmsAddress();

        umsAddress.setId( addressForm.getId() );
        umsAddress.setConsigneeName( addressForm.getConsigneeName() );
        umsAddress.setConsigneeMobile( addressForm.getConsigneeMobile() );
        umsAddress.setProvince( addressForm.getProvince() );
        umsAddress.setCity( addressForm.getCity() );
        umsAddress.setArea( addressForm.getArea() );
        umsAddress.setDetailAddress( addressForm.getDetailAddress() );
        umsAddress.setDefaulted( addressForm.getDefaulted() );

        return umsAddress;
    }

    @Override
    public List<MemberAddressDTO> pos2dtos(List<UmsAddress> umsAddresses) {
        if ( umsAddresses == null ) {
            return null;
        }

        List<MemberAddressDTO> list = new ArrayList<MemberAddressDTO>( umsAddresses.size() );
        for ( UmsAddress umsAddress : umsAddresses ) {
            list.add( umsAddressToMemberAddressDTO( umsAddress ) );
        }

        return list;
    }

    protected MemberAddressDTO umsAddressToMemberAddressDTO(UmsAddress umsAddress) {
        if ( umsAddress == null ) {
            return null;
        }

        MemberAddressDTO memberAddressDTO = new MemberAddressDTO();

        memberAddressDTO.setId( umsAddress.getId() );
        memberAddressDTO.setMemberId( umsAddress.getMemberId() );
        memberAddressDTO.setConsigneeName( umsAddress.getConsigneeName() );
        memberAddressDTO.setConsigneeMobile( umsAddress.getConsigneeMobile() );
        memberAddressDTO.setProvince( umsAddress.getProvince() );
        memberAddressDTO.setCity( umsAddress.getCity() );
        memberAddressDTO.setArea( umsAddress.getArea() );
        memberAddressDTO.setZipCode( umsAddress.getZipCode() );
        memberAddressDTO.setDetailAddress( umsAddress.getDetailAddress() );
        memberAddressDTO.setDefaulted( umsAddress.getDefaulted() );

        return memberAddressDTO;
    }
}

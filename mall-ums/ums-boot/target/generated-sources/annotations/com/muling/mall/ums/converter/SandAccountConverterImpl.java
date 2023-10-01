package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberSandDTO;
import com.muling.mall.ums.pojo.entity.UmsSandAccount;
import com.muling.mall.ums.pojo.vo.SandAccountVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-01T17:21:14+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 14.0.1 (Oracle Corporation)"
)
@Component
public class SandAccountConverterImpl implements SandAccountConverter {

    @Override
    public SandAccountVO po2vo(UmsSandAccount sandAccount) {
        if ( sandAccount == null ) {
            return null;
        }

        SandAccountVO sandAccountVO = new SandAccountVO();

        sandAccountVO.setId( sandAccount.getId() );
        sandAccountVO.setNickName( sandAccount.getNickName() );

        return sandAccountVO;
    }

    @Override
    public MemberSandDTO po2dto(UmsSandAccount sandAccount) {
        if ( sandAccount == null ) {
            return null;
        }

        MemberSandDTO memberSandDTO = new MemberSandDTO();

        memberSandDTO.setMemberId( sandAccount.getMemberId() );
        memberSandDTO.setUserId( sandAccount.getUserId() );
        memberSandDTO.setNickName( sandAccount.getNickName() );
        memberSandDTO.setStatus( sandAccount.getStatus() );

        return memberSandDTO;
    }
}

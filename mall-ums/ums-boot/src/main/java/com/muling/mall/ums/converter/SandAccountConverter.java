package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberSandDTO;
import com.muling.mall.ums.pojo.entity.UmsSandAccount;
import com.muling.mall.ums.pojo.vo.SandAccountVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SandAccountConverter {

    SandAccountConverter INSTANCE = Mappers.getMapper(SandAccountConverter.class);

    SandAccountVO po2vo(UmsSandAccount sandAccount);

    MemberSandDTO po2dto(UmsSandAccount sandAccount);

}

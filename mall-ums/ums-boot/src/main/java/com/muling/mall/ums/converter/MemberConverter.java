package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberRegisterDTO;
import com.muling.mall.ums.pojo.dto.MemberSimpleDTO;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.vo.MemberCoinRank;
import com.muling.mall.ums.pojo.vo.MemberSimpleVO;
import com.muling.mall.ums.pojo.vo.MemberVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberConverter {

    MemberConverter INSTANCE = Mappers.getMapper(MemberConverter.class);

    MemberVO po2vo(UmsMember member);

    MemberDTO po2dto(UmsMember member);

    MemberCoinRank po2rank(UmsMember member);

    MemberSimpleVO po2SimpleVo(UmsMember member);

    MemberVO po2vo(com.muling.mall.ums.es.entity.UmsMember member);

    MemberSimpleDTO po2simpleDTO(UmsMember member);

    @Mappings({
            @Mapping(source = "id", target = "memberId"),
            @Mapping(source = "nickName", target = "username")
    })
    MemberAuthDTO po2authDTO(UmsMember member);

    MemberRegisterDTO po2registerDTO(UmsMember member);

    UmsMember dto2Po(MemberDTO memberDTO);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

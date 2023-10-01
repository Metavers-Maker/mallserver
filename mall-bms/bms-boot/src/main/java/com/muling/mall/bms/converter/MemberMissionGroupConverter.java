package com.muling.mall.bms.converter;

import com.muling.mall.bms.pojo.entity.OmsMemberMissionGroup;
import com.muling.mall.bms.pojo.vo.app.MemberMissionGroupVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMissionGroupConverter {

    MemberMissionGroupConverter INSTANCE = Mappers.getMapper(MemberMissionGroupConverter.class);

//    @Mappings({
//            @Mapping(source = "status.value", target = "status"),
//            @Mapping(source = "itemType.value", target = "itemType")
//    })
    MemberMissionGroupVO po2vo(OmsMemberMissionGroup memberMissionGroup);

    List<MemberMissionGroupVO> po2voList(List<OmsMemberMissionGroup> memberMissionGroups);
}

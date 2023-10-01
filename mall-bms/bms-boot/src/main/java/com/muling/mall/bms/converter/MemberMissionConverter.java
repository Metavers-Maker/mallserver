package com.muling.mall.bms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.bms.pojo.entity.OmsMemberMission;
import com.muling.mall.bms.pojo.form.app.MemberMissionForm;
import com.muling.mall.bms.pojo.vo.app.MemberMissionVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMissionConverter {

    MemberMissionConverter INSTANCE = Mappers.getMapper(MemberMissionConverter.class);

    MemberMissionVO po2vo(OmsMemberMission memberMission);

    OmsMemberMission form2po(MemberMissionForm configForm);

    List<MemberMissionVO> po2voList(List<OmsMemberMission> memberMissions);

    Page<MemberMissionVO> entity2PageVO(Page<OmsMemberMission> configsPage);

    void updatePo(MemberMissionForm configForm, @MappingTarget OmsMemberMission config);

}

package com.muling.mall.bms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.bms.pojo.entity.OmsMissionConfig;
import com.muling.mall.bms.pojo.form.admin.MissionConfigForm;
import com.muling.mall.bms.pojo.vo.app.MissionConfigVO;
import com.muling.mall.bms.pojo.vo.app.MissionConfigVOApp;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MissionConfigConverter {

    MissionConfigConverter INSTANCE = Mappers.getMapper(MissionConfigConverter.class);

    MissionConfigVO po2vo(OmsMissionConfig missionConfig);

    OmsMissionConfig form2po(MissionConfigForm configForm);

    List<MissionConfigVO> po2voList(List<OmsMissionConfig> missionConfigs);

    Page<MissionConfigVO> entity2PageVO(Page<OmsMissionConfig> configsPage);

    Page<MissionConfigVOApp> entity2PageVOApp(Page<OmsMissionConfig> configsPage);

    void updatePo(MissionConfigForm configForm, @MappingTarget OmsMissionConfig config);
}

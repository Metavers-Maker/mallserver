package com.muling.mall.bms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.bms.pojo.entity.OmsCompoundConfig;
import com.muling.mall.bms.pojo.entity.OmsMissionConfig;
import com.muling.mall.bms.pojo.entity.OmsMissionGroupConfig;
import com.muling.mall.bms.pojo.form.admin.CompoundConfigForm;
import com.muling.mall.bms.pojo.form.admin.MissionConfigForm;
import com.muling.mall.bms.pojo.form.admin.MissionGroupConfigForm;
import com.muling.mall.bms.pojo.vo.app.MissionConfigVO;
import com.muling.mall.bms.pojo.vo.app.MissionGroupConfigVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MissionGroupConfigConverter {

    MissionGroupConfigConverter INSTANCE = Mappers.getMapper(MissionGroupConfigConverter.class);

    MissionGroupConfigVO po2vo(OmsMissionGroupConfig market);

    List<MissionGroupConfigVO> po2voList(List<OmsMissionGroupConfig> markets);

    OmsMissionGroupConfig form2po(MissionGroupConfigForm configForm);

    Page<MissionGroupConfigVO> entity2PageVO(Page<OmsMissionGroupConfig> configsPage);

    void updatePo(MissionGroupConfigForm configForm, @MappingTarget OmsMissionGroupConfig config);
}

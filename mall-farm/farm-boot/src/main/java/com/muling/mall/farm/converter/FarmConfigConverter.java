package com.muling.mall.farm.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.form.admin.FarmConfigForm;
import com.muling.mall.farm.pojo.vo.app.FarmConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmConfigConverter {

    FarmConfigConverter INSTANCE = Mappers.getMapper(FarmConfigConverter.class);

    FarmConfig form2po(FarmConfigForm form);

    void updatePo(FarmConfigForm form, @MappingTarget FarmConfig config);

    FarmConfigVO po2vo(FarmConfig configs);

    Page<FarmConfigVO> entity2PageVO(Page<FarmConfig> configsPage);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

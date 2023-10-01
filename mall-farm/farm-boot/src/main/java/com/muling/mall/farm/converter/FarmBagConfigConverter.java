package com.muling.mall.farm.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import com.muling.mall.farm.pojo.form.admin.FarmBagConfigForm;
import com.muling.mall.farm.pojo.vo.app.FarmBagConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmBagConfigConverter {

    FarmBagConfigConverter INSTANCE = Mappers.getMapper(FarmBagConfigConverter.class);

    FarmBagConfig form2po(FarmBagConfigForm form);

    void updatePo(FarmBagConfigForm form, @MappingTarget FarmBagConfig news);

    FarmBagConfigVO po2vo(FarmBagConfig configs);

    Page<FarmBagConfigVO> entity2PageVO(Page<FarmBagConfig> configsPage);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

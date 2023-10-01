package com.muling.mall.pms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.form.BrandForm;
import com.muling.mall.pms.pojo.vo.BrandVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandConverter {

    BrandConverter INSTANCE = Mappers.getMapper(BrandConverter.class);

    List<BrandVO> brands2vo(List<PmsBrand> brands);

    PmsBrand form2Po(BrandForm brandForm);

    List<BrandVO> brands2VoList(List<com.muling.mall.pms.es.entity.PmsBrand> brands);

    Page<BrandVO> entity2PageVO(Page<PmsBrand> brandPage);


    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

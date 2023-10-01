package com.muling.mall.pms.converter;

import com.muling.mall.pms.pojo.dto.SkuInfoDTO;
import com.muling.mall.pms.pojo.entity.PmsSku;
import com.muling.mall.pms.pojo.form.GoodsFormDTO;
import com.muling.mall.pms.pojo.form.SkuForm;
import com.muling.mall.pms.pojo.form.UpdateSkuForm;
import com.muling.mall.pms.pojo.vo.SkuVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkuConverter {

    SkuConverter INSTANCE = Mappers.getMapper(SkuConverter.class);

    @Mappings({
            @Mapping(source = "name", target = "skuName"),
            @Mapping(source = "id", target = "skuId")
    })
    SkuInfoDTO do2dto(PmsSku sku);

//    PmsSku form2po(GoodsFormDTO.SkuFormDTO skuFormDTO);

    PmsSku form2po(SkuForm skuForm);

    void updatePo(UpdateSkuForm skuForm, @MappingTarget PmsSku sku);

    List<SkuVO> po2voList(List<PmsSku> pmsSkuList);

    List<SkuVO> skus2VoList(List<com.muling.mall.pms.es.entity.PmsSku> pmsSkuList);


    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

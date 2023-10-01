package com.muling.mall.pms.converter;

import com.muling.common.base.IBaseEnum;
import com.muling.mall.pms.enums.BindEnum;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.form.GoodsFormDTO;
import com.muling.mall.pms.pojo.vo.GoodsPageVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SpuConverter {

    SpuConverter INSTANCE = Mappers.getMapper(SpuConverter.class);

    PmsSpu formToPo(GoodsFormDTO goodsFormDTO);

    void updatePo(GoodsFormDTO goodsFormDTO, @MappingTarget PmsSpu spu);

    @Mappings({
            @Mapping(source = "bind.value", target = "bind")
    })
    GoodsPageVO po2Vo(PmsSpu spu);

    List<GoodsPageVO> po2voList(List<PmsSpu> spuList);

    List<GoodsPageVO> spus2VoList(List<com.muling.mall.pms.es.entity.PmsSpu> spuList);

    GoodsPageVO spu2Vo(com.muling.mall.pms.es.entity.PmsSpu spu);

    @Mappings({
            @Mapping(source = "bind.value", target = "bind")
    })
    SpuInfoDTO po2dto(PmsSpu spu);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

//    default BindEnum fromBind(Integer bind) {
//        if (bind == null) {
//            return null;
//        }
//        return IBaseEnum.getEnumByValue(bind, BindEnum.class);
//    }
}

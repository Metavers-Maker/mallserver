package com.muling.mall.pms.converter;

import com.muling.mall.pms.pojo.entity.PmsBanner;
import com.muling.mall.pms.pojo.form.BannerForm;
import com.muling.mall.pms.pojo.vo.BannerVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BannerConverter {

    BannerConverter INSTANCE = Mappers.getMapper(BannerConverter.class);

    @Mappings({
            @Mapping(source = "linkType.value", target = "linkType")
    })
    BannerVO do2vo(PmsBanner banner);

    List<BannerVO> bannersToVOs(List<PmsBanner> banners);

    List<BannerVO> bannersToVoList(List<com.muling.mall.pms.es.entity.PmsBanner> banners);

    PmsBanner form2po(BannerForm bannerForm);

    void updatePo(BannerForm bannerForm, @MappingTarget PmsBanner banner);


}

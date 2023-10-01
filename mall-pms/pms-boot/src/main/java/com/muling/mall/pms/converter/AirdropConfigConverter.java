package com.muling.mall.pms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.pms.pojo.entity.PmsAirdropConfig;
import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.form.AirdropConfigForm;
import com.muling.mall.pms.pojo.vo.AirdropConfigVO;
import com.muling.mall.pms.pojo.vo.BrandVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AirdropConfigConverter {

    AirdropConfigConverter INSTANCE = Mappers.getMapper(AirdropConfigConverter.class);

    AirdropConfigVO do2vo(PmsAirdropConfig hot);

    List<AirdropConfigVO> hotsToVOs(List<PmsAirdropConfig> hots);

    PmsAirdropConfig form2Po(AirdropConfigForm airdropConfigForm);

    Page<AirdropConfigVO> entity2PageVO(Page<PmsAirdropConfig> brandPage);

}

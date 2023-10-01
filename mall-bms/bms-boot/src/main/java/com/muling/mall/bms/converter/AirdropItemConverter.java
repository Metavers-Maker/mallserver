package com.muling.mall.bms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.bms.pojo.entity.BmsAirdropItem;
import com.muling.mall.bms.pojo.form.AirdropItemForm;
import com.muling.mall.bms.pojo.vo.AirdropItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AirdropItemConverter {

    AirdropItemConverter INSTANCE = Mappers.getMapper(AirdropItemConverter.class);

    AirdropItemVO do2vo(BmsAirdropItem hot);

    List<AirdropItemVO> hotsToVOs(List<BmsAirdropItem> hots);

    BmsAirdropItem form2Po(AirdropItemForm airdropItemForm);

    Page<AirdropItemVO> entity2PageVO(Page<BmsAirdropItem> brandPage);
}

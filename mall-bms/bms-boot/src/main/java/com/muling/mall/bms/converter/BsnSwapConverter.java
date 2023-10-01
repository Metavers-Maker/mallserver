package com.muling.mall.bms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.bms.pojo.entity.BmsAirdropItem;
import com.muling.mall.bms.pojo.entity.BmsBsnSwap;
import com.muling.mall.bms.pojo.form.AirdropItemForm;
import com.muling.mall.bms.pojo.vo.AirdropItemVO;
import com.muling.mall.bms.pojo.vo.app.BsnSwapVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BsnSwapConverter {

    BsnSwapConverter INSTANCE = Mappers.getMapper(BsnSwapConverter.class);

    BsnSwapVO do2vo(BmsBsnSwap bsnSwap);

    Page<BsnSwapVO> po2PageVO(Page<BmsBsnSwap> page);
}

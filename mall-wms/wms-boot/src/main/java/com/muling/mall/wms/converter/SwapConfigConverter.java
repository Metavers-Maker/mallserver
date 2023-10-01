package com.muling.mall.wms.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.wms.pojo.entity.WmsSwapConfig;
import com.muling.mall.wms.pojo.form.admin.SwapConfigForm;
import com.muling.mall.wms.pojo.vo.SwapConfigVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SwapConfigConverter {

    SwapConfigConverter INSTANCE = Mappers.getMapper(SwapConfigConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status")
    })
    SwapConfigVO do2vo(WmsSwapConfig config);

    List<SwapConfigVO> po2voList(List<WmsSwapConfig> configs);

    Page<SwapConfigVO> entity2PageVO(Page<WmsSwapConfig> configsPage);


    WmsSwapConfig form2po(SwapConfigForm configForm);

    void updatePo(SwapConfigForm configForm, @MappingTarget WmsSwapConfig config);


}

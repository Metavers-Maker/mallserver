package com.muling.mall.farm.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.vo.app.FarmMemberItemVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmMemberItemConverter {

    FarmMemberItemConverter INSTANCE = Mappers.getMapper(FarmMemberItemConverter.class);

    @Mappings({
            @Mapping(source = "id", target = "itemId"),
            @Mapping(target = "id", ignore = true)
    })
    FarmMemberItem dto2po(MemberItemDTO memberItemDTO);

    Page<FarmMemberItemVO> entity2PageVO(Page<FarmMemberItem> farmMemberItems);

    @Mappings({
            @Mapping(source = "id", target = "itemId"),
            @Mapping(target = "id", ignore = true)
    })
    void updatePo(MemberItemDTO memberItemDTO, @MappingTarget FarmMemberItem farmMemberItem);


    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

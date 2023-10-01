package com.muling.mall.bms.converter;

import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.form.admin.ItemTransferAdminForm;
import com.muling.mall.bms.pojo.form.app.ItemMintForm;
import com.muling.mall.bms.pojo.vo.app.MemberItemVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberItemConverter {

    MemberItemConverter INSTANCE = Mappers.getMapper(MemberItemConverter.class);

    @Mappings({
            @Mapping(source = "status.value", target = "status"),
            @Mapping(source = "freeze.value", target = "freeze"),
            @Mapping(source = "freezeType.value", target = "freezeType"),

    })
    MemberItemVO po2vo(OmsMemberItem memberItems);

    MemberItemDTO po2dto(OmsMemberItem memberItems);

    List<MemberItemDTO> po2dtoList(List<OmsMemberItem> memberItemList);

    List<MemberItemVO> po2voList(List<OmsMemberItem> memberItems);

    List<MemberItemVO> items2voList(List<com.muling.mall.bms.es.entity.OmsMemberItem> memberItems);


    void updatePo(ItemMintForm mintForm, @MappingTarget OmsMemberItem item);

    void updatePo(ItemTransferAdminForm transferAdminForm, @MappingTarget OmsMemberItem item);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

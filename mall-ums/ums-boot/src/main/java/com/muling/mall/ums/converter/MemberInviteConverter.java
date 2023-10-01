package com.muling.mall.ums.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.pojo.form.MemberInviteForm;
import com.muling.mall.ums.pojo.vo.MemberInviteVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberInviteConverter {

    MemberInviteConverter INSTANCE = Mappers.getMapper(MemberInviteConverter.class);

    void updatePo(MemberInviteForm form, @MappingTarget UmsMemberInvite memberInvite);

    MemberInviteVO po2vo(UmsMemberInvite memberInvite);

    Page<MemberInviteVO> entity2PageVO(Page<UmsMemberInvite> entityPage);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

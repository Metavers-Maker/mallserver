package com.muling.admin.converter;

import com.muling.admin.pojo.entity.SysMenu;
import com.muling.admin.pojo.vo.menu.MenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 菜单对象转换器
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuConverter {

    MenuConverter INSTANCE = Mappers.getMapper(MenuConverter.class);

    MenuVO entity2VO(SysMenu entity);

}

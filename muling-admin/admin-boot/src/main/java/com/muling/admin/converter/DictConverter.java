package com.muling.admin.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.admin.pojo.entity.SysDict;
import com.muling.admin.pojo.form.DictForm;
import com.muling.admin.pojo.vo.dict.DictPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 字典类型对象转换器
 *
 * @author haoxr
 * @date 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface DictConverter {

    DictConverter INSTANCE = Mappers.getMapper(DictConverter.class);

    Page<DictPageVO> entity2Page(Page<SysDict> page);

    DictForm entity2Form(SysDict entity);

    SysDict form2Entity(DictForm entity);
}

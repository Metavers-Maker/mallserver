package com.muling.admin.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.admin.pojo.entity.SysDictItem;
import com.muling.admin.pojo.form.DictItemForm;
import com.muling.admin.pojo.vo.dict.DictItemPageVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * 字典数据项对象转换器
 *
 * @author haoxr
 * @date 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface DictItemConverter {

    DictItemConverter INSTANCE = Mappers.getMapper(DictItemConverter.class);

    Page<DictItemPageVO> entity2Page(Page<SysDictItem> page);

    DictItemForm entity2Form(SysDictItem entity);

    @InheritInverseConfiguration(name = "entity2Form")
    SysDictItem form2Entity(DictItemForm entity);

    void updatePo(DictItemForm form, @MappingTarget SysDictItem dictItem);

}

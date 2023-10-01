package com.muling.admin.converter;

import com.muling.admin.pojo.entity.SysPathRule;
import com.muling.admin.pojo.form.PathRuleForm;
import com.muling.admin.pojo.vo.permission.PathRuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * 部门对象转换器
 *
 * @author haoxr
 * @date 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface PathRuleConverter {

    PathRuleConverter INSTANCE = Mappers.getMapper(PathRuleConverter.class);

    PathRuleVO entity2DetailVO(SysPathRule entity);

    SysPathRule form2Entity(PathRuleForm pathRuleForm);

    void updatePo(PathRuleForm pathRuleForm, @MappingTarget SysPathRule entity);
}

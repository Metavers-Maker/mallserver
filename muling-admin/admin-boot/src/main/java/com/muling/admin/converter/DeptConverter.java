package com.muling.admin.converter;

import com.muling.admin.pojo.entity.SysDept;
import com.muling.admin.pojo.form.DeptForm;
import com.muling.admin.pojo.vo.dept.DeptDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 部门对象转换器
 *
 * @author haoxr
 * @date 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface DeptConverter {

    DeptConverter INSTANCE = Mappers.getMapper(DeptConverter.class);

    DeptDetailVO entity2DetailVO(SysDept entity);

    SysDept form2Entity(DeptForm deptForm);

}

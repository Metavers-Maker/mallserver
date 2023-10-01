package com.muling.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.pojo.entity.SysDept;
import com.muling.admin.pojo.form.DeptForm;
import com.muling.admin.pojo.vo.dept.DeptDetailVO;
import com.muling.admin.pojo.vo.dept.DeptVO;
import com.muling.common.web.domain.OptionVO;

import java.util.List;

/**
 * 菜单控制器
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2021-08-22
 */
public interface ISysDeptService extends IService<SysDept> {
    /**
     * 部门表格（Table）层级列表
     *
     * @param status 部门状态： 1-开启 0-禁用
     * @param name
     * @return
     */
    List<DeptVO> listTableDepartments(Integer status, String name);

    /**
     * 部门下拉选项
     *
     * @return
     */
    List<OptionVO> listDeptOptions();

    /**
     * 新增/修改部门
     *
     * @param deptForm
     * @return
     */
    Long saveDept(DeptForm deptForm);

    /**
     * 修改部门
     *
     * @param deptId
     * @param deptForm
     * @return
     */
    Long updateDept(Long deptId, DeptForm deptForm);

    /**
     * 删除部门
     *
     * @param ids 部门ID，多个以英文逗号,拼接字符串
     * @return
     */
    boolean deleteByIds(String ids);

    /**
     * 获取部门详情
     *
     * @param deptId
     * @return
     */
    DeptDetailVO getDeptDetail(Long deptId);
}

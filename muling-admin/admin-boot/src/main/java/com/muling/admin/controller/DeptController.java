package com.muling.admin.controller;

import com.muling.admin.pojo.form.DeptForm;
import com.muling.admin.pojo.vo.dept.DeptDetailVO;
import com.muling.admin.pojo.vo.dept.DeptVO;
import com.muling.admin.service.ISysDeptService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.Result;
import com.muling.common.web.domain.OptionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2020-11-06
 */
@Api(tags = "admin-部门")
@RestController
@RequestMapping("/api/v1/depts")
@RequiredArgsConstructor
public class DeptController {

    private final ISysDeptService deptService;

    @ApiOperation(value = "获取部门列表")
    @GetMapping("/table")
    public Result<List<DeptVO>> listDepartments(
            @ApiParam("部门状态") Integer status,
            @ApiParam("部门名称") String name
    ) {
        List<DeptVO> list = deptService.listTableDepartments(status, name);
        return Result.success(list);
    }

    @ApiOperation(value = "获取部门下拉选项")
    @GetMapping("/select")
    public Result getSelectList() {
        List<OptionVO> deptSelectList = deptService.listDeptOptions();
        return Result.success(deptSelectList);
    }

    @ApiOperation(value = "获取部门详情")
    @GetMapping("/{id}")
    public Result detail(
            @ApiParam("部门ID") @PathVariable Long id) {
        DeptDetailVO sysDept = deptService.getDeptDetail(id);
        return Result.success(sysDept);
    }

    @ApiOperation(value = "新增部门")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@RequestBody DeptForm deptForm) {
        Long id = deptService.saveDept(deptForm);
        return Result.success(id);
    }

    @ApiOperation(value = "修改部门")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result update(@PathVariable Long id, @RequestBody DeptForm deptForm) {
        Long deptId = deptService.updateDept(id, deptForm);
        return Result.success(deptId);
    }

    @ApiOperation(value = "删除部门")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(@ApiParam("部门ID") @PathVariable("ids") String ids) {
        boolean status = deptService.deleteByIds(ids);
        return Result.judge(status);
    }

}

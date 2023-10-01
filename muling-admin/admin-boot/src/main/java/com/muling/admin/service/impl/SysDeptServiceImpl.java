package com.muling.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.admin.constant.SystemConstants;
import com.muling.admin.converter.DeptConverter;
import com.muling.admin.mapper.SysDeptMapper;
import com.muling.admin.pojo.entity.SysDept;
import com.muling.admin.pojo.form.DeptForm;
import com.muling.admin.pojo.vo.dept.DeptDetailVO;
import com.muling.admin.pojo.vo.dept.DeptVO;
import com.muling.admin.service.ISysDeptService;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.web.domain.OptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 部门业务类
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2021-08-22
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    /**
     * 部门表格（Table）层级列表
     *
     * @param name 部门名称
     * @return
     */
    @Override
    public List<DeptVO> listTableDepartments(Integer status, String name) {
        List<SysDept> deptList = this.list(
                new LambdaQueryWrapper<SysDept>()
                        .like(StrUtil.isNotBlank(name), SysDept::getName, name)
                        .eq(Validator.isNotNull(status), SysDept::getStatus, status)
                        .orderByAsc(SysDept::getSort)
        );
        return recursion(deptList);
    }

    /**
     * 递归生成部门表格层级列表
     *
     * @param deptList 部门列表
     * @return 部门列表
     */
    private static List<DeptVO> recursion(List<SysDept> deptList) {
        List<DeptVO> deptTableList = new ArrayList<>();
        // 保存所有节点的 id
        Set<Long> nodeIdSet = deptList.stream()
                .map(SysDept::getId)
                .collect(Collectors.toSet());
        for (SysDept sysDept : deptList) {
            // 不在节点 id 集合中存在的 id 即为顶级节点 id, 递归生成列表
            Long parentId = sysDept.getParentId();
            if (!nodeIdSet.contains(parentId)) {
                deptTableList.addAll(recursionTableList(parentId, deptList));
                nodeIdSet.add(parentId);
            }
        }
        // 如果结果列表为空说明所有的节点都是独立分散的, 直接转换后返回
        if (deptTableList.isEmpty()) {
            return deptList.stream()
                    .map(item -> {
                        DeptVO deptVO = new DeptVO();
                        BeanUtil.copyProperties(item, deptVO);
                        return deptVO;
                    })
                    .collect(Collectors.toList());
        }
        return deptTableList;
    }

    /**
     * 递归生成部门表格层级列表
     *
     * @param parentId
     * @param deptList
     * @return
     */
    public static List<DeptVO> recursionTableList(Long parentId, List<SysDept> deptList) {
        List<DeptVO> deptTableList = new ArrayList<>();
        Optional.ofNullable(deptList).orElse(new ArrayList<>())
                .stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .forEach(dept -> {
                    DeptVO deptVO = new DeptVO();
                    BeanUtil.copyProperties(dept, deptVO);
                    List<DeptVO> children = recursionTableList(dept.getId(), deptList);
                    deptVO.setChildren(children);
                    deptTableList.add(deptVO);
                });
        return deptTableList;
    }


    /**
     * 部门下拉选项
     *
     * @return
     */
    @Override
    public List<OptionVO> listDeptOptions() {
        List<SysDept> deptList = this.list(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getStatus, GlobalConstants.STATUS_YES)
                .orderByAsc(SysDept::getSort)
        );
        List<OptionVO> deptSelectList = recurDeptTreeOptions(SystemConstants.ROOT_DEPT_ID, deptList);
        return deptSelectList;
    }


    /**
     * 递归生成部门表格层级列表
     *
     * @param parentId
     * @param deptList
     * @return
     */
    public static List<OptionVO> recurDeptTreeOptions(long parentId, List<SysDept> deptList) {
        List<OptionVO> deptTreeSelectList = new ArrayList<>();
        Optional.ofNullable(deptList).orElse(new ArrayList<>())
                .stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .forEach(dept -> {
                    OptionVO optionVO = new OptionVO(dept.getId(), dept.getName());
                    List<OptionVO> children = recurDeptTreeOptions(dept.getId(), deptList);
                    if (CollectionUtil.isNotEmpty(children)) {
                        optionVO.setChildren(children);
                    }
                    deptTreeSelectList.add(optionVO);
                });
        return deptTreeSelectList;
    }


    /**
     * 保存（新增/修改）部门
     *
     * @param deptForm
     * @return
     */
    @Override
    public Long saveDept(DeptForm deptForm) {
        SysDept entity = DeptConverter.INSTANCE.form2Entity(deptForm);
        // 部门路径
        String treePath = generateDeptTreePath(deptForm.getParentId());
        entity.setTreePath(treePath);
        // 保存部门并返回部门ID
        this.save(entity);
        return entity.getId();
    }

    @Override
    public Long updateDept(Long deptId, DeptForm deptForm) {
        // form->entity
        SysDept entity = DeptConverter.INSTANCE.form2Entity(deptForm);
        entity.setId(deptId);
        // 部门路径
        String treePath = generateDeptTreePath(deptForm.getParentId());
        entity.setTreePath(treePath);
        // 保存部门并返回部门ID
        this.updateById(entity);
        return entity.getId();
    }

    /**
     * 删除部门
     *
     * @param ids 部门ID，多个以英文逗号,拼接字符串
     * @return
     */
    @Override
    public boolean deleteByIds(String ids) {
        AtomicBoolean result = new AtomicBoolean(true);
        List<String> idList = Arrays.asList(ids.split(","));
        // 删除部门及子部门
        Optional.ofNullable(idList).orElse(new ArrayList<>()).forEach(id ->
                result.set(this.remove(new LambdaQueryWrapper<SysDept>()
                        .eq(SysDept::getId, id)
                        .or()
                        .apply("concat (',',tree_path,',') like concat('%,',{0},',%')", id)))
        );
        return result.get();
    }

    @Override
    public DeptDetailVO getDeptDetail(Long deptId) {
        SysDept entity = this.getOne(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getId, deptId)
                .select(
                        SysDept::getId,
                        SysDept::getName,
                        SysDept::getParentId,
                        SysDept::getStatus,
                        SysDept::getSort

                ));

        DeptDetailVO detailVO = DeptConverter.INSTANCE.entity2DetailVO(entity);
        return detailVO;
    }


    /**
     * 生成部门路径
     *
     * @param parentId
     * @return
     */
    private String generateDeptTreePath(Long parentId) {
        String treePath = null;
        if (SystemConstants.ROOT_DEPT_ID.equals(parentId)) {
            treePath = parentId + "";
        } else {
            SysDept parent = this.getById(parentId);
            if (parent != null) {
                treePath = parent.getTreePath() + "," + parent.getId();
            }
        }
        return treePath;
    }


}

package com.muling.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.pojo.entity.SysPathRule;
import com.muling.admin.pojo.form.PathRuleForm;

public interface ISysPathRuleService extends IService<SysPathRule> {

    /**
     * 新增
     *
     * @param pathRuleForm 表单
     * @return
     */
    boolean save(PathRuleForm pathRuleForm);


    /**
     * 修改
     *
     * @param id
     * @param pathRuleForm ¬表单
     * @return
     */
    boolean update(Long id, PathRuleForm pathRuleForm);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean delete(Long id);
}

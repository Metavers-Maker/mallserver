package com.muling.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.pojo.entity.SysDict;
import com.muling.admin.pojo.form.DictForm;


public interface ISysDictService extends IService<SysDict> {

    /**
     * 新增字典类型
     *
     * @param dictForm 字典表单
     * @return
     */
    boolean saveDict(DictForm dictForm);


    /**
     * 修改字典类型
     *
     * @param id
     * @param dictForm 字典表单
     * @return
     */
    boolean updateDict(Long id, DictForm dictForm);

    /**
     * 删除字典
     *
     * @param idsStr 字典ID，多个以英文逗号(,)分割
     * @return
     */
    boolean deleteDicts(String idsStr);

    /**
     * 修改显示状态
     *
     * @param id  ID
     * @param visible 是否显示(1->显示；2->隐藏)
     * @return
     */
    public boolean updateVisible(Long id, Integer visible);
}

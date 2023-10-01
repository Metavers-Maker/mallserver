package com.muling.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.pojo.entity.SysDictItem;
import com.muling.admin.pojo.form.DictItemForm;
import com.muling.common.web.domain.OptionVO;

import java.util.List;


public interface ISysDictItemService extends IService<SysDictItem> {

    IPage<SysDictItem> list(Page<SysDictItem> page, SysDictItem dict);


    /**
     * 新增字典数据项
     *
     * @param dictItemForm 字典数据项表单
     * @return
     */
    boolean saveDictItem(DictItemForm dictItemForm);

    /**
     * 修改字典数据项
     *
     * @param id           字典数据项ID
     * @param dictItemForm 字典数据项表单
     * @return
     */
    boolean updateDictItem(Long id, DictItemForm dictItemForm);

    /**
     * 删除字典数据项
     *
     * @param idsStr 字典数据项ID，多个以英文逗号(,)分割
     * @return
     */
    boolean deleteDictItems(String idsStr);

    /**
     * 根据字典类型编码获取字典数据项
     *
     * @param dictCode 字典类型编码
     * @return
     */
    List<OptionVO> listDictItemsByDictCode(String dictCode);

    /**
     * 修改显示状态
     *
     * @param id  ID
     * @param visible 是否显示(1->显示；2->隐藏)
     * @return
     */
    public boolean updateVisible(Long id, Integer visible);

}

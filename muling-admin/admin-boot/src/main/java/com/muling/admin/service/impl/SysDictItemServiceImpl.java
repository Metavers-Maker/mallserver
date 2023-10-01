package com.muling.admin.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.admin.converter.DictItemConverter;
import com.muling.admin.mapper.SysDictItemMapper;
import com.muling.admin.pojo.entity.SysDictItem;
import com.muling.admin.pojo.form.DictItemForm;
import com.muling.admin.service.ISysDictItemService;
import com.muling.common.web.domain.OptionVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Override
    public IPage<SysDictItem> list(Page<SysDictItem> page, SysDictItem dict) {
        List<SysDictItem> list = this.baseMapper.list(page, dict);
        page.setRecords(list);
        return page;
    }

    /**
     * 新增字典数据项
     *
     * @param dictItemForm 字典数据项表单
     * @return
     */
    @Override
    public boolean saveDictItem(DictItemForm dictItemForm) {
        // 实体对象转换 form->entity
        SysDictItem entity = DictItemConverter.INSTANCE.form2Entity(dictItemForm);
        // 持久化
        boolean result = this.save(entity);
        return result;
    }

    /**
     * 修改字典数据项
     *
     * @param id           字典数据项ID
     * @param dictItemForm 字典数据项表单
     * @return
     */
    @Override
    public boolean updateDictItem(Long id, DictItemForm dictItemForm) {
        // 获取字典类型
        SysDictItem dictItem = this.getById(id);
        Assert.isTrue(dictItem != null, "字典项不存在");

        DictItemConverter.INSTANCE.updatePo(dictItemForm, dictItem);
        return this.updateById(dictItem);
    }

    /**
     * 删除字典数据项
     *
     * @param idsStr 字典数据项ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    public boolean deleteDictItems(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除数据为空");
        //
        List<Long> ids = Arrays.asList(idsStr.split(","))
                .stream()
                .map(id -> Long.parseLong(id))
                .collect(Collectors.toList());

        // 删除字典数据项
        boolean result = this.removeByIds(ids);
        return result;
    }

    /**
     * 根据字典类型编码获取字典数据项
     *
     * @param dictCode 字典类型编码
     * @return
     */
    @Override
    public List<OptionVO> listDictItemsByDictCode(String dictCode) {

        // 数据字典项
        List<SysDictItem> dictItems = this.list(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictCode, dictCode)
                .select(SysDictItem::getValue, SysDictItem::getName)
        );

        // 转换下拉数据
        List<OptionVO> options = Optional.ofNullable(dictItems).orElse(new ArrayList<>()).stream()
                .map(dictItem -> new OptionVO(dictItem.getValue(), dictItem.getName()))
                .collect(Collectors.toList());

        return options;
    }

    @Override
    public boolean updateVisible(Long menuId, Integer visible) {
        boolean result = this.update(new LambdaUpdateWrapper<SysDictItem>()
                .eq(SysDictItem::getId, menuId)
                .set(SysDictItem::getStatus, visible)
        );
        return result;
    }

}

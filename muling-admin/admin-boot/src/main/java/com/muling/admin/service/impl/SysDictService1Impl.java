package com.muling.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.admin.converter.DictConverter;
import com.muling.admin.mapper.SysDictMapper;
import com.muling.admin.pojo.entity.SysDict;
import com.muling.admin.pojo.entity.SysDictItem;
import com.muling.admin.pojo.form.DictForm;
import com.muling.admin.service.ISysDictItemService;
import com.muling.admin.service.ISysDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDictService1Impl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

    private final ISysDictItemService sysDictItemService;

    @Override
    public boolean saveDict(DictForm dictForm) {
        // 实体对象转换 form->entity
        SysDict entity = DictConverter.INSTANCE.form2Entity(dictForm);
        // 持久化
        boolean result = this.save(entity);
        return result;
    }

    @Override
    public boolean updateDict(Long id, DictForm dictForm) {
        // 获取字典类型
        SysDict dict = this.getById(id);
        Assert.isTrue(dict != null, "字典类型不存在");

        SysDict entity = DictConverter.INSTANCE.form2Entity(dictForm);
        boolean result = this.updateById(entity);
        if (result) {
            // 字典类型code变化，同步修改字典项的类型code
            String oldCode = dict.getCode();
            String newCode = dictForm.getCode();
            if (!StrUtil.equals(oldCode, newCode)) {
                sysDictItemService.update(new LambdaUpdateWrapper<SysDictItem>()
                        .eq(SysDictItem::getDictCode, oldCode)
                        .set(SysDictItem::getDictCode, newCode)
                );
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDicts(String idsStr) {

        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除数据为空");
        //
        List<Long> ids = Arrays.asList(idsStr.split(","))
                .stream()
                .map(id -> Long.parseLong(id))
                .collect(Collectors.toList());

        // 删除字典项
        List<String> dictTypeCodes = this.list(new LambdaQueryWrapper<SysDict>()
                        .in(SysDict::getId, ids)
                        .select(SysDict::getCode))
                .stream().map(dictType -> dictType.getCode())
                .collect(Collectors.toList()
                );
        if (CollectionUtil.isNotEmpty(dictTypeCodes)) {
            sysDictItemService.remove(new LambdaQueryWrapper<SysDictItem>().in(SysDictItem::getDictCode, dictTypeCodes));
        }
        // 删除字典类型
        boolean result = this.removeByIds(ids);
        return result;
    }

    @Override
    public boolean updateVisible(Long menuId, Integer visible) {
        boolean result = this.update(new LambdaUpdateWrapper<SysDict>()
                .eq(SysDict::getId, menuId)
                .set(SysDict::getStatus, visible)
        );
        return result;
    }
}

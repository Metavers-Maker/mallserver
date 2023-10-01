package com.muling.global.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.global.converter.UpdateConfigConverter;
import com.muling.global.mapper.UpdateConfigMapper;
import com.muling.global.pojo.entity.UpdateConfig;
import com.muling.global.pojo.form.UpdateConfigForm;
import com.muling.global.pojo.vo.UpdateConfigVO;
import com.muling.global.service.IUpdateConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IUpdateConfigServiceImpl extends ServiceImpl<UpdateConfigMapper, UpdateConfig> implements IUpdateConfigService {

    private final UpdateConfigConverter configConverter;

    @Override
    public boolean add(UpdateConfigForm configForm) {
        UpdateConfig updateConfig = configConverter.form2po(configForm);
        return save(updateConfig);
    }

    @Override
    public boolean update(Long configId, UpdateConfigForm configForm) {
        UpdateConfig updateConfig = getById(configId);
        if (updateConfig == null) {
            throw new RuntimeException("全局配置不存在");
        }
        configConverter.updatePo(configForm, updateConfig);
        return updateById(updateConfig);
    }

    @Override
    public Map<String, UpdateConfigVO> map() {
        List<UpdateConfig> list = this.list();
        Map<String, UpdateConfigVO> map = Optional.ofNullable(list)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors
                        .toMap(UpdateConfig::getType, updateConfig -> configConverter.po2vo(updateConfig)));
        return map;
    }
}

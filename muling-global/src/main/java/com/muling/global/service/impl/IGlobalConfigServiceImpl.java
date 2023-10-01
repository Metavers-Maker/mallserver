package com.muling.global.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.global.converter.GlobalConfigConverter;
import com.muling.global.mapper.GlobalConfigMapper;
import com.muling.global.pojo.entity.GlobalConfig;
import com.muling.global.pojo.form.GlobalConfigForm;
import com.muling.global.pojo.vo.GlobalConfigVO;
import com.muling.global.service.IGlobalConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IGlobalConfigServiceImpl extends ServiceImpl<GlobalConfigMapper, GlobalConfig> implements IGlobalConfigService {

    private final GlobalConfigConverter configConverter;

    @Override
    public boolean add(GlobalConfigForm configForm) {
        GlobalConfig globalConfig = configConverter.form2po(configForm);
        return save(globalConfig);
    }

    @Override
    public boolean update(Long configId, GlobalConfigForm configForm) {
        GlobalConfig globalConfig = getById(configId);
        if (globalConfig == null) {
            throw new RuntimeException("全局配置不存在");
        }
        configConverter.updatePo(configForm, globalConfig);
        return updateById(globalConfig);
    }

    @Override
    public List<GlobalConfigVO> voList() {
        List<GlobalConfig> list = this.list();
        List<GlobalConfigVO> result = Optional.ofNullable(list)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(globalConfig -> configConverter.po2vo(globalConfig)).collect(Collectors.toList());

//        Map<Long, GlobalConfigVO> map = Optional.ofNullable(list)
//                .orElseGet(Collections::emptyList)
//                .stream()
//                .collect(Collectors
//                        .toMap(GlobalConfig::getType, globalConfig -> configConverter.po2vo(globalConfig)));
        return result;
    }
}

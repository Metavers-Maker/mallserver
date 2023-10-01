package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsMissionGroupConfig;
import com.muling.mall.bms.pojo.form.admin.MissionGroupConfigForm;
import com.muling.mall.bms.pojo.query.admin.MissionGroupConfigPageQuery;
import com.muling.mall.bms.pojo.vo.app.MissionGroupConfigVO;

import java.util.Collection;

public interface IMissionGroupConfigService extends IService<OmsMissionGroupConfig> {

    public IPage<MissionGroupConfigVO> page(MissionGroupConfigPageQuery queryParams);

    boolean add(MissionGroupConfigForm form);

    boolean update(Long id, MissionGroupConfigForm form);

    boolean update(Long id, Integer visible);

    public boolean delete(Collection<String> ids);
}

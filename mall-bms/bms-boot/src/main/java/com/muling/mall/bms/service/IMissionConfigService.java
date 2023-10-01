package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsMissionConfig;
import com.muling.mall.bms.pojo.form.admin.MissionConfigForm;
import com.muling.mall.bms.pojo.query.admin.MissionConfigPageQuery;
import com.muling.mall.bms.pojo.query.app.MissionItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MissionConfigVO;

import java.util.Collection;

public interface IMissionConfigService extends IService<OmsMissionConfig> {

    public IPage<MissionConfigVO> page(MissionConfigPageQuery queryParams);

    public IPage<MissionConfigVO> pageApp(MissionItemPageQuery queryParams);

    boolean add(MissionConfigForm form);

    boolean update(Long id, MissionConfigForm form);

    boolean update(Long id, Integer visible);

    public boolean delete(Collection<String> ids);

}

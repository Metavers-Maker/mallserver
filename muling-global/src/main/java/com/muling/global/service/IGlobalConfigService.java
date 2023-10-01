package com.muling.global.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.global.pojo.entity.GlobalConfig;
import com.muling.global.pojo.form.GlobalConfigForm;
import com.muling.global.pojo.vo.GlobalConfigVO;

import java.util.List;

public interface IGlobalConfigService extends IService<GlobalConfig> {


    boolean add(GlobalConfigForm configForm);

    boolean update(Long configId, GlobalConfigForm configForm);

    public List<GlobalConfigVO> voList();
}

package com.muling.global.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.global.pojo.entity.UpdateConfig;
import com.muling.global.pojo.form.UpdateConfigForm;
import com.muling.global.pojo.vo.UpdateConfigVO;

import java.util.Map;

public interface IUpdateConfigService extends IService<UpdateConfig> {


    boolean add(UpdateConfigForm configForm);

    boolean update(Long configId, UpdateConfigForm configForm);

    public Map<String, UpdateConfigVO> map();
}

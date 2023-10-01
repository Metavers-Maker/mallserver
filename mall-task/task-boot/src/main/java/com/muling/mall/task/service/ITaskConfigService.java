package com.muling.mall.task.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.enums.VisibleEnum;
import com.muling.mall.task.pojo.entity.TaskConfig;
import com.muling.mall.task.pojo.form.admin.TaskConfigForm;
import com.muling.mall.task.pojo.query.app.TaskConfigPageQuery;
import com.muling.mall.task.pojo.vo.app.TaskConfigVO;

import java.util.Collection;

public interface ITaskConfigService extends IService<TaskConfig> {

    public IPage<TaskConfigVO> page(TaskConfigPageQuery queryParams);

    boolean add(TaskConfigForm form);

    boolean update(Long id, TaskConfigForm form);

    boolean update(Long id, VisibleEnum visible);

    public boolean delete(Collection<String> ids);

}

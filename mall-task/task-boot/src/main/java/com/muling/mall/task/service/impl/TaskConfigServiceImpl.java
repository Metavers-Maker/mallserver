package com.muling.mall.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.task.converter.TaskConfigConverter;
import com.muling.mall.task.mapper.TaskConfigMapper;
import com.muling.mall.task.pojo.entity.TaskConfig;
import com.muling.mall.task.pojo.form.admin.TaskConfigForm;
import com.muling.mall.task.pojo.query.app.TaskConfigPageQuery;
import com.muling.mall.task.pojo.vo.app.TaskConfigVO;
import com.muling.mall.task.service.ITaskConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class TaskConfigServiceImpl extends ServiceImpl<TaskConfigMapper, TaskConfig> implements ITaskConfigService {


    @Override
    public IPage<TaskConfigVO> page(TaskConfigPageQuery queryParams) {
        LambdaQueryWrapper<TaskConfig> queryWrapper = new LambdaQueryWrapper<TaskConfig>()
                .eq(queryParams.getTaskType() != null, TaskConfig::getTaskType, queryParams.getTaskType())
                .eq(TaskConfig::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(TaskConfig::getUpdated);
        Page<TaskConfig> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<TaskConfigVO> result = TaskConfigConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(TaskConfigForm form) {
        TaskConfig config = TaskConfigConverter.INSTANCE.form2po(form);
        return save(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, TaskConfigForm form) {
        TaskConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        TaskConfigConverter.INSTANCE.updatePo(form, config);
        return updateById(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, VisibleEnum visible) {
        boolean status = update(new LambdaUpdateWrapper<TaskConfig>()
                .eq(TaskConfig::getId, id)
                .set(TaskConfig::getVisible, visible));
        return status;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Collection<String> ids) {
        return removeByIds(ids);
    }

}

package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.bms.converter.MissionGroupConfigConverter;
import com.muling.mall.bms.mapper.MissionGroupConfigMapper;
import com.muling.mall.bms.pojo.entity.OmsMissionGroupConfig;
import com.muling.mall.bms.pojo.form.admin.MissionGroupConfigForm;
import com.muling.mall.bms.pojo.query.admin.MissionGroupConfigPageQuery;
import com.muling.mall.bms.pojo.vo.app.MissionGroupConfigVO;
import com.muling.mall.bms.service.IMissionGroupConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class MissionGroupConfigServiceImpl extends ServiceImpl<MissionGroupConfigMapper, OmsMissionGroupConfig> implements IMissionGroupConfigService {

    @Override
    public IPage<MissionGroupConfigVO> page(MissionGroupConfigPageQuery queryParams) {
        LambdaQueryWrapper<OmsMissionGroupConfig> queryWrapper = new LambdaQueryWrapper<OmsMissionGroupConfig>()
                .eq(OmsMissionGroupConfig::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(OmsMissionGroupConfig::getUpdated);
        Page<OmsMissionGroupConfig> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<MissionGroupConfigVO> result = MissionGroupConfigConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(MissionGroupConfigForm form) {
        OmsMissionGroupConfig config = MissionGroupConfigConverter.INSTANCE.form2po(form);
        return save(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, MissionGroupConfigForm form) {
        OmsMissionGroupConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        MissionGroupConfigConverter.INSTANCE.updatePo(form, config);
        return updateById(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, Integer visible) {
        boolean status = update(new LambdaUpdateWrapper<OmsMissionGroupConfig>()
                .eq(OmsMissionGroupConfig::getId, id)
                .set(OmsMissionGroupConfig::getVisible, visible));
        return status;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Collection<String> ids) {
        return removeByIds(ids);
    }

}

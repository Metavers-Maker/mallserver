package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.bms.converter.MissionConfigConverter;
import com.muling.mall.bms.mapper.MissionConfigMapper;
import com.muling.mall.bms.pojo.entity.OmsMissionConfig;
import com.muling.mall.bms.pojo.form.admin.MissionConfigForm;
import com.muling.mall.bms.pojo.query.admin.MissionConfigPageQuery;
import com.muling.mall.bms.pojo.query.app.MissionItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MissionConfigVO;
import com.muling.mall.bms.service.IMissionConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class MissionConfigServiceImpl extends ServiceImpl<MissionConfigMapper, OmsMissionConfig> implements IMissionConfigService {

    @Override
    public IPage<MissionConfigVO> page(MissionConfigPageQuery queryParams) {
        LambdaQueryWrapper<OmsMissionConfig> queryWrapper = new LambdaQueryWrapper<OmsMissionConfig>()
                .orderByDesc(OmsMissionConfig::getUpdated);
        Page<OmsMissionConfig> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<MissionConfigVO> result = MissionConfigConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Override
    public IPage<MissionConfigVO> pageApp(MissionItemPageQuery queryParams) {
        LambdaQueryWrapper<OmsMissionConfig> queryWrapper = new LambdaQueryWrapper<OmsMissionConfig>()
                .eq(OmsMissionConfig::getVisible, 1)
                .orderByDesc(OmsMissionConfig::getUpdated);
        Page<OmsMissionConfig> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<MissionConfigVO> result = MissionConfigConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(MissionConfigForm form) {
        OmsMissionConfig config = MissionConfigConverter.INSTANCE.form2po(form);
        return save(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, MissionConfigForm form) {
        OmsMissionConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        MissionConfigConverter.INSTANCE.updatePo(form, config);
        return updateById(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, Integer visible) {
        boolean status = update(new LambdaUpdateWrapper<OmsMissionConfig>()
                .eq(OmsMissionConfig::getId, id)
                .set(OmsMissionConfig::getVisible, visible));
        return status;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Collection<String> ids) {
        return removeByIds(ids);
    }

}

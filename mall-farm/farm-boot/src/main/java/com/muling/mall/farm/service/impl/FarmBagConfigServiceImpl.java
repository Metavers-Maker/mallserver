package com.muling.mall.farm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.farm.converter.FarmBagConfigConverter;
import com.muling.mall.farm.mapper.FarmBagConfigMapper;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import com.muling.mall.farm.pojo.form.admin.FarmBagConfigForm;
import com.muling.mall.farm.pojo.query.app.FarmBagConfigPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmBagConfigVO;
import com.muling.mall.farm.service.IFarmBagConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class FarmBagConfigServiceImpl extends ServiceImpl<FarmBagConfigMapper, FarmBagConfig> implements IFarmBagConfigService {


    @Override
    public IPage<FarmBagConfigVO> page(FarmBagConfigPageQuery queryParams) {
        LambdaQueryWrapper<FarmBagConfig> queryWrapper = new LambdaQueryWrapper<FarmBagConfig>()
                .eq(FarmBagConfig::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(FarmBagConfig::getUpdated);
        Page<FarmBagConfig> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<FarmBagConfigVO> result = FarmBagConfigConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(FarmBagConfigForm form) {
        FarmBagConfig config = FarmBagConfigConverter.INSTANCE.form2po(form);
        return save(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, FarmBagConfigForm form) {
        FarmBagConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        FarmBagConfigConverter.INSTANCE.updatePo(form, config);
        return updateById(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, VisibleEnum visible) {
        boolean status = update(new LambdaUpdateWrapper<FarmBagConfig>()
                .eq(FarmBagConfig::getId, id)
                .set(FarmBagConfig::getVisible, visible));
        return status;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Collection<String> ids) {
        return removeByIds(ids);
    }

    @Override
    public FarmBagConfig getBySpuId(Long spuId) {
        return baseMapper.getBySpuId(spuId);
    }

}

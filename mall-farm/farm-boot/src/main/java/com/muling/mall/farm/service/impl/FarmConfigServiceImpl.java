package com.muling.mall.farm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.farm.converter.FarmConfigConverter;
import com.muling.mall.farm.mapper.FarmConfigMapper;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.form.admin.FarmConfigForm;
import com.muling.mall.farm.pojo.query.app.FarmConfigPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmConfigVO;
import com.muling.mall.farm.service.IFarmConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class FarmConfigServiceImpl extends ServiceImpl<FarmConfigMapper, FarmConfig> implements IFarmConfigService {


    @Override
    public IPage<FarmConfigVO> page(FarmConfigPageQuery queryParams) {
        LambdaQueryWrapper<FarmConfig> queryWrapper = new LambdaQueryWrapper<FarmConfig>()
                .eq(FarmConfig::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(FarmConfig::getUpdated);
        Page<FarmConfig> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<FarmConfigVO> result = FarmConfigConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(FarmConfigForm form) {
        FarmConfig config = FarmConfigConverter.INSTANCE.form2po(form);
        return save(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, FarmConfigForm form) {
        FarmConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        FarmConfigConverter.INSTANCE.updatePo(form, config);
        return updateById(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, VisibleEnum visible) {
        boolean status = update(new LambdaUpdateWrapper<FarmConfig>()
                .eq(FarmConfig::getId, id)
                .set(FarmConfig::getVisible, visible));
        return status;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Collection<String> ids) {
        return removeByIds(ids);
    }

}

package com.muling.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.enums.StatusEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.wms.converter.SwapConfigConverter;
import com.muling.mall.wms.mapper.WmsSwapConfigMapper;
import com.muling.mall.wms.pojo.entity.WmsSwapConfig;
import com.muling.mall.wms.pojo.form.admin.SwapConfigForm;
import com.muling.mall.wms.pojo.query.app.SwapConfigPageQuery;
import com.muling.mall.wms.pojo.vo.SwapConfigVO;
import com.muling.mall.wms.service.IWmsSwapConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class WmsSwapConfigServiceImpl extends ServiceImpl<WmsSwapConfigMapper, WmsSwapConfig> implements IWmsSwapConfigService {


    @Override
    public IPage<SwapConfigVO> page(SwapConfigPageQuery queryParams) {
        LambdaQueryWrapper<WmsSwapConfig> queryWrapper = new LambdaQueryWrapper<WmsSwapConfig>()
                .eq(WmsSwapConfig::getStatus, StatusEnum.ENABLE)
                .orderByDesc(WmsSwapConfig::getUpdated);
        Page<WmsSwapConfig> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<SwapConfigVO> result = SwapConfigConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(SwapConfigForm form) {
        WmsSwapConfig config = SwapConfigConverter.INSTANCE.form2po(form);
        return save(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, SwapConfigForm form) {
        WmsSwapConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        SwapConfigConverter.INSTANCE.updatePo(form, config);
        return updateById(config);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Long id, StatusEnum status) {
        boolean result = update(new LambdaUpdateWrapper<WmsSwapConfig>()
                .eq(WmsSwapConfig::getId, id)
                .set(WmsSwapConfig::getStatus, status));
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Collection<String> ids) {
        return removeByIds(ids);
    }

    @Override
    public WmsSwapConfig getById(Long id) {
        return this.baseMapper.selectById(id);
    }


}

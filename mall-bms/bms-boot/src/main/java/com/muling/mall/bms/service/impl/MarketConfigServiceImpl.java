package com.muling.mall.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.bms.converter.MarketConfigConverter;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.mapper.MarketConfigMapper;
import com.muling.mall.bms.pojo.entity.OmsMarketConfig;
import com.muling.mall.bms.pojo.form.admin.MarketConfigForm;
import com.muling.mall.bms.pojo.query.app.MarketConfigPageQueryApp;
import com.muling.mall.bms.pojo.vo.app.MarketConfigVO;
import com.muling.mall.bms.service.IMarketConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MarketConfigServiceImpl extends ServiceImpl<MarketConfigMapper, OmsMarketConfig> implements IMarketConfigService {

    @Override
    public IPage<MarketConfigVO> page(MarketConfigPageQueryApp queryParams) {
        LambdaQueryWrapper<OmsMarketConfig> queryWrapper = new LambdaQueryWrapper<OmsMarketConfig>()
                .eq(queryParams.getSpuId() != null, OmsMarketConfig::getSpuId, queryParams.getSpuId());
        queryWrapper.orderByDesc(OmsMarketConfig::getUpdated);
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        List<MarketConfigVO> list = MarketConfigConverter.INSTANCE.po2voList(page.getRecords());
        return page.setRecords(list);
    }

    @Override
    public boolean isOpen(Long spuId) {
        List<OmsMarketConfig> marketConfigs = this.list(new LambdaQueryWrapper<OmsMarketConfig>()
                .eq(OmsMarketConfig::getSpuId, spuId)
                .eq(OmsMarketConfig::getStatus, StatusEnum.ENABLE));
        if (marketConfigs.size() == 0) {
            throw new BizException("寄售未开通");
        }
        return true;
    }


    @Override
    public boolean save(MarketConfigForm configForm) {
        Assert.isTrue(configForm.getSpuId() != null, "必须关联Spu");
        OmsMarketConfig config = MarketConfigConverter.INSTANCE.form2po(configForm);
        OmsMarketConfig target = this.getOne(new LambdaQueryWrapper<OmsMarketConfig>().eq(OmsMarketConfig::getSpuId, configForm.getSpuId()));
        Assert.isTrue(target == null, "该Spu已经存在，不允许重复创建");
        config.setStatus(StatusEnum.DISABLED);
        boolean b = this.save(config);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, MarketConfigForm configForm) {
        OmsMarketConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        MarketConfigConverter.INSTANCE.updatePo(configForm, config);
        LambdaUpdateWrapper<OmsMarketConfig> lambdaUpdateWrapper = new LambdaUpdateWrapper<OmsMarketConfig>();
        lambdaUpdateWrapper.eq(OmsMarketConfig::getId, id);
        lambdaUpdateWrapper.set(OmsMarketConfig::getName, configForm.getName());
        lambdaUpdateWrapper.set(OmsMarketConfig::getCoinType, configForm.getCoinType());
        lambdaUpdateWrapper.set(OmsMarketConfig::getFee, configForm.getFee());
        return this.update(lambdaUpdateWrapper);
    }
}

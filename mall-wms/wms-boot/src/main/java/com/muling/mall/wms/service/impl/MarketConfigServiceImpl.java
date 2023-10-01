package com.muling.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.wms.converter.MarketConfigConverter;
import com.muling.mall.wms.mapper.WmsMarketConfigMapper;
import com.muling.mall.wms.pojo.entity.WmsMarketConfig;
import com.muling.mall.wms.pojo.form.admin.MarketConfigForm;
import com.muling.mall.wms.pojo.query.app.MarketConfigPageQueryApp;
import com.muling.mall.wms.pojo.vo.MarketConfigVO;
import com.muling.mall.wms.service.IMarketConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MarketConfigServiceImpl extends ServiceImpl<WmsMarketConfigMapper, WmsMarketConfig> implements IMarketConfigService {

    @Override
    public IPage<MarketConfigVO> page(MarketConfigPageQueryApp queryParams) {
        LambdaQueryWrapper<WmsMarketConfig> queryWrapper = new LambdaQueryWrapper<WmsMarketConfig>();

        queryWrapper.orderByDesc(WmsMarketConfig::getUpdated);

        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);

        List<MarketConfigVO> list = MarketConfigConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public boolean save(MarketConfigForm configForm) {
        WmsMarketConfig config = MarketConfigConverter.INSTANCE.form2po(configForm);
        boolean b = this.save(config);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public WmsMarketConfig getByCoinType(Integer coinType,Integer opType) {
        return this.baseMapper.selectOne(Wrappers.<WmsMarketConfig>lambdaQuery()
                .eq(WmsMarketConfig::getCoinType, coinType)
                .eq(WmsMarketConfig::getOpType, opType));
    }

    @Override
    public boolean updateById(Long id, MarketConfigForm configForm) {
        WmsMarketConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        MarketConfigConverter.INSTANCE.updatePo(configForm, config);

        return updateById(config);
    }
}

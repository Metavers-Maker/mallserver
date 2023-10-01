package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.bms.converter.FarmPoolConverter;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.mapper.FarmPoolMapper;
import com.muling.mall.bms.pojo.entity.OmsFarmPool;
import com.muling.mall.bms.pojo.form.admin.StakeConfigForm;
import com.muling.mall.bms.pojo.query.admin.StakePageQuery;
import com.muling.mall.bms.pojo.vo.app.FarmPoolVO;
import com.muling.mall.bms.service.IFarmPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class FarmPoolServiceImpl extends ServiceImpl<FarmPoolMapper, OmsFarmPool> implements IFarmPoolService {

    @Override
    public IPage<FarmPoolVO> page(StakePageQuery queryParams) {
        LambdaQueryWrapper<OmsFarmPool> wrapper = Wrappers.<OmsFarmPool>lambdaQuery()
                .eq(queryParams.getSpuId() != null, OmsFarmPool::getSpuId, queryParams.getSpuId())
                .orderByDesc(OmsFarmPool::getUpdated);
        ;
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<FarmPoolVO> list = FarmPoolConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public boolean save(StakeConfigForm configForm) {
        OmsFarmPool config = FarmPoolConverter.INSTANCE.form2po(configForm);
        boolean b = this.save(config);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, StakeConfigForm configForm) {
        OmsFarmPool config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        FarmPoolConverter.INSTANCE.updatePo(configForm, config);

        return updateById(config);
    }

    @Override
    public List<OmsFarmPool> list(StatusEnum status) {
        LambdaQueryWrapper<OmsFarmPool> wrapper = Wrappers.<OmsFarmPool>lambdaQuery()
                .eq(OmsFarmPool::getStatus, status);

        return list(wrapper);
    }
}

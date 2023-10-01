package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.bms.converter.CompoundConfigConverter;
import com.muling.mall.bms.mapper.CompoundConfigMapper;
import com.muling.mall.bms.pojo.entity.OmsCompoundConfig;
import com.muling.mall.bms.pojo.form.admin.CompoundConfigForm;
import com.muling.mall.bms.pojo.query.admin.CompoundPageQuery;
import com.muling.mall.bms.pojo.vo.app.CompoundVO;
import com.muling.mall.bms.service.ICompoundConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CompoundConfigServiceImpl extends ServiceImpl<CompoundConfigMapper, OmsCompoundConfig> implements ICompoundConfigService {


    @Override
    public IPage<CompoundVO> page(CompoundPageQuery queryParams) {
        LambdaQueryWrapper<OmsCompoundConfig> wrapper = Wrappers.<OmsCompoundConfig>lambdaQuery()
                .orderByDesc(OmsCompoundConfig::getUpdated);
        ;
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<CompoundVO> list = CompoundConfigConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public boolean save(CompoundConfigForm compoundConfigForm) {
        OmsCompoundConfig config = CompoundConfigConverter.INSTANCE.form2po(compoundConfigForm);
        boolean b = this.save(config);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, CompoundConfigForm compoundConfigForm) {
        OmsCompoundConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        CompoundConfigConverter.INSTANCE.updatePo(compoundConfigForm, config);

        return updateById(config);
    }


}

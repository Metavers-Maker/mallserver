package com.muling.mall.pms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.GroundConverter;
import com.muling.mall.pms.mapper.PmsGroundMapper;
import com.muling.mall.pms.pojo.entity.PmsGround;
import com.muling.mall.pms.pojo.form.GroundForm;
import com.muling.mall.pms.pojo.query.GroundPageQuery;
import com.muling.mall.pms.pojo.vo.GroundVO;
import com.muling.mall.pms.service.IPmsGroundService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PmsGroundServiceImpl extends ServiceImpl<PmsGroundMapper, PmsGround> implements IPmsGroundService {

    @Override
    public IPage<GroundVO> page(GroundPageQuery queryParams) {
        LambdaQueryWrapper<PmsGround> wrapper = Wrappers.<PmsGround>lambdaQuery()
                .eq(queryParams.getType() != null, PmsGround::getType, queryParams.getType())
                .eq(PmsGround::getVisible, ViewTypeEnum.VISIBLE)
                .orderByDesc(PmsGround::getSort)
                .orderByDesc(PmsGround::getUpdated);
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<GroundVO> list = GroundConverter.INSTANCE.po2Vo(page.getRecords());
        return page.setRecords(list);
    }

    @Override
    public boolean save(GroundForm hotForm) {
        PmsGround hot = GroundConverter.INSTANCE.form2Po(hotForm);
        boolean b = this.save(hot);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, GroundForm hotForm) {
        PmsGround hot = getById(id);
        if (hot == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        BeanUtil.copyProperties(hotForm, hot);

        return updateById(hot);
    }
}

package com.muling.mall.otc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.otc.converter.PayInfoConverter;
import com.muling.mall.otc.mapper.PayInfoMapper;
import com.muling.mall.otc.pojo.entity.OtcPayInfo;
import com.muling.mall.otc.pojo.form.PayInfoForm;
import com.muling.mall.otc.pojo.query.app.PayInfoPageQuery;
import com.muling.mall.otc.pojo.vo.PayInfoVO;
import com.muling.mall.otc.service.IPayInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class PayInfoServiceImpl extends ServiceImpl<PayInfoMapper, OtcPayInfo> implements IPayInfoService {

    @Override
    public IPage<PayInfoVO> page(PayInfoPageQuery queryParams) {
        LambdaQueryWrapper<OtcPayInfo> wrapper = Wrappers.<OtcPayInfo>lambdaQuery()
                .orderByDesc(OtcPayInfo::getUpdated);
        ;
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<PayInfoVO> list = PayInfoConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public boolean save(PayInfoForm configForm) {
        Long memberId = MemberUtils.getMemberId();
        OtcPayInfo payInfo = PayInfoConverter.INSTANCE.form2po(configForm);
        payInfo.setMemberId(memberId);
        boolean b = this.save(payInfo);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, PayInfoForm configForm) {
        Long memberId = MemberUtils.getMemberId();
        OtcPayInfo payInfo = getById(id);
        if (payInfo == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        Assert.isTrue(payInfo.getMemberId().longValue() == memberId.longValue(), "用户不匹配");
        PayInfoConverter.INSTANCE.updatePo(configForm, payInfo);

        return updateById(payInfo);
    }

}

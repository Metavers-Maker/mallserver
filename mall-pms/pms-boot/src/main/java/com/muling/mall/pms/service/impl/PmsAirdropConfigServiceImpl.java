package com.muling.mall.pms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.constant.RedisConstants;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.converter.AirdropConfigConverter;
import com.muling.mall.pms.converter.BrandConverter;
import com.muling.mall.pms.converter.SkuConverter;
import com.muling.mall.pms.mapper.PmsAirdropConfigMapper;
import com.muling.mall.pms.mapper.PmsBrandMapper;
import com.muling.mall.pms.pojo.entity.PmsAirdropConfig;
import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.entity.PmsSku;
import com.muling.mall.pms.pojo.form.AirdropConfigForm;
import com.muling.mall.pms.pojo.form.BrandForm;
import com.muling.mall.pms.pojo.form.SkuForm;
import com.muling.mall.pms.pojo.query.AirdropConfigPageQuery;
import com.muling.mall.pms.pojo.query.BrandPageQuery;
import com.muling.mall.pms.pojo.vo.AirdropConfigVO;
import com.muling.mall.pms.pojo.vo.BrandVO;
import com.muling.mall.pms.service.IPmsAirdropConfigService;
import com.muling.mall.pms.service.IPmsBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PmsAirdropConfigServiceImpl extends ServiceImpl<PmsAirdropConfigMapper, PmsAirdropConfig> implements IPmsAirdropConfigService {

    private final BusinessNoGenerator businessNoGenerator;

    @Override
    public IPage<AirdropConfigVO> page(AirdropConfigPageQuery queryParams) {
        LambdaQueryWrapper<PmsAirdropConfig> queryWrapper = new LambdaQueryWrapper<PmsAirdropConfig>()
                .orderByDesc(PmsAirdropConfig::getSort)
                .orderByDesc(PmsAirdropConfig::getUpdated);
        Page<PmsAirdropConfig> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<AirdropConfigVO> result = AirdropConfigConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(AirdropConfigForm airdropConfigForm) {
        PmsAirdropConfig airdropConfig = AirdropConfigConverter.INSTANCE.form2Po(airdropConfigForm);
        Long airdropId = businessNoGenerator.generateLong(BusinessTypeEnum.AIRDROP);
        airdropConfig.setId(airdropId);
        boolean b = this.save(airdropConfig);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, AirdropConfigForm airdropConfigForm) {
        PmsAirdropConfig airdropConfig = getById(id);
        if (airdropConfig == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        BeanUtil.copyProperties(airdropConfigForm, airdropConfig);
        return updateById(airdropConfig);
    }

}

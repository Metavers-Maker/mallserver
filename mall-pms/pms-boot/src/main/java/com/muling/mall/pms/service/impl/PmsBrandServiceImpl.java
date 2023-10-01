package com.muling.mall.pms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.converter.BrandConverter;
import com.muling.mall.pms.mapper.PmsBrandMapper;
import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.form.BrandForm;
import com.muling.mall.pms.pojo.query.BrandPageQuery;
import com.muling.mall.pms.pojo.vo.BrandVO;
import com.muling.mall.pms.service.IPmsBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PmsBrandServiceImpl extends ServiceImpl<PmsBrandMapper, PmsBrand> implements IPmsBrandService {

    private final BusinessNoGenerator businessNoGenerator;

    @Override
    public List<BrandVO> getAppBrandDetails(List<Long> brandIds) {
        List<PmsBrand> brands = this.listByIds(brandIds);
        List<BrandVO> result = BrandConverter.INSTANCE.brands2vo(brands);
        return result;
    }

    @Override
    public PmsBrand getBrandDetails(Long brandId) {
        PmsBrand pmsBrand = this.baseMapper.selectById(brandId);
        return pmsBrand;
    }

    @Override
    public IPage<BrandVO> page(BrandPageQuery queryParams) {
        LambdaQueryWrapper<PmsBrand> queryWrapper = new LambdaQueryWrapper<PmsBrand>()
                .orderByDesc(PmsBrand::getSort)
                .orderByDesc(PmsBrand::getUpdated);
        Page<PmsBrand> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        Page<BrandVO> result = BrandConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Override
    public boolean save(BrandForm brandForm) {
        PmsBrand brand = BrandConverter.INSTANCE.form2Po(brandForm);

        Long id = businessNoGenerator.generateLong(BusinessTypeEnum.BRAND);
        brand.setId(id);
        boolean b = this.save(brand);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, BrandForm brandForm) {
        PmsBrand brand = getById(id);
        if (brand == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        BeanUtil.copyProperties(brandForm, brand);

        return updateById(brand);
    }
}

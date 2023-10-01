package com.muling.mall.pms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.common.enums.LinkTypeEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.BannerConverter;
import com.muling.mall.pms.mapper.PmsBannerMapper;
import com.muling.mall.pms.pojo.entity.PmsBanner;
import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.form.BannerForm;
import com.muling.mall.pms.pojo.vo.BannerVO;
import com.muling.mall.pms.service.IPmsBannerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PmsBannerServiceImpl extends ServiceImpl<PmsBannerMapper, PmsBanner> implements IPmsBannerService {

    public List<BannerVO> getBannerVisibleLists(Integer linktype) {
        LambdaQueryWrapper<PmsBanner> queryWrapper = new LambdaQueryWrapper<PmsBanner>()
                .eq(PmsBanner::getVisible, ViewTypeEnum.VISIBLE)
                .eq(linktype!=null, PmsBanner::getLinkType, linktype)
                .orderByDesc(PmsBanner::getSort)
                .orderByDesc(PmsBanner::getUpdated);
        List<PmsBanner> list = this.list(queryWrapper);
        List<BannerVO> result = BannerConverter.INSTANCE.bannersToVOs(list);
        return result;
    }

    @Override
    public boolean save(BannerForm bannerForm) {
        PmsBanner banner = BannerConverter.INSTANCE.form2po(bannerForm);
        boolean b = this.save(banner);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, BannerForm bannerForm) {
        PmsBanner banner = getById(id);
        if (banner == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        BannerConverter.INSTANCE.updatePo(bannerForm, banner);

        return updateById(banner);
    }

}

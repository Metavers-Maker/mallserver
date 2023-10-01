package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.pms.pojo.entity.PmsBanner;
import com.muling.mall.pms.pojo.form.BannerForm;
import com.muling.mall.pms.pojo.vo.BannerVO;

import java.util.List;

public interface IPmsBannerService extends IService<PmsBanner> {

    public List<BannerVO> getBannerVisibleLists(Integer linktype);

    public boolean save(BannerForm bannerForm);

    public boolean updateById(Long id, BannerForm bannerForm);
}

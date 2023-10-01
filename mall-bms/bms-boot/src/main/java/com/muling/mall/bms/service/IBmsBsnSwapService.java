package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.enums.ChainSwapTypeEnum;
import com.muling.mall.bms.pojo.entity.BmsBsnSwap;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.query.admin.BsnSwapPageQuery;
import com.muling.mall.bms.pojo.vo.app.BsnSwapVO;

import java.util.List;

public interface IBmsBsnSwapService extends IService<BmsBsnSwap> {

    public IPage<BsnSwapVO> page(BsnSwapPageQuery queryParams);

    public boolean save(Long fromMemberId, Long toMemberId, OmsMemberItem memberItem, ChainSwapTypeEnum swapTypeEnum);

    public boolean saveBatch(Long fromMemberId, Long toMemberId, List<OmsMemberItem> memberItems, ChainSwapTypeEnum swapTypeEnum);

}

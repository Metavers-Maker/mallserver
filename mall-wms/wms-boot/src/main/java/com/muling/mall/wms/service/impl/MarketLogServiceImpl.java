package com.muling.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.base.BasePageQuery;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.wms.converter.TransferLogConverter;
import com.muling.mall.wms.mapper.WmsMarketLogMapper;
import com.muling.mall.wms.pojo.entity.WmsMarketLog;
import com.muling.mall.wms.pojo.entity.WmsWalletLog;
import com.muling.mall.wms.pojo.vo.WmsMarketLogVO;
import com.muling.mall.wms.service.IMarketLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketLogServiceImpl extends ServiceImpl<WmsMarketLogMapper, WmsMarketLog> implements IMarketLogService {

    @Override
    public IPage<WmsMarketLogVO> page(BasePageQuery queryParams) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<WmsMarketLog> wrapper = Wrappers.<WmsMarketLog>lambdaQuery()
                .eq(WmsMarketLog::getMemberId, memberId)
                .orderByDesc(WmsMarketLog::getCreated);

        IPage page = page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<WmsMarketLogVO> list = TransferLogConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }


}

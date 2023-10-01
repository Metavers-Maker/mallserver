package com.muling.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.base.BasePageQuery;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.wms.converter.TransferConfigConverter;
import com.muling.mall.wms.converter.TransferLogConverter;
import com.muling.mall.wms.mapper.WmsTransferLogMapper;
import com.muling.mall.wms.pojo.entity.WmsTransferConfig;
import com.muling.mall.wms.pojo.entity.WmsTransferLog;
import com.muling.mall.wms.pojo.vo.TransferLogVO;
import com.muling.mall.wms.pojo.vo.TransferVO;
import com.muling.mall.wms.service.IWmsTransferLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WmsTransferLogServiceImpl extends ServiceImpl<WmsTransferLogMapper, WmsTransferLog> implements IWmsTransferLogService {


    @Override
    public IPage<TransferLogVO> page(BasePageQuery queryParams) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<WmsTransferLog> wrapper = Wrappers.<WmsTransferLog>lambdaQuery()
                .eq(WmsTransferLog::getSourceId, memberId)
                .orderByDesc(WmsTransferLog::getCreated);
        ;
        IPage page = page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<TransferLogVO> list = TransferLogConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }


}

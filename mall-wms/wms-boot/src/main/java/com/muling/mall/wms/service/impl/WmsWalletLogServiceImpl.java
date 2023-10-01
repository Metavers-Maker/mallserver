package com.muling.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.wms.converter.WalletLogConverter;
import com.muling.mall.wms.mapper.WmsWalletLogMapper;
import com.muling.mall.wms.pojo.entity.WmsWalletLog;
import com.muling.mall.wms.pojo.query.app.WalletLogPageQuery;
import com.muling.mall.wms.pojo.vo.WalletLogVO;
import com.muling.mall.wms.service.IWmsWalletLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WmsWalletLogServiceImpl extends ServiceImpl<WmsWalletLogMapper, WmsWalletLog> implements IWmsWalletLogService {

    public IPage<WalletLogVO> page(WalletLogPageQuery queryParams) {

        Long memberId = MemberUtils.getMemberId();

        LambdaQueryWrapper<WmsWalletLog> wrapper = Wrappers.<WmsWalletLog>lambdaQuery()
                .eq(WmsWalletLog::getMemberId, memberId)
                .eq(queryParams.getCoinType() != null, WmsWalletLog::getCoinType, queryParams.getCoinType())
                .orderByDesc(WmsWalletLog::getCreated);
        ;
        Page<WmsWalletLog> page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        Page<WalletLogVO> voPage = WalletLogConverter.INSTANCE.entity2PageVO(page);
        return voPage;
    }
}

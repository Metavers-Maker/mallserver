package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.bms.converter.TransferConfigConverter;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.mapper.TransferConfigMapper;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.entity.OmsTransferConfig;
import com.muling.mall.bms.pojo.form.admin.TransferConfigForm;
import com.muling.mall.bms.pojo.query.admin.TransferPageQuery;
import com.muling.mall.bms.pojo.vo.app.TransferVO;
import com.muling.mall.bms.service.ITransferConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class TransferConfigServiceImpl extends ServiceImpl<TransferConfigMapper, OmsTransferConfig> implements ITransferConfigService {


    @Override
    public IPage<TransferVO> page(TransferPageQuery queryParams) {

        LambdaQueryWrapper<OmsTransferConfig> wrapper = Wrappers.<OmsTransferConfig>lambdaQuery()
                .eq(queryParams.getSpuId() != null, OmsTransferConfig::getSpuId, queryParams.getSpuId())
                .orderByDesc(OmsTransferConfig::getUpdated);
        ;
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<TransferVO> list = TransferConfigConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public boolean save(TransferConfigForm transferConfigForm) {
        OmsTransferConfig config = TransferConfigConverter.INSTANCE.form2po(transferConfigForm);
        boolean b = this.save(config);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, TransferConfigForm transferConfigForm) {
        OmsTransferConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        TransferConfigConverter.INSTANCE.updatePo(transferConfigForm, config);

        return updateById(config);
    }

    @Override
    public BigDecimal getTransferConsumeValue(List<OmsMemberItem> items) {

        //根据items中的spuIds查询出所有的转账配置
        List<Long> spuIds = items.stream().map(OmsMemberItem::getSpuId).collect(Collectors.toList());
        List<OmsTransferConfig> configs = list(new LambdaQueryWrapper<OmsTransferConfig>().in(OmsTransferConfig::getSpuId, spuIds));
        if (configs.isEmpty()) {
            throw new BizException(ResultCode.TRANSFER_ITEM_DISABLED);
        }

        long count = configs.stream().map(OmsTransferConfig::getType).distinct().count();
        if (count > 1) {
            throw new BizException(ResultCode.TRANSFER_ITEM_CONSUME_TYPE_DIFFERENT);
        }

        //转换成map，key为spuId，value为转账配置
        Map<Long, OmsTransferConfig> configMap = configs.stream().collect(Collectors.toMap(OmsTransferConfig::getSpuId, v -> v));

        BigDecimal value = BigDecimal.ZERO;
        for (Long spuId : spuIds) {
            OmsTransferConfig config = configMap.get(spuId);
            if (config == null || config.getStatus() == StatusEnum.DISABLED) {
                throw new BizException(ResultCode.TRANSFER_ITEM_DISABLED);
            }
            value.add(config.getTypeValue());
        }


        return value;
    }

    @Override
    public OmsTransferConfig getBySpuId(Long spuId) {
        OmsTransferConfig config = getOne(new LambdaQueryWrapper<OmsTransferConfig>().eq(OmsTransferConfig::getSpuId, spuId));
        return config;
    }

}

package com.muling.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.wms.converter.TransferConfigConverter;
import com.muling.mall.wms.mapper.WmsTransferConfigMapper;
import com.muling.mall.wms.pojo.entity.WmsTransferConfig;
import com.muling.mall.wms.pojo.form.admin.TransferConfigForm;
import com.muling.mall.wms.service.IWmsTransferConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WmsTransferConfigServiceImpl extends ServiceImpl<WmsTransferConfigMapper, WmsTransferConfig> implements IWmsTransferConfigService {


    @Override
    public boolean save(TransferConfigForm transferConfigForm) {
        boolean exists = this.baseMapper.exists(Wrappers.<WmsTransferConfig>lambdaQuery().eq(WmsTransferConfig::getCoinType, transferConfigForm.getType()));
        if (exists) {
            throw new BizException("转赠配置已存在");
        }
        WmsTransferConfig config = TransferConfigConverter.INSTANCE.form2po(transferConfigForm);
        boolean b = this.save(config);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, TransferConfigForm transferConfigForm) {
        WmsTransferConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        TransferConfigConverter.INSTANCE.updatePo(transferConfigForm, config);

        return updateById(config);
    }

    @Override
    public WmsTransferConfig getByCoinType(Integer coinType) {
        return this.getOne(Wrappers.<WmsTransferConfig>lambdaQuery().eq(WmsTransferConfig::getCoinType, coinType));
    }
}

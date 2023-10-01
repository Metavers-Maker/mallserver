package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.entity.OmsTransferConfig;
import com.muling.mall.bms.pojo.form.admin.TransferConfigForm;
import com.muling.mall.bms.pojo.query.admin.TransferPageQuery;
import com.muling.mall.bms.pojo.vo.app.TransferVO;

import java.math.BigDecimal;
import java.util.List;

public interface ITransferConfigService extends IService<OmsTransferConfig> {


    public IPage<TransferVO> page(TransferPageQuery queryParams);


    public boolean save(TransferConfigForm transferConfigForm);

    public boolean updateById(Long id, TransferConfigForm transferConfigForm);

    /**
     * 获得转赠消耗的虚拟币
     *
     * @param items
     * @return
     */
    public BigDecimal getTransferConsumeValue(List<OmsMemberItem> items);

    public OmsTransferConfig getBySpuId(Long spuId);
}

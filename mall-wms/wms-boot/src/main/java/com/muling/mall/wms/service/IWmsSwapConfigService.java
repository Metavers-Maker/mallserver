package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.enums.StatusEnum;
import com.muling.mall.wms.pojo.entity.WmsSwapConfig;
import com.muling.mall.wms.pojo.form.admin.SwapConfigForm;
import com.muling.mall.wms.pojo.query.app.SwapConfigPageQuery;
import com.muling.mall.wms.pojo.vo.SwapConfigVO;

import java.util.Collection;

public interface IWmsSwapConfigService extends IService<WmsSwapConfig> {

    public IPage<SwapConfigVO> page(SwapConfigPageQuery queryParams);

    boolean add(SwapConfigForm form);

    boolean update(Long id, SwapConfigForm form);

    boolean update(Long id, StatusEnum status);

    public boolean delete(Collection<String> ids);

    public WmsSwapConfig getById(Long id);
}

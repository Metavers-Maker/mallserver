package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.mall.wms.pojo.form.app.NewSwapForm;
import com.muling.mall.wms.pojo.form.app.SwapForm;
import com.muling.mall.wms.pojo.query.app.SwapConfigPageQuery;
import com.muling.mall.wms.pojo.vo.SwapConfigVO;

public interface IWmsSwapService {

    public IPage<SwapConfigVO> page(SwapConfigPageQuery queryParams);

    public boolean swap(NewSwapForm swapForm);
}

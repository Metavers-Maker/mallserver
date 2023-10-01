package com.muling.mall.bms.es.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.mall.bms.pojo.query.app.ItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MemberItemVO;

public interface IOmsMemberItemEService {

    IPage<MemberItemVO> page(ItemPageQuery queryParams);

}

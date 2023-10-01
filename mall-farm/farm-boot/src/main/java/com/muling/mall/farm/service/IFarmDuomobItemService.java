package com.muling.mall.farm.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.farm.pojo.dto.FarmAdItemDTO;
import com.muling.mall.farm.pojo.dto.FarmDuomobItemDTO;
import com.muling.mall.farm.pojo.entity.FarmAdItem;
import com.muling.mall.farm.pojo.entity.FarmDuomobItem;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.query.app.FarmMemberItemPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmAdVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberItemVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;

import java.math.BigDecimal;
import java.util.List;

public interface IFarmDuomobItemService extends IService<FarmDuomobItem> {

    /**
     * 检测订单号
     *
     * @return
     */
    public boolean checkTrans(String orderId);

    public boolean saveDTO(FarmDuomobItem farmDuomobItem);

}

package com.muling.mall.farm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.farm.pojo.dto.FarmRakeDTO;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.entity.FarmRake;
import com.muling.mall.farm.pojo.query.app.FarmMemberItemPageQuery;
import com.muling.mall.farm.pojo.query.app.FarmMemberPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmMemberItemVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;
import com.muling.mall.farm.pojo.vo.app.FarmRakeVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IFarmRakeService extends IService<FarmRake>  {

    /**
     * 创建奖励记录
     *
     * @param farmRakeDTO
     */
    public boolean create(FarmRakeDTO farmRakeDTO);

    /**
     * 奖励领取
     *
     * @return
     */
    public Map<Integer,FarmRakeVO> claim();




}

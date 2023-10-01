package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsMemberMissionGroup;
import com.muling.mall.bms.pojo.query.app.MissionGroupItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MemberMissionGroupVO;

public interface IMemberMissionGroupService extends IService<OmsMemberMissionGroup> {

    public IPage<MemberMissionGroupVO> page(MissionGroupItemPageQuery queryParams);
    /**
     * 申请任务包
     */
    public MemberMissionGroupVO apply(String missionGroudName);

    /**
     * 获取任务包奖励
     */
    public boolean claim(Long id);

}


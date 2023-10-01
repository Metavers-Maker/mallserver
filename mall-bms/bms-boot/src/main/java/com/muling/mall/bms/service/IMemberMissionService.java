package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsMemberMission;
import com.muling.mall.bms.pojo.form.app.MemberMissionForm;
import com.muling.mall.bms.pojo.vo.app.MemberMissionVO;

public interface IMemberMissionService extends IService<OmsMemberMission> {

//    public IPage<MissionItemVO> page(MarketPageQueryApp queryParams);
    /**
     * 创建任务
     */
    public MemberMissionVO apply(Long id);
    /**
     * 提交任务
     */
    public boolean submit(Long id, MemberMissionForm form);

    /**
     * 审核任务
     */
    public boolean check(Long id,Integer state);

}


package com.muling.mall.farm.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.query.app.FarmMemberItemPageQuery;
import com.muling.mall.farm.pojo.query.app.FarmMemberPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmMemberItemVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;

import java.time.LocalDateTime;
import java.util.List;

public interface IFarmService {

    /**
     * 获得农场机器
     *
     * @return
     */
    public FarmMemberVO get(Long farmId);

    /**
     * 农场列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<FarmMember> pageFarm(Integer pageNum, Integer pageSize);

    /**
     * 农场机器列表
     *
     * @param queryParams
     * @return
     */
    public Page<FarmMemberItemVO> page(FarmMemberItemPageQuery queryParams);

    /**
     * 允许关闭的工作包
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<FarmMemberItem> closeFarmBagPage(Integer pageNum, Integer pageSize);

    /**
     * 创建农场机器和工作包
     *
     * @param itemId
     */
    public void create(Long itemId);

    /**
     * 开启农场机器，准备领取奖励
     *
     * @return
     */
    public boolean open(Long farmId);

    /**
     * 领取农场机器奖励
     *
     * @return
     */
    public boolean claim(Long farmId);

    /**
     * 激活工作包
     *
     * @param farmItemId
     * @return
     */
    public boolean activate(Long farmItemId);

    /**
     * 关闭工作包
     *
     * @param farmMemberItems
     */
    public void close(List<FarmMemberItem> farmMemberItems);

    /**
     * 检测用户
     *
     * @param
     */
    public boolean checkUser();

    /**
     * 开启农场
     *
     * @param
     */
    public boolean openFarm(Long[] memberIds);

    /**
     * 关闭农场
     *
     * @param
     */
    public boolean closeFarm(Long[] memberIds);

    /**
     * 重新领取农场
     */
    public boolean reClaim(Long farmId);

    /**
     * 重置（按用户重置）
     *
     * @param memberIds
     * @param farmId
     */
    public boolean reset(List<Long> memberIds,Long farmId);

    /**
     * 重置(按农场重置)
     *
     * @param farmMembers
     */
    public boolean resetFarms(List<FarmMember> farmMembers);



}

package com.muling.mall.farm.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.farm.pojo.dto.FarmAdItemDTO;
import com.muling.mall.farm.pojo.entity.FarmAd;
import com.muling.mall.farm.pojo.entity.FarmAdItem;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.query.app.FarmMemberItemPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmAdVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberItemVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;

import java.math.BigDecimal;
import java.util.List;

public interface IFarmAdService extends IService<FarmAd> {

    /**
     * 请求一个Ad任务
     * @return
     */
    public FarmAdVO openAd();

    /**
     * 获取Ad任务奖励
     *
     * @return
     */
    public FarmAdVO rewardAd();

    /**
     *  获取Ad任务进度
     * @return
     */
    public Integer stepAd();

    /**
     *  Ad回调
     * @return
     */
    public boolean adCallback(FarmAdItemDTO farmAdItemDTO);

    /**
     *  Ad Step增加
     * @return
     */
    public boolean stepAdGo(Long adSn);

    /**
     *  今天是否完成任务
     * @return
     */
    public boolean isComplete(Integer maxNum);

}

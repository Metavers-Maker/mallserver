package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.mall.bms.pojo.dto.ClaimDTO;
import com.muling.mall.bms.pojo.dto.StakeDTO;
import com.muling.mall.bms.pojo.dto.WithdrawDTO;
import com.muling.mall.bms.pojo.query.app.FarmClaimPageQuery;
import com.muling.mall.bms.pojo.query.app.FarmLogPageQuery;
import com.muling.mall.bms.pojo.vo.app.FarmClaimVO;
import com.muling.mall.bms.pojo.vo.app.FarmLogVO;

public interface IFarmService {

    public boolean stake(Long memberId, StakeDTO stakeDTO);

    public boolean withdraw(Long memberId, WithdrawDTO withdrawDTO);

    public boolean settlePool(Long poolId);

    public boolean claim(Long memberId, ClaimDTO claimDTO);

    public void settle();

    public IPage<FarmClaimVO> claimPage(Long memberId, FarmClaimPageQuery queryParams);

    public IPage<FarmLogVO> logPage(Long memberId, FarmLogPageQuery queryParams);
}

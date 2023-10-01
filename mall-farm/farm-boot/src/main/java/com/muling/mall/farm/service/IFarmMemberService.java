package com.muling.mall.farm.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IFarmMemberService extends IService<FarmMember> {

    public FarmMember getByMemberId(Long memberId, Long farmId);

    public boolean create(MemberItemDTO memberItemDTO, FarmConfig farmConfig, FarmBagConfig config);

    public boolean open(FarmMember farmMember, FarmConfig farmConfig);

    public boolean claim(FarmMember FarmMember,Integer parentBurnCode);

    public boolean close(List<FarmMemberItem> farmMemberItems);

    public boolean activate(FarmMemberItem farmMemberItem, FarmBagConfig config);

    public boolean claimPass(Long farmId);

    public boolean reset(Long memberId,Long farmId);

}

package com.muling.mall.farm.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;

public interface IFarmMemberItemService extends IService<FarmMemberItem> {

    public boolean create(MemberItemDTO memberItemDTO, FarmMember farmMember, FarmConfig farmConfig, FarmBagConfig config);

    public boolean activate(FarmMember farmMember,FarmMemberItem farmMemberItem, FarmBagConfig config);

    public boolean close(FarmMemberItem farmMemberItem);

    public boolean claim(FarmMember farmMember,Integer parentBurnCode);

    public FarmMemberItem getByItemId(Long itemId);

    public boolean refresh(Long id);

    public boolean disableByAdmin(Long id);

    public boolean enableByAdmin(Long id);

    public boolean closeByAdmin(Long id);

    public boolean freezeByAdmin(Long id);

    public boolean fixByAdmin(Long id);

}

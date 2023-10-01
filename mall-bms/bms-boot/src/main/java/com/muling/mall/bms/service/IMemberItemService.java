package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.common.result.Result;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.bms.enums.ItemLogTypeEnum;
import com.muling.mall.bms.enums.ItemTypeEnum;
import com.muling.mall.bms.event.CreateMemberItemEvent;
import com.muling.mall.bms.event.TransMemberItemEvent;
import com.muling.mall.bms.pojo.dto.CompoundDTO;
import com.muling.mall.bms.pojo.entity.BmsAirdropItem;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.form.admin.ItemTransferAdminForm;
import com.muling.mall.bms.pojo.form.app.ItemMintForm;
import com.muling.mall.bms.pojo.form.app.ItemTransferForm;
import com.muling.mall.bms.pojo.form.app.ItemTransferOutsideForm;
import com.muling.mall.bms.pojo.query.app.ItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MemberItemVO;
import com.muling.mall.bms.pojo.vo.app.OpenItemVO;

import java.util.List;

public interface IMemberItemService extends IService<OmsMemberItem> {

    /**
     * 首次发行的接口
     */
    public boolean publish(Long spuId);

    /**
     * 首发藏品锁定
     */
    public List<MemberItemDTO> lockPublish(Long memberId, Long spuId, Integer count);

    /**
     * 批量设置资源图
     */
    public boolean batchUrl(Long spuId, String pathUrl);

    /**
     * 首发藏品解锁
     */
    public boolean unlockPublish(Long memberId, Long spuId, List<String> itemNos, boolean payResult);

    /**
     * 订单核验
     */
    public String checkByOrderSn(String orderSn);

    public OpenItemVO open(Long spuId);

    public List<OmsMemberItem> selectItemsByMemberIdAndIds(Long memberId, Long[] items);

    public List<OmsMemberItem> selectItemsByMemberIdAndTypeAndIds(Long memberId, ItemTypeEnum type, Long[] transferItemIds);

    public boolean transfer(ItemTransferForm transferForm);

    public boolean transferOutside(ItemTransferOutsideForm transferOutsideForm);

    public IPage<MemberItemVO> page(ItemPageQuery queryParams);

    public boolean transferById(Long id, ItemTransferAdminForm transferForm);

    public boolean airdrop(Long memberId, Long spuId, Integer count, String reason);

    public boolean transferSourceToTarget(Long itemId, Long source, Long target);

    public boolean compound(Long memberId, CompoundDTO compoundDTO);

    public boolean freeze(OmsMemberItem item, ItemLogTypeEnum typeEnum);

    public boolean unFreeze(OmsMemberItem item, ItemLogTypeEnum typeEnum);

    public Integer itemCount(Long spuId, Integer itemType);

}


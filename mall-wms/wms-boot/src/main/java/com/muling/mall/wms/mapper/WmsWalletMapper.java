package com.muling.mall.wms.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WmsWalletMapper extends MPJBaseMapper<WmsWallet> {

    @Select("SELECT * FROM wms_wallet WHERE member_id =#{memberId} AND coin_type = #{coinType} limit 1")
    public WmsWallet selectByMemberIdAndCoinType(Long memberId, Integer coinType);
}

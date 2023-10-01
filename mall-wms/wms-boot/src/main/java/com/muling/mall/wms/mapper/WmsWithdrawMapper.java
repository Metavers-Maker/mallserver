package com.muling.mall.wms.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.pojo.entity.WmsWithdraw;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WmsWithdrawMapper extends MPJBaseMapper<WmsWithdraw> {

//    @Select("SELECT * FROM wms_wallet WHERE member_id =#{memberId} AND coin_type = #{coinType} limit 1")
//    public WmsWithdraw selectByMemberIdAndCoinType(Long memberId, Integer coinType);
}

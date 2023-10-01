package com.muling.mall.farm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FarmMemberItemMapper extends BaseMapper<FarmMemberItem> {

    @Select("<script>" +
            " SELECT * from farm_member_item where item_id =#{itemId} limit 1" +
            "</script>")
    FarmMemberItem getByItemId(Long itemId);
}

package com.muling.mall.farm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FarmBagConfigMapper extends BaseMapper<FarmBagConfig> {

    @Select("<script>" +
            " SELECT * from farm_bag_config where spu_id =#{spuId} limit 1" +
            "</script>")
    FarmBagConfig getBySpuId(Long spuId);
}

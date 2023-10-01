package com.muling.mall.pms.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.muling.mall.pms.pojo.dto.SkuInfoDTO;
import com.muling.mall.pms.pojo.entity.PmsSku;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存单元(SKU)持久层
 *
 * @author haoxr
 * @date 2022/2/6
 */
@Mapper
public interface PmsSkuMapper extends MPJBaseMapper<PmsSku> {

    /**
     * 获取商品库存单元信息
     *
     * @param skuId
     * @return
     */
    SkuInfoDTO getSkuInfo(Long skuId);

}

package com.muling.mall.pms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.query.SpuAdminPageQuery;
import com.muling.mall.pms.pojo.query.SpuPageQuery;
import com.muling.mall.pms.pojo.vo.GoodsPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品持久层
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2022/2/5
 */
@Mapper
public interface PmsSpuMapper extends MPJBaseMapper<PmsSpu> {

    @Select(" SELECT * from pms_spu ")
    List<PmsSpu> listAll();
}

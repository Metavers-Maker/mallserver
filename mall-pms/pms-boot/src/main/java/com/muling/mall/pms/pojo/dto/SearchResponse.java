package com.muling.mall.pms.pojo.dto;

import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.entity.PmsSubject;
import com.muling.mall.pms.pojo.vo.BrandVO;
import com.muling.mall.pms.pojo.vo.GoodsPageVO;
import com.muling.mall.pms.pojo.vo.SubjectVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel
public class SearchResponse {

    @ApiModelProperty("商品列表")
    List<GoodsPageVO> goods;

    @ApiModelProperty("发行方列表")
    List<BrandVO> brands;

    @ApiModelProperty("系列列表")
    List<SubjectVO> subjects;
}

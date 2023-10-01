package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.pms.pojo.dto.CheckPriceDTO;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.form.GoodsFormDTO;
import com.muling.mall.pms.pojo.query.SpuAdminPageQuery;
import com.muling.mall.pms.pojo.query.SpuPageQuery;
import com.muling.mall.pms.pojo.vo.GoodsPageVO;

import java.util.List;

/**
 * 商品业务接口
 *
 * @author haoxr
 * @date 2022/2/5
 */
public interface IPmsSpuService extends IService<PmsSpu> {

    public List<GoodsPageVO> pageSpuDetails(SpuPageQuery spuPageQuery);

    public List<GoodsPageVO> listSpuBySubjectId(Long subjectId, Integer dev);

    IPage<PmsSpu> listAdminSpuPage(SpuAdminPageQuery queryParams);

    /**
     * 「移动端」获取商品详情
     *
     * @param spuId
     * @return
     */
    GoodsPageVO getAppSpuDetail(Long spuId);

    List<GoodsPageVO> getSpuPage(SpuAdminPageQuery queryParams);

    List<GoodsPageVO> getAppSpuDetails(List<Long> spuIds);

    boolean addGoods(GoodsFormDTO goodsFormDTO);

    boolean removeByGoodsIds(List<Long> spuIds);

    boolean updateGoods(Long id, GoodsFormDTO goodsFormDTO);

    List<PmsSpu> listAll();

    SpuInfoDTO getSpuInfo(Long spuId);

    /**
     * 商品发布锁定
     */
    SpuInfoDTO publishLock(Long spuId);

    /**
     * 商品发布揭开锁定
     */
    boolean publishUnlock(Long spuId, boolean isOk);

    /**
     * 商品发布揭开锁定
     */
    boolean saleChange(Long spuId, Integer count);

    /**
     * 商品验价
     */
    boolean checkPrice(CheckPriceDTO checkPriceDTO);

}

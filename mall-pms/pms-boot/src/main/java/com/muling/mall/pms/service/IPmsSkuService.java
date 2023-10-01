package com.muling.mall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.pms.pojo.dto.CheckPriceDTO;
import com.muling.mall.pms.pojo.dto.SkuInfoDTO;
import com.muling.mall.pms.pojo.dto.app.LockStockDTO;
import com.muling.mall.pms.pojo.entity.PmsSku;
import com.muling.mall.pms.pojo.form.SkuForm;
import com.muling.mall.pms.pojo.form.UpdateSkuForm;
import com.muling.mall.pms.pojo.vo.SkuVO;

import java.util.List;

/**
 * 商品库存单元接口
 *
 * @author haoxr
 * @date 2022/2/5 17:11
 */
public interface IPmsSkuService extends IService<PmsSku> {

    /**
     * 获取商品的库存数量
     *
     * @param skuId
     * @return
     */
    Integer getStockNum(Long skuId);

    /**
     * @param skuForm
     * @return
     */
    boolean add(SkuForm skuForm);

    /**
     * @param skuId
     * @return
     */
    boolean update(Long skuId, UpdateSkuForm skuForm);

    /**
     * 根据skuId获取sku信息
     *
     * @param spuIds
     * @return
     */
    List<SkuVO> getAppSkuDetails(List<Long> spuIds);

    /**
     * 获取商品库存信息
     *
     * @param skuId
     * @return
     */
    SkuInfoDTO getSkuInfo(Long skuId);

    /**
     * 锁定商品库存
     */
    boolean lockStock(LockStockDTO lockStockDTO);

    /**
     * 解锁库存
     */
    boolean unlockStock(String orderToken);

    /**
     * 扣减库存
     */
    boolean deductStock(String orderToken);

    /**
     * 商品验价
     *
     * @param checkPriceDTO
     * @return
     */
    boolean checkPrice(CheckPriceDTO checkPriceDTO);

    /**
     * 「实验室」修改商品库存数量
     *
     * @param skuId
     * @param stockNum 商品库存数量
     * @return
     */
    boolean updateStockNum(Long skuId, Integer stockNum);

    /**
     * 扣减商品库存
     *
     * @param skuId
     * @param num   用于下订单的商品库存数量
     * @param rndNum   用于开盲盒产生的商品库存数量
     * @return
     */
    boolean deductStock(Long skuId, Integer num, Integer rndNum, boolean deductTotal);

    /**
     * 增加商品库存
     *
     * @param skuId
     * @param num   用于下订单的商品库存数量
     * @param rndNum   用于开盲盒产生的商品库存数量
     * @return
     */
    boolean addStock(Long skuId, Integer num, Integer rndNum);
    public boolean deductMint(Long skuId, Integer num);
}

package com.muling.mall.pms.service;

/**
 * Pms-BSN服务
 *
 * @author haoxr
 * @date 2022/2/5
 */
public interface IPmsBsnService {

    boolean mintGoods(String chain, Long spuId);

    boolean queryGoods(String chain, Long spuId);

}

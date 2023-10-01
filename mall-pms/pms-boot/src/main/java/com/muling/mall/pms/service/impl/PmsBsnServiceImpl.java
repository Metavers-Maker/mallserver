package com.muling.mall.pms.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.muling.common.cert.service.HttpApiClientBSN;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.common.constant.PmsConstants;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.service.IPmsBrandService;
import com.muling.mall.pms.service.IPmsBsnService;
import com.muling.mall.pms.service.IPmsSpuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 商品业务实现类
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2021/8/8
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PmsBsnServiceImpl implements IPmsBsnService {

    private final IPmsSpuService spuService;

    private final RedissonClient redissonClient;

    private final HttpApiClientBSN httpApiClientBSN;


    @Transactional
    @Override
    public boolean mintGoods(String chain, Long spuid) {
        boolean retUpdate = false;
        PmsSpu spu = spuService.getById(spuid);
        if (spu == null) {
            throw new BizException("目标商品不存在");
        }
        Assert.isTrue(spu.getMintStatus() == 0, "商品已铸造或铸造中");
        Assert.isTrue(chain.equals("BSN") == true, "支持BSN链");
        Assert.isTrue(spu.getName() != null, "NFT未设置名称");
        Assert.isTrue(spu.getSymbol() != null, "NFT未设置Sympol");
        //          Assert.isTrue(spu.getMetadataUrl() != null, "NFT未设置元数据链接");
        if (chain != null && chain.equals("BSN")) {
            JSONObject tjson = new JSONObject();
            tjson.set("nftName", spu.getName());
            tjson.set("tim", System.currentTimeMillis());
            String opIdStr = tjson.toString();
            String nftName = spu.getName();
            String nftUrl = spu.getMetadataUrl();
            String nftSymbol = spu.getSymbol();
            String nftOwn = null;
            JSONObject ret = httpApiClientBSN.createNftClass(opIdStr, spu.getId(), nftName, nftUrl, nftSymbol, nftOwn);
            if (ret.get("code").equals(200)) {
                JSONObject data = (JSONObject) ret.get("data");
                if (data != null) {
                    String operationId = data.get("operation_id").toString();
                    spu.setOperationId(operationId);
                    spu.setMintStatus(1);
                    retUpdate = spuService.updateById(spu);
                }
            } else {
                log.info("铸造NFT类别失败:{}", ret.toString());
                throw new BizException("铸造NFT类别失败");
            }
        }
        return retUpdate;
    }

    @Override
    public boolean queryGoods(String chain, Long spuId) {
        boolean retUpdate = false;
        PmsSpu spu = spuService.getById(spuId);
        if (spu == null) {
            throw new BizException("目标商品不存在");
        }
        if (chain != null && chain.equals("BSN")) {
            JSONObject tjson = new JSONObject();
            tjson.set("nftName", spu.getName());
            tjson.set("tim", System.currentTimeMillis());
            String opIdStr = tjson.toString();
            JSONObject ret = httpApiClientBSN.queryNftClass(opIdStr, spu.getId());
            if (ret.get("code").equals(200)) {
                JSONObject data = (JSONObject) ret.get("data");
                if (data != null) {
                    spu.setContract(data.get("tx_hash").toString());
                    spu.setMintStatus(2);
                    retUpdate = spuService.updateById(spu);
                }
            } else {
                throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
            }
        }
        return retUpdate;
    }

    /**
     * 定时任务查询
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void querySchedule() {
        log.info("[SCHEDULE] nft class mint query start");
        RLock lock = redissonClient.getLock(PmsConstants.PMS_MINT_AUTO_QUERY_PREFIX);
        try {
            lock.lock();
            List<PmsSpu> pmsSpuList = spuService.list(new LambdaQueryWrapper<PmsSpu>()
                    .eq(PmsSpu::getMintStatus, 1));
            //
            pmsSpuList.forEach(spu -> {
                RLock lockItem = redissonClient.getLock(PmsConstants.PMS_MINT_PREFIX + spu.getId());
                try {
                    lockItem.lock();
                    String operationId = spu.getOperationId();
                    JSONObject tjson = new JSONObject();
                    tjson.set("nftName", spu.getName());
                    tjson.set("tim", System.currentTimeMillis());
                    String opIdStr = tjson.toString();
                    JSONObject ret = httpApiClientBSN.queryNftClass(opIdStr, spu.getId());
                    if (ret.get("code").equals(200)) {
                        JSONObject data = (JSONObject) ret.get("data");
                        if (data != null) {
                            spu.setContract(data.get("tx_hash").toString());
                            spu.setMintStatus(2);
                            spuService.updateById(spu);
                        }
                    } else {
                        log.info("NFT-Pms 铸造查询失败{} {}", spu.getId(), spu.getName());
                    }
                } finally {
                    //释放锁
                    if (lockItem.isLocked() && lock.isHeldByCurrentThread()) {
                        lockItem.unlock();
                    }
                }
                //
            });
            //
        } catch (Exception e) {
            log.error("[SCHEDULE] nft-class mint query error occur", e);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[SCHEDULE] nft-class mint query unlock");
            }
            log.info("[SCHEDULE] nft-class mint query end");
        }
    }


}

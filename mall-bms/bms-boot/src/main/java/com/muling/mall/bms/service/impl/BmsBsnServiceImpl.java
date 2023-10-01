package com.muling.mall.bms.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.cert.config.BSNConfig;
import com.muling.common.cert.service.HttpApiClientBSN;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.DoubleUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.converter.FarmClaimConverter;
import com.muling.mall.bms.converter.FarmLogConverter;
import com.muling.mall.bms.enums.*;
import com.muling.mall.bms.mapper.FarmClaimMapper;
import com.muling.mall.bms.pojo.dto.ClaimDTO;
import com.muling.mall.bms.pojo.dto.StakeDTO;
import com.muling.mall.bms.pojo.dto.WithdrawDTO;
import com.muling.mall.bms.pojo.entity.*;
import com.muling.mall.bms.pojo.query.app.FarmClaimPageQuery;
import com.muling.mall.bms.pojo.query.app.FarmLogPageQuery;
import com.muling.mall.bms.pojo.vo.app.FarmClaimVO;
import com.muling.mall.bms.pojo.vo.app.FarmLogVO;
import com.muling.mall.bms.service.*;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class BmsBsnServiceImpl implements IBmsBsnService {
    private final SpuFeignClient spuFeignClient;
    private final MemberFeignClient memberFeignClient;
    private final RedissonClient redissonClient;
    private final IMemberItemService memberItemService;
    private final HttpApiClientBSN httpApiClientBSN;
    private final StringRedisTemplate redisTemplate;
    private final BSNConfig bsnConfig;
    private final IBmsBsnSwapService swapService;

    private boolean mintInner(OmsMemberItem item) {
        if (item.getStatus().equals(ItemStatusEnum.UN_MINT) == false) {
            log.info("物品:{}, No:{}正在铸造中或者已铸造", item.getName(), item.getItemNo());
            return false;
        }
        boolean retMint = false;
        RLock lockItem = redissonClient.getLock(OmsConstants.ITEM_MINT_PREFIX + item.getId());
        try {
            lockItem.lock();
            //获取用户的链上账户
            String nftName = item.getName();
            String nftUrl = item.getPicUrl();
            String nftNo = item.getItemNo();
            String nftOwn = null;
            Result<MemberDTO> memberDTOResult = memberFeignClient.getMemberById(item.getMemberId());
            if (memberDTOResult != null && memberDTOResult.getData() != null) {
                nftOwn = memberDTOResult.getData().getChainAddress();
            }
            JSONObject tjson = new JSONObject();
            tjson.set("nftName", nftName);
            tjson.set("itemNo", nftNo);
            tjson.set("tim", System.currentTimeMillis());
            String opIdStr = tjson.toString();
            JSONObject ret = httpApiClientBSN.mintNFT(opIdStr, item.getSpuId(), item.getName(), nftUrl, nftOwn);
            if (ret.get("code").equals(200)) {
                JSONObject data = (JSONObject) ret.get("data");
                if (data != null) {
                    String operationId = data.get("operation_id").toString();
                    item.setOperationId(operationId);
                    item.setStatus(ItemStatusEnum.MINTING);
                    retMint = memberItemService.updateById(item);
                }
            } else {
                log.info("铸造NFT失败 id{},spu{},No.{}", item.getId(), item.getSpuId(), item.getItemNo());
            }
        } finally {
            //释放锁
            if (lockItem.isLocked() && lockItem.isHeldByCurrentThread()) {
                lockItem.unlock();
            }
        }
        return retMint;
    }

    @Override
    public boolean mintById(Long id) {
        OmsMemberItem item = memberItemService.getById(id);
        if (item == null) {
            throw new RuntimeException("会员商品不存在");
        }
        //验证商品信息
        Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(item.getSpuId());
        Assert.isTrue(Result.isSuccess(spuInfo), "获得spu失败");
        Assert.isTrue(spuInfo.getData().getPublishStatus() == 2, "商品未发布");
        Assert.isTrue(spuInfo.getData().getMintStatus() == 2, "商品未上链");
        Assert.isTrue(item.getStatus() == ItemStatusEnum.UN_MINT, "物品已上链或上链中");
        return mintInner(item);
    }

    /**
     * 批量铸造
     */
    @Override
    public boolean mintBySpu(Long spuId) {
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_MINT_BATCH_PREFIX);
        try {
            lock.lock();
            //验证商品信息
            Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(spuId);
            Assert.isTrue(Result.isSuccess(spuInfo), "获得spu失败");
            Assert.isTrue(spuInfo.getData().getPublishStatus() == 2, "商品未发布");
            Assert.isTrue(spuInfo.getData().getMintStatus() == 2, "商品未上链");
            //循环获取数据
            Integer pageNum = 1;
            Integer pageSize = 20;
            LambdaQueryWrapper<OmsMemberItem> queryWrapper = new LambdaQueryWrapper<OmsMemberItem>()
                    .eq(OmsMemberItem::getSpuId, spuId)
                    .eq(OmsMemberItem::getStatus, ItemStatusEnum.UN_MINT)
                    .orderByAsc(OmsMemberItem::getId);
            Page<OmsMemberItem> page = memberItemService.page(new Page(pageNum, pageSize), queryWrapper);
            if (page.getRecords().size() > 0) {
                //商品操作
                page.getRecords().forEach(memberItem -> {
                    mintInner(memberItem);
                });
                while (page.hasNext()) {
                    Thread.sleep(20);
                    //
                    pageNum = pageNum + 1;
                    page = memberItemService.page(new Page(pageNum, pageSize), queryWrapper);
                    page.getRecords().forEach(memberItem -> {
                        mintInner(memberItem);
                    });
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return true;
    }

    @Override
    public boolean mintUpdateById(Long id) {
        return false;
    }

    @Override
    public boolean mintQueryById(Long id) {
        boolean retUpdate = false;
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_MINT_PREFIX + id);
        try {
            lock.lock();
            OmsMemberItem item = memberItemService.getById(id);
            if (item == null) {
                throw new RuntimeException("会员商品不存在");
            }
            Assert.isTrue(item.getStatus() == ItemStatusEnum.MINTING, "物品已铸造或铸造中");
            String operationId = item.getOperationId();
            JSONObject ret = httpApiClientBSN.queryBSN(operationId);
            if (ret.get("code").equals(200)) {
                JSONObject data = (JSONObject) ret.get("data");
                if (data != null) {
                    if (data.get("module").equals("nft") && data.get("type").equals("mint_nft") && data.getInt("status") == 1) {
                        String nftId = data.get("nft_id").toString();
                        String blockH = data.get("block_height").toString();
                        String txHash = data.get("tx_hash").toString();
                        item.setHexId(nftId);
                        item.setHash(txHash);
                        item.setStatus(ItemStatusEnum.MINTED);
                        retUpdate = memberItemService.updateById(item);
                    }
                }
            } else {
                throw new BizException("铸造NFT失败");
            }
        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return retUpdate;
    }

    /**
     * 定时任务查询
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void querySchedule() {
        log.info("[SCHEDULE] nft mint query start");
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_MINT_AUTO_QUERY_PREFIX);
        try {
            lock.lock();
            List<OmsMemberItem> memberItemList = memberItemService.list(new LambdaQueryWrapper<OmsMemberItem>()
                    .eq(OmsMemberItem::getStatus, ItemStatusEnum.MINTING));
            memberItemList.forEach(memberItem -> {
                RLock lockItem = redissonClient.getLock(OmsConstants.ITEM_MINT_PREFIX + memberItem.getId());
                try {
                    lockItem.lock();
                    String operationId = memberItem.getOperationId();
                    JSONObject ret = httpApiClientBSN.queryBSN(operationId);
                    if (ret.get("code").equals(200)) {
                        JSONObject data = (JSONObject) ret.get("data");
                        if (data != null) {
                            if (data.get("module").equals("nft") && data.get("type").equals("mint_nft") && data.getInt("status") == 1) {
                                String nftId = data.get("nft_id").toString();
                                String blockH = data.get("block_height").toString();
                                String txHash = data.get("tx_hash").toString();
                                memberItem.setHexId(nftId);
                                memberItem.setHash(txHash);
                                memberItem.setStatus(ItemStatusEnum.MINTED);
                                memberItemService.updateById(memberItem);
                                log.info("NFT铸造查询成功{}-{}-{}", nftId, memberItem.getSpuId(), memberItem.getItemNo());
                            }
                        }
                    } else {
                        log.info("NFT铸造查询失败{} {}", memberItem.getSpuId(), memberItem.getItemNo());
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
            log.error("[SCHEDULE] nft mint query error occur", e);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[SCHEDULE] nft mint query unlock");
            }
            log.info("[SCHEDULE] nft mint query end");
        }
    }

    @Override
    public boolean transQueryById(Long id) {
        boolean retUpdate = false;
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_BSN_TRANSFER_PREFIX + id);
        try {
            lock.lock();
            BmsBsnSwap bsnSwap = swapService.getById(id);
            Assert.isTrue(bsnSwap != null, "转移数据不存在");
            Assert.isTrue(bsnSwap.getStatus() == 1, "转移数据状态不正确");
            Assert.isTrue(bsnSwap.getOperationId() != null, "转移数据操作ID不正确");
            //
            OmsMemberItem memberItem = memberItemService.getById(bsnSwap.getItemId());
            if (memberItem == null) {
                //当物品销毁(例如开盲盒，合成导致的物品销毁)以后 可能会走到这里, 转移的数据表应该设置未废弃状态，退出并执行下一条数据
                bsnSwap.setStatus(4);
                boolean reUpdate = swapService.updateById(bsnSwap);
                log.info("物品不存在! spuId{},itemNo{}", bsnSwap.getSpuId(), bsnSwap.getItemNo());
                return reUpdate;
            }
            //
            String fromAddr = bsnSwap.getFromAddr();
            if (fromAddr == null) {
                Result<MemberDTO> fromMember = memberFeignClient.getMemberById(bsnSwap.getFromId());
                if (Result.isSuccess(fromMember)) {
                    fromAddr = fromMember.getData().getChainAddress();
                    bsnSwap.setFromAddr(fromAddr);
                }
            }
            String toAddr = bsnSwap.getToAddr();
            if (toAddr == null) {
                Result<MemberDTO> toMember = memberFeignClient.getMemberById(bsnSwap.getToId());
                if (Result.isSuccess(toMember)) {
                    toAddr = toMember.getData().getChainAddress();
                    bsnSwap.setToAddr(toAddr);
                }
            }
            Assert.isTrue(fromAddr != null, "来源的链上地址缺失");
            Assert.isTrue(toAddr != null, "目标的链上地址缺失");
            //
            String operationId = bsnSwap.getOperationId();
            JSONObject ret = httpApiClientBSN.queryBSN(operationId);
            if (ret.get("code").equals(200)) {
                JSONObject data = (JSONObject) ret.get("data");
                if (data != null) {
                    if (data.get("module").equals("nft") && data.get("type").equals("transfer_nft") && data.getInt("status") == 1) {
                        String nftId = data.get("nft_id").toString();
                        String blockH = data.get("block_height").toString();
                        String txHash = data.get("tx_hash").toString();
                        bsnSwap.setStatus(2);
                        retUpdate = swapService.updateById(bsnSwap);
                        return retUpdate;
                    }
                }
            } else {
                throw new BizException("转移NFT失败");
            }
        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return retUpdate;
    }

    //转移上链
    private boolean transEnsureInner(BmsBsnSwap bmsBsnSwap) {
        //
        boolean retUpdate = false;
        OmsMemberItem memberItem = memberItemService.getById(bmsBsnSwap.getItemId());
        if (memberItem == null) {
            //当物品销毁(例如开盲盒，合成导致的物品销毁)以后 可能会走到这里, 转移的数据表应该设置未废弃状态，退出并执行下一条数据
            bmsBsnSwap.setStatus(4);
            boolean reUpdate = swapService.updateById(bmsBsnSwap);
            log.info("物品不存在! spuId{},itemNo{}", bmsBsnSwap.getSpuId(), bmsBsnSwap.getItemNo());
            return reUpdate;
        }
        try {
            //这里留一个更新地址的机会
            String fromAddr = bmsBsnSwap.getFromAddr();
            if (fromAddr == null) {
                Result<MemberDTO> fromMember = memberFeignClient.getMemberById(bmsBsnSwap.getFromId());
                if (Result.isSuccess(fromMember)) {
                    fromAddr = fromMember.getData().getChainAddress();
                    bmsBsnSwap.setFromAddr(fromAddr);
                }
            }
            String toAddr = bmsBsnSwap.getToAddr();
            if (toAddr == null) {
                Result<MemberDTO> toMember = memberFeignClient.getMemberById(bmsBsnSwap.getToId());
                if (Result.isSuccess(toMember)) {
                    toAddr = toMember.getData().getChainAddress();
                    bmsBsnSwap.setToAddr(toAddr);
                }
            }
            //物品身上的地址与目标地址一致，才可以进行链上转移
            JSONObject ret = httpApiClientBSN.queryBSN(bmsBsnSwap.getOperationId());
            if (ret.get("code").equals(200)) {
                JSONObject data = (JSONObject) ret.get("data");
                if (data != null) {
                    if (data.getStr("module").equals("nft") && data.getStr("type").equals("transfer_nft") && data.getInt("status") == 1) {
                        //nft模块, 并且是交易类型, 标识处理完成
                        bmsBsnSwap.setStatus(2);
                        retUpdate = swapService.updateById(bmsBsnSwap);
                        if (retUpdate) {
                            //更新关联物品的地址 和 设置物品未非转移状态
                            memberItem.setCurAddress(bmsBsnSwap.getToAddr());
                            memberItem.setTransfer(ItemTransferEnum.NON_TRANSFER);
                            retUpdate = memberItemService.updateById(memberItem);
                        }
                    }
                }
            } else {
                log.info("查询NFT转移失败 id{},spu{},No.{}", bmsBsnSwap.getId(), bmsBsnSwap.getSpuId(), bmsBsnSwap.getItemNo());
            }
        } catch (Exception e) {
            throw e;
        }
        return retUpdate;
    }

    //转移上链
    private boolean transInner(BmsBsnSwap bmsBsnSwap) {
        OmsMemberItem memberItem = memberItemService.getById(bmsBsnSwap.getItemId());
        if (memberItem == null) {
            //当物品销毁(例如开盲盒，合成导致的物品销毁)以后 可能会走到这里, 转移的数据表应该设置未废弃状态，退出并执行下一条数据
            bmsBsnSwap.setStatus(4);
            boolean reUpdate = swapService.updateById(bmsBsnSwap);
            log.info("物品不存在! spuId{},itemNo{}", bmsBsnSwap.getSpuId(), bmsBsnSwap.getItemNo());
            return reUpdate;
        }
        //中间发现有未上链的数据，则所有的转移都退出
        if (memberItem.getHexId() == null) {
            log.info("物品未上链! spuId{},itemNo{}", bmsBsnSwap.getSpuId(), bmsBsnSwap.getItemNo());
            throw new BizException("物品未上链");
        }
        //发现物品已经在转移，则退出，执行下一条数据
        if (memberItem.getTransfer().equals(ItemTransferEnum.TRANSFER)) {
            log.info("物品转移中! spuId{},itemNo{}", bmsBsnSwap.getSpuId(), bmsBsnSwap.getItemNo());
            return false;
        }
        //获取地址
        String fromAddr = null;
        if (bmsBsnSwap.getFromId().equals(0l)) {
            fromAddr = bsnConfig.getAccount();
        } else {
            Result<MemberDTO> fromMember = memberFeignClient.getMemberById(bmsBsnSwap.getFromId());
            if (Result.isSuccess(fromMember)) {
                fromAddr = fromMember.getData().getChainAddress();
            }
        }
        String toAddr = null;
        if (bmsBsnSwap.getToId().equals(0l)) {
            toAddr = bsnConfig.getAccount();
        } else {
            Result<MemberDTO> toMember = memberFeignClient.getMemberById(bmsBsnSwap.getToId());
            if (Result.isSuccess(toMember)) {
                toAddr = toMember.getData().getChainAddress();
            }
        }
        Assert.isTrue(fromAddr != null, "来源的链上地址缺失");
        Assert.isTrue(toAddr != null, "目标的链上地址缺失");
        try {
            //物品身上的地址与目标地址一致，才可以进行链上转移
            JSONObject tjson = new JSONObject();
            tjson.set("nftName", bmsBsnSwap.getSpuName());
            tjson.set("itemNo", bmsBsnSwap.getItemNo());
            tjson.set("tim", System.currentTimeMillis());
            String opIdStr = tjson.toString();
            JSONObject ret = httpApiClientBSN.transNFT(opIdStr, bmsBsnSwap.getSpuId(), memberItem.getHexId(), fromAddr, toAddr);
            if (ret.get("code").equals(200)) {
                JSONObject data = (JSONObject) ret.get("data");
                if (data != null) {
                    String operationId = data.get("operation_id").toString();
                    bmsBsnSwap.setOperationId(operationId);
                    bmsBsnSwap.setStatus(1); //该条数据已经执行转移操作，希望进行查询
                    bmsBsnSwap.setFromAddr(fromAddr);
                    bmsBsnSwap.setToAddr(toAddr);
                    boolean retUpdate = swapService.updateById(bmsBsnSwap);
                    if (retUpdate) {
                        //设置物品为转移状态
                        memberItem.setTransfer(ItemTransferEnum.TRANSFER);
                        retUpdate = memberItemService.updateById(memberItem);
                    }
                    return retUpdate;
                }
            } else {
                log.info("转移NFT失败 id{},spu{},No.{}", bmsBsnSwap.getId(), bmsBsnSwap.getSpuId(), bmsBsnSwap.getItemNo());
                throw new BizException("NFT转移失败");
            }
        } catch (Exception e) {
            throw e;
        }
        return false;
    }

    /**
     * 定时查询转移情况(2分钟一次)
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void execTransSchedule() {
        log.info("[SCHEDULE] nft trans exec start");
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_BSN_AUTO_TRANS_PREFIX);
        try {
            lock.lock();
            Integer pageNum = 1;
            Integer pageSize = 20;
            //循环获取数据 进行链上确认（状态为1可以进行链上确认）
            LambdaQueryWrapper<BmsBsnSwap> queryWrapperEnsure = new LambdaQueryWrapper<BmsBsnSwap>()
                    .eq(BmsBsnSwap::getStatus, 1)
                    .orderByAsc(BmsBsnSwap::getId);
            Page<BmsBsnSwap> pageEnsure = swapService.page(new Page(pageNum, pageSize), queryWrapperEnsure);
            if (pageEnsure.getRecords().size() > 0) {
                //商品操作
                pageEnsure.getRecords().forEach(bsnSwap -> {
                    transEnsureInner(bsnSwap);
                });
                while (pageEnsure.hasNext()) {
                    Thread.sleep(20);
                    //
                    pageNum = pageNum + 1;
                    pageEnsure = swapService.page(new Page(pageNum, pageSize), queryWrapperEnsure);
                    pageEnsure.getRecords().forEach(bsnSwap -> {
                        transEnsureInner(bsnSwap);
                    });
                }
            }

            //循环获取数据 - 链上转移（状态为0可以进行链上转移）
            LambdaQueryWrapper<BmsBsnSwap> queryWrapperTrans = new LambdaQueryWrapper<BmsBsnSwap>()
                    .eq(BmsBsnSwap::getStatus, 0)
                    .orderByAsc(BmsBsnSwap::getId);
            Page<BmsBsnSwap> pageTrans = swapService.page(new Page(pageNum, pageSize), queryWrapperTrans);
            if (pageTrans.getRecords().size() > 0) {
                //商品操作
                pageTrans.getRecords().forEach(bsnSwap -> {
                    transInner(bsnSwap);
                });
                while (pageTrans.hasNext()) {
                    Thread.sleep(20);
                    //
                    pageNum = pageNum + 1;
                    pageTrans = swapService.page(new Page(pageNum, pageSize), queryWrapperTrans);
                    pageTrans.getRecords().forEach(bsnSwap -> {
                        transInner(bsnSwap);
                    });
                }
            }
        } catch (Exception e) {
            log.error("[SCHEDULE] nft trans exec error occur", e);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[SCHEDULE] nft trans exec unlock");
            }
            log.info("[SCHEDULE] nft trans exec end");
        }
    }

}

package com.muling.mall.bms.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.muling.common.base.IBaseEnum;
import com.muling.common.cert.config.BSNConfig;
import com.muling.common.cert.service.HttpApiClientBSN;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.DateUtils;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.converter.MemberItemConverter;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.bms.enums.*;
import com.muling.mall.bms.mapper.MemberItemMapper;
import com.muling.mall.bms.pojo.dto.CompoundDTO;
import com.muling.mall.bms.pojo.entity.*;
import com.muling.mall.bms.pojo.form.admin.ItemTransferAdminForm;
import com.muling.mall.bms.pojo.form.app.ItemTransferForm;
import com.muling.mall.bms.pojo.form.app.ItemTransferOutsideForm;
import com.muling.mall.bms.pojo.query.app.ItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MemberItemVO;
import com.muling.mall.bms.pojo.vo.app.OpenItemVO;
import com.muling.mall.bms.service.*;
import com.muling.mall.bms.util.LotteryUtils;
import com.muling.mall.bms.util.Prize;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.pms.enums.BindEnum;
import com.muling.mall.pms.enums.InsideEnum;
import com.muling.mall.pms.pojo.dto.RndDTO;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.mall.ums.pojo.dto.MemberSimpleDTO;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("memberItemService")
@Slf4j
@AllArgsConstructor
public class MemberItemServiceImpl extends ServiceImpl<MemberItemMapper, OmsMemberItem> implements IMemberItemService {

    private final SpuFeignClient spuFeignClient;
    private final MemberFeignClient memberFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final IItemLogService itemLogService;
    private final WalletFeignClient walletFeignClient;
    private final ITransferConfigService transferConfigService;
    private final IBmsBsnSwapService bmsBsnSwapService;

    private final BSNConfig bsnConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String checkByOrderSn(String orderSn) {
        String s = redisTemplate.opsForValue().get(OmsConstants.ORDER_SN_ITEMS_PREFIX + orderSn);
        if (StrUtil.isBlank(s)) {
            throw new BizException("订单号对应的物品不存在");
        }
        //根据物品Ids，返回List
        return s;
    }

    /**
     * 内部生成物品的接口
     */
    private boolean newItemBatch(List<OmsMemberItem> items, List<OmsItemLog> itemsLog) {
        //设置开始持有时间
        boolean saveFlag = saveBatch(items, items.size());
        itemLogService.saveBatch(itemsLog, items.size());
        return saveFlag;
    }

    /**
     * 发行藏品
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public boolean publish(Long spuId) {
        //记得加锁
        boolean retUpdate = false;
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_PUBLISH_PREFIX + spuId);
        try {
            lock.lock();
            Result<SpuInfoDTO> spuInfo = spuFeignClient.publishLock(spuId);
            Assert.isTrue(Result.isSuccess(spuInfo), "获得spu失败");
            Assert.isTrue(spuInfo.getData().getPublishStatus() == 1, "Spu已经发布");
            Assert.isTrue(StrUtil.isNotBlank(spuInfo.getData().getSourceUrl()), "请配置商品SourceUrl");
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
            tomorrow = tomorrow.withHour(0).withMinute(0).withSecond(0);
            List<OmsMemberItem> memberItemList = new ArrayList<>();
            List<OmsItemLog> itemLogList = new ArrayList<>();
            for (int i = 0; i < spuInfo.getData().getTotal(); i++) {
                //生成物品
                Integer intNum = i + 1;
                String itemNo = intNum.toString();
                OmsMemberItem memberItem = new OmsMemberItem()
                        .setFromType(FromTypeEnum.FROM_PUBLISH)
                        .setSwapPrice(spuInfo.getData().getPrice())
                        .setMemberId(0l)
                        .setFreezeType(ItemFreezeTypeEnum.PUBLISH)
                        .setStatus(ItemStatusEnum.UN_MINT)
                        .setContract(spuInfo.getData().getContract())
//                        .setHexId(IdUtils.make64NanoId()) //上链后生成
                        .setName(spuInfo.getData().getName())
                        .setType(spuInfo.getData().getType())
                        .setBind(IBaseEnum.getEnumByValue(spuInfo.getData().getBind(), BindEnum.class))
                        .setItemNo(itemNo)
                        .setPicUrl(spuInfo.getData().getPicUrl())
                        .setSourceUrl(spuInfo.getData().getSourceUrl())
                        .setSourceType(spuInfo.getData().getSourceType())
                        .setStarted(tomorrow)
                        .setStickNum(0)
                        .setSpuId(spuInfo.getData().getId())
                        .setCurAddress(bsnConfig.getAccount());
                memberItemList.add(memberItem);
                //生成Log
                OmsItemLog itemLog = new OmsItemLog()
                        .setMemberId(0L)
                        .setMemberFrom(0L)
                        .setMemberTo(0L)
                        .setSpuId(memberItem.getSpuId())
                        .setItemNo(memberItem.getItemNo())
                        .setItemName(memberItem.getName())
                        .setPicUrl(memberItem.getPicUrl())
                        .setSourceUrl(memberItem.getSourceUrl())
                        .setPrice(spuInfo.getData().getPrice())
                        .setType(ItemLogTypeEnum.PUBLISH);
                itemLogList.add(itemLog);
            }
            //批量铸造
            newItemBatch(memberItemList, itemLogList);
            //
            spuFeignClient.publishUnlock(spuId, true);
            retUpdate = true;
            //
        } catch (Exception e) {
            spuFeignClient.publishUnlock(spuId, false);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return retUpdate;
    }

    /**
     * 首发藏品锁定
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public List<MemberItemDTO> lockPublish(Long memberId, Long spuId, Integer count) {
        Assert.isTrue(count > 0, "藏品数量>=0");
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_PUBLISH_BUY_PREFIX + spuId);
        try {
            lock.lock();
            IPage page = this.baseMapper.selectPage(new Page(1, count), new LambdaQueryWrapper<OmsMemberItem>()
                    .eq(OmsMemberItem::getMemberId, 0l)
                    .eq(OmsMemberItem::getSpuId, spuId)
                    .eq(OmsMemberItem::getFreezeType, ItemFreezeTypeEnum.PUBLISH)
                    .eq(OmsMemberItem::getFreeze, ItemFreezeStatusEnum.UN_FREEZE));
            if (page.getRecords().size() < count) {
                //Assert.isTrue(page.getRecords().size() == count, "藏品数量不匹配");
                //数量不匹配
                List<MemberItemDTO> memberItemDTOList = new ArrayList<>();
                return memberItemDTOList;
            }
            //
            List<OmsMemberItem> memberItemList = page.getRecords();
            for (int i = 0; i < memberItemList.size(); i++) {
                memberItemList.get(i).setFreeze(ItemFreezeStatusEnum.FREEZE);
            }
            this.updateBatchById(memberItemList);
            List<MemberItemDTO> memberItemDTOList = MemberItemConverter.INSTANCE.po2dtoList(memberItemList);
            return memberItemDTOList;
        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    /**
     * 首发藏品解锁释放物品
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public boolean unlockPublish(Long memberId, Long spuId, List<String> itemNos, boolean payResult) {
        //
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        tomorrow = tomorrow.withHour(0).withMinute(0).withSecond(0);
        //
        boolean retUpdate = false;
        List<OmsMemberItem> memberItemList = this.baseMapper.selectList(new LambdaQueryWrapper<OmsMemberItem>()
                .eq(OmsMemberItem::getSpuId, spuId)
                .in(OmsMemberItem::getItemNo, itemNos));
        if (memberItemList.size() == 0) {
            log.info("释放异常，物品为空");
            return true;
        }
        Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(spuId);
        Assert.isTrue(Result.isSuccess(spuInfo), "获得spu失败");
        if (payResult) {
            //支付成功
            List<OmsItemLog> itemLogList = new ArrayList<OmsItemLog>();
            for (int i = 0; i < memberItemList.size(); i++) {
                memberItemList.get(i).setFreeze(ItemFreezeStatusEnum.UN_FREEZE);
                memberItemList.get(i).setFreezeType(ItemFreezeTypeEnum.COMMON);
                memberItemList.get(i).setMemberId(memberId);
                memberItemList.get(i).setStarted(tomorrow);
                memberItemList.get(i).setStickNum(0);
                memberItemList.get(i).setFromType(FromTypeEnum.FROM_MARKET_1_GET);
                //市场日志
                OmsItemLog itemLog = new OmsItemLog()
                        .setMemberId(memberId)
                        .setMemberFrom(0L)
                        .setMemberTo(memberId)
                        .setSpuId(memberItemList.get(i).getSpuId())
                        .setItemNo(memberItemList.get(i).getItemNo())
                        .setItemName(memberItemList.get(i).getName())
                        .setPicUrl(memberItemList.get(i).getPicUrl())
                        .setSourceUrl(memberItemList.get(i).getSourceUrl())
                        .setType(ItemLogTypeEnum.BUY)
                        .setPrice(spuInfo.getData().getPrice())
                        .setReason("首发购买：" + memberItemList.get(i).getName());
                itemLogList.add(itemLog);
            }
            retUpdate = this.updateBatchById(memberItemList);
            //生成空投Log
            if (retUpdate) {
                //创建首发上链任务
                bmsBsnSwapService.saveBatch(0l, memberId, memberItemList, ChainSwapTypeEnum.PUBLISH_BUY);
                //常见日志
                retUpdate = itemLogService.saveBatch(itemLogList);
                //更新库存
                spuFeignClient.saleChange(spuId, itemNos.size());
            }
        } else {
            //支付失败
            for (int i = 0; i < memberItemList.size(); i++) {
                memberItemList.get(i).setFreeze(ItemFreezeStatusEnum.UN_FREEZE);
            }
            retUpdate = this.updateBatchById(memberItemList);
        }
        return retUpdate;
    }

    /**
     * 批量设置资源图
     */
    @Override
    @Transactional
    public boolean batchUrl(Long spuId, String pathStr) {
        boolean retUpdate = false;
        Assert.isTrue(StringUtils.isNotBlank(pathStr), "路径错误");
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_PUBLISH_BUY_PREFIX + spuId);
        try {
            lock.lock();
            List<OmsMemberItem> memberItemList = this.baseMapper.selectList(new LambdaQueryWrapper<OmsMemberItem>()
                    .eq(OmsMemberItem::getSpuId, spuId));
            Assert.isTrue(memberItemList.size() > 0, "物品为空");
            //
            String finalPath = pathStr;
            if (finalPath.endsWith("/") == false) {
                finalPath = pathStr + "/";
            }
            //
            for (int i = 0; i < memberItemList.size(); i++) {
                String url = finalPath + memberItemList.get(i).getItemNo() + ".png";
                memberItemList.get(i).setSourceUrl(url);
            }
            //
            retUpdate = this.updateBatchById(memberItemList, memberItemList.size());
            return retUpdate;
        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean airdrop(Long memberId, Long spuId, Integer count, String reason) {
        //
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        tomorrow = tomorrow.withHour(0).withMinute(0).withSecond(0);
        //
        boolean reUpdate = false;
        Assert.isTrue(count > 0, "藏品数量>=0");
        Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(spuId);
        Assert.isTrue(Result.isSuccess(spuInfo), "获得spu失败");
        Assert.isTrue(spuInfo.getData().getPublishStatus() == 2, "商品未发布");
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_PUBLISH_BUY_PREFIX + spuId);
        try {
            lock.lock();
            IPage page = this.baseMapper.selectPage(new Page(1, count), new LambdaQueryWrapper<OmsMemberItem>()
                    .eq(OmsMemberItem::getMemberId, 0l)
                    .eq(OmsMemberItem::getSpuId, spuId)
                    .eq(OmsMemberItem::getFreezeType, ItemFreezeTypeEnum.PUBLISH)
                    .eq(OmsMemberItem::getFreeze, ItemFreezeStatusEnum.UN_FREEZE));
            Assert.isTrue(page.getRecords().size() == count, "藏品数量不匹配");
            List<OmsMemberItem> memberItemList = page.getRecords();
            List<OmsItemLog> itemLogList = new ArrayList<OmsItemLog>();
            for (int i = 0; i < memberItemList.size(); i++) {
                memberItemList.get(i).setStarted(tomorrow);
                memberItemList.get(i).setStickNum(0);
                memberItemList.get(i).setMemberId(memberId);
                memberItemList.get(i).setFreezeType(ItemFreezeTypeEnum.COMMON);
                memberItemList.get(i).setFromType(FromTypeEnum.FROM_AIRDROP_GET);
                //空投日志
                OmsItemLog itemLog = new OmsItemLog()
                        .setMemberId(memberId)
                        .setMemberFrom(0L)
                        .setMemberTo(memberId)
                        .setSpuId(memberItemList.get(i).getSpuId())
                        .setItemNo(memberItemList.get(i).getItemNo())
                        .setItemName(memberItemList.get(i).getName())
                        .setPicUrl(memberItemList.get(i).getPicUrl())
                        .setSourceUrl(memberItemList.get(i).getSourceUrl())
                        .setPrice(spuInfo.getData().getPrice())
                        .setType(ItemLogTypeEnum.AIRDROP)
                        .setReason("空投物品：" + memberItemList.get(i).getName() + "-来源：" + reason);
                itemLogList.add(itemLog);
            }
            reUpdate = this.updateBatchById(memberItemList);
            //生成空投Log
            if (reUpdate) {
                //创建空投上链任务
                bmsBsnSwapService.saveBatch(0l, memberId, memberItemList, ChainSwapTypeEnum.AIRDROP);
                //
                reUpdate = itemLogService.saveBatch(itemLogList);
                //更新库存
                spuFeignClient.saleChange(spuId, count);
            }
        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return reUpdate;
    }

    /**
     * 盲盒奖励(释放物品)
     */
    @GlobalTransactional
    private OpenItemVO boxPrize(Long memberId, RndDTO rndDTO) {
        OpenItemVO itemVO = new OpenItemVO();
        itemVO.setSpuId(rndDTO.getSpuId());
        //物品奖励
        if (rndDTO.getSpuId() != 0) {
            Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(rndDTO.getSpuId());
            Assert.isTrue(Result.isSuccess(spuInfo), "获得spu失败");
            SpuInfoDTO spuInfoData = spuInfo.getData();
            Assert.isTrue(spuInfoData.getPublishStatus() == 2, "商品未发布");
            RLock lock = redissonClient.getLock(OmsConstants.ITEM_PUBLISH_BUY_PREFIX + rndDTO.getSpuId());
            try {
                lock.lock();
                //
                LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
                tomorrow = tomorrow.withHour(0).withMinute(0).withSecond(0);
                //物品奖励（从0地址中获取对应的物品）
                IPage page = this.baseMapper.selectPage(new Page(1, rndDTO.getSpuCount()), new LambdaQueryWrapper<OmsMemberItem>()
                        .eq(OmsMemberItem::getMemberId, 0l)
                        .eq(OmsMemberItem::getSpuId, rndDTO.getSpuId())
                        .eq(OmsMemberItem::getFreezeType, ItemFreezeTypeEnum.PUBLISH)
                        .eq(OmsMemberItem::getFreeze, ItemFreezeStatusEnum.UN_FREEZE));
                Assert.isTrue(page.getRecords().size() == rndDTO.getSpuCount(), "藏品数量不匹配");
                //
                List<String> itemNos = new ArrayList<>();
                List<OmsMemberItem> memberItemList = page.getRecords();
                List<OmsItemLog> itemLogList = new ArrayList<OmsItemLog>();
                for (int i = 0; i < memberItemList.size(); i++) {
                    //修改物品归属关系
                    memberItemList.get(i).setStarted(tomorrow);
                    memberItemList.get(i).setStickNum(0);
                    memberItemList.get(i).setMemberId(memberId);
                    memberItemList.get(i).setFreezeType(ItemFreezeTypeEnum.COMMON);
                    memberItemList.get(i).setFromType(FromTypeEnum.FROM_BOX_GET);
                    itemNos.add(memberItemList.get(i).getItemNo());
                    //盲盒日志
                    OmsItemLog itemLog = new OmsItemLog()
                            .setMemberId(memberId)
                            .setMemberFrom(0L)
                            .setMemberTo(memberId)
                            .setSpuId(memberItemList.get(i).getSpuId())
                            .setItemNo(memberItemList.get(i).getItemNo())
                            .setItemName(memberItemList.get(i).getName())
                            .setPicUrl(memberItemList.get(i).getPicUrl())
                            .setSourceUrl(memberItemList.get(i).getSourceUrl())
                            .setPrice(spuInfo.getData().getPrice())
                            .setType(ItemLogTypeEnum.OPEN)
                            .setReason("盲盒奖励：" + memberItemList.get(i).getName());
                    itemLogList.add(itemLog);
                }
                itemVO.setItemNos(itemNos);
                //
                boolean reUpdate = this.updateBatchById(memberItemList);
                //生成空投Log
                if (reUpdate) {
                    //创建盲盒开出的上链任务
                    bmsBsnSwapService.saveBatch(0l, memberId, memberItemList, ChainSwapTypeEnum.BBOX);
                    //更新库存(从总库存扣除)
                    spuFeignClient.saleChange(rndDTO.getSpuId(), rndDTO.getSpuCount());
                    //保存日志
                    itemLogService.saveBatch(itemLogList);
                }
            } finally {
                //释放锁
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
            //返回值
            itemVO.setType(0);
            itemVO.setCount(rndDTO.getSpuCount());
            itemVO.setName(spuInfoData.getName());
            itemVO.setPicUrl(spuInfoData.getPicUrl());
        }
        //积分奖励
        if (rndDTO.getCoinCount() > 0) {
            BigDecimal coinCount = new BigDecimal(rndDTO.getCoinCount());
            WalletDTO walletDTO = new WalletDTO();
            walletDTO.setMemberId(memberId);
            walletDTO.setCoinType(rndDTO.getCoinType());
            walletDTO.setBalance(coinCount);
            walletDTO.setOpType(WalletOpTypeEnum.OPEN.getValue());
            walletDTO.setRemark("开盲盒");
            walletFeignClient.updateBalance(walletDTO);
            //返回值
            itemVO.setType(1);
            itemVO.setCoinType(rndDTO.getCoinType());
            itemVO.setBalance(coinCount);
        }
        return itemVO;
    }

    /**
     * 开盲盒
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public OpenItemVO open(Long id) {
        //记得加锁
        OpenItemVO openItemVO = null;
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_OPEN_PREFIX + id);
        try {
            lock.lock();
            Long memberId = MemberUtils.getMemberId();
            OmsMemberItem item = getById(id);
            if (item == null) {
                throw new BizException(ResultCode.DATA_NOT_EXIST);
            }
            if (item.getType() != ItemTypeEnum.BLIND_BOX.getValue()) {
                throw new BizException("该商品不是盲盒");
            }
            if (item.getMemberId().longValue() != memberId) {
                throw new BizException("该商品不是您的");
            }
            //锁定盲盒库存（回来的数据都可以作为奖励）
            Result<List<RndDTO>> skuRndInfo = spuFeignClient.spuRndLock(item.getSpuId());
            //生成概率列表
            List<Prize> prizes = new ArrayList<>();
            skuRndInfo.getData().forEach((boxItem) -> {
                //最大数量，产出和最大相等
                Prize t_prize = new Prize();
                t_prize.setCfgId(boxItem.getId());
                t_prize.setProb(boxItem.getProd());
                prizes.add(t_prize);
            });
            Assert.isTrue(prizes.size() != 0, "打开盲盒失败！");
            //开盲盒奖励
            Prize open = LotteryUtils.open(prizes);
            RndDTO rewardRndDTO = new RndDTO();
            for (int i = 0; i < skuRndInfo.getData().size(); i++) {
                if (skuRndInfo.getData().get(i).getId().equals(open.getCfgId())) {
                    rewardRndDTO = skuRndInfo.getData().get(i);
                }
            }
            //释放盲盒库存
            spuFeignClient.spuRndUnlock(item.getSpuId(), rewardRndDTO.getId());

            //删除物品
            removeById(id);
            //增加物品日志
            OmsItemLog drop = new OmsItemLog()
                    .setMemberId(item.getMemberId())
                    .setMemberFrom(item.getMemberId())
                    .setMemberTo(0L)
                    .setSpuId(item.getSpuId())
                    .setSkuId(item.getSpuId())
                    .setItemNo(item.getItemNo())
                    .setItemName(item.getName())
                    .setPicUrl(item.getPicUrl())
                    .setSourceUrl(item.getSourceUrl())
                    .setType(ItemLogTypeEnum.OPEN)
                    .setReason("删除盲盒：" + item.getName());
            itemLogService.save(drop);
            //奖励物品
            openItemVO = this.boxPrize(memberId, rewardRndDTO);
            //
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return openItemVO;
    }

    public List<OmsMemberItem> selectItemsByMemberIdAndTypeAndIds(Long memberId, ItemTypeEnum type, Long[] transferItemIds) {
        LambdaQueryWrapper<OmsMemberItem> eq = Wrappers.<OmsMemberItem>lambdaQuery()
                .eq(OmsMemberItem::getMemberId, memberId)
                .eq(OmsMemberItem::getType, type)
                .in(OmsMemberItem::getId, transferItemIds);
        return this.baseMapper.selectList(eq);
    }

    public List<OmsMemberItem> selectItemsByMemberIdAndIds(Long memberId, Long[] items) {
        LambdaQueryWrapper<OmsMemberItem> eq = Wrappers.<OmsMemberItem>lambdaQuery()
                .eq(OmsMemberItem::getMemberId, memberId)
                .in(OmsMemberItem::getId, items);
        return this.baseMapper.selectList(eq);
    }

    public List<OmsMemberItem> selectUnFreezeItemsByMemberIdAndIds(Long memberId, Long[] items) {
        LambdaQueryWrapper<OmsMemberItem> eq = Wrappers.<OmsMemberItem>lambdaQuery()
                .eq(OmsMemberItem::getMemberId, memberId)
                .eq(OmsMemberItem::getFreeze, ItemFreezeStatusEnum.UN_FREEZE)
                .in(OmsMemberItem::getId, items);
        return this.baseMapper.selectList(eq);
    }

    /**
     * 直接转赠物品到目标
     */
    @Override
    @GlobalTransactional
    public boolean transfer(ItemTransferForm transferForm) {
        //记得加锁
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_TRANSFER_PREFIX + memberId);
        try {
            lock.lock();

            Long[] itermIds = transferForm.getItermId();
            String uid = transferForm.getToUid();

            Result<MemberAuthDTO> toMember = memberFeignClient.loadUserByUid(uid);
            Assert.isTrue(Result.isSuccess(toMember), "没有找到该用户");
            Long toMemberId = toMember.getData().getMemberId();
            List<OmsMemberItem> items = this.selectUnFreezeItemsByMemberIdAndIds(memberId, itermIds);
            Assert.isTrue(items.size() == itermIds.length, "物品不匹配");
            Result<MemberSimpleDTO> member = memberFeignClient.getSimpleUserById(toMemberId);
            Assert.isTrue(Result.isSuccess(member), "转移用户不存在");
            Assert.isTrue(member.getData().getId().longValue() != memberId.longValue(), "不能给自己转移");
            Result<MemberSimpleDTO> selfMember = memberFeignClient.getSimpleUserById(memberId);
            Assert.isTrue(Result.isSuccess(selfMember), "用户不存在");
            //
            for (int i = 0; i < items.size(); i++) {
                OmsMemberItem omsMemberItem = items.get(i);
                //判断是否绑定
                BindEnum bind = omsMemberItem.getBind();
                if (bind == BindEnum.BIND && false) {
                    throw new BizException(ResultCode.ITEM_ALREADY_BIND);
                }
                //
                if (omsMemberItem.getInside() == InsideEnum.OUTSIDE) {
                    throw new BizException(ResultCode.ITEM_UNSUPPORTED_TRANSFER);
                }
                //外链，判断冷却期
                if (omsMemberItem.getInside() == InsideEnum.INSIDE && false) {
                    OmsTransferConfig config = transferConfigService.getBySpuId(omsMemberItem.getSpuId());
                    LocalDateTime started = omsMemberItem.getStarted();
                    if (started != null) {
                        boolean in = DateUtil.isIn(DateUtil.date(), DateUtils.localDateTimeToDate(started), DateUtils.localDateTimeToDate(started.plusSeconds(config.getIcd())));
                        if (in) {
                            throw new BizException("还在冷却期");
                        }
                    }
                    omsMemberItem.setStarted(LocalDateTime.now());
                }
                //转移NFT
                omsMemberItem.setFromType(FromTypeEnum.FROM_TRANSFORM_GET);
                omsMemberItem.setMemberId(toMemberId);
                omsMemberItem.setStickNum(0);
                omsMemberItem.setStarted(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
            }
            //转赠
            boolean reUpdate = updateBatchById(items);
            if (reUpdate) {
                //创建转赠上链任务
                bmsBsnSwapService.saveBatch(0l, memberId, items, ChainSwapTypeEnum.TRANSFER);
            }
            //待优化
//            OmsTransferConfig config = transferConfigService.getBySpuId(items.get(0).getSpuId());
//            BigDecimal transferConsumeValue = transferConfigService.getTransferConsumeValue(items);
//            if (transferConsumeValue.compareTo(BigDecimal.ZERO) > 0) {
//                //转赠消耗
//                WalletDTO walletDTO = new WalletDTO()
//                        .setMemberId(memberId)
//                        .setBalance(transferConsumeValue.negate())
//                        .setCoinType(config.getType())
//                        .setOpType(WalletOpTypeEnum.TRANSFER_CONSUME.getValue())
//                        .setRemark("转赠消耗");
//                Result result = walletFeignClient.updateBalance(walletDTO);
//                if (!Result.isSuccess(result)) {
//                    throw new BizException(ResultCode.getValue(result.getCode()));
//                }
//            }
            List<OmsItemLog> itemLogList = Lists.newArrayList();
            //转赠日志
            for (int i = 0; i < items.size(); i++) {
                OmsMemberItem item = items.get(i);
                OmsItemLog transferLog = new OmsItemLog()
                        .setMemberId(memberId)
                        .setMemberFrom(memberId)
                        .setMemberTo(toMemberId)
                        .setSpuId(item.getSpuId())
                        .setItemNo(item.getItemNo())
                        .setItemName(item.getName())
                        .setPicUrl(item.getPicUrl())
                        .setSourceUrl(item.getSourceUrl())
                        .setPrice(item.getSwapPrice())
                        .setType(ItemLogTypeEnum.TRANSFER)
                        .setReason("转赠：" + item.getName() + " 到" + member.getData().getNickName());
                itemLogList.add(transferLog);
                //
                OmsItemLog transferReceiveLog = new OmsItemLog()
                        .setMemberId(toMemberId)
                        .setMemberFrom(memberId)
                        .setMemberTo(toMemberId)
                        .setSpuId(item.getSpuId())
                        .setItemNo(item.getItemNo())
                        .setItemName(item.getName())
                        .setPicUrl(item.getPicUrl())
                        .setPrice(item.getSwapPrice())
                        .setSourceUrl(item.getSourceUrl())
                        .setType(ItemLogTypeEnum.TRANSFER_RECEIVE)
                        .setReason("转赠接收：" + item.getName() + " 从" + selfMember.getData().getNickName());
                itemLogList.add(transferReceiveLog);
            }
            itemLogService.saveBatch(itemLogList);
        } catch (Exception e) {
            log.error("", e);
            return false;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean transferOutside(ItemTransferOutsideForm transferOutsideForm) {
        //记得加锁
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_TRANSFER_PREFIX + memberId);

        try {
            lock.lock();

            Long[] transferItemIds = transferOutsideForm.getTransferItemId();
            Long[] itermIds = transferOutsideForm.getItermId();
            String uid = transferOutsideForm.getToUid();
            Result<MemberAuthDTO> toMember = memberFeignClient.loadUserByUid(uid);
            Assert.isTrue(Result.isSuccess(toMember), "没有找到该用户");
            Long toMemberId = toMember.getData().getMemberId();
            List<OmsMemberItem> transferItems = this.selectItemsByMemberIdAndTypeAndIds(memberId, ItemTypeEnum.TRANSFER, transferItemIds);
            Assert.isTrue(transferItems.size() == transferItemIds.length, "物品不匹配");
            List<OmsMemberItem> items = this.selectUnFreezeItemsByMemberIdAndIds(memberId, itermIds);
            Assert.isTrue(items.size() == itermIds.length, "物品不匹配");
            Result<MemberSimpleDTO> member = memberFeignClient.getSimpleUserById(toMemberId);
            Assert.isTrue(member != null, "转移用户不存在");
            Assert.isTrue(member.getData().getId().longValue() != memberId.longValue(), "不能给自己转移");


            for (int i = 0; i < items.size(); i++) {
                OmsMemberItem omsMemberItem = items.get(i);

                //判断是否绑定
                BindEnum bind = omsMemberItem.getBind();
                if (bind == BindEnum.BIND) {
                    throw new BizException(ResultCode.ITEM_ALREADY_BIND);
                }

                //外链，判断冷却期
                if (omsMemberItem.getInside() == InsideEnum.OUTSIDE) {
                    OmsTransferConfig config = transferConfigService.getBySpuId(omsMemberItem.getSpuId());
                    LocalDateTime started = omsMemberItem.getStarted();
                    if (started != null) {
                        boolean in = DateUtil.isIn(DateUtil.date(), DateUtils.localDateTimeToDate(started), DateUtils.localDateTimeToDate(started.plusSeconds(config.getOcd())));
                        if (in) {
                            throw new BizException("还在冷却期");
                        }
                    }
                }
                omsMemberItem.setInside(InsideEnum.OUTSIDE);
                omsMemberItem.setTransfer(ItemTransferEnum.NON_TRANSFER);
                omsMemberItem.setStarted(LocalDateTime.now());

                //转移NFT
                omsMemberItem.setMemberId(toMemberId);
            }

            OmsTransferConfig config = transferConfigService.getBySpuId(items.get(0).getSpuId());
            BigDecimal transferConsumeValue = transferConfigService.getTransferConsumeValue(items);
            if (transferConsumeValue.compareTo(BigDecimal.ZERO) > 0) {
                //转赠消耗
                WalletDTO walletDTO = new WalletDTO()
                        .setMemberId(memberId)
                        .setBalance(transferConsumeValue.negate())
                        .setCoinType(config.getType())
                        .setOpType(WalletOpTypeEnum.TRANSFER_CONSUME.getValue())
                        .setRemark("转赠消耗");
                Result result = walletFeignClient.updateBalance(walletDTO);
                if (!Result.isSuccess(result)) {
                    throw new BizException(ResultCode.getValue(result.getCode()));
                }
            }
            //转赠
            updateBatchById(items);
            //删除转赠卡
            removeByIds(transferItems);

            List<OmsItemLog> itemLogList = Lists.newArrayList();
            //增加转移卡和删除转赠卡日志
            for (int i = 0; i < transferItems.size(); i++) {
                OmsMemberItem transferItem = transferItems.get(i);
                OmsMemberItem item = items.get(i);
                OmsItemLog transferItemLog = new OmsItemLog()
                        .setMemberId(memberId)
                        .setMemberFrom(memberId)
                        .setMemberTo(0L)
                        .setSpuId(transferItem.getSpuId())
                        .setItemNo(transferItem.getItemNo())
                        .setItemName(transferItem.getName())
                        .setPicUrl(transferItem.getPicUrl())
                        .setSourceUrl(transferItem.getSourceUrl())
                        .setType(ItemLogTypeEnum.TRANSFER_CONSUME)
                        .setReason("删除转赠卡：" + item.getName() + " 转赠物品：" + item.getName());

                OmsItemLog itemLog = new OmsItemLog()
                        .setMemberId(memberId)
                        .setMemberFrom(memberId)
                        .setMemberTo(toMemberId)
                        .setSpuId(item.getSpuId())
                        .setItemNo(item.getItemNo())
                        .setItemName(item.getName())
                        .setPicUrl(item.getPicUrl())
                        .setSourceUrl(item.getSourceUrl())
                        .setType(ItemLogTypeEnum.TRANSFER_OUTSIDE)
                        .setReason("转赠：" + item.getName() + " 到" + member.getData().getNickName());
                itemLogList.add(transferItemLog);
                itemLogList.add(itemLog);
            }
            itemLogService.saveBatch(itemLogList);
        } catch (Exception e) {
            log.error("", e);
            return false;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return true;
    }

    public IPage page(ItemPageQuery queryParams) {

        Long memberId = MemberUtils.getMemberId();

        LambdaQueryWrapper<OmsMemberItem> wrapper = Wrappers.<OmsMemberItem>lambdaQuery()
                .eq(OmsMemberItem::getMemberId, memberId)
                .eq(queryParams.getType() != null, OmsMemberItem::getType, queryParams.getType())
                .eq(queryParams.getSpuId() != null, OmsMemberItem::getSpuId, queryParams.getSpuId())
                .orderByDesc(OmsMemberItem::getCreated);
        ;

        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<MemberItemVO> list = MemberItemConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public boolean transferById(Long id, ItemTransferAdminForm transferForm) {
//记得加锁
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_TRANSFER_PREFIX + id);
        try {
            lock.lock();
            OmsMemberItem item = getById(id);
            if (item == null) {
                throw new RuntimeException("会员商品不存在");
            }
            if (item.getTransfer() == ItemTransferEnum.TRANSFER) {
                throw new RuntimeException("会员商品已经转移");
            }

            OmsItemLog itemLog = new OmsItemLog()
                    .setMemberId(item.getMemberId())
                    .setMemberFrom(0L)
                    .setMemberTo(item.getMemberId())
                    .setSpuId(item.getSpuId())
                    .setItemNo(item.getItemNo())
                    .setItemName(item.getName())
                    .setPicUrl(item.getPicUrl())
                    .setSourceUrl(item.getSourceUrl())
                    .setType(ItemLogTypeEnum.CHAIN_TRANSFER)
                    .setReason("转移物品：" + item.getHash() + "到" + transferForm.getHash() + "，铸造合约：" + item.getContract());

            MemberItemConverter.INSTANCE.updatePo(transferForm, item);
            updateById(item);

            itemLogService.save(itemLog);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return false;
    }

    @Override
    public boolean transferSourceToTarget(Long itemId, Long source, Long target) {
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_AIRDROP_PREFIX + source + "_" + itemId);
        boolean save = false;
        try {
            lock.lock();

            OmsMemberItem item = getOne(Wrappers.<OmsMemberItem>lambdaQuery().eq(OmsMemberItem::getId, itemId).eq(OmsMemberItem::getMemberId, source));
            if (item == null) {
                throw new RuntimeException("会员商品不存在");
            }
            item.setMemberId(target);
            updateById(item);

            OmsItemLog itemLog = new OmsItemLog()
                    .setMemberId(item.getMemberId())
                    .setMemberFrom(source)
                    .setMemberTo(target)
                    .setSpuId(item.getSpuId())
                    .setItemNo(item.getItemNo())
                    .setItemName(item.getName())
                    .setPicUrl(item.getPicUrl())
                    .setSourceUrl(item.getSourceUrl())
                    .setType(ItemLogTypeEnum.ADMIN_TRANSFER)
                    .setReason("管理员转移物品：" + item.getName());

            save = itemLogService.save(itemLog);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return save;
    }

    @Override
    @GlobalTransactional
    public boolean compound(Long memberId, CompoundDTO compoundDTO) {

        RLock lock = redissonClient.getLock(OmsConstants.ITEM_COMPOUND_PREFIX + memberId);
        boolean save = false;
        try {
            lock.lock();
//            Long compoundId = compoundDTO.getCompoundId();
//            OmsCompoundConfig config = compoundConfigService.getById(compoundId);
//            if (config == null) {
//                throw new BizException("合成配置不存在");
//            }
//            if (config.getStatus() != StatusEnum.ENABLE) {
//                throw new BizException("合成配置已关闭");
//            }
//
//            Long[] itemIds = compoundDTO.getItemIds();
//            //根据itemIds查询商品信息
//            List<OmsMemberItem> items = this.selectUnFreezeItemsByMemberIdAndIds(memberId, itemIds);
//            if (items.size() != itemIds.length) {
//                throw new BizException("商品数量与用户拥有不匹配");
//            }
//            //判断是否满足合成规则
//            List<OmsCompoundConfig.Rule> rules = JSONUtil.toList(config.getData(), OmsCompoundConfig.Rule.class);
//            for (OmsCompoundConfig.Rule rule : rules) {
//                Long spuId = rule.getSpuId();
//                Integer count = rule.getCount();
//                long result = items.stream().filter(item -> item.getSpuId().equals(spuId)).count();
//                if (result < count) {
//                    throw new BizException("商品不足");
//                }
//            }
//
//            //消耗代币
//            BigDecimal typeValue = config.getTypeValue();
//            Integer type = config.getType();
//            if (typeValue.compareTo(BigDecimal.ZERO) > 0) {
//                WalletDTO walletDTO = new WalletDTO()
//                        .setMemberId(memberId)
//                        .setCoinType(type)
//                        .setBalance(typeValue.negate())
//                        .setOpType(WalletOpTypeEnum.COMPOUND_CONSUME.getValue())
//                        .setRemark("合成");
//                Result result = walletFeignClient.updateBalance(walletDTO);
//                if (!Result.isSuccess(result)) {
//                    throw new BizException(ResultCode.getValue(result.getCode()));
//                }
//            }
//
//            //删除商品到ID为1的用户
//            for (OmsMemberItem item : items) {
//                item.setMemberId(1L);
//            }
//            updateBatchById(items);
//
//            //合成后的物品
//            Long spuId = config.getSpuId();
////            Result<SkuInfoDTO> skuInfo = spuFeignClient.getSkuInfo(skuId);
////            Assert.isTrue(Result.isSuccess(skuInfo), "获得sku失败");
////            SkuInfoDTO data = skuInfo.getData();
//            Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(spuId);
//            Assert.isTrue(Result.isSuccess(spuInfo), "获得spu失败");
//            SpuInfoDTO spuData = spuInfo.getData();
//            Long increment = redisTemplate.opsForValue().increment(RedisConstants.OMS_ITEM_NO_SPU_PREFIX + spuData.getId(), 1);
//            OmsMemberItem compoundItem = new OmsMemberItem()
//                    .setMemberId(memberId)
//                    .setStatus(ItemStatusEnum.UN_MINT)
//                    .setItemNo(increment + "")
//                    .setContract(spuData.getContract())
//                    .setHexId(String.format("%064x", System.nanoTime()))
//                    .setName(spuData.getName())
//                    .setType(spuData.getType())
//                    .setPicUrl(spuData.getPicUrl())
//                    .setSpuId(spuData.getId());
////                    .setSkuId(data.getSkuId());
//            save = save(compoundItem);
//
//            //删除商品日志
//            List<OmsItemLog> logs = Lists.newArrayList();
//            for (OmsMemberItem item : items) {
//                OmsItemLog itemLog = new OmsItemLog()
//                        .setMemberId(item.getMemberId())
//                        .setMemberFrom(memberId)
//                        .setMemberTo(GlobalConstants.ADMIN_MEMBER_ID)
//                        .setSpuId(item.getSpuId())
////                        .setSkuId(item.getSkuId())
//                        .setItemNo(item.getItemNo())
//                        .setItemName(item.getName())
//                        .setPicUrl(item.getPicUrl())
//                        .setType(ItemLogTypeEnum.COMPOUND_CONSUME)
//                        .setReason("合成消耗：" + item.getName());
//                logs.add(itemLog);
//            }
//            OmsItemLog itemLog = new OmsItemLog()
//                    .setMemberId(memberId)
//                    .setMemberFrom(0L)
//                    .setMemberTo(memberId)
//                    .setSpuId(compoundItem.getSpuId())
////                    .setSkuId(compoundItem.getSkuId())
//                    .setItemNo(compoundItem.getItemNo())
//                    .setItemName(compoundItem.getName())
//                    .setPicUrl(compoundItem.getPicUrl())
//                    .setType(ItemLogTypeEnum.COMPOUND)
//                    .setReason("合成：" + compoundItem.getName());
//            logs.add(itemLog);
//            save = itemLogService.saveBatch(logs);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return save;
    }

    @Transactional
    public boolean freeze(OmsMemberItem item, ItemLogTypeEnum typeEnum) {

        ItemFreezeStatusEnum freeze = item.getFreeze();
        if (freeze == ItemFreezeStatusEnum.FREEZE) {
            throw new BizException("商品已冻结");
        }
        boolean b = this.updateById(item.setFreeze(ItemFreezeStatusEnum.FREEZE));
        if (b) {
            Long memberId = item.getMemberId();
            OmsItemLog itemLog = new OmsItemLog()
                    .setMemberId(memberId)
                    .setMemberFrom(memberId)
                    .setMemberTo(memberId)
                    .setSpuId(item.getSpuId())
                    .setItemNo(item.getItemNo())
                    .setItemName(item.getName())
                    .setPicUrl(item.getPicUrl())
                    .setSourceUrl(item.getSourceUrl())
                    .setType(typeEnum)
                    .setReason(typeEnum.getLabel() + ":" + item.getName());
            b = itemLogService.save(itemLog);
        }
        return b;
    }

    @Transactional
    public boolean unFreeze(OmsMemberItem item, ItemLogTypeEnum typeEnum) {

        ItemFreezeStatusEnum freeze = item.getFreeze();
        if (freeze != ItemFreezeStatusEnum.FREEZE) {
            throw new BizException("商品未冻结");
        }
        item.setFreezeType(ItemFreezeTypeEnum.COMMON);
        item.setFreeze(ItemFreezeStatusEnum.UN_FREEZE);
        boolean b = this.updateById(item);
        if (b) {
            Long memberId = item.getMemberId();
            OmsItemLog itemLog = new OmsItemLog()
                    .setMemberId(memberId)
                    .setMemberFrom(memberId)
                    .setMemberTo(memberId)
                    .setSpuId(item.getSpuId())
                    .setItemNo(item.getItemNo())
                    .setItemName(item.getName())
                    .setPicUrl(item.getPicUrl())
                    .setSourceUrl(item.getSourceUrl())
                    .setType(typeEnum)
                    .setReason(typeEnum.getLabel() + ":" + item.getName());
            b = itemLogService.save(itemLog);
        }
        return b;
    }

    public Integer itemCount(Long spuId, Integer itemType) {
        //获取物品数量
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<OmsMemberItem> queryWrapper = new LambdaQueryWrapper<OmsMemberItem>()
                .eq(OmsMemberItem::getMemberId, memberId)
                .eq(OmsMemberItem::getSpuId, spuId)
                .ne(OmsMemberItem::getStatus, 1);
        return 0;
    }
}

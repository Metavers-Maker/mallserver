package com.muling.mall.bms.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.muling.common.base.IBaseEnum;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.enums.VCodeTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.VCodeUtils;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.converter.MarketConverter;
import com.muling.mall.bms.enums.*;
import com.muling.mall.bms.event.ChainTransMemberItemEvent;
import com.muling.mall.bms.event.TransMemberItemEvent;
import com.muling.mall.bms.mapper.MarketMapper;
import com.muling.mall.bms.pojo.entity.OmsItemLog;
import com.muling.mall.bms.pojo.entity.OmsMarket;
import com.muling.mall.bms.pojo.entity.OmsMarketConfig;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.form.app.MarketCreateForm;
import com.muling.mall.bms.pojo.form.app.MarketUpdateForm;
import com.muling.mall.bms.pojo.query.app.MarketBuyPageQueryApp;
import com.muling.mall.bms.pojo.query.app.MarketPageQueryApp;
import com.muling.mall.bms.pojo.vo.app.MarketVO;
import com.muling.mall.bms.service.*;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberSimpleDTO;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class MarketServiceImpl extends ServiceImpl<MarketMapper, OmsMarket> implements IMarketService {

    private final IMemberItemService memberItemService;
    private final WalletFeignClient walletFeignClient;
    private final SpuFeignClient spuFeignClient;
    private final MemberFeignClient memberFeignClient;
    private final IItemLogService itemLogService;
    private final RedissonClient redissonClient;
    private final IMarketConfigService marketConfigService;
    private final IBmsBsnSwapService bmsBsnSwapService;
    private final StringRedisTemplate stringRedisTemplate;
    private final Environment env;

    @Override
    public IPage<MarketVO> page(MarketPageQueryApp queryParams) {

        QueryWrapper<OmsMarket> queryWrapper = new QueryWrapper<OmsMarket>()
                .eq("status", MarketStatusEnum.UP)
                .eq(queryParams.getItemId() != null, "item_id", queryParams.getItemId())
                .eq(queryParams.getSpuId() != null, "spu_id", queryParams.getSpuId())
                .eq(queryParams.getSubjectId() != null, "subject_id", queryParams.getSubjectId());
        if (queryParams.getItemType() != null) {
            queryWrapper.eq(queryParams.getItemType() != null, "item_type", IBaseEnum.getEnumByValue(queryParams.getItemType(), ItemTypeEnum.class));
        }
        queryWrapper.orderBy(StrUtil.isNotBlank(queryParams.getOrderBy()), queryParams.isAsc(), queryParams.getOrderBy());
        queryWrapper.orderByDesc("updated");

        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);

        List<MarketVO> list = MarketConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public IPage<MarketVO> pageMe(MarketPageQueryApp queryParams) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<OmsMarket> queryWrapper = new LambdaQueryWrapper<OmsMarket>()
                .eq(OmsMarket::getMemberId, memberId)
                .eq(queryParams.getItemId() != null, OmsMarket::getItemId, queryParams.getItemId())
                .eq(queryParams.getSpuId() != null, OmsMarket::getSpuId, queryParams.getSpuId());
        if (queryParams.getStatus() != null) {
            queryWrapper.eq(queryParams.getStatus() != null, OmsMarket::getStatus, IBaseEnum.getEnumByValue(queryParams.getStatus(), MarketStatusEnum.class));
        }
        if (queryParams.getItemType() != null) {
            queryWrapper.eq(queryParams.getItemType() != null, OmsMarket::getItemType, IBaseEnum.getEnumByValue(queryParams.getItemType(), ItemTypeEnum.class));
        }
        queryWrapper.orderByDesc(OmsMarket::getUpdated);

        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);

        List<MarketVO> list = MarketConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public IPage<MarketVO> buyPageMe(MarketBuyPageQueryApp queryParams) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<OmsMarket> queryWrapper = new LambdaQueryWrapper<OmsMarket>()
                .eq(OmsMarket::getBuyerId, memberId)
                .ne(OmsMarket::getStatus, MarketStatusEnum.CLOSE)
                .eq(queryParams.getItemId() != null, OmsMarket::getItemId, queryParams.getItemId())
                .eq(queryParams.getSpuId() != null, OmsMarket::getSpuId, queryParams.getSpuId());
        if (queryParams.getItemType() != null) {
            queryWrapper.eq(queryParams.getItemType() != null, OmsMarket::getItemType, IBaseEnum.getEnumByValue(queryParams.getItemType(), ItemTypeEnum.class));
        }
        queryWrapper.orderByDesc(OmsMarket::getUpdated);
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        List<MarketVO> list = MarketConverter.INSTANCE.po2voList(page.getRecords());
        return page.setRecords(list);
    }

    @Override
    public List<MarketVO> getListByIds(List<Long> ids) {
        List<OmsMarket> omsMarketList = this.listByIds(ids);
        List<MarketVO> marketVOList = MarketConverter.INSTANCE.po2voList(omsMarketList);
        for (int i = 0; i < marketVOList.size(); i++) {
            Result<MemberDTO> memberDTOResult = memberFeignClient.getMemberById(marketVOList.get(i).getMemberId());
            String nickName = memberDTOResult.getData().getNickName();
            marketVOList.get(i).setMemberName(nickName);
        }
        return marketVOList;
    }

    @Override
    @Transactional
    public boolean save(MarketCreateForm marketForm) {
        //
        Long memberId = MemberUtils.getMemberId();
        //用户验证
        Result<MemberDTO> member = memberFeignClient.getMemberById(memberId);
        Assert.isTrue(Result.isSuccess(member), "用户信息没找到");
        Assert.isTrue(member.getData().getAuthStatus() == 3, "用户未实名");
        //商品验证
        OmsMemberItem item = memberItemService.getById(marketForm.getItemId());
        if (item == null) {
            log.error("商品不存在，itemId={}", marketForm.getItemId());
            throw new BizException("商品不存在");
        }
        if (item.getMemberId().longValue() != memberId.longValue()) {
            throw new BizException("用户没有该物品");
        }
        if (item.getFreeze() == ItemFreezeStatusEnum.FREEZE) {
            throw new BizException("该物品已冻结");
        }
        //锁定物品操作权限
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_OP_PREFIX + marketForm.getItemId());
        try {
            lock.lock();
            //验证码验证
            boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
            boolean jump = false;
            if (isDev) {
                //开发版本
                if (marketForm.getCode().equals("6666")) {
                    jump = true;
                }
            }
            //验证码确认
            if (!jump) {
                boolean b = VCodeUtils.checkVCode(stringRedisTemplate, VCodeTypeEnum.MARKET_SELL, member.getData().getMobile(), marketForm.getCode());
                if (!b) {
                    throw new BizException(ResultCode.VERIFY_CODE_ERROR);
                }
            }
            //查询市场配置
            Long spuId = item.getSpuId();
            Result<SpuInfoDTO> spuInfoDTOResult = spuFeignClient.getSpuInfo(spuId);
            Assert.isTrue(Result.isSuccess(spuInfoDTOResult), "物品SPU不存在");
            SpuInfoDTO supInfo = spuInfoDTOResult.getData();
            OmsMarketConfig marketConfig = marketConfigService.getOne(new LambdaQueryWrapper<OmsMarketConfig>()
                    .eq(OmsMarketConfig::getSpuId, spuId));
            Assert.isTrue(marketConfig != null, "商品寄售未开启");
            Assert.isTrue(marketConfig.getStatus() == StatusEnum.ENABLE, "商品寄售未开放");
            Assert.isTrue(marketConfig.getSpuId().longValue() == spuId.longValue(), "市场配置不匹配");
            Integer coinType = marketConfig.getCoinType();
            BigDecimal fee = marketConfig.getFee();
            //生成市场记录
            OmsMarket market = new OmsMarket()
                    .setMemberId(memberId)
                    .setItemId(marketForm.getItemId())
                    .setItemType(IBaseEnum.getEnumByValue(item.getType(), ItemTypeEnum.class))
                    .setItemNo(item.getItemNo())
                    .setSpuId(item.getSpuId())
                    .setSubjectId(supInfo.getSubjectId())
                    .setName(item.getName())
                    .setPicUrl(item.getPicUrl())
                    .setSourceUrl(item.getSourceUrl())
                    .setPrice(marketForm.getPrice())
                    .setFee(fee)
                    .setTokenId(item.getHexId())
                    .setStatus(MarketStatusEnum.UP);
            //
            boolean f = save(market);
            if (f) {
                //更改物品为市场冻结状态
                item.setFreezeType(ItemFreezeTypeEnum.MARKET);
                f = memberItemService.freeze(item, ItemLogTypeEnum.MARKET_FREEZE);
            }
            return f;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional
    public boolean close(List<Long> itemIds) {
        Long memberId = MemberUtils.getMemberId();
        List<OmsMarket> list = list(Wrappers.<OmsMarket>lambdaQuery()
                .eq(OmsMarket::getMemberId, memberId)
                .in(OmsMarket::getItemId, itemIds));
        if (itemIds.size() != list.size()) {
            throw new BizException("市场列表某些不属于该用户");
        }
        // 非完成和锁定态，可以关闭
        boolean status = update(new LambdaUpdateWrapper<OmsMarket>()
                .eq(OmsMarket::getMemberId, memberId)
                .in(OmsMarket::getItemId, itemIds)
                .eq(OmsMarket::getStatus, MarketStatusEnum.UP)
                .set(OmsMarket::getStatus, MarketStatusEnum.CLOSE));
        if (status) {
            status = memberItemService.update(Wrappers.<OmsMemberItem>lambdaUpdate()
                    .eq(OmsMemberItem::getMemberId, memberId)
                    .in(OmsMemberItem::getId, list.stream().map(OmsMarket::getItemId).collect(Collectors.toList()))
                    .set(OmsMemberItem::getFreeze, ItemFreezeStatusEnum.UN_FREEZE)
                    .set(OmsMemberItem::getFreezeType, ItemFreezeTypeEnum.COMMON))
            ;
        }
        return status;
    }

    @Override
    @Transactional
    public boolean updateById(Long id, MarketUpdateForm marketForm) {
        Long memberId = MemberUtils.getMemberId();
        OmsMarket market = getById(id);
        if (market == null) {
            log.error("市场商品不存在，marketId={}", id);
            throw new BizException("市场商品不存在");
        }

        if (market.getStatus() != MarketStatusEnum.DOWN) {
            throw new BizException("下架状态才可以修改");
        }
        if (market.getMemberId().longValue() != memberId.longValue()) {
            throw new BizException("用户没有该物品");
        }

        market.setPrice(marketForm.getPrice());
        return updateById(market);
    }

    @Override
    @Transactional
    public boolean lockById(Long marketId, Long memberId) {

        RLock lock = redissonClient.getLock(OmsConstants.ITEM_MARKET_PREFIX + marketId);
        try {
            lock.lock();
            //验证商品
            OmsMarket market = getById(marketId);
            if (market == null) {
                log.error("市场商品不存在，marketId={}", marketId);
                throw new BizException("市场商品不存在");
            }
            if (market.getStatus() != MarketStatusEnum.UP) {
                throw new BizException("上架状态才可以锁定物品");
            }
            //验证买方
            Result<MemberSimpleDTO> buyer = memberFeignClient.getSimpleUserById(memberId);
            Assert.isTrue(Result.isSuccess(buyer), "购买用户不存在");
            //购买方锁定订单
            market
                    .setStatus(MarketStatusEnum.LOCK)
                    .setBuyerId(buyer.getData().getId())
                    .setBuyerName(buyer.getData().getNickName())
            ;
            return updateById(market);

        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional
    public boolean unlockById(Long id) {
        OmsMarket market = getById(id);
        if (market == null) {
            log.error("市场商品不存在，marketId={}", id);
            throw new BizException("市场商品不存在");
        }
        if (market.getStatus() != MarketStatusEnum.LOCK) {
            throw new BizException("锁定状态才可以解锁");
        }
        market
                .setStatus(MarketStatusEnum.UP)
                .setBuyerId(null)
                .setBuyerName(null)
        ;
        return updateById(market);
    }

    /**
     * 二级订单支付成功后调用的函数
     */
    @Override
    @Transactional
    public void buyItem(TransMemberItemEvent event) {
        Long memberId = event.getMemberId();
        //目前只处理了一个物品
        Long marketId = event.getOrderItems().get(0).getMarketId();
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_MARKET_PREFIX + marketId);
        try {
            lock.lock();
            OmsMarket market = getById(marketId);
            if (market == null) {
                log.error("市场商品不存在，marketId={}", marketId);
                throw new BizException("市场商品不存在");
            }
            if (market.getStatus() != MarketStatusEnum.LOCK) {
                throw new BizException("锁定状态才可以购买");
            }
//            if (market.getMemberId().longValue() == memberId.longValue()) {
//                throw new BizException("不能购买自己上架的商品");
//            }
            OmsMemberItem item = memberItemService.getById(market.getItemId());
            if (item == null) {
                log.error("商品不存在，itemId={}", market.getItemId());
                throw new BizException("商品不存在");
            }
            Assert.isTrue(item.getFreeze() == ItemFreezeStatusEnum.FREEZE, "该物品未冻结");
            Assert.isTrue(item.getFreezeType() == ItemFreezeTypeEnum.MARKET, "该物品未在市场冻结");
            Result<MemberSimpleDTO> seller = memberFeignClient.getSimpleUserById(market.getMemberId());
            Assert.isTrue(Result.isSuccess(seller), "售卖用户不存在");
            Result<MemberSimpleDTO> buyer = memberFeignClient.getSimpleUserById(memberId);
            Assert.isTrue(Result.isSuccess(buyer), "购买用户不存在");
            //更新市场商品状态
            updateById(market
                    .setStatus(MarketStatusEnum.COMPLETE)
                    .setBuyerId(memberId)
                    .setBuyerName(buyer.getData().getNickName())
                    .setBuyTimed(LocalDateTime.now())
                    .setOrderSn(event.getOrderSn())
            );
            //更新用户商品状态
            memberItemService.unFreeze(item, ItemLogTypeEnum.MARKET_UNFREEZE);
            //更新物品归属
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
            tomorrow = tomorrow.withHour(0).withMinute(0).withSecond(0);
            //
            item.setMemberId(memberId);
            item.setStarted(tomorrow);
            item.setFromType(FromTypeEnum.FROM_MARKET_2_GET);
            item.setSwapPrice(market.getPrice());
            boolean retUpdate = memberItemService.updateById(item);
            if (retUpdate) {
                //创建盲盒开出的上链任务
                bmsBsnSwapService.save(market.getMemberId(), market.getBuyerId(), item, ChainSwapTypeEnum.BBOX);
                //日志
                List<OmsItemLog> itemLogList = Lists.newArrayList();
                //发售日志
                OmsItemLog sellerLog = new OmsItemLog()
                        .setMemberId(market.getMemberId())
                        .setMemberFrom(market.getMemberId())
                        .setMemberTo(memberId)
                        .setSpuId(item.getSpuId())
                        .setItemNo(item.getItemNo())
                        .setItemName(item.getName())
                        .setPicUrl(item.getPicUrl())
                        .setSourceUrl(item.getSourceUrl())
                        .setPrice(market.getPrice())
                        .setType(ItemLogTypeEnum.MARKET_SELL)
                        .setReason("市场售卖：" + item.getName() + " 到" + buyer.getData().getNickName());
                itemLogList.add(sellerLog);
                //购买日志
                OmsItemLog buyerLog = new OmsItemLog()
                        .setMemberId(memberId)
                        .setMemberFrom(market.getMemberId())
                        .setMemberTo(memberId)
                        .setSpuId(item.getSpuId())
                        .setItemNo(item.getItemNo())
                        .setItemName(item.getName())
                        .setPicUrl(item.getPicUrl())
                        .setSourceUrl(item.getSourceUrl())
                        .setPrice(market.getPrice())
                        .setType(ItemLogTypeEnum.MARKET_BUY)
                        .setReason("市场购买：" + item.getName() + " 从" + seller.getData().getNickName());
                itemLogList.add(buyerLog);
                itemLogService.saveBatch(itemLogList);
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 后台系统撤销
     */
    @GlobalTransactional
    @Override
    public boolean adminCancle(Long marketId) {
        //目前只处理了一个物品
        boolean reUpdate = false;
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_MARKET_PREFIX + marketId);
        try {
            lock.lock();
            //检查市场
            OmsMarket market = getById(marketId);
            if (market == null) {
                log.error("市场商品不存在，marketId={}", marketId);
                throw new BizException("市场商品不存在");
            }
            Assert.isTrue(market.getStatus() == MarketStatusEnum.UP, "市场状态不正确，后台无法撤销");
            //检查物品
            OmsMemberItem item = memberItemService.getById(market.getItemId());
            if (item == null) {
                log.error("商品不存在，itemId={}", market.getItemId());
                throw new BizException("商品不存在");
            }
            //关闭市场
            market.setStatus(MarketStatusEnum.CLOSE);
            reUpdate = this.updateById(market);
            if (reUpdate) {
                //物品解冻
                reUpdate = memberItemService.update(Wrappers.<OmsMemberItem>lambdaUpdate()
                        .eq(OmsMemberItem::getId, item.getId())
                        .set(OmsMemberItem::getFreeze, ItemFreezeStatusEnum.UN_FREEZE)
                        .set(OmsMemberItem::getFreezeType, ItemFreezeTypeEnum.COMMON));
                Assert.isTrue(reUpdate, "物品解冻异常");
                //增加物品日志
                OmsItemLog itemLog = new OmsItemLog()
                        .setMemberId(market.getMemberId())
                        .setMemberFrom(market.getMemberId())
                        .setMemberTo(market.getMemberId())
                        .setSpuId(market.getSpuId())
                        .setItemNo(market.getItemNo())
                        .setItemName(market.getName())
                        .setPicUrl(market.getPicUrl())
                        .setSourceUrl(market.getSourceUrl())
                        .setType(ItemLogTypeEnum.MARKET_ADMIN_CANCLE)
                        .setReason("后台撤销：" + market.getName());
                itemLogService.save(itemLog);
            }
            return reUpdate;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    //
}

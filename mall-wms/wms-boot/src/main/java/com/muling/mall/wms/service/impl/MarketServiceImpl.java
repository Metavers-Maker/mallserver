package com.muling.mall.wms.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.enums.StatusEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.api.MemberAuthFeignClient;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.api.MemberInviteFeignClient;
import com.muling.mall.ums.pojo.dto.*;
import com.muling.mall.wms.common.constant.WmsConstants;
import com.muling.mall.wms.common.enums.CoinStatusEnum;
import com.muling.mall.wms.common.enums.MarketStatusEnum;
import com.muling.mall.wms.common.enums.MarketStepEnum;
import com.muling.mall.wms.converter.MarketConverter;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.mapper.WmsMarketMapper;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.muling.mall.wms.pojo.entity.WmsMarket;
import com.muling.mall.wms.pojo.entity.WmsMarketConfig;
import com.muling.mall.wms.pojo.entity.WmsMarketLog;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.pojo.form.app.MarketCreateForm;
import com.muling.mall.wms.pojo.form.app.MarketUpdateForm;
import com.muling.mall.wms.pojo.query.app.MarketBuyPageQueryApp;
import com.muling.mall.wms.pojo.query.app.MarketPageQueryApp;
import com.muling.mall.wms.pojo.query.app.MarketSellPageQueryApp;
import com.muling.mall.wms.pojo.vo.MarketDispatchVO;
import com.muling.mall.wms.pojo.vo.MarketVO;
import com.muling.mall.wms.service.IMarketConfigService;
import com.muling.mall.wms.service.IMarketLogService;
import com.muling.mall.wms.service.IMarketService;
import com.muling.mall.wms.service.IWmsWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@Service
public class MarketServiceImpl extends ServiceImpl<WmsMarketMapper, WmsMarket> implements IMarketService {

    private final IWmsWalletService walletService;
    private final MemberFeignClient memberFeignClient;

    private final MemberInviteFeignClient memberInviteFeignClient;

    private final MemberAuthFeignClient memberAuthFeignClient;
    private final RedissonClient redissonClient;
    private final IMarketConfigService marketConfigService;

    private final IMarketLogService marketLogService;

//    private final IMarketConfigService marketConfigService;
    private final BusinessNoGenerator businessNoGenerator;

    @Override
    public IPage<MarketVO> page(MarketPageQueryApp queryParams) {
        QueryWrapper<WmsMarket> queryWrapper = new QueryWrapper<WmsMarket>()
                .eq("status", MarketStatusEnum.UP)
                .eq("step", MarketStepEnum.INIT);
        queryWrapper.orderBy(StrUtil.isNotBlank(queryParams.getOrderBy()), queryParams.isAsc(), queryParams.getOrderBy());
        queryWrapper.orderByDesc("updated");
        //
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        List<MarketVO> list = MarketConverter.INSTANCE.po2voList(page.getRecords());
        //
        return page.setRecords(list);
    }

    //拉取与自己相关订单
    @Override
    public IPage<MarketVO> pageMe(MarketPageQueryApp queryParams) {
        Long memberId = MemberUtils.getMemberId();
        QueryWrapper<WmsMarket> queryWrapper = new QueryWrapper<WmsMarket>().and(wrapper->{
                    wrapper.eq("member_id", memberId).or().eq("buyer_id", memberId);
                });
        if(queryParams.getStep()!=null) {
            queryWrapper.eq("step", queryParams.getStep());
        }
        if(queryParams.getStatus()!=null) {
            queryWrapper.eq("status", queryParams.getStatus());
        }
        queryWrapper.orderByDesc("updated");
        //
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        List<MarketVO> list = MarketConverter.INSTANCE.po2voList(page.getRecords());
        return page.setRecords(list);
    }

    //订单的另一方
    @Override
    public IPage<MarketVO> buyPageMe(MarketBuyPageQueryApp queryParams) {
        Long memberId = MemberUtils.getMemberId();
        QueryWrapper<WmsMarket> queryWrapper = new QueryWrapper<WmsMarket>()
                .eq("buyer_id", memberId);
        if(queryParams.getStep()!=null) {
            queryWrapper.eq("step", queryParams.getStep());
        }
        if(queryParams.getStatus()!=null) {
            queryWrapper.eq("status", queryParams.getStatus());
        }
        queryWrapper.orderByDesc("updated");
        //
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        List<MarketVO> list = MarketConverter.INSTANCE.po2voList(page.getRecords());
        return page.setRecords(list);
    }

    //发起订单的一方
    @Override
    public IPage<MarketVO> sellPageMe(MarketSellPageQueryApp queryParams) {
        Long memberId = MemberUtils.getMemberId();
        QueryWrapper<WmsMarket> queryWrapper = new QueryWrapper<WmsMarket>()
                .eq("member_id", memberId);
        if(queryParams.getStep()!=null) {
            queryWrapper.eq("step", queryParams.getStep());
        }
        if(queryParams.getStatus()!=null) {
            queryWrapper.eq("status", queryParams.getStatus());
        }
        queryWrapper.orderByDesc("updated");
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        List<MarketVO> list = MarketConverter.INSTANCE.po2voList(page.getRecords());
        return page.setRecords(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createBuy(MarketCreateForm marketForm) {
        //创建一个购买订单
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_LOCK_UPDATE_PREFIX + memberId);
        boolean f = false;
        try {
            lock.lock();
            Integer coinType = marketForm.getCoinType();
            //获取市场配置
            WmsMarketConfig config = marketConfigService.getByCoinType(coinType,0);
            Assert.isTrue(config != null, "市场配置不存在");
            Assert.isTrue(config.getStatus() != StatusEnum.DISABLED, "市场配置不可用");
            //判断数量区间
            BigDecimal balance = marketForm.getBalance();
            if (balance.compareTo(config.getMinBalance()) < 0 || balance.compareTo(config.getMaxBalance()) > 0) {
                throw new BizException("数量不符合要求");
            }
            BigDecimal singlePrice = marketForm.getSinglePrice();
            //判断价格区间
            if (singlePrice.compareTo(config.getMinPrice()) < 0 || singlePrice.compareTo(config.getMaxPrice()) > 0) {
                throw new BizException("单价不符合要求");
            }
            //获取用户信息
            Result<MemberDTO> memberDTOResult = memberFeignClient.getMemberById(memberId);
            Assert.isTrue(memberDTOResult.getData() != null, "目标用户不存在");
            Assert.isTrue(memberDTOResult.getData().getStatus().intValue() == 1, "账号被封禁");
            Assert.isTrue(memberDTOResult.getData().getWechat() != null, "未绑定支付信息");
            Assert.isTrue(memberDTOResult.getData().getWechat().isEmpty() == false, "未绑定支付信息");
            //
            Boolean isVip = false;
            if (memberDTOResult.getData().getExt()!=null && memberDTOResult.getData().getExt().containsKey("black")) {
                if(  memberDTOResult.getData().getExt().get("black").toString().compareTo("1") == 0 ) {
                    isVip = true;
                }
            }
            if (isVip == false) {
                //非VIP用户，有限制
                //判断当日取消次数
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime started = now.withHour(0).withMinute(0).withSecond(0);
                LocalDateTime ended = now.withHour(23).withMinute(59).withSecond(59);
                QueryWrapper<WmsMarket> queryWrapper = new QueryWrapper<WmsMarket>()
                        .eq("member_id", memberId)
                        .eq("step", MarketStepEnum.BUYER_CANCLE.getValue())
                        .ge(started != null, "updated", started)
                        .le(ended != null, "updated", ended);
                IPage page_cancle = this.baseMapper.selectPage(new Page(1, 10), queryWrapper);
                List<MarketVO> list_cancle = MarketConverter.INSTANCE.po2voList(page_cancle.getRecords());
                Assert.isTrue(list_cancle.size()<3, "当日取消次数超过上限");

                //获取当前是否有求购单
                QueryWrapper<WmsMarket> queryWrapper_buy = new QueryWrapper<WmsMarket>()
                        .eq("member_id", memberId)
                        .eq("step", MarketStepEnum.INIT.getValue());
                IPage page_buy = this.baseMapper.selectPage(new Page(1, 10), queryWrapper_buy);
                List<MarketVO> list_buy = MarketConverter.INSTANCE.po2voList(page_buy.getRecords());
                Assert.isTrue(list_buy.size()<1, "求购单已存在，不能发布");
            }
            //录入信息
            JSONObject ext = new JSONObject();
            ext.set("phone",memberDTOResult.getData().getMobile());
            ext.set("wechat",memberDTOResult.getData().getWechat());
            //
            if (memberDTOResult.getData().getWechat()!=null ) {
                JSONObject wechatJson = JSONUtil.parseObj(memberDTOResult.getData().getWechat());
                if(  wechatJson.containsKey("payCode") && wechatJson.get("payCode").toString().isEmpty() == false) {
                    //绑定银行卡
                    ext.set("bindbank",1);
                }
                if(  wechatJson.containsKey("wechat") && wechatJson.get("wechat").toString().isEmpty() == false) {
                    //绑定wx
                    ext.set("bindwechat",1);
                }
                if(  wechatJson.containsKey("alipay") && wechatJson.get("alipay").toString().isEmpty() == false) {
                    //绑定alipay
                    ext.set("bindalipay",1);
                }
            }

            //生成订单ID
            Long ordersn = businessNoGenerator.generateLong(BusinessTypeEnum.ORDER);
            WmsMarket wmsMarket = new WmsMarket();
            wmsMarket.setOrderSn(ordersn);
            wmsMarket.setOpType(0);
            wmsMarket.setMemberId(memberId);
            if (isVip == true) {
                String str="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                StringBuilder sb = new StringBuilder(4);
                for(int i=0;i<4;i++) {
                    char ch=str.charAt(new Random().nextInt(str.length()));
                    sb.append(ch);
                }
                String tmpNickName = memberDTOResult.getData().getNickName();
                String headStr = tmpNickName.substring(0,tmpNickName.length()-4);
                tmpNickName = headStr + sb.toString();
                wmsMarket.setMemberName(tmpNickName);
            } else {
                wmsMarket.setMemberName(memberDTOResult.getData().getNickName());
            }
            wmsMarket.setCoinType(coinType);
            wmsMarket.setBalance(balance);
            wmsMarket.setSinglePrice(marketForm.getSinglePrice());
            wmsMarket.setTotalPrice(marketForm.getSinglePrice().multiply(balance));
            wmsMarket.setStatus(MarketStatusEnum.UP);
            wmsMarket.setStep(MarketStepEnum.INIT);
            wmsMarket.setExt(ext);
            f = this.save(wmsMarket);
            if (f == true) {
                //写日志
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(ordersn);
                log.setMemberId(memberId);
                log.setCoinType(coinType);
                log.setBalance(balance);
                log.setStatus(1);
                log.setRemark("创建买单");
                marketLogService.save(log);
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
        return f;
    }

    @Override
    public Integer buyStar() {
        //
        Long memberId = MemberUtils.getMemberId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime started = now.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime ended = now.withHour(23).withMinute(59).withSecond(59);
        QueryWrapper<WmsMarket> queryWrapper = new QueryWrapper<WmsMarket>()
                .eq("member_id", memberId)
                .eq("step", MarketStepEnum.BUYER_CANCLE.getValue())
                .ge(started != null, "updated", started)
                .le(ended != null, "updated", ended);
        IPage page_cancle = this.baseMapper.selectPage(new Page(1, 10), queryWrapper);
        List<MarketVO> list_cancle = MarketConverter.INSTANCE.po2voList(page_cancle.getRecords());
        Integer starNum = 3 - list_cancle.size();
        if (starNum.intValue()<0) {
            starNum = 0;
        }
        return starNum;
    }

//    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSell(MarketCreateForm marketForm) {
        //创建一个购买订单
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_LOCK_UPDATE_PREFIX + memberId);
        boolean f = false;
        try {
            lock.lock();
            Integer coinType = marketForm.getCoinType();
            BigDecimal balance = marketForm.getBalance();
            WmsMarketConfig config = marketConfigService.getByCoinType(coinType,1);
            Assert.isTrue(config != null, "市场配置不存在");
            Assert.isTrue(config.getStatus() != StatusEnum.DISABLED, "市场配置不可用");
            //判断最大最小
            if (balance.compareTo(config.getMinBalance()) < 0 || balance.compareTo(config.getMaxBalance()) > 0) {
                throw new BizException("金额不符合要求");
            }
            //
            WmsMarket wmsMarket = new WmsMarket();
            boolean billFlag = this.save(wmsMarket);
            if (billFlag == true) {
                //
            }
            //算手续费
            BigDecimal fee = BigDecimal.ZERO;
            if (config.getFee() != null && config.getFee().compareTo(BigDecimal.ZERO) > 0) {
                if (config.getFeeType() == 0) {
                    fee = config.getFee();
                } else if (config.getFeeType() == 1) {
                    fee = config.getFee().multiply(balance);
                }
            }
            BigDecimal totalFee = fee.compareTo(config.getMinFee()) > 0 ? fee : config.getMinFee();
            //
            WalletDTO source = new WalletDTO()
                    .setMemberId(memberId)
                    .setBalance(balance.negate())
                    .setFee(totalFee.negate())
                    .setCoinType(coinType)
                    .setOpType(WalletOpTypeEnum.MARKET_CREATE.getValue())
                    .setRemark(WalletOpTypeEnum.MARKET_CREATE.getLabel());
            f = walletService.updateBalance(source);
            //创建市场日志

        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return f;
    }

    @Override
    public boolean destroyBuy(Long marketId) {
        //买家订单取消
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();
            WmsMarket market = getById(marketId);
            Assert.isTrue(market != null, "市场商品不存在"+marketId);
            Assert.isTrue(market.getStatus() == MarketStatusEnum.UP, "非上架状态");
            Assert.isTrue(market.getStep() ==  MarketStepEnum.INIT, "非初始化状态");
            Assert.isTrue(market.getMemberId().longValue() == memberId.longValue(), "非本人锁定");
            //买方订单取消
            market.setStep(MarketStepEnum.BUYER_CANCLE);
            market.setStatus(MarketStatusEnum.CANCLE);
            b = this.updateById(market);
            if(b == true) {
                //取消订单日志更新
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(market.getOrderSn());
                log.setMemberId(memberId);
                log.setCoinType(market.getCoinType());
                log.setBalance(market.getBalance());
                log.setStatus(7);
                log.setRemark("订单销毁");
                marketLogService.save(log);
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
        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Long id, MarketUpdateForm marketForm) {

        Long memberId = MemberUtils.getMemberId();
        WmsMarket market = getById(id);
        Assert.isTrue(market != null,"市场商品不存在，marketId={}"+id);
        Assert.isTrue(market.getStatus() == MarketStatusEnum.DOWN,"下架状态才可以修改");
        Assert.isTrue(market.getMemberId().longValue() == memberId.longValue(),"不是该用户的订单");
        market
                .setBalance(marketForm.getBalance())
                .setCoinType(marketForm.getCoinType())
                .setSinglePrice(marketForm.getSinglePrice());

        boolean b = updateById(market);
        if (b) {
            //更新市场日志
            WmsMarketLog log = new WmsMarketLog();
            log.setOrderSn(market.getOrderSn());
            log.setMemberId(memberId);
            log.setCoinType(market.getCoinType());
            log.setBalance(market.getBalance());
            log.setStatus(2);
            log.setRemark("订单更新");
            marketLogService.save(log);
        }
        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lock(Long marketId) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();

        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return b;
    }

    @Override
    public boolean buyCancel(Long marketId) {
//        Assert.isTrue(false, "11月21号开放该功能，请联系客服");
        //m卖家订单取消
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();
            WmsMarket market = getById(marketId);
            Assert.isTrue(market != null, "市场商品不存在"+marketId);
            Assert.isTrue(market.getStatus() == MarketStatusEnum.UP, "非上架状态");
            Assert.isTrue(market.getStep() ==  MarketStepEnum.BUYER_LOCK, "非锁定状态");
            Assert.isTrue(market.getBuyerId().longValue() ==  memberId.longValue(), "非本人锁定");
            //计算过期时间（120分钟-2小时）
            LocalDateTime nowtime = LocalDateTime.now();
            nowtime = nowtime.withHour(2);
            Assert.isTrue(nowtime.compareTo(market.getBuyTimed())>0, "订单未过期,不可取消");
            //卖方订单取消
            market.setStep(MarketStepEnum.BUYER_CANCLE);
            market.setStatus(MarketStatusEnum.CANCLE);
            //
            b = this.updateById(market);
            if(b == true && market.getCoinType().intValue() == 0) {
                //退还用户各种费用
                List<WalletDTO> list = Lists.newArrayList();
                //返还建设值+手续费
                WalletDTO walletDTO0 = new WalletDTO();
                walletDTO0.setMemberId(market.getBuyerId());
                walletDTO0.setCoinType(0);
                walletDTO0.setBalance(market.getBalance().add(market.getFee()));
                walletDTO0.setOpType(WalletOpTypeEnum.MARKET_SELL_REBACK.getValue());
                walletDTO0.setRemark(WalletOpTypeEnum.MARKET_SELL_REBACK.getLabel());
                list.add(walletDTO0);
                //返还荣誉值
                WalletDTO walletDTO1 = new WalletDTO();
                walletDTO1.setMemberId(market.getBuyerId());
                walletDTO1.setCoinType(1);
                walletDTO1.setBalance(market.getBalance());
                walletDTO1.setOpType(WalletOpTypeEnum.MARKET_SELL_REBACK.getValue());
                walletDTO1.setRemark(WalletOpTypeEnum.MARKET_SELL_REBACK.getLabel());
                list.add(walletDTO1);
                //更新钱包
                if (!list.isEmpty()) {
                    walletService.updateBalances(list);
                }
                //取消订单日志更新
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(market.getOrderSn());
                log.setMemberId(memberId);
                log.setCoinType(market.getCoinType());
                log.setBalance(market.getBalance());
                log.setStatus(12);
                log.setRemark("卖方订单取消");
                marketLogService.save(log);
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
        return b;
    }

    @Override
    public boolean cancel(Long marketId) {
        //买家订单取消
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();
            WmsMarket market = getById(marketId);
            Assert.isTrue(market != null, "市场商品不存在"+marketId);
            Assert.isTrue(market.getStatus() == MarketStatusEnum.UP, "非上架状态");
            Assert.isTrue(market.getStep() ==  MarketStepEnum.BUYER_LOCK, "非锁定状态");
            Assert.isTrue(market.getMemberId().longValue() ==  memberId.longValue(), "非本人锁定");
            //买方订单取消
            market.setStep(MarketStepEnum.BUYER_CANCLE);
            market.setStatus(MarketStatusEnum.CANCLE);
            b = this.updateById(market);
            if(b == true && market.getCoinType().intValue() == 0) {
                //退还用户各种费用
                //建设操作相关
                List<WalletDTO> list = Lists.newArrayList();
                //返还建设值+手续费
                WalletDTO walletDTO0 = new WalletDTO();
                walletDTO0.setMemberId(market.getBuyerId());
                walletDTO0.setCoinType(0);
                walletDTO0.setBalance(market.getBalance().add(market.getFee()));
                walletDTO0.setOpType(WalletOpTypeEnum.MARKET_SELL_REBACK.getValue());
                walletDTO0.setRemark(WalletOpTypeEnum.MARKET_SELL_REBACK.getLabel());
                list.add(walletDTO0);
                //返还荣誉值
                WalletDTO walletDTO1 = new WalletDTO();
                walletDTO1.setMemberId(market.getBuyerId());
                walletDTO1.setCoinType(1);
                walletDTO1.setBalance(market.getBalance());
                walletDTO1.setOpType(WalletOpTypeEnum.MARKET_SELL_REBACK.getValue());
                walletDTO1.setRemark(WalletOpTypeEnum.MARKET_SELL_REBACK.getLabel());
                list.add(walletDTO1);
                //更新钱包
                if (!list.isEmpty()) {
                    walletService.updateBalances(list);
                }
                //取消订单日志更新
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(market.getOrderSn());
                log.setMemberId(memberId);
                log.setCoinType(market.getCoinType());
                log.setBalance(market.getBalance());
                log.setStatus(4);
                log.setRemark("买家订单取消");
                marketLogService.save(log);
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
        return b;
    }

    @Override
    public boolean commit(Long marketId) {
        //买家确认提交信息
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();
            WmsMarket market = getById(marketId);
            Assert.isTrue(market != null, "市场商品不存在"+marketId);
            Assert.isTrue(market.getStatus() == MarketStatusEnum.UP, "非上架状态");
            Assert.isTrue(market.getStep() ==  MarketStepEnum.BUYER_LOCK, "非锁定状态");
            Assert.isTrue(market.getMemberId().longValue() ==  memberId.longValue(), "非本人锁定");

            //计算过期时间（30分钟）
            LocalDateTime nowtime = LocalDateTime.now();
            nowtime = nowtime.withHour(2);
            Assert.isTrue(nowtime.compareTo(market.getBuyTimed())<0, "订单已过期,不可提交");
            //
            market.setStep(MarketStepEnum.BUYER_COMMIT);

            b = this.updateById(market);
            if( b == true) {
                //买家订单日志确认更新
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(market.getOrderSn());
                log.setMemberId(memberId);
                log.setCoinType(market.getCoinType());
                log.setBalance(market.getBalance());
                log.setStatus(5);
                log.setRemark("买家订单确认");
                marketLogService.save(log);
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
        return b;
    }

    @Override
    public boolean confirm(Long marketId) {
        //卖家确认-释放积分
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();
            WmsMarket market = getById(marketId);
            Assert.isTrue(market != null, "市场商品不存在"+marketId);
            Assert.isTrue(market.getStatus() == MarketStatusEnum.UP, "非上架状态");
            Assert.isTrue(market.getStep() ==  MarketStepEnum.BUYER_COMMIT, "非提交状态");
            Assert.isTrue(market.getBuyerId().longValue() ==  memberId.longValue(), "非本人出售");
            //
            market.setStep(MarketStepEnum.SELLER_COMMIT_COMPLETE);
            market.setStatus(MarketStatusEnum.COMPLETE);
            b = this.updateById(market);
            if (b) {
                //积分转移
                List<WalletDTO> list = Lists.newArrayList();
                WalletDTO walletDTO0 = new WalletDTO()
                        .setMemberId(market.getMemberId())
                        .setBalance(market.getBalance())
                        .setCoinType(market.getCoinType())
                        .setOpType(WalletOpTypeEnum.MARKET_SELL_CONFIRM.getValue())
                        .setRemark(WalletOpTypeEnum.MARKET_SELL_CONFIRM.getLabel());
                list.add(walletDTO0);
                //
                if(market.getCoinType().intValue() == 0) {
                    //如果是建设值，则增加荣誉值购买奖励
                    WalletDTO walletDTO1 = new WalletDTO()
                            .setMemberId(market.getMemberId())
                            .setBalance(market.getBalance())
                            .setCoinType(1)
                            .setOpType(WalletOpTypeEnum.MARKET_SELL_REWARD.getValue())
                            .setRemark(WalletOpTypeEnum.MARKET_SELL_REWARD.getLabel());
                    list.add(walletDTO1);
                }
                //发放奖励
                if (!list.isEmpty()) {
                    b = walletService.updateBalances(list);
                }
            }
            if (b) {
                //卖家订单确认确认日志
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(market.getOrderSn());
                log.setMemberId(memberId);
                log.setCoinType(market.getCoinType());
                log.setBalance(market.getBalance());
                log.setStatus(6);
                log.setRemark("卖家订单确认");
                marketLogService.save(log);
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
        return b;
    }

    @Override
    @Transactional
    public boolean close(List<Long> marketIds) {
        Long memberId = MemberUtils.getMemberId();
        List<WmsMarket> list = list(Wrappers.<WmsMarket>lambdaQuery()
                .eq(WmsMarket::getMemberId, memberId)
                .in(WmsMarket::getId, marketIds));
        if (marketIds.size() != list.size()) {
            throw new BizException("市场列表某些不属于该用户");
        }
        for (WmsMarket market : list) {
            if (market.getStatus() == MarketStatusEnum.COMPLETE
                    || market.getStatus() == MarketStatusEnum.CLOSE
                    || market.getStep() != MarketStepEnum.INIT
            ) {
                throw new BizException(ResultCode.REQUEST_INVALID, "商品状态不对");
            }
        }

        boolean status = update(new LambdaUpdateWrapper<WmsMarket>()
                .eq(WmsMarket::getMemberId, memberId)
                .in(WmsMarket::getId, marketIds)
                .ne(WmsMarket::getStatus, MarketStatusEnum.COMPLETE)
                .set(WmsMarket::getStatus, MarketStatusEnum.CLOSE));
        if (status) {
            //退钱
            List<WmsMarket> wmsMarkets = listByIds(marketIds);
            List<WalletDTO> walletDTOList = Lists.newArrayList();
            for (WmsMarket market : wmsMarkets) {
                WalletDTO source = new WalletDTO()
                        .setMemberId(memberId)
                        .setBalance(market.getBalance())
                        .setCoinType(market.getCoinType())
                        .setOpType(WalletOpTypeEnum.MARKET_CLOSE.getValue())
                        .setRemark(WalletOpTypeEnum.MARKET_CLOSE.getLabel());
                walletDTOList.add(source);
            }
            walletService.updateBalances(walletDTOList);

            //关闭市场日志
        }
        return status;
    }

    //admin相关
    @Override
    public boolean coinFreeze(Long marketId) {
        //客服订单冻结
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();
            WmsMarket market = getById(marketId);
            Assert.isTrue(market != null, "市场商品不存在"+marketId);
            //
            market.setStep(MarketStepEnum.FREEZE);
            market.setStatus(MarketStatusEnum.FREEZE);
            b = this.updateById(market);
            if (b) {
                //客服订单冻结状态
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(market.getOrderSn());
                log.setMemberId(99999999L);
                log.setCoinType(market.getCoinType());
                log.setBalance(market.getBalance());
                log.setStatus(8);
                log.setRemark("客服订单冻结");
                marketLogService.save(log);
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
        return b;
    }

    @Override
    public boolean coinReturn(Long marketId) {
        //客服积分退回
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();
            WmsMarket market = getById(marketId);
            Assert.isTrue(market != null, "市场商品不存在"+marketId);
            Assert.isTrue(market.getStatus() == MarketStatusEnum.FREEZE, "非冻结状态");
            Assert.isTrue(market.getStep() == MarketStepEnum.FREEZE, "非冻结状态");
            //订单取消逻辑
            market.setStep(MarketStepEnum.BUYER_CANCLE);
            market.setStatus(MarketStatusEnum.CANCLE);
            b = this.updateById(market);
            if (b) {
                //退还用户各种费用
                //建设操作相关
                List<WalletDTO> list = Lists.newArrayList();
                //返还建设值+手续费
                WalletDTO walletDTO0 = new WalletDTO();
                walletDTO0.setMemberId(market.getBuyerId());
                walletDTO0.setCoinType(0);
                walletDTO0.setBalance(market.getBalance().add(market.getFee()));
                walletDTO0.setOpType(WalletOpTypeEnum.MARKET_SELL_REBACK.getValue());
                walletDTO0.setRemark(WalletOpTypeEnum.MARKET_SELL_REBACK.getLabel());
                list.add(walletDTO0);
                //返还荣誉值
                WalletDTO walletDTO1 = new WalletDTO();
                walletDTO1.setMemberId(market.getBuyerId());
                walletDTO1.setCoinType(1);
                walletDTO1.setBalance(market.getBalance());
                walletDTO1.setOpType(WalletOpTypeEnum.MARKET_SELL_REBACK.getValue());
                walletDTO1.setRemark(WalletOpTypeEnum.MARKET_SELL_REBACK.getLabel());
                list.add(walletDTO1);
                //更新钱包
                if (!list.isEmpty()) {
                    b = walletService.updateBalances(list);
                }
            }
            if (b) {
                //卖家订单确认确认日志
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(market.getOrderSn());
                log.setMemberId(99999999L);
                log.setCoinType(market.getCoinType());
                log.setBalance(market.getBalance());
                log.setStatus(9);
                log.setRemark("客服积分退回");
                marketLogService.save(log);
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
        return b;
    }

    @Override
    public boolean coinPay(Long marketId) {
        //卖家确认-释放积分
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();
            WmsMarket market = getById(marketId);
            Assert.isTrue(market != null, "市场商品不存在"+marketId);
            Assert.isTrue(market.getStatus() == MarketStatusEnum.FREEZE, "非冻结状态");
            Assert.isTrue(market.getStep() ==  MarketStepEnum.FREEZE, "非冻结状态");
            //
            market.setStep(MarketStepEnum.SELLER_COMMIT_COMPLETE);
            market.setStatus(MarketStatusEnum.COMPLETE);
            b = this.updateById(market);
            if (b) {
                //积分转移
                List<WalletDTO> list = Lists.newArrayList();
                WalletDTO walletDTO0 = new WalletDTO()
                        .setMemberId(market.getMemberId())
                        .setBalance(market.getBalance())
                        .setCoinType(market.getCoinType())
                        .setOpType(WalletOpTypeEnum.MARKET_SELL_CONFIRM.getValue())
                        .setRemark(WalletOpTypeEnum.MARKET_SELL_CONFIRM.getLabel());
                list.add(walletDTO0);
                if(market.getCoinType().intValue() == 0) {
                    //如果是建设值，则增加荣誉值购买奖励
                    WalletDTO walletDTO1 = new WalletDTO()
                            .setMemberId(market.getMemberId())
                            .setBalance(market.getBalance())
                            .setCoinType(1)
                            .setOpType(WalletOpTypeEnum.MARKET_SELL_REWARD.getValue())
                            .setRemark(WalletOpTypeEnum.MARKET_SELL_REWARD.getLabel());
                    list.add(walletDTO1);
                }
                if (!list.isEmpty()) {
                    b = walletService.updateBalances(list);
                }
            }
            if (b) {
                //卖家订单确认确认日志
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(market.getOrderSn());
                log.setMemberId(99999999L);
                log.setCoinType(market.getCoinType());
                log.setBalance(market.getBalance());
                log.setStatus(10);
                log.setRemark("客服积分支付");
                marketLogService.save(log);
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
        return b;
    }

    //
    @Override
    public boolean coinCancle(Long marketId) {
        //客服订单冻结
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_PREFIX + marketId);
        boolean b = false;
        try {
            lock.lock();
            WmsMarket market = getById(marketId);
            Assert.isTrue(market != null, "市场商品不存在"+marketId);
            Assert.isTrue(market.getStatus() == MarketStatusEnum.UP, "非上架状态");
            Assert.isTrue(market.getStep() ==  MarketStepEnum.INIT, "非等待状态");
            //
            market.setStep(MarketStepEnum.BUYER_CANCLE);
            market.setStatus(MarketStatusEnum.CANCLE);
            b = this.updateById(market);
            if (b) {
                //客服订单冻结状态
                WmsMarketLog log = new WmsMarketLog();
                log.setOrderSn(market.getOrderSn());
                log.setMemberId(99999999L);
                log.setCoinType(market.getCoinType());
                log.setBalance(market.getBalance());
                log.setStatus(11);
                log.setRemark("客服订单撤销");
                marketLogService.save(log);
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
        return b;
    }
    //

    @Override
    public MarketDispatchVO coinDispatch(LocalDateTime date) {
        //客服订单冻结
        RLock lock = redissonClient.getLock(WmsConstants.WALLET_MARKET_DISPATCH_PREFIX);
        boolean b = false;
        MarketDispatchVO marketDispatchVO = new MarketDispatchVO();
        try {
            lock.lock();
            BigDecimal totalFee = new BigDecimal(0.0);
            Integer totalNum = 0;
            //查找所有未分红的订单
            Integer pageNum = 1;
            Integer pageSize = 500;
            IPage<WmsMarket> page;
            LocalDateTime started = null;
            LocalDateTime ended = null;
            if (date!=null) {
                started = date.withHour(0).withMinute(0).withSecond(0);
                ended = date.withHour(23).withMinute(59).withSecond(59);
            }
            //一页，一页拉，一直到数据拉空
            do {
                QueryWrapper<WmsMarket> queryWrapper = new QueryWrapper<WmsMarket>()
                        .eq("dispatch", 0)
                        .eq("status", MarketStatusEnum.COMPLETE)
                        .ge(started != null, "updated", started)
                        .le(ended != null, "updated", ended);
                queryWrapper.orderByDesc("created");
                page = this.baseMapper.selectPage(new Page(pageNum, pageSize), queryWrapper);
                int dataSize = page.getRecords().size();
                totalNum += dataSize;
                //
                if ( dataSize > 0) {
                    //计算Fee,并设置已经分红
                    for (WmsMarket wmsMarket : page.getRecords()) {
                        totalFee = totalFee.add(wmsMarket.getFee());
                        this.update(Wrappers.<WmsMarket>lambdaUpdate()
                                .set(WmsMarket::getDispatch, 1)
                                .eq(WmsMarket::getId, wmsMarket.getId()));
                    }
                }
                //
                if (page.getTotal() == 0) {
                    break; //全拉下来后,跳出
                }
                if (page.getTotal() == dataSize) {
                    break; //全拉下来后,跳出
                }
            } while (true);
            //
            marketDispatchVO.setTotalFee(totalFee);
            marketDispatchVO.setTotalNum(totalNum);
            marketDispatchVO.setPageTotal(page.getTotal());
            //
            if (totalFee.compareTo(BigDecimal.valueOf(0.0))>0) {
                //给Star分红
                StarDispatchDTO starDispatch = new StarDispatchDTO();
                starDispatch.setStar(1);
                starDispatch.setFee(totalFee.multiply(BigDecimal.valueOf(0.05)));
                memberInviteFeignClient.starDispatch(starDispatch);
                starDispatch.setStar(2);
                starDispatch.setFee(totalFee.multiply(BigDecimal.valueOf(0.05)));
                memberInviteFeignClient.starDispatch(starDispatch);
                starDispatch.setStar(3);
                starDispatch.setFee(totalFee.multiply(BigDecimal.valueOf(0.2)));
                memberInviteFeignClient.starDispatch(starDispatch);
                starDispatch.setStar(4);
                starDispatch.setFee(totalFee.multiply(BigDecimal.valueOf(0.05)));
                memberInviteFeignClient.starDispatch(starDispatch);
                starDispatch.setStar(5);
                starDispatch.setFee(totalFee.multiply(BigDecimal.valueOf(0.025)));
                memberInviteFeignClient.starDispatch(starDispatch);
                //0.025 到特殊用户
                List<WalletDTO> list = Lists.newArrayList();
                WalletDTO walletDTO0 = new WalletDTO()
                        .setMemberId(2l)
                        .setBalance(totalFee.multiply(BigDecimal.valueOf(0.025)))
                        .setCoinType(0)
                        .setOpType(WalletOpTypeEnum.TEAM_STAR_FEE_REWARD.getValue())
                        .setRemark(WalletOpTypeEnum.TEAM_STAR_FEE_REWARD.getLabel());
                list.add(walletDTO0);
                //如果是建设值，则增加荣誉值购买奖励
                WalletDTO walletDTO1 = new WalletDTO()
                        .setMemberId(2l)
                        .setBalance(totalFee.multiply(BigDecimal.valueOf(0.025)))
                        .setCoinType(1)
                        .setOpType(WalletOpTypeEnum.TEAM_STAR_FEE_REWARD.getValue())
                        .setRemark(WalletOpTypeEnum.TEAM_STAR_FEE_REWARD.getLabel());
                list.add(walletDTO1);
                //
                if (!list.isEmpty()) {
                    walletService.updateBalances(list);
                }
            }
            //
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return marketDispatchVO;
    }
    //
}

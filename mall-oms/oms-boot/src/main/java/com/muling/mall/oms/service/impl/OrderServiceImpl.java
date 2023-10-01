package com.muling.mall.oms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.base.IBaseEnum;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.constant.RedisConstants;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.api.ItemFeignClient;
import com.muling.mall.bms.api.MarketFeignClient;
import com.muling.mall.bms.dto.MarketItemDTO;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.bms.enums.MarketStatusEnum;
import com.muling.mall.bms.event.TransMemberItemEvent;
import com.muling.mall.bms.event.TransPublishMemberItemEvent;
import com.muling.mall.oms.constant.OmsConstants;
import com.muling.mall.oms.converter.OrderItemConverter;
import com.muling.mall.oms.enums.OrderStatusEnum;
import com.muling.mall.oms.enums.OrderTypeEnum;
import com.muling.mall.oms.mapper.OrderMapper;
import com.muling.mall.oms.pojo.dto.OrderItemDTO;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.entity.OmsOrderItem;
import com.muling.mall.oms.pojo.form.*;
import com.muling.mall.oms.pojo.query.OrderPageQuery;
import com.muling.mall.oms.pojo.vo.OrderConfirmVO;
import com.muling.mall.oms.pojo.vo.OrderPageVO;
import com.muling.mall.oms.pojo.vo.OrderSubmitVO;
import com.muling.mall.oms.service.*;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.pms.pojo.dto.CheckPriceDTO;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.muling.common.constant.GlobalConstants.MQ_ORDER_CREATE_EXCHANGE;
import static com.muling.common.constant.GlobalConstants.MQ_ORDER_CREATE_KEY;

/**
 * 订单业务实现类
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OmsOrder> implements IOrderService {

    private final IOrderItemService orderItemService;
    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final MemberFeignClient memberFeignClient;
    private final BusinessNoGenerator businessNoGenerator;
    private final SpuFeignClient spuFeignClient;
    private final ItemFeignClient itemFeignClient;
    private final MarketFeignClient marketFeignClient;
    private final RedissonClient redissonClient;

    /**
     * 订单分页列表
     *
     * @param queryParams
     * @return
     */
    @Transactional
    @Override
    public IPage<OrderPageVO> listOrderPages(OrderPageQuery queryParams) {
        Page<OrderPageVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        List<OrderPageVO> list = this.baseMapper.listOrderPages(page, queryParams);
        page.setRecords(list);
        return page;
    }

    /**
     * 获取首发订单的商品明细
     *
     * @return
     */
    private List<OrderItemDTO> getOrderItems(Long spuId, Integer count, SpuInfoDTO spuInfoDTO) {
        List<OrderItemDTO> orderItems = new ArrayList<>();
        //
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setSpuId(spuInfoDTO.getId());
        orderItemDTO.setPicUrl(spuInfoDTO.getPicUrl());
        orderItemDTO.setSpuName(spuInfoDTO.getName());
        orderItemDTO.setPrice(spuInfoDTO.getPrice());
        orderItemDTO.setMarketId(null);
        orderItemDTO.setCount(count);
        orderItems.add(orderItemDTO);
        //
        return orderItems;
    }

    /**
     * 订单验价，进入结算页面的订单总价和当前所有商品的总价是否一致
     *
     * @param orderTotalAmount 订单总金额
     * @param orderItems       订单商品明细
     * @return true：订单总价和商品总价一致；false：订单总价和商品总价不一致。
     */
    private boolean checkOrderPrice(Long orderTotalAmount, List<OrderItemDTO> orderItems) {
        CheckPriceDTO checkPriceDTO = new CheckPriceDTO();
        List<CheckPriceDTO.CheckSku> checkSkus = orderItems.stream().map(orderFormItem -> {
            CheckPriceDTO.CheckSku checkSku = new CheckPriceDTO.CheckSku();
            checkSku.setSpuId(orderFormItem.getSpuId());
            checkSku.setCount(orderFormItem.getCount());
            return checkSku;
        }).collect(Collectors.toList());
        // 订单总金额
        checkPriceDTO.setOrderTotalAmount(orderTotalAmount);
        // 订单的商品明细
        checkPriceDTO.setCheckSkus(checkSkus);
        // 调用验价接口，比较订单总金额和商品明细总金额，不一致则说明商品价格变动
        Result<Boolean> checkPriceResult = spuFeignClient.checkPrice(checkPriceDTO);
        log.info("checkPriceResult：{}", JSONUtil.toJsonStr(checkPriceResult));
        boolean result = Result.isSuccess(checkPriceResult) && Boolean.TRUE.equals(checkPriceResult.getData());
        Assert.isTrue(result, "验价接口调用失败，建议重新下单");
        return result;
    }

    /**
     * （首发市场签发订单sn）
     */
    @Override
    public OrderConfirmVO confirm(OrderConfirmForm orderConfirmForm) {

        Long memberId = MemberUtils.getMemberId();

        Result<MemberDTO> member = memberFeignClient.getMemberById(memberId);
        Assert.isTrue(Result.isSuccess(member), "用户信息没找到");
        Assert.isTrue(member.getData().getAuthStatus() == 3, "用户未实名");

        //提交订单前,有未处理的订单要先处理
        String omsOrderNoPay = redisTemplate.opsForValue().get(RedisConstants.OMS_ORDER_WITHOUT_PAY_SUFFIX + memberId);
        if (StrUtil.isNotBlank(omsOrderNoPay)) {
            throw new BizException(ResultCode.ORDER_NO_PAY);
        }
        //白名单控制？

        //时间判断
        String spuId = redisTemplate.opsForValue().get(RedisConstants.PMS_SPU_START_PREFIX + orderConfirmForm.getSpuId());
        if (StrUtil.isNotBlank(spuId)) {
            throw new BizException("该商品未开始销售");
        }
        //检测SPU的状态
        Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(orderConfirmForm.getSpuId());
        Assert.isTrue(Result.isSuccess(spuInfo), "商品信息没找到");
        SpuInfoDTO spuInfoDTO = spuInfo.getData();
        if (spuInfoDTO.getPublishStatus() != 2) {
            throw new BizException("商品未发行");
        }
        // 获取原请求线程的参数
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        orderConfirmVO.setPicUrl(spuInfoDTO.getPicUrl());
        orderConfirmVO.setCount(orderConfirmForm.getCount());
        // 获取订单的商品信息
        CompletableFuture<Void> orderItemsCompletableFuture = CompletableFuture.runAsync(() -> {
            // 请求参数传递给子线程
            RequestContextHolder.setRequestAttributes(attributes);
            List<OrderItemDTO> orderItems = this.getOrderItems(orderConfirmForm.getSpuId(), orderConfirmForm.getCount(), spuInfoDTO);
            orderConfirmVO.setOrderItems(orderItems);
        }, threadPoolExecutor);
//        // 获取会员收获地址
//        CompletableFuture<Void> addressesCompletableFuture = CompletableFuture.runAsync(() -> {
//            RequestContextHolder.setRequestAttributes(attributes);
//            List<MemberAddressDTO> addresses = addressFeignService.listCurrMemberAddresses().getData();
//            orderConfirmVO.setAddresses(addresses);
//        }, threadPoolExecutor);
        // 生成唯一 token，防止订单重复提交
        CompletableFuture<Void> orderSnCompletableFuture = CompletableFuture.runAsync(() -> {
            // 请求参数传递给子线程
            RequestContextHolder.setRequestAttributes(attributes);
            String orderSn = businessNoGenerator.generate(BusinessTypeEnum.ORDER);
            orderConfirmVO.setOrderSn(orderSn);
            redisTemplate.opsForValue().set(OmsConstants.ORDER_TOKEN_PREFIX + orderSn, orderSn, Duration.ofSeconds(OmsConstants.ORDER_TOKEN_EXPIRE_TIME));
        }, threadPoolExecutor);
        //
        CompletableFuture.allOf(orderItemsCompletableFuture, orderSnCompletableFuture).join();
        log.info("订单确认响应：{}", orderConfirmVO);
        return orderConfirmVO;
    }

    /**
     * 首发市场订单提交
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public OrderSubmitVO submit(OrderSubmitForm orderSubmitForm) {
        Long memberId = MemberUtils.getMemberId();
        log.info("订单提交数据:[{}]{}", memberId, JSONUtil.toJsonStr(orderSubmitForm));
        // 订单类型校验
        Assert.isTrue(orderSubmitForm.getOrderType() == 0, "秒杀订单类型只能为0");
        // 订单基础信息校验
        List<OrderItemDTO> orderItems = orderSubmitForm.getOrderItems();
        Assert.isTrue(CollectionUtil.isNotEmpty(orderItems), "订单没有商品");
        Assert.isTrue(orderItems.size() == 1, "非1条商品信息");
        // 订单验价
        Long orderTotalAmount = orderSubmitForm.getTotalAmount();
        boolean checkResult = this.checkOrderPrice(orderTotalAmount, orderItems);
        Assert.isTrue(checkResult, "当前页面已过期，请重新刷新页面再提交");
        // 订单重复提交校验
        String orderSn = orderSubmitForm.getOrderSn();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(OmsConstants.RELEASE_LOCK_LUA_SCRIPT, Long.class);
        Long execute = this.redisTemplate.execute(redisScript, Collections.singletonList(OmsConstants.ORDER_TOKEN_PREFIX + orderSn), orderSn);
        Assert.isTrue(execute.equals(1l), "订单不可重复提交:" + orderSn);
        //防止同一个用户提交多个订单并未处理，需要检查redis中是否存在未处理完成的订单
        RLock lock = redissonClient.getLock(OmsConstants.ORDER_SECSKILL_PREFIX + memberId);
        OrderItemDTO orderItemDTO = orderItems.get(0);
        try {
            lock.lock();
            //提交订单前，设置订单正在处理中
            redisTemplate.opsForValue().set(RedisConstants.OMS_ORDER_WITHOUT_PAY_SUFFIX + memberId, orderSubmitForm.getOrderSn());
            // 锁定商品库存
            Result<List<MemberItemDTO>> memberItemDTOList = itemFeignClient.lockPublish(memberId, orderItemDTO.getSpuId(), orderItemDTO.getCount());
            Assert.isTrue(Result.isSuccess(memberItemDTOList), "锁仓失败");
            Assert.isTrue(memberItemDTOList.getData().size() != 0, "商品不足");
            //获取物品ids
            List<String> itemNos = new ArrayList<>();
            memberItemDTOList.getData().forEach(memberItemDTO -> {
                itemNos.add(memberItemDTO.getItemNo().toString());
            });
            String itemNosStr = String.join(",", itemNos);
            try {
                // 1级生成订单
                OmsOrder order = new OmsOrder();
                order.setOrderType(0);
                order.setPicUrl(orderItemDTO.getPicUrl());
                order.setOrderName(orderItemDTO.getSpuName());
                order.setOrderSn(orderSn);
                order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getValue());
                order.setSourceType(OrderTypeEnum.APP.getValue());
                order.setMemberId(memberId);
                order.setReceiveId(0l);
                order.setFeeCount(0l);
                order.setRemark(orderSubmitForm.getRemark());
                order.setPayAmount(orderSubmitForm.getPayAmount());
                order.setTotalQuantity(orderItems.stream().map(OrderItemDTO::getCount).reduce(0, Integer::sum));
                order.setTotalAmount(orderItems.stream().map(item -> item.getPrice() * item.getCount()).reduce(0L, Long::sum));
                boolean result = this.save(order);
                if (result) {
                    //生成订单详情(每一个藏品都生成一条记录)
                    List<OmsOrderItem> orderItemList = Lists.newArrayList();
                    memberItemDTOList.getData().forEach(memberItemDTO -> {
                        Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(memberItemDTO.getSpuId());
                        OmsOrderItem omsOrderItem = new OmsOrderItem();
                        omsOrderItem.setMarketId(null);
                        omsOrderItem.setOrderId(order.getId());
                        omsOrderItem.setType(spuInfo.getData().getType());
                        omsOrderItem.setPrice(spuInfo.getData().getPrice());
                        omsOrderItem.setSpuId(memberItemDTO.getSpuId());
                        omsOrderItem.setPicUrl(memberItemDTO.getPicUrl());
                        omsOrderItem.setSpuName(memberItemDTO.getName());
                        omsOrderItem.setItemNo(memberItemDTO.getItemNo());
                        omsOrderItem.setProductId(spuInfo.getData().getProductId());
                        omsOrderItem.setTotalAmount(spuInfo.getData().getPrice());
                        orderItemList.add(omsOrderItem);
                    });
                    result = orderItemService.saveBatch(orderItemList);
                    if (result) {
                        // 订单超时取消
                        rabbitTemplate.convertAndSend(MQ_ORDER_CREATE_EXCHANGE, MQ_ORDER_CREATE_KEY, orderSn);
                    }
                }
            } catch (Exception e) {
                //锁定物品后的异常
                itemFeignClient.unlockPublish(memberId, orderItemDTO.getSpuId(), itemNosStr, false);
            }
        } catch (Exception e) {
            log.error("秒杀异常" + orderSn + ":", e);
            redisTemplate.opsForValue().set(OmsConstants.ORDER_TOKEN_PREFIX + orderSn, orderSn, Duration.ofSeconds(OmsConstants.ORDER_TOKEN_EXPIRE_TIME));
            redisTemplate.delete(RedisConstants.OMS_ORDER_WITHOUT_PAY_SUFFIX + memberId);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        // 成功响应返回值构建
        OrderSubmitVO submitVO = new OrderSubmitVO();
        submitVO.setOrderSn(orderSn);
        return submitVO;
    }

    /**
     * 2级市场订单申请（签发订单sn）
     *
     * @return
     */
    @Override
    public OrderConfirmVO confirmMarket(OrderM2ConfirmForm orderM2ConfirmForm) {
        //
        Long memberId = MemberUtils.getMemberId();
        Result<MemberDTO> member = memberFeignClient.getMemberById(memberId);
        Assert.isTrue(Result.isSuccess(member), "用户信息没找到");
        Assert.isTrue(member.getData().getAuthStatus() == 3, "用户未实名");
        //提交订单前,有未处理的订单要先处理
        String omsOrderNoPay = redisTemplate.opsForValue().get(RedisConstants.OMS_ORDER_WITHOUT_PAY_SUFFIX + memberId);
        if (StrUtil.isNotBlank(omsOrderNoPay)) {
            throw new BizException(ResultCode.ORDER_NO_PAY);
        }
        //获取市场物品信息
        Result<MarketItemDTO> marketInfo = marketFeignClient.getMarketInfo(orderM2ConfirmForm.getMarketId());
        Assert.isTrue(Result.isSuccess(marketInfo), "市场信息没找到");
        MarketItemDTO marketItemDTO = marketInfo.getData();
        //不能下单自己的物品
        if (marketItemDTO.getMemberId().equals(memberId) == true) {
            throw new BizException("不能购买自己的物品");
        }
        //确认市场物品信息是up状态
        if (marketItemDTO.getStatus().equals(MarketStatusEnum.UP) == false) {
            throw new BizException(ResultCode.ORDER_SELL_OVER);
        }
        // 获取原请求线程的参数
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        // 获取订单的商品信息
        CompletableFuture<Void> orderItemsCompletableFuture = CompletableFuture.runAsync(() -> {
            // 请求参数传递给子线程
            RequestContextHolder.setRequestAttributes(attributes);
            List<OrderItemDTO> orderItems = new ArrayList<>();
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setMarketId(marketItemDTO.getId());
            orderItemDTO.setPicUrl(marketItemDTO.getPicUrl());
            orderItemDTO.setPrice(marketItemDTO.getPrice());
            orderItemDTO.setSpuName(marketItemDTO.getName());
            orderItemDTO.setCount(1);
//            orderItemDTO.setType(marketItemDTO.getItemType().getValue());
            orderItems.add(orderItemDTO);
            orderConfirmVO.setOrderItems(orderItems);
        }, threadPoolExecutor);

//        // 获取会员收获地址
//        CompletableFuture<Void> addressesCompletableFuture = CompletableFuture.runAsync(() -> {
//            RequestContextHolder.setRequestAttributes(attributes);
//            List<MemberAddressDTO> addresses = addressFeignService.listCurrMemberAddresses().getData();
//            orderConfirmVO.setAddresses(addresses);
//        }, threadPoolExecutor);

        // 生成唯一订单号，防止订单重复提交
        CompletableFuture<Void> orderSnCompletableFuture = CompletableFuture.runAsync(() -> {
            // 请求参数传递给子线程
            RequestContextHolder.setRequestAttributes(attributes);
            String orderSn = businessNoGenerator.generate(BusinessTypeEnum.ORDER);
            orderConfirmVO.setOrderSn(orderSn);
            redisTemplate.opsForValue().set(OmsConstants.ORDER_TOKEN_PREFIX + orderSn, orderSn, Duration.ofSeconds(OmsConstants.ORDER_TOKEN_EXPIRE_TIME));
        }, threadPoolExecutor);

        CompletableFuture.allOf(orderItemsCompletableFuture, orderSnCompletableFuture).join();
        log.info("订单确认响应：{}", orderConfirmVO);
        return orderConfirmVO;
    }

    /**
     * 锁定市场条目
     */
    private boolean lockStockMarket(String orderSn, Long marketId, Long memberId) {
        Result result = marketFeignClient.lockMarketItem(marketId, memberId);
        if (!Result.isSuccess(result)) {
            log.error("锁定寄售失败，marketId{}, orderSn：{}", marketId, orderSn);
            throw new BizException("锁定商品库存失败，建议重新下单");
        }
        return true;
    }

    /**
     * 2级订单提交
     */
    @Override
    @GlobalTransactional
    public OrderSubmitVO submitMarket(OrderSubmitForm orderSubmitForm) {
        log.info("订单提交数据:{}", JSONUtil.toJsonStr(orderSubmitForm));
        Long memberId = MemberUtils.getMemberId();
        // 订单基础信息校验
        List<OrderItemDTO> orderItems = orderSubmitForm.getOrderItems();
        Assert.isTrue(orderSubmitForm.getOrderType() == 1, "非2级市场订单，不能提交");
        Assert.isTrue(CollectionUtil.isNotEmpty(orderItems), "订单没有商品");
        Assert.isTrue(orderItems.size() == 1, "订单只能有一个商品");
        Assert.isTrue(orderItems.get(0).getCount() == 1, "订单商品数量只能为1");
        // 订单重复提交校验
        String orderSn = orderSubmitForm.getOrderSn();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(OmsConstants.RELEASE_LOCK_LUA_SCRIPT, Long.class);
        Long execute = this.redisTemplate.execute(redisScript, Collections.singletonList(OmsConstants.ORDER_TOKEN_PREFIX + orderSn), orderSn);
        Assert.isTrue(execute.equals(1l), "订单不可重复提交:" + orderSn);
        //
        OrderItemDTO orderItemDTO = orderItems.get(0);
        Result<MarketItemDTO> marketItemDTOResult = marketFeignClient.getMarketInfo(orderItemDTO.getMarketId());
        Assert.isTrue(Result.isSuccess(marketItemDTOResult), "市场信息未发现");
        MarketItemDTO marketInfo = marketItemDTOResult.getData();
        //
        Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(marketInfo.getSpuId());
        Assert.isTrue(Result.isSuccess(spuInfo), "商品信息未发现");
        // 生成订单
        OmsOrder order;
        try {
            //提交订单前，设置订单正在处理中
            redisTemplate.opsForValue().set(RedisConstants.OMS_ORDER_WITHOUT_PAY_SUFFIX + memberId, orderSubmitForm.getOrderSn());
//            // 订单验价（2级验价）
//            Long orderTotalAmount = orderSubmitForm.getTotalAmount();
//            boolean checkResult = this.checkOrderPrice(orderTotalAmount, orderItems);
//            Assert.isTrue(checkResult, "当前页面已过期，请重新刷新页面再提交");
            // 锁定二级市场物品
            this.lockStockMarket(orderSn, orderItemDTO.getMarketId(), memberId);
            // 计算手续费（保留整数，向下取整数 例如12.1 取12）
            BigDecimal feeCount = marketInfo.getFee().multiply(BigDecimal.valueOf(marketInfo.getPrice())).setScale(0, BigDecimal.ROUND_DOWN);
            // 创建订单
            order = new OmsOrder()
                    .setOrderType(1)
                    .setOrderName(marketInfo.getName())
                    .setOrderSn(orderSn)
                    .setPicUrl(marketInfo.getPicUrl())
                    .setStatus(OrderStatusEnum.PENDING_PAYMENT.getValue())
                    .setSourceType(OrderTypeEnum.APP.getValue())
                    .setMemberId(MemberUtils.getMemberId())
                    .setReceiveId(marketInfo.getMemberId())
                    .setFeeCount(feeCount.longValue())
                    .setRemark(orderSubmitForm.getRemark())
                    .setPayAmount(orderSubmitForm.getPayAmount())
                    .setTotalQuantity(orderItems.stream().map(OrderItemDTO::getCount).reduce(0, Integer::sum))
                    .setTotalAmount(orderItems.stream().map(item -> item.getPrice() * item.getCount()).reduce(0L, Long::sum));
            boolean result = this.save(order);
            //创建收获地址
            // orderDeliveryService.save(orderSubmitForm.getDeliveryAddress(), order);
            if (result) {
                //添加订单明细（只支持一个物品）
                List<OmsOrderItem> orderItemList = new ArrayList<>();
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                BeanUtil.copyProperties(orderItemDTO, omsOrderItem);
                omsOrderItem.setOrderId(order.getId());
                omsOrderItem.setMarketId(marketInfo.getId());
                omsOrderItem.setPicUrl(marketInfo.getPicUrl());
                omsOrderItem.setItemNo(marketInfo.getItemNo());
                omsOrderItem.setType(marketInfo.getItemType().getValue());
                omsOrderItem.setTotalAmount(orderItemDTO.getPrice() * orderItemDTO.getCount());
                orderItemList.add(omsOrderItem);
                //
                result = orderItemService.saveBatch(orderItemList);
                if (result) {
                    // 订单超时取消(延时队列)
                    rabbitTemplate.convertAndSend(MQ_ORDER_CREATE_EXCHANGE, MQ_ORDER_CREATE_KEY, orderSn);
                }
            }
            Assert.isTrue(result, "订单提交失败:" + orderSn);
        } catch (Exception e) {
            log.error("submit", e);
            redisTemplate.opsForValue().set(OmsConstants.ORDER_TOKEN_PREFIX + orderSn, orderSn, Duration.ofSeconds(OmsConstants.ORDER_TOKEN_EXPIRE_TIME));
            redisTemplate.delete(RedisConstants.OMS_ORDER_WITHOUT_PAY_SUFFIX + memberId);
            throw e;
        }
        // 成功响应返回值构建
        OrderSubmitVO submitVO = new OrderSubmitVO();
        submitVO.setOrderId(order.getId());
        submitVO.setOrderSn(order.getOrderSn());
        return submitVO;
    }

    /**
     * 系统自动关闭订单（不允许直接调用，由支付服务调用）
     */
    @Override
    @Transactional
    public boolean closeOrder(Long orderId) {
        OmsOrder order = findById(orderId);
        if (order == null || !OrderStatusEnum.PENDING_PAYMENT.getValue().equals(order.getStatus())) {
            log.info("订单超时取消失败，orderSn:{} ,status:{}", order.getOrderSn(), IBaseEnum.getEnumByValue(order.getStatus(), OrderStatusEnum.class).getLabel());
            return false;
        }
        //设置订单状态
        order.setOutTradeNo(null);
        order.setStatus(OrderStatusEnum.AUTO_CANCEL.getValue());
        boolean b = this.updateById(order);
        if (b) {
            List<OmsOrderItem> omsOrderItemList = orderItemService.getByOrderId(order.getId());
            OmsOrderItem omsOrderItem = omsOrderItemList.get(0);
            if (order.getOrderType() == 0) {
                //自动关闭首发订单 Ids
                List<String> itemNos = new ArrayList<>();
                omsOrderItemList.forEach(orderItem -> {
                    itemNos.add(orderItem.getItemNo());
                });
                String itemNosStr = String.join(",", itemNos);
                itemFeignClient.unlockPublish(order.getMemberId(), omsOrderItem.getSpuId(), itemNosStr, false);
            } else if (order.getOrderType() == 1) {
                //自动关闭2级订单
                if (omsOrderItem != null) {
                    marketFeignClient.unlockMarketItem(omsOrderItem.getMarketId());
                }
            }
            //关闭订单后，清除未处理订单
            redisTemplate.delete(RedisConstants.OMS_ORDER_WITHOUT_PAY_SUFFIX + order.getMemberId());
        }
        return b;
    }

    /**
     * 用户关闭订单（不允许直接调用，由支付服务调用）
     */
    @Override
    public boolean cancelOrder(Long orderId, String reason) {
        Long memberId = MemberUtils.getMemberId();
        log.info("订单超时取消，订单ID：{}", orderId);
        OmsOrder order = findById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (order.getMemberId().longValue() != memberId.longValue()) {
            throw new BizException("订单不属于当前用户");
        }
        if (!OrderStatusEnum.PENDING_PAYMENT.getValue().equals(order.getStatus())) {
            throw new BizException("取消失败，订单状态不支持取消"); // 通过自定义异常，将异常信息抛出由异常处理器捕获显示给前端页面
        }
        // 如果已经有outTradeNo了就先进行关单
        order.setOutTradeNo(null);
        order.setStatus(OrderStatusEnum.USER_CANCEL.getValue());
        order.setReason(reason);
        boolean result = this.updateById(order);
        if (result) {
            List<OmsOrderItem> omsOrderItemList = orderItemService.getByOrderId(order.getId());
            OmsOrderItem omsOrderItem = omsOrderItemList.get(0);
            if (order.getOrderType() == 0) {
                //关闭首发订单 Ids
                List<String> itemIds = new ArrayList<>();
                omsOrderItemList.forEach(orderItem -> {
                    itemIds.add(orderItem.getItemNo());
                });
                String itemIdsStr = String.join(",", itemIds);
                itemFeignClient.unlockPublish(order.getMemberId(), omsOrderItem.getSpuId(), itemIdsStr, false);
            } else if (order.getOrderType() == 1) {
                //关闭2级订单
                marketFeignClient.unlockMarketItem(omsOrderItem.getMarketId());
            } else {
                throw new BizException("错误订单类型");
            }
        }
        //取消订单后，清除未处理订单
        redisTemplate.delete(RedisConstants.OMS_ORDER_WITHOUT_PAY_SUFFIX + order.getMemberId());
        return result;
    }

    @Override
    public boolean refundOrder(Long orderId) {
        log.info("订单退款，订单ID：{}", orderId);
        RLock lock = redissonClient.getLock(OmsConstants.ORDER_ID_PREFIX + orderId);
        boolean result = false;
//        try {
//            lock.lock();
//            OmsOrder order = this.getById(orderId);
//            if (order == null) {
//                throw new BizException("订单不存在");
//            }
//            if (!OrderStatusEnum.APPLY_REFUND.getValue().equals(order.getStatus())) {
//                throw new BizException("退款失败，订单状态不支持退款"); // 通过自定义异常，将异常信息抛出由异常处理器捕获显示给前端页面
//            }
//            String out_refund_no = IdUtils.makeOrderId(order.getMemberId(), "alir_");
//            log.info("商户退款单号拼接完成：{}", out_refund_no);
//            try {
//                AlipayTradeRefundResponse response = alipayService.refund(order.getOutTradeNo(), order.getReason(), order.getPayAmount().intValue(), out_refund_no);
//                if (response.isSuccess()) {
//                    order.setRefundId(response.getTradeNo());
//                    log.info("退款成功，商户单号：{}", response.getTradeNo());
//                }
//            } catch (AlipayApiException e) {
//                log.error(e.getMessage(), e);
//                throw new BizException("阿里退款异常");
//            }
//            order.setStatus(OrderStatusEnum.REFUNDED.getValue());
//            order.setOutRefundNo(out_refund_no);
//            result = this.updateById(order);
//            if (result) {
//                //退款需要发消息删除物品
//            }
//        } finally {
//            //释放锁
//            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
        return result;
    }

    @Override
    public boolean applyRefundOrder(OrderRefundForm orderRefundForm) {
        Long memberId = MemberUtils.getMemberId();
        Long orderId = orderRefundForm.getOrderId();
        RLock lock = redissonClient.getLock(OmsConstants.ORDER_ID_PREFIX + orderId);
        boolean result = false;
        try {
            lock.lock();
            log.info("订单退款，会员ID：{} , 订单ID：{} , 原因：{}", orderId, orderRefundForm.getRefundReason());
            OmsOrder order = this.getById(orderRefundForm.getOrderId());
            if (order == null) {
                throw new BizException("订单不存在");
            }
            if (order.getMemberId().longValue() != memberId.longValue()) {
                throw new BizException("订单不属于当前用户");
            }
            if (!OrderStatusEnum.PAYED.getValue().equals(order.getStatus())) {
                throw new BizException("退款失败，订单状态不支持退款"); // 通过自定义异常，将异常信息抛出由异常处理器捕获显示给前端页面
            }
            order.setStatus(OrderStatusEnum.APPLY_REFUND.getValue());
            order.setReason(orderRefundForm.getRefundReason());
            result = this.updateById(order);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return result;
    }

    public boolean cancelApplyRefundOrder(Long orderId) {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(OmsConstants.ORDER_ID_PREFIX + orderId);
        boolean result = false;
        try {
            lock.lock();
            log.info("取消订单退款，会员ID：{} , 订单ID：{}", orderId);
            OmsOrder order = this.getById(orderId);
            if (order == null) {
                throw new BizException("订单不存在");
            }
            if (order.getMemberId().longValue() != memberId.longValue()) {
                throw new BizException("订单不属于当前用户");
            }
            if (!OrderStatusEnum.APPLY_REFUND.getValue().equals(order.getStatus())) {
                throw new BizException("取消申请退款失败，订单状态不支持取消申请退款"); // 通过自定义异常，将异常信息抛出由异常处理器捕获显示给前端页面
            }
            order.setStatus(OrderStatusEnum.PAYED.getValue());
            result = this.updateById(order);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return result;
    }

    public boolean rejectApplyRefundOrder(Long orderId) {
        RLock lock = redissonClient.getLock(OmsConstants.ORDER_ID_PREFIX + orderId);
        boolean result = false;
        try {
            lock.lock();
            log.info("拒绝申请订单退款，会员ID：{} , 订单ID：{}", orderId);
            OmsOrder order = this.getById(orderId);
            if (order == null) {
                throw new BizException("订单不存在");
            }
            if (!OrderStatusEnum.APPLY_REFUND.getValue().equals(order.getStatus())) {
                throw new BizException("拒绝申请退款失败，订单状态不支持拒绝申请退款"); // 通过自定义异常，将异常信息抛出由异常处理器捕获显示给前端页面
            }
            order.setStatus(OrderStatusEnum.PAYED.getValue());
            result = this.updateById(order);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return result;
    }

    @Transactional
    @Override
    public OmsOrder findById(Long id) {
        return this.baseMapper.findById(id);
    }

    @Transactional
    @Override
    public OmsOrder findByOrderSn(String orderSn) {
        return this.baseMapper.findByOrderSn(orderSn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(Long id) {
        log.info("=======================订单删除，订单ID：{}=======================", id);
        OmsOrder order = this.getById(id);
        if (
                order != null &&
                        !OrderStatusEnum.AUTO_CANCEL.getValue().equals(order.getStatus()) &&
                        !OrderStatusEnum.USER_CANCEL.getValue().equals(order.getStatus())
        ) {
            throw new BizException("订单删除失败，订单不存在或订单状态不支持删除");
        }
        return this.removeById(id);
    }

    public boolean isExistTransactionId(String transactionId) {
        return this.baseMapper.exists(Wrappers.<OmsOrder>lambdaQuery().eq(OmsOrder::getTransactionId, transactionId));
    }

    @Override
    public OmsOrder getByOutTradeNoAndStatus(String outTradeNo, Integer status) {
        QueryWrapper<OmsOrder> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(OmsOrder::getOutTradeNo, outTradeNo).eq(OmsOrder::getStatus, status);
        OmsOrder orderDO = getOne(wrapper);
        return orderDO;
    }

    /**
     * 支付成功-释放物品
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public Map<String, String> payOrderSuccess(OmsOrder order) {
        //
        Map<String, String> params = new HashMap<>();
        order.setStatus(OrderStatusEnum.PAYED.getValue());
        order.setPayTime(new Date());
        updateById(order);
        if (order.getOrderType() == 0) {
            //首发支付成功
            List<OmsOrderItem> orderItems = orderItemService.getByOrderId(order.getId());
            //首发物品转移
            List<TransPublishMemberItemEvent.ItemProperty> list = OrderItemConverter.INSTANCE.po2voList(orderItems);
            TransPublishMemberItemEvent publishMemberItemEvent = new TransPublishMemberItemEvent();
            publishMemberItemEvent.setMemberId(order.getMemberId());
            publishMemberItemEvent.setOrderSn(order.getOrderSn());
            publishMemberItemEvent.setOrderItems(list);
            rabbitTemplate.convertAndSend(GlobalConstants.MQ_ORDER_PAY_SUCCESS_EXCHANGE, GlobalConstants.MQ_ORDER_PAY_SUCCESS_KEY, publishMemberItemEvent);
        } else if (order.getOrderType() == 1) {
            //2级支付成功
            List<OmsOrderItem> orderItems = orderItemService.getByOrderId(order.getId());
            //市场物品转移
            TransMemberItemEvent transMemberItemEvent = new TransMemberItemEvent();
            transMemberItemEvent.setMemberId(order.getMemberId());
            transMemberItemEvent.setOrderSn(order.getOrderSn());
            List<TransMemberItemEvent.ItemProperty> list = OrderItemConverter.INSTANCE.po2voListMarket(orderItems);
            transMemberItemEvent.setOrderItems(list);
            rabbitTemplate.convertAndSend(GlobalConstants.MQ_ITEM_TRANS_SUCCESS_EXCHANGE, GlobalConstants.MQ_ITEM_TRANS_SUCCESS_KEY, transMemberItemEvent);
        }
        redisTemplate.delete(RedisConstants.OMS_ORDER_WITHOUT_PAY_SUFFIX + order.getMemberId());
        params.put("orderSn", order.getOrderSn());
        return params;
    }
    //
}

package com.muling.admin.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.admin.constant.OrderTypeEnum;
import com.muling.admin.constant.SystemConstants;
import com.muling.admin.constant.TimeDimension;
import com.muling.admin.mapper.StatisticsMapper;
import com.muling.admin.pojo.dto.StatisticsDTO;
import com.muling.admin.pojo.dto.StatisticsDetail;
import com.muling.admin.pojo.dto.StatisticsRecordTimeModel;
import com.muling.admin.pojo.entity.Statistics;
import com.muling.admin.service.IStatisticsService;
import com.muling.common.protocol.StatisticsRequest;
import com.muling.common.result.Result;
import com.muling.common.util.BeanUtil;
import com.muling.common.util.DateUtil;
import com.muling.common.util.TimeZoneUtil;
import com.muling.common.util.ValidateUtil;
import com.muling.mall.oms.api.OrderFeignClient;
import com.muling.mall.oms.dto.OrderDTO;
import com.muling.mall.oms.enums.OrderStatusEnum;
import com.muling.mall.pms.api.SkuFeignClient;
import com.muling.mall.pms.pojo.dto.SkuInfoDTO;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import jodd.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import static com.muling.admin.service.impl.StatisticsRecordHelper.YYYY_MM_DD_HH_MM_SS;
import static com.muling.admin.service.impl.StatisticsRecordUtil.getAddedMonthTime;

/**
 * @author chen
 */
@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl extends ServiceImpl<StatisticsMapper, Statistics> implements IStatisticsService {

    private final MemberFeignClient memberFeignClient;

    private final OrderFeignClient orderFeignClient;

    private final SkuFeignClient skuFeignClient;

    private final StatisticsRecordHelper statisticsRecordHelper;

    private final RedissonClient redissonClient;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 5分钟跑一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void calculate() {
        logger.info("[SCHEDULE] Statistics start");
        RLock lock = redissonClient.getLock(SystemConstants.STATISTICS_KEY);
        try {
            lock.lock();

            Long recordTime = System.currentTimeMillis();

            logger.info("[SCHEDULE] Statistics lock success, record time {}", DateUtil.getDate(recordTime, TimeZoneUtil.getTimeZone(28800), YYYY_MM_DD_HH_MM_SS));
            List<Statistics> statistics = new ArrayList<>();

            StatisticsRecordTimeModel model = statisticsRecordHelper.getStatisticsRecordTime(recordTime);

            saveOrUpdateRecord(statistics, model.getDayBeginTime(), model.getDayEndTime(), model.getDayBeginTimeDate().getTime(), TimeDimension.DAY);

            saveOrUpdateRecord(statistics, model.getMonthBeginTime(), model.getMonthEndTime(), model.getMonthBeginTimeDate().getTime(), TimeDimension.MONTH);

            this.saveOrUpdateBatch(statistics);
        } catch (Exception e) {
            logger.error("[SCHEDULE] Statistics error occur", e);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                logger.info("[SCHEDULE] Statistics unlock");
            }
            logger.info("[SCHEDULE] Statistics end");
        }
    }

    public void calculateFix() {
        logger.info("[SCHEDULE] Statistics start");
        RLock lock = redissonClient.getLock(SystemConstants.STATISTICS_KEY);
        try {
            lock.lock();

            Long now = System.currentTimeMillis();

            StatisticsRecordTimeModel model = statisticsRecordHelper.getStatisticsRecordTime(now);

            calculateDaily(model);

            calculateMonth(model);

        } catch (Exception e) {
            logger.error("[SCHEDULE] Statistics error occur", e);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                logger.info("[SCHEDULE] Statistics unlock");
            }
            logger.info("[SCHEDULE] Statistics end");
        }
    }

    private void calculateMonth(StatisticsRecordTimeModel model) {
        List<Statistics> statistics = new ArrayList<>();
        saveOrUpdateRecord(statistics, model.getMonthBeginTime(), model.getMonthEndTime(), model.getMonthBeginTimeDate().getTime(), TimeDimension.MONTH);
        this.saveOrUpdateBatch(statistics);
    }

    private void calculateDaily(StatisticsRecordTimeModel model) {
        for (long time = model.getMonthBeginTimeDate().getTime(); time < model.getMonthEndTimeDate().getTime(); ) {
            StatisticsRecordTimeModel dayModel = statisticsRecordHelper.getStatisticsRecordTime4Day(time);
            logger.info("[SCHEDULE] Statistics lock success, record time {}", DateUtil.getDate(time, TimeZoneUtil.getTimeZone(28800), YYYY_MM_DD_HH_MM_SS));
            List<Statistics> statistics = new ArrayList<>();

            saveOrUpdateRecord(statistics, dayModel.getDayBeginTime(), dayModel.getDayEndTime(), dayModel.getDayBeginTimeDate().getTime(), TimeDimension.DAY);

            this.saveOrUpdateBatch(statistics);
            time += 24 * 60 * 60 * 1000;
        }
    }

    public void saveOrUpdateRecord(List<Statistics> statistics, String begin, String end, Long beginTimeStamp, Integer timeDimension) {
        Result<List<MemberDTO>> membersResult = memberFeignClient.list(begin, end);
        Result<List<OrderDTO>> ordersResult = orderFeignClient.list(begin, end);
        Result<List<SkuInfoDTO>> skuResult = skuFeignClient.list(begin, end);
        List<MemberDTO> memberDTOS = membersResult.getData();
        List<OrderDTO> orderDTOS = ordersResult.getData();
        List<SkuInfoDTO> skuInfoDtos = skuResult.getData();

        Long memberAmount = ValidateUtil.isNotEmpty(memberDTOS) ? (long) memberDTOS.size() : 0L;
        Long orderAmount = 0L;
        Long orderPayedAmount = 0L;
        Long totalSaleAmount = 0L;
        Long l2MarketOrderAmount = 0L;
        Long l2MarketOrderPayedAmount = 0L;
        Long l2MarketTotalSaleAmount = 0L;
        Set<Long> orderedMemberIds = new HashSet<>();
        Set<Long> l2MarketOrderedMemberIds = new HashSet<>();
        if (ValidateUtil.isNotEmpty(orderDTOS)) {
            for (OrderDTO orderDTO : orderDTOS) {
                if (orderDTO.getOrderType().equals(OrderTypeEnum.L1_MARKET.getValue())) {
                    orderAmount++;
                    if (orderDTO.getStatus().equals(OrderStatusEnum.PAYED.getValue())) {
                        orderPayedAmount++;
                        totalSaleAmount += orderDTO.getPayAmount();
                    }
                    orderedMemberIds.add(orderDTO.getMemberId());
                } else if (orderDTO.getOrderType().equals(OrderTypeEnum.L2_MARKET.getValue())) {
                    l2MarketOrderAmount++;
                    if (orderDTO.getStatus().equals(OrderStatusEnum.PAYED.getValue())) {
                        l2MarketOrderPayedAmount++;
                        l2MarketTotalSaleAmount += orderDTO.getPayAmount();
                    }
                    l2MarketOrderedMemberIds.add(orderDTO.getMemberId());
                }
            }
        }

        Long totalSkuStockNum = ValidateUtil.isNotEmpty(skuInfoDtos) ? skuInfoDtos.stream().mapToLong(getSkuInfoDTOToLongFunction()).sum() : 0L;

        Statistics statisticsExist = baseMapper.selectOne(Wrappers.<Statistics>lambdaQuery().eq(Statistics::getRecordTime, beginTimeStamp).eq(Statistics::getTimeDimension, timeDimension));

        saveOrUpdate(statistics, beginTimeStamp, timeDimension, memberAmount, orderAmount, orderPayedAmount, totalSaleAmount, l2MarketOrderAmount,
                l2MarketOrderPayedAmount, l2MarketTotalSaleAmount, (long) orderedMemberIds.size(), (long) l2MarketOrderedMemberIds.size(), totalSkuStockNum, statisticsExist);
    }

    @NotNull
    private ToLongFunction<SkuInfoDTO> getSkuInfoDTOToLongFunction() {
        return o -> (o.getTotalStockNum() == null ? 0L : o.getTotalStockNum()) + (o.getTotalRndStockNum() == null ? 0L : o.getTotalRndStockNum());
    }

    private void saveOrUpdate(List<Statistics> statistics, Long beginTimeStamp, Integer timeDimension, Long memberAmount,
                              Long orderAmount, Long orderPayedAmount, Long totalSaleAmount, Long l2MarketOrderAmount,
                              Long l2MarketOrderPayedAmount, Long l2MarketTotalSaleAmount, Long orderedMemberAmount, Long l2MarketOrderedMemberAmount,
                              Long totalSkuStockNum, Statistics statisticsExist) {
        if (ValidateUtil.isEmpty(statisticsExist)) {
            logger.info("[SCHEDULE] Statistics not exist old statistics, record time {}", beginTimeStamp);
            statistics.add(new Statistics()
                    .setAddedMemberAmount(memberAmount)
                    .setOrderAmount(orderAmount)
                    .setOrderPayedAmount(orderPayedAmount)
                    .setSkuTotalAmount(totalSkuStockNum)
                    .setSaleAmount(totalSaleAmount)
                    .setOrderedMemberAmount(orderedMemberAmount)
                    .setTimeDimension(timeDimension)
                    .setRecordTime(beginTimeStamp)
                    .setL2MarketOrderAmount(l2MarketOrderAmount)
                    .setL2MarketSaleAmount(l2MarketTotalSaleAmount)
                    .setL2MarketOrderPayedAmount(l2MarketOrderPayedAmount)
                    .setL2MarketOrderedMemberAmount(l2MarketOrderedMemberAmount));
        } else {
            logger.info("[SCHEDULE] Statistics exist old statistics, record time {}", beginTimeStamp);
            statistics.add(statisticsExist
                    .setAddedMemberAmount(memberAmount)
                    .setOrderAmount(orderAmount)
                    .setOrderPayedAmount(orderPayedAmount)
                    .setSkuTotalAmount(totalSkuStockNum)
                    .setSaleAmount(totalSaleAmount)
                    .setOrderedMemberAmount(orderedMemberAmount)
                    .setTimeDimension(timeDimension)
                    .setRecordTime(beginTimeStamp)
                    .setL2MarketOrderAmount(l2MarketOrderAmount)
                    .setL2MarketSaleAmount(l2MarketTotalSaleAmount)
                    .setL2MarketOrderPayedAmount(l2MarketOrderPayedAmount)
                    .setL2MarketOrderedMemberAmount(l2MarketOrderedMemberAmount));
        }
    }

    @Override
    public StatisticsDTO query(StatisticsRequest input) {
        if (ValidateUtil.isEmpty(input.getBeginTime()) && ValidateUtil.isEmpty(input.getEndTime())) {
            LambdaQueryWrapper<Statistics> lambdaQueryWrapper = Wrappers.<Statistics>lambdaQuery()
                    .eq(Statistics::getTimeDimension, TimeDimension.MONTH);
            List<Statistics> statisticsList = baseMapper.selectList(lambdaQueryWrapper);
            return new StatisticsDTO()
                    .setTotalGoodsAmount(statisticsList.stream().mapToLong(Statistics::getSkuTotalAmount).sum())
                    .setTotalMemberAmount(statisticsList.stream().mapToLong(Statistics::getAddedMemberAmount).sum())
                    .setTotalOrderAmount(statisticsList.stream().mapToLong(Statistics::getOrderAmount).sum())
                    .setTotalSaleAmount(statisticsList.stream().mapToLong(Statistics::getSaleAmount).sum());
        }

        if (ValidateUtil.isNotEmpty(input.getBeginTime()) && ValidateUtil.isNotEmpty(input.getEndTime())) {
            Assert.notNull(input.getTimeDimension());
            LambdaQueryWrapper<Statistics> lambdaQueryWrapper = Wrappers.<Statistics>lambdaQuery()
                    .ge(Statistics::getRecordTime, input.getBeginTime())
                    .le(Statistics::getRecordTime, input.getEndTime())
                    .eq(Statistics::getTimeDimension, input.getTimeDimension())
                    .orderByAsc(Statistics::getRecordTime);
            List<Statistics> statisticsList = baseMapper.selectList(lambdaQueryWrapper);

            Map<Long, Statistics> statisticsRecordMap = statisticsList.stream().collect(Collectors.toMap(Statistics::getRecordTime, Function.identity()));

            List<StatisticsDetail> list = new LinkedList<>();
            int count = StatisticsRecordUtil.getCount(input.getBeginTime(), input.getEndTime(), input.getTimeDimension());
            Long timestamp = input.getBeginTime();
            for (int i = 0; i < count; i++) {

                Statistics statistics = statisticsRecordMap.get(timestamp);
                if (ValidateUtil.isEmpty(statistics)) {
                    statistics = new Statistics()
                            .setRecordTime(timestamp)
                            .setAddedMemberAmount(0L)
                            .setOrderAmount(0L)
                            .setOrderPayedAmount(0L)
                            .setSkuTotalAmount(0L)
                            .setSaleAmount(0L)
                            .setOrderedMemberAmount(0L)
                            .setL2MarketOrderAmount(0L)
                            .setL2MarketOrderedMemberAmount(0L)
                            .setL2MarketOrderPayedAmount(0L)
                            .setL2MarketSaleAmount(0L)
                            .setTimeDimension(input.getTimeDimension());
                }

                StatisticsDetail statisticsDetail = BeanUtil.copy(statistics, StatisticsDetail.class)
                        .setTimestamp(statistics.getRecordTime())
                        .setTimeStr(DateUtil.getDate(statistics.getRecordTime(), TimeZoneUtil.getTimeZone(28800), YYYY_MM_DD_HH_MM_SS))
                ;

                list.add(statisticsDetail);

                if (input.getTimeDimension().equals(TimeDimension.DAY)) {
                    timestamp += TimeUtil.MILLIS_IN_DAY;
                    continue;
                }
                if (input.getTimeDimension().equals(TimeDimension.MONTH)) {
                    timestamp = getAddedMonthTime(timestamp);
                }
            }

            list.sort(Comparator.comparing(StatisticsDetail::getTimestamp));

            return new StatisticsDTO()
                    .setTotalGoodsAmount(list.stream().mapToLong(StatisticsDetail::getSkuTotalAmount).sum())
                    .setTotalMemberAmount(list.stream().mapToLong(StatisticsDetail::getAddedMemberAmount).sum())
                    .setTotalOrderAmount(list.stream().mapToLong(StatisticsDetail::getOrderAmount).sum())
                    .setTotalSaleAmount(list.stream().mapToLong(StatisticsDetail::getSaleAmount).sum())
                    .setL2MarketTotalOrderAmount(list.stream().mapToLong(StatisticsDetail::getL2MarketOrderAmount).sum())
                    .setL2MarketTotalSaleAmount(list.stream().mapToLong(StatisticsDetail::getL2MarketSaleAmount).sum())
                    .setList(list);
        }

        return null;
    }
}

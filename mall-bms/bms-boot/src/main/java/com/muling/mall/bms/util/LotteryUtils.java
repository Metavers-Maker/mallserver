package com.muling.mall.bms.util;

import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotteryUtils {

    public static Prize open(List<Prize> items) {
        List<Double> originRates = originRates(items);
        int originIndex = LotteryUtils.lottery(originRates);
        Prize item = items.get(originIndex);
        return item;
    }

    /**
     * 概率列表
     *
     * @param items
     * @return
     */
    public static List<Double> originRates(List<Prize> items) {
        List<Double> originRates = new ArrayList<>();
        for (Prize item : items) {
            double probability = item.getProb();
            originRates.add(probability);
        }
        return originRates;
    }

    /**
     * 抽奖算法
     *
     * @param originRates 原始的概率列表，保证顺序和实际物品对应
     * @return 物品的索引
     */
    public static int lottery(List<Double> originRates) {
        // 计算总概率，这样可以保证不一定总概率是1
        double sumRate = 0d;
        for (double rate : originRates) {
            sumRate += rate;
        }
        // 计算每个物品在总概率的基础下的概率情况
        List<Double> sortOriginRates = new ArrayList<>();
        double tempSumRate = 0d;
        for (double rate : originRates) {
            tempSumRate += rate;
            sortOriginRates.add(tempSumRate / sumRate);
        }
        // 根据区块值来获取抽取到的物品索引
        double nextDouble = Math.random();
        sortOriginRates.add(nextDouble);
        Collections.sort(sortOriginRates);
        return sortOriginRates.indexOf(nextDouble);
    }

    public static void main(String[] args) {
//        Prize prize = new Prize();
//        prize.setSkuId(1L);
//        prize.setSpuId(1L);
//        prize.setName("10%几率");
//        prize.setProb(0.003);
//
//        Prize prize2 = new Prize();
//        prize2.setCoinType(0);
//        prize2.setCoinCount(2);
//        prize2.setType(1);
//        prize2.setName("90%几率");
//        prize2.setProb(0.997);
//
//        List list = new ArrayList();
//        list.add(prize);
//        list.add(prize2);
//        System.out.println(JSONUtil.toJsonStr(list));
//
//        String jsonStr = JSONUtil.toJsonStr(list);
//        List<Prize> prizes = JSONUtil.toList(jsonStr, Prize.class);
//        System.out.println(prizes);
//        for (int i = 0; i < 100; i++) {
//            Prize open = LotteryUtils.open(list);
//            System.out.println(open);
//        }
    }
}

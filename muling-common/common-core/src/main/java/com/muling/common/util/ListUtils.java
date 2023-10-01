package com.muling.common.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    /**
     * 将源List按照指定元素数量拆分为多个List
     *
     * @param source       源List
     * @param splitItemNum 每个List中元素数量
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int splitItemNum) {
        List<List<T>> result = new ArrayList<List<T>>();

        if (source != null && source.size() > 0 && splitItemNum > 0) {
            if (source.size() <= splitItemNum) {
                // 源List元素数量小于等于目标分组数量
                result.add(source);
            } else {
                // 计算拆分后list数量
                int splitNum = (source.size() % splitItemNum == 0) ? (source.size() / splitItemNum) : (source.size() / splitItemNum + 1);

                List<T> value = null;
                for (int i = 0; i < splitNum; i++) {
                    if (i < splitNum - 1) {
                        value = source.subList(i * splitItemNum, (i + 1) * splitItemNum);
                    } else {
                        // 最后一组
                        value = source.subList(i * splitItemNum, source.size());
                    }
                    result.add(value);
                }
            }
        }
        return result;
    }

    public <T> List<List<T>> averageAssign2(List<T> source, int splitNum) {

        List<List<T>> result = new ArrayList<List<T>>();
        //循环下标
        int i = 0;
        //集合总数 / 分割每段数
        for (; i < source.size() / splitNum; i++) {
            //splitNum代表每段分割的子集合数据条数
            result.add(source.subList(i * splitNum, (i + 1) * splitNum));
        }
        //如果有余数，再将剩下的追加进去
        if (source.size() % splitNum != 0) {
            result.add(source.subList(i * splitNum, i * splitNum + source.size() % splitNum));
        }
        return result;
    }
}

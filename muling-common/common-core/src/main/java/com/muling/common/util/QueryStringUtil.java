package com.muling.common.util;

import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QueryStringUtil {

    /**
     * 获得查询字符串
     *
     * @param param
     * @return
     */
    public static String getQueryString(Map<String, ? extends Object[]> param, String exclude) {

        return getQueryString(param, exclude, null);
    }


    /**
     * 获得查询字符串
     *
     * @param param
     * @return
     */
    public static String getQueryString(Map<String, ? extends Object[]> param) {

        return getQueryString(param, null);
    }

    /**
     * 获得查询字符串
     *
     * @param param
     * @param exclude
     * @param maskField
     * @return
     */
    public static String getQueryStringWithMask(Map<String, ? extends Object[]> param, String exclude, String maskField) {

        return getQueryString(param, exclude, maskField);
    }

    /**
     * 获得查询字符串
     *
     * @param param
     * @param maskField
     * @return
     */
    public static String getQueryStringWithMask(Map<String, ? extends Object[]> param, String maskField) {

        return getQueryString(param, null, maskField);
    }

    /**
     * 获得查询字符串
     *
     * @param param
     * @param exclude
     * @param maskField
     * @return
     */
    private static String getQueryString(Map<String, ? extends Object[]> param, String exclude, String maskField) {

        if (ValidateUtil.isEmpty(param)) {
            return StringPool.EMPTY;
        }

        List<String> keys = new ArrayList(param.keySet());

        Collections.sort(keys);

        List<String> params = new ArrayList<>();
        for (String key : keys) {
            if (!key.equals(exclude)) {
                if (key.equals(maskField)) {
                    params.add(key + "=***");
                } else {
                    // 仅计算参数不为空的
                    String value = StringUtil.join(param.get(key));
                    if (ValidateUtil.isNotEmpty(value)) {
                        params.add(key + StringPool.EQUALS + StringUtil.join(param.get(key)));
                    }
                }
            }
        }

        return StringUtil.join(params, StringPool.AMPERSAND);
    }
}

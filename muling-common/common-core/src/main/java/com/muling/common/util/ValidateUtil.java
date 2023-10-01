package com.muling.common.util;


import jodd.util.StringUtil;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;


public class ValidateUtil {

    /**
     * 空校验
     * <li> 如果value为null返回true
     * <li> 如果value是String类型，如果其中包含的是空白字符则返回true
     * <li> 如果value是Collection类型，如果内容为空则返回true
     * <li> 如果value是Map类型，如果内容为空则返回true
     * <li> 如果value是Array类型，如果长度为0则返回true
     *
     */
    public static boolean isEmpty(Object value) {

        return !isNotEmpty(value);
    }

    /**
     * 不为空校验
     * <li> 如果value为null返回false
     * <li> 如果value是String类型，如果其中包含的是空白字符则返回false
     * <li> 如果value是Collection类型，如果内容为空则返回false
     * <li> 如果value是Map类型，如果内容为空则返回false
     * <li> 如果value是Array类型，如果长度为0则返回false
     *
     */
    public static boolean isNotEmpty(Object value) {

        if (value == null) {
            return false;
        }

        if (value instanceof String) {
            return !StringUtil.isBlank((CharSequence) value);
        }

        if (value instanceof Collection) {
            return !((Collection<?>) value).isEmpty();
        }

        if (value instanceof Map) {
            return !((Map<?, ?>) value).isEmpty();
        }

        if (value.getClass().isArray()) {
            return Array.getLength(value) != 0;
        }
        return true;
    }



    /**
     * 是否满足正则表达式
     *
     * @param str
     * @param reg
     * @return
     */
    public static boolean matchReg(String str, String reg) {

        if (str == null || reg == null) {
            return false;
        }

        return matchReg(str, Pattern.compile(reg));
    }

    /**
     * 是否满足正则表达式
     *
     * @param str
     * @param pattern
     * @return
     */
    public static boolean matchReg(String str, Pattern pattern) {

        if (str == null || pattern == null) {
            return false;
        }

        return pattern.matcher(str).find();
    }

    /**
     * 是否满足正则表达式
     *
     * @param str
     * @param reg
     * @return
     */
    public static boolean matchAllReg(String str, String reg) {

        if (str == null || reg == null) {
            return false;
        }

        return matchReg(str, Pattern.compile(reg));
    }

    /**
     * 是否满足正则表达式
     *
     * @param str
     * @param pattern
     * @return
     */
    public static boolean matchAllReg(String str, Pattern pattern) {

        if (str == null || pattern == null) {
            return false;
        }

        return pattern.matcher(str).matches();
    }

    public static boolean isValidDate(String date, DateFormat format) {

        //设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
        format.setLenient(false);
        try {
            format.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    /**
     * !=null && true
     *
     * @param b
     * @return
     */
    public static Boolean isTrue(Boolean b) {

        return b != null && b;
    }

    /**
     * !=null && false
     *
     * @param b
     * @return
     */
    public static Boolean isFalse(Boolean b) {

        return b != null && !b;
    }
}
package com.muling.common.util;

import jodd.util.StringPool;

import java.util.TimeZone;

public class TimeZoneUtil {

    private static final String GMT = "GMT";

    /**
     * UTC零时区对应的ZoneId标识
     */
    public static final String TIME_ZONE_UTC_ZERO = GMT + "00:00";

    /**
     * 根据时区偏移量获取Java中的TimeZone对象
     *
     * @param utcOffset 单位：s
     * @return
     */
    public static TimeZone getTimeZone(int utcOffset) {

        return TimeZone.getTimeZone(parseGMTFormat(utcOffset));
    }

    /**
     * 获取UTC零时区Java中的TimeZone对象
     *
     * @return
     */
    public static TimeZone getZeroTimeZone() {

        return TimeZone.getTimeZone(TIME_ZONE_UTC_ZERO);
    }

    /**
     * 解析时区偏移量
     *
     * @param offset 单位：s
     * @return 时区的GMT表现形式，不带夏令时，例如 GMT+08:00
     */
    public static String parseGMTFormat(int offset) {

        return GMT + parseOffset(parseSign(offset), parseHour(Math.abs(offset)),
                parseMinute(Math.abs(offset)));
    }

    /**
     * 解析时区偏移量
     *
     * @return 时区的GMT表现形式，不带夏令时，例如 +08:00
     */
    public static String parseGMTOffset(int offset) {

        return parseOffset(parseSign(offset), parseHour(Math.abs(offset)), parseMinute(Math.abs(offset)));
    }

    // ============================== private method ==============================

    private static String parseSign(int offset) {
        return offset >= 0 ? StringPool.PLUS : StringPool.DASH;
    }

    private static String parseHour(int absOffset) {

        String hour = String.valueOf(absOffset / 3600); // 将偏移量转化为小时（小数去除不要）

        if (hour.length() == 1) {
            hour = StringPool.ZERO + hour;
        }

        return hour;
    }

    private static String parseMinute(int absOffset) {

        String sDecimalsZone = String.valueOf((absOffset % 3600) / 60); // 取出分钟数

        if (sDecimalsZone.length() == 1) {
            sDecimalsZone = sDecimalsZone + StringPool.ZERO;
        } else if (sDecimalsZone.length() > 3) {
            sDecimalsZone = sDecimalsZone.substring(0, 2);
        }

        return sDecimalsZone;
    }

    private static String parseOffset(String sign, String hour, String minute) {
        return sign + hour + StringPool.COLON + minute;
    }
}

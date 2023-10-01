package com.muling.common.util;

import jodd.time.TimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeFormatUtil {

    public static int resolveDayCount(Long beginTime, Long endTime) {

        return Math.round(((float) endTime - beginTime) / TimeUtil.MILLIS_IN_DAY);
    }

    public static int resolveMonthCount(Long beginTime, Long endTime) {

        TimeZone timeZone = TimeZoneUtil.getTimeZone(28800);

        Integer endMonth = DateUtil.getMonth(DateUtil.getDate(endTime, timeZone), timeZone);
        Integer beginMonth = DateUtil.getMonth(DateUtil.getDate(beginTime, timeZone), timeZone);

        Integer endYear = DateUtil.getYear(DateUtil.getDate(endTime, timeZone), timeZone);
        Integer beginYear = DateUtil.getYear(DateUtil.getDate(beginTime, timeZone), timeZone);

        return (endMonth - beginMonth + 1) + (endYear - beginYear) * 12;
    }

    public static Long getFirstDayOfMonth(long timeTamp) {

        Date date = DateUtil.getDate(timeTamp);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND, 0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis();
    }

    /**
     * 获取当天的某个整点时间戳
     *
     * @return 当天的某个整点时间戳
     */
    public static long getTodayTime(int value) {

        //设置时区
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, value);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 当前的小时数
     *
     * @return 当前的小时数
     */
    public static int getCurrentHour() {

        GregorianCalendar gCal = new GregorianCalendar();

        return gCal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 当前的分钟数
     *
     * @return 当前的分钟数
     */
    public static int getCurrentMinute() {

        GregorianCalendar gCal = new GregorianCalendar();

        return gCal.get(Calendar.MINUTE);
    }


    /**
     * 获取当天某一时刻的时间戳
     *
     * @param hour 时
     * @param minute 分
     * @param seconds 秒
     * @return
     */
    public static long getTimestampWithMomentOfDay(int hour, int minute, int seconds) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, seconds);

        return calendar.getTimeInMillis();
    }

}

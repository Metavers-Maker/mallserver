package com.muling.admin.service.impl;

import com.muling.admin.constant.TimeDimension;
import com.muling.admin.pojo.dto.StatisticsRecordTimeModel;
import com.muling.common.util.DateUtil;
import com.muling.common.util.TimeZoneUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
@Component
public class StatisticsRecordHelper  {

    public static final Integer CHINA_ZONE_ID = 8 * 60 * 60;
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    public Integer getUserOnlineRecordSequence(Long recordTime, Long dayBeginTime) {

        Long duration = recordTime - dayBeginTime;

        BigDecimal dur = new BigDecimal(duration);
        BigDecimal unit = new BigDecimal(10 * 60 * 1000);

        return (int) Math.floor(dur.divide(unit, BigDecimal.ROUND_HALF_DOWN).doubleValue());
    }

    public boolean isWeekend(Long recordTime) {

        TimeZone timeZone = TimeZoneUtil.getTimeZone(CHINA_ZONE_ID);

        Calendar calendar = DateUtil.getCalendar(recordTime, timeZone);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    public StatisticsRecordTimeModel getStatisticsRecordTime(Long timeStamp) {

        return new StatisticsRecordTimeModel()
                .setDayBeginTimeDate(getDayBeginTimeDate(timeStamp))
                .setMonthBeginTimeDate(getMonthBeginTimeDate(timeStamp))
                .setDayEndTimeDate(getDayEndTimeDate(timeStamp))
                .setMonthEndTimeDate(getMonthEndTimeDate(timeStamp));
    }

    public StatisticsRecordTimeModel getStatisticsRecordTime4Day(Long timeStamp) {

        return new StatisticsRecordTimeModel()
                .setDayBeginTimeDate(getDayBeginTimeDate(timeStamp))
                .setDayEndTimeDate(getDayEndTimeDate(timeStamp));
    }

    public Date getBeginTimeStamp(Long timeStamp, Integer timeDimension) {

        if (timeDimension.equals(TimeDimension.DAY)) {
            return getDayBeginTimeDate(timeStamp);
        }

        return getMonthBeginTimeDate(timeStamp);
    }

    public Date getEndTimeDate(Long timeStamp, Integer timeDimension) {

        if (timeDimension.equals(TimeDimension.DAY)) {
            return getDayEndTimeDate(timeStamp);
        }

        return getMonthEndTimeDate(timeStamp);
    }

    public Date getDayBeginTimeDate(Long timeStamp) {

        TimeZone timeZone = TimeZoneUtil.getTimeZone(CHINA_ZONE_ID);

        Calendar calendar = DateUtil.getCalendar(timeStamp, timeZone);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public Date getDayEndTimeDate(Long timeStamp) {

        TimeZone timeZone = TimeZoneUtil.getTimeZone(CHINA_ZONE_ID);

        Calendar calendar = DateUtil.getCalendar(timeStamp, timeZone);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public String[] getWeekTimeStamp(Long timeStamp) {

        TimeZone timeZone = TimeZoneUtil.getTimeZone(CHINA_ZONE_ID);

        Calendar calendar = DateUtil.getCalendar(timeStamp, timeZone);
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 1));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String startTime = DateUtil.format(calendar.getTime(), YYYY_MM_DD_HH_MM_SS);
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        String endTime = DateUtil.format(calendar.getTime(), YYYY_MM_DD_HH_MM_SS);

        return new String[]{startTime, endTime};
    }

    public Date getMonthBeginTimeDate(Long timeStamp) {

        TimeZone timeZone = TimeZoneUtil.getTimeZone(CHINA_ZONE_ID);

        Calendar calendar = DateUtil.getCalendar(timeStamp, timeZone);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();

    }

    public Date getMonthEndTimeDate(Long timeStamp) {

        TimeZone timeZone = TimeZoneUtil.getTimeZone(CHINA_ZONE_ID);

        Calendar calendar = DateUtil.getCalendar(timeStamp, timeZone);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }
}

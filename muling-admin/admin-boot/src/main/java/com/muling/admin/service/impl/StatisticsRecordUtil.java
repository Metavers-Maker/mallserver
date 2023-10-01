package com.muling.admin.service.impl;


import com.muling.admin.constant.TimeDimension;
import com.muling.common.util.DateUtil;
import com.muling.common.util.StringPool;
import com.muling.common.util.TimeZoneUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.muling.common.util.TimeFormatUtil.resolveDayCount;
import static com.muling.common.util.TimeFormatUtil.resolveMonthCount;

public class StatisticsRecordUtil {

    public static String percentage(Long divisor, Long dividend) {

        if (dividend == null || dividend == 0L) {
            return StringPool.ZERO;
        }

        DecimalFormat df = new DecimalFormat("0.0");

        return df.format((float)divisor * 100.0 / dividend);
    }

    public static Double divide(Long divisor, Long dividend, Integer scale) {

        if (dividend == null || dividend == 0L) {
            return 0D;
        }

        return new BigDecimal(divisor).divide(new BigDecimal(dividend), scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static String recordKey(String targetId, Long recordTime, Integer timeDimension) {

        return targetId + StringPool.UNDERSCORE
                + recordTime + StringPool.UNDERSCORE
                + timeDimension;
    }

    public static int getCount(Long beginTime, Long endTime, Integer timeDimension) {

        if (timeDimension.equals(TimeDimension.DAY)) {
            return resolveDayCount(beginTime, endTime);
        }


        return resolveMonthCount(beginTime, endTime);
    }

    public static long getAddedMonthTime(Long timestamp) {

        TimeZone timeZone = TimeZoneUtil.getTimeZone(28800);

        Date date = DateUtil.getDate(timestamp, timeZone);

        return DateUtil.addMonths(date, 1, timeZone).getTime();
    }
}

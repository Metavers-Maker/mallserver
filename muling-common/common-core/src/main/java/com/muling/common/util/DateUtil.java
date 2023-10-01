package com.muling.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd-HH-mm-ss";
    /**
     * 将timestamp转换为formatStr格式的String型数据
     */
    public static String getDate(long timestamp, TimeZone timeZone, String formatStr) {

        final SimpleDateFormat format = new SimpleDateFormat(formatStr);

        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }

        return format.format(new Date(timestamp));
    }

    /**
     * 获取Calendar实例
     */
    public static Calendar getCalendar(long time, TimeZone timeZone) {

        Calendar calendar = Calendar.getInstance();

        if (timeZone != null) {
            calendar.setTimeZone(timeZone);
        }

        calendar.setTime(new Date(time));

        return calendar;
    }

    /**
     * <br>根据输入的时间戳计算该时间在指定时区(utcOffset确定)内对应日期的整点(specific分别对应0,1,...,23)</br>
     * <br>或者23:59:59(specific对应24)</>
     *
     * @param timestamp 时间戳
     * @param utcOffset 时区偏移量，单位秒
     * @param specific  整点值
     * @return 时间戳
     */
    public static long getSpecificTimeOfDay(long timestamp, int utcOffset, int specific) {

        return getSpecificTimeOfDay(timestamp, TimeZoneUtil.getTimeZone(utcOffset), specific);
    }

    /**
     * <br>根据输入的时间戳计算该时间在指定时区(timeZone确定)内对应日期的整点(specific分别对应0,1,...,23)</br>
     * <br>或者23:59:59(specific对应24)</>
     *
     * @param timestamp 时间戳
     * @param timeZone  时区
     * @param specific  整点值
     * @return 时间戳
     */
    public static long getSpecificTimeOfDay(long timestamp, TimeZone timeZone, int specific) {

        if (specific < 0 || specific > 24) {
            return timestamp;
        }

        Calendar calendar = getCalendar(timestamp, timeZone);

        if (specific == 24) {
            calendar.set(Calendar.AM_PM, 0);
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        } else {
            calendar.set(Calendar.AM_PM, 0);
            calendar.set(Calendar.HOUR, specific);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        return calendar.getTime().getTime();
    }

    // <editor-fold desc="TAKE FROM https://github.com/maxirosson/jdroid-java">

    /**
     * @param dateFormatted The formatted string to parse
     * @param dateFormat
     * @return A date that represents the formatted string
     */
    public static Date parse(String dateFormatted, String dateFormat) {
        return parse(dateFormatted, dateFormat, false);
    }

    public static Date parse(String dateFormatted, String dateFormat, boolean useUtc) {
        return parse(dateFormatted, new SimpleDateFormat(dateFormat), useUtc);
    }

    /**
     * @param dateFormatted The formatted string to parse
     * @param dateFormat
     * @return A date that represents the formatted string
     */
    public static Date parse(String dateFormatted, SimpleDateFormat dateFormat) {
        return parse(dateFormatted, dateFormat, false);
    }

    public static Date parse(String dateFormatted, SimpleDateFormat dateFormat, boolean useUtc) {
        Date date = null;
        if (dateFormatted != null) {
            if (useUtc) {
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            }
            try {
                date = dateFormat.parse(dateFormatted);
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return date;
    }

    /**
     * 根据字符串时间获取时间
     *
     * @param dateFormatted 字符串时间
     * @param formatPattern 时间格式
     * @param timeZone      所在时区偏移
     * @return
     */
    public static Date parse(String dateFormatted,
                             String formatPattern,
                             TimeZone timeZone) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatPattern);
            if (timeZone != null) {
                format.setTimeZone(timeZone);
            }
            return format.parse(dateFormatted);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将制定date转换为指定时区指定格式的时间字符串
     *
     * @param date
     * @param dateFormat
     * @param timeZone
     * @return
     */
    public static String format(Date date, String dateFormat, TimeZone timeZone) {
        DateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(timeZone);
        return format(date, simpleDateFormat, false);
    }

    /**
     * 将制定date转换为指定时区指定格式的时间字符串
     *
     * @param date
     * @param dateFormat
     * @param offset     时区偏离量，单位秒
     * @return
     */
    public static String format(Date date, String dateFormat, int offset) {
        return format(date, dateFormat, TimeZoneUtil.getTimeZone(offset));
    }

    /**
     * @param date       The {@link Date} to be formatted
     * @param dateFormat The {@link DateFormat} used to format the {@link Date}
     * @return A String that represent the date with the pattern
     */
    public static String format(Date date, String dateFormat) {
        return format(date, dateFormat, false);
    }

    public static String format(Date date, String dateFormat, boolean useUtc) {
        return format(date, new SimpleDateFormat(dateFormat), useUtc);
    }

    /**
     * Transform the {@link Date} to a {@link String} using the received {@link SimpleDateFormat}
     *
     * @param date       The {@link Date} to be formatted
     * @param dateFormat The {@link DateFormat} used to format the {@link Date}
     * @return A String that represent the date with the pattern
     */
    public static String format(Date date, DateFormat dateFormat) {
        return format(date, dateFormat, false);
    }

    public static String format(Date date, DateFormat dateFormat, boolean useUtc) {
        if (useUtc) {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return date != null ? dateFormat.format(date) : null;
    }

    public static String formatDateTime(Date date) {
        return format(date, DateFormat.getDateTimeInstance());
    }

    public static String formatDate(Date date) {
        return format(date, DateFormat.getDateInstance());
    }

    public static String formatTime(Date date) {
        return format(date, DateFormat.getTimeInstance(DateFormat.SHORT));
    }

    /**
     * Creates a {@link Date} for the specified day
     *
     * @param year        The year
     * @param monthOfYear The month number (starting on 0)
     * @param dayOfMonth  The day of the month
     * @return The {@link Date}
     */
    public static Date getDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        truncateTime(calendar);
        return calendar.getTime();
    }

    public static Date getDate(Date date, Date time, Boolean is24Hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (time != null) {
            calendar.set(Calendar.HOUR_OF_DAY, DateUtil.getHour(time, is24Hour));
            calendar.set(Calendar.MINUTE, DateUtil.getMinute(time));
            calendar.set(Calendar.SECOND, 0);
        } else {
            truncateTime(calendar);
        }
        return calendar.getTime();
    }

    public static Date getDate(Date date, Date time) {
        return getDate(date, time, true);
    }

    public static Date getDate(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return calendar.getTime();
    }

    public static Date getDate(long milliseconds, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        calendar.setTimeInMillis(milliseconds);
        return calendar.getTime();
    }

    public static Date getTime(int hour, int minutes) {
        return getTime(hour, minutes, true);
    }

    public static Date getTime(int hour, int minutes, Boolean is24Hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        truncateDate(calendar);
        return calendar.getTime();
    }

    public static int getYear() {
        return getYear(now());
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getYear(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth() {
        return getMonth(now());
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getMonth(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHour(Date date, Boolean is24Hour) {
        return DateUtil.getHour(date, TimeZone.getDefault(), is24Hour);
    }

    public static int getHour(Date date, TimeZone timeZone, Boolean is24Hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(timeZone);
        return calendar.get(is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR);
    }

    public static int getMinute(Date date) {
        return DateUtil.getMinute(date, TimeZone.getDefault());
    }

    public static int getMinute(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(timeZone);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSeconds(Date date) {
        return DateUtil.getSeconds(date, TimeZone.getDefault());
    }

    public static int getSeconds(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(timeZone);
        return calendar.get(Calendar.SECOND);
    }


    public static Date setHour(Date date, int hours, Boolean is24Hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR, hours);
        return calendar.getTime();
    }

    public static Date setMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static Date addSeconds(Date date, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    public static Date addMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static Date addHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    public static Date addMonths(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    public static Date addMonths(Date date, int months, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    public static Date addYears(Date date, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    /**
     * Truncate the date asigning it to 1st of January of 1980
     *
     * @param date The {@link Date} to truncate
     * @return The truncated {@link Date}
     */
    public static Date truncateDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        truncateDate(calendar);
        return calendar.getTime();
    }

    /**
     * Truncate the {@link Calendar} date asigning it to 1st of January of 1980
     *
     * @param calendar The {@link Calendar} to truncate
     */
    public static void truncateDate(Calendar calendar) {
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.YEAR, 1980);
    }

    /**
     * Truncate the date removing hours, minutes, seconds and milliseconds
     *
     * @param date The {@link Date} to truncate
     * @return The truncated {@link Date}
     */
    public static Date truncateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        truncateTime(calendar);
        return calendar.getTime();
    }

    /**
     * Truncate the {@link Calendar} removing hours, minutes, seconds and milliseconds
     *
     * @param calendar The {@link Calendar} to truncate
     */
    public static void truncateTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * @return the current moment
     */
    public static Date now() {
        return new Date();
    }

    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    /**
     * @param date      The date to compare
     * @param startDate The left between' side
     * @param endDate   The right between's side
     * @return <code>true</code> if the date is in the middle of startDate and endDate
     */
    public static boolean isBetween(Date date, Date startDate, Date endDate) {
        return DateUtil.isBeforeEquals(startDate, date) && DateUtil.isAfterEquals(endDate, date);
    }

    /**
     * Tests if the date is before than the specified dateToCompare.
     *
     * @param date          the date to compare with the dateToCompare.
     * @param dateToCompare the date to compare with the date.
     * @return <code>true</code> if the instant of time represented by <code>date</code> object is earlier than the
     * instant represented by <tt>dateToCompare</tt>; <code>false</code> otherwise.
     */
    public static boolean isBefore(Date date, Date dateToCompare) {
        return date.compareTo(dateToCompare) < 0;
    }

    /**
     * Tests if the date is before or equals than the specified dateToCompare.
     *
     * @param date          the date to compare with the dateToCompare.
     * @param dateToCompare the date to compare with the date.
     * @return <code>true</code> if the instant of time represented by <code>date</code> object is earlier or equal than
     * the instant represented by <tt>dateToCompare</tt>; <code>false</code> otherwise.
     */
    public static boolean isBeforeEquals(Date date, Date dateToCompare) {
        return date.compareTo(dateToCompare) <= 0;
    }

    /**
     * Tests if the date is after or equals than the specified dateToCompare.
     *
     * @param date          the date to compare with the dateToCompare.
     * @param dateToCompare the date to compare with the date.
     * @return <code>true</code> if the instant of time represented by <code>date</code> object is later or equal than
     * the instant represented by <tt>dateToCompare</tt>; <code>false</code> otherwise.
     */
    public static boolean isAfterEquals(Date date, Date dateToCompare) {
        return date.compareTo(dateToCompare) >= 0;
    }

    /**
     * Tests if the date is after than the specified dateToCompare.
     *
     * @param date          the date to compare with the dateToCompare.
     * @param dateToCompare the date to compare with the date.
     * @return <code>true</code> if the instant of time represented by <code>date</code> object is later than the
     * instant represented by <tt>dateToCompare</tt>; <code>false</code> otherwise.
     */
    public static boolean isAfter(Date date, Date dateToCompare) {
        return date.compareTo(dateToCompare) > 0;
    }

    /**
     * Returns true if two periods overlap
     *
     * @param startDate1 the period one start date
     * @param endDate1   the period one end date
     * @param startDate2 the period two start date
     * @param endDate2   the period two end date
     * @return true if overlap
     */
    public static boolean periodsOverlap(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        return (startDate1.before(endDate2) || startDate1.equals(endDate2))
                && (endDate1.after(startDate2) || endDate1.equals(startDate2));
    }

    /**
     * Returns true if the first period contains the second periods
     *
     * @param startDate1 the period one start date
     * @param endDate1   the period one end date
     * @param startDate2 the period two start date
     * @param endDate2   the period two end date
     * @return true if the first period contains the second period
     */
    public static boolean containsPeriod(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        return DateUtil.isBeforeEquals(startDate1, startDate2) && DateUtil.isAfterEquals(endDate1, endDate2);
    }

    public static Boolean isToday(Date date) {
        return truncateTime(date).equals(today());
    }

    public static Boolean isToday(Long timestamp) {
        return isToday(DateUtil.getDate(timestamp));
    }

    public static Boolean isYesterdayOrPrevious(Date date) {
        return isYesterdayOrPrevious(date.getTime());
    }

    public static Boolean isYesterdayOrPrevious(Long timestamp) {
        return timestamp < today().getTime();
    }

    public static Boolean isSameDay(Date a, Date b) {
        return DateUtil.truncateTime(a).equals(DateUtil.truncateTime(b));
    }

    /**
     * @return a day after today
     */
    public static Date tomorrow() {
        return DateUtil.addDays(today(), 1);
    }

    public static Date today() {
        return truncateTime(DateUtil.now());
    }

    /**
     * @return a day before today
     */
    public static Date yesterday() {
        return DateUtil.addDays(today(), -1);
    }

    /**
     * @param months amount of months to move the calendar
     * @return a date that is <code>months</code> in the future/past. Use negative values for past dates.
     */
    public static Date monthsAway(int months) {
        return DateUtil.addMonths(today(), months);
    }

    /**
     * @return a date that is one month in the future
     */
    public static Date oneMonthInFuture() {
        return DateUtil.monthsAway(1);
    }

    /**
     * @return a date that is one month in the past
     */
    public static Date oneMonthInPast() {
        return DateUtil.monthsAway(-1);
    }

    public static Date getLastWeekDayOfPreviousWeek() {
        return getLastWeekDayOfPreviousWeek(DateUtil.now());
    }

    public static Date getLastWeekDayOfPreviousWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int diff = -(dayOfWeek + 1);
        return DateUtil.addDays(date, diff);
    }


    /**
     * @param date Date that includes the desired month in order to calculate the last day of that month
     * @return the date of the last day of the month
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        DateUtil.truncateTime(calendar);
        return calendar.getTime();
    }


    public static Date getLastDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        DateUtil.truncateTime(calendar);
        return calendar.getTime();
    }



    /**
     * @param fromDate the start date
     * @param toDate   the end date
     * @return an integer representing the amount of days between fromDate and toDate
     */
    public static Integer differenceInDays(Date fromDate, Date toDate) {
        Long diff = toDate.getTime() - fromDate.getTime();
        diff = diff / (TimeUnit.DAYS.toMillis(1));
        return diff.intValue();
    }

    /**
     * 不足1天按1天计
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Integer differenceInDaysIncludeNotEnough(Date fromDate, Date toDate) {
        long dayInMills = TimeUnit.DAYS.toMillis(1);
        Long diff = toDate.getTime() - fromDate.getTime();
        long remainder = diff % dayInMills;
        diff = diff / dayInMills;

        return remainder > 0 ? diff.intValue() + 1 : diff.intValue();
    }

    /**
     * 不足1天按1天计
     *
     * @param fromDateTimestamp
     * @param toDateTimestamp
     * @return
     */
    public static Integer differenceInDaysIncludeNotEnough(long fromDateTimestamp, long toDateTimestamp) {
        long dayInMills = TimeUnit.DAYS.toMillis(1);
        Long diff = toDateTimestamp - fromDateTimestamp;
        long remainder = diff % dayInMills;
        diff = diff / dayInMills;

        return remainder > 0 ? diff.intValue() + 1 : diff.intValue();
    }

    /**
     * @param fromDate the start date
     * @param toDate   the end date
     * @return an double representing the amount of hours between fromDate and toDate
     */
    public static double differenceInHours(Date fromDate, Date toDate) {
        double diff = toDate.getTime() - fromDate.getTime();
        diff = diff / (TimeUnit.HOURS.toMillis(1));
        return diff;
    }

    /**
     * @param fromDate the start date
     * @param toDate   the end date
     * @return an integer representing the amount of minutes between fromDate and toDate
     */
    public static Integer differenceInMinutes(Date fromDate, Date toDate) {
        Long diff = toDate.getTime() - fromDate.getTime();
        diff = diff / (TimeUnit.MINUTES.toMillis(1));
        return diff.intValue();
    }

    public static String formatDuration(long duration) {

        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - (hours * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - (hours * 60 * 60) - (minutes * 60);
        long milliseconds = TimeUnit.MILLISECONDS.toMillis(duration) - (hours * 60 * 60 * 1000) - (minutes * 60 * 1000)
                - (seconds * 1000);

        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours);
            builder.append("h, ");
        }
        if ((minutes > 0) || (builder.length() > 0)) {
            builder.append(minutes);
            builder.append("m, ");
        }
        if ((seconds > 0) || (builder.length() > 0)) {
            builder.append(seconds);
            builder.append("s, ");
        }
        if (milliseconds >= 0) {
            builder.append(milliseconds);
            builder.append("ms");
        }

        return builder.toString();
    }

    public static Date findDayOfYear(int dayOfWeekCode,
                                     int monthIndex,
                                     int week,
                                     TimeZone timeZone,
                                     Date currentYear) {
        Calendar currentCal = Calendar.getInstance(timeZone);
        currentCal.setFirstDayOfWeek(Calendar.MONDAY); // 测试发现，当前的数据库夏令时配置是从周一开始计算为一周的开始时间
        currentCal.setTime(currentYear);
        currentCal.setTimeZone(timeZone);
        currentCal.setMinimalDaysInFirstWeek(1); //只有一天也当作一周
        currentCal.set(Calendar.MONTH, monthIndex - 1); //第几个月
        currentCal.set(Calendar.WEEK_OF_MONTH, week); //第几周
        currentCal.set(Calendar.DAY_OF_WEEK, dayOfWeekCode);
        return currentCal.getTime();
    }
    // </editor-fold>

    public static Long getStartOfDay(long timestamp, TimeZone timeZone) {
        LocalDateTime localDateTime = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(timestamp), timeZone.toZoneId());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);

        return Date.from(startOfDay.atZone(timeZone.toZoneId()).toInstant()).getTime();
    }

    public static Long getStartOfDay(Date date, TimeZone timeZone) {
        return getStartOfDay(date.getTime(), timeZone);
    }

    public static Long getStartOfDay(Date date) {
        return getStartOfDay(date.getTime(), TimeZone.getDefault());
    }

    public static Long getEndOfDay(long timestamp, TimeZone timeZone) {
        LocalDateTime localDateTime = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(timestamp), timeZone.toZoneId());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);

        return Date.from(endOfDay.atZone(timeZone.toZoneId()).toInstant()).getTime();
    }

    public static Long getEndOfDay(Date date, TimeZone timeZone) {
        return getEndOfDay(date.getTime(), timeZone);
    }

    public static Long getEndOfDay(Date date) {
        return getEndOfDay(date, TimeZone.getDefault());
    }
}

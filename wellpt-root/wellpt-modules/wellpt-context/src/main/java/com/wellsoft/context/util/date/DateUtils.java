/*
 * @(#)2012-11-13 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.date;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.datetime.DateFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Description: 日期格式化/转换工具类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-13.1	zhulh		2012-11-13		Create
 * </pre>
 * @date 2012-11-13
 */
public class DateUtils {

    /**
     * Constant marker for rounding
     */
    public final static int TYPE_START = 1;

    /**
     * Constant marker for ceiling
     */
    public final static int TYPE_END = 2;

    public static final String FULL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MIN_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME_HOUR_PATTERN = "yyyy-MM-dd HH";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String MONTH_PATTERN = "yyyy-MM";
    public static final String YEAR_PATTERN = "yyyy";
    public static final String DATE_PATTERN_ZH_CN = "yyyy年MM月dd日";
    public static final String MONTH_PATTERN_ZH_CN = "yyyy年MM月";
    public static final String MONTH_DATE_PATTERN_ZH_CN = "MM月dd日";
    public static final String SHORT_DATE_PATTERN_ZH_CN = "dd日";
    /**
     * 日期转换器（yyyy-MM-dd HH:mm:ss.SSS）
     */
    public static final DateFormatter fullDateTimeFormatter = new DateFormatter(
            FULL_DATE_TIME_PATTERN);
    /**
     * 日期转换器（yyyy-MM-dd HH:mm:ss）
     */
    public static final DateFormatter dateTimeFormatter = new DateFormatter(DATE_TIME_PATTERN);
    /**
     * 日期转换器（yyyy-MM-dd HH:mm）
     */
    public static final DateFormatter dateTimeMinFormatter = new DateFormatter(
            DATE_TIME_MIN_PATTERN);
    /**
     * 日期转换器（yyyy-MM-dd HH）
     */
    public static final DateFormatter dateTimeHourFormatter = new DateFormatter(
            DATE_TIME_HOUR_PATTERN);
    /**
     * 日期转换器（yyyy-MM-dd）
     */
    public static final DateFormatter dateFormatter = new DateFormatter(DATE_PATTERN);
    /**
     * 日期转换器（yyyy-MM）
     */
    public static final DateFormatter monthFormatter = new DateFormatter(MONTH_PATTERN);
    /**
     * 日期转换器（yyyy）
     */
    public static final DateFormatter yearFormatter = new DateFormatter(YEAR_PATTERN);
    /**
     * 日期转换器（yyyy年MM月dd日）
     */
    public static final DateFormatter dateFormatterZhCn = new DateFormatter(DATE_PATTERN_ZH_CN);
    /**
     * 日期转换器（yyyy年MM月）
     */
    public static final DateFormatter monthFormatterZhCn = new DateFormatter(MONTH_PATTERN_ZH_CN);
    /**
     * 日期转换器（MM月dd日）
     */
    public static final DateFormatter monthDateFormatterZhCn = new DateFormatter(
            MONTH_DATE_PATTERN_ZH_CN);
    /**
     * 日期转换器（dd日）
     */
    public static final DateFormatter shortDateFormatterZhCn = new DateFormatter(
            SHORT_DATE_PATTERN_ZH_CN);
    /**
     * 字符串格式匹配器（yyyy-MM-dd HH:mm:ss.SSS）
     */
    public static final Pattern fullDateTimePattern = Pattern
            .compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}");
    /**
     * 字符串格式匹配器（yyyy-MM-dd HH:mm:ss）
     */
    public static final Pattern dateTimePattern = Pattern.compile(
            "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    /**
     * 字符串格式匹配器（yyyy-MM-dd HH:mm）
     */
    public static final Pattern dateTimeMinPattern = Pattern.compile(
            "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
    /**
     * 字符串格式匹配器（yyyy-MM-dd HH）
     */
    public static final Pattern dateTimeHourPattern = Pattern.compile(
            "\\d{4}-\\d{2}-\\d{2} \\d{2}");
    /**
     * 字符串格式匹配器（yyyy-MM-dd）
     */
    public static final Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    /**
     * 字符串格式匹配器（yyyy-MM）
     */
    public static final Pattern monthPattern = Pattern.compile("\\d{4}-\\d{2}");
    /**
     * 字符串格式匹配器（yyyy年MM月dd日）
     */
    public static final Pattern datePatternZhCn = Pattern.compile("\\d{4}年\\d{2}月\\d{2}日");
    /**
     * 字符串格式匹配器（yyyy年MM月）
     */
    public static final Pattern monthPatternZhCn = Pattern.compile("\\d{4}年\\d{2}月");
    /**
     * 字符串格式匹配器（MM月dd日）
     */
    public static final Pattern monthDatePatternZhCn = Pattern.compile("\\d{2}月\\d{2}日");
    /**
     * 字符串格式匹配器（dd日）
     */
    public static final Pattern shortDatePatternZhCn = Pattern.compile("\\d{2}日");
    /**
     * 字符串格式匹配器（yyyy）
     */
    public static final Pattern yearPattern = Pattern.compile("\\d{4}");
    private static final Map<String, DateFormatter> dateFormatterMap = new HashMap<String, DateFormatter>();

    /**
     * 字符串转化为日期
     *
     * @param source 字符串日期（支持多种格式）
     * @return 日期Date
     * @throws ParseException 当字符串格式错误时抛出
     */
    public static Date parse(String source) throws ParseException {
        if (fullDateTimePattern.matcher(source).find()) {
            return fullDateTimeFormatter.parse(source, Locale.getDefault());
        } else if (dateTimePattern.matcher(source).find()) {
            return dateTimeFormatter.parse(source, Locale.getDefault());
        } else if (dateTimeMinPattern.matcher(source).find()) {
            return dateTimeMinFormatter.parse(source, Locale.getDefault());
        } else if (dateTimeHourPattern.matcher(source).find()) {
            return dateTimeHourFormatter.parse(source, Locale.getDefault());
        } else if (datePattern.matcher(source).find()) {
            return dateFormatter.parse(source, Locale.getDefault());
        } else if (datePatternZhCn.matcher(source).find()) {
            return dateFormatterZhCn.parse(source, Locale.getDefault());
        } else if (monthPattern.matcher(source).find()) {
            return monthFormatter.parse(source, Locale.getDefault());
        } else if (monthPatternZhCn.matcher(source).find()) {
            return monthFormatterZhCn.parse(source, Locale.getDefault());
        } else if (monthDatePatternZhCn.matcher(source).find()) {
            return monthDateFormatterZhCn.parse(source, Locale.getDefault());
        } else if (shortDatePatternZhCn.matcher(source).find()) {
            return shortDateFormatterZhCn.parse(source, Locale.getDefault());
        } else if (yearPattern.matcher(source).find()) {
            return yearFormatter.parse(source, Locale.getDefault());
        }
        return dateTimeFormatter.parse(source, Locale.getDefault());
    }

    /**
     * @param source
     * @return
     */
    public static String parsePattern(String source) {
        if (fullDateTimePattern.matcher(source).find()) {
            return FULL_DATE_TIME_PATTERN;
        } else if (dateTimePattern.matcher(source).find()) {
            return DATE_TIME_PATTERN;
        } else if (dateTimeMinPattern.matcher(source).find()) {
            return DATE_TIME_MIN_PATTERN;
        } else if (dateTimeHourPattern.matcher(source).find()) {
            return DATE_TIME_HOUR_PATTERN;
        } else if (datePattern.matcher(source).find()) {
            return DATE_PATTERN;
        } else if (datePatternZhCn.matcher(source).find()) {
            return DATE_PATTERN_ZH_CN;
        } else if (monthPattern.matcher(source).find()) {
            return MONTH_PATTERN;
        } else if (monthPatternZhCn.matcher(source).find()) {
            return MONTH_PATTERN_ZH_CN;
        } else if (monthDatePatternZhCn.matcher(source).find()) {
            return MONTH_DATE_PATTERN_ZH_CN;
        } else if (shortDatePatternZhCn.matcher(source).find()) {
            return SHORT_DATE_PATTERN_ZH_CN;
        } else if (yearPattern.matcher(source).find()) {
            return YEAR_PATTERN;
        }
        return DATE_TIME_PATTERN;
    }

    /**
     * 字符串转化为日期
     *
     * @param source 字符串日期（支持多种格式）
     * @return 日期Date
     * @throws ParseException 当字符串格式错误时抛出
     */
    public static Date parse(String source, int type) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.parse(source));
        if (dateTimePattern.matcher(source).find()) {
            if (type == TYPE_END) {
                calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
            } else if (type == TYPE_START) {
                calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
            }
        } else if (dateTimeMinPattern.matcher(source).find()) {
            if (type == TYPE_END) {
                calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
            } else if (type == TYPE_START) {
                calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
            }
        } else if (dateTimeHourPattern.matcher(source).find()) {
            if (type == TYPE_END) {
                calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
                calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
            } else if (type == TYPE_START) {
                calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
                calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
            }
        } else if (datePattern.matcher(source).find() || datePatternZhCn.matcher(source).find()
                || monthDatePatternZhCn.matcher(source).find() || shortDatePatternZhCn.matcher(
                source).find()) {
            if (type == TYPE_END) {
                calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
                calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
            } else if (type == TYPE_START) {
                calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
                calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
            }
        } else if (monthPattern.matcher(source).find() || monthPatternZhCn.matcher(source).find()) {
            if (type == TYPE_END) {
                calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
                calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else if (type == TYPE_START) {
                calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
                calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
            }
        } else if (yearPattern.matcher(source).find()) {
            if (type == TYPE_END) {
                calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
                calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.MONTH, calendar.getMaximum(Calendar.MONTH));
            } else if (type == TYPE_START) {
                calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
                calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
                calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.MONTH, calendar.getMinimum(Calendar.MONTH));
            }
        }
        return calendar.getTime();
    }


    /**
     * 字符串转换成时间（yyyy-MM-dd HH:mm:ss.SSS）
     *
     * @param source 时间字符串
     * @return 时间对象
     * @throws ParseException 当字符串格式错误时抛出
     */
    public static Date parseFullDateTime(String source) throws ParseException {
        return fullDateTimeFormatter.parse(source, Locale.getDefault());
    }

    /**
     * 时间转字符串（yyyy-MM-dd HH:mm:ss.SSS）
     *
     * @param date 日期对象
     * @return 字符串时间
     */
    public static String formatFullDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return fullDateTimeFormatter.print(date, Locale.getDefault());
    }

    /**
     * 字符串转换成时间（yyyy-MM-dd HH:mm:ss）
     *
     * @param source 时间字符串
     * @return 时间对象
     * @throws ParseException 当字符串格式错误时抛出
     */
    public static Date parseDateTime(String source) throws ParseException {
        return dateTimeFormatter.parse(source, Locale.getDefault());
    }

    /**
     * 时间对象转字符串（yyyy-MM-dd HH:mm:ss）
     *
     * @param date 时间对象
     * @return 时间字符串
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return dateTimeFormatter.print(date, Locale.getDefault());
    }

    /**
     * 字符串转换成时间（yyyy-MM-dd HH:mm）
     *
     * @param source 时间字符串
     * @return 时间对象
     * @throws ParseException 当字符串格式错误时抛出
     */
    public static Date parseDateTimeMin(String source) throws ParseException {
        return dateTimeMinFormatter.parse(source, Locale.getDefault());
    }

    /**
     * 时间对象转字符串（yyyy-MM-dd HH:mm）
     *
     * @param date 时间对象
     * @return 时间字符串
     */
    public static String formatDateTimeMin(Date date) {
        if (date == null) {
            return null;
        }
        return dateTimeMinFormatter.print(date, Locale.getDefault());
    }

    /**
     * 字符串转换成时间（yyyy-MM-dd HH）
     *
     * @param source 时间字符串
     * @return 时间对象
     * @throws ParseException 当字符串格式错误时抛出
     */
    public static Date parseDateTimeHour(String source) throws ParseException {
        return dateTimeHourFormatter.parse(source, Locale.getDefault());
    }

    /**
     * 时间对象转字符串（yyyy-MM-dd HH）
     *
     * @param date 时间对象
     * @return 时间字符串
     */
    public static String formatDateTimeHour(Date date) {
        if (date == null) {
            return null;
        }
        return dateTimeHourFormatter.print(date, Locale.getDefault());
    }

    /**
     * 字符串转换成日期（yyyy-MM-dd）
     *
     * @param source 日期字符串
     * @return 日期对象
     * @throws ParseException 当字符串格式错误时抛出
     */
    public static Date parseDate(String source) throws ParseException {
        return dateFormatter.parse(source, Locale.getDefault());
    }

    /**
     * 时间对象转字符串
     *
     * @param date    时间对象
     * @param pattern 时间转换格式
     * @return 时间字符串
     */
    public synchronized static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        if (!dateFormatterMap.containsKey(pattern)) {
            dateFormatterMap.put(pattern, new DateFormatter(pattern));
        }

        return dateFormatterMap.get(pattern).print(date, Locale.getDefault());
    }

    public static String convertDate(Date converDate) {
        Date now = new Date();
        if (DateUtils.isSameDate(now, converDate)) {
            return format(converDate, "HH:mm");
        } else if (formatYear(now).equals(formatYear(converDate))) {
            return format(converDate, "MM-dd HH:mm");
        }
        return formatDateTimeMin(converDate);
    }

    /**
     * 时间对象转字符串（yyyy-MM-dd）
     *
     * @param date 时间对象
     * @return 日期字符串
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormatter.print(date, Locale.getDefault());
    }

    /**
     * 月份字符串转换成Date对象（yyyy-MM）
     *
     * @param source 年月字符串
     * @return Date对象
     * @throws ParseException 当字符串格式错误时抛出
     */
    public static Date parseMonth(String source) throws ParseException {
        return monthFormatter.parse(source, Locale.getDefault());
    }

    /**
     * 时间对象转年月字符串（yyyy-MM）
     *
     * @param date 时间对象
     * @return 年月字符串
     */
    public static String formatMonth(Date date) {
        if (date == null) {
            return null;
        }
        return monthFormatter.print(date, Locale.getDefault());
    }

    public static String formatYear(Date date) {
        if (date == null) {
            return null;
        }
        return yearFormatter.print(date, Locale.getDefault());
    }

    /**
     * 小时数转秒数（单位转换）
     *
     * @param hour 小时数
     * @return 小时数对应的秒数
     */
    public static Integer hourToSecond(final Double hour) {
        return Double.valueOf(hour * 3600).intValue();
    }

    /**
     * 毫秒数转天数（单位转换）
     *
     * @param millisecond 毫秒数
     * @return 毫秒数对应天数
     */
    public static Double millisecondToDay(Long millisecond) {
        return (millisecond) / (24 * 3600 * 1000 * 1d);
    }

    /**
     * 毫秒数转小时数（单位转换）
     *
     * @param millisecond 毫秒数
     * @return 毫秒数对应小时数
     */
    public static Double millisecondToHour(Long millisecond) {
        return (millisecond) / (3600 * 1000 * 1d);
    }

    /**
     * 毫秒数转分钟数（单位转换）
     *
     * @param millisecond 毫秒数
     * @return 毫秒数对应分钟数
     */
    public static Double millisecondToMinute(Long millisecond) {
        return (millisecond) / (60 * 1000 * 1d);
    }

    /**
     * 计算两个时间间隔的小时数
     *
     * @param from 结束时间
     * @param to   开始时间
     * @return 相差小时数
     */
    public static Double calculateAsHour(final Date from, final Date to) {
        long totalTime = from.getTime() - to.getTime();
        return millisecondToHour(totalTime);
    }

    /**
     * 计算两个时间间隔的分钟数
     *
     * @param from 结束时间
     * @param to   开始时间
     * @return 相差分钟数
     */
    public static Double calculateAsMinute(final Date from, final Date to) {
        long totalTime = from.getTime() - to.getTime();
        return millisecondToMinute(totalTime);
    }

    /**
     * 判断两个日期是否同一天
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return true-同一天;flase 不同 一天
     */
    public static Boolean isSameDate(Date date1, Date date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        String tempStr1 = simpleDateFormat.format(date1);
        String tempStr2 = simpleDateFormat.format(date2);
        return tempStr1.equals(tempStr2);
    }

    /**
     * 计算时间周期[fromTime,toTime]与时间周期[fromRange, toRange]相交的部分的毫秒数
     *
     * @param fromTime  周期1开始时间
     * @param toTime    周期1结束时间
     * @param fromRange 周期2开始时间
     * @param toRange   周期2结束时间
     * @return 相交的部分毫秒数
     */
    public static long getUnionTime(Date fromTime, Date toTime, Date fromRange, Date toRange) {
        long totalTime = Long.valueOf(0);
        // 开始时间、结束时间不在时间区段中，返回0
        if ((fromTime.after(toRange)) || (toTime.before(fromRange))) {

        } else {// 计算有效工作时间
            totalTime = toRange.getTime() - fromRange.getTime();
            long fromOffset = fromTime.getTime() - fromRange.getTime();
            long toOffset = toTime.getTime() - toRange.getTime();
            // 减去已经开始的有效时间
            if (fromOffset > 0) {
                totalTime = totalTime - fromOffset;
            }
            // 减去没有到达的有效时间
            if (toOffset < 0) {
                totalTime = totalTime - (-toOffset);
            }
        }
        return totalTime;
    }

    /**
     * 根据Date对象获取Calendar对象
     *
     * @param date Date对象
     * @return Calendar对象
     */
    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 获取某个时间的Calendar对象当天23:59:59对应的Calendar对象
     *
     * @param source 时间Calendar对象
     * @return Calendar对象
     */
    public static Calendar getMaxTimeCalendar(Calendar source) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(source.get(Calendar.YEAR), source.get(Calendar.MONTH),
                source.get(Calendar.DAY_OF_MONTH), 23, 59,
                59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 获取某个时间的Calendar对象当天00:00:00对应的Calendar对象
     *
     * @param source 时间Calendar对象
     * @return Calendar对象
     */
    public static Calendar getMinTimeCalendar(Calendar source) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(source.get(Calendar.YEAR), source.get(Calendar.MONTH),
                source.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }


    /**
     * 计算间隔时间 返回
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param count 次数
     * @return
     */
    public static List<Date> calculationInterval(Date start, Date end, int count) {
        if (count < 1) {
            throw new RuntimeException("次数不能小于1");
        }
        if (start == null) {
            throw new RuntimeException("开始时间不能为空");
        }
        List<Date> dateList = new ArrayList<>();
        if (count == 1) {
            dateList.add(start);
            return dateList;
        }
        if (end == null) {
            throw new RuntimeException("结束时间不能为空");
        }
        if (end.getTime() < start.getTime()) {
            throw new RuntimeException("结束时间不能小于开始时间");
        }
        if (count == 2) {
            dateList.add(start);
            dateList.add(end);
            return dateList;
        }
        count = count - 1;
        long totalMilliseconds = end.getTime() - start.getTime();
        long averageIntervalInMilliseconds = totalMilliseconds % count == 0 ? totalMilliseconds / count : totalMilliseconds / count + 1;
        for (int i = 0; i <= count; i++) {
            dateList.add(new Date(start.getTime() + i * averageIntervalInMilliseconds));
        }
        return dateList;
    }

    public static String calculateAsFormatZH(Date form, Date to, String type) {
        Long usedTime = to.getTime() - form.getTime();
        StringBuffer usedFmt = new StringBuffer();
        if (usedTime > 24 * 3600 * 1000) {
            //如果大于1天
            long day = usedTime / (24 * 3600 * 1000);
            usedFmt.append(day + "天");
            usedTime = usedTime - (day * 24 * 3600 * 1000);
        }
        if ("day".equals(type)) {
            return usedFmt.toString();
        }
        if (usedTime > 3600 * 1000) {
            //如果大于1小时
            long hour = usedTime / (3600 * 1000);
            usedFmt.append(hour + "小时");
            usedTime = usedTime - (hour * 3600 * 1000);
        }
        if ("hour".equals(type)) {
            return usedFmt.toString();
        }
        if (usedTime > 60 * 1000) {
            long minute = usedTime / (60 * 1000);
            usedFmt.append(minute + "分钟");
            usedTime = usedTime - minute * 60 * 1000;
        }
        if ("minute".equals(type)) {
            if (StringUtils.isBlank(usedFmt.toString())) {
                return "1分钟";
            }
            return usedFmt.toString();
        }
        if (usedTime > 1000) {
            long second = usedTime / 1000;
            usedFmt.append(second + "秒");
            usedTime = usedTime - second * 1000;
        }
        if ("second".equals(type)) {
            return usedFmt.toString();
        }
        usedFmt.append(usedTime + "毫秒");
        return usedFmt.toString();
    }
}

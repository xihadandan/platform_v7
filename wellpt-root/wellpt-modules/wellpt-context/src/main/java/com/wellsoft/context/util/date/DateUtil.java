/*
 * @(#)2014-3-5 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.date;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-5.1	Administrator		2014-3-5		Create
 * </pre>
 * @date 2014-3-5
 */
public class DateUtil {
    private static Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 对于Calendar获得的月、日、时、分、秒、如果是个位的话，在前面补一个零
     *
     * @param sourceStr
     * @return
     */
    public static String getFormatDate(int sourceDate) {
        String date = String.valueOf(sourceDate);
        if (date.length() < 2) {
            date = "0" + date;
        }
        return date;
    }

    /**
     * 获取年份
     *
     * @return
     */
    public static String getYear() {
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));// 获取年份
        return year;
    }

    /**
     * 获取月份
     *
     * @return
     */
    public static String getMonth() {
        Calendar cal = Calendar.getInstance();
        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);// 获取月份
        return month;
    }

    /**
     * 获取指定日期N天后的时间
     *
     * @return
     */
    public static Date getSpecifyDate(Date date, int monunt) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.add(Calendar.DATE, monunt);// N天后的日期
        Date finalDate = new Date(c.getTimeInMillis());
        return finalDate;
    }

    /**
     * 获取指定日期N天前的时间
     *
     * @return
     */
    public static Date getPrevDate(Date date, int monunt) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.add(Calendar.DATE, monunt * -1);// N天前的日期
        Date finalDate = new Date(c.getTimeInMillis());
        return finalDate;
    }

    /**
     * 获取指定日期N天后的时间
     *
     * @return
     */
    public static Date getNextDate(Date date, int monunt) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.add(Calendar.DATE, monunt * 1);// N天前的日期
        Date finalDate = new Date(c.getTimeInMillis());
        return finalDate;
    }

    /**
     * 获取指定日期N分钟前的时间
     *
     * @param date
     * @param monunt 单位分钟
     * @return java.util.Date
     **/
    public static Date getPreMinute(Date date, int monunt) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.add(Calendar.MINUTE, monunt * -1);// N天前的日期
        Date finalDate = new Date(c.getTimeInMillis());
        return finalDate;
    }

    /**
     * 获取指定日期N分钟后的时间
     *
     * @param date
     * @param monunt 单位分钟
     * @return java.util.Date
     **/
    public static Date getNextMinute(Date date, int monunt) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.add(Calendar.MINUTE, monunt * 1);// N天前的日期
        Date finalDate = new Date(c.getTimeInMillis());
        return finalDate;
    }

    /**
     * 日期格式化,格式自己指定(日期 → 字符串)
     *
     * @param date
     * @return
     */
    public static String getFormatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 日期格式化,格式自己指定(字符串 → 日期)
     *
     * @param date
     * @return
     */
    public static Date getFormatDateByStr(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return date;
    }

    /**
     * 获取上周一日期
     *
     * @param date 当前日期
     * @return
     */
    public static Date getLastWeekMonday(Date date) {
        Date a = DateUtils.addDays(date, -1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(a);
        cal.add(Calendar.WEEK_OF_YEAR, -1);// 一周
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获取上周日日期
     *
     * @param date 当前日期
     * @return
     */
    public static Date getLastWeekSunday(Date date) {
        Date a = DateUtils.addDays(new Date(), -1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(a);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
    }

    /**
     * 获取本周一日期
     *
     * @param date 当前日期
     * @return
     */
    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1); // 解决周日会出现 并到下一周的情况
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获取本周日日期
     *
     * @param date 当前日期
     * @return
     */
    public static Date getThisWeekSunday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.getTime();
    }

    /**
     * 获取当前月第一天
     *
     * @return
     */
    public static Date getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * /获取当前月最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 获取一个时间段内的所有工作日，就是周一到周五
     */
    public static List<Date> getWorkDayInPeriod(Date startTime, Date endTime) {
        return getDayOfWeekInPeriod(startTime, endTime, 2, 3, 4, 5, 6);
    }

    /**
     * 获取一个时间段内的指定的星期几, 1-7，分别代表周日-周六
     */
    public static List<Date> getDayOfWeekInPeriod(Date startTime, Date endTime, Integer... dayOfWeeks) {
        List<Integer> days = Arrays.asList(dayOfWeeks);
        List<Date> list = Lists.newArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        while (calendar.getTime().before(endTime)) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (days.contains(dayOfWeek)) {
                list.add(calendar.getTime());
            }
            calendar.add(Calendar.DATE, 1);
        }
        return list;
    }

    /**
     * 获取一个时间段内的指定的日期, 1-31分别代表1号-31号
     */
    public static List<Date> getDayOfMonthInPeriod(Date startTime, Date endTime, Integer... dayOfMonths) {
        List<Integer> days = Arrays.asList(dayOfMonths);
        List<Date> list = Lists.newArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        while (calendar.getTime().before(endTime)) {
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            if (days.contains(dayOfMonth)) {
                list.add(calendar.getTime());
            }
            calendar.add(Calendar.DATE, 1);
        }
        return list;
    }

    /**
     * 获取一个时间段内，每年指定的日期
     */
    public static List<Date> getDayOfYearInPeriod(Date periodStart, Date periodEnd, String repeatValue) {
        List<Date> list = Lists.newArrayList();
        Calendar calendar = Calendar.getInstance();
        String year = DateUtil.getFormatDate(periodStart, "yyyy");
        Date first = DateUtil.getFormatDateByStr(year + "-" + repeatValue, "yyyy-MM-dd");
        do {
            if (first.after(periodStart) && first.before(periodEnd)) {
                list.add(first);
            }
            calendar.setTime(first);
            calendar.add(Calendar.YEAR, 1);
            first = calendar.getTime();
        } while (first.before(periodEnd));
        return list;
    }

    // 判断两个时间段是否冲突:true 是冲突， false 是不冲突
    public static boolean isConflictOfTwoPeriod(Date s1, Date e1, Date s2, Date e2) {
        if (e1.getTime() < s2.getTime() || s1.getTime() > e2.getTime()) {
            return false;
        }
        return true;
    }

}

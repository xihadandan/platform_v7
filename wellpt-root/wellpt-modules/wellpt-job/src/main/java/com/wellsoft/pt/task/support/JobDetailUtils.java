package com.wellsoft.pt.task.support;

import com.wellsoft.pt.task.entity.JobDetails;
import com.wellsoft.pt.task.job.JobDetail;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class JobDetailUtils {
    private static Logger LOG = LoggerFactory.getLogger(JobDetailUtils.class);

    public static JobDetail getJobDetail(JobDetails details) {
        JobDetail jobDetail = new JobDetail();
        int timingMode = details.getTimingMode() == null ? TimingMode.INTERVAL : details.getTimingMode();
        switch (timingMode) {
            case TimingMode.EVERY_DATE:

            case TimingMode.EVERY_WEEK:

            case TimingMode.EVERY_MONTH:

            case TimingMode.EVERY_SEASON:

            case TimingMode.EVERY_YEAR:
                jobDetail = resolveCronJobDetail(details);
                break;
            case TimingMode.INTERVAL:
                jobDetail = resolveSimpleJobDetail(details);
                break;
            default:
                break;
        }

        return jobDetail;
    }

    private static JobDetail resolveCronJobDetail(JobDetails details) {
        JobDetail jobDetail = resolveSimpleJobDetail(details);
        jobDetail.setType(JobDetail.TYPE_TIMING);

        String expression = null;
        int timingMode = details.getTimingMode();
        switch (timingMode) {
            case TimingMode.EVERY_DATE:
                expression = resolveEveryDayCronExpression(details);
                break;
            case TimingMode.EVERY_WEEK:
                expression = resolveEveryWeekCronExpression(details);
                break;
            case TimingMode.EVERY_MONTH:
                expression = resolveEveryMonthCronExpression(details);
                break;
            case TimingMode.EVERY_SEASON:
                expression = resolveEverySeasonCronExpression(details);
                break;
            case TimingMode.EVERY_YEAR:
                expression = resolveEveryYearCronExpression(details);
                break;
            default:
                break;
        }
        jobDetail.setExpression(expression);

        return jobDetail;
    }

    private static JobDetail resolveSimpleJobDetail(JobDetails details) {
        JobDetail jobDetail = new JobDetail();
        jobDetail.setName(details.getName());
        jobDetail.setJobUuid(details.getUuid());
        jobDetail.setTenantId(details.getTenantId());
        if (StringUtils.isNotBlank(details.getStarter())) {
            jobDetail.setUserId(details.getStarter());
        } else {
            jobDetail.setUserId(details.getCreator());
        }
        jobDetail.setRemark(details.getRemark());
        jobDetail.setType(JobDetail.TYPE_TEMPORARY);
        jobDetail.setAutoScheduling(Boolean.TRUE.equals(details.getAutoScheduling()));
        jobDetail.setAssignIp(details.getAssignIp());

        Date startTime = details.getStartTime();
        Date endTime = details.getEndTime();
        if (startTime != null && endTime != null && startTime.after(endTime)) {
            throw new IllegalArgumentException("结束时间不能在开始时间之前！");
        }
        // 开始时间为空则取当前时间作为开始时间
        startTime = startTime == null ? Calendar.getInstance().getTime() : startTime;
        Class<?> jobClass = getJobClass(details.getJobClassName());
        int repeatCount = details.getRepeatCount() == null ? 0 : details.getRepeatCount();
        long repeatInterval = getRepeatInterval(details.getRepeatIntervalTime());

        jobDetail.setJobClass(jobClass);
        jobDetail.setStartTime(startTime);
        jobDetail.setEndTime(endTime);
        jobDetail.setRepeatCount(repeatCount);
        jobDetail.setRepeatInterval(repeatInterval);

        return jobDetail;
    }

    public static Class<?> getJobClass(String jobClassName) {
        try {
            return Class.forName(jobClassName);
        } catch (ClassNotFoundException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("找不到指定的任务类[" + jobClassName + "]");
        }
    }

    private static long getRepeatInterval(String repeatIntervalTime) {
        if (StringUtils.isBlank(repeatIntervalTime)) {
            return 0;
        }
        String[] timeParts = StringUtils.split(repeatIntervalTime, ":");
        String hour = timeParts[0];
        String minute = timeParts[1];
        String second = timeParts[2];
        int totalSecond = Integer.valueOf(hour) * 60 * 60 + Integer.valueOf(
                minute) * 60 + Integer.valueOf(second);

        if (totalSecond < 1) {
            throw new RuntimeException("重复时间间隔不能小于1秒！");
        }

        return totalSecond * 1000;
    }

    private static StringBuilder resolveTimePoint(final String timePoint) {
        StringBuilder sb = new StringBuilder();
        String tmpTimePoint = timePoint;
        if (StringUtils.isBlank(tmpTimePoint)) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:MM:ss");
            tmpTimePoint = timeFormat.format(Calendar.getInstance().getTime());
        }
        String[] timeParts = StringUtils.split(tmpTimePoint, ":");
        String hour = timeParts[0];
        String minute = timeParts[1];
        String second = timeParts[2];
        sb.append(second);
        sb.append(" ");
        sb.append(minute);
        sb.append(" ");
        sb.append(hour);
        return sb;
    }

    private static String resolveEveryDayCronExpression(JobDetails details) {
        StringBuilder sb = resolveTimePoint(details.getTimePoint());
        sb.append(" ");
        sb.append("* * ?");
        return sb.toString();
    }

    private static String resolveEveryWeekCronExpression(JobDetails details) {
        String repeatDayOfWeek = details.getRepeatDayOfWeek();
        if (StringUtils.isBlank(repeatDayOfWeek)) {
            throw new RuntimeException("星期一到星期日没有设置！");
        }
        StringBuilder sb = resolveTimePoint(details.getTimePoint());
        sb.append(" ");
        sb.append("? *");
        sb.append(" ");
        sb.append(repeatDayOfWeek);
        return sb.toString();
    }

    private static String resolveEveryMonthCronExpression(JobDetails details) {
        String repeatDayOfMonth = details.getRepeatDayOfMonth();
        if (StringUtils.isBlank(repeatDayOfMonth)) {
            throw new RuntimeException("每月的日期没有设置！");
        }
        if (repeatDayOfMonth.indexOf(
                'L') != -1 && repeatDayOfMonth.length() > 1 && repeatDayOfMonth.indexOf(",") >= 0) {
            throw new RuntimeException("每月最后一天不能与其他天数同时设置！");
        }
        StringBuilder sb = resolveTimePoint(details.getTimePoint());
        sb.append(" ");
        sb.append(repeatDayOfMonth);
        sb.append(" ");
        sb.append("* ?");
        return sb.toString();
    }

    private static String resolveEverySeasonCronExpression(JobDetails details) {
        if (StringUtils.isBlank(details.getRepeatDayOfSeason())) {
            throw new RuntimeException("每季度的重复日期没有设置！");
        }
        if (details.getRepeatDayOfSeason().indexOf(" ") < 0) {
            throw new RuntimeException("每季度的重复日期数据错误！");
        }
        String[] dayAndMonth = details.getRepeatDayOfSeason().split(" ");

        if (dayAndMonth.length != 2) {
            throw new RuntimeException("每季度的重复日期数据错误！");
        }
        StringBuilder sb = resolveTimePoint(details.getTimePoint());
        sb.append(" ").append(details.getRepeatDayOfSeason()).append("/3").append(" ?");
        return sb.toString();
    }

    private static String resolveEveryYearCronExpression(JobDetails details) {
        if (StringUtils.isBlank(details.getRepeatDayOfYear())) {
            throw new RuntimeException("每年的重复日期没有设置！");
        }
        if (details.getRepeatDayOfYear().indexOf(" ") < 0) {
            throw new RuntimeException("每年的重复日期数据错误！");
        }
        String[] dayAndMonth = details.getRepeatDayOfYear().split(" ");

        if (dayAndMonth.length != 2) {
            throw new RuntimeException("每年的重复日期数据错误！");
        }
        StringBuilder sb = resolveTimePoint(details.getTimePoint());
        sb.append(" ").append(details.getRepeatDayOfYear()).append(" ?");
        return sb.toString();
    }

}

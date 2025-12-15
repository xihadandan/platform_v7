/*
 * @(#)2012-11-8 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.job;

import com.wellsoft.pt.task.exception.TriggerNotSupportException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.Job;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Description: 将实体类JobDetail转化封装成JobInfo
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-8.1	zhulh		2012-11-8		Create
 * </pre>
 * @date 2012-11-8
 */
@Component
public class JobDetailResolver {
    private static Logger LOG = LoggerFactory.getLogger(JobDetailResolver.class);

    /**
     * 实体类JobDetail转化为quartz中的JobDetail
     *
     * @param detail
     * @return
     */
    private static org.quartz.JobDetail resolveJobDetail(JobDetail detail) {
        String name = detail.getName();
        // 租户ID作为任务组名
        String group = detail.getTenantId();
        Class<? extends Job> jobClass = (Class<? extends Job>) detail.getJobClass(); //getJobClass(detail);
        boolean volatility = detail.isVolatility();//FIXME: quartz2.3 无该属性配置
        boolean durability = detail.isDurability();
        boolean recover = detail.isRecover();
        org.quartz.JobDetail job = JobBuilder.newJob().withIdentity(name, group).ofType(
                jobClass).storeDurably(
                durability).requestRecovery(recover).setJobData(detail.getJobData()).build();

        return job;
    }

    /**
     * 实体类JobDetail转化为quartz中的Trigger
     *
     * @param detail
     */
    private static Trigger resolveTrigger(JobDetail detail, String jobName, String jobGroup) {
        String triggerType = detail.getType();
        // 任务名称
        String name = detail.getName();
        // 租户ID作为触发器组名
        String group = detail.getTenantId();
        Date startTime = detail.getStartTime();
        Date endTime = detail.getEndTime();
        Trigger trigger = null;
        // 定时任务
        if (JobDetail.TYPE_TIMING.equals(triggerType)) {
            String cronExpression = detail.getExpression();
            try {
                trigger = TriggerBuilder.newTrigger().withDescription(
                        detail.getSchedulerName()).withSchedule(
                        CronScheduleBuilder.cronSchedule(cronExpression)).withIdentity(name,
                        group).forJob(jobName, jobGroup).startAt(startTime).endAt(
                        endTime).usingJobData(detail.getJobData())
                        .build();
            } catch (Exception e) {
                LOG.error(ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        } else if (JobDetail.TYPE_TEMPORARY.equals(triggerType)) {// 临时任务
            int repeatCount = detail.getRepeatCount();
            long repeatInterval = detail.getRepeatInterval();
            trigger = TriggerBuilder.newTrigger().withDescription(
                    detail.getSchedulerName()).withSchedule(
                    SimpleScheduleBuilder.simpleSchedule().withRepeatCount(
                            repeatCount).withIntervalInMilliseconds(
                            repeatInterval).build().getScheduleBuilder()).withIdentity(name,
                    group).forJob(jobName, jobGroup).startAt(startTime).endAt(endTime).usingJobData(
                    detail.getJobData()).build();
        } else {
            throw new TriggerNotSupportException("Trigger not support for " + triggerType);
        }
        return trigger;
    }

    /**
     * 将实体类JobDetail转化封装成JobInfo
     *
     * @param detail
     * @param data
     * @return
     */
    public JobInfo resolve(JobDetail detail) {
        //解析JobDetail
        org.quartz.JobDetail job = resolveJobDetail(detail);
        //解析Trigger
        Trigger trigger = resolveTrigger(detail, job.getKey().getName(), job.getKey().getGroup());
        //设置返回JobInfo
        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobDetail(job);
        jobInfo.setTrigger(trigger);

        return jobInfo;
    }

    /**
     * 返回JobDetail中的Java类
     *
     * @param detail
     * @param jobClass
     * @return
     */
    //	private static Class<?> getJobClass(JobDetail detail) {
    //		Class<?> jobClass = null;
    //		try {
    //			jobClass = Class.forName(detail.getJobClassName());
    //		} catch (ClassNotFoundException e) {
    //			e.printStackTrace();
    //
    //		}
    //		return jobClass;
    //	}
}

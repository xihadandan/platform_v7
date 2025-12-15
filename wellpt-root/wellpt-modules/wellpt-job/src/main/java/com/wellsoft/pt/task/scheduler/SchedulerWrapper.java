/*
 * @(#)2012-11-8 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.scheduler;

import com.wellsoft.pt.task.job.JobDetail;
import com.wellsoft.pt.task.job.JobDetailResolver;
import com.wellsoft.pt.task.service.JobFiredDetailsService;
import com.wellsoft.pt.task.support.JobFiredType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * Description: 如何描述该类
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
public class SchedulerWrapper {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * applicationContext-quartz.xml中配置的SchedulerFactoryBean
     */
    @Autowired(required = false)
    private Scheduler scheduler;

    @Autowired
    private JobFiredDetailsService jobFiredDetailsService;

    @Autowired
    private JobDetailResolver jobDetailResolver;

    /**
     * @param jobDetail
     * @param data
     */
    private static void preCheck(JobDetail jobDetail, boolean checkTenantId, boolean checkUserId) {
        if (checkTenantId) {
            String jTenantId = jobDetail.getTenantId();
            if (StringUtils.isBlank(jTenantId)) {
                throw new RuntimeException("请指定JobDetail的租户ID！");
            }
        }
        if (checkUserId) {
            String jUserId = jobDetail.getUserId();
            if (StringUtils.isBlank(jUserId)) {
                throw new RuntimeException("请指定JobDetail的用户ID！");
            }
        }
    }

    /**
     * 调度任务
     *
     * @param jobDetail
     * @param data
     * @return
     */
    public Date startJob(JobDetail jobDetail) {
        preCheck(jobDetail, true, true);
        jobFiredDetailsService.scheduleJobDetail(JobFiredType.START, jobDetail);
        return Calendar.getInstance().getTime();
    }

    /**
     * 重新加载调度任务
     *
     * @param jobDetail
     * @param data
     * @return
     */
    public Date restartJob(JobDetail jobDetail) {
        preCheck(jobDetail, true, true);
        jobFiredDetailsService.scheduleJobDetail(JobFiredType.RESTART, jobDetail);
        return Calendar.getInstance().getTime();
    }

    /**
     * 触发任务立即执行
     *
     * @param jobDetail
     * @param data
     */
    public void triggerJob(JobDetail jobDetail) {
        preCheck(jobDetail, true, false);
        jobFiredDetailsService.scheduleJobDetail(JobFiredType.START, jobDetail);
    }

    /**
     * 暂停任务
     *
     * @param jobDetail
     */
    public void pauseJob(JobDetail jobDetail) {
        preCheck(jobDetail, true, false);
        jobFiredDetailsService.scheduleJobDetail(JobFiredType.PAUSE, jobDetail);
    }

    /**
     * 恢复任务
     *
     * @param jobDetail
     */
    public void resumeJob(JobDetail jobDetail) {
        preCheck(jobDetail, true, false);
        jobFiredDetailsService.scheduleJobDetail(JobFiredType.RESUME, jobDetail);
    }

    /**
     * @param jobDetail
     */
    public void stopJob(JobDetail jobDetail) {
        preCheck(jobDetail, true, false);
        jobFiredDetailsService.scheduleJobDetail(JobFiredType.STOP, jobDetail);
    }

    /**
     * 删除任务
     *
     * @param jobDetail
     * @return
     */
    public boolean deleteJob(JobDetail jobDetail) {
        preCheck(jobDetail, true, false);
        jobFiredDetailsService.scheduleJobDetail(JobFiredType.DELETE, jobDetail);
        return true;
    }

    /**
     * 判断任务是否存在任务
     *
     * @param jobDetail
     * @return
     */
    public boolean isExists(JobDetail jobDetail) {
        if (scheduler == null) {
            return false;
        }

        preCheck(jobDetail, true, false);

        String jobName = jobDetail.getName();
        String groupName = jobDetail.getTenantId();
        boolean result = false;
        try {
            result = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName)) != null;
        } catch (SchedulerException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 获取任务状态
     *
     * @param jobDetail
     * @return
     */
    public int getState(JobDetail jobDetail) {
        if (scheduler == null) {
            return Trigger.TriggerState.NONE.ordinal();
        }

        preCheck(jobDetail, true, false);

        String jobName = jobDetail.getName();
        String groupName = jobDetail.getTenantId();
        int status = Trigger.TriggerState.NONE.ordinal();
        try {
            status = scheduler.getTriggerState(TriggerKey.triggerKey(jobName, groupName)).ordinal();
        } catch (SchedulerException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return status;
    }

}

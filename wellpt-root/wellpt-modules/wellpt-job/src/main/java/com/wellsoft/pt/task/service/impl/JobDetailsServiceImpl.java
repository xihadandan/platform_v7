/*
 * @(#)2013-9-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service.impl;

import com.wellsoft.context.Context;
import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.ClassUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import com.wellsoft.pt.task.JobStateEnum;
import com.wellsoft.pt.task.bean.JobDetailsBean;
import com.wellsoft.pt.task.dao.JobDetailsDao;
import com.wellsoft.pt.task.entity.JobDetails;
import com.wellsoft.pt.task.job.Job;
import com.wellsoft.pt.task.job.JobDetail;
import com.wellsoft.pt.task.service.JobDetailsService;
import com.wellsoft.pt.task.service.SchedulerService;
import com.wellsoft.pt.task.support.JobDetailUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.*;
import org.quartz.impl.JobExecutionContextImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-9-17.1	zhulh		2013-9-17		Create
 * </pre>
 * @date 2013-9-17
 */
@Service
public class JobDetailsServiceImpl extends
        AbstractJpaServiceImpl<JobDetails, JobDetailsDao, String> implements
        JobDetailsService {

    @Autowired(required = false)
    private StdScheduler scheduler;

    @Autowired
    private SchedulerService schedulerService;

    @Value("${allowStopJobDetail:false}")
    private String allowStopJob;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#get(java.lang.String)
     */
    @Override
    public JobDetailsBean getBean(String uuid) {
        JobDetails jobDetails = dao.getOne(uuid);
        JobDetailsBean bean = new JobDetailsBean();
        BeanUtils.copyProperties(jobDetails, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#saveBean(com.wellsoft.pt.task.entity.JobDetails)
     */
    @Override
    @Transactional
    public void saveBean(JobDetailsBean bean) {
        JobDetails jobDetails = new JobDetails();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            jobDetails = this.dao.getOne(bean.getUuid());
        }

        BeanUtils.copyProperties(bean, jobDetails);
        jobDetails.setTenantId(TenantContextHolder.getTenantId());
        Class<?> jobClass = JobDetailUtils.getJobClass(bean.getJobClassName());
        // 自动调度
        try {
            jobDetails.setAutoScheduling(((Job) jobClass.newInstance()).isAutoScheduling());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        this.dao.save(jobDetails);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#query(com.wellsoft.pt.core.support.QueryInfo)
     */
    @Override
    public List<JobDetails> query(QueryInfo queryInfo) {
        List<JobDetails> jobDetails = this.dao.listByEntity(new JobDetails(),
                queryInfo.getPropertyFilters(),
                queryInfo.getOrderBy(), queryInfo.getPagingInfo());
        for (JobDetails details : jobDetails) {
            try {
                String triggerName = details.getName();
                String triggerGroup = details.getTenantId();
                if (scheduler == null) {
                    continue;
                }
                details.setState(JobStateEnum.fromTriggerState(scheduler.getTriggerState(
                        TriggerKey.triggerKey(triggerName, triggerGroup)))
                        .getCode());
                Trigger trigger = scheduler.getTrigger(
                        TriggerKey.triggerKey(triggerName, triggerGroup));
                if (trigger != null) {
                    details.setNextFireTime(trigger.getNextFireTime());
                }
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return jobDetails;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        removeJob(uuid);
        this.dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            removeJob(uuid);
            this.dao.delete(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#start(java.lang.String)
     */
    @Override
    @Transactional
    public void start(String uuid) {
        JobDetail jobDetail = getJobDetail(uuid);
        Date startTime = jobDetail.getStartTime();
        Date sysDate = this.dao.getSysDate();
        if (startTime == null || startTime.before(sysDate)) {
            jobDetail.setStartTime(sysDate);
        }
        schedulerService.startJob(jobDetail);
    }


    @Override
    @Transactional
    public void debugJob(String uuid) {
        JobDetail jobDetail = getJobDetail(uuid);
        jobDetail.setType("debug");
        String jobName = jobDetail.getName();
        String groupName = jobDetail.getTenantId();
        try {
            //1.停掉运行的job
            scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
            //2.执行对应job
            Job job = (Job) jobDetail.getJobClass().newInstance();
            org.quartz.JobDetail qJobDetail1 =
                    JobBuilder.newJob().withIdentity(JobKey.jobKey(jobName, groupName)).ofType(
                            (Class<? extends org.quartz.Job>) jobDetail.getJobClass()).usingJobData(
                            jobDetail.getJobData()).build();
            JobExecutionContextImpl context = new JobExecutionContextImpl(scheduler,
                    new TriggerFiredBundle(qJobDetail1,
                            (OperableTrigger) TriggerBuilder.newTrigger().withIdentity(
                                    TriggerKey.triggerKey(jobName, groupName)).build(),
                            new BaseCalendar(), false, null,
                            null, null, null), job);
            job.execute(context);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    private JobDetail getJobDetail(String uuid) {
        JobDetails details = dao.getOne(uuid);
        return JobDetailUtils.getJobDetail(details);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#pause(java.lang.String)
     */
    @Override
    @Transactional
    public void pause(String uuid) {
        JobDetail jobDetail = getJobDetail(uuid);
        if (!Config.getAppEnv().equalsIgnoreCase(Config.ENV_DEV) && jobDetail.isAutoScheduling()) {
            throw new RuntimeException("任务[" + jobDetail.getName() + "]为自动调度的任务，不能暂停！");
        }
        schedulerService.pauseJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#resume(java.lang.String)
     */
    @Override
    @Transactional
    public void resume(String uuid) {
        JobDetail jobDetail = getJobDetail(uuid);
        schedulerService.resumeJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#stop(java.lang.String)
     */
    @Override
    @Transactional
    public void stop(String uuid) {
        JobDetail jobDetail = getJobDetail(uuid);
        if (Context.isDebug() || "true".equals(allowStopJob) || Config.getAppEnv().equalsIgnoreCase(Config.ENV_DEV)
                || !jobDetail.isAutoScheduling()) {
            schedulerService.stopJob(jobDetail);
            return;
        }
        throw new RuntimeException("任务[" + jobDetail.getName() + "]为自动调度的任务，不能停止！");
    }

    /**
     * @param uuid
     */
    private void removeJob(String uuid) {
        try {
            JobDetail jobDetail = getJobDetail(uuid);
            schedulerService.deleteJob(jobDetail);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#startAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void startAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.start(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#pauseAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void pauseAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.pause(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#resumeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void resumeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.resume(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#stopAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void stopAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.stop(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#removeByName(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByName(String name) {
        this.dao.deleteByName(name);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#listAutoScheduling()
     */
    @Override
    public List<JobDetails> listAutoScheduling() {
        return this.dao.listByFieldEqValue("autoScheduling", true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobDetailsService#isRunning(java.lang.String)
     */
    @Override
    public boolean isRunning(String uuid) {
        JobDetails details = this.getOne(uuid);
        try {
            String triggerName = details.getName();
            String triggerGroup = details.getTenantId();
            if (scheduler == null) {
                return true;
            }
            Trigger.TriggerState triggerState = scheduler.getTriggerState(
                    TriggerKey.triggerKey(triggerName, triggerGroup));
            return Trigger.TriggerState.NORMAL.equals(triggerState);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public Select2QueryData loadJobClassSelection(Select2QueryInfo queryInfo) throws Exception {
        Map<String, Class<?>> jobClasses = ClassUtils.getJobClasses();
        Select2QueryData result = new Select2QueryData();
        Iterator iterator = jobClasses.keySet().iterator();

        while (iterator.hasNext()) {
            String simpleName = (String) iterator.next();
            Class clazz = jobClasses.get(simpleName);
            Description description = (Description) clazz.getAnnotation(Description.class);
            if (description != null && StringUtils.isNotBlank(description.value())) {
                simpleName = description.value();
            }
            try {

                result.addResultData(
                        new Select2DataBean(clazz.getCanonicalName(), simpleName));
            } catch (Exception e) {
                logger.error("获取实体异常：", e);
                throw new RuntimeException(e);

            }

        }

        return result;
    }

    @Override
    public List<JobDetails> listByJobClassName(String jobClassName) {
        return this.dao.listByFieldEqValue("jobClassName", jobClassName);
    }

    @Override
    @Transactional
    public void updateLastExecuteInstance(String jobName, String instance) {
        this.dao.updateLastExecuteInstance(jobName, instance);
    }


}

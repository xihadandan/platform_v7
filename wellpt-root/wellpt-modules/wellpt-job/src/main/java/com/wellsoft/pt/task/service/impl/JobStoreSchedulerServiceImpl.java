/*
 * @(#)2013-10-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.task.job.JobDetail;
import com.wellsoft.pt.task.service.JobStoreSchedulerService;
import com.wellsoft.pt.task.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Description: 供外部API类TaskApiFacade调用的服务类，将任务信息同时保存到数据库
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-18.1	zhulh		2013-10-18		Create
 * </pre>
 * @date 2013-10-18
 */
@Service
@Transactional
public class JobStoreSchedulerServiceImpl extends BaseServiceImpl implements JobStoreSchedulerService {

    @Autowired
    private SchedulerService scheduleService;

    // @Autowired
    // private JobDetailsService jobDetailsService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobStoreSchedulerService#startJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public Date startJob(JobDetail jobDetail) {
        Date date = scheduleService.startJob(jobDetail);
        // JobDetails jobDetails = getJobDetails(jobDetail);

        // if (SpringSecurityUtils.getCurrentUser() == null &&
        // !IgnoreLoginUtils.isIgnoreLogin()) {
        // try {
        // IgnoreLoginUtils.login(jobDetail.getTenantId(),
        // jobDetail.getUserId());
        // jobDetailsService.save(jobDetails);
        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        // IgnoreLoginUtils.logout();
        // }
        // } else {
        // jobDetailsService.save(jobDetails);
        // }

        return date;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobStoreSchedulerService#restartJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public Date restartJob(JobDetail jobDetail) {
        Date date = scheduleService.restartJob(jobDetail);
        return date;
    }

    /**
     * @param jobDetail
     * @return
     */
    // private JobDetails getJobDetails(JobDetail jobDetail) {
    // JobDetails details = new JobDetails();
    // details.setName(jobDetail.getName());
    // details.setJobClassName(jobDetail.getJobClass().getCanonicalName());
    // details.setType(jobDetail.getType());
    // details.setExpression(jobDetail.getExpression());
    // details.setStartTime(jobDetail.getStartTime());
    // details.setEndTime(jobDetail.getEndTime());
    // details.setRepeatCount(jobDetail.getRepeatCount());
    // details.setRepeatInterval(jobDetail.getRepeatInterval());
    // details.setTenantId(jobDetail.getTenantId());
    // return details;
    // }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobStoreSchedulerService#pauseJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public void pauseJob(JobDetail jobDetail) {
        scheduleService.pauseJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobStoreSchedulerService#resumeJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public void resumeJob(JobDetail jobDetail) {
        scheduleService.resumeJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobStoreSchedulerService#deleteJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public boolean deleteJob(JobDetail jobDetail) {
        boolean result = scheduleService.deleteJob(jobDetail);
        if (!result) {
            return result;
        }

        // if (SpringSecurityUtils.getCurrentUser() == null &&
        // !IgnoreLoginUtils.isIgnoreLogin()) {
        // try {
        // IgnoreLoginUtils.login(jobDetail.getTenantId(),
        // jobDetail.getUserId());
        // jobDetailsService.removeByName(jobDetail.getName());
        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        // IgnoreLoginUtils.logout();
        // }
        // } else {
        // jobDetailsService.removeByName(jobDetail.getName());
        // }
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobStoreSchedulerService#isExists(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isExists(JobDetail jobDetail) {
        return scheduleService.isExists(jobDetail);
    }

}

/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service.impl;

import com.wellsoft.pt.task.job.JobDetail;
import com.wellsoft.pt.task.scheduler.SchedulerWrapper;
import com.wellsoft.pt.task.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Description: 任务调度服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
@Service
@Transactional
public class SchedulerServiceImpl implements SchedulerService {
    // @Autowired
    // private JobDetailDao jobDetailDao;

    @Autowired
    private SchedulerWrapper schedulerWrapper;

    // /**
    // * (non-Javadoc)
    // * @see com.wellsoft.pt.task.service.SchedulerService#initJobs()
    // */
    // @Override
    // public void initJobs() {
    // List<JobDetail> jobDetails = jobDetailDao.getAll();
    // for (JobDetail jobDetail : jobDetails) {
    // startJob(jobDetail, null);
    // }
    // }
    //
    // /**
    // * (non-Javadoc)
    // * @see
    // com.wellsoft.pt.task.service.SchedulerService#saveJob(com.wellsoft.pt.task.support.JobDetail)
    // */
    // @Override
    // public void saveJob(JobDetail jobDetail) {
    // jobDetailDao.save(jobDetail);
    // }

    // /**
    // * (non-Javadoc)
    // * @see
    // com.wellsoft.pt.task.service.SchedulerService#getJobDetail(java.lang.Class)
    // */
    // @Override
    // public JobDetail getJobDetail(Class<?> jobClass) {
    // return jobDetailDao.findUniqueBy("jobClassName",
    // jobClass.getClass().getCanonicalName());
    // }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.SchedulerService#startJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public Date startJob(JobDetail jobDetail) {
        return schedulerWrapper.startJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.SchedulerService#restartJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public Date restartJob(JobDetail jobDetail) {
        return schedulerWrapper.restartJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.SchedulerService#triggerJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public void triggerJob(JobDetail jobDetail) {
        schedulerWrapper.triggerJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.SchedulerService#pauseJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public void pauseJob(JobDetail jobDetail) {
        schedulerWrapper.pauseJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.SchedulerService#resumeJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public void resumeJob(JobDetail jobDetail) {
        schedulerWrapper.resumeJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.SchedulerService#stopJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public void stopJob(JobDetail jobDetail) {
        schedulerWrapper.stopJob(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.SchedulerService#deleteJob(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    public boolean deleteJob(JobDetail jobDetail) {
        boolean result = schedulerWrapper.deleteJob(jobDetail);
        return result;
        // 从数据库中删除
        // if (Boolean.TRUE.equals(result) &&
        // StringUtils.isNotBlank(jobDetail.getUuid())) {
        // jobDetailDao.delete(jobDetail);
        // } else {
        // throw new JobDetailDeleteFailureException("JobDetail " +
        // jobDetail.getName() + " delete failure");
        // }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.SchedulerService#isExists(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isExists(JobDetail jobDetail) {
        return schedulerWrapper.isExists(jobDetail);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.SchedulerService#getState(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    @Transactional(readOnly = true)
    public int getState(JobDetail jobDetail) {
        return schedulerWrapper.getState(jobDetail);
    }

}

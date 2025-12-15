/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.facade;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.task.job.JobDetail;
import com.wellsoft.pt.task.service.JobStoreSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-25.1	rzhu		2013-5-25		Create
 * </pre>
 * @date 2013-5-25
 */
@Component
public class TaskApiFacade extends AbstractApiFacade {
    @Autowired
    private JobStoreSchedulerService schedulerService;

    /**
     * 调度任务
     *
     * @param jobDetail
     * @return
     */
    public Date startJob(JobDetail jobDetail) {
        logger.debug("启动任务: " + jobDetail.getName());
        return schedulerService.startJob(jobDetail);
    }

    /**
     * 重新启动任务
     *
     * @param jobDetail
     */
    public void restartJob(JobDetail jobDetail) {
        logger.debug("重启任务: " + jobDetail.getName());
        schedulerService.restartJob(jobDetail);
    }

    /**
     * 暂停任务
     *
     * @param jobDetail
     */
    public void pauseJob(JobDetail jobDetail) {
        logger.debug("暂停任务: " + jobDetail.getName());
        schedulerService.pauseJob(jobDetail);
    }

    /**
     * 恢复任务
     *
     * @param jobDetail
     */
    public void resumeJob(JobDetail jobDetail) {
        logger.debug("恢复任务: " + jobDetail.getName());
        schedulerService.resumeJob(jobDetail);
    }

    /**
     * 停止任务
     *
     * @param jobDetail
     */
    public void deleteJob(JobDetail jobDetail) {
        logger.debug("停止任务: " + jobDetail.getName());
        schedulerService.deleteJob(jobDetail);
    }

    /**
     * 判断任务是否存在任务
     *
     * @param jobDetail
     * @return
     */
    public boolean isExists(JobDetail jobDetail) {
        return schedulerService.isExists(jobDetail);
    }
}

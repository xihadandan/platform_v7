/*
 * @(#)2013-10-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.task.job.JobDetail;

import java.util.Date;

/**
 * Description: 如何描述该类
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
public interface JobStoreSchedulerService extends BaseService {
    /**
     * 调度任务
     *
     * @param jobDetail
     * @return
     */
    Date startJob(JobDetail jobDetail);

    /**
     * 重新启动任务，更新新的配置信息
     *
     * @param jobDetail
     */
    Date restartJob(JobDetail jobDetail);

    /**
     * 暂停任务
     *
     * @param jobDetail
     */
    void pauseJob(JobDetail jobDetail);

    /**
     * 如何描述该方法
     *
     * @param jobDetail
     */
    void resumeJob(JobDetail jobDetail);

    /**
     * 删除任务
     *
     * @param jobDetail
     */
    boolean deleteJob(JobDetail jobDetail);

    /**
     * 判断任务是否存在任务
     *
     * @param jobDetail
     * @return
     */
    public boolean isExists(JobDetail jobDetail);

}

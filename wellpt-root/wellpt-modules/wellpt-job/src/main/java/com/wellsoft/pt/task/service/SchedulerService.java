/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service;

import com.wellsoft.pt.task.job.JobDetail;

import java.util.Date;

/**
 * Description: 任务调度服务类
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
public interface SchedulerService {
    //	/**
    //	 * 初始化任务，目前没有使用集群，暂时使用这个方法初始化数据
    //	 *
    //	 * @param jobDetail
    //	 */
    //	void initJobs();
    //
    //	/**
    //	 * 保存任务，若不存在新增，若存在则更新
    //	 *
    //	 * @param jobDetail
    //	 */
    //	void saveJob(JobDetail jobDetail);

    //	/**
    //	 * 根据任务类名返回任务的详细信息
    //	 *
    //	 * @param jobCls
    //	 * @return
    //	 */
    //	JobDetail getJobDetail(Class<?> jobClass);

    /**
     * 调度任务
     *
     * @param jobDetail
     * @return
     */
    Date startJob(JobDetail jobDetail);

    /**
     * 重新加载调度任务
     *
     * @param jobDetail
     * @return
     */
    Date restartJob(JobDetail jobDetail);

    /**
     * 触发任务立即执行
     *
     * @param jobDetail
     * @return
     */
    void triggerJob(JobDetail jobDetail);

    /**
     * 暂停任务
     *
     * @param jobDetail
     */
    void pauseJob(JobDetail jobDetail);

    /**
     * 恢复任务
     *
     * @param jobDetail
     */
    void resumeJob(JobDetail jobDetail);

    /**
     * 停止任务
     *
     * @param jobDetail
     */
    void stopJob(JobDetail jobDetail);

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
     */
    boolean isExists(JobDetail jobDetail);

    /**
     * 获取任务状态
     *
     * @param jobDetail
     * @return
     */
    int getState(JobDetail jobDetail);

}

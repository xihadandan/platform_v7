/*
 * @(#)2013-9-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.task.bean.JobDetailsBean;
import com.wellsoft.pt.task.dao.JobDetailsDao;
import com.wellsoft.pt.task.entity.JobDetails;

import java.util.Collection;
import java.util.List;

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
public interface JobDetailsService extends JpaService<JobDetails, JobDetailsDao, String> {
    /**
     * 根据配置信息获取任务信息
     *
     * @param uuid
     * @return
     */
    JobDetailsBean getBean(String uuid);

    /**
     * 保存任务信息
     *
     * @param bean
     */
    void saveBean(JobDetailsBean bean);

    /**
     * 当前任务列表查询
     *
     * @param queryInfo
     * @return
     */
    List<JobDetails> query(QueryInfo queryInfo);

    /**
     * 根据UUID，删除任务信息
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 根据UUID，批量删除任务信息
     *
     * @param uuids
     */
    void removeAll(Collection<String> uuids);

    /**
     * 根据任务信息名称，删除任务
     *
     * @param name
     */
    void removeByName(String name);

    /**
     * 根据任务信息UUID，启动任务
     *
     * @param uuid
     */
    void start(String uuid);


    /**
     * 调试任务
     *
     * @param uuid
     */
    void debugJob(String uuid);

    /**
     * 根据任务信息UUID，暂停任务
     *
     * @param uuid
     */
    void pause(String uuid);

    /**
     * 根据任务信息UUID，恢复任务
     *
     * @param uuid
     */
    void resume(String uuid);

    /**
     * 根据任务信息UUID，批量启动任务
     *
     * @param uuids
     */
    void startAll(Collection<String> uuids);

    /**
     * 根据任务信息UUID，停止任务
     *
     * @param uuid
     */
    void stop(String uuid);

    /**
     * 根据任务信息UUID，批量暂停任务
     *
     * @param uuids
     */
    void pauseAll(Collection<String> uuids);

    /**
     * 根据任务信息UUID，批量恢复任务
     *
     * @param uuids
     */
    void resumeAll(Collection<String> uuids);

    /**
     * 根据任务信息UUID，批量停止任务
     *
     * @param uuids
     */
    void stopAll(Collection<String> uuids);

    /**
     * 获取自动调度的任务配置
     *
     * @return
     */
    List<JobDetails> listAutoScheduling();

    /**
     * 检查任务是否在运行
     *
     * @param jobDetailsUuid
     * @return
     */
    boolean isRunning(String uuid);


    /**
     * 获取定时任务调度类下拉选择
     *
     * @param queryInfo
     * @return
     * @throws Exception
     */
    Select2QueryData loadJobClassSelection(Select2QueryInfo queryInfo) throws Exception;

    List<JobDetails> listByJobClassName(String jobClassName);

    void updateLastExecuteInstance(String jobName, String instance);
}

/*
 * @(#)2015-5-1 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskFormOpinionLogDao;
import com.wellsoft.pt.bpm.engine.entity.TaskFormOpinionLog;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2015-5-1.1	zhulh		2015-5-1		Create
 * </pre>
 * @date 2015-5-1
 */
public interface TaskFormOpinionLogService extends JpaService<TaskFormOpinionLog, TaskFormOpinionLogDao, String> {

    TaskFormOpinionLog get(String uuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskFormOpinionLog> getByFlowInstUuid(String flowInstUuid);

    /**
     * @param taskInstUuid
     * @param flowInstUuid
     * @param recordField
     * @param recordValue
     * @param valueOf
     * @return
     */
    String log(String taskInstUuid, String flowInstUuid, String taskFormOpinionUuid, String fieldName, String content,
               Integer recordWay);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    List<TaskFormOpinionLog> getByTaskOperationUuid(String taskOperationUuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void removeByTaskOperationUuid(String taskOperationUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param example
     * @param orderBy
     * @return
     */
    List<TaskFormOpinionLog> findByExample(TaskFormOpinionLog example, String orderBy);

    long countByEntity(TaskFormOpinionLog entity);
}

/*
 * @(#)2014-3-9 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskSubFlowRelationDao;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlowRelation;
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
 * 2014-3-9.1	zhulh		2014-3-9		Create
 * </pre>
 * @date 2014-3-9
 */
public interface TaskSubFlowRelationService extends JpaService<TaskSubFlowRelation, TaskSubFlowRelationDao, String> {

    /**
     * 根据流程实例UUID获取子流程
     *
     * @param taskSubFlowUuid
     * @return
     */
    public List<TaskSubFlowRelation> getByTaskSubFlowUuid(String taskSubFlowUuid);

    /**
     * 如何描述该方法
     *
     * @param taskSubFlowUuid
     */
    public void removeByTaskSubFlowUuid(String taskSubFlowUuid);

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    public List<TaskSubFlowRelation> findByExample(TaskSubFlowRelation example);
}

/*
 * @(#)2014-3-9 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskSubFlowRelationDao;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlowRelation;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowRelationService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Service
public class TaskSubFlowRelationServiceImpl extends
        AbstractJpaServiceImpl<TaskSubFlowRelation, TaskSubFlowRelationDao, String> implements
        TaskSubFlowRelationService {

    private static final String GET_BY_TASK_SUB_FLOW_UUID = "from TaskSubFlowRelation t where t.taskSubFlow.uuid = :taskSubFlowUuid";

    private static final String REMOVE_BY_TASK_SUB_FLOW_UUID = "delete from TaskSubFlowRelation t where t.taskSubFlow.uuid = :taskSubFlowUuid";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowRelationService#getByTaskSubFlowUuid(java.lang.String)
     */
    @Override
    public List<TaskSubFlowRelation> getByTaskSubFlowUuid(String taskSubFlowUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskSubFlowUuid", taskSubFlowUuid);
        return this.dao.listByHQL(GET_BY_TASK_SUB_FLOW_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowRelationService#removeByTaskSubFlowUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByTaskSubFlowUuid(String taskSubFlowUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskSubFlowUuid", taskSubFlowUuid);
        this.dao.deleteByHQL(REMOVE_BY_TASK_SUB_FLOW_UUID, values);

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowRelationService#findByExample(com.wellsoft.pt.bpm.engine.entity.TaskSubFlowRelation)
     */
    @Override
    public List<TaskSubFlowRelation> findByExample(TaskSubFlowRelation example) {
        return dao.listByEntity(example);
    }
}

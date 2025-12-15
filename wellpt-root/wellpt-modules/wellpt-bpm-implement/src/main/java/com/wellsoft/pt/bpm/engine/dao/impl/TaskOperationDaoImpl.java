/*
 * @(#)2012-11-28 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskOperationDao;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

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
 * 2012-11-28.1	zhulh		2012-11-28		Create
 * </pre>
 * @date 2012-11-28
 */
@Repository
public class TaskOperationDaoImpl extends AbstractJpaDaoImpl<TaskOperation, String> implements TaskOperationDao {

    private static final String QUERY_ASSIGNEE_BY_TASK_INST_UUID = "select assignee as assignee from TaskOperation task_operation where task_operation.taskInstUuid = :taskInstUuid";

    private static final String QUERY_BY_FLOW_INST_UUID = "from TaskOperation task_operation where task_operation.flowInstUuid = :flowInstUuid order by createTime asc";

    private static final String COUNT_OPINION = "select count(*) from TaskOperation task_operation where task_operation.opinionValue = :opinionValue and task_operation.taskId = :taskId and task_operation.flowInstUuid = :flowInstUuid";

    private static final String COUNT_ALL_OPINION = "select count(*) from TaskOperation task_operation where task_operation.taskId = :taskId and task_operation.flowInstUuid = :flowInstUuid and task_operation.opinionValue is not null";

    private static final String QUERY_BY_TASK_INST_UUID = "from TaskOperation task_operation where task_operation.taskInstUuid = :taskInstUuid order by createTime asc";

    private static final String QUERY_TASK_INST_UUID_BY_USER_ID = "select t.taskInstUuid from TaskOperation t where t.flowInstUuid = :flowInstUuid and t.assignee = :assignee order by t.createTime desc";

    /**
     * @param flowInstUuid
     * @return
     */
    @Override
    public List<TaskOperation> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return listByHQL(QUERY_BY_FLOW_INST_UUID, values);
    }

    /**
     * 统计流程环节中意见立场为指定值的数量
     *
     * @param opinionValue
     * @param taskId
     * @param flowInstUuid
     * @return
     */
    @Override
    public Long countOpinion(String opinionValue, String taskId, String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("opinionValue", opinionValue);
        values.put("taskId", taskId);
        values.put("flowInstUuid", flowInstUuid);
        return this.getNumberByHQL(COUNT_OPINION, values);
    }

    /**
     * 统计流程环节中意见立场不办空的总数量
     *
     * @param taskId
     * @param flowInstUuid
     * @return
     */
    @Override
    public Long countAllOpinion(String taskId, String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskId", taskId);
        values.put("flowInstUuid", flowInstUuid);
        return this.getNumberByHQL(COUNT_ALL_OPINION, values);
    }

    /**
     * @param taskInstUuid
     * @return
     */
    @Override
    public List<TaskOperation> getByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.listByHQL(QUERY_BY_TASK_INST_UUID, values);
    }

}

/*
 * @(#)2012-11-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskCounterSignDao;
import com.wellsoft.pt.bpm.engine.entity.TaskCounterSign;
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
 * 2012-11-29.1	zhulh		2012-11-29		Create
 * </pre>
 * @date 2012-11-29
 */
@Repository
public class TaskCounterSignDaoImpl extends AbstractJpaDaoImpl<TaskCounterSign, String> implements TaskCounterSignDao {

    private static final String IS_COUNTER_SIGNING = "select count(*) from TaskCounterSign task_counter_sign where task_counter_sign.taskInstUuid = :taskInstUuid and task_counter_sign.parent.uuid is null";

    private static final String GET_BY_TASK_AND_USER = "from TaskCounterSign task_counter_sign where task_counter_sign.taskInstUuid = :taskInstUuid and task_counter_sign.assignee = :assignee";

    private static final String GET_BY_TASK_INST = "from TaskCounterSign task_counter_sign where task_counter_sign.taskInstUuid = :taskInstUuid";

    // 获取已存在的会签发起人
    private static final String QUERY_CREATORS = "select distinct task_counter_sign.owner from TaskCounterSign task_counter_sign where task_counter_sign.taskInstUuid = :taskInstUuid";

    /**
     * 判断任务是否在会签中
     *
     * @param taskInstUuid
     * @return
     */
    public boolean isCounterSigning(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return (Long) this.getNumberByHQL(IS_COUNTER_SIGNING, values) > 0;
    }

    /**
     * 根据任务UUID及用户ID获取任务会签实体
     *
     * @param taskInstUuid
     * @return
     */
    public TaskCounterSign getByTaskInst(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.getOneByHQL(GET_BY_TASK_INST, values);
    }

    /**
     * 根据任务UUID及用户ID获取任务会签实体
     *
     * @param taskInstUuid
     * @param assignee
     * @return
     */
    public List<TaskCounterSign> getCounterSigns(String taskInstUuid, String assignee) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("assignee", assignee);
        return this.listByHQL(GET_BY_TASK_AND_USER, values);
    }

    /**
     * 获取已存在的会签发起人
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getCountSignCreators(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return (List<String>) this.getCharSequenceByHQL(QUERY_CREATORS, values);
    }

}

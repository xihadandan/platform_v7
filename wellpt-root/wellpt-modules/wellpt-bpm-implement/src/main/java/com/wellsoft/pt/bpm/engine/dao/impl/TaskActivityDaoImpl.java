/*
 * @(#)2013-4-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskActivityDao;
import com.wellsoft.pt.bpm.engine.entity.TaskActivity;
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
 * 2013-4-6.1	zhulh		2013-4-6		Create
 * </pre>
 * @date 2013-4-6
 */
@Repository
public class TaskActivityDaoImpl extends AbstractJpaDaoImpl<TaskActivity, String> implements TaskActivityDao {

    private static final String QUERY_HIS_BY_FLOW_INST_UUID = "from TaskActivity task_activity where task_activity.flowInstUuid = :flowInstUuid and task_activity.endTime is not null";

    private static final String QUERY_BY_FLOW_INST_UUID = "from TaskActivity task_activity where task_activity.flowInstUuid = :flowInstUuid order by createTime asc";

    /**
     * @param flowInstUuid
     * @return
     */
    @Override
    public List<TaskActivity> getHistoryActivities(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.listByHQL(QUERY_HIS_BY_FLOW_INST_UUID, values);
    }

    /**
     * 获取流程流转日志
     *
     * @param flowInstUuid
     * @return
     */
    @Override
    public List<TaskActivity> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.listByHQL(QUERY_BY_FLOW_INST_UUID, values);
    }
}

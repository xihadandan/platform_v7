/*
 * @(#)2012-11-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.FlowInstanceDao;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-19.1	zhulh		2012-11-19		Create
 * </pre>
 * @date 2012-11-19
 */
@Repository
public class FlowInstanceDaoImpl extends AbstractJpaDaoImpl<FlowInstance, String> implements FlowInstanceDao {

    private static final String QUERY_ACTIVE_SUB_FLOW = "select count(flow_instance.uuid) from FlowInstance flow_instance where flow_instance.parent.uuid = :parentFlowInstUuid and flow_instance.isActive = :isActive and flow_instance.endTime is null"
            + " and exists(select task_sub_flow.uuid from TaskSubFlow task_sub_flow where flow_instance.uuid = task_sub_flow.flowInstUuid and task_sub_flow.parentFlowInstUuid = :parentFlowInstUuid and task_sub_flow.isMerge = :isMerge)";

    private static final String COUNT_BY_FLOW_DEF_UUID = "select count(*) from FlowInstance flow_instance where flow_instance.flowDefinition.uuid = :flowDefUuid";

    private static final String QUERY_ACTIVE_COUNT_BY_FLOW_DEF_UUID = "select count(flow_instance.uuid) from FlowInstance flow_instance where flow_instance.isActive = :isActive and flow_instance.endTime is null and flow_instance.flowDefinition.uuid = :flowDefUuid";

    /**
     * 判断流程的所有子流程是否已经全部结束
     *
     * @param parentFlowInstUuid
     * @return
     */
    @Override
    public boolean isAllAsyncSubFlowCompleted(String parentFlowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("parentFlowInstUuid", parentFlowInstUuid);
        values.put("isActive", true);
        values.put("isMerge", true);
        return (Long) this.getNumberByHQL(QUERY_ACTIVE_SUB_FLOW, values) < 1;
    }

    /**
     * 计算流程定义被使用的流程实例数量
     *
     * @param flowDefUuid
     * @return
     */
    @Override
    public Long countByFlowDefUuid(String flowDefUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowDefUuid", flowDefUuid);
        return this.getNumberByHQL(COUNT_BY_FLOW_DEF_UUID, values);
    }

    @Override
    public Long countActivityByFlowDefUuid(String flowDefUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowDefUuid", flowDefUuid);
        values.put("isActive", true);
        return this.getNumberByHQL(QUERY_ACTIVE_COUNT_BY_FLOW_DEF_UUID, values);
    }

}

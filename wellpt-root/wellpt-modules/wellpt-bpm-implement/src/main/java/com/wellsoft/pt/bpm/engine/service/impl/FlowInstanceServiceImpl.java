/*
 * @(#)2013-3-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.bpm.engine.dao.FlowDefinitionDao;
import com.wellsoft.pt.bpm.engine.dao.FlowInstanceDao;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 流程实例服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-17.1	zhulh		2013-3-17		Create
 * </pre>
 * @date 2013-3-17
 */
@Service
public class FlowInstanceServiceImpl extends AbstractJpaServiceImpl<FlowInstance, FlowInstanceDao, String> implements
        FlowInstanceService {

    private final String GET_BY_TASK_INST_UUID = "from FlowInstance t1 where t1.uuid = (select t2.flowInstance.uuid from TaskInstance t2 where t2.uuid = :taskInstUuid)";

    private final String COUNT_FLOW_MANAGEMENT = "select count(uuid) from FlowManagement t1 where t1.type = :type "
            + " and exists (select distinct t2.uuid from TaskInstance t2 where t2.uuid = :taskInstUuid and t1.flowDefUuid = t2.flowDefinition.uuid) "
            + " and t1.orgId in (:orgIds)";

    private final String COUNT_ALL_FLOW_MANAGEMENT = "select count(uuid) from FlowManagement t1 where 1 = 1 "
            + " and exists (select distinct t2.uuid from TaskInstance t2 where t2.uuid = :taskInstUuid and t1.flowDefUuid = t2.flowDefinition.uuid) "
            + " and t1.orgId in (:orgIds)";

    private final String COUNT_BY_PARENT_UUID = "select count(uuid) from FlowInstance t where t.parent.uuid = :flowInstUuid";

    private final String GET_UNFINISHED_SUB_FLOW_INSTANCE = "from FlowInstance t where t.parent.uuid = :flowInstUuid and t.endTime is null order by t.createTime asc";

    private final String GET_FLOW_DEFINITION_BY_UUID = "from FlowDefinition t1 where t1.uuid = (select t2.flowDefinition.uuid from FlowInstance t2 where t2.uuid = :flowInstUuid)";

    private final String LIST_UUID_BY_DATA_UUID = "select t.uuid as uuid from FlowInstance t where t.dataUuid = :dataUuid";

    private final String LIST_SYSTEM_BY_UUID = "select t.system as system from FlowInstance t where t.uuid = :uuid";

    @Autowired
    private FlowDefinitionDao flowDefinitionDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#get(java.lang.String)
     */
    @Override
    public FlowInstance get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#getByTaskInstUuid(java.lang.String)
     */
    @Override
    public FlowInstance getByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.getOneByHQL(GET_BY_TASK_INST_UUID, values);
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl#save(com.wellsoft.context.jdbc.entity.IdEntity)
     */
    @Override
    @Transactional
    public void save(FlowInstance entity) {
        super.save(entity);
    }

    @Override
    @Transactional
    public void merge(FlowInstance flowInstance) {
        this.dao.getSession().merge(flowInstance);
    }

    /**
     * 判断流程的所有同步子流程是否已经全部结束
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#isAllSubFlowCompleted(com.wellsoft.pt.bpm.engine.entity.FlowInstance)
     */
    @Override
    public boolean isAllAsyncSubFlowCompleted(FlowInstance flowInstance) {
        return dao.isAllAsyncSubFlowCompleted(flowInstance.getUuid());
    }

    /**
     * 判断指定的流程定义UUID是否已经被流程实例使用
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#isFlowDefInUse(java.lang.String)
     */
    @Override
    public boolean isFlowDefInUse(String flowDefUuid) {
        return dao.countByFlowDefUuid(flowDefUuid) > 0;
    }

    /**
     * 根据流程实例UUID删除流程实例
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String flowInstUuid) {
        dao.delete(flowInstUuid);
    }

    @Override
    public boolean isFlowActivityDefInUse(String flowDefUuid) {
        return dao.countActivityByFlowDefUuid(flowDefUuid) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#countAllFlowManagement(java.util.Map)
     */
    @Override
    public Long countAllFlowManagement(Map<String, Object> values) {
        return dao.getNumberByHQL(COUNT_ALL_FLOW_MANAGEMENT, values);
    }

    @Override
    public <X> List<X> find(String queryString, Map<String, Object> values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = dao.getSession().createQuery(queryString);
        if (values != null) {
            query.setProperties(values);
        }
        return query.list();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#countFlowManagement(java.util.Map)
     */
    @Override
    public Long countFlowManagement(Map<String, Object> values) {
        return dao.getNumberByHQL(COUNT_FLOW_MANAGEMENT, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#isMainFlowInstance(java.lang.String)
     */
    @Override
    public boolean isMainFlowInstance(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        Long count = dao.getNumberByHQL(COUNT_BY_PARENT_UUID, values);
        return count > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#getUnfinishedSubFlowInstances(java.lang.String)
     */
    @Override
    public List<FlowInstance> getUnfinishedSubFlowInstances(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return dao.listByHQL(GET_UNFINISHED_SUB_FLOW_INSTANCE, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#getFlowDefinitionByUuid(java.lang.String)
     */
    @Override
    public FlowDefinition getFlowDefinitionByUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return flowDefinitionDao.getOneByHQL(GET_FLOW_DEFINITION_BY_UUID, values);
    }

    @Override
    public List<FlowDefinition> getFlowDefinitionByids(List<String> ids) {
        return flowDefinitionDao.listByFieldInValues("id", ids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceService#getUuidByDataUuid(java.lang.String)
     */
    @Override
    public String getUuidByDataUuid(String dataUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("dataUuid", dataUuid);
        List<String> flowInstUuids = this.dao.listCharSequenceByHQL(LIST_UUID_BY_DATA_UUID, values);
        if (CollectionUtils.isNotEmpty(flowInstUuids)) {
            return flowInstUuids.get(0);
        }
        return null;
    }

    /**
     * @param flowInstUuid
     * @return
     */
    @Override
    public String getSystemByUuid(String flowInstUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", flowInstUuid);
        List<String> systems = this.dao.listCharSequenceByHQL(LIST_SYSTEM_BY_UUID, values);
        if (CollectionUtils.isNotEmpty(systems)) {
            return systems.get(0);
        }
        return null;
    }

    @Override
    public boolean isSubFlowInstance(String flowInstUuid) {
        String hql = "select count(t.uuid) from FlowInstance t where t.uuid = :flowInstUuid and t.parent.uuid is not null";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.countByHQL(hql, values) > 0;
    }

}

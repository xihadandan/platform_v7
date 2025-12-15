/*
 * @(#)2015-4-9 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowManagement;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.service.FlowManagementService;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-4-9.1	zhulh		2015-4-9		Create
 * </pre>
 * @date 2015-4-9
 */
@Service
@Transactional
public class FlowManagementServiceImpl extends BaseServiceImpl implements FlowManagementService {

    //    @Autowired
//    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#create(java.lang.Integer, java.util.List, java.lang.String)
     */
    @Override
    public void create(Integer type, Collection<String> orgIds, String flowDefUuid) {
        for (String orgId : orgIds) {
            FlowManagement flowManagement = new FlowManagement();
            flowManagement.setType(type);
            flowManagement.setOrgId(orgId);
            flowManagement.setFlowDefUuid(flowDefUuid);
            this.dao.save(flowManagement);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#save(com.wellsoft.pt.bpm.engine.entity.FlowManagement)
     */
    @Override
    public void save(FlowManagement flowManagement) {
        this.dao.save(flowManagement);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#remove(java.lang.String, java.lang.Integer)
     */
    @Override
    public void remove(String flowDefUuid, Integer type) {
        FlowManagement example = new FlowManagement();
        example.setFlowDefUuid(flowDefUuid);
        example.setType(type);
        List<FlowManagement> flowManagements = this.dao.findByExample(example);
        for (FlowManagement flowManagement : flowManagements) {
            this.dao.delete(flowManagement);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#remove(java.lang.Integer, java.util.List, java.lang.String)
     */
    @Override
    public void remove(Integer type, List<String> orgIds, String flowDefUuid) {
        for (String orgId : orgIds) {
            FlowManagement example = new FlowManagement();
            example.setType(type);
            example.setOrgId(orgId);
            example.setFlowDefUuid(flowDefUuid);
            List<FlowManagement> flowManagements = this.dao.findByExample(example);
            for (FlowManagement flowManagement : flowManagements) {
                this.dao.delete(flowManagement);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#removeByFlowDefId(java.lang.Integer, java.util.List, java.lang.String)
     */
    @Override
    public void removeByFlowDefId(Integer type, List<String> orgIds, String flowDefId) {
        List<String> flowDefUuids = flowDefinitionService.getAllUuidsById(flowDefId);
        for (String flowDefUuid : flowDefUuids) {
            remove(type, orgIds, flowDefUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#hasPermission(java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    public boolean hasPermission(String userId, String taskInstUuid, Integer type) {
//        Set<String> userOrgIds = orgApiFacade.getUserOrgIds(userId);
        Set<String> userOrgIds = workflowOrgService.getUserRelatedIds(userId);
        List<String> deletes = new ArrayList<String>();
        for (String orgId : userOrgIds) {
            if (orgId.startsWith(IdPrefix.USER.getValue())) {
                deletes.add(orgId);
            }
        }
        userOrgIds.removeAll(deletes);
        userOrgIds.add(userId);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("type", type);
        values.put("taskInstUuid", taskInstUuid);
        values.put("orgIds", userOrgIds);
        return (Long) flowInstanceService.countFlowManagement(values) > 0;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#hasPermission(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasPermission(String userId, String taskInstUuid) {
//        Set<String> userOrgIds = orgApiFacade.getUserOrgIds(userId);
        Set<String> userOrgIds = workflowOrgService.getUserRelatedIds(userId);
        List<String> deletes = new ArrayList<String>();
        for (String orgId : userOrgIds) {
            if (orgId.startsWith(IdPrefix.USER.getValue())) {
                deletes.add(orgId);
            }
        }
        userOrgIds.removeAll(deletes);
        userOrgIds.add(userId);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("orgIds", userOrgIds);
        return (Long) flowInstanceService.countAllFlowManagement(values) > 0;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#upgrade(java.lang.String)
     */
    @Override
    public void upgrade(String flowDefUuid) {
        FlowDefinition flowDefinition = this.dao.get(FlowDefinition.class, flowDefUuid);
        if (flowDefinition == null) {
            return;
        }
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        if (flowDelegate.getFlow() != null) {
            List<UserUnitElement> viewers = flowDelegate.getFlow().getProperty().getViewers();
            if (viewers != null) {
                FlowDefinitionServiceImpl.setFlowViewers(flowDefinition, viewers);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#add(java.lang.Integer, java.util.List, java.lang.String)
     */
    @Override
    public void add(Integer type, List<String> orgIds, String flowDefUuid) {
        // 查找要新增的orgIds
        FlowManagement example = new FlowManagement();
        example.setFlowDefUuid(flowDefUuid);
        List<FlowManagement> flowManagements = this.dao.findByExample(example);
        List<String> ids = new ArrayList<String>();
        ids.addAll(orgIds);
        List<String> existsOrgIds = new ArrayList<String>();
        for (FlowManagement flowManagement : flowManagements) {
            existsOrgIds.add(flowManagement.getOrgId());
        }
        ids.removeAll(existsOrgIds);

        // 新增
        create(type, ids, flowDefUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowManagementService#addByFlowDefId(java.lang.Integer, java.util.List, java.lang.String)
     */
    @Override
    public void addByFlowDefId(Integer type, List<String> orgIds, String flowDefId) {
        List<String> flowDefUuids = flowDefinitionService.getAllUuidsById(flowDefId);
        for (String flowDefUuid : flowDefUuids) {
            add(type, orgIds, flowDefUuid);
        }
    }

    /**
     * @param userId
     * @param flowDefUuid
     * @return
     */
    @Override
    public List<Integer> listManagementPermission(String userId, String flowDefUuid) {
        String hql = "select distinct t.type from FlowManagement t where t.flowDefUuid = :flowDefUuid and t.orgId in(:orgIds)";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowDefUuid", flowDefUuid);
        values.put("orgIds", PermissionGranularityUtils.getCurrentUserSids());
        List<Integer> permissions = this.dao.query(hql, values, Integer.class);
        return permissions;
    }

}

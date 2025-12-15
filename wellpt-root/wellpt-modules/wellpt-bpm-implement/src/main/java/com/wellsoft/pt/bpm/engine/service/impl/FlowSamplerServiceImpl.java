/*
 * @(#)2019年9月5日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowVariables;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.support.event.FlowDataSnapshotCreateEvent;
import com.wellsoft.pt.document.MongoDocumentService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.criterion.Order;
import com.wellsoft.pt.jpa.event.EventListenerPair;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;

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
 * 2019年9月5日.1	zhulh		2019年9月5日		Create
 * </pre>
 * @date 2019年9月5日
 */
@Service
public class FlowSamplerServiceImpl extends WellptTransactionalEventListener<FlowDataSnapshotCreateEvent> implements FlowSamplerService {

    private static final String FLOW_DATA_PURPOSE = "snapshot";

    private static final String KEY_WF_SAMPLER_ENABLE = "wf.sampler.enable";

    private static final String KEY_WF_SAMPLER_FLOW_DEF_ID = "wf.sampler.flowDefId";

    public static final String MONGO_COLLECTION_NAME = "flow_data_snapshot";

    @Autowired
    private MongoDocumentService mongoDocumentService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskActivityService taskActivityService;

    /**
     * @param taskData
     * @param taskOperationUuid
     * @param taskInstUuid
     * @param flowInstance
     */
    @Override
    public void createSnapshot(TaskData taskData, String taskOperationUuid, String taskInstUuid, FlowInstance flowInstance) {
        // 流程定义ID过滤
        if (!isSampled(taskData, flowInstance)) {
            return;
        }

        FlowDataSnapshotCreateEvent flowDataSnapshotCreateEvent = new FlowDataSnapshotCreateEvent(taskOperationUuid, taskInstUuid, flowInstance.getUuid(), taskData, flowInstance.getSystem());
        ApplicationContextHolder.getApplicationContext().publishEvent(flowDataSnapshotCreateEvent);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowSamplerService#listWithoutDyformDataByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<FlowDataSnapshot> listWithoutDyformDataByFlowInstUuid(String flowInstUuid) {
        List<FlowDataSnapshot> flowDataSnapshots = Lists.newArrayList();
        List<Map<String, Object>> queryItems = mongoDocumentService.listByFieldEqValue(MONGO_COLLECTION_NAME, "flowInstUuid", flowInstUuid);

        queryItems.forEach(item -> {
            flowDataSnapshots.add(JsonUtils.json2Object(JsonUtils.object2Json(item), FlowDataSnapshot.class));
        });

//        // 按创建时间降序
//        Collections.sort(flowDataSnapshots, new Comparator<FlowDataSnapshot>() {
//
//            @Override
//            public int compare(FlowDataSnapshot o1, FlowDataSnapshot o2) {
//                Date t1 = o1.getCreateTime();
//                Date t2 = o2.getCreateTime();
//                if (t1 == null || t2 == null) {
//                    return 1;
//                }
//                return -t1.compareTo(t2);
//            }
//
//        });
        return flowDataSnapshots;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowSamplerService#getAsStringById(java.lang.String)
     */
    @Override
    public String getAsStringById(String id) {
        Map<String, Object> dataMap = mongoDocumentService.getOneAsMap(MONGO_COLLECTION_NAME, id);
        return JsonUtils.object2Json(dataMap);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowSamplerService#isSampled(java.lang.String)
     */
    @Override
    public boolean isSampled(String flowDefId) {
        FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
        if (flowDefinition == null) {
            return false;
        }
        return isSampled(flowDefinition);
    }

    private boolean isSampled(FlowDefinition flowDefinition) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        Map<String, Object> generalSettings = workFlowSettings.getGeneral();
        if (MapUtils.isEmpty(generalSettings)) {
            return false;
        }
        if (BooleanUtils.isNotTrue((Boolean) generalSettings.get("enabledAudit"))) {
            return false;
        }
        String auditScope = Objects.toString(generalSettings.get("auditScope"), StringUtils.EMPTY);
        if (StringUtils.equals(auditScope, "all")) {
            return true;
        }
        List<String> auditFlowDefIds = (List<String>) generalSettings.get("auditFlowDefIds");
        if (CollectionUtils.isEmpty(auditFlowDefIds)) {
            return false;
        }

        return auditFlowDefIds.contains(flowDefinition.getId())
                || auditFlowDefIds.contains(WorkFlowVariables.FLOW_CATEGORY_PREFIX + flowDefinition.getCategory());
    }

    /**
     * @param taskData
     * @param flowInstance
     * @return
     */
    @Override
    public boolean isSampled(TaskData taskData, FlowInstance flowInstance) {
        return isSampled(flowInstance.getFlowDefinition());
    }

    @Override
    public FlowDataSnapshotAuditLog getFlowDataSnapshotAuditLogByObjecId(String objectId) {
        Map<String, Object> dataMap = mongoDocumentService.getOneAsMap(MONGO_COLLECTION_NAME, objectId);
        String flowInstUuid = Objects.toString(dataMap.get("flowInstUuid"), StringUtils.EMPTY);
        Date createTime = (Date) dataMap.get("createTime");
        DBObject query = new BasicDBObject()
                .append("createTime", new BasicDBObject("$lt", createTime))
                .append("flowInstUuid", flowInstUuid);
        List<Order> orders = Lists.newArrayList(Order.desc("createTime"));
        List<QueryItem> queryItems = mongoDocumentService.listQueryItem(MONGO_COLLECTION_NAME, query, orders, new PagingInfo(1, 1));
        Map<String, Object> oldDataMap = CollectionUtils.isEmpty(queryItems) ? Collections.emptyMap() : queryItems.get(0);
        return FlowDataSnapshotAuditLog.create(dataMap, oldDataMap);
    }

    @Override
    public FlowDataDyformFieldModifyInfo getDyformFieldModifyInfo(String flowInstUuid, List<String> fieldNames) {
        DBObject query = new BasicDBObject()
                .append("flowInstUuid", flowInstUuid);
        List<Order> orders = Lists.newArrayList(Order.desc("createTime"));
        List<QueryItem> queryItems = mongoDocumentService.listQueryItem(MONGO_COLLECTION_NAME, query, orders, new PagingInfo(1, Integer.MAX_VALUE));
        FlowDataDyformFieldModifyInfo modifyInfo = null;
        if (CollectionUtils.isNotEmpty(queryItems)) {
            modifyInfo = FlowDataDyformFieldModifyInfo.create(fieldNames, queryItems);
        } else {
            TaskOperation taskOperation = taskOperationService.getLastestByFlowInstUuid(flowInstUuid);
            if (taskOperation != null) {
                modifyInfo = FlowDataDyformFieldModifyInfo.createDefaults(fieldNames, taskOperation.getAssigneeName(), taskOperation.getCreateTime());
            }
        }
        return modifyInfo;
    }

    @Override
    public boolean onAddEvent(List<EventListenerPair> eventListenerPairs, ApplicationEvent event) {
        FlowDataSnapshotCreateEvent event1 = (FlowDataSnapshotCreateEvent) event;
        for (EventListenerPair eventListenerPair : eventListenerPairs) {
            if (eventListenerPair.getEvent() instanceof FlowDataSnapshotCreateEvent) {
                FlowDataSnapshotCreateEvent event2 = (FlowDataSnapshotCreateEvent) eventListenerPair.getEvent();
                if (StringUtils.equals(event1.getFlowInstUuid(), event2.getFlowInstUuid())
                        && this.equals(eventListenerPair.getListener())) {
                    eventListenerPair.markIgnoreExecute();// 忽略重复的事件
                }
            }
        }
        return true;
    }

    @Override
    public TransactionPhase getPhase() {
        return TransactionPhase.BEFORE_COMMIT;
    }

    @Override
    public boolean isTransExecute() {
        return true;
    }

    @Override
    public void onApplicationEvent(FlowDataSnapshotCreateEvent event) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String flowInstUuid = event.getFlowInstUuid();
        String taskInstUuid = event.getTaskInstUuid();
        TaskData taskData = event.getTaskData();
        String formUuid = taskData.getFormUuid();
        String dataUuid = taskData.getDataUuid();
        String taskOperationUuid = event.getTaskOperationUuid();
        FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
        TaskInstance taskInstance = null;
        if (StringUtils.isBlank(formUuid) || StringUtils.isBlank(dataUuid)) {
            if (StringUtils.isNotBlank(taskInstUuid)) {
                taskInstance = taskService.getTask(taskInstUuid);
                formUuid = taskInstance.getFormUuid();
                dataUuid = taskInstance.getDataUuid();
            } else {
                formUuid = flowInstance.getFormUuid();
                dataUuid = flowInstance.getDataUuid();
            }
        }
        String taskName = StringUtils.EMPTY;
        String taskId = StringUtils.EMPTY;
        String actionName = StringUtils.EMPTY;
        String actionType = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(taskOperationUuid)) {
            TaskOperation taskOperation = taskOperationService.getOne(taskOperationUuid);
            if (taskOperation != null) {
                taskName = taskOperation.getTaskName();
                taskId = taskOperation.getTaskId();
                actionName = taskOperation.getAction();
                actionType = taskOperation.getActionType();
            }
        }
        if (StringUtils.isBlank(taskName) && StringUtils.isNotBlank(taskInstUuid)) {
            TaskActivity taskactivity = taskActivityService.getByCreatorAndPreTaskInstUuid(userDetails.getUserId(), taskInstUuid);
            if (taskactivity != null) {
                taskInstance = taskService.getTask(taskactivity.getTaskInstUuid());
            }
            if (taskInstance == null) {
                taskInstance = taskService.getTask(taskInstUuid);
            }
            if (taskInstance != null) {
                taskName = taskInstance.getName();
                taskId = taskInstance.getId();
                actionName = taskInstance.getAction();
                actionType = taskInstance.getActionType();
            }
            String key = taskInstUuid + userDetails.getUserId();
            if (taskData != null && StringUtils.isNotBlank(taskData.getAction(key)) && StringUtils.isNotBlank(taskData.getActionType(key))) {
                actionName = taskData.getAction(key);
                actionType = taskData.getActionType(key);
            }
        } else if (StringUtils.isBlank(actionType)) {
            String key = taskInstUuid + userDetails.getUserId();
            if (taskData != null && StringUtils.isNotBlank(taskData.getAction(key)) && StringUtils.isNotBlank(taskData.getActionType(key))) {
                actionName = taskData.getAction(key);
                actionType = taskData.getActionType(key);
            }
        }

        FlowDataSnapshot flowDataSnapshot = new FlowDataSnapshot();
        flowDataSnapshot.setUuid(UuidUtils.createUuid());
        flowDataSnapshot.setTitle(flowInstance.getTitle());
        flowDataSnapshot.setFlowName(flowInstance.getName());
        flowDataSnapshot.setFlowDefId(flowInstance.getId());
        flowDataSnapshot.setFlowInstUuid(flowInstUuid);
        flowDataSnapshot.setTaskInstUuid(taskInstUuid);
        flowDataSnapshot.setTaskName(taskName);
        flowDataSnapshot.setTaskId(taskId);
        flowDataSnapshot.setTaskOperationUuid(taskOperationUuid);
        flowDataSnapshot.setActionName(actionName);
        flowDataSnapshot.setActionType(actionType);
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        flowDataSnapshot.setFormUuid(dyFormData.getFormUuid());
        flowDataSnapshot.setDataUuid(dyFormData.getDataUuid());
        flowDataSnapshot.setFormDatas(JsonUtils.object2Json(dyFormData.getFormDatas()));
        flowDataSnapshot.setCreateUserId(userDetails.getUserId());
        flowDataSnapshot.setCreateUserName(userDetails.getUserName());
        flowDataSnapshot.setCreateTime(Calendar.getInstance().getTime());
        flowDataSnapshot.setSystem(flowInstance.getSystem());
        flowDataSnapshot.setTenant(userDetails.getTenantId());
        mongoDocumentService.save(MONGO_COLLECTION_NAME, flowDataSnapshot);
    }

}

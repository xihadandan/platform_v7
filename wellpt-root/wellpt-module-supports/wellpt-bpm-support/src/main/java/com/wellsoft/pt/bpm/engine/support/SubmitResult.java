/*
 * @(#)2015-3-20 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskOperationItem;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-20.1	Administrator		2015-3-20		Create
 * </pre>
 * @date 2015-3-20
 */
public class SubmitResult extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8737114814227458556L;

    // 提交的流程实例UUID
    private String flowInstUuid;

    // 提交的环节实例UUID
    private String fromTaskInstUuid;

    // 提交的环节实例ID
    private String fromTaskId;

    // 自动提交类型
    private String sameUserSubmitType;
    // 与前环节相同，可进行自动提交的流程环节名称
    private String sameUserSubmitTaskName;
    // 与前环节相同，可进行自动提交的流程环节实例UUID
    private String sameUserSubmitTaskInstUuid;
    // 与前环节相同，可进行自动提交的流程环节实例前环节操作UUID
    private String sameUserSubmitTaskOperationUuid;

    // 操作信息<环节实例UUID, 流程操作UUID>
    private List<TaskOperationItem> taskOperationItems = Lists.newArrayListWithCapacity(0);

    // 提交后生成子流程实例的UUID
    private List<String> subFlowInstUUids = Lists.newArrayListWithCapacity(0);

    // 子流程是否同步启动
    private boolean subFlowSyncStartd = false;

    // 提交后生成环节实例的UUID
    private List<String> taskInstUUids = Lists.newArrayList();

    // 打印结果的mongo文件ID
    private String printResultFileId;

    // 提交后进入下一环节的环节信息
    private List<Map<String, Object>> nextTasks = Lists.newArrayListWithExpectedSize(0);

    // 提交后进入下一环节的环节办理人ID<环节UUID，<办理人ID，办理人名称>>
    private Map<String, Map<String, String>> taskTodoUsers = Maps.newHashMapWithExpectedSize(0);

    // 其他数据
    private Object data;

    private Boolean formDataUpdated;

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the fromTaskInstUuid
     */
    public String getFromTaskInstUuid() {
        return fromTaskInstUuid;
    }

    /**
     * @param fromTaskInstUuid 要设置的fromTaskInstUuid
     */
    public void setFromTaskInstUuid(String fromTaskInstUuid) {
        this.fromTaskInstUuid = fromTaskInstUuid;
    }

    /**
     * @return the fromTaskId
     */
    public String getFromTaskId() {
        return fromTaskId;
    }

    /**
     * @param fromTaskId 要设置的fromTaskId
     */
    public void setFromTaskId(String fromTaskId) {
        this.fromTaskId = fromTaskId;
    }

    /**
     * @return the sameUserSubmitType
     */
    public String getSameUserSubmitType() {
        return sameUserSubmitType;
    }

    /**
     * @param sameUserSubmitType 要设置的sameUserSubmitType
     */
    public void setSameUserSubmitType(String sameUserSubmitType) {
        this.sameUserSubmitType = sameUserSubmitType;
    }

    /**
     * @return the sameUserSubmitTaskName
     */
    public String getSameUserSubmitTaskName() {
        return sameUserSubmitTaskName;
    }

    /**
     * @param sameUserSubmitTaskName 要设置的sameUserSubmitTaskName
     */
    public void setSameUserSubmitTaskName(String sameUserSubmitTaskName) {
        this.sameUserSubmitTaskName = sameUserSubmitTaskName;
    }

    /**
     * @return the sameUserSubmitTaskInstUuid
     */
    public String getSameUserSubmitTaskInstUuid() {
        return sameUserSubmitTaskInstUuid;
    }

    /**
     * @param sameUserSubmitTaskInstUuid 要设置的sameUserSubmitTaskInstUuid
     */
    public void setSameUserSubmitTaskInstUuid(String sameUserSubmitTaskInstUuid) {
        this.sameUserSubmitTaskInstUuid = sameUserSubmitTaskInstUuid;
    }

    /**
     * @return the sameUserSubmitTaskOperationUuid
     */
    public String getSameUserSubmitTaskOperationUuid() {
        return sameUserSubmitTaskOperationUuid;
    }

    /**
     * @param sameUserSubmitTaskOperationUuid 要设置的sameUserSubmitTaskOperationUuid
     */
    public void setSameUserSubmitTaskOperationUuid(String sameUserSubmitTaskOperationUuid) {
        this.sameUserSubmitTaskOperationUuid = sameUserSubmitTaskOperationUuid;
    }

    /**
     * @return the taskOperationItems
     */
    public List<TaskOperationItem> getTaskOperationItems() {
        return taskOperationItems;
    }

    /**
     * @param taskOperationItems 要设置的taskOperationItems
     */
    public void setTaskOperationItems(List<TaskOperationItem> taskOperationItems) {
        this.taskOperationItems = taskOperationItems;
    }

    /**
     * @return the subFlowInstUUids
     */
    public List<String> getSubFlowInstUUids() {
        return subFlowInstUUids;
    }

    /**
     * @param subFlowInstUUids 要设置的subFlowInstUUids
     */
    public void setSubFlowInstUUids(List<String> subFlowInstUUids) {
        this.subFlowInstUUids = subFlowInstUUids;
    }

    /**
     * @return the subFlowSyncStartd
     */
    public boolean isSubFlowSyncStartd() {
        return subFlowSyncStartd;
    }

    /**
     * @param subFlowSyncStartd 要设置的subFlowSyncStartd
     */
    public void setSubFlowSyncStartd(boolean subFlowSyncStartd) {
        if (!this.subFlowSyncStartd) {
            this.subFlowSyncStartd = subFlowSyncStartd;
        }
    }

    /**
     * @return the taskInstUUids
     */
    public List<String> getTaskInstUUids() {
        return taskInstUUids;
    }

    /**
     * @param taskInstUUids 要设置的taskInstUUids
     */
    public void setTaskInstUUids(List<String> taskInstUUids) {
        this.taskInstUUids = taskInstUUids;
    }

    /**
     * @return the printResultFileId
     */
    public String getPrintResultFileId() {
        return printResultFileId;
    }

    /**
     * @param printResultFileId 要设置的printResultFileId
     */
    public void setPrintResultFileId(String printResultFileId) {
        this.printResultFileId = printResultFileId;
    }

    /**
     * @return the taskTodoUsers
     */
    public Map<String, Map<String, String>> getTaskTodoUsers() {
        return taskTodoUsers;
    }

    /**
     * @param taskTodoUsers 要设置的taskTodoUsers
     */
    public void setTaskTodoUsers(Map<String, Map<String, String>> taskTodoUsers) {
        this.taskTodoUsers = taskTodoUsers;
    }

    /**
     * @return the nextTasks
     */
    public List<Map<String, Object>> getNextTasks() {
        return nextTasks;
    }

    /**
     * @param nextTasks 要设置的nextTasks
     */
    public void setNextTasks(List<Map<String, Object>> nextTasks) {
        this.nextTasks = nextTasks;
    }

    /**
     * 添加环节办理人
     *
     * @param taskInstUuid
     * @param todoUserSids
     */
    public SubmitResult addNextTaskInfo(TaskInstance taskInstance, Set<FlowUserSid> todoUserSids) {
        // 下一环节信息
        addTaskInfo(taskInstance);
        // 下一环节办理人信息
        if (CollectionUtils.isNotEmpty(todoUserSids)) {
            addTodoUsers(taskInstance.getUuid(), todoUserSids);
        }
        return this;
    }

    /**
     * @param uuid
     */
    private void addTaskInfo(TaskInstance taskInstance) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("uuid", taskInstance.getUuid());
        map.put("id", taskInstance.getId());
        map.put("name", taskInstance.getName());
        map.put("type", taskInstance.getType());
        nextTasks.add(map);
    }

    /**
     * 添加环节办理人
     *
     * @param taskInstUuid
     * @param todoUserSids
     */
    public void addTodoUsers(String taskInstUuid, Set<FlowUserSid> todoUserSids) {
        Map<String, String> todoUsers = Maps.newLinkedHashMap();
        for (FlowUserSid flowUserSid : todoUserSids) {
            todoUsers.put(flowUserSid.getId(), flowUserSid.getName());
        }
        this.taskTodoUsers.put(taskInstUuid, todoUsers);
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(Object data) {
        this.data = data;
    }

    public Boolean getFormDataUpdated() {
        return formDataUpdated;
    }

    public void setFormDataUpdated(Boolean formDataUpdated) {
        this.formDataUpdated = formDataUpdated;
    }
}

/*
 * @(#)2012-12-4 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Lists;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowVariables;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Description: 提交任务时传的流程数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-4.1	zhulh		2012-12-4		Create
 * </pre>
 * @date 2012-12-4
 */
public class TaskData extends HashMap<Object, Object> {
    private static final long serialVersionUID = -895623316146831984L;

    // 任务操作动作前缀
    private static final String OPERATION_KEY_PREFIX = "OPERATION_";

    // 任务操作类型前缀
    private static final String OPERATION_TYPE_KEY_PREFIX = "OPERATION_TYPE_";

    // 意见立地内容前缀
    private static final String OPINIONS_KEY_PREFIX = "OPINIONS_";

    // 意见立场值前缀
    private static final String OPINION_VALUE_KEY_PREFIX = "OPINION_VALUE_";

    // 意见立场值文本前缀
    private static final String OPINION_LABEL_KEY_PREFIX = "OPINION_LABEL_";

    // 意见立场内容前缀
    private static final String OPINION_TEXT_KEY_PREFIX = "OPINION_TEXT_";

    // 意见附件
    private static final String OPINION_FILE_KEY_PREFIX = "OPINION_FILE_";

    // 意见立场内容前缀
    private static final String TASK_FORM_OPINION_LOG_UUIDS_KEY_PREFIX = "TASK_FORM_OPINION_LOG_UUIDS_";

    private static final List<String> emptyList = Collections.emptyList();

    // 发起操作按钮权限
    private List<String> startRights = Collections.emptyList();

    // 待办操作按钮权限
    private List<String> todoRights = Collections.emptyList();

    // 已办操作按钮权限
    private List<String> doneRights = Collections.emptyList();

    // 督办操作按钮权限
    private List<String> monitorRights = Collections.emptyList();

    // 监控操作按钮权限
    private List<String> adminRights = Collections.emptyList();

    // 自定义的参数
    private Map<String, Object> customParamMap = new HashMap<String, Object>(0);

    // 前一流程属性
    private Map<String, Object> preTaskProperties = new HashMap<String, Object>(0);

    // 要更新的表单数据
    private Map<String, DyFormData> updatedDyformDataMap = new HashMap<String, DyFormData>(0);

    // 记录提交结果信息
    private SubmitResult submitResult = new SubmitResult();

    /**
     * 获取动态表单UUID
     *
     * @return the formUuid
     */
    public String getFormUuid() {
        Object formUuid = this.get(WorkFlowVariables.FORM_UUID);
        return formUuid == null ? null : formUuid.toString();
    }

    /**
     * 设置动态表单定义UUID
     *
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.put(WorkFlowVariables.FORM_UUID, formUuid);
    }

    /**
     * 获取动态表单数据UUID
     *
     * @return the dataUuid
     */
    public String getDataUuid() {
        Object dataUuid = this.get(WorkFlowVariables.DATA_UUID);
        return dataUuid == null ? null : dataUuid.toString();
    }

    /**
     * 设置动态表单数据UUID
     *
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.put(WorkFlowVariables.DATA_UUID, dataUuid);
    }

    // /**
    // * 获取动态表单数据
    // *
    // * @return the formData
    // */
    // public Object getFormData() {
    // Object formData = this.get(WorkFlowVariables.FORM_DATA);
    // return formData == null ? null : formData;
    // }
    //
    // /**
    // * 设置动态表单数据
    // *
    // * @param formData 要设置的formData
    // */
    // public void setFormData(Object formData) {
    // this.put(WorkFlowVariables.FORM_DATA, formData);
    // }

    /**
     * @return the startRights
     */
    public List<String> getStartRights() {
        return startRights;
    }

    /**
     * @param startRights 要设置的startRights
     */
    public void setStartRights(List<String> startRights) {
        this.startRights = startRights;
    }

    /**
     * @return the todoRights
     */
    public List<String> getTodoRights() {
        return todoRights;
    }

    /**
     * @param todoRights 要设置的todoRights
     */
    public void setTodoRights(List<String> todoRights) {
        this.todoRights = todoRights;
    }

    /**
     * @return the doneRights
     */
    public List<String> getDoneRights() {
        return doneRights;
    }

    /**
     * @param doneRights 要设置的doneRights
     */
    public void setDoneRights(List<String> doneRights) {
        this.doneRights = doneRights;
    }

    /**
     * @return the monitorRights
     */
    public List<String> getMonitorRights() {
        return monitorRights;
    }

    /**
     * @param monitorRights 要设置的monitorRights
     */
    public void setMonitorRights(List<String> monitorRights) {
        this.monitorRights = monitorRights;
    }

    /**
     * @return the adminRights
     */
    public List<String> getAdminRights() {
        return adminRights;
    }

    /**
     * @param adminRights 要设置的adminRights
     */
    public void setAdminRights(List<String> adminRights) {
        this.adminRights = adminRights;
    }

    /**
     * @return the customDynamicButtons
     */
    @SuppressWarnings("unchecked")
    public List<CustomDynamicButton> getCustomDynamicButtons() {
        List<CustomDynamicButton> buttons = (List<CustomDynamicButton>) this.get("customDynamicButtons");
        return buttons == null ? new ArrayList<CustomDynamicButton>(0) : buttons;
    }

    /**
     * @param customDynamicButtons 要设置的customDynamicButtons
     */
    public void setCustomDynamicButtons(List<CustomDynamicButton> customDynamicButtons) {
        this.put("customDynamicButtons", customDynamicButtons);
    }

    /**
     * 是否启动流程
     *
     * @param flowInstUuid
     */
    public boolean getStartNewFlow(String flowInstUuid) {
        Boolean startNewFlow = (Boolean) this.get(WorkFlowVariables.START_NEW_FLOW + flowInstUuid);
        return startNewFlow == null ? false : startNewFlow;
    }

    /**
     * 是否启动流程
     *
     * @param flowInstUuid
     * @param newFlow
     */
    public void setStartNewFlow(String flowInstUuid, boolean newFlow) {
        this.put(WorkFlowVariables.START_NEW_FLOW + flowInstUuid, newFlow);
    }

    /**
     * 获取当前操作流程的用户ID
     *
     * @return the userId
     */
    public String getUserId() {
        Object userId = this.get(WorkFlowVariables.USER_ID);
        return userId == null ? null : userId.toString();
    }

    /**
     * 设置当前操作流程的用户ID
     *
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.put(WorkFlowVariables.USER_ID, userId);
    }

    /**
     * 获取当前操作流程的用户名
     *
     * @return
     */
    public String getUserName() {
        Object userName = this.get(WorkFlowVariables.USER_NAME);
        return userName == null ? null : userName.toString();
    }

    /**
     * 设置当前操作流程的用户名
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.put(WorkFlowVariables.USER_NAME, userName);
    }

    /**
     * 获取当前操作流程的用户信息
     *
     * @return
     */
    public UserDetails getUserDetails() {
        UserDetails userDetails = (UserDetails) this.get(WorkFlowVariables.USER_DETAILS);
        if (userDetails == null) {
            userDetails = SpringSecurityUtils.getCurrentUser();
            setUserDetails(userDetails);
        }
        return userDetails;
    }

    /**
     * 设置当前操作流程的用户信息
     *
     * @param userDetails
     */
    public void setUserDetails(UserDetails userDetails) {
        this.put(WorkFlowVariables.USER_DETAILS, userDetails);
    }

    /**
     * 获取当前流程所有者ID
     *
     * @return
     */
    public String getFlowOwnerId() {
        Object ownerId = this.get(WorkFlowVariables.FLOW_OWNER_ID);
        return ownerId == null ? null : ownerId.toString();
    }

    /**
     * 设置当前流程所有者ID
     *
     * @param 要设置的flowOwnerId
     */
    public void setFlowOwnerId(String flowOwnerId) {
        this.put(WorkFlowVariables.FLOW_OWNER_ID, flowOwnerId);
    }

    /**
     * 获取当前流程发起部门ID
     *
     * @param userId 要设置的userId
     */
    public String getFlowStartDepartmentId() {
        Object departmentId = this.get(WorkFlowVariables.FLOW_START_DEPARTMENT_ID);
        return departmentId == null ? null : departmentId.toString();
    }

    /**
     * 设置当前流程发起部门ID
     *
     * @param userId 要设置的userId
     */
    public void setFlowStartDepartmentId(String departmentId) {
        this.put(WorkFlowVariables.FLOW_START_DEPARTMENT_ID, departmentId);
    }

    /**
     * 获取当前流程所在部门ID
     *
     * @param userId 要设置的userId
     */
    public String getFlowOwnerDepartmentId() {
        Object departmentId = this.get(WorkFlowVariables.FLOW_OWNER_DEPARTMENT_ID);
        return departmentId == null ? null : departmentId.toString();
    }

    /**
     * 设置当前流程所在部门ID
     *
     * @param userId 要设置的userId
     */
    public void setFlowOwnerDepartmentId(String departmentId) {
        this.put(WorkFlowVariables.FLOW_OWNER_DEPARTMENT_ID, departmentId);
    }

    /**
     * 获取当前流程发起单位ID
     *
     * @param userId 要设置的userId
     */
    public String getFlowStartUnitId() {
        Object unitId = this.get(WorkFlowVariables.FLOW_START_UNIT_ID);
        return unitId == null ? null : unitId.toString();
    }

    /**
     * 设置当前流程发起单位ID
     *
     * @param userId 要设置的userId
     */
    public void setFlowStartUnitId(String unitId) {
        this.put(WorkFlowVariables.FLOW_START_UNIT_ID, unitId);
    }

    /**
     * 获取当前流程所在部门ID
     *
     * @param userId 要设置的userId
     */
    public String getFlowOwnerUnitId() {
        Object unitId = this.get(WorkFlowVariables.FLOW_OWNER_UNIT_ID);
        return unitId == null ? null : unitId.toString();
    }

    /**
     * 设置当前流程所在部门ID
     *
     * @param userId 要设置的userId
     */
    public void setFlowOwnerUnitId(String unitId) {
        this.put(WorkFlowVariables.FLOW_OWNER_UNIT_ID, unitId);
    }

    /**
     * @param flowInstUuid
     * @return
     */
    public boolean isRecordUnitDone(String flowInstUuid) {
        Object recordUnitDone = this.get("RecordUnitDone_" + flowInstUuid);
        return recordUnitDone == null ? false : (Boolean) recordUnitDone;
    }

    /**
     * @param flowInstUuid
     * @param done
     */
    public void setRecordUnitDone(String flowInstUuid, boolean done) {
        this.put("RecordUnitDone_" + flowInstUuid, done);
    }

    /**
     * 选择的下一流向ID
     *
     * @return the toDirectionId
     */
    public String getToDirectionId(String fromTaskId) {
        String toDirectionId = getToDirectionIds().get(fromTaskId);
        // 子流程自动提交且环节ID相关时需区分不同的流程
        if (StringUtils.isBlank(toDirectionId) && this.getToken() != null) {
            FlowDelegate flowDelegate = this.getToken().getFlowDelegate();
            if (flowDelegate != null) {
                toDirectionId = getToDirectionIds().get(flowDelegate.getFlow().getId() + "_" + fromTaskId);
            }
        }
        return toDirectionId;
    }

    /**
     * 选择的下一流向ID
     *
     * @param toDirectionId 要设置的toDirectionId
     */
    public void setToDirectionId(String fromTaskId, String toDirectionId) {
        getToDirectionIds().put(fromTaskId, toDirectionId);
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getToDirectionIds() {
        Map<String, String> toDirectionIds = (Map<String, String>) this.get(WorkFlowVariables.TO_DIRECTION_ID_MAP);
        if (toDirectionIds == null) {
            toDirectionIds = new HashMap<String, String>();
            this.put(WorkFlowVariables.TO_DIRECTION_ID_MAP, toDirectionIds);
        }
        return toDirectionIds;
    }

    /**
     * @param toDirectionIds
     */
    public void setToDirectionIds(Map<String, String> toDirectionIds) {
        if (toDirectionIds != null) {
            getToDirectionIds().putAll(toDirectionIds);
        }
    }

    /**
     * 选择的下一环节ID
     *
     * @return the toTaskId
     */
    public String getToTaskId(String fromTaskId) {
        String toTaskId = getToTaskIds().get(fromTaskId);
        // 子流程自动提交且环节ID相关时需区分不同的流程
        if (StringUtils.isBlank(toTaskId) && this.getToken() != null) {
            FlowDelegate flowDelegate = this.getToken().getFlowDelegate();
            if (flowDelegate != null) {
                toTaskId = getToTaskIds().get(flowDelegate.getFlow().getId() + "_" + fromTaskId);
            }
        }
        return toTaskId;
    }

    /**
     * 选择的下一环节ID
     *
     * @param toTaskId 要设置的toTaskId
     */
    public void setToTaskId(String fromTaskId, String toTaskId) {
        getToTaskIds().put(fromTaskId, toTaskId);
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getToTaskIds() {
        Map<String, String> toTaskIds = (Map<String, String>) this.get(WorkFlowVariables.TO_TASK_ID_MAP);
        if (toTaskIds == null) {
            toTaskIds = new HashMap<String, String>();
            this.put(WorkFlowVariables.TO_TASK_ID_MAP, toTaskIds);
        }
        return toTaskIds;
    }

    /**
     * @param toTaskIds
     */
    public void setToTaskIds(Map<String, String> toTaskIds) {
        if (toTaskIds != null) {
            getToTaskIds().putAll(toTaskIds);
        }
    }

    /**
     * 是否跳转到下一环节
     *
     * @param fromTaskId
     * @return
     */
    public boolean isGotoTask(String fromTaskId) {
        Boolean gotoTask = (Boolean) this.get(WorkFlowVariables.GOTO_TASK + fromTaskId);
        return gotoTask == null ? false : gotoTask;
    }

    /**
     * 是否跳转到下一环节
     *
     * @param fromTaskId
     * @param gotoTask
     */
    public void setGotoTask(String fromTaskId, boolean gotoTask) {
        this.put(WorkFlowVariables.GOTO_TASK + fromTaskId, gotoTask);
    }

    /**
     * 选择的下一子流程ID
     *
     * @return the toSubFlowId
     */
    public String getToSubFlowId() {
        Object toSubFlowId = this.get(WorkFlowVariables.TO_SUB_FLOW_ID);
        return toSubFlowId == null ? null : toSubFlowId.toString();
    }

    /**
     * 选择的下一子流程ID
     *
     * @param toSubFlowId 要设置的toSubFlowId
     */
    public void setToSubFlowId(String toSubFlowId) {
        this.put(WorkFlowVariables.TO_SUB_FLOW_ID, toSubFlowId);
    }

    /**
     * 获取已完成的子流程ID
     *
     * @return the completedSubFlowId
     */
    public String getCompletedSubFlowId() {
        Object compeletedSubFlowId = this.get(WorkFlowVariables.COMPLETED_SUB_FLOW_ID);
        return compeletedSubFlowId == null ? null : compeletedSubFlowId.toString();
    }

    /**
     * 设置已完成的子流程ID
     *
     * @param completedSubFlowId 要设置的completedSubFlowId
     */
    public void setCompletedSubFlowId(String completedSubFlowId) {
        this.put(WorkFlowVariables.COMPLETED_SUB_FLOW_ID, completedSubFlowId);
    }

    /**
     * 获取已完成的子流程实例UUID
     *
     * @param subFlowId
     * @param uuid
     */
    public String getCompletedSubFlowInstUuid(String completedSubFlowId) {
        Object subFlowInstUuid = this.get(WorkFlowVariables.COMPLETED_SUB_FLOW_INST_UUID + completedSubFlowId);
        return subFlowInstUuid == null ? null : subFlowInstUuid.toString();
    }

    /**
     * 设置已完成的子流程实例UUID
     *
     * @param subFlowId
     * @param uuid
     */
    public void setCompletedSubFlowInstUuid(String completedSubFlowId, String subFlowInstUuid) {
        this.put(WorkFlowVariables.COMPLETED_SUB_FLOW_INST_UUID + completedSubFlowId, subFlowInstUuid);
    }

    /**
     * 获取已完成的子流程动态表单定义UUID
     *
     * @param completedSubFlowId
     * @return
     */
    public String getCompletedSubFlowFormUuid(String completedSubFlowId) {
        Object formUuid = this.get(WorkFlowVariables.COMPLETED_SUB_FLOW_FORM_UUID + completedSubFlowId);
        return formUuid == null ? null : formUuid.toString();
    }

    /**
     * 设置已完成的子流程动态表单定义UUID
     *
     * @param completedSubFlowId
     * @param formUuid
     */
    public void setCompletedSubFlowFormUuid(String completedSubFlowId, String formUuid) {
        this.put(WorkFlowVariables.COMPLETED_SUB_FLOW_FORM_UUID + completedSubFlowId, formUuid);
    }

    /**
     * 获取已完成的子流程动态表单数据UUID
     *
     * @param completedSubFlowId
     * @return
     */
    public String getCompletedSubFlowDataUuid(String completedSubFlowId) {
        Object dataUuid = this.get(WorkFlowVariables.COMPLETED_SUB_FLOW_DATA_UUID + completedSubFlowId);
        return dataUuid == null ? null : dataUuid.toString();
    }

    /**
     * 设置已完成的子流程动态表单数据UUID
     *
     * @param completedSubFlowId
     * @param dataUuid
     */
    public void setCompletedSubFlowDataUuid(String completedSubFlowId, String dataUuid) {
        this.put(WorkFlowVariables.COMPLETED_SUB_FLOW_DATA_UUID + completedSubFlowId, dataUuid);
    }

    /**
     * 获取已完成的子流程动态表单数据
     *
     * @param completedSubFlowId
     * @return
     */
    public Object getCompletedSubFlowFormData(String completedSubFlowId) {
        return this.get(WorkFlowVariables.COMPLETED_SUB_FLOW_FORM_DATA + completedSubFlowId);
    }

    /**
     * 设置已完成的子流程动态表单数据
     *
     * @param completedSubFlowId
     * @param formData
     */
    public void setCompletedSubFlowFormData(String completedSubFlowId, Object formData) {
        this.put(WorkFlowVariables.COMPLETED_SUB_FLOW_FORM_DATA + completedSubFlowId, formData);
    }

    /**
     * 获取第一个环节ID
     *
     * @param taskId
     */
    public String getFirstTaskId() {
        Object firstTaskId = this.get("firstTaskId");
        return firstTaskId == null ? "" : firstTaskId.toString();
    }

    /**
     * 设置第一个环节ID
     *
     * @param taskId
     */
    public void setFirstTaskId(String taskId) {
        this.put("firstTaskId", taskId);
    }

    /**
     * 获取第一个环节名称
     *
     * @param taskId
     */
    public String getFirstTaskName() {
        Object firstTaskName = this.get("firstTaskName");
        return firstTaskName == null ? "" : firstTaskName.toString();
    }

    /**
     * 设置第一个环节名称
     *
     * @param taskName
     */
    public void setFirstTaskName(String taskName) {
        this.put("firstTaskName", taskName);
    }

    /**
     * 环节ID
     *
     * @return
     */
    public String getTaskId() {
        Object taskId = this.get(WorkFlowVariables.TASK_ID);
        return taskId == null ? null : taskId.toString();
    }

    /**
     * 环节ID
     *
     * @param taskId
     */
    public void setTaskId(String taskId) {
        this.put(WorkFlowVariables.TASK_ID, taskId);
    }

    /**
     * 环节名称
     *
     * @return
     */
    public String getTaskName() {
        Object taskName = this.get(WorkFlowVariables.TASK_NAME);
        return taskName == null ? null : taskName.toString();
    }

    /**
     * 环节名称
     *
     * @param taskName
     */
    public void setTaskName(String taskName) {
        this.put(WorkFlowVariables.TASK_NAME, taskName);
    }

    /**
     * 环节参与者名称
     *
     * @return
     */
    public String getTaskRawUserNames() {
        Object rawUserNames = this.get(WorkFlowVariables.TASK_USER_RAW_NAMES);
        return rawUserNames == null ? null : rawUserNames.toString();
    }

    /**
     * 环节参与者名称
     *
     * @param taskName
     */
    public void setTaskRawUserNames(String userRawNames) {
        this.put(WorkFlowVariables.TASK_USER_RAW_NAMES, userRawNames);
    }

    /**
     * 流水号定义ID
     *
     * @return
     */
    public String getSerialNoDefId() {
        Object serialNoDefId = this.get(WorkFlowVariables.SERIAL_NO_DEF_ID);
        return serialNoDefId == null ? null : serialNoDefId.toString();
    }

    /**
     * 流水号定义ID
     *
     * @param serialNo
     */
    public void setSerialNoDefId(String serialNoDefId) {
        this.put(WorkFlowVariables.SERIAL_NO_DEF_ID, serialNoDefId);
    }

    /**
     * 环节流水号
     *
     * @return
     */
    public String getTaskSerialNo() {
        Object serialNo = this.get(WorkFlowVariables.SERIAL_NO);
        return serialNo == null ? null : serialNo.toString();
    }

    /**
     * 环节流水号
     *
     * @param preSerialNo
     */
    public void setTaskSerialNo(String serialNo) {
        this.put(WorkFlowVariables.SERIAL_NO, serialNo);
    }

    /**
     * 是否指定用户参与者
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean isSpecifyTaskUser(String taskId) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.IS_SPECIFY_TASK_USER.getName());
        return map == null ? false : map.get(taskId) == null ? false : map.get(taskId);
    }

    /**
     * 是否指定用户参与者
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public void setSpecifyTaskUser(String taskId, boolean isSpecifyTaskUser) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.IS_SPECIFY_TASK_USER.getName());
        if (map == null) {
            map = new HashMap<String, Boolean>();
            this.put(WorkFlowVariables.IS_SPECIFY_TASK_USER.getName(), map);
        }
        map.put(taskId, isSpecifyTaskUser);
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isSpecifyTaskCopyUser(String taskId) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.IS_SPECIFY_TASK_COPY_USER.getName());
        return map == null ? false : map.get(taskId) == null ? false : map.get(taskId);
    }

    /**
     * @param taskId
     * @param isSpecifyTaskCopyUser
     */
    public void setSpecifyTaskCopyUser(String taskId, boolean isSpecifyTaskCopyUser) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.IS_SPECIFY_TASK_COPY_USER.getName());
        if (map == null) {
            map = new HashMap<String, Boolean>();
            this.put(WorkFlowVariables.IS_SPECIFY_TASK_USER.getName(), map);
        }
        map.put(taskId, isSpecifyTaskCopyUser);
    }


    /**
     * 只需要其中一个人办理
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean isAnyone(String taskId) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.IS_ANYONE);
        return map == null ? false : (map.get(taskId) == null ? false : map.get(taskId));
    }

    /**
     * 只需要其中一个人办理
     *
     * @return
     */
    public void setIsAnyone(String taskId, boolean isAnyone) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put(taskId, isAnyone);
        this.put(WorkFlowVariables.IS_ANYONE, map);
    }

    /**
     * 按人员顺序依次办理
     *
     * @param taskId
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean isByOrder(String taskId) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.IS_BY_ORDER);
        return map == null ? false : (map.get(taskId) == null ? false : map.get(taskId));
    }

    /**
     * 按人员顺序依次办理
     *
     * @param taskId
     * @param isByOrder
     */
    public void setIsByOrder(String taskId, boolean isByOrder) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put(taskId, isByOrder);
        this.put(WorkFlowVariables.IS_BY_ORDER, map);
    }

    /**
     * @param taskId
     * @return
     */
    public Set<String> getTaskUsers(String taskId) {
        Set<FlowUserSid> sids = getTaskUserSids(taskId);
        Set<String> orgIds = new LinkedHashSet<String>(0);
        orgIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(sids));
        return orgIds;
    }

    /**
     * @param taskId
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<FlowUserSid> getTaskUserSids(String taskId) {
        Object map = this.get(WorkFlowVariables.FLOW_USER_SIDS);
        return map == null ? new LinkedHashSet<FlowUserSid>(0)
                : (((Map<String, Set<FlowUserSid>>) map).get(taskId) == null ? new LinkedHashSet<FlowUserSid>(0)
                : ((Map<String, Set<FlowUserSid>>) map).get(taskId));
    }

    /**
     * @return
     */
    public Map<String, Set<FlowUserSid>> getTaskUserSidsMap() {
        return (Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_USER_SIDS);
    }

    /**
     * @param taskUsers
     */
    public void setTaskUsers(Map<String, List<String>> taskUsers) {
        if (taskUsers == null || taskUsers.isEmpty()) {
            return;
        }
        Map<String, List<String>> rawTaskUsers = new HashMap<String, List<String>>();
        for (String taskId : taskUsers.keySet()) {
            List<String> users = taskUsers.get(taskId);
            if (users != null && !users.isEmpty()) {
                this.setSpecifyTaskUser(taskId, true);
                rawTaskUsers.put(taskId, users);
            }
        }
        this.addTaskUsers(rawTaskUsers);
    }

    /**
     * @param taskId
     * @param userIds
     */
    public void addTaskUsers(String taskId, List<String> userIds) {
        if (StringUtils.isBlank(taskId) || CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<FlowUserSid> sids = new ArrayList<FlowUserSid>();
        for (String userId : userIds) {
            sids.add(new FlowUserSid(userId, IdentityResolverStrategy.resolveAsName(userId)));
        }
        addTaskUserSids(taskId, sids);
    }

    /**
     * @param taskUsers
     */
    private void addTaskUsers(Map<String, List<String>> taskUsers) {
        for (String key : taskUsers.keySet()) {
            addTaskUsers(key, taskUsers.get(key));
        }
    }

    /**
     * @param taskId
     * @param userSids
     */
    @SuppressWarnings("unchecked")
    public void addTaskUserSids(String taskId, List<FlowUserSid> userSids) {
        Map<String, Set<FlowUserSid>> map = (Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_USER_SIDS);
        if (map == null) {
            map = new HashMap<String, Set<FlowUserSid>>();
        }
        if (!map.containsKey(taskId)) {
            map.put(taskId, new LinkedHashSet<FlowUserSid>());
        }
        map.get(taskId).addAll(userSids);

        Map<String, List<FlowUserSid>> taskUsers = new HashMap<String, List<FlowUserSid>>();
        taskUsers.put(taskId, Arrays.asList(map.get(taskId).toArray(new FlowUserSid[0])));
        this.addTaskUserSids(taskUsers);
    }

    /**
     * @param taskUserSids
     */
    @SuppressWarnings("unchecked")
    private void addTaskUserSids(Map<String, List<FlowUserSid>> taskUserSids) {
        if (taskUserSids == null) {
            return;
        }
        if (!this.containsKey(WorkFlowVariables.FLOW_USER_SIDS)) {
            this.put(WorkFlowVariables.FLOW_USER_SIDS, new HashMap<String, Set<FlowUserSid>>());
        }
        for (String key : taskUserSids.keySet()) {
            Set<FlowUserSid> linkedSet = new LinkedHashSet<FlowUserSid>();
            if (taskUserSids.get(key) == null) {
                continue;
            }
            linkedSet.addAll(taskUserSids.get(key));
            ((Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_USER_SIDS)).put(key, linkedSet);
        }
    }

    /**
     * @param taskId
     */
    @SuppressWarnings("unchecked")
    public void resetTaskUsers(String taskId) {
        Object map = this.get(WorkFlowVariables.FLOW_USER_SIDS);
        if (map != null) {
            ((Map<String, Set<FlowUserSid>>) map).remove(taskId);
        }
    }

    /**
     * @param taskUserJobPaths
     */
    public void setTaskUserJobPaths(Map<String, List<String>> taskUserJobPaths) {
        this.put("taskUserJobPaths", taskUserJobPaths);
    }

    /**
     * @return
     */
    public Map<String, List<String>> getTaskUserJobPaths() {
        return (Map<String, List<String>>) this.get("taskUserJobPaths");
    }

    /**
     * @return
     */
    public List<String> getTaskUserJobPaths(String taskId) {
        Map<String, List<String>> taskUserJobPaths = (Map<String, List<String>>) this.get("taskUserJobPaths");
        return taskUserJobPaths == null ? null : taskUserJobPaths.get(taskId);
    }

    /**
     * @param copyUserSids
     */
    public void setSkipTaskCopyUsers(List<FlowUserSid> copyUserSids) {
        this.put("SKIP_TASK_COPY_USERS", copyUserSids);
    }

    /**
     *
     */
    public List<FlowUserSid> getSkipTaskCopyUsers() {
        Object list = this.get("SKIP_TASK_COPY_USERS");
        return list == null ? Collections.emptyList() : (List<FlowUserSid>) list;
    }

    /**
     * @param taskId
     */
    public Set<String> getTaskCopyUsers(String taskId) {
        Set<FlowUserSid> sids = getTaskCopyUserSids(taskId);
        Set<String> orgIds = new LinkedHashSet<String>(0);
        orgIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(sids));
        return orgIds;
    }

    /**
     * @param taskId
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<FlowUserSid> getTaskCopyUserSids(String taskId) {
        Object map = this.get(WorkFlowVariables.FLOW_COPY_USER_SIDS);
        return map == null ? new LinkedHashSet<FlowUserSid>(0)
                : (((Map<String, Set<FlowUserSid>>) map).get(taskId) == null ? new LinkedHashSet<FlowUserSid>(0)
                : ((Map<String, Set<FlowUserSid>>) map).get(taskId));
    }

    public Map<String, Set<FlowUserSid>> getTaskCopyUserSidsMap() {
        return (Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_COPY_USER_SIDS);
    }

    /**
     * @param taskId
     * @param copyUserIds
     */
    public void addTaskCopyUsers(String taskId, List<String> copyUserIds) {
        if (StringUtils.isBlank(taskId) || CollectionUtils.isEmpty(copyUserIds)) {
            return;
        }
        List<FlowUserSid> sids = new ArrayList<FlowUserSid>();
        for (String copyUserId : copyUserIds) {
            sids.add(new FlowUserSid(copyUserId, IdentityResolverStrategy.resolveAsName(copyUserId)));
        }
        addTaskCopyUserSids(taskId, sids);
    }

    /**
     * @param taskCopyUsers
     */
    public void addTaskCopyUsers(Map<String, List<String>> taskCopyUsers) {
        for (String key : taskCopyUsers.keySet()) {
            addTaskCopyUsers(key, taskCopyUsers.get(key));
        }
    }

    /**
     * @param taskId
     * @param copyUserSids
     */
    @SuppressWarnings("unchecked")
    public void addTaskCopyUserSids(String taskId, List<FlowUserSid> copyUserSids) {
        Map<String, Set<FlowUserSid>> map = (Map<String, Set<FlowUserSid>>) this
                .get(WorkFlowVariables.FLOW_COPY_USER_SIDS);
        if (map == null) {
            map = new HashMap<String, Set<FlowUserSid>>();
        }
        if (!map.containsKey(taskId)) {
            map.put(taskId, new LinkedHashSet<FlowUserSid>());
        }
        map.get(taskId).addAll(copyUserSids);

        Map<String, List<FlowUserSid>> taskCopyUsers = new HashMap<String, List<FlowUserSid>>();
        taskCopyUsers.put(taskId, Arrays.asList(map.get(taskId).toArray(new FlowUserSid[0])));
        this.addTaskCopyUserSids(taskCopyUsers);
    }

    /**
     * @param taskCopyUserSids
     */
    @SuppressWarnings("unchecked")
    private void addTaskCopyUserSids(Map<String, List<FlowUserSid>> taskCopyUserSids) {
        if (taskCopyUserSids == null) {
            return;
        }
        if (!this.containsKey(WorkFlowVariables.FLOW_COPY_USER_SIDS)) {
            this.put(WorkFlowVariables.FLOW_COPY_USER_SIDS, new HashMap<String, Set<FlowUserSid>>());
        }
        for (String key : taskCopyUserSids.keySet()) {
            Set<FlowUserSid> linkedSet = new LinkedHashSet<FlowUserSid>();
            if (taskCopyUserSids.get(key) == null) {
                continue;
            }
            linkedSet.addAll(taskCopyUserSids.get(key));
            ((Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_COPY_USER_SIDS)).put(key, linkedSet);
        }
    }

    /**
     * @param taskId
     * @return
     */
    public Set<String> getTaskDecisionMakers(String taskId) {
        Set<FlowUserSid> sids = getTaskDecisionMakerSids(taskId);
        Set<String> orgIds = new LinkedHashSet<String>(0);
        orgIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(sids));
        return orgIds;
    }

    /**
     * @param taskId
     * @return
     */
    public Set<FlowUserSid> getTaskDecisionMakerSids(String taskId) {
        Object map = this.get(WorkFlowVariables.FLOW_DECISION_MAKER_SIDS);
        return map == null ? new LinkedHashSet<FlowUserSid>(0)
                : (((Map<String, Set<FlowUserSid>>) map).get(taskId) == null ? new LinkedHashSet<FlowUserSid>(0)
                : ((Map<String, Set<FlowUserSid>>) map).get(taskId));
    }

    /**
     * @param taskId
     * @param decisionMakerIds
     */
    public void addTaskDecisionMakers(String taskId, List<String> decisionMakerIds) {
        if (StringUtils.isBlank(taskId) || CollectionUtils.isEmpty(decisionMakerIds)) {
            return;
        }
        List<FlowUserSid> sids = new ArrayList<FlowUserSid>();
        for (String decisionMakerId : decisionMakerIds) {
            sids.add(new FlowUserSid(decisionMakerId, IdentityResolverStrategy.resolveAsName(decisionMakerId)));
        }
        addTaskDecisionMakerSids(taskId, sids);
    }


    /**
     * @param taskDecisionMakers
     */
    public void addTaskDecisionMakers(Map<String, List<String>> taskDecisionMakers) {
        for (String key : taskDecisionMakers.keySet()) {
            addTaskDecisionMakers(key, taskDecisionMakers.get(key));
        }
    }

    /**
     * @param taskId
     * @param decisionMakerSids
     */
    public void addTaskDecisionMakerSids(String taskId, List<FlowUserSid> decisionMakerSids) {
        Map<String, Set<FlowUserSid>> map = (Map<String, Set<FlowUserSid>>) this
                .get(WorkFlowVariables.FLOW_DECISION_MAKER_SIDS);
        if (map == null) {
            map = new HashMap<>();
        }
        if (!map.containsKey(taskId)) {
            map.put(taskId, new LinkedHashSet<>());
        }
        map.get(taskId).addAll(decisionMakerSids);

        Map<String, List<FlowUserSid>> taskDecisionMakers = new HashMap<String, List<FlowUserSid>>();
        taskDecisionMakers.put(taskId, Arrays.asList(map.get(taskId).toArray(new FlowUserSid[0])));
        this.addTaskDecisionMakerSids(taskDecisionMakers);
    }

    /**
     * @param taskDecisionMakers
     */
    @SuppressWarnings("unchecked")
    private void addTaskDecisionMakerSids(Map<String, List<FlowUserSid>> taskDecisionMakers) {
        if (taskDecisionMakers == null) {
            return;
        }
        if (!this.containsKey(WorkFlowVariables.FLOW_DECISION_MAKER_SIDS)) {
            this.put(WorkFlowVariables.FLOW_DECISION_MAKER_SIDS, new HashMap<String, Set<FlowUserSid>>());
        }
        for (String key : taskDecisionMakers.keySet()) {
            Set<FlowUserSid> linkedSet = new LinkedHashSet<FlowUserSid>();
            if (taskDecisionMakers.get(key) == null) {
                continue;
            }
            linkedSet.addAll(taskDecisionMakers.get(key));
            ((Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_DECISION_MAKER_SIDS)).put(key, linkedSet);
        }
    }

    /**
     * @param taskId
     * @return
     */
    public Set<String> getTaskMonitors(String taskId) {
        Set<FlowUserSid> sids = getTaskMonitorSids(taskId);
        Set<String> orgIds = new LinkedHashSet<String>(0);
        orgIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(sids));
        return orgIds;
    }

    /**
     * @param taskId
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<FlowUserSid> getTaskMonitorSids(String taskId) {
        Object map = this.get(WorkFlowVariables.FLOW_MONITOR_SIDS);
        return map == null ? new LinkedHashSet<FlowUserSid>(0)
                : (((Map<String, Set<FlowUserSid>>) map).get(taskId) == null ? new LinkedHashSet<FlowUserSid>(0)
                : ((Map<String, Set<FlowUserSid>>) map).get(taskId));
    }

    public Map<String, Set<FlowUserSid>> getTaskMonitorSidsMap() {
        return (Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_MONITOR_SIDS);
    }

    /**
     * @param taskId
     * @param monitorIds
     */
    public void addTaskMonitors(String taskId, List<String> monitorIds) {
        if (StringUtils.isBlank(taskId) || CollectionUtils.isEmpty(monitorIds)) {
            return;
        }
        List<FlowUserSid> sids = new ArrayList<FlowUserSid>();
        for (String monitorId : monitorIds) {
            sids.add(new FlowUserSid(monitorId, IdentityResolverStrategy.resolveAsName(monitorId)));
        }
        addTaskMonitorSids(taskId, sids);
    }

    /**
     * @param taskMomitors
     */
    public void addTaskMonitors(Map<String, List<String>> taskMomitors) {
        for (String key : taskMomitors.keySet()) {
            addTaskMonitors(key, taskMomitors.get(key));
        }
    }

    /**
     * @param taskId
     * @param monitorSids
     */
    @SuppressWarnings("unchecked")
    public void addTaskMonitorSids(String taskId, List<FlowUserSid> monitorSids) {
        Map<String, Set<FlowUserSid>> map = (Map<String, Set<FlowUserSid>>) this
                .get(WorkFlowVariables.FLOW_MONITOR_SIDS);
        if (map == null) {
            map = new HashMap<String, Set<FlowUserSid>>();
        }
        if (!map.containsKey(taskId)) {
            map.put(taskId, new LinkedHashSet<FlowUserSid>());
        }
        map.get(taskId).addAll(monitorSids);

        Map<String, List<FlowUserSid>> taskMonitorUsers = new HashMap<String, List<FlowUserSid>>();
        taskMonitorUsers.put(taskId, Arrays.asList(map.get(taskId).toArray(new FlowUserSid[0])));
        this.addTaskMonitorSids(taskMonitorUsers);
    }

    /**
     * @param taskMomitorSids
     */
    @SuppressWarnings("unchecked")
    public void addTaskMonitorSids(Map<String, List<FlowUserSid>> taskMomitorSids) {
        if (taskMomitorSids == null) {
            return;
        }
        if (!this.containsKey(WorkFlowVariables.FLOW_MONITOR_SIDS)) {
            this.put(WorkFlowVariables.FLOW_MONITOR_SIDS, new HashMap<String, Set<FlowUserSid>>());
        }
        for (String key : taskMomitorSids.keySet()) {
            Set<FlowUserSid> linkedSet = new LinkedHashSet<FlowUserSid>();
            if (taskMomitorSids.get(key) == null) {
                continue;
            }
            linkedSet.addAll(taskMomitorSids.get(key));
            ((Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_MONITOR_SIDS)).put(key, linkedSet);
        }
    }

    /**
     * @param taskId
     * @return
     */
    public Set<String> getTaskAdmins(String taskId) {
        Set<FlowUserSid> sids = getTaskAdminSids(taskId);
        Set<String> orgIds = new LinkedHashSet<String>(0);
        orgIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(sids));
        return orgIds;
    }

    /**
     * @param taskId
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<FlowUserSid> getTaskAdminSids(String taskId) {
        Object map = this.get(WorkFlowVariables.FLOW_ADMIN_SIDS);
        return map == null ? new LinkedHashSet<FlowUserSid>(0)
                : (((Map<String, Set<FlowUserSid>>) map).get(taskId) == null ? new LinkedHashSet<FlowUserSid>(0)
                : ((Map<String, Set<FlowUserSid>>) map).get(taskId));
    }

    public Map<String, Set<FlowUserSid>> getTaskAdminSidsMap() {
        return (Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_ADMIN_SIDS);
    }

    /**
     * @param taskId
     * @param adminIds
     */
    public void addTaskAdmins(String taskId, List<String> adminIds) {
        if (StringUtils.isBlank(taskId) || CollectionUtils.isEmpty(adminIds)) {
            return;
        }
        List<FlowUserSid> sids = new ArrayList<FlowUserSid>();
        for (String adminId : adminIds) {
            sids.add(new FlowUserSid(adminId, IdentityResolverStrategy.resolveAsName(adminId)));
        }
        addTaskAdminSids(taskId, sids);
    }

    /**
     * @param taskMomitors
     */
    public void addTaskAdmins(Map<String, List<String>> taskAdmins) {
        for (String key : taskAdmins.keySet()) {
            addTaskAdmins(key, taskAdmins.get(key));
        }
    }

    /**
     * @param taskId
     * @param adminSids
     */
    @SuppressWarnings("unchecked")
    public void addTaskAdminSids(String taskId, List<FlowUserSid> adminSids) {
        Map<String, Set<FlowUserSid>> map = (Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_ADMIN_SIDS);
        if (map == null) {
            map = new HashMap<String, Set<FlowUserSid>>();
        }
        if (!map.containsKey(taskId)) {
            map.put(taskId, new LinkedHashSet<FlowUserSid>());
        }
        map.get(taskId).addAll(adminSids);

        Map<String, List<FlowUserSid>> taskAdminUsers = new HashMap<String, List<FlowUserSid>>();
        taskAdminUsers.put(taskId, Arrays.asList(map.get(taskId).toArray(new FlowUserSid[0])));
        this.addTaskAdminSids(taskAdminUsers);
    }

    /**
     * @param taskAdminSids
     */
    @SuppressWarnings("unchecked")
    public void addTaskAdminSids(Map<String, List<FlowUserSid>> taskAdminSids) {
        if (taskAdminSids == null) {
            return;
        }
        if (!this.containsKey(WorkFlowVariables.FLOW_ADMIN_SIDS)) {
            this.put(WorkFlowVariables.FLOW_ADMIN_SIDS, new HashMap<String, Set<FlowUserSid>>());
        }
        for (String key : taskAdminSids.keySet()) {
            Set<FlowUserSid> linkedSet = new LinkedHashSet<FlowUserSid>();
            if (taskAdminSids.get(key) == null) {
                continue;
            }
            linkedSet.addAll(taskAdminSids.get(key));
            ((Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_ADMIN_SIDS)).put(key, linkedSet);
        }
    }

    /**
     * @param taskId
     * @return
     */
    public Set<String> getTaskViewers(String taskId) {
        Set<FlowUserSid> sids = getTaskViewerSids(taskId);
        Set<String> orgIds = new LinkedHashSet<String>(0);
        orgIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(sids));
        return orgIds;
    }

    /**
     * @param taskId
     * @return
     */
    public Set<FlowUserSid> getTaskViewerSids(String taskId) {
        Object map = this.get(WorkFlowVariables.FLOW_VIEWER_SIDS);
        return map == null ? new LinkedHashSet<>(0)
                : (((Map<String, Set<FlowUserSid>>) map).get(taskId) == null ? new LinkedHashSet<>(0)
                : ((Map<String, Set<FlowUserSid>>) map).get(taskId));
    }

    /**
     * @param taskId
     * @param viewerSids
     */
    public void addTaskViewerSids(String taskId, List<FlowUserSid> viewerSids) {
        Map<String, Set<FlowUserSid>> map = (Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_VIEWER_SIDS);
        if (map == null) {
            map = new HashMap<>();
        }
        if (!map.containsKey(taskId)) {
            map.put(taskId, new LinkedHashSet<>());
        }
        map.get(taskId).addAll(viewerSids);

        Map<String, List<FlowUserSid>> taskViewerUsers = new HashMap<>();
        taskViewerUsers.put(taskId, Arrays.asList(map.get(taskId).toArray(new FlowUserSid[0])));
        this.addTaskViewerSids(taskViewerUsers);
    }

    /**
     * @param taskViewerUsers
     */
    public void addTaskViewerSids(Map<String, List<FlowUserSid>> taskViewerUsers) {
        if (taskViewerUsers == null) {
            return;
        }
        if (!this.containsKey(WorkFlowVariables.FLOW_VIEWER_SIDS)) {
            this.put(WorkFlowVariables.FLOW_VIEWER_SIDS, new HashMap<String, Set<FlowUserSid>>());
        }
        for (String key : taskViewerUsers.keySet()) {
            Set<FlowUserSid> linkedSet = new LinkedHashSet<>();
            if (taskViewerUsers.get(key) == null) {
                continue;
            }
            linkedSet.addAll(taskViewerUsers.get(key));
            ((Map<String, Set<FlowUserSid>>) this.get(WorkFlowVariables.FLOW_VIEWER_SIDS)).put(key, linkedSet);
        }
    }

    /**
     * 使用用户自定义的动态按钮
     */
    public boolean getUseCustomDynamicButton(String taskId) {
        Boolean useButton = (Boolean) this.get(WorkFlowVariables.USER_CUSTOM_DYNAMIC_BUTTON + taskId);
        return useButton == null ? false : useButton;
    }

    /**
     * @param taskId
     * @param useCustomDyBtn
     */
    public void setUseCustomDynamicButton(String taskId, boolean useCustomDyBtn) {
        this.put(WorkFlowVariables.USER_CUSTOM_DYNAMIC_BUTTON + taskId, useCustomDyBtn);
    }

    /**
     * @param taskId
     */
    public CustomDynamicButton getCustomDynamicButton(String taskId) {
        CustomDynamicButton button = (CustomDynamicButton) this.get("customDynamicButton" + taskId);
        return button;

    }

    /**
     * @param taskId
     * @param customDynamicButton
     */
    public void setCustomDynamicButton(String taskId, CustomDynamicButton customDynamicButton) {
        this.put("customDynamicButton" + taskId, customDynamicButton);
    }

    /**
     * 办理人为空自动进入下一个环节
     *
     * @param taskId
     */
    @SuppressWarnings("unchecked")
    public String getEmptyToTask(String taskId) {
        Map<String, String> map = (Map<String, String>) this.get(WorkFlowVariables.EMPTY_TO_TASK);
        return (map == null) ? null : (map.get(taskId)) == null ? null : map.get(taskId).toString();
    }

    /**
     * 办理人为空自动进入下一个环节
     *
     * @param taskId
     * @param emptyToTask
     */
    public void setEmptyToTask(String taskId, String emptyToTask) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(taskId, emptyToTask);
        this.put(WorkFlowVariables.EMPTY_TO_TASK, map);
    }

    /**
     * @param taskId
     */
    @SuppressWarnings("unchecked")
    public boolean isEmptyToTask(String taskId) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.IS_EMPTY_TO_TASK);
        return map == null ? false : (map.get(taskId) == null ? false : map.get(taskId));
    }

    /**
     * @param taskId
     * @param isEmptyToTask
     */
    public void setIsEmptyToTask(String taskId, boolean isEmptyToTask) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put(taskId, isEmptyToTask);
        this.put(WorkFlowVariables.IS_EMPTY_TO_TASK, map);
    }

    /**
     * 办理人为空转办时消息通知已办人员
     *
     * @param taskId
     */
    @SuppressWarnings("unchecked")
    public boolean getEmptyNoteDone(String taskId) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.EMPTY_NOTE_DONE);
        return map == null ? false : (map.get(taskId) == null ? false : map.get(taskId));
    }

    /**
     * 办理人为空转办时消息通知已办人员
     *
     * @param taskId
     * @param emptyNoteDone
     */
    public void setEmptyNoteDone(String taskId, boolean emptyNoteDone) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put(taskId, emptyNoteDone);
        this.put(WorkFlowVariables.EMPTY_NOTE_DONE, map);
    }

    /**
     * 设置任务的环节标识
     *
     * @param key
     * @param taskIdentityUuid
     */
    public void setTaskIdentityUuid(String key, String taskIdentityUuid) {
        this.put(WorkFlowVariables.TASK_IDENTITY_UUID + key, taskIdentityUuid);
    }

    /**
     * 获取任务的环节标识
     *
     * @param key
     * @return
     */
    public String getTaskIdentityUuid(String key) {
        Object taskIdentityUuid = this.get(WorkFlowVariables.TASK_IDENTITY_UUID + key);
        return taskIdentityUuid == null ? null : taskIdentityUuid.toString();
    }

    /**
     * 前一环节ID
     *
     * @param taskId
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getPreTaskId(String toTaskId) {
        Map<String, String> map = (Map<String, String>) this.get(WorkFlowVariables.PRE_TASK_ID);
        return map == null ? null : map.get(toTaskId) == null ? null : map.get(toTaskId).toString();
    }

    /**
     * 前一环节ID
     *
     * @param toTaskId
     * @param preTaskId
     */
    public void setPreTaskId(String toTaskId, String preTaskId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(toTaskId, preTaskId);
        this.put(WorkFlowVariables.PRE_TASK_ID, map);
    }

    /**
     * 前一环节实例UUID
     *
     * @param toTaskId
     * @param uuid
     */
    @SuppressWarnings("unchecked")
    public String getPreTaskInstUuid(String toTaskId) {
        Map<String, String> map = (Map<String, String>) this.get(WorkFlowVariables.PRE_TASK_INST_UUID);
        return map == null ? null : map.get(toTaskId) == null ? null : map.get(toTaskId).toString();
    }

    /**
     * 前一环节实例UUID
     *
     * @param toTaskId
     * @param uuid
     */
    public void setPreTaskInstUuid(String toTaskId, String preTaskInstUuid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(toTaskId, preTaskInstUuid);
        this.put(WorkFlowVariables.PRE_TASK_INST_UUID, map);
    }

    /**
     * @param toTaskId
     * @param preGatewayIds
     */
    public void setPreGatewayIds(String toTaskId, List<String> preGatewayIds) {
        this.put("preGatewayIds_" + toTaskId, preGatewayIds);
    }

    /**
     * @param toTaskId
     */
    public List<String> getPreGatewayIds(String toTaskId) {
        return (List<String>) this.get("preGatewayIds_" + toTaskId);
    }

    /**
     * 是否取消任务
     *
     * @param taskId
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean isCancel(String taskId) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.IS_CANCEL);
        return map == null ? false : (map.get(taskId) == null ? false : map.get(taskId));
    }

    /**
     * 是否取消任务
     *
     * @param taskId
     * @param cancel
     */
    public void setIsCancel(String taskId, boolean cancel) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put(taskId, cancel);
        this.put(WorkFlowVariables.IS_CANCEL, map);
    }

    /**
     * 是否取消任务
     *
     * @param taskId
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean isRollback(String taskId) {
        Map<String, Boolean> map = (Map<String, Boolean>) this.get(WorkFlowVariables.IS_ROLLBACK);
        return map == null ? false : (map.get(taskId) == null ? false : map.get(taskId));
    }

    /**
     * 是否取消任务
     *
     * @param taskId
     * @param rollback
     */
    public void setIsRollback(String taskId, boolean rollback) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put(taskId, rollback);
        this.put(WorkFlowVariables.IS_ROLLBACK, map);
    }

    /**
     * 获取ACL角色
     */
    public String getAclRole() {
        Object aclRole = this.get("ACL_ROLE");
        return aclRole == null ? null : aclRole.toString();
    }

    /**
     * 设置ACL角色
     */
    public void setAclRole(String aclRole) {
        this.put("ACL_ROLE", aclRole);
    }

    /**
     * @return
     */
    public String getViewFormMode() {
        Object viewFormMode = this.get("VIEW_FORM_MODE");
        return viewFormMode == null ? null : viewFormMode.toString();
    }

    /**
     * @param viewFormMode
     */
    public void setViewFormMode(String viewFormMode) {
        this.put("VIEW_FORM_MODE", viewFormMode);
    }

    /**
     * 任务操作动作
     *
     * @param key taskUuid + userId
     */
    public String getAction(String key) {
        Object action = this.get(OPERATION_KEY_PREFIX + key);
        return action == null ? null : action.toString();
    }

    /**
     * 任务操作动作
     *
     * @param key        taskUuid + userId
     * @param actionType
     */
    public void setAction(String key, String action) {
        this.put(OPERATION_KEY_PREFIX + key, action);
    }

    /**
     * 任务操作类型
     *
     * @param key taskUuid + userId
     */
    public String getActionType(String key) {
        Object actionType = this.get(OPERATION_TYPE_KEY_PREFIX + key);
        return actionType == null ? null : actionType.toString();
    }

    /**
     * 任务操作类型
     *
     * @param key        taskUuid + userId
     * @param actionType
     */
    public void setActionType(String key, String actionType) {
        this.put(OPERATION_TYPE_KEY_PREFIX + key, actionType);
    }

    /**
     * 获取意见立场列表
     *
     * @param taskId
     * @param options
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getOptions(String taskId) {
        List<Map<String, String>> options = (List<Map<String, String>>) this.get(OPINIONS_KEY_PREFIX + taskId);
        return options == null ? new ArrayList<Map<String, String>>(0) : options;
    }

    /**
     * 获取意见立场列表
     *
     * @param taskId
     * @param options
     */
    public void setOptions(String taskId, List<Map<String, String>> options) {
        this.put(OPINIONS_KEY_PREFIX + taskId, options);
    }

    /**
     * 获取意见立场值
     *
     * @param key taskUuid + userId
     */
    public String getOpinionValue(String key) {
        Object value = this.get(OPINION_VALUE_KEY_PREFIX + key);
        return value == null ? null : value.toString();
    }

    /**
     * 设置意见立场值
     *
     * @param key          taskUuid + userId
     * @param opinionValue
     */
    public void setOpinionValue(String key, String opinionValue) {
        this.put(OPINION_VALUE_KEY_PREFIX + key, opinionValue);
    }

    /**
     * 获取意见立场值文本
     *
     * @param key taskUuid + userId
     */
    public String getOpinionLabel(String key) {
        Object label = this.get(OPINION_LABEL_KEY_PREFIX + key);
        return label == null ? null : label.toString();
    }

    /**
     * 设置意见立场值文本
     *
     * @param key          taskUuid + userId
     * @param opinionValue
     */
    public void setOpinionLabel(String key, String opinionLabel) {
        this.put(OPINION_LABEL_KEY_PREFIX + key, opinionLabel);
    }

    /**
     * 获取意见内容
     *
     * @param key taskUuid + userId
     */
    public String getOpinionText(String key) {
        Object text = this.get(OPINION_TEXT_KEY_PREFIX + key);
        return text == null ? null : text.toString();
    }

    /**
     * 设置意见内容
     *
     * @param key         taskUuid + userId
     * @param opinionText
     */
    public void setOpinionText(String key, String opinionText) {
        this.put(OPINION_TEXT_KEY_PREFIX + key, opinionText);
    }

    /**
     * 获取意见内容
     *
     * @param key taskUuid + userId
     */
    public List<LogicFileInfo> getOpinionFiles(String key) {
        List<LogicFileInfo> files = (List<LogicFileInfo>) this.get(OPINION_FILE_KEY_PREFIX + key);
        return CollectionUtils.isEmpty(files) ? Collections.emptyList() : files;
    }

    /**
     * 设置意见附件
     *
     * @param key
     * @param logicFileInfos
     */
    public void setOpinionFiles(String key, List<LogicFileInfo> logicFileInfos) {
        this.put(OPINION_FILE_KEY_PREFIX + key, logicFileInfos);
    }

    /**
     * 获取动态表单数据
     *
     * @param dataUuid
     */
    public DyFormData getDyFormData(String dataUuid) {
        return (DyFormData) this.get(dataUuid);
    }

    /**
     * 设置动态表单数据
     *
     * @param dataUuid
     * @param rootFormDataBean
     */
    // public void setRootFormData(String dataUuid, FormAndDataBean
    // rootFormDataBean) {
    // this.put(dataUuid, rootFormDataBean);
    // }
    public void setDyFormData(String dataUuid, DyFormData dyFormData) {
        this.put(dataUuid, dyFormData);
        this.removeUpdatedDyFormData(dataUuid, dyFormData);
    }

    /**
     * @param dataUuid
     * @return
     */
    public Map<String, Map<String, List<String>>> getUpdatedFormDatas(String dataUuid) {
        return (Map<String, Map<String, List<String>>>) this.get("updatedFormDatas_" + dataUuid);
    }

    /**
     * @param dataUuid
     * @param updatedFormDatas
     */
    public void setUpdatedFormDatas(String dataUuid, Map<String, Map<String, List<String>>> updatedFormDatas) {
        this.put("updatedFormDatas_" + dataUuid, updatedFormDatas);
    }

    /**
     * @param dataUuid
     * @param dyFormData
     */
    public void addUpdatedDyFormData(String dataUuid, DyFormData dyFormData) {
        updatedDyformDataMap.put(dataUuid + "_" + dyFormData.getFormUuid(), dyFormData);
    }

    /**
     * @param dataUuid
     * @param dyFormData
     */
    public void removeUpdatedDyFormData(String dataUuid, DyFormData dyFormData) {
        updatedDyformDataMap.remove(dataUuid + "_" + dyFormData.getFormUuid());
    }

    /**
     * @return
     */
    public List<DyFormData> getAllUpdatedDyFormDatas() {
        return Lists.newArrayList(updatedDyformDataMap.values());
    }

    /**
     * @return the updatedDyformDataMap
     */
    public Map<String, DyFormData> getUpdatedDyformDataMap() {
        return updatedDyformDataMap;
    }

    /**
     * @param updatedDyformDataMap 要设置的updatedDyformDataMap
     */
    public void setUpdatedDyformDataMap(Map<String, DyFormData> updatedDyformDataMap) {
        this.updatedDyformDataMap = updatedDyformDataMap;
    }

    /**
     * 设置流程实例标题
     *
     * @param subFlowId
     * @param title
     */
    public String getTitle(String flowDefId) {
        Object title = this.get(WorkFlowVariables.TITLE + flowDefId);
        return title == null ? null : title.toString();
    }

    /**
     * 设置流程实例标题
     *
     * @param subFlowId
     * @param title
     */
    public void setTitle(String flowDefId, String title) {
        this.put(WorkFlowVariables.TITLE + flowDefId, title);
    }

    /**
     * 获取环节自动提交
     *
     * @param taskId
     * @return
     */
    public boolean getAutoSubmit(String taskId) {
        Boolean autoSubmit = (Boolean) this.get(getFlowInstUuid() + WorkFlowVariables.AUTO_SUBMIT + taskId);
        return autoSubmit == null ? false : autoSubmit;
    }

    /**
     * 设置环节自动提交
     *
     * @param taskId
     * @param autoSubmit
     */
    public void setAutoSubmit(String taskId, boolean autoSubmit) {
        this.put(getFlowInstUuid() + WorkFlowVariables.AUTO_SUBMIT + taskId, autoSubmit);
    }

    /**
     * 获取自动提交的办理人
     *
     * @param taskId
     * @param users
     */
    @SuppressWarnings("unchecked")
    public List<String> getAutoSubmitUsers(String taskId) {
        List<String> users = (List<String>) this.get(getFlowInstUuid() + WorkFlowVariables.AUTO_SUBMIT_USERS + taskId);
        return users == null ? new ArrayList<String>(0) : users;
    }

    /**
     * 设置自动提交的办理人
     *
     * @param taskId
     * @param users
     */
    public void setAutoSubmitUsers(String taskId, List<String> users) {
        this.put(getFlowInstUuid() + WorkFlowVariables.AUTO_SUBMIT_USERS + taskId, users);
    }

    /**
     * 获取是否异步执行
     *
     * @param subFlowId
     * @param async
     */
    public boolean getIsAsync(String flowId) {
        Boolean async = (Boolean) this.get(WorkFlowVariables.NEW_FLOW_IS_ASYNC + flowId);
        return async == null ? false : async;
    }

    /**
     * 设置是否异步执行
     *
     * @param subFlowId
     * @param async
     */
    public void setIsAsync(String flowId, boolean async) {
        this.put(WorkFlowVariables.NEW_FLOW_IS_ASYNC + flowId, async);
    }

    /**
     * 获取流程实例是否按静默的方式执行
     *
     * @param flowInstUuid
     * @return
     */
    public boolean isSilent(String flowInstUuid) {
        Boolean isSilent = (Boolean) this.get("isSilent" + flowInstUuid);
        return isSilent == null ? false : isSilent;
    }

    /**
     * 设置流程实例是否按静默的方式执行
     *
     * @param flowInstUuid
     * @param silent
     */
    public void setSilent(String flowInstUuid, boolean silent) {
        this.put("isSilent" + flowInstUuid, silent);
    }

    /**
     * 获取子环节是否异步执行
     *
     * @param taskId
     * @return
     */
    // public boolean getSubTaskNodeIsAsync(String taskId) {
    // Boolean async = (Boolean) this.get("SUB_TASK_NODE_" + taskId);
    // return async == null ? false : async;
    // }

    /**
     * 设置子环节是否异步执行
     *
     * @param id
     * @param async
     */
    // public void setSubTaskNodeIsAsync(String taskId, boolean async) {
    // this.put("SUB_TASK_NODE_" + taskId, async);
    // }

    /**
     * 获取子流程等待合并<subFlowInstUuid, isWait>
     *
     * @param waitForMerge
     */
    @SuppressWarnings("unchecked")
    public Map<String, Boolean> getWaitForMerge() {
        Map<String, Boolean> merge = (Map<String, Boolean>) this.get("waitForMerge");
        return merge == null ? new HashMap<String, Boolean>() : merge;
    }

    /**
     * 设置子流程等待合并<subFlowInstUuid, isWait>
     *
     * @param waitForMerge
     */
    public void setWaitForMerge(Map<String, Boolean> waitForMerge) {
        this.put("waitForMerge", waitForMerge);
    }

    /**
     * 获取任务操作UUID
     *
     * @param taskUuid
     * @param operationUuid
     */
    public String getOperationUuid(String taskUuid) {
        Object uuid = this.get("OperationUuid" + taskUuid);
        return uuid == null ? null : uuid.toString();
    }

    /**
     * 设置任务操作UUID
     *
     * @param taskUuid
     * @param operationUuid
     */
    public void setOperationUuid(String taskUuid, String operationUuid) {
        this.put("OperationUuid" + taskUuid, operationUuid);
    }

    /** 预留字段开始 **/
    // // 16字符长度
    // private String reservedText1;
    // // 64字符长度
    // private String reservedText2;
    // // 64字符长度
    // private String reservedText3;
    // // 255字符长度
    // private String reservedText4;
    // // 255字符长度
    // private String reservedText5;
    // // 255字符长度
    // private String reservedText6;
    // // 2000字符长度
    // private String reservedText7;
    //
    // private Integer reservedNumber1;
    // private Double reservedNumber2;
    // private Double reservedNumber3;
    //
    // private Date reservedDate1;
    // private Date reservedDate2;

    /**
     * 设置是否更新预留字段
     *
     * @param flowInstUuid
     * @param update
     */
    // public void setUpdateReservedFields(String flowInstUuid, boolean update)
    // {
    // this.put("UpdateReservedFields_" + flowInstUuid, update);
    // }

    /**
     * 获取是否更新预留字段
     *
     * @param flowInstUuid
     * @return
     */
    // public boolean isUpdateReservedFields(String flowInstUuid) {
    // Object updateReservedFields = this.get("UpdateReservedFields_" +
    // flowInstUuid);
    // return updateReservedFields == null ? false : (Boolean)
    // updateReservedFields;
    // }

    /**
     * @return the reservedText1
     */
    public String getReservedText1() {
        Object reservedText1 = this.get("reservedText1");
        return reservedText1 == null ? null : reservedText1.toString();
    }

    /**
     * @param reservedText1 要设置的reservedText1
     */
    public void setReservedText1(String reservedText1) {
        this.put("reservedText1", reservedText1);

        setUpdateReservedText1(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText1() {
        Object updateReservedText1 = this.get("updateReservedText1");
        return updateReservedText1 == null ? false : (Boolean) updateReservedText1;
    }

    /**
     * @param updateReservedText1
     */
    private void setUpdateReservedText1(boolean updateReservedText1) {
        this.put("updateReservedText1", updateReservedText1);
    }

    /**
     * @return the reservedText2
     */
    public String getReservedText2() {
        Object reservedText2 = this.get("reservedText2");
        return reservedText2 == null ? null : reservedText2.toString();
    }

    /**
     * @param reservedText2 要设置的reservedText2
     */
    public void setReservedText2(String reservedText2) {
        this.put("reservedText2", reservedText2);

        setUpdateReservedText2(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText2() {
        Object updateReservedText2 = this.get("updateReservedText2");
        return updateReservedText2 == null ? false : (Boolean) updateReservedText2;
    }

    /**
     * @param updateReservedText2
     */
    private void setUpdateReservedText2(boolean updateReservedText2) {
        this.put("updateReservedText2", updateReservedText2);
    }

    /**
     * @return the reservedText3
     */
    public String getReservedText3() {
        Object reservedText3 = this.get("reservedText3");
        return reservedText3 == null ? null : reservedText3.toString();
    }

    /**
     * @param reservedText3 要设置的reservedText3
     */
    public void setReservedText3(String reservedText3) {
        this.put("reservedText3", reservedText3);

        setUpdateReservedText3(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText3() {
        Object updateReservedText3 = this.get("updateReservedText3");
        return updateReservedText3 == null ? false : (Boolean) updateReservedText3;
    }

    /**
     * @param updateReservedText3
     */
    private void setUpdateReservedText3(boolean updateReservedText3) {
        this.put("updateReservedText3", updateReservedText3);
    }

    /**
     * @return the reservedText4
     */
    public String getReservedText4() {
        Object reservedText4 = this.get("reservedText4");
        return reservedText4 == null ? null : reservedText4.toString();
    }

    /**
     * @param reservedText4 要设置的reservedText4
     */
    public void setReservedText4(String reservedText4) {
        this.put("reservedText4", reservedText4);

        setUpdateReservedText4(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText4() {
        Object updateReservedText4 = this.get("updateReservedText4");
        return updateReservedText4 == null ? false : (Boolean) updateReservedText4;
    }

    /**
     * @param updateReservedText4
     */
    private void setUpdateReservedText4(boolean updateReservedText4) {
        this.put("updateReservedText4", updateReservedText4);
    }

    /**
     * @return the reservedText5
     */
    public String getReservedText5() {
        Object reservedText5 = this.get("reservedText5");
        return reservedText5 == null ? null : reservedText5.toString();
    }

    /**
     * @param reservedText5 要设置的reservedText5
     */
    public void setReservedText5(String reservedText5) {
        this.put("reservedText5", reservedText5);

        setUpdateReservedText5(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText5() {
        Object updateReservedText5 = this.get("updateReservedText5");
        return updateReservedText5 == null ? false : (Boolean) updateReservedText5;
    }

    /**
     * @param updateReservedText5
     */
    private void setUpdateReservedText5(boolean updateReservedText5) {
        this.put("updateReservedText5", updateReservedText5);
    }

    /**
     * @return the reservedText6
     */
    public String getReservedText6() {
        Object reservedText6 = this.get("reservedText6");
        return reservedText6 == null ? null : reservedText6.toString();
    }

    /**
     * @param reservedText6 要设置的reservedText6
     */
    public void setReservedText6(String reservedText6) {
        this.put("reservedText6", reservedText6);

        setUpdateReservedText6(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText6() {
        Object updateReservedText6 = this.get("updateReservedText6");
        return updateReservedText6 == null ? false : (Boolean) updateReservedText6;
    }

    /**
     * @param updateReservedText6
     */
    private void setUpdateReservedText6(boolean updateReservedText6) {
        this.put("updateReservedText6", updateReservedText6);
    }

    /**
     * @return the reservedText7
     */
    public String getReservedText7() {
        Object reservedText7 = this.get("reservedText7");
        return reservedText7 == null ? null : reservedText7.toString();
    }

    /**
     * @param reservedText7 要设置的reservedText7
     */
    public void setReservedText7(String reservedText7) {
        this.put("reservedText7", reservedText7);

        setUpdateReservedText7(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText7() {
        Object updateReservedText7 = this.get("updateReservedText7");
        return updateReservedText7 == null ? false : (Boolean) updateReservedText7;
    }

    /**
     * @param updateReservedText7
     */
    private void setUpdateReservedText7(boolean updateReservedText7) {
        this.put("updateReservedText7", updateReservedText7);
    }

    /**
     * @return the reservedText8
     */
    public String getReservedText8() {
        Object reservedText8 = this.get("reservedText8");
        return reservedText8 == null ? null : reservedText8.toString();
    }

    /**
     * @param reservedText8 要设置的reservedText8
     */
    public void setReservedText8(String reservedText8) {
        this.put("reservedText8", reservedText8);

        setUpdateReservedText8(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText8() {
        Object updateReservedText8 = this.get("updateReservedText8");
        return updateReservedText8 == null ? false : (Boolean) updateReservedText8;
    }

    /**
     * @param updateReservedText8
     */
    private void setUpdateReservedText8(boolean updateReservedText8) {
        this.put("updateReservedText8", updateReservedText8);
    }

    /**
     * @return the reservedText9
     */
    public String getReservedText9() {
        Object reservedText9 = this.get("reservedText9");
        return reservedText9 == null ? null : reservedText9.toString();
    }

    /**
     * @param reservedText9 要设置的reservedText9
     */
    public void setReservedText9(String reservedText9) {
        this.put("reservedText9", reservedText9);

        setUpdateReservedText9(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText9() {
        Object updateReservedText9 = this.get("updateReservedText9");
        return updateReservedText9 == null ? false : (Boolean) updateReservedText9;
    }

    /**
     * @param updateReservedText9
     */
    private void setUpdateReservedText9(boolean updateReservedText9) {
        this.put("updateReservedText9", updateReservedText9);
    }

    /**
     * @return the reservedText10
     */
    public String getReservedText10() {
        Object reservedText10 = this.get("reservedText10");
        return reservedText10 == null ? null : reservedText10.toString();
    }

    /**
     * @param reservedText10 要设置的reservedText10
     */
    public void setReservedText10(String reservedText10) {
        this.put("reservedText10", reservedText10);

        setUpdateReservedText10(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText10() {
        Object updateReservedText10 = this.get("updateReservedText10");
        return updateReservedText10 == null ? false : (Boolean) updateReservedText10;
    }

    /**
     * @param updateReservedText10
     */
    private void setUpdateReservedText10(boolean updateReservedText10) {
        this.put("updateReservedText10", updateReservedText10);
    }

    /**
     * @return the reservedText11
     */
    public String getReservedText11() {
        Object reservedText11 = this.get("reservedText11");
        return reservedText11 == null ? null : reservedText11.toString();
    }

    /**
     * @param reservedText11 要设置的reservedText11
     */
    public void setReservedText11(String reservedText11) {
        this.put("reservedText11", reservedText11);

        setUpdateReservedText11(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText11() {
        Object updateReservedText11 = this.get("updateReservedText11");
        return updateReservedText11 == null ? false : (Boolean) updateReservedText11;
    }

    /**
     * @param updateReservedText11
     */
    private void setUpdateReservedText11(boolean updateReservedText11) {
        this.put("updateReservedText11", updateReservedText11);
    }

    /**
     * @return the reservedText12
     */
    public String getReservedText12() {
        Object reservedText12 = this.get("reservedText12");
        return reservedText12 == null ? null : reservedText12.toString();
    }

    /**
     * @param reservedText12 要设置的reservedText12
     */
    public void setReservedText12(String reservedText12) {
        this.put("reservedText12", reservedText12);

        setUpdateReservedText12(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedText12() {
        Object updateReservedText12 = this.get("updateReservedText12");
        return updateReservedText12 == null ? false : (Boolean) updateReservedText12;
    }

    /**
     * @param updateReservedText12
     */
    private void setUpdateReservedText12(boolean updateReservedText12) {
        this.put("updateReservedText12", updateReservedText12);
    }

    /**
     * @return the reservedNumber1
     */
    public Integer getReservedNumber1() {
        Object reservedNumber1 = this.get("reservedNumber1");
        return reservedNumber1 == null ? null : Integer.valueOf(reservedNumber1.toString());
    }

    /**
     * @param reservedNumber1 要设置的reservedNumber1
     */
    public void setReservedNumber1(Integer reservedNumber1) {
        this.put("reservedNumber1", reservedNumber1);

        setUpdateReservedNumber1(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedNumber1() {
        Object updateReservedNumber1 = this.get("updateReservedNumber1");
        return updateReservedNumber1 == null ? false : (Boolean) updateReservedNumber1;
    }

    /**
     * @param updateReservedNumber1
     */
    private void setUpdateReservedNumber1(boolean updateReservedNumber1) {
        this.put("updateReservedNumber1", updateReservedNumber1);
    }

    /**
     * @return the reservedNumber2
     */
    public Double getReservedNumber2() {
        Object reservedNumber2 = this.get("reservedNumber2");
        return reservedNumber2 == null ? null : Double.valueOf(reservedNumber2.toString());
    }

    /**
     * @param reservedNumber2 要设置的reservedNumber2
     */
    public void setReservedNumber2(Double reservedNumber2) {
        this.put("reservedNumber2", reservedNumber2);

        setUpdateReservedNumber2(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedNumber2() {
        Object updateReservedNumber2 = this.get("updateReservedNumber2");
        return updateReservedNumber2 == null ? false : (Boolean) updateReservedNumber2;
    }

    /**
     * @param updateReservedNumber2
     */
    private void setUpdateReservedNumber2(boolean updateReservedNumber2) {
        this.put("updateReservedNumber2", updateReservedNumber2);
    }

    /**
     * @return the reservedNumber3
     */
    public Double getReservedNumber3() {
        Object reservedNumber3 = this.get("reservedNumber3");
        return reservedNumber3 == null ? null : Double.valueOf(reservedNumber3.toString());
    }

    /**
     * @param reservedNumber3 要设置的reservedNumber3
     */
    public void setReservedNumber3(Double reservedNumber3) {
        this.put("reservedNumber3", reservedNumber3);

        setUpdateReservedNumber3(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedNumber3() {
        Object updateReservedNumber3 = this.get("updateReservedNumber3");
        return updateReservedNumber3 == null ? false : (Boolean) updateReservedNumber3;
    }

    /**
     * @param updateReservedNumber3
     */
    private void setUpdateReservedNumber3(boolean updateReservedNumber3) {
        this.put("updateReservedNumber3", updateReservedNumber3);
    }

    /**
     * @return the reservedDate1
     */
    public Date getReservedDate1() {
        Object reservedDate1 = this.get("reservedDate1");
        return reservedDate1 == null ? null : (Date) reservedDate1;
    }

    /**
     * @param reservedDate1 要设置的reservedDate1
     */
    public void setReservedDate1(Date reservedDate1) {
        this.put("reservedDate1", reservedDate1);

        setUpdateReservedDate1(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedDate1() {
        Object updateReservedDate1 = this.get("updateReservedDate1");
        return updateReservedDate1 == null ? false : (Boolean) updateReservedDate1;
    }

    /**
     * @param updateReservedDate1
     */
    private void setUpdateReservedDate1(boolean updateReservedDate1) {
        this.put("updateReservedDate1", updateReservedDate1);
    }

    /**
     * @return the reservedDate2
     */
    public Date getReservedDate2() {
        Object reservedDate2 = this.get("reservedDate2");
        return reservedDate2 == null ? null : (Date) reservedDate2;
    }

    /**
     * @param reservedDate2 要设置的reservedDate2
     */
    public void setReservedDate2(Date reservedDate2) {
        this.put("reservedDate2", reservedDate2);

        setUpdateReservedDate2(true);
    }

    /**
     * @return
     */
    public boolean isUpdateReservedDate2() {
        Object updateReservedDate2 = this.get("updateReservedDate2");
        return updateReservedDate2 == null ? false : (Boolean) updateReservedDate2;
    }

    /**
     * @param updateReservedDate2
     */
    private void setUpdateReservedDate2(boolean updateReservedDate2) {
        this.put("updateReservedDate2", updateReservedDate2);
    }

    /** 预留字段结束 **/

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @return
     */
    public Integer getTransferCode(String taskInstUuid) {
        Object transferCode = this.get("transferCode_" + taskInstUuid);
        return transferCode == null ? null : (Integer) transferCode;
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @param todoType
     */
    public void setTransferCode(String taskInstUuid, Integer transferCode) {
        this.put("transferCode_" + taskInstUuid, transferCode);
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @return
     */
    public Integer getActionCode(String taskInstUuid) {
        Object actionCode = this.get("actionCode_" + taskInstUuid);
        return actionCode == null ? null : (Integer) actionCode;
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @param todoType
     */
    public void setActionCode(String taskInstUuid, Integer actionCode) {
        this.put("actionCode_" + taskInstUuid, actionCode);
    }

    /**
     * @param key
     * @param string
     */
    public void setCustomData(String key, Object value) {
        customParamMap.put(key, value);
    }

    /**
     * @param key
     * @return
     */
    public Object getCustomData(String key) {
        Object value = customParamMap.get(key);
        return value == null ? null : value;
    }

    /**
     * @return
     */
    public Set<String> getCustomDataKeySet() {
        return customParamMap.keySet();
    }

    /**
     * 获取自定义数据是否已同步
     *
     * @param flowInstUuid
     * @return
     */
    public boolean isCustomDataSynchronized(String flowInstUuid) {
        Object sync = this.get("isCustomDataSynchronized_" + flowInstUuid);
        return sync == null ? false : (Boolean) sync;
    }

    /**
     * 设置自定义数据是否已同步
     *
     * @param flowInstUuid
     * @param sync
     */
    public void setCustomDataSynchronized(String flowInstUuid, boolean sync) {
        this.put("isCustomDataSynchronized_" + flowInstUuid, sync);
    }

    /**
     * @param key
     * @return
     */
    public Object removeCustomData(String key) {
        return customParamMap.remove(key);
    }

    /**
     * 获取提交按钮ID
     *
     * @return
     */
    public String getSubmitButtonId() {
        Object submitButtonId = this.get("submitButtonId");
        return submitButtonId == null ? "" : submitButtonId.toString();
    }

    /**
     * 设置提交按钮ID
     *
     * @param submitButtonId
     */
    public void setSubmitButtonId(String submitButtonId) {
        this.put("submitButtonId", submitButtonId);
    }

    public String getJobSelected(String userId) {
        Object job = this.get("jobSelected_" + userId);
        return job == null ? "" : (String) job;
    }

    public void setJobSelected(String userId, String jobId) {
        this.put("jobSelected_" + userId, jobId);
    }

    public String getMainJob() {
        Object job = this.get("mainJob");
        return job == null ? "" : (String) job;
    }

    public void setMainJob(String jobId) {
        this.put("mainJob", jobId);
    }

    /**
     * 获取业务类型
     *
     * @return
     */
    public String getBusinessType() {
        Object businessType = this.get("businessType");
        return businessType == null ? "" : businessType.toString();
    }

    /**
     * 设置业务类型
     *
     * @return
     */
    public void setBusinessType(String businessType) {
        this.put("businessType", businessType);
    }

    /**
     * 设置要退回到的环节
     *
     * @param taskInstUuid
     * @param rollBackToTaskId
     */
    public void setRollbackToTaskId(String taskInstUuid, String rollBackToTaskId) {
        this.put("RollbackToTaskId_" + taskInstUuid, rollBackToTaskId);
    }

    /**
     * 设置要退回到的环节
     *
     * @param taskInstUuid
     * @param rollBackToTaskId
     */
    public String getRollbackToTaskId(String taskInstUuid) {
        Object rollBackToTaskId = this.get("RollbackToTaskId_" + taskInstUuid);
        return rollBackToTaskId == null ? null : rollBackToTaskId.toString();
    }

    /**
     * 设置要退回到的环节实例UUID
     *
     * @param taskInstUuid
     * @param rollBackToTaskId
     */
    public void setRollbackToTaskInstUuid(String taskInstUuid, String rollBackToTaskUuid) {
        this.put("rollbackToTaskInstUuid_" + taskInstUuid, rollBackToTaskUuid);
    }

    /**
     * 设置要退回到的环节实例UUID
     *
     * @param taskInstUuid
     * @param rollBackToTaskId
     */
    public String getRollbackToTaskInstUuid(String taskInstUuid) {
        Object rollbackToTaskInstUuid = this.get("rollbackToTaskInstUuid_" + taskInstUuid);
        return rollbackToTaskInstUuid == null ? null : rollbackToTaskInstUuid.toString();
    }

    /**
     * 获取是否退回到前环节
     *
     * @param taskInstUuid
     * @return
     */
    public void setRollbackToPreTask(String taskInstUuid, boolean isRollbackToPreTask) {
        this.put("RollbackToPreTask_" + taskInstUuid, isRollbackToPreTask);
    }

    /**
     * 获取是否退回到前环节，默认为false
     *
     * @param taskInstUuid
     * @return
     */
    public boolean isRollbackToPreTask(String taskInstUuid) {
        Object rollbackToPreTaskAssignee = this.get("RollbackToPreTask_" + taskInstUuid);
        return rollbackToPreTaskAssignee == null ? false : (Boolean) rollbackToPreTaskAssignee;
    }

    /**
     * 设置套打模板ID
     *
     * @param taskId
     * @param printTemplateId
     */
    public void setPrintTemplateId(String taskId, String printTemplateId) {
        this.put("PrintTemplateId_" + taskId, printTemplateId);
    }

    /**
     * 获取套打模板ID
     *
     * @param taskId
     * @return
     */
    public String getPrintTemplateId(String taskId) {
        Object printTemplateId = this.get("PrintTemplateId_" + taskId);
        return printTemplateId == null ? "" : printTemplateId.toString();
    }

    /**
     * 设置套打模板ID
     *
     * @param taskId
     * @param printTemplateUuid
     */
    public void setPrintTemplateUuid(String taskId, String printTemplateUuid) {
        this.put("PrintTemplateUuid_" + taskId, printTemplateUuid);
    }

    /**
     * 获取套打模板ID
     *
     * @param taskId
     * @return
     */
    public String getPrintTemplateUuid(String taskId) {
        Object printTemplateUuid = this.get("PrintTemplateUuid_" + taskId);
        return printTemplateUuid == null ? "" : printTemplateUuid.toString();
    }

    /**
     * 设置归档夹UUID
     *
     * @param taskId
     * @return
     */
    public void setArchiveFolderUuid(String taskId, String archiveFolderUuid) {
        this.put("ArchiveFolderUuid_" + taskId, archiveFolderUuid);
    }

    /**
     * 获取归档夹UUID
     *
     * @param taskId
     * @return
     */
    public String getArchiveFolderUuid(String taskId) {
        Object archiveFolderUuid = this.get("ArchiveFolderUuid_" + taskId);
        return archiveFolderUuid == null ? "" : archiveFolderUuid.toString();
    }

    /**
     * 设置任务是否进行分支处理
     *
     * @param fromTaskId
     * @param forking
     */
    public void setTaskForking(String fromTaskId, boolean forking) {
        this.put("TaskForking_" + fromTaskId, forking);
    }

    /**
     * 返回任务是否进行分支处理
     *
     * @param fromTaskId
     * @param forking
     */
    public boolean isTaskForking(String fromTaskId) {
        Object forking = this.get("TaskForking_" + fromTaskId);
        return forking == null ? false : (Boolean) forking;
    }

    /**
     * 设置任务是否进行合并处理
     *
     * @param toTaskId
     * @param joining
     */
    public void setTaskJoining(String toTaskId, boolean joining) {
        this.put("TaskJoining_" + toTaskId, joining);
    }

    /**
     * 返回任务是否进行合并处理
     *
     * @param toTaskId
     * @return
     */
    public boolean isTaskJoining(String toTaskId) {
        Object joining = this.get("TaskJoining_" + toTaskId);
        return joining == null ? false : (Boolean) joining;
    }

    /**
     * @param taskId
     * @param allowJoin
     */
    public void setTaskAllowJoin(String taskId, boolean allowJoin) {
        this.put("TaskAllowJoin_" + taskId, allowJoin);
    }

    /**
     * @param taskId
     * @param allowJoin
     */
    public boolean isTaskAllowJoin(String taskId) {
        Object allowJoin = this.get("TaskAllowJoin_" + taskId);
        return allowJoin == null ? true : (Boolean) allowJoin;
    }

    /**
     * @param fromTaskId
     * @param toTaskId
     * @param direction
     */
    public void setTaskDirection(String fromTaskId, String toTaskId, Direction direction) {
        this.put("TaskDirection_" + fromTaskId + "_" + toTaskId, direction);
    }

    /**
     * @param fromTaskId
     * @param toTaskId
     * @return
     */
    public Direction getTaskDirection(String fromTaskId, String toTaskId) {
        Object direction = this.get("TaskDirection_" + fromTaskId + "_" + toTaskId);
        return direction == null ? null : (Direction) direction;
    }

    /**
     * @param toTaskId
     * @param forkingOrder
     */
    public void setTaskForkingOrder(String toTaskId, int forkingOrder) {
        this.put("TaskForkingOrder_" + toTaskId, forkingOrder);
    }

    /**
     * @param toTaskId
     * @param forkingOrder
     */
    public int getTaskForkingOrder(String toTaskId) {
        Object forkingOrder = this.get("TaskForkingOrder_" + toTaskId);
        return forkingOrder == null ? 0 : (Integer) forkingOrder;
    }

    /**
     * @param entityUuid
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getParallelTaskInstUuids(String taskInstUuid) {
        Object parallelTaskInstUuids = this.get("parallelTaskInstUuids_" + taskInstUuid);
        return parallelTaskInstUuids == null ? new ArrayList<String>(0) : (List<String>) parallelTaskInstUuids;
    }

    /**
     * @param entityUuid
     * @return
     */
    public void setParallelTaskInstUuids(String taskInstUuid, List<String> parallelTaskInstUuids) {
        this.put("parallelTaskInstUuids_" + taskInstUuid, parallelTaskInstUuids);
    }

    /**
     * 设置前一流程属性
     *
     * @param toTaskId
     * @param string
     * @param isParallel
     */
    public void setPreTaskProperties(String toTaskId, String propertyName, Object propertyValue) {
        preTaskProperties.put(toTaskId + propertyName, propertyValue);
    }

    /**
     * 获取前一流程属性
     *
     * @param toTaskId
     * @param string
     * @param isParallel
     */
    public Object getPreTaskProperties(String toTaskId, String propertyName) {
        return preTaskProperties.get(toTaskId + propertyName);
    }

    /**
     * @return
     */
    public boolean isDaemon() {
        Object daemon = this.get("daemonTask");
        return daemon == null ? false : (Boolean) daemon;
    }

    /**
     * 如何描述该方法
     *
     * @param daemon
     */
    public void setDaemon(boolean daemon) {
        this.put("daemonTask", daemon);
    }

    /**
     * @return
     */
    public boolean isGenerateSerialNumber() {
        Object generateSerialNumber = this.get("generateSerialNumber");
        return generateSerialNumber == null ? true : (Boolean) generateSerialNumber;
    }

    /**
     * @param generateSerialNumber
     */
    public void setGenerateSerialNumber(boolean generateSerialNumber) {
        this.put("generateSerialNumber", generateSerialNumber);
    }

    /**
     * @return
     */
    public boolean isSendMsg() {
        Object sendMsg = this.get("sendMsg");
        return sendMsg == null ? true : (Boolean) sendMsg;
    }

    /**
     * @param sendMsg
     */
    public void setSendMsg(boolean sendMsg) {
        this.put("sendMsg", sendMsg);
    }

    /**
     * @return
     */
    public String getMsgSendWay() {
        return (String) this.get("msgSendWay");
    }

    /**
     * @param msgSendWay
     */
    public void setMsgSendWay(String msgSendWay) {
        this.put("msgSendWay", msgSendWay);
    }

    /**
     * @return
     */
    public boolean isArchive() {
        Object archive = this.get("archive");
        return archive == null ? true : (Boolean) archive;
    }

    /**
     * @param archive
     */
    public void setArchive(boolean archive) {
        this.put("archive", archive);
    }

    /**
     * 是否为api调用
     *
     * @return
     */
    public boolean isApiInvoke() {
        Boolean apiInvoke = (Boolean) this.get("ApiInvoke");
        return apiInvoke == null ? false : apiInvoke;
    }

    /**
     * 是否为api调用
     *
     * @return
     */
    public void setApiInvoke(boolean apiInvoke) {
        this.put("ApiInvoke", apiInvoke);
    }

    /**
     * 如何描述该方法
     *
     * @param taskId
     * @param firstTaskNode
     */
    public void setIsFirstTaskNode(String taskId, Boolean firstTaskNode) {
        this.put("isFirstTaskNode_" + taskId, firstTaskNode);
    }

    /**
     * @param taskId
     * @return
     */
    public Boolean getIsFirstTaskNode(String taskId) {
        Boolean isFirstTaskNode = (Boolean) this.get("isFirstTaskNode_" + taskId);
        return isFirstTaskNode == null ? false : isFirstTaskNode;
    }

    /**
     * @return
     */
    public SubmitResult getSubmitResult() {
        return submitResult;
    }

    /* modified by huanglinchuan 2014.10.22 begin */

    public Token getToken() {
        return (Token) this.get("token");
    }

    public void setToken(Token token) {
        this.put("token", token);
    }

    /* modified by huanglinchuan 2014.10.22 end */

    /**
     * 意见日志信息
     *
     * @param key
     * @param opinionLogUuids
     */
    public void setTaskFormOpinionLogUuids(String key, List<String> taskFormOpinionLogUuids) {
        this.put(TASK_FORM_OPINION_LOG_UUIDS_KEY_PREFIX + key, taskFormOpinionLogUuids);
    }

    /**
     * 意见日志信息
     *
     * @param key
     * @param opinionLogUuids
     */
    @SuppressWarnings("unchecked")
    public List<String> getTaskFormOpinionLogUuids(String key) {
        List<String> uuids = (List<String>) this.get(TASK_FORM_OPINION_LOG_UUIDS_KEY_PREFIX + key);
        return uuids == null ? emptyList : (List<String>) uuids;
    }

    /**
     * 环节办理人的人员选项代表的组织用户
     *
     * @param taskId
     * @param optionUnitUsers
     */
    public void setTaskOptionUnitUserIds(String taskId, Set<String> optionUnitUsers) {
        this.put("TaskOptionUnitUserIds_" + taskId, optionUnitUsers);
    }

    /**
     * @param taskId
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<String> getTaskOptionUnitUserIds(String taskId) {
        Set<String> optionUnitUserIds = (Set<String>) this.get("TaskOptionUnitUserIds_" + taskId);
        return (optionUnitUserIds == null ? new HashSet<String>(0) : optionUnitUserIds);
    }

    /**
     * @return
     */
    public String getFlowInstUuid() {
        Object flowInstUuid = this.get(WorkFlowVariables.FLOW_INST_UUID);
        return flowInstUuid == null ? StringUtils.EMPTY : flowInstUuid.toString();
    }

    /**
     * @param flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.put(WorkFlowVariables.FLOW_INST_UUID, flowInstUuid);
    }

    /**
     *
     */
    public String getTaskInstUuid() {
        Object taskInstUuid = this.get(WorkFlowVariables.TASK_INST_UUID);
        return taskInstUuid == null ? StringUtils.EMPTY : taskInstUuid.toString();
    }

    /**
     * @param taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.put(WorkFlowVariables.TASK_INST_UUID, taskInstUuid);
    }

    /**
     *
     */
    public String getParentTaskInstUuid(String flowInstUuid) {
        Object parentTaskInstUuid = this.get(flowInstUuid + "_parentTaskInstUuid");
        return parentTaskInstUuid == null ? StringUtils.EMPTY : parentTaskInstUuid.toString();
    }

    /**
     * @param taskInstUuid
     */
    public void setParentTaskInstUuid(String flowInstUuid, String parentTaskInstUuid) {
        this.put(flowInstUuid + "_parentTaskInstUuid", parentTaskInstUuid);
    }

    /**
     * 获取办理时限类型
     *
     * @param flowDefId
     * @return
     */
    public String getLimitType(String flowDefId) {
        Object limitType = this.get(flowDefId + "_limitType");
        return limitType == null ? StringUtils.EMPTY : limitType.toString();
    }

    /**
     * 设置办理时限类型
     *
     * @param flowDefId
     * @param limitTimeType
     */
    public void setLimitType(String flowDefId, String limitTimeType) {
        this.put(flowDefId + "_limitType", limitTimeType);
    }

    /**
     * 获取办理时限
     *
     * @param flowDefId
     * @return
     */
    public String getLimitTime(String flowDefId) {
        Object limitTime = this.get(flowDefId + "_limitTime");
        return limitTime == null ? StringUtils.EMPTY : limitTime.toString();
    }

    /**
     * 设置办理时限
     *
     * @param flowDefId
     * @param limitTime
     */
    public void setLimitTime(String flowDefId, String limitTime) {
        this.put(flowDefId + "_limitTime", limitTime);
    }

    /**
     * 获取时限计时单位
     *
     * @param flowDefId
     * @return
     */
    public Integer getLimitUnit(String flowDefId) {
        Object limitUnit = this.get(flowDefId + "_limitUnit");
        return limitUnit == null ? null : Integer.valueOf(limitUnit.toString());
    }

    /**
     * 设置时限计时单位
     *
     * @param flowDefId
     * @param limitUnit
     */
    public void setLimitUnit(String flowDefId, Integer limitUnit) {
        this.put(flowDefId + "_limitUnit", limitUnit);
    }

    /**
     * @param taskId
     * @return
     */
    public String getTaskTodoId(String taskId) {
        Object todoId = this.get("TaskTodoId_" + taskId);
        return ObjectUtils.toString(todoId, StringUtils.EMPTY);
    }

    /**
     * @param taskId
     */
    public void setTaskTodoId(String taskId, String todoId) {
        this.put("TaskTodoId_" + taskId, todoId);
    }

    /**
     * @param taskId
     * @return
     */
    public String getTaskTodoName(String taskId) {
        Object todoName = this.get("TaskTodoName_" + taskId);
        return ObjectUtils.toString(todoName, StringUtils.EMPTY);
    }

    /**
     * @param taskId
     */
    public void setTaskTodoName(String taskId, String todoName) {
        this.put("TaskTodoName_" + taskId, todoName);
    }

    /**
     * @param toTaskId
     * @param taskBranchUuid
     */
    public void setRelatedTaskBranchUuid(String toTaskId, String taskBranchUuid) {
        this.put("RelatedTaskBranchUuid_" + toTaskId, taskBranchUuid);
    }

    /**
     * @param toTaskId
     * @return
     */
    public String getRelatedTaskBranchUuid(String toTaskId) {
        Object taskBranchUuid = this.get("RelatedTaskBranchUuid_" + toTaskId);
        return ObjectUtils.toString(taskBranchUuid, StringUtils.EMPTY);
    }

    /**
     * @param subTaskId
     * @param logAddSubflow
     */
    public void setLogAddSubflow(String subTaskId, boolean logAddSubflow) {
        this.put("LogAddSubflow_" + subTaskId, logAddSubflow);
    }

    /**
     * @param subTaskId
     * @return
     */
    public boolean isLogAddSubflow(String subTaskId) {
        Object logAddSubflow = this.get("LogAddSubflow_" + subTaskId);
        return Boolean.TRUE.equals(logAddSubflow);
    }

    /**
     * @param subTaskId
     * @param actionName
     */
    public void setAddSubflowActionName(String subTaskId, String actionName) {
        this.put("AddSubflowActionName_" + subTaskId, actionName);
    }

    /**
     * @param subTaskId
     * @return
     */
    public String getAddSubflowActionName(String subTaskId) {
        Object actionName = this.get("AddSubflowActionName_" + subTaskId);
        return ObjectUtils.toString(actionName, StringUtils.EMPTY);
    }

    /**
     * @param branchTaskId
     * @param logAddBranchTask
     */
    public void setLogAddBranchTask(String branchTaskId, boolean logAddBranchTask) {
        this.put("LogAddBranchTask_" + branchTaskId, logAddBranchTask);
    }

    /**
     * @param branchTaskId
     * @return
     */
    public boolean isLogAddBranchTask(String branchTaskId) {
        Object logAddBranchTask = this.get("LogAddBranchTask_" + branchTaskId);
        return Boolean.TRUE.equals(logAddBranchTask);
    }

    /**
     * @return
     */
    public boolean isLogUserOperation() {
        Object isLogUserOperation = this.get("isLogUserOperation");
        return isLogUserOperation == null ? true : Boolean.TRUE.equals(isLogUserOperation);
    }

    /**
     * @param branchTaskId
     * @param logAddBranchTask
     */
    public void setLogUserOperation(boolean bogUserOperation) {
        this.put("isLogUserOperation", bogUserOperation);
    }

    /**
     * @param branchTaskId
     * @param actionName
     */
    public void setAddBranchTaskActionName(String branchTaskId, String actionName) {
        this.put("AddBranchTaskActionName_" + branchTaskId, actionName);
    }

    /**
     * @param branchTaskId
     * @return
     */
    public String getAddBranchTaskActionName(String branchTaskId) {
        Object actionName = this.get("AddBranchTaskActionName_" + branchTaskId);
        return ObjectUtils.toString(actionName, StringUtils.EMPTY);
    }

    /**
     * @param fromTaskId
     * @param useFormFieldUsers
     */
    public void setUseFormFieldUsers(String fromTaskId, boolean useFormFieldUsers) {
        this.put("UseFormFieldUsers_" + fromTaskId, useFormFieldUsers);
    }

    /**
     * @param fromTaskId
     * @return
     */
    public boolean isUseFormFieldUsers(String fromTaskId) {
        Object useFormFieldUsers = this.get("UseFormFieldUsers_" + fromTaskId);
        return Boolean.TRUE.equals(useFormFieldUsers);
    }

    /**
     * 过滤仅使用人可用的按钮
     */
    public void filterOwnerCustomDynamicButton() {
        List<CustomDynamicButton> filteredButtons = new ArrayList<CustomDynamicButton>();
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        List<CustomDynamicButton> customDynamicButtons = this.getCustomDynamicButtons();
        for (CustomDynamicButton button : customDynamicButtons) {
            if (CollectionUtils.isEmpty(button.getOwners())) {
                filteredButtons.add(button);
                continue;
            }

            List<String> ownerIds = IdentityResolverStrategy.resolveUserIds(button.getOwners());
            if (ownerIds.contains(currentUserId)) {// 在使用人范围内，允许操作到
                filteredButtons.add(button);
                continue;
            }

        }
        this.setCustomDynamicButtons(filteredButtons);
    }

    /**
     * 设置退回后允许直接提交至本环节
     *
     * @param fromTaskId
     * @param toTaskId
     */
    public void setTaskOfAllowReturnAfterRollback(String fromTaskId, String toTaskId) {
        this.put("allowReturnAfterRollback_" + fromTaskId, toTaskId);
    }

    /**
     * 获取退回后允许直接提交至本环节
     *
     * @param fromTaskId
     */
    public String getTaskOfAllowReturnAfterRollback(String fromTaskId) {
        return (String) this.get("allowReturnAfterRollback_" + fromTaskId);
    }

    /**
     * @param toTaskId
     * @param sidGranularity
     */
    public void setSidGranularity(String toTaskId, String sidGranularity) {
        this.put("sidGranularity_" + toTaskId, sidGranularity);
    }

    /**
     * @param toTaskId
     * @return
     */
    public String getSidGranularity(String toTaskId) {
        return (String) this.get("sidGranularity_" + toTaskId);
    }

    /**
     * @param taskId
     * @return
     */
    public List<Record> getRecords(String taskId) {
        return (List<Record>) this.get("records_" + taskId);
    }

    /**
     * @param taskId
     * @param records
     */
    public void setRecords(String taskId, List<Record> records) {
        this.put("records_" + taskId, records);
    }

    /**
     * @param taskId
     * @return
     */
    public Boolean isAutoCompletedTask(String taskId) {
        return BooleanUtils.isTrue((Boolean) this.get("autoCompletedTask_" + taskId));
    }

    /**
     * @param taskId
     * @param completed
     */
    public void setAutoCompletedTask(String taskId, boolean completed) {
        this.put("autoCompletedTask_" + taskId, completed);
    }


    /**
     * @param userId
     * @return
     */
    public String getUserJobIdentityId(String userId, String taskInstUuid) {
        String jobIdentityId = (String) this.get("userJobIdentityId_" + taskInstUuid + userId);
        return jobIdentityId == null ? (String) this.get("userJobIdentityId_" + null + userId) : jobIdentityId;
    }

    /**
     * @param userId
     * @param taskInstUuid
     * @param jobIdentityId
     */
    public void setUserJobIdentityId(String userId, String taskInstUuid, String jobIdentityId) {
        this.put("userJobIdentityId_" + taskInstUuid + userId, jobIdentityId);
    }

}

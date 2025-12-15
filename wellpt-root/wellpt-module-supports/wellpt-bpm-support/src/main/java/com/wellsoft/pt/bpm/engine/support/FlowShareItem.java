/*
 * @(#)2018年6月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.workflow.work.bean.WorkProcessBean;

import java.util.Date;
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
 * 2018年6月4日.1	zhulh		2018年6月4日		Create
 * </pre>
 * @date 2018年6月4日
 */
public class FlowShareItem extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -540748796337373433L;

    // 流程定义ID
    private String flowDefId;
    // 流程实例UUID
    private String flowInstUuid;
    // 环节实例UUID
    private String taskInstUuid;
    // 表单定义UUID
    private String formUuid;
    // 表单数据UUID
    private String dataUuid;
    // 承办部门(人)
    private String todoName;
    // 当前环节
    private String currentTaskName;
    // 当前环节ID
    private String currentTaskId;
    // 当前环节办理人
    private String currentTodoUserName;
    // 反馈时限
    private Date dueTime;
    // 计时状态(0正常、1预警、2到期、3逾期)
    private Integer timingState;
    // 反馈时限格式化字符串
    private String dueTimeFormatString;
    // 剩余时限
    private String remainingTime;
    // 办理情况
    private List<WorkProcessBean> workProcesses;
    // 办理结果附件
    private List<LogicFileInfo> resultFiles;
    // 上级环节ID
    private String parentTaskId;
    // 上级环节实例UUID
    private String parentTaskInstUuid;
    // 上级流程实例UUID
    private String parentFlowInstUuid;
    // 分发完成状态
    private Integer dispatchState;
    // 分发结果信息
    private String dispatchResultMsg;

    private String flowLabelId;

    private String flowLabel;

    // 扩展列
    private Map<String, Object> extras = Maps.newHashMap();

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

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
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the todoName
     */
    public String getTodoName() {
        return todoName;
    }

    /**
     * @param todoName 要设置的todoName
     */
    public void setTodoName(String todoName) {
        this.todoName = todoName;
    }

    /**
     * @return the currentTaskName
     */
    public String getCurrentTaskName() {
        return currentTaskName;
    }

    /**
     * @param currentTaskName 要设置的currentTaskName
     */
    public void setCurrentTaskName(String currentTaskName) {
        this.currentTaskName = currentTaskName;
    }

    /**
     * @return the currentTodoUserName
     */
    public String getCurrentTodoUserName() {
        return currentTodoUserName;
    }

    /**
     * @param currentTodoUserName 要设置的currentTodoUserName
     */
    public void setCurrentTodoUserName(String currentTodoUserName) {
        this.currentTodoUserName = currentTodoUserName;
    }

    /**
     * @return the dueTime
     */
    public Date getDueTime() {
        return dueTime;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public Integer getTimingState() {
        return timingState;
    }

    public void setTimingState(Integer timingState) {
        this.timingState = timingState;
    }

    /**
     * @return the dueTimeFormatString
     */
    public String getDueTimeFormatString() {
        return dueTimeFormatString;
    }

    /**
     * @param dueTimeFormatString 要设置的dueTimeFormatString
     */
    public void setDueTimeFormatString(String dueTimeFormatString) {
        this.dueTimeFormatString = dueTimeFormatString;
    }

    /**
     * @return the remainingTime
     */
    public String getRemainingTime() {
        return remainingTime;
    }

    /**
     * @param remainingTime 要设置的remainingTime
     */
    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    /**
     * @return the workProcesses
     */
    public List<WorkProcessBean> getWorkProcesses() {
        return workProcesses;
    }

    /**
     * @param workProcesses 要设置的workProcesses
     */
    public void setWorkProcesses(List<WorkProcessBean> workProcesses) {
        this.workProcesses = workProcesses;
    }

    /**
     * @return the resultFiles
     */
    public List<LogicFileInfo> getResultFiles() {
        return resultFiles;
    }

    /**
     * @param resultFiles 要设置的resultFiles
     */
    public void setResultFiles(List<LogicFileInfo> resultFiles) {
        this.resultFiles = resultFiles;
    }

    /**
     * @return the parentTaskId
     */
    public String getParentTaskId() {
        return parentTaskId;
    }

    /**
     * @param parentTaskId 要设置的parentTaskId
     */
    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    /**
     * @return the parentTaskInstUuid
     */
    public String getParentTaskInstUuid() {
        return parentTaskInstUuid;
    }

    /**
     * @param parentTaskInstUuid 要设置的parentTaskInstUuid
     */
    public void setParentTaskInstUuid(String parentTaskInstUuid) {
        this.parentTaskInstUuid = parentTaskInstUuid;
    }

    /**
     * @return the parentFlowInstUuid
     */
    public String getParentFlowInstUuid() {
        return parentFlowInstUuid;
    }

    /**
     * @param parentFlowInstUuid 要设置的parentFlowInstUuid
     */
    public void setParentFlowInstUuid(String parentFlowInstUuid) {
        this.parentFlowInstUuid = parentFlowInstUuid;
    }

    /**
     * @return the dispatchState
     */
    public Integer getDispatchState() {
        return dispatchState;
    }

    /**
     * @param dispatchState 要设置的dispatchState
     */
    public void setDispatchState(Integer dispatchState) {
        this.dispatchState = dispatchState;
    }

    /**
     * @return the dispatchResultMsg
     */
    public String getDispatchResultMsg() {
        return dispatchResultMsg;
    }

    /**
     * @param dispatchResultMsg 要设置的dispatchResultMsg
     */
    public void setDispatchResultMsg(String dispatchResultMsg) {
        this.dispatchResultMsg = dispatchResultMsg;
    }

    /**
     * @return the extras
     */
    public Map<String, Object> getExtras() {
        return extras;
    }

    /**
     * @param extras 要设置的extras
     */
    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    public String getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(String currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    public String getFlowLabelId() {
        return flowLabelId;
    }

    public void setFlowLabelId(String flowLabelId) {
        this.flowLabelId = flowLabelId;
    }

    public String getFlowLabel() {
        return flowLabel;
    }

    public void setFlowLabel(String flowLabel) {
        this.flowLabel = flowLabel;
    }
}

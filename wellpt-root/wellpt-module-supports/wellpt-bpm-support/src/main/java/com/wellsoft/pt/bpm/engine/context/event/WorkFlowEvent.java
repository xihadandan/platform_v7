/*
 * @(#)2013-8-20 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.event;

import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.workflow.enums.WorkFlowFieldMapping;

import javax.persistence.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 流程事件对象
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-8-20.1	zhulh		2013-8-20		Create
 * </pre>
 * @date 2013-8-20
 */
public class WorkFlowEvent implements Event {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -678545843094275827L;
    // 当前用户ID
    private String userId;
    // 操作动作
    private String action;
    // 操作动作类型
    private String actionType;
    // 操作动作代码
    private Integer actionCode;
    // 操作动作代码
    private Integer transferCode;
    // 当前任务ID
    private String taskId;
    // 当前任务名称
    private String taskName;
    // 当前任务UUID
    private String taskInstUuid;
    // 签署意见值
    private String taskOpinionValue;
    // 签署意见名称
    private String taskOpinionName;
    // 签署意见内容
    private String taskOpinionText;
    // 签署意见附件
    private List<LogicFileInfo> taskOpinionFiles;
    // 前一个任务ID
    private String preTaskId;
    // 下一个任务ID
    private String nextTaskId;
    // 流向ID
    private String directionId;
    // 当前流程实例ID
    private String flowInstId;
    // 当前流程实例UUID
    private String flowInstUuid;
    // 当前流程标题
    private String title;
    // 当前流程发起人ID
    private String flowStartUserId;
    // 当前流程所有者ID
    private String flowOwnerId;
    // 发起流程实例的部门ID
    private String flowStartDepartmentId;
    // 流程实例所属部门ID
    private String flowOwnerDepartmentId;
    // 发起流程实例的单位ID
    private String flowStartUnitId;
    // 流程实例所属单位ID
    private String flowOwnerUnitId;
    // 到期时间
    private Date dueTime;
    // 是否子流程实例
    private boolean isSubFlowInstce;
    // 流程定义UUID
    private String flowDefUuid;
    // 流程定义ID
    private String flowId;
    // 流程名称
    private String flowName;
    // 动态表单定义UUID
    private String formUuid;
    // 动态表单数据UUID
    private String dataUuid;
    // 动态表单数据
    private DyFormData dyFormData;
    // 运行时数据
    private TaskData taskData;
    // 预留字段
    @Transient
    private Map<WorkFlowFieldMapping, Object> reservedFieldMap = new HashMap<WorkFlowFieldMapping, Object>();

    /**
     *
     */
    public WorkFlowEvent(String flowInstUuid) {
        super();
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @param userId
     * @param action
     * @param actionType
     * @param actionCode
     * @param transferCode
     * @param startedTaskNodeId
     * @param taskId
     * @param taskName
     * @param taskInstUuid
     * @param taskOpinionValue
     * @param taskOpinionName
     * @param taskOpinionText
     * @param taskOpinionFiles
     * @param preTaskId
     * @param nextTaskId
     * @param directionId
     * @param flowInstId
     * @param flowInstUuid
     * @param title
     * @param flowStartUserId
     * @param flowOwnerId
     * @param flowStartDepartmentId
     * @param flowOwnerDepartmentId
     * @param flowStartUnitId
     * @param flowOwnerUnitId
     * @param dueTime
     * @param isSubFlowInstce
     * @param flowDefUuid
     * @param flowId
     * @param flowName
     * @param formUuid
     * @param dataUuid
     * @param dyFormData
     * @param taskData
     * @param reservedFieldMap
     */
    public WorkFlowEvent(String userId, String action, String actionType, Integer actionCode, Integer transferCode,
                         String startedTaskNodeId, String taskId, String taskName, String taskInstUuid, String taskOpinionValue,
                         String taskOpinionName, String taskOpinionText, List<LogicFileInfo> taskOpinionFiles, String preTaskId, String nextTaskId, String directionId,
                         String flowInstId, String flowInstUuid, String title, String flowStartUserId, String flowOwnerId,
                         String flowStartDepartmentId, String flowOwnerDepartmentId, String flowStartUnitId, String flowOwnerUnitId,
                         Date dueTime, boolean isSubFlowInstce, String flowDefUuid, String flowId, String flowName, String formUuid,
                         String dataUuid, DyFormData dyFormData, TaskData taskData,
                         Map<WorkFlowFieldMapping, Object> reservedFieldMap) {
        super();
        this.userId = userId;
        this.action = action;
        this.actionType = actionType;
        this.actionCode = actionCode;
        this.transferCode = transferCode;
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskInstUuid = taskInstUuid;
        this.taskOpinionValue = taskOpinionValue;
        this.taskOpinionName = taskOpinionName;
        this.taskOpinionText = taskOpinionText;
        this.taskOpinionFiles = taskOpinionFiles;
        this.preTaskId = preTaskId;
        this.nextTaskId = nextTaskId;
        this.directionId = directionId;
        this.flowInstId = flowInstId;
        this.flowInstUuid = flowInstUuid;
        this.title = title;
        this.flowStartUserId = flowStartUserId;
        this.flowOwnerId = flowOwnerId;
        this.flowStartDepartmentId = flowStartDepartmentId;
        this.flowOwnerDepartmentId = flowOwnerDepartmentId;
        this.flowStartUnitId = flowStartUnitId;
        this.flowOwnerUnitId = flowOwnerUnitId;
        this.dueTime = dueTime;
        this.isSubFlowInstce = isSubFlowInstce;
        this.flowDefUuid = flowDefUuid;
        this.flowId = flowId;
        this.flowName = flowName;
        this.formUuid = formUuid;
        this.dataUuid = dataUuid;
        this.dyFormData = dyFormData;
        this.taskData = taskData;
        this.reservedFieldMap = reservedFieldMap;
    }

    /**
     * @return the userId
     */
    @Override
    public String getUserId() {
        return userId;
    }

    /**
     * @return the action
     */
    @Override
    public String getAction() {
        return action;
    }

    /**
     * @return the actionType
     */
    @Override
    public String getActionType() {
        return actionType;
    }

    /**
     * @return the actionCode
     */
    @Override
    public Integer getActionCode() {
        return actionCode;
    }

    /**
     * @return the transferCode
     */
    @Override
    public Integer getTransferCode() {
        return transferCode;
    }

    /**
     * @return the taskId
     */
    @Override
    public String getTaskId() {
        return taskId;
    }

    /**
     * @return the taskName
     */
    @Override
    public String getTaskName() {
        return taskName;
    }

    /**
     * @return the taskInstUuid
     */
    @Override
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getTaskOpinionValue()
     */
    @Override
    public String getTaskOpinionValue() {
        return taskOpinionValue;
    }

    /**
     * @return the taskOpinionName
     */
    @Override
    public String getTaskOpinionName() {
        return taskOpinionName;
    }

    /**
     * @param taskOpinionName 要设置的taskOpinionName
     */
    public void setTaskOpinionName(String taskOpinionName) {
        this.taskOpinionName = taskOpinionName;
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getTaskOpinionText()
     */
    @Override
    public String getTaskOpinionText() {
        return taskOpinionText;
    }

    /**
     * 获取签署意见附件
     *
     * @return
     */
    @Override
    public List<LogicFileInfo> getTaskOpinionFiles() {
        return this.taskOpinionFiles;
    }

    /**
     * @return the preTaskId
     */
    @Override
    public String getPreTaskId() {
        return preTaskId;
    }

    /**
     * @return the nextTaskId
     */
    @Override
    public String getNextTaskId() {
        return nextTaskId;
    }

    /**
     * 获取流向ID(流向事件有效)
     *
     * @return
     */
    @Override
    public String getDirectionId() {
        return this.directionId;
    }

    /**
     * @return the flowInstId
     */
    @Override
    public String getFlowInstId() {
        return flowInstId;
    }

    /**
     * @return the flowInstUuid
     */
    @Override
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @return the title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * @return the flowStartUserId
     */
    @Override
    public String getFlowStartUserId() {
        return flowStartUserId;
    }

    /**
     * @param flowStartUserId 要设置的flowStartUserId
     */
    public void setFlowStartUserId(String flowStartUserId) {
        this.flowStartUserId = flowStartUserId;
    }

    /**
     * @return the flowOwnerId
     */
    @Override
    public String getFlowOwnerId() {
        return flowOwnerId;
    }

    /**
     * @param flowOwnerId 要设置的flowOwnerId
     */
    public void setFlowOwnerId(String flowOwnerId) {
        this.flowOwnerId = flowOwnerId;
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getFlowStartDepartmentId()
     */
    @Override
    public String getFlowStartDepartmentId() {
        return flowStartDepartmentId;
    }

    /**
     * @return the flowOwnerDepartmentId
     */
    @Override
    public String getFlowOwnerDepartmentId() {
        return flowOwnerDepartmentId;
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getFlowStartUnitId()
     */
    @Override
    public String getFlowStartUnitId() {
        return flowStartUnitId;
    }

    /**
     * @return the flowOwnerUnitId
     */
    @Override
    public String getFlowOwnerUnitId() {
        return flowOwnerUnitId;
    }

    /**
     * @return the dueTime
     */
    @Override
    public Date getDueTime() {
        return dueTime;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return the isSubFlowInstce
     */
    @Override
    public boolean isSubFlowInstce() {
        return isSubFlowInstce;
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getFlowDefUuid()
     */
    @Override
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @return the flowId
     */
    @Override
    public String getFlowId() {
        return flowId;
    }

    /**
     * @return the flowName
     */
    @Override
    public String getFlowName() {
        return flowName;
    }

    /**
     * @return the formUuid
     */
    @Override
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @return the dataUuid
     */
    @Override
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getFormData()
     */
    @Override
    public DyFormData getDyFormData() {
        return this.dyFormData;
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getTaskData()
     */
    @Override
    public TaskData getTaskData() {
        return taskData;
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText1()
     */
    @Override
    public String getReservedText1() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_1);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText2()
     */
    @Override
    public String getReservedText2() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_2);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText3()
     */
    @Override
    public String getReservedText3() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_3);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText4()
     */
    @Override
    public String getReservedText4() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_4);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText5()
     */
    @Override
    public String getReservedText5() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_5);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText6()
     */
    @Override
    public String getReservedText6() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_6);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText7()
     */
    @Override
    public String getReservedText7() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_7);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText8()
     */
    @Override
    public String getReservedText8() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_8);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText9()
     */
    @Override
    public String getReservedText9() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_9);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText10()
     */
    @Override
    public String getReservedText10() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_10);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText11()
     */
    @Override
    public String getReservedText11() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_11);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedText12()
     */
    @Override
    public String getReservedText12() {
        return (String) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_TEXT_12);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedNumber1()
     */
    @Override
    public Integer getReservedNumber1() {
        return (Integer) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_NUMBER_1);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedNumber2()
     */
    @Override
    public Double getReservedNumber2() {
        return (Double) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_NUMBER_2);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedNumber3()
     */
    @Override
    public Double getReservedNumber3() {
        return (Double) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_NUMBER_3);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedDate1()
     */
    @Override
    public Date getReservedDate1() {
        return (Date) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_DATE_1);
    }

    /**
     * (non-Javadoc)
     *
     * @see Event#getReservedDate2()
     */
    @Override
    public Date getReservedDate2() {
        return (Date) reservedFieldMap.get(WorkFlowFieldMapping.RESERVED_DATE_2);
    }

}

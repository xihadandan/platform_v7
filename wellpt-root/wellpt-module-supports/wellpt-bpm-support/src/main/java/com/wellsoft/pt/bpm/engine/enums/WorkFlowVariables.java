/*
 * @(#)2013-3-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-25.1	zhulh		2013-3-25		Create
 * </pre>
 * @date 2013-3-25
 */
public enum WorkFlowVariables {
    // 是否启动流程
    START_NEW_FLOW("startNewFlow"),

    // 动态表单定义UUID
    FORM_UUID("formUuid"),
    // 动态表单数据UUID
    DATA_UUID("dataUuid"),
    // 动态表单数据
    FORM_DATA("formData"),

    // 办理意见
    OPINION("opinion"),

    // 流程标题
    TITLE("title"),

    // 流程申请人
    CREATOR("wf.creator"),

    // 是否来自手机APP的操作标识
    IS_MOBILE_APP("isMobileApp"),

    // 当前操作流程的用户ID
    USER_ID("userId"),
    // 当前操作流程的用户名
    USER_NAME("userName"),
    // 当前操作流程的用户信息
    USER_DETAILS("userDetails"),
    // 当前流程所有者ID
    FLOW_OWNER_ID("flowOwnerId"),
    // 当前流程发起部门ID
    FLOW_START_DEPARTMENT_ID("flowStartDepartmentId"),
    // 当前流程所在部门ID
    FLOW_OWNER_DEPARTMENT_ID("flowOwnerDepartmentId"),
    // 当前流程发起单位ID
    FLOW_START_UNIT_ID("flowStartUnitId"),
    // 当前流程所在单位ID
    FLOW_OWNER_UNIT_ID("flowOwnerUnitId"),
    // 当前流程的流程实例UUID
    FLOW_INST_UUID("flowInstUuid"),
    // 是否撤回任务
    IS_CANCEL("isCancel"),
    // 是否退回任务
    IS_ROLLBACK("isRollback"),
    // 只否由用户指定参与者
    IS_SPECIFY_TASK_USER("isSpecifyTaskUser"),
    IS_SPECIFY_TASK_COPY_USER("isSpecifyTaskCopyUser"),
    // 是否办理人为空自动进入下一个环节
    IS_EMPTY_TO_TASK("isEmptyToTask"),
    // 办理人为空自动进入下一个环节
    EMPTY_TO_TASK("emptyToTask"),
    // 办理人为空转办时消息通知已办人员
    EMPTY_NOTE_DONE("emptyNoteDone"),
    // 只需要其中一个人办理
    IS_ANYONE("isAnyone"),
    // 按人员顺序依次办理
    IS_BY_ORDER("isByOrder"),
    // 流程参与者
    TASK_TODO_USERS("taskTodoUsers"),
    // 流程参与者
    FLOW_USERS("flowUsers"),
    // 流程参与者授权主体
    FLOW_USER_SIDS("flowUsersSids"),
    // 流程抄送者
    FLOW_COPY_USERS("flowCopyUsers"),
    // 流程抄送者授权主体
    FLOW_COPY_USER_SIDS("flowCopyUserSids"),
    // 流程决策人员授权主体
    FLOW_DECISION_MAKER_SIDS("flowDecisionMakerSids"),
    // 流程督办者
    FLOW_MONITORS("flowMonitors"),
    // 流程督办者授权主体
    FLOW_MONITOR_SIDS("flowMonitorSids"),
    // 流程监控者
    FLOW_ADMINS("flowAdmins"),
    // 流程监控者授权主体
    FLOW_ADMIN_SIDS("flowAdminSids"),
    // 流程阅读者授权主体
    FLOW_VIEWER_SIDS("flowViewerSids"),

    // 环节ID
    TASK_ID("taskId"),
    // 环节名称
    TASK_NAME("taskName"),
    // 环节参与人原始配置名称
    TASK_USER_RAW_NAMES("taskUserNames"),
    // 环节实例UUID
    TASK_INST_UUID("taskInstUuid"),
    // 流程任务参与者标识UUID
    TASK_IDENTITY_UUID("taskIdentityUuid"),
    // 前一环节ID
    PRE_TASK_ID("preTaskId"),
    // 前一环节实例UUID
    PRE_TASK_INST_UUID("preTaskInstUuid"),
    // 选择的下一流向ID
    TO_DIRECTION_ID("toDirectionId"),
    // 选择的下一流向ID MAP
    TO_DIRECTION_ID_MAP("toDirectionIdMap"),
    // 选择的下一环节ID
    TO_TASK_ID("toTaskId"),
    // 选择的下一环节ID MAP
    TO_TASK_ID_MAP("toTaskIdMap"),
    // 跳转到下一环节ID
    GOTO_TASK("gotoTask"),
    // 选择的下一子流程ID
    TO_SUB_FLOW_ID("toSubFlowId"),
    // 已完成的子流程ID
    COMPLETED_SUB_FLOW_ID("completedSubFlowId"),
    // 已完成的子流程实例UUID
    COMPLETED_SUB_FLOW_INST_UUID("completedSubFlowInstUuid"),
    // 已完成的子流程动态表单定义UUID
    COMPLETED_SUB_FLOW_FORM_UUID("completedSubFlowFormUuid"),
    // 已完成的子流程动态表单数据UUID
    COMPLETED_SUB_FLOW_DATA_UUID("completedSubFlowDataUuid"),
    // 已完成的子流程动态表单数据
    COMPLETED_SUB_FLOW_FORM_DATA("completedSubFlowFormData"),

    // 流水号定义ID
    SERIAL_NO_DEF_ID("serialNoDefId"),
    // 流水号
    SERIAL_NO("serialNo"),

    // 使用用户自定义的动态按钮
    USER_CUSTOM_DYNAMIC_BUTTON("useCustomDynamicButton"),
    // 用户自定义动态按钮的提交环节
    CUSTOM_DYNAMIC_BUTTON_TASK_ID("customDynamicButtonTaskId"),
    // 用户自定义动态按钮的提交对象
    CUSTOM_DYNAMIC_BUTTON_USER_IDS("customDynamicButtonUserIds"),
    // 用户自定义动态按钮的抄送对象
    CUSTOM_DYNAMIC_BUTTON_COPY_USER_IDS("customDynamicButtonCopyUserIds"),

    // 新流程是异步执行
    NEW_FLOW_IS_ASYNC("isAsync"),

    // 自动提交
    AUTO_SUBMIT("autoSubmit"),
    // 自动提交的办理人
    AUTO_SUBMIT_USERS("autoSubmitUsers"),

    // 所有流程
    FLOW_ALL("ALL"),
    // 流程分类前缀
    FLOW_CATEGORY_PREFIX("FLOW_CATEGORY_");

    private String name;

    /**
     * @param name
     */
    private WorkFlowVariables(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.name;
    }

}

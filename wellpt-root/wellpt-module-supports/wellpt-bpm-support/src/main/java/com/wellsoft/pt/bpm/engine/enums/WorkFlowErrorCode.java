/*
 * @(#)2013-3-8 V1.0
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
 * 2013-3-8.1	zhulh		2013-3-8		Create
 * </pre>
 * @date 2013-3-8
 */
public enum WorkFlowErrorCode {
    // 工作流异常
    WorkFlowException("WorkFlow Exception", "WorkFlowException"),
    // 任务没有指定参与者
    TaskNotAssignedUser("Task Not Assigned User", "TaskNotAssignedUser"),
    // 任务没有指定抄送人
    TaskNotAssignedCopyUser("Task Not Assigned Copy User", "TaskNotAssignedCopyUser"),
    // 任务没有指定督办人
    TaskNotAssignedMonitor("Task Not Assigned Monitor", "TaskNotAssignedMonitor"),
    // 只能选择一个用户
    OnlyChooseOneUser("Only Choose One User", "OnlyChooseOneUser"),
    // 无法找到可用的判断分支流向
    JudgmentBranchFlowNotFound("Judgment Branch Flow Not Found", "JudgmentBranchFlowNotFound"),
    // 找到多个判断分支流向
    MultipleJudgmentBranch("Multiple Judgment Branch", "MultipleJudgmentBranch"),
    // 找不到子流程的流程ID异常类
    SubFlowNotFound("Sub Flow Not Found", "SubFlowNotFound"),
    // 子流程合并等待异常类
    SubFlowMerge("Sub Flow Merge", "SubFlowMerge"),
    // 用户没有权限访问流程异常类
    IdentityNotFlowPermission("Identity Not Flow Permission", "IdentityNotFlowPermission"),
    // 找不到退回操作的退回环节异常类
    RollbackTaskNotFound("Rollback TaskNot Found", "RollbackTaskNotFound");

    private String name;
    private String value;

    private WorkFlowErrorCode(String name, String value) {
        this.name = name;
        this.value = value;
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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

}

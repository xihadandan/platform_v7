/*
 * @(#)2014-5-20 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-5-20.1	zhulh		2014-5-20		Create
 * </pre>
 * @date 2014-5-20
 */
public enum JsonDataErrorCode {
    // 业务异常
    BusinessException,
    // 工作流异常
    WorkFlowException,
    // 任务没有指定参与者
    TaskNotAssignedUser,
    // 一人多职未选择职位
    MultiJobNotSelected,
    // 任务没有指定抄送人
    TaskNotAssignedCopyUser,
    // 任务没有指定转办人
    TaskNotAssignedTransferUser,
    // 任务没有指定会签人
    TaskNotAssignedCounterSignUser,
    // 任务没有指定加签人
    TaskNotAssignedAddSignUser,
    // 任务没有指定督办人
    TaskNotAssignedMonitor,
    // 任务没有指定决策人员
    TaskNotAssignedDecisionMaker,
    // 任务数据已变更
    TaskDataChanged,
    // 选择具体办理人
    ChooseSpecificUser,
    // 只能选择一个办理人
    OnlyChooseOneUser,
    // 无法找到可用的判断分支流向
    JudgmentBranchFlowNotFound,
    // 找到多个判断分支流向
    MultiJudgmentBranch,
    // 找不到子流程的流程ID异常类
    SubFlowNotFound,
    // 子流程合并等待异常类
    SubFlowMerge,
    // 用户没有权限访问流程异常类
    IdentityNotFlowPermission,
    // 找不到退回操作的退回环节异常类
    RollbackTaskNotFound,
    // 找不到特送环节操作的环节异常类
    GotoTaskNotFound,
    // 选择归档夹
    ChooseArchiveFolder,
    // 没有填写打印原因
    PrintReasonNotAssigned,
    // 必填域为空
    RequiredFieldIsBlank,
    // 登录超时
    SessionExpired,
    // Hibernate乐观锁
    StaleObjectState,
    // 文件已经签出
    FileHasCheckOut,
    // 保存表单数据异常
    SaveData,
    // 字段验证
    FieldValidation,

    /**
     * 参数异常,//added by hunt, 处理检查表单数据唯一性验证时添加的
     **/
    ParameterException,
    /**
     * 表单定义格式错误
     **/
    FormDefinitionFomatException,

    /**
     * 表单定义更新失败
     */
    FormDefinitionUpdateException,
    /**
     * 表单定义保存失败
     */
    FormDefinitionSaveException,

    /**
     * 表单数据检验错误
     */
    FormDataValidateException,

    /**
     * 显示单据正被表单使用
     */
    FormModelBeUsedException,

    /**
     * 语法异常
     **/
    SQLGrammarException
}

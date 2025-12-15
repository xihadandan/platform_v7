/*
 * @(#)2013-6-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Lists;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;

import java.util.HashMap;
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
 * 2013-6-4.1	zhulh		2013-6-4		Create
 * </pre>
 * @date 2013-6-4
 */
public class WorkFlowOperation {
    // 提交
    public static final String SUBMIT = "Submit";
    // 会签提交
    public static final String COUNTER_SIGN_SUBMIT = "CounterSign Submit";
    // 转办提交
    public static final String TRANSFER_SUBMIT = "Transfer Submit";
    // 委托提交
    public static final String DELEGATION_SUBMIT = "Delegation Submit";
    // 完成
    public static final String COMPLETE = "Complete";
    // 退回
    public static final String ROLLBACK = "Rollback";
    // 直接退回
    public static final String DIRECT_ROLLBACK = "DirectRollback";
    // 退回主流程
    public static final String ROLLBACK_TO_MAIN_FLOW = "RollbackToMainFlow";
    // 撤回
    public static final String CANCEL = "Cancel";
    // 转办
    public static final String TRANSFER = "Transfer";
    // 会签
    public static final String COUNTER_SIGN = "CounterSign";
    // 加签
    public static final String ADD_SIGN = "AddSign";
    // 关注
    public static final String ATTENTION = "Attention";
    // 取消关注
    public static final String UNFOLLOW = "Unfollow";
    // 抄送
    public static final String COPY_TO = "CopyTo";
    // 催办
    public static final String REMIND = "Remind";
    // 标记已阅
    public static final String MARK_READ = "MarkRead";
    // 标记未阅
    public static final String MARK_UNREAD = "MarkUnread";
    // 移交人员
    public static final String HAND_OVER = "HandOver";
    // 移交环节
    public static final String GOTO_TASK = "GotoTask";
    // 签署意见
    public static final String SIGN_OPINION = "SignOpinion";
    // 挂起
    public static final String SUSPEND = "Suspend";
    // 恢复
    public static final String RESUME = "Resume";
    // 计时器挂起
    public static final String SUSPEND_TIMER = "SuspendTimer";
    // 计时器恢复
    public static final String RESUME_TIMER = "ResumeTimer";
    // 拒绝，直接结束
    public static final String REJECT = "Reject";
    // 委托
    public static final String DELEGATION = "Delegation";
    // 办理人为空自动进入下一结点
    public static final String SKIP_TASK = "SkipTask";
    // 删除
    public static final String DELETE = "Delete";
    // 恢复
    public static final String RECOVER = "Recover";
    // 直接回收委托任务在待办状态中
    public static final String TAKE_BACK_TODO_DELEGATION = "TakeBackTodoDelegation";
    // 添加承办部门
    public static final String ADD_SUB_FLOW = "AddSubFlow";
    // 重办
    public static final String REDO = "Redo";
    // 信息分发
    public static final String DISTRIBUTE_INFO = "DistributeInfo";
    // 协办时限
    public static final String CHANGE_LIMIT_TIME = "ChangeLimitTime";
    // 终止
    public static final String STOP = "Stop";
    // 套打
    public static final String PRINT = "Print";
    // 保存
    public static final String SAVE = "Save";
    // 保存草稿
    public static final String SAVE_DRAFT = "SaveDraft";

    private static Map<String, String> operationMap = new HashMap<String, String>();

    static {
        operationMap.put(SUBMIT, "提交");
        operationMap.put(COUNTER_SIGN_SUBMIT, "提交");
        operationMap.put(TRANSFER_SUBMIT, "提交");
        operationMap.put(DELEGATION_SUBMIT, "提交");
        operationMap.put(COMPLETE, "完成");
        operationMap.put(ROLLBACK, "退回");
        operationMap.put(DIRECT_ROLLBACK, "直接退回");
        operationMap.put(ROLLBACK_TO_MAIN_FLOW, "退回主流程");
        operationMap.put(CANCEL, "撤回");
        operationMap.put(TRANSFER, "转办");
        operationMap.put(COUNTER_SIGN, "会签");
        operationMap.put(ADD_SIGN, "加签");
        operationMap.put(ATTENTION, "关注");
        operationMap.put(COPY_TO, "抄送");
        operationMap.put(REMIND, "催办");
        operationMap.put(MARK_READ, "标记已阅");
        operationMap.put(MARK_UNREAD, "标记未阅");
        operationMap.put(UNFOLLOW, "取消关注");
        operationMap.put(HAND_OVER, "移交办理人");
        operationMap.put(GOTO_TASK, "跳转环节");
        operationMap.put(SIGN_OPINION, "签署意见");
        operationMap.put(SUSPEND, "挂起");
        operationMap.put(RESUME, "恢复");
        operationMap.put(SUSPEND_TIMER, "计时器挂起");
        operationMap.put(RESUME_TIMER, "计时器恢复");
        operationMap.put(REJECT, "拒绝");
        operationMap.put(DELEGATION, "委托");
        operationMap.put(SKIP_TASK, "自动进入下一环节");
        operationMap.put(DELETE, "删除");
        operationMap.put(RECOVER, "恢复");
        operationMap.put(TAKE_BACK_TODO_DELEGATION, "委托回收");
        operationMap.put(ADD_SUB_FLOW, "添加承办部门(人)");
        operationMap.put(REDO, "重办");
        operationMap.put(DISTRIBUTE_INFO, "信息分发");
        operationMap.put(CHANGE_LIMIT_TIME, "协办时限");
        operationMap.put(STOP, "终止");
        operationMap.put(PRINT, "套打");
        operationMap.put(SAVE, "保存");
        operationMap.put(SAVE_DRAFT, "保存草稿");
    }

    /**
     * 根据操作类型获取其中文名
     *
     * @param operation
     * @return
     */
    public static String getName(String operation) {
        return operationMap.get(operation);
    }

    /**
     * 判断是否为提交的操作代码
     *
     * @param code
     * @return
     */
    public static boolean isActionCodeOfSubmit(Integer code) {
        return ActionCode.SUBMIT.getCode().equals(code) || ActionCode.COUNTER_SIGN_SUBMIT.getCode().equals(code)
                || ActionCode.ADD_SIGN_SUBMIT.getCode().equals(code)
                || ActionCode.TRANSFER_SUBMIT.getCode().equals(code)
                || ActionCode.DELEGATION_SUBMIT.getCode().equals(code);
    }

    /**
     * @return
     */
    public static List<Integer> getActionCodeOfSubmit() {
        return Lists.newArrayList(ActionCode.SUBMIT.getCode(), ActionCode.COUNTER_SIGN_SUBMIT.getCode(),
                ActionCode.ADD_SIGN_SUBMIT.getCode(), ActionCode.TRANSFER_SUBMIT.getCode(),
                ActionCode.DELEGATION_SUBMIT.getCode());
    }

    /**
     * *
     * 判断是否为退回的操作代码
     *
     * @param code
     * @return
     */
    public static boolean isActionCodeOfRollback(Integer code) {
        return ActionCode.ROLLBACK.getCode().equals(code) || ActionCode.DIRECT_ROLLBACK.getCode().equals(code);
    }

    /**
     * @return
     */
    public static List<Integer> getActionCodeOfRollback() {
        return Lists.newArrayList(ActionCode.ROLLBACK.getCode(), ActionCode.DIRECT_ROLLBACK.getCode());
    }

    /**
     * 判断是否为提交的操作类型
     *
     * @param actionType
     * @return
     */
    public static boolean isActionTypeOfSubmit(String actionType) {
        return WorkFlowOperation.SUBMIT.equals(actionType) || WorkFlowOperation.COUNTER_SIGN_SUBMIT.equals(actionType)
                || WorkFlowOperation.TRANSFER_SUBMIT.equals(actionType)
                || WorkFlowOperation.DELEGATION_SUBMIT.equals(actionType);
    }

    /**
     * 判断是否为退回的操作类型
     *
     * @param actionType
     * @return
     */
    public static boolean isActionTypeOfRollback(String actionType) {
        return WorkFlowOperation.ROLLBACK.equals(actionType) || WorkFlowOperation.DIRECT_ROLLBACK.equals(actionType)
                || WorkFlowOperation.ROLLBACK_TO_MAIN_FLOW.equals(actionType);
    }

}

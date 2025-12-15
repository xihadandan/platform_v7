/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.executor.transfer.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-2.1	zhulh		2014-10-2		Create
 * </pre>
 * @date 2014-10-2
 */
public class TaskExecutorFactory {

    /**
     * 如何描述该方法
     *
     * @param operation
     * @return
     */
    public static TaskExecutor getTaskActionExecutor(ActionCode actionCode) {
        TaskExecutor taskExecutor = null;
        Integer code = actionCode.getCode();
        switch (code) {
            case 1:
                // 提交 SUBMIT(1)
                taskExecutor = ApplicationContextHolder.getBean(TodoSubmitTaskActionExecutor.class);
                break;
            case 2:
                // 会签提交 COUNTER_SIGN_SUBMIT(2)
                taskExecutor = ApplicationContextHolder.getBean(TodoCounterSignSubmitTaskActionExecutor.class);
                break;
            case 34:
                // 加签提交 ADD_SIGN_SUBMIT(34)
                taskExecutor = ApplicationContextHolder.getBean(TodoAddSignSubmitTaskActionExecutor.class);
                break;
            case 3:
                // 转办提交 TRANSFER_SUBMIT(3)
                taskExecutor = ApplicationContextHolder.getBean(TodoTransferSubmitTaskActionExecutor.class);
                break;
            case 4:
                // 退回 ROLLBACK(4)
                taskExecutor = ApplicationContextHolder.getBean(RollbackTaskActionExecutor.class);
                break;
            case 5:
                // 直接退回 DIRECT_ROLLBACK(5)
                taskExecutor = ApplicationContextHolder.getBean(RollbackTaskActionExecutor.class);
                break;
            case 6:
                // 撤回 CANCEL(6)
                taskExecutor = ApplicationContextHolder.getBean(CancelTaskActionExecutor.class);
                break;
            case 7:
                // 转办 TRANSFER(7)
                taskExecutor = ApplicationContextHolder.getBean(TransferTaskActionExecutor.class);
                break;
            case 8:
                // 会签 COUNTER_SIGN(8)
                taskExecutor = ApplicationContextHolder.getBean(CounterSignTaskActionExecutor.class);
                break;
            case 33:
                // 加签 COUNTER_SIGN(33)
                taskExecutor = ApplicationContextHolder.getBean(AddSignTaskActionExecutor.class);
                break;
            case 9:
                // 关注 ATTENTION(9)
                taskExecutor = ApplicationContextHolder.getBean(AttentionTaskActionExecutor.class);
                break;
            case 10:
                // 取消关注 UNFOLLOW(10)
                taskExecutor = ApplicationContextHolder.getBean(UnfollowTaskActionExecutor.class);
                break;
            case 11:
                // 抄送 COPY_TO(11)
                taskExecutor = ApplicationContextHolder.getBean(CopyToTaskActionExecutor.class);
                break;
            case 12:
                // 催办 REMIND(12)
                taskExecutor = ApplicationContextHolder.getBean(RemindTaskActionExecutor.class);
                break;
            case 13:
                // 标记已阅 MARK_READ(13)
                taskExecutor = ApplicationContextHolder.getBean(MarkReadTaskActionExecutor.class);
                break;
            case 14:
                // 标记未阅MARK_UNREAD(14)
                taskExecutor = ApplicationContextHolder.getBean(MarkUnreadTaskActionExecutor.class);
                break;
            case 15:
                // 移交人员 HAND_OVER(15)
                taskExecutor = ApplicationContextHolder.getBean(HandOverTaskActionExecutor.class);
                break;
            case 16:
                // 移交环节 GOTO_TASK(16)
                taskExecutor = ApplicationContextHolder.getBean(GotoTaskTaskActionExecutor.class);
                break;
            case 17:
                // 签署意见 SIGN_OPINION(17)
                taskExecutor = ApplicationContextHolder.getBean(SignOpinionTaskActionExecutor.class);
                break;
            case 18:
                // 挂起 SUSPEND(18)
                taskExecutor = ApplicationContextHolder.getBean(SuspendTaskActionExecutor.class);
                break;
            case 19:
                // 恢复 RESUME(19)
                taskExecutor = ApplicationContextHolder.getBean(ResumeTaskActionExecutor.class);
                break;
            case 20:
                // 计时器挂起 SUSPEND_TIMER(20)
                taskExecutor = ApplicationContextHolder.getBean(SuspendTimerTaskActionExecutor.class);
                break;
            case 21:
                // 计时器恢复 RESUME_TIMER(21)
                taskExecutor = ApplicationContextHolder.getBean(ResumeTimerTaskActionExecutor.class);
                break;
            case 22:
                // 拒绝，直接结束 REJECT(22)
                taskExecutor = ApplicationContextHolder.getBean(RejectTaskActionExecutor.class);
                break;
            case 23:
                // 委托 DELEGATION(23)
                taskExecutor = ApplicationContextHolder.getBean(DelegationTaskActionExecutor.class);
                break;
            case 24:
                // 办理人为空自动进入下一环节 SKIP_TASK(24)
                // TODO
                break;
            case 26:
                // 直接回收委托任务在待办状态中
                taskExecutor = ApplicationContextHolder.getBean(TakeBackTodoDelegationTaskActionExecutor.class);
                break;
            case 27:
                // 委托提交
                taskExecutor = ApplicationContextHolder.getBean(TodoDelegationSubmitTaskActionExecutor.class);
                break;
            case 37:
                // 补审补办提交
                taskExecutor = ApplicationContextHolder.getBean(TodoSupplementSubmitTaskActionExecutor.class);
                break;
            default:
                break;
        }

        return taskExecutor;
    }

    public static TaskExecutor getTaskTransferExecutor(Integer transferCode) {
        TaskExecutor taskExecutor = null;
        switch (transferCode) {
            case 1:
                // 提交流转 Submit(1)
                taskExecutor = ApplicationContextHolder.getBean(SubmitTaskTransferExecutor.class);
                break;
            case 2:
                // 退回流转
                taskExecutor = ApplicationContextHolder.getBean(RollBackTaskTransferExecutor.class);
                break;
            case 3:
                // 撤回流转
                taskExecutor = ApplicationContextHolder.getBean(CancelTaskTransferExecutor.class);
                break;
            case 4:
                // 移交环节流转
                taskExecutor = ApplicationContextHolder.getBean(GotoTaskTaskTransferExecutor.class);
                break;
            case 5:
                // 办理人为空自动跳过
                taskExecutor = ApplicationContextHolder.getBean(SkipTaskTaskTransferExecutor.class);
                break;
            case 6:
                // 转办提交流转
                taskExecutor = ApplicationContextHolder.getBean(TransferSubmitTaskTransferExecutor.class);
                break;
            default:
                break;
        }
        return taskExecutor;
    }
}

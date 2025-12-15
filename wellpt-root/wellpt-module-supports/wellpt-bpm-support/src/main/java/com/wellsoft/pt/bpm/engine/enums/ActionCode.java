/*
 * @(#)2014-10-28 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

/**
 * Description: 任务操作代码
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-28.1	zhulh		2014-10-28		Create
 * </pre>
 * @date 2014-10-28
 */
public enum ActionCode {
    // 提交
    SUBMIT(1),
    // 会签提交
    COUNTER_SIGN_SUBMIT(2),
    // 加签提交
    ADD_SIGN_SUBMIT(34),
    // 转办提交
    TRANSFER_SUBMIT(3),
    // 委托提交
    DELEGATION_SUBMIT(27),
    // 自动提交
    AUTO_SUBMIT(35),
    // 自动跳过
    SKIP_SUBMIT(36),
    // 补审补办
    SUPPLEMENT(37),
    // 退回
    ROLLBACK(4),
    // 直接退回
    DIRECT_ROLLBACK(5),
    // 撤回
    CANCEL(6),
    // 转办
    TRANSFER(7),
    // 会签
    COUNTER_SIGN(8),
    // 加签
    ADD_SIGN(33),
    // 关注
    ATTENTION(9),
    // 取消关注
    UNFOLLOW(10),
    // 抄送
    COPY_TO(11),
    // 催办
    REMIND(12),
    // 标记已阅
    MARK_READ(13),
    // 标记未阅
    MARK_UNREAD(14),
    // 移交人员
    HAND_OVER(15),
    // 移交环节
    GOTO_TASK(16),
    // 签署意见
    SIGN_OPINION(17),
    // 挂起
    SUSPEND(18),
    // 恢复
    RESUME(19),
    // 计时器挂起
    SUSPEND_TIMER(20),
    // 计时器恢复
    RESUME_TIMER(21),
    // 拒绝，直接结束
    REJECT(22),
    // 委托
    DELEGATION(23),
    // 办理人为空自动进入下一环节
    SKIP_TASK(24),
    // 删除
    DELETE(25),
    // 直接回收委托任务在待办状态中，
    TAKE_BACK_TODO_DELEGATION(26),
    // 重办
    REDO(28),
    // 信息分发
    DISTRIBUTE_INFO(29),
    // 协办时限
    CHANGE_LIMIT_TIME(30),
    // 终止
    STOP(31),
    // 添加承办部门
    ADD_SUB_FLOW(32);

    private Integer code;

    private ActionCode(Integer code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

}

/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/25/22.1	zhulh		10/25/22		Create
 * </pre>
 * @date 10/25/22
 */
public interface BizProcessItemListener extends Listener {

    /**
     * 业务事项创建
     *
     * @param event
     */
    void onCreated(ProcessItemEvent event);

    /**
     * 业务事项启动
     *
     * @param event
     */
    void onStarted(ProcessItemEvent event);

    /**
     * 业务事项挂起
     *
     * @param event
     */
    void onSuspended(ProcessItemEvent event);

    /**
     * 业务事项恢复
     *
     * @param event
     */
    void onResume(ProcessItemEvent event);

    /**
     * 业务事项撤回
     *
     * @param event
     */
    void onCancelled(ProcessItemEvent event);

    /**
     * 业务事项暂停
     *
     * @param event
     */
    void onTimerStarted(ProcessItemEvent event);

    /**
     * 业务事项暂停
     *
     * @param event
     */
    void onTimerPaused(ProcessItemEvent event);

    /**
     * 业务事项恢复
     *
     * @param event
     */
    void onTimerResumed(ProcessItemEvent event);

    /**
     * 业务事项计时到期
     *
     * @param event
     */
    void onTimerDue(ProcessItemEvent event);

    /**
     * 业务事项计时逾期
     *
     * @param event
     */
    void onTimerOverDue(ProcessItemEvent event);

    /**
     * 业务事项完成
     *
     * @param event
     */
    void onCompleted(ProcessItemEvent event);

    /**
     * 业务重新开始
     *
     * @param event
     */
    void onRestarted(ProcessItemEvent event);

    /**
     * 自定义业务事项事件发生
     *
     * @param event
     */
    void onCustomEvent(ProcessItemEvent event);
}

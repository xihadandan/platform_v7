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
public abstract class AbstractBizProcessItemListener implements BizProcessItemListener {

    /**
     * 监听器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "业务事项监听器";
    }

    /**
     * 业务事项创建
     *
     * @param event
     */
    @Override
    public void onCreated(ProcessItemEvent event) {

    }

    /**
     * 业务事项启动
     *
     * @param event
     */
    @Override
    public void onStarted(ProcessItemEvent event) {

    }

    /**
     * 业务事项挂起
     *
     * @param event
     */
    @Override
    public void onSuspended(ProcessItemEvent event) {

    }

    /**
     * 业务事项恢复
     *
     * @param event
     */
    @Override
    public void onResume(ProcessItemEvent event) {

    }

    /**
     * 业务事项撤回
     *
     * @param event
     */
    @Override
    public void onCancelled(ProcessItemEvent event) {

    }

    /**
     * 业务事项暂停
     *
     * @param event
     */
    @Override
    public void onTimerStarted(ProcessItemEvent event) {

    }

    /**
     * 业务事项暂停
     *
     * @param event
     */
    @Override
    public void onTimerPaused(ProcessItemEvent event) {

    }

    /**
     * 业务事项恢复
     *
     * @param event
     */
    @Override
    public void onTimerResumed(ProcessItemEvent event) {

    }

    /**
     * 业务事项计时到期
     *
     * @param event
     */
    @Override
    public void onTimerDue(ProcessItemEvent event) {

    }

    /**
     * 业务事项计时逾期
     *
     * @param event
     */
    @Override
    public void onTimerOverDue(ProcessItemEvent event) {

    }

    /**
     * 业务事项完成
     *
     * @param event
     */
    @Override
    public void onCompleted(ProcessItemEvent event) {

    }

    /**
     * 业务重新开始
     *
     * @param itemEvent
     */
    @Override
    public void onRestarted(ProcessItemEvent itemEvent) {

    }

    /**
     * 自定义业务事项事件发生
     *
     * @param event
     */
    @Override
    public void onCustomEvent(ProcessItemEvent event) {

    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }

}

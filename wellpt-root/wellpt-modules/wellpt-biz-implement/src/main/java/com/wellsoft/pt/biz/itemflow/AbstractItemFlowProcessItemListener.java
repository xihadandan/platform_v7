/*
 * @(#)12/12/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.itemflow;

import com.wellsoft.pt.biz.listener.AbstractBizProcessItemListener;
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
 * 12/12/23.1	zhulh		12/12/23		Create
 * </pre>
 * @date 12/12/23
 */
public abstract class AbstractItemFlowProcessItemListener extends AbstractBizProcessItemListener implements ItemFlowProcessItemListener {

    /**
     * 业务事项创建
     *
     * @param event
     */
    @Override
    public void onCreated(ProcessItemEvent event) {
        // 单据反馈
        returnIfRequired(event);
        // 事项流流转
        continueItemFlowIfRequired(event);
    }

    /**
     * 业务事项启动
     *
     * @param event
     */
    @Override
    public void onStarted(ProcessItemEvent event) {
        // 单据反馈
        returnIfRequired(event);
        // 事项流流转
        continueItemFlowIfRequired(event);
    }

    /**
     * 业务事项挂起
     *
     * @param event
     */
    @Override
    public void onSuspended(ProcessItemEvent event) {
        // 单据反馈
        returnIfRequired(event);
        // 事项流流转
        continueItemFlowIfRequired(event);
    }

    /**
     * 业务事项恢复
     *
     * @param event
     */
    @Override
    public void onResume(ProcessItemEvent event) {
        // 单据反馈
        returnIfRequired(event);
        // 事项流流转
        continueItemFlowIfRequired(event);
    }

    /**
     * 业务事项撤回
     *
     * @param event
     */
    @Override
    public void onCancelled(ProcessItemEvent event) {
        // 单据反馈
        returnIfRequired(event);
        // 事项流流转
        continueItemFlowIfRequired(event);
    }

    /**
     * 业务事项完成
     *
     * @param event
     */
    @Override
    public void onCompleted(ProcessItemEvent event) {
        // 单据反馈
        returnIfRequired(event);
        // 事项流流转
        continueItemFlowIfRequired(event);
    }

    /**
     * 自定义业务事项事件发生
     *
     * @param event
     */
    @Override
    public void onCustomEvent(ProcessItemEvent event) {
        // 单据反馈
        returnIfRequired(event);
        // 事项流流转
        continueItemFlowIfRequired(event);
    }

    /**
     * 单据反馈
     *
     * @param event
     */
    protected abstract void returnIfRequired(ProcessItemEvent event);

    /**
     * 事项流流转
     *
     * @param event
     */
    protected abstract void continueItemFlowIfRequired(ProcessItemEvent event);

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
        return Integer.MAX_VALUE;
    }

}

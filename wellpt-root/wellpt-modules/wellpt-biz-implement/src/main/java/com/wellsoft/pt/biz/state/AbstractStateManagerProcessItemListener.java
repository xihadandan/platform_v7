/*
 * @(#)12/6/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state;

import com.wellsoft.pt.biz.listener.RuntimeListener;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description: 状态管理——业务事项事件发生监听抽象类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/6/23.1	zhulh		12/6/23		Create
 * </pre>
 * @date 12/6/23
 */
public abstract class AbstractStateManagerProcessItemListener implements StateManagerProcessItemListener, RuntimeListener {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected DyFormFacade dyFormFacade;

    /**
     * 业务事项创建
     *
     * @param event
     */
    @Override
    public void onCreated(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项启动
     *
     * @param event
     */
    @Override
    public void onStarted(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项挂起
     *
     * @param event
     */
    @Override
    public void onSuspended(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项恢复
     *
     * @param event
     */
    @Override
    public void onResume(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项撤回
     *
     * @param event
     */
    @Override
    public void onCancelled(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项暂停
     *
     * @param event
     */
    @Override
    public void onTimerStarted(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项暂停
     *
     * @param event
     */
    @Override
    public void onTimerPaused(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项恢复
     *
     * @param event
     */
    @Override
    public void onTimerResumed(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项计时到期
     *
     * @param event
     */
    @Override
    public void onTimerDue(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项计时逾期
     *
     * @param event
     */
    @Override
    public void onTimerOverDue(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务事项完成
     *
     * @param event
     */
    @Override
    public void onCompleted(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 业务重新开始
     *
     * @param event
     */
    @Override
    public void onRestarted(ProcessItemEvent event) {
        changeBusinessStateIfRequired(event);
    }

    /**
     * 自定义业务事项事件发生
     *
     * @param itemEvent
     */
    @Override
    public void onCustomEvent(ProcessItemEvent itemEvent) {
        changeBusinessStateIfRequired(itemEvent);
    }

    protected void changeBusinessStateIfRequired(ProcessItemEvent event) {
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(event.getProcessDefUuid());
        this.changeBusinessStateIfRequired(event, parser);
    }

    /**
     * 变更业务状态
     *
     * @param event
     * @param parser
     */
    protected abstract void changeBusinessStateIfRequired(ProcessItemEvent event, ProcessDefinitionJsonParser parser);

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

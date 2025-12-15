/*
 * @(#)2013-10-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;

/**
 * Description: 流程监听器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-30.1	zhulh		2013-10-30		Create
 * </pre>
 * @date 2013-10-30
 */
public interface FlowListener extends Listener {

    /**
     * 流程实例创建事件处理，保存为草稿就创建FlowInstance
     *
     * @param event
     * @throws WorkFlowException
     */
    void onCreated(Event event) throws WorkFlowException;

    /**
     * 流程启动事件处理，起草环节提交
     *
     * @param event
     * @throws WorkFlowException
     */
    void onStarted(Event event) throws WorkFlowException;

    /**
     * 流程结束事件处理
     *
     * @param event
     * @throws WorkFlowException
     */
    void onEnd(Event event) throws WorkFlowException;

    /**
     * 流程实例删除事件处理
     *
     * @param event
     * @throws WorkFlowException
     */
    void onDeleted(Event event) throws WorkFlowException;
}

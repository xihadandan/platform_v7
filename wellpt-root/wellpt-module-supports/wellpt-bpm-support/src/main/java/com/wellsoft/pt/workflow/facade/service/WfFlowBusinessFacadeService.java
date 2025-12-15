/*
 * @(#)11/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.workflow.enums.EnumFlowBizStateTriggerType;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/21/22.1	zhulh		11/21/22		Create
 * </pre>
 * @date 11/21/22
 */
public interface WfFlowBusinessFacadeService extends Facade {

    /**
     * 流程创建
     *
     * @param event
     */
    void onFlowStarted(Event event);

    /**
     * 流程办结
     *
     * @param event
     */
    void onFlowEnd(Event event);

    /**
     * 环节创建
     *
     * @param event
     */
    void onTaskCreated(Event event);

    /**
     * 环节完成
     *
     * @param event
     */
    void onTaskCompleted(Event event);

    /**
     * 环节操作
     *
     * @param event
     */
    void onTaskOperation(Event event);

    /**
     * 环节归属
     *
     * @param event
     */
    void onTaskBelongTo(Event event);

    /**
     * 流向流转
     *
     * @param event
     */
    void onDirectionTransition(Event event);

    /**
     * 状态管理
     *
     * @param event
     * @param triggerTypes
     * @param flowBusinessDefinitionJson
     */
    void stateManager(Event event, List<EnumFlowBizStateTriggerType> triggerTypes, FlowBusinessDefinitionJson flowBusinessDefinitionJson);
}

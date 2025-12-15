/*
 * @(#)11/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.listener;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;

/**
 * Description: 流程业务监听器
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
public interface FlowBusinessListener extends Listener {

    /**
     * 业务状态变更
     *
     * @param oldStateName
     * @param newStateName
     * @param oldStateCode
     * @param newStateCode
     * @param stateConfig
     * @param flowBusinessDefinitionJson
     * @param event
     */
    void onBusinessStateChanged(String oldStateName, String newStateName, String oldStateCode, String newStateCode, FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig,
                                FlowBusinessDefinitionJson flowBusinessDefinitionJson, Event event);

}

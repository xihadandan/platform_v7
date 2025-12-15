/*
 * @(#)8/12/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.support.event;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;
import org.springframework.context.ApplicationEvent;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/12/24.1	    zhulh		8/12/24		    Create
 * </pre>
 * @date 8/12/24
 */
public class FlowBusinessStateChangedEvent extends ApplicationEvent {

    private String oldStateName;
    private String newStateName;
    private String oldStateCode;
    private String newStateCode;
    private DyFormData dyFormData;
    private FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig;
    private com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson flowBusinessDefinitionJson;
    private Event flowEvent;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public FlowBusinessStateChangedEvent(String oldStateName, String newStateName, String oldStateCode, String newStateCode,
                                         DyFormData dyFormData, FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig,
                                         FlowBusinessDefinitionJson flowBusinessDefinitionJson, Event flowEvent) {
        super(flowEvent);
        this.oldStateName = oldStateName;
        this.newStateName = newStateName;
        this.oldStateCode = oldStateCode;
        this.newStateCode = newStateCode;
        this.dyFormData = dyFormData;
        this.stateConfig = stateConfig;
        this.flowBusinessDefinitionJson = flowBusinessDefinitionJson;
        this.flowEvent = flowEvent;
    }

    /**
     * @return the oldStateName
     */
    public String getOldStateName() {
        return oldStateName;
    }

    /**
     * @return the newStateName
     */
    public String getNewStateName() {
        return newStateName;
    }

    /**
     * @return the oldStateCode
     */
    public String getOldStateCode() {
        return oldStateCode;
    }

    /**
     * @return the newStateCode
     */
    public String getNewStateCode() {
        return newStateCode;
    }

    /**
     * @return the dyFormData
     */
    public DyFormData getDyFormData() {
        return dyFormData;
    }

    /**
     * @return the stateConfig
     */
    public FlowBusinessDefinitionJson.FlowBusinessStateConfig getStateConfig() {
        return stateConfig;
    }

    /**
     * @return the flowBusinessDefinitionJson
     */
    public FlowBusinessDefinitionJson getFlowBusinessDefinitionJson() {
        return flowBusinessDefinitionJson;
    }

    /**
     * @return the flowEvent
     */
    public Event getFlowEvent() {
        return flowEvent;
    }
}

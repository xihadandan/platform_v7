/*
 * @(#)8/12/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.listener.impl;

import com.wellsoft.pt.biz.facade.service.BizWorkflowIntegrationService;
import com.wellsoft.pt.workflow.support.event.FlowBusinessStateChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Description: 流程业务状态变更监听
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
@Component
public class FlowBusinessStateChangedEventListenerImpl implements ApplicationListener<FlowBusinessStateChangedEvent> {

    @Autowired
    private BizWorkflowIntegrationService workflowIntegrationService;

    @Override
    public void onApplicationEvent(FlowBusinessStateChangedEvent event) {
        workflowIntegrationService.onFlowBusinessStateChanged(event);
    }

}

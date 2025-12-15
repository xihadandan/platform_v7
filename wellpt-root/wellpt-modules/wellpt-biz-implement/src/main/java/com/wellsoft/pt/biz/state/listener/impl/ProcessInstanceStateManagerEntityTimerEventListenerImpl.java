/*
 * @(#)8/8/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.listener.impl;

import com.wellsoft.pt.biz.enums.EnumStateTriggerType;
import com.wellsoft.pt.biz.listener.event.ProcessEntityTimerEvent;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.state.AbstractStateManagerProcessEntityTimerListener;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.StateDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/8/24.1	    zhulh		8/8/24		    Create
 * </pre>
 * @date 8/8/24
 */
@Component
public class ProcessInstanceStateManagerEntityTimerEventListenerImpl extends AbstractStateManagerProcessEntityTimerListener {

    @Autowired
    private BizProcessInstanceService processInstanceService;

    @Override
    public String getName() {
        return "业务办件状态管理业务主体状态计时监听器实现";
    }

    @Override
    protected void changeBusinessStateIfRequired(ProcessEntityTimerEvent event, ProcessDefinitionJsonParser parser) {
        // 业务办件状态管理——业务主体状态计时事件发生
        ProcessDefinitionJson.ProcessFormConfig processFormConfig = parser.getProcessFormConfig();
        if (processFormConfig == null) {
            return;
        }
        List<StateDefinition.StateConfig> stateConfigs = StateManagerHelper.
                getStateConfigs(processFormConfig.getStates(),
                        EnumStateTriggerType.PROCESS_ENTITY_TIMER_EVENT_PUBLISHED, config ->
                                StringUtils.equals(event.getEventType(), config.getTimerEventId())
                );

        // 变更业务办件状态
        if (CollectionUtils.isNotEmpty(stateConfigs)) {
            stateConfigs.forEach(config -> processInstanceService.changeProcessInstanceState(event, config, processFormConfig, parser));
        }
    }
}

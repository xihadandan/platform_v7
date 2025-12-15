/*
 * @(#)8/8/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.listener.impl;

import com.wellsoft.pt.biz.enums.EnumStateTriggerType;
import com.wellsoft.pt.biz.listener.event.ProcessEntityTimerEvent;
import com.wellsoft.pt.biz.service.BizProcessNodeInstanceService;
import com.wellsoft.pt.biz.state.AbstractStateManagerProcessEntityTimerListener;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.biz.support.StateDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
public class ProcessNodeInstanceStateManagerEntityTimerEventListenerImpl extends AbstractStateManagerProcessEntityTimerListener {

    @Autowired
    private BizProcessNodeInstanceService processNodeInstanceService;

    @Override
    public String getName() {
        return "过程节点办件状态管理业务主体状态计时监听器实现";
    }

    @Override
    protected void changeBusinessStateIfRequired(ProcessEntityTimerEvent event, ProcessDefinitionJsonParser parser) {
        // 过程节点办件状态管理——业务主体状态计时事件发生
        Map<String, List<StateDefinition>> nodeStateMap = parser.getNodeStateMap();
        for (Map.Entry<String, List<StateDefinition>> entry : nodeStateMap.entrySet()) {
            List<StateDefinition.StateConfig> stateConfigs = StateManagerHelper.getStateConfigs(entry.getValue(),
                    EnumStateTriggerType.PROCESS_ENTITY_TIMER_EVENT_PUBLISHED, config ->
                            StringUtils.equals(event.getEventType(), config.getItemEventId())
            );

            if (CollectionUtils.isEmpty(stateConfigs)) {
                continue;
            }

            ProcessNodeConfig processNodeConfig = parser.getProcessNodeConfigById(entry.getKey());
            // 变更过程节点办件状态
            stateConfigs.forEach(config -> processNodeInstanceService.changeNodeInstanceState(event, config, processNodeConfig.getFormConfig(), processNodeConfig, parser));
        }
    }

}

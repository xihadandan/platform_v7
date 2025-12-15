/*
 * @(#)12/8/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.listener.impl;

import com.wellsoft.pt.biz.enums.EnumStateTriggerType;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.service.BizProcessNodeInstanceService;
import com.wellsoft.pt.biz.state.AbstractStateManagerStateListener;
import com.wellsoft.pt.biz.state.support.StateConfigMatcher;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/8/23.1	zhulh		12/8/23		Create
 * </pre>
 * @date 12/8/23
 */
@Component
public class ProcessNodeInstanceStateManagerStateListenerImpl extends AbstractStateManagerStateListener {

    @Autowired
    private BizProcessNodeInstanceService processNodeInstanceService;

    /**
     * @param event
     * @param stateTriggerType
     * @param configMatcher
     */
    @Override
    protected void doStateChanged(Event event, EnumStateTriggerType stateTriggerType, StateConfigMatcher configMatcher) {
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(event.getProcessDefUuid());
        // 过程节点办件状态管理——业务事项、过程节点、业务流程状态变更
        Map<String, List<StateDefinition>> nodeStateMap = parser.getNodeStateMap();
        for (Map.Entry<String, List<StateDefinition>> entry : nodeStateMap.entrySet()) {
            List<StateDefinition.StateConfig> stateConfigs = StateManagerHelper.getStateConfigs(entry.getValue(),
                    stateTriggerType, configMatcher);

            if (CollectionUtils.isEmpty(stateConfigs)) {
                continue;
            }

            ProcessNodeConfig processNodeConfig = parser.getProcessNodeConfigById(entry.getKey());
            // 变更过程节点办件状态
            stateConfigs.forEach(config -> processNodeInstanceService.changeNodeInstanceState(event, config,
                    processNodeConfig.getFormConfig(), processNodeConfig, parser));
        }
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
        return -20;
    }
}

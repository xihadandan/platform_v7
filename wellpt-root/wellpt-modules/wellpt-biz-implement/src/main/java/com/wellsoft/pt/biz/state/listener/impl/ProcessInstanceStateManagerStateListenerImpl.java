/*
 * @(#)12/8/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.listener.impl;

import com.wellsoft.pt.biz.enums.EnumStateTriggerType;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.state.AbstractStateManagerStateListener;
import com.wellsoft.pt.biz.state.support.StateConfigMatcher;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * 12/8/23.1	zhulh		12/8/23		Create
 * </pre>
 * @date 12/8/23
 */
@Component
public class ProcessInstanceStateManagerStateListenerImpl extends AbstractStateManagerStateListener {

    @Autowired
    private BizProcessInstanceService processInstanceService;

    /**
     * @param event
     * @param stateTriggerType
     * @param configMatcher
     */
    @Override
    protected void doStateChanged(Event event, EnumStateTriggerType stateTriggerType, StateConfigMatcher configMatcher) {
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(event.getProcessDefUuid());
        // 业务办件状态管理——业务事项、过程节点、业务流程状态变更
        ProcessDefinitionJson.ProcessFormConfig processFormConfig = parser.getProcessFormConfig();
        if (processFormConfig == null) {
            return;
        }
        List<StateDefinition.StateConfig> stateConfigs = StateManagerHelper.getStateConfigs(processFormConfig.getStates(), stateTriggerType, configMatcher);

        // 变更业务办件状态
        if (CollectionUtils.isNotEmpty(stateConfigs)) {
            stateConfigs.forEach(config -> processInstanceService.changeProcessInstanceState(event, config, processFormConfig, parser));
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
        return -10;
    }
}

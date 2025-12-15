/*
 * @(#)12/6/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.listener.impl;

import com.wellsoft.pt.biz.enums.EnumStateTriggerType;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.state.AbstractStateManagerProcessItemListener;
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
 * Description: 业务主体状态管理——业务事项事件发生监听
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
@Component
public class ProcessEntityStateManagerItemEventListenerImpl extends AbstractStateManagerProcessItemListener {

    @Autowired
    private BizProcessInstanceService processInstanceService;

    /**
     * 监听器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "业务主体状态管理事项监听器实现";
    }

    /**
     * 变更业务状态
     *
     * @param event
     * @param parser
     */
    protected void changeBusinessStateIfRequired(ProcessItemEvent event, ProcessDefinitionJsonParser parser) {
        // 业务主体状态管理——业务事项事件发生
        ProcessDefinitionJson.ProcessEntityConfig entityConfig = parser.getProcessEntityConfig();
        if (entityConfig == null) {
            return;
        }
        List<StateDefinition.StateConfig> stateConfigs = StateManagerHelper.getStateConfigs(entityConfig.getStates(), EnumStateTriggerType.ITEM_EVENT_PUBLISHED, config ->
                StringUtils.equals(event.getItemCode(), config.getItemCode()) &&
                        StringUtils.equals(event.getEventType(), config.getItemEventId())
        );

        // 变更业务主体状态
        if (CollectionUtils.isNotEmpty(stateConfigs)) {
            stateConfigs.forEach(config -> processInstanceService.changeProcessEntityState(event, config, parser));
        }
    }

}

/*
 * @(#)12/8/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.listener.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumStateTriggerType;
import com.wellsoft.pt.biz.listener.BizEventListenerPublisher;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.state.AbstractStateManagerStateListener;
import com.wellsoft.pt.biz.state.event.BizItemStateChangedEvent;
import com.wellsoft.pt.biz.state.support.StateConfigMatcher;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.biz.utils.BusinessIntegrationContextHolder;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
public class ProcessItemInstanceStateManagerStateListenerImpl extends AbstractStateManagerStateListener {

    @Autowired
    private BizProcessItemInstanceService processItemInstanceService;

    @Autowired
    private BizEventListenerPublisher eventListenerPublisher;

    /**
     * @param event
     * @param stateTriggerType
     * @param configMatcher
     */
    @Override
    protected void doStateChanged(Event event, EnumStateTriggerType stateTriggerType, StateConfigMatcher configMatcher) {
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(event.getProcessDefUuid());
        // 业务事项办件状态管理——业务事项、过程节点、业务流程状态变更
        Map<String, List<StateDefinition>> itemStateMap = parser.getItemStateMap();
        for (Map.Entry<String, List<StateDefinition>> entry : itemStateMap.entrySet()) {
            List<StateDefinition.StateConfig> stateConfigs = StateManagerHelper.getStateConfigs(entry.getValue(), stateTriggerType, configMatcher);

            if (CollectionUtils.isEmpty(stateConfigs)) {
                continue;
            }

            ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(entry.getKey());
            // 变更业务事项办件状态
            stateConfigs.forEach(config -> processItemInstanceService.changeItemInstanceState(event, config, processItemConfig.getFormConfig(), processItemConfig, parser));
        }

        // 事件定义->事件发布，自定义事件发布
        Map<String, List<ProcessItemConfig.DefineEventPublishConfig>> itemEventPublishMap = parser.getItemEventPublishMap();
        if (event instanceof BizItemStateChangedEvent) {
            String itemId = ((BizItemStateChangedEvent) event).getItemId();
            String itemState = ((BizItemStateChangedEvent) event).getNewState();
            List<ProcessItemConfig.DefineEventPublishConfig> eventPublishConfigs = itemEventPublishMap.get(itemId);
            if (CollectionUtils.isNotEmpty(eventPublishConfigs)) {
                ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
                eventPublishConfigs.forEach(config -> {
                    if (StringUtils.equals(itemState, config.getItemState()) && evaluateAdditionalCondition(event, config)) {
                        publishCustomEvent((BizItemStateChangedEvent) event, config, processItemConfig);
                    }
                });
            }
        }
    }

    /**
     * @param event
     * @param eventPublishConfig
     * @return
     */
    private boolean evaluateAdditionalCondition(Event event, ProcessItemConfig.DefineEventPublishConfig eventPublishConfig) {
        if (eventPublishConfig.getAdditionalCondition()) {
            return StateManagerHelper.evaluateConditionExpression(event, eventPublishConfig.getExpressionScriptType(), eventPublishConfig.getConditionExpression());
        }
        return true;
    }

    /**
     * @param event
     * @param eventPublishConfig
     */
    private void publishCustomEvent(BizItemStateChangedEvent event, ProcessItemConfig.DefineEventPublishConfig eventPublishConfig,
                                    ProcessItemConfig processItemConfig) {
        BizProcessItemInstanceEntity processItemInstanceEntity = (BizProcessItemInstanceEntity) event.getSource();
        DyFormData eventDyformData = event.getDyFormData();
        if (eventDyformData == null) {
            eventDyformData = ApplicationContextHolder.getBean(DyFormFacade.class).getDyFormData(event.getFormUuid(), event.getDataUuid(), false);
        }
        Map<String, Object> extraData = Maps.newHashMap();
        if (BusinessIntegrationContextHolder.getWorkflowEvent() != null) {
            extraData.put("taskData", BusinessIntegrationContextHolder.getWorkflowEvent().getTaskData());
        }
        eventListenerPublisher.publishItemCustomEvent(processItemInstanceEntity, eventPublishConfig.getEventId(),
                eventPublishConfig.getResultField(), eventDyformData, processItemConfig, extraData);
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
        return -40;
    }

}

/*
 * @(#)11/17/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.biz.dto.BizProcessItemDataDto;
import com.wellsoft.pt.biz.entity.BizBusinessIntegrationEntity;
import com.wellsoft.pt.biz.entity.BizFormStateHistoryEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumBizBiEventTriggerType;
import com.wellsoft.pt.biz.enums.EnumBizBiType;
import com.wellsoft.pt.biz.facade.service.BizProcessItemFacadeService;
import com.wellsoft.pt.biz.facade.service.BizWorkflowIntegrationService;
import com.wellsoft.pt.biz.listener.BizEventListenerPublisher;
import com.wellsoft.pt.biz.service.BizBusinessIntegrationService;
import com.wellsoft.pt.biz.service.BizFormStateHistoryService;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.utils.BusinessIntegrationContextHolder;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowScriptHelper;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.workflow.enums.EnumFlowBizStateTriggerType;
import com.wellsoft.pt.workflow.facade.service.WfFlowBusinessFacadeService;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;
import com.wellsoft.pt.workflow.support.event.FlowBusinessStateChangedEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/17/22.1	zhulh		11/17/22		Create
 * </pre>
 * @date 11/17/22
 */
@Service
public class BizWorkflowIntegrationServiceImpl extends AbstractApiFacade implements BizWorkflowIntegrationService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BizBusinessIntegrationService businessIntegrationService;

    @Autowired
    private BizProcessItemInstanceService processItemInstanceService;

    @Autowired
    private BotFacadeService botFacadeService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private TsTimerFacadeService timerFacadeService;

    @Autowired
    private BizProcessItemFacadeService processItemFacadeService;

    @Autowired
    private WfFlowBusinessFacadeService flowBusinessFacadeService;

    @Autowired
    private BizEventListenerPublisher eventListenerPublisher;

    @Autowired
    private BizFormStateHistoryService formStateHistoryService;

    /**
     * 流程启动时处理
     *
     * @param event
     */
    @Override
    public void onFlowStarted(Event event) {
        BizProcessItemInstanceEntity itemInstanceEntity = getItemInstanceEntity(event.getFlowInstUuid(), event.getTaskData());
        if (itemInstanceEntity == null) {
            return;
        }

        // 流程状态管理
        workflowStateMamager(itemInstanceEntity, event, EnumFlowBizStateTriggerType.FlowStarted);

        // 事件发布
        publishEventIfRequired(itemInstanceEntity, event, EnumBizBiEventTriggerType.FlowStarted);
    }

    /**
     * 流程办结时处理
     *
     * @param event
     */
    @Override
    @Transactional
    public void onFlowEnd(Event event) {
        BizProcessItemInstanceEntity itemInstanceEntity = getItemInstanceEntity(event.getFlowInstUuid(), event.getTaskData());
        if (itemInstanceEntity == null) {
            return;
        }

        // 流程状态管理
        workflowStateMamager(itemInstanceEntity, event, EnumFlowBizStateTriggerType.FlowEnd);

        // 办结时反馈
        returnOnOverIfRequired(itemInstanceEntity, event);

        // 完成事项办件
        completeItemInstance(itemInstanceEntity, event);

        // 事件发布
        publishEventIfRequired(itemInstanceEntity, event, EnumBizBiEventTriggerType.FlowEnd);
    }

    /**
     * 环节创建时处理
     *
     * @param event
     */
    @Override
    public void onTaskCreated(Event event) {
        BizProcessItemInstanceEntity itemInstanceEntity = getItemInstanceEntity(event.getFlowInstUuid(), event.getTaskData());
        if (itemInstanceEntity == null) {
            return;
        }

        // 流程状态管理
        workflowStateMamager(itemInstanceEntity, event, EnumFlowBizStateTriggerType.TaskCreated, EnumFlowBizStateTriggerType.TaskBelongTo);

        // 事件发布
        publishEventIfRequired(itemInstanceEntity, event, EnumBizBiEventTriggerType.TaskCreated);
        publishEventIfRequired(itemInstanceEntity, event, EnumBizBiEventTriggerType.TaskBelongTo);
    }

    /**
     * 环节完成时处理
     *
     * @param event
     */
    @Override
    public void onTaskCompleted(Event event) {
        BizProcessItemInstanceEntity itemInstanceEntity = getItemInstanceEntity(event.getFlowInstUuid(), event.getTaskData());
        if (itemInstanceEntity == null) {
            return;
        }

        // 流程状态管理
        workflowStateMamager(itemInstanceEntity, event, EnumFlowBizStateTriggerType.TaskCompleted, EnumFlowBizStateTriggerType.TaskOperation);

        // 事件发布
        publishEventIfRequired(itemInstanceEntity, event, EnumBizBiEventTriggerType.TaskCompleted);
        publishEventIfRequired(itemInstanceEntity, event, EnumBizBiEventTriggerType.TaskOperation);
    }

    /**
     * 流向流转
     *
     * @param event
     */
    @Override
    public void onDirectionTransition(Event event) {
        BizProcessItemInstanceEntity itemInstanceEntity = getItemInstanceEntity(event.getFlowInstUuid(), event.getTaskData());
        if (itemInstanceEntity == null) {
            return;
        }

        // 流程状态管理
        workflowStateMamager(itemInstanceEntity, event, EnumFlowBizStateTriggerType.DirectionTransition);

        // 指定流向反馈
        returnOnDirectionIfRequired(itemInstanceEntity, event);

        // 事件发布
        publishEventIfRequired(itemInstanceEntity, event, EnumBizBiEventTriggerType.DirectionTransition);
    }

    /**
     * 同步计时信息
     *
     * @param flowInstUuid
     * @param taskTimer
     * @param taskData
     */
    @Override
    @Transactional
    public void syncTimerInfoIfRequired(String flowInstUuid, TaskTimer taskTimer, TaskData taskData) {
        BizProcessItemInstanceEntity itemInstanceEntity = getItemInstanceEntity(flowInstUuid, taskData);
        if (itemInstanceEntity == null) {
            return;
        }

        String timerUuid = taskTimer.getTimerUuid();
        if (StringUtils.isNotBlank(timerUuid)) {
            processItemInstanceService.updateTimerData(itemInstanceEntity, timerFacadeService.getTimer(timerUuid));
        }
    }

    /**
     * @param event
     */
    @Override
    public void onFlowBusinessStateChanged(FlowBusinessStateChangedEvent event) {
        Event flowEvent = event.getFlowEvent();
        String flowInstUuid = flowEvent.getFlowInstUuid();
        BizProcessItemInstanceEntity itemInstanceEntity = getItemInstanceEntity(flowInstUuid, flowEvent.getTaskData());
        if (itemInstanceEntity == null) {
            return;
        }

        String oldStateName = event.getOldStateName();
        String newStateName = event.getNewStateName();
        String oldStateCode = event.getOldStateCode();
        String newStateCode = event.getNewStateCode();
        DyFormData dyFormData = event.getDyFormData();
        FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig = event.getStateConfig();
        com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson flowBusinessDefinitionJson = event.getFlowBusinessDefinitionJson();


        List<BizFormStateHistoryEntity> historyEntities = Lists.newArrayList();
        StateManagerHelper.addFlowBusinessFormStateHistory(dyFormData, oldStateName, newStateName, stateConfig.getStateNameField(),
                itemInstanceEntity, flowEvent, stateConfig, historyEntities);
        StateManagerHelper.addFlowBusinessFormStateHistory(dyFormData, oldStateCode, newStateCode, stateConfig.getStateCodeField(),
                itemInstanceEntity, flowEvent, stateConfig, historyEntities);
        if (CollectionUtils.isNotEmpty(historyEntities)) {
            formStateHistoryService.saveAll(historyEntities);
        }
    }

    /**
     * 流程状态管理
     *
     * @param itemInstanceEntity
     * @param event
     * @param triggerType
     */
    private void workflowStateMamager(BizProcessItemInstanceEntity itemInstanceEntity, Event event, EnumFlowBizStateTriggerType... triggerType) {
        List<FlowBusinessDefinitionJson.FlowBusinessState> states = getStateConfigs(getProcessItemConfig(itemInstanceEntity, event));
        if (CollectionUtils.isNotEmpty(states)) {
            FlowBusinessDefinitionJson flowBusinessDefinitionJson = new FlowBusinessDefinitionJson();
            flowBusinessDefinitionJson.setStates(states);
            flowBusinessFacadeService.stateManager(event, Lists.newArrayList(triggerType), flowBusinessDefinitionJson);
        }
    }

    /**
     * 事件发布
     *
     * @param itemInstanceEntity
     * @param event
     * @param triggerType
     */
    private void publishEventIfRequired(BizProcessItemInstanceEntity itemInstanceEntity, Event event, EnumBizBiEventTriggerType triggerType) {
        ProcessItemConfig processItemConfig = getProcessItemConfig(itemInstanceEntity, event);

        List<ProcessItemConfig.EventPublishConfig> eventPublishConfigs = null;
        switch (triggerType) {
            case FlowStarted:
            case FlowEnd:
                eventPublishConfigs = getEventPublishConfigs(processItemConfig, triggerType, config ->
                        evaluateAdditionalCondition(config, event));
                break;
            case TaskCreated:
            case TaskCompleted:
            case TaskBelongTo:
                eventPublishConfigs = getEventPublishConfigs(processItemConfig, triggerType,
                        config -> config.getTaskIds() != null && config.getTaskIds().contains(event.getTaskId())
                                && evaluateAdditionalCondition(config, event));
                break;
            case TaskOperation:
                eventPublishConfigs = getEventPublishConfigs(processItemConfig, triggerType,
                        config -> config.getTaskIds() != null && config.getTaskIds().contains(event.getTaskId())
                                && StringUtils.equals(event.getActionType(), config.getActionType())
                                && evaluateAdditionalCondition(config, event));
                break;
            case DirectionTransition:
                eventPublishConfigs = getEventPublishConfigs(processItemConfig, triggerType,
                        config -> config.getDirectionIds() != null && config.getDirectionIds().contains(getDirectionId(event))
                                && evaluateAdditionalCondition(config, event));
                break;
        }
        if (CollectionUtils.isNotEmpty(eventPublishConfigs)) {
            eventPublishConfigs.forEach(config -> publishCustomItemEvent(itemInstanceEntity, event, config, processItemConfig));
        }
    }

    /**
     * 计算附加条件表达式
     *
     * @param config
     * @param event
     * @return
     */
    private boolean evaluateAdditionalCondition(ProcessItemConfig.EventPublishConfig config, Event event) {
        if (config.getAdditionalCondition()) {
            return WorkFlowScriptHelper.evaluateConditionExpression(event, config.getExpressionScriptType(), config.getConditionExpression());
        }
        return true;
    }

    /**
     * 发布事件
     *
     * @param itemInstanceEntity
     * @param event
     * @param eventPublishConfig
     * @param processItemConfig
     */
    private void publishCustomItemEvent(BizProcessItemInstanceEntity itemInstanceEntity, Event event,
                                        ProcessItemConfig.EventPublishConfig eventPublishConfig,
                                        ProcessItemConfig processItemConfig) {
        DyFormData eventDyformData = event.getDyFormData();
        if (eventDyformData == null) {
            eventDyformData = dyFormFacade.getDyFormData(event.getFormUuid(), event.getDataUuid());
        }
        Map<String, Object> extraData = Maps.newHashMap();
        extraData.put("taskData", event.getTaskData());
        eventListenerPublisher.publishItemCustomEvent(itemInstanceEntity, eventPublishConfig.getEventId(),
                eventPublishConfig.getResultField(), eventDyformData, processItemConfig, extraData);
    }

    /**
     * 办结时反馈
     *
     * @param itemInstanceEntity
     * @param event
     */
    public void returnOnOverIfRequired(BizProcessItemInstanceEntity itemInstanceEntity, Event event) {
        if (itemInstanceEntity == null) {
            return;
        }
        ProcessItemConfig.BusinessIntegrationConfig businessIntegrationConfig = getBusinessIntegrationConfig(getProcessItemConfig(itemInstanceEntity, event));

        // 单据转换处理
        doReturnWithBotRuleId(event, itemInstanceEntity, businessIntegrationConfig);
    }

    /**
     * 指定流向反馈
     *
     * @param itemInstanceEntity
     * @param event
     */
    public void returnOnDirectionIfRequired(BizProcessItemInstanceEntity itemInstanceEntity, Event event) {
        if (itemInstanceEntity == null) {
            return;
        }
        ProcessItemConfig.BusinessIntegrationConfig businessIntegrationConfig = getBusinessIntegrationConfig(getProcessItemConfig(itemInstanceEntity, event));
        if (!businessIntegrationConfig.isReturnWithDirection()) {
            return;
        }
        // 当前流向判断
        String returnDirectionId = businessIntegrationConfig.getReturnDirectionId();
        if (StringUtils.isBlank(returnDirectionId)) {
            return;
        }
        String directionId = getDirectionId(event);
        if (StringUtils.isBlank(directionId)) {
            return;
        }
        List<String> returnDirectionIds = Arrays.asList(StringUtils.split(returnDirectionId, Separator.SEMICOLON.getValue()));
        if (!returnDirectionIds.contains(directionId)) {
            return;
        }

        // 单据转换处理
        doReturnWithBotRuleId(event, itemInstanceEntity, businessIntegrationConfig);
    }

    /**
     * 单据转换处理
     *
     * @param event
     * @param itemInstanceEntity
     * @param businessIntegrationConfig
     */
    private void doReturnWithBotRuleId(Event event, BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig.BusinessIntegrationConfig businessIntegrationConfig) {
        // 是否使用单据转换判断
        String formDataType = businessIntegrationConfig.getFormDataType();
        if (!ProcessItemConfig.BusinessIntegrationConfig.FORM_DATA_TYPE_USE_BOT.equals(formDataType)) {
            logger.info("流程业务集成配置没有使用单据转换!");
            return;
        }

        // 反馈单据转换规则非空判断
        String returnBotRuleId = businessIntegrationConfig.getReturnBotRuleId();
        if (StringUtils.isBlank(returnBotRuleId)) {
            logger.info("流程业务集成反馈单据转换规则为空!");
            return;
        }

        String sourceFormUuid = event.getFormUuid();
        String sourceDataUuid = event.getDataUuid();
        DyFormData sourceDyformData = event.getDyFormData();
        // String targetFormUuid = itemInstanceEntity.getFormUuid();
        String targetDataUuid = itemInstanceEntity.getDataUuid();
        if (StringUtils.equals(sourceDataUuid, targetDataUuid)) {
            logger.info(String.format("流程业务集成反馈数据为同一笔数据[%s]，无需反馈!", targetDataUuid));
            return;
        }

        // 单据转换数据反馈处理
        Set<BotParam.BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotParam.BotFromParam botFromParam = new BotParam.BotFromParam(sourceDataUuid, sourceFormUuid,
                sourceDyformData);
        froms.add(botFromParam);
        BotParam botParam = new BotParam(returnBotRuleId, froms);
        botParam.setFroms(froms);
        botParam.setTargetUuid(targetDataUuid);
        try {
            botFacadeService.startBot(botParam);
        } catch (Exception e) {
            logger.error("单据转换:流程数据合并到业务事项时出错！", e);
            throw new RuntimeException("流程数据合并到业务事项时出错,请检查单据转换配置！", e);
        }
    }

    /**
     * 完成事项办件
     *
     * @param itemInstanceEntity
     * @param event
     */
    @Transactional
    public void completeItemInstance(BizProcessItemInstanceEntity itemInstanceEntity, Event event) {
        if (itemInstanceEntity == null || StringUtils.isBlank(itemInstanceEntity.getUuid())) {
            return;
        }
        BizProcessItemDataDto dto = new BizProcessItemDataDto();
        dto.setItemInstUuid(itemInstanceEntity.getUuid());
        processItemFacadeService.complete(dto);
    }

    /**
     * 获取业务事项
     *
     * @param flowInstUuid
     * @param taskData
     * @return
     */
    private BizProcessItemInstanceEntity getItemInstanceEntity(String flowInstUuid, TaskData taskData) {
        String key = "flowBusinessIntegrationProcessItemInstanceEntity_" + flowInstUuid;
        BizProcessItemInstanceEntity itemInstanceEntity = (BizProcessItemInstanceEntity) taskData.get(key);
        if (itemInstanceEntity != null) {
            return itemInstanceEntity;
        }

        String itemInstUuid = getItemInstUuid(flowInstUuid);
        itemInstanceEntity = processItemInstanceService.getOne(itemInstUuid);
        taskData.put(key, itemInstanceEntity);
        return itemInstanceEntity;
    }

    /**
     * 获取业务事项实例UUID
     *
     * @param flowInstUuid
     * @return
     */
    private String getItemInstUuid(String flowInstUuid) {
        BizBusinessIntegrationEntity integrationEntity = businessIntegrationService.getByTypeAndBizInstUuid(EnumBizBiType.Workflow.getValue(), flowInstUuid);
        if (integrationEntity == null) {
            logger.error(String.format("流程业务集成实例[flowInstUuid: %s]不存在!", flowInstUuid));
            String itemInstUuid = BusinessIntegrationContextHolder.getItemInstUuid();
            if (StringUtils.isNotBlank(itemInstUuid)) {
                return itemInstUuid;
            } else {
                logger.error(String.format("业务集成上下文中事项实例UUID为[%s]不存在!", itemInstUuid));
            }
            return null;
        }

        String itemInstUuid = integrationEntity.getItemInstUuid();
        return itemInstUuid;
    }

    /**
     * 获取业务事项配置
     *
     * @param itemInstanceEntity
     * @param event
     * @return
     */
    private ProcessItemConfig getProcessItemConfig(BizProcessItemInstanceEntity itemInstanceEntity, Event event) {
        TaskData taskData = event.getTaskData();
        String key = "processItemConfig_" + itemInstanceEntity.getUuid();
        ProcessItemConfig processItemConfig = (ProcessItemConfig) taskData.get(key);
        if (processItemConfig != null) {
            return processItemConfig;
        }

        String processDefUuid = itemInstanceEntity.getProcessDefUuid();
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processDefUuid);
        processItemConfig = parser.getProcessItemConfigById(itemInstanceEntity.getItemId());
        taskData.put(key, processItemConfig);
        return processItemConfig;
    }

    /**
     * 获取流程业务集成配置
     *
     * @param processItemConfig
     * @return
     */
    private ProcessItemConfig.BusinessIntegrationConfig getBusinessIntegrationConfig(ProcessItemConfig processItemConfig) {
        return processItemConfig.getBusinessIntegrationConfigByType(EnumBizBiType.Workflow.getValue());
    }

    /**
     * 获取流程集成事件发布配置
     *
     * @param processItemConfig
     * @param triggerType
     * @param predicate
     * @return
     */
    private List<ProcessItemConfig.EventPublishConfig> getEventPublishConfigs(ProcessItemConfig processItemConfig,
                                                                              EnumBizBiEventTriggerType triggerType,
                                                                              Predicate<ProcessItemConfig.EventPublishConfig> predicate) {
        ProcessItemConfig.BusinessIntegrationConfig businessIntegrationConfig = getBusinessIntegrationConfig(processItemConfig);
        List<ProcessItemConfig.EventPublishConfig> eventPublishConfigs = businessIntegrationConfig.getEventPublishConfigs();
        if (CollectionUtils.isEmpty(eventPublishConfigs)) {
            return Collections.emptyList();
        }

        return eventPublishConfigs.stream().filter(config -> triggerType.getValue().equals(config.getTriggerType())
                        && predicate.test(config))
                .collect(Collectors.toList());
    }

    /**
     * 获取流程集成状态管理配置
     *
     * @param processItemConfig
     * @return
     */
    private List<FlowBusinessDefinitionJson.FlowBusinessState> getStateConfigs(ProcessItemConfig processItemConfig) {
        ProcessItemConfig.BusinessIntegrationConfig businessIntegrationConfig = getBusinessIntegrationConfig(processItemConfig);
        return businessIntegrationConfig != null ? businessIntegrationConfig.getStates() : null;
    }

    /**
     * 获取流向ID
     *
     * @param event
     * @return
     */
    private String getDirectionId(Event event) {
        return event.getDirectionId();
    }

}

/*
 * @(#)11/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.groovy.GroovyUtils;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowScriptHelper;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.workflow.enums.EnumFlowBizStateTriggerType;
import com.wellsoft.pt.workflow.enums.EnumFlowBizStateValueType;
import com.wellsoft.pt.workflow.facade.service.WfFlowBusinessFacadeService;
import com.wellsoft.pt.workflow.listener.FlowBusinessListener;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;
import com.wellsoft.pt.workflow.support.FlowBusinessStateMatcher;
import com.wellsoft.pt.workflow.support.event.FlowBusinessStateChangedEvent;
import com.wellsoft.pt.workflow.utils.FlowBusinessDefinitionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description: 如何描述该类
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
@Service
public class WfFlowBusinessFacadeServiceImpl extends AbstractApiFacade implements WfFlowBusinessFacadeService {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired(required = false)
    private Map<String, FlowBusinessListener> flowBusinessListenerMap;

    /**
     * 获取流程业务定义
     *
     * @param event
     * @return
     */
    private FlowBusinessDefinitionJson getFlowBusinessDefinitionJson(Event event) {
        String flowBizDefId = Objects.toString(event.getTaskData().getCustomData(CustomRuntimeData.KEY_FLOW_BIZ_DEF_ID), StringUtils.EMPTY);
        FlowBusinessDefinitionJson flowBusinessDefinitionJson = FlowBusinessDefinitionUtils.getBusinessDefinitionJsonById(flowBizDefId);
        return flowBusinessDefinitionJson;
    }


    /**
     * 根据触发类型获取对应的状态配置
     *
     * @param event
     * @param flowBusinessDefinitionJson
     * @param stateTriggerType
     * @param flowBusinessStateMatcher
     * @return
     */
    private List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> getStateConfigs(Event event, FlowBusinessDefinitionJson flowBusinessDefinitionJson,
                                                                                     EnumFlowBizStateTriggerType stateTriggerType,
                                                                                     FlowBusinessStateMatcher flowBusinessStateMatcher) {
        List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> stateConfigs = Lists.newArrayList();
        List<FlowBusinessDefinitionJson.FlowBusinessState> states = flowBusinessDefinitionJson.getStates();
        if (CollectionUtils.isEmpty(states)) {
            return Collections.emptyList();
        }

        for (FlowBusinessDefinitionJson.FlowBusinessState state : states) {
            List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> configs = state.getStateConfigs();
            if (CollectionUtils.isEmpty(configs)) {
                continue;
            }
            for (FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig : configs) {
                if (StringUtils.equals(stateTriggerType.getValue(), stateConfig.getTriggerType())
                        && flowBusinessStateMatcher.match(stateConfig) && evaluateAdditionalCondition(event, stateConfig)) {
                    // 冗余字段
                    stateConfig.setStateNameField(state.getStateNameField());
                    stateConfig.setStateCodeField(state.getStateCodeField());
                    stateConfigs.add(stateConfig);
                }
            }
        }
        return stateConfigs;
    }

    private boolean evaluateAdditionalCondition(Event event, FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig) {
        if (stateConfig.getAdditionalCondition()) {
            return WorkFlowScriptHelper.evaluateConditionExpression(event, stateConfig.getExpressionScriptType(), stateConfig.getConditionExpression());
        }
        return true;
    }

    /**
     * 流程创建
     *
     * @param event
     */
    @Override
    public void onFlowStarted(Event event) {
        FlowBusinessDefinitionJson flowBusinessDefinitionJson = getFlowBusinessDefinitionJson(event);
        flowStartedStateManager(event, flowBusinessDefinitionJson);
    }

    private void flowStartedStateManager(Event event, FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> stateConfigs = getStateConfigs(event, flowBusinessDefinitionJson,
                EnumFlowBizStateTriggerType.FlowStarted, config -> true);
        // 变更业务状态
        stateConfigs.forEach(config -> changeBusinessState(event, config, flowBusinessDefinitionJson));
    }

    /**
     * 流程办结
     *
     * @param event
     */
    @Override
    public void onFlowEnd(Event event) {
        FlowBusinessDefinitionJson flowBusinessDefinitionJson = getFlowBusinessDefinitionJson(event);
        flowEndStateManager(event, flowBusinessDefinitionJson);
    }

    private void flowEndStateManager(Event event, FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> stateConfigs = getStateConfigs(event, flowBusinessDefinitionJson,
                EnumFlowBizStateTriggerType.FlowEnd, config -> true
        );
        // 变更业务状态
        stateConfigs.forEach(config -> changeBusinessState(event, config, flowBusinessDefinitionJson));
    }

    /**
     * 环节创建
     *
     * @param event
     */
    @Override
    public void onTaskCreated(Event event) {
        FlowBusinessDefinitionJson flowBusinessDefinitionJson = getFlowBusinessDefinitionJson(event);
        taskCreatedStateManager(event, flowBusinessDefinitionJson);
    }

    private void taskCreatedStateManager(Event event, FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> stateConfigs = getStateConfigs(event, flowBusinessDefinitionJson,
                EnumFlowBizStateTriggerType.TaskCreated, config ->
                        config.getTaskIds() != null && config.getTaskIds().contains(event.getTaskId())
        );
        // 变更业务状态
        stateConfigs.forEach(config -> changeBusinessState(event, config, flowBusinessDefinitionJson));
    }

    /**
     * 环节完成
     *
     * @param event
     */
    @Override
    public void onTaskCompleted(Event event) {
        FlowBusinessDefinitionJson flowBusinessDefinitionJson = getFlowBusinessDefinitionJson(event);
        taskCompletedStateManager(event, flowBusinessDefinitionJson);
    }

    private void taskCompletedStateManager(Event event, FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> stateConfigs = getStateConfigs(event, flowBusinessDefinitionJson,
                EnumFlowBizStateTriggerType.TaskCompleted, config ->
                        config.getTaskIds() != null && config.getTaskIds().contains(event.getTaskId())
        );
        // 变更业务状态
        stateConfigs.forEach(config -> changeBusinessState(event, config, flowBusinessDefinitionJson));
    }

    /**
     * 环节操作
     *
     * @param event
     */
    @Override
    public void onTaskOperation(Event event) {
        FlowBusinessDefinitionJson flowBusinessDefinitionJson = getFlowBusinessDefinitionJson(event);
        taskOperationStateManager(event, flowBusinessDefinitionJson);
    }


    private void taskOperationStateManager(Event event, FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> stateConfigs = getStateConfigs(event, flowBusinessDefinitionJson,
                EnumFlowBizStateTriggerType.TaskOperation, config ->
                        config.getTaskIds() != null && config.getTaskIds().contains(event.getTaskId()) &&
                                StringUtils.equals(event.getActionType(), config.getActionType())
        );
        // 变更业务状态
        stateConfigs.forEach(config -> changeBusinessState(event, config, flowBusinessDefinitionJson));
    }

    /**
     * 环节归属
     *
     * @param event
     */
    @Override
    public void onTaskBelongTo(Event event) {
        FlowBusinessDefinitionJson flowBusinessDefinitionJson = getFlowBusinessDefinitionJson(event);
        taskBelongToStateManager(event, flowBusinessDefinitionJson);
    }

    private void taskBelongToStateManager(Event event, FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> stateConfigs = getStateConfigs(event, flowBusinessDefinitionJson,
                EnumFlowBizStateTriggerType.TaskBelongTo, config ->
                        config.getTaskIds() != null && config.getTaskIds().contains(event.getTaskId())
        );
        // 变更业务状态
        stateConfigs.forEach(config -> changeBusinessState(event, config, flowBusinessDefinitionJson));
    }

    /**
     * 流向流转
     *
     * @param event
     */
    @Override
    public void onDirectionTransition(Event event) {
        FlowBusinessDefinitionJson flowBusinessDefinitionJson = getFlowBusinessDefinitionJson(event);
        directionTransitionStateManager(event, flowBusinessDefinitionJson);
    }


    private void directionTransitionStateManager(Event event, FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        List<FlowBusinessDefinitionJson.FlowBusinessStateConfig> stateConfigs = getStateConfigs(event, flowBusinessDefinitionJson,
                EnumFlowBizStateTriggerType.DirectionTransition, config -> {
                    if (CollectionUtils.isEmpty(config.getDirectionIds())) {
                        return false;
                    }
                    return config.getDirectionIds().contains(getDirectionId(event));
                });
        // 变更业务状态
        stateConfigs.forEach(config -> changeBusinessState(event, config, flowBusinessDefinitionJson));
    }

    /**
     * 状态管理
     *
     * @param event
     * @param triggerTypes
     * @param flowBusinessDefinitionJson
     */
    @Override
    public void stateManager(Event event, List<EnumFlowBizStateTriggerType> triggerTypes, FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        for (EnumFlowBizStateTriggerType triggerType : triggerTypes) {
            doStateManager(event, triggerType, flowBusinessDefinitionJson);
        }
    }

    public void doStateManager(Event event, EnumFlowBizStateTriggerType triggerType, FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        switch (triggerType) {
            case FlowStarted:
                flowStartedStateManager(event, flowBusinessDefinitionJson);
                break;
            case FlowEnd:
                flowEndStateManager(event, flowBusinessDefinitionJson);
                break;
            case TaskCreated:
                taskCreatedStateManager(event, flowBusinessDefinitionJson);
                break;
            case TaskCompleted:
                taskCompletedStateManager(event, flowBusinessDefinitionJson);
                break;
            case TaskOperation:
                taskOperationStateManager(event, flowBusinessDefinitionJson);
                break;
            case TaskBelongTo:
                taskBelongToStateManager(event, flowBusinessDefinitionJson);
                break;
            case DirectionTransition:
                directionTransitionStateManager(event, flowBusinessDefinitionJson);
                break;
        }
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

    /**
     * 获取表单数据
     *
     * @param event
     * @return
     */
    private DyFormData getDyformData(Event event) {
        DyFormData dyFormData = event.getDyFormData();
        if (dyFormData != null) {
            return dyFormData;
        }
        return dyFormFacade.getDyFormData(event.getFormUuid(), event.getDataUuid());
    }

    /**
     * 设置状态值
     *
     * @param dyFormData
     * @param stateField
     * @param value
     * @param event
     * @return
     */
    private boolean setStateValue(DyFormData dyFormData, String stateField, String value, Event event) {
        if (StringUtils.isNotBlank(stateField)) {
            if (dyFormData.isFieldExist(stateField)) {
                dyFormData.setFieldValue(stateField, value);
                // 标记需更新表单
                event.getTaskData().addUpdatedDyFormData(dyFormData.getDataUuid(), dyFormData);
                return true;
            }
        }
        return false;
    }


    /**
     * 变更业务状态
     *
     * @param event
     * @param stateConfig
     * @param flowBusinessDefinitionJson
     */
    private void changeBusinessState(Event event, FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig,
                                     FlowBusinessDefinitionJson flowBusinessDefinitionJson) {
        // 状态名称
        String stateName = renderStateNameValue(event, stateConfig);
        // 状态代码
        String stateCode = renderStateCodeValue(event, stateConfig);

        // 设置表单状态数据
        DyFormData dyFormData = getDyformData(event);
        String oldStateName = getStateValue(dyFormData, stateConfig.getStateNameField());
        String oldStateCode = getStateValue(dyFormData, stateConfig.getStateCodeField());
        boolean stateNameChanged = setStateValue(dyFormData, stateConfig.getStateNameField(), stateName, event);
        boolean stateCodeChanged = setStateValue(dyFormData, stateConfig.getStateCodeField(), stateCode, event);

        // 发布状态变更事件
        if (stateNameChanged || stateCodeChanged) {
            publishBusinessStateChangedEvent(oldStateName, stateName, oldStateCode, stateCode, dyFormData, stateConfig, flowBusinessDefinitionJson, event);
        }
    }

    /**
     * @param dyFormData
     * @param stateField
     * @return
     */
    public static String getStateValue(DyFormData dyFormData, String stateField) {
        if (StringUtils.isNotBlank(stateField)) {
            if (dyFormData.isFieldExist(stateField)) {
                return Objects.toString(dyFormData.getFieldValue(stateField), StringUtils.EMPTY);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 渲染状态名称值
     *
     * @param event
     * @param stateConfig
     * @return
     */
    private String renderStateNameValue(Event event, FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig) {
        return renderStateValue(event, stateConfig.getStateNameScriptType(), stateConfig.getStateNameValue());
    }

    /**
     * 渲染状态代码值
     *
     * @param event
     * @param stateConfig
     * @return
     */
    private String renderStateCodeValue(Event event, FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig) {
        return renderStateValue(event, stateConfig.getStateCodeScriptType(), stateConfig.getStateCodeValue());
    }

    /**
     * @param event
     * @param valueType
     * @param script
     * @return
     */
    private String renderStateValue(Event event, String valueType, String script) {
        if (StringUtils.isBlank(valueType)) {
            valueType = EnumFlowBizStateValueType.FixedValue.getValue();
        }
        Object value = null;
        EnumFlowBizStateValueType stateValueType = EnumFlowBizStateValueType.getByValue(valueType);
        switch (stateValueType) {
            case Freemarker:
                value = renderStateFreemarkerValue(event, script);
                break;
            case Groovy:
                value = renderStateGroovyValue(event, script);
                break;
            default:
                value = script;
        }
        return Objects.toString(value, StringUtils.EMPTY);
    }

    /**
     * 渲染freemarker表达式
     *
     * @param event
     * @param script
     * @return
     */
    private Object renderStateFreemarkerValue(Event event, String script) {
        if (StringUtils.isNotBlank(script)) {
            try {
                return StringUtils.trim(TemplateEngineFactory.getDefaultTemplateEngine().process(script, WorkFlowScriptHelper.getScriptVariables(event)));
            } catch (Exception e) {
                logger.error(String.format("流程业务状态freemarker表达式[%s]渲染出错！", script), e);
                throw new RuntimeException(e);
            }
        }
        return script;
    }

    /**
     * 渲染Groovy脚本
     *
     * @param event
     * @param script
     * @return
     */
    private Object renderStateGroovyValue(Event event, String script) {
        if (StringUtils.isNotBlank(script)) {
            try {
                return GroovyUtils.run(script, WorkFlowScriptHelper.getScriptVariables(event));
            } catch (Exception e) {
                logger.error(String.format("流程业务状态groovy脚本[%s]渲染出错！", script), e);
                throw new RuntimeException(e);
            }
        }
        return script;
    }

    /**
     * 发布业务状态变更事件
     *
     * @param oldStateName
     * @param newStateName
     * @param oldStateCode
     * @param newStateCode
     * @param stateConfig
     * @param flowBusinessDefinitionJson
     * @param event
     */
    private void publishBusinessStateChangedEvent(String oldStateName, String newStateName, String oldStateCode,
                                                  String newStateCode, DyFormData dyFormData, FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig,
                                                  FlowBusinessDefinitionJson flowBusinessDefinitionJson, Event event) {
        String listener = flowBusinessDefinitionJson.getListener();
        if (MapUtils.isNotEmpty(flowBusinessListenerMap) && StringUtils.isNotBlank(listener)
                && flowBusinessListenerMap.containsKey(listener)) {
            flowBusinessListenerMap.get(listener).onBusinessStateChanged(oldStateName, newStateName, oldStateCode, newStateCode,
                    stateConfig, flowBusinessDefinitionJson, event);
        }

        FlowBusinessStateChangedEvent stateChangedEvent = new FlowBusinessStateChangedEvent(oldStateName, newStateName, oldStateCode, newStateCode,
                dyFormData, stateConfig, flowBusinessDefinitionJson, event);
        ApplicationContextHolder.getApplicationContext().publishEvent(stateChangedEvent);
    }

}

/*
 * @(#)12/8/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.groovy.GroovyUtils;
import com.wellsoft.pt.biz.entity.BizFormStateHistoryEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.enums.*;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.workflow.enums.EnumFlowBizStateTriggerType;
import com.wellsoft.pt.workflow.enums.EnumFlowBizStateValueType;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 12/8/23.1	zhulh		12/8/23		Create
 * </pre>
 * @date 12/8/23
 */
public class StateManagerHelper {

    private static Logger LOG = LoggerFactory.getLogger(StateManagerHelper.class);

    /**
     * 获取业务主体状态配置
     *
     * @param states
     * @param stateTriggerType
     * @param stateConfigMatcher
     * @return
     */
    public static List<StateDefinition.StateConfig> getStateConfigs(List<StateDefinition> states,
                                                                    EnumStateTriggerType stateTriggerType,
                                                                    StateConfigMatcher stateConfigMatcher) {
        if (CollectionUtils.isEmpty(states)) {
            return Collections.emptyList();
        }

        List<StateDefinition.StateConfig> stateConfigs = Lists.newArrayList();
        for (StateDefinition stateDefinition : states) {
            List<StateDefinition.StateConfig> configs = stateDefinition.getStateConfigs();
            if (CollectionUtils.isEmpty(configs)) {
                continue;
            }

            for (StateDefinition.StateConfig stateConfig : configs) {
                if (StringUtils.equals(stateTriggerType.getValue(), stateConfig.getTriggerType())
                        && stateConfigMatcher.match(stateConfig)) {
                    // 冗余字段
                    stateConfig.setStateNameField(stateDefinition.getStateNameField());
                    stateConfig.setStateCodeField(stateDefinition.getStateCodeField());
                    stateConfigs.add(stateConfig);
                }
            }
        }
        return stateConfigs;
    }


    /**
     * 设置业务主体状态值
     *
     * @param dyFormData
     * @param event
     * @return
     */
    public static boolean setStateValue(DyFormData dyFormData, String stateField, String value, Event event) {
        if (StringUtils.isNotBlank(stateField)) {
            if (dyFormData.isFieldExist(stateField)) {
                dyFormData.setFieldValue(stateField, value);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取业务主体状态值
     *
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
    public static String renderStateNameValue(Event event, StateDefinition.StateConfig stateConfig) {
        return renderStateValue(event, stateConfig.getStateNameScriptType(), stateConfig.getStateNameValue());
    }

    /**
     * 渲染状态代码值
     *
     * @param event
     * @param stateConfig
     * @return
     */
    public static String renderStateCodeValue(Event event, StateDefinition.StateConfig stateConfig) {
        return renderStateValue(event, stateConfig.getStateCodeScriptType(), stateConfig.getStateCodeValue());
    }

    /**
     * @param event
     * @param valueType
     * @param script
     * @return
     */
    private static String renderStateValue(Event event, String valueType, String script) {
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
        return StringUtils.trim(Objects.toString(value, StringUtils.EMPTY));
    }

    /**
     * 渲染freemarker表达式
     *
     * @param event
     * @param script
     * @return
     */
    private static Object renderStateFreemarkerValue(Event event, String script) {
        if (StringUtils.isNotBlank(script)) {
            try {
                return TemplateEngineFactory.getDefaultTemplateEngine().process(script, getScriptVariables(event));
            } catch (Exception e) {
                LOG.error(String.format("流程业务状态freemarker表达式[%s]渲染出错！", script), e);
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
    private static Object renderStateGroovyValue(Event event, String script) {
        if (StringUtils.isNotBlank(script)) {
            try {
                return GroovyUtils.run(script, getScriptVariables(event));
            } catch (Exception e) {
                LOG.error(String.format("流程业务状态groovy脚本[%s]渲染出错！", script), e);
                throw new RuntimeException(e);
            }
        }
        return script;
    }

    /**
     * 获取脚本变量
     *
     * @param event
     * @return
     */
    private static Map<String, Object> getScriptVariables(Event event) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        // 表单数据
        DyFormData dyFormData = event.getDyFormData();
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(event.getFormUuid(), event.getDataUuid(), false);
        }
        Map<String, Object> formData = Maps.newHashMap();
        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
        if (MapUtils.isNotEmpty(formDatas)) {
            for (Map.Entry<String, List<Map<String, Object>>> entry : formDatas.entrySet()) {
                formData.put(dyFormFacade.getFormIdByFormUuid(entry.getKey()), entry.getValue());
            }
        }
        formData.put(dyFormData.getFormId(), dyFormData.getFormDataOfMainform());

        Map<String, Object> values = Maps.newHashMap();
        values.put("event", event);
        values.put("dyFormData", dyFormData);
        values.put("formData", formData);

        // 当前用户相关变量
        values.putAll(TemplateEngineFactory.getExplainRootModel());
        return values;
    }

    /**
     * 计算条件表达式
     *
     * @param event
     * @param scriptType
     * @param expression
     * @return
     */
    public static boolean evaluateConditionExpression(Event event, String scriptType, String expression) {
        boolean result = false;
        EnumFlowBizStateValueType stateValueType = EnumFlowBizStateValueType.getByValue(scriptType);
        switch (stateValueType) {
            case Groovy:
                result = evaluateGroovyConditionExpression(event, expression);
                break;
            default:
        }
        return result;
    }

    /**
     * 计算groovy条件表达式
     *
     * @param event
     * @param expression
     * @return
     */
    private static boolean evaluateGroovyConditionExpression(Event event, String expression) {
        if (StringUtils.isNotBlank(expression)) {
            try {
                return Boolean.TRUE.equals(GroovyUtils.run(expression, getScriptVariables(event)));
            } catch (Exception e) {
                LOG.error(String.format("流程业务groovy条件表达式脚本[%s]执行出错！", expression), e);
                throw new RuntimeException(e);
            }
        }
        return false;
    }


    /**
     * @param dyFormData
     * @param oldState
     * @param newState
     * @param stateField
     * @param event
     * @param stateConfig
     * @param historyEntities
     */
    public static void addFormStateHistory(EnumStateHistoryType historyType, DyFormData dyFormData, String oldState, String newState, String stateField,
                                           Event event, StateDefinition.StateConfig stateConfig, ProcessDefinitionJsonParser parser,
                                           List<BizFormStateHistoryEntity> historyEntities) {
        if (StringUtils.equals(oldState, newState)) {
            return;
        }

        BizFormStateHistoryEntity entity = new BizFormStateHistoryEntity();
        entity.setFormUuid(dyFormData.getFormUuid());
        entity.setDataUuid(dyFormData.getDataUuid());
        entity.setType(historyType);
        entity.setStateField(stateField);
        entity.setOldState(oldState);
        entity.setNewState(newState);
        entity.setTriggerType(stateConfig.getTriggerType());
        entity.setTriggerInfo(getTriggerInfo(event, stateConfig, parser));
        entity.setEntityId(event.getEntityId());
        entity.setProcessInstUuid(event.getProcessInstUuid());
        historyEntities.add(entity);
    }

    /**
     * @param event
     * @param stateConfig
     * @param parser
     * @return
     */
    private static String getTriggerInfo(Event event, StateDefinition.StateConfig stateConfig, ProcessDefinitionJsonParser parser) {
        EnumStateTriggerType triggerType = EnumStateTriggerType.valueOf(stateConfig.getTriggerType());
        if (triggerType == null) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(triggerType.getName());
        switch (triggerType) {
            case PROCESS_STATE_CHANGED:
                EnumBizProcessState enumBizProcessState = EnumBizProcessState.getByValue(stateConfig.getProcessState());
                if (enumBizProcessState != null) {
                    sb.append("，" + enumBizProcessState.getName());
                }
                break;
            case PROCESS_NODE_STATE_CHANGED:
                sb.append("，" + stateConfig.getProcessNodeName() + "(" + stateConfig.getProcessNodeCode() + ")");
                EnumBizProcessNodeState enumBizProcessNodeState = EnumBizProcessNodeState.getByValue(stateConfig.getProcessNodeState());
                if (enumBizProcessNodeState != null) {
                    sb.append(enumBizProcessNodeState.getName());
                }
                break;
            case ITEM_STATE_CHANGED:
                sb.append("，" + stateConfig.getItemName() + "(" + stateConfig.getItemCode() + ")");
                EnumBizProcessItemState enumBizProcessItemState = EnumBizProcessItemState.getByValue(stateConfig.getItemState());
                if (enumBizProcessItemState != null) {
                    sb.append(enumBizProcessItemState.getName());
                }
                break;
            case ITEM_EVENT_PUBLISHED:
                sb.append("，" + stateConfig.getItemName() + "(" + stateConfig.getItemCode() + ")");
                ProcessItemEvent processItemEvent = (ProcessItemEvent) event;
                ProcessItemConfig itemConfig = parser.getProcessItemConfigById(processItemEvent.getItemId());
                ProcessItemConfig.DefineEventConfig defineEventConfig = itemConfig.getDefineEvents().stream()
                        .filter(config -> StringUtils.equals(config.getId(), stateConfig.getItemEventId())).findFirst().orElse(null);
                if (defineEventConfig != null) {
                    sb.append(defineEventConfig.getName());
                }
                break;
            case PROCESS_ENTITY_TIMER_EVENT_PUBLISHED:
                EnumProcessEntityTimerEventType timerEventType = EnumProcessEntityTimerEventType.getByValue(stateConfig.getTimerEventId());
                if (timerEventType != null) {
                    sb.append("，" + timerEventType.getName());
                }
                break;
        }
        return sb.toString();
    }

    /**
     * @param dyFormData
     * @param oldState
     * @param newState
     * @param stateCodeField
     * @param itemInstanceEntity
     * @param event
     * @param stateConfig
     * @param historyEntities
     */
    public static void addFlowBusinessFormStateHistory(DyFormData dyFormData, String oldState, String newState,
                                                       String stateCodeField, BizProcessItemInstanceEntity itemInstanceEntity,
                                                       com.wellsoft.pt.bpm.engine.context.event.Event event, FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig,
                                                       List<BizFormStateHistoryEntity> historyEntities) {
        if (StringUtils.equals(oldState, newState)) {
            return;
        }

        BizFormStateHistoryEntity entity = new BizFormStateHistoryEntity();
        entity.setFormUuid(dyFormData.getFormUuid());
        entity.setDataUuid(dyFormData.getDataUuid());
        entity.setType(EnumStateHistoryType.Workflow);
        entity.setStateField(stateCodeField);
        entity.setOldState(oldState);
        entity.setNewState(newState);
        entity.setTriggerType(stateConfig.getTriggerType());
        entity.setTriggerInfo(getFlowTriggerInfo(event, stateConfig));
        entity.setEntityId(itemInstanceEntity.getUuid());
        entity.setItemInstUuid(itemInstanceEntity.getUuid());
        entity.setProcessNodeInstUuid(itemInstanceEntity.getProcessNodeInstUuid());
        entity.setProcessInstUuid(itemInstanceEntity.getProcessInstUuid());
        historyEntities.add(entity);
    }

    /**
     * @param event
     * @param stateConfig
     * @return
     */
    private static String getFlowTriggerInfo(com.wellsoft.pt.bpm.engine.context.event.Event event, FlowBusinessDefinitionJson.FlowBusinessStateConfig stateConfig) {
        EnumFlowBizStateTriggerType triggerType = EnumFlowBizStateTriggerType.getByValue(stateConfig.getTriggerType());
        if (triggerType == null) {
            return StringUtils.EMPTY;
        }
        Token token = event.getTaskData().getToken();
        if (token == null) {
            return StringUtils.EMPTY;
        }

        FlowDelegate flowDelegate = token.getFlowDelegate();
        StringBuilder sb = new StringBuilder();
        sb.append(triggerType.getName());
        switch (triggerType) {
            case FlowStarted:
            case FlowEnd:
                break;
            case TaskCreated:
            case TaskCompleted:
            case TaskBelongTo:
            case TaskOperation:
                Node taskNode = flowDelegate.getTaskNode(event.getTaskId());
                String taskName = taskNode == null ? event.getTaskId() : taskNode.getName() + "(" + taskNode.getId() + ")";
                sb.append("，" + taskName);
                if (EnumFlowBizStateTriggerType.TaskOperation.equals(triggerType)) {
                    sb.append(event.getAction());
                }
                break;
            case DirectionTransition:
                Direction direction = flowDelegate.getDirection(event.getDirectionId());
                String directionName = direction == null ? event.getDirectionId() : direction.getName();
                sb.append("，" + directionName);
                break;
        }
        return sb.toString();
    }

}

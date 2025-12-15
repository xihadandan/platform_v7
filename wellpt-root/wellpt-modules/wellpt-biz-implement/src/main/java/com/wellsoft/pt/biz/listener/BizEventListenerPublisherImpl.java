/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.biz.dto.BizMilestoneDto;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumProcessEventType;
import com.wellsoft.pt.biz.enums.EnumProcessItemEventType;
import com.wellsoft.pt.biz.enums.EnumProcessNodeEventType;
import com.wellsoft.pt.biz.facade.service.BizMilestoneFacadeService;
import com.wellsoft.pt.biz.itemflow.ItemFlowProcessItemListener;
import com.wellsoft.pt.biz.listener.event.ProcessEvent;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.listener.event.ProcessNodeEvent;
import com.wellsoft.pt.biz.state.StateManagerProcessItemListener;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
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
 * 10/25/22.1	zhulh		10/25/22		Create
 * </pre>
 * @date 10/25/22
 */
@Component
public class BizEventListenerPublisherImpl implements BizEventListenerPublisher {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private Map<String, BizProcessListener> processListenerMap;

    @Autowired(required = false)
    private Map<String, BizProcessNodeListener> processNodeListenerMap;

    @Autowired(required = false)
    private Map<String, BizProcessItemListener> processItemListenerMap;

    @Autowired
    private List<StateManagerProcessItemListener> stateManagerProcessItemListeners;

    @Autowired
    private ItemFlowProcessItemListener itemFlowProcessItemListener;

    @Autowired
    private BizMilestoneFacadeService milestoneFacadeService;

    private List<BizProcessItemListener> getBizProcessItemListener(ProcessItemConfig processItemConfig) {
        String listener = processItemConfig.getListener();
        if (StringUtils.isBlank(listener) || MapUtils.isEmpty(processItemListenerMap)) {
            return Collections.emptyList();
        }

        List<BizProcessItemListener> itemListeners = Lists.newArrayList();
        String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
        for (String itemListener : listeners) {
            BizProcessItemListener processItemListener = processItemListenerMap.get(itemListener);
            if (processItemListener != null) {
                itemListeners.add(processItemListener);
            }
        }
        return itemListeners;
    }

    /**
     * 发布事项创建事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemCreated(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.Created);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onCreated(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onCreated(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onCreated(itemEvent);
    }

    /**
     * 发布事项开始事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemStarted(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.Started);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onStarted(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onStarted(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onStarted(itemEvent);
    }

    /**
     * 发布事项开始计时事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemTimerStarted(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.TimerStarted);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onTimerStarted(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onTimerStarted(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onTimerStarted(itemEvent);
    }

    /**
     * 发布事项暂停计时事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemTimerPaused(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.TimerPaused);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onTimerPaused(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onTimerPaused(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onTimerPaused(itemEvent);
    }

    /**
     * 发布事项恢复计时事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemTimerResumed(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.TimerPaused);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onTimerResumed(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onTimerResumed(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onTimerResumed(itemEvent);
    }

    /**
     * 发布事项计时到期事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemTimerDue(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.TimerDue);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onTimerDue(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onTimerDue(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onTimerDue(itemEvent);
    }

    /**
     * 发布事项计时逾期事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemTimerOverDue(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.TimerOverdue);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onTimerOverDue(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onTimerOverDue(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onTimerOverDue(itemEvent);
    }

    /**
     * 发布事项挂起事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemSuspended(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.Suspended);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onSuspended(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onSuspended(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onSuspended(itemEvent);
    }

    /**
     * 发布事项恢复事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemResumed(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.Resumed);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onResume(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onResume(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onResume(itemEvent);
    }

    /**
     * 发布事项撤销事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemCanceled(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.Cancelled);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onCancelled(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onCancelled(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onCancelled(itemEvent);
    }

    /**
     * 发布事项完成事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemCompleted(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.Completed);
        // 发布事件监听
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onCompleted(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onCompleted(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onCompleted(itemEvent);
    }

    /**
     * 发布事项重新开始事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    @Override
    public void publishItemRestarted(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig) {
        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        // 发布事件监听
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, EnumProcessItemEventType.Updated);
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onRestarted(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onRestarted(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onRestarted(itemEvent);
    }

    /**
     * 发布事项自定义事件监听
     *
     * @param itemInstanceEntity
     * @param eventType
     * @param resultField
     * @param eventDyformData
     * @param processItemConfig
     * @param extraData
     */
    @Override
    public void publishItemCustomEvent(BizProcessItemInstanceEntity itemInstanceEntity, String eventType, String resultField,
                                       DyFormData eventDyformData, ProcessItemConfig processItemConfig, Map<String, Object> extraData) {
        ProcessItemConfig.DefineEventConfig defineEventConfig = processItemConfig.getDefineEvents()
                .stream().filter(config -> StringUtils.equals(config.getId(), eventType)).findFirst().orElse(null);
        if (defineEventConfig == null) {
            logger.warn("发布的事项{}在事件定义中不存在！", eventType);
            return;
        }

        List<BizProcessItemListener> processItemListeners = getBizProcessItemListener(processItemConfig);
        // 发布事件监听
        ProcessItemEvent itemEvent = new ProcessItemEvent(itemInstanceEntity, eventType);
        if (MapUtils.isNotEmpty(extraData)) {
            itemEvent.getExtraData().putAll(extraData);
        }
        if (CollectionUtils.isNotEmpty(processItemListeners)) {
            processItemListeners.stream().forEach(listener -> listener.onRestarted(itemEvent));
        }
        // 状态管理监听业务事项事件
        stateManagerProcessItemListeners.stream().forEach(listener -> listener.onRestarted(itemEvent));
        // 事项流监听处理
        itemFlowProcessItemListener.onCustomEvent(itemEvent);

        // 里程碑事件
        if (defineEventConfig.getMilestone() && StringUtils.isNotBlank(resultField)) {
            String name = defineEventConfig.getName();
            String remark = defineEventConfig.getRemark();
            List<String> resultFields = Arrays.asList(StringUtils.split(resultField, Separator.SEMICOLON.getValue()));
            BizMilestoneDto dto = new BizMilestoneDto(name, itemInstanceEntity.getUuid(), remark, eventDyformData, resultFields);
            milestoneFacadeService.saveDto(dto);
        }
    }

    /**
     * 发布业务流程创建事件监听
     *
     * @param processInstanceEntity
     * @param parser
     */
    @Override
    public void publishProcessCreated(BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser) {
        List<BizProcessListener> processListeners = getBizProcessListener(parser);
        if (CollectionUtils.isEmpty(processListeners)) {
            return;
        }
        ProcessEvent processEvent = new ProcessEvent(processInstanceEntity, EnumProcessEventType.Created);
        processListeners.stream().forEach(listener -> listener.onCreated(processEvent));
    }

    /**
     * 发布业务流程创建事件监听
     *
     * @param processInstanceEntity
     * @param parser
     */
    @Override
    public void publishProcessStarted(BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser) {
        List<BizProcessListener> processListeners = getBizProcessListener(parser);
        if (CollectionUtils.isEmpty(processListeners)) {
            return;
        }
        ProcessEvent processEvent = new ProcessEvent(processInstanceEntity, EnumProcessEventType.Started);
        processListeners.stream().forEach(listener -> listener.onStarted(processEvent));
    }

    /**
     * 发布业务流程完成事件监听
     *
     * @param processInstanceEntity
     * @param parser
     */
    @Override
    public void publishProcessCompleted(BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser) {
        List<BizProcessListener> processListeners = getBizProcessListener(parser);
        if (CollectionUtils.isEmpty(processListeners)) {
            return;
        }
        ProcessEvent processEvent = new ProcessEvent(processInstanceEntity, EnumProcessEventType.Completed);
        processListeners.stream().forEach(listener -> listener.onCompleted(processEvent));
    }

    /**
     * 发布过程节点创建事件监听
     *
     * @param processNodeInstanceEntity
     * @param processNodeConfig
     */
    @Override
    public void publishProcessNodeCreated(BizProcessNodeInstanceEntity processNodeInstanceEntity, ProcessNodeConfig processNodeConfig) {
        List<BizProcessNodeListener> processNodeListeners = getBizProcessNodeListener(processNodeConfig);
        if (CollectionUtils.isEmpty(processNodeListeners)) {
            return;
        }
        ProcessNodeEvent processEvent = new ProcessNodeEvent(processNodeInstanceEntity, EnumProcessNodeEventType.Created);
        processNodeListeners.stream().forEach(listener -> listener.onCreated(processEvent));
    }

    private List<BizProcessListener> getBizProcessListener(ProcessDefinitionJsonParser parser) {
        String listener = parser.getProcessListener();
        if (StringUtils.isBlank(listener) || MapUtils.isEmpty(processListenerMap)) {
            return Collections.emptyList();
        }

        List<BizProcessListener> processListeners = Lists.newArrayList();
        String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
        for (String processListener : listeners) {
            BizProcessListener bizProcessListener = processListenerMap.get(processListener);
            if (bizProcessListener != null) {
                processListeners.add(bizProcessListener);
            }
        }
        return processListeners;
    }


    /**
     * 发布过程节点开始事件监听
     *
     * @param processNodeInstanceEntity
     * @param processNodeConfig
     */
    @Override
    public void publishProcessNodeStarted(BizProcessNodeInstanceEntity processNodeInstanceEntity, ProcessNodeConfig processNodeConfig) {
        List<BizProcessNodeListener> processNodeListeners = getBizProcessNodeListener(processNodeConfig);
        if (CollectionUtils.isEmpty(processNodeListeners)) {
            return;
        }
        ProcessNodeEvent processEvent = new ProcessNodeEvent(processNodeInstanceEntity, EnumProcessNodeEventType.Started);
        processNodeListeners.stream().forEach(listener -> listener.onStarted(processEvent));
    }

    /**
     * 发布过程节点完成事件监听
     *
     * @param processNodeInstanceEntity
     * @param processNodeConfig
     */
    @Override
    public void publishProcessNodeCompleted(BizProcessNodeInstanceEntity processNodeInstanceEntity, ProcessNodeConfig processNodeConfig) {
        List<BizProcessNodeListener> processNodeListeners = getBizProcessNodeListener(processNodeConfig);
        if (CollectionUtils.isEmpty(processNodeListeners)) {
            return;
        }
        ProcessNodeEvent processEvent = new ProcessNodeEvent(processNodeInstanceEntity, EnumProcessNodeEventType.Completed);
        processNodeListeners.stream().forEach(listener -> listener.onCompleted(processEvent));
    }

    private List<BizProcessNodeListener> getBizProcessNodeListener(ProcessNodeConfig processNodeConfig) {
        String listener = processNodeConfig.getListener();
        if (StringUtils.isBlank(listener) || MapUtils.isEmpty(processNodeListenerMap)) {
            return Collections.emptyList();
        }

        List<BizProcessNodeListener> processNodeListeners = Lists.newArrayList();
        String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
        for (String processNodeListener : listeners) {
            BizProcessNodeListener bizProcessNodeListener = processNodeListenerMap.get(processNodeListener);
            if (bizProcessNodeListener != null) {
                processNodeListeners.add(bizProcessNodeListener);
            }
        }
        return processNodeListeners;
    }

}

/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;

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
public interface BizEventListenerPublisher {
    /**
     * 发布事项创建事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemCreated(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项开始事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemStarted(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项开始计时事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemTimerStarted(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项暂停计时事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemTimerPaused(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项恢复计时事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemTimerResumed(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项计时到期事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemTimerDue(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项计时逾期事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemTimerOverDue(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项挂起事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemSuspended(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项恢复事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemResumed(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项撤销事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemCanceled(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项完成事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemCompleted(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

    /**
     * 发布事项重新开始事件监听
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     */
    void publishItemRestarted(BizProcessItemInstanceEntity itemInstanceEntity, ProcessItemConfig processItemConfig);

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
    void publishItemCustomEvent(BizProcessItemInstanceEntity itemInstanceEntity, String eventType, String resultField,
                                DyFormData eventDyformData, ProcessItemConfig processItemConfig, Map<String, Object> extraData);

    /**
     * 发布业务流程创建事件监听
     *
     * @param processInstanceEntity
     * @param parser
     */
    void publishProcessCreated(BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser);

    /**
     * 发布业务流程开始事件监听
     *
     * @param processInstanceEntity
     * @param parser
     */
    void publishProcessStarted(BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser);

    /**
     * 发布业务流程完成事件监听
     *
     * @param processInstanceEntity
     * @param parser
     */
    void publishProcessCompleted(BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser);

    /**
     * 发布过程节点创建事件监听
     *
     * @param processNodeInstanceEntity
     * @param processNodeConfig
     */
    void publishProcessNodeCreated(BizProcessNodeInstanceEntity processNodeInstanceEntity, ProcessNodeConfig processNodeConfig);

    /**
     * 发布过程节点开始事件监听
     *
     * @param processNodeInstanceEntity
     * @param processNodeConfig
     */
    void publishProcessNodeStarted(BizProcessNodeInstanceEntity processNodeInstanceEntity, ProcessNodeConfig processNodeConfig);

    /**
     * 发布过程节点完成事件监听
     *
     * @param processNodeInstanceEntity
     * @param processNodeConfig
     */
    void publishProcessNodeCompleted(BizProcessNodeInstanceEntity processNodeInstanceEntity, ProcessNodeConfig processNodeConfig);

}

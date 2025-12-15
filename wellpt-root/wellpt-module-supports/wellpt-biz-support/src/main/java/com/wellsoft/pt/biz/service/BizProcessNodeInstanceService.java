/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizProcessNodeInstanceDao;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.support.WorkTimePeriod;

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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
public interface BizProcessNodeInstanceService extends JpaService<BizProcessNodeInstanceEntity, BizProcessNodeInstanceDao, String> {

    /**
     * 创建过程节点
     *
     * @param processInstanceEntity
     * @param processNodeConfig
     * @param parser
     * @return
     */
    BizProcessNodeInstanceEntity create(BizProcessInstanceEntity processInstanceEntity,
                                        ProcessNodeConfig processNodeConfig, ProcessDefinitionJsonParser parser);

    /**
     * 根据过节节点实例UUID，开始过程节点
     *
     * @param uuid
     */
    void startByUuid(String uuid);

    /**
     * 根据过程节点ID及业务主体ID获取过程节点实例
     *
     * @param id
     * @param entityId
     * @return
     */
    BizProcessNodeInstanceEntity getByIdAndEntityId(String id, String entityId);

    /**
     * 根据业务流程实例UUID获取过程结点实例列表
     *
     * @param processInstUuid
     * @return
     */
    List<BizProcessNodeInstanceEntity> listByProcessInstUuid(String processInstUuid);

    /**
     * 根据业务流程实例UUID列表获取过程结点实例列表
     *
     * @param processInstUuids
     * @return
     */
    List<BizProcessNodeInstanceEntity> listByProcessInstUuids(List<String> processInstUuids);

    /**
     * 根据业务流程定义ID及业务主体ID获取过程结点实例列表
     *
     * @param processDefId
     * @param entityId
     * @return
     */
    List<BizProcessNodeInstanceEntity> listByProcessDefIdAndEntityId(String processDefId, String entityId);

    /**
     * 根据业务流程实例UUID、过程节点编码、过程节点状态，判断是否存在过程节点实例
     *
     * @param code
     * @param state
     * @param processInstUuid
     * @return
     */
    boolean isExistsProcessNodeInstanceByCode(String code, String state, String processInstUuid);

    /**
     * 根据事项ID列表、业务流程实例UUID，判断对应的事项是否都完成
     *
     * @param nodeIds
     * @param processInstUuid
     * @return
     */
    boolean isCompleteByNodeIdsAndProcessInstUuid(List<String> nodeIds, String processInstUuid);

    /**
     * 根据过程节点实例UUID，完成过程节点
     *
     * @param uuid
     * @return
     */
    BizProcessNodeInstanceEntity completeByUuid(String uuid);

    /**
     * 根据过程节点实例UUID，重新开始过程节点
     *
     * @param uuid
     * @return
     */
    BizProcessNodeInstanceEntity restartByUuid(String uuid);

    /**
     * 获取过程节点实例的工作时间段信息
     *
     * @return
     */
    WorkTimePeriod getWorkTimePeriodByUuid(String uuid);

    /**
     * 变更业务事项办件状态
     *
     * @param event
     * @param stateConfig
     * @param processItemConfig
     * @param processNodeConfig
     * @param parser
     */
    void changeNodeInstanceState(Event event, StateDefinition.StateConfig stateConfig, ProcessNodeConfig.ProcessNodeFormConfig processItemConfig, ProcessNodeConfig processNodeConfig, ProcessDefinitionJsonParser parser);

}

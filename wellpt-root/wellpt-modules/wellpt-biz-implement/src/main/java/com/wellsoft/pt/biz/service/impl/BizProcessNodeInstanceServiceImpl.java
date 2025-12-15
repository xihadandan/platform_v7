/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.biz.condition.ProcessNodeConditionEvaluator;
import com.wellsoft.pt.biz.dao.BizProcessNodeInstanceDao;
import com.wellsoft.pt.biz.entity.BizFormStateHistoryEntity;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumBizProcessNodeState;
import com.wellsoft.pt.biz.enums.EnumStateHistoryType;
import com.wellsoft.pt.biz.listener.BizEventListenerPublisher;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.service.BizFormStateHistoryService;
import com.wellsoft.pt.biz.service.BizProcessNodeInstanceService;
import com.wellsoft.pt.biz.state.BizStateChangedPublisher;
import com.wellsoft.pt.biz.state.event.BizProcessNodeInstanceDyformStateChangedEvent;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.biz.utils.ProcessTitleUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.timer.support.WorkTimePeriod;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

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
@Service
public class BizProcessNodeInstanceServiceImpl extends AbstractJpaServiceImpl<BizProcessNodeInstanceEntity, BizProcessNodeInstanceDao, String> implements BizProcessNodeInstanceService {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BizEventListenerPublisher eventListenerPublisher;

    @Autowired
    private BizStateChangedPublisher stateChangedPublisher;

    @Autowired
    private ProcessNodeConditionEvaluator processNodeConditionEvaluator;

    @Autowired
    private BizFormStateHistoryService formStateHistoryService;

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    /**
     * 创建过程节点
     *
     * @param processInstanceEntity
     * @param processNodeConfig
     * @param parser
     * @return
     */
    @Override
    public BizProcessNodeInstanceEntity create(BizProcessInstanceEntity processInstanceEntity, ProcessNodeConfig processNodeConfig, ProcessDefinitionJsonParser parser) {
        String entityName = processInstanceEntity.getEntityName();
        String entityId = processInstanceEntity.getEntityId();
        ProcessNodeConfig parentNodeConfig = parser.getParentProcessNodeConfigByNodeId(processNodeConfig.getId());
        BizProcessNodeInstanceEntity parentNodeInstanceEntity = null;
        // 上级过程结点
        if (parentNodeConfig != null) {
            String parentProcessNodeId = parentNodeConfig.getId();
            parentNodeInstanceEntity = this.getByIdAndEntityId(parentProcessNodeId, entityId);
            if (parentNodeInstanceEntity == null) {
                parentNodeInstanceEntity = this.create(processInstanceEntity, parentNodeConfig, parser);
            }
        }

        // 检查过程节点状态条件是否满足
        boolean result = checkProcessNodeState(EnumBizProcessNodeState.Running.getValue(), processInstanceEntity, processNodeConfig, parser);
        if (!result) {
            throw new BusinessException(String.format("过程节点[%s]不满足启动条件！", processNodeConfig.getName()));
        }
        String name = processNodeConfig.getName();
        String id = processNodeConfig.getId();
        String code = processNodeConfig.getCode();
        String title = ProcessTitleUtils.generateProcessNodeInstanceTitle(name);
        ProcessNodeConfig.ProcessNodeFormConfig processNodeFormConfig = processNodeConfig.getFormConfig();
        String formUuid = processNodeFormConfig.getFormUuid();
        String dataUuid = null;

        if (StringUtils.isBlank(formUuid)) {
            throw new BusinessException(String.format("过程节点[%s]的办理单据不能为空！", processNodeConfig.getName()));
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put(processNodeFormConfig.getEntityIdField(), entityId);
        // 根据业务主体ID查询对应的表单数据UUID
        List<String> dataUuids = dyFormFacade.queryUniqueForFields(formUuid, params, null);
        if (CollectionUtils.isNotEmpty(dataUuids)) {
            dataUuid = dataUuids.get(0);
        } else {
            DyFormData dyFormData = dyFormFacade.createDyformData(formUuid);
            setProcessNodeMappingFieldValues(entityName, entityId, processNodeConfig, dyFormData);
            dataUuid = dyFormFacade.saveFormData(dyFormData);
        }
        Date startTime = null;//Calendar.getInstance().getTime();
        Date endTime = null;
        Integer timeLimitType = processNodeConfig.getTimeLimitType();
        Integer timeLimit = processNodeConfig.getTimeLimit();
        Double totalTime = 0d;
        Boolean milestone = processNodeConfig.getMilestone();
        String state = EnumBizProcessNodeState.Created.getValue();
        String parentNodeInstUuid = parentNodeInstanceEntity != null ? parentNodeInstanceEntity.getUuid() : StringUtils.EMPTY;
        String processInstUuid = processInstanceEntity.getUuid();
        String processDefUuid = parser.getProcessDefUuid();

        BizProcessNodeInstanceEntity entity = new BizProcessNodeInstanceEntity();
        entity.setName(name);
        entity.setId(id);
        entity.setCode(code);
        entity.setTitle(title);
        entity.setEntityName(entityName);
        entity.setEntityId(entityId);
        entity.setFormUuid(formUuid);
        entity.setDataUuid(dataUuid);
        entity.setStartTime(startTime);
        entity.setEndTime(endTime);
        entity.setTimeLimitType(timeLimitType);
        entity.setTimeLimit(timeLimit);
        entity.setTotalTime(totalTime);
        entity.setMilestone(milestone);
        entity.setState(state);
        entity.setParentNodeInstUuid(parentNodeInstUuid);
        entity.setProcessInstUuid(processInstUuid);
        entity.setProcessDefUuid(processDefUuid);
        this.save(entity);

        // 发布过程节点创建事件监听
        eventListenerPublisher.publishProcessNodeCreated(entity, processNodeConfig);

        // 发布过节节点状态变更
        stateChangedPublisher.publishNodeStateChanged(EnumBizProcessNodeState.Created, null, entity);
        return entity;
    }

    /**
     * 根据过节节点实例UUID，开始过程节点
     *
     * @param uuid
     */
    @Override
    @Transactional
    public void startByUuid(String uuid) {
        BizProcessNodeInstanceEntity nodeInstanceEntity = this.getOne(uuid);
        String oldState = nodeInstanceEntity.getState();
        if (StringUtils.equals(EnumBizProcessNodeState.Created.getValue(), oldState)) {
            nodeInstanceEntity.setStartTime(Calendar.getInstance().getTime());
            nodeInstanceEntity.setState(EnumBizProcessNodeState.Running.getValue());
            this.save(nodeInstanceEntity);

            ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(nodeInstanceEntity.getProcessDefUuid());
            ProcessNodeConfig processNodeConfig = parser.getProcessNodeConfigById(nodeInstanceEntity.getId());
            // 发布过程节点开始事件监听
            eventListenerPublisher.publishProcessNodeStarted(nodeInstanceEntity, processNodeConfig);

            // 发布过节节点状态变更
            stateChangedPublisher.publishNodeStateChanged(EnumBizProcessNodeState.Running, EnumBizProcessNodeState.Created, nodeInstanceEntity);
        }
    }

    /**
     * 检查过程节点状态条件是否满足
     *
     * @param changedState
     * @param processInstanceEntity
     * @param processNodeConfig
     * @param parser
     * @return
     */
    private boolean checkProcessNodeState(String changedState, BizProcessInstanceEntity processInstanceEntity,
                                          ProcessNodeConfig processNodeConfig, ProcessDefinitionJsonParser parser) {
        ProcessNodeConfig.StateCondition stateCondition = processNodeConfig.getStateConditionByChangedState(changedState);
        if (stateCondition == null) {
            return true;
        }
        return processNodeConditionEvaluator.evaluate(processInstanceEntity, stateCondition, parser);
    }

    private void setProcessNodeMappingFieldValues(String entityName, String entityId, ProcessNodeConfig processNodeConfig, DyFormData dyFormData) {
        ProcessNodeConfig.ProcessNodeFormConfig formConfig = processNodeConfig.getFormConfig();
        String entityNameField = formConfig.getEntityNameField();
        String entityIdField = formConfig.getEntityIdField();
        if (dyFormData.isFieldExist(entityNameField)) {
            dyFormData.setFieldValue(entityNameField, entityName);
        }
        if (dyFormData.isFieldExist(entityIdField)) {
            dyFormData.setFieldValue(entityIdField, entityId);
        }
    }

    /**
     * 根据过程节点ID及业务主体ID获取过程节点实例
     *
     * @param id
     * @param entityId
     * @return
     */
    @Override
    public BizProcessNodeInstanceEntity getByIdAndEntityId(String id, String entityId) {
        Assert.hasText(id, "过程节点ID不能为空！");
        Assert.hasText(entityId, "业务主体ID不能为空！");

        BizProcessNodeInstanceEntity entity = new BizProcessNodeInstanceEntity();
        entity.setId(id);
        entity.setEntityId(entityId);
        List<BizProcessNodeInstanceEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据业务流程实例UUID获取过程结点实例列表
     *
     * @param processInstUuid
     * @return
     */
    @Override
    public List<BizProcessNodeInstanceEntity> listByProcessInstUuid(String processInstUuid) {
        Assert.hasText(processInstUuid, "业务流程实例UUID不能为空！");

        BizProcessNodeInstanceEntity entity = new BizProcessNodeInstanceEntity();
        entity.setProcessInstUuid(processInstUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * 根据业务流程实例UUID列表获取过程结点实例列表
     *
     * @param processInstUuids
     * @return
     */
    @Override
    public List<BizProcessNodeInstanceEntity> listByProcessInstUuids(List<String> processInstUuids) {
        Assert.notEmpty(processInstUuids, "业务流程实例UUID列表不能为空！");

        return this.dao.listByFieldInValues("processInstUuid", processInstUuids);
    }

    /**
     * 根据业务流程定义ID及业务主体ID获取过程结点实例列表
     *
     * @param processDefId
     * @param entityId
     * @return
     */
    @Override
    public List<BizProcessNodeInstanceEntity> listByProcessDefIdAndEntityId(String processDefId, String entityId) {
        Assert.hasText(processDefId, "业务流程定义ID不能为空！");
        Assert.hasText(entityId, "业务主体ID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("processDefId", processDefId);
        params.put("entityId", entityId);
        String hql = "from BizProcessNodeInstanceEntity t where t.entityId = :entityId and exists(select d.uuid from BizProcessDefinitionEntity d where d.id = :processDefId and d.uuid = t.processDefUuid)";
        return this.dao.listByHQL(hql, params);
    }

    /**
     * 根据业务流程实例UUID、过程节点编码、过程节点状态，判断是否存在过程节点实例
     *
     * @param code
     * @param state
     * @param processInstUuid
     * @return
     */
    @Override
    public boolean isExistsProcessNodeInstanceByCode(String code, String state, String processInstUuid) {
        Assert.hasText(code, "过程节点编码不能为空！");
        Assert.hasText(state, "过程节点状态不能为空！");
        if (StringUtils.isBlank(processInstUuid)) {
            return false;
        }
        // Assert.hasText(processInstUuid, "业务流程实例UUID不能为空！");

        BizProcessNodeInstanceEntity entity = new BizProcessNodeInstanceEntity();
        entity.setProcessInstUuid(processInstUuid);
        entity.setCode(code);
        entity.setState(state);
        return this.dao.countByEntity(entity) > 0;
    }

    /**
     * 根据事项ID列表、业务流程实例UUID，判断对应的事项是否都完成
     *
     * @param nodeIds
     * @param processInstUuid
     * @return
     */
    @Override
    public boolean isCompleteByNodeIdsAndProcessInstUuid(List<String> nodeIds, String processInstUuid) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return true;
        }

        Assert.hasText(processInstUuid, "业务流程实例UUID不能为空！");

        String hql = "select t.id as id from BizProcessNodeInstanceEntity t where t.processInstUuid = :processInstUuid and t.id in(:nodeIds) and t.state = :state";
        Map<String, Object> values = Maps.newHashMap();
        values.put("processInstUuid", processInstUuid);
        values.put("nodeIds", nodeIds);
        values.put("state", EnumBizProcessNodeState.Completed.getValue());
        List<String> completedNodeIds = this.dao.listCharSequenceByHQL(hql, values);

        List<String> checkNodeIds = Lists.newArrayList(nodeIds);
        checkNodeIds.removeAll(completedNodeIds);
        return CollectionUtils.isEmpty(checkNodeIds);
    }

    /**
     * 根据过程节点实例UUID，完成过程节点
     *
     * @param uuid
     */
    @Override
    @Transactional
    public BizProcessNodeInstanceEntity completeByUuid(String uuid) {
        Assert.hasText(uuid, "过程节点实例UUID不能为空！");

        BizProcessNodeInstanceEntity nodeInstanceEntity = this.dao.getOne(uuid);
        nodeInstanceEntity.setState(EnumBizProcessNodeState.Completed.getValue());
        nodeInstanceEntity.setEndTime(Calendar.getInstance().getTime());
        this.dao.save(nodeInstanceEntity);

        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(nodeInstanceEntity.getProcessDefUuid());
        ProcessNodeConfig processNodeConfig = parser.getProcessNodeConfigById(nodeInstanceEntity.getId());
        // 发布过程节点完成事件监听
        eventListenerPublisher.publishProcessNodeCompleted(nodeInstanceEntity, processNodeConfig);

        // 发布过节节点状态变更
        stateChangedPublisher.publishNodeStateChanged(EnumBizProcessNodeState.Completed, EnumBizProcessNodeState.Running, nodeInstanceEntity);

        return nodeInstanceEntity;
    }

    /**
     * 根据过程节点实例UUID，重新开始过程节点
     *
     * @param uuid
     * @return
     */
    @Override
    public BizProcessNodeInstanceEntity restartByUuid(String uuid) {
        Assert.hasText(uuid, "过程节点实例UUID不能为空！");

        BizProcessNodeInstanceEntity entity = this.dao.getOne(uuid);
        entity.setState(EnumBizProcessNodeState.Running.getValue());
        entity.setEndTime(null);
        this.dao.save(entity);
        return entity;
    }

    /**
     * @param uuid
     * @return
     */
    @Override
    public WorkTimePeriod getWorkTimePeriodByUuid(String uuid) {
        Assert.hasText(uuid, "过程节点实例UUID不能为空！");

        BizProcessNodeInstanceEntity entity = this.dao.getOne(uuid);
        Date startTime = entity.getStartTime();
        Date endTime = entity.getEndTime();
        if (endTime == null) {
            endTime = Calendar.getInstance().getTime();
        }

        WorkTimePeriod workTimePeriod = workTimePlanFacadeService.getWorkTimePeriod(null, startTime, endTime);
        return workTimePeriod;
    }

    /**
     * 变更业务事项办件状态
     *
     * @param event
     * @param stateConfig
     * @param processItemConfig
     * @param processNodeConfig
     * @param parser
     */
    @Override
    @Transactional
    public void changeNodeInstanceState(Event event, StateDefinition.StateConfig stateConfig,
                                        ProcessNodeConfig.ProcessNodeFormConfig processItemConfig,
                                        ProcessNodeConfig processNodeConfig, ProcessDefinitionJsonParser parser) {
        // 设置表单状态数据
        DyFormData dyFormData = getProcessNodeInstanceDyformData(processNodeConfig, event);
        if (dyFormData == null) {
            logger.error("process instance form data is null");
            return;
        }

        String oldStateName = StateManagerHelper.getStateValue(dyFormData, stateConfig.getStateNameField());
        String oldStateCode = StateManagerHelper.getStateValue(dyFormData, stateConfig.getStateCodeField());
        // 状态名称
        String stateName = StateManagerHelper.renderStateNameValue(event, stateConfig);
        // 状态代码
        String stateCode = StateManagerHelper.renderStateCodeValue(event, stateConfig);

        boolean stateNameChanged = StateManagerHelper.setStateValue(dyFormData, stateConfig.getStateNameField(), stateName, event);
        boolean stateCodeChanged = StateManagerHelper.setStateValue(dyFormData, stateConfig.getStateCodeField(), stateCode, event);

        // 保存变更值
        if (stateNameChanged || stateCodeChanged) {
            dyFormFacade.saveFormData(dyFormData);

            List<BizFormStateHistoryEntity> historyEntities = Lists.newArrayList();
            if (stateNameChanged) {
                StateManagerHelper.addFormStateHistory(EnumStateHistoryType.Node, dyFormData, oldStateName, stateName,
                        stateConfig.getStateNameField(), event, stateConfig, parser, historyEntities);
                logger.info("change business entity state name {}:{}", new Object[]{stateConfig.getStateNameField(), stateName});
            }
            if (stateCodeChanged) {
                StateManagerHelper.addFormStateHistory(EnumStateHistoryType.Node, dyFormData, oldStateCode, stateCode,
                        stateConfig.getStateCodeField(), event, stateConfig, parser, historyEntities);
                logger.info("change business entity state name {}:{}", new Object[]{stateConfig.getStateCodeField(), stateCode});
            }

            // 发布状态变更事件
            publishProcessNodeInstanceDyformStateChangedEvent(dyFormData, stateName, stateCode, stateConfig, parser, event);

            // 保存状态变更历史
            if (CollectionUtils.isNotEmpty(historyEntities)) {
                historyEntities.forEach(stateHistoryEntity -> {
                    String handleNodeInstUuid = Objects.toString(event.getExtraData().get("handleNodeInstUuid"), StringUtils.EMPTY);
                    if (StringUtils.isNotBlank(handleNodeInstUuid)) {
                        stateHistoryEntity.setProcessNodeInstUuid(handleNodeInstUuid);
                    }
                });
                formStateHistoryService.saveAll(historyEntities);
            }
        }
    }

    /**
     * 获取过程节点办件表单数据
     *
     * @param processNodeConfig
     * @param event
     * @return
     */
    private DyFormData getProcessNodeInstanceDyformData(ProcessNodeConfig processNodeConfig, Event event) {
        String processNodeId = processNodeConfig.getId();
        String entityId = event.getEntityId();
        BizProcessNodeInstanceEntity nodeInstanceEntity = this.getByIdAndEntityId(processNodeId, entityId);
        if (nodeInstanceEntity == null) {
            return null;
        }

        event.getExtraData().put("handleNodeInstUuid", nodeInstanceEntity.getUuid());
        String formUuid = nodeInstanceEntity.getFormUuid();
        String dataUuid = nodeInstanceEntity.getDataUuid();
        if (StringUtils.isBlank(formUuid) || StringUtils.isBlank(dataUuid)) {
            return null;
        }

        return dyFormFacade.getDyFormData(formUuid, dataUuid);
    }

    /**
     * 发布过程节点办件表单数据状态变更事件
     *
     * @param dyFormData
     * @param stateName
     * @param stateCode
     * @param stateConfig
     * @param parser
     * @param event
     */
    private void publishProcessNodeInstanceDyformStateChangedEvent(DyFormData dyFormData, String stateName, String stateCode,
                                                                   StateDefinition.StateConfig stateConfig,
                                                                   ProcessDefinitionJsonParser parser, Event event) {
        ApplicationContextHolder.getApplicationContext().publishEvent(new BizProcessNodeInstanceDyformStateChangedEvent(event, dyFormData,
                stateConfig.getStateNameField(), stateName, stateConfig.getStateCodeField(), stateCode));
    }

}

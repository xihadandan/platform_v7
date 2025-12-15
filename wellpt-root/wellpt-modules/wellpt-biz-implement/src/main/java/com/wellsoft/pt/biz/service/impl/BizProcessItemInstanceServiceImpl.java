/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.biz.dao.BizProcessItemInstanceDao;
import com.wellsoft.pt.biz.dto.BizProcessItemDataDto;
import com.wellsoft.pt.biz.entity.*;
import com.wellsoft.pt.biz.enums.EnumBizProcessItemState;
import com.wellsoft.pt.biz.enums.EnumStateHistoryType;
import com.wellsoft.pt.biz.listener.BizEventListenerPublisher;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.query.BizProcessItemInstanceCountQueryItem;
import com.wellsoft.pt.biz.service.*;
import com.wellsoft.pt.biz.state.BizStateChangedPublisher;
import com.wellsoft.pt.biz.state.event.BizProcessItemInstanceDyformStateChangedEvent;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.biz.utils.ProcessTitleUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dto.TsTimerDto;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.timer.support.TimerWorkTime;
import com.wellsoft.pt.timer.support.WorkTimePeriod;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
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
public class BizProcessItemInstanceServiceImpl extends AbstractJpaServiceImpl<BizProcessItemInstanceEntity, BizProcessItemInstanceDao, String> implements BizProcessItemInstanceService {

    @Autowired
    private BizItemDefinitionService bizItemDefinitionService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BizProcessInstanceService processInstanceService;

    @Autowired
    private BizProcessNodeInstanceService processNodeInstanceService;

    @Autowired
    private BizEventListenerPublisher eventListenerPublisher;

    @Autowired
    private BizStateChangedPublisher stateChangedPublisher;

    @Autowired
    private BizFormStateHistoryService formStateHistoryService;

    @Autowired
    private TsTimerFacadeService timerFacadeService;

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    /**
     * 根据业务事项定义UUID列表获取业务事项实例数量
     *
     * @param itemDefUuids
     * @return
     */
    @Override
    public Long countByItemDefUuids(List<String> itemDefUuids) {
        String hql = "from BizProcessItemInstanceEntity t where t.itemDefUuid in(:itemDefUuids)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("itemDefUuids", itemDefUuids);
        return this.dao.countByHQL(hql, values);
    }

    /**
     * 根据过程节点UUID列表获取对应业务事项实例数量
     *
     * @param processNodeInstUuids
     * @return
     */
    @Override
    public Map<String, Long> getCountMapByProcessNodeInstUuids(List<String> processNodeInstUuids) {
        Assert.notEmpty(processNodeInstUuids, "过程节点实例UUID不能为空！");

        Map<String, Object> values = Maps.newHashMap();
        values.put("processNodeInstUuids", processNodeInstUuids);
        List<BizProcessItemInstanceCountQueryItem> items = this.dao.listItemByNameSQLQuery("countItemInstanceByProcessNodeInstUuids",
                BizProcessItemInstanceCountQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE));

        Map<String, Long> countMap = Maps.newHashMap();
        items.stream().forEach(item -> countMap.put(item.getProcessNodeInstUuid(), item.getCount()));
        return countMap;
    }

    /**
     * 业务事项保存为草稿
     *
     * @param itemDataDto
     * @param dyFormData
     * @return
     */
    @Override
    @Transactional
    public String saveAsDraft(BizProcessItemDataDto itemDataDto, DyFormData dyFormData) {
        String processDefId = itemDataDto.getProcessDefId();
        String processItemId = itemDataDto.getItemId();
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
        if (BooleanUtils.isFalse(parser.getProcessDefinitionJson().getEnabled())) {
            throw new BusinessException("业务流程[" + parser.getProcessDefName() + "]已禁用，不能发起事项！");
        }
        String itemId = StringUtils.split(processItemId, Separator.SEMICOLON.getValue())[0];
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
        BizItemDefinitionEntity itemDefinitionEntity = bizItemDefinitionService.getById(processItemConfig.getItemDefId());

        dyFormFacade.saveFormData(dyFormData);

        BizProcessItemInstanceEntity parentItemInstanceEntity = null;
        String parentItemInstUuid = itemDataDto.getParentItemInstUuid();
        if (StringUtils.isNotBlank(parentItemInstUuid)) {
            parentItemInstanceEntity = this.getOne(parentItemInstUuid);
        }

        String itemDefName = itemDefinitionEntity.getName();
        String itemDefId = itemDefinitionEntity.getId();
        String itemName = itemDataDto.getItemName();
        String itemCode = itemDataDto.getItemCode();
        if (StringUtils.isBlank(itemName)) {
            itemName = parser.getProcessItemNameById(processItemId);
        }
        if (StringUtils.isBlank(itemCode)) {
            itemCode = parser.getProcessItemCodeById(processItemId);
        }
        String itemType = itemDefinitionEntity.getType();
        String title = ProcessTitleUtils.generateItemInstanceTitle(itemName);
        // String entityName = Objects.toString(dyFormData.getFieldValue(processItemConfig.getEntityNameField()), StringUtils.EMPTY);
        // String entityId = Objects.toString(dyFormData.getFieldValue(processItemConfig.getEntityIdField()), StringUtils.EMPTY);
        String formUuid = dyFormData.getFormUuid();
        String dataUuid = dyFormData.getDataUuid();
        Integer timeLimitType = processItemConfig.getTimeLimitType();
        // Integer timeLimit = parseTimeLimit(dyFormData, processItemConfig);
        Double totalTime = null;
        Boolean milestone = processItemConfig.getMilestone();
        Boolean multiple = isMultipleItemId(processItemId);
        String state = EnumBizProcessItemState.Created.getValue();
        String timerUuid = null;
        Integer timerState = 0;
        Integer timingState = 0;
        Integer overDueState = 0;
        Date dueTime = null;
        String belongItemInstUuid = null;
        String itemDefUuid = itemDefinitionEntity.getUuid();
        String processNodeInstUuid = null;
        String processInstUuid = null;
        String processDefUuid = parser.getProcessDefUuid();
        // String processDefId = parser.getProcessDefId();
        String itemFlowId = itemDataDto.getItemFlowId();
        String itemFlowInstUuid = itemDataDto.getItemFlowInstUuid();

        BizProcessItemInstanceEntity itemInstanceEntity = new BizProcessItemInstanceEntity();
        // 事项流，事项流定义ID为事项ID，事项流实例UUID为发起的事项实例UUID
        // 不是选择多个事项发起的事项、不是指定组合事项下的事项，按事项流方式发起
        if (StringUtils.isBlank(itemFlowInstUuid) && !multiple && !parser.isIncludeItem(itemId)) {
            itemFlowId = itemId;
            itemFlowInstUuid = SnowFlake.getId() + StringUtils.EMPTY;
            itemInstanceEntity.setUuid(itemFlowInstUuid);
            itemInstanceEntity.setCreator(SpringSecurityUtils.getCurrentUserId());
            itemInstanceEntity.setCreateTime(Calendar.getInstance().getTime());
        }
        itemInstanceEntity.setItemDefName(itemDefName);
        itemInstanceEntity.setItemDefId(itemDefId);
        itemInstanceEntity.setItemName(itemName);
        itemInstanceEntity.setItemCode(itemCode);
        itemInstanceEntity.setItemId(processItemId);
        itemInstanceEntity.setItemType(itemType);
        itemInstanceEntity.setTitle(title);
        // itemInstanceEntity.setEntityName(entityName);
        // itemInstanceEntity.setEntityId(entityId);
        itemInstanceEntity.setFormUuid(formUuid);
        itemInstanceEntity.setDataUuid(dataUuid);
        itemInstanceEntity.setTimeLimitType(timeLimitType);
        // itemInstanceEntity.setTimeLimit(timeLimit);
        itemInstanceEntity.setTotalTime(totalTime);
        itemInstanceEntity.setMilestone(milestone);
        itemInstanceEntity.setMultiple(multiple);
        itemInstanceEntity.setState(state);
        itemInstanceEntity.setTimerUuid(timerUuid);
        itemInstanceEntity.setTimerState(timerState);
        itemInstanceEntity.setTimingState(timingState);
        itemInstanceEntity.setDueTime(dueTime);
        itemInstanceEntity.setParentItemInstUuid(parentItemInstUuid);
        itemInstanceEntity.setBelongItemInstUuid(belongItemInstUuid);
        itemInstanceEntity.setItemDefUuid(itemDefUuid);
        // itemInstanceEntity.setProcessNodeInstUuid(processNodeInstUuid);
        // itemInstanceEntity.setProcessInstUuid(processInstUuid);
        itemInstanceEntity.setProcessDefUuid(processDefUuid);
        itemInstanceEntity.setProcessDefId(processDefId);
        itemInstanceEntity.setItemFlowDefId(itemFlowId);
        itemInstanceEntity.setItemFlowInstUuid(itemFlowInstUuid);

        // 同步表单字段映射数据
        syncFormDataMapping(itemInstanceEntity, dyFormData, processItemConfig);

        // 获取业务流程实例
        if (parentItemInstanceEntity == null) {
            BizProcessInstanceEntity processInstanceEntity = getProcessInstance(itemInstanceEntity.getEntityId(), parser);
            if (processInstanceEntity == null) {
                processInstanceEntity = processInstanceService.create(itemInstanceEntity.getEntityName(),
                        itemInstanceEntity.getEntityId(), parser);
            }
            processInstUuid = processInstanceEntity.getUuid();

            // 获取过程节点
            ProcessNodeConfig processNodeConfig = parser.getProcessNodeConfigByItemId(itemId);
            BizProcessNodeInstanceEntity processNodeInstanceEntity = getProcessNodeInstance(itemInstanceEntity.getEntityId(), processNodeConfig, parser);
            if (processNodeInstanceEntity == null) {
                processNodeInstanceEntity = processNodeInstanceService.create(processInstanceEntity, processNodeConfig, parser);
            }
            processNodeInstUuid = processNodeInstanceEntity.getUuid();
        } else {
            processInstUuid = parentItemInstanceEntity.getProcessInstUuid();
            processNodeInstUuid = parentItemInstanceEntity.getProcessNodeInstUuid();
        }

        itemInstanceEntity.setProcessInstUuid(processInstUuid);
        itemInstanceEntity.setProcessNodeInstUuid(processNodeInstUuid);
        this.dao.save(itemInstanceEntity);

        // 发布事件
        eventListenerPublisher.publishItemCreated(itemInstanceEntity, processItemConfig);

        // 发布状态变更
        stateChangedPublisher.publishItemStateChanged(EnumBizProcessItemState.Created, null, itemInstanceEntity);

        return itemInstanceEntity.getUuid();
    }

    /**
     * 是否多个事项一起发起
     *
     * @param processItemId
     * @return
     */
    private boolean isMultipleItemId(String processItemId) {
        return StringUtils.indexOf(processItemId, Separator.SEMICOLON.getValue()) != -1;
    }

    /***
     * 获取业务流程实例
     *
     * @param entityId
     * @param parser
     * @return
     */
    private BizProcessInstanceEntity getProcessInstance(String entityId, ProcessDefinitionJsonParser parser) {
        String processDefId = parser.getProcessDefId();
        BizProcessInstanceEntity processInstanceEntity = processInstanceService.getByIdAndEntityId(processDefId, entityId);
        return processInstanceEntity;
    }

    /**
     * 获取过程节点实例
     *
     * @param entityId
     * @param processNodeConfig
     * @param parser
     * @return
     */
    private BizProcessNodeInstanceEntity getProcessNodeInstance(String entityId, ProcessNodeConfig processNodeConfig, ProcessDefinitionJsonParser parser) {
        String processNodeId = processNodeConfig.getId();
        BizProcessNodeInstanceEntity nodeInstanceEntity = processNodeInstanceService.getByIdAndEntityId(processNodeId, entityId);
        return nodeInstanceEntity;
    }

    /**
     * 更新事项实例
     *
     * @param uuid
     * @param dyFormData
     */
    @Override
    @Transactional
    public void updateByUUid(String uuid, DyFormData dyFormData) {
        dyFormFacade.saveFormData(dyFormData);

        BizProcessItemInstanceEntity itemInstanceEntity = this.getOne(uuid);
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());
        String itemId = StringUtils.split(itemInstanceEntity.getItemId(), Separator.SEMICOLON.getValue())[0];
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
        // 同步表单字段映射数据
        syncFormDataMapping(itemInstanceEntity, dyFormData, processItemConfig);
        this.dao.save(itemInstanceEntity);
    }

    /**
     * 完成事项办件
     *
     * @param uuid
     */
    @Override
    @Transactional
    public void completeByUuid(String uuid) {
        Assert.hasText(uuid, "事项实例UUID不能为空！");

        BizProcessItemInstanceEntity itemInstanceEntity = this.getOne(uuid);
        itemInstanceEntity.setEndTime(Calendar.getInstance().getTime());
        itemInstanceEntity.setState(EnumBizProcessItemState.Completed.getValue());
        this.dao.save(itemInstanceEntity);
    }

    /**
     * @param uuid
     * @return
     */
    @Override
    public WorkTimePeriod getWorkTimePeriodByUuid(String uuid) {
        Assert.hasText(uuid, "事项实例UUID不能为空！");

        BizProcessItemInstanceEntity itemInstanceEntity = this.getOne(uuid);
        String timerUuid = itemInstanceEntity.getTimerUuid();
        TsTimerDto timerDto = null;
        String workTimePlanUuid = null;
        if (StringUtils.isNotBlank(timerUuid)) {
            timerDto = timerFacadeService.getTimer(timerUuid);
            workTimePlanUuid = timerDto.getWorkTimePlanUuid();
        }
        Date startTime = itemInstanceEntity.getStartTime();
        Date endTime = itemInstanceEntity.getEndTime();
        if (endTime == null) {
            endTime = Calendar.getInstance().getTime();
        }

        WorkTimePeriod workTimePeriod = workTimePlanFacadeService.getWorkTimePeriod(workTimePlanUuid, startTime, endTime);
        return workTimePeriod;
    }

    /**
     * @param uuid
     * @return
     */
    @Override
    public TimerWorkTime getTimerWorkTimeByUuid(String uuid) {
        Assert.hasText(uuid, "事项实例UUID不能为空！");

        BizProcessItemInstanceEntity itemInstanceEntity = this.getOne(uuid);
        String timerUuid = itemInstanceEntity.getTimerUuid();
        return timerFacadeService.getTimerWorkTime(timerUuid);
    }

    /**
     * 根据过程节点实例UUID获取业务事项实例列表
     *
     * @param processNodeInstUuid
     * @return
     */
    @Override
    public List<BizProcessItemInstanceEntity> listByProcessNodeInstUuid(String processNodeInstUuid) {
        Assert.hasText(processNodeInstUuid, "过程节点实例UUID不能为空！");

        BizProcessItemInstanceEntity entity = new BizProcessItemInstanceEntity();
        entity.setProcessNodeInstUuid(processNodeInstUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * 根据所属事项实例UUID获取业务事项实例列表
     *
     * @param belongItemInstUuid
     * @return
     */
    @Override
    public List<BizProcessItemInstanceEntity> listByBelongItemInstUuid(String belongItemInstUuid) {
        Assert.hasText(belongItemInstUuid, "所属事项实例UUID不能为空！");

        BizProcessItemInstanceEntity entity = new BizProcessItemInstanceEntity();
        entity.setBelongItemInstUuid(belongItemInstUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * 根据上级事项实例UUID获取业务事项实例列表
     *
     * @param parentItemInstUuid
     * @return
     */
    @Override
    public List<BizProcessItemInstanceEntity> listByParentItemInstUuid(String parentItemInstUuid) {
        Assert.hasText(parentItemInstUuid, "上级事项实例UUID不能为空！");

        BizProcessItemInstanceEntity entity = new BizProcessItemInstanceEntity();
        entity.setParentItemInstUuid(parentItemInstUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * 根据业务流程实例UUID列表获取业务事项实例列表
     *
     * @param processInstUuids
     * @return
     */
    @Override
    public List<BizProcessItemInstanceEntity> listByProcessInstUuids(List<String> processInstUuids) {
        Assert.notEmpty(processInstUuids, "业务流程实例UUID列表不能为空！");

        return this.dao.listByFieldInValues("processInstUuid", processInstUuids);
    }

    /**
     * 同步表单字段映射数据
     *
     * @param itemInstanceEntity
     * @param dyFormData
     * @param processItemConfig
     */
    private void syncFormDataMapping(BizProcessItemInstanceEntity itemInstanceEntity, DyFormData dyFormData, ProcessItemConfig processItemConfig) {
        ProcessItemConfig.ProcessItemFormConfig formConfig = processItemConfig.getFormConfig();
        if (StringUtils.isBlank(formConfig.getEntityNameField())) {
            throw new BusinessException("事项[" + processItemConfig.getItemName() + "]的办理单业务主体名称字段没有配置！");
        }
        if (StringUtils.isBlank(formConfig.getEntityIdField())) {
            throw new BusinessException("事项[" + processItemConfig.getItemName() + "]的办理单业务主体ID字段没有配置！");
        }
        String entityName = Objects.toString(dyFormData.getFieldValue(formConfig.getEntityNameField()), StringUtils.EMPTY);
        String entityId = Objects.toString(dyFormData.getFieldValue(formConfig.getEntityIdField()), StringUtils.EMPTY);
        Integer timeLimit = parseTimeLimit(dyFormData, processItemConfig);
        itemInstanceEntity.setEntityName(entityName);
        itemInstanceEntity.setEntityId(entityId);
        itemInstanceEntity.setTimeLimit(timeLimit);
    }

    private Integer parseTimeLimit(DyFormData dyFormData, ProcessItemConfig processItemConfig) {
        Integer timeLimit = null;
        if (StringUtils.isBlank(processItemConfig.getFormConfig().getTimeLimitField())) {
            return null;
        }
        Object timeLimitObject = dyFormData.getFieldValue(processItemConfig.getFormConfig().getTimeLimitField());
        if (timeLimitObject != null && StringUtils.isNotBlank(Objects.toString(timeLimitObject, StringUtils.EMPTY))) {
            try {
                timeLimit = Integer.valueOf(timeLimitObject.toString());
            } catch (Exception e) {
                throw new BusinessException(String.format("办理时限[%s]解析错误，请输入有效的办理时限！", timeLimitObject.toString()));
            }
        }
        return timeLimit;
    }


    /**
     * 更新计时器数据
     *
     * @param itemInstanceEntity
     * @param tsTimerDto
     */
    @Override
    @Transactional
    public void updateTimerData(BizProcessItemInstanceEntity itemInstanceEntity, TsTimerDto tsTimerDto) {
        String timerUuid = tsTimerDto.getUuid();
        Integer timerState = tsTimerDto.getStatus();
        Integer timingState = tsTimerDto.getTimingState();
        Date dueTime = tsTimerDto.getDueTime();
        Double usedTimeLimit = tsTimerDto.getInitTimeLimit() - tsTimerDto.getTimeLimit();

        itemInstanceEntity.setTimerUuid(timerUuid);
        itemInstanceEntity.setTimerState(timerState);
        itemInstanceEntity.setTimingState(timingState);
        itemInstanceEntity.setDueTime(dueTime);
        itemInstanceEntity.setTotalTime(usedTimeLimit);

        this.dao.save(itemInstanceEntity);
    }

    /**
     * 保存子事项实例为草稿
     *
     * @param parentItemInstanceEntity
     * @param dispenseEntity
     * @param dyFormData
     * @return
     */
//    @Override
//    @Transactional
//    public String saveSubItemAsDraft(BizProcessItemInstanceEntity parentItemInstanceEntity,
//                                     BizProcessItemInstanceDispenseEntity dispenseEntity, DyFormData dyFormData,
//                                     ProcessItemConfig.DispenseFormConfig dispenseFormConfig) {
//        String processDefUuid = parentItemInstanceEntity.getProcessDefUuid();
//        String itemCode = dispenseEntity.getItemCode();
//        List<ItemData> itemDataList = bizItemDefinitionService.listItemDataByProcessDefUuidAndItemCode(parentItemInstanceEntity.getProcessDefUuid(), itemCode);
//        if (CollectionUtils.isEmpty(itemDataList)) {
//            throw new BusinessException(String.format("业务流程定义[%s]下不存在事项编码为[%s]的事项，无法分发子事项！", processDefUuid, itemCode));
//        }
//        if (CollectionUtils.size(itemDataList) > 1) {
//            throw new BusinessException(String.format("业务流程定义[%s]下存在多个事项编码为[%s]的事项，无法分发子事项！", processDefUuid, itemCode));
//        }
//        ItemData itemData = itemDataList.get(0);
//
//        String dispenseFormType = ProcessItemConfig.DispenseFormConfig.TYPE_USE_PARENT_ITEM_FORM;
//        if (dispenseFormConfig != null) {
//            dispenseFormType = dispenseFormConfig.getType();
//        }
//        boolean useParentItemForm = ProcessItemConfig.DispenseFormConfig.TYPE_USE_PARENT_ITEM_FORM.equalsIgnoreCase(dispenseFormType);
//
//        String itemDefName = itemData.getItemDefName();
//        String itemDefId = itemData.getItemDefId();
//        String itemName = itemData.getItemName();
//        // String itemCode = itemData.getItemCode();
//        String processItemId = parentItemInstanceEntity.getItemId() + Separator.SLASH.getValue() + itemDefId + Separator.COLON.getValue() + itemCode;
//        String itemType = itemData.getItemType();
//        String title = ProcessTitleUtils.generateItemInstanceTitle(itemName);
//        String entityName = useParentItemForm ? parentItemInstanceEntity.getEntityName() : Objects.toString(dyFormData.getFieldValue(dispenseFormConfig.getEntityNameField()), StringUtils.EMPTY);
//        String entityId = useParentItemForm ? parentItemInstanceEntity.getEntityId() : Objects.toString(dyFormData.getFieldValue(dispenseFormConfig.getEntityIdField()), StringUtils.EMPTY);
//        String formUuid = dyFormData.getFormUuid();
//        String dataUuid = dyFormData.getDataUuid();
//        Integer timeLimitType = parentItemInstanceEntity.getTimeLimitType();
//        // Integer timeLimit = parseTimeLimit(dyFormData, processItemConfig);
//        Double totalTime = null;
//        Boolean milestone = parentItemInstanceEntity.getMilestone();
//        Boolean dispenseItem = parentItemInstanceEntity.getDispenseItem();
//        String state = EnumBizProcessItemState.Created.getValue();
//        String timerUuid = null;
//        Integer timerState = 0;
//        Integer timingState = 0;
//        Integer overDueState = 0;
//        Date dueTime = null;
//        String parentItemInstUuid = parentItemInstanceEntity.getUuid();
//        String belongItemInstUuid = null;
//        String itemDefUuid = itemData.getItemDefUuid();
//        String processNodeInstUuid = parentItemInstanceEntity.getProcessNodeInstUuid();
//        String processInstUuid = parentItemInstanceEntity.getProcessInstUuid();
//        // String processDefUuid = parentItemInstanceEntity.getProcessDefUuid();
//        String processDefId = parentItemInstanceEntity.getProcessDefId();
//
//        BizProcessItemInstanceEntity itemInstanceEntity = new BizProcessItemInstanceEntity();
//        itemInstanceEntity.setItemDefName(itemDefName);
//        itemInstanceEntity.setItemDefId(itemDefId);
//        itemInstanceEntity.setItemName(itemName);
//        itemInstanceEntity.setItemCode(itemCode);
//        itemInstanceEntity.setItemId(processItemId);
//        itemInstanceEntity.setItemType(itemType);
//        itemInstanceEntity.setTitle(title);
//        itemInstanceEntity.setEntityName(entityName);
//        itemInstanceEntity.setEntityId(entityId);
//        itemInstanceEntity.setFormUuid(formUuid);
//        itemInstanceEntity.setDataUuid(dataUuid);
//        itemInstanceEntity.setTimeLimitType(timeLimitType);
//        // itemInstanceEntity.setTimeLimit(timeLimit);
//        itemInstanceEntity.setTotalTime(totalTime);
//        itemInstanceEntity.setMilestone(milestone);
//        itemInstanceEntity.setDispenseItem(dispenseItem);
//        itemInstanceEntity.setState(state);
//        itemInstanceEntity.setTimerUuid(timerUuid);
//        itemInstanceEntity.setTimerState(timerState);
//        itemInstanceEntity.setTimingState(timingState);
//        itemInstanceEntity.setDueTime(dueTime);
//        itemInstanceEntity.setParentItemInstUuid(parentItemInstUuid);
//        itemInstanceEntity.setBelongItemInstUuid(belongItemInstUuid);
//        itemInstanceEntity.setItemDefUuid(itemDefUuid);
//        itemInstanceEntity.setProcessNodeInstUuid(processNodeInstUuid);
//        itemInstanceEntity.setProcessInstUuid(processInstUuid);
//        itemInstanceEntity.setProcessDefUuid(processDefUuid);
//        itemInstanceEntity.setProcessDefId(processDefId);
//
//        this.dao.save(itemInstanceEntity);
//        return itemInstanceEntity.getUuid();
//    }

    /**
     * 根据事项编号、业务流程实例UUID，判断是否完成里程碑
     *
     * @param itemCode
     * @param processInstUuid
     * @return
     */
    @Override
    public boolean isCompleteMilestone(String itemCode, String processInstUuid) {
        Assert.hasText(itemCode, "事项编码不能为空！");
        Assert.hasText(processInstUuid, "业务流程实例UUID不能为空！");

        BizProcessItemInstanceEntity entity = new BizProcessItemInstanceEntity();
        entity.setItemCode(itemCode);
        entity.setProcessInstUuid(processInstUuid);
        // 事项完成则完成里程碑
        entity.setState(EnumBizProcessItemState.Completed.getValue());
        return this.dao.countByEntity(entity) > 0;
    }

    /**
     * 根据计时器UUID获取业务事项实例
     *
     * @param timerUuid
     * @return
     */
    @Override
    public BizProcessItemInstanceEntity getByTimerUuid(String timerUuid) {
        Assert.hasText(timerUuid, "计时器UUID不能为空！");

        BizProcessItemInstanceEntity entity = new BizProcessItemInstanceEntity();
        entity.setTimerUuid(timerUuid);
        List<BizProcessItemInstanceEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据业务流程定义ID、事项ID列表获取相应的事项办理状态
     *
     * @param processDefId
     * @param itemIds
     * @return
     */
    @Override
    public Map<String, String> listItemStatesByProcessDefIdAndItemIds(String processDefId, List<String> itemIds) {
        Assert.hasText(processDefId, "业务流程定义ID不能为空！");
        Assert.notEmpty(itemIds, "业务事项ID列表不能为空！");

        String hql = "select t.itemId as itemId, t.state as state from BizProcessItemInstanceEntity t where t.processDefId = :processDefId and t.itemId in(:itemIds) order by t.createTime asc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("processDefId", processDefId);
        values.put("itemIds", itemIds);
        List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, values, new PagingInfo(1, Integer.MAX_VALUE));

        Map<String, String> stateMap = Maps.newHashMap();
        queryItems.forEach(queryItem -> stateMap.put(queryItem.getString("itemId"), queryItem.getString("state")));
        return stateMap;
    }

    /**
     * 根据事项ID列表、业务流程实例UUID，判断对应的事项是否都完成
     *
     * @param itemIds
     * @param processInstUuid
     * @return
     */
    @Override
    public boolean isCompleteByItemIdsAndProcessInstUuid(List<String> itemIds, String processInstUuid) {
        if (CollectionUtils.isEmpty(itemIds)) {
            return true;
        }

        Assert.hasText(processInstUuid, "业务流程实例UUID不能为空！");

        String hql = "select t.itemId as itemId from BizProcessItemInstanceEntity t where t.processInstUuid = :processInstUuid and t.itemId in(:itemIds) and t.state = :state";
        Map<String, Object> values = Maps.newHashMap();
        values.put("processInstUuid", processInstUuid);
        values.put("itemIds", itemIds);
        values.put("state", EnumBizProcessItemState.Completed.getValue());
        List<String> completedItemIds = this.dao.listCharSequenceByHQL(hql, values);

        List<String> checkItemIds = Lists.newArrayList(itemIds);
        checkItemIds.removeAll(completedItemIds);
        return CollectionUtils.isEmpty(checkItemIds);
    }

    /**
     * 根据事项ID、业务主体ID，获取最新的业务事项实例
     *
     * @param itemId
     * @param entityId
     * @return
     */
    @Override
    public BizProcessItemInstanceEntity getByItemIdAndEntityId(String itemId, String entityId) {
        Assert.hasText(itemId, "过程节点ID不能为空！");
        Assert.hasText(entityId, "业务主体ID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("itemId", itemId);
        params.put("entityId", entityId);
        String hql = "from BizProcessItemInstanceEntity t where t.itemId = :itemId and t.entityId = :entityId order by t.createTime desc";
        List<BizProcessItemInstanceEntity> entities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 变更业务事项办件状态
     *
     * @param event
     * @param stateConfig
     * @param processItemFormConfig
     * @param processItemConfig
     * @param parser
     */
    @Override
    @Transactional
    public void changeItemInstanceState(Event event, StateDefinition.StateConfig stateConfig,
                                        ProcessItemConfig.ProcessItemFormConfig processItemFormConfig,
                                        ProcessItemConfig processItemConfig, ProcessDefinitionJsonParser parser) {
        // 设置表单状态数据
        DyFormData dyFormData = getProcessItemInstanceDyformData(processItemConfig, event);
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
                StateManagerHelper.addFormStateHistory(EnumStateHistoryType.Item, dyFormData, oldStateName, stateName,
                        stateConfig.getStateNameField(), event, stateConfig, parser, historyEntities);
                logger.info("change business entity state name {}:{}", new Object[]{stateConfig.getStateNameField(), stateName});
            }
            if (stateCodeChanged) {
                StateManagerHelper.addFormStateHistory(EnumStateHistoryType.Item, dyFormData, oldStateCode, stateCode,
                        stateConfig.getStateCodeField(), event, stateConfig, parser, historyEntities);
                logger.info("change business entity state name {}:{}", new Object[]{stateConfig.getStateCodeField(), stateCode});
            }

            // 发布状态变更事件
            publishProcessItemInstanceDyformStateChangedEvent(dyFormData, stateName, stateCode, stateConfig, parser, event);

            // 保存状态变更历史
            if (CollectionUtils.isNotEmpty(historyEntities)) {
                // 设置事项实例UUID
                historyEntities.forEach(stateHistoryEntity -> {
                    String handleItemInstUuid = Objects.toString(event.getExtraData().get("handleItemInstUuid"), StringUtils.EMPTY);
                    if (StringUtils.isNotBlank(handleItemInstUuid)) {
                        stateHistoryEntity.setItemInstUuid(handleItemInstUuid);
                    }
                });
                formStateHistoryService.saveAll(historyEntities);
            }
        }
    }


    /**
     * 获取业务事项办件表单数据
     *
     * @param processItemConfig
     * @param event
     * @return
     */
    private DyFormData getProcessItemInstanceDyformData(ProcessItemConfig processItemConfig, Event event) {
        DyFormData dyFormData = null;
        // 当前事项
        if (event instanceof ProcessItemEvent && StringUtils.equals(processItemConfig.getId(), ((ProcessItemEvent) event).getItemId())) {
            dyFormData = event.getDyFormData();
            if (dyFormData != null) {
                dyFormData = dyFormFacade.getDyFormData(event.getFormUuid(), event.getDataUuid());
            }
            event.getExtraData().put("handleItemInstUuid", ((ProcessItemEvent) event).getItemInstUuid());
        } else {
            // 其他事项，取最新的事项办件
            BizProcessItemInstanceEntity processItemInstanceEntity = this.getByItemIdAndEntityId(processItemConfig.getId(), event.getEntityId());
            if (processItemInstanceEntity != null) {
                dyFormData = dyFormFacade.getDyFormData(processItemInstanceEntity.getFormUuid(), processItemInstanceEntity.getDataUuid());
                event.getExtraData().put("handleItemInstUuid", processItemInstanceEntity.getUuid());
            }
        }
        return dyFormData;
    }

    /**
     * 发布业务事项办件表单数据状态变更事件
     *
     * @param dyFormData
     * @param stateName
     * @param stateCode
     * @param stateConfig
     * @param parser
     * @param event
     */
    private void publishProcessItemInstanceDyformStateChangedEvent(DyFormData dyFormData, String stateName, String stateCode,
                                                                   StateDefinition.StateConfig stateConfig,
                                                                   ProcessDefinitionJsonParser parser, Event event) {
        ApplicationContextHolder.getApplicationContext().publishEvent(new BizProcessItemInstanceDyformStateChangedEvent(event, dyFormData,
                stateConfig.getStateNameField(), stateName, stateConfig.getStateCodeField(), stateCode));
    }

}

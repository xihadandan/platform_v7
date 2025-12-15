/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.biz.dao.BizProcessInstanceDao;
import com.wellsoft.pt.biz.entity.BizFormStateHistoryEntity;
import com.wellsoft.pt.biz.entity.BizProcessEntityTimerEntity;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumBizProcessNodeState;
import com.wellsoft.pt.biz.enums.EnumBizProcessState;
import com.wellsoft.pt.biz.enums.EnumStateHistoryType;
import com.wellsoft.pt.biz.listener.BizEventListenerPublisher;
import com.wellsoft.pt.biz.listener.BizProcessEntityStateTimerListener;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.service.BizFormStateHistoryService;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.state.BizStateChangedPublisher;
import com.wellsoft.pt.biz.state.event.BizEntityStateChangedEvent;
import com.wellsoft.pt.biz.state.event.BizProcessInstanceDyformStateChangedEvent;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.biz.utils.ProcessTitleUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.timer.dto.TsTimerConfigDto;
import com.wellsoft.pt.timer.dto.TsTimerDto;
import com.wellsoft.pt.timer.enums.EnumTimeLimitType;
import com.wellsoft.pt.timer.enums.EnumTimerStatus;
import com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.timer.support.TsTimerParam;
import com.wellsoft.pt.timer.support.TsTimerParamBuilder;
import com.wellsoft.pt.timer.support.WorkTimePeriod;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.ParseException;
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
public class BizProcessInstanceServiceImpl extends AbstractJpaServiceImpl<BizProcessInstanceEntity, BizProcessInstanceDao, String> implements BizProcessInstanceService {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BizEventListenerPublisher eventListenerPublisher;

    @Autowired
    private BizStateChangedPublisher stateChangedPublisher;

    @Autowired
    private TsTimerConfigFacadeService timerConfigFacadeService;

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    @Autowired
    private TsTimerFacadeService timerFacadeService;

    @Autowired
    private BizProcessEntityTimerServiceImpl processEntityTimerService;

    @Autowired
    private BizFormStateHistoryService formStateHistoryService;

    /**
     * 创建业务流程实例
     *
     * @param entityName
     * @param entityId
     * @param parser
     * @return
     */
    @Override
    public BizProcessInstanceEntity create(String entityName, String entityId, ProcessDefinitionJsonParser parser) {
        ProcessDefinitionJson.ProcessFormConfig processFormConfig = parser.getProcessFormConfig();

        String name = parser.getProcessDefName();// processDefinitionJson.getName();
        String id = parser.getProcessDefId();//processDefinitionJson.getId();
        String title = ProcessTitleUtils.generateProcessInstanceTitle(name);
        String formUuid = processFormConfig.getFormUuid(); //processDefinitionJson.getFormUuid();
        String dataUuid = null;

        Map<String, Object> params = Maps.newHashMap();
        params.put(processFormConfig.getEntityIdField(), entityId);
        // 根据业务主体ID查询对应的办理表单数据UUID
        List<String> dataUuids = dyFormFacade.queryUniqueForFields(formUuid, params, null);
        if (CollectionUtils.isNotEmpty(dataUuids)) {
            dataUuid = dataUuids.get(0);
        } else {
            // 创建新的表单数据
            DyFormData dyFormData = dyFormFacade.createDyformData(formUuid);
            setProcessMappingFieldValues(entityName, entityId, dyFormData, processFormConfig);
            dataUuid = dyFormFacade.saveFormData(dyFormData);
        }
        Date startTime = null;//Calendar.getInstance().getTime();
        Date endTime = null;
        String state = EnumBizProcessState.Created.getValue();
        String processDefUuid = parser.getProcessDefUuid();
        String entityFormId = dyFormFacade.getFormIdByFormUuid(parser.getProcessEntityConfig().getFormUuid());

        BizProcessInstanceEntity entity = new BizProcessInstanceEntity();
        entity.setName(name);
        entity.setId(id);
        entity.setTitle(title);
        entity.setEntityName(entityName);
        entity.setEntityId(entityId);
        entity.setEntityFormId(entityFormId);
        entity.setFormUuid(formUuid);
        entity.setDataUuid(dataUuid);
        entity.setStartTime(startTime);
        entity.setEndTime(endTime);
        entity.setState(state);
        entity.setProcessDefUuid(processDefUuid);
        this.save(entity);

        // 发布业务流程创建事件监听
        eventListenerPublisher.publishProcessCreated(entity, parser);

        // 发布业务流程状态变更
        stateChangedPublisher.publishProcessStateChanged(EnumBizProcessState.Created, null, entity);
        return entity;
    }

    /**
     * 根据业务流程实例UUID，启动业务流程
     *
     * @param uuid
     */
    @Override
    @Transactional
    public void startByUuid(String uuid) {
        BizProcessInstanceEntity processInstanceEntity = this.getOne(uuid);
        String oldState = processInstanceEntity.getState();
        if (StringUtils.equals(EnumBizProcessState.Created.getValue(), oldState)) {
            processInstanceEntity.setStartTime(Calendar.getInstance().getTime());
            processInstanceEntity.setState(EnumBizProcessNodeState.Running.getValue());
            this.save(processInstanceEntity);

            ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processInstanceEntity.getProcessDefUuid());
            // 发布业务流程开始事件监听
            eventListenerPublisher.publishProcessStarted(processInstanceEntity, parser);

            // 发布业务流程状态变更
            stateChangedPublisher.publishProcessStateChanged(EnumBizProcessState.Running, EnumBizProcessState.Created, processInstanceEntity);
        }
    }

    /**
     * @param entityName
     * @param entityId
     * @param dyFormData
     * @param processFormConfig
     */
    private void setProcessMappingFieldValues(String entityName, String entityId, DyFormData dyFormData,
                                              ProcessDefinitionJson.ProcessFormConfig processFormConfig) {
        String entityNameField = processFormConfig.getEntityNameField();
        String entityIdField = processFormConfig.getEntityIdField();
        if (dyFormData.isFieldExist(entityNameField)) {
            dyFormData.setFieldValue(entityNameField, entityName);
        }
        if (dyFormData.isFieldExist(entityIdField)) {
            dyFormData.setFieldValue(entityIdField, entityId);
        }
    }

    /**
     * 根据业务流程定义UUID获取业务流程实例数量
     *
     * @param processDefUuids
     * @return
     */
    @Override
    public Long countByProcessDefUuids(List<String> processDefUuids) {
        String hql = "from BizProcessInstanceEntity t where t.processDefUuid in(:processDefUuids)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("processDefUuids", processDefUuids);
        return this.dao.countByHQL(hql, values);
    }

    /**
     * 根据业务流程ID及业务主体ID获取业务流程实例
     *
     * @param id
     * @param entityId
     * @return
     */
    @Override
    public BizProcessInstanceEntity getByIdAndEntityId(String id, String entityId) {
        Assert.hasText(id, "过程节点ID不能为空！");
        Assert.hasText(entityId, "业务主体ID不能为空！");

        BizProcessInstanceEntity entity = new BizProcessInstanceEntity();
        entity.setId(id);
        entity.setEntityId(entityId);
        List<BizProcessInstanceEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 完成业务流程
     *
     * @param processInstUuid
     */
    @Override
    @Transactional
    public void completeByUuid(String processInstUuid) {
        Assert.hasText(processInstUuid, "业务流程实例UUID不能为空！");

        BizProcessInstanceEntity processInstanceEntity = this.dao.getOne(processInstUuid);
        processInstanceEntity.setState(EnumBizProcessState.Completed.getValue());
        processInstanceEntity.setEndTime(Calendar.getInstance().getTime());

        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processInstanceEntity.getProcessDefUuid());
        // 发布业务流程完成事件监听
        eventListenerPublisher.publishProcessCompleted(processInstanceEntity, parser);

        // 发布业务流程状态变更
        stateChangedPublisher.publishProcessStateChanged(EnumBizProcessState.Completed, EnumBizProcessState.Running, processInstanceEntity);

        this.dao.save(processInstanceEntity);
    }

    /**
     * 重新开始业务流程
     *
     * @param processInstUuid
     */
    @Override
    public void restartByUuid(String processInstUuid) {
        Assert.hasText(processInstUuid, "业务流程实例UUID不能为空！");

        BizProcessInstanceEntity processInstanceEntity = this.dao.getOne(processInstUuid);
        processInstanceEntity.setState(EnumBizProcessState.Running.getValue());
        processInstanceEntity.setEndTime(null);
        this.dao.save(processInstanceEntity);
    }

    /**
     * @param processInstUuid
     * @return
     */
    @Override
    public WorkTimePeriod getWorkTimePeriodByUuid(String processInstUuid) {
        Assert.hasText(processInstUuid, "业务流程实例UUID不能为空！");

        BizProcessInstanceEntity processInstanceEntity = this.dao.getOne(processInstUuid);
        Date startTime = processInstanceEntity.getStartTime();
        Date endTime = processInstanceEntity.getEndTime();
        if (endTime == null) {
            endTime = Calendar.getInstance().getTime();
        }

        WorkTimePeriod workTimePeriod = workTimePlanFacadeService.getWorkTimePeriod(null, startTime, endTime);
        return workTimePeriod;
    }

    /**
     * 变更业务主体状态
     *
     * @param event
     * @param stateConfig
     * @param parser
     */
    @Override
    @Transactional
    public void changeProcessEntityState(Event event, StateDefinition.StateConfig stateConfig, ProcessDefinitionJsonParser parser) {
        // 设置表单状态数据
        DyFormData entityDyformData = getEntityDyformData(parser.getProcessEntityConfig(), event.getEntityId());
        if (entityDyformData == null) {
            logger.error("business entity form data is null");
            return;
        }

        // 状态名称
        String stateName = StateManagerHelper.renderStateNameValue(event, stateConfig);
        // 状态代码
        String stateCode = StateManagerHelper.renderStateCodeValue(event, stateConfig);

        String oldStateName = StateManagerHelper.getStateValue(entityDyformData, stateConfig.getStateNameField());
        String oldStateCode = StateManagerHelper.getStateValue(entityDyformData, stateConfig.getStateCodeField());
        boolean stateNameChanged = StateManagerHelper.setStateValue(entityDyformData, stateConfig.getStateNameField(), stateName, event);
        boolean stateCodeChanged = StateManagerHelper.setStateValue(entityDyformData, stateConfig.getStateCodeField(), stateCode, event);

        // 保存变更值
        if (stateNameChanged || stateCodeChanged) {
            dyFormFacade.saveFormData(entityDyformData);

            List<BizFormStateHistoryEntity> historyEntities = Lists.newArrayList();
            if (stateNameChanged) {
                StateManagerHelper.addFormStateHistory(EnumStateHistoryType.Entity, entityDyformData, oldStateName, stateName,
                        stateConfig.getStateNameField(), event, stateConfig, parser, historyEntities);
                logger.info("change business entity state name {}:{}", new Object[]{stateConfig.getStateNameField(), stateName});
            }
            if (stateCodeChanged) {
                StateManagerHelper.addFormStateHistory(EnumStateHistoryType.Entity, entityDyformData, oldStateCode, stateCode,
                        stateConfig.getStateCodeField(), event, stateConfig, parser, historyEntities);
                logger.info("change business entity state name {}:{}", new Object[]{stateConfig.getStateCodeField(), stateCode});
            }

            // 状态计时
            startProcessEntityStateTimerIfRequired(oldStateCode, stateCode, stateConfig, entityDyformData, parser, event);

            // 发布状态变更事件
            publishBusinessEntityStateChangedEvent(entityDyformData, stateName, stateCode, stateConfig, parser, event);

            // 保存状态变更历史
            if (CollectionUtils.isNotEmpty(historyEntities)) {
                formStateHistoryService.saveAll(historyEntities);
            }
        }
    }

    /**
     * @param oldStateCode
     * @param newStateCode
     * @param stateConfig
     * @param entityDyformData
     * @param parser
     * @param event
     */
    private void startProcessEntityStateTimerIfRequired(String oldStateCode, String newStateCode, StateDefinition.StateConfig stateConfig, DyFormData entityDyformData,
                                                        ProcessDefinitionJsonParser parser, Event event) {
        ProcessDefinitionJson.ProcessEntityConfig entityConfig = parser.getProcessEntityConfig();
        List<ProcessDefinitionJson.StateTimerConfig> timerConfigs = entityConfig.getTimers();
        if (CollectionUtils.isEmpty(timerConfigs)) {
            return;
        }

        // 状态变更暂停或结束计时
        if (!StringUtils.equals(oldStateCode, newStateCode)) {
            timerConfigs.stream().filter(timerConfig -> {
                String stateCodeField = timerConfig.getStateCodeField();
                if (StringUtils.isNotBlank(oldStateCode) && StringUtils.equals(stateConfig.getStateCodeField(), stateCodeField)) {
                    return !timerConfig.getStateCodes().contains(newStateCode);
                }
                return false;
            }).forEach(timerConfig -> {
                pauseOrStopProcessEntityStateTimer(oldStateCode, newStateCode, timerConfig, entityDyformData, parser, event);
            });
        }

        timerConfigs.stream().filter(timerConfig -> {
            String stateCodeField = timerConfig.getStateCodeField();
            if (StringUtils.equals(stateConfig.getStateCodeField(), stateCodeField)) {
                return timerConfig.getStateCodes().contains(newStateCode);
            }
            return false;
        }).forEach(timerConfig -> {
            startProcessEntityStateTimer(oldStateCode, newStateCode, timerConfig, entityDyformData, parser, event);
        });
    }

    /**
     * @param oldStateCode
     * @param newStateCode
     * @param timerConfig
     * @param entityDyformData
     * @param parser
     * @param event
     */
    private void pauseOrStopProcessEntityStateTimer(String oldStateCode, String newStateCode, ProcessDefinitionJson.StateTimerConfig timerConfig, DyFormData entityDyformData, ProcessDefinitionJsonParser parser, Event event) {
        // 获取已经存在的计时器信息
        BizProcessEntityTimerEntity timerEntity = processEntityTimerService.getNormalByEntityIdAndState(event.getEntityId(),
                timerConfig.getStateCodeField(), oldStateCode, timerConfig.getId(), event.getProcessInstUuid());
        if (timerEntity != null) {
            if (timerConfig.isStopTimerWhileStateChanged()) {
                timerFacadeService.stopTimer(timerEntity.getTimerUuid());
            } else if (!Integer.valueOf(EnumTimerStatus.PASUE.getValue()).equals(timerEntity.getTimerState())) {
                timerFacadeService.pauseTimer(timerEntity.getTimerUuid());
            }

            timerEntity.setStateCode(newStateCode);
            // 更新计时器信息
            processEntityTimerService.updateTimerData(timerEntity, timerFacadeService.getTimer(timerEntity.getTimerUuid()));
        }
    }

    /**
     * @param oldStateCode
     * @param newStateCode
     * @param timerConfig
     * @param entityDyformData
     * @param parser
     * @param event
     */
    private void startProcessEntityStateTimer(String oldStateCode, String newStateCode, ProcessDefinitionJson.StateTimerConfig timerConfig,
                                              DyFormData entityDyformData, ProcessDefinitionJsonParser parser, Event event) {
        // 已经在计时状态中，更新状态代码
        if (StringUtils.isNotBlank(oldStateCode) && timerConfig.getStateCodes().contains(oldStateCode)
                && timerConfig.getStateCodes().contains(newStateCode)) {
            if (!StringUtils.equals(oldStateCode, newStateCode)) {
                BizProcessEntityTimerEntity timerEntity = processEntityTimerService.getNormalByEntityIdAndState(event.getEntityId(),
                        timerConfig.getStateCodeField(), oldStateCode, timerConfig.getId(), event.getProcessInstUuid());
                if (timerEntity != null) {
                    timerEntity.setStateCode(newStateCode);
                    processEntityTimerService.save(timerEntity);
                }
            }
            return;
        }

        String timerConfigId = timerConfig.getTimerConfigId();
        String workTimePlanId = timerConfig.getWorkTimePlanId();
        TsTimerConfigDto timerConfigDto = timerConfigFacadeService.getDtoById(timerConfigId);

        String timeLimitType = timerConfigDto.getTimeLimitType();
        String timerConfigUuid = timerConfigDto.getUuid();
        boolean isDateOfLimitTime = StringUtils.equals(timeLimitType, EnumTimeLimitType.DATE.getValue())
                || StringUtils.equals(timeLimitType, EnumTimeLimitType.CUSTOM_DATE.getValue());
        String workTimePlanUuid = workTimePlanFacadeService.getActiveWorkTimePlanUuidById(workTimePlanId, null);
        Double limitTime = null;
        Date dueTime = null;

        // 动态时限
        try {
            if (StringUtils.equals(timeLimitType, EnumTimeLimitType.CUSTOM_NUMBER.getValue())) {
                limitTime = Double.parseDouble(Objects.toString(entityDyformData.getFieldValue(timerConfig.getTimeLimitField())));
            } else if (StringUtils.equals(timeLimitType, EnumTimeLimitType.CUSTOM_DATE.getValue())) {
                // 动态截止时间
                dueTime = DateUtils.parse(Objects.toString(entityDyformData.getFieldValue(timerConfig.getTimeLimitField())));
            }
        } catch (ParseException e) {
            throw new RuntimeException("状态计时动态时限解析失败！", e);
        }

        // 获取已经存在的计时器信息
        BizProcessEntityTimerEntity timerEntity = processEntityTimerService.getNormalByEntityIdAndState(event.getEntityId(),
                timerConfig.getStateCodeField(), newStateCode, timerConfig.getId(), event.getProcessInstUuid());
        // 生成计时器
        if (timerEntity == null) {
            Date startTime = Calendar.getInstance().getTime();
            TsTimerParamBuilder builder = TsTimerParamBuilder.create();
            builder.setTimerConfigUuid(timerConfigUuid)
                    .setWorkTimePlanUuid(workTimePlanUuid).setStartTime(startTime)
                    .setDateOfLimitTime(isDateOfLimitTime).setTimeLimit(limitTime)
                    .setDueTime(dueTime).setListener(BizProcessEntityStateTimerListener.LISTENER_BEAN_NAME);
            // 添加预警信息
            if (timerConfig.isEnableAlarmDoing()) {
                builder.addAlarm(timerConfig.getId(), timerConfig.getAlarmTimeLimit(), timerConfig.getAlarmTimingMode(), timerConfig.getAlarmCount());
            }
            TsTimerParam timerParam = builder.build();
            TsTimerDto timerDto = timerFacadeService.startTimer(timerParam);

            Double usedTimeLimit = timerDto.getInitTimeLimit() - timerDto.getTimeLimit();
            BizProcessEntityTimerEntity entity = new BizProcessEntityTimerEntity();
            entity.setName(timerConfig.getName());
            entity.setId(timerConfig.getId());
            entity.setEntityId(event.getEntityId());
            entity.setStateField(timerConfig.getStateCodeField());
            entity.setStateCode(newStateCode);
            entity.setTimerUuid(timerDto.getUuid());
            entity.setTimerState(timerDto.getStatus());
            entity.setTimingState(timerDto.getTimingState());
            entity.setDueTime(timerDto.getDueTime());
            entity.setTotalTime(usedTimeLimit);
            entity.setProcessInstUuid(event.getProcessInstUuid());
            processEntityTimerService.save(entity);
        } else {
            // 恢复计时器
            if (Integer.valueOf(EnumTimerStatus.PASUE.getValue()).equals(timerEntity.getTimerState())) {
                timerFacadeService.resumeTimer(timerEntity.getTimerUuid());

                // 更新计时器信息
                processEntityTimerService.updateTimerData(timerEntity, timerFacadeService.getTimer(timerEntity.getTimerUuid()));
            }
        }
    }

    private String queryEntityDataUuid(String formUuid, String entityIdValue, String entityIdField) {
        String dataUuid = null;
        if (JpaEntity.UUID.equals(entityIdField)) {
            dataUuid = entityIdValue;
        } else {
            Map<String, Object> params = Maps.newHashMap();
            params.put(entityIdField, entityIdValue);
            // 根据业务主体ID查询对应的表单数据UUID
            List<String> dataUuids = dyFormFacade.queryUniqueForFields(formUuid, params, null);
            if (CollectionUtils.isNotEmpty(dataUuids)) {
                dataUuid = dataUuids.get(0);
            }
        }
        return dataUuid;
    }

    /**
     * 发布业务主体状态变更事件
     *
     * @param entityDyformData
     * @param stateName
     * @param stateCode
     * @param stateConfig
     * @param parser
     * @param event
     */
    private void publishBusinessEntityStateChangedEvent(DyFormData entityDyformData, String stateName, String
            stateCode,
                                                        StateDefinition.StateConfig stateConfig,
                                                        ProcessDefinitionJsonParser parser, Event event) {
        ApplicationContextHolder.getApplicationContext().publishEvent(new BizEntityStateChangedEvent(event, entityDyformData,
                stateConfig.getStateNameField(), stateName, stateConfig.getStateCodeField(), stateCode));
    }

    /**
     * 变更业务办件状态
     *
     * @param event
     * @param stateConfig
     * @param processFormConfig
     * @param parser
     */
    @Override
    @Transactional
    public void changeProcessInstanceState(Event event, StateDefinition.StateConfig stateConfig, ProcessDefinitionJson.ProcessFormConfig processFormConfig, ProcessDefinitionJsonParser parser) {

        // 设置表单状态数据
        DyFormData dyFormData = getProcessInstanceDyformData(processFormConfig, event);
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
                StateManagerHelper.addFormStateHistory(EnumStateHistoryType.Process, dyFormData, oldStateName, stateName,
                        stateConfig.getStateNameField(), event, stateConfig, parser, historyEntities);
                logger.info("change business entity state name {}:{}", new Object[]{stateConfig.getStateNameField(), stateName});
            }
            if (stateCodeChanged) {
                StateManagerHelper.addFormStateHistory(EnumStateHistoryType.Process, dyFormData, oldStateCode, stateCode,
                        stateConfig.getStateCodeField(), event, stateConfig, parser, historyEntities);
                logger.info("change business entity state name {}:{}", new Object[]{stateConfig.getStateCodeField(), stateCode});
            }

            // 发布状态变更事件
            publishProcessInstanceDyformStateChangedEvent(dyFormData, stateName, stateCode, stateConfig, parser, event);

            // 保存状态变更历史
            if (CollectionUtils.isNotEmpty(historyEntities)) {
                formStateHistoryService.saveAll(historyEntities);
            }
        }
    }

    /**
     * 获取业务办件表单数据
     *
     * @param processFormConfig
     * @param event
     * @return
     */
    private DyFormData getProcessInstanceDyformData(ProcessDefinitionJson.ProcessFormConfig
                                                            processFormConfig, Event event) {
        String processInstUuid = event.getProcessInstUuid();
        if (StringUtils.isBlank(processInstUuid)) {
            return null;
        }

        BizProcessInstanceEntity processInstanceEntity = this.getOne(processInstUuid);
        if (processInstanceEntity == null) {
            return null;
        }

        String formUuid = processInstanceEntity.getFormUuid();
        String dataUuid = processInstanceEntity.getDataUuid();
        if (StringUtils.isBlank(formUuid) || StringUtils.isBlank(dataUuid)) {
            return null;
        }

        return dyFormFacade.getDyFormData(formUuid, dataUuid);
    }

    /**
     * 发布业务办件表单数据状态变更事件
     *
     * @param dyFormData
     * @param stateName
     * @param stateCode
     * @param stateConfig
     * @param parser
     * @param event
     */
    private void publishProcessInstanceDyformStateChangedEvent(DyFormData dyFormData, String stateName, String
            stateCode, StateDefinition.StateConfig stateConfig, ProcessDefinitionJsonParser parser, Event event) {
        ApplicationContextHolder.getApplicationContext().publishEvent(new BizProcessInstanceDyformStateChangedEvent(event, dyFormData,
                stateConfig.getStateNameField(), stateName, stateConfig.getStateCodeField(), stateCode));
    }

    /**
     * 根据表单定义UUID及业务主体ID获取办理过的业务流程定义UUID列表
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    @Override
    public List<String> listProcessDefUuidByEntityFormIdAndEntityId(String entityFormId, String entityId) {
        Assert.hasText(entityFormId, "业务主体表单定义ID不能为空！");
        Assert.hasText(entityId, "业务主体ID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("entityFormId", entityFormId);
        params.put("entityId", entityId);
        String hql = "select distinct t.processDefUuid as processDefUuid from BizProcessInstanceEntity t where t.entityFormId = :entityFormId and t.entityId = :entityId";
        List<String> processDefUuids = this.dao.listCharSequenceByHQL(hql, params);
        return processDefUuids;
    }

    /**
     * 根据表单定义ID及业务主体ID获取办理过的业务流程实例列表
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    @Override
    public List<BizProcessInstanceEntity> listByEntityFormIdAndEntityId(String entityFormId, String entityId) {
        Assert.hasText(entityFormId, "业务主体表单定义ID不能为空！");
        Assert.hasText(entityId, "业务主体ID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("entityFormId", entityFormId);
        params.put("entityId", entityId);
        String hql = "from BizProcessInstanceEntity t where t.entityFormId = :entityFormId and t.entityId = :entityId";
        return this.listByHQL(hql, params);
    }

    /**
     * 获取业务主体表单数据
     *
     * @param processEntityConfig
     * @param entityIdValue
     * @return
     */
    @Override
    public DyFormData getEntityDyformData(ProcessDefinitionJson.ProcessEntityConfig processEntityConfig, String entityIdValue) {
        String formUuid = processEntityConfig.getFormUuid();
        String dataUuid = null;
        String entityIdField = processEntityConfig.getEntityIdField();
        if (StringUtils.isBlank(formUuid) || StringUtils.isBlank(entityIdField) || StringUtils.isBlank(entityIdValue)) {
            return null;
        }

        dataUuid = queryEntityDataUuid(formUuid, entityIdValue, entityIdField);
        if (StringUtils.isBlank(dataUuid)) {
            return null;
        }

        return dyFormFacade.getDyFormData(formUuid, dataUuid);
    }

    /**
     * 获取业务主体主表数据
     *
     * @param entityFormUuid
     * @param entityIdValue
     * @param entityIdField
     * @return
     */
    @Override
    public Map<String, Object> getEntityFormDataOfMainform(String entityFormUuid, String entityIdValue, String entityIdField) {
        if (StringUtils.isBlank(entityFormUuid) || StringUtils.isBlank(entityIdField) || StringUtils.isBlank(entityIdValue)) {
            return null;
        }

        String dataUuid = queryEntityDataUuid(entityFormUuid, entityIdValue, entityIdField);
        if (StringUtils.isBlank(dataUuid)) {
            return null;
        }

        return dyFormFacade.getFormDataOfMainform(entityFormUuid, dataUuid);
    }

    @Override
    public long countByEntity(BizProcessInstanceEntity entity) {
        return this.dao.countByEntity(entity);
    }

}

/*
 * @(#)11/17/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.biz.entity.BizBusinessIntegrationEntity;
import com.wellsoft.pt.biz.entity.BizNewItemRelationEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumBizBiEventTriggerType;
import com.wellsoft.pt.biz.facade.service.BizBusinessIntegrationFacadeService;
import com.wellsoft.pt.biz.listener.bi.BizWorkflowIntegrationDirectionListener;
import com.wellsoft.pt.biz.listener.bi.BizWorkflowIntegrationFlowListener;
import com.wellsoft.pt.biz.listener.bi.BizWorkflowIntegrationTaskListener;
import com.wellsoft.pt.biz.listener.bi.BizWorkflowIntegrationTimerListener;
import com.wellsoft.pt.biz.service.BizBusinessIntegrationService;
import com.wellsoft.pt.biz.support.ItemFlowDefinition;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.WorkflowIntegrationParams;
import com.wellsoft.pt.biz.utils.BusinessIntegrationContextHolder;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.facade.service.WfFlowBusinessDefinitionFacadeService;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 11/17/22.1	zhulh		11/17/22		Create
 * </pre>
 * @date 11/17/22
 */
@Service
public class BizBusinessIntegrationFacadeServiceImpl extends AbstractApiFacade implements BizBusinessIntegrationFacadeService {

    @Autowired
    private BizBusinessIntegrationService businessIntegrationService;

    @Autowired
    private WfFlowBusinessDefinitionFacadeService flowBusinessDefinitionFacadeService;

    @Autowired
    @Qualifier("workService")
    private WorkService workService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BotFacadeService botFacadeService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    /**
     * 发起业务集成
     *
     * @param itemInstanceEntity
     * @param dyFormData
     * @param parser
     */
    @Transactional
    @Override
    public void startBusinessIntegration(BizProcessItemInstanceEntity itemInstanceEntity, DyFormData dyFormData,
                                         ProcessDefinitionJsonParser parser, Map<String, Object> extraParams) {
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemInstanceEntity.getItemId());
        List<ProcessItemConfig.BusinessIntegrationConfig> businessIntegrationConfigs = processItemConfig.getBusinessIntegrationConfigs();
        try {
            // 设置集成上下文信息
            BusinessIntegrationContextHolder.setItemInstUuid(itemInstanceEntity.getUuid());

            for (ProcessItemConfig.BusinessIntegrationConfig config : businessIntegrationConfigs) {
                String type = config.getType();
                switch (type) {
                    case "1":
                        // 发起工作流集成
                        if (config.isEnabled()) {
                            startWorkflowBusinessIntegration(itemInstanceEntity, dyFormData, config, extraParams);
                        } else {
                            logger.warn("Workflow business integration disabled, item instance uuid is " + itemInstanceEntity.getUuid());
                        }
                        break;
                }
            }
        } finally {
            BusinessIntegrationContextHolder.removeItemInstUuid();
        }
    }

    /**
     * 撤回业务集成
     *
     * @param itemInstanceEntity
     * @param parser
     * @param extraParams
     */
    @Override
    public void cancelBusinessIntegration(BizProcessItemInstanceEntity itemInstanceEntity, ProcessDefinitionJsonParser parser,
                                          Map<String, Object> extraParams) {
        List<BizBusinessIntegrationEntity> bizBusinessIntegrationEntities = businessIntegrationService.listByItemInstUuid(itemInstanceEntity.getUuid());
        for (BizBusinessIntegrationEntity entity : bizBusinessIntegrationEntities) {
            String type = entity.getType();
            switch (type) {
                case "1":
                    // 撤回工作流集成
                    cancelWorkflowBusinessIntegration(itemInstanceEntity, entity, extraParams);
                    break;
            }
        }
    }

    /**
     * 重新开始业务集成
     *
     * @param itemInstanceEntity
     * @param parser
     * @param newItemRelationEntity
     */
    @Override
    public void restartBusinessIntegration(BizProcessItemInstanceEntity itemInstanceEntity, ProcessDefinitionJsonParser parser,
                                           BizNewItemRelationEntity newItemRelationEntity) {
        List<BizBusinessIntegrationEntity> bizBusinessIntegrationEntities = businessIntegrationService.listByItemInstUuid(itemInstanceEntity.getUuid());
        for (BizBusinessIntegrationEntity entity : bizBusinessIntegrationEntities) {
            String type = entity.getType();
            switch (type) {
                case "1":
                    // 重新开始工作流集成
                    restartWorkflowBusinessIntegration(itemInstanceEntity, entity, parser, newItemRelationEntity);
                    break;
            }
        }
    }

    /**
     * 发起工作流集成
     *
     * @param itemInstanceEntity
     * @param dyFormData
     * @param config
     */
    private void startWorkflowBusinessIntegration(BizProcessItemInstanceEntity itemInstanceEntity, DyFormData dyFormData,
                                                  ProcessItemConfig.BusinessIntegrationConfig config, Map<String, Object> extraParams) {
        String itemInstUuid = itemInstanceEntity.getUuid();
        String type = config.getType();
        // 已存在业务集成实例根据附加参数，同步、提交流程数据
        BizBusinessIntegrationEntity businessIntegrationEntity = businessIntegrationService.getByTypeAndItemInstUuid(type, itemInstUuid);
        if (businessIntegrationEntity != null) {
            submitWorkflowBusinessIntegrationIfRequried(businessIntegrationEntity, itemInstanceEntity, dyFormData, config, extraParams);
            return;
        }

        String itemId = itemInstanceEntity.getItemId();
        String formDataType = config.getFormDataType();
        // String flowBizDefId = config.getFlowBizDefId();
        String flowDefId = config.getFlowDefId();// flowBusinessDefinitionFacadeService.getFlowDefIdById(flowBizDefId);

        DyFormData newDyformData = dyFormData;
        // 使用单据转换
        if (ProcessItemConfig.BusinessIntegrationConfig.FORM_DATA_TYPE_USE_BOT.equals(formDataType)) {
            String botId = config.getCopyBotRuleId();
            newDyformData = copyDyFormDataWithBotId(botId, dyFormData);
        }

        // 从流程单据提交业务事项
        WorkflowIntegrationParams workflowIntegrationParams = BusinessIntegrationContextHolder.getWorkflowIntegrationParams();
        WorkBean workBean = null;
        if (workflowIntegrationParams != null && isDraftByFlowInstUuid(workflowIntegrationParams.getFlowInstUuid())) {
            FlowInstance flowInstance = flowService.getFlowInstance(workflowIntegrationParams.getFlowInstUuid());
            workBean = workflowIntegrationParams.getWorkData();
            if (workBean == null) {
                workBean = new WorkBean();
            }
            workBean.setFlowInstUuid(workflowIntegrationParams.getFlowInstUuid());
            // workBean.setFlowBizDefId(flowBizDefId);
            workBean.setFormUuid(newDyformData.getFormUuid());
            workBean.setDataUuid(newDyformData.getDataUuid());
            // 更新草稿流程的表单数据
            if (!(StringUtils.equals(flowInstance.getFormUuid(), newDyformData.getFormUuid())
                    && StringUtils.equals(flowInstance.getDataUuid(), newDyformData.getDataUuid()))) {
                flowInstance.setFormUuid(newDyformData.getFormUuid());
                flowInstance.setDataUuid(newDyformData.getDataUuid());
            }
            workBean.setDyFormData(newDyformData);
            workBean.setLoadDyFormData(false);
            workBean = workService.getWorkData(workBean);
        } else {
            if (workflowIntegrationParams != null) {
                workBean = workflowIntegrationParams.getWorkData();
            }
            if (workBean == null) {
                workBean = workService.newWorkById(flowDefId);
            }
            // workBean.setFlowBizDefId(flowBizDefId);
            workBean.setFormUuid(newDyformData.getFormUuid());
            workBean.setDataUuid(newDyformData.getDataUuid());
            workBean.setDyFormData(newDyformData);
            // 从流程直接提交，设置附加参数
            if (workflowIntegrationParams != null && workflowIntegrationParams.getExtraParams() != null) {
                workBean.getExtraParams().putAll(workflowIntegrationParams.getExtraParams());
            }
        }

        // 复制交互式的参数
        Map<String, InteractionTaskData> interactionTaskDataMap = BusinessIntegrationContextHolder.getInteractionTaskDataMap();
        if (interactionTaskDataMap != null && interactionTaskDataMap.containsKey(itemId)) {
            InteractionTaskData interactionTaskData = interactionTaskDataMap.get(itemId);
            extractAndFillItemUsers(itemId, interactionTaskData.getTaskUsers());
            extractAndFillItemUsers(itemId, interactionTaskData.getTaskCopyUsers());
            extractAndFillItemUsers(itemId, interactionTaskData.getTaskMonitors());
            extractAndFillItemUsers(itemId, interactionTaskData.getTaskUserJobPaths());
            BeanUtils.copyProperties(interactionTaskData, workBean);
        }

        // 解析流程集成参数
        // 提交到的环节
        String toTaskId = getToTaskId(workflowIntegrationParams);
        // 环节办理人
        boolean customTaskUsers = isCustomTaskUsers(workflowIntegrationParams);
        if (customTaskUsers) {
            List<String> userIds = getCustomTaskUsers(workflowIntegrationParams);
            // 设置流程办理人
            setTaskUserIfRequired(workBean, toTaskId, userIds);
        }

        // 提交操作
        workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
        workBean.setActionType(WorkFlowOperation.SUBMIT);
        // 添加运行时监听器
        addRuntimeListeners(workBean, config);

        try {
            // 自动提交
            String flowInstUuid = null;
            if (FlowConstant.AUTO_SUBMIT.equals(toTaskId)) {
                ResultMessage resultMessage = workService.submit(workBean);
                SubmitResult submitResult = (SubmitResult) resultMessage.getData();
                flowInstUuid = submitResult.getFlowInstUuid();
                // 提交结果
                if (workflowIntegrationParams != null) {
                    workflowIntegrationParams.setSubmitResult(submitResult);
                }
            } else {
                // 提交到指定环节
                if (StringUtils.isNotBlank(workBean.getFlowInstUuid())) {
                    throw new WorkFlowException(String.format("流程实例已经存在，不能提交到指定环节[%s]", toTaskId));
                }
                String flowDefUuid = workBean.getFlowDefUuid();
                TaskData taskData = workService.createTaskDataFromWorkData(workBean);
                FlowInstance flowInstance = flowService.saveAsDraft(flowDefUuid, taskData);
                flowInstUuid = flowInstance.getUuid();
                // 设置流程提交的环节
                taskData.setToTaskId(FlowDelegate.START_FLOW_ID, toTaskId);
                flowService.startFlowInstance(flowInstUuid, taskData);
                // 保存更新的表单数据
                saveUpdatedDyFormDatasIfRequired(taskData);
            }

            businessIntegrationService.saveBusinessIntegration(itemInstUuid, type, flowInstUuid);
        } catch (JsonDataException e) {
            Object data = e.getData();
            // 添加事项ID标识，用于识别不同事项的流程交互性数据
            if (data instanceof Map) {
                Map<Object, Object> dataMap = ((Map<Object, Object>) data);
                dataMap.put("itemId", itemId);
                if (dataMap.containsKey("taskId")) {
                    dataMap.put("taskId", itemId + "_" + dataMap.get("taskId"));
                }
            }
            throw e;
        }
    }

    /**
     * 根据附加参数，更新流程信息信息
     *
     * @param businessIntegrationEntity
     * @param itemInstanceEntity
     * @param dyFormData
     * @param config
     * @param extraParams
     */
    private void submitWorkflowBusinessIntegrationIfRequried(BizBusinessIntegrationEntity businessIntegrationEntity,
                                                             BizProcessItemInstanceEntity itemInstanceEntity,
                                                             DyFormData dyFormData, ProcessItemConfig.BusinessIntegrationConfig config,
                                                             Map<String, Object> extraParams) {
        // 同步流程集成的流程数据单据转换ID
        if (MapUtils.isNotEmpty(extraParams)) {
            String syncFlowBotId = Objects.toString(extraParams.get("syncFlowBotId"), StringUtils.EMPTY);
            // 同步流程数据
            if (StringUtils.isNotBlank(syncFlowBotId)) {
                syncDyFormDataWithBotId(syncFlowBotId, dyFormData, businessIntegrationEntity);
            }
        }

        // 提交流程
        String itemId = itemInstanceEntity.getItemId();
        try {
            FlowInstance flowInstance = flowService.getFlowInstance(businessIntegrationEntity.getBizInstUuid());
            WorkBean workBean = new WorkBean();
            workBean.setFlowInstUuid(flowInstance.getUuid());
            workBean.setFormUuid(flowInstance.getFormUuid());
            workBean.setDataUuid(flowInstance.getDataUuid());
            workBean.setLoadDyFormData(false);
            workBean = workService.getWorkData(workBean);

            // 复制交互式的参数
            Map<String, InteractionTaskData> interactionTaskDataMap = BusinessIntegrationContextHolder.getInteractionTaskDataMap();
            if (interactionTaskDataMap != null && interactionTaskDataMap.containsKey(itemId)) {
                InteractionTaskData interactionTaskData = interactionTaskDataMap.get(itemId);
                extractAndFillItemUsers(itemId, interactionTaskData.getTaskUsers());
                extractAndFillItemUsers(itemId, interactionTaskData.getTaskCopyUsers());
                extractAndFillItemUsers(itemId, interactionTaskData.getTaskMonitors());
                extractAndFillItemUsers(itemId, interactionTaskData.getTaskUserJobPaths());
                BeanUtils.copyProperties(interactionTaskData, workBean);
            }

            // 提交操作
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
            workBean.setActionType(WorkFlowOperation.SUBMIT);

            workService.submit(workBean);
        } catch (JsonDataException e) {
            Object data = e.getData();
            // 添加事项ID标识，用于识别不同事项的流程交互性数据
            if (data instanceof Map) {
                Map<Object, Object> dataMap = ((Map<Object, Object>) data);
                dataMap.put("itemId", itemId);
                if (dataMap.containsKey("taskId")) {
                    dataMap.put("taskId", itemId + "_" + dataMap.get("taskId"));
                }
            }
            throw e;
        }
    }

    /**
     * @param syncFlowBotId
     * @param sourceDyformData
     * @param businessIntegrationEntity
     */
    private void syncDyFormDataWithBotId(String syncFlowBotId, DyFormData sourceDyformData, BizBusinessIntegrationEntity businessIntegrationEntity) {
        String sourceFormUuid = sourceDyformData.getFormUuid();
        String sourceDataUuid = sourceDyformData.getDataUuid();
        FlowInstance flowInstance = flowService.getFlowInstance(businessIntegrationEntity.getBizInstUuid());
        String targetFormUuid = flowInstance.getFormUuid();
        String targetDataUuid = flowInstance.getDataUuid();

        if (StringUtils.equals(sourceFormUuid, targetFormUuid) && StringUtils.equals(sourceDataUuid, targetDataUuid)) {
            return;
        }

        // 单据转换数据反馈处理
        Set<BotParam.BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotParam.BotFromParam botFromParam = new BotParam.BotFromParam(sourceDataUuid, sourceFormUuid,
                sourceDyformData);
        froms.add(botFromParam);
        BotParam botParam = new BotParam(syncFlowBotId, froms);
        botParam.setFroms(froms);
        botParam.setTargetUuid(targetDataUuid);
        try {
            botFacadeService.startBot(botParam);
        } catch (Exception e) {
            logger.error("单据转换: 同步流程集成的流程单据时出错！", e);
            throw new RuntimeException("事项数据同步流程集成的流程单据时出错,请检查单据转换配置！", e);
        }
    }

    /**
     * 提取事项对应的办理人放入真正的环节中
     *
     * @param itemId
     * @param taskUsers
     */
    private void extractAndFillItemUsers(String itemId, Map<String, List<String>> taskUsers) {
        if (MapUtils.isNotEmpty(taskUsers)) {
            String prefix = itemId + "_";
            Map<String, List<String>> itemUsers = Maps.newHashMap();
            for (Map.Entry<String, List<String>> entry : taskUsers.entrySet()) {
                String key = entry.getKey();
                if (StringUtils.startsWith(key, prefix)) {
                    String taskId = StringUtils.substring(key, prefix.length());
                    if (!taskUsers.containsKey(taskId)) {
                        itemUsers.put(taskId, entry.getValue());
                    }
                }
            }
            if (MapUtils.isNotEmpty(itemUsers)) {
                taskUsers.putAll(itemUsers);
            }
        }
    }

    /**
     * @param taskData
     */
    private void saveUpdatedDyFormDatasIfRequired(TaskData taskData) {
        List<DyFormData> dyFormDatas = taskData.getAllUpdatedDyFormDatas();
        for (DyFormData dyFormData : dyFormDatas) {
            dyFormData.doForceCover();
            dyFormFacade.saveFormData(dyFormData);
            taskData.removeUpdatedDyFormData(dyFormData.getDataUuid(), dyFormData);
        }
    }

    /**
     * @param workBean
     * @param toTaskId
     * @param userIds
     */
    private void setTaskUserIfRequired(WorkBean workBean, String toTaskId, List<String> userIds) {
        // 自动提交
        if (FlowConstant.AUTO_SUBMIT.equals(toTaskId)) {
            // 指定办理人
            if (CollectionUtils.isNotEmpty(userIds)) {
                FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(workBean.getFlowDefUuid());
                List<String> toTaskIds = flowDelegate.getNextTaskNodes(flowDelegate.getStartNode().getToID());
                Map<String, List<String>> taskUsers = Maps.newHashMap();
                for (String taskId : toTaskIds) {
                    taskUsers.put(taskId, userIds);
                }
                Map<String, List<String>> existsTaskUsers = workBean.getTaskUsers();
                if (existsTaskUsers != null) {
                    existsTaskUsers.putAll(taskUsers);
                    existsTaskUsers = Maps.newHashMap(existsTaskUsers);
                    existsTaskUsers.putAll(taskUsers);
                    workBean.setTaskUsers(existsTaskUsers);
                } else {
                    workBean.setTaskUsers(taskUsers);
                }
            }
        } else {
            // 指定办理人
            if (CollectionUtils.isNotEmpty(userIds)) {
                Map<String, List<String>> taskUsers = Maps.newHashMap();
                taskUsers.put(toTaskId, userIds);
                Map<String, List<String>> existsTaskUsers = workBean.getTaskUsers();
                if (existsTaskUsers != null) {
                    existsTaskUsers = Maps.newHashMap(existsTaskUsers);
                    existsTaskUsers.putAll(taskUsers);
                    workBean.setTaskUsers(existsTaskUsers);
                } else {
                    workBean.setTaskUsers(taskUsers);
                }
            }
        }
    }

    /**
     * 获取提交的环节
     *
     * @param workflowIntegrationParams
     * @return
     */
    private String getToTaskId(WorkflowIntegrationParams workflowIntegrationParams) {
        // 默认自动提交
        if (workflowIntegrationParams == null) {
            return FlowConstant.AUTO_SUBMIT;
        }
        ItemFlowDefinition.EdgeConfiguration edgeConfiguration = workflowIntegrationParams.getEdgeConfiguration();
        if (edgeConfiguration == null) {
            return FlowConstant.AUTO_SUBMIT;
        }
        return StringUtils.isNotBlank(edgeConfiguration.getToTaskId()) ? edgeConfiguration.getToTaskId() : FlowConstant.AUTO_SUBMIT;
//        ProcessItemConfig.BusinessIntegrationNewItemConfig newItemConfig = workflowIntegrationParams.getNewItemConfig();
//        if (newItemConfig == null) {
//            return FlowConstant.AUTO_SUBMIT;
//        }
//        return StringUtils.isNotBlank(newItemConfig.getToTaskId()) ? newItemConfig.getToTaskId() : FlowConstant.AUTO_SUBMIT;
    }

    /**
     * 是否配置指定办理人
     *
     * @param workflowIntegrationParams
     * @return
     */
    private boolean isCustomTaskUsers(WorkflowIntegrationParams workflowIntegrationParams) {
        return false;
//        if (workflowIntegrationParams == null) {
//            return false;
//        }
//        ProcessItemConfig.BusinessIntegrationNewItemConfig newItemConfig = workflowIntegrationParams.getNewItemConfig();
//        if (newItemConfig == null) {
//            return false;
//        }
//        return EnumBizBiNewItemTaskUserSouce.Custom.getValue().equals(newItemConfig.getTaskUserSource());
    }

    /**
     * 获取流程办理人
     *
     * @param workflowIntegrationParams
     * @return
     */
    private List<String> getCustomTaskUsers(WorkflowIntegrationParams workflowIntegrationParams) {
        return Collections.emptyList();
//        ProcessItemConfig.BusinessIntegrationNewItemConfig newItemConfig = workflowIntegrationParams.getNewItemConfig();
//        return resolveTaskUsers(newItemConfig, workflowIntegrationParams);
    }

//    private List<String> resolveTaskUsers(ProcessItemConfig.BusinessIntegrationNewItemConfig newItemConfig, WorkflowIntegrationParams workflowIntegrationParams) {
//        // 办理人类型，1、组织机构、4、源流程历史环节办理人、8、人员选项
//        String taskUserType = newItemConfig.getTaskUserType();
//        if (StringUtils.isBlank(taskUserType)) {
//            return Collections.emptyList();
//        }
//
//        String taskUsreId = newItemConfig.getTaskUserId();
//        if (StringUtils.isBlank(taskUsreId)) {
//            return Collections.emptyList();
//        }
//
//        Set<String> userIds = Sets.newLinkedHashSet();
//        List<String> configUserIds = Arrays.asList(StringUtils.split(taskUsreId, Separator.SEMICOLON.getValue()));
//        EnumBizBiNewItemTaskUserType enumBizBiNewItemTaskUserType = EnumBizBiNewItemTaskUserType.getByValue(taskUserType);
//        switch (enumBizBiNewItemTaskUserType) {
//            // 组织机构
//            case Org:
//                userIds.addAll(configUserIds);
//                break;
//            // 源流程历史环节办理人
//            case TaskHistory:
//                userIds.addAll(resolveTaskHistoryUsers(configUserIds, workflowIntegrationParams.getEvent()));
//                break;
//            // 人员选项
//            case UserOption:
//                configUserIds.forEach(userOption -> {
//                    if (Participant.PriorUser.name().equals(userOption)) {
//                        userIds.add(SpringSecurityUtils.getCurrentUserId());
//                    } else if (Participant.Creator.name().equals(userOption)) {
//                        userIds.add(workflowIntegrationParams.getEvent().getFlowStartUserId());
//                    }
//                });
//                break;
//        }
//        return Lists.newArrayList(userIds);
//    }

    /**
     * 解析源流程历史环节办理人
     *
     * @param taskIds
     * @param event
     * @return
     */
//    private Collection<String> resolveTaskHistoryUsers(List<String> taskIds, Event event) {
//        TaskHistoryIdentityResolver taskHistoryIdentityResolver = ApplicationContextHolder.getBean(TaskHistoryIdentityResolver.class);
//        List<FlowUserSid> flowUserSids = taskHistoryIdentityResolver.resolve(null, event.getTaskData().getToken(), taskIds, ParticipantType.TodoUser);
//        List<String> userIds = Lists.newArrayList();
//        flowUserSids.forEach(sid -> userIds.add(sid.getId()));
//        return userIds;
//    }

    /**
     * 根据流程实例UUID判断是否草稿
     *
     * @param flowInstUuid
     * @return
     */
    private boolean isDraftByFlowInstUuid(String flowInstUuid) {
        if (StringUtils.isBlank(flowInstUuid)) {
            return false;
        }
        return flowService.hasDraftPermission(SpringSecurityUtils.getCurrentUserId(), flowInstUuid);
    }

    /**
     * 添加运行时监听器
     *
     * @param workBean
     * @param config
     */
    private void addRuntimeListeners(WorkBean workBean, ProcessItemConfig.BusinessIntegrationConfig config) {
        // 流程监听器
        workBean.addExtraParam(CustomRuntimeData.KEY_FLOW_LISTENER, BizWorkflowIntegrationFlowListener.BEAN_NAME);
        // 环节监听器
        if (isRequiredTaskListener(config)) {
            workBean.addExtraParam(CustomRuntimeData.KEY_TASK_LISTENER, BizWorkflowIntegrationTaskListener.BEAN_NAME);
        }
        // 流向监听器
        if (isRequiredDirectionListener(config)) {
            workBean.addExtraParam(CustomRuntimeData.KEY_DIRECTION_LISTENER, BizWorkflowIntegrationDirectionListener.BEAN_NAME);
        }
        // 计时监听器
        if (config.isSyncTimerInfo()) {
            workBean.addExtraParam(CustomRuntimeData.KEY_TIMER_LISTENER, BizWorkflowIntegrationTimerListener.BEAN_NAME);
        }
    }

    /**
     * 是否需要环节监听器
     *
     * @param config
     * @return
     */
    private boolean isRequiredTaskListener(ProcessItemConfig.BusinessIntegrationConfig config) {
        return ProcessItemConfig.BusinessIntegrationConfig.FORM_DATA_TYPE_USE_BOT.equals(config.getFormDataType()) ||
                CollectionUtils.isNotEmpty(config.getEventPublishConfigs()) ||
                CollectionUtils.isNotEmpty(config.getStates());
    }

    /**
     * 是否需要流向监听器
     *
     * @param config
     * @return
     */
    private boolean isRequiredDirectionListener(ProcessItemConfig.BusinessIntegrationConfig config) {
        boolean requiredDirectionListener = false;
        List<ProcessItemConfig.EventPublishConfig> eventPublishConfigs = config.getEventPublishConfigs();
        if (CollectionUtils.isNotEmpty(eventPublishConfigs)) {
            for (ProcessItemConfig.EventPublishConfig eventPublishConfig : eventPublishConfigs) {
                if (EnumBizBiEventTriggerType.DirectionTransition.getValue().equals(eventPublishConfig.getTriggerType())) {
                    requiredDirectionListener = true;
                    break;
                }
            }
        }
        return requiredDirectionListener || ProcessItemConfig.BusinessIntegrationConfig.FORM_DATA_TYPE_USE_BOT.equals(config.getFormDataType()) ||
                CollectionUtils.isNotEmpty(config.getEventPublishConfigs());
    }

    /**
     * 单据转换处理
     *
     * @param botRuleId
     * @param dyFormData
     * @return
     */
    private DyFormData copyDyFormDataWithBotId(String botRuleId, DyFormData dyFormData) {
        Set<BotParam.BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotParam.BotFromParam botFromParam = new BotParam.BotFromParam(dyFormData.getDataUuid(), dyFormData.getFormUuid(), dyFormData);
        froms.add(botFromParam);
        BotParam botParam = new BotParam(botRuleId, froms);
        botParam.setFroms(froms);
        BotResult botResult = null;
        try {
            botResult = botFacadeService.startBot(botParam);
        } catch (Exception e) {
            logger.error("业务事项发起流程时单据转换出错！", e);
            throw new BusinessException("业务事项发起流程时单据转换出错！", e);
        }
        Object dyformData = botResult.getDyformData();
        Object data = botResult.getData();
        String newFormUuid = null;
        String newDataUuid = null;
        if (dyformData instanceof DyFormData) {
            return (DyFormData) dyformData;
        } else if (data instanceof Map) {
            Map<String, Object> formData = (Map<String, Object>) data;
            newFormUuid = (String) formData.get("form_uuid");
            newDataUuid = botResult.getDataUuid();
            if (StringUtils.isBlank(newFormUuid)) {
                throw new WorkFlowException("业务集成使用的单据转换规则[" + botRuleId + "]配置异常，没有勾选“保存单据”，无法分发，请联系管理员修改");
            }
        } else {
            throw new BusinessException("业务事项发起流程失败，无法取到业务事项数据单据转换后的数据！");
        }

        return dyFormFacade.getDyFormData(newFormUuid, newDataUuid);
    }


    /**
     * 撤回工作流集成
     *
     * @param itemInstanceEntity
     * @param businessIntegrationEntity
     * @param extraParams
     */
    private void cancelWorkflowBusinessIntegration(BizProcessItemInstanceEntity itemInstanceEntity, BizBusinessIntegrationEntity businessIntegrationEntity,
                                                   Map<String, Object> extraParams) {
        // 同步流程集成的流程数据单据转换ID
        if (MapUtils.isNotEmpty(extraParams)) {
            String syncFlowBotId = Objects.toString(extraParams.get("syncFlowBotId"), StringUtils.EMPTY);
            // 同步流程数据
            if (StringUtils.isNotBlank(syncFlowBotId)) {
                DyFormData dyFormData = dyFormFacade.getDyFormData(itemInstanceEntity.getFormUuid(), itemInstanceEntity.getDataUuid(), false);
                syncDyFormDataWithBotId(syncFlowBotId, dyFormData, businessIntegrationEntity);
            }
        }

        WorkflowIntegrationParams workflowIntegrationParams = BusinessIntegrationContextHolder.getWorkflowIntegrationParams();
        // 当前流程送结束
        WorkBean workBean = workflowIntegrationParams != null ? workflowIntegrationParams.getWorkData() : null;
        if (workBean == null) {
            workBean = new WorkBean();
            String flowInstUuid = businessIntegrationEntity.getBizInstUuid();
            TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
            workBean.setFlowInstUuid(flowInstUuid);
            workBean.setOpinionText(workflowIntegrationParams != null ? workflowIntegrationParams.getOpinionText() : "业务流程撤回");
            if (taskInstance != null) {
                workBean.setTaskInstUuid(taskInstance.getUuid());
                workBean.setFormUuid(taskInstance.getFormUuid());
                workBean.setDataUuid(taskInstance.getDataUuid());
                workBean.setFlowDefUuid(taskInstance.getFlowDefinition().getUuid());
            } else {
                FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
                workBean.setFormUuid(flowInstance.getFormUuid());
                workBean.setDataUuid(flowInstance.getDataUuid());
                workBean.setFlowDefUuid(flowInstance.getFlowDefinition().getUuid());
            }
        }
        workBean.setGotoTask(true);
        workBean.setGotoTaskId(FlowConstant.END_FLOW_ID);
        workBean.setAction("业务流程撤回");
        workBean.setActionType(WorkFlowOperation.CANCEL);
        workService.gotoTask(workBean);
    }

    /**
     * 重新开始工作流集成
     *
     * @param itemInstanceEntity
     * @param businessIntegrationEntity
     */
    private void restartWorkflowBusinessIntegration(BizProcessItemInstanceEntity itemInstanceEntity, BizBusinessIntegrationEntity businessIntegrationEntity,
                                                    ProcessDefinitionJsonParser parser, BizNewItemRelationEntity newItemRelationEntity) {
        WorkflowIntegrationParams workflowIntegrationParams = BusinessIntegrationContextHolder.getWorkflowIntegrationParams();
        String opinionText = workflowIntegrationParams.getOpinionText();

        String flowInstUuid = businessIntegrationEntity.getBizInstUuid();
        String taskInstUuid = extractSourceTaskInstUuid(newItemRelationEntity);
        FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
        if (StringUtils.isBlank(taskInstUuid)) {
            // 流程已办结，撤回处理
            if (flowInstance.getEndTime() != null) {
                taskService.cancelOverWithLastTaskOwner(flowInstUuid, opinionText);
            }
        } else {
            String userId = SpringSecurityUtils.getCurrentUserId();
            String userName = SpringSecurityUtils.getCurrentUserName();
            TaskInstance lastTaskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
            if (taskService.isAllowedCancel(userId, taskInstUuid)) {
                WorkBean workBean = new WorkBean();
                workBean.setFlowInstUuid(flowInstUuid);
                workBean.setTaskInstUuid(lastTaskInstance.getUuid());
                workBean.setFlowDefUuid(lastTaskInstance.getFlowDefinition().getUuid());
                workBean.setFormUuid(lastTaskInstance.getFormUuid());
                workBean.setDataUuid(lastTaskInstance.getDataUuid());
                workBean.setOpinionText(opinionText);
                workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.CANCEL));
                workBean.setActionType(WorkFlowOperation.CANCEL);
                workService.cancelWithWorkData(workBean);
            } else {
                String key = taskInstUuid + userId;
                TaskInstance taskInstance = taskService.getTask(taskInstUuid);
                TaskData taskData = new TaskData();
                taskData.setUserId(userId);
                taskData.setUserName(userName);
                taskData.setOpinionText(key, opinionText);
                taskData.addTaskUsers(taskInstance.getId(), Arrays.asList(StringUtils.split(taskInstance.getOwner(), Separator.SEMICOLON.getValue())));
                taskService.gotoTask(lastTaskInstance.getUuid(), taskInstance.getId(), taskData);
            }
        }
    }

    private String extractSourceTaskInstUuid(BizNewItemRelationEntity newItemRelationEntity) {
        String extraData = newItemRelationEntity.getExtraData();
        if (StringUtils.isBlank(extraData)) {
            return StringUtils.EMPTY;
        }
        Map<String, String> extraDataMap = JsonUtils.json2Object(extraData, Map.class);
        return extraDataMap.get("sourceTaskInstUuid");
    }

}

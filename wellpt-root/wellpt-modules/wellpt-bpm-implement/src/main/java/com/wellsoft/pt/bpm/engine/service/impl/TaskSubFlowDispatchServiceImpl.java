/*
 * @(#)2013-5-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bot.exception.BotException;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotParam.BotFromParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.dao.TaskSubFlowDispatchDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.NewFlow;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.ReservedFieldUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * Description: 任务子流程分发服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年10月29日.1	zhulh		2021年10月29日		Create
 * </pre>
 * @date 2021年10月29日
 */
@Service
public class TaskSubFlowDispatchServiceImpl
        extends AbstractJpaServiceImpl<TaskSubFlowDispatch, TaskSubFlowDispatchDao, String>
        implements TaskSubFlowDispatchService {

    @Autowired
    private FlowService flowService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BotFacadeService botFacadeService;
    @Autowired
    private TaskSubFlowService taskSubFlowService;
    @Autowired
    private TaskOperationService taskOperationService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    @Qualifier("workService")
    private WorkService workService;
    @Autowired
    private FlowIndexDocumentService flowIndexDocumentService;
    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService#dispatchSubFlow(com.wellsoft.pt.bpm.engine.support.NewFlow, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.support.TaskData, java.util.List, java.lang.String, java.lang.String, java.util.List, boolean)
     */
    @Override
    @Transactional
    public String dispatchSubFlow(NewFlow newFlow, FlowInstance parentFlowInstance, TaskInstance parentTaskInstance,
                                  TaskData taskData, List<String> users, String todoId, String todoName, List<String> monitors,
                                  boolean async) {
        StopWatch stopWatch = new StopWatch("startSubFlowInstance");
        stopWatch.start("发起子流程实例");
        String subFlowId = newFlow.getFlowId();
        String toTaskId = newFlow.getToTaskId();
        String sidGranularity = newFlow.getSidGranularity();
        String monitorId = StringUtils.join(monitors, Separator.SEMICOLON.getValue());
        FlowInstance subFlowInstance = null;
        // 发起的新流程ID
        if (CollectionUtils.isEmpty(users)) {
            taskData.put("startNewFlowId", newFlow.getId());
        }
        // 保存草稿时，办理人不为空设置成办理人的草稿
        if (SubTaskNode.DRAFT.equals(toTaskId)) {
            if (CollectionUtils.isNotEmpty(users)) {
                Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
                taskUsers.put(toTaskId, users);
                taskData.setTaskUsers(taskUsers);
                taskData.setSidGranularity(toTaskId, sidGranularity);
            }
        }

        String subFlowInstUuid = (String) taskData.get("subFlowInstUuid");
        String taskSubFlowUuid = (String) taskData.get("taskSubFlowUuid");
        // 异步分发
        if (async) {
            // 提交为草稿
            subFlowInstance = flowService.createEmptySubFlowInstance(parentFlowInstance, subFlowId);
            // subFlowInstance =
            // flowService.saveSubFlowAsDraftByFlowDefId(parentFlowInstance, subFlowId,
            // taskData);
            // 添加子流程异步分发信息，进入任务调度进行分发处理
            addDispatchInfo(newFlow, subFlowInstance, parentFlowInstance, parentTaskInstance, taskData, users, todoId,
                    todoName, monitors);
            return subFlowInstance.getUuid();
        } else {
            // 同步分发，没有指定子流程实例UUID的生成新的子流程
            if (StringUtils.isBlank(subFlowInstUuid)) {
                subFlowInstance = flowService.saveSubFlowAsDraftByFlowDefId(parentFlowInstance, parentTaskInstance,
                        subFlowId, taskData, todoName);
            } else {
                // 更新空的流程实例为草稿
                subFlowInstance = flowService.updateSubFlowAsDraftByFlowDefId(subFlowInstUuid, parentFlowInstance,
                        parentTaskInstance, subFlowId, taskData, todoName);
            }

            // 索引流程文档到全文检索库
            taskData.setFlowInstUuid(subFlowInstance.getUuid());
            flowIndexDocumentService.indexWorkflowDocument(taskData);
        }

        if (SubTaskNode.DRAFT.equals(toTaskId)) {
            // 保存子流程信息
            saveSubFlow(taskSubFlowUuid, newFlow, parentFlowInstance, parentTaskInstance, subFlowInstance, todoId,
                    todoName, monitorId);
            subFlowInstUuid = subFlowInstance.getUuid();
        } else if (SubTaskNode.AUTO_SUBMIT.equals(toTaskId)) {// 自动提交
            subFlowInstUuid = subFlowInstance.getUuid();
            taskData.setParentTaskInstUuid(subFlowInstUuid, parentTaskInstance.getUuid());
            flowService.startFlowInstance(subFlowInstUuid, taskData);
            // 提交第一个任务
            List<TaskInstance> subTaskInstances = taskService.getUnfinishedTasks(subFlowInstUuid);
            for (TaskInstance subTaskInstance : subTaskInstances) {
                if (CollectionUtils.isNotEmpty(users)) {
                    // 设置自动提交
                    taskData.setAutoSubmit(subTaskInstance.getId(), true);
                    // 设置自动提交的办理人
                    taskData.setAutoSubmitUsers(subTaskInstance.getId(), users);
                    taskData.setSidGranularity(subTaskInstance.getId(), sidGranularity);
                }
                taskData.setTitle(subFlowInstance.getId(), subFlowInstance.getTitle());
                // 操作动作及操作类型
                String key = subTaskInstance.getUuid() + taskData.getUserId();
                taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
                taskData.setActionType(key, WorkFlowOperation.SUBMIT);
                taskService.submit(subTaskInstance, taskData);
            }
            // 保存子流程信息
            if (CollectionUtils.isNotEmpty(users)) {
                saveSubFlow(taskSubFlowUuid, newFlow, parentFlowInstance, parentTaskInstance, subFlowInstance, todoId,
                        todoName, monitorId);
            } else {
                subTaskInstances = taskService.getUnfinishedTasks(subFlowInstUuid);
                List<String> todoUserIds = new ArrayList<String>();
                List<String> todoUserNames = new ArrayList<String>();
                for (TaskInstance subTaskInstance : subTaskInstances) {
                    todoUserIds.add(subTaskInstance.getTodoUserId());
                    todoUserNames.add(subTaskInstance.getTodoUserName());
                }
                saveSubFlow(taskSubFlowUuid, newFlow, parentFlowInstance, parentTaskInstance, subFlowInstance,
                        StringUtils.join(todoUserIds, Separator.SEMICOLON.getValue()),
                        StringUtils.join(todoUserNames, Separator.SEMICOLON.getValue()), monitorId);
            }
            subFlowInstUuid = subFlowInstance.getUuid();

            // 索引流程文档到全文检索库
            flowIndexDocumentService.indexWorkflowDocument(taskData);
        } else if (StringUtils.isNotBlank(toTaskId)) {// 提交到指定环节
            if (CollectionUtils.isNotEmpty(users)) {
                Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
                taskUsers.put(toTaskId, users);
                taskData.setTaskUsers(taskUsers);
            }
            taskData.setSidGranularity(toTaskId, sidGranularity);
            // 设置流程提交的环节
            taskData.setToTaskId(FlowDelegate.START_FLOW_ID, toTaskId);
            taskData.setTitle(subFlowInstance.getId(), subFlowInstance.getTitle());
            subFlowInstUuid = subFlowInstance.getUuid();
            taskData.setParentTaskInstUuid(subFlowInstUuid, parentTaskInstance.getUuid());
            FlowInstance flowInstance = flowService.startFlowInstance(subFlowInstUuid, taskData);

            // 未完成的环节增加已办权限
            TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(subFlowInstUuid);
            if (taskInstance.getEndTime() == null) {
                // 主流程发起的子流程不添加已办权限
                // taskService.addDonePermission(taskData.getUserId(), taskInstance.getUuid());
                // 记录操作日志
                taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT),
                        ActionCode.SUBMIT.getCode(), WorkFlowOperation.SUBMIT, "", "", "提交到子流程环节", taskData.getUserId(),
                        taskData.getTaskUsers(toTaskId), null, null, null, taskInstance, subFlowInstance, taskData);
            }
            // 保存子流程信息
            if (CollectionUtils.isNotEmpty(users)) {
                saveSubFlow(taskSubFlowUuid, newFlow, parentFlowInstance, parentTaskInstance, subFlowInstance, todoId,
                        todoName, monitorId);
            } else {
                saveSubFlow(taskSubFlowUuid, newFlow, parentFlowInstance, parentTaskInstance, subFlowInstance,
                        taskInstance.getTodoUserId(), taskInstance.getTodoUserName(), monitorId);
            }

            // 索引流程文档到全文检索库
            flowIndexDocumentService.indexWorkflowDocument(taskData);

            // 工作到达通知
            List<MessageTemplate> todoMessageTemplates = taskData.getToken().getFlowDelegate()
                    .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_TODO.getType());
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TODO, todoMessageTemplates, taskInstance,
                    flowInstance, users, ParticipantType.TodoUser);
        } else {
            throw new RuntimeException("子流程分发没有指定目标环节");
        }

        // 实时同步的单据转换规则
        String syncBotRuleId = newFlow.getSyncBotRuleId();
        if (StringUtils.isNotBlank(syncBotRuleId) && StringUtils.isNotBlank(subFlowInstUuid)) {
            addSyncSubFlowParameter(parentFlowInstance.getUuid(), subFlowInstUuid, newFlow);
        }

        // 添加日志
        String subTaskId = parentTaskInstance.getId();
        if (taskData.isLogAddSubflow(subTaskId) && subFlowInstance != null) {
            TaskInstance subTaskInstance = taskService.getLastTaskInstanceByFlowInstUuid(subFlowInstance.getUuid());
            String actionName = taskData.getAddSubflowActionName(subTaskId);
            String action = StringUtils.isNotBlank(actionName) ? actionName
                    : WorkFlowOperation.getName(WorkFlowOperation.ADD_SUB_FLOW);
            List<String> taskUsers = Arrays.asList(StringUtils.split(todoId, Separator.SEMICOLON.getValue()));
            taskOperationService.saveTaskOperation(action, ActionCode.ADD_SUB_FLOW.getCode(),
                    WorkFlowOperation.ADD_SUB_FLOW, null, null, null, taskData.getUserId(), taskUsers, null, null, null,
                    subTaskInstance, subFlowInstance, taskData);
        }

        // 保存更新的表单数据
        List<DyFormData> dyFormDatas = taskData.getAllUpdatedDyFormDatas();
        for (DyFormData dyFormData : dyFormDatas) {
            dyFormData.doForceCover();
            dyFormFacade.saveFormData(dyFormData);
            taskData.removeUpdatedDyFormData(dyFormData.getDataUuid(), dyFormData);
        }

        stopWatch.stop();
        logger.info("发起子流程实例{}，共耗时{}秒", subFlowInstUuid, stopWatch.getLastTaskInfo().getTimeSeconds());
        return subFlowInstUuid;
    }

    /**
     * @param newFlow
     * @param parentFlowInstance
     * @param parentTaskInstance
     * @param taskData
     * @param users
     * @param todoId
     * @param todoName
     * @param monitors
     */
    private void addDispatchInfo(NewFlow newFlow, FlowInstance subFlowInstance, FlowInstance parentFlowInstance,
                                 TaskInstance parentTaskInstance, TaskData taskData, List<String> users, String todoId, String todoName,
                                 List<String> monitors) {
        String monitorId = StringUtils.join(monitors, Separator.SEMICOLON.getValue());
        String taskSubFlowUuid = saveSubFlow(null, newFlow, parentFlowInstance, parentTaskInstance, subFlowInstance,
                todoId, todoName, monitorId);
        TaskSubFlowDispatch taskSubFlowDispatch = new TaskSubFlowDispatch();
        taskSubFlowDispatch.setParentTaskInstUuid(parentTaskInstance.getUuid());
        taskSubFlowDispatch.setParentFlowInstUuid(parentFlowInstance.getUuid());
        taskSubFlowDispatch.setFlowInstUuid(subFlowInstance.getUuid());
        taskSubFlowDispatch.setTaskSubFlowUuid(taskSubFlowUuid);
        taskSubFlowDispatch.setNewFlowId(newFlow.getId());
        taskSubFlowDispatch.setTaskUsers(StringUtils.join(users, Separator.SEMICOLON.getValue()));
        taskSubFlowDispatch.setLogAddSubflow(taskData.isLogAddSubflow(parentTaskInstance.getId()));
        taskSubFlowDispatch.setCompletionState(TaskSubFlowDispatch.STATUS_NORMAL);
        taskSubFlowDispatch.setTenant(SpringSecurityUtils.getCurrentTenantId());
        taskSubFlowDispatch.setSystem(RequestSystemContextPathResolver.system());
        this.save(taskSubFlowDispatch);
    }

    /**
     * 增加同步的子流程参数
     *
     * @param parentFlowInstUuid
     * @param subFlowInstUuid
     * @param newFlow
     */
    private void addSyncSubFlowParameter(String parentFlowInstUuid, String subFlowInstUuid, NewFlow newFlow) {
        FlowService flowService = ApplicationContextHolder.getBean(FlowService.class);
        FlowInstanceParameter parameter = new FlowInstanceParameter();
        parameter.setFlowInstUuid(parentFlowInstUuid);
        parameter.setName(
                FlowConstant.SUB_FLOW.KEY_SYNC_SUB_FLOW_INST_UUIDS + Separator.UNDERLINE.getValue() + newFlow.getId());
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(parameter);
        if (CollectionUtils.isEmpty(parameters)) {
            parameter.setValue(subFlowInstUuid);
            flowService.saveFlowInstanceParameter(parameter);
        } else {
            Set<String> subFlowInstUuids = Sets.newLinkedHashSet();
            for (FlowInstanceParameter flowInstanceParameter : parameters) {
                String[] values = StringUtils.split(flowInstanceParameter.getValue(), Separator.SEMICOLON.getValue());
                subFlowInstUuids.addAll(Arrays.asList(values));
                subFlowInstUuids.add(subFlowInstUuid);
                flowInstanceParameter.setValue(StringUtils.join(subFlowInstUuids, Separator.SEMICOLON.getValue()));
                flowService.saveFlowInstanceParameter(flowInstanceParameter);
            }
        }
    }

    /**
     * 保存子流程信息
     *
     * @param taskSubFlowUuid
     * @param subTaskNode
     * @param subFlowIndex
     * @param subFlowId
     * @param flowInstance
     * @param taskInstance
     * @param subFlowInstance
     * @param todoId
     * @param todoName
     * @param monitorId
     */
    private String saveSubFlow(String taskSubFlowUuid, NewFlow newFlow, FlowInstance parentFlowInstance,
                               TaskInstance parentTaskInstance, FlowInstance subFlowInstance, String todoId, String todoName,
                               String monitorId) {
        TaskSubFlow taskSubFlow = null;
        if (StringUtils.isNotBlank(taskSubFlowUuid)) {
            taskSubFlow = taskSubFlowService.getOne(taskSubFlowUuid);
        } else {
            taskSubFlow = new TaskSubFlow();
        }
        taskSubFlow.setParentTaskInstUuid(parentTaskInstance.getUuid());
        taskSubFlow.setParentFlowInstUuid(parentFlowInstance.getUuid());
        taskSubFlow.setParentTaskId(parentTaskInstance.getId());
        taskSubFlow.setFlowInstUuid(subFlowInstance.getUuid());
        taskSubFlow.setFlowId(newFlow.getFlowId());
        taskSubFlow.setSortOrder(newFlow.getSortOrder());
        // 设置办理对象
        // taskSubFlow.setTodoId(todoId);
        taskSubFlow.setTodoName(todoName);
        // taskSubFlow.setMonitorId(monitorId);
        taskSubFlow.setIsMajor(newFlow.isMajor());
        // 办结时合并
        taskSubFlow.setIsMerge(newFlow.isReturnWithOver());
        taskSubFlow.setIsWait(newFlow.isWait());
        taskSubFlow.setIsShare(newFlow.isShare());
        taskSubFlow.setNotifyDoing(newFlow.isNotifyDoing());
        // 拷贝信息
        taskSubFlow.setCopyBotRuleId(newFlow.getCopyBotRuleId());
        // 实时同步
        taskSubFlow.setSyncBotRuleId(newFlow.getSyncBotRuleId());
        // 办结时反馈
        taskSubFlow.setReturnWithOver(newFlow.isReturnWithOver());
        // 流向反馈
        taskSubFlow.setReturnWithDirection(newFlow.isReturnWithDirection());
        // 反馈流向
        taskSubFlow.setReturnDirectionId(newFlow.getReturnDirectionId());
        // 反馈信息
        taskSubFlow.setReturnBotRuleId(newFlow.getReturnBotRuleId());
        taskSubFlow.setCopyFields(newFlow.getCopyFields());
        taskSubFlow.setReturnOverrideFields(newFlow.getReturnOverrideFields());
        taskSubFlow.setReturnAdditionFields(newFlow.getReturnAdditionFields());
        // 保存任务子流程
        taskSubFlowService.save(taskSubFlow);
        return taskSubFlow.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService#dispatchByUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void dispatchByUuid(String dispatchUuid, Map<String, Object> options) {
        Stopwatch timer = Stopwatch.createStarted();
        TaskSubFlowDispatch taskSubFlowDispatch = this.getOne(dispatchUuid);
        TaskSubFlow taskSubFlow = taskSubFlowService.getOne(taskSubFlowDispatch.getTaskSubFlowUuid());
        FlowInstance parentFlowInstance = flowService.getFlowInstance(taskSubFlowDispatch.getParentFlowInstUuid());
        TaskInstance parentTaskInstance = taskService.getTask(taskSubFlowDispatch.getParentTaskInstUuid());
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentTaskInstance.getFlowDefinition());
        NewFlow newFlow = getNewFlow(taskSubFlowDispatch, parentTaskInstance, flowDelegate);
        List<String> users = Lists.newArrayList();
        if (StringUtils.isNotBlank(taskSubFlowDispatch.getTaskUsers())) {
            users = Arrays
                    .asList(StringUtils.split(taskSubFlowDispatch.getTaskUsers(), Separator.SEMICOLON.getValue()));
        }
        String todoId = StringUtils.EMPTY;
        String todoName = taskSubFlow.getTodoName();
        List<String> monitors = Lists.newArrayListWithExpectedSize(0);
        // 当前用户信息
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = new TaskData();
        String userId = userDetails.getUserId();
        taskData.setUserId(userId);
        taskData.setUserName(userDetails.getUserName());
        taskData.setUserDetails(userDetails);
        taskData.setLogAddSubflow(parentTaskInstance.getId(), taskSubFlowDispatch.getLogAddSubflow());
        // 操作类型
        String key = StringUtils.EMPTY + userId;
        taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
        taskData.setActionType(key, WorkFlowOperation.SUBMIT);
        // 表单数据
        DyFormData subformData = getDispatchedSubformData(taskSubFlow.getFlowInstUuid());
        DyFormData dyFormData = copyDyFormDataWithBotId(newFlow.getCopyBotRuleId(), taskData,
                parentTaskInstance.getFormUuid(), parentTaskInstance.getDataUuid(), options, subformData);
        ReservedFieldUtils.setReservedFields(dyFormData, taskData);
        // 非异步分发
        String flowInstUuid = taskSubFlowDispatch.getFlowInstUuid();
        String taskSubFlowUuid = taskSubFlow.getUuid();
        taskData.put("subFlowInstUuid", flowInstUuid);
        taskData.put("taskSubFlowUuid", taskSubFlowUuid);
        // 标记为后端运行
        taskData.setDaemon(true);
        logger.info("准备流程分发数据耗时：{}", timer.stop());
        // 同步发起子流程
        dispatchSubFlow(newFlow, parentFlowInstance, parentTaskInstance, taskData, users, todoId, todoName, monitors,
                false);
        // 更新分发状态
        taskSubFlowDispatch.setCompletionState(TaskSubFlowDispatch.STATUS_COMPLETED);
        taskSubFlowDispatch.setResultMsg("success");
        this.save(taskSubFlowDispatch);
    }

    private DyFormData getDispatchedSubformData(String flowInstUuid) {
        // 取相应的主流程的对应的从表数据
        String subFormDataUuidOfParent = null;
        String subFormUuidOfParent = null;
        DyFormData subFormDataOfParent = null;
        List<FlowInstanceParameter> params = flowInstanceParameterService.getByFlowInstanceUuid(flowInstUuid);
        if (CollectionUtils.isNotEmpty(params)) {
            for (FlowInstanceParameter param : params) {
                if (StringUtils.equals("parentSubFormUuid", param.getName())) {
                    subFormUuidOfParent = param.getValue();
                } else if (StringUtils.equals("parentSubFormDataUuid", param.getName())) {
                    subFormDataUuidOfParent = param.getValue();
                }
            }
        }
        if (subFormUuidOfParent != null && subFormDataUuidOfParent != null) {
            subFormDataOfParent = dyFormFacade.getDyFormData(subFormUuidOfParent, subFormDataUuidOfParent);
        }
        return subFormDataOfParent;
    }

    /**
     * @param taskSubFlowDispatch
     * @param flowDelegate
     * @return
     */
    private NewFlow getNewFlow(TaskSubFlowDispatch taskSubFlowDispatch, TaskInstance parentTaskInstance,
                               FlowDelegate flowDelegate) {
        String newFlowId = taskSubFlowDispatch.getNewFlowId();
        SubTaskNode subTaskNode = (SubTaskNode) flowDelegate.getTaskNode(parentTaskInstance.getId());
        List<NewFlow> newFlows = subTaskNode.getNewFlows();
        for (NewFlow newFlow : newFlows) {
            if (StringUtils.equals(newFlowId, newFlow.getId())) {
                return newFlow;
            }
        }
        logger.error("要分发的流程ID：" + newFlowId + " 找不到对应的新流程" + "。父流程定义ID是" + flowDelegate.getFlow().getId()
                + " 对应的环节子流程ID是：" + parentTaskInstance.getId());
        throw new BusinessException("要分发的流程ID：" + newFlowId + " 找不到对应的新流程" + "。父流程定义ID是"
                + flowDelegate.getFlow().getId() + " 对应的环节子流程ID是：" + parentTaskInstance.getId());
    }

    /**
     * @param botRuleId
     * @param taskData
     * @param formUuid
     * @param dataUuid
     */
    @SuppressWarnings("unchecked")
    private DyFormData copyDyFormDataWithBotId(String botRuleId, TaskData taskData, String formUuid, String dataUuid,
                                               Map<String, Object> options, DyFormData subformData) {
        String formDataKey = "dyFormData_" + formUuid + "_" + dataUuid;
        DyFormData sourceFormData = (DyFormData) options.get(formDataKey);
        if (sourceFormData == null) {
            sourceFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            options.put(formDataKey, sourceFormData);
        }
        Set<BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotFromParam botFromParam = new BotFromParam(dataUuid, formUuid, sourceFormData);
        froms.add(botFromParam);
        if (subformData != null) {
            froms.add(new BotFromParam(subformData.getDataUuid(), subformData.getFormUuid(), subformData));
        }
        BotParam botParam = new BotParam(botRuleId, froms);
        botParam.setFroms(froms);
        BotResult botResult = null;
        try {
            Map<String, Object> jsonBody = Maps.newHashMap();
            jsonBody.put("dispatchedFormData", subformData != null ? subformData.getFormDataOfMainform() : sourceFormData.getFormDataOfMainform());
            botParam.setJsonBody(jsonBody);
            botResult = botFacadeService.startBot(botParam);
        } catch (Exception e) {
            logger.error("子流程分发时单据转换出错！", e);
            if (e instanceof BotException) {
                throw e;
            } else {
                throw new WorkFlowException("子流程分发时单据转换出错！" + e.getMessage(), e);
            }
        }
        Object data = botResult.getData();
        String subflowFormUuid = null;
        String subflowDataUuid = null;
        DyFormData dyFormData = null;
        if (botResult.getDyformData() instanceof DyFormData) {
            dyFormData = (DyFormData) botResult.getDyformData();
        }
        if (data instanceof Map) {
            Map<String, Object> formData = (Map<String, Object>) data;
            subflowFormUuid = (String) formData.get("form_uuid");
            subflowDataUuid = botResult.getDataUuid();
            if (StringUtils.isBlank(subflowFormUuid)) {
                throw new WorkFlowException("子流程使用的单据转换规则[" + botRuleId + "]配置异常，没有勾选“保存单据”，无法分发，请联系管理员修改");
            }
            // DyFormData subflowFormData = dyFormFacade.getDyFormData(subflowFormUuid,
            // subflowDataUuid);
        } else {
            throw new WorkFlowException("子流程分发失败，无法取到流程数据转换后的数据！");
        }
        taskData.setFormUuid(subflowFormUuid);
        taskData.setDataUuid(subflowDataUuid);
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(subflowFormUuid, subflowDataUuid);
        }
        taskData.setDyFormData(subflowDataUuid, dyFormData);
        return dyFormData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService#list2Dispatching(int)
     */
    @Override
    public List<TaskSubFlowDispatch> list2Dispatching() {
        String hql1 = "select t.uuid as uuid, t.creator as creator, t.system as system, t.parentTaskInstUuid as parentTaskInstUuid, t.parentFlowInstUuid as parentFlowInstUuid from TaskSubFlowDispatch t where t.completionState = :completionState order by t.createTime asc";
        String hql2 = "select t.uuid as uuid, t.creator as creator, t.system as system, t.parentTaskInstUuid as parentTaskInstUuid, t.parentFlowInstUuid as parentFlowInstUuid from TaskSubFlowDispatch t where t.completionState = :completionState and t.parentFlowInstUuid = :parentFlowInstUuid order by t.createTime asc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("completionState", TaskSubFlowDispatch.STATUS_NORMAL);
        List<TaskSubFlowDispatch> taskSubFlowDispatchs = this.dao.listByHQLAndPage(hql1, values, new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(taskSubFlowDispatchs)) {
            values.put("parentFlowInstUuid", taskSubFlowDispatchs.get(0).getParentFlowInstUuid());
            taskSubFlowDispatchs = this.dao.listByHQLAndPage(hql2, values, new PagingInfo(1, Integer.MAX_VALUE));
        }
        return taskSubFlowDispatchs;
    }

    /**
     * @return
     */
    @Override
    public List<String> listParentFlowInstUuidOfDispatching() {
        String hql = "select distinct t.parentFlowInstUuid as parentFlowInstUuid from TaskSubFlowDispatch t where t.completionState = :completionState";
        Map<String, Object> values = Maps.newHashMap();
        values.put("completionState", TaskSubFlowDispatch.STATUS_NORMAL);
        return this.dao.listCharSequenceByHQL(hql, values);
    }

    /**
     * @param parentFlowInstUuid
     * @return
     */
    @Override
    public List<TaskSubFlowDispatch> list2DispatchingByParenFlowInstUuid(String parentFlowInstUuid) {
        String hql = "select t.uuid as uuid, t.creator as creator, t.parentTaskInstUuid as parentTaskInstUuid, t.parentFlowInstUuid as parentFlowInstUuid from TaskSubFlowDispatch t where t.completionState = :completionState and t.parentFlowInstUuid = :parentFlowInstUuid order by t.createTime asc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("completionState", TaskSubFlowDispatch.STATUS_NORMAL);
        values.put("parentFlowInstUuid", parentFlowInstUuid);
        List<TaskSubFlowDispatch> taskSubFlowDispatchs = this.dao.listByHQLAndPage(hql, values, new PagingInfo(1, Integer.MAX_VALUE));
        return taskSubFlowDispatchs;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService#markDispatchErrorByUuid(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void markDispatchErrorByUuid(String uuid, String message) {
        String hql = "update TaskSubFlowDispatch t set t.resultMsg = :resultMsg, t.completionState = :completionState where t.uuid = :uuid and t.completionState = 0";
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        if (StringUtils.isNotBlank(message)) {
            values.put("resultMsg", message);
        } else {
            values.put("resultMsg", "error");
        }
        values.put("completionState", TaskSubFlowDispatch.STATUS_STOP);
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService#countDispatchingByParentFlowInstUuid(java.lang.String)
     */
    @Override
    public long countDispatchingByParentFlowInstUuid(String parentFlowInstUuid) {
        TaskSubFlowDispatch entity = new TaskSubFlowDispatch();
        entity.setParentFlowInstUuid(parentFlowInstUuid);
        entity.setCompletionState(TaskSubFlowDispatch.STATUS_NORMAL);
        return this.dao.countByEntity(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService#countDispatchingByParentTaskInstUuid(java.lang.String)
     */
    @Override
    public long countDispatchingByParentTaskInstUuid(String parentTaskInstUuid) {
        TaskSubFlowDispatch entity = new TaskSubFlowDispatch();
        entity.setParentTaskInstUuid(parentTaskInstUuid);
        entity.setCompletionState(TaskSubFlowDispatch.STATUS_NORMAL);
        return this.dao.countByEntity(entity);
    }

    /**
     * 根据主流程实例UUID，获取分发中、分发失败的子流程数量
     *
     * @param parentFlowInstUuid
     * @return
     */
    @Override
    public long countDispatchingAndErrorByParentFlowInstUuid(String parentFlowInstUuid) {
        Map<String, Object> values = Maps.newHashMap();
        List<Integer> completionStates = Lists.newArrayList(TaskSubFlowDispatch.STATUS_NORMAL, TaskSubFlowDispatch.STATUS_STOP);
        values.put("parentFlowInstUuid", parentFlowInstUuid);
        values.put("completionStates", completionStates);
        String hql = "from TaskSubFlowDispatch t where t.parentFlowInstUuid = :parentFlowInstUuid and t.completionState in(:completionStates)";
        return this.dao.countByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService#resendByParentTaskInstUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void resendByParentTaskInstUuid(String parentTaskInstUuid) {
        TaskSubFlowDispatch entity = new TaskSubFlowDispatch();
        entity.setParentTaskInstUuid(parentTaskInstUuid);
        entity.setCompletionState(TaskSubFlowDispatch.STATUS_STOP);
        List<TaskSubFlowDispatch> entities = this.dao.listByEntity(entity);
        for (TaskSubFlowDispatch taskSubFlowDispatch : entities) {
            taskSubFlowDispatch.setCompletionState(TaskSubFlowDispatch.STATUS_NORMAL);
            taskSubFlowDispatch.setResultMsg("重新补发！");
        }
        this.dao.saveAll(entities);
    }

    @Override
    public List<TaskSubFlowDispatch> getByFlowInstUuid(String flowInstUuid) {
        return this.dao.listByFieldEqValue("flowInstUuid", flowInstUuid);
    }

    /**
     * @param flowInstUuid
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        String hql = "delete from TaskSubFlowDispatch t where t.flowInstUuid = :flowInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(hql, values);
    }

    /**
     * @param parentFlowInstUuid
     * @return
     */
    @Override
    public List<TaskSubFlowDispatch> listByParentFlowInstUuid(String parentFlowInstUuid) {
        return this.dao.listByFieldEqValue("parentFlowInstUuid", parentFlowInstUuid);
    }

    @Override
    @Transactional
    public void updateDispatchMsgByUuid(String uuid, String resultMsg) {
        String hql = "update TaskSubFlowDispatch t set t.resultMsg = :resultMsg where t.uuid = :uuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        values.put("resultMsg", resultMsg);
        this.dao.updateByHQL(hql, values);
    }

}

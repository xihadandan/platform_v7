/*
 * @(#)2014-2-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dispatcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bot.exception.BotException;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotParam.BotFromParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.access.SidGranularityResolver;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.core.handler.SubTaskHandler;
import com.wellsoft.pt.bpm.engine.delegate.FlowInstanceDelegate;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.exception.SubFlowNotFoundException;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.timer.support.TimerHelper;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUnit;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.dto.OrganizationDto;
import com.wellsoft.pt.rule.engine.RuleEngine;
import com.wellsoft.pt.rule.engine.RuleEngineFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.text.ParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-2-25.1	zhulh		2014-2-25		Create
 * </pre>
 * @date 2014-2-25
 */
@Service
@Transactional
public class DefaultDispatcherFlowResolver extends AbstractDispatcherFlowResolver {

    @Autowired
    protected SubTaskHandler subTaskHandler;

    @Autowired
    protected FlowService flowService;

    @Autowired
    protected DyFormFacade dyFormFacade;

    @Autowired
    protected WorkflowOrgService workflowOrgService;

    @Autowired
    private SidGranularityResolver sidGranularityResolver;

    @Autowired
    private IdentityResolverStrategy identityResolverStrategy;

    @Autowired
    private BotFacadeService botFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.dispatcher.DispatcherFlowResolver#resolve(com.wellsoft.pt.bpm.engine.context.ExecutionContext, com.wellsoft.pt.bpm.engine.node.SubTaskNode)
     */
    @Override
    public void resolve(ExecutionContext context, SubTaskNode subTaskNode) {
        // 分阶段办理
        if (subTaskNode.isCheckInStage() && isInStages(context, subTaskNode)) {
            startSubTaskInStages(context, subTaskNode);
        } else {
            // 进入子流程
            List<NewFlow> newFlows = subTaskNode.getNewFlows();
            for (int index = 0; index < newFlows.size(); index++) {
                startSubTask(context, context.getFlowInstance(), context.getToken().getTask(), subTaskNode,
                        newFlows.get(index), index);
            }
        }
        // 子流程启动完后，初始化前后置关系
        subTaskHandler.initSubTaskRelations(context, subTaskNode);
        // 获取发起子流程个数
        List<String> subFlowInstUUids = context.getToken().getTaskData().getSubmitResult().getSubFlowInstUUids();
        if (CollectionUtils.isEmpty(subFlowInstUUids)) {
            logger.warn("环节实例[{}]没有发起任何子流程！", context.getToken().getTask().getUuid());
            throw new RuntimeException("环节实例没有发起任何子流程！");
        } else {
            logger.info("环节实例[{}]发起任务{}个子流程！", context.getToken().getTask().getUuid(), subFlowInstUUids.size());
        }

        // 跟进人员的流程参数
        addSubTaskMonitors(context, subTaskNode);
    }

    /**
     * 分阶段办理
     *
     * @param executionContext
     * @param subTaskNode
     */
    private void startSubTaskInStages(ExecutionContext executionContext, SubTaskNode subTaskNode) {
        FlowInstance parentFlowInstance = executionContext.getFlowInstance();
        TaskInstance parentTaskInstance = executionContext.getToken().getTask();
        TaskData taskData = executionContext.getToken().getTaskData();
        // 阶段划分
        String stageDivisionFormId = subTaskNode.getStageDivisionFormId();
        // 阶段状态
        String stageStateField = subTaskNode.getStageState();
        String dataUuid = taskData.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        List<DyFormData> stageDivisionFormDatas = dyFormData.getDyformDatasByFormId(stageDivisionFormId);
        DyFormData stageDyformData = null;
        for (DyFormData stageDivisionFormData : stageDivisionFormDatas) {
            if (!stageDivisionFormData.isFieldExist(stageStateField)) {
                continue;
            }
            String stageState = ObjectUtils.toString(stageDivisionFormData.getFieldValue(stageStateField),
                    StringUtils.EMPTY);
            // 阶段已完成，忽略掉
            if (StringUtils.equals(stageState, NewFlowStages.STAGE_STATE_COMPLETED)) {
                continue;
            }
            stageDyformData = stageDivisionFormData;
            break;
        }
        // 阶段处理的表单数据为空，直接返回
        if (stageDyformData == null) {
            return;
        }

        // 根据阶段处理的表单数据，发起子流程
        List<NewFlowStage> stages = subTaskNode.getStages();
        for (NewFlowStage stage : stages) {
            String newFlowId = stage.getNewFlowId();
            NewFlow newFlow = getNewFlowById(subTaskNode, newFlowId);
            if (newFlow == null) {
                continue;
            }
            // 实例数量：1、单一实例; 2、按办理人生成
            String createInstanceWay = stage.getCreateInstanceWay();
            // 办理人字段
            NewFlowTaskUsers newFlowTaskUsers = null;
            // 发起子流程时外部传入的办理人
            Set<String> subTaskUserIds = taskData.getTaskUsers(newFlow.getId());
            if (subTaskUserIds.isEmpty()) {
                String taskUsersField = stage.getTaskUsers();
                String taskUsers = (String) stageDyformData.getFieldValue(taskUsersField);
                newFlowTaskUsers = getNewFlowTaskUsers(taskUsers, subTaskNode, newFlow, createInstanceWay, executionContext);
            } else {
                newFlowTaskUsers = getNewFlowTaskUsers(StringUtils.join(subTaskUserIds, Separator.SEMICOLON.getValue()),
                        subTaskNode, newFlow, createInstanceWay, executionContext);
            }
            // 过滤添加承办操作附加的办理人
            newFlowTaskUsers = filterAdditionalNewFlowTaskUsersIfRequire(dataUuid, taskData, parentTaskInstance,
                    parentFlowInstance, newFlow, newFlowTaskUsers);

            List<String> users = newFlowTaskUsers.getUsers();
            List<String> userNames = newFlowTaskUsers.getUserNames();
            List<String> monitorIds = Lists.newArrayList();

            String subFlowDefId = newFlow.getFlowId();

            if (StringUtils.equals(createInstanceWay, FlowConstant.CREATE_INSTANCE_WAY.SINGLETON)) {
                List<String> taskTodoUsers = resolveTaskTodoUsers(users, userNames, newFlowTaskUsers.getType(),
                        subTaskNode, newFlow);
                createStageTaskInstance(executionContext, parentFlowInstance, parentTaskInstance, taskData, dataUuid,
                        dyFormData, stageDyformData, stage, newFlow, taskTodoUsers, userNames, monitorIds,
                        subFlowDefId);
            } else {
                // 按照办理人多实例
                for (int index = 0; index < users.size(); index++) {
                    List<String> userNames2 = Arrays.asList(new String[]{userNames.get(index)});
                    List<String> taskTodoUsers = resolveTaskTodoUsers(Arrays.asList(new String[]{users.get(index)}),
                            userNames2, newFlowTaskUsers.getType(), subTaskNode, newFlow);
                    createStageTaskInstance(executionContext, parentFlowInstance, parentTaskInstance, taskData,
                            dataUuid, dyFormData, stageDyformData, stage, newFlow, taskTodoUsers, userNames2,
                            monitorIds, subFlowDefId);
                }
            }
        }

        // 更新表单阶段状态
        stageDyformData.setFieldValue(stageStateField, NewFlowStages.STAGE_STATE_DOING);
        // dyFormData.doForceCover();
        // dyFormFacade.saveFormData(dyFormData);
        taskData.addUpdatedDyFormData(dataUuid, dyFormData);
    }

    /**
     * 如何描述该方法
     *
     * @param executionContext
     * @param parentFlowInstance
     * @param parentTaskInstance
     * @param taskData
     * @param dataUuid
     * @param dyFormData
     * @param stageDyformData
     * @param stage
     * @param newFlow
     * @param users
     * @param userNames
     * @param subFlowDefId
     */
    private void createStageTaskInstance(ExecutionContext executionContext, FlowInstance parentFlowInstance,
                                         TaskInstance parentTaskInstance, TaskData taskData, String dataUuid, DyFormData dyFormData,
                                         DyFormData stageDyformData, NewFlowStage stage, NewFlow newFlow, List<String> users, List<String> userNames,
                                         List<String> monitorIds, String subFlowDefId) {
        copyMainFormData(newFlow, subFlowDefId, taskData, dyFormData.getFormUuid(), dataUuid, dyFormData);
        // 复制任务数据
        TaskData subTaskData = copyTaskData(taskData);
        // 办理时限
        String limitTimeField = stage.getLimitTime();
        if (StringUtils.isNotBlank(limitTimeField) && stageDyformData.isFieldExist(limitTimeField)) {
            String limitTime = (String) stageDyformData.getFieldValue(limitTimeField);
            if (StringUtils.isNotBlank(limitTime)) {
                Date currentDate = null;
                Date limitDate = null;
                try {
                    currentDate = TimerHelper.convertTime(Calendar.getInstance().getTime(), TimerUnit.DATE);
                    limitDate = TimerHelper.convertTime(DateUtils.parse(limitTime), TimerUnit.DATE);
                    if (currentDate.after(limitDate)) {
                        throw new WorkFlowException("办理时限不能小于当前时间！");
                    }
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                    throw new WorkFlowException("子流程办理时限解析错误！");
                }
                String flowDefId = newFlow.getFlowId();
                subTaskData.setLimitType(flowDefId, TaskTimer.LIMIT_TIME_TYPE_FORM_DATE);
                subTaskData.setLimitTime(flowDefId, limitTime);
                subTaskData.setLimitUnit(flowDefId, TimerUnit.DATE);
            }
        }
        FlowInstanceDelegate flowInstanceDelegate = new FlowInstanceDelegate();
        String subFlowInstUuid = flowInstanceDelegate.startSubFlowInstance(newFlow, parentFlowInstance,
                parentTaskInstance, subTaskData, users, StringUtils.join(users, Separator.SEMICOLON.getValue()),
                StringUtils.join(userNames, Separator.SEMICOLON.getValue()), monitorIds);
        executionContext.getToken().getTaskData().getSubmitResult().getSubFlowInstUUids().add(subFlowInstUuid);
        executionContext.getToken().getTaskData().getSubmitResult().setSubFlowSyncStartd(newFlow.isWait());

        // 阶段使用的表单UUID
        FlowInstanceParameter stageFormUuid = new FlowInstanceParameter();
        stageFormUuid.setFlowInstUuid(subFlowInstUuid);
        stageFormUuid.setName("stageFormUuid_" + parentTaskInstance.getUuid());
        stageFormUuid.setValue(stageDyformData.getFormUuid());
        flowService.saveFlowInstanceParameter(stageFormUuid);
        // 阶段使用的数据UUID
        FlowInstanceParameter stageDataUuid = new FlowInstanceParameter();
        stageDataUuid.setFlowInstUuid(subFlowInstUuid);
        stageDataUuid.setName("stageDataUuid_" + parentTaskInstance.getUuid());
        stageDataUuid.setValue(stageDyformData.getDataUuid());
        flowService.saveFlowInstanceParameter(stageDataUuid);
    }

    /**
     * @param context
     * @param subTaskNode
     * @return
     */
    private void addSubTaskMonitors(ExecutionContext context, SubTaskNode subTaskNode) {
        List<UserUnitElement> subTaskMonitorElements = subTaskNode.getSubTaskMonitors();
        if (CollectionUtils.isEmpty(subTaskMonitorElements)) {
            return;
        }
        List<FlowUserSid> followUpUserSids = identityResolverStrategy.resolve(subTaskNode, context.getToken(),
                subTaskMonitorElements, ParticipantType.MonitorUser);
        List<String> followUpUserIds = IdentityResolverStrategy.resolveAsOrgIds(followUpUserSids);
        if (CollectionUtils.isEmpty(followUpUserIds)) {
            return;
        }

        FlowInstanceParameter example = new FlowInstanceParameter();
        String flowInstUuid = context.getToken().getFlowInstance().getUuid();
        String name = FlowConstant.SUB_FLOW.KEY_FOLLOW_UP_USERS + Separator.UNDERLINE.getValue() + subTaskNode.getId();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(StringUtils.join(followUpUserIds, Separator.SEMICOLON.getValue()));
            flowService.saveFlowInstanceParameter(example);
        } else {
            for (FlowInstanceParameter flowInstanceParameter : parameters) {
                flowInstanceParameter.setValue(StringUtils.join(followUpUserIds, Separator.SEMICOLON.getValue()));
                flowService.saveFlowInstanceParameter(flowInstanceParameter);
            }
        }
    }

    /**
     * @param subTaskNode
     * @param id
     * @return
     */
    private NewFlow getNewFlowById(SubTaskNode subTaskNode, String id) {
        List<NewFlow> newFlows = subTaskNode.getNewFlows();
        for (NewFlow newFlow : newFlows) {
            if (StringUtils.equals(newFlow.getId(), id)) {
                return newFlow;
            }
        }
        return null;
    }

    /**
     * @param context
     * @param subTaskNode
     * @return
     */
    private boolean isInStages(ExecutionContext context, SubTaskNode subTaskNode) {
        // 分阶段办理判断条件
        String inStagesCondition = subTaskNode.getInStagesCondition();
        if (StringUtils.isBlank(inStagesCondition)) {
            return false;
        }
        TaskData taskData = context.getToken().getTaskData();
        RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
        DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
        ruleEngine.setVariable("dyFormData", dyFormData);
        ruleEngine.setVariable("dyform", dyFormData.getFormDataOfMainform());
        String exp = inStagesCondition;
        String scriptText = "if (" + exp + "){ set conditionResult = true end } end";
        ruleEngine.execute(scriptText);
        Object conditionResult = ruleEngine.getVariable("conditionResult");
        return Boolean.TRUE.equals(conditionResult);
    }

    /**
     * 启动子流程
     *
     * @param parentFlowInstance
     * @param parentTaskInstance
     * @param subTaskNode
     */
    /* modified by huanglinchuan 2014.10.24 begin */
    public void startSubTask(ExecutionContext executionContext, FlowInstance parentFlowInstance,
                             TaskInstance parentTaskInstance, SubTaskNode subTaskNode, NewFlow newFlow, int subFlowIndex) {
        // 子流程定义ID
        String subFlowDefId = newFlow.getFlowId();
        // 获取子流程中的新流程ID
        if (StringUtils.isBlank(subFlowDefId)) {
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("excludeFlowId", executionContext.getToken().getFlowInstance().getId());
            variables.put("subTaskId", subTaskNode.getId());
            variables.put("submitButtonId", executionContext.getToken().getTaskData().getSubmitButtonId());
            throw new SubFlowNotFoundException(variables);
        }

        // 流程提交的环节
        String toTaskId = newFlow.getToTaskId();
        if (StringUtils.isBlank(toTaskId)) {
            throw new WorkFlowException("提交的环节不能为空！");
        }

        // 创建方式
        String createWay = newFlow.getCreateWay();

        switch (createWay) {
            case FlowConstant.CREATE_WAY.DYFORM:
                // 表单字段
                createSubFlowInstanceByDyform(executionContext, parentFlowInstance, parentTaskInstance, subTaskNode,
                        newFlow, subFlowIndex);
                break;
            case FlowConstant.CREATE_WAY.MAIN_FORM:
                // 主表
                createSubFlowInstanceByMainform(executionContext, parentFlowInstance, parentTaskInstance, subTaskNode,
                        newFlow, subFlowIndex);
                break;
            case FlowConstant.CREATE_WAY.SUBFORM:
                // 从表
                createSubFlowInstanceBySubform(executionContext, parentFlowInstance, parentTaskInstance, subTaskNode,
                        newFlow, subFlowIndex, StringUtils.EMPTY);
                break;
            case FlowConstant.CREATE_WAY.CUSTOM:
                // 自定义接口
                createSubFlowInstanceByCustomInterface(executionContext, parentFlowInstance, parentTaskInstance,
                        subTaskNode, newFlow, subFlowIndex);
                break;

            default:
                break;
        }
    }

    /**
     * @param executionContext
     * @param flowInstance
     * @param parentTask
     * @param subTaskNode
     * @param newFlow
     * @return
     */
    private boolean checkNewFlowCondition(ExecutionContext executionContext, FlowInstance parentFlowInstance,
                                          TaskInstance parentTaskInstance, SubTaskNode subTaskNode, NewFlow newFlow,
                                          List<String> taskUsers, List<String> toCheckOrgIds, String createInstanceWay) {
        String conditions = newFlow.getConditions();
        if (StringUtils.isBlank(conditions)) {
            return true;
        }

        // 获取条件表达式
        NewFlowConditionJson newFlowConditionJson = JsonUtils.json2Object(conditions, NewFlowConditionJson.class);
        String condition = newFlowConditionJson.getCondition();
        if (StringUtils.isBlank(condition)) {
            return true;
        }

        // 准备数据
        TaskData taskData = executionContext.getToken().getTaskData();
        String formUuid = parentFlowInstance.getFormUuid();
        String dataUuid = parentFlowInstance.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            taskData.setDyFormData(dataUuid, dyFormData);
        }

        // 执行条件表达式
        RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
        ruleEngine.setVariable("token", executionContext.getToken());
        ruleEngine.setVariable("node", subTaskNode);
        ruleEngine.setVariable("businessType", subTaskNode.getBusinessType());
        ruleEngine.setVariable("businessRole", subTaskNode.getBusinessRole());
        ruleEngine.setVariable("taskUsers", taskUsers);
        ruleEngine.setVariable("taskOrgIds", toCheckOrgIds);
        ruleEngine.setVariable("createInstanceWay", createInstanceWay);
        ruleEngine.setVariable("dyFormData", dyFormData);
        ruleEngine.setVariable("dyform", dyFormData.getFormDataOfMainform());
        String exp = condition;
        String scriptText = "if (" + exp + "){ set conditionResult = true end } end";
        ruleEngine.execute(scriptText);
        Object conditionResult = ruleEngine.getVariable("conditionResult");
        return Boolean.TRUE.equals(conditionResult);
    }

    /**
     * @param executionContext
     * @param flowInstance
     * @param parentTask
     * @param subTaskNode
     * @param newFlow
     * @param subFlowIndex
     */
    private void createSubFlowInstanceByDyform(ExecutionContext executionContext, FlowInstance flowInstance,
                                               TaskInstance parentTask, SubTaskNode subTaskNode, NewFlow newFlow, int subFlowIndex) {
        // 从主表创建
        if (newFlow.isMainFormCreateWay()) {
            createSubFlowInstanceByMainform(executionContext, flowInstance, parentTask, subTaskNode, newFlow,
                    subFlowIndex);
        } else {
            // 从表字段创建
            createSubFlowInstanceBySubform(executionContext, flowInstance, parentTask, subTaskNode, newFlow,
                    subFlowIndex, newFlow.getCreateInstanceFormId());
        }
    }

    /* add by huanglinchuan 2014.10.24 begin */

    /**
     * @param executionContext
     * @param flowInstance
     * @param parentTask
     * @param subTaskNode
     * @param newFlow
     * @param subFlowIndex
     */
    private void createSubFlowInstanceByMainform(ExecutionContext executionContext, FlowInstance parentFlowInstance,
                                                 TaskInstance parentTaskInstance, SubTaskNode subTaskNode, NewFlow newFlow, int subFlowIndex) {
        // 子流程定义ID
        String subFlowDefId = newFlow.getFlowId();
        // 复制任务数据
        TaskData taskData = copyTaskData(executionContext.getToken().getTaskData());
        // 是否异步执行(是否等待)
        taskData.setIsAsync(subFlowDefId, subTaskNode.isAsync());

        // 主流程表单定义UUID
        String formUuid = executionContext.getToken().getTaskData().getFormUuid();
        // 主流程表单数据UUID
        String dataUuid = executionContext.getToken().getTaskData().getDataUuid();
        // 主流程表单数据
        DyFormData dyFormData = getDyformData(executionContext);

        // 流程提交的环节
        String toTaskId = newFlow.getToTaskId();
        if (StringUtils.isBlank(toTaskId)) {
            throw new WorkFlowException("提交的环节不能为空！");
        }

        /* modified by huanglinchuan 2014.10.24 begin */
        // 实例数量：1、单一实例; 2、按办理人生成
        String createInstanceWay = newFlow.getCreateInstanceWay();
        if (StringUtils.isBlank(createInstanceWay)) {
            createInstanceWay = FlowConstant.CREATE_INSTANCE_WAY.SINGLETON;
        }

        // 办理人字段
        String taskUsersField = newFlow.getTaskUsers();
        if (StringUtils.isBlank(taskUsersField)) {
            // 办理人字段必须指定,否则不处理
            return;
        }

        // 办理人字段
        NewFlowTaskUsers newFlowTaskUsers = null;
        // 发起子流程时外部传入的办理人
        Set<String> subTaskUserIds = taskData.getTaskUsers(newFlow.getId());
        List<String> toCheckOrgIds = Lists.newArrayList();
        if (CollectionUtils.isEmpty(subTaskUserIds)) {
            String taskUsers = (String) dyFormData.getFieldValue(taskUsersField);
            newFlowTaskUsers = getNewFlowTaskUsers(taskUsers, subTaskNode, newFlow, createInstanceWay, executionContext);
            // 标记使用表单字段设置办理人
            taskData.setUseFormFieldUsers(parentTaskInstance.getId(), true);
            if (StringUtils.isNotBlank(taskUsers)) {
                toCheckOrgIds.addAll(Arrays.asList(taskUsers.split(Separator.SEMICOLON.getValue())));
            }
        } else {
            newFlowTaskUsers = getNewFlowTaskUsers(StringUtils.join(subTaskUserIds, Separator.SEMICOLON.getValue()),
                    subTaskNode, newFlow, createInstanceWay, executionContext);
            toCheckOrgIds.addAll(subTaskUserIds);
        }
        // 过滤添加承办操作附加的办理人
        newFlowTaskUsers = filterAdditionalNewFlowTaskUsersIfRequire(dataUuid, taskData, parentTaskInstance,
                parentFlowInstance, newFlow, newFlowTaskUsers);

        List<String> monitorIds = Lists.newArrayList();

        // 单一实例
        if (StringUtils.equals(createInstanceWay, FlowConstant.CREATE_INSTANCE_WAY.SINGLETON)) {
            List<String> toCheckUsers = newFlowTaskUsers.getUsers();
            // 分发条件判断
            if (!checkNewFlowCondition(executionContext, parentFlowInstance, parentTaskInstance, subTaskNode, newFlow,
                    toCheckUsers, toCheckOrgIds, createInstanceWay)) {
                return;
            }

            List<String> userNames = newFlowTaskUsers.getUserNames();
            List<String> taskTodoUsers = resolveTaskTodoUsers(toCheckUsers, userNames, newFlowTaskUsers.getType(),
                    subTaskNode, newFlow);

            // 使用表单字段，但办理人为空，忽略掉
            if (CollectionUtils.isEmpty(taskTodoUsers) && taskData.isUseFormFieldUsers(parentTaskInstance.getId())) {
                return;
            }

            // 复制表单数据
            copyMainFormData(newFlow, subFlowDefId, taskData, formUuid, dataUuid, dyFormData);
            // 复制任务数据
            TaskData subTaskData = copyTaskData(taskData);
            FlowInstanceDelegate flowInstanceDelegate = new FlowInstanceDelegate();
            String subFlowInstUuid = flowInstanceDelegate.startSubFlowInstance(newFlow,
                    executionContext.getFlowInstance(), executionContext.getToken().getTask(), subTaskData,
                    taskTodoUsers, StringUtils.join(toCheckUsers, Separator.SEMICOLON.getValue()),
                    StringUtils.join(userNames, Separator.SEMICOLON.getValue()), monitorIds);
            executionContext.getToken().getTaskData().getSubmitResult().getSubFlowInstUUids().add(subFlowInstUuid);
            executionContext.getToken().getTaskData().getSubmitResult().setSubFlowSyncStartd(newFlow.isWait());
        } else if (StringUtils.equals(createInstanceWay, FlowConstant.CREATE_INSTANCE_WAY.BY_USER)) {
            List<String> users = newFlowTaskUsers.getUsers();
            List<String> userNames = newFlowTaskUsers.getUserNames();
            // 按照办理人多实例
            for (int index = 0; index < users.size(); index++) {
                String userId = users.get(index);
                List<String> toCheckUsers = Lists.newArrayList();
                toCheckUsers.add(userId);
                // 分发条件判断
                if (!checkNewFlowCondition(executionContext, parentFlowInstance, parentTaskInstance, subTaskNode,
                        newFlow, toCheckUsers, toCheckOrgIds, createInstanceWay)) {
                    continue;
                }

                String userName = userNames.get(index);
                List<String> toCheckNames = Lists.newArrayList();
                toCheckNames.add(userName);
                List<String> taskTodoUsers = resolveTaskTodoUsers(toCheckUsers, toCheckNames,
                        newFlowTaskUsers.getType(), subTaskNode, newFlow);

                // 复制表单数据
                copyMainFormData(newFlow, subFlowDefId, taskData, formUuid, dataUuid, dyFormData);
                // 复制任务数据
                TaskData subTaskData = copyTaskData(taskData);
                FlowInstanceDelegate flowInstanceDelegate = new FlowInstanceDelegate();
                String subFlowInstUuid = flowInstanceDelegate.startSubFlowInstance(newFlow,
                        executionContext.getFlowInstance(), executionContext.getToken().getTask(), subTaskData,
                        taskTodoUsers, userId, userName, monitorIds);
                executionContext.getToken().getTaskData().getSubmitResult().getSubFlowInstUUids().add(subFlowInstUuid);
                executionContext.getToken().getTaskData().getSubmitResult().setSubFlowSyncStartd(newFlow.isWait());
            }
        }

    }

    /**
     * @param executionContext
     * @param flowInstance
     * @param parentTask
     * @param subTaskNode
     * @param newFlow
     * @param subFlowIndex
     * @param formId
     */
    private void createSubFlowInstanceBySubform(ExecutionContext executionContext, FlowInstance parentFlowInstance,
                                                TaskInstance parentTaskInstance, SubTaskNode subTaskNode, NewFlow newFlow, int subFlowIndex,
                                                String formId) {
        // 子流程定义ID
        String subFlowDefId = newFlow.getFlowId();
        // 复制任务数据
        TaskData taskData = copyTaskData(executionContext.getToken().getTaskData());
        // 是否异步执行(是否等待)
        taskData.setIsAsync(subFlowDefId, subTaskNode.isAsync());

        // 主流程表单定义UUID
        String formUuid = executionContext.getToken().getTaskData().getFormUuid();
        // 主流程表单数据UUID
        String dataUuid = executionContext.getToken().getTaskData().getDataUuid();
        // 主流程表单数据
        DyFormData dyFormData = getDyformData(executionContext);

        // 流程提交的环节
        String toTaskId = newFlow.getToTaskId();
        if (StringUtils.isBlank(toTaskId)) {
            throw new WorkFlowException("提交的环节不能为空！");
        }

        String subformId = StringUtils.isNotBlank(formId) ? formId : newFlow.getSubformId();
        // 实例数量：1、单一实例; 2、按办理人生成
        String createInstanceWay = newFlow.getCreateInstanceWay();
        if (StringUtils.isBlank(createInstanceWay)) {
            createInstanceWay = FlowConstant.CREATE_INSTANCE_WAY.SINGLETON;
        }

        // 办理人字段
        String taskUsersField = newFlow.getTaskUsers();
        if (StringUtils.isBlank(taskUsersField)) {
            // 班里人必须指定,否则不处理
            return;
        }

        if (StringUtils.isBlank(subformId)) {
            return;
        }
        String subFormUuid = dyFormFacade.getFormUuidById(subformId);
        if (StringUtils.isBlank(subFormUuid)) {
            subFormUuid = subformId;
        }
        List<Map<String, Object>> subFormDatas = dyFormData.getFormDatas(subFormUuid);
        if (CollectionUtils.isEmpty(subFormDatas)) {
            return;
        }
        // 兼容处理前端直接提交时从表没有form_uuid的情况
        String subformUuid = subFormUuid;
        subFormDatas.forEach(subFormData -> {
            if (subFormData != null && !subFormData.containsKey("form_uuid")) {
                subFormData.put("form_uuid", subformUuid);
            }
        });

        // 按从表行分批次生成实例
        boolean createInstanceBatch = newFlow.isCreateInstanceBatch();
        if (createInstanceBatch) {
            createSubFlowInstanceBySubformBatch(executionContext, parentFlowInstance, parentTaskInstance, subTaskNode,
                    newFlow, subFlowDefId, taskData, formUuid, dataUuid, dyFormData, subFormUuid, createInstanceWay,
                    taskUsersField, subFormDatas);
        } else {
            createSubFlowInstanceBySubformData(executionContext, parentFlowInstance, parentTaskInstance, subTaskNode,
                    newFlow, subFlowDefId, taskData, formUuid, dataUuid, dyFormData, createInstanceWay, taskUsersField,
                    subFormDatas);
        }
        /* modified by huanglinchuan 2014.10.24 end */
    }

    /**
     * @param executionContext
     * @param flowInstance
     * @param parentTask
     * @param subTaskNode
     * @param newFlow
     * @param subFlowDefId
     * @param taskData
     * @param formUuid
     * @param dataUuid
     * @param dyFormData
     * @param createInstanceWay
     * @param taskUsersField
     * @param subFormDatas
     * @param subTaskMonitors
     */
    private void createSubFlowInstanceBySubformBatch(ExecutionContext executionContext, FlowInstance parentFlowInstance,
                                                     TaskInstance parentTaskInstance, SubTaskNode subTaskNode, NewFlow newFlow, String subFlowDefId,
                                                     TaskData taskData, String formUuid, String dataUuid, DyFormData dyFormData, String subFormUuid,
                                                     String createInstanceWay, String taskUsersField, List<Map<String, Object>> subFormDatas) {
        // 获取已完成的批次数据UUID
        List<String> completedBatchDataUuids = getCompletedBatchDataUuids(parentFlowInstance, parentTaskInstance,
                subFormUuid);
        for (Map<String, Object> subFormData : subFormDatas) {
            String subFormDataUuid = ObjectUtils.toString(subFormData.get(IdEntity.UUID), StringUtils.EMPTY);
            // 按未处理的批次数据UUID生成流程实例
            if (!completedBatchDataUuids.contains(subFormDataUuid)) {
                createSubFlowInstanceBySubformDataRowData(executionContext, parentFlowInstance, parentTaskInstance,
                        subTaskNode, newFlow, subFlowDefId, taskData, formUuid, dataUuid, dyFormData, createInstanceWay,
                        taskUsersField, subFormData);
                // 保存当前批次的流程参数信息
                saveOrUpdateCurrentBatchInfo(parentFlowInstance, parentTaskInstance, subFormUuid, subFormDataUuid);
                break;
            }
        }
    }

    /**
     * @param parentFlowInstance
     * @param parentTaskInstance
     * @param subFormUuid
     * @param subFormDataUuid
     */
    private void saveOrUpdateCurrentBatchInfo(FlowInstance parentFlowInstance, TaskInstance parentTaskInstance,
                                              String subFormUuid, String subFormDataUuid) {
        String parentFlowInstUuid = parentFlowInstance.getUuid();
        String parentTaskInstUuid = parentTaskInstance.getUuid();
        String name = FlowConstant.SUB_FLOW.KEY_BATCH_FORM_INFO + Separator.UNDERLINE.getValue() + parentTaskInstUuid;
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(parentFlowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(subFormUuid + Separator.COLON.getValue() + subFormDataUuid);
            flowService.saveFlowInstanceParameter(example);
        } else {
            for (FlowInstanceParameter flowInstanceParameter : parameters) {
                String batchFormInfo = flowInstanceParameter.getValue();
                String[] batchFormInfos = StringUtils.split(batchFormInfo, Separator.SEMICOLON.getValue());
                List<String> batchFormInfoList = Lists.newArrayList();
                batchFormInfoList.addAll(Arrays.asList(batchFormInfos));
                String newBatchFormInfo = subFormUuid + Separator.COLON.getValue() + subFormDataUuid;
                // 忽略掉多个子流程实例同批次的数据
                if (!batchFormInfoList.contains(newBatchFormInfo)) {
                    batchFormInfoList.add(newBatchFormInfo);
                    flowInstanceParameter.setValue(StringUtils.join(batchFormInfoList, Separator.SEMICOLON.getValue()));
                    flowService.saveFlowInstanceParameter(flowInstanceParameter);
                }
            }
        }
    }

    /**
     * @param parentFlowInstance
     * @param parentTaskInstance
     * @param subFormUuid
     * @return
     */
    private List<String> getCompletedBatchDataUuids(FlowInstance parentFlowInstance, TaskInstance parentTaskInstance,
                                                    String subFormUuid) {
        String subTaskId = parentTaskInstance.getId();
        String subFormId = dyFormFacade.getFormIdByFormUuid(subFormUuid);
        String parentFlowInstUuid = parentFlowInstance.getUuid();
        String name = FlowConstant.SUB_FLOW.KEY_COMPLATED_BATCH + Separator.UNDERLINE.getValue() + subTaskId
                + Separator.UNDERLINE.getValue() + subFormId;
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(parentFlowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        List<String> dataUuids = Lists.newArrayList();
        for (FlowInstanceParameter flowInstanceParameter : parameters) {
            String completedBatchDataUuid = flowInstanceParameter.getValue();
            String[] completedBatchDataUuids = StringUtils.split(completedBatchDataUuid,
                    Separator.SEMICOLON.getValue());
            dataUuids.addAll(Arrays.asList(completedBatchDataUuids));
        }
        return dataUuids;
    }

    /**
     * @param executionContext
     * @param flowInstance
     * @param parentTask
     * @param subTaskNode
     * @param newFlow
     * @param subFlowDefId
     * @param taskData
     * @param formUuid
     * @param dataUuid
     * @param dyFormData
     * @param createInstanceWay
     * @param taskUsersField
     * @param subFormDatas
     */
    private void createSubFlowInstanceBySubformData(ExecutionContext executionContext, FlowInstance parentFlowInstance,
                                                    TaskInstance parentTaskInstance, SubTaskNode subTaskNode, NewFlow newFlow, String subFlowDefId,
                                                    TaskData taskData, String formUuid, String dataUuid, DyFormData dyFormData, String createInstanceWay,
                                                    String taskUsersField, List<Map<String, Object>> subFormDatas) {
        for (Map<String, Object> subFormData : subFormDatas) {
            createSubFlowInstanceBySubformDataRowData(executionContext, parentFlowInstance, parentTaskInstance,
                    subTaskNode, newFlow, subFlowDefId, taskData, formUuid, dataUuid, dyFormData, createInstanceWay,
                    taskUsersField, subFormData);
        }
    }

    /**
     * @param executionContext
     * @param parentFlowInstance
     * @param parentTaskInstance
     * @param subTaskNode
     * @param newFlow
     * @param subFlowDefId
     * @param taskData
     * @param formUuid
     * @param dataUuid
     * @param dyFormData
     * @param createInstanceWay
     * @param taskUsersField
     * @param subFormData
     */
    private void createSubFlowInstanceBySubformDataRowData(ExecutionContext executionContext,
                                                           FlowInstance parentFlowInstance, TaskInstance parentTaskInstance, SubTaskNode subTaskNode, NewFlow newFlow,
                                                           String subFlowDefId, TaskData taskData, String formUuid, String dataUuid, DyFormData dyFormData,
                                                           String createInstanceWay, String taskUsersField, Map<String, Object> subFormData) {
        // 办理人字段
        NewFlowTaskUsers newFlowTaskUsers = null;
        // 发起子流程时外部传入的办理人
        Set<String> subTaskUserIds = taskData.getTaskUsers(newFlow.getId());
        List<String> toCheckOrgIds = Lists.newArrayList();
        if (subTaskUserIds.isEmpty()) {
            if (subFormData == null || !subFormData.containsKey(taskUsersField)) {
                return;
            }
            String taskUsers = StringUtils.trimToEmpty((String) subFormData.get(taskUsersField));
            if (StringUtils.isBlank(taskUsers)) {
                return;
            }
            newFlowTaskUsers = getNewFlowTaskUsers(taskUsers, subTaskNode, newFlow, createInstanceWay, executionContext);
            toCheckOrgIds.addAll(Arrays.asList(taskUsers.split(Separator.SEMICOLON.getValue())));
        } else {
            newFlowTaskUsers = getNewFlowTaskUsers(StringUtils.join(subTaskUserIds, Separator.SEMICOLON.getValue()),
                    subTaskNode, newFlow, createInstanceWay, executionContext);
            toCheckOrgIds.addAll(subTaskUserIds);
        }
        // 过滤添加承办操作附加的办理人
        newFlowTaskUsers = filterAdditionalNewFlowTaskUsersIfRequire(dataUuid, taskData, parentTaskInstance,
                parentFlowInstance, newFlow, newFlowTaskUsers);

        List<String> monitorIds = Lists.newArrayList();

        // 单一实例
        if (StringUtils.equals(createInstanceWay, FlowConstant.CREATE_INSTANCE_WAY.SINGLETON)) {
            List<String> toCheckUsers = newFlowTaskUsers.getUsers();
            // 分发条件判断
            if (!checkNewFlowCondition(executionContext, parentFlowInstance, parentTaskInstance, subTaskNode, newFlow,
                    toCheckUsers, toCheckOrgIds, createInstanceWay)) {
                return;
            }

            List<String> userNames = newFlowTaskUsers.getUserNames();
            List<String> users = resolveTaskTodoUsers(toCheckUsers, userNames, newFlowTaskUsers.getType(), subTaskNode, newFlow);

            // 复制表单数据
            copyMainAndSubFormData(newFlow, subFlowDefId, taskData, formUuid, dataUuid, dyFormData, subFormData);
            // 复制任务数据
            TaskData subTaskData = copyTaskData(taskData);
            FlowInstanceDelegate flowInstanceDelegate = new FlowInstanceDelegate();
            String subFlowInstUuid = flowInstanceDelegate.startSubFlowInstance(newFlow,
                    executionContext.getFlowInstance(), executionContext.getToken().getTask(), subTaskData, users,
                    StringUtils.join(toCheckUsers, Separator.SEMICOLON.getValue()),
                    StringUtils.join(userNames, Separator.SEMICOLON.getValue()), monitorIds);
            executionContext.getToken().getTaskData().getSubmitResult().getSubFlowInstUUids().add(subFlowInstUuid);
            executionContext.getToken().getTaskData().getSubmitResult().setSubFlowSyncStartd(newFlow.isWait());

            FlowInstanceParameter paraOfParentMainFormDataUuid = new FlowInstanceParameter();
            paraOfParentMainFormDataUuid.setFlowInstUuid(subFlowInstUuid);
            paraOfParentMainFormDataUuid.setName("parentMainFormDataUuid");
            paraOfParentMainFormDataUuid.setValue(dataUuid);
            flowService.saveFlowInstanceParameter(paraOfParentMainFormDataUuid);

            FlowInstanceParameter paraOfParentSubFormFormUuid = new FlowInstanceParameter();
            paraOfParentSubFormFormUuid.setFlowInstUuid(subFlowInstUuid);
            paraOfParentSubFormFormUuid.setName("parentSubFormUuid");
            paraOfParentSubFormFormUuid.setValue((String) subFormData.get("form_uuid"));
            flowService.saveFlowInstanceParameter(paraOfParentSubFormFormUuid);

            FlowInstanceParameter paraOfParentSubFormDataUuid = new FlowInstanceParameter();
            paraOfParentSubFormDataUuid.setFlowInstUuid(subFlowInstUuid);
            paraOfParentSubFormDataUuid.setName("parentSubFormDataUuid");
            paraOfParentSubFormDataUuid.setValue((String) subFormData.get("uuid"));
            flowService.saveFlowInstanceParameter(paraOfParentSubFormDataUuid);
        } else if (StringUtils.equals(createInstanceWay, FlowConstant.CREATE_INSTANCE_WAY.BY_USER)) {
            List<String> users = newFlowTaskUsers.getUsers();
            List<String> userNames = newFlowTaskUsers.getUserNames();
            // 按照办理人多实例
            for (int index = 0; index < users.size(); index++) {
                String userId = users.get(index);

                List<String> toCheckUsers = Lists.newArrayList();
                toCheckUsers.add(userId);
                // 分发条件判断
                if (!checkNewFlowCondition(executionContext, parentFlowInstance, parentTaskInstance, subTaskNode,
                        newFlow, toCheckUsers, toCheckOrgIds, createInstanceWay)) {
                    continue;
                }

                String userName = userNames.get(index);
                List<String> toCheckNames = Lists.newArrayList();
                toCheckNames.add(userName);
                List<String> taskTodoUsers = resolveTaskTodoUsers(toCheckUsers, toCheckNames,
                        newFlowTaskUsers.getType(), subTaskNode, newFlow);

                // 复制表单数据
                copyMainAndSubFormData(newFlow, subFlowDefId, taskData, formUuid, dataUuid, dyFormData, subFormData);
                // 复制任务数据
                TaskData subTaskData = copyTaskData(taskData);
                FlowInstanceDelegate flowInstanceDelegate = new FlowInstanceDelegate();
                String subFlowInstUuid = flowInstanceDelegate.startSubFlowInstance(newFlow,
                        executionContext.getFlowInstance(), executionContext.getToken().getTask(), subTaskData,
                        taskTodoUsers, userId, userName, monitorIds);
                executionContext.getToken().getTaskData().getSubmitResult().getSubFlowInstUUids().add(subFlowInstUuid);
                executionContext.getToken().getTaskData().getSubmitResult().setSubFlowSyncStartd(newFlow.isWait());

                FlowInstanceParameter paraOfParentMainFormDataUuid = new FlowInstanceParameter();
                paraOfParentMainFormDataUuid.setFlowInstUuid(subFlowInstUuid);
                paraOfParentMainFormDataUuid.setName("parentMainFormDataUuid");
                paraOfParentMainFormDataUuid.setValue(dataUuid);
                flowService.saveFlowInstanceParameter(paraOfParentMainFormDataUuid);

                FlowInstanceParameter paraOfParentSubFormFormUuid = new FlowInstanceParameter();
                paraOfParentSubFormFormUuid.setFlowInstUuid(subFlowInstUuid);
                paraOfParentSubFormFormUuid.setName("parentSubFormUuid");
                paraOfParentSubFormFormUuid.setValue((String) subFormData.get("form_uuid"));
                flowService.saveFlowInstanceParameter(paraOfParentSubFormFormUuid);

                FlowInstanceParameter paraOfParentSubFormDataUuid = new FlowInstanceParameter();
                paraOfParentSubFormDataUuid.setFlowInstUuid(subFlowInstUuid);
                paraOfParentSubFormDataUuid.setName("parentSubFormDataUuid");
                paraOfParentSubFormDataUuid.setValue((String) subFormData.get("uuid"));
                flowService.saveFlowInstanceParameter(paraOfParentSubFormDataUuid);
            }
        }
    }

    /**
     * @param executionContext
     * @return
     */
    private DyFormData getDyformData(ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        // 主流程表单定义UUID
        String formUuid = taskData.getFormUuid();
        // 主流程表单数据UUID
        String dataUuid = taskData.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            taskData.setDyFormData(dataUuid, dyFormData);
        }
        return dyFormData;
    }

    /**
     * 如何描述该方法
     *
     * @param taskUsers
     * @return
     */
    private NewFlowTaskUsers getNewFlowTaskUsers(String taskUsers, SubTaskNode subTaskNode, NewFlow newFlow,
                                                 String createInstanceWay, ExecutionContext executionContext) {
        String businessType = subTaskNode.getBusinessType();
        String granularity = newFlow.getGranularity();
        // 选择行政、业务组织及分发粒度
        if (StringUtils.isNotBlank(businessType) && StringUtils.isNotBlank(granularity)) {
            return resolveOrgNewFlowTaskUsers(taskUsers, subTaskNode, newFlow, executionContext);
        }

        String businessRole = subTaskNode.getBusinessRole();
        String userType = NewFlowTaskUsers.TYPE_ORG;
        List<String> users = Lists.newArrayList();
        List<String> userNames = Lists.newArrayList();
        // 解析业务单位的人员
        if (StringUtils.isNotBlank(businessType) && StringUtils.isNotBlank(businessRole)) {
            userType = NewFlowTaskUsers.TYPE_BIZ;
            if (StringUtils.isNotBlank(taskUsers)) {
                String[] ids = taskUsers.split(Separator.SEMICOLON.getValue());
                for (String id : ids) {
                    // 业务分类结点，按办理人生成实例，7.0组织没有业务分类节点
//                    BusinessCategoryOrgDto businessCategoryOrgDto = businessFacadeService
//                            .getBusinessCategoryOrgByCategoryUuidAndId(businessType, id);
//                    String orgName = id;
//                    if (businessCategoryOrgDto != null) {
//                        orgName = businessCategoryOrgDto.getName();
//                    }
                    String orgName = workflowOrgService.getNameById(id);
                    users.add(id);
                    userNames.add(orgName);
                }
            }
        } else {
            if (StringUtils.isNotBlank(taskUsers)) {
                String[] ids = taskUsers.split(Separator.SEMICOLON.getValue());
                Map<String, String> idNames = workflowOrgService.getNamesByIds(Lists.newArrayList(ids));
                for (String id : ids) {
                    if (StringUtils.startsWith(id, IdPrefix.USER.getValue())) {
                        List<OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobs(id, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(executionContext.getToken()));
                        users.add(id);
                        if (CollectionUtils.isNotEmpty(userJobDtos)) {
                            String deptName = getUserDepartmentName(userJobDtos);
                            if (StringUtils.isNotBlank(deptName)) {
                                userNames.add(deptName);
                            } else {
                                userNames.add(idNames.get(id));
                            }
                        } else {
                            userNames.add(idNames.get(id));
                        }
//                        MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(id);
//                        if (user != null) {
//                            users.add(id);
//                            String deptName = getUserDepartmentName(id);
//                            if (StringUtils.isNotBlank(deptName)) {
//                                userNames.add(user.getUserName() + "（" + deptName + "）");
//                            } else {
//                                userNames.add(user.getUserName());
//                            }
//                        }
                    } else {
                        users.add(id);
                        userNames.add(idNames.get(id));
                    }
//                    else if (StringUtils.startsWith(id, IdPrefix.DUTY.getValue())) {
//                        users.add(id);
//                        MultiOrgDuty duty = orgApiFacade.getDutyById(id);
//                        if (duty != null) {
//                            userNames.add(duty.getName());
//                        } else {
//                            userNames.add(id);
//                        }
//                    } else if (StringUtils.startsWith(id, IdPrefix.DEPARTMENT.getValue())
//                            || StringUtils.startsWith(id, IdPrefix.JOB.getValue())) {
//                        users.add(id);
//                        // MultiOrgElement orgElement = orgApiFacade.getOrgElementById(id);
//                        if (idNames.containsKey(id)) {
//                            userNames.add(idNames.get(id));
//                        } else {
//                            userNames.add(id);
//                        }
//                    } else if (StringUtils.startsWith(id, IdPrefix.GROUP.getValue())) {
//                        // MultiOrgGroup group = orgApiFacade.getGroupById(id);
//                        users.add(id);
//                        if (idNames.containsKey(id)) {
//                            userNames.add(idNames.get(id));
//                        } else {
//                            userNames.add(id);
//                        }
//                    }
                }
            }
        }
        NewFlowTaskUsers newFlowTaskUsers = new NewFlowTaskUsers();
        newFlowTaskUsers.setType(userType);
        newFlowTaskUsers.setUserNames(userNames);
        newFlowTaskUsers.setUsers(users);
        return newFlowTaskUsers;
    }

    /**
     * @param taskUsers
     * @param subTaskNode
     * @param newFlow
     * @param executionContext
     * @return
     */
    private NewFlowTaskUsers resolveOrgNewFlowTaskUsers(String taskUsers, SubTaskNode subTaskNode, NewFlow newFlow, ExecutionContext executionContext) {
        NewFlowTaskUsers newFlowTaskUsers = new NewFlowTaskUsers();
        newFlowTaskUsers.setType(NewFlowTaskUsers.TYPE_ORG);
        if (StringUtils.isBlank(taskUsers)) {
            return newFlowTaskUsers;
        }

        Token token = executionContext.getToken();
        List<String> userIds = Lists.newArrayList();
        List<String> userNames = Lists.newArrayList();
        Consumer<FlowUserSid> sidConsumer = flowUserSid -> {
            userIds.add(flowUserSid.getId());
            userNames.add(flowUserSid.getName());
        };
        String orgId = subTaskNode.getBusinessType();
        String bizOrgId = orgId;
        String granularity = newFlow.getGranularity();
        List<String> taskUserIds = Arrays.asList(taskUsers.split(Separator.SEMICOLON.getValue()));
        // 业务组织
        if (StringUtils.startsWith(bizOrgId, IdPrefix.BIZ_ORG.getValue())) {
            switch (granularity) {
                case "bizItem":
                    List<String> bizRoleIds = Arrays.asList(StringUtils.split(newFlow.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Map<String, String> bizItemUserMap = workflowOrgService.getBizOrgUsersByIdsAndBizRoleIds(taskUserIds, bizRoleIds, bizOrgId);
                    mapToUsers(bizItemUserMap, userIds, userNames);
                    break;
                case "bizRole":
                    Map<String, String> bizRoleUserMap = Maps.newHashMap();
                    List<String> taskBizRoleIds = Lists.newArrayList();
                    taskUserIds.forEach(userId -> {
                        if (StringUtils.contains(userId, Separator.SLASH.getValue())) {
                            bizRoleUserMap.putAll(workflowOrgService.getBizOrgUsersByIdWithBizRoleId(userId, bizOrgId));
                        } else {
                            taskBizRoleIds.add(userId);
                        }
                    });
                    if (CollectionUtils.isNotEmpty(taskBizRoleIds)) {
                        bizRoleUserMap.putAll(workflowOrgService.getBizOrgUsersByBizRoleIds(taskBizRoleIds, bizOrgId));
                    }
                    mapToUsers(bizRoleUserMap, userIds, userNames);
                    break;
                case "member":
                    Map<String, String> memberUserMap = workflowOrgService.getBizOrgUsersByIds(taskUserIds, bizOrgId);
                    List<String> filterUserIds = workflowOrgService.filterUserIdsByBizOrgId(Lists.newArrayList(memberUserMap.keySet()), bizOrgId);
                    Map<String, String> filterMemberUserMap = Maps.newHashMap();
                    memberUserMap.forEach((key, value) -> {
                        if (filterUserIds.contains(key)) {
                            filterMemberUserMap.put(key, value);
                        }
                    });
                    mapToUsers(filterMemberUserMap, userIds, userNames);
                    break;
            }
        } else {
            // 行政组织
            switch (granularity) {
                case "dept":
                    taskUserIds = taskUserIds.stream().filter(userId -> !(StringUtils.startsWith(userId, IdPrefix.USER.getValue())
                            || StringUtils.startsWith(userId, IdPrefix.JOB.getValue()))).collect(Collectors.toList());
                    List<FlowUserSid> deptSids = sidGranularityResolver.resolve(subTaskNode, token, taskUserIds,
                            SidGranularity.DEPARTMENT, orgId);
                    deptSids.forEach(sidConsumer);
                    newFlowTaskUsers.setSidGranularity(SidGranularity.DEPARTMENT);
                    break;
                case "job":
                    taskUserIds = taskUserIds.stream().filter(userId -> !StringUtils.startsWith(userId, IdPrefix.USER.getValue())).collect(Collectors.toList());
                    List<FlowUserSid> jobSids = sidGranularityResolver.resolve(subTaskNode, token, taskUserIds,
                            SidGranularity.JOB, orgId);
                    jobSids.forEach(sidConsumer);
                    newFlowTaskUsers.setSidGranularity(SidGranularity.JOB);
                    break;
                case "user":
                    List<FlowUserSid> userSids = sidGranularityResolver.resolve(subTaskNode, token, taskUserIds,
                            SidGranularity.USER, orgId);
                    userSids.forEach(sidConsumer);
                    newFlowTaskUsers.setSidGranularity(SidGranularity.USER);
                    break;
            }
        }

        newFlowTaskUsers.setUserNames(userNames);
        newFlowTaskUsers.setUsers(userIds);
        return newFlowTaskUsers;
    }

    /**
     * @param userMap
     * @param userIds
     * @param userNames
     */
    private void mapToUsers(Map<String, String> userMap, List<String> userIds, List<String> userNames) {
        for (Map.Entry<String, String> entry : userMap.entrySet()) {
            userIds.add(entry.getKey());
            userNames.add(entry.getValue());
        }
    }

    /**
     * @param toCheckUsers
     * @param type
     * @param subTaskNode
     * @return
     */
    private List<String> resolveTaskTodoUsers(List<String> toCheckUsers, List<String> toCheckNames, String type,
                                              SubTaskNode subTaskNode, NewFlow newFlow) {
        if (NewFlowTaskUsers.TYPE_ORG.equals(type)) {
            return toCheckUsers;
        }

        String businessType = subTaskNode.getBusinessType();
        String businessTypeName = subTaskNode.getBusinessTypeName();
        String businessRole = subTaskNode.getBusinessRole();
        String businessRoleName = subTaskNode.getBusinessRoleName();

        Set<String> returnUserIds = Sets.newLinkedHashSet();
        for (int index = 0; index < toCheckUsers.size(); index++) {
            String orgElementId = toCheckUsers.get(index);
            String orgElementName = toCheckNames.get(index);
//            Set<String> userIdSet = businessFacadeService.getUserByOrgUuidAndRoleUuid(businessType, orgElementId,
//                    businessRole);
            Set<String> userIdSet = workflowOrgService.listUserIdWithOrgRoleIdAndOrgId(orgElementId, businessRole, businessType);
            if (CollectionUtils.isEmpty(userIdSet)) {
                if (StringUtils.isBlank(businessTypeName)) {
                    OrganizationDto organization = workflowOrgService.getOrganizationById(businessType);
                    businessTypeName = organization.getName();
                }
                throw new RuntimeException("业务类型为[" + businessTypeName + "]的业务单位通讯录结点[" + orgElementName + "]没有配置角色["
                        + businessRoleName + "]人员！");
            }
            returnUserIds.addAll(userIdSet);
        }

        return Arrays.asList(returnUserIds.toArray(new String[0]));
    }

    /**
     * @param dataUuid
     * @param parentTaskInstance
     * @param taskData
     * @param newFlow
     * @param newFlowTaskUsers
     * @return
     */
    private NewFlowTaskUsers filterAdditionalNewFlowTaskUsersIfRequire(String dataUuid, TaskData taskData,
                                                                       TaskInstance parentTaskInstance, FlowInstance parentFlowInstance, NewFlow newFlow,
                                                                       NewFlowTaskUsers newFlowTaskUsers) {
        String fromTaskId = parentTaskInstance.getId();
        if (!taskData.isUseFormFieldUsers(fromTaskId)) {
            // 确保流程参数存储使用表单的办理人
            addFormFieldUserFlowInstanceParameterOnce(dataUuid, taskData, parentTaskInstance, parentFlowInstance,
                    newFlow, newFlowTaskUsers);
            return newFlowTaskUsers;
        }

        List<String> oldFormFieldUsers = getAndUpdateFormFieldUserByFlowInstanceParameter(dataUuid, taskData,
                parentTaskInstance, parentFlowInstance, newFlow, newFlowTaskUsers);

        List<String> newFormFieldUsers = newFlowTaskUsers.getUsers();
        List<String> newFormFieldUserNames = newFlowTaskUsers.getUserNames();
        // 去除已存在的组织名称
        Map<String, Object> newFormFieldUserMap = Maps.newHashMap();
        for (int index = 0; index < newFormFieldUsers.size(); index++) {
            newFormFieldUserMap.put(newFormFieldUsers.get(index), newFormFieldUserNames.get(index));
        }
        for (String oldFormFieldUser : oldFormFieldUsers) {
            if (newFormFieldUserMap.containsKey(oldFormFieldUser)) {
                newFormFieldUserNames.remove(newFormFieldUserMap.get(oldFormFieldUser));
            }
        }
        // 去除已存在的组织ID
        newFormFieldUsers.removeAll(oldFormFieldUsers);
        return newFlowTaskUsers;
    }

    /**
     * @param dataUuid
     * @param taskData
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @param newFlow
     * @param newFlowTaskUsers
     */
    private void addFormFieldUserFlowInstanceParameterOnce(String dataUuid, TaskData taskData,
                                                           TaskInstance parentTaskInstance, FlowInstance parentFlowInstance, NewFlow newFlow,
                                                           NewFlowTaskUsers newFlowTaskUsers) {
        String flowInstUuid = parentFlowInstance.getUuid();
        // 分发环节实例UUID + "_" + 表单数据UUID + "_" + 子流程定义ID + "_" + 表单字段
        String name = parentTaskInstance.getUuid() + Separator.UNDERLINE.getValue() + dataUuid
                + Separator.UNDERLINE.getValue() + newFlow.getId() + Separator.UNDERLINE.getValue()
                + newFlow.getTaskUsers();

        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(StringUtils.join(newFlowTaskUsers.getUsers(), Separator.SEMICOLON.getValue()));
            flowService.saveFlowInstanceParameter(example);
        }
    }

    /**
     *
     */
    private void updateFormFieldUserFlowInstanceParameter(String dataUuid, TaskData taskData,
                                                          TaskInstance parentTaskInstance, FlowInstance parentFlowInstance, NewFlow newFlow,
                                                          NewFlowTaskUsers newFlowTaskUsers) {
        String flowInstUuid = parentFlowInstance.getUuid();
        // 分发环节实例UUID + "_" + 表单数据UUID + "_" + 子流程定义ID + "_" + 表单字段
        String name = parentTaskInstance.getUuid() + Separator.UNDERLINE.getValue() + dataUuid
                + Separator.UNDERLINE.getValue() + newFlow.getId() + Separator.UNDERLINE.getValue()
                + newFlow.getTaskUsers();

        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(StringUtils.join(newFlowTaskUsers.getUsers(), Separator.SEMICOLON.getValue()));
            flowService.saveFlowInstanceParameter(example);
        } else {
            for (FlowInstanceParameter flowInstanceParameter : parameters) {
                flowInstanceParameter
                        .setValue(StringUtils.join(newFlowTaskUsers.getUsers(), Separator.SEMICOLON.getValue()));
                flowService.saveFlowInstanceParameter(flowInstanceParameter);
            }
        }
    }

    /**
     * @param dataUuid
     * @param taskData
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @param newFlow
     * @param newFlowTaskUsers
     */
    @SuppressWarnings("unchecked")
    private List<String> getAndUpdateFormFieldUserByFlowInstanceParameter(String dataUuid, TaskData taskData,
                                                                          TaskInstance parentTaskInstance, FlowInstance parentFlowInstance, NewFlow newFlow,
                                                                          NewFlowTaskUsers newFlowTaskUsers) {
        String flowInstUuid = parentFlowInstance.getUuid();
        // 分发环节实例UUID + "_" + 表单数据UUID + "_" + 子流程定义ID + "_" + 表单字段
        String name = parentTaskInstance.getUuid() + Separator.UNDERLINE.getValue() + dataUuid
                + Separator.UNDERLINE.getValue() + newFlow.getId() + Separator.UNDERLINE.getValue()
                + newFlow.getTaskUsers();

        String taskDataKey = flowInstUuid + name;
        List<String> tmpFormFieldUsers = (List<String>) taskData.get(taskDataKey);
        if (tmpFormFieldUsers != null) {
            return tmpFormFieldUsers;
        }

        List<String> formFieldUsers = Lists.newArrayList();

        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        for (FlowInstanceParameter flowInstanceParameter : parameters) {
            String formFieldUser = flowInstanceParameter.getValue();
            if (StringUtils.isNotBlank(formFieldUser)) {
                List<String> strings = Arrays.asList(StringUtils.split(formFieldUser, Separator.SEMICOLON.getValue()));
                formFieldUsers.addAll(strings);
            }
        }

        // 标记任务数据
        taskData.put(taskDataKey, formFieldUsers);
        // 更新新的任务参数
        Boolean formFieldUserUpdated = (Boolean) taskData.get(taskDataKey + "Update");
        if (!Boolean.TRUE.equals(formFieldUserUpdated)) {
            updateFormFieldUserFlowInstanceParameter(dataUuid, taskData, parentTaskInstance, parentFlowInstance,
                    newFlow, newFlowTaskUsers);
            taskData.put(taskDataKey + "Update", true);

        }
        return formFieldUsers;
    }

    /**
     * @param userJobDtos
     * @return
     */
    private String getUserDepartmentName(List<OrgUserJobDto> userJobDtos) {
        String deptName = StringUtils.EMPTY;
        if (CollectionUtils.isEmpty(userJobDtos)) {
            return deptName;
        }
        OrgUserJobDto userJob = userJobDtos.stream().filter(job -> job.isPrimary()).findFirst().orElse(userJobDtos.get(0));
        List<String> idPaths = Arrays.asList(StringUtils.split(userJob.getJobIdPath(), Separator.SLASH.getValue()));
        List<String> namePaths = Arrays.asList(StringUtils.split(userJob.getJobNamePath(), Separator.SLASH.getValue()));
        if (CollectionUtils.size(idPaths) != CollectionUtils.size(namePaths)) {
            return deptName;
        }
        for (int index = idPaths.size() - 1; index >= 0; index--) {
            String deptId = idPaths.get(index);
            if (StringUtils.startsWith(deptId, IdPrefix.DEPARTMENT.getValue())) {
                return namePaths.get(index);
            }
        }
        return deptName;
    }

    /**
     * @param userId
     * @return
     */
//    private String getUserDepartmentName(String userId) {
//        String deptName = StringUtils.EMPTY;
//        MultiOrgUserWorkInfo workInfo = orgApiFacade.getUserWorkInfoByUserId(userId);
//        if (workInfo == null) {
//            return StringUtils.EMPTY;
//        }
//        String deptId = workInfo.getDeptIds();
//        if (StringUtils.isEmpty(deptId)) {
//            return deptName;
//        }
//        List<String> deptIds = java.util.Arrays.asList(StringUtils.split(deptId, Separator.SEMICOLON.getValue()));
//        deptName = orgApiFacade.getNameByOrgEleId(deptIds.get(0));
//        return deptName;
//    }
    private void createSubFlowInstanceByCustomInterface(ExecutionContext executionContext, FlowInstance flowInstance,
                                                        TaskInstance parentTask, SubTaskNode subTaskNode, NewFlow newFlow, int subFlowIndex) {
        // 子流程定义ID
        String interfaceName = newFlow.getInterfaceName();
        String interfaceValue = newFlow.getInterfaceValue();
        CustomDispatcherFlowResolver dispatcher = ApplicationContextHolder.getBean(interfaceValue,
                CustomDispatcherFlowResolver.class);
        if (dispatcher == null) {
            throw new WorkFlowException("找不到[" + interfaceName + ":" + interfaceValue + "]的流程分发实现");
        }
        dispatcher.create(executionContext, flowInstance, parentTask, subTaskNode, subFlowIndex);
    }

    ;

    /**
     * 将主流程主表字段信息拷贝到从表
     *
     * @param newFlow
     * @param subFlowDefId
     * @param taskData
     * @param formUuid
     * @param dataUuid
     * @param formAndDataBean
     */
    protected void copyMainFormData(NewFlow newFlow, String subFlowDefId, TaskData taskData, String formUuid,
                                    String dataUuid, DyFormData mainFormData) {
        StopWatch stopWatch = new StopWatch("startSubTasks");
        stopWatch.start("复制主表数据");
        String botRuleId = newFlow.getCopyBotRuleId();
        copyDyFormDataWithBotId(botRuleId, taskData, formUuid, dataUuid);
        stopWatch.stop();
        logger.info("复制主表数据，共耗时{}秒", stopWatch.getLastTaskInfo().getTimeSeconds());
    }

    /**
     * @param newFlow
     * @param subFlowDefId
     * @param taskData
     * @param formUuid
     * @param dataUuid
     * @param mainFormData
     * @param subFormData
     */
    protected void copyMainAndSubFormData(NewFlow newFlow, String subFlowDefId, TaskData taskData, String formUuid,
                                          String dataUuid, DyFormData mainFormData, Map<String, Object> subFormData) {
        String botRuleId = newFlow.getCopyBotRuleId();
        copyDyFormDataWithBotId(botRuleId, taskData, formUuid, dataUuid, subFormData);
    }

    /**
     * @param botRuleId
     * @param taskData
     * @param formUuid
     * @param dataUuid
     */
    private void copyDyFormDataWithBotId(String botRuleId, TaskData taskData, String formUuid, String dataUuid) {
        copyDyFormDataWithBotId(botRuleId, taskData, formUuid, dataUuid, null);
    }

    /**
     * @param botRuleId
     * @param taskData
     * @param formUuid
     * @param dataUuid
     */
    @SuppressWarnings("unchecked")
    private void copyDyFormDataWithBotId(String botRuleId, TaskData taskData, String formUuid, String dataUuid, Map<String, Object> subFormData) {
        Boolean syncDispatch = (Boolean) taskData.get("syncDispatchSubFlow");
        // 异步由发送再复制表单数据
        if (!BooleanUtils.isTrue(syncDispatch)) {
            return;
        }

        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }
        Set<BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotFromParam botFromParam = new BotFromParam(dataUuid, formUuid, taskData.getDyFormData(dataUuid));
        froms.add(botFromParam);
        if (subFormData != null) {
            String subformUuid = ObjectUtils.toString(subFormData.get("form_uuid"), StringUtils.EMPTY);
            String subformDataUuid = ObjectUtils.toString(subFormData.get("uuid"), StringUtils.EMPTY);
            if (StringUtils.isNotBlank(subformUuid) && StringUtils.isNotBlank(subformDataUuid)) {
                froms.add(new BotFromParam(subformDataUuid, subformUuid, dyFormFacade.getDyFormData(subformUuid, subformDataUuid)));
            }
        }
        BotParam botParam = new BotParam(botRuleId, froms);
        botParam.setFroms(froms);
        BotResult botResult = null;
        try {
            Map<String, Object> jsonBody = Maps.newHashMap();
            jsonBody.put("dispatchedFormData", MapUtils.isNotEmpty(subFormData) ? subFormData : dyFormData.getFormDataOfMainform());
            botParam.setJsonBody(jsonBody);
            botResult = botFacadeService.startBot(botParam);
        } catch (Exception e) {
            if (e instanceof BotException) {
                throw e;
            } else {
                throw new WorkFlowException("子流程分发时单据转换出错！" + e.getMessage(), e);
            }
        }
        Object data = botResult.getData();
        String subflowFormUuid = null;
        String subflowDataUuid = null;
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
    }

    public static class NewFlowTaskUsers {
        // 人员类型1、组织用户，2、业务单位通讯录
        public static final String TYPE_ORG = "1";
        public static final String TYPE_BIZ = "2";
        private String sidGranularity = SidGranularity.USER;
        private String type;
        private List<String> users = Lists.newArrayList();
        private List<String> userNames = Lists.newArrayList();

        /**
         * @return the sidGranularity
         */
        public String getSidGranularity() {
            return sidGranularity;
        }

        /**
         * @param sidGranularity 要设置的sidGranularity
         */
        public void setSidGranularity(String sidGranularity) {
            this.sidGranularity = sidGranularity;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type 要设置的type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the users
         */
        public List<String> getUsers() {
            return users;
        }

        /**
         * @param users 要设置的users
         */
        public void setUsers(List<String> users) {
            this.users = users;
        }

        /**
         * @return the userNames
         */
        public List<String> getUserNames() {
            return userNames;
        }

        /**
         * @param userNames 要设置的userNames
         */
        public void setUserNames(List<String> userNames) {
            this.userNames = userNames;
        }

    }

}

/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.FlowPermissionEvaluator;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.access.TaskHistoryIdentityResolver;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.*;
import com.wellsoft.pt.bpm.engine.core.handler.SubTaskHandler;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.dispatcher.DefaultDispatcherFlowResolver;
import com.wellsoft.pt.bpm.engine.dispatcher.DispatcherFlowResolver;
import com.wellsoft.pt.bpm.engine.element.RightConfigElement;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.exception.TaskNotAssignedDecisionMakerException;
import com.wellsoft.pt.bpm.engine.exception.TaskNotAssignedMonitorException;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.node.*;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityItem;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStack;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStackFactary;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.service.TaskActivityService;
import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.security.acl.support.AclPermission;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * Description: 流程执行上下文件
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-2.1	zhulh		2012-11-2		Create
 * </pre>
 * @date 2012-11-2
 */
public class ExecutionContext {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private FlowDefinition flowDefinition;

    private FlowInstance flowInstance;

    private FlowDelegate flowDelegate;

    private Node sourceNode;

    private Node targetNode;

    private Token token;

    /**
     * @param token
     */
    public ExecutionContext(Token token) {
        this.token = token;
        this.flowInstance = token.getFlowInstance();
        this.flowDefinition = flowInstance.getFlowDefinition();
        this.flowDelegate = token.getFlowDelegate();
    }

    private static final IdentityResolverStrategy getIdentityResolverStrategy() {
        return ApplicationContextHolder.getBean(IdentityResolverStrategy.class);
    }

    /**
     * @param transition
     */
    public void setTransition(Transition transition) {
    }

    /**
     * @return
     */
    public Node getTransitionSource() {
        return this.sourceNode;
    }

    /**
     * @param taskNode
     */
    public void setTransitionSource(Node taskNode) {
        this.sourceNode = taskNode;
    }

    /**
     * @return
     */
    public Node getTransitionTarget() {
        return targetNode;
    }

    /**
     * @param node
     */
    public void setTransitionTarget(Node node) {
        this.targetNode = node;
    }

    /**
     * @return the flowDefinition
     */
    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    /**
     * @param flowDefinition 要设置的flowDefinition
     */
    public void setFlowDefinition(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    /**
     * @return the flowInstance
     */
    public FlowInstance getFlowInstance() {
        return flowInstance;
    }

    /**
     * @param flowInstance 要设置的flowInstance
     */
    public void setFlowInstance(FlowInstance flowInstance) {
        this.flowInstance = flowInstance;
    }

    /**
     * @return the flowDelegate
     */
    public FlowDelegate getFlowDelegate() {
        return flowDelegate;
    }

    /**
     * @param flowDelegate 要设置的flowDelegate
     */
    public void setFlowDelegate(FlowDelegate flowDelegate) {
        this.flowDelegate = flowDelegate;
    }

    /**
     * @return the token
     */
    public Token getToken() {
        return token;
    }

    /**
     * @param token 要设置的token
     */
    public void setToken(Token token) {
        this.token = token;
    }

    /**
     * 评估这次转流程转换是否可行，计算转向的下一结点，用户计算、权限计算在结点运行时计算
     *
     * @param transition
     */
    public boolean evaluate(Transition transition) {
        TaskData taskData = token.getTaskData();
        // 1、前置子流程判断
        if (this.token.getTask() != null && this.token.getTask().getParent() != null) {
            String key = this.token.getTask().getUuid() + taskData.getUserId();
            if (WorkFlowOperation.isActionTypeOfSubmit(taskData.getActionType(key))
                    || WorkFlowOperation.isActionCodeOfSubmit(taskData.getActionCode(this.token.getTask().getUuid()))) {
                SubTaskHandler subTaskHandler = ApplicationContextHolder.getBean(SubTaskHandler.class);
                // 检验子流程是否可以提交当前流程
                subTaskHandler.checkSubFlowAllowSubmit(transition.getFrom(), this);

                // 更新子流程前后置关系的状态
                subTaskHandler.updateSubFlowRelationStatus(transition.getFrom(), this);
            }
        }

        return true;
    }

    /**
     * @param transition
     */
    public boolean evaluateTransition(Transition transition) {
        TaskData taskData = token.getTaskData();
        String fromTaskId = transition.getFromId();
        // 2、流向计算
        boolean useCustomButton = taskData.getUseCustomDynamicButton(fromTaskId);
        boolean startNewFlow = taskData.getStartNewFlow(token.getFlowInstance().getUuid());
        TransitionResolver transitionResolver = TransitionResolverFactory.getResolver(transition);
        if (useCustomButton && !startNewFlow) {
            CustomDynamicButton customButton = taskData.getCustomDynamicButton(fromTaskId);
            customButton = token.getFlowDelegate().convertTaskCustomDynamicButton(fromTaskId, customButton);
            String taskId = null;
            if (customButton != null) {
                taskId = customButton.getTaskId();
            }
            // 计算出转向的下一结点
            Node to = null;
            // 按流程自动流转
            if (StringUtils.equals(FlowConstant.AUTO_SUBMIT, taskId)) {
                transitionResolver.resolveForkTask(transition, token);
            } else if (StringUtils.isNotBlank(taskId)) {
                to = token.getFlowDelegate().getTaskNode(taskId.toString());
                List<Node> tos = new ArrayList<Node>();
                tos.add(to);
                transition.setTos(tos);
            } else {
                to = token.getFlowDelegate().getToTaskNodeWidthDirectionIdDigest(fromTaskId, customButton.getId());
                if (to != null) {
                    List<Node> tos = new ArrayList<Node>();
                    tos.add(to);
                    transition.setTos(tos);
                } else {
                    throw new WorkFlowException("Custom dynamic button task id is not set");
                }
            }
        } else if (taskData.isCancel(fromTaskId) || taskData.isRollback(fromTaskId) || taskData.isGotoTask(fromTaskId)) {
            // taskData.isGotoTask(fromTaskId) 增加特送环节处理 add by wujx 2016-01-21
            String toTaskId = taskData.getToTaskId(fromTaskId);
            if (StringUtils.isNotBlank(toTaskId)) {
                Node to = this.flowDelegate.getTaskNode(toTaskId);
                List<Node> tos = new ArrayList<Node>();
                tos.add(to);
                transition.setTos(tos);
            } else {
                // 计算出转向的下一结点
                transitionResolver.resolveForkTask(transition, token);
            }
        } else {
            // 退回设置
            boolean isReturnAfterRollback = getReturnAfterRollback(transition, fromTaskId, taskData, token, flowDelegate);
            // 计算出转向的下一结点
            if (!isReturnAfterRollback) {
                // 办理人为空自动进入下一个环节，如果没有指定下一节点则由当前节点算出，否则进入指定的下一节点
                if (StringUtils.isNotBlank(taskData.getEmptyToTask(transition.getFrom().getId()))) {
                    String toTaskId = taskData.getEmptyToTask(transition.getFrom().getId());
                    Node to = this.flowDelegate.getTaskNode(toTaskId);
                    List<Node> tos = new ArrayList<Node>();
                    tos.add(to);
                    transition.setTos(tos);
                } else {
                    transitionResolver.resolveForkTask(transition, token);
                }
            }
        }

        if (transition.getTos().size() == 0) {
            throw new WorkFlowException("Transition resolve error!");
        }

        // 查检开始结点的分支模式、结束结点的聚合模式的正确性，并设置任务数据所需要的运转模式
        transitionResolver.checkAndPrepare(transition, token);

        // 运转聚合模型处理
        boolean allowJoin = transitionResolver.resolveJoinTask(transition, token);

        // 设置当前任务是否允许聚合
        taskData.setTaskAllowJoin(transition.getFromId(), allowJoin);

        return allowJoin;
    }

    /**
     * @param transition
     * @param fromTaskId
     * @param taskData
     * @param token
     * @param flowDelegate
     * @return
     */
    private boolean getReturnAfterRollback(Transition transition, String fromTaskId, TaskData taskData, Token token, FlowDelegate flowDelegate) {
        boolean isReturnAfterRollback = false;
        TaskInstance taskInstance = token.getTask();
        if (taskInstance != null && (StringUtils.equals(WorkFlowOperation.ROLLBACK, taskInstance.getActionType())
                || StringUtils.equals(WorkFlowOperation.DIRECT_ROLLBACK, taskInstance.getActionType()))) {
            TaskActivityService taskActivityService = ApplicationContextHolder.getBean(TaskActivityService.class);
            TaskActivity taskActivity = taskActivityService.getByTaskInstUuid(taskInstance.getUuid());
            if (taskActivity == null) {
                return isReturnAfterRollback;
            }
            String toTaskId = taskActivity.getPreTaskId();
            if (flowDelegate.isXmlDefinition()) {
                if (flowDelegate.isAllowReturnAfterRollback(toTaskId)) {
                    // 仅允许提交至本环节
                    if (token.getFlowDelegate().isOnlyReturnAfterRollback(toTaskId)) {
                        List<Node> tos = Lists.newArrayList(this.flowDelegate.getTaskNode(toTaskId));
                        transition.setTos(tos);
                        taskData.resetTaskUsers(toTaskId);
                        taskData.addTaskUsers(toTaskId, Lists.<String>newArrayList(taskActivity.getCreator()));
                        isReturnAfterRollback = true;
                    } else {
                        // 退回后允许直接提交至本环节
                        if (StringUtils.equals(toTaskId, taskData.getToTaskId(fromTaskId))) {
                            List<Node> tos = Lists.newArrayList(this.flowDelegate.getTaskNode(toTaskId));
                            transition.setTos(tos);
                            taskData.resetTaskUsers(toTaskId);
                            taskData.addTaskUsers(toTaskId, Lists.<String>newArrayList(taskActivity.getCreator()));
                            isReturnAfterRollback = true;
                        } else {
                            taskData.setTaskOfAllowReturnAfterRollback(fromTaskId, toTaskId);
                        }
                    }
                }
            } else {
                TaskElement taskElement = flowDelegate.getFlow().getTask(toTaskId);
                RightConfigElement rightConfigElement = taskElement.getTodoRightConfig();
                if (rightConfigElement == null) {
                    return isReturnAfterRollback;
                }
                String submitModeOfAfterRollback = rightConfigElement.getSubmitModeOfAfterRollback();
                // 重新逐环节提交
                if (StringUtils.equals("default", submitModeOfAfterRollback)) {
                } else if (StringUtils.equals("alternative", submitModeOfAfterRollback)) {
                    // 可选提交至本环节
                    if (StringUtils.equals(toTaskId, taskData.getToTaskId(fromTaskId))) {
                        List<Node> tos = Lists.newArrayList(this.flowDelegate.getTaskNode(toTaskId));
                        transition.setTos(tos);
                        taskData.resetTaskUsers(toTaskId);
                        taskData.addTaskUsers(toTaskId, Lists.<String>newArrayList(taskActivity.getCreator()));
                        isReturnAfterRollback = true;
                    } else {
                        taskData.setTaskOfAllowReturnAfterRollback(fromTaskId, toTaskId);
                    }
                } else {
                    // 直接提交至本环节
                    List<Node> tos = Lists.newArrayList(this.flowDelegate.getTaskNode(toTaskId));
                    transition.setTos(tos);
                    taskData.resetTaskUsers(toTaskId);
                    taskData.addTaskUsers(toTaskId, Lists.<String>newArrayList(taskActivity.getCreator()));
                    isReturnAfterRollback = true;
                    // 直接提交至本环节时抄送跳过的环节
                    if (rightConfigElement.isCopyToSkipTask()) {
                        List<TaskActivityQueryItem> activityItems = taskActivityService.getAllActivityByTaskInstUuid(token.getTask().getUuid());
                        TaskActivityStack stack = TaskActivityStackFactary.build(taskActivity.getPreTaskInstUuid(), activityItems);
                        List<TaskActivityItem> items = stack.getAvailableToRollbackTaskActivityItems();

                        List<FlowUserSid> copyToUserIds = null;
                        boolean startSkip = false;
                        List<String> skipTaskIds = Lists.newArrayList();
                        for (TaskActivityItem item : items) {
                            if (StringUtils.equals(item.getTaskId(), fromTaskId)) {
                                startSkip = true;
                                continue;
                            }
                            if (startSkip) {
                                skipTaskIds.add(item.getTaskId());
                            }
                        }
                        if (CollectionUtils.isNotEmpty(skipTaskIds)) {
                            TaskHistoryIdentityResolver historyIdentityResolver = ApplicationContextHolder.getBean(TaskHistoryIdentityResolver.class);
                            copyToUserIds = historyIdentityResolver.resolve(tos.get(0), token, skipTaskIds, ParticipantType.CopyUser);
                        }
                        if (CollectionUtils.isNotEmpty(copyToUserIds)) {
                            taskData.setSkipTaskCopyUsers(copyToUserIds);
                        }
                    }
                }
            }
        }
        return isReturnAfterRollback;
    }

    /**
     * @param transition
     */
    public boolean evaluateTaskUser(Transition transition) {
        TaskData taskData = token.getTaskData();
        List<Node> tos = transition.getTos();

        String fromTaskId = transition.getFromId();
        if (taskData.isCancel(fromTaskId)) {
            return true;
        }
        if (taskData.isRollback(fromTaskId)) {
            return true;
        }

        // 3、用户计算
        boolean useCustomButton = taskData.getUseCustomDynamicButton(fromTaskId);
        boolean startNewFlow = taskData.getStartNewFlow(token.getFlowInstance().getUuid());
        for (Node to : tos) {
            // 任务节点
            if (to instanceof ScriptNode) {
                continue;
            }

            String toTaskId = to.getId();
            Direction direction = flowDelegate.getDirection(fromTaskId, toTaskId);
            if (direction != null && StringUtils.equals(FlowConstant.BRANCH_MODE.DYNAMIC, direction.getBranchMode())) {
                // 动态分支的流程不用先计算用户
            } else if (useCustomButton && !startNewFlow) {
                if (to instanceof TaskNode || to instanceof SubTaskNode || to instanceof CollaborationTask) {
                    CustomDynamicButton customButton = taskData.getCustomDynamicButton(fromTaskId);
                    customButton = token.getFlowDelegate().convertTaskCustomDynamicButton(fromTaskId, customButton);
                    // 用户计算、权限计算在进入结点时计算
                    // 提交按钮主送对象
                    List<String> rawUserIds = new ArrayList<String>(0);
                    if (customButton != null) {
                        rawUserIds = customButton.getUsers();
                    }
                    // 如果自定义的按钮没有配置参与人则从环节配置中取
                    if (IdentityResolverStrategy.isCustomBtnUsersEmpty(rawUserIds)) {
                        List<FlowUserSid> userIds = getIdentityResolverStrategy().resolve(to, token);
                        taskData.addTaskUserSids(toTaskId, userIds);
                    } else if (rawUserIds.size() == 4) {
                        List<FlowUserSid> userIds = IdentityResolverStrategy.resolveCustomBtnUsers(token, to,
                                rawUserIds, ParticipantType.TodoUser);

                        if (userIds.isEmpty()) {
                            // 解析前台界面用户选择的组织框选择的办理人员
                            userIds.addAll(IdentityResolverStrategy.resolveTaskUsers(token, to, toTaskId));
                        }
                        // 如果自定义按钮解析出来的用户为空
                        // String key = WorkFlowVariables.FLOW_USERS +
                        // to.getId();
                        // token.getVariables().put(key, userIds);
                        taskData.addTaskUserSids(toTaskId, userIds);
                    } else {
                        throw new WorkFlowException("Custom dynamic button user id is not set");
                    }

                    // 提交按钮抄送对象
                    List<String> rawCopyUserIds = new ArrayList<String>(0);
                    if (customButton != null) {
                        rawCopyUserIds = customButton.getCopyUsers();
                    }
                    if (rawCopyUserIds.size() == 4) {
                        List<FlowUserSid> copyUserIds = IdentityResolverStrategy.resolveCustomBtnUsers(token, to,
                                rawCopyUserIds, ParticipantType.CopyUser);
                        // 解析前台界面用户选择的组织框选择的抄送人员
                        // copyUserIds.addAll(IdentityResolverStrategy.resolveTaskCopyUsers(token,
                        // to, taskId));
                        // 如果自定义按钮解析出来的用户为空
                        // 如果自定义的按钮没有配置参与人则从环节配置中取
                        if (!copyUserIds.isEmpty()) {
                            taskData.addTaskCopyUserSids(toTaskId, copyUserIds);
                        }
                    }
                }
            } else {
                // Node node = transition.getTo();
                List<FlowUserSid> userIds = new ArrayList<FlowUserSid>(0);
                if (to instanceof TaskNode || to instanceof SubTaskNode || to instanceof CollaborationTask) {
                    // 用户计算
                    if (taskData.getAutoSubmit(transition.getFrom().getId())) {
                        List<String> rawAutoSubmitUsers = taskData.getAutoSubmitUsers(
                                transition.getFrom().getId());
                        String sidGranularity = taskData.getSidGranularity(transition.getFrom().getId());
                        if (StringUtils.isNotBlank(sidGranularity)) {
                            taskData.setSidGranularity(to.getId(), sidGranularity);
                        }
                        userIds = IdentityResolverStrategy.resolveFlowUserSids(to, token, rawAutoSubmitUsers);
                        // 自动提交的用户为空则取流程配置
                        if (userIds.isEmpty()) {
                            userIds = getIdentityResolverStrategy().resolve(to, token);
                        }
                        taskData.addTaskUserSids(toTaskId, userIds);
                    } else {
                        userIds = getIdentityResolverStrategy().resolve(to, token);
                        Set<FlowUserSid> taskUserSids = taskData.getTaskUserSids(toTaskId);
                        if (CollectionUtils.isNotEmpty(taskUserSids)) {
                            taskUserSids.clear();
                        }
                        taskData.addTaskUserSids(toTaskId, userIds);
                    }
                }
            }

            // RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
            // ruleEngine.setVariable("node", to);
            // ruleEngine.setVariable("token", token);
            // String scriptText =
            // "SetOperation A = LeaderOf(Unit('J0000000566,U0010000036')) ∩ (TaskHistory('T804') ∪ FormField('tdr,iofs')) ∪ Option('Creator'); end";
            // Result result = ruleEngine.execute(scriptText);
            // Object a = ruleEngine.getVariable("A");
            // System.out.println(result);
            // System.out.println(a);

            // RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
            // Map<String, String> map = new HashMap<String, String>();
            // map.put("zjxj", "M5");
            // map.put("zjxj", "M6");
            // ruleEngine.setVariable("dyFormData",
            // token.getTaskData().getDyFormData(token.getTaskData().getDataUuid()));
            // ruleEngine.setVariable("dyform", map);
            // ruleEngine.setVariable("node", to);
            // ruleEngine.setVariable("token", token);
            // ruleEngine.setVariable("fromTaskId", "T160");
            // String exp =
            // "(5 + 6 > 8 && ${fromTaskId} == 'T1160') || 1 != 2 && (${dyform.bz} == 'M6' && ${dyform.bz} not contains '测试112233')";
            // String scriptText = "if (" + exp +
            // "){ set conditionResult = true end }  end";
            // Result result = ruleEngine.execute(scriptText);
            // Object a = ruleEngine.getVariable("conditionResult");
            // System.out.println(result);
            // System.out.println(a);

            // 用户权限计算
            // 如果是启动流程则无需进行权限判断
            if (!taskData.getStartNewFlow(this.token.getFlowInstance().getUuid())
                    && (to instanceof TaskNode || to instanceof SubTaskNode)) {
                FlowPermissionEvaluator permissionEvaluator = ApplicationContextHolder
                        .getBean(FlowPermissionEvaluator.class);
                // 权限判断，判断流程的参与者是否为参与权限
                permissionEvaluator.hasPermission(this.flowDefinition, toTaskId,
                        taskData.getTaskUsers(toTaskId), AclPermission.TODO);
            }

            // 设置任务环节的督办人及监控人
            if (to instanceof TaskNode || to instanceof SubTaskNode) {
                // 设置督办人
                setFlowMonitors(to);
                // 设置监控人
                setFlowAdmins(to);
                // 设置阅读者
                setFlowViewers(to);
            }

            // 协作环节决策人员
            if (to instanceof CollaborationTask) {
                // 设置环节决策人员
                setDecisionMakers(to);
            }
        }
        return true;
    }

    /**
     * 环节决策人员
     *
     * @param to
     */
    private void setDecisionMakers(Node to) {
        String taskId = to.getId();
        List<FlowUserSid> decisionMakerIds = new ArrayList<FlowUserSid>();
        Set<String> rawDecisionMakerIds = token.getTaskData().getTaskDecisionMakers(taskId);
        if (CollectionUtils.isEmpty(rawDecisionMakerIds)) {
            decisionMakerIds.addAll(getIdentityResolverStrategy().resolve(to, token,
                    token.getFlowDelegate().getTaskDecisionMakers(taskId), ParticipantType.TodoUser, SidGranularity.USER));
            if (CollectionUtils.isEmpty(decisionMakerIds)) {
                Map<String, Object> variables = new HashMap<String, Object>();
                String name = token.getFlowDelegate().getFlow().getName();
                variables.put("title", "(" + name + ":" + to.getName() + ")");
                variables.put("taskName", to.getName());
                variables.put("taskId", taskId);
                variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
                throw new TaskNotAssignedDecisionMakerException(variables, token);
            }
        } else {
            decisionMakerIds.addAll(IdentityResolverStrategy.resolveFlowUserSids(to, token, rawDecisionMakerIds));
            token.getTaskData().getTaskDecisionMakerSids(taskId).clear();
            List<String> jobPaths = token.getTaskData().getTaskUserJobPaths("decisionMakers_" + taskId);
            if (CollectionUtils.isNotEmpty(jobPaths)) {
                FlowUserJobIdentityService flowUserJobIdentityService = ApplicationContextHolder.getBean(FlowUserJobIdentityService.class);
                flowUserJobIdentityService.addUnitUserJobIdentity(decisionMakerIds, jobPaths, false, to.getId(), token, ParticipantType.TodoUser);
            }
        }

        token.getTaskData().addTaskDecisionMakerSids(taskId, decisionMakerIds);
    }

    /**
     * 设置流程的督办人
     *
     * @param to
     */
    private void setFlowMonitors(Node to) {
        // 设置督办人
        String taskId = to.getId();
        FlowDelegate flowDelegate = token.getFlowDelegate();
        TaskData taskData = token.getTaskData();
        String isSetMonitor = flowDelegate.getIsSetMonitor(to.getId());
        boolean isInheritMonitor = flowDelegate.getIsInheritMonitor(to.getId());
        List<FlowUserSid> monitorIds = new ArrayList<FlowUserSid>();
        // 1、不设置督办人
        if ("0".equals(isSetMonitor) || StringUtils.isBlank(isSetMonitor)) {
            List<UserUnitElement> flowMonitors = flowDelegate.getFlowMonitors();
            // 流程督办人的权限颗粒度
            String flowGranularity = flowDelegate.getFlowGranularity(flowMonitors);
            // 流程督办人
            monitorIds.addAll(getIdentityResolverStrategy().resolve(to, token, flowMonitors, ParticipantType.SuperviseUser, flowGranularity));
            taskData.addTaskMonitorSids(taskId, monitorIds);
        } else if ("1".equals(isSetMonitor)) { // 2、现在确定
            monitorIds.addAll(getIdentityResolverStrategy().resolve(to, token,
                    flowDelegate.getTaskMonitors(taskId), ParticipantType.SuperviseUser));
            taskData.put("resolvedTaskMonitorIds_" + taskId, Lists.newArrayList(monitorIds));
            // 继承已存在的督办人
            List<FlowUserSid> inheritMonitors = new ArrayList<FlowUserSid>(0);
            if (isInheritMonitor) {
                inheritMonitors = getIdentityResolverStrategy().resolve(to, token,
                        flowDelegate.getFlowMonitors(), ParticipantType.SuperviseUser);
            }
            // 添加环节督办人的流程参数
            addTaskMonitorParameters(monitorIds, inheritMonitors, token.getFlowInstance().getUuid(), to);
            // 流程督办人
            monitorIds.addAll(inheritMonitors);
            taskData.addTaskMonitorSids(taskId, monitorIds);
        } else if ("2".equals(isSetMonitor)) { // 3、由前一环节办理人确定
            Set<String> rawMonitorIds = taskData.getTaskMonitors(taskId);
            if (CollectionUtils.isEmpty(rawMonitorIds) && !taskData.isDaemon()) {
                // 非后台提交运行的工作前端需选择督办人
                String name = flowDelegate.getFlow().getName();
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("title", "(" + name + ":" + to.getName() + ")");
                variables.put("taskId", taskId);
                variables.put("submitButtonId", taskData.getSubmitButtonId());
                throw new TaskNotAssignedMonitorException(variables, token);
            } else {
                monitorIds.addAll(IdentityResolverStrategy.resolveFlowUserSids(to, token, rawMonitorIds));
            }
            taskData.put("resolvedTaskMonitorIds_" + taskId, Lists.newArrayList(monitorIds));
            // 继承已存在的督办人
            List<FlowUserSid> inheritMonitors = new ArrayList<FlowUserSid>(0);
            if (isInheritMonitor) {
                inheritMonitors = getIdentityResolverStrategy().resolve(to, token,
                        flowDelegate.getFlowMonitors(), ParticipantType.SuperviseUser);
            }
            // 添加环节督办人的流程参数
            addTaskMonitorParameters(monitorIds, inheritMonitors, token.getFlowInstance().getUuid(), to);
            // 流程督办人
            monitorIds.addAll(inheritMonitors);
            // 清空办理人前台页面选择确定的
            taskData.getTaskMonitorSids(taskId).clear();
            taskData.addTaskMonitorSids(taskId, monitorIds);
        }
    }

    /**
     * 添加环节督办人的流程参数
     *
     * @param monitorIds
     * @param inheritMonitors
     * @param flowInstUuid
     * @param to
     */
    private void addTaskMonitorParameters(List<FlowUserSid> monitorIds, List<FlowUserSid> inheritMonitors,
                                          String flowInstUuid, Node to) {
        List<String> tmpMonitorIds = new ArrayList<String>();
        for (FlowUserSid flowUserSid : monitorIds) {
            tmpMonitorIds.add(flowUserSid.getId());
        }
        if (CollectionUtils.isNotEmpty(inheritMonitors)) {
            for (FlowUserSid inheritMonitor : inheritMonitors) {
                tmpMonitorIds.remove(inheritMonitor.getId());
            }
        }

        if (CollectionUtils.isEmpty(tmpMonitorIds)) {
            return;
        }

        FlowService flowService = ApplicationContextHolder.getBean(FlowService.class);
//        for (String tmpMonitorId : tmpMonitorIds) {
        FlowInstanceParameter parameter = new FlowInstanceParameter();
        parameter.setFlowInstUuid(flowInstUuid);
        parameter.setName("taskSuperviseIds_" + to.getId());
        parameter.setValue(StringUtils.join(tmpMonitorIds, Separator.SEMICOLON.getValue()));
        flowService.saveFlowInstanceParameter(parameter);
//        }
    }

    /**
     * 设置流程的监控人
     */
    private void setFlowAdmins(Node node) {
        List<UserUnitElement> rawAdmins = token.getFlowDelegate().getFlowAdmins();
        // 流程监控者的权限颗粒度
        String flowGranularity = token.getFlowDelegate().getFlowGranularity(rawAdmins);
        List<FlowUserSid> adminIds = new ArrayList<FlowUserSid>();
        // 不管有无设置监控者权限，默认增加管理员拥有监控权限 modify by wujx 2015-12-25 begin
        // OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        List<String> rawOrgIds = workflowOrgService.listCurrentTenantAdminIds(OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
        // List<String> rawOrgIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        adminIds.addAll(IdentityResolverStrategy.resolveFlowUserSids(node, token, rawOrgIds));
        adminIds.addAll(getIdentityResolverStrategy().resolve(node, token, rawAdmins, ParticipantType.MonitorUser,
                flowGranularity));
        // // 没有设置监控人，则默认系统管理员有权限
        // if (rawAdmins.isEmpty()) {
        // OrgApiFacade orgApiFacade =
        // ApplicationContextHolder.getBean(OrgApiFacade.class);
        // adminIds.addAll(orgApiFacade.getAllAdminIds());
        // } else {
        // adminIds.addAll(getIdentityResolverStrategy().resolve(node, token,
        // rawAdmins, ParticipantType.MonitorUser));
        // }
        // 不管有无设置监控者权限，默认增加管理员拥有监控权限 modify by wujx 2015-12-25 end
        // 放入流程变量
        token.getTaskData().addTaskAdminSids(node.getId(), adminIds);
    }

    /**
     * 设置流程的阅读者
     *
     * @param node
     */
    private void setFlowViewers(Node node) {
        List<UserUnitElement> rawViewers = token.getFlowDelegate().getFlowViewers();
        // 流程阅读者的权限颗粒度
        String flowGranularity = token.getFlowDelegate().getFlowGranularity(rawViewers);
        List<FlowUserSid> viewerIds = new ArrayList<FlowUserSid>();
        viewerIds.addAll(getIdentityResolverStrategy().resolve(node, token, rawViewers, ParticipantType.ViewerUser,
                flowGranularity));
        // 放入流程变量
        token.getTaskData().addTaskViewerSids(node.getId(), viewerIds);
    }

    /**
     * @param subTaskNode
     */
    public void startSubTasks(SubTaskNode subTaskNode) {
        StopWatch stopWatch = new StopWatch("startSubTasks");
        stopWatch.start("分发子流程");
        TaskData taskData = this.getToken().getTaskData();
        Object dispatcherFlowResolverName = taskData.getCustomData(CustomRuntimeData.KEY_DISPATCHER_FLOW_RESOLVER);
        DispatcherFlowResolver dispatcherFlowResolver = null;
        if (dispatcherFlowResolverName == null) {
            dispatcherFlowResolver = ApplicationContextHolder.getBean("defaultDispatcherFlowResolver",
                    DefaultDispatcherFlowResolver.class);
        } else {
            dispatcherFlowResolver = ApplicationContextHolder.getBean(dispatcherFlowResolverName.toString(),
                    DispatcherFlowResolver.class);
        }
        dispatcherFlowResolver.resolve(this, subTaskNode);
        stopWatch.stop();
        if (logger.isInfoEnabled()) {
            logger.info("分发{}个子流程，共耗时{}秒", getToken().getTaskData().getSubmitResult().getSubFlowInstUUids().size(),
                    stopWatch.getLastTaskInfo().getTimeSeconds());
        }
    }

    /**
     * 判断子流程是否同步启动
     *
     * @param subTaskNode
     * @return
     */
    public boolean isSubTaskSyncStartd(SubTaskNode subTaskNode) {
        return getToken().getTaskData().getSubmitResult().isSubFlowSyncStartd();
    }

}

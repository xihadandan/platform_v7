/*
 * @(#)2015-3-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.access.*;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.delegation.service.TaskDelegationTakeBackService;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.*;
import com.wellsoft.pt.bpm.engine.executor.RollBackTask;
import com.wellsoft.pt.bpm.engine.executor.RollbackTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStack;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStackFactary;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskOperationItem;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.log.entity.UserOperationLog;
import com.wellsoft.pt.log.service.UserOperationLogService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-10.1	zhulh		2015-3-10		Create
 * </pre>
 * @date 2015-3-10
 */
@Service
public class SameUserSubmitServiceImpl implements SameUserSubmitService {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private IdentityResolverStrategy identityResolverStrategy;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private UserOperationLogService userOperationLogService;

    @Autowired
    private UnitIdentityResolver unitIdentityResolver;

    @Autowired
    private BizUnitIdentityResolver bizUnitIdentityResolver;

    @Autowired
    private FormFieldIdentityResolver formFieldIdentityResolver;

    @Autowired
    private TaskHistoryIdentityResolver taskHistoryIdentityResolver;

    @Autowired
    private AclTaskService aclTaskService;

    @Autowired
    private TaskDelegationTakeBackService taskDelegationTakeBackService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.SameUserSubmitService#doSameUserSubmit(com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    @Transactional
    public void doSameUserSubmit(ExecutionContext executionContext, String taskId, String sameUserSubmit) {
        TaskData taskData = executionContext.getToken().getTaskData();
        String taskInstUuid = executionContext.getToken().getTask().getUuid();
        UserDetails user = taskData.getUserDetails();
        List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuidAndUserDetails(taskInstUuid, user);
        if (CollectionUtils.isEmpty(taskIdentities)) {
            return;
        }

        // 按顺序办理的情况下需要第一个办理
        boolean firstOrder = true;
        for (TaskIdentity taskIdentity : taskIdentities) {
            if (taskIdentity.getSortOrder() != null && !Integer.valueOf(0).equals(taskIdentity.getSortOrder())) {
                firstOrder = false;
            }
        }
        if (!firstOrder) {
            return;
        }

        // 记录当前用户可进行自动提交的<流程环节实例UUID，前环节操作UUID>
        SubmitResult submitResult = taskData.getSubmitResult();
        String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
        String taskName = executionContext.getToken().getTask().getName();
        for (TaskOperationItem item : submitResult.getTaskOperationItems()) {
            String taskOperationUuid = item.getUuid();
            String optTaskInstUuid = item.getTaskInstUuid();
            if (StringUtils.equals(optTaskInstUuid, preTaskInstUuid)) {
                submitResult.setSameUserSubmitType(sameUserSubmit);
                submitResult.setSameUserSubmitTaskName(taskName);
                submitResult.setSameUserSubmitTaskInstUuid(taskInstUuid);
                submitResult.setSameUserSubmitTaskOperationUuid(taskOperationUuid);
            }
        }
    }

    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     */
    @Override
    @Transactional
    public void doAutoSubmit(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        String mode = autoSubmitRuleElement.getMode();
        if (StringUtils.isBlank(mode)) {
            return;
        }
        String taskInstUuid = executionContext.getToken().getTask().getUuid();
        List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuid(taskInstUuid);
        if (CollectionUtils.isEmpty(taskIdentities)) {
            return;
        }

        switch (mode) {
            // 前置审批
            case "before":
                doBeforeAutoSubmit(executionContext, node, autoSubmitRuleElement, taskIdentities);
                break;
            // 后置审批
            case "after":
                doAfterAutoSubmit(executionContext, node, autoSubmitRuleElement, taskIdentities);
                break;
            default:
                break;
        }
    }

    /**
     * @param flowInstUuid
     * @return
     */
    private FlowInstanceParameter getSameUserAutoSubmitParameter(String flowInstUuid) {
        FlowInstanceParameter parameter = flowInstanceParameterService.getByFlowInstUuidAndName(flowInstUuid, "sameUserAutoSubmit");
        if (parameter == null) {
            parameter = new FlowInstanceParameter();
            parameter.setFlowInstUuid(flowInstUuid);
            parameter.setName("sameUserAutoSubmit");
            parameter.setValue(JsonUtils.object2Json(new SameUserAutoSubmit()));
        }
        return parameter;
    }

    /**
     * @param sameUserAutoSubmit
     * @param flowInstUuid
     * @return
     */
    private FlowInstanceParameter saveSameUserAutoSubmitParameter(SameUserAutoSubmit sameUserAutoSubmit, String flowInstUuid) {
        FlowInstanceParameter parameter = getSameUserAutoSubmitParameter(flowInstUuid);
        if (parameter != null) {
            parameter.setValue(JsonUtils.object2Json(sameUserAutoSubmit));
        } else {
            parameter = new FlowInstanceParameter();
            parameter.setFlowInstUuid(flowInstUuid);
            parameter.setName("sameUserAutoSubmit");
            parameter.setValue(JsonUtils.object2Json(sameUserAutoSubmit));
        }
        flowInstanceParameterService.save(parameter);
        return parameter;
    }

    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @param taskIdentities
     * @param matchIdentities
     * @return
     */
    private SameUserAutoSubmit updateExitCondition(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement,
                                                   List<TaskIdentity> taskIdentities, List<TaskIdentity> matchIdentities) {
        String flowInstUuid = executionContext.getFlowInstance().getUuid();
        FlowInstanceParameter parameter = getSameUserAutoSubmitParameter(flowInstUuid);
        SameUserAutoSubmit sameUserAutoSubmit = JsonUtils.json2Object(parameter.getValue(), SameUserAutoSubmit.class);
        sameUserAutoSubmit.setMode(autoSubmitRuleElement.getMode());

        boolean exit = sameUserAutoSubmit.isExit();
        // 退出范围，single单次退出前置审批、all全流程退出前置审批
        String exitScope = autoSubmitRuleElement.getExitScope();
        if (StringUtils.equals("all", exitScope) && exit) {
            return sameUserAutoSubmit;
        }

        // 退出规则
        List<String> exitConditions = autoSubmitRuleElement.getExitConditions();
        if (CollectionUtils.isNotEmpty(exitConditions)) {
            boolean singleUser = CollectionUtils.size(taskIdentities) <= 1 || CollectionUtils.size(taskIdentities) == CollectionUtils.size(matchIdentities);
            for (String exitCondition : exitConditions) {
                switch (exitCondition) {
                    // 后续环节数据版本发生变更
                    case "dataChanged":
                        updateExitConditionOnDataChanged(sameUserAutoSubmit, singleUser, exitConditions, node, executionContext);
                        break;
                    // 后续环节可编辑表单时/前序环节可编辑表单时
                    case "canEditForm":
                        updateExitConditionOnCanEditForm(sameUserAutoSubmit, singleUser, exitConditions, node, executionContext);
                        break;
                    // 后续环节可编辑表单且存在必填字段时/前序环节可编辑表单且存在必填字段时
                    case "editAndRequiredField":
                        updateExitConditionOnEditAndRequiredField(sameUserAutoSubmit, singleUser, exitConditions, node, executionContext);
                        break;
                    // 后续环节需要选择流向时/前序环节需要选择流向时
                    case "chooseDirection":
                        updateExitConditionOnChooseDirection(sameUserAutoSubmit, singleUser, exitConditions, node, executionContext);
                        break;
                    // 后续环节需要选择下一环节办理人/抄送人时/前序环节需要选择下一环节办理人/抄送人时
                    case "chooseUser":
                        updateExitConditionOnChooseUser(sameUserAutoSubmit, singleUser, exitConditions, node, executionContext);
                        break;
                }
            }
        }

        parameter.setValue(JsonUtils.object2Json(sameUserAutoSubmit));
        flowInstanceParameterService.save(parameter);

        return sameUserAutoSubmit;
    }

    /**
     * @param sameUserAutoSubmit
     * @param singleUser
     * @param exitConditions
     * @param node
     * @param executionContext
     */
    private void updateExitConditionOnDataChanged(SameUserAutoSubmit sameUserAutoSubmit, boolean singleUser,
                                                  List<String> exitConditions, Node node, ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        String dataUuid = taskData.getDataUuid();
        Map<String, Map<String, List<String>>> updatedFormDatas = taskData.getUpdatedFormDatas(dataUuid);
        boolean dataChanged = false;
        if (MapUtils.isEmpty(updatedFormDatas)) {
            sameUserAutoSubmit.setDataChanged(false);
            return;
        }

        List<Record> records = taskData.getRecords(taskData.getPreTaskId(node.getId()));
        for (Map.Entry<String, Map<String, List<String>>> entry : updatedFormDatas.entrySet()) {
            Map<String, List<String>> mainFormDatas = entry.getValue();
            // 主表单数据发生变更
            if (StringUtils.equals(taskData.getFormUuid(), entry.getKey())) {
                if (MapUtils.isNotEmpty(mainFormDatas) && CollectionUtils.isNotEmpty(mainFormDatas.get(dataUuid))) {
                    List<String> updatedFields = Lists.newArrayList(mainFormDatas.get(dataUuid));
                    List<String> recordFields = records.stream().map(Record::getField).collect(Collectors.toList());
                    updatedFields.removeAll(recordFields);
                    if (CollectionUtils.isNotEmpty(updatedFields)) {
                        dataChanged = true;
                    } else {
                        dataChanged = false;
                    }
                    break;
                }
            } else {
                if (MapUtils.isNotEmpty(mainFormDatas)) {
                    for (Map.Entry<String, List<String>> subEntry : mainFormDatas.entrySet()) {
                        if (CollectionUtils.isNotEmpty(subEntry.getValue())) {
                            dataChanged = true;
                            break;
                        }
                    }
                } else {
                    dataChanged = false;
                }
            }

            if (dataChanged) {
                break;
            }
        }
        sameUserAutoSubmit.setDataChanged(dataChanged);
    }

    /**
     * @param sameUserAutoSubmit
     * @param singleUser
     * @param exitConditions
     * @param node
     * @param executionContext
     */
    private void updateExitConditionOnCanEditForm(SameUserAutoSubmit sameUserAutoSubmit, boolean singleUser,
                                                  List<String> exitConditions, Node node, ExecutionContext executionContext) {
        FlowDelegate flowDelegate = executionContext.getFlowDelegate();
        if (flowDelegate.getFlow().getTask(node.getId()).getIsCanEditForm()) {
            // 仅一人办理或办理人全部去重时判断
            if (exitConditions.contains("singleUserOnCanEditForm")) {
                if (singleUser) {
                    sameUserAutoSubmit.setCanEditForm(true);
                } else {
                    sameUserAutoSubmit.setCanEditForm(false);
                }
            } else {
                sameUserAutoSubmit.setCanEditForm(true);
            }
        } else {
            sameUserAutoSubmit.setCanEditForm(false);
        }
    }

    /**
     * @param sameUserAutoSubmit
     * @param singleUser
     * @param exitConditions
     * @param node
     * @param executionContext
     */
    private void updateExitConditionOnEditAndRequiredField(SameUserAutoSubmit sameUserAutoSubmit, boolean singleUser, List<String> exitConditions, Node node, ExecutionContext executionContext) {
        FlowDelegate flowDelegate = executionContext.getFlowDelegate();
        TaskElement taskElement = flowDelegate.getFlow().getTask(node.getId());
        if (taskElement.getIsCanEditForm() && CollectionUtils.isNotEmpty(taskElement.getNotNullFields())) {
            // 仅一人办理或办理人全部去重时判断
            if (exitConditions.contains("singleUserOnEditAndRequiredField")) {
                if (singleUser) {
                    sameUserAutoSubmit.setEditAndRequiredField(true);
                } else {
                    sameUserAutoSubmit.setEditAndRequiredField(false);
                }
            } else {
                sameUserAutoSubmit.setEditAndRequiredField(true);
            }
        } else {
            sameUserAutoSubmit.setEditAndRequiredField(false);
        }
    }

    /**
     * @param sameUserAutoSubmit
     * @param singleUser
     * @param exitConditions
     * @param node
     * @param executionContext
     */
    private void updateExitConditionOnChooseDirection(SameUserAutoSubmit sameUserAutoSubmit, boolean singleUser,
                                                      List<String> exitConditions, Node node, ExecutionContext executionContext) {
//        String mode = sameUserAutoSubmit.getMode();
        // 前置、后置审批一样的逻辑
//        if (StringUtils.equals("before", mode) || StringUtils.equals("after", mode)) {
        FlowDelegate flowDelegate = executionContext.getToken().getFlowDelegate();
        List<Direction> directions = flowDelegate.getDirections(node.getId());
        boolean chooseDirection = false;
        if (CollectionUtils.size(directions) > 1) {
            chooseDirection = true;
        }
        if (!chooseDirection) {
            for (Direction direction : directions) {
                TaskElement taskElement = flowDelegate.getFlow().getTask(direction.getToID());
                if (taskElement != null && (flowDelegate.isConditionTask(node) || (node.getForkMode() == ForkMode.MULTI.getValue()
                        && taskElement.getParallelGateway().getIsChooseForkingDirection()))) {
                    chooseDirection = true;
                    break;
                }
            }
        }
        if (chooseDirection) {
            // 仅一人办理或办理人全部去重时判断
            if (exitConditions.contains("singleUserOnChooseDirection")) {
                if (singleUser) {
                    sameUserAutoSubmit.setChooseDirection(true);
                } else {
                    sameUserAutoSubmit.setChooseDirection(false);
                }
            } else {
                sameUserAutoSubmit.setChooseDirection(true);
            }
        } else {
            sameUserAutoSubmit.setChooseDirection(false);
        }
//        } else {
//            FlowDelegate flowDelegate = executionContext.getFlowDelegate();
//            TaskElement taskElement = flowDelegate.getFlow().getTask(node.getId());
//            List<Direction> directions = flowDelegate.getDirections(node.getId());
//            if (CollectionUtils.size(directions) > 1 || flowDelegate.isConditionTask(node) || (node.getForkMode() == ForkMode.MULTI.getValue()
//                    && taskElement.getParallelGateway().getIsChooseForkingDirection())) {
//                // 仅一人办理或办理人全部去重时判断
//                if (exitConditions.contains("singleUserOnChooseDirection")) {
//                    if (singleUser) {
//                        sameUserAutoSubmit.setChooseDirection(true);
//                    } else {
//                        sameUserAutoSubmit.setChooseDirection(false);
//                    }
//                } else {
//                    sameUserAutoSubmit.setChooseDirection(true);
//                }
//            } else {
//                sameUserAutoSubmit.setChooseDirection(false);
//            }
//        }
    }

    /**
     * @param sameUserAutoSubmit
     * @param singleUser
     * @param exitConditions
     * @param node
     * @param executionContext
     */
    private void updateExitConditionOnChooseUser(SameUserAutoSubmit sameUserAutoSubmit, boolean singleUser,
                                                 List<String> exitConditions, Node node, ExecutionContext executionContext) {
//        String mode = sameUserAutoSubmit.getMode();
        // 前置、后置审批一样的逻辑
//        if (StringUtils.equals("before", mode) || StringUtils.equals("after", mode)) {
        FlowDelegate flowDelegate = executionContext.getToken().getFlowDelegate();
        List<Direction> directions = flowDelegate.getDirections(node.getId());
        boolean chooseUser = false;
        for (Direction direction : directions) {
            TaskElement taskElement = flowDelegate.getFlow().getTask(direction.getToID());
            if (taskElement != null && ((!taskElement.isSetUser() || taskElement.isSelectAgain())
                    || ("2".equals(taskElement.getIsSetCopyUser()))
                    || ("2".equals(taskElement.getIsSetMonitor())))) {
                chooseUser = true;
                break;
            }
        }
        if (chooseUser) {
            // 仅一人办理或办理人全部去重时判断
            if (exitConditions.contains("singleUserOnChooseUser")) {
                if (singleUser) {
                    sameUserAutoSubmit.setChooseUser(true);
                } else {
                    sameUserAutoSubmit.setChooseUser(false);
                }
            } else {
                sameUserAutoSubmit.setChooseUser(true);
            }
        } else {
            sameUserAutoSubmit.setChooseUser(false);
        }
//        } else {
//            // 后置审批
//            TaskData taskData = executionContext.getToken().getTaskData();
//            if (taskData.isSpecifyTaskUser(node.getId()) || taskData.isSpecifyTaskCopyUser(node.getId())) {
//                // 仅一人办理或办理人全部去重时判断
//                if (exitConditions.contains("singleUserOnChooseUser")) {
//                    if (singleUser) {
//                        sameUserAutoSubmit.setChooseUser(true);
//                    } else {
//                        sameUserAutoSubmit.setChooseUser(false);
//                    }
//                } else {
//                    sameUserAutoSubmit.setChooseUser(true);
//                }
//            } else {
//                sameUserAutoSubmit.setChooseUser(false);
//            }
//        }
    }

    /**
     * 前置审批
     *
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @param taskIdentities
     */
    private void doBeforeAutoSubmit(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement,
                                    List<TaskIdentity> taskIdentities) {
        List<TaskIdentity> matchIdentities = Lists.newArrayList();
        String flowInstUuid = executionContext.getFlowInstance().getUuid();
        // 获取前置审批人员
        List<String> operatorIds = getOperatorIds(flowInstUuid, autoSubmitRuleElement, executionContext);
        for (TaskIdentity taskIdentity : taskIdentities) {
            if (isMatchAutoSubmitRule(taskIdentity, operatorIds, executionContext, node, autoSubmitRuleElement)) {
                matchIdentities.add(taskIdentity);
            }
        }

        // 更新退出条件
        SameUserAutoSubmit sameUserAutoSubmit = updateExitCondition(executionContext, node, autoSubmitRuleElement, taskIdentities, matchIdentities);
        boolean exit = sameUserAutoSubmit.isExit();
        // 单次退出重置
        if (exit && !StringUtils.equals("all", autoSubmitRuleElement.getExitScope())) {
            sameUserAutoSubmit.reset();
            saveSameUserAutoSubmitParameter(sameUserAutoSubmit, flowInstUuid);
        }

        if (exit || CollectionUtils.isEmpty(matchIdentities)) {
            return;
        }

        // 处理方式，submit自动审批，skip自动跳过
        String handleMode = autoSubmitRuleElement.getHandleMode();
        switch (handleMode) {
            case "submit":
                handleSubmit(executionContext, node, autoSubmitRuleElement, taskIdentities, matchIdentities, sameUserAutoSubmit);
                break;
            case "skip":
                handleSkip(executionContext, node, autoSubmitRuleElement, taskIdentities, matchIdentities, sameUserAutoSubmit);
                break;
            default:
                break;
        }
    }

    /**
     * @param flowInstUuid
     * @param autoSubmitRuleElement
     * @param executionContext
     * @return
     */
    private List<String> getOperatorIds(String flowInstUuid, AutoSubmitRuleElement autoSubmitRuleElement, ExecutionContext executionContext) {
        List<String> matchTaskTypes = getMatchTaskTypes(autoSubmitRuleElement);
        if (CollectionUtils.size(matchTaskTypes) > 1) {
            List<TaskOperation> taskOperations = taskOperationService.listByFlowInstUuidAndActionCodes(flowInstUuid, WorkFlowOperation.getActionCodeOfSubmit());
            return taskOperations.stream().map(taskOperation -> {
                String assignee = taskOperation.getAssignee();
                if (ActionCode.DELEGATION_SUBMIT.getCode().equals(taskOperation.getActionCode())
                        && StringUtils.isNotBlank(taskOperation.getTaskIdentityUuid())) {
                    TaskIdentity taskIdentity = identityService.get(taskOperation.getTaskIdentityUuid());
                    if (taskIdentity != null) {
                        assignee = taskIdentity.getOwnerId();
                    }
                }
                return assignee;
            }).collect(Collectors.toList());
        }

        FlowElement flowElement = executionContext.getFlowDelegate().getFlow();
        List<TaskOperation> taskOperations = taskOperationService.listByFlowInstUuidAndActionCodes(flowInstUuid, WorkFlowOperation.getActionCodeOfSubmit());
        String startTaskId = executionContext.getFlowDelegate().getStartNode().getToID();
        List<String> matchTypes = autoSubmitRuleElement.getMatchTypes();
        Set<String> operatorIds = taskOperations.stream().filter(taskOperation -> {
            TaskElement taskElement = flowElement.getTask(taskOperation.getTaskId());
            if (taskElement == null) {
                return false;
            }
            if (StringUtils.equals(startTaskId, taskElement.getId())
                    && ((TaskNodeType.UserTask.getValue().equals(taskElement.getType()) && matchTypes.contains("taskIncludeStart"))
                    || (TaskNodeType.CollaborationTask.getValue().equals(taskElement.getType()) && matchTypes.contains("collaborationIncludeStart")))) {
                return true;
            }
            if (!matchTaskTypes.contains(taskElement.getType())) {
                return false;
            }
            return true;
        }).flatMap(taskOperation -> {
            String assignee = taskOperation.getAssignee();
            if (ActionCode.DELEGATION_SUBMIT.getCode().equals(taskOperation.getActionCode())
                    && StringUtils.isNotBlank(taskOperation.getTaskIdentityUuid())) {
                TaskIdentity taskIdentity = identityService.get(taskOperation.getTaskIdentityUuid());
                if (taskIdentity != null) {
                    assignee = taskIdentity.getOwnerId();
                }
            }
            return Stream.of(assignee);
        }).collect(Collectors.toSet());
        return Lists.newArrayList(operatorIds);
    }

    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @param taskIdentities
     * @param matchIdentities
     * @param sameUserAutoSubmit
     */
    private void handleSubmit(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement,
                              List<TaskIdentity> taskIdentities, List<TaskIdentity> matchIdentities, SameUserAutoSubmit sameUserAutoSubmit) {
        TaskData taskData = executionContext.getToken().getTaskData();
        FlowInstance flowInstance = executionContext.getFlowInstance();
        TaskInstance taskInstance = executionContext.getToken().getTask();

        // 处理匹配的待办
        boolean updateTaskIdentity = handleTaskIdentity(taskInstance, executionContext, node, taskIdentities, matchIdentities, taskIdentity -> {
            submitTaskIdentity(taskIdentity, autoSubmitRuleElement, sameUserAutoSubmit, node, executionContext);
        });
        if (updateTaskIdentity) {
            identityService.updateTaskIdentity(taskInstance, executionContext.getToken().getTaskData());
        }

        // sameUserAutoSubmit.setTaskIdentities(matchIdentities.stream().map(TaskIdentity::getUuid).collect(Collectors.toList()));
        saveSameUserAutoSubmitParameter(sameUserAutoSubmit, flowInstance.getUuid());

        afterHandleSubmitOrSkip(false, taskInstance, taskIdentities, matchIdentities, taskData, node, executionContext);
    }

    /**
     * @param taskInstance
     * @param node
     * @param executionContext
     */
    private void updateTaskActivityTransferCode(TaskInstance taskInstance, Node node, ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        String taskInstUuid = taskInstance.getUuid();
        String preTaskInstUuid = taskData.getPreTaskInstUuid(node.getId());
        TaskActivity taskActivity = taskActivityService.getByTaskInstUuid(taskInstUuid);
        Integer transferCode = taskActivity.getTransferCode();
        // 存储最初的流转代码
        String originalKey = preTaskInstUuid + "_original_transferCode";
        String keepOriginalKey = taskInstUuid + "_original_transferCode";
        if (taskData.getTransferCode(originalKey) != null) {
            taskData.setTransferCode(keepOriginalKey, taskData.getTransferCode(originalKey));
        } else {
            taskData.setTransferCode(keepOriginalKey, transferCode);
        }

        // 转换代码
        taskData.setTransferCode(taskInstance.getUuid(), TransferCode.SkipTask.getCode());
        taskActivity.setTransferCode(TransferCode.SkipTask.getCode());
        taskActivityService.save(taskActivity);
    }

    /**
     * @param taskInstance
     * @param executionContext
     * @param node
     * @param taskIdentities
     * @param matchIdentities
     * @param consumer
     */
    private boolean handleTaskIdentity(TaskInstance taskInstance, ExecutionContext executionContext, Node node,
                                       List<TaskIdentity> taskIdentities, List<TaskIdentity> matchIdentities, Consumer<TaskIdentity> consumer) {
        // 按顺序办理
        if (node instanceof TaskNode && ((TaskNode) node).isByOrder()
                && CollectionUtils.size(taskIdentities) != CollectionUtils.size(matchIdentities)) {
            Comparator<TaskIdentity> comparator = (o1, o2) -> {
                if (o1.getSortOrder() == null) {
                    return -1;
                }
                if (o2.getSortOrder() == null) {
                    return 1;
                }
                return o1.getSortOrder().compareTo(o2.getSortOrder());
            };
            Collections.sort(taskIdentities, comparator);
            Collections.sort(matchIdentities, comparator);

            TaskIdentity firstTaskIdentity = taskIdentities.get(0);
            List<TaskIdentity> allTaskIdentities = Lists.newArrayList(taskIdentities);
            allTaskIdentities.removeAll(matchIdentities);
            matchIdentities.forEach(consumer);
            if (CollectionUtils.isNotEmpty(allTaskIdentities) && firstTaskIdentity != allTaskIdentities.get(0)) {
                TaskIdentity nextTaskIdentity = allTaskIdentities.get(0);
                nextTaskIdentity.setSuspensionState(SuspensionState.NORMAL.getState());
                identityService.restore(Lists.newArrayList(nextTaskIdentity));
            }
//            int sortOrder = taskIdentities.get(0).getSortOrder();
//            List<TaskIdentity> matchOrderIdentities = Lists.newArrayList();
//            for (TaskIdentity taskIdentity : matchIdentities) {
//                if (sortOrder == taskIdentity.getSortOrder()) {
//                    matchOrderIdentities.add(taskIdentity);
//                    sortOrder++;
//                } else {
//                    break;
//                }
//            }
//            if (CollectionUtils.isNotEmpty(matchOrderIdentities)) {
//                matchOrderIdentities.forEach(consumer);
//            }
//            TaskIdentity nextTaskIdentity = taskIdentities.get(sortOrder);
//            if (nextTaskIdentity != taskIdentities.get(0)) {
//                nextTaskIdentity.setSuspensionState(SuspensionState.NORMAL.getState());
//                identityService.restore(Lists.newArrayList(nextTaskIdentity));
//            }
            return false;
        } else {
            matchIdentities.forEach(consumer);
            return true;
        }
    }

    /**
     * @param taskIdentity
     * @param autoSubmitRuleElement
     * @param sameUserAutoSubmit
     * @param node
     * @param executionContext
     */
    private void submitTaskIdentity(TaskIdentity taskIdentity, AutoSubmitRuleElement autoSubmitRuleElement,
                                    SameUserAutoSubmit sameUserAutoSubmit, Node node, ExecutionContext executionContext) {
        // 自动提交意见，latest使用最后一次人工填写意见、default使用缺省意见
        String submitOpinionMode = autoSubmitRuleElement.getSubmitOpinionMode();
        String defaultSubmitOpinionText = autoSubmitRuleElement.getDefaultSubmitOpinionText();
        FlowInstance flowInstance = executionContext.getFlowInstance();
        TaskInstance taskInstance = executionContext.getToken().getTask();
        String flowInstUuid = flowInstance.getUuid();

        String opinionText = defaultSubmitOpinionText;
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            Map<String, String> i18nMap = executionContext.getFlowDelegate().getFlow().getI18n().get(LocaleContextHolder.getLocale().toString());
            if (MapUtils.isNotEmpty(i18nMap) && StringUtils.isNotBlank(i18nMap.get("defaultSubmitOpinionText"))) {
                opinionText = i18nMap.get("defaultSubmitOpinionText");
            }
        }
        String userId = taskIdentity.getUserId();
        if (StringUtils.equals(submitOpinionMode, "latest")) {
            TaskOperation taskOperation = taskOperationService.getLastestByUserIdAndActionCodes(userId, WorkFlowOperation.getActionCodeOfSubmit(), flowInstUuid);
            if (taskOperation != null) {
                opinionText = taskOperation.getOpinionText();
            }
        }
        identityService.removeTodo(taskIdentity);
        // 任务提交，处理委托出去的任务
        taskDelegationTakeBackService.taskSubmitToTakeBack(taskIdentity.getTaskInstUuid(), userId);

        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT), ActionCode.AUTO_SUBMIT.getCode(),
                WorkFlowOperation.SUBMIT, null, null, opinionText, userId, null, null,
                taskIdentity.getUuid(), null, taskInstance, flowInstance, executionContext.getToken().getTaskData());

        sameUserAutoSubmit.getUserSkipMap().put(taskIdentity.getUserId(), node.getId());
        if (!sameUserAutoSubmit.getSkipTaskIds().contains(node.getId())) {
            sameUserAutoSubmit.getSkipTaskIds().add(node.getId());
        }
    }

    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @param taskIdentities
     * @param matchIdentities
     */
    private void handleSkip(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement,
                            List<TaskIdentity> taskIdentities, List<TaskIdentity> matchIdentities, SameUserAutoSubmit sameUserAutoSubmit) {
        TaskData taskData = executionContext.getToken().getTaskData();
        FlowInstance flowInstance = executionContext.getFlowInstance();
        TaskInstance taskInstance = executionContext.getToken().getTask();

        // 后置审批去重只有自动跳过
        ActionCode actionCode = ActionCode.SKIP_SUBMIT;// "after".equals(autoSubmitRuleElement.getMode()) ? ActionCode.SKIP_SUBMIT : ActionCode.AUTO_SUBMIT;
        // 处理匹配的待办
        boolean updateTaskIdentity = handleTaskIdentity(taskInstance, executionContext, node, taskIdentities, matchIdentities, taskIdentity -> {
            skipTaskIdentity(taskIdentity, actionCode, autoSubmitRuleElement, sameUserAutoSubmit, node, executionContext);
        });
        if (updateTaskIdentity) {
            identityService.updateTaskIdentity(taskInstance, executionContext.getToken().getTaskData());
        }

        // sameUserAutoSubmit.setTaskIdentities(matchIdentities.stream().map(TaskIdentity::getUuid).collect(Collectors.toList()));
        saveSameUserAutoSubmitParameter(sameUserAutoSubmit, flowInstance.getUuid());

        afterHandleSubmitOrSkip(true, taskInstance, taskIdentities, matchIdentities, taskData, node, executionContext);
    }

    /**
     * @param isSkip
     * @param taskInstance
     * @param taskIdentities
     * @param matchIdentities
     * @param taskData
     * @param node
     * @param executionContext
     */
    private void afterHandleSubmitOrSkip(boolean isSkip, TaskInstance taskInstance, List<TaskIdentity> taskIdentities, List<TaskIdentity> matchIdentities,
                                         TaskData taskData, Node node, ExecutionContext executionContext) {
        // 协作节点
        if (TaskNodeType.CollaborationTask.getValue().equals(node.getType())) {
            if (isSkip) {
                return;
            }
            for (int index = 0; index < matchIdentities.size() - 1; index++) {
                TaskIdentity doneTaskIdentity = matchIdentities.get(index);
                TaskIdentity identity = new TaskIdentity();
                identity.setTaskInstUuid(doneTaskIdentity.getTaskInstUuid());
                identity.setTodoType(doneTaskIdentity.getTodoType());
                identity.setUserId(doneTaskIdentity.getUserId());
                identityService.addTodo(identity);
            }
            identityService.updateTaskIdentity(taskInstance, taskData);
        } else {
            // 标记自动进入下一环节
            if (CollectionUtils.size(taskIdentities) == CollectionUtils.size(matchIdentities)
                    || !aclTaskService.hasPermission(taskInstance.getUuid(), AclPermission.TODO)) {
                taskData.setAutoCompletedTask(taskInstance.getId(), true);
                updateTaskActivityTransferCode(taskInstance, node, executionContext);
            }
        }
    }

    private void skipTaskIdentity(TaskIdentity taskIdentity, ActionCode actionCode, AutoSubmitRuleElement autoSubmitRuleElement,
                                  SameUserAutoSubmit sameUserAutoSubmit, Node node, ExecutionContext executionContext) {
        // TaskData taskData = executionContext.getToken().getTaskData();
        FlowInstance flowInstance = executionContext.getFlowInstance();
        TaskInstance taskInstance = executionContext.getToken().getTask();
        boolean keepRecord = autoSubmitRuleElement.isKeepRecord();
        String userId = taskIdentity.getUserId();
        identityService.removeTodo(taskIdentity);

        String opinionText = "";//"(重复审批自动跳过)";
        if (keepRecord) {
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT), actionCode.getCode(),
                    WorkFlowOperation.SUBMIT, null, null, opinionText, userId, null, null,
                    taskIdentity.getUuid(), null, taskInstance, flowInstance, executionContext.getToken().getTaskData());
        } else {
            saveSkipUserOperationLog(userId, taskInstance, flowInstance, opinionText);
        }

        sameUserAutoSubmit.getUserSkipMap().put(taskIdentity.getUserId(), node.getId());
        if (!sameUserAutoSubmit.getSkipTaskIds().contains(node.getId())) {
            sameUserAutoSubmit.getSkipTaskIds().add(node.getId());
        }
    }

    /**
     * @param userId
     * @param taskInstance
     * @param flowInstance
     * @param opinionText
     */
    private void saveSkipUserOperationLog(String userId, TaskInstance taskInstance, FlowInstance flowInstance, String opinionText) {
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setModuleName("工作流程");
        userOperationLog.setModuleId(ModuleID.WORKFLOW.getValue());
        userOperationLog.setOperation("SKIP_TASK_SUBMIT");
        userOperationLog.setUserName(SpringSecurityUtils.getCurrentUserName());
        userOperationLog.setContent(userId);
        userOperationLog.setDetails(taskInstance.getUuid());
        userOperationLog.setSystem(RequestSystemContextPathResolver.system());
        userOperationLog.setTenant(SpringSecurityUtils.getCurrentTenantId());
        userOperationLogService.save(userOperationLog);
    }

    /**
     * @param todoUserId
     * @param operatorIds
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @return
     */
    private boolean isMatchAutoSubmitRule(TaskIdentity taskIdentity, List<String> operatorIds, ExecutionContext executionContext,
                                          Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        boolean match = false;
        // 委托不作为重复审批
        if (WorkFlowTodoType.Delegation.equals(taskIdentity.getTodoType())) {
            return match;
        }
        String todoUserId = taskIdentity.getUserId();
        if (CollectionUtils.isEmpty(operatorIds) || !operatorIds.contains(todoUserId)) {
            return match;
        }

        // 规则生效环节
        match = isMatchEffectiveTask(executionContext, node, autoSubmitRuleElement);
        if (!match) {
            return match;
        }

        // 重复审批人员判定，start流程发起人、task审批节点办理人、collaboration协作节点办理人(不含决策人)、branch包含分支、条件分支之后环节的办理人
        List<String> matchTypes = autoSubmitRuleElement.getMatchTypes();
        if (CollectionUtils.isEmpty(matchTypes)) {
            match = false;
            return match;
        }

        for (String matchType : matchTypes) {
            switch (matchType) {
                case "start":
                    match = false;
                    break;
                case "task":
                    // 规则生效人员
                    if (TaskNodeType.UserTask.getValue().equals(node.getType())) {
                        boolean taskMatchStartUser = isMatchStartUser(todoUserId, executionContext, node, autoSubmitRuleElement);
                        if (taskMatchStartUser) {
                            if (matchTypes.contains("taskIncludeStart")) {
                                match = true;
                            } else {
                                match = false;
                            }
                        } else {
                            match = isMatchEffectiveUser(todoUserId, executionContext, node, autoSubmitRuleElement);
                        }
                    } else {
                        match = false;
                    }
                    break;
                case "collaboration":
                    // 规则生效人员
                    if (TaskNodeType.CollaborationTask.getValue().equals(node.getType())) {
                        boolean collaborationMatchStartUser = isMatchStartUser(todoUserId, executionContext, node, autoSubmitRuleElement);
                        if (collaborationMatchStartUser) {
                            if (matchTypes.contains("collaborationIncludeStart")) {
                                match = true;
                            } else {
                                match = false;
                            }
                        } else {
                            match = isMatchEffectiveUser(todoUserId, executionContext, node, autoSubmitRuleElement);
                        }
                    } else {
                        match = false;
                    }
                    break;
                case "branch":
                    match = isMatchEffectiveUser(todoUserId, executionContext, node, autoSubmitRuleElement);
                    break;
                default:
                    match = false;
                    break;
            }

            if (match) {
                break;
            }
        }

        return match;
    }

    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @return
     */
    private boolean isMatchStartUser(String todoUserId, ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        String startUserId = executionContext.getFlowInstance().getStartUserId();
        return StringUtils.equals(startUserId, todoUserId);
    }

    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @return
     */
    private boolean isMatchEffectiveTask(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        // 规则生效环节，all全流程、include指定环节、exclude除指定环节外的其他环节
        String effectiveTask = autoSubmitRuleElement.getEffectiveTask();
        if (StringUtils.equals(effectiveTask, "all")) {
            return true;
        }

        List<String> taskIds = autoSubmitRuleElement.getTaskIds();
        if (StringUtils.equals(effectiveTask, "include") && CollectionUtils.isNotEmpty(taskIds)
                && taskIds.contains(node.getId())) {
            return true;
        }

        if (StringUtils.equals(effectiveTask, "exclude") && CollectionUtils.isNotEmpty(taskIds)
                && !taskIds.contains(node.getId())) {
            return true;
        }

        return false;
    }

    /**
     * @param todoUserId
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @return
     */
    private boolean isMatchEffectiveUser(String todoUserId, ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        // 规则生效人员，all全员工、include指定人员、exclude除指定人员外的其他人员
        String effectiveUser = autoSubmitRuleElement.getEffectiveUser();
        if (StringUtils.equals(effectiveUser, "all")) {
            return true;
        }

        List<UserUnitElement> userUnitElements = autoSubmitRuleElement.getUsers();
        if (StringUtils.equals(effectiveUser, "include") && CollectionUtils.isNotEmpty(userUnitElements)) {
            List<FlowUserSid> flowUserSids = identityResolverStrategy.resolve(node, executionContext.getToken(), userUnitElements, ParticipantType.TodoUser, SidGranularity.USER);
            if (CollectionUtils.isEmpty(flowUserSids)) {
                return false;
            }
            List<String> includeUserIds = flowUserSids.stream().map(FlowUserSid::getId).collect(Collectors.toList());
            return includeUserIds.contains(todoUserId);
        }

        if (StringUtils.equals(effectiveUser, "exclude") && CollectionUtils.isNotEmpty(userUnitElements)) {
            List<FlowUserSid> flowUserSids = identityResolverStrategy.resolve(node, executionContext.getToken(), userUnitElements, ParticipantType.TodoUser, SidGranularity.USER);
            if (CollectionUtils.isEmpty(flowUserSids)) {
                return false;
            }
            List<String> includeUserIds = flowUserSids.stream().map(FlowUserSid::getId).collect(Collectors.toList());
            return !includeUserIds.contains(todoUserId);
        }

        return false;
    }

    /**
     * 后置审批
     *
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @param taskIdentities
     */
    private void doAfterAutoSubmit(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement,
                                   List<TaskIdentity> taskIdentities) {
        List<TaskIdentity> matchIdentities = Lists.newArrayList();
        // 获取后置审批人员
        List<String> operatorIds = resolveAfterUserIds(executionContext, node, autoSubmitRuleElement);
        for (TaskIdentity taskIdentity : taskIdentities) {
            if (isMatchAutoSubmitRule(taskIdentity, operatorIds, executionContext, node, autoSubmitRuleElement)) {
                matchIdentities.add(taskIdentity);
            }
        }

        // 更新退出条件
        SameUserAutoSubmit sameUserAutoSubmit = updateExitCondition(executionContext, node, autoSubmitRuleElement, taskIdentities, matchIdentities);
        boolean exit = sameUserAutoSubmit.isExit();
        // 单次退出重置
        if (exit && !StringUtils.equals("all", autoSubmitRuleElement.getExitScope())) {
            sameUserAutoSubmit.reset();
            saveSameUserAutoSubmitParameter(sameUserAutoSubmit, executionContext.getFlowInstance().getUuid());
        }

        if (exit || CollectionUtils.isEmpty(matchIdentities)) {
            return;
        }

        // 处理方式，submit自动审批，skip自动跳过
        String handleMode = autoSubmitRuleElement.getHandleMode();
        switch (handleMode) {
            case "skip":
                handleSkip(executionContext, node, autoSubmitRuleElement, taskIdentities, matchIdentities, sameUserAutoSubmit);
                break;
            default:
                break;
        }
    }

    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     * @return
     */
    private List<String> resolveAfterUserIds(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        // String flowInstUuid = executionContext.getFlowInstance().getUuid();
        RollbackTaskActionExecutor rollbackTaskActionExecutor = ApplicationContextHolder.getBean(RollbackTaskActionExecutor.class);
        List<RollBackTask> rollBackTasks = rollbackTaskActionExecutor.buildToRollbackTasks(executionContext.getToken().getTask());
        List<String> handleTaskIds = rollBackTasks.stream().map(rollBackTask -> rollBackTask.getId()).collect(Collectors.toList());
        // List<TaskOperation> taskOperations = taskOperationService.listByFlowInstUuidAndActionCodes(flowInstUuid, WorkFlowOperation.getActionCodeOfSubmit());
        // List<String> handleTaskIds = taskOperations.stream().flatMap(taskOperation -> Stream.of(taskOperation.getTaskId())).collect(Collectors.toList());
        // List<String> handleTaskIds = taskInstanceService.listIdByFlowInstUuid(flowInstUuid);
        Set<TaskElement> availableTaskElements = Sets.newHashSet();
        List<String> matchTaskTypes = getMatchTaskTypes(autoSubmitRuleElement);
        extractNextAvailableTask(node, handleTaskIds, executionContext.getFlowDelegate(), matchTaskTypes, availableTaskElements);

        Set<String> rawUnitUsers = Sets.newHashSet();
        Map<String, Set<String>> rawBizUnitUserMap = Maps.newHashMap();
        Set<String> rawFormFieldUsers = Sets.newHashSet();
        Set<String> rawTaskHistoryUsers = Sets.newHashSet();
        TaskData taskData = executionContext.getToken().getTaskData();
        String rollbackToId = null;
        if (taskData.isRollback(taskData.getPreTaskId(node.getId()))) {
            rollbackToId = node.getId();
        }
        String rollbackToTaskId = rollbackToId;
        availableTaskElements.forEach(task -> {
            List<UserUnitElement> userUnitElements = task.getUsers();
            if (CollectionUtils.isEmpty(userUnitElements) || !task.isSetUser()) {
                return;
            }
            userUnitElements.forEach(userUnitElement -> {
                Integer type = userUnitElement.getType();
                String value = userUnitElement.getValue();
                if (StringUtils.isBlank(value)) {
                    return;
                }
                List<String> values = Arrays.asList(StringUtils.split(value, Separator.SEMICOLON.getValue()));
                if (Integer.valueOf(1).equals(type)) { // 通过行政组织设置办理人
                    rawUnitUsers.addAll(values);
                } else if (Integer.valueOf(2).equals(type)) { // 通过文档域设置办理人
                    rawFormFieldUsers.addAll(values);
                } else if (Integer.valueOf(4).equals(type)) { // 通过办理环节设置办理人
                    // 排除退回的当前环节
                    if (StringUtils.isNotBlank(rollbackToTaskId)) {
                        if (!values.contains(rollbackToTaskId)) {
                            rawTaskHistoryUsers.addAll(values);
                        }
                    } else {
                        rawTaskHistoryUsers.addAll(values);
                    }
                } else if (Integer.valueOf(32).equals(type)) { // 通过业务组织设置办理人
                    Set<String> rawBizUnitUsers = rawBizUnitUserMap.get(userUnitElement.getBizOrgId());
                    if (rawBizUnitUsers == null) {
                        rawBizUnitUsers = Sets.newHashSet();
                        rawBizUnitUserMap.put(userUnitElement.getBizOrgId(), rawBizUnitUsers);
                    }
                    rawBizUnitUsers.addAll(values);
                }
            });
        });

        Token token = executionContext.getToken();
        ParticipantType participantType = ParticipantType.TodoUser;
        String sidGranularity = SidGranularity.USER;
        Set<FlowUserSid> userSids = Sets.newHashSet();
        // 1、解析组织选择框选择的用户
        List<FlowUserSid> unitUserIds = unitIdentityResolver.resolve(node, token, Lists.newArrayList(rawUnitUsers), participantType,
                sidGranularity);
        userSids.addAll(unitUserIds);
        // 2、解析表单域中的用户
        List<FlowUserSid> formFieldUserIds = formFieldIdentityResolver.resolve(node, token, Lists.newArrayList(rawFormFieldUsers),
                participantType, sidGranularity);
        userSids.addAll(formFieldUserIds);
        // 3、解析已运行过的流程的参与者
        List<FlowUserSid> taskHistoryUserIds = taskHistoryIdentityResolver.resolve(node, token, Lists.newArrayList(rawTaskHistoryUsers),
                participantType, sidGranularity);
        userSids.addAll(taskHistoryUserIds);
        // 4、 解析业务组织的参与者
        for (Map.Entry<String, Set<String>> entry : rawBizUnitUserMap.entrySet()) {
            String bizOrgId = entry.getKey();
            Set<String> rawBizUnitUsers = entry.getValue();
            UserUnitElement userUnitElement = new UserUnitElement();
            userUnitElement.setType(32);
            userUnitElement.setBizOrgId(bizOrgId);
            userUnitElement.setValue(StringUtils.join(rawBizUnitUsers, Separator.SEMICOLON.getValue()));
            List<FlowUserSid> bizUnitUserIds = bizUnitIdentityResolver.resolve(node, token, userUnitElement, participantType, sidGranularity);
            userSids.addAll(bizUnitUserIds);
        }

        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        Integer transferCode = taskData.getTransferCode(taskData.getPreTaskInstUuid(node.getId()));
        if (TransferCode.RollBack.getCode().equals(transferCode)
                || TransferCode.Cancel.getCode().equals(transferCode)) {
            return userSids.stream().filter(userSid -> !StringUtils.equals(userSid.getId(), currentUserId))
                    .map(FlowUserSid::getId).collect(Collectors.toList());
        } else {
            return userSids.stream().map(FlowUserSid::getId).collect(Collectors.toList());
        }
    }

    /**
     * @param autoSubmitRuleElement
     * @return
     */
    private List<String> getMatchTaskTypes(AutoSubmitRuleElement autoSubmitRuleElement) {
        List<String> matchTypes = autoSubmitRuleElement.getMatchTypes();
        List<String> taskTypes = Lists.newArrayList();
        if (matchTypes.contains("task")) {
            taskTypes.add(TaskNodeType.UserTask.getValue());
        }
        if (matchTypes.contains("collaboration")) {
            taskTypes.add(TaskNodeType.CollaborationTask.getValue());
        }
        return taskTypes;
    }

    /**
     * @param taskId
     * @param handleTaskIds
     * @param flowDelegate
     * @param matchTaskTypes
     * @param taskElements
     */
    private void extractNextAvailableTask(Node node, List<String> handleTaskIds, FlowDelegate flowDelegate,
                                          List<String> matchTaskTypes, Set<TaskElement> taskElements) {
        TaskNode taskNode = (TaskNode) node;
        List<String> toIds = Lists.newArrayList(taskNode.getToIDs());
        if (CollectionUtils.isEmpty(toIds)) {
            List<Direction> directions = flowDelegate.getAvailableGatewayToDirections(taskNode.getId());
            toIds.addAll(directions.stream().map(toDirection -> toDirection.getToID()).collect(Collectors.toList()));
        }
        // List<Direction> directions = flowDelegate.getDirections(taskId);
        toIds.forEach(toTaskId -> {
            // String toTaskId = direction.getToID();
            if (handleTaskIds.contains(toTaskId)) {
                return;
            }
            Node toNode = flowDelegate.getTaskNode(toTaskId);
            if (toNode != null && toNode instanceof TaskNode) {
                if (matchTaskTypes.contains(toNode.getType())) {
                    taskElements.add(flowDelegate.getFlow().getTask(toTaskId));
                }
                extractNextAvailableTask(toNode, handleTaskIds, flowDelegate, matchTaskTypes, taskElements);
            }
        });
    }

    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     */
    @Override
    @Transactional
    public boolean doAutoSupplement(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        boolean supplement = false;
        String flowInstUuid = executionContext.getFlowInstance().getUuid();
        if (taskService.countUnfinishedTasks(flowInstUuid) > 0) {
            return supplement;
        }

        // 补审补办方式，task按最后跳过环节补审补办、user按人员补审补办
        String supplementMode = autoSubmitRuleElement.getSupplementMode();
        switch (supplementMode) {
            case "task":
                supplement = doAutoSupplementByTask(executionContext, node, autoSubmitRuleElement);
                break;
            case "user":
                supplement = doAutoSupplementByUser(executionContext, node, autoSubmitRuleElement);
                break;
        }
        return supplement;
    }


    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     */
    private boolean doAutoSupplementByTask(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        String flowInstUuid = executionContext.getFlowInstance().getUuid();
        // 生成任务活动堆栈
        List<TaskActivityQueryItem> allTaskActivities = taskActivityService.getAllActivityByFlowInstUuid(flowInstUuid);
        // 生成任务活动操作堆栈
        List<TaskOperation> taskOperations = taskOperationService.getAllTaskOperationByFlowInstUuid(flowInstUuid);
        TaskActivityStack stack = TaskActivityStackFactary.build(null, allTaskActivities, taskOperations);
        // List<TaskOperation> taskOperations = taskOperationService.listByFlowInstUuidAndActionCodes(flowInstUuid, WorkFlowOperation.getActionCodeOfSubmit());
        Collections.sort(taskOperations, IdEntityComparators.CREATE_TIME_DESC);

        // 获取审批人员
        // 待补审<环节，人员>
        Map<String, List<String>> userSupplementMap = getUserSupplementMapByTask(flowInstUuid, autoSubmitRuleElement, stack);
        if (MapUtils.isEmpty(userSupplementMap)) {
            return false;
        }

        TaskData taskData = executionContext.getToken().getTaskData();
        FlowInstance flowInstance = executionContext.getFlowInstance();
        // List<String> todoUserIds = Lists.newArrayList(userSupplementMap.keySet());
        // 补审补办操作权限，submit只有提交权限、default同补审环节权限
        String supplementOperateRight = autoSubmitRuleElement.getSupplementOperateRight();
        // 补审补办表单权限, 只有阅读权限readonly、default同补审环节权限
        String supplementViewFormMode = autoSubmitRuleElement.getSupplementViewFormMode();

        FlowInstanceParameter parameter = getSupplementParameter(flowInstUuid);
        SameUserSupplementInfo supplementInfo = null;
        String preTaskInstUuid = taskData.getPreTaskInstUuid(node.getId());
        if (parameter != null) {
            supplementInfo = JsonUtils.json2Object(parameter.getValue(), SameUserSupplementInfo.class);
        } else {
            supplementInfo = new SameUserSupplementInfo();
        }

        boolean hasSupplementTask = false;
        for (Map.Entry<String, List<String>> entry : userSupplementMap.entrySet()) {
            String todoTaskId = entry.getKey();
            List<String> todoUserIds = entry.getValue();
            TaskOperation taskOperation = taskOperations.stream()
                    .filter(opt -> StringUtils.equals(opt.getTaskId(), todoTaskId) && WorkFlowOperation.isActionCodeOfSubmit(opt.getActionCode()))
                    .findFirst().orElse(null);
            TaskInstance taskInstance = null;
            if (taskOperation == null) {
                taskInstance = taskInstanceService.getLastTaskInstanceByTaskIdAndFlowInstUuid(todoTaskId, flowInstance.getUuid());
            } else {
                taskInstance = taskInstanceService.get(taskOperation.getTaskInstUuid());
            }
            if (taskInstance == null || CollectionUtils.isEmpty(todoUserIds)) {
                continue;
            }

            TaskElement taskElement = executionContext.getFlowDelegate().getFlow().getTask(taskInstance.getId());
            String taskName = (taskElement != null ? taskElement.getName() : taskInstance.getName()) + "（补审补办）";
            TaskInstance supplementTaskInstance = createSupplementTaskInstance(flowInstance, todoUserIds, taskName, taskInstance);
            // supplementTaskInstance.setIsParallel(true);
            // supplementTaskInstance.setParallelTaskInstUuid(taskInstance.getUuid());
            taskInstanceService.save(supplementTaskInstance);
            supplementInfo.addSupplementTaskUuid(preTaskInstUuid, supplementTaskInstance.getUuid());

            aclTaskService.changeAcl(preTaskInstUuid, supplementTaskInstance.getUuid());
            TaskInstance preTaskInstance = taskService.getTask(preTaskInstUuid);
            preTaskInstance.setSuspensionState(SuspensionState.SUSPEND.getState());
            taskService.save(preTaskInstance);
//            taskService.copyPermissions(preTaskInstUuid, supplementTaskInstance.getUuid(), null, null);
//            aclTaskService.removePermission(preTaskInstUuid);

            List<TaskIdentity> skipTaskIdentities = identityService.getByTaskInstUuidAndUserIds(taskInstance.getUuid(), todoUserIds);
            Collections.sort(skipTaskIdentities, IdEntityComparators.CREATE_TIME_DESC);
            // 添加待办标识
            for (String todoUserId : todoUserIds) {
                TaskIdentity taskIdentity = new TaskIdentity();
                taskIdentity.setTaskInstUuid(supplementTaskInstance.getUuid());
                taskIdentity.setUserId(todoUserId);
                taskIdentity.setTodoType(WorkFlowTodoType.Supplement);
                taskIdentity.setSuspensionState(SuspensionState.NORMAL.getState());
                taskIdentity.setViewFormMode(ViewFormMode.from(supplementViewFormMode));
                taskIdentity.setTodoTypeOperate(TodoTypeOperate.from(supplementOperateRight));
                TaskIdentity skipTaskIdentity = skipTaskIdentities.stream().filter(identity -> StringUtils.equals(todoUserId, identity.getUserId()))
                        .findFirst().orElse(null);
                if (skipTaskIdentity != null) {
                    taskIdentity.setIdentityId(skipTaskIdentity.getIdentityId());
                    taskIdentity.setIdentityIdPath(skipTaskIdentity.getIdentityIdPath());
                }
                identityService.addTodo(taskIdentity);
            }

            // 保存任务活动数据
            saveSupplementTaskActivity(node, flowInstance, taskData.getPreTaskId(node.getId()), preTaskInstUuid, supplementTaskInstance);
            hasSupplementTask = true;
        }
        if (hasSupplementTask) {
            flowInstance.setIsActive(true);
            flowInstance.setEndTime(null);
            flowInstanceService.save(flowInstance);

            // 保存补审补办流程参数
            saveSupplementParameter(supplementInfo, flowInstance.getUuid());
            return true;
        }

        return false;
    }

    /**
     * @param supplementInfo
     * @param flowInstUuid
     */
    private void saveSupplementParameter(SameUserSupplementInfo supplementInfo, String flowInstUuid) {
        FlowInstanceParameter parameter = flowInstanceParameterService.getByFlowInstUuidAndName(flowInstUuid, KEY_SUPPLEMENT_INFO);
        if (parameter != null) {
            parameter.setValue(JsonUtils.object2Json(supplementInfo));
        } else {
            parameter = new FlowInstanceParameter();
            parameter.setFlowInstUuid(flowInstUuid);
            parameter.setName(KEY_SUPPLEMENT_INFO);
            parameter.setValue(JsonUtils.object2Json(supplementInfo));
        }
        flowInstanceParameterService.save(parameter);
    }

    /**
     * @param flowInstUuid
     * @return
     */
    private FlowInstanceParameter getSupplementParameter(String flowInstUuid) {
        return flowInstanceParameterService.getByFlowInstUuidAndName(flowInstUuid, KEY_SUPPLEMENT_INFO);
    }

    /**
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     */
    private boolean doAutoSupplementByUser(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        String flowInstUuid = executionContext.getFlowInstance().getUuid();
        // 生成任务活动堆栈
        List<TaskActivityQueryItem> allTaskActivities = taskActivityService.getAllActivityByFlowInstUuid(flowInstUuid);
        // 生成任务活动操作堆栈
        List<TaskOperation> taskOperations = taskOperationService.getAllTaskOperationByFlowInstUuid(flowInstUuid);
        TaskActivityStack taskActivityStack = TaskActivityStackFactary.build(null, allTaskActivities, taskOperations);
        // List<TaskOperation> taskOperations = taskOperationService.listByFlowInstUuidAndActionCodes(flowInstUuid, WorkFlowOperation.getActionCodeOfSubmit());
        Collections.sort(taskOperations, IdEntityComparators.CREATE_TIME_DESC);

        // 待补审人员及环节
        Map<String, String> userSupplementMap = getUserSupplementMapByUser(flowInstUuid, autoSubmitRuleElement, taskActivityStack);
        if (MapUtils.isEmpty(userSupplementMap)) {
            return false;
        }

        Collections.sort(allTaskActivities, (o1, o2) -> {
            Date t1 = o1.getCreateTime();
            Date t2 = o2.getCreateTime();
            if (t1 == null || t2 == null) {
                return 1;
            }
            return -t1.compareTo(t2);
        });

        TaskData taskData = executionContext.getToken().getTaskData();
        FlowInstance flowInstance = executionContext.getFlowInstance();
        List<String> todoUserIds = Lists.newArrayList(userSupplementMap.keySet());
        String taskName = autoSubmitRuleElement.getSupplementTaskName();
        if (StringUtils.isBlank(taskName)) {
            taskName = "补审补办";
        }

        String preTaskInstUuid = taskData.getPreTaskInstUuid(node.getId());
        TaskInstance taskInstance = taskInstanceService.get(preTaskInstUuid);
        TaskInstance supplementTaskInstance = createSupplementTaskInstance(flowInstance, todoUserIds, taskName, taskInstance);
        taskInstanceService.save(supplementTaskInstance);
        flowInstance.setIsActive(true);
        flowInstance.setEndTime(null);
        flowInstanceService.save(flowInstance);

        aclTaskService.changeAcl(preTaskInstUuid, supplementTaskInstance.getUuid());

        // 添加待办标识
        for (String todoUserId : todoUserIds) {
            TaskIdentity taskIdentity = new TaskIdentity();
            taskIdentity.setTaskInstUuid(supplementTaskInstance.getUuid());
            taskIdentity.setUserId(todoUserId);
            taskIdentity.setTodoType(WorkFlowTodoType.Supplement);
            taskIdentity.setSuspensionState(SuspensionState.NORMAL.getState());
            taskIdentity.setViewFormMode(ViewFormMode.READONLY);
            taskIdentity.setTodoTypeOperate(TodoTypeOperate.SUBMIT);
            String skipTaskId = userSupplementMap.get(todoUserId);
            TaskIdentity skipTaskIdentity = null;
            if (StringUtils.isNotBlank(skipTaskId)) {
                TaskActivityQueryItem taskActivityQueryItem = allTaskActivities.stream().filter(item -> StringUtils.equals(skipTaskId, item.getTaskId()))
                        .findFirst().orElse(null);
                if (taskActivityQueryItem != null) {
                    List<TaskIdentity> skipTaskIdentities = identityService.getByTaskInstUuidAndUserIds(taskActivityQueryItem.getTaskInstUuid(), Lists.newArrayList(todoUserId));
                    if (CollectionUtils.isNotEmpty(skipTaskIdentities)) {
                        Collections.sort(skipTaskIdentities, IdEntityComparators.CREATE_TIME_DESC);
                        skipTaskIdentity = skipTaskIdentities.get(0);
                    }
                }
            }
            if (skipTaskIdentity != null) {
                taskIdentity.setIdentityId(skipTaskIdentity.getIdentityId());
                taskIdentity.setIdentityIdPath(skipTaskIdentity.getIdentityIdPath());
            }
            identityService.addTodo(taskIdentity);
        }

        // 保存任务活动数据
        saveSupplementTaskActivity(node, flowInstance, taskData.getPreTaskId(node.getId()), preTaskInstUuid, supplementTaskInstance);

        FlowInstanceParameter parameter = getSupplementParameter(flowInstUuid);
        SameUserSupplementInfo supplementInfo = null;
        if (parameter != null) {
            supplementInfo = JsonUtils.json2Object(parameter.getValue(), SameUserSupplementInfo.class);
        } else {
            supplementInfo = new SameUserSupplementInfo();
        }
        supplementInfo.addSupplementTaskUuid(preTaskInstUuid, supplementTaskInstance.getUuid());

        // 保存补审补办流程参数
        saveSupplementParameter(supplementInfo, flowInstance.getUuid());
        return true;
    }

    /**
     * @param flowInstance
     * @param todoUserIds
     * @param taskName
     * @param taskInstance
     * @return
     */
    private TaskInstance createSupplementTaskInstance(FlowInstance flowInstance, List<String> todoUserIds, String taskName, TaskInstance taskInstance) {
        TaskInstance supplementTaskInstance = new TaskInstance();
        BeanUtils.copyProperties(taskInstance, supplementTaskInstance, JpaEntity.BASE_FIELDS);
        supplementTaskInstance.setFlowInstance(flowInstance);
        supplementTaskInstance.setFlowDefinition(flowInstance.getFlowDefinition());
        supplementTaskInstance.setParallelTaskInstUuid(null);
        supplementTaskInstance.setIsParallel(false);
        supplementTaskInstance.setName(taskName);
        supplementTaskInstance.setOwner(StringUtils.join(todoUserIds, Separator.SEMICOLON.getValue()));
        supplementTaskInstance.setAssignee(SpringSecurityUtils.getCurrentUserId());
        supplementTaskInstance.setAssigneeName(SpringSecurityUtils.getCurrentUserName());
        supplementTaskInstance.setTodoUserId(supplementTaskInstance.getOwner());
        supplementTaskInstance.setTodoUserName(IdentityResolverStrategy.resolveAsNames(todoUserIds));
        supplementTaskInstance.setAction(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
        supplementTaskInstance.setActionType(WorkFlowOperation.SUBMIT);
        supplementTaskInstance.setSuspensionState(SuspensionState.NORMAL.getState());
        supplementTaskInstance.setDuration(0);
        supplementTaskInstance.setTimingState(0);
        supplementTaskInstance.setAlarmState(0);
        supplementTaskInstance.setOverDueState(0);
        supplementTaskInstance.setAlarmTime(null);
        supplementTaskInstance.setDueTime(null);
        supplementTaskInstance.setEndTime(null);
        return supplementTaskInstance;
    }

    /**
     * @param node
     * @param flowInstance
     * @param preTaskId
     * @param preTaskInstUuid
     * @param supplementTaskInstance
     */
    private void saveSupplementTaskActivity(Node node, FlowInstance flowInstance, String preTaskId, String preTaskInstUuid, TaskInstance supplementTaskInstance) {
        TaskActivity taskActivity = new TaskActivity();
        taskActivity.setTaskId(supplementTaskInstance.getId());
        taskActivity.setTaskInstUuid(supplementTaskInstance.getUuid());
        taskActivity.setPreTaskId(preTaskId);
        taskActivity.setPreTaskInstUuid(preTaskInstUuid);
        taskActivity.setFlowInstUuid(flowInstance.getUuid());
        taskActivity.setStartTime(Calendar.getInstance().getTime());
        taskActivity.setTransferCode(TransferCode.Submit.getCode());
        taskActivityService.save(taskActivity);
    }

    /**
     * @param flowInstUuid
     * @param autoSubmitRuleElement
     * @return
     */
    private Map<String, List<String>> getUserSupplementMapByTask(String flowInstUuid, AutoSubmitRuleElement autoSubmitRuleElement,
                                                                 TaskActivityStack taskActivityStack) {
        Map<String, String> userSupplementMap = getUserSupplementMapByUser(flowInstUuid, autoSubmitRuleElement, taskActivityStack);
        FlowInstanceParameter flowInstanceParameter = getSameUserAutoSubmitParameter(flowInstUuid);
        SameUserAutoSubmit sameUserAutoSubmit = JsonUtils.json2Object(flowInstanceParameter.getValue(), SameUserAutoSubmit.class);

        Map<String, List<String>> taskUsers = Maps.newHashMap();
        List<String> skipTaskIds = sameUserAutoSubmit.getSkipTaskIds();
        Collection<String> taskIds = userSupplementMap.values();
        for (String skipTaskId : skipTaskIds) {
            if (taskIds.contains(skipTaskId)) {
                List<String> userIds = Lists.newArrayList();
                for (Map.Entry<String, String> entry : userSupplementMap.entrySet()) {
                    if (StringUtils.equals(skipTaskId, entry.getValue())) {
                        userIds.add(entry.getKey());
                    }
                }
                taskUsers.put(skipTaskId, userIds);
                break;
            }
        }
        return taskUsers;
    }

    /**
     * @param flowInstUuid
     * @param autoSubmitRuleElement
     * @return
     */
    private Map<String, String> getUserSupplementMapByUser(String flowInstUuid, AutoSubmitRuleElement autoSubmitRuleElement,
                                                           TaskActivityStack taskActivityStack) {
        Map<String, String> userSupplementMap = Maps.newLinkedHashMap();
        FlowInstanceParameter flowInstanceParameter = getSameUserAutoSubmitParameter(flowInstUuid);
        List<TaskOperationItem> operationItems = taskActivityStack.getTaskOperationItems();
        Set<String> operatorIds = operationItems.stream().map(TaskOperationItem::getOperator).collect(Collectors.toSet());
        Set<String> operateTaskIds = operationItems.stream().map(TaskOperationItem::getTaskId).collect(Collectors.toSet());
        SameUserAutoSubmit sameUserAutoSubmit = JsonUtils.json2Object(flowInstanceParameter.getValue(), SameUserAutoSubmit.class);
        // 补审补办规则，1存在未审批过的人员时补审补办、2存在未审批人员，且有环节被跳过时补审补办
        String supplementRule = autoSubmitRuleElement.getSupplementRule();
        Map<String, String> undoMap = Maps.newLinkedHashMap();
        undoMap.putAll(sameUserAutoSubmit.getUserSubmitMap());
        undoMap.putAll(sameUserAutoSubmit.getUserSkipMap());
        if (StringUtils.equals(supplementRule, "1")) {
            undoMap.keySet().forEach(userId -> {
                if (!operatorIds.contains(userId)) {
                    userSupplementMap.put(userId, undoMap.get(userId));
                }
            });
        } else {
            undoMap.keySet().forEach(userId -> {
                if (!operatorIds.contains(userId) && !operateTaskIds.contains(undoMap.get(userId))) {
                    userSupplementMap.put(userId, undoMap.get(userId));
                }
            });
        }
        return userSupplementMap;
    }

    /**
     * @param token
     * @return
     */
    @Override
    public boolean isSupplementToCompleted(Token token) {
        PropertyElement propertyElement = token.getFlowDelegate().getFlow().getProperty();
        AutoSubmitRuleElement autoSubmitRuleElement = propertyElement.getAutoSubmitRule();
        if (!propertyElement.isEnabledAutoSubmit() || autoSubmitRuleElement == null
                || !StringUtils.equals("after", autoSubmitRuleElement.getMode())) {
            return false;
        }

        String sameUserSupplementInfo = (String) token.getTaskData().getCustomData(KEY_SUPPLEMENT_INFO);
        if (StringUtils.isBlank(sameUserSupplementInfo) && token.getFlowInstance() != null && StringUtils.isNotBlank(token.getFlowInstance().getUuid())) {
            FlowInstanceParameter flowInstanceParameter = flowInstanceParameterService.getByFlowInstUuidAndName(token.getFlowInstance().getUuid(), KEY_SUPPLEMENT_INFO);
            if (flowInstanceParameter != null) {
                sameUserSupplementInfo = flowInstanceParameter.getValue();
            }
        }
        if (StringUtils.isBlank(sameUserSupplementInfo)) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void completedSupplement(ExecutionContext executionContext, Node node) {
        PropertyElement propertyElement = executionContext.getToken().getFlowDelegate().getFlow().getProperty();
        AutoSubmitRuleElement autoSubmitRuleElement = propertyElement.getAutoSubmitRule();
        if (!propertyElement.isEnabledAutoSubmit() || autoSubmitRuleElement == null
                || !StringUtils.equals("after", autoSubmitRuleElement.getMode())) {
            return;
        }

        FlowInstance flowInstance = executionContext.getFlowInstance();
        // String supplementTaskInstUuid = executionContext.getToken().getTaskData().getPreTaskInstUuid(node.getId());
        FlowInstanceParameter parameter = getSupplementParameter(flowInstance.getUuid());
        if (parameter == null) {
            return;
        }

        // 存在多个补审补办任务时，有未完成时流程不结束
        List<String> unfinishedTaskUuids = taskService.getUnfinishedTaskInstanceUuids(flowInstance.getUuid());
        if (CollectionUtils.isNotEmpty(unfinishedTaskUuids) && CollectionUtils.size(unfinishedTaskUuids) > 0) {
            // 已办权限合并到未办结的任务上
            // taskService.copyPermissions(supplementTaskInstUuid, unfinishedTaskUuids.get(0), null, AclPermission.TODO);

            flowInstance.setIsActive(true);
            flowInstance.setEndTime(null);
            flowInstanceService.save(flowInstance);
        }
//        else {
//            // 合并补审补办的权限到开始补审环节
//            SameUserSupplementInfo supplementInfo = JsonUtils.json2Object(parameter.getValue(), SameUserSupplementInfo.class);
//            String forkTaskInstUuid = supplementInfo.getForkTaskInstUuid();
//            List<String> supplementTaskUuids = supplementInfo.getSupplementTaskUuids();
//            List<TaskActivityQueryItem> activityItems = taskActivityService.getAllActivityByTaskInstUuid(forkTaskInstUuid);
//            Collections.sort(activityItems, (o1, o2) -> -o1.getCreateTime().compareTo(o2.getCreateTime()));
//            for (String supplementTaskUuid : supplementTaskUuids) {
//                List<String> lastestSupplementTaskUuids = getLastestSupplementTaskUuids(activityItems, supplementTaskUuid);
//                for (String lastestSupplementTaskUuid : lastestSupplementTaskUuids) {
//                    taskService.copyPermissions(lastestSupplementTaskUuid, forkTaskInstUuid, null, AclPermission.TODO);
//                    aclTaskService.removePermission(lastestSupplementTaskUuid);
//                }
//            }
//        }
    }

    @Override
    @Transactional
    public void updateCanceledSupplementTaskName(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement) {
        String flowInstUuid = executionContext.getFlowInstance().getUuid();
        FlowInstanceParameter parameter = getSupplementParameter(flowInstUuid);
        if (parameter == null) {
            return;
        }

        String preTaskInstUuid = executionContext.getToken().getTaskData().getPreTaskInstUuid(node.getId());
        SameUserSupplementInfo supplementInfo = JsonUtils.json2Object(parameter.getValue(), SameUserSupplementInfo.class);
        Map<String, List<String>> supplementTaskUuidMap = supplementInfo.getSupplementTaskUuidMap();
        if (MapUtils.isEmpty(supplementTaskUuidMap)) {
            return;
        }

        TaskInstance currentTaskInstance = executionContext.getToken().getTask();
        if (currentTaskInstance == null) {
            return;
        }

        for (Map.Entry<String, List<String>> entry : supplementTaskUuidMap.entrySet()) {
            if (entry.getValue().contains(preTaskInstUuid)) {
                String cancelToTaskUuid = entry.getKey();
                TaskInstance taskInstance = taskInstanceService.get(cancelToTaskUuid);
                if (taskInstance != null && StringUtils.equals(taskInstance.getId(), currentTaskInstance.getId())
                        && !StringUtils.equals(taskInstance.getName(), currentTaskInstance.getName())) {
                    currentTaskInstance.setName(taskInstance.getName());
                    taskService.save(currentTaskInstance);
                    for (Map.Entry<String, List<String>> cancelEntry : supplementTaskUuidMap.entrySet()) {
                        if (cancelEntry.getValue().contains(cancelToTaskUuid)) {
                            supplementInfo.addSupplementTaskUuid(cancelEntry.getKey(), currentTaskInstance.getUuid());
                            saveSupplementParameter(supplementInfo, flowInstUuid);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @param activityItems
     * @param supplementTaskUuid
     * @return
     */
    private List<String> getLastestSupplementTaskUuids(List<TaskActivityQueryItem> activityItems, String supplementTaskUuid) {
        TaskActivityQueryItem activityQueryItem = null;
        List<TaskActivityQueryItem> items = Lists.newArrayList();
        for (TaskActivityQueryItem activityItem : activityItems) {
            items.add(activityItem);
            if (StringUtils.equals(activityItem.getTaskInstUuid(), supplementTaskUuid)) {
                activityQueryItem = activityItem;
                break;
            }
        }

        List<String> lastestSupplementTaskUuids = Lists.newArrayList();
        Map<String, List<TaskActivityQueryItem>> groupMap = ListUtils.list2group(items, "preTaskInstUuid");
        String preTaskInstUuid = supplementTaskUuid;
        List<TaskActivityQueryItem> preTaskActivityItems = groupMap.get(activityQueryItem.getTaskInstUuid());
        if (CollectionUtils.isEmpty(preTaskActivityItems) || CollectionUtils.size(preTaskActivityItems) <= 1) {
            lastestSupplementTaskUuids.add(preTaskInstUuid);
        } else {
            for (TaskActivityQueryItem preTaskActivityItem : preTaskActivityItems) {
                lastestSupplementTaskUuids.addAll(getLastestSupplementTaskUuids(activityItems, preTaskActivityItem.getTaskInstUuid()));
            }
        }
        return lastestSupplementTaskUuids;
    }

    public AutoSubmitRuleElement getAutoSubmitRuleElement() {
        AutoSubmitRuleElement autoSubmitRuleElement = new AutoSubmitRuleElement();
        autoSubmitRuleElement.setMode("before");
        autoSubmitRuleElement.setHandleMode("submit");
        autoSubmitRuleElement.setEffectiveTask("all");
        autoSubmitRuleElement.setEffectiveUser("all");
        autoSubmitRuleElement.setExitConditions(Lists.newArrayList("dataChanged"));
        autoSubmitRuleElement.setKeepRecord(true);
        autoSubmitRuleElement.setMatchTypes(Lists.newArrayList("start", "task", "collaboration", "branch"));
        autoSubmitRuleElement.setSupplementRule("1");
        autoSubmitRuleElement.setSupplementMode("task");
        autoSubmitRuleElement.setSupplementOperateRight("submit");
        autoSubmitRuleElement.setSupplementViewFormMode("readonly");
        return autoSubmitRuleElement;
    }

}

/*
 * @(#)2012-11-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberBuildParams;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.access.SidGranularityResolver;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.delegation.DefaultDelegationExecutor.FlowDelegationSettingsContextHolder;
import com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.*;
import com.wellsoft.pt.bpm.engine.exception.TaskNotAssignedDecisionMakerException;
import com.wellsoft.pt.bpm.engine.exception.TaskNotAssignedUserException;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.CollaborationTask;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.bpm.engine.util.ReservedFieldUtils;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import com.wellsoft.pt.workflow.enums.WorkFlowFieldMapping;
import com.wellsoft.pt.workflow.event.WorkCopyToEvent;
import com.wellsoft.pt.workflow.event.WorkDoneEvent;
import com.wellsoft.pt.workflow.event.WorkTodoEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
 * 2012-11-23.1	zhulh		2012-11-23		Create
 * </pre>
 * @date 2012-11-23
 */
@Service
@Transactional
public class TaskRepositoryImpl implements TaskRepository {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FlowService flowService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskBranchService taskBranchService;

    @Autowired
    private AclTaskService aclTaskService;

    @Autowired
    private ReadMarkerService readMarkerService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private TaskOperationService taskOperationService;

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private DelegationExecutor delegationExecutor;

    @Autowired
    private FlowDelegationSettingsService flowDelegationSettingsService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private SidGranularityResolver sidGranularityResolver;

    @Autowired
    private UserInfoFacadeService userInfoFacadeService;

    //    @Autowired
//    private WfTaskInstanceTodoUserService wfTaskInstanceTodoUserService;
    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    /**
     * (non-Javadoc)
     */
    @Override
    public void storeEnter(Node node, ExecutionContext executionContext) {
        // 流程到达，标记为未读
        // readMarkerService.markNew(executionContext.getFlowInstance());
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void store(Node node, ExecutionContext executionContext) {
        final String taskId = node.getId();
        Token token = executionContext.getToken();
        FlowInstance flowInstance = token.getFlowInstance();
        TaskData taskData = token.getTaskData();
        String currentUserId = taskData.getUserId();
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        TaskInstance parentTask = token.getParentTask();
        List<MessageTemplate> todoMessageTemplates = executionContext.getToken().getFlowDelegate()
                .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_TODO.getType());
        List<MessageTemplate> todoCopyMessageTemplates = executionContext.getToken().getFlowDelegate()
                .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_COPY.getType());

        // 增加参与者的待办权限
        Set<FlowUserSid> ids = taskData.getTaskUserSids(taskId);
        Set<FlowUserSid> todoUserSids = new LinkedHashSet<FlowUserSid>();
        Set<String> userIds = new LinkedHashSet<String>();
        if (!StringUtils.startsWith(currentUserId, IdPrefix.USER.getValue())
                && userInfoFacadeService.isNotStaffUser(currentUserId)) {
            Iterator<FlowUserSid> flowUserSidIterator = ids.iterator();
            while (flowUserSidIterator.hasNext()) {
                FlowUserSid fus = flowUserSidIterator.next();
                if (currentUserId.equalsIgnoreCase(fus.getId())) {
                    todoUserSids.add(fus);
                    userIds.add(fus.getId());
                    flowUserSidIterator.remove();
                }
            }
        }

        todoUserSids.addAll(ids);
        // todoUserSids.addAll(sidGranularityResolver.resolveWithSid(node, token, ids));
        userIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(todoUserSids));
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            for (FlowUserSid sid : todoUserSids) {
                sid.setName(IdentityResolverStrategy.resolveAsName(sid.getId()));
            }
        }
        // 办理人为空，且没有设置"办理人为空自动进入下一个环节"或环节是协作节点，则进行用户选择
        if (!isIncludeUser(todoUserSids, token) && (!taskData.isEmptyToTask(taskId) || node instanceof CollaborationTask)) {
            Map<String, Object> variables = new HashMap<String, Object>();
            String name = token.getFlowDelegate().getFlow().getName();
            variables.put("title", "(" + name + ":" + node.getName() + ")");
            variables.put("taskName", node.getName());
            variables.put("taskId", taskId);
            variables.put("submitButtonId", taskData.getSubmitButtonId());
            throw new TaskNotAssignedUserException(variables, token);
        }

        // 决策人员
        Set<FlowUserSid> decisionSids = null;
        if (node instanceof CollaborationTask) {
            decisionSids = Sets.newLinkedHashSet(sidGranularityResolver.resolveWithSid(node, token, taskData.getTaskDecisionMakerSids(taskId)));
            if (CollectionUtils.isEmpty(decisionSids)) {
                Map<String, Object> variables = new HashMap<String, Object>();
                String name = token.getFlowDelegate().getFlow().getName();
                variables.put("title", "(" + name + ":" + node.getName() + ")");
                variables.put("taskName", node.getName());
                variables.put("taskId", taskId);
                variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
                throw new TaskNotAssignedDecisionMakerException(variables, token);
            }
        }

        // 按人员顺序依次办理
        boolean isAnyone = taskData.isAnyone(taskId);
        boolean isByOrder = taskData.isByOrder(taskId);
        if (node instanceof CollaborationTask) {
            if (isByOrder) {
                throw new WorkFlowException("协作环节不能按顺序办理！");
            }
            if (CollectionUtils.size(todoUserSids) <= 1) {
                throw new WorkFlowException("协作环节需多人办理！");
            }
        }

        // 保存任务
        TaskInstance taskInstance = createTask(flowDefinition, flowInstance, parentTask, node, token, todoUserSids);

        // 办理人为空变更权限
        if (todoUserSids.isEmpty() && taskData.isEmptyToTask(taskId)) {
            String entityUuid = taskData.getPreTaskInstUuid(taskId);
            String newEntityUuid = taskInstance.getUuid();
            aclTaskService.changeAcl(entityUuid, newEntityUuid);
        }

        // <办理人，办理人对应的委托信息>
        Map<String, TaskDelegation> taskDelegationMap = new HashMap<String, TaskDelegation>();
        boolean hasTaskDelegation = false;
        int index = 0;
        FlowDelegationSettingsContextHolder.remove();
        for (FlowUserSid userSid : todoUserSids) {
            String userId = userSid.getId();
            // 设置职务代理人，如果当前用户等于待办人员，则忽略
            if (StringUtils.equals(userId, currentUserId)) {
                continue;
            }
            TaskDelegation taskDelegation = delegationExecutor.checkedAndPrepareDelegation(userSid, userIds,
                    taskInstance, flowInstance, executionContext);
            if (taskDelegation == null) {
                continue;
            }
            taskDelegationMap.put(userId, taskDelegation);
        }
        FlowDelegationSettingsContextHolder.remove();

        index = 0;
        Map<String, TaskDelegation> userTaskDelegationMap = Maps.newLinkedHashMap();
        List<TaskIdentity> toSavedTaskIdentities = Lists.newArrayList();
        for (FlowUserSid todoUserSid : todoUserSids) {
            String userId = todoUserSid.getId();
            // 添加流程的待办人员
            TaskIdentity taskIdentity = addTodoUser(taskData, flowInstance, taskInstance, index, isAnyone, isByOrder,
                    todoUserSid, todoMessageTemplates);
            TaskDelegation taskDelegation = taskDelegationMap.get(userId);
            // 工作委托处理，按顺序办理是只需处理第一个委托
            if (taskDelegation != null && (!isByOrder || (isByOrder && index == 0))) {
                if (!isByOrder) {
                    identityService.addTodo(taskIdentity);
                }
                flowInstanceService.flushSession();
                delegationExecutor.delegationWork(taskInstance, taskIdentity, taskDelegation);
                hasTaskDelegation = true;
            } else {
                if (!isByOrder || StringUtils.isBlank(taskIdentity.getUuid())) {
                    toSavedTaskIdentities.add(taskIdentity);
                }
            }
            userTaskDelegationMap.put(userId, taskDelegation);
            index++;
        }

        // 批量保存不是按人员顺序依次办理的待办权限
        if (!isByOrder && CollectionUtils.isNotEmpty(toSavedTaskIdentities)) {
            Set<String> todoUserIds = toSavedTaskIdentities.stream().map(identity -> identity.getUserId()).collect(Collectors.toSet());
            taskService.addTodoPermission(todoUserIds, taskInstance.getUuid());
            identityService.saveAll(toSavedTaskIdentities);
        }

        // 委托用户粒度大于人员的待办
        List<FlowUserSid> userInSids = todoUserSids.stream().filter(sid -> !IdPrefix.startsUser(sid.getId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(userInSids)) {
            boolean hasDelegationOfUserInSid = delegationExecutor.delegationWorkOfUserInSid(userInSids, taskInstance, flowInstance, userTaskDelegationMap, executionContext);
            if (hasDelegationOfUserInSid) {
                hasTaskDelegation = hasDelegationOfUserInSid;
            }
        }

        // 决策人员有待办权限
        if (CollectionUtils.isNotEmpty(decisionSids)) {
            taskService.addTodoPermission(decisionSids.stream().map(sid -> sid.getId()).collect(Collectors.toSet()), taskInstance.getUuid());
            decisionSids.forEach(sid -> {
                if (userTaskDelegationMap.containsKey(sid.getId())) {
                    return;
                }
                userTaskDelegationMap.put(sid.getId(), null);
            });
        }
        // 发送工作到达通知
        sendTodoMessage(isByOrder, userTaskDelegationMap, taskInstance, flowInstance, taskData, todoMessageTemplates);

        // 非顺序办理的存在委托处理，从权限更新办理人
        if (!isByOrder && hasTaskDelegation) {
            identityService.updateTaskIdentityFromPermission(taskInstance, taskData);
        }

        // add by wujx 2016-01-26 begin
        // 工作到达消息通知（当承办人为空时，也要触发流程定义中，符合条件的消息分发）
        if (todoUserSids.isEmpty() && !taskData.getStartNewFlow(flowInstance.getUuid())) {
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TODO, todoMessageTemplates, taskInstance,
                    flowInstance, StringUtils.EMPTY, ParticipantType.TodoUser);
        }
        // add by wujx 2016-01-26 end

        // 办理人为空转办时消息通知已办人员
        if (todoUserSids.isEmpty() && taskData.getEmptyNoteDone(taskId)) {
            List<MessageTemplate> emptyMessageTemplates = executionContext.getToken().getFlowDelegate()
                    .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_EMPTY_NOTE_DONE.getType());
            // 办理人为空的消息
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_EMPTY_NOTE_DONE, emptyMessageTemplates,
                    taskInstance, flowInstance, taskData.getUserId(), ParticipantType.TodoUser);
        }

        // 决策人员
        List<String> superviseUserIds = null;
        if (node instanceof CollaborationTask && CollectionUtils.isNotEmpty(decisionSids)) {
            // 保存流程参数
            addTaskDecisionMakerParameters(decisionSids, node, taskInstance, flowInstance);
            superviseUserIds = taskService.getSuperviseUserIds(taskInstance.getUuid(), false);
            // 决策人员有督办权限
            Set<String> toAddSuperviseIds = Sets.newLinkedHashSet(decisionSids.stream().map(sid -> sid.getId()).collect(Collectors.toSet()));
            toAddSuperviseIds.removeAll(superviseUserIds);
            taskService.addSupervisePermission(toAddSuperviseIds, taskInstance.getUuid());
        }

        // 增加抄送者的待阅权限
        Set<String> copyUserIds = taskData.getTaskCopyUsers(taskId);
        if (CollectionUtils.isNotEmpty(copyUserIds)) {
            taskService.addUnreadPermission(copyUserIds, taskInstance.getUuid());
            // 抄送消息
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_COPY, todoCopyMessageTemplates,
                    taskInstance, flowInstance, copyUserIds, ParticipantType.CopyUser);
            // 记录抄送日志
            //			taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.COPY_TO),
            //					ActionCode.COPY_TO.getCode(), WorkFlowOperation.COPY_TO, StringUtils.EMPTY, StringUtils.EMPTY,
            //					StringUtils.EMPTY, currentUserId, null, copyUserIds, null, null, taskInstance,
            //					taskInstance.getFlowInstance(), taskData);
            ApplicationContextHolder.publishEvent(new WorkCopyToEvent(this, Sets.newHashSet(copyUserIds), flowInstance, taskInstance));
        }
        // 对环节实例增加基于群组督办者的督办权限
        // String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
        // if (StringUtils.isNotBlank(preTaskInstUuid)) {
        // String supervisorSid = "GROUP_SUPERVISOR_" + preTaskInstUuid;
        // aclService.removeAllMember(supervisorSid,
        // ModuleID.WORKFLOW.getValue());
        // }
        // 添加督办权限
        Set<String> monitorIds = taskData.getTaskMonitors(taskId);
        if (CollectionUtils.isNotEmpty(monitorIds)) {
            if (superviseUserIds == null) {
                superviseUserIds = taskService.getSuperviseUserIds(taskInstance.getUuid(), false);
            }
            Set<String> toAddSuperviseIds = Sets.newLinkedHashSet(monitorIds);
            toAddSuperviseIds.removeAll(superviseUserIds);
            if (CollectionUtils.isNotEmpty(toAddSuperviseIds)) {
                taskService.addSupervisePermission(toAddSuperviseIds, taskInstance.getUuid());
            }
//            for (String monitorId : monitorIds) {
//                if (!superviseUserIds.contains(monitorId)) {
//                    taskService.addSupervisePermission(monitorId, taskInstance.getUuid());
//                }
//            }
        }

        // 2、督办工作到达通知
        Set<String> msgMonitorIds = Sets.newLinkedHashSet();
        List<MessageTemplate> superviseMessageTemplates = executionContext.getToken().getFlowDelegate()
                .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_SUPERVISE.getType());
        if (CollectionUtils.isNotEmpty(superviseMessageTemplates)) {
            if (CollectionUtils.isNotEmpty(monitorIds)) {
                msgMonitorIds.addAll(monitorIds);
            }
            if (superviseUserIds == null) {
                superviseUserIds = taskService.getSuperviseUserIds(taskInstance.getUuid(), false);
            }
            msgMonitorIds.addAll(superviseUserIds);
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_SUPERVISE, superviseMessageTemplates,
                    taskInstance, flowInstance, msgMonitorIds, ParticipantType.SuperviseUser);
        }

        // 监控权限
        Set<String> adminIds = taskData.getTaskAdmins(taskId);
        if (CollectionUtils.isNotEmpty(adminIds)) {
            List<String> monitorUserIds = taskService.getMonitorUserIds(taskInstance.getUuid(), false);
            Set<String> toAddMonitorIds = Sets.newLinkedHashSet(adminIds);
            toAddMonitorIds.removeAll(monitorUserIds);
            if (CollectionUtils.isNotEmpty(toAddMonitorIds)) {
                taskService.addMonitorPermission(toAddMonitorIds, taskInstance.getUuid());
            }
//            for (String adminId : adminIds) {
//                if (!monitorUserIds.contains(adminId)) {
//                    taskService.addMonitorPermission(adminId, taskInstance.getUuid());
//                }
//            }
        }

        // 查阅权限
        Set<String> viewerIds = taskData.getTaskViewers(taskId);
        if (CollectionUtils.isNotEmpty(viewerIds)) {
            List<String> viewerUserIds = taskService.getViewerUserIds(taskInstance.getUuid(), false);
            Set<String> toAddViewerIds = Sets.newLinkedHashSet(viewerIds);
            toAddViewerIds.removeAll(viewerUserIds);
            if (CollectionUtils.isNotEmpty(toAddViewerIds)) {
                taskService.addViewerPermission(toAddViewerIds, taskInstance.getUuid());
            }
        }

        // 流程到达，标记为未读
        readMarkerService.markNew(taskInstance);
        // 取消置顶
        taskService.untopping(Lists.newArrayList(taskInstance.getUuid()));

        // 设置流程实例的当前环节
        // flowInstance.setCurrentTaskInstance(taskInstance);
        flowInstanceService.save(flowInstance);

        token.setTask(taskInstance);
        token.setParentTask(taskInstance.getParent());

        String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
        List<String> preGatewayIds = taskData.getPreGatewayIds(taskId);
        Integer transferCode = taskData.getTransferCode(preTaskInstUuid);
        transferCode = transferCode == null ? TransferCode.Submit.getCode() : transferCode;

        // 判断是否为办理人为空自动进入下一结点
        if (todoUserSids.isEmpty() && taskData.isEmptyToTask(taskId)) {
            String taskInstUuid = taskInstance.getUuid();

            // 存储最初的流转代码
            String originalKey = preTaskInstUuid + "_original_transferCode";
            String keepOriginalKey = taskInstUuid + "_original_transferCode";
            if (taskData.getTransferCode(originalKey) != null) {
                taskData.setTransferCode(keepOriginalKey, taskData.getTransferCode(originalKey));
            } else {
                taskData.setTransferCode(keepOriginalKey, transferCode);
            }
            // 用户身份
            String userJobIdentity = taskData.getUserJobIdentityId(currentUserId, preTaskInstUuid);
            if (StringUtils.isNotBlank(userJobIdentity)) {
                taskData.setUserJobIdentityId(currentUserId, taskInstUuid, userJobIdentity);
            }

            transferCode = TransferCode.SkipTask.getCode();
            // 保存忽略流程操作信息
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.SKIP_TASK),
                    ActionCode.SKIP_TASK.getCode(), WorkFlowOperation.SKIP_TASK, null, null, null, null, null, null,
                    null, null, taskInstance, flowInstance, taskData);

            // 转换代码
            taskData.setTransferCode(taskInstance.getUuid(), transferCode);
            // 办理意见
            String preKey = preTaskInstUuid + taskData.getUserId();
            String key = taskInstUuid + taskData.getUserId();
            taskData.setOpinionLabel(key, taskData.getOpinionLabel(preKey));
            taskData.setOpinionValue(key, taskData.getOpinionValue(preKey));
            taskData.setOpinionText(key, taskData.getOpinionText(preKey));
            taskData.setOpinionFiles(key, taskData.getOpinionFiles(preKey));
            taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.SKIP_TASK));
            taskData.setActionCode(key, ActionCode.SKIP_TASK.getCode());
            taskData.setActionType(key, WorkFlowOperation.SKIP_TASK);
        } else if (TransferCode.SkipTask.getCode().equals(transferCode)) {
            // 提取最初的流转代码
            String originalKey = preTaskInstUuid + "_original_transferCode";
            if (taskData.getTransferCode(originalKey) != null) {
                transferCode = taskData.getTransferCode(originalKey);
            }
        }

        // 保存任务活动数据
        TaskActivity taskActivity = new TaskActivity();
        taskActivity.setTaskId(taskId);
        taskActivity.setTaskInstUuid(taskInstance.getUuid());
        taskActivity.setPreTaskId(taskData.getPreTaskId(taskId));
        taskActivity.setPreTaskInstUuid(preTaskInstUuid);
        if (CollectionUtils.isNotEmpty(preGatewayIds)) {
            taskActivity.setPreGatewayIds(StringUtils.join(preGatewayIds, Separator.SEMICOLON.getValue()));
        }
        taskActivity.setFlowInstUuid(flowInstance.getUuid());
        taskActivity.setStartTime(Calendar.getInstance().getTime());
        taskActivity.setTransferCode(transferCode);
        taskActivityService.save(taskActivity);

        // 更新操作历史
        String preOperationUuid = taskData.getOperationUuid(preTaskInstUuid);
        if (StringUtils.isNotBlank(preOperationUuid)) {
            taskOperationService.setUsers(preOperationUuid, userIds, copyUserIds);
        } else {
            // 发起环节提交并抄送
            taskData.put("startTaskCopyUserIds_" + taskInstance.getUuid(), copyUserIds);
        }

        if (StringUtils.isNotEmpty(preTaskInstUuid)) {
            // 发送待办事件
            Set<String> msgUserIds = new HashSet<String>(0);
            for (FlowUserSid todoUserSid : todoUserSids) {
                // 工作委托处理
                if (taskDelegationMap.containsKey(todoUserSid.getId())) {
                    TaskDelegation taskDelegation = taskDelegationMap.get(todoUserSid.getId());
                    List<String> trustees = Arrays.asList(StringUtils.split(taskDelegation.getTrustee(),
                            Separator.SEMICOLON.getValue()));
                    msgUserIds.addAll(trustees);
                } else {
                    msgUserIds.add(todoUserSid.getId());
                }
            }
//            if (StringUtils.isNotEmpty(preTaskInstUuid) && node instanceof TaskNode) {
//                // 确认自动提交和自动提交
//                String sameUserSubmitStr = ((TaskNode) node).getSameUserSubmit();
//                if ((TransferCode.Submit.getCode().equals(transferCode)
//                        || TransferCode.TransferSubmit.getCode().equals(transferCode)
//                        || TransferCode.DelegationSubmit.getCode().equals(transferCode)) && ((SameUserSubmit.AUTO_SUBMIT.equals(sameUserSubmitStr)
//                        || SameUserSubmit.CONFIRM_SUBMIT.equals(sameUserSubmitStr)
//                        || SameUserSubmit.NO_ACTION_REFRESH.equals(sameUserSubmitStr)))) {
//                    msgUserIds.remove(currentUserId);
//                }
//            }
            ApplicationContextHolder.publishEvent(new WorkTodoEvent(this, msgUserIds, flowInstance, taskInstance, taskData));
        }
    }

    /**
     * @param todoUserSids
     * @return
     */
    private boolean isIncludeUser(Set<FlowUserSid> todoUserSids, Token token) {
        if (CollectionUtils.isEmpty(todoUserSids)) {
            return false;
        }
        List<String> sids = Lists.newArrayList();
        for (FlowUserSid sid : todoUserSids) {
            if (StringUtils.startsWith(sid.getId(), IdPrefix.USER.getValue())) {
                return true;
            }
            sids.add(sid.getId());
        }
        Map<String, String> userMap = workflowOrgService.getUsersByIds(sids, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
        return MapUtils.isNotEmpty(userMap);
    }

    /**
     * @param decisionSids
     * @param node
     * @param taskInstance
     * @param flowInstance
     */
    private void addTaskDecisionMakerParameters(Set<FlowUserSid> decisionSids, Node node, TaskInstance taskInstance, FlowInstance flowInstance) {
        Map<String, String> decisionIdMap = decisionSids.stream().collect(Collectors.toMap(sid -> sid.getId(), sid -> sid.getIdentityId()));
        // String decisionIds = decisionSids.stream().map(sid -> sid.getId()).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
        FlowInstanceParameter parameter = new FlowInstanceParameter();
        parameter.setFlowInstUuid(flowInstance.getUuid());
        parameter.setName("decisionMakers_" + node.getId() + "_" + taskInstance.getUuid());
        parameter.setValue(JsonUtils.object2Json(decisionIdMap));
        flowInstanceParameterService.save(parameter);
    }

    /**
     * @param node
     * @param taskInstance
     * @param flowInstance
     */
    private FlowInstanceParameter getTaskDecisionMakerParameters(Node node, TaskInstance taskInstance, FlowInstance flowInstance) {
        String parameterName = "decisionMakers_" + node.getId() + "_" + taskInstance.getUuid();
        return flowInstanceParameterService.getByFlowInstUuidAndName(flowInstance.getUuid(), parameterName);
    }

    /**
     * 添加流程的待办人员
     *
     * @param flowInstance
     * @param taskInstance
     * @param index
     * @param isAnyone
     * @param isByOrder
     * @param userSid
     */
    private TaskIdentity addTodoUser(TaskData taskData, FlowInstance flowInstance, TaskInstance taskInstance,
                                     int index, boolean isAnyone, boolean isByOrder, FlowUserSid userSid, List<MessageTemplate> messageTemplates) {
        String userId = userSid.getId();
        String toTaskId = taskInstance.getId();
        if (!isByOrder || (isByOrder && index == 0)) {
            String fromTaskId = taskData.getPreTaskId(toTaskId);
            String entityUuid = taskData.getPreTaskInstUuid(toTaskId);
            String newEntityUuid = taskInstance.getUuid();

            boolean taskForking = taskData.isTaskForking(fromTaskId);
            boolean taskJoining = taskData.isTaskJoining(toTaskId);
            // 正常单一流转
            if (!taskForking && !taskJoining) {
                if (index == 0) {
                    aclTaskService.changeAcl(entityUuid, newEntityUuid);
                }
                if (isByOrder) {
                    taskService.addTodoPermission(userId, newEntityUuid);
                }
            } else if (taskForking && !taskJoining) {
                taskService.copyPermissions(entityUuid, newEntityUuid, null, AclPermission.TODO);
                taskService.addTodoPermission(userId, newEntityUuid);
                taskService.addDonePermission(taskData.getUserId(), newEntityUuid);
            } else if (!taskForking && taskJoining) {
                // 合并流转
                aclTaskService.changeAcl(entityUuid, newEntityUuid);
                taskService.addTodoPermission(userId, newEntityUuid);
            } else {
                throw new WorkFlowException("无法识别的环节流转模型[多路分支->多路聚合]!");
            }
        }

        // 按人员顺序依次办理
        TaskIdentity identity = null;
        if (isByOrder) {
            identity = new TaskIdentity();
            identity.setTaskInstUuid(taskInstance.getUuid());
            identity.setTodoType(WorkFlowTodoType.Submit);
            identity.setUserId(userId);
            identity.setSortOrder(index);
            identity.setIdentityId(userSid.getIdentityId());
            identity.setIdentityIdPath(userSid.getIdentityIdPath());
            identityService.save(identity);
        } else {// 只需要其中一个人办理、其他
            identity = new TaskIdentity();
            identity.setTaskInstUuid(taskInstance.getUuid());
            identity.setTodoType(WorkFlowTodoType.Submit);
            identity.setUserId(userId);
            identity.setIdentityId(userSid.getIdentityId());
            identity.setIdentityIdPath(userSid.getIdentityIdPath());
            // identityService.save(identity);
        }

        return identity;
    }

    /**
     * 发送工作到达通知
     *
     * @param isByOrder
     * @param userTaskDelegationMap
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @param todoMessageTemplates
     */
    private void sendTodoMessage(boolean isByOrder, Map<String, TaskDelegation> userTaskDelegationMap, TaskInstance taskInstance, FlowInstance flowInstance,
                                 TaskData taskData, List<MessageTemplate> todoMessageTemplates) {
        if (CollectionUtils.isEmpty(todoMessageTemplates)) {
            return;
        }
        String preTaskId = taskData.getPreTaskId(taskInstance.getId());
        if (taskData.getStartNewFlow(flowInstance.getUuid()) || taskData.isRollback(preTaskId)
                || taskData.isCancel(preTaskId)) {
            return;
        }

        List<String> toSendUserIds = Lists.newArrayList();
        for (Map.Entry<String, TaskDelegation> entry : userTaskDelegationMap.entrySet()) {
            String userId = entry.getKey();
            TaskDelegation taskDelegation = entry.getValue();
            // 委托人可见待办，发送工作到达通知
            if (taskDelegation != null) {
                FlowDelegationSettings flowDelegationSettings = flowDelegationSettingsService.getOne(taskDelegation
                        .getDelegationSettingsUuid());
                if (flowDelegationSettings != null
                        && Boolean.TRUE.equals(flowDelegationSettings.getDelegationTaskVisible())) {
                    toSendUserIds.add(userId);
//                    // 1、工作到达通知
//                    MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TODO, todoMessageTemplates,
//                            taskInstance, flowInstance, userId, ParticipantType.TodoUser);
                }
            } else {
                toSendUserIds.add(userId);
//                // 1、工作到达通知
//                MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TODO, todoMessageTemplates, taskInstance,
//                        flowInstance, userId, ParticipantType.TodoUser);
            }

            // 按顺序办理只发给第一个人
            if (isByOrder) {
                break;
            }
        }

        // 1、工作到达通知
        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TODO, todoMessageTemplates, taskInstance,
                flowInstance, toSendUserIds, ParticipantType.TodoUser);
    }

    /**
     * 保存任务
     *
     * @param node
     * @param flowInstance
     * @param flowDefinition
     * @param parentTask
     * @return
     */
    private TaskInstance createTask(FlowDefinition flowDefinition, FlowInstance flowInstance, TaskInstance parentTask,
                                    Node node, Token token, Set<FlowUserSid> todoUserSids) {
        TaskData taskData = token.getTaskData();
        String taskId = node.getId();
        // 创建任务
        TaskInstance task = new TaskInstance();
        task.setName(node.getName());
        task.setId(taskId);
        task.setType(Integer.valueOf(node.getType()));
        task.setFlowDefinition(flowDefinition);
        task.setFlowInstance(flowInstance);

        // 操作动作及操作动作类型
        String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
        String userId = taskData.getUserId();
        String key = StringUtils.isBlank(preTaskInstUuid) ? userId : preTaskInstUuid + userId;
        String action = taskData.getAction(key);
        String actionType = taskData.getActionType(key);
        task.setAction(action);
        task.setActionType(actionType);

        // 并联信息
        Boolean isParallel = (Boolean) taskData.getPreTaskProperties(taskId, FlowConstant.BRANCH.IS_PARALLEL);
        String parallelTaskInstUuid = (String) taskData.getPreTaskProperties(taskId,
                FlowConstant.BRANCH.PARALLEL_TASK_INST_UUID);
        String fromTaskId = taskData.getPreTaskId(taskId);
        String toTaskId = taskId;
        boolean taskForking = taskData.isTaskForking(fromTaskId);
        boolean taskJoining = taskData.isTaskJoining(toTaskId);
        // 正常单一流转
        if (!taskForking && !taskJoining) {
        } else if (taskForking && !taskJoining) {
            // 分支流转
            isParallel = true;
            parallelTaskInstUuid = preTaskInstUuid;
        } else if (!taskForking && taskJoining) {
            // 保存子流程完成的批次数据信息
            saveCompletedBatchDataUuids(flowInstance, parallelTaskInstUuid);
            // 合并流转
            isParallel = false;
            parallelTaskInstUuid = null;
        } else {
            throw new WorkFlowException("无法识别的环节流转模型[多路分支->多路聚合]!");
        }
        task.setIsParallel((isParallel == null) ? false : isParallel);
        task.setParallelTaskInstUuid(parallelTaskInstUuid);

        // 计时信息
        task.setTimingState(0);
        task.setAlarmState(0);
        task.setOverDueState(0);

        String formUuid = taskData.getFormUuid();
        String dataUuid = taskData.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        List<String> mappingFieldNames = new ArrayList<String>();
        List<Object> mappingFieldValues = new ArrayList<Object>();


        // 设置前办理人
        task.setAssignee(taskData.getUserId());
        task.setAssigneeName(taskData.getUserName());
        List<String> todoUserIds = new ArrayList<String>();
        List<String> todoUserNames = new ArrayList<String>();
        boolean isByOrder = taskData.isByOrder(taskId);
        for (FlowUserSid todoUserSid : todoUserSids) {
            todoUserIds.add(todoUserSid.getId());
            todoUserNames.add(todoUserSid.getName());
            // 按顺序办理，只显示第一个办理人信息
            if (isByOrder) {
                break;
            }
        }

        // 设置待办人员 待办人员长度判断（长度不能超过300个，不然数据库字段存储长度不够） 多个是用;隔开
        String todoUserIdString = StringUtils.join(todoUserIds, Separator.SEMICOLON.getValue());
        String todoUserNameString = StringUtils.join(todoUserNames, Separator.SEMICOLON.getValue());
        if (StringUtils.isNotBlank(todoUserIdString) && todoUserIdString.split(";").length > 300) {
            throw new BusinessException("下一环节的办理人数超出可处理范围，请联系管理员！");
        }
        task.setTodoUserId(todoUserIdString);
        task.setTodoUserName(todoUserNameString);
        // 设置任务所有者
        task.setOwner(todoUserIdString);
        task.setStartTime(Calendar.getInstance().getTime());

        // 获取动态表单UUID
        // 如果环节中没有设置表单或环节中设置的表单与流程中设置的表单一致则使用相同的数据，否则复制字段相同的数据
        // 复制对象属性
        String taskFormUuid = node.getFormID();
        if (dyFormData == null || "-1".equals(taskFormUuid) || StringUtils.isBlank(taskFormUuid)
                || StringUtils.equals(taskFormUuid, formUuid)
                || StringUtils.equals(flowInstance.getFormUuid(), formUuid)) {
            task.setFormUuid(flowInstance.getFormUuid());
            task.setDataUuid(dataUuid);
        } else {
            DyFormFormDefinition flowDyFormFormDefinition = dyFormFacade.getFormDefinition(formUuid);
            DyFormFormDefinition taskDyFormFormDefinition = dyFormFacade.getFormDefinition(taskFormUuid);
            String flowPformUuid = flowDyFormFormDefinition.getpFormUuid();
            String taskPformUuid = taskDyFormFormDefinition.getpFormUuid();
            if (StringUtils.isNotBlank(flowPformUuid)) {
                flowDyFormFormDefinition = dyFormFacade.getFormDefinition(flowPformUuid);
            }
            if (StringUtils.isNotBlank(taskPformUuid)) {
                taskDyFormFormDefinition = dyFormFacade.getFormDefinition(taskPformUuid);
            }
            if (StringUtils.equals(flowDyFormFormDefinition.getTableName(), taskDyFormFormDefinition.getTableName())) {
                task.setFormUuid(flowInstance.getFormUuid());
                task.setDataUuid(dataUuid);
            } else {
                task.setFormUuid(taskFormUuid);
                String newDataUuid = dyFormFacade.copyFormData(dyFormData, node.getFormID());
                task.setDataUuid(newDataUuid);
            }
        }

        // 生成流水号
        try {
            String serialNo = taskData.getTaskSerialNo();
            if (StringUtils.isBlank(serialNo) && taskData.isGenerateSerialNumber()) {
                String serialNoDefId = token.getFlowDelegate().getTaskSerialNoDefId(taskId);
                if (StringUtils.isNotBlank(serialNoDefId)) {
                    if (basicDataApiFacade.getSerialNumberById(serialNoDefId) != null) {
                        List<IdEntity> entities = new ArrayList<IdEntity>();
                        TaskInstance taskInstance = new TaskInstance();
                        BeanUtils.copyProperties(task, taskInstance);
                        FlowInstance flowInstance2 = new FlowInstance();
                        BeanUtils.copyProperties(flowInstance, flowInstance2);
                        entities.add(taskInstance);
                        entities.add(flowInstance2);
                        // add by huanglinchuan 2014.10.17 begin
                        entities.add(flowDefinition);
                        // add by huanglinchuan 2014.10.17 end
                        serialNo = basicDataApiFacade.getSerialNumber(serialNoDefId, entities, true,
                                WorkFlowVariables.SERIAL_NO.getName());
                    } else {
                        List<String> snFieldNames = dyFormData.getFieldNamesByMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue());
                        SerialNumberBuildParams params = new SerialNumberBuildParams();
                        params.setSerialNumberId(serialNoDefId);
                        params.setIsBackEnd(true);
                        params.setOccupied(true);
                        if (CollectionUtils.isNotEmpty(snFieldNames)) {
                            params.setFormField(snFieldNames.get(0));
                        } else {
                            throw new BusinessException(String.format("环节[%s]配置生成流水号，但没有应用于表单字段！", node.getName()));
                        }
                        Map<String, Object> renderParams = new HashMap<String, Object>();
                        renderParams.put("流程名称", flowDefinition.getName());
                        renderParams.put("流程ID", flowDefinition.getId());
                        renderParams.put("流程编号", flowDefinition.getCode());
                        Map<String, Object> flowDefinitionMap = new HashMap<String, Object>();
                        flowDefinitionMap.put("name", flowDefinition.getName());
                        flowDefinitionMap.put("id", flowDefinition.getId());
                        flowDefinitionMap.put("code", flowDefinition.getCode());
                        renderParams.put("flowDefinition", flowDefinitionMap);
                        params.setRenderParams(renderParams);
                        params.setFormUuid(dyFormData.getFormUuid());
                        params.setDataUuid(dyFormData.getDataUuid());
                        renderParams.put("dyFormData", dyFormData);
                        serialNo = basicDataApiFacade.generateSerialNumber(params);
                    }

                    task.setSerialNo(serialNo);
                    // 添加同步表单流程号映射字段、值
                    mappingFieldNames.add(WorkFlowFieldMapping.SERIAL_NO.getValue());
                    mappingFieldValues.add(serialNo);
                }
            } else {
                task.setSerialNo(serialNo);
            }
        } catch (JsonDataException e) {
            throw e;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        // 任务父节点
        task.setParent(parentTask);
        // 设置预留字段
        ReservedFieldUtils.setReservedFields(flowInstance, taskData);
        this.flowInstanceService.save(flowInstance);

        // 保存任务
        this.taskInstanceService.save(task);
        this.taskInstanceService.flushSession();

//        Map<String, List<String>> userJobPaths = (Map<String, List<String>>) taskData.get("taskUserJobPaths");
//        if (userJobPaths != null) {
//            List<String> userJobPathList = userJobPaths.get(taskId);
//            if (userJobPathList != null && userJobPathList.size() > 0 && userJobPathList.size() == todoUserIds.size()) {
//                //保存环节实例_任务待办人员扩展
//                for (int i = 0; i < userJobPathList.size(); i++) {
//                    WfTaskInstanceTodoUserEntity entity = new WfTaskInstanceTodoUserEntity();
//                    entity.setTaskInstUuid(task.getUuid());
//                    entity.setTodoUserId(todoUserIds.get(i));
//                    entity.setTodoUserName(todoUserNames.get(i));
//                    entity.setTodoUserJobPath(userJobPathList.get(i));
//                    entity.setTaskId(taskId);
//                    entity.setFlowInstUuid(flowInstance.getUuid());
//                    wfTaskInstanceTodoUserService.save(entity);
//                }
//            }
//        }

        // 正常单一流转
        if (!taskForking && !taskJoining) {
            if (Boolean.TRUE.equals(task.getIsParallel())) {
                taskBranchService.changeCurrentTask(task, flowInstance, taskData);
            }
        } else if (taskForking && !taskJoining) {
            // 分支流转
            taskBranchService.createBranchTask(task, flowInstance, taskData);
        } else if (!taskForking && taskJoining) {
            // 完成前一分支
            TaskInstance preTaskInstance = taskService.getTask(preTaskInstUuid);
            if (StringUtils.isNotBlank(preTaskInstance.getParallelTaskInstUuid())) {
                taskBranchService.completeBranchTask(preTaskInstance);
            }
            // 合并流转
            parallelTaskInstUuid = (String) taskData.getPreTaskProperties(taskId,
                    FlowConstant.BRANCH.PARALLEL_TASK_INST_UUID);
            if (StringUtils.isNotBlank(parallelTaskInstUuid)) {
                taskBranchService.joinBranchTask(task, flowInstance, taskData);
            }
        }
        // 相关联的分支UUID
        String relatedTaskBranchUuid = taskData.getRelatedTaskBranchUuid(toTaskId);
        if (StringUtils.isNotBlank(relatedTaskBranchUuid)) {
            taskBranchService.syncBranchTask(task, relatedTaskBranchUuid);
        }

        // 同步表单流程号映射字段值
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_ID.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_NAME.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK_ID.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.TASK_INST_UUID.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.FLOW_INST_UUID.getValue());
        mappingFieldValues.add(todoUserIdString);
        mappingFieldValues.add(todoUserNameString);
        mappingFieldValues.add(task.getName());
        mappingFieldValues.add(task.getId());
        mappingFieldValues.add(task.getUuid());
        mappingFieldValues.add(flowInstance.getUuid());
        boolean startNewFlow = taskData.getStartNewFlow(flowInstance.getUuid());
        if (startNewFlow) {
            mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_FLOW_STATE_CODE.getValue());
            mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_FLOW_STATE_NAME.getValue());
            mappingFieldValues.add(WorkFlowState.Todo);
            mappingFieldValues.add(token.getFlowDelegate().getFlowStateName(WorkFlowState.Todo));
        }
        syncTaskFieldMapping(mappingFieldNames, mappingFieldValues, dyFormData, formUuid, dataUuid, taskData);

        // 添加环节实例UUID的提交结果
        if (taskForking) {
            taskData.getSubmitResult().getTaskInstUUids().remove(preTaskInstUuid);
            taskData.getSubmitResult().getTaskInstUUids().add(task.getUuid());
        }

        // 添加环节办理人的提交结果
        if (!startNewFlow) {
            taskData.getSubmitResult().addNextTaskInfo(task, todoUserSids);
        }

        return task;
    }

    /**
     * @param flowInstance
     * @param parallelTaskInstUuid
     */
    private void saveCompletedBatchDataUuids(FlowInstance flowInstance, String parallelTaskInstUuid) {
        if (StringUtils.isBlank(parallelTaskInstUuid)) {
            return;
        }
        String flowInstUuid = flowInstance.getUuid();
        TaskInstance taskInstance = taskService.get(parallelTaskInstUuid);
        String taskInstUuid = taskInstance.getUuid();
        String name = FlowConstant.SUB_FLOW.KEY_BATCH_FORM_INFO + Separator.UNDERLINE.getValue() + taskInstUuid;
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        for (FlowInstanceParameter flowInstanceParameter : parameters) {
            String batchFormInfo = flowInstanceParameter.getValue();
            String[] batchFormInfos = StringUtils.split(batchFormInfo, Separator.SEMICOLON.getValue());
            for (String formInfo : batchFormInfos) {
                String[] formInfos = StringUtils.split(formInfo, Separator.COLON.getValue());
                if (formInfos.length == 2) {
                    String batchFormUuid = formInfos[0];
                    String batchDataUuid = formInfos[1];
                    saveCompletedBatchDataUuid(flowInstance.getUuid(), taskInstance.getId(), batchFormUuid,
                            batchDataUuid);
                }
            }
        }
    }

    /**
     * @param flowInstUuid
     * @param taskId
     * @param batchFormUuid
     * @param batchDataUuid
     */
    private void saveCompletedBatchDataUuid(String flowInstUuid, String taskId, String batchFormUuid,
                                            String batchDataUuid) {
        String subTaskId = taskId;
        String subFormId = dyFormFacade.getFormIdByFormUuid(batchFormUuid);
        String parentFlowInstUuid = flowInstUuid;
        String name = FlowConstant.SUB_FLOW.KEY_COMPLATED_BATCH + Separator.UNDERLINE.getValue() + subTaskId
                + Separator.UNDERLINE.getValue() + subFormId;
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(parentFlowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(batchDataUuid);
            flowService.saveFlowInstanceParameter(example);
        } else {
            for (FlowInstanceParameter flowInstanceParameter : parameters) {
                String completedBatchDataUuid = flowInstanceParameter.getValue();
                String[] completedBatchDataUuids = StringUtils.split(completedBatchDataUuid,
                        Separator.SEMICOLON.getValue());
                List<String> dataUuids = Lists.newArrayList();
                dataUuids.addAll(Arrays.asList(completedBatchDataUuids));
                dataUuids.add(batchDataUuid);
                flowInstanceParameter.setValue(StringUtils.join(dataUuids, Separator.SEMICOLON.getValue()));
                flowService.saveFlowInstanceParameter(flowInstanceParameter);
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param mappingNames
     * @param mappingValues
     * @param dyFormData
     * @param formUuid
     * @param dataUuid
     * @param taskData
     */
    private void syncTaskFieldMapping(List<String> mappingNames, List<Object> mappingValues, DyFormData dyFormData,
                                      String formUuid, String dataUuid, TaskData taskData) {
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid, false);
        }
        // 同步表单流程号映射字段值
        boolean hasUpdateMappingField = false;
        for (int index = 0; index < mappingNames.size(); index++) {
            String mappingName = mappingNames.get(index);
            Object mappingValue = mappingValues.get(index);
            if (dyFormData != null && dyFormData.hasFieldMappingName(mappingName)) {
                dyFormData.setFieldValueByMappingName(mappingName, mappingValue);
                hasUpdateMappingField = true;
            }
        }
        if (hasUpdateMappingField) {
            // dyFormData.doForceCover();
            // String updatedDataUuid = dyFormFacade.saveFormData(dyFormData);
            taskData.setDataUuid(dataUuid);
            taskData.setDyFormData(dataUuid, dyFormData);
            // taskData.setDyFormData(dataUuid, dyFormFacade.getDyFormData(formUuid, updatedDataUuid, false));
            taskData.addUpdatedDyFormData(dataUuid, dyFormData);
        }
    }

    /**
     * @param node
     * @param executionContext
     * @return
     */
    public boolean complete(Node node, ExecutionContext executionContext) {
        // 任务结束
        TaskInstance taskInstance = executionContext.getToken().getTask();
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        TaskData taskData = executionContext.getToken().getTaskData();
        String currentUserId = taskData.getUserId();
        final String taskInstUuid = taskInstance.getUuid();
        final String flowInstUuid = flowInstance.getUuid();
        String key = taskInstUuid + currentUserId;
        String taskIdentityUuid = taskData.getTaskIdentityUuid(key);
        TaskIdentity taskIdentity = null;
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            taskIdentity = identityService.get(taskIdentityUuid);
        }

        // 设置前办理人
        taskInstance.setAssignee(currentUserId);
        taskInstance.setAssigneeName(taskData.getUserName());
        this.taskInstanceService.merge(taskInstance);

        // 如果是退回、撤回、跳转操作，删除该任务的所有待办人员
        if (taskData.isRollback(node.getId()) || taskData.isCancel(node.getId()) || taskData.isGotoTask(node.getId())) {
            // 退回添加已办权限
            if (taskData.isRollback(node.getId())) {
                taskService.addDonePermission(currentUserId, taskInstUuid);
            }
            // 删除所有待办权限
            identityService.removeTodoByTaskInstUuid(taskInstUuid);
            return true;
        }
        // 保存任务完成时间
        boolean updateTaskIdentity = true;
        // 协作节点提交
        if (node instanceof CollaborationTask && !taskData.isRollback(node.getId())) {
            // 决策人员完成
            if (WorkFlowOperation.COMPLETE.equals(taskData.getActionType(key))) {
                identityService.removeTodoByTaskInstUuid(taskInstUuid);
                // aclService.removePermission(TaskInstance.class, taskInstUuid, AclPermission.TODO);
            } else {
                // 将其他已办的人变为待办
                List<AclTaskEntry> todoSids = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
                List<AclTaskEntry> doneSids = aclTaskService.getSid(taskInstUuid, AclPermission.DONE);
                doneSids.removeAll(todoSids);
                Set<String> ownerTaskIdentityUuids = Sets.newHashSet();
                List<TaskIdentity> allIdentities = identityService.getByTaskInstUuid(taskInstUuid);
                // List<TaskIdentity> todoIdentities = Lists.newArrayList();
                List<TaskIdentity> doneIdentities = Lists.newArrayList();
                allIdentities.forEach(identity -> {
                    if (StringUtils.isBlank(identity.getSourceTaskIdentityUuid())) {
                        ownerTaskIdentityUuids.add(identity.getUuid());
                    }
                    if (Integer.valueOf(SuspensionState.DELETED.getState()).equals(identity.getSuspensionState())) {
                        doneIdentities.add(identity);
                    }
                });
                Set<String> doneUserIdSet = Sets.newHashSet();
                if (taskIdentity != null && StringUtils.isNotBlank(taskIdentity.getSourceTaskIdentityUuid())
                        && (WorkFlowTodoType.Transfer.equals(taskIdentity.getTodoType()) ||
                        WorkFlowTodoType.Delegation.equals(taskIdentity.getTodoType()))) {
                    doneUserIdSet.add(taskIdentity.getCreator());
                }
                List<TaskIdentity> appendIdentities = doneIdentities.stream().filter(doneTaskIdentity -> {
                    String doneUserId = doneTaskIdentity.getUserId();
                    if (StringUtils.equals(doneUserId, currentUserId) || doneUserIdSet.contains(doneUserId)) {
                        return false;
                    } else if (WorkFlowTodoType.AddSign.equals(doneTaskIdentity.getTodoType())) {
                        if (!ownerTaskIdentityUuids.contains(doneTaskIdentity.getSourceTaskIdentityUuid())) {
                            return false;
                        }
                    } else if (StringUtils.isNotBlank(doneTaskIdentity.getSourceTaskIdentityUuid())) {
                        return false;
                    }
                    doneUserIdSet.add(doneUserId);
                    AclTaskEntry sid = doneSids.stream().filter(doneSid -> StringUtils.equals(doneSid.getSid(), doneUserId)).findFirst().orElse(null);
                    return sid != null;
                }).collect(Collectors.toList());
                appendIdentities.forEach(doneTaskIdentity -> {
                    TaskIdentity identity = new TaskIdentity();
                    identity.setTaskInstUuid(doneTaskIdentity.getTaskInstUuid());
                    identity.setTodoType(doneTaskIdentity.getTodoType());
                    identity.setUserId(doneTaskIdentity.getUserId());
                    identity.setIdentityId(doneTaskIdentity.getIdentityId());
                    identity.setIdentityIdPath(doneTaskIdentity.getIdentityIdPath());
                    identityService.addTodo(identity);
                });

                // 当前用户办理
                if (taskIdentity != null) {
                    // 删除待办权限
                    identityService.removeTodo(taskIdentity);
                } else {
                    // 删除待办权限
                    identityService.removeTodoByTaskInstUuidAndUserId(taskInstUuid, currentUserId);
                }
            }
        } else if (node instanceof TaskNode && ((TaskNode) node).isAnyone()) {
            // 1、只需要其中一个人办理
//            List<TaskIdentity> identities = identityService.getTodoByTaskInstUuid(taskInstUuid);
//            if (identities != null && !identities.isEmpty()) {
//                TaskIdentity[] ids = identities.toArray(new TaskIdentity[0]);
//                for (int index = 0; index < ids.length; index++) {
//                    TaskIdentity identity = ids[index];
//                    identities.remove(identity);
//                    identityService.removeTodo(identity);
//                }
//            }
//            // 删除待办权限
//            aclService.removePermission(TaskInstance.class, taskInstUuid, AclPermission.TODO);
            identityService.removeTodoByTaskInstUuid(taskInstUuid);
        } else if (node instanceof TaskNode && ((TaskNode) node).isByOrder() && !taskData.getStartNewFlow(flowInstUuid)) {
            // 2、按人员顺序依次办理
            List<TaskIdentity> identities = identityService.getByOrdersByTaskInstUuid(taskInstUuid);
            TaskIdentity[] ids = identities.toArray(new TaskIdentity[0]);

            if (ids.length > 0) {
                TaskIdentity identity = null;
                TaskIdentity nextIdentity = null;

                //当前处理用户为原顺序处理人员，取下标1为下一处理人；否则取下标0为下一处理人
                if (StringUtils.equals(ids[0].getUserId(), currentUserId)) {
                    identity = ids[0];
                    if (ids.length > 1) {
                        nextIdentity = ids[1];
                    }
                } else {
                    nextIdentity = ids[0];
                    // 转办、委托提交返回的按顺序办理处理
                    if (taskIdentity != null && StringUtils.equals(taskIdentity.getUuid(), nextIdentity.getUuid())) {
                        if (ids.length > 1) {
                            identity = ids[0];
                            nextIdentity = ids[1];
                        } else {
                            identity = ids[0];
                            nextIdentity = null;
                        }
                    }
                }

                if (identity != null) {
                    identities.remove(identity);
                    // 删除待办权限
                    identityService.removeTodo(identity);
                }

                if (nextIdentity != null) {
                    List<TaskIdentity> taskIdentities = new ArrayList<TaskIdentity>();
                    nextIdentity.setSuspensionState(SuspensionState.NORMAL.getState());
                    taskIdentities.add(nextIdentity);
                    identityService.restore(taskIdentities);
                    updateTaskIdentity = false;
                    Map<String, TaskDelegation> userTaskDelegationMap = Maps.newLinkedHashMap();
                    userTaskDelegationMap.put(nextIdentity.getUserId(), null);
                    // 按顺序办理的委托处理，如果当前用户等于待办人员，则忽略
                    if (!StringUtils.equals(nextIdentity.getUserId(), currentUserId)) {
                        FlowDelegationSettingsContextHolder.remove();
                        FlowUserSid todoUserSid = flowUserJobIdentityService.getFlowUserSids(Lists.newArrayList(nextIdentity)).get(0);
                        TaskDelegation taskDelegation = delegationExecutor.checkedAndPrepareDelegation(
                                todoUserSid, Sets.newHashSet(nextIdentity.getUserId()), taskInstance,
                                flowInstance, executionContext);
                        FlowDelegationSettingsContextHolder.remove();
                        if (taskDelegation != null) {
                            userTaskDelegationMap.put(nextIdentity.getUserId(), taskDelegation);
                            flowInstanceService.getDao().getSession().flush();
                            delegationExecutor.delegationWork(taskInstance, nextIdentity, taskDelegation);
                        }
                    }

                    List<MessageTemplate> todoMessageTemplates = executionContext.getToken().getFlowDelegate()
                            .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_TODO.getType());
                    // 发送工作到达通知
                    sendTodoMessage(true, userTaskDelegationMap, taskInstance, flowInstance, taskData, todoMessageTemplates);
                }
            } else {
                // 删除待办权限
                if (taskIdentity != null) {
                    identityService.removeTodo(taskIdentity);
                } else {
                    identityService.removeTodoByTaskInstUuidAndUserId(taskInstUuid, currentUserId);
                }
            }
        } else {
            // 3、正常办理
            if (taskIdentity != null) {
                // 删除待办权限，权限颗粒度为用户
                if (StringUtils.startsWith(taskIdentity.getUserId(), IdPrefix.USER.getValue())) {
                    identityService.removeTodo(taskIdentity);
                    if (hasSidTodoUserId(taskIdentity.getUserId(), taskInstance.getTodoUserId())) {
                        List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuidAndUserDetails(taskInstUuid, SpringSecurityUtils.getCurrentUser());
                        taskIdentities.forEach(identity -> {
                            // 权限颗粒度大于用户处理
                            handleSidTaskIdentity(currentUserId, taskInstUuid, identity, executionContext);
                        });
                    }
                } else {
                    // 权限颗粒度大于用户处理
                    handleSidTaskIdentity(currentUserId, taskInstUuid, taskIdentity, executionContext);
                }
            } else {
                // 删除待办权限
                identityService.removeTodoByTaskInstUuidAndUserId(taskInstUuid, currentUserId);
            }
        }

        // 更新办理人员及权限
        if (updateTaskIdentity) {
            identityService.updateTaskIdentity(taskInstance, taskData);
        }

        // 增加已办权限，主流程发起的子流程不添加已办权限
        if (StringUtils.isBlank(taskData.getParentTaskInstUuid(flowInstUuid))) {
            taskService.addDonePermission(currentUserId, taskInstUuid);
        }
        boolean result = aclTaskService.getSid(taskInstUuid, AclPermission.TODO).size() == 0;

        // 发布已办事件
        ApplicationContextHolder.publishEvent(new WorkDoneEvent(this, currentUserId, flowInstance, taskInstance, taskData));

        // 环节未结束更新预留字段
        if (!result) {
            // 设置预留字段
            ReservedFieldUtils.setReservedFields(flowInstance, taskData);
            this.flowInstanceService.save(flowInstance);
        }

        return result;
    }

    /**
     * @param userId
     * @param todoUserId
     * @return
     */
    private boolean hasSidTodoUserId(String userId, String todoUserId) {
        if (StringUtils.isBlank(todoUserId)) {
            return false;
        }
        List<String> userSids = Lists.newArrayList(PermissionGranularityUtils.getCurrentUserSids());
        List<String> todoUserIds = Lists.newArrayList(StringUtils.split(todoUserId, Separator.SEMICOLON.getValue()));
        userSids.remove(userId);
        todoUserIds.remove(userId);
        return CollectionUtils.containsAny(userSids, todoUserIds);
    }

    /**
     * @param currentUserId
     * @param taskInstUuid
     * @param taskIdentity
     * @param executionContext
     */
    private void handleSidTaskIdentity(String currentUserId, String taskInstUuid, TaskIdentity taskIdentity, ExecutionContext executionContext) {
        // 权限颗粒度大于用户
        String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(executionContext.getToken());
        aclTaskService.addDonePermission(currentUserId, PermissionGranularityUtils.getCurrentUserSids(), taskInstUuid);
        Map<String, String> userMap = workflowOrgService.getUsersByIds(Lists.newArrayList(taskIdentity.getUserId()), orgVersionIds);
        List<String> orgUserIds = Lists.newArrayList(userMap.keySet());
        List<String> sidDoneUserIds = aclTaskService.listSidDoneMarkerUserId(taskIdentity.getUserId(), taskInstUuid);
        orgUserIds.removeAll(sidDoneUserIds);
        if (CollectionUtils.isEmpty(orgUserIds)) {
            List<TaskIdentity> taskIdentities = identityService.listBySourceTaskIdentityUuid(taskIdentity.getUuid());
            if (isTargetTaskIdentityCompleted(taskIdentity, taskIdentities, Lists.newArrayList(WorkFlowTodoType.Transfer, WorkFlowTodoType.CounterSign))) {
                identityService.removeTodo(taskIdentity);
            }
        }

//        // 用户在多个部门下的处理
//        List<TaskIdentity> identities = identityService.getTodoByTaskInstUuid(taskInstUuid);
//        identities = identities.stream().filter(identity -> {
//            if (StringUtils.startsWith(identity.getUserId(), IdPrefix.USER.getValue())
//                    || StringUtils.equals(identity.getUuid(), taskIdentity.getUuid())) {
//                return false;
//            }
//            return true;
//        }).collect(Collectors.toList());
//        identities.forEach(identity -> {
//            boolean memberOf = workflowOrgService.isMemberOf(currentUserId, identity.getUserId(), orgVersionIds);
//            if (memberOf) {
//                aclTaskService.addDonePermission(currentUserId, identity.getUserId(), taskInstUuid);
//                Map<String, String> memberUserMap = workflowOrgService.getUsersByIds(Lists.newArrayList(identity.getUserId()), orgVersionIds);
//                List<String> memberOrgUserIds = Lists.newArrayList(memberUserMap.keySet());
//                List<String> memberSidDoneUserIds = aclTaskService.listSidDoneMarkerUserId(identity.getUserId(), taskInstUuid);
//                memberOrgUserIds.removeAll(memberSidDoneUserIds);
//                if (CollectionUtils.isEmpty(memberOrgUserIds)) {
//                    identityService.removeTodo(identity);
//                }
//            }
//        });
    }

    /**
     * @param taskIdentity
     * @param taskIdentities
     * @param todoTypes
     * @return
     */
    private boolean isTargetTaskIdentityCompleted(TaskIdentity taskIdentity, List<TaskIdentity> taskIdentities, List<Integer> todoTypes) {
        List<TaskIdentity> targetIdentities = taskIdentities.stream()
                .filter(identity -> StringUtils.equals(taskIdentity.getUuid(), identity.getSourceTaskIdentityUuid())
                        && !Integer.valueOf(SuspensionState.DELETED.getState()).equals(identity.getSuspensionState())
                        && todoTypes.contains(identity.getTodoType()))
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(targetIdentities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.repository.TaskRepository#copyPermissions2OtherParallelTaskInstances(java.lang.String, com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public void copyPermissions2OtherParallelTaskInstances(String userId, TaskInstance taskInstance) {
        // 并行任务管理，当并行任务完成时把权限添加到其他未完成的任务
        if (taskInstance != null && Boolean.TRUE.equals(taskInstance.getIsParallel())) {
            String sourceEntityUuid = taskInstance.getUuid();
            String parallelTaskInstUuid = taskInstance.getParallelTaskInstUuid();
            Map<String, Object> values = new HashMap<String, Object>();
            String hql = "from TaskInstance t where t.uuid != :taskInstUuid and t.parallelTaskInstUuid = :parallelTaskInstUuid and t.endTime is null";
            values.put("taskInstUuid", taskInstance.getUuid());
            values.put("parallelTaskInstUuid", parallelTaskInstUuid);
            List<TaskInstance> parallelTaskInstances = taskInstanceService.find(hql, values);
            for (int index = 0; index < parallelTaskInstances.size(); index++) {
                TaskInstance parallelTaskInstance = parallelTaskInstances.get(index);
                String targetEntityUuid = parallelTaskInstance.getUuid();
                // 将源实体的所有ACL权限复制到目标实体
                taskService.copyPermissions(sourceEntityUuid, targetEntityUuid, null, AclPermission.TODO);
                taskService.addDonePermission(userId, targetEntityUuid);
            }
            if (CollectionUtils.isNotEmpty(parallelTaskInstances)) {
                // 删除旧的权限
                aclTaskService.removePermission(sourceEntityUuid);
            }
        }
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void storeLeave(Node node, ExecutionContext executionContext) {
        // 任务结束
        TaskInstance taskInstance = executionContext.getToken().getTask();
        Date startTime = taskInstance.getStartTime();
        Date endTime = Calendar.getInstance().getTime();
        taskInstance.setDuration(endTime.getTime() - startTime.getTime());
        taskInstance.setEndTime(endTime);
        // 环节结束，挂起非最新结点
        if (executionContext.getToken().getTransition() == null) {
            taskInstance.setSuspensionState(SuspensionState.SUSPEND.getState());
        } else {
            for (Node n : executionContext.getToken().getTransition().getTos()) {
                if (!FlowDelegate.END_FLOW_ID.equals(n.getId())) {
                    taskInstance.setSuspensionState(SuspensionState.SUSPEND.getState());
                }
            }
        }
        this.taskInstanceService.merge(taskInstance);

        // 保存任务活动数据
        TaskActivity taskActivity = new TaskActivity();
        taskActivity.setTaskInstUuid(taskInstance.getUuid());
        List<TaskActivity> taskActivities = taskActivityService.findByExample(taskActivity);
        if (taskActivities.size() == 1) {
            taskActivity = taskActivities.get(0);
            taskActivity.setEndTime(endTime);
            taskActivityService.save(taskActivity);
        }

        // 环节结点时删除其环节督办权限
        List<String> taskSuperviseIds = getTaskSuperviseIds(node, executionContext.getToken().getFlowInstance());
        for (String taskSuperviseId : taskSuperviseIds) {
            taskService.removeSupervisePermission(taskSuperviseId, taskInstance.getUuid());
        }

        // 删除决策人督办权限
        if (node instanceof CollaborationTask) {
            FlowInstanceParameter flowInstanceParameter = getTaskDecisionMakerParameters(node, taskInstance, executionContext.getToken().getFlowInstance());
            if (flowInstanceParameter != null && StringUtils.isNotBlank(flowInstanceParameter.getValue())) {
                List<String> cobbaboartionSuperviseIds = Lists.newArrayList();
                if (StringUtils.startsWith(flowInstanceParameter.getValue(), "{")) {
                    Map<String, String> superviseIdMap = JsonUtils.json2Object(flowInstanceParameter.getValue(), Map.class);
                    cobbaboartionSuperviseIds.addAll(superviseIdMap.keySet());
                } else {
                    cobbaboartionSuperviseIds.addAll(Arrays.asList(StringUtils.split(flowInstanceParameter.getValue(), Separator.SEMICOLON.getValue())));
                }
                IdentityResolverStrategy identityResolverStrategy = ApplicationContextHolder.getBean(IdentityResolverStrategy.class);
                List<FlowUserSid> flowSuperviseSids = identityResolverStrategy.resolve(node, executionContext.getToken(),
                        executionContext.getToken().getFlowDelegate().getFlowMonitors(), ParticipantType.SuperviseUser);
                cobbaboartionSuperviseIds.removeAll(flowSuperviseSids.stream().map(sid -> sid.getId()).collect(Collectors.toList()));
                cobbaboartionSuperviseIds.removeAll(taskSuperviseIds);
                cobbaboartionSuperviseIds.forEach(superviseId -> {
                    taskService.removeSupervisePermission(superviseId, taskInstance.getUuid());
                });
            }
        }
    }

    /**
     * @param node
     * @param flowInstance
     * @return
     */
    private List<String> getTaskSuperviseIds(Node node, FlowInstance flowInstance) {
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstance.getUuid());
        example.setName("taskSuperviseIds_" + node.getId());
        List<FlowInstanceParameter> flowInstanceParameters = flowInstanceParameterService.findByExample(example);
        return flowInstanceParameters.stream().filter(parameter -> StringUtils.isNotBlank(parameter.getValue()))
                .flatMap(parameter -> Arrays.asList(StringUtils.split(parameter.getValue(), Separator.SEMICOLON.getValue())).stream())
                .collect(Collectors.toList());
    }

}

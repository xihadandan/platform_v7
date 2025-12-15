/*
 * @(#)2014-10-3 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.JobIdentity;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.*;
import com.wellsoft.pt.bpm.engine.executor.*;
import com.wellsoft.pt.bpm.engine.executor.param.CancelTransferParam;
import com.wellsoft.pt.bpm.engine.node.CollaborationTask;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.parser.activity.*;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.service.AclEntryService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 2014-10-3.1	zhulh		2014-10-3		Create
 * </pre>
 * @date 2014-10-3
 */
@Service
@Transactional
public class CancelTaskActionExecutorImpl extends TaskActionExecutor implements CancelTaskActionExecutor {

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private TaskBranchService taskBranchService;

    @Autowired
    private TaskFormOpinionService taskFormOpinionService;

    @Autowired
    private TaskFormOpinionLogService taskFormOpinionLogService;

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private DelegationExecutor delegationExecutor;

    @Autowired
    private IdentityResolverStrategy identityResolverStrategy;

    @Autowired
    private AclEntryService aclEntryService;
    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private DmsFileService dmsFileService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    /**
     * @param userId
     * @param topItem
     * @return
     */
    private static TaskOperationItem getTaskOperationItem(String userId, TaskActivityItem topItem) {
        TaskOperationStack stack = topItem.getOperationStack();
        Iterator<TaskOperationItem> it = stack.iterator();
        while (it.hasNext()) {
            TaskOperationItem taskOperationItem = it.next();// stack.pop();
            if (userId.equals(taskOperationItem.getOperator())) {
                return taskOperationItem;
            }
            // 委托提交
            Integer actionCode = taskOperationItem.getActionCode();
            if (ActionCode.DELEGATION_SUBMIT.getCode().equals(actionCode)) {
                String taskInstUuid = topItem.getTaskInstUuid();
                IdentityService identityService = ApplicationContextHolder.getBean(IdentityService.class);
                List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuidAndOwnerId(taskInstUuid, userId);
                if (!taskIdentities.isEmpty()) {
                    return taskOperationItem;
                }
            }
        }
        return null;
    }

    private static Integer getActionCode() {
        return ActionCode.CANCEL.getCode();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
    @Override
    public void execute(Param param) {
        TaskInstance taskInstance = param.getTaskInstance();
        TaskData taskData = param.getTaskData();

        // 检查工作数据状态
        preCheckTaskSuspensionState(taskInstance, WorkFlowOperation.CANCEL);

        // 如果工作已经办结则不可撤回
        boolean cancelOver = false;
        if (taskInstance.getEndTime() != null && taskInstance.getFlowInstance().getEndTime() != null) {
            cancelOver = true;
            // throw new WorkFlowException("工作已经办结，不可撤回!");
        }

        String userId = taskData.getUserId();
        String taskInstUuid = taskInstance.getUuid();

        // 获取当前流程中完成的任务
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        String flowInstUuid = flowInstance.getUuid();

        // 生成任务活动堆栈
        List<TaskActivityQueryItem> allTaskActivities = taskActivityService.getAllActivityByTaskInstUuid(taskInstUuid);
        // 生成任务活动操作堆栈
        List<TaskOperation> allTaskOperations = taskOperationService.getAllTaskOperationByFlowInstUuid(flowInstUuid);
        TaskActivityStack stack = TaskActivityStackFactary.build(taskInstUuid, allTaskActivities, allTaskOperations);

        TaskActivityItem topItem = stack.pop();
        TaskActivityItem secondItem = null;
        if (!stack.isEmpty()) {
            secondItem = stack.pop();
        }

        // 撤回办结
        if (cancelOver) {
            cancelOverTask(taskInstance, userId, allTaskActivities, allTaskOperations, taskData, topItem, secondItem);
            return;
        } else if (taskInstance.getEndTime() != null) {
            // 防止重复撤回
            throw new WorkFlowException("工作已处理，不可撤回!");
        }

        // 环节流转代码
        Integer transferCode = topItem.getTransferCode();
        // 1、提交流转->撤回
        if (TransferCode.Submit.getCode().equals(transferCode)) {
            Boolean isPallel = topItem.getIsParallel();
            // 1、正常提交、退回到下一个环节后撤回
            if (Boolean.FALSE.equals(isPallel)) {
                if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                    cancelSubmitTask(taskInstance, taskData, topItem, secondItem);
                } else if (!topItem.getOperationItems().isEmpty()) {
                    // 环节操作撤回
                    doCancelMultiUserTask(taskInstance, taskData, userId, flowInstance, topItem, allTaskOperations);
                } else if (secondItem != null && secondItem.getIsParallel()
                        && isAllowedCancelForOverParallelTask(userId, allTaskActivities, allTaskOperations)) {
                    // 并行流程撤回操作
                    cancelForOverParallelTask(taskInstance, userId, allTaskActivities, allTaskOperations, taskData,
                            topItem);
                }
            } else {
                // 并行流程撤回操作
                String parallelTaskInstUuid = topItem.getParallelTaskInstUuid();
                String preTaskInstUuid = topItem.getPreTaskInstUuid();
                // 1、并行分发提交撤回
                if (preTaskInstUuid.equals(parallelTaskInstUuid) && userId.equals(topItem.getCreator())) {
                    cancelSubmitParallelTask(taskInstance, taskData, topItem, secondItem);
                } else if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                    // 2、并行分支提交后进入下一环节撤回
                    cancelSubmitTask(taskInstance, taskData, topItem, secondItem);
                } else if (isAllowedCancelForOverParallelTask(userId, allTaskActivities, allTaskOperations)) {
                    // 3、并行分支提交结束后撤回
                    cancelForOverParallelTask(taskInstance, userId, allTaskActivities, allTaskOperations, taskData,
                            topItem);
                } else if (!topItem.getOperationItems().isEmpty()) {
                    // 4、并行分支环节操作撤回
                    doCancelMultiUserTask(taskInstance, taskData, userId, flowInstance, topItem, allTaskOperations);
                }
            }
        } else if (TransferCode.RollBack.getCode().equals(transferCode)) {
            // 2、退回流转->撤回
            if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                cancelRollbackTask(taskInstance, taskData, topItem, allTaskOperations);
            } else if (!topItem.getOperationItems().isEmpty()) {
                // 环节操作撤回
                doCancelMultiUserTask(taskInstance, taskData, userId, flowInstance, topItem, allTaskOperations);
            }
        } else if (TransferCode.Cancel.getCode().equals(transferCode)) {
            // 3、撤回流转->撤回
            if (!topItem.getOperationItems().isEmpty()) {
                // 环节操作撤回
                doCancelMultiUserTask(taskInstance, taskData, userId, flowInstance, topItem, allTaskOperations);
            }
        } else if (TransferCode.GotoTask.getCode().equals(transferCode)) {
            // 4、移交环节流转
        } else if (TransferCode.SkipTask.getCode().equals(transferCode)) {
            // 5、办理人为空自动跳过
            cancelSubmitTask(taskInstance, taskData, topItem, secondItem);
        } else if (TransferCode.TransferSubmit.getCode().equals(transferCode)) {
            // 6、转办提交流转->撤回
            if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                cancelTransferSubmitTask(taskInstance, taskData, topItem, secondItem);
            } else if (!topItem.getOperationItems().isEmpty()) {
                // 环节操作撤回
                doCancelMultiUserTask(taskInstance, taskData, userId, flowInstance, topItem, allTaskOperations);
            }
        } else if (TransferCode.DelegationSubmit.getCode().equals(transferCode)) {
            // 7、委托提交流转->撤回
            Boolean isPallel = topItem.getIsParallel();
            if (Boolean.FALSE.equals(isPallel)) {
                if (isAllowCancelDelegationSubmit(topItem, userId)) {
                    cancelDelegationSubmitTask(taskInstance, taskData, topItem, secondItem);
                } else if (!topItem.getOperationItems().isEmpty()) {
                    // 环节操作撤回
                    doCancelMultiUserTask(taskInstance, taskData, userId, flowInstance, topItem, allTaskOperations);
                }
            } else {
                // 并行流程撤回操作
                String parallelTaskInstUuid = topItem.getParallelTaskInstUuid();
                String preTaskInstUuid = topItem.getPreTaskInstUuid();
                // 1、并行分发提交撤回
                if (preTaskInstUuid.equals(parallelTaskInstUuid) && userId.equals(topItem.getCreator())) {
                    cancelSubmitParallelTask(taskInstance, taskData, topItem, secondItem);
                } else if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                    // 2、并行分支提交后进入下一环节撤回
                    cancelSubmitTask(taskInstance, taskData, topItem, secondItem);
                } else if (isAllowedCancelForOverParallelTask(userId, allTaskActivities, allTaskOperations)) {
                    // 3、并行分支提交结束后撤回
                    cancelForOverParallelTask(taskInstance, userId, allTaskActivities, allTaskOperations, taskData,
                            topItem);
                } else if (!topItem.getOperationItems().isEmpty()) {
                    // 4、并行分支环节操作撤回
                    doCancelMultiUserTask(taskInstance, taskData, userId, flowInstance, topItem, allTaskOperations);
                }
            }
        }

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());

        // 任务：7905 开发-主导-支持配置已办中是否出现已撤回的流程
        removeAclEntry(taskInstance);

        // task：5190 已办流程撤回后清除归档库对应文件
        // removeDmsFile(taskInstance.getDataUuid(),
        // allTaskActivities.get(0).getFlowInstUuid());
    }

    /**
     * 撤回动作，移除归档文件
     * task：5190 已办流程撤回后清除归档库对应文件
     *
     * @param dataUuid
     */
    private void removeDmsFile(String dataUuid, String flowInstUuid) {
        if (StringUtils.isNotBlank(dataUuid)) {
            List<DmsFileEntity> results = dmsFileService.getByDataUuid(dataUuid);
            if (CollectionUtils.isNotEmpty(results)) {
                // 和学敏确认：不管对文件有没有删除权限，只要是撤回，都将文件清除
                dmsFileService.removeAll(results);
            } else {
                results = dmsFileService.getByDataUuid(flowInstUuid);
                if (CollectionUtils.isNotEmpty(results)) {
                    dmsFileService.removeAll(results);
                }
            }

            // 可能因为某些问题，导致flowInstUuid为null，从而将所有记录都查询出来，清除会导致所有数据丢失
            if (StringUtils.isNotBlank(flowInstUuid)) {
                FlowInstanceParameter dataUuidParameter = new FlowInstanceParameter();
                dataUuidParameter.setFlowInstUuid(flowInstUuid);
                List<FlowInstanceParameter> dataUuidparameters = flowService
                        .findFlowInstanceParameter(dataUuidParameter);
                for (FlowInstanceParameter parameter : dataUuidparameters) {
                    String dataDefUuid = parameter.getValue();
                    DmsFileEntity entity = new DmsFileEntity();
                    entity.setDataDefUuid(dataDefUuid);
                    results = dmsFileService.findByExample(entity);
                    if (CollectionUtils.isNotEmpty(results)) {
                        dmsFileService.removeAll(results);
                    }
                }
            }
        }
    }

    /**
     * 撤回动作，移除已办权限
     * 任务：7905 开发-主导-支持配置已办中是否出现已撤回的流程
     *
     * @param taskInstance
     * @return void
     **/
    private void removeAclEntry(TaskInstance taskInstance) {
        // 1、用户在此流程实例中只办理了一次，此用户撤回时，直接移除已办权限

        // 2、用户在此流程实例中，办理了多闪，此用户撤回时，已办权限不移除
        List<TaskOperation> taskOperations = taskOperationService
                .getByFlowInstUuid(taskInstance.getFlowInstance().getUuid());
        List<TaskOperation> existTaskOperation = Lists.newArrayList();

        Set<String> taskIds = Sets.newHashSet();
        // 会签、转办、退回、直接退回、移交办理人、跳转环节 计数
        Integer count = 0;
        if (taskOperations != null && taskOperations.size() > 0) {
            for (TaskOperation taskOperation : taskOperations) {
                if (taskOperation.getAssignee().equals(SpringSecurityUtils.getCurrentUserId())) {
                    existTaskOperation.add(taskOperation);
                    taskIds.add(taskOperation.getTaskId());
                    if (WorkFlowOperation.TRANSFER.equals(taskOperation.getActionType())
                            || WorkFlowOperation.COUNTER_SIGN.equals(taskOperation.getActionType())
                            || WorkFlowOperation.ROLLBACK.equals(taskOperation.getActionType())
                            || WorkFlowOperation.HAND_OVER.equals(taskOperation.getActionType())
                            || WorkFlowOperation.GOTO_TASK.equals(taskOperation.getActionType())
                            || WorkFlowOperation.DIRECT_ROLLBACK.equals(taskOperation.getActionType())) {
                        ++count;
                    }
                }
            }
        }
        // 会签、转办、退回、直接退回、移交办理人、跳转环节 撤回就加n笔
        // && 提交 撤回就两笔
        // &&提交 办结流程 操作撤回就一笔
        if ((taskIds.size() + count) == 2 || (taskIds.size() == 2 &&
                isExistCancel(existTaskOperation) && !isExistHandOverOrGotoTask(existTaskOperation))
                || (taskIds.size() + count) == 1) {
            List<TaskInstance> taskInstances = taskInstanceService
                    .getOrderDescByFlowInstUuid(existTaskOperation.get(0).getFlowInstUuid());
            if (taskInstances != null && taskInstances.size() > 0) {
                // 移除权限
                aclEntryService.deleteByObjectIdIdentity(taskInstances.get(0).getUuid());
            }
        }
    }

    /**
     * 存在移交办理人或跳转环节，返回true
     *
     * @param existTaskOperation
     * @return java.lang.Boolean
     **/
    private Boolean isExistHandOverOrGotoTask(List<TaskOperation> existTaskOperation) {
        Boolean isExistHandOverOrGotoTask = Boolean.FALSE;
        for (TaskOperation taskOperation : existTaskOperation) {
            if (WorkFlowOperation.HAND_OVER.equals(taskOperation.getActionType())
                    || WorkFlowOperation.GOTO_TASK.equals(taskOperation.getActionType())) {
                isExistHandOverOrGotoTask = Boolean.TRUE;
                break;
            }
        }
        return isExistHandOverOrGotoTask;
    }

    /**
     * 符合条件的环节操作中，是否存在撤回操作
     * 如果中间过程存在移交办理人或跳转环节，返回false
     *
     * @param existTaskOperation
     * @return java.lang.Boolean
     **/
    private Boolean isExistCancel(List<TaskOperation> existTaskOperation) {
        Boolean isExistCancel = Boolean.FALSE;
        for (TaskOperation taskOperation : existTaskOperation) {
            if (StringUtils.equals(taskOperation.getAction(), WorkFlowPrivilege.Cancel.getName())) {
                isExistCancel = Boolean.TRUE;
                break;
            }
        }

        return isExistCancel;
    }

    /**
     * @param userId
     * @return
     */
    private boolean isAllowCancelDelegationSubmit(TaskActivityItem topItem, String userId) {
        // 存在操作记录，不可撤回
        if (!topItem.getOperationItems().isEmpty()) {
            return false;
        }
        if (userId.equals(topItem.getCreator())) {
            return true;
        }
        String taskInstUuid = topItem.getPreTaskInstUuid();
        List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuidAndOwnerId(taskInstUuid, userId);
        for (TaskIdentity taskIdentity : taskIdentities) {
            if (WorkFlowTodoType.Delegation.equals(taskIdentity.getTodoType())
                    && StringUtils.equals(taskIdentity.getUserId(), topItem.getCreator())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 1、撤回提交办结
     * 2、撤回转办提交办结
     * 3、撤回移交环节办结
     *
     * @param currentTaskInstance
     * @param userId
     * @param allTaskActivities
     * @param allTaskOperations
     * @param taskData
     * @param topItem
     * @param secondItem
     */
    @SuppressWarnings("unchecked")
    private void cancelOverTask(TaskInstance currentTaskInstance, String userId,
                                List<TaskActivityQueryItem> allTaskActivities, List<TaskOperation> allTaskOperations, TaskData taskData,
                                TaskActivityItem topItem, TaskActivityItem secondItem) {
        // 并行流程撤回操作
        if (secondItem != null && secondItem.getIsParallel()
                && isAllowedCancelForOverParallelTask(userId, allTaskActivities, allTaskOperations)) {
            cancelForOverParallelTask(currentTaskInstance, userId, allTaskActivities, allTaskOperations, taskData,
                    topItem);
            return;
        }

        TaskOperationItem taskOperation = getOverTaskOperationItem(userId, topItem);
        if (taskOperation == null) {
            if (TransferCode.SkipTask.getCode().equals(topItem.getTransferCode()) && CollectionUtils.isEmpty(topItem.getOperationItems())) {
                cancelSubmitTask(currentTaskInstance, taskData, topItem, secondItem);
                // 撤回流程实例
                if (taskService.countUnfinishedTasks(topItem.getFlowInstUuid()) > 0) {
                    FlowInstance flowInstance = currentTaskInstance.getFlowInstance();
                    flowInstance.setIsActive(true);
                    flowInstance.setDuration(0l);
                    flowInstance.setEndTime(null);
                    this.dao.save(flowInstance);
                }
            }
            return;
        }

        Integer actionCode = taskOperation.getActionCode();
        String identityJson = taskOperation.getExtraInfo();
        TaskIdentity historyTaskIdentity = null;
        if (StringUtils.isNotBlank(identityJson)) {
            if (ActionCode.TRANSFER_SUBMIT.getCode().equals(actionCode)) {
                historyTaskIdentity = JsonUtils.json2Object(identityJson, TaskIdentity.class);
                identityService.restoreTodoTransferSubmit(historyTaskIdentity);
            } else if (ActionCode.GOTO_TASK.getCode().equals(actionCode)) {
                Collection<TaskIdentity> taskIdentities = JsonUtils.toCollection(identityJson, TaskIdentity.class);
                List<TaskIdentity> list = new ArrayList<TaskIdentity>();
                list.addAll(taskIdentities);
                identityService.restore(list);
            } else {
                historyTaskIdentity = JsonUtils.json2Object(identityJson, TaskIdentity.class);
                identityService.restoreTodoSumit(historyTaskIdentity);
            }

            String targetEntityUuid = taskOperation.getTaskInstUuid();
            // 复制与删除其他并行任务的权限
            String otherParallelTaskInstUuid = topItem.getTaskInstUuid();
            if (!StringUtils.equals(otherParallelTaskInstUuid, targetEntityUuid)) {
                taskService.copyPermissions(otherParallelTaskInstUuid, targetEntityUuid, null, AclPermission.TODO);
                aclTaskService.removeSidPermission(userId, otherParallelTaskInstUuid);
            }
            // 撤回环节实例
            TaskInstance taskInstance = taskService.getTask(targetEntityUuid);
            taskInstance.setAction("撤回");
            taskInstance.setActionType("Cancel");
            taskInstance.setDuration(0);
            taskInstance.setEndTime(null);
            taskInstance.setSuspensionState(SuspensionState.NORMAL.getState());
            taskService.save(taskInstance);

            // 撤回流程实例
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            flowInstance.setIsActive(true);
            flowInstance.setDuration(0l);
            flowInstance.setEndTime(null);
            this.dao.save(flowInstance);

            // 添加管理员监控权限
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
            String taskId = taskInstance.getId();
            Node taskNode = flowDelegate.getTaskNode(taskId);
            String formUuid = taskInstance.getFormUuid();
            String dataUuid = taskInstance.getDataUuid();
            taskData.setFormUuid(formUuid);
            taskData.setDataUuid(dataUuid);
            taskData.setDyFormData(dataUuid, dyFormFacade.getDyFormData(formUuid, dataUuid));
            Token token = new Token(taskInstance, taskData);
            List<UserUnitElement> rawAdmins = flowDelegate.getFlowAdmins();
            List<String> adminIds = new ArrayList<String>();
            // 没有设置监控人，则默认系统管理员有权限
            if (CollectionUtils.isEmpty(rawAdmins)) {
//                adminIds.addAll(orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId()));
                adminIds.addAll(workflowOrgService.listCurrentTenantAdminIds());
            } else {
                adminIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(
                        identityResolverStrategy.resolve(taskNode, token, rawAdmins, ParticipantType.MonitorUser)));
            }
            for (String adminId : adminIds) {
                taskService.addMonitorPermission(adminId, targetEntityUuid);
            }

            List<UserUnitElement> rawMonitors = new ArrayList<>();
            rawMonitors.addAll(flowDelegate.getFlowMonitors());
            rawMonitors.addAll(flowDelegate.getTaskMonitors(taskId));
            // 设置督办人
            List<String> monitorIds = new ArrayList<String>();
            monitorIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(
                    identityResolverStrategy.resolve(taskNode, token, rawMonitors, ParticipantType.SuperviseUser)));
            for (String monitorId : monitorIds) {
                taskService.addSupervisePermission(monitorId, targetEntityUuid);
            }

            // 撤回操作身份
            if (historyTaskIdentity != null) {
                taskData.setUserJobIdentityId(userId, historyTaskIdentity.getTaskInstUuid(), historyTaskIdentity.getIdentityId());
            }
            // 保存操作历史
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                    ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null, null,
                    null, taskInstance, flowInstance, taskData);

            // 撤回办理意见
            cancelOpinionText(taskData, topItem.getOperationItems(), userId, formUuid, dataUuid);

            // 任务：7905 开发-主导-支持配置已办中是否出现已撤回的流程
            removeAclEntry(taskInstance);

            // task：5190 已办流程撤回后清除归档库对应文件
            // removeDmsFile(dataUuid, allTaskActivities.get(0).getFlowInstUuid());
        }
    }

    /**
     * 获取办结的那个操作
     *
     * @param userId
     * @param topItem
     * @return
     */
    private TaskOperationItem getOverTaskOperationItem(String userId, TaskActivityItem topItem) {
        List<TaskOperationItem> items = topItem.getOperationItems();
        TaskActivityStackFactary.sortTaskOperationItem(items, false);
        for (TaskOperationItem taskOperationItem : items) {
            Integer actionCode = taskOperationItem.getActionCode();
            // 确保返回的第一个指定的操作代码为指定用户操作的，否则返回空
            if (ActionCode.SUBMIT.getCode().equals(actionCode)
                    || ActionCode.TRANSFER_SUBMIT.getCode().equals(actionCode)
                    || ActionCode.DELEGATION_SUBMIT.getCode().equals(actionCode)
                    || ActionCode.GOTO_TASK.getCode().equals(actionCode)) {
                if (userId.equals(taskOperationItem.getOperator())) {
                    return taskOperationItem;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 如何描述该方法
     *
     * @param userId
     * @param allTaskActivities
     * @param allTaskOperations
     * @param topItem
     */
    private void cancelForOverParallelTask(TaskInstance currentTaskInstance, String userId,
                                           List<TaskActivityQueryItem> allTaskActivities, List<TaskOperation> allTaskOperations, TaskData taskData,
                                           TaskActivityItem topItem) {
        TaskOperation taskOperation = getOverParallelTaskOperation(userId, allTaskActivities, allTaskOperations);
        if (taskOperation == null) {
            return;
        }

        if (Boolean.FALSE.equals(currentTaskInstance.getIsParallel())) {
            TaskActivityItem secondItem = null;
            String taskInstUuid = taskOperation.getTaskInstUuid();
            TaskActivityStack otherStack = TaskActivityStackFactary.build(taskInstUuid, allTaskActivities,
                    allTaskOperations);
            if (!otherStack.isEmpty()) {
                secondItem = otherStack.peek();
            }
            if (secondItem != null) {
                cancelSubmitSerialTask(currentTaskInstance, taskData, topItem, secondItem);
            }
        } else {
            String identityJson = taskOperation.getExtraInfo();
            TaskIdentity historyTaskIdentity = null;
            if (StringUtils.isNotBlank(identityJson)) {
                historyTaskIdentity = JsonUtils.json2Object(identityJson, TaskIdentity.class);

                identityService.restoreTodoSumit(historyTaskIdentity);

                String targetEntityUuid = taskOperation.getTaskInstUuid();
                // 复制与删除其他并行任务的权限
                String otherParallelTaskInstUuid = topItem.getTaskInstUuid();
                taskService.copyPermissions(otherParallelTaskInstUuid, targetEntityUuid, null, AclPermission.TODO);
                aclTaskService.removeSidPermission(userId, otherParallelTaskInstUuid);

                // 记录撤回本身
                String newIdentityJson = JsonUtils.object2Json(historyTaskIdentity);
                TaskInstance taskInstance = taskService.getTask(targetEntityUuid);
                taskInstance.setDuration(0);
                taskInstance.setEndTime(null);
                taskInstance.setSuspensionState(SuspensionState.NORMAL.getState());
                taskService.save(taskInstance);

                // 恢复分支状态
                taskBranchService.restoreBranchTaskByCurrentTaskInstUuid(taskInstance.getUuid());

                FlowInstance flowInstance = taskInstance.getFlowInstance();
                // 保存操作历史
                taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                        ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null,
                        historyTaskIdentity.getUuid(), newIdentityJson, taskInstance, flowInstance, taskData);
            }
        }
    }

    /**
     * @param userId
     * @param allTaskActivities
     * @param allTaskOperations
     */
    private boolean isAllowedCancelForOverParallelTask(String userId, List<TaskActivityQueryItem> allTaskActivities,
                                                       List<TaskOperation> allTaskOperations) {
        return getOverParallelTaskOperation(userId, allTaskActivities, allTaskOperations) != null;
    }

    public TaskOperation getOverParallelTaskOperation(String userId, List<TaskActivityQueryItem> allTaskActivities,
                                                      List<TaskOperation> allTaskOperations) {
        List<String> parallelTaskUuids = new ArrayList<String>();
        for (TaskActivityQueryItem taskActivityQueryItem : allTaskActivities) {
            if (Boolean.TRUE.equals(taskActivityQueryItem.getIsParallel()) && taskActivityQueryItem.getEndTime() != null
                    && userId.equals(taskActivityQueryItem.getModifier())) {
                parallelTaskUuids.add(taskActivityQueryItem.getTaskInstUuid());
            }
        }
        TaskOperation lastTaskOperation = null;
        Collections.sort(allTaskOperations, new Comparator<TaskOperation>() {

            @Override
            public int compare(TaskOperation o1, TaskOperation o2) {
                return o1.getCreateTime().before(o2.getCreateTime()) ? 1 : -1;
            }
        });
        for (TaskOperation taskOperation : allTaskOperations) {
            if (userId.equals(taskOperation.getAssignee())
                    && parallelTaskUuids.contains(taskOperation.getTaskInstUuid())) {
                lastTaskOperation = taskOperation;
                break;
            }
        }
        return lastTaskOperation;
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param userId
     * @param flowInstance
     * @param topItem
     */
    private void doCancelMultiUserTask(TaskInstance taskInstance, TaskData taskData, String userId,
                                       FlowInstance flowInstance, TaskActivityItem topItem, List<TaskOperation> allTaskOperations) {
        TaskOperationItem operationItem = getTaskOperationItem(userId, topItem);
        Integer actionCode = -1;
        String identityJson = null;
        TaskIdentity historyTaskIdentity = null;
        if (operationItem != null) {
            actionCode = operationItem.getActionCode();
            identityJson = operationItem.getExtraInfo();
            if (StringUtils.isNotBlank(identityJson)) {
                historyTaskIdentity = JsonUtils.json2Object(identityJson, TaskIdentity.class);
            }
        }
        switch (actionCode) {
            case 1:
                // 提交
                if (identityService.isAllowedRestoreTodoSumit(historyTaskIdentity)) {
                    // 多人提交撤回
                    cancelMultiUserSubmitTask(taskInstance, taskData, userId, flowInstance, topItem, operationItem,
                            allTaskOperations);
                }
                break;
            case 2:
                // 会签提交
                if (identityService.isAllowedRestoreTodoCounterSignSubmit(historyTaskIdentity)) {
                    // 会签提交撤回
                    cancelTodoCounterSignSubmit(taskInstance, taskData, userId, flowInstance, topItem, historyTaskIdentity,
                            operationItem, allTaskOperations);
                }
                break;
            case 3:
                // 转办提交
                if (identityService.isAllowedRestoreTodoTransferSubmit(historyTaskIdentity) || CollectionUtils
                        .isNotEmpty(identityService.getTodoByTaskInstUuid(historyTaskIdentity.getTaskInstUuid()))) {
                    // 转办提交撤回
                    cancelTodoTransferSubmit(taskInstance, taskData, userId, flowInstance, topItem, historyTaskIdentity,
                            operationItem, allTaskOperations);
                }
                break;
            case 7:
                // 转办
                if (identityService.isAllowedRestoreTransfer(historyTaskIdentity)) {
                    // 转办撤回
                    cancelTransferTask(taskInstance, taskData, userId, flowInstance, topItem, historyTaskIdentity,
                            operationItem, allTaskOperations);
                }
                break;
            case 8:
                // 会签
                if (identityService.isAllowedRestoreCounterSign(historyTaskIdentity)) {
                    // 会签撤回
                    cancelCounterSign(taskInstance, taskData, userId, flowInstance, topItem, historyTaskIdentity,
                            operationItem, allTaskOperations);
                }
                break;
            case 27:
                // 委托提交
                if (identityService.isAllowedRestoreTodoDelegationSubmit(historyTaskIdentity)) {
                    // 委托提交撤回
                    cancelTodoDelegationSubmit(taskInstance, taskData, userId, flowInstance, topItem, historyTaskIdentity,
                            operationItem, allTaskOperations);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param taskData
     * @param userId
     * @param flowInstance
     * @param topItem
     * @param historyTaskIdentity
     */
    private void cancelTodoTransferSubmit(TaskInstance taskInstance, TaskData taskData, String userId,
                                          FlowInstance flowInstance, TaskActivityItem topItem, TaskIdentity historyTaskIdentity,
                                          TaskOperationItem operationItem, List<TaskOperation> allTaskOperations) {
        // 恢复正常待办提交的操作
        identityService.restoreTodoTransferSubmit(historyTaskIdentity);

        // 撤回操作身份
        if (operationItem != null) {
            taskData.setUserJobIdentityId(userId, taskInstance.getUuid(), operationItem.getOperatorIdentityId());
        }
        // 记录撤回本身
        String identityJson = JsonUtils.object2Json(historyTaskIdentity);
        // 保存操作历史
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null,
                historyTaskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);

        // 撤回办理意见
        List<TaskOperationItem> operationItems = TaskActivityStackFactary.convert2OperationItem(allTaskOperations,
                topItem.getTaskInstUuid());
        cancelOpinionText(taskData, operationItems, userId, taskInstance.getFormUuid(), taskInstance.getDataUuid());
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param userId
     * @param flowInstance
     * @param topItem
     * @param historyTaskIdentity
     * @param allTaskOperations
     */
    private void cancelTodoDelegationSubmit(TaskInstance taskInstance, TaskData taskData, String userId,
                                            FlowInstance flowInstance, TaskActivityItem topItem, TaskIdentity historyTaskIdentity,
                                            TaskOperationItem operationItem, List<TaskOperation> allTaskOperations) {
        // 恢复正常待办提交的操作
        identityService.restoreTodoDelegationSubmit(userId, historyTaskIdentity);

        // 撤回操作身份
        if (operationItem != null) {
            taskData.setUserJobIdentityId(userId, taskInstance.getUuid(), operationItem.getOperatorIdentityId());
        }
        // 记录撤回本身
        String identityJson = JsonUtils.object2Json(historyTaskIdentity);
        // 保存操作历史
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null,
                historyTaskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);

        // 撤回办理意见
        List<TaskOperationItem> operationItems = TaskActivityStackFactary.convert2OperationItem(allTaskOperations,
                topItem.getTaskInstUuid());
        cancelOpinionText(taskData, operationItems, userId, taskInstance.getFormUuid(), taskInstance.getDataUuid());
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param taskData
     * @param userId
     * @param flowInstance
     * @param topItem
     */
    private void cancelTodoCounterSignSubmit(TaskInstance taskInstance, TaskData taskData, String userId,
                                             FlowInstance flowInstance, TaskActivityItem topItem, TaskIdentity historyTaskIdentity,
                                             TaskOperationItem operationItem, List<TaskOperation> allTaskOperations) {
        // 恢复正常待办提交的操作
        identityService.restoreTodoCounterSignSubmit(historyTaskIdentity);

        // 撤回操作身份
        if (operationItem != null) {
            taskData.setUserJobIdentityId(userId, taskInstance.getUuid(), operationItem.getOperatorIdentityId());
        }
        // 记录撤回本身
        String identityJson = JsonUtils.object2Json(historyTaskIdentity);
        // 保存操作历史
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null,
                historyTaskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);

        // 撤回办理意见
        List<TaskOperationItem> operationItems = TaskActivityStackFactary.convert2OperationItem(allTaskOperations,
                topItem.getTaskInstUuid());
        cancelOpinionText(taskData, operationItems, userId, taskInstance.getFormUuid(), taskInstance.getDataUuid());
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param taskData
     * @param userId
     * @param flowInstance
     * @param topItem
     * @param historyTaskIdentity
     */
    private void cancelCounterSign(TaskInstance taskInstance, TaskData taskData, String userId,
                                   FlowInstance flowInstance, TaskActivityItem topItem, TaskIdentity historyTaskIdentity,
                                   TaskOperationItem operationItem, List<TaskOperation> allTaskOperations) {
        // 恢复正常待办提交的操作
        identityService.restoreCounterSign(historyTaskIdentity);

        // 撤回操作身份
        if (operationItem != null) {
            taskData.setUserJobIdentityId(userId, taskInstance.getUuid(), operationItem.getOperatorIdentityId());
        }
        // 记录撤回本身
        String identityJson = JsonUtils.object2Json(historyTaskIdentity);
        // 保存操作历史
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null,
                historyTaskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);

        // 撤回办理意见
        List<TaskOperationItem> operationItems = TaskActivityStackFactary.convert2OperationItem(allTaskOperations,
                topItem.getTaskInstUuid());
        cancelOpinionText(taskData, operationItems, userId, taskInstance.getFormUuid(), taskInstance.getDataUuid());
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param taskData
     * @param userId
     * @param flowInstance
     * @param topItem
     */
    private void cancelTransferTask(TaskInstance taskInstance, TaskData taskData, String userId,
                                    FlowInstance flowInstance, TaskActivityItem topItem, TaskIdentity historyTaskIdentity,
                                    TaskOperationItem operationItem, List<TaskOperation> allTaskOperations) {
        // 恢复正常待办提交的操作
        identityService.restoreTransfer(historyTaskIdentity);

        // 撤回操作身份
        if (operationItem != null) {
            taskData.setUserJobIdentityId(userId, taskInstance.getUuid(), operationItem.getOperatorIdentityId());
        }
        // 记录撤回本身
        String identityJson = JsonUtils.object2Json(historyTaskIdentity);
        // 保存操作历史
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null,
                historyTaskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);

        // 撤回办理意见
        List<TaskOperationItem> operationItems = TaskActivityStackFactary.convert2OperationItem(allTaskOperations,
                topItem.getTaskInstUuid());
        cancelOpinionText(taskData, operationItems, userId, taskInstance.getFormUuid(), taskInstance.getDataUuid());
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param taskData
     * @param userId
     * @param flowInstance
     * @param topItem
     */
    private void cancelMultiUserSubmitTask(TaskInstance taskInstance, TaskData taskData, String userId,
                                           FlowInstance flowInstance, TaskActivityItem topItem, TaskOperationItem operationItem,
                                           List<TaskOperation> allTaskOperations) {
        Integer actionCode = operationItem.getActionCode();
        if (ActionCode.SUBMIT.getCode().equals(actionCode)) {
            String identityJson = operationItem.getExtraInfo();
            TaskIdentity historyTaskIdentity = JsonUtils.json2Object(identityJson, TaskIdentity.class);
            if (identityService.isAllowedRestoreTodoSumit(historyTaskIdentity)) {
                // 按顺序办理撤回
                if (historyTaskIdentity.getSortOrder() != null) {
                    // 删除其他待办权限
                    aclTaskService.removePermission(historyTaskIdentity.getTaskInstUuid(), AclPermission.TODO);
                    // 删除顺序为空的待办标识
                    identityService.removeTodoWithoutSortOrderByTaskInstUuid(historyTaskIdentity.getTaskInstUuid());
                    // 撤回委托
                    delegationExecutor.cancelDelegationByTaskInstUuid(historyTaskIdentity.getTaskInstUuid());
                }
                // 恢复正常待办提交的操作
                identityService.restoreTodoSumit(historyTaskIdentity);

                // 撤回操作身份
                if (operationItem != null) {
                    taskData.setUserJobIdentityId(userId, taskInstance.getUuid(), operationItem.getOperatorIdentityId());
                }
                // 保存操作历史
                taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                        ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null,
                        historyTaskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);

                // 撤回办理意见
                List<TaskOperationItem> operationItems = TaskActivityStackFactary
                        .convert2OperationItem(allTaskOperations, topItem.getTaskInstUuid());
                cancelOpinionText(taskData, operationItems, userId, taskInstance.getFormUuid(),
                        taskInstance.getDataUuid());
            }
        }
    }

    /**
     * @param topItem
     * @param secondItem
     */
    private void cancelSubmitTask(TaskInstance taskInstance, TaskData taskData, TaskActivityItem topItem,
                                  TaskActivityItem secondItem) {
        String parallelTaskInstUuid = taskInstance.getParallelTaskInstUuid();
        Boolean isParallel = taskInstance.getIsParallel();
        // 并行流程提交撤回
        if (Boolean.TRUE.equals(isParallel) && StringUtils.isNotBlank(parallelTaskInstUuid)) {
            cancelSubmitParallelTask(taskInstance, taskData, topItem, secondItem);
        } else {
            // 并行分支提交聚合撤回
            if (secondItem != null) {
                parallelTaskInstUuid = secondItem.getParallelTaskInstUuid();
                isParallel = secondItem.getIsParallel();
                if (Boolean.TRUE.equals(isParallel) && StringUtils.isNotBlank(parallelTaskInstUuid)) {
                    cancelSubmit2MergeParallelTask(taskInstance, taskData, topItem, secondItem);
                } else {
                    // 串行流程提交撤回
                    cancelSubmitSerialTask(taskInstance, taskData, topItem, secondItem);
                }
            } else {
                // 串行流程提交撤回
                cancelSubmitSerialTask(taskInstance, taskData, topItem, secondItem);
            }
        }
    }

    /**
     * 并行流程提交撤回
     *
     * @param taskInstance
     * @param taskData
     * @param topItem
     * @param secondItem
     */
    private void cancelSubmitParallelTask(TaskInstance taskInstance, TaskData taskData, TaskActivityItem topItem,
                                          TaskActivityItem secondItem) {
        String parallelTaskInstUuid = taskInstance.getParallelTaskInstUuid();
        String preTaskInstUuid = topItem.getPreTaskInstUuid();
        // 并行中的串行流程提交撤回
        if (!StringUtils.equals(parallelTaskInstUuid, preTaskInstUuid)) {
            cancelSubmitSerialTask(taskInstance, taskData, topItem, secondItem);
            return;
        }

        String taskKey = taskInstance.getUuid() + SpringSecurityUtils.getCurrentUserId();
        // 1、将还在运行的并行流程结束
        // 结束并行的其他任务
        String flowInstUuid = taskInstance.getFlowInstance().getUuid();
        List<TaskInstance> unfinishedTasks = taskService.getUnfinishedTasks(taskInstance.getFlowInstance().getUuid());
        Date endTime = Calendar.getInstance().getTime();
        for (TaskInstance unfinishedTask : unfinishedTasks) {
            if (unfinishedTask.getUuid() != taskInstance.getUuid()) {
                identityService.removeTodoByTaskInstUuid(unfinishedTask.getUuid());
                // 任务结束
                Date startTime = unfinishedTask.getStartTime();
                unfinishedTask.setTodoUserId(StringUtils.EMPTY);
                unfinishedTask.setTodoUserName(StringUtils.EMPTY);
                unfinishedTask.setDuration(endTime.getTime() - startTime.getTime());
                unfinishedTask.setEndTime(endTime);
                unfinishedTask.setSuspensionState(SuspensionState.SUSPEND.getState());

                this.dao.save(unfinishedTask);

                // 撤回子流程分支环节，同时结束未办结的子流程
                if (Integer.valueOf(2).equals(unfinishedTask.getType())) {
                    taskService.stopByParentTaskInstUuid(unfinishedTask.getUuid(),
                            WorkFlowOperation.getName(WorkFlowOperation.CANCEL), WorkFlowOperation.CANCEL, null, null,
                            "主流程撤销办结!", taskData.getOpinionFiles(taskKey));
                }
            }
        }

        // 2、撤回分支信息
        taskBranchService.cancelBranchTaskByParallelTaskInstUuid(parallelTaskInstUuid);

        // 3、撤回当前流程
        cancelSubmitSerialTask(taskInstance, taskData, topItem, secondItem, null, null);
        // 4、清空非待办环节的ACL已办权限
        List<String> unfinishedTaskInstanceUuids = taskService.getUnfinishedTaskInstanceUuids(flowInstUuid);
        if (CollectionUtils.isNotEmpty(unfinishedTaskInstanceUuids)) {
            List<String> finishedTaskInstanceUuids = taskService.getFinishedTaskInstanceUuids(flowInstUuid);
            finishedTaskInstanceUuids.removeAll(unfinishedTaskInstanceUuids);
            for (String finishedTaskInstanceUuid : finishedTaskInstanceUuids) {
                List<AclTaskEntry> aclSids = aclTaskService.getSid(finishedTaskInstanceUuid, AclPermission.DONE);
                // 复制已办权限
                for (String unfinishedTaskInstanceUuid : unfinishedTaskInstanceUuids) {
                    for (AclTaskEntry aclSid : aclSids) {
                        if (!aclTaskService.hasPermission(unfinishedTaskInstanceUuid, AclPermission.DONE, aclSid.getSid())) {
                            aclTaskService.addPermission(unfinishedTaskInstanceUuid, AclPermission.DONE, aclSid.getSid());
                        }
                    }
                }
                // 删除冗余的已办权限
                aclTaskService.removePermission(finishedTaskInstanceUuid, AclPermission.DONE);
            }
        }
    }

    /**
     * 并行分支提交聚合撤回
     *
     * @param taskInstance
     * @param taskData
     * @param topItem
     * @param secondItem
     */
    private void cancelSubmit2MergeParallelTask(TaskInstance taskInstance, TaskData taskData, TaskActivityItem topItem,
                                                TaskActivityItem secondItem) {
        String parallelTaskInstUuid = secondItem.getParallelTaskInstUuid();
        Boolean isParallel = secondItem.getIsParallel();

        // 1、撤回当前流程
        cancelSubmitSerialTask(taskInstance, taskData, topItem, secondItem, isParallel, parallelTaskInstUuid);
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param topItem
     * @param secondItem
     */
    private void cancelSubmitSerialTask(TaskInstance taskInstance, TaskData taskData, TaskActivityItem topItem,
                                        TaskActivityItem secondItem) {
        String parallelTaskInstUuid = taskInstance.getParallelTaskInstUuid();
        Boolean isParallel = taskInstance.getIsParallel();
        cancelSubmitSerialTask(taskInstance, taskData, topItem, secondItem, isParallel, parallelTaskInstUuid);
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param topItem
     * @param secondItem
     * @param isParallel
     * @param parallelTaskInstUuid
     */
    private void cancelSubmitSerialTask(TaskInstance taskInstance, TaskData taskData, TaskActivityItem topItem,
                                        TaskActivityItem secondItem, Boolean isParallel, String parallelTaskInstUuid) {
        String taskInstUuid = topItem.getTaskInstUuid();
        String userId = taskData.getUserId();
        String fromTaskId = topItem.getTaskId();
        String toTaskId = secondItem.getTaskId();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        // 撤回环节已经不存在
        if (!flowDelegate.existsTaskNode(toTaskId)) {
            throw new WorkFlowException("流程定义已更新，不存在撤回环节，不能进行撤回！");
        }
        // 撤回到当前人
        String assignee = taskData.getUserId();
        // 表单定义UUID
        String formUuid = taskInstance.getFormUuid();
        taskData.setFormUuid(formUuid);
        // 表单数据UUID
        String dataUuid = taskInstance.getDataUuid();
        taskData.setDataUuid(dataUuid);
        // 设置提交到的环节
        taskData.setToTaskId(fromTaskId, toTaskId);
        // 设置撤回的环节
        taskData.setIsCancel(fromTaskId, true);
        // 设置办理人的用户
        // 撤回到协作环节
        if (flowDelegate.getTaskNode(toTaskId) instanceof CollaborationTask) {
            String toTaskInstUuid = secondItem.getTaskInstUuid();
            taskData.resetTaskUsers(toTaskId);
            Set<String> taskOwners = taskService.getTaskOwners(toTaskInstUuid);
            List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(toTaskInstUuid).stream()
                    .filter(identity -> StringUtils.isBlank(identity.getSourceTaskIdentityUuid()) && taskOwners.contains(identity.getUserId())).collect(Collectors.toList());
            List<FlowUserSid> flowUserSids = flowUserJobIdentityService.getFlowUserSids(taskIdentities);
            if (CollectionUtils.isNotEmpty(flowUserSids)) {
                taskData.addTaskUserSids(toTaskId, flowUserSids);
            } else {
                Map<String, List<String>> taskUsers = Maps.newHashMap();
                List<String> taskUserIds = Lists.newArrayList();
                taskUserIds.addAll(taskService.getTaskOwners(toTaskInstUuid));
                taskUsers.put(toTaskId, taskUserIds);
                taskData.setTaskUsers(taskUsers);
            }
            // 环节决策人
            String decisionMakerName = "decisionMakers_" + toTaskId + "_" + toTaskInstUuid;
            FlowInstanceParameter parameter = flowInstanceParameterService.getByFlowInstUuidAndName(taskInstance.getFlowInstance().getUuid(), decisionMakerName);
            if (parameter != null && StringUtils.isNotBlank(parameter.getValue())) {
                if (StringUtils.startsWith(parameter.getValue(), "{")) {
                    Token token = new Token(flowDelegate, null, taskData);
                    String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
                    List<FlowUserSid> decisionMakerSids = Lists.newArrayList();
                    Map<String, String> decisionMakerMap = JsonUtils.json2Object(parameter.getValue(), Map.class);
                    decisionMakerMap.forEach((decisionMakerId, jobId) -> {
                        FlowUserSid decisionMakerSid = new FlowUserSid(decisionMakerId, decisionMakerId);
                        if (StringUtils.isNotBlank(jobId)) {
                            List<String> jobIds = Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue()));
                            List<OrgUserJobDto> orgUserJobDtos = workflowOrgService.listUserJobIdentity(decisionMakerId, orgVersionIds);
                            decisionMakerSid.setOrgUserJobDtos(orgUserJobDtos.stream().filter(orgUserJobDto -> jobIds.contains(orgUserJobDto.getJobId())).collect(Collectors.toList()));
                        }
                        decisionMakerSids.add(decisionMakerSid);
                    });
                    taskData.addTaskDecisionMakerSids(toTaskId, decisionMakerSids);
                } else {
                    taskData.addTaskDecisionMakers(toTaskId, Arrays.asList(StringUtils.split(parameter.getValue(), Separator.SEMICOLON.getValue())));
                }
            }
        } else {
            List<FlowUserSid> flowUserSids = Lists.newArrayList(new FlowUserSid(userId, taskData.getUserName()));
            addUserJobIdentity(userId, toTaskId, flowUserSids, secondItem, taskData, flowDelegate);
//        Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
//        List<String> taskUserIds = new ArrayList<String>();
//        taskUserIds.add(assignee);
//        taskUsers.put(toTaskId, taskUserIds);
            taskData.resetTaskUsers(toTaskId);
            taskData.addTaskUserSids(toTaskId, flowUserSids);
            // taskData.setTaskUsers(taskUsers);
        }

        // 设置流转代码为撤销
        taskData.setTransferCode(taskInstUuid, TransferCode.Cancel.getCode());
        // 设置操作代码为撤销
        taskData.setActionCode(taskInstUuid, getActionCode());

        // 撤回操作身份
        TaskOperationItem taskOperationItem = getTaskOperationItem(userId, secondItem);
        if (taskOperationItem != null) {
            taskData.setUserJobIdentityId(userId, taskInstUuid, taskOperationItem.getOperatorIdentityId());
        }

        // 保存操作历史
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null, null, null,
                taskInstance, taskInstance.getFlowInstance(), taskData);

        // 撤回办理意见
        cancelOpinionText(taskData, secondItem.getOperationItems(), userId, formUuid, dataUuid);

        // 撤回抄送数据
        cancelCopyTo(taskInstance, taskData, secondItem, userId);

        // 撤回流转参数
        CancelTransferParam transferParam = new CancelTransferParam();
        transferParam.setSourceTaskInstUuid(taskInstance.getUuid());
        transferParam.setTargetTaskInstUuid(secondItem.getTaskInstUuid());
        transferParam.setTodoUserId(assignee);

        // 分支信息
        taskData.setPreTaskProperties(toTaskId, FlowConstant.BRANCH.IS_PARALLEL, isParallel);
        taskData.setPreTaskProperties(toTaskId, FlowConstant.BRANCH.PARALLEL_TASK_INST_UUID, parallelTaskInstUuid);

        // 正常提交
        Param sourceParam = new Param(taskInstance, taskData, null, false);
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
        taskExecutor.execute(sourceParam);

        // 撤回消息通知
        if (StringUtils.isNotBlank(taskInstance.getOwner())) {
            List<MessageTemplate> messageTemplates = flowDelegate.getMessageTemplateMap()
                    .get(WorkFlowMessageTemplate.WF_WORK_REVOKE.getType());
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_REVOKE, messageTemplates, taskInstance,
                    taskInstance.getFlowInstance(),
                    Lists.newArrayList(taskInstance.getOwner().split(Separator.SEMICOLON.getValue())),
                    ParticipantType.TodoUser);
        }

        // 撤回子流程环节，同时结束未办结的子流程
        if (Integer.valueOf(2).equals(taskInstance.getType())) {
            String parentFlowInstUuid = taskInstance.getFlowInstance().getUuid();
            taskService.stopByParentFlowInstUuid(parentFlowInstUuid,
                    WorkFlowOperation.getName(WorkFlowOperation.CANCEL), WorkFlowOperation.CANCEL, null, null,
                    "主流程撤销办结!", taskData.getOpinionFiles(taskInstance.getUuid() + userId));
        }
    }

    /**
     * 添加用户身份标识
     *
     * @param userId
     * @param flowUserSids
     * @param secondItem
     * @param taskData
     */
    private void addUserJobIdentity(String userId, String toTaskId, List<FlowUserSid> flowUserSids, TaskActivityItem secondItem,
                                    TaskData taskData, FlowDelegate flowDelegate) {
        JobIdentity jobIdentity = flowDelegate.getJobIdentity(toTaskId);
        if (!jobIdentity.isUserSelectJob()) {
            return;
        }
        TaskOperationItem taskOperationItem = getTaskOperationItem(userId, secondItem);
        if (taskOperationItem == null) {
            return;
        }
        String extraInfo = taskOperationItem.getExtraInfo();
        String identityIdPath = null;
        if (StringUtils.isNotBlank(extraInfo)) {
            TaskIdentity historyTaskIdentity = JsonUtils.json2Object(extraInfo, TaskIdentity.class);
            if (historyTaskIdentity != null && (!StringUtils.equals(userId, historyTaskIdentity.getCreator())
                    || isCancelTransfer(historyTaskIdentity.getTaskInstUuid()))) {
                identityIdPath = historyTaskIdentity.getIdentityIdPath();
            }
        }
        if (StringUtils.isNotBlank(identityIdPath)) {
            Token token = new Token(flowDelegate, null, taskData);
            String[] idPaths = StringUtils.split(identityIdPath, Separator.SEMICOLON.getValue());
            for (String idPath : idPaths) {
                flowUserJobIdentityService.addUnitUserJobIdentity(flowUserSids, idPath, taskOperationItem.getTaskId(), token, ParticipantType.TodoUser);
            }
        }
    }

    /**
     * @param taskInstUuid
     * @return
     */
    private boolean isCancelTransfer(String taskInstUuid) {
        TaskActivity taskActivity = taskActivityService.getByTaskInstUuid(taskInstUuid);
        return TransferCode.Cancel.getCode().equals(taskActivity.getTransferCode());
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param secondItem
     * @param userId
     */
    private void cancelCopyTo(TaskInstance taskInstance, TaskData taskData, TaskActivityItem secondItem, String userId) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        if (!BooleanUtils.isTrue(workFlowSettings.isCancelCopyTo())) {
            return;
        }

        String currentTaskInstUuid = taskInstance.getUuid();
        String taskInstUuid = secondItem.getTaskInstUuid();
        List<TaskOperation> taskOperations = taskOperationService.listByTaskInstUuids(Lists.newArrayList(taskInstUuid, currentTaskInstUuid));
        Set<String> copyToUserIds = taskOperations.stream().filter(taskOperation -> {
                    // 当前环节抄送
                    if (StringUtils.equals(currentTaskInstUuid, taskOperation.getTaskInstUuid()) && ActionCode.COPY_TO.getCode().equals(taskOperation.getActionCode())) {
                        return StringUtils.isNotBlank(taskOperation.getCopyUserId());
                    } else if (StringUtils.equals(taskInstUuid, taskOperation.getTaskInstUuid()) && workFlowSettings.isCancelAutoCopyTo()) {
                        // 提交时自动抄送
                        return StringUtils.isNotBlank(taskOperation.getCopyUserId());
                    }
                    return false;
                }).flatMap(taskOperation -> Arrays.asList(StringUtils.split(taskOperation.getCopyUserId(), Separator.SEMICOLON.getValue())).stream())
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(copyToUserIds)) {
            copyToUserIds.forEach(copyToUserId -> {
                aclTaskService.removePermission(currentTaskInstUuid, Lists.newArrayList(AclPermission.UNREAD, AclPermission.FLAG_READ), copyToUserId);
            });
        }
    }

    /**
     * @param userId
     * @param operationItems
     * @return
     */
    private TaskOperationItem getAllowedCancelOperationItem(String userId, List<TaskOperationItem> operationItems) {
        List<TaskOperationItem> items = new ArrayList<TaskOperationItem>();
        items.addAll(operationItems);
        TaskActivityStackFactary.sortTaskOperationItem(items, false);

        TaskOperationItem item = null;
        for (TaskOperationItem taskOperationItem : items) {
            if (userId.equals(taskOperationItem.getOperator())) {
                item = taskOperationItem;
                break;
            }
            // 委托提交
            Integer actionCode = taskOperationItem.getActionCode();
            if (ActionCode.DELEGATION_SUBMIT.getCode().equals(actionCode)) {
                String taskInstUuid = taskOperationItem.getTaskInstUuid();
                IdentityService identityService = ApplicationContextHolder.getBean(IdentityService.class);
                List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuidAndOwnerId(taskInstUuid, userId);
                if (!taskIdentities.isEmpty()) {
                    item = taskOperationItem;
                    break;
                }
            }
        }
        return item;
    }

    /**
     * @param topItem
     */
    private void cancelRollbackTask(TaskInstance taskInstance, TaskData taskData, TaskActivityItem topItem, List<TaskOperation> allTaskOperations) {
        String taskInstUuid = topItem.getTaskInstUuid();
        String userId = taskData.getUserId();
        String fromTaskId = topItem.getTaskId();
        String toTaskId = topItem.getPreTaskId();
        String preTaskInstUuid = topItem.getPreTaskInstUuid();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        // 表单定义UUID
        String formUuid = taskInstance.getFormUuid();
        taskData.setFormUuid(formUuid);
        // 表单数据UUID
        String dataUuid = taskInstance.getDataUuid();
        taskData.setDataUuid(dataUuid);
        // 设置提交到的环节
        taskData.setToTaskId(fromTaskId, toTaskId);
        // 设置办理人的用户
        Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
        List<String> taskUserIds = new ArrayList<String>();
        boolean setTaskUsers = false;
        if (flowDelegate.isByOrder(toTaskId)) {
            List<TaskIdentity> taskIdentities = Lists.newArrayList();
            List<TaskIdentity> identities = identityService.getByTaskInstUuid(topItem.getPreTaskInstUuid());
            TaskIdentity userIdentity = identities.stream().filter(identity -> StringUtils.equals(userId, identity.getUserId())).findFirst().orElse(null);
            if (userIdentity != null) {
                identities.stream().filter(identity -> identity.getSortOrder() != null).sorted((o1, o2) -> {
                    return o1.getSortOrder().compareTo(o2.getSortOrder());
                }).forEach(identity -> {
                    if (identity.getSortOrder() >= userIdentity.getSortOrder()) {
                        taskIdentities.add(identity);
                        taskUserIds.add(identity.getUserId());
                    }
                });
                if (CollectionUtils.isNotEmpty(taskIdentities)) {
                    List<FlowUserSid> flowUserSids = flowUserJobIdentityService.getFlowUserSids(taskIdentities);
                    taskData.addTaskUserSids(toTaskId, flowUserSids);
                } else {
                    taskUsers.put(toTaskId, taskUserIds);
                }
                taskData.setIsByOrder(toTaskId, true);
                taskData.setTaskUsers(taskUsers);
                setTaskUsers = true;
            }
        }
        if (!setTaskUsers) {
            Set<String> taskOwners = taskService.getTaskOwners(preTaskInstUuid);
            List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(preTaskInstUuid).stream()
                    .filter(identity -> StringUtils.isBlank(identity.getSourceTaskIdentityUuid()) && taskOwners.contains(identity.getUserId())).collect(Collectors.toList());
            List<FlowUserSid> flowUserSids = flowUserJobIdentityService.getFlowUserSids(taskIdentities);
            if (CollectionUtils.isNotEmpty(flowUserSids)) {
                taskData.addTaskUserSids(toTaskId, flowUserSids);
            } else {
                taskUserIds.addAll(taskOwners);
                taskUsers.put(toTaskId, taskUserIds);
                taskData.setTaskUsers(taskUsers);
            }
        }
        // 设置撤回的环节
        taskData.setIsCancel(fromTaskId, true);

        // 设置流转代码为撤销
        taskData.setTransferCode(taskInstUuid, TransferCode.Cancel.getCode());
        // 设置操作代码为撤销
        taskData.setActionCode(taskInstUuid, getActionCode());

        // 撤回操作身份
        TaskOperationItem taskOperationItem = getTaskOperationItem(userId, topItem);
        if (taskOperationItem != null) {
            taskData.setUserJobIdentityId(userId, taskInstUuid, taskOperationItem.getOperatorIdentityId());
        } else {
            Collections.sort(allTaskOperations, IdEntityComparators.CREATE_TIME_DESC);
            TaskOperation rollbackTaskOperation = allTaskOperations.stream().filter(taskOperation -> StringUtils.equals(taskOperation.getAssignee(), userId)
                            && (ActionCode.ROLLBACK.getCode().equals(taskOperation.getActionCode())
                            || ActionCode.DIRECT_ROLLBACK.getCode().equals(taskOperation.getActionCode())))
                    .findFirst().orElse(null);
            if (rollbackTaskOperation != null) {
                taskData.setUserJobIdentityId(userId, taskInstUuid, rollbackTaskOperation.getOperatorIdentityId());
            }
        }
        // 保存操作历史
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null, null, null,
                taskInstance, taskInstance.getFlowInstance(), taskData);

        // 撤回办理意见
        List<TaskOperationItem> operationItems = TaskActivityStackFactary.convert2OperationItem(allTaskOperations,
                topItem.getPreTaskInstUuid());
        cancelOpinionText(taskData, operationItems, userId, formUuid, dataUuid);

        // 正常提交
        Param sourceParam = new Param(taskInstance, taskData, null, false);
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
        taskExecutor.execute(sourceParam);

        // 还原当前撤回环节实例的并行标识
        TaskInstance preTaskInstance = taskService.getTask(preTaskInstUuid);
        List<TaskInstance> unfinishedTasks = taskService.getUnfinishedTasks(topItem.getFlowInstUuid());
        for (TaskInstance unfinishedTask : unfinishedTasks) {
            if (StringUtils.equals(preTaskInstance.getId(), unfinishedTask.getId())) {
                unfinishedTask.setIsParallel(preTaskInstance.getIsParallel());
                unfinishedTask.setParallelTaskInstUuid(preTaskInstance.getParallelTaskInstUuid());
                taskService.save(unfinishedTask);
            }
        }

        // 撤回并联退回到外部
        // 获取并行的待办标识
        List<TaskIdentity> taskIdentities = getParallelTaskIdentities(preTaskInstUuid, allTaskOperations);
        if (CollectionUtils.isNotEmpty(taskIdentities)) {
            // 还原并行任务
            identityService.restore(taskIdentities);
            Set<String> restoredTaskInstUuidSet = Sets.newHashSet();
            for (TaskIdentity taskIdentity : taskIdentities) {
                if (Integer.valueOf(SuspensionState.NORMAL.getState()).equals(taskIdentity.getSuspensionState())) {
                    String parallelTaskUuid = identityService.get(taskIdentity.getUuid()).getTaskInstUuid();
                    if (restoredTaskInstUuidSet.contains(parallelTaskUuid)) {
                        continue;
                    }
                    TaskInstance parallelTaskInstance = taskService.get(parallelTaskUuid);
                    parallelTaskInstance.setEndTime(null);
                    parallelTaskInstance.setSuspensionState(SuspensionState.NORMAL.getState());
                    taskService.save(parallelTaskInstance);
                }
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param preTaskInstUuid
     * @param allTaskOperations
     * @return
     */
    private List<TaskIdentity> getParallelTaskIdentities(String preTaskInstUuid,
                                                         List<TaskOperation> allTaskOperations) {
        List<TaskIdentity> parallelTaskIdentities = Lists.newArrayList();
        List<TaskOperation> preTaskOperations = Lists.newArrayList();
        // 获取进行退回的操作
        for (TaskOperation taskOperation : allTaskOperations) {
            String optTaskInstUuid = taskOperation.getTaskInstUuid();
            String optActionType = taskOperation.getActionType();
            if (StringUtils.equals(preTaskInstUuid, optTaskInstUuid)
                    && (WorkFlowOperation.ROLLBACK.equals(optActionType)
                    || WorkFlowOperation.DIRECT_ROLLBACK.equals(optActionType))) {
                preTaskOperations.add(taskOperation);
            }
        }
        // 获取操作存储的并行待办信息
        if (CollectionUtils.isNotEmpty(preTaskOperations)) {
            TaskOperation taskOperation = preTaskOperations.get(preTaskOperations.size() - 1);
            if (StringUtils.isNotBlank(taskOperation.getExtraInfo())) {
                TaskIdentity optTaskIdentity = JsonUtils.json2Object(taskOperation.getExtraInfo(), TaskIdentity.class);
                List<Map<String, Object>> identities = optTaskIdentity.getParallelTaskIdentities();
                if (CollectionUtils.isNotEmpty(identities)) {
                    for (Map<String, Object> map : identities) {
                        parallelTaskIdentities
                                .add(JsonUtils.json2Object(JsonUtils.object2Json(map), TaskIdentity.class));
                    }
                }
            }
        }
        return parallelTaskIdentities;
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param taskData
     * @param topItem
     * @param secondItem
     */
    private void cancelTransferSubmitTask(TaskInstance taskInstance, TaskData taskData, TaskActivityItem topItem,
                                          TaskActivityItem secondItem) {
        String taskInstUuid = topItem.getTaskInstUuid();
        String userId = taskData.getUserId();
        String fromTaskId = topItem.getTaskId();
        String toTaskId = topItem.getPreTaskId();
        String assignee = topItem.getCreator();
        // 表单定义UUID
        String formUuid = taskInstance.getFormUuid();
        taskData.setFormUuid(formUuid);
        // 表单数据UUID
        String dataUuid = taskInstance.getDataUuid();
        taskData.setDataUuid(dataUuid);
        // 设置提交到的环节
        taskData.setToTaskId(fromTaskId, toTaskId);
        // 设置办理人的用户
        Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
        List<String> taskUserIds = new ArrayList<String>();
        taskUserIds.add(assignee);
        taskUsers.put(toTaskId, taskUserIds);
        // 设置撤回的环节
        taskData.setIsCancel(fromTaskId, true);
        // 设置提交到的环节
        taskData.setTaskUsers(taskUsers);

        // 设置流转代码为撤销
        taskData.setTransferCode(taskInstUuid, TransferCode.Cancel.getCode());
        // 设置操作代码为撤销
        taskData.setActionCode(taskInstUuid, getActionCode());

        // 保存操作历史
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null, null, null,
                taskInstance, taskInstance.getFlowInstance(), taskData);

        // 撤回办理意见
        cancelOpinionText(taskData, secondItem.getOperationItems(), userId, formUuid, dataUuid);

        // 正常提交
        Param sourceParam = new Param(taskInstance, taskData, null, false);
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
        taskExecutor.execute(sourceParam);

        // 撤回消息通知
        if (StringUtils.isNotBlank(taskInstance.getOwner())) {
            List<MessageTemplate> messageTemplates = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition())
                    .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_REVOKE.getType());
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_REVOKE, messageTemplates, taskInstance,
                    taskInstance.getFlowInstance(),
                    Lists.newArrayList(taskInstance.getOwner().split(Separator.SEMICOLON.getValue())),
                    ParticipantType.TodoUser);
        }

        // 撤回子流程环节，同时结束未办结的子流程
        if (Integer.valueOf(2).equals(taskInstance.getType())) {
            String parentFlowInstUuid = taskInstance.getFlowInstance().getUuid();
            taskService.stopByParentFlowInstUuid(parentFlowInstUuid,
                    WorkFlowOperation.getName(WorkFlowOperation.CANCEL), WorkFlowOperation.CANCEL, null, null,
                    "主流程撤销办结!", taskData.getOpinionFiles(taskInstance.getUuid() + userId));
        }
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param topItem
     * @param secondItem
     */
    private void cancelDelegationSubmitTask(TaskInstance taskInstance, TaskData taskData, TaskActivityItem topItem,
                                            TaskActivityItem secondItem) {
        String taskInstUuid = topItem.getTaskInstUuid();
        String userId = taskData.getUserId();
        String fromTaskId = topItem.getTaskId();
        String toTaskId = topItem.getPreTaskId();
        // 委托撤回到当前用户
        String assignee = taskData.getUserId();
        // 表单定义UUID
        String formUuid = taskInstance.getFormUuid();
        taskData.setFormUuid(formUuid);
        // 表单数据UUID
        String dataUuid = taskInstance.getDataUuid();
        taskData.setDataUuid(dataUuid);
        // 设置提交到的环节
        taskData.setToTaskId(fromTaskId, toTaskId);
        // 设置办理人的用户
        Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
        List<String> taskUserIds = new ArrayList<String>();
        taskUserIds.add(assignee);
        taskUsers.put(toTaskId, taskUserIds);
        // 设置撤回的环节
        taskData.setIsCancel(fromTaskId, true);
        // 设置提交到的环节
        taskData.setTaskUsers(taskUsers);

        // 设置流转代码为撤销
        taskData.setTransferCode(taskInstUuid, TransferCode.Cancel.getCode());
        // 设置操作代码为撤销
        taskData.setActionCode(taskInstUuid, getActionCode());

        // 保存操作历史
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, null, null, null, userId, null, null, null, null,
                taskInstance, taskInstance.getFlowInstance(), taskData);

        // 撤回办理意见
        cancelOpinionText(taskData, secondItem.getOperationItems(), userId, formUuid, dataUuid);

        // 正常提交
        Param sourceParam = new Param(taskInstance, taskData, null, false);
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
        taskExecutor.execute(sourceParam);

        // 撤回消息通知
        if (StringUtils.isNotBlank(taskInstance.getOwner())) {
            List<MessageTemplate> messageTemplates = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition())
                    .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_REVOKE.getType());
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_REVOKE, messageTemplates, taskInstance,
                    taskInstance.getFlowInstance(),
                    Lists.newArrayList(taskInstance.getOwner().split(Separator.SEMICOLON.getValue())),
                    ParticipantType.TodoUser);
        }

        // 撤回子流程环节，同时结束未办结的子流程
        if (Integer.valueOf(2).equals(taskInstance.getType())) {
            String parentFlowInstUuid = taskInstance.getFlowInstance().getUuid();
            taskService.stopByParentFlowInstUuid(parentFlowInstUuid,
                    WorkFlowOperation.getName(WorkFlowOperation.CANCEL), WorkFlowOperation.CANCEL, null, null,
                    "主流程撤销办结!", taskData.getOpinionFiles(taskInstance.getUuid() + userId));
        }
    }

    /**
     * 撤回办理意见
     *
     * @param taskData
     * @param operationItems
     * @param userId
     * @param formUuid
     * @param dataUuid
     */
    public void cancelOpinionText(TaskData taskData, List<TaskOperationItem> operationItems, String userId,
                                  String formUuid, String dataUuid) {
        // 撤回办理意见
        TaskOperationItem operationItem = getAllowedCancelOperationItem(userId, operationItems);
        if (operationItem != null) {
            List<TaskFormOpinionLog> taskFormOpinionLogs = taskFormOpinionLogService
                    .getByTaskOperationUuid(operationItem.getUuid());
            DyFormData dyFormData = null;
            if (!taskFormOpinionLogs.isEmpty()) {
                dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            }
            boolean updateDyformData = false;
            for (TaskFormOpinionLog taskFormOpinionLog : taskFormOpinionLogs) {
                String fieldName = taskFormOpinionLog.getFieldName();
                String taskFormOpinionUuid = taskFormOpinionLog.getTaskFormOpinionUuid();
                String newContent = "";
                TaskFormOpinion taskFormOpinion = null;
                if (StringUtils.isNotBlank(taskFormOpinionUuid)) {
                    taskFormOpinion = taskFormOpinionService.get(taskFormOpinionUuid);
                    String allContent = taskFormOpinion.getContent();
                    String lastContent = taskFormOpinionLog.getContent();
                    if (StringUtils.isNotBlank(allContent) && StringUtils.isNotBlank(lastContent)
                            && (allContent.lastIndexOf(lastContent) != -1)) {
                        int startIndex = allContent.lastIndexOf(lastContent);
                        int endIndex = startIndex + lastContent.length();
                        int len = allContent.length();
                        newContent = allContent.substring(0, startIndex) + allContent.substring(endIndex, len);
                        taskFormOpinion.setContent(newContent);
                        taskFormOpinionService.save(taskFormOpinion);
                        dyFormData.setFieldValue(fieldName, newContent);
                        updateDyformData = true;

                        // 撤回意见
                        taskFormOpinionLog.setStatus(WorkFlowSuspensionState.Delete);
                        taskFormOpinionLogService.save(taskFormOpinionLog);
                    }
                } else {
                    if (!dyFormData.isValueBlank(fieldName)) {
                        Object fieldValue = dyFormData.getFieldValue(fieldName);
                        if (fieldValue.toString().equals(taskFormOpinionLog.getContent())) {
                            dyFormData.setFieldValue(fieldName, newContent);
                            updateDyformData = true;

                            // 撤回意见
                            taskFormOpinionLog.setStatus(WorkFlowSuspensionState.Delete);
                            taskFormOpinionLogService.save(taskFormOpinionLog);
                        }
                    }
                }
            }
            if (updateDyformData) {
                dyFormFacade.saveFormData(dyFormData);
                taskData.setDyFormData(dataUuid, dyFormData);
            }
        }
    }
}

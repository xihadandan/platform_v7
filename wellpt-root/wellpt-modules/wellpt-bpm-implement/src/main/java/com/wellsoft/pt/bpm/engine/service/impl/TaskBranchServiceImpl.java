/*
 * @(#)2019年3月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.dao.TaskBranchDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.ColumnElement;
import com.wellsoft.pt.bpm.engine.element.ParallelGatewayElement;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicColumn;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.query.FlowShareDataQueryItem;
import com.wellsoft.pt.bpm.engine.query.api.TaskInfoDistributionQueryItem;
import com.wellsoft.pt.bpm.engine.query.api.TaskOperationQueryItem;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.TitleExpressionUtils;
import com.wellsoft.pt.bpm.engine.util.UndertakeSituationUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年3月7日.1	zhulh		2019年3月7日		Create
 * </pre>
 * @date 2019年3月7日
 */
@Service
public class TaskBranchServiceImpl extends AbstractJpaServiceImpl<TaskBranch, TaskBranchDao, String> implements
        TaskBranchService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskFormAttachmentLogService taskFormAttachmentLogService;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private TaskInfoDistributionService taskInfoDistributionService;

    @Autowired
    private IdentityResolverStrategy identityResolverStrategy;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#forkBranchTask(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    @Transactional
    public void createBranchTask(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        String toTaskId = taskInstance.getId();
        String fromTaskId = taskData.getPreTaskId(toTaskId);
        Direction direction = taskData.getTaskDirection(fromTaskId, toTaskId);
        // 流程实例UUID
        String flowInstUuid = flowInstance.getUuid();
        // 发起并行任务的环节实例UUID
        String parallelTaskInstUuid = taskData.getPreTaskInstUuid(toTaskId);
        // // 发起并行任务的环节实例ID
        String parallelTaskId = taskData.getPreTaskId(toTaskId);
        // 并行任务的第一个环节实例UUID
        String initTaskInstUuid = taskInstance.getUuid();
        // 当前并行分支所在的环节实例UUID
        String currentTaskInstUuid = initTaskInstUuid;
        // 聚合的环节实例UUID
        String joinTaskInstUuid = null;
        // 办理对象ID
        String todoId = taskData.getTaskTodoId(toTaskId);
        if (StringUtils.isBlank(todoId)) {
            todoId = taskInstance.getTodoUserId();
        }
        // 办理对象名称
        String todoName = taskData.getTaskTodoName(toTaskId);
        if (StringUtils.isBlank(todoName)) {
            todoName = taskInstance.getTodoUserName();
        }
        // 0、静态分支;1、独立分支;2、主办分支;3、协办分支
        String branchType = direction != null ? direction.getBranchInstanceType() : FlowConstant.BRANCH_TYPE.STATIC;
        if (StringUtils.equals(direction.getBranchMode(), FlowConstant.BRANCH_MODE.STATIC)) {
            branchType = FlowConstant.BRANCH_TYPE.STATIC;
        }
        // 是否合并
        Boolean isMerge = (direction != null && direction.isIndependentBranch()) ? false : true;
        Boolean isMergedBranchTaskFinished = (Boolean) taskData.getPreTaskProperties(toTaskId,
                FlowConstant.BRANCH.IS_MERGED_BRANCH_TASK_FINISHED);
        // 需要合并的分支都已经办结，新增的分支设置为不需要合并
        if (Boolean.TRUE.equals(isMergedBranchTaskFinished)) {
            isMerge = false;
        }
        // 是否共享
        Boolean isShare = (direction != null && direction.isShareBranch()) ? direction.isShareBranch() : false;
        // 标记分支是否已经完成
        Boolean completed = false;
        // 完成状态 0运行中、1正常结束、2终止、3撤销、4退回主流程
        Integer completionState = TaskBranch.STATUS_NORMAL;
        // 排序
        Integer sortOrder = taskData.getTaskForkingOrder(toTaskId);

        TaskBranch taskBranch = new TaskBranch();
        taskBranch.setFlowInstUuid(flowInstUuid);
        taskBranch.setParallelTaskInstUuid(parallelTaskInstUuid);
        taskBranch.setParallelTaskId(parallelTaskId);
        taskBranch.setInitTaskInstUuid(initTaskInstUuid);
        taskBranch.setCurrentTaskInstUuid(currentTaskInstUuid);
        taskBranch.setJoinTaskInstUuid(joinTaskInstUuid);
        taskBranch.setTodoId(todoId);
        taskBranch.setTodoName(todoName);
        taskBranch.setBranchType(branchType);
        taskBranch.setIsMerge(isMerge);
        taskBranch.setIsShare(isShare);
        taskBranch.setCompleted(completed);
        taskBranch.setCompletionState(completionState);
        taskBranch.setSortOrder(sortOrder);
        this.dao.save(taskBranch);

        // 添加日志
        if (taskData.isLogAddBranchTask(parallelTaskId)) {
            String actionName = taskData.getAddSubflowActionName(parallelTaskId);
            String action = StringUtils.isNotBlank(actionName) ? actionName : WorkFlowOperation
                    .getName(WorkFlowOperation.ADD_SUB_FLOW);
            List<String> taskUsers = Arrays.asList(StringUtils.split(todoId, Separator.SEMICOLON.getValue()));
            taskOperationService.saveTaskOperation(action, ActionCode.ADD_SUB_FLOW.getCode(),
                    WorkFlowOperation.ADD_SUB_FLOW, null, null, null, taskData.getUserId(), taskUsers, null, null,
                    null, taskInstance, flowInstance, taskData);
        }

        // 添加跟进人员
        addBranchTaskMonitors(parallelTaskId, taskData);
    }

    /**
     * 添加跟进人员
     *
     * @param parallelTaskId
     * @param taskData
     */
    @Override
    @Transactional
    public void addBranchTaskMonitors(String parallelTaskId, TaskData taskData) {
        Token token = taskData.getToken();
        TaskElement taskElement = token.getFlowDelegate().getFlow().getTask(parallelTaskId);
        ParallelGatewayElement gatewayElement = taskElement.getParallelGateway();
        List<UserUnitElement> subTaskMonitorElements = gatewayElement.getBranchTaskMonitors();
        if (CollectionUtils.isEmpty(subTaskMonitorElements)) {
            return;
        }
        String key = "addBranchTaskMonitors_" + parallelTaskId;
        if (BooleanUtils.isTrue((Boolean) taskData.get(key))) {
            return;
        }

        Node fromNode = token.getFlowDelegate().getTaskNode(parallelTaskId);
        List<FlowUserSid> followUpUserSids = identityResolverStrategy.resolve(fromNode,
                token, subTaskMonitorElements, ParticipantType.MonitorUser);
        List<String> followUpUserIds = IdentityResolverStrategy.resolveAsOrgIds(followUpUserSids);
        taskData.put(key, true);
        if (CollectionUtils.isEmpty(followUpUserIds)) {
            return;
        }

        FlowInstanceParameter example = new FlowInstanceParameter();
        String flowInstUuid = token.getFlowInstance().getUuid();
        String name = FlowConstant.SUB_FLOW.KEY_FOLLOW_UP_USERS + Separator.UNDERLINE.getValue() + parallelTaskId;
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
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#changeCurrentTask(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    @Transactional
    public void changeCurrentTask(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        String toTaskId = taskInstance.getId();
        String parallelTaskInstUuid = taskInstance.getParallelTaskInstUuid();
        String preTaskInstUuid = taskData.getPreTaskInstUuid(toTaskId);
        String currentTaskInstUuid = taskInstance.getUuid();
        String preTaskId = taskData.getPreTaskId(toTaskId);
        // 如果是撤回操作，确保撤回的环节实例是分支环节，如果不是取撤回环节的前环节实例UUID
        boolean clearJoinTaskInstUuid = false;
        if (taskData.isCancel(preTaskId)) {
            TaskInstance cancelTaskInstance = taskService.getTask(preTaskInstUuid);
            if (!Boolean.TRUE.equals(cancelTaskInstance.getIsParallel())) {
                TaskActivity taskActivity = taskActivityService.getByTaskInstUuid(cancelTaskInstance.getUuid());
                if (StringUtils.isNotBlank(taskActivity.getPreTaskInstUuid())) {
                    preTaskInstUuid = taskActivity.getPreTaskInstUuid();
                    clearJoinTaskInstUuid = true;
                }
            }
        }
        String hql = "update TaskBranch t set t.currentTaskInstUuid = :currentTaskInstUuid, t.completed = :completed, t.completionState = :completionState where t.parallelTaskInstUuid = :parallelTaskInstUuid and t.currentTaskInstUuid = :preTaskInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        values.put("preTaskInstUuid", preTaskInstUuid);
        values.put("currentTaskInstUuid", currentTaskInstUuid);
        values.put("completed", false);
        values.put("completionState", TaskBranch.STATUS_NORMAL);
        int updateCount = this.dao.updateByHQL(hql, values);

        // 从聚合环节撤回，清空聚合的环节实例UUID
        if (clearJoinTaskInstUuid && updateCount > 0) {
            hql = "update TaskBranch t set t.joinTaskInstUuid = null where t.parallelTaskInstUuid = :parallelTaskInstUuid and t.isMerge = true";
            this.dao.updateByHQL(hql, values);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#syncBranchTask(com.wellsoft.pt.bpm.engine.entity.TaskInstance, java.lang.String)
     */
    @Override
    public void syncBranchTask(TaskInstance taskInstance, String relatedTaskBranchUuid) {
        TaskBranch taskBranch = this.dao.getOne(relatedTaskBranchUuid);
        taskBranch.setCurrentTaskInstUuid(taskInstance.getUuid());
        taskBranch.setCompleted(false);
        taskBranch.setCompletionState(TaskBranch.STATUS_NORMAL);
        this.dao.save(taskBranch);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#joinBranchTask(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    @Transactional
    public void joinBranchTask(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        String joinTaskInstUuid = taskInstance.getUuid();
        String parallelTaskInstUuid = (String) taskData.getPreTaskProperties(taskInstance.getId(),
                FlowConstant.BRANCH.PARALLEL_TASK_INST_UUID);
        String hql = "update TaskBranch t set t.joinTaskInstUuid = :joinTaskInstUuid where t.parallelTaskInstUuid = :parallelTaskInstUuid and t.isMerge = true";
        Map<String, Object> values = Maps.newHashMap();
        values.put("joinTaskInstUuid", joinTaskInstUuid);
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#stopBranchTaskByCurrentTaskInstUuid(java.lang.String)
     */
    @Override
    public void stopBranchTaskByCurrentTaskInstUuid(String currentTaskInstUuid) {
        String hql = "update TaskBranch t set t.completed = :completed, t.completionState = :completionState where t.currentTaskInstUuid = :currentTaskInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("currentTaskInstUuid", currentTaskInstUuid);
        values.put("completed", true);
        values.put("completionState", TaskBranch.STATUS_STOP);
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#restoreBranchTaskByCurrentTaskInstUuid(java.lang.String)
     */
    @Override
    public void restoreBranchTaskByCurrentTaskInstUuid(String currentTaskInstUuid) {
        String hql = "update TaskBranch t set t.completed = :completed, t.completionState = :completionState where t.currentTaskInstUuid = :currentTaskInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("currentTaskInstUuid", currentTaskInstUuid);
        values.put("completed", false);
        values.put("completionState", TaskBranch.STATUS_NORMAL);
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#getByCurrentTaskInstUuid(java.lang.String)
     */
    @Override
    public TaskBranch getByCurrentTaskInstUuid(String currentTaskInstUuid) {
        List<TaskBranch> taskBranchs = this.dao.listByFieldEqValue("currentTaskInstUuid", currentTaskInstUuid);
        if (taskBranchs.size() == 1) {
            return taskBranchs.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#getByCurrentTaskInstUuids(java.util.Collection)
     */
    @Override
    public List<TaskBranch> getByCurrentTaskInstUuids(Collection<String> currentTaskInstUuids) {
        List<Object> inValues = Lists.newArrayList();
        inValues.addAll(currentTaskInstUuids);
        List<TaskBranch> taskBranchs = this.dao.listByFieldInValues("currentTaskInstUuid", inValues);
        return taskBranchs;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskBranch> getByFlowInstUuid(String flowInstUuid) {
        return this.dao.listByFieldEqValue("flowInstUuid", flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#cancelBranchTaskByParallelTaskInstUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void cancelBranchTaskByParallelTaskInstUuid(String parallelTaskInstUuid) {
        String hql = "update TaskBranch t set t.completed = :completed, t.completionState = :completionState where t.parallelTaskInstUuid = :parallelTaskInstUuid and t.completed = false";
        Map<String, Object> values = Maps.newHashMap();
        values.put("completed", true);
        values.put("completionState", TaskBranch.STATUS_CANCEL);
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#rollbackBranchTaskByParallelTaskInstUuid(java.lang.String)
     */
    @Override
    public void rollbackBranchTaskByParallelTaskInstUuid(String parallelTaskInstUuid) {
        String hql = "update TaskBranch t set t.completed = :completed, t.completionState = :completionState where t.parallelTaskInstUuid = :parallelTaskInstUuid and t.completed = false";
        Map<String, Object> values = Maps.newHashMap();
        values.put("completed", true);
        values.put("completionState", TaskBranch.STATUS_ROLLBACK);
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#completeBranchTask(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    @Transactional
    public void completeBranchTask(TaskInstance currentTaskInstance) {
        String parallelTaskInstUuid = currentTaskInstance.getParallelTaskInstUuid();
        String hql = "update TaskBranch t set t.completed = :completed, t.completionState = :completionState where t.parallelTaskInstUuid = :parallelTaskInstUuid and t.currentTaskInstUuid = :currentTaskInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("completed", true);
        values.put("completionState", TaskBranch.STATUS_COMPLETED);
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        values.put("currentTaskInstUuid", currentTaskInstance.getUuid());
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#isTheLastMergeBranch(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public boolean isTheLastMergeBranch(TaskInstance currentTaskInstance) {
        return countUnfinishedMergeBranch(currentTaskInstance) == 1;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#countUnfinishedMergeBranch(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public long countUnfinishedMergeBranch(TaskInstance currentTaskInstance) {
        String hql = "from TaskBranch t where t.parallelTaskInstUuid = :parallelTaskInstUuid and t.isMerge = :isMerge and t.completed = :completed";
        Map<String, Object> values = Maps.newHashMap();
        values.put("isMerge", true);
        values.put("completed", false);
        values.put("parallelTaskInstUuid", currentTaskInstance.getParallelTaskInstUuid());
        return this.dao.countByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#countUnfinishedBranchByFlowDefUuidAndTaskIds(java.lang.String, java.util.List)
     */
    @Override
    public long countUnfinishedBranchByFlowDefUuidAndTaskIds(String flowDefUuid, List<String> parallelTaskIds) {
        String hql = "from TaskBranch t1 where t1.parallelTaskId in(:parallelTaskIds) and t1.completed = :completed and exists(select t2.uuid from FlowInstance t2 where t2.flowDefinition.uuid = :flowDefUuid and t2.endTime is null and t1.flowInstUuid = t2.uuid)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("completed", false);
        values.put("flowDefUuid", flowDefUuid);
        values.put("parallelTaskIds", parallelTaskIds);
        return this.dao.countByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#isBranchTaskCompleted(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public boolean isBranchTaskCompleted(TaskInstance currentTaskInstance) {
        return isBranchTaskCompletedByParallelTaskInstUuid(currentTaskInstance.getParallelTaskInstUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#isBranchTaskCompletedByParallelTaskInstUuid(java.lang.String)
     */
    @Override
    public boolean isBranchTaskCompletedByParallelTaskInstUuid(String parallelTaskInstUuid) {
        String hql = "from TaskBranch t where t.parallelTaskInstUuid = :parallelTaskInstUuid and t.isMerge = :isMerge and t.completed = :completed";
        Map<String, Object> values = Maps.newHashMap();
        values.put("isMerge", true);
        values.put("completed", false);
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        Long count = this.dao.countByHQL(hql, values);
        return count.intValue() == 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#getBranchTaskData(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance)
     */
    @Override
    public BranchTaskData getBranchTaskData(TaskInstance taskInstance, FlowInstance flowInstance) {
        String flowInstUuid = flowInstance.getUuid();
        BranchTaskData branchTaskData = null;

        TaskBranch example = new TaskBranch();
        example.setFlowInstUuid(flowInstUuid);
        long branchCount = this.dao.countByEntity(example);
        if (branchCount == 0) {
            return branchTaskData;
        }

        List<TaskBranch> taskBranchs = getByFlowInstUuid(flowInstUuid);
        Set<String> parallelTaskIds = Sets.newHashSet();
        for (TaskBranch taskBranch : taskBranchs) {
            parallelTaskIds.add(taskBranch.getParallelTaskId());
        }

        // 不支持多并行分支环节展示
        if (parallelTaskIds.size() > 1) {
            return branchTaskData;
        }

        branchTaskData = new BranchTaskData();

        String branchTaskId = parallelTaskIds.iterator().next();

        // 获取分支环节
        FlowDefinition flowDefinition = taskInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
        ParallelGatewayElement parallelGatewayElement = getBranchTaskParallelGatewayElement(flowDelegate, branchTaskId);

        branchTaskData.setFlowInstUuid(flowInstUuid);
        branchTaskData.setTaskInstUuid(taskInstance.getUuid());
        branchTaskData.setBusinessType(parallelGatewayElement.getBusinessType());
        branchTaskData.setBusinessRole(parallelGatewayElement.getBusinessRole());
        branchTaskData.setUndertakeSituationPlaceHolder(parallelGatewayElement.getUndertakeSituationPlaceHolder());
        branchTaskData.setInfoDistributionPlaceHolder(parallelGatewayElement.getInfoDistributionPlaceHolder());
        branchTaskData.setOperationRecordPlaceHolder(parallelGatewayElement.getOperationRecordPlaceHolder());
        branchTaskData.setOperationRecordPlaceHolder(parallelGatewayElement.getOperationRecordPlaceHolder());

        return branchTaskData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#loadBranchTaskData(com.wellsoft.pt.workflow.work.bean.BranchTaskData)
     */
    @Override
    public BranchTaskData loadBranchTaskData(BranchTaskData branchTaskData) {
        String taskInstUuid = branchTaskData.getTaskInstUuid();
        String flowInstUuid = branchTaskData.getFlowInstUuid();
        // <显示位置，承办信息列表>
        Map<String, List<FlowShareData>> shareDatas = Maps.newHashMapWithExpectedSize(0);
        // <显示位置，信息分发列表>
        Map<String, List<TaskInfoDistributionData>> distributeInfos = Maps.newHashMapWithExpectedSize(0);
        // <显示位置，操作记录列表>
        Map<String, List<TaskOperationData>> taskOperations = Maps.newHashMapWithExpectedSize(0);

        // 承办信息
        if (StringUtils.isNotBlank(branchTaskData.getUndertakeSituationPlaceHolder())) {
            shareDatas.put(branchTaskData.getUndertakeSituationPlaceHolder(), getUndertakeSituationDatas(taskInstUuid, flowInstUuid));
        }
        // 信息分发
        if (StringUtils.isNotBlank(branchTaskData.getInfoDistributionPlaceHolder())) {
            distributeInfos.put(branchTaskData.getInfoDistributionPlaceHolder(), getDistributeInfos(flowInstUuid));
        }
        // 操作记录
        if (StringUtils.isNotBlank(branchTaskData.getOperationRecordPlaceHolder())) {
            taskOperations.put(branchTaskData.getOperationRecordPlaceHolder(), getTaskOperations(flowInstUuid));
        }
        branchTaskData.setShareDatas(shareDatas);
        branchTaskData.setDistributeInfos(distributeInfos);
        branchTaskData.setTaskOperations(taskOperations);
        return branchTaskData;
    }

    /**
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    private List<FlowShareData> getUndertakeSituationDatas(String taskInstUuid, String flowInstUuid) {
        // 主流程定义
        FlowInstance flowInstance = this.flowInstanceService.get(flowInstUuid);
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);

        // 获取子流程办理信息
        List<FlowShareDataQueryItem> shareDataQueryItems = getBranchTaskShareDataQueryItems(flowInstUuid);
        // 按上级环节实例UUID分组
        ImmutableListMultimap<String, FlowShareDataQueryItem> immutableListMultimap = Multimaps.index(
                shareDataQueryItems.iterator(), new Function<FlowShareDataQueryItem, String>() {

                    @Override
                    public String apply(FlowShareDataQueryItem flowShareItem) {
                        return flowShareItem.getBelongToTaskInstUuid();
                    }

                });
        ImmutableMap<String, Collection<FlowShareDataQueryItem>> immutableMap = immutableListMultimap.asMap();

        List<String> parallelTaskUuids = Arrays.asList(immutableMap.keySet().toArray(new String[]{}));
        List<TaskInstance> parallelTaskInstances = taskInstanceService.listByUuids(parallelTaskUuids);
        Map<String, TaskInstance> parallelTaskInstanceMap = ConvertUtils.convertElementToMap(parallelTaskInstances,
                IdEntity.UUID);

        boolean isLatest = true;
        boolean isMajor = false;
        boolean isSupervise = false;
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        List<FlowShareData> shareDatas = Lists.newArrayList();
        for (Entry<String, Collection<FlowShareDataQueryItem>> entry : immutableMap.entrySet()) {
            String parentTaskInstUuid = entry.getKey();
            List<FlowShareDataQueryItem> queryItems = Lists.newArrayList();
            queryItems.addAll(entry.getValue());
            if (CollectionUtils.isEmpty(queryItems)) {
                continue;
            }

            Collections.sort(queryItems, UndertakeSituationUtils.getFlowShareDataQueryItemComparator());

            FlowShareData flowShareData = new FlowShareData();

            // 设置发起发支的流程信息
            if (CollectionUtils.isNotEmpty(queryItems)) {
                FlowShareDataQueryItem flowShareItem = queryItems.iterator().next();
                flowShareData.setBelongToTaskId(flowShareItem.getBelongToTaskId());
                flowShareData.setBelongToTaskInstUuid(flowShareItem.getBelongToTaskInstUuid());
                flowShareData.setBelongToFlowInstUuid(flowShareItem.getBelongToFlowInstUuid());
            }
            TaskInstance parallelTaskInstance = parallelTaskInstanceMap.get(parentTaskInstUuid);
            String branchTaskId = parallelTaskInstance.getId();
            ParallelGatewayElement parallelGatewayElement = getBranchTaskParallelGatewayElement(flowDelegate,
                    branchTaskId);
            flowShareData.setBusinessType(parallelGatewayElement.getBusinessType());
            flowShareData.setBusinessRole(parallelGatewayElement.getBusinessRole());

            // 最新办理阶段
            if (isLatest) {
                isMajor = UndertakeSituationUtils.isMajorUser(user, taskInstUuid, queryItems);
                isSupervise = UndertakeSituationUtils.isFollowUpUser(user, branchTaskId, flowInstUuid);
                flowShareData.setMajor(isMajor);
                flowShareData.setSupervise(isSupervise);
                flowShareData.setOver(false);
            } else {
                flowShareData.setOver(true);
            }

            // 办理进度标题
            flowShareData.setTitle(getUndertakeSituationTitle(parallelGatewayElement, flowDelegate,
                    parallelTaskInstance, flowInstance, flowDefinition));
            // 操作按钮，主办、跟进人员才有操作按钮
            if (isLatest && (isMajor || isSupervise)) {
                flowShareData.setButtons(UndertakeSituationUtils.getUndertakeSituationButtons(parallelGatewayElement,
                        isMajor, isSupervise));
            }
            // 列配置
            flowShareData.setColumns(UndertakeSituationUtils.getUndertakeSituationColumns(parallelGatewayElement));
            // 办理信息
            List<FlowShareRowData> shareItems = getFlowShareItems(flowInstUuid, flowShareData.getColumns(), queryItems,
                    parallelGatewayElement, flowDelegate, user, isMajor, isSupervise, isLatest);
            flowShareData.setShareItems(shareItems);

            shareDatas.add(flowShareData);
            isLatest = false;
        }

        return shareDatas;
    }

    /**
     * @param flowInstUuid
     * @return
     */
    private List<FlowShareDataQueryItem> getBranchTaskShareDataQueryItems(String flowInstUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        List<FlowShareDataQueryItem> shareDataQueryItems = this.dao.listItemByNameSQLQuery(
                "getBranchTaskShareDataQuery", FlowShareDataQueryItem.class, values, new PagingInfo(1,
                        Integer.MAX_VALUE, false));
        return shareDataQueryItems;
    }

    /**
     * @param columns
     * @param queryItems
     * @param parallelGatewayElement
     * @param flowDelegate
     * @param user
     * @param isSupervise
     * @return
     */
    private List<FlowShareRowData> getFlowShareItems(String flowInstUuid, List<CustomDynamicColumn> columns,
                                                     Collection<FlowShareDataQueryItem> shareDataQueryItems, ParallelGatewayElement parallelGatewayElement,
                                                     FlowDelegate flowDelegate, UserDetails user, boolean isMajor, boolean isSupervise, boolean isLatest) {
        // 组装数据
        List<FlowShareItem> flowShareItems = Lists.newArrayList();
        // 扩展列
        List<ColumnElement> extendColumns = UndertakeSituationUtils.getExtendColumns(parallelGatewayElement);
        // 扩展列的表单数据
        Map<String, DyFormData> subflowDyformDataMap = Maps.newHashMap();
        List<TaskActivity> taskActivities = taskService.getTaskActivities(flowInstUuid);
        List<TaskFormAttachmentLog> taskFormAttachmentLogs = taskFormAttachmentLogService
                .listByFlowInstUuid(flowInstUuid);
        // 固定列数据
        for (FlowShareDataQueryItem shareDataQueryItem : shareDataQueryItems) {
            // 流程数据是否共享
            if (Boolean.TRUE.equals(shareDataQueryItem.getIsShare())) {
            } else if (isSupervise) {
                // 跟踪人员权限可见全部子流程实例
            } else if (StringUtils.isNotBlank(shareDataQueryItem.getCurrentTodoUserId())
                    //					&& taskService.hasViewPermission(user, shareDataQueryItem.getTaskInstUuid())
                    && StringUtils.contains(shareDataQueryItem.getCurrentTodoUserId(), user.getUserId())) {
                // 当前子流程实例的办理人可见
            } else if (isOperateByCurrentUser(shareDataQueryItem)) {
                // 判断当前用户是否有办理过已办结的分支流
            } else {
                continue;
            }

            FlowShareItem flowShareItem = new FlowShareItem();

            BeanUtils.copyProperties(shareDataQueryItem, flowShareItem);

            // 承办部门
            UndertakeSituationUtils.setTodoName(shareDataQueryItem, flowShareItem);

            // 设置当前环节名称
            UndertakeSituationUtils.setCurrentTaskName(shareDataQueryItem, flowShareItem);

            // 办理时限
            UndertakeSituationUtils.setDueTimeFormatString(shareDataQueryItem, flowShareItem);

            // 剩余时限
            UndertakeSituationUtils.setRemainingTime(shareDataQueryItem, flowShareItem, isLatest);

            // 办理附件
            UndertakeSituationUtils.setResultFiles(shareDataQueryItem, flowShareItem, taskActivities,
                    taskFormAttachmentLogs);

            // 扩展列数据
            for (ColumnElement columnElement : extendColumns) {
                String sources = columnElement.getSources();
                if (StringUtils.isNotBlank(sources)) {
                    String fieldName = sources;
                    Object fieldValue = UndertakeSituationUtils.getFieldValue(fieldName, flowShareItem,
                            subflowDyformDataMap);
                    flowShareItem.getExtras().put(columnElement.getIndex(), fieldValue);
                }
            }

            flowShareItems.add(flowShareItem);
        }

        // 转化为统一处理的列表数据
        List<FlowShareRowData> valueList = Lists.newArrayList();
        for (FlowShareItem flowShareItem : flowShareItems) {
            valueList.add(UndertakeSituationUtils.convertRowData(flowShareItem, columns));
        }

        return valueList;
    }

    /**
     * @param shareDataQueryItem
     * @return
     */
    private boolean isOperateByCurrentUser(FlowShareDataQueryItem shareDataQueryItem) {
        String parallelTaskInstUuid = shareDataQueryItem.getBelongToTaskInstUuid();
        String branchTaskInstUuid = shareDataQueryItem.getTaskInstUuid();
        Map<String, Object> values = Maps.newHashMap();
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        values.put("branchTaskInstUuid", branchTaskInstUuid);
        values.put("userId", SpringSecurityUtils.getCurrentUserId());
        List<QueryItem> queryItems = this.dao.listQueryItemByNameSQLQuery("countOperateByCurrentUserQuery", values,
                new PagingInfo(1, Integer.MAX_VALUE, false));
        return queryItems.get(0).getInt("count") > 0;
    }

    /**
     * @param flowInstUuid
     * @return
     */
    private List<TaskInfoDistributionData> getDistributeInfos(String flowInstUuid) {
        List<TaskInfoDistributionData> taskInfoDistributionDatas = Lists.newArrayList();
        List<TaskInfoDistributionQueryItem> taskInfoDistributions = this.taskInfoDistributionService
                .getBranchTaskDistributeInfos(flowInstUuid);
        if (CollectionUtils.isEmpty(taskInfoDistributions)) {
            return taskInfoDistributionDatas;
        }

        // 按作记录按父环节实例UUID分组并计算各自分组的标题
        ImmutableListMultimap<String, TaskInfoDistributionQueryItem> immutableListMultimap = Multimaps.index(
                taskInfoDistributions.iterator(), new Function<TaskInfoDistributionQueryItem, String>() {

                    @Override
                    public String apply(TaskInfoDistributionQueryItem taskInfoDistributionQueryItem) {
                        return taskInfoDistributionQueryItem.getDistributeTaskInstUuid();
                    }

                });
        ImmutableMap<String, Collection<TaskInfoDistributionQueryItem>> immutableMap = immutableListMultimap.asMap();

        // 主流程定义
        FlowInstance flowInstance = this.flowInstanceService.get(flowInstUuid);
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);

        for (Entry<String, Collection<TaskInfoDistributionQueryItem>> entry : immutableMap.entrySet()) {
            // 分组数据
            TaskInfoDistributionData taskInfoDistributionData = new TaskInfoDistributionData();
            Collection<TaskInfoDistributionQueryItem> groupTaskInfoDistributionQueryItems = entry.getValue();
            List<TaskInfoDistributionBean> distributionBeans = Lists.newArrayList();
            for (TaskInfoDistributionQueryItem taskInfoDistribution : groupTaskInfoDistributionQueryItems) {
                TaskInfoDistributionBean distributionBean = new TaskInfoDistributionBean();
                BeanUtils.copyProperties(taskInfoDistribution, distributionBean);
                distributionBean.setDistributorName(IdentityResolverStrategy.resolveAsName(distributionBean
                        .getCreator()));
                String repoFileIds = taskInfoDistribution.getRepoFileIds();
                if (StringUtils.isNotBlank(repoFileIds)) {
                    List<MongoFileEntity> mongoFileEntities = mongoFileService.getFilesFromFolder(
                            taskInfoDistribution.getUuid(), null);
                    if (CollectionUtils.isNotEmpty(mongoFileEntities)) {
                        List<LogicFileInfo> logicFileInfos = new ArrayList<LogicFileInfo>();
                        for (MongoFileEntity mongoFileEntity : mongoFileEntities) {
                            logicFileInfos.add(mongoFileEntity.getLogicFileInfo());
                        }
                        distributionBean.setLogicFileInfos(logicFileInfos);
                    }
                }
                distributionBeans.add(distributionBean);
            }
            taskInfoDistributionData.setDistributeInfos(distributionBeans);

            // 分组标题
            String distributeTaskInstUuid = entry.getKey();
            TaskInstance taskInstance = taskService.getTask(distributeTaskInstUuid);
            ParallelGatewayElement parallelGatewayElement = getBranchTaskParallelGatewayElement(flowDelegate,
                    taskInstance.getId());
            taskInfoDistributionData.setTitle(getInfoDistributionTitle(parallelGatewayElement, flowDelegate,
                    taskInstance, flowInstance, flowDefinition));

            // 归属流程实例UUID
            taskInfoDistributionData.setBelongToFlowInstUuid(flowInstUuid);
            taskInfoDistributionDatas.add(taskInfoDistributionData);
        }

        return taskInfoDistributionDatas;
    }

    /**
     * @param flowInstUuid
     * @return
     */
    private List<TaskOperationData> getTaskOperations(String flowInstUuid) {
        List<TaskOperationData> taskOperationDatas = Lists.newArrayList();
        List<TaskOperationQueryItem> taskOperations = taskOperationService.getBranchTaskRelateOperation(flowInstUuid);
        if (CollectionUtils.isEmpty(taskOperations)) {
            return taskOperationDatas;
        }

        // 按作记录按父环节实例UUID分组并计算各自分组的标题
        ImmutableListMultimap<String, TaskOperationQueryItem> immutableListMultimap = Multimaps.index(
                taskOperations.iterator(), new Function<TaskOperationQueryItem, String>() {

                    @Override
                    public String apply(TaskOperationQueryItem taskOperationQueryItem) {
                        return taskOperationQueryItem.getBelongToTaskInstUuid();
                    }

                });
        ImmutableMap<String, Collection<TaskOperationQueryItem>> immutableMap = immutableListMultimap.asMap();

        // 主流程定义
        FlowInstance flowInstance = this.flowInstanceService.get(flowInstUuid);
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);

        for (Entry<String, Collection<TaskOperationQueryItem>> entry : immutableMap.entrySet()) {
            // 分组数据
            TaskOperationData taskOperationData = new TaskOperationData();
            Collection<TaskOperationQueryItem> groupTaskOperationQueryItems = entry.getValue();
            List<TaskOperationBean> operationBeans = Lists.newArrayList();
            for (TaskOperationQueryItem taskOperation : groupTaskOperationQueryItems) {
                TaskOperationBean operationBean = new TaskOperationBean();
                BeanUtils.copyProperties(taskOperation, operationBean);
                String userId = operationBean.getUserId();
                if (StringUtils.isNotBlank(userId)) {
                    List<String> orgIds = Arrays.asList(StringUtils.split(userId, Separator.SEMICOLON.getValue()));
                    operationBean.setUserName(IdentityResolverStrategy.resolveAsNames(orgIds));
                } else {
                    operationBean.setUserName(StringUtils.EMPTY);
                }
                if (StringUtils.isBlank(operationBean.getOpinionText())) {
                    operationBean.setOpinionText(StringUtils.EMPTY);
                }
                operationBeans.add(operationBean);
            }
            taskOperationData.setOperations(operationBeans);

            // 分组标题
            String distributeTaskInstUuid = entry.getKey();
            TaskInstance taskInstance = taskService.getTask(distributeTaskInstUuid);
            ParallelGatewayElement parallelGatewayElement = getBranchTaskParallelGatewayElement(flowDelegate,
                    taskInstance.getId());
            taskOperationData.setTitle(getOperationRecordTitle(parallelGatewayElement, flowDelegate, taskInstance,
                    flowInstance, flowDefinition));

            // 归属流程实例UUID
            taskOperationData.setBelongToFlowInstUuid(flowInstUuid);
            taskOperationDatas.add(taskOperationData);
        }
        return taskOperationDatas;
    }

    /**
     * @param flowDelegate
     * @param branchTaskId
     * @return
     */
    private ParallelGatewayElement getBranchTaskParallelGatewayElement(FlowDelegate flowDelegate, String branchTaskId) {
        TaskElement taskElement = flowDelegate.getFlow().getTask(branchTaskId);
        ParallelGatewayElement parallelGatewayElement = taskElement.getParallelGateway();
        return parallelGatewayElement;
    }

    /**
     * @param parallelGatewayElement
     * @param flowDelegate
     * @param taskInstance
     * @param flowInstance
     * @param flowDefinition
     * @return
     */
    private String getUndertakeSituationTitle(ParallelGatewayElement parallelGatewayElement, FlowDelegate flowDelegate,
                                              TaskInstance taskInstance, FlowInstance flowInstance, FlowDefinition flowDefinition) {
        String undertakeSituationTitleExpression = parallelGatewayElement.getUndertakeSituationTitleExpression();
        String title = TitleExpressionUtils.getBranchTaskInfoTitleExpression(undertakeSituationTitleExpression,
                flowDelegate, taskInstance, flowInstance, flowDefinition);
        return title;
    }

    /**
     * @param taskElement
     * @param flowDelegate
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @param parentFlowDefinition
     * @return
     */
    private String getInfoDistributionTitle(ParallelGatewayElement parallelGatewayElement, FlowDelegate flowDelegate,
                                            TaskInstance taskInstance, FlowInstance flowInstance, FlowDefinition flowDefinition) {
        String infoDistributionTitleExpression = parallelGatewayElement.getInfoDistributionTitleExpression();
        String title = TitleExpressionUtils.getBranchTaskInfoTitleExpression(infoDistributionTitleExpression,
                flowDelegate, taskInstance, flowInstance, flowDefinition);
        return title;
    }

    /**
     * @param parallelGatewayElement
     * @param flowDelegate
     * @param taskInstance
     * @param flowInstance
     * @param flowDefinition
     * @return
     */
    private String getOperationRecordTitle(ParallelGatewayElement parallelGatewayElement, FlowDelegate flowDelegate,
                                           TaskInstance taskInstance, FlowInstance flowInstance, FlowDefinition flowDefinition) {
        String operationRecordTitleExpression = parallelGatewayElement.getOperationRecordTitleExpression();
        String title = TitleExpressionUtils.getBranchTaskInfoTitleExpression(operationRecordTitleExpression,
                flowDelegate, taskInstance, flowInstance, flowDefinition);
        return title;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#filterBranchTaskActivity(com.wellsoft.pt.bpm.engine.entity.TaskBranch, java.util.List)
     */
    @Override
    public List<TaskActivity> filterBranchTaskActivity(TaskBranch branchTask, List<TaskActivity> taskActivities) {
        String parallelTaskInstUuid = branchTask.getParallelTaskInstUuid();
        String currentTaskInstUuid = branchTask.getCurrentTaskInstUuid();
        return UndertakeSituationUtils.filterBranchTaskActivity(parallelTaskInstUuid, currentTaskInstUuid,
                taskActivities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#isDynamicBranchTask(java.lang.String)
     */
    @Override
    public boolean isDynamicBranchTask(String parallelTaskInstUuid) {
        String hql = "select count(t.uuid) from TaskBranch t where t.parallelTaskInstUuid = :parallelTaskInstUuid and t.branchType <> 0";
        Map<String, Object> values = Maps.newHashMap();
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        return this.dao.countByHQL(hql, values) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskBranchService#listByParallelTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskBranch> listByParallelTaskInstUuid(String parallelTaskInstUuid) {
        return this.dao.listByFieldEqValue("parallelTaskInstUuid", parallelTaskInstUuid);
    }

    /**
     * @param flowInstUuid
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        String hql = "delete from TaskBranch t where t.flowInstUuid = :flowInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(hql, values);
    }

}

/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.access.SidGranularityResolver;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.dao.TaskCounterSignDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.repository.TaskRepository;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.exceptions.FormDataValidateException;
import com.wellsoft.pt.dyform.implement.data.utils.ValidateMsg;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.service.FlowFormatService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
 * 2014-10-2.1	zhulh		2014-10-2		Create
 * </pre>
 * @date 2014-10-2
 */
public abstract class TaskActionExecutor extends BaseServiceImpl implements TaskExecutor {

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected AclTaskService aclTaskService;

    @Autowired
    protected IdentityService identityService;

    @Autowired
    protected IdentityResolverStrategy identityResolverStrategy;
    @Autowired
    protected TaskCounterSignDao taskCounterSignDao;
    @Autowired
    protected ReadMarkerService readMarkerService;
    @Autowired
    protected TaskRepository taskRepository;
    @Autowired
    protected TaskOperationService taskOperationService;
    @Autowired
    protected DyFormFacade dyFormFacade;
    @Autowired
    protected FlowFormatService flowFormatService;
    @Autowired
    private SidGranularityResolver sidGranularityResolver;
    @Autowired
    private TaskFormOpinionService taskFormOpinionService;

    @Autowired
    protected FlowUserJobIdentityService flowUserJobIdentityService;

    @Autowired
    private TaskDelegationService taskDelegationService;

    @Autowired
    private FlowSamplerService flowSamplerService;

    @Autowired
    protected WorkflowOrgService workflowOrgService;

    /**
     * 获取待办人员中有会签的人员
     *
     * @param todoUsers
     * @param users
     * @param ignoreUserId
     * @return
     */
    protected static List<String> getConflictTodoUsers(List<String> todoUsers, List<String> users, String ignoreUserId) {
        Set<String> conflictUsers = new LinkedHashSet<String>();
        for (String todoUser : todoUsers) {
            if (users.contains(todoUser) && !todoUser.equals(ignoreUserId)) {
                conflictUsers.add(todoUser);
            }
        }
        return Arrays.asList(conflictUsers.toArray(new String[0]));
    }

    /**
     * @param object
     * @return
     */
    public static final String object2String(Object object) {
        return object == null || object == "null" ? "" : object.toString();
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    protected TaskIdentity getCurrentUserTaskIdentity(String taskInstUuid, UserDetails userDetails) {
        UserDetails currentUser = userDetails;
        if (currentUser == null) {
            currentUser = SpringSecurityUtils.getCurrentUser();
        }
        List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuidAndUserDetails(taskInstUuid,
                currentUser);
        // 优先取用户自身粒度的待办标识
        TaskIdentity userTaskIdentity = taskIdentities.stream().filter(identity -> StringUtils.equals(identity.getUserId(), SpringSecurityUtils.getCurrentUserId())).findFirst().orElse(null);
        if (userTaskIdentity != null) {
            return userTaskIdentity;
        }
        if (!taskIdentities.isEmpty()) {
            return taskIdentities.get(0);
        }
        // 工作委托后，受拖人拥有委托人的流程权限
        List<TaskIdentity> ownerTaskIdentities = identityService.getByTaskInstUuidAndOwnerId(taskInstUuid, userDetails.getUserId());
        for (TaskIdentity taskIdentity : ownerTaskIdentities) {
            if (WorkFlowTodoType.Delegation.equals(taskIdentity.getTodoType()) && StringUtils.isNotBlank(taskIdentity.getSourceTaskIdentityUuid())) {
                return identityService.get(taskIdentity.getSourceTaskIdentityUuid());
            }
        }
        throw new WorkFlowException("工作已被处理，无法进行操作!");
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param rawUsers
     * @return
     */
    protected List<FlowUserSid> resolveTaskUserSids(TaskInstance taskInstance, TaskData taskData, List<String> rawUsers) {
        Token token = taskData.getToken();
        if (token == null) {
            token = new Token(taskInstance, taskData);
        }
        FlowDelegate flowDelegate = token.getFlowDelegate();
        Node node = flowDelegate.getTaskNode(taskInstance.getId());
        List<FlowUserSid> userSids = sidGranularityResolver.resolve(node, token, rawUsers, SidGranularity.USER);
        List<String> jobPaths = taskData.getTaskUserJobPaths(taskInstance.getId());
        if (CollectionUtils.isNotEmpty(jobPaths)) {
            flowUserJobIdentityService.addUnitUserJobIdentity(userSids, jobPaths, false, taskInstance.getId(), token, ParticipantType.TodoUser);
        }
        return userSids;
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param rawUsers
     * @param granularity
     * @return
     */
    protected List<FlowUserSid> resolveTaskUserSids(TaskInstance taskInstance, TaskData taskData, List<String> rawUsers, String granularity) {
        Token token = taskData.getToken();
        if (token == null) {
            token = new Token(taskInstance, taskData);
        }
        FlowDelegate flowDelegate = token.getFlowDelegate();
        Node node = flowDelegate.getTaskNode(taskInstance.getId());
        return sidGranularityResolver.resolve(node, token, rawUsers, granularity);
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     */
    protected boolean setOpinionRecords(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity,
                                        Map<String, Object> values) {
        String taskInstUuid = taskInstance.getUuid();
        values.put("taskName", taskInstance.getName());
        TaskForm taskForm = taskService.getTaskForm(taskInstUuid);
        List<Record> records = taskForm.getRecords();
        if (records == null || records.isEmpty()) {
            return false;
        }
        Token token = taskData.getToken();
        if (token == null) {
            token = new Token(taskInstance, taskData);
        }
        FlowDelegate flowDelegate = token.getFlowDelegate();
        Integer todoType = taskIdentity.getTodoType();
        values.put("flowDelegate", flowDelegate);
        values.put("token", token);
        values.put("taskNode", flowDelegate.getTaskNode(taskInstance.getId()));
        values.put("todoType", todoType);
        if (WorkFlowTodoType.Delegation.equals(todoType)) {
            String taskIdentityUuid = taskIdentity.getUuid();
            TaskDelegation taskDelegation = taskDelegationService.get(SpringSecurityUtils.getCurrentUserId(),
                    taskInstUuid, taskIdentityUuid);
            if (taskDelegation != null) {
                values.put("trusteeName", taskDelegation.getTrusteeName());
                values.put("consignorName", taskDelegation.getConsignorName());
            } else {
                values.put("todoType", WorkFlowTodoType.Submit);
            }
        }
        String toTaskId = taskData.getToTaskId(taskInstance.getId());
        if (!values.containsKey("toTaskId")) {
            values.put("toTaskId", toTaskId);
        }
        String key = taskInstUuid + taskData.getUserId();
        List<IdEntity> entities = new ArrayList<IdEntity>();
        TaskInstance taskInstanceModel = new TaskInstance();
        BeanUtils.copyProperties(taskInstance, taskInstanceModel);
        entities.add(taskInstanceModel);
        FlowInstance flowInstanceModel = new FlowInstance();
        BeanUtils.copyProperties(taskInstance.getFlowInstance(), flowInstanceModel);
        entities.add(flowInstanceModel);
        TaskIdentity taskIdentityModel = new TaskIdentity();
        BeanUtils.copyProperties(taskIdentity, taskIdentityModel);
        entities.add(taskIdentityModel);
        TaskOperation taskOperation = new TaskOperation();
        String opinionText = taskData.getOpinionText(key);
        taskOperation.setOpinionText(opinionText);
        taskOperation.setOpinionLabel(taskData.getOpinionLabel(key));
        taskOperation.setOpinionValue(taskData.getOpinionValue(key));
        taskOperation.setOpinionFileIds(taskData.getOpinionFiles(key).stream().map(file -> file.getFileID()).collect(Collectors.joining(Separator.SEMICOLON.getValue())));
        taskOperation.setAction(taskData.getAction(key));
        taskOperation.setActionType(taskData.getActionType(key));
        entities.add(taskOperation);
        values.put("opinionText", opinionText);
        values.put("opinionLabel", taskData.getOpinionLabel(key));
        values.put("opinionValue", taskData.getOpinionValue(key));
        values.put("opinionFiles", taskData.getOpinionFiles(key));

        String formUuid = taskInstance.getFormUuid();
        String dataUuid = taskInstance.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }

        String flowInstUuid = taskInstance.getFlowInstance().getUuid();
        // 解析并保存信息记录
        List<String> opinionLogUuids = taskFormOpinionService.saveOpinionRecords(opinionText, taskInstUuid,
                flowInstUuid, entities, dyFormData, records, values);
        // 生成信息记录后进行检验
        if (CollectionUtils.isNotEmpty(opinionLogUuids)) {
            ValidateMsg validateMsg = dyFormData.validateFormDataWithDatabaseConstraints();
            if (CollectionUtils.isNotEmpty(validateMsg.getErrors())) {
                validateMsg.setMsg("流程生成的信息记录设置到表单后，表单数据检验失败");
                throw new FormDataValidateException(validateMsg);
            }
        }
        dyFormFacade.saveFormData(dyFormData);

        // 意见日志信息
        taskData.setTaskFormOpinionLogUuids(key, opinionLogUuids);

        return true;
    }

    // /**
    // * @param identities
    // * @return
    // */
    // public static String taskIdentitiesToJson(List<TaskIdentity> identities)
    // {
    // return JsonUtils.object2Json(identities);
    // }
    //
    // /**
    // * @param identity
    // * @return
    // */
    // public static String taskIdentityToJson(TaskIdentity identity) {
    // List<TaskIdentity> identities = new ArrayList<TaskIdentity>();
    // identities.add(identity);
    // return JsonUtils.object2Json(identities);
    // }
    //
    // @SuppressWarnings("unchecked")
    // public static List<TaskIdentity> jsonToTaskIdentity(String identityJson)
    // {
    // List<TaskIdentity> taskIdentities = new ArrayList<TaskIdentity>();
    // if (StringUtils.isBlank(identityJson)) {
    // return taskIdentities;
    // }
    // if (identityJson.startsWith("{")) {
    // TaskIdentity taskIdentity = JsonUtils.json2Object(identityJson,
    // TaskIdentity.class);
    // taskIdentities.add(taskIdentity);
    // } else if (identityJson.startsWith("[{")) {
    // taskIdentities = JsonUtils.json2Object(identityJson, List.class);
    // }
    // return taskIdentities;
    // }

    /**
     * @param taskData
     * @param taskInstance
     * @param flowInstance
     */
    protected void createFlowInstanceSnapshot(TaskData taskData, TaskInstance taskInstance, FlowInstance flowInstance) {
        String taskInstUuid = taskData.getTaskInstUuid();
        if (taskInstance != null) {
            taskInstUuid = taskInstance.getUuid();
        }
        String taskOperationUuid = taskData.getOperationUuid(taskInstUuid);
        flowSamplerService.createSnapshot(taskData, taskOperationUuid, taskInstUuid, flowInstance);
    }

    /**
     * @param taskInstance
     * @param action
     */
    protected void preCheckTaskSuspensionState(TaskInstance taskInstance, String action) {
        String actionName = WorkFlowOperation.getName(action);
        if (StringUtils.isBlank(actionName)) {
            actionName = action;
        }
        if (SuspensionState.SUSPEND.getState() == taskInstance.getSuspensionState()) {
            if (WorkFlowOperation.CANCEL.equals(action)) {
                if (taskInstance.getFlowInstance().getEndTime() != null) {//已经提交到下一流程
                    throw new WorkFlowException("撤回失败！下一环节已办理，无法撤回！");
                } else {
                    throw new WorkFlowException("撤回失败！请勿重复撤回！");
                }
            }
            throw new WorkFlowException("当前环节处于挂起状态，不能进行" + actionName + "!");
        }
    }

    /**
     * @param currentUserId
     * @param taskInstUuid
     * @param taskIdentity
     * @param token
     */
    protected void handleSidTaskIdentity(String currentUserId, String taskInstUuid, TaskIdentity taskIdentity, Token token) {
        // 权限颗粒度大于用户
        String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
        aclTaskService.addDonePermission(currentUserId, PermissionGranularityUtils.getCurrentUserSids(), taskInstUuid);
        Map<String, String> userMap = workflowOrgService.getUsersByIds(Lists.newArrayList(taskIdentity.getUserId()), orgVersionIds);
        List<String> orgUserIds = Lists.newArrayList(userMap.keySet());
        List<String> sidDoneUserIds = aclTaskService.listSidDoneMarkerUserId(taskIdentity.getUserId(), taskInstUuid);
        orgUserIds.removeAll(sidDoneUserIds);
        if (CollectionUtils.isEmpty(orgUserIds)) {
            identityService.suspenseTodo(taskIdentity);
        }
    }

    /**
     * @param sid
     * @param taskInstUuid
     * @param token
     * @return
     */
    protected boolean isSidTaskIdentityComplete(String sid, String taskInstUuid, Token token) {
        String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
        Map<String, String> userMap = workflowOrgService.getUsersByIds(Lists.newArrayList(sid), orgVersionIds);
        List<String> orgUserIds = Lists.newArrayList(userMap.keySet());
        List<String> sidDoneUserIds = aclTaskService.listSidDoneMarkerUserId(sid, taskInstUuid);
        orgUserIds.removeAll(sidDoneUserIds);
        return CollectionUtils.isEmpty(orgUserIds);
    }

}

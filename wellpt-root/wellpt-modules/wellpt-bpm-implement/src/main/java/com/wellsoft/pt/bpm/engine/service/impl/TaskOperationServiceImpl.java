/*
 * @(#)2013-4-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.pt.bpm.engine.dao.TaskOperationDao;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.log.support.FlowOperationLoggerHolder;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskOperationItem;
import com.wellsoft.pt.bpm.engine.query.api.TaskOperationQueryItem;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentService;
import com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.bpm.engine.support.SubmitResult;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.log.LogEvent;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.support.ContextLogs;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.event.WorkCopyToEvent;
import com.wellsoft.pt.workflow.event.WorkDoneEvent;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
 * 2013-4-28.1	zhulh		2013-4-28		Create
 * </pre>
 * @date 2013-4-28
 */
@Service
public class TaskOperationServiceImpl extends AbstractJpaServiceImpl<TaskOperation, TaskOperationDao, String>
        implements TaskOperationService {

    private static final String GET_ALL_OPERATION_BY_TASK_INST_UUID = "from TaskOperation t1 where exists (select t2.uuid from TaskInstance t2 where t1.flowInstUuid = t2.flowInstance.uuid and t2.uuid = :taskInstUuid)";
    private static final String GET_ALL_OPERATION_BY_TASK_INST_UUID_NEW = "from TaskOperation t1 where t1.flowInstUuid = (select t2.flowInstance.uuid from TaskInstance t2 where t2.uuid = :taskInstUuid)";
    private static final String GET_ALL_OPERATION_BY_FLOW_INST_UUID = "from TaskOperation t1 where t1.flowInstUuid = :flowInstUuid";
    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from TaskOperation t1 where t1.flowInstUuid = :flowInstUuid";
    private static final String GET_BY_FLOW_INST_UUID_AND__ANDTASK_IDS = "from TaskOperation t1 where t1.flowInstUuid = :flowInstUuid and t1.taskId in (:taskIds)";
    private static final String LIST_BY_PARENT_TASK_INST_UUID = "from TaskOperation t1 where t1.taskInstUuid in(select t2.uuid from TaskInstance t2 where t2.parent.uuid = :parentTaskInstUuid)";
    //    @Autowired
//    private LogFacadeService logFacadeService;
//    @Autowired
//    private UserOperationLogService userOperationLogService;
    @Autowired
    private TaskFormOpinionLogService taskFormOpinionLogService;
    //    @Autowired
//    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private TaskFormAttachmentService taskFormAttachmentService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;
    @Autowired
    private WfFlowSettingService flowSettingService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#get(java.lang.String)
     */
    @Override
    public TaskOperation get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * 统计流程环节中意见立场为指定值的数量
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#countOpinion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public long countOpinion(String opinionValue, String taskId, String flowInstUuid) {
        return dao.countOpinion(opinionValue, taskId, flowInstUuid);
    }

    /**
     * 统计流程环节中意见立场不办空的总数量
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#countAllOpinion(java.lang.String, java.lang.String)
     */
    @Override
    public long countAllOpinion(String taskId, String flowInstUuid) {
        return dao.countAllOpinion(taskId, flowInstUuid);
    }

    /**
     * 设置操作历史的主送对象及抄送对象
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#setUsers(java.lang.String, java.util.Set, java.util.Set)
     */
    @Override
    @Transactional
    public void setUsers(String uuid, Collection<String> userIds, Collection<String> copyUserIds) {
        if (uuid == null) {
            return;
        }

        TaskOperation taskOperation = this.dao.getOne(uuid);
        // 设置主送对象
//        StringBuilder userId = new StringBuilder();
//        Iterator<String> it = userIds.iterator();
//        while (it.hasNext()) {
//            userId.append(it.next());
//            if (it.hasNext()) {
//                userId.append(Separator.SEMICOLON.getValue());
//            }
//        }

        taskOperation.setUserId(StringUtils.join(userIds, Separator.SEMICOLON.getValue()));

        // 设置抄送对象
//        StringBuilder copyUserId = new StringBuilder();
//        it = copyUserIds.iterator();
//        while (it.hasNext()) {
//            copyUserId.append(it.next());
//            if (it.hasNext()) {
//                copyUserId.append(Separator.SEMICOLON.getValue());
//            }
//        }
        if (StringUtils.isNotBlank(taskOperation.getCopyUserId())) {
            Set<String> copyUserSet = Sets.newLinkedHashSet(Arrays.asList(StringUtils.split(taskOperation.getCopyUserId(), Separator.SEMICOLON.getValue())));
            copyUserSet.addAll(copyUserIds);
            taskOperation.setCopyUserId(StringUtils.join(copyUserSet, Separator.SEMICOLON.getValue()));
        } else {
            taskOperation.setCopyUserId(StringUtils.join(copyUserIds, Separator.SEMICOLON.getValue()));
        }

        // 更新操作历史
        this.dao.save(taskOperation);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getByTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskOperation> getByTaskInstUuid(String taskInstUuid) {
        return dao.getByTaskInstUuid(taskInstUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getLastestByUserId(java.lang.String, java.lang.String)
     */
    @Override
    public TaskOperation getLastestByUserId(String userId, String flowInstUuid) {
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");

        TaskOperation example = new TaskOperation();
        example.setAssignee(userId);
        example.setFlowInstUuid(flowInstUuid);
        List<TaskOperation> taskOperations = this.dao.listByEntityAndPage(example, null, "createTime desc");

        if (CollectionUtils.isNotEmpty(taskOperations)) {
            return taskOperations.get(0);
        }
        return null;
    }

    /**
     * @param userId
     * @param actionCodes
     * @param flowInstUuid
     * @return
     */
    @Override
    public TaskOperation getLastestByUserIdAndActionCodes(String userId, List<Integer> actionCodes, String flowInstUuid) {
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");
        String hql = "from TaskOperation t where t.assignee = :userId and t.flowInstUuid = :flowInstUuid and t.actionCode in(:actionCodes) order by t.createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("flowInstUuid", flowInstUuid);
        params.put("actionCodes", actionCodes);
        List<TaskOperation> taskOperations = this.dao.listByHQL(hql, params);

        if (CollectionUtils.isNotEmpty(taskOperations)) {
            return taskOperations.get(0);
        }
        return null;
    }

    @Override
    public TaskOperation getLastestDoneByUserId(String userId, String flowInstUuid) {
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");

        String hql = "from TaskOperation t where t.assignee = :userId and t.flowInstUuid = :flowInstUuid and t.actionCode in(:actionCodes) order by t.createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        List<Integer> actionCodes = WorkFlowOperation.getActionCodeOfSubmit();
        actionCodes.addAll(WorkFlowOperation.getActionCodeOfRollback());
        params.put("userId", userId);
        params.put("flowInstUuid", flowInstUuid);
        params.put("actionCodes", actionCodes);
        List<TaskOperation> taskOperations = this.dao.listByHQL(hql, params);

        if (CollectionUtils.isNotEmpty(taskOperations)) {
            return taskOperations.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getLastestCopyToByUserId(java.lang.String, java.lang.String)
     */
    @Override
    public TaskOperation getLastestCancelAfterByFlowInstUuid(String flowInstUuid) {
        TaskOperation example = new TaskOperation();
        example.setAssignee(SpringSecurityUtils.getCurrentUserId());
        example.setFlowInstUuid(flowInstUuid);
        List<TaskOperation> taskOperations = this.dao.listByEntityAndPage(example, null, "createTime desc");
        if (taskOperations.isEmpty()) {
            return null;
        }
        final String actionType = "Cancel";
        if (actionType.equals(taskOperations.get(0).getActionType())) {
            return taskOperations.get(1);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getLastestCopyToByUserId(java.lang.String, java.lang.String)
     */
    @Override
    public TaskOperation getLastestCopyToByUserId(String userId, String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("flowInstUuid", flowInstUuid);
        List<TaskOperation> taskOperations = listByNameSQLQuery("getLastestCopyToByUserId", values);
        if (taskOperations.isEmpty()) {
            return null;
        }

        return taskOperations.get(0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getLastestTaskInstUuidBySubmitAndCopyToUser(java.lang.String, java.lang.String)
     */
    @Override
    public String getLastestTaskInstUuidBySubmitAndCopyToUser(String userId, String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("flowInstUuid", flowInstUuid);
        List<QueryItem> queryItems = this.dao.listQueryItemByNameSQLQuery("getLastestTaskInstUuidBySubmitAndCopyToUser",
                values, new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(queryItems)) {
            return queryItems.get(0).getString(QueryItem.getKey("task_inst_uuid"));
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#saveTaskOperation(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.util.Collection, java.lang.String, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    @Transactional
    public String saveTaskOperation(String action, Integer actionCode, String actionType, String opinionValue,
                                    String opinionName, String opinionText, String operator, Collection<String> userIds,
                                    Collection<String> copyUserIds, String taskIdentityUuid, String extraInfo, TaskInstance taskInstance,
                                    FlowInstance flowInstance, TaskData taskData) {
        String flowInstUuid = flowInstance.getUuid();
        // 保存任务历史
        TaskOperation taskOperation = new TaskOperation();

        final String taskId = taskInstance.getId();
        final String taskInstUuid = taskInstance.getUuid();
        final String taskName = taskInstance.getName();
        String key = taskInstUuid + taskData.getUserId();
        // 操作名称
        String taskAction = taskData.getAction(key);
        taskAction = StringUtils.isNotBlank(action) ? action : taskAction;
        // 操作代码
        Integer taskActionCode = taskData.getActionCode(taskInstUuid);
        taskActionCode = actionCode != null ? actionCode : taskActionCode;
        // 操作类型
        String taskActionType = taskData.getActionType(key);
        taskActionType = StringUtils.isNotBlank(actionType) ? actionType : taskActionType;
        // 意见立场值
        String taskOpinionValue = taskData.getOpinionValue(key);
        taskOpinionValue = StringUtils.isNotBlank(opinionValue) ? opinionValue : taskOpinionValue;
        // 意见立场名称
        String taskOpinionName = taskData.getOpinionLabel(key);
        taskOpinionName = StringUtils.isNotBlank(opinionName) ? opinionName : taskOpinionName;
        // 办理意见
        String taskOpinionText = taskData.getOpinionText(key);
        taskOpinionText = StringUtils.isNotBlank(opinionText) ? opinionText : taskOpinionText;
        // 办理意见附件
        List<LogicFileInfo> taskOpinionFiles = taskData.getOpinionFiles(key);
        List<String> taskOpinionFileIds = taskOpinionFiles.stream().map(file -> file.getFileID()).collect(Collectors.toList());
        // 任务操作者
        String taskOperator = taskData.getUserId();
        taskOperator = StringUtils.isNotBlank(operator) ? operator : taskOperator;
        String assigneeName = taskData.getUserName();
        assigneeName = StringUtils.isNotBlank(assigneeName) ? assigneeName : SpringSecurityUtils.getCurrentUserName();
        // 获取用户操作的身份
        String taskOperatorIdentityId = null;
        String taskOperatorIdentityNamePath = null;
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        Locale locale = LocaleContextHolder.getLocale();
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        if (!workFlowSettings.isShowOperatorPrimaryIdentity() && (WorkFlowOperation.isActionCodeOfSubmit(taskActionCode)
                || WorkFlowOperation.isActionCodeOfRollback(taskActionCode)
                || ActionCode.TRANSFER.getCode().equals(taskActionCode)
                || ActionCode.COUNTER_SIGN.getCode().equals(taskActionCode)
                || ActionCode.ADD_SIGN.getCode().equals(taskActionCode)
                || ActionCode.DELEGATION.getCode().equals(taskActionCode)
                || ActionCode.TAKE_BACK_TODO_DELEGATION.getCode().equals(taskActionCode)
                || ActionCode.AUTO_SUBMIT.getCode().equals(taskActionCode)
                || ActionCode.SKIP_SUBMIT.getCode().equals(taskActionCode))) {
            List<OrgUserJobDto> orgUserJobDtos = flowUserJobIdentityService.getUserOperateJobIdentity(taskOperator, taskInstance, taskIdentityUuid, taskData);
            taskOperatorIdentityId = orgUserJobDtos.stream().map(OrgUserJobDto::getJobId).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
            taskOperatorIdentityNamePath = orgUserJobDtos.stream().map(OrgUserJobDto::getJobNamePath).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
        }
        LocaleContextHolder.setLocale(locale);
        // 是否移动端应用的操作
        Boolean isMobileApp = JsonDataServicesContextHolder.isMobileRequest();

        taskOperation.setAction(taskAction);
        taskOperation.setActionType(taskActionType);
        taskOperation.setActionCode(taskActionCode);
        taskOperation.setOpinionValue(taskOpinionValue);
        taskOperation.setOpinionLabel(taskOpinionName);
        taskOperation.setOpinionText(taskOpinionText);
        taskOperation.setOpinionFileIds(StringUtils.join(taskOpinionFileIds, Separator.SEMICOLON.getValue()));
        taskOperation.setAssignee(taskOperator);
        taskOperation.setAssigneeName(assigneeName);
        taskOperation.setOperatorIdentityId(taskOperatorIdentityId);
        taskOperation.setOperatorIdentityNamePath(taskOperatorIdentityNamePath);
        taskOperation.setTaskId(taskId);
        taskOperation.setTaskName(taskName);
        taskOperation.setTaskInstUuid(taskInstUuid);
        taskOperation.setFlowInstUuid(flowInstUuid);
        taskOperation.setIsMobileApp(isMobileApp);

        // 设置主送对象
        if (userIds != null) {
            taskOperation.setUserId(StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
        }
        // 设置抄送对象
        if (copyUserIds != null) {
            taskOperation.setCopyUserId(StringUtils.join(copyUserIds, Separator.SEMICOLON.getValue()));
        } else {
            // 发起环节提交并抄送
            String preOperationUuid = taskData.getOperationUuid(taskInstUuid);
            if (StringUtils.isBlank(preOperationUuid)) {
                Set<String> startTaskCopyUserIds = (Set<String>) taskData.get("startTaskCopyUserIds_" + taskInstUuid);
                if (CollectionUtils.isNotEmpty(startTaskCopyUserIds)) {
                    taskOperation.setCopyUserId(StringUtils.join(startTaskCopyUserIds, Separator.SEMICOLON.getValue()));
                }
            }
        }
        // 待办信息UUID
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            taskOperation.setTaskIdentityUuid(taskIdentityUuid);
        } else {
            taskOperation.setTaskIdentityUuid(taskData.getTaskIdentityUuid(key));
        }
        // 附加信息
        taskOperation.setExtraInfo(extraInfo);

        dao.save(taskOperation);

        // 文件放入夹
        if (CollectionUtils.isNotEmpty(taskOpinionFileIds)) {
            mongoFileService.pushFilesToFolder(flowInstUuid, taskOpinionFileIds, taskOperation.getUuid());
        }

        // 发送已办事件
        if (StringUtils.equals(WorkFlowOperation.COPY_TO, actionType)) {
            ApplicationContextHolder
                    .publishEvent(new WorkCopyToEvent(this, Sets.newHashSet(copyUserIds), flowInstance, taskInstance));
        } else {
            ApplicationContextHolder.publishEvent(new WorkDoneEvent(this, operator, flowInstance, taskInstance, taskData));
        }

        // 用户操作日志
        if (taskData.isLogUserOperation() != false) {
            BusinessOperationLog log = new BusinessOperationLog();
            FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
            log.setModuleId(flowDefinition.getModuleId());
            // log.setModuleName("工作流程");
            log.setDataDefType(ModuleID.WORKFLOW.getValue());
            log.setDataDefId(flowDefinition.getId());
            log.setDataDefName(flowDefinition.getName());
            log.setOperation(WorkFlowOperation.getName(taskActionType) == null ? "提交"
                    : WorkFlowOperation.getName(taskActionType));
            if (StringUtils.isNotBlank(taskAction)) {
                log.setOperation2(taskAction);
            } else {
                log.setOperation2(log.getOperation());
            }
            log.setUserId(taskData.getUserId());
            log.setUserName(taskData.getUserName());
            log.setDataId(flowInstance.getUuid());
            String title = taskData.getTitle(flowInstance.getId());
            if (StringUtils.isBlank(title)) {
                title = flowInstance.getTitle();
            }
            log.setDataName(title);
            // userOperationLogService.save(log);
            Map<String, Object> details2 = Maps.newHashMap();
            // DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
            // if (null != dyFormData) {
            // details2.put("dyform", dyFormData.cloneDyFormDatasToJson());
            // }
            // details2.put("flowInstUuid", flowInstUuid);
            // details2.put("taskInstUuid", taskInstUuid);
            ContextLogs.sendLogEvent(new LogEvent(log, details2));
        }

        String taskOperationUuid = taskOperation.getUuid();

        // 意见日志信息
        List<String> taskFormOpinionLogUuids = taskData.getTaskFormOpinionLogUuids(key);
        if (CollectionUtils.isNotEmpty(taskFormOpinionLogUuids)) {
            for (String taskFormOpinionLogUuid : taskFormOpinionLogUuids) {
                TaskFormOpinionLog taskFormOpinionLog = this.taskFormOpinionLogService.getOne(taskFormOpinionLogUuid);
                if (taskFormOpinionLog != null) {
                    taskFormOpinionLog.setTaskOperationUuid(taskOperationUuid);
                    this.taskFormOpinionLogService.save(taskFormOpinionLog);
                }
            }
        } else {
            String taskIdKey = taskId + taskData.getUserId();
            taskFormOpinionLogUuids = taskData.getTaskFormOpinionLogUuids(taskIdKey);
            if (CollectionUtils.isNotEmpty(taskFormOpinionLogUuids)) {
                for (String taskFormOpinionLogUuid : taskFormOpinionLogUuids) {
                    TaskFormOpinionLog taskFormOpinionLog = this.taskFormOpinionLogService
                            .getOne(taskFormOpinionLogUuid);
                    if (taskFormOpinionLog != null && StringUtils.isBlank(taskFormOpinionLog.getTaskInstUuid())) {
                        taskFormOpinionLog.setFlowInstUuid(flowInstUuid);
                        taskFormOpinionLog.setTaskInstUuid(taskInstUuid);
                        taskFormOpinionLog.setTaskOperationUuid(taskOperationUuid);
                        this.taskFormOpinionLogService.save(taskFormOpinionLog);
                    }
                }
            }
        }

        // 表单附件及日志处理
        if (WorkFlowOperation.isActionCodeOfSubmit(taskActionCode)
                || WorkFlowOperation.isActionCodeOfRollback(taskActionCode)) {
            DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
            if (dyFormData != null) {
                taskFormAttachmentService.saveSubmitOrRollbackAttachments(dyFormData, taskOperation);
            }
        }

        // 提交结果
        if (ActionCode.SUBMIT.getCode().equals(taskActionCode)
                || ActionCode.TRANSFER_SUBMIT.getCode().equals(taskActionCode)
                || ActionCode.DELEGATION_SUBMIT.getCode().equals(taskActionCode)
                || ActionCode.SKIP_TASK.getCode().equals(taskActionCode)
                || ActionCode.AUTO_SUBMIT.getCode().equals(taskActionCode)
                || ActionCode.SKIP_SUBMIT.getCode().equals(taskActionCode)) {
            SubmitResult submitResult = taskData.getSubmitResult();
            TaskOperationItem optItem = new TaskOperationItem();
            optItem.setUuid(taskOperationUuid);
            optItem.setTaskId(taskId);
            optItem.setTaskInstUuid(taskInstUuid);
            optItem.setTaskName(taskName);
            submitResult.getTaskOperationItems().add(optItem);
        }
        // 操作日志
        FlowOperationLoggerHolder.addTaskOperationUuid(taskOperationUuid);
        return taskOperationUuid;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getAssigneeByHQL(java.lang.String, java.util.Map)
     */
    @Override
    public List<String> getAssigneeByHQL(String hql, Map<String, Object> values) {
        return this.dao.listCharSequenceByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getAllTaskOperationByTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskOperation> getAllTaskOperationByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.listByHQL(GET_ALL_OPERATION_BY_TASK_INST_UUID_NEW, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getAllTaskOperationByTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskOperation> getAllTaskOperationByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_ALL_OPERATION_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getByFlowInstUuidAndTaskIds(java.lang.String, java.util.List)
     */
    @Override
    public List<TaskOperation> getByFlowInstUuidAndTaskIds(String flowInstUuid, List<String> taskIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        values.put("taskIds", taskIds);
        return this.dao.listByHQL(GET_BY_FLOW_INST_UUID_AND__ANDTASK_IDS, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskOperation> getByFlowInstUuid(String flowInstUuid) {
        return dao.getByFlowInstUuid(flowInstUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#removeByFlowInstUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(REMOVE_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getBranchTaskRelateOperation(java.lang.String)
     */
    @Override
    public List<TaskOperationQueryItem> getBranchTaskRelateOperation(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        List<String> actionTypes = new ArrayList<String>();
        actionTypes.add(WorkFlowOperation.ADD_SUB_FLOW);
        actionTypes.add(WorkFlowOperation.REMIND);
        actionTypes.add(WorkFlowOperation.REDO);
        actionTypes.add(WorkFlowOperation.DISTRIBUTE_INFO);
        actionTypes.add(WorkFlowOperation.CHANGE_LIMIT_TIME);
        actionTypes.add(WorkFlowOperation.STOP);
        values.put("flowInstUuid", flowInstUuid);
        values.put("actionTypes", actionTypes);
        List<TaskOperationQueryItem> taskOperationQueryItems = this.dao.listItemByNameSQLQuery(
                "getBranchTaskRelateOperationQuery", TaskOperationQueryItem.class, values,
                new PagingInfo(1, Integer.MAX_VALUE, false));
        return taskOperationQueryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#getSubflowRelateOperation(java.lang.String)
     */
    @Override
    public Page<TaskOperationQueryItem> getSubflowRelateOperation(String flowInstUuid, String keyword,
                                                                  Page<TaskOperationQueryItem> page) {
        PagingInfo pagingInfo = null;
        if (page != null) {
            pagingInfo = new PagingInfo(page.getPageNo(), page.getPageSize(), true);
        } else {
            page = new Page<>();
            pagingInfo = new PagingInfo(1, Integer.MAX_VALUE, false);
        }
        Map<String, Object> values = new HashMap<String, Object>();
        List<String> actionTypes = new ArrayList<String>();
        actionTypes.add(WorkFlowOperation.ADD_SUB_FLOW);
        actionTypes.add(WorkFlowOperation.REMIND);
        actionTypes.add(WorkFlowOperation.REDO);
        actionTypes.add(WorkFlowOperation.DISTRIBUTE_INFO);
        actionTypes.add(WorkFlowOperation.CHANGE_LIMIT_TIME);
        actionTypes.add(WorkFlowOperation.STOP);
        if (StringUtils.isNotBlank(keyword)) {
            values.put("keyword", "%" + keyword + "%");
        }
        values.put("flowInstUuid", flowInstUuid);
        values.put("actionTypes", actionTypes);
        values.put("actionTypes", actionTypes);
        List<TaskOperationQueryItem> taskOperationQueryItems = this.dao.listItemByNameSQLQuery(
                "getSubflowRelateOperationQueryByParentFlowInstUuid", TaskOperationQueryItem.class, values, pagingInfo);
        // List<TaskOperation> taskOperations =
        // this.dao.listByNameSQLQuery("getSubflowRelateOperation", values);
        page.setResult(taskOperationQueryItems);
        page.setTotalCount(pagingInfo.getTotalCount());
        return page;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskOperationService#listByParentTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskOperation> listByParentTaskInstUuid(String parentTaskInstUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("parentTaskInstUuid", parentTaskInstUuid);
        return this.dao.listByHQL(LIST_BY_PARENT_TASK_INST_UUID, values);
    }

    /**
     * @param taskInstUuid
     * @return
     */
    @Override
    public List<TaskOperation> listByTaskInstUuid(String taskInstUuid) {
        Assert.hasLength(taskInstUuid, "环节实例UUID不能为空！");

        TaskOperation entity = new TaskOperation();
        entity.setTaskInstUuid(taskInstUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * @param taskInstUuids
     * @return
     */
    @Override
    public List<TaskOperation> listByTaskInstUuids(ArrayList<String> taskInstUuids) {
        Assert.notEmpty(taskInstUuids, "环节实例UUID列表不能为空！");

        String hql = "from TaskOperation t where t.taskInstUuid in(:taskInstUuids)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("taskInstUuids", taskInstUuids);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<TaskOperation> getTaskOperationListByTimeInterval(String startDateTime, String endDateTime) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("startDateTime", startDateTime);
        values.put("endDateTime", endDateTime);
        List<TaskOperation> taskOperations = this.dao.listByNameSQLQuery("getTaskOperationListByTimeInterval", values);
        return taskOperations;
    }

    /**
     * @param flowInstUuid
     * @param actionCodes
     * @return
     */
    @Override
    public List<String> listAssigneeByFlowInstUuidAndActionCodes(String flowInstUuid, List<Integer> actionCodes) {
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");
        Assert.notEmpty(actionCodes, "操作类型代码列表不能为空！");

        String hql = "select distinct t.assignee from TaskOperation t where t.flowInstUuid = :flowInstUuid and t.actionCode in (:actionCodes)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("flowInstUuid", flowInstUuid);
        params.put("actionCodes", actionCodes);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    /**
     * @param flowInstUuid
     * @param actionCodes
     * @return
     */
    @Override
    public List<TaskOperation> listByFlowInstUuidAndActionCodes(String flowInstUuid, List<Integer> actionCodes) {
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");
        Assert.notEmpty(actionCodes, "操作类型代码列表不能为空！");

        String hql = "from TaskOperation t where t.flowInstUuid = :flowInstUuid and t.actionCode in (:actionCodes)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("flowInstUuid", flowInstUuid);
        params.put("actionCodes", actionCodes);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public TaskOperation getLastestByTaskInstUuid(String taskInstUuid) {
        Assert.hasLength(taskInstUuid, "环节实例UUID不能为空！");

        String hql = "from TaskOperation t where t.taskInstUuid = :taskInstUuid order by t.createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("taskInstUuid", taskInstUuid);
        List<TaskOperation> taskOperations = this.dao.listByHQL(hql, params);
        return CollectionUtils.isEmpty(taskOperations) ? null : taskOperations.get(0);
    }

    @Override
    public TaskOperation getLastestByFlowInstUuid(String flowInstUuid) {
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");

        String hql = "from TaskOperation t where t.flowInstUuid = :flowInstUuid order by t.createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("flowInstUuid", flowInstUuid);
        List<TaskOperation> taskOperations = this.dao.listByHQL(hql, params);
        return CollectionUtils.isEmpty(taskOperations) ? null : taskOperations.get(0);
    }

    @Override
    public List<TaskOperation> listByTaskInstUuidAndActionCodes(String taskInstUuid, List<Integer> actionCodes) {
        Assert.hasLength(taskInstUuid, "环节实例UUID不能为空！");
        Assert.notEmpty(actionCodes, "操作类型代码列表不能为空！");

        String hql = "from TaskOperation t where t.taskInstUuid = :taskInstUuid and t.actionCode in (:actionCodes) order by t.createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("taskInstUuid", taskInstUuid);
        params.put("actionCodes", actionCodes);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

}

/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.PrinttemplateDateUtil;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.bpm.engine.access.FlowPermissionEvaluator;
import com.wellsoft.pt.bpm.engine.access.FlowPermissionEvaluatorContext;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.FlowListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.core.*;
import com.wellsoft.pt.bpm.engine.core.handler.AbstractHandler;
import com.wellsoft.pt.bpm.engine.dao.TaskActivityDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.*;
import com.wellsoft.pt.bpm.engine.exception.PrintTemplateNotAssignedException;
import com.wellsoft.pt.bpm.engine.exception.PrintTemplateNotFoundException;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskExecutor;
import com.wellsoft.pt.bpm.engine.executor.TaskExecutorFactory;
import com.wellsoft.pt.bpm.engine.executor.param.GotoTaskParam;
import com.wellsoft.pt.bpm.engine.executor.param.HandOverParam;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.StartNode;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.parser.activity.*;
import com.wellsoft.pt.bpm.engine.query.*;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.timer.TimerExecutor;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.bpm.engine.util.ReservedFieldUtils;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.common.translate.service.TranslateService;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.log.LogEvent;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.support.ContextLogs;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.IgnoreLoginUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.support.TimerWorkTime;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import com.wellsoft.pt.workflow.service.impl.FlowFormatServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 流程任务的操作服务实现类
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
@Service
@Transactional
public class TaskServiceImpl extends BaseServiceImpl implements TaskService {

    private static final String TASK_LOCK_CACHE_ID = ModuleID.WORKFLOW.getName();
    private static final String QUERY_TODO_TASKS = "from TaskInstance task_instance where task_instance.flowInstance.uuid = :flowInstUuid and task_instance.endTime is null";
    private static final String QUERY_TODO_TASK_INST_UUIDS = "select task_instance.uuid from TaskInstance task_instance where task_instance.flowInstance.uuid = :flowInstUuid and task_instance.endTime is null";
    private static final String QUERY_FINISHED_TASKS = "from TaskInstance task_instance where task_instance.flowInstance.uuid = :flowInstUuid and task_instance.endTime is not null";
    private static final String QUERY_ALL_TASKS_BY_FLOW_INST_UUID = "from TaskInstance task_instance where task_instance.flowInstance.uuid = :flowInstUuid";
    private static final String QUERY_FINISHED_TASK_UUIDS = "select task_instance.uuid from TaskInstance task_instance where task_instance.flowInstance.uuid = :flowInstUuid and task_instance.endTime is not null";
    private static final String IS_ALLOWED_ROLLBACK_TO_BASK = "select count(uuid) from TaskIdentity t where t.userId = :userId and t.taskInstUuid = :taskInstUuid and t.suspensionState = 0";
    private static final String GET_OTHER_UNFINISHED_PARALLEL_TASK = "from TaskInstance t where t.uuid != :taskInstUuid and t.parallelTaskInstUuid = :parallelTaskInstUuid and t.endTime is null";
    private static final String LIST_UNFINISHED_PARALLEL_TASK = "from TaskInstance t where t.parallelTaskInstUuid in(:parallelTaskInstUuids) and t.endTime is null";
    private static final String GET_OTHER_PARALLEL_TASK = "from TaskInstance t where t.uuid <> :taskInstUuid and t.parallelTaskInstUuid = :parallelTaskInstUuid";
    @Autowired
    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private TaskInstanceService taskInstanceService;
    @Autowired
    private TaskOperationService taskOperationService;
    @Autowired
    private TaskInfoDistributionService taskInfoDistributionService;
    @Autowired
    private TaskActivityDao taskActivityDao;
    @Autowired
    private TaskActivityService taskActivityService;
    @Autowired(required = false)
    private Map<String, FlowListener> listenerMap;
    @Autowired
    private AclTaskService aclTaskService;
    @Autowired
    private WorkFlowAclServiceWrapper aclServiceWrapper;
    @Autowired
    private FlowPermissionEvaluator flowPermissionEvaluator;
    @Autowired
    private ReadMarkerService readMarkerService;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private TranslateService translateService;
    @Autowired
    private DyFormFacade dyFormFacade;
    //    @Autowired
//    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private TaskDelegationService taskDelegationService;
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;
    @Autowired
    private FlowManagementService flowManagementService;
    //    @Autowired
//    private UserOperationLogService userOperationLogService;
    @Autowired
    private TimerExecutor timerExecutor;
    @Autowired
    private TaskTimerService taskTimerService;
    @Autowired
    private TaskTimerUserService taskTimerUserService;
    @Autowired
    private TaskTimerLogService taskTimerLogService;
    @Autowired
    private TsTimerFacadeService timerFacadeService;
    @Autowired
    private TaskBranchService taskBranchService;
    @Autowired
    private TaskSubFlowService taskSubFlowService;
    @Autowired
    private TaskSubFlowRelationService taskSubFlowRelationService;
    @Autowired
    private TaskSubFlowDispatchService taskSubFlowDispatchService;
    @Autowired
    private TaskFormOpinionService taskFormOpinionService;
    @Autowired
    private TaskFormAttachmentService taskFormAttachmentService;
    @Autowired
    private TaskFormAttachmentLogService taskFormAttachmentLogService;
    @Autowired
    private TaskFormOpinionLogService taskFormOpinionLogService;
    @Autowired
    private TaskInstanceToppingService taskInstanceToppingService;
    //    @Autowired
//    private SecurityAuditFacadeService securityApiFacade;
    @Autowired
    private FlowIndexDocumentService flowIndexDocumentService;
    @Autowired
    private FlowSchemaService flowSchemaService;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private WfFlowSettingService flowSettingService;
    @Autowired
    private FlowService flowService;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    @Autowired
    private FlowSamplerService flowSamplerService;

    /**
     * @param userId
     * @param topItem
     * @return
     */
    private static TaskOperationItem getTaskOperationItem(String userId, TaskActivityItem topItem) {
        TaskOperationStack stack = topItem.getOperationStack();
        while (!stack.isEmpty()) {
            TaskOperationItem taskOperationItem = stack.pop();
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
        // List<TaskOperationItem> operationItems = topItem.getOperationItems();
        // for (int index = operationItems.size() - 1; index >= 0; index--) {
        // TaskOperationItem taskOperationItem = operationItems.get(index);
        // if (userId.equals(taskOperationItem.getOperator())) {
        // return taskOperationItem;
        // }
        // }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getUnfinishedTasks(java.lang.String)
     */
    @Override
    public List<TaskInstance> getUnfinishedTasks(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.taskInstanceService.find(QUERY_TODO_TASKS, values);
    }

    @Override
    public List<String> getUnfinishedTaskUuids(String flowInstUuid) {
        List<TaskInstance> unfinishTasks = this.getUnfinishedTasks(flowInstUuid);
        if (CollectionUtils.isNotEmpty(unfinishTasks)) {
            return Lists.transform(unfinishTasks, new Function<TaskInstance, String>() {
                @Override
                public String apply(TaskInstance taskInstance) {
                    return taskInstance.getUuid();
                }
            });
        }
        return null;
    }

    @Override
    public String getLastTaskInstanceUuidByFlowInstUuid(String flowInstUuid) {
        TaskInstance instance = this.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
        return instance != null ? instance.getUuid() : null;
    }

    /**
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    @Override
    public String getLastTaskInstanceUuidByTaskInstUuidAndFlowInstUuid(String taskInstUuid, String flowInstUuid) {
        String taskFlowInstUuid = flowInstUuid;
        if (StringUtils.isNotBlank(taskInstUuid)) {
            TaskInstance taskInstance = getTask(taskInstUuid);
            if (taskInstance != null && taskInstance.getEndTime() == null) {
                return taskInstUuid;
            } else if (taskInstance != null && StringUtils.isBlank(taskFlowInstUuid)) {
                taskFlowInstUuid = taskInstance.getFlowInstance().getUuid();
            }
        }
        return getLastTaskInstanceUuidByFlowInstUuid(taskFlowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getUnfinishedTaskInstanceUuids(java.lang.String)
     */
    @Override
    public List<String> getUnfinishedTaskInstanceUuids(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.taskInstanceService.listCharSequenceByHQL(QUERY_TODO_TASK_INST_UUIDS, values);
    }

    /**
     * 根据并行环节实例UUID，获取流程实例未完成的任务列表
     *
     * @param parallelTaskInstUuids
     * @return
     */
    @Override
    public List<TaskInstance> listUnfinishedTaskByParallelTaskInstUuids(List<String> parallelTaskInstUuids) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("parallelTaskInstUuids", parallelTaskInstUuids);
        return taskInstanceService.find(LIST_UNFINISHED_PARALLEL_TASK, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getFinishedTasks(java.lang.String)
     */
    @Override
    public List<TaskInstance> getFinishedTasks(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.taskInstanceService.find(QUERY_FINISHED_TASKS, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getFinishedTaskInstanceUuids(java.lang.String)
     */
    @Override
    public List<String> getFinishedTaskInstanceUuids(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.taskInstanceService.listCharSequenceByHQL(QUERY_FINISHED_TASK_UUIDS, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#countUnfinishedTasks(java.lang.String)
     */
    @Override
    public long countUnfinishedTasks(String flowInstUuid) {
        return this.taskInstanceService.countUnfinishedTasks(flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTodoTasks(java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskInstance> getTodoTasks(String userId, String flowInstUuid) {
//        QueryInfo<TaskInstance> queryInfo = new QueryInfo<TaskInstance>();
//        queryInfo.getPage().setPageNo(1);
//        queryInfo.getPage().setPageSize(100);
//        queryInfo.getPage().setAutoCount(false);
//        queryInfo.addOrderby("createTime", "desc");
//        queryInfo.setWhereHql("o.flowInstance.uuid = :flowInstUuid");
//        queryInfo.addQueryParams("flowInstUuid", flowInstUuid);
//        aclTaskService.query(TaskInstance.class, queryInfo, AclPermission.TODO, userId);
        String hql = "from TaskInstance t where t.flowInstance.uuid = :flowInstUuid and exists(select 1 from AclTaskEntry e where e.objectIdIdentity = t.uuid and e.sid = :userId and e.todoAuth = :todoAuth) order by t.createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("flowInstUuid", flowInstUuid);
        params.put("userId", userId);
        params.put("todoAuth", true);
        return taskInstanceService.listByHQL(hql, params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getAllByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskInstance> getAllByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.taskInstanceService.find(QUERY_ALL_TASKS_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public TaskInstance save(FlowDefinition flowDefinition, FlowInstance flowInstance, TaskInstance parentTask,
                             Node taskNode) {
        TaskInstance task = new TaskInstance();
        task.setName(taskNode.getName());
        task.setId(taskNode.getId());
        task.setType(Integer.valueOf(taskNode.getType()));
        task.setFlowDefinition(flowDefinition);
        task.setFlowInstance(flowInstance);
        // task.setAssignee(SpringSecurityUtils.getCurrentUserName());
        task.setStartTime(Calendar.getInstance().getTime());

        // 任务父节点
        task.setParent(parentTask);

        taskInstanceService.save(task);

        return task;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#save(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public void save(TaskInstance task) {
        taskInstanceService.save(task);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void delete(String userId, String taskInstUuid) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        if (isAllowedDelete(userId, taskInstUuid)) {
            if (workFlowSettings.isTodoPhysicalDelete()) {
                TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
                deleteTask(userId, taskInstance);
            } else {
                logicalDeleteByAdmin(userId, taskInstUuid);
            }
        } else {
            throw new WorkFlowException("任务当前所在的状态不允许删除!");
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#deleteByAdmin(java.lang.String, java.lang.String)
     */

    @Override
    public void deleteByAdmin(String userId, String taskInstUuid) {
        delete(userId, taskInstUuid);
    }

    @Override
    public void logicalDeleteByAdmin(String userId, String taskInstUuid) {
        TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
        Integer falseDeleteByAdmin = SuspensionState.LOGIC_DELETED.getState();
        taskInstance.setSuspensionState(falseDeleteByAdmin);
        taskInstanceService.save(taskInstance);

        // 用户操作日志
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        BusinessOperationLog bussinessLog = new BusinessOperationLog();
        bussinessLog.setModuleId(flowDefinition.getModuleId());
        bussinessLog.setDataDefType(ModuleID.WORKFLOW.getValue());
        bussinessLog.setDataDefId(flowDefinition.getId());
        bussinessLog.setDataDefName(flowDefinition.getName());
        bussinessLog.setOperation(WorkFlowOperation.getName(WorkFlowOperation.DELETE));
        bussinessLog.setUserId(SpringSecurityUtils.getCurrentUserId());
        bussinessLog.setUserName(SpringSecurityUtils.getCurrentUserName());
        bussinessLog.setDataId(flowInstance.getUuid());
        bussinessLog.setDataName(flowInstance.getTitle());
        Map<String, Object> details = Maps.newHashMap();
        ContextLogs.sendLogEvent(new LogEvent(bussinessLog, details));

        TaskData taskData = new TaskData();
        String key = taskInstUuid + userId;
        taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.DELETE));
        taskData.setActionType(key, WorkFlowOperation.DELETE);
        flowSamplerService.createSnapshot(taskData, null, taskInstUuid, flowInstance);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#forceDelete(java.lang.String, java.lang.String)
     */
    @Override
    public void forceDelete(String userId, String taskInstUuid) {
        TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
        deleteTask(userId, taskInstance);
    }

    /**
     * 删除任务
     *
     * @param taskInstance
     */
    public void deleteTask(String userId, TaskInstance taskInstance) {
        if (taskInstance == null) {
            return;
        }

//        Set<TaskInstance> taskInstances = taskInstance.getChildren();
//        for (TaskInstance child : taskInstances) {
//            deleteTask(userId, child);
//        }

        // Date endTime = Calendar.getInstance().getTime();
        //
        // // 1、更改任务实例为删除状态
        // taskInstance.setSuspensionState(SuspensionState.DELETED.getState());
        // taskInstance.setEndTime(endTime);
        // taskInstanceService.save(taskInstance);
        //
        // // 2、流程实例设置禁用状态
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        Set<FlowInstance> flowInstances = flowInstance.getChildrens();
        List<FlowInstance> flowInstanceList = Lists.newArrayList(flowInstances);
        flowInstanceList.forEach(child -> {
            TaskInstance childTaskInstance = getLastTaskInstanceByFlowInstUuid(child.getUuid());
            if (childTaskInstance != null) {
                flowInstances.remove(child);
                deleteTask(userId, childTaskInstance);
            } else {
                flowService.deleteDraft(userId, child.getUuid());
            }
        });
        // flowInstance.setIsActive(false);
        // flowInstance.setEndTime(endTime);
        // flowInstanceService.save(flowInstance);
        //
        // // 3、删除ACL权限
        // aclService.removeAcl(TaskInstance.class, taskInstance.getUuid());

        // 4、停止计时器
        timerExecutor.stop(flowInstance);

        // 5、发布流程删除事件
        // 表单数据
        String formUuid = flowInstance.getFormUuid();
        String dataUuid = flowInstance.getDataUuid();
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        TaskData taskData = new TaskData();
        taskData.setUserId(userId);
        taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
        taskData.setFormUuid(formUuid);
        taskData.setDataUuid(dataUuid);
        taskData.setDyFormData(dataUuid, dyFormData);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        String[] listeners = flowDelegate.getStartNode().getListeners();
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstance.getUuid());
        example.setName(CustomRuntimeData.KEY_FLOW_LISTENER);
        List<FlowInstanceParameter> flowInstanceParameters = flowInstanceParameterService.findByExample(example);
        for (FlowInstanceParameter flowInstanceParameter : flowInstanceParameters) {
            listeners = (String[]) ArrayUtils.addAll(listeners, StringUtils.split(flowInstanceParameter.getValue(), Separator.SEMICOLON.getValue()));
        }
        ExecutionContext executionContext = null;
        if (listenerMap != null && !listenerMap.isEmpty()) {
            Token token = new Token(flowInstance, taskData);
            executionContext = new ExecutionContext(token);
            Event event = AbstractHandler.getEvent(flowDelegate.getTaskNode(taskInstance.getId()), Listener.FLOW,
                    executionContext);
            // task#8100: 跳转环节触发环节的监听器
            // if (!WorkFlowOperation.GOTO_TASK.equals(event.getActionType())) {
            for (String listener : listeners) {
                FlowListener flowListener = listenerMap.get(listener);
                if (flowListener == null) {
                    continue;
                }
                try {
                    flowListener.onDeleted(event);
                } catch (Exception e) {
                    String errorString = ExceptionUtils.getStackTrace(e);
                    logger.error(errorString);
                    if (e instanceof WorkFlowException) {
                        throw (WorkFlowException) e;
                    } else {
                        throw new WorkFlowException(
                                "流程实例事件监听器" + "[" + flowListener.getName() + "]" + "执行流程实例删除事件处理出现异常: " + errorString);
                    }
                }
                // }
            }
        }
        // 删除全文检索ES内容
        taskData.setFlowInstUuid(flowInstance.getUuid());
        flowIndexDocumentService.deleteIndex(taskData);
//        // 查询子流程实例更新子流程删除
//        List<TaskSubFlow> subFlows = taskSubFlowService.getAllByParentFlowInstUuid(flowInstance.getUuid());
//        if (subFlows != null && subFlows.size() > 0) {
//            for (TaskSubFlow subFlow : subFlows) {
//                TaskData subTaskData = new TaskData();
//                subTaskData.setFlowInstUuid(subFlow.getFlowInstUuid());
//                flowIndexDocumentService.deleteIndex(subTaskData);
//            }
//        }

        // 执行事件脚本
        executeEventScript(taskInstance, flowInstance, flowDelegate, taskData, executionContext, Pointcut.DELETED);

        // // 6、保存操作记录
        // TaskData taskData = new TaskData();
        // taskData.setUserId(userId);
        // taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
        // String key = taskInstance.getUuid() + userId;
        // // 办理意见
        // taskData.setOpinionLabel(key, null);
        // taskData.setOpinionValue(key, null);
        // taskData.setOpinionText(key, null);
        // taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.DELETE),
        // ActionCode.DELETE.getCode(), WorkFlowOperation.DELETE, "", "", "",
        // userId, null, null, null, null,
        // taskInstance, taskInstance.getFlowInstance(), taskData);

        // 物理删除处理
        // if
        // (ConfigKeyUtils.VALUE_TRUE.equalsIgnoreCase(Config.getValue(ConfigKeyUtils.PHYSICAL_DELETE_ENABLE)))
        // {
        // }

        // 用户操作日志
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        BusinessOperationLog bussinessLog = new BusinessOperationLog();
        bussinessLog.setModuleId(flowDefinition.getModuleId());
        bussinessLog.setDataDefType(ModuleID.WORKFLOW.getValue());
        bussinessLog.setDataDefId(flowDefinition.getId());
        bussinessLog.setDataDefName(flowDefinition.getName());
        bussinessLog.setOperation(WorkFlowOperation.getName(WorkFlowOperation.DELETE));
        bussinessLog.setUserId(SpringSecurityUtils.getCurrentUserId());
        bussinessLog.setUserName(SpringSecurityUtils.getCurrentUserName());
        bussinessLog.setDataId(flowInstance.getUuid());
        bussinessLog.setDataName(flowInstance.getTitle());
        Map<String, Object> details = Maps.newHashMap();
        ContextLogs.sendLogEvent(new LogEvent(bussinessLog, details));

        physicalDelete(userId, taskInstance);
    }

    /**
     * 如何描述该方法
     *
     * @param flowInstance
     * @param flowDelegate
     * @param pointcut
     */
    private void executeEventScript(TaskInstance taskInstance, FlowInstance flowInstance, FlowDelegate flowDelegate,
                                    TaskData taskData, ExecutionContext executionContext, String pointcut) {
        Script script = flowDelegate.getFlowEventScript(pointcut);
        if (script == null) {
            return;
        }
        Token token = new Token(flowInstance, taskData);
        ExecutionContext ec = executionContext;
        if (ec == null) {
            ec = new ExecutionContext(token);
        }
        Event event = AbstractHandler.getEvent(flowDelegate.getTaskNode(taskInstance.getId()), Listener.FLOW, ec);
        WorkFlowScriptHelper.executeEventScript(event, script);
    }

    /**
     * 如何描述该方法
     *
     * @param userId
     * @param taskInstance
     */
    private void physicalDelete(String userId, TaskInstance taskInstance) {
        Map<String, Object> map = new HashMap<String, Object>();

        FlowDefinition flowDefinition = taskInstance.getFlowDefinition();
        String flowDefUuid = flowDefinition.getUuid();
        String flowInstUuid = taskInstance.getFlowInstance().getUuid();
        FlowSchema flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());//  flowDefinition.getFlowSchema();
        // 流程定义(WF_FLOW_DEFINITION、WF_FLOW_SCHEMA)
        FlowDefinitionDeleteQueryItem flowDefinitionDeleteQueryItem = new FlowDefinitionDeleteQueryItem();
        BeanUtils.copyProperties(flowDefinition, flowDefinitionDeleteQueryItem);
        flowDefinitionDeleteQueryItem.setFlowSchemaUuid(flowSchema.getUuid());

        FlowSchemaDeleteQueryItem flowSchemaDeleteQueryItem = new FlowSchemaDeleteQueryItem();
        flowSchemaDeleteQueryItem.setUuid(flowSchema.getUuid());
        flowSchemaDeleteQueryItem.setCreator(flowSchema.getCreator());
        flowSchemaDeleteQueryItem.setCreateTime(flowSchema.getCreateTime());
        flowSchemaDeleteQueryItem.setModifier(flowSchema.getModifier());
        flowSchemaDeleteQueryItem.setModifyTime(flowSchema.getModifyTime());
        flowSchemaDeleteQueryItem.setRecVer(flowSchema.getRecVer());
        flowSchemaDeleteQueryItem.setName(flowSchema.getName());
        flowSchemaDeleteQueryItem.setContent(flowSchema.getContentAsString());
        flowSchemaDeleteQueryItem.setDefinitionJson(flowSchema.getDefinitionJsonAsString());

        map.put("flowDefinition", flowDefinitionDeleteQueryItem);
        map.put("flowSchema", flowSchemaDeleteQueryItem);

        // 流程实例(WF_FLOW_INSTANCE、WF_FLOW_INSTANCE_PARAM)
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        FlowInstanceDeleteQueryItem flowInstanceDeleteQueryItem = new FlowInstanceDeleteQueryItem();
        BeanUtils.copyProperties(flowInstance, flowInstanceDeleteQueryItem);
        flowInstanceDeleteQueryItem.setFlowDefUuid(flowDefUuid);
        if (flowInstance.getParent() != null) {
            flowInstanceDeleteQueryItem.setParentFlowInstUuid(flowInstance.getParent().getUuid());
        }
        List<FlowInstanceParameter> flowInstanceParameters = flowInstanceParameterService
                .getByFlowInstanceUuid(flowInstUuid);

        map.put("flowInstance", flowInstanceDeleteQueryItem);
        map.put("flowInstanceParameters", flowInstanceParameters);

        // 环节实例(WF_TASK_INSTANCE)
        List<TaskInstance> taskInstances = taskInstanceService.getByFlowInstUuid(flowInstUuid);
        List<TaskInstanceDeleteQueryItem> taskInstanceDeleteQueryItems = new ArrayList<TaskInstanceDeleteQueryItem>();
        for (TaskInstance ti : taskInstances) {
            TaskInstanceDeleteQueryItem taskInstanceDeleteQueryItem = new TaskInstanceDeleteQueryItem();
            BeanUtils.copyProperties(ti, taskInstanceDeleteQueryItem);
            taskInstanceDeleteQueryItem.setFlowDefUuid(flowDefUuid);
            taskInstanceDeleteQueryItem.setFlowInstUuid(flowInstUuid);
            if (ti.getParent() != null) {
                taskInstanceDeleteQueryItem.setParallelTaskInstUuid(ti.getParent().getUuid());
            }
            taskInstanceDeleteQueryItems.add(taskInstanceDeleteQueryItem);
        }
        map.put("taskInstances", taskInstanceDeleteQueryItems);

        // 环节置顶(WF_TASK_INSTANCE_TOPPING)
        List<TaskInstanceTopping> taskInstanceToppings = taskInstanceToppingService.listByTaskInstUuid(taskInstance.getUuid());
        map.put("taskInstanceToppings", taskInstanceToppings);

        // 分支流(WF_TASK_BRANCH)
        List<TaskBranch> taskBranches = taskBranchService.getByFlowInstUuid(flowInstUuid);
        map.put("taskBranches", taskBranches);

        // 环节流转(WF_TASK_ACTIVITY)
        List<TaskActivity> taskActivities = taskActivityService.getByFlowInstUuid(flowInstUuid);
        map.put("taskActivities", taskActivities);

        // 环节操作(WF_TASK_OPERATION)
        List<TaskOperation> taskOperations = taskOperationService.getAllTaskOperationByFlowInstUuid(flowInstUuid);
        map.put("taskOperations", taskOperations);
        // 流程办理意见附件
        List<LogicFileInfo> taskOperationFiles = Lists.newArrayList();
        taskOperations.forEach(taskOperation -> {
            taskOperationFiles.addAll(mongoFileService.getNonioFilesFromFolder(flowInstUuid, taskOperation.getUuid()));
        });
        map.put("taskOperationFiles", taskOperationFiles);

        // 环节待办信息(WF_TASK_IDENTITY)
        List<TaskIdentity> taskIdentities = identityService.getByFlowInstUuid(flowInstUuid);
        map.put("taskIdentities", taskIdentities);

        // 环节委托(WF_TASK_DELEGATION)
        List<TaskDelegation> taskDelegations = taskDelegationService.getByFlowInstUuid(flowInstUuid);
        map.put("taskDelegations", taskDelegations);

        // 流程计时系统(WF_TASK_TIMER、WF_TASK_TIMER_USER、WF_TASK_TIMER_LOG)
        List<TaskTimer> taskTimers = taskTimerService.getByFlowInstUuid(flowInstUuid);
        List<TaskTimerUser> taskTimerUsers = new ArrayList<TaskTimerUser>();
        for (TaskTimer taskTimer : taskTimers) {
            String taskTimerUuid = taskTimer.getUuid();
            List<TaskTimerUser> timerUsers = taskTimerUserService.getByTaskTimerUuid(taskTimerUuid);
            taskTimerUsers.addAll(timerUsers);
        }
        List<TaskTimerLog> taskTimerLogs = taskTimerLogService.getByFlowInstUuid(flowInstUuid);
        map.put("taskTimers", taskTimers);
        map.put("taskTimerUsers", taskTimerUsers);
        map.put("taskTimerLogs", taskTimerLogs);

        // 子流程信息(WF_TASK_SUB_FLOW_DISPATCH、WF_TASK_SUB_FLOW、WF_TASK_SUB_FLOW_RELATION)
        List<TaskSubFlowDispatch> taskSubFlowDispatches = taskSubFlowDispatchService.getByFlowInstUuid(flowInstUuid);
        List<TaskSubFlow> taskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstUuid);
        List<TaskSubFlowDeleteQueryItem> taskSubFlowDeleteQueryItems = new ArrayList<TaskSubFlowDeleteQueryItem>();
        for (TaskSubFlow subFlow : taskSubFlows) {
            TaskSubFlowDeleteQueryItem taskSubFlowDeleteQueryItem = new TaskSubFlowDeleteQueryItem();
            BeanUtils.copyProperties(subFlow, taskSubFlowDeleteQueryItem);
            taskSubFlowDeleteQueryItems.add(taskSubFlowDeleteQueryItem);
        }
        List<TaskSubFlowRelationDeleteQueryItem> taskSubFlowRelationDeleteQueryItems = new ArrayList<TaskSubFlowRelationDeleteQueryItem>();
        for (TaskSubFlow taskSubFlow : taskSubFlows) {
            List<TaskSubFlowRelation> subFlowRelations = taskSubFlowRelationService.getByTaskSubFlowUuid(taskSubFlow.getUuid());
            for (TaskSubFlowRelation taskSubFlowRelation : subFlowRelations) {
                TaskSubFlowRelationDeleteQueryItem taskSubFlowRelationDeleteQueryItem = new TaskSubFlowRelationDeleteQueryItem();
                BeanUtils.copyProperties(taskSubFlowRelation, taskSubFlowRelationDeleteQueryItem);
                taskSubFlowRelationDeleteQueryItem.setTaskSubFlowUuid(taskSubFlow.getUuid());
                taskSubFlowRelationDeleteQueryItems.add(taskSubFlowRelationDeleteQueryItem);
            }
        }
        map.put("taskSubFlowDispatches", taskSubFlowDispatches);
        map.put("taskSubFlows", taskSubFlowDeleteQueryItems);
        map.put("taskSubFlowRelations", taskSubFlowRelationDeleteQueryItems);

        // 信息分发(WF_TASK_INFO_DISTRIBUTION)
        List<TaskInfoDistribution> taskInfoDistributions = taskInfoDistributionService.listByFlowInstUuid(flowInstUuid);
        map.put("taskInfoDistributions", taskInfoDistributions);
        List<String> taskInfoDistributionUuids = taskInfoDistributions.stream().map(taskInfoDistribution -> taskInfoDistribution.getUuid()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(taskInfoDistributionUuids)) {
            map.put("taskInfoDistributionFiles", mongoFileService.getNonioFilesFromFolders(taskInfoDistributionUuids));
        }

        // 流程表单意见(WF_TASK_FORM_OPINION、WF_TASK_FORM_OPINION_LOG)
        List<TaskFormOpinion> taskFormOpinions = taskFormOpinionService.getByFlowInstUuid(flowInstUuid);
        List<TaskFormOpinionLog> taskFormOpinionLogs = taskFormOpinionLogService.getByFlowInstUuid(flowInstUuid);
        map.put("taskFormOpinions", taskFormOpinions);
        map.put("taskFormOpinionLogs", taskFormOpinionLogs);

        // 表单附件(WF_TASK_FORM_ATTACHMENT、WF_TASK_FORM_ATTACHMENT_LOG)
        List<TaskFormAttachment> taskFormAttachments = taskFormAttachmentService.getByFlowInstUuid(flowInstUuid);
        List<TaskFormAttachmentLog> taskFormAttachmentLogs = taskFormAttachmentLogService.listByFlowInstUuid(flowInstUuid);
        map.put("taskFormAttachments", taskFormAttachments);
        map.put("taskFormAttachmentLogs", taskFormAttachmentLogs);

        // ACL权限
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("objectIdIdentity", taskInstance.getUuid());
        List<AclObjectIdentityDeleteQueryItem> aclObjectIdentityDeleteQueryItems = this.nativeDao
                .namedQuery("getAclObjectIdentityQueryItem", values, AclObjectIdentityDeleteQueryItem.class);
        List<AclEntryDeleteQueryItem> aclEntryDeleteQueryItems = this.nativeDao.namedQuery("aclEntryQueryItem", values,
                AclEntryDeleteQueryItem.class);
        map.put("aclObjectIdentities", aclObjectIdentityDeleteQueryItems);
        map.put("aclEntries", aclEntryDeleteQueryItems);

        // 表单定义
        String formUuid = taskInstance.getFormUuid();
        String dataUuid = taskInstance.getDataUuid();
        DyFormFormDefinition dyFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        DyFormDefinitionDeleteQueryItem dyFormDefinitionDeleteQueryItem = new DyFormDefinitionDeleteQueryItem(
                dyFormDefinition);

        /*
         * try { if (dyFormDefinition.getHtml() != null) {
         * dyFormDefinitionDeleteQueryItem
         * .setHtml(IOUtils.toString(dyFormDefinition.getHtml() .getCharacterStream()));
         * } } catch (Exception e) { logger.error(ExceptionUtils.getStackTrace(e)); }
         */
        map.put("formDefinition", dyFormDefinitionDeleteQueryItem);
        // 表单数据
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        map.put("dyFormData", dyFormData);

        // 流程名称
        String name = flowInstance.getName();
        // 流程实例标题
        String title = flowInstance.getTitle();
        // 流水号
        String serialNo = taskInstance.getSerialNo();
        // 流程环节
        String taskName = taskInstance.getName();
        // 操作人ID
        String operator = SpringSecurityUtils.getCurrentUserId();
        // 操作人名称
        String operatorName = SpringSecurityUtils.getCurrentUserName();
        // 操作类型
        String operation = WorkFlowOperation.getName(WorkFlowOperation.DELETE);
        // 操作时间
        Date operateTime = Calendar.getInstance().getTime();
        // 删除的数据内容
        String content = JsonUtils.object2Json(map);
        TaskDeleteLog log = new TaskDeleteLog();
        log.setName(name);
        log.setTitle(title);
        log.setSerialNo(serialNo);
        log.setTaskName(taskName);
        log.setOperator(operator);
        log.setOperatorName(operatorName);
        log.setOperation(operation);
        log.setOperateTime(operateTime);
        log.setContent(content);
        log.setRemark("删除流程流转信息");
        this.dao.save(log);

        // 删除流程发送的在线消息
        messageClientApiFacade.deleteByCorrelationId(flowInstUuid);

        // 1、删除动态表单数据
        dyFormFacade.delFullFormData(formUuid, dataUuid);
        // 2、删除ACL权限
        aclTaskService.removeAcl(taskInstance.getUuid());
        // 3、删除流程表单意见、表单附件
        taskFormOpinionLogService.removeByFlowInstUuid(flowInstUuid);
        taskFormOpinionService.removeByFlowInstUuid(flowInstUuid);
        taskFormAttachmentLogService.removeByFlowInstUuid(flowInstUuid);
        taskFormAttachmentService.removeByFlowInstUuid(flowInstUuid);
        // 4、删除子流程信息
        taskSubFlowDispatchService.removeByFlowInstUuid(flowInstUuid);
        for (TaskSubFlow taskSubFlow : taskSubFlows) {
            taskSubFlowRelationService.removeByTaskSubFlowUuid(taskSubFlow.getUuid());
        }
        taskSubFlowService.removeByFlowInstUuid(flowInstUuid);
        // 删除信息分发
        taskInfoDistributionService.removeByFlowInstUuid(flowInstUuid);
        if (CollectionUtils.isNotEmpty(taskInfoDistributionUuids)) {
            taskInfoDistributionUuids.forEach(folderUuid -> {
                mongoFileService.popAllFilesFromFolder(folderUuid);
            });
        }
        // 5、删除流程计时系统
        for (TaskTimer taskTimer : taskTimers) {
            taskTimerLogService.removeByTaskTimerUuid(taskTimer.getUuid());
            taskTimerUserService.removeByTaskTimerUuid(taskTimer.getUuid());
        }
        taskTimerService.removeByFlowInstUuid(flowInstUuid);
        // 6、删除环节委托
        taskDelegationService.removeByFlowInstUuid(flowInstUuid);
        // 7、删除环节待办信息
        identityService.removeByFlowInstUuid(flowInstUuid);
        // 8、删除环节操作
        taskOperationService.removeByFlowInstUuid(flowInstUuid);
        taskOperations.forEach(taskOperation -> {
            mongoFileService.popAllFilesFromFolder(flowInstUuid, taskOperation.getUuid());
        });
        // 9、删除环节流转
        taskActivityService.removeByFlowInstUuid(flowInstUuid);
        // 删除环节置顶
        taskInstanceToppingService.removeByTaskInstUuid(taskInstance.getUuid());
        // 10、删除环节分支实例
        taskBranchService.removeByFlowInstUuid(flowInstUuid);
        // 11、删除环节实例
        taskInstanceService.removeByFlowInstUuid(flowInstUuid);
        // 12、删除流程归档
        List<FlowInstanceParameter> archiveBotFormDataParameters = Lists.newArrayList();
        flowInstanceParameters.forEach(flowInstanceParameter -> {
            // 归档文件
            if (StringUtils.equals("archiveFileUuids", flowInstanceParameter.getName()) && StringUtils.isNotBlank(flowInstanceParameter.getValue())) {
                List<String> fileUuids = Arrays.asList(StringUtils.split(flowInstanceParameter.getValue(), Separator.SEMICOLON.getValue()));
                for (String fileUuid : fileUuids) {
                    dmsFileServiceFacade.physicalDeleteFileWithoutPermission(fileUuid);
                }
            } else if (StringUtils.startsWith(flowInstanceParameter.getName(), "archiveId_")) {
                archiveBotFormDataParameters.add(flowInstanceParameter);
            }
        });
        if (CollectionUtils.isNotEmpty(archiveBotFormDataParameters)) {
            archiveBotFormDataParameters.forEach(parameter -> {
                String parameterName = parameter.getName();
                if (StringUtils.endsWith(parameterName, "_formUuid")) {
                    FlowInstanceParameter dataUuidParameter = archiveBotFormDataParameters.stream().filter(item -> {
                        if (StringUtils.endsWith(item.getName(), "_dataUuid") &&
                                StringUtils.equals(parameterName, StringUtils.replace(item.getName(), "_dataUuid", "_formUuid"))) {
                            return true;
                        }
                        return false;
                    }).findFirst().orElse(null);
                    if (dataUuidParameter != null) {
                        dyFormFacade.delFullFormData(parameter.getValue(), dataUuidParameter.getValue());
                    }
                }
            });
        }
        // 13、删除流程实例
        flowInstanceParameterService.removeByFlowInstUuid(flowInstUuid);
        flowInstanceService.remove(flowInstUuid);
        flowInstanceService.flushSession();
        flowInstanceService.clearSession();
    }

    @Override
    @Transactional
    public void recover(String userId, String taskInstUuid) {
        TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
        taskInstance.setSuspensionState(SuspensionState.NORMAL.getState());
        taskInstanceService.save(taskInstance);

        // 用户操作日志
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        BusinessOperationLog bussinessLog = new BusinessOperationLog();
        bussinessLog.setModuleId(flowDefinition.getModuleId());
        bussinessLog.setDataDefType(ModuleID.WORKFLOW.getValue());
        bussinessLog.setDataDefId(flowDefinition.getId());
        bussinessLog.setDataDefName(flowDefinition.getName());
        bussinessLog.setOperation(WorkFlowOperation.getName(WorkFlowOperation.DELETE));
        bussinessLog.setUserId(userId);
        bussinessLog.setUserName(workflowOrgService.getNameById(userId));
        bussinessLog.setDataId(flowInstance.getUuid());
        bussinessLog.setDataName(flowInstance.getTitle());
        Map<String, Object> details = Maps.newHashMap();
        ContextLogs.sendLogEvent(new LogEvent(bussinessLog, details));

        TaskData taskData = new TaskData();
        String key = taskInstUuid + userId;
        taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.RECOVER));
        taskData.setActionType(key, WorkFlowOperation.RECOVER);
        flowSamplerService.createSnapshot(taskData, null, taskInstUuid, flowInstance);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void remark(String uuid) {
        // TODO Auto-generated method stub

    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void remark(TaskInstance task) {
        // TODO Auto-generated method stub

    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void submit(String taskUid, TaskData taskData) {
        TaskInstance task = taskInstanceService.get(taskUid);
        submit(task, taskData);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void submit(TaskInstance taskInstance, TaskData taskData) {
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        // 设置不是启动新流程
        taskData.setStartNewFlow(flowInstance.getUuid(), false);

        // 更新流程标题
        String fromTitle = taskData.getTitle(flowInstance.getId());
        if (StringUtils.isNotBlank(fromTitle)) {
            flowInstance.setTitle(fromTitle);
        }
        if (StringUtils.isBlank(taskData.getFormUuid())) {
            taskData.setFormUuid(taskInstance.getFormUuid());
        }
        if (StringUtils.isBlank(taskData.getDataUuid())) {
            taskData.setDataUuid(taskInstance.getDataUuid());
        }

        if (taskData.getCustomDataKeySet().isEmpty()) {
            List<FlowInstanceParameter> parameters = flowInstanceParameterService
                    .getByFlowInstanceUuid(flowInstance.getUuid());
            for (FlowInstanceParameter parameter : parameters) {
                taskData.setCustomData(parameter.getName(), parameter.getValue());
            }
        }

        // 提交任务
        String taskInstUuid = taskInstance.getUuid();
        String userId = taskData.getUserId();
        String key = taskInstUuid + userId;
        TaskIdentity taskIdentity = null;
        String taskIdentityUuid = taskData.getTaskIdentityUuid(key);
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            taskIdentity = identityService.get(taskIdentityUuid);
        }

        List<TaskIdentity> taskIdentities = new ArrayList<TaskIdentity>();
        if (taskIdentity == null) {
            taskIdentities = identityService.getTodoByTaskInstUuidAndUserDetails(taskInstUuid,
                    taskData.getUserDetails());
        } else {
            taskIdentities.add(taskIdentity);
        }

        if (CollectionUtils.isNotEmpty(taskIdentities)) {
            taskIdentity = taskIdentities.get(0);
        }

        flowUserJobIdentityService.selectSubmtJobIdentity(taskInstance, taskIdentity, taskData, flowInstance);

        if (taskIdentity == null || WorkFlowOperation.COMPLETE.equals(taskData.getActionType(key))
                || taskData.isRollback(taskInstance.getId())) {
            // 正常提交
            TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
            Param param = new Param(taskInstance, taskData, null, taskData.isLogUserOperation());
            taskExecutor.execute(param);
        } else {
            // taskIdentity = taskIdentities.get(0);
            Integer todoType = taskIdentity.getTodoType();
            if (WorkFlowTodoType.Submit.equals(todoType)) {
                // 正常提交
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
                Param param = new Param(taskInstance, taskData, taskIdentity);
                taskExecutor.execute(param);
            } else if (WorkFlowTodoType.CounterSign.equals(todoType)) {
                // 会签提交
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.COUNTER_SIGN_SUBMIT);
                Param param = new Param(taskInstance, taskData, taskIdentity);
                taskExecutor.execute(param);
            } else if (WorkFlowTodoType.AddSign.equals(todoType)) {
                // 加签提交
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.ADD_SIGN_SUBMIT);
                Param param = new Param(taskInstance, taskData, taskIdentity);
                taskExecutor.execute(param);
            } else if (WorkFlowTodoType.Transfer.equals(todoType)) {
                // 转办提交
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.TRANSFER_SUBMIT);
                Param param = new Param(taskInstance, taskData, taskIdentity);
                taskExecutor.execute(param);
            } else if (WorkFlowTodoType.Delegation.equals(todoType)) {
                // 委托提交
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.DELEGATION_SUBMIT);
                Param param = new Param(taskInstance, taskData, taskIdentity);
                taskExecutor.execute(param);
            } else if (WorkFlowTodoType.HandOver.equals(todoType)) {
                // 正常提交
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
                Param param = new Param(taskInstance, taskData, taskIdentity);
                taskExecutor.execute(param);
            } else if (WorkFlowTodoType.Supplement.equals(todoType)) {
                // 补审补办提交
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUPPLEMENT);
                Param param = new Param(taskInstance, taskData, taskIdentity);
                taskExecutor.execute(param);
            } else {
                // 正常提交
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
                Param param = new Param(taskInstance, taskData);
                taskExecutor.execute(param);
            }
        }
        // 移除已读记录
        if (StringUtils.isNotBlank(taskInstUuid)) {
            readMarkerService.markNew(taskInstUuid);
        }
        taskInstanceService.flushSession();
        taskInstanceService.clearSession();

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        // 解锁
        SubmitResult submitResult = taskData.getSubmitResult();
        if (MapUtils.isNotEmpty(submitResult.getTaskTodoUsers())) {
            releaseLock(taskInstUuid);
        } else {
            removeCurrentUserLock(taskInstUuid);
        }
        // flowIndexDocumentService.indexWorkflowDocument(taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#rollback(java.lang.String, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void rollback(String taskInstUuid, TaskData taskData) {
        // rollbackService.rollback(taskInstUuid, taskData);
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.ROLLBACK);
        Param param = new Param(taskInstanceService.get(taskInstUuid), taskData);
        taskExecutor.execute(param);

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        flowIndexDocumentService.indexWorkflowDocument(taskData);

        // 解锁
        releaseLock(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#rollbackToMainFlow(java.lang.String, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void rollbackToMainFlow(String taskInstUuid, TaskData taskData) {
        String userId = taskData.getUserId();
        String key = taskInstUuid + userId;
        String opinionText = taskData.getOpinionText(key);
        String opinionLabel = taskData.getOpinionLabel(key);
        String opinionValue = taskData.getOpinionValue(key);
        List<LogicFileInfo> opinionFiles = taskData.getOpinionFiles(key);
        String action = taskData.getAction(key);
        String actionType = taskData.getActionType(key);

        TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
        TaskInstance parentTaskInstance = taskInstance.getParent();
        String parallelTaskInstUuid = parentTaskInstance.getParallelTaskInstUuid();
        // 分支环节、分支子流程环节混合的环节实例
        if (StringUtils.isNotBlank(parallelTaskInstUuid)) {
            if (parentTaskInstance.getEndTime() != null) {
                throw new RuntimeException("分支子流程环节已结束，不能退回主流程！");
            }

            // 退回前一环节
            TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.ROLLBACK);
            String mainTaskKey = parentTaskInstance.getUuid() + userId;
            TaskData rollbackTaskData = new TaskData();
            rollbackTaskData.setUserId(userId);
            rollbackTaskData.setOpinionText(mainTaskKey, opinionText);
            rollbackTaskData.setOpinionLabel(mainTaskKey, opinionLabel);
            rollbackTaskData.setOpinionValue(mainTaskKey, opinionValue);
            rollbackTaskData.setOpinionFiles(mainTaskKey, opinionFiles);
            rollbackTaskData.setAction(mainTaskKey, action);
            rollbackTaskData.setActionType(mainTaskKey, actionType);
            rollbackTaskData.setRollbackToPreTask(parentTaskInstance.getUuid(), true);
            TaskIdentity taskIdentity = new TaskIdentity();
            taskIdentity.setUuid(UUID.randomUUID().toString());
            taskIdentity.setTodoType(WorkFlowTodoType.Submit);
            taskIdentity.setUserId(userId);
            taskIdentity.setSuspensionState(SuspensionState.NORMAL.getState());
            taskIdentity.setTaskInstUuid(parentTaskInstance.getUuid());
            Param param = new Param(parentTaskInstance, rollbackTaskData, taskIdentity);
            taskExecutor.execute(param);

            // 子流程送结束
            this.stopByParentTaskInstUuid(parentTaskInstance.getUuid(), action, actionType, opinionLabel, opinionValue,
                    opinionText, opinionFiles);
        } else {
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            FlowInstance parentFlowInstance = flowInstance.getParent();
            String parentFlowInstUuid = parentFlowInstance.getUuid();

            // 获取未办结的主流程
            List<TaskInstance> mainTaskInstances = getUnfinishedTasks(parentFlowInstUuid);
            if (mainTaskInstances.isEmpty() || mainTaskInstances.size() > 1) {
                throw new RuntimeException("流程环节信息错误，不能退回!");
            }
            // 主办子流程退回时自动办结协办子流程通知模板
            List<MessageTemplate> messageTemplates = FlowDelegateUtils
                    .getFlowDelegate(parentFlowInstance.getFlowDefinition()).getMessageTemplateMap()
                    .get(WorkFlowMessageTemplate.WF_WORK_MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW.getType());

            // 主流程退回到上一个环节
            for (TaskInstance mainTaskInstance : mainTaskInstances) {
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.ROLLBACK);
                String mainTaskKey = mainTaskInstance.getUuid() + userId;
                TaskData rollbackTaskData = new TaskData();
                rollbackTaskData.setUserId(userId);
                rollbackTaskData.setOpinionText(mainTaskKey, opinionText);
                rollbackTaskData.setOpinionLabel(mainTaskKey, opinionLabel);
                rollbackTaskData.setOpinionValue(mainTaskKey, opinionValue);
                rollbackTaskData.setOpinionFiles(mainTaskKey, opinionFiles);
                rollbackTaskData.setAction(mainTaskKey, action);
                rollbackTaskData.setActionType(mainTaskKey, actionType);
                rollbackTaskData.setRollbackToPreTask(mainTaskInstance.getUuid(), true);
                TaskIdentity taskIdentity = new TaskIdentity();
                taskIdentity.setUuid(UUID.randomUUID().toString());
                taskIdentity.setTodoType(WorkFlowTodoType.Submit);
                taskIdentity.setUserId(userId);
                taskIdentity.setSuspensionState(SuspensionState.NORMAL.getState());
                taskIdentity.setTaskInstUuid(mainTaskInstance.getUuid());
                Param param = new Param(mainTaskInstance, rollbackTaskData, taskIdentity);
                taskExecutor.execute(param);
            }

            // 其他子流程
            List<TaskSubFlow> subFlows = taskSubFlowService.getOthersBySubFlowInstUuid(flowInstance.getUuid());
            // 未办结子流程的办理人员
            List<String> messageTodoUserIds = Lists.newArrayList();
            // 二级分发子流程
            List<TaskInstance> subTaskInstances = Lists.newArrayList();
            for (TaskSubFlow taskSubFlow : subFlows) {
                this.dao.getSession().evict(taskSubFlow);
                List<TaskInstance> taskInstances = getUnfinishedTasks(taskSubFlow.getFlowInstUuid());
                for (TaskInstance subFlowTaskInstance : taskInstances) {
                    if (Integer.valueOf(2).equals(subFlowTaskInstance.getType())) {
                        subTaskInstances.add(subFlowTaskInstance);
                    } else {
                        messageTodoUserIds.addAll(getTodoUserIds(subFlowTaskInstance.getUuid()));
                    }
                }
            }

            // 子流程送结束
            this.stopByParentFlowInstUuid(parentFlowInstUuid, action, actionType, opinionLabel, opinionValue,
                    opinionText, opinionFiles);
            // 主办子流程退回时自动办结协办子流程通知
            if (CollectionUtils.isNotEmpty(messageTodoUserIds)) {
                TaskData msgTaskData = new TaskData();
                Token token = new Token(parentTaskInstance, msgTaskData);
                msgTaskData.setToken(token);
                msgTaskData.setUserId(userId);
                msgTaskData.setFormUuid(parentFlowInstance.getFormUuid());
                msgTaskData.setDataUuid(parentFlowInstance.getDataUuid());
                List<TaskSubFlow> taskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstance.getUuid());
                if (CollectionUtils.isNotEmpty(taskSubFlows)) {
                    msgTaskData.setCustomData("承办部门", taskSubFlows.get(0).getTodoName());
                    msgTaskData.setCustomData("流程实例名称", flowInstance.getTitle());
                }
                MessageClientUtils.send(msgTaskData,
                        WorkFlowMessageTemplate.WF_WORK_MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW, messageTemplates,
                        parentTaskInstance, parentFlowInstance, messageTodoUserIds, ParticipantType.TodoUser);
            }

            // 二级分发子流程
            if (CollectionUtils.isNotEmpty(messageTemplates) && CollectionUtils.isNotEmpty(subTaskInstances)) {
                for (TaskInstance subTaskInstance : subTaskInstances) {
                    FlowInstance subFlowInstance = flowInstanceService.getByTaskInstUuid(subTaskInstance.getUuid());
                    TaskData msgTaskData = new TaskData();
                    msgTaskData.setCustomData("流程实例名称", subFlowInstance.getTitle());
                    messageTodoUserIds = Lists.newArrayList();
                    List<TaskInstance> subNormalTaskInstanceList = taskInstanceService
                            .getNormalByParentTaskInstUuid(subTaskInstance.getUuid());
                    for (TaskInstance subNormalTaskInstance : subNormalTaskInstanceList) {
                        messageTodoUserIds.addAll(getTodoUserIds(subNormalTaskInstance.getUuid()));
                    }
                    // 子流程送结束
                    this.stopByParentFlowInstUuid(subFlowInstance.getUuid(), action, actionType, opinionLabel,
                            opinionValue, opinionText, opinionFiles);
                    if (CollectionUtils.isNotEmpty(messageTodoUserIds)) {
                        Token token = new Token(parentTaskInstance, msgTaskData);
                        msgTaskData.setToken(token);
                        msgTaskData.setUserId(userId);
                        msgTaskData.setFormUuid(parentFlowInstance.getFormUuid());
                        msgTaskData.setDataUuid(parentFlowInstance.getDataUuid());
                        MessageClientUtils.send(msgTaskData,
                                WorkFlowMessageTemplate.WF_WORK_MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW,
                                messageTemplates, parentTaskInstance, parentFlowInstance, messageTodoUserIds,
                                ParticipantType.TodoUser);
                    }
                }
            }
        }

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        // 解锁
        releaseLock(taskInstUuid);
    }

    /**
     * @param parentFlowInstUuid
     * @param excludeFlowInstUuid
     * @return
     */
    private List<String> listTodoUserIdsByParentFlowInstUuidWithExcludeFlowInstUuid(String parentFlowInstUuid,
                                                                                    String excludeFlowInstUuid) {
        return taskInstanceService.listTodoUserIdsByParentFlowInstUuidWithExcludeFlowInstUuid(parentFlowInstUuid,
                excludeFlowInstUuid);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void cancel(String uuid) {
        TaskInstance task = taskInstanceService.get(uuid);
        this.cancel(task);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#cancel(java.lang.String, java.lang.String)
     */
    @Override
    public void cancel(String uuid, String opinionText) {
        TaskInstance task = taskInstanceService.get(uuid);
        this.cancel(task, opinionText);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#cancel(java.lang.String, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void cancel(String uuid, TaskData taskData) {
        TaskInstance task = taskInstanceService.get(uuid);
        this.cancel(task, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#cancel(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void cancel(TaskInstance taskInstance, TaskData taskData) {
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.CANCEL);
        Param param = new Param(taskInstance, taskData);
        taskExecutor.execute(param);

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        flowIndexDocumentService.indexWorkflowDocument(taskData);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void cancel(TaskInstance taskInstance) {
        String opinionText = null;
        this.cancel(taskInstance, opinionText);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#cancel(com.wellsoft.pt.bpm.engine.entity.TaskInstance, java.lang.String)
     */
    @Override
    public void cancel(TaskInstance taskInstance, String opinionText) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        String key = taskInstance.getUuid() + userId;

        TaskData taskData = new TaskData();
        taskData.setFlowInstUuid(taskInstance.getFlowInstance().getUuid());
        taskData.setUserId(userId);
        taskData.setUserName(userName);
        // 操作动作
        taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.CANCEL));
        taskData.setActionType(key, WorkFlowOperation.CANCEL);
        // 撤回意见
        taskData.setOpinionText(key, opinionText);

        cancel(taskInstance, taskData);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#cancelOver(java.lang.String)
     */
    @Override
    @Transactional
    public void cancelOver(String flowInstUuid) {
        cancelOver(flowInstUuid, StringUtils.EMPTY, false);
    }

    /**
     * 撤回已结束任务，办理人为最后环节的所有者
     *
     * @param flowInstUuid
     * @param opinionText
     */
    @Override
    @Transactional
    public void cancelOverWithLastTaskOwner(String flowInstUuid, String opinionText) {
        cancelOver(flowInstUuid, opinionText, true);
    }

    public void cancelOver(String flowInstUuid, String opinionText, boolean useTaskOwner) {
        List<TaskActivity> taskActivities = getTaskActivities(flowInstUuid);
        if (!taskActivities.isEmpty()) {
            TaskActivity taskActivity = taskActivities.get(taskActivities.size() - 1);
            TaskInstance taskInstance = this.taskInstanceService.get(taskActivity.getTaskInstUuid());
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            String userId = SpringSecurityUtils.getCurrentUserId();
            String userName = SpringSecurityUtils.getCurrentUserName();
            List<String> todoUserIds = Lists.newArrayList();
            if (useTaskOwner) {
                todoUserIds.addAll(Arrays.asList(StringUtils.split(taskInstance.getOwner(), Separator.SEMICOLON.getValue())));
                if (CollectionUtils.isEmpty(todoUserIds)) {
                    todoUserIds.add(userId);
                }
            } else {
                todoUserIds.add(userId);
            }
            String taskInstUuid = taskInstance.getUuid();
            TaskData taskData = new TaskData();
            taskData.setFlowInstUuid(taskInstance.getFlowInstance().getUuid());
            taskData.setUserId(userId);
            taskData.setUserName(userName);
            todoUserIds.stream().forEach(todoUserId -> {
                List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuidAndUserId(taskInstUuid, todoUserId);
                if (CollectionUtils.isEmpty(taskIdentities)) {
                    TaskIdentity taskIdentity = new TaskIdentity();
                    taskIdentity.setTaskInstUuid(taskInstUuid);
                    taskIdentity.setTodoType(WorkFlowTodoType.Submit);
                    taskIdentity.setUserId(todoUserId);
                    identityService.addTodo(taskIdentity);
                } else {
                    aclTaskService.addTodoPermission(todoUserId, taskInstUuid);
                }
            });
            taskInstance.setEndTime(null);
            this.taskInstanceService.save(taskInstance);
            flowInstance.setEndTime(null);
            this.flowInstanceService.save(flowInstance);
            // 保存操作历史
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CANCEL),
                    ActionCode.CANCEL.getCode(), WorkFlowOperation.CANCEL, "", "", opinionText, userId, null, null, null, null,
                    taskInstance, flowInstance, taskData);
        } else {
            throw new RuntimeException("撤回失败!");
        }
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void transfer(String userId, String uuid, List<String> userIds, String opinionName, String opinionValue,
                         String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData) {
        TaskInstance task = taskInstanceService.get(uuid);
        this.transfer(userId, task, userIds, opinionName, opinionValue, opinionText, opinionFiles, taskData);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void transfer(String userId, TaskInstance taskInstance, List<String> userIds, String opinionName,
                         String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData) {
        List<String> transferUsers = setTaskUserJobPathsIfRequired(userIds, taskInstance.getId(), taskData);
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.TRANSFER);
        taskData.setUserId(userId);
        taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
        String key = taskInstance.getUuid() + userId;
        String action = taskData.getAction(key);
        taskData.setOpinionLabel(key, opinionName);
        taskData.setOpinionValue(key, opinionValue);
        taskData.setOpinionText(key, opinionText);
        taskData.setOpinionFiles(key, opinionFiles);
        taskData.setAction(key,
                StringUtils.isNotBlank(action) ? action : WorkFlowOperation.getName(WorkFlowOperation.TRANSFER));
        taskData.setActionType(key, WorkFlowOperation.TRANSFER);
        taskData.setCustomData("transferUsers", transferUsers);
        Param param = new Param(taskInstance, taskData);
        taskExecutor.execute(param);

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        flowIndexDocumentService.indexWorkflowDocument(taskData);
        // 解锁
        removeCurrentUserLock(taskInstance.getUuid());
    }

    /**
     * @param userIds
     * @param taskId
     * @param taskData
     * @return
     */
    private List<String> setTaskUserJobPathsIfRequired(List<String> userIds, String taskId, TaskData taskData) {
        List<String> orgIds = userIds;
        List<String> orgIdPaths = null;
        if (isOrgIdPath(orgIds)) {
            orgIdPaths = Lists.newArrayList(orgIds);
            orgIds = extractOrgIds(orgIds);
        }
        if (CollectionUtils.isNotEmpty(orgIdPaths)) {
            Map<String, List<String>> taskUserJobPaths = taskData.getTaskUserJobPaths();
            if (MapUtils.isEmpty(taskUserJobPaths)) {
                taskUserJobPaths = Maps.newHashMap();
            }
            taskUserJobPaths.put(taskId, orgIdPaths);
            taskData.setTaskUserJobPaths(taskUserJobPaths);
        }
        return orgIds;
    }

    /**
     * @param orgIdPaths
     * @return
     */
    private List<String> extractOrgIds(List<String> orgIdPaths) {
        List<String> orgIds = Lists.newArrayList();
        for (String orgIdPath : orgIdPaths) {
            if (org.apache.commons.lang3.StringUtils.contains(orgIdPath, Separator.SLASH.getValue())) {
                String[] ids = org.apache.commons.lang3.StringUtils.split(orgIdPath, Separator.SLASH.getValue());
                int length = ids.length;
                // 业务角色ID
                if (length >= 2 && (org.apache.commons.lang3.StringUtils.startsWith(ids[length - 2], IdPrefix.BIZ_PREFIX.getValue())
                        || org.apache.commons.lang3.StringUtils.startsWith(ids[length - 2], IdPrefix.BIZ_ORG_DIM.getValue()))
                        && !IdPrefix.hasPrefix(ids[length - 1])) {
                    orgIds.add(ids[length - 2] + Separator.SLASH.getValue() + ids[length - 1]);
                } else {
                    orgIds.add(ids[length - 1]);
                }
            } else {
                orgIds.add(orgIdPath);
            }
        }
        return orgIds;
    }

    /**
     * @param orgIds
     * @return
     */
    private boolean isOrgIdPath(List<String> orgIds) {
        for (String orgId : orgIds) {
            if (org.apache.commons.lang3.StringUtils.contains(orgId, Separator.SLASH.getValue())) {
                String[] ids = org.apache.commons.lang3.StringUtils.split(orgId, Separator.SLASH.getValue());
                // 业务角色不是组织路径
                if (ids.length == 2 && (org.apache.commons.lang3.StringUtils.startsWith(ids[0], IdPrefix.BIZ_PREFIX.getValue())
                        || org.apache.commons.lang3.StringUtils.startsWith(ids[0], IdPrefix.BIZ_ORG_DIM.getValue()))
                        && !IdPrefix.hasPrefix(ids[1])) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void counterSign(String userId, String uuid, List<String> userIds, String opinionName, String opinionValue,
                            String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData) {
        TaskInstance task = taskInstanceService.get(uuid);
        // 强制加载对象内容
        // Hibernate.initialize(task);
        this.counterSign(userId, task, userIds, opinionName, opinionValue, opinionText, opinionFiles, taskData);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void counterSign(String userId, TaskInstance taskInstance, List<String> userIds, String opinionName,
                            String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData) {
        List<String> counterSignUsers = setTaskUserJobPathsIfRequired(userIds, taskInstance.getId(), taskData);
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.COUNTER_SIGN);
        taskData.setUserId(userId);
        taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
        String key = taskInstance.getUuid() + userId;
        taskData.setOpinionLabel(key, opinionName);
        taskData.setOpinionValue(key, opinionValue);
        taskData.setOpinionText(key, opinionText);
        taskData.setOpinionFiles(key, opinionFiles);
        String action = taskData.getAction(key);
        if (StringUtils.isBlank(action)) {
            taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.COUNTER_SIGN));
        }
        taskData.setActionType(key, WorkFlowOperation.COUNTER_SIGN);
        taskData.setCustomData("counterSignUsers", counterSignUsers);
        Param param = new Param(taskInstance, taskData);
        taskExecutor.execute(param);

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        flowIndexDocumentService.indexWorkflowDocument(taskData);
        // 解锁
        removeCurrentUserLock(taskInstance.getUuid());
    }

    /**
     * @param userId
     * @param taskInstUuid
     * @param userIds
     * @param opinionName
     * @param opinionValue
     * @param opinionText
     * @param opinionFiles
     * @param taskData
     */
    @Override
    public void addSign(String userId, String taskInstUuid, List<String> userIds, String opinionName, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData) {
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.ADD_SIGN);
        taskData.setUserId(userId);
        taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
        TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
        String key = taskInstance.getUuid() + userId;
        taskData.setOpinionLabel(key, opinionName);
        taskData.setOpinionValue(key, opinionValue);
        taskData.setOpinionText(key, opinionText);
        taskData.setOpinionFiles(key, opinionFiles);
        String action = taskData.getAction(key);
        if (StringUtils.isBlank(action)) {
            taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.ADD_SIGN));
        }
        taskData.setActionType(key, WorkFlowOperation.ADD_SIGN);
        List<String> addSignUsers = setTaskUserJobPathsIfRequired(userIds, taskInstance.getId(), taskData);
        taskData.setCustomData("addSignUsers", addSignUsers);
        Param param = new Param(taskInstance, taskData);
        taskExecutor.execute(param);

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        flowIndexDocumentService.indexWorkflowDocument(taskData);
        // 解锁
        removeCurrentUserLock(taskInstance.getUuid());
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
     * (non-Javadoc)
     */
    @Override
    public void attention(String userId, String uuid) {
        TaskInstance task = taskInstanceService.get(uuid);
        this.attention(userId, task);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void attention(String userId, TaskInstance task) {
        // 判断是否有关注，如果没有添加关注
        if (!aclTaskService.hasPermission(task.getUuid(), AclPermission.ATTENTION, userId)) {
            aclTaskService.addPermission(task.getUuid(), AclPermission.ATTENTION, userId);
        }
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public PrintResult print(String uuid, String printTemplateId) {
        TaskInstance task = taskInstanceService.get(uuid);
        return this.print(task, printTemplateId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#print(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public PrintResult print(String uuid, String printTemplateId, String printTemplateUuid, String lang) {
        TaskInstance task = taskInstanceService.get(uuid);
        return this.print(task, printTemplateId, printTemplateUuid, lang);
    }

    @Override
    public PrintResult print(TaskInstance task, String printTemplateId) {
        return print(task, printTemplateId, null, null);
    }

    @Override
    public InputStream getPrintResultAsInputStream(TaskInstance task, String printTemplateId, String printTemplateUuid,
                                                   String lang) {
        // 设置打印模板
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        String tmpPrintTemplateId = printTemplateId, tmpPrintTemplateUuid = printTemplateUuid;
        if (StringUtils.isBlank(tmpPrintTemplateId) && StringUtils.isBlank(tmpPrintTemplateUuid)) {
            List<PrintTemplate> templates = flowDelegate.getTaskPrintTemplates(task.getId());
            if (templates.size() == 0) {
                throw new PrintTemplateNotFoundException();
            } else if (templates.size() == 1) {
                tmpPrintTemplateId = templates.get(0).getId();
                tmpPrintTemplateUuid = templates.get(0).getUuid();
            } else if (templates.size() > 1) {
                List<QueryItem> items = new ArrayList<QueryItem>();
                for (PrintTemplate template : templates) {
                    QueryItem item = new QueryItem();
                    item.put("name", template.getName());
                    item.put("id", template.getId());
                    item.put("uuid", template.getUuid());
                    items.add(item);
                }
                throw new PrintTemplateNotAssignedException(items);
            }
        }

        // 设置流程相关数据
        TaskInstance taskInstance = new TaskInstance();
        BeanUtils.copyProperties(task, taskInstance);
        FlowInstance flowInstance = new FlowInstance();
        BeanUtils.copyProperties(task.getFlowInstance(), flowInstance);
        List<IdEntity> entities = new ArrayList<IdEntity>();
        entities.add(taskInstance);
        entities.add(flowInstance);
        String formUuid = task.getFormUuid();
        String dataUuid = task.getDataUuid();
        // FormAndDataBean formAndDataBean =
        // dytableApiFacade.getFormShowData(formUuid, dataUuid);
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);

        // 解决套打附件正文内容
        // <字段， 附件monofile列表>
        Map<String, List<MongoFileEntity>> bodyFiles = new HashMap<String, List<MongoFileEntity>>();
        // 先取正文附件字段
        List<DyformFieldDefinition> fieldDefinitions = dyFormFacade.getFieldDefinitions(formUuid);
        // Set<String> recordFields = getRecordFields(flowDelegate);
        Map<String, Object> recordFieldValueMap = new HashMap<String, Object>();
        // word打印清除表单大字段的HTML格式
        com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate printTemplate = basicDataApiFacade
                .getPrintTemplateById(printTemplateId);
        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
            String fieldName = fieldDefinition.getName();
            // 如果是附件字段
            if (dyFormData.isFileField(fieldName)) {
                List<MongoFileEntity> fileEntities = mongoFileService.getFilesFromFolder(dataUuid, fieldName);
                bodyFiles.put(fieldName, fileEntities);
            } else {
                // 信息格式清除HTML标签的字段值
                Object recordFieldValue = dyFormData.isValueAsMapField(fieldName)
                        ? dyFormData.getFieldDisplayValue(fieldName)
                        : dyFormData.getFieldValue(fieldName);
                if (recordFieldValue != null
                        && (printTemplate.doIsTemplateFileTypeAsWord() || printTemplate.doIsTemplateFileTypeAsWordXml()
                        || printTemplate.doIsTemplateFileTypeAsWordPoi())) {
                    String inputString = null;
                    try {
                        if (recordFieldValue instanceof Clob) {
                            inputString = IOUtils.toString(((Clob) recordFieldValue).getCharacterStream());
                        } else if (recordFieldValue instanceof String) {
                            inputString = (String) recordFieldValue;
                        } else {
                            continue;
                        }
                        inputString = inputString.replaceAll("</p>", "</p> \n");
                        inputString = inputString.replaceAll("<br>", " \n");
                        inputString = "<div>" + inputString + "</div>";
                        recordFieldValueMap.put(fieldName, FlowFormatServiceImpl.html2Text(inputString));
                    } catch (IOException ex) {
                        logger.warn(ex.getMessage(), ex);
                    } catch (SQLException ex) {
                        logger.warn(ex.getMessage(), ex);
                    }
                }
            }
        }
        // 打印
        Map<String, List<Map<String, Object>>> bjsqdShowMap = dyFormData.getDisplayValuesKeyAsFormId();// 办件单对应的所有字段显示值（包含主表和从表）
        Map<String, Object> bjsqdFormMap = new HashMap<String, Object>();
        bjsqdFormMap.putAll(dyFormData.getFormDataOfMainform());
        if (printTemplate.doIsTemplateFileTypeAsWord() || printTemplate.doIsTemplateFileTypeAsWordXml()
                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()
                || printTemplate.doIsTemplateFileTypeAsWordPoi()) {
            bjsqdFormMap.putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), bjsqdShowMap));
        } else {
            bjsqdFormMap.putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), bjsqdShowMap, false));
        }
        bjsqdFormMap.putAll(recordFieldValueMap);
        InputStream inputStream = basicDataApiFacade.getPrintResultAsInputStream(tmpPrintTemplateId, printTemplateUuid,
                lang, entities, bjsqdFormMap, bodyFiles);

        if (inputStream == null) {
            throw new WorkFlowException("打印模板调用接口,返回文件流出错!");
        }
        return inputStream;
    }

    /**
     * (non-Javadoc)
     */
    public PrintResult print(TaskInstance task, String printTemplateId, String printTemplateUuid, String lang) {
        // 设置打印模板
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        String tmpPrintTemplateId = printTemplateId, tmpPrintTemplateUuid = printTemplateUuid;
        if (StringUtils.isBlank(tmpPrintTemplateId) && StringUtils.isBlank(tmpPrintTemplateUuid)) {
            List<PrintTemplate> templates = flowDelegate.getTaskPrintTemplates(task.getId());
            if (templates.size() == 0) {
                throw new PrintTemplateNotFoundException();
            } else if (templates.size() == 1) {
                tmpPrintTemplateId = templates.get(0).getId();
                tmpPrintTemplateUuid = templates.get(0).getUuid();
            } else if (templates.size() > 1) {
                List<QueryItem> items = new ArrayList<QueryItem>();
                for (PrintTemplate template : templates) {
                    QueryItem item = new QueryItem();
                    item.put("name", template.getName());
                    item.put("id", template.getId());
                    item.put("uuid", template.getUuid());
                    items.add(item);
                }
                throw new PrintTemplateNotAssignedException(items);
            }
        }

        // 设置流程相关数据
        TaskInstance taskInstance = new TaskInstance();
        BeanUtils.copyProperties(task, taskInstance);
        FlowInstance flowInstance = new FlowInstance();
        BeanUtils.copyProperties(task.getFlowInstance(), flowInstance);
        List<IdEntity> entities = new ArrayList<IdEntity>();
        entities.add(taskInstance);
        entities.add(flowInstance);
        String formUuid = task.getFormUuid();
        String dataUuid = task.getDataUuid();
        // FormAndDataBean formAndDataBean =
        // dytableApiFacade.getFormShowData(formUuid, dataUuid);
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);

        // 解决套打附件正文内容
        // <字段， 附件monofile列表>
        Map<String, List<MongoFileEntity>> bodyFiles = new HashMap<String, List<MongoFileEntity>>();
        // 先取正文附件字段
        List<DyformFieldDefinition> fieldDefinitions = dyFormFacade.getFieldDefinitions(formUuid);
        // Set<String> recordFields = getRecordFields(flowDelegate);
        Map<String, Object> recordFieldValueMap = new HashMap<String, Object>();
        // word打印清除表单大字段的HTML格式
        com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate printTemplate = basicDataApiFacade
                .getPrintTemplateById(printTemplateId);
        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
            String fieldName = fieldDefinition.getName();
            // 如果是附件字段
            if (dyFormData.isFileField(fieldName)) {
                List<MongoFileEntity> fileEntities = mongoFileService.getFilesFromFolder(dataUuid, fieldName);
                bodyFiles.put(fieldName, fileEntities);
            } else {
                // 信息格式清除HTML标签的字段值
                Object recordFieldValue = dyFormData.isValueAsMapField(fieldName)
                        ? dyFormData.getFieldDisplayValue(fieldName)
                        : dyFormData.getFieldValue(fieldName);
                if (recordFieldValue != null
                        && (printTemplate.doIsTemplateFileTypeAsWord() || printTemplate.doIsTemplateFileTypeAsWordXml()
                        || printTemplate.doIsTemplateFileTypeAsWordPoi())) {
                    String inputString = null;
                    try {
                        if (recordFieldValue instanceof Clob) {
                            inputString = IOUtils.toString(((Clob) recordFieldValue).getCharacterStream());
                        } else if (recordFieldValue instanceof String) {
                            inputString = (String) recordFieldValue;
                        } else {
                            continue;
                        }
                        inputString = inputString.replaceAll("</p>", "</p> \n");
                        inputString = inputString.replaceAll("<br>", " \n");
                        inputString = "<div>" + inputString + "</div>";
                        recordFieldValueMap.put(fieldName, FlowFormatServiceImpl.html2Text(inputString));
                    } catch (IOException ex) {
                        logger.warn(ex.getMessage(), ex);
                    } catch (SQLException ex) {
                        logger.warn(ex.getMessage(), ex);
                    }
                }
            }
        }
        // 打印
        Map<String, List<Map<String, Object>>> bjsqdShowMap = dyFormData.getDisplayValuesKeyAsFormId();// 办件单对应的所有字段显示值（包含主表和从表）
        Map<String, Object> bjsqdFormMap = new HashMap<String, Object>();
        bjsqdFormMap.putAll(dyFormData.getFormDataOfMainform());
        if (printTemplate.doIsTemplateFileTypeAsWord() || printTemplate.doIsTemplateFileTypeAsWordXml()
                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()
                || printTemplate.doIsTemplateFileTypeAsWordPoi()) {
            bjsqdFormMap.putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), bjsqdShowMap));
        } else {
            bjsqdFormMap.putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), bjsqdShowMap, false));
        }
        bjsqdFormMap.putAll(recordFieldValueMap);
        InputStream inputStream = basicDataApiFacade.getPrintResultAsInputStream(tmpPrintTemplateId, printTemplateUuid,
                lang, entities, bjsqdFormMap, bodyFiles);

        if (inputStream == null) {
            throw new WorkFlowException("打印模板调用接口,返回文件流出错!");
        }

        // 下载打印结果
        String printFileName = flowInstance.getTitle();
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())
                && printFileName.matches(".*[\\p{IsHan}].*")) {
            printFileName = translateService.translate(printFileName, "zh", LocaleContextHolder.getLocale().getLanguage());
        }
        String templateType = printTemplate.getTemplateType();
        if (com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate.TEMPLATE_TYPE_HTML.equals(templateType)) {
            printFileName += ".html";
        } else if (printTemplate.doIsTemplateFileTypeAsWordPoi()) {
            printFileName += ".docx";
        } else {
            printFileName += ".doc";
        }
        PrintResult result = new PrintResult();
        result.setStream(inputStream);
        result.setTemplateId(tmpPrintTemplateId);
        result.setTemplateUuid(tmpPrintTemplateUuid);
        result.setFileName(printFileName);
        // 用户操作日志（提交并套打）
        if (null == ContextLogs.getLogEvent()) {
            FlowDefinition flowDefinition = task.getFlowDefinition();
            BusinessOperationLog bussinessLog = new BusinessOperationLog();
            bussinessLog.setModuleId(flowDefinition.getModuleId());
            bussinessLog.setDataDefType(ModuleID.WORKFLOW.getValue());
            bussinessLog.setDataDefId(flowDefinition.getId());
            bussinessLog.setDataDefName(flowDefinition.getName());
            bussinessLog.setOperation(WorkFlowOperation.getName(WorkFlowOperation.PRINT));
            bussinessLog.setUserId(SpringSecurityUtils.getCurrentUserId());
            bussinessLog.setUserName(SpringSecurityUtils.getCurrentUserName());
            bussinessLog.setDataId(flowInstance.getUuid());
            bussinessLog.setDataName(flowInstance.getTitle());
            Map<String, Object> details = Maps.newHashMap();
            ContextLogs.sendLogEvent(new LogEvent(bussinessLog, details));
        }
        return result;
    }

    /**
     * @param flowDelegate
     * @return
     */
    private Set<String> getRecordFields(FlowDelegate flowDelegate) {
        Set<String> fields = new HashSet<String>();
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            TaskForm taskForm = flowDelegate.getTaskForm(node.getId());
            List<Record> records = taskForm.getRecords();
            if (CollectionUtils.isNotEmpty(records)) {
                for (Record record : records) {
                    String field = record.getField();
                    fields.addAll(Arrays.asList(StringUtils.split(field, Separator.SEMICOLON.getValue())));
                }
            }
        }
        return fields;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#get(java.lang.String)
     */
    @Override
    public TaskInstance get(String taskInstUuid) {
        return this.taskInstanceService.get(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getFormData(java.lang.String)
     */
    @Override
    public DyFormData getFormData(String taskInstUuid) {
        return getFormData(this.getTask(taskInstUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getFormData(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public DyFormData getFormData(TaskInstance taskInstance) {
        String formUuid = taskInstance.getFormUuid();
        String dataUuid = taskInstance.getDataUuid();
        return dyFormFacade.getDyFormData(formUuid, dataUuid);
        // return dytableApiFacade.getFormData(formUuid, dataUuid);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void query(Page<TaskInstance> taskPage, String hql, Object... values) {
        taskInstanceService.query(taskPage, hql, values);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public TaskForm getTaskForm(String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        Token token = new Token(taskInstance, null);
        TaskForm taskForm = null;
        if (token.getFlowDelegate().existsTaskNode(taskInstance.getId())) {
            taskForm = token.getFlowDelegate().getTaskForm(taskInstance.getId());
        } else {
            taskForm = new TaskForm(taskInstance.getFormUuid());
        }
        return taskForm;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getStartTaskForm(java.lang.String)
     */
    @Override
    public TaskForm getStartTaskForm(String flowDefUuid) {
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinitionService.getOne(flowDefUuid));
        return flowDelegate.getStartTaskForm();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getConfigInfo(java.lang.String)
     */
    @Override
    public TaskData getConfigInfo(String taskInstUuid) {
        return getTaskData(this.taskInstanceService.get(taskInstUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTaskData(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public TaskData getTaskData(TaskInstance taskInstance) {
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        TaskData taskData = flowDelegate.getTaskConfigInfo(taskInstance.getId());
        // 过滤动态按钮
        taskData.filterOwnerCustomDynamicButton();
        return taskData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getNextConfigInfo(java.lang.String)
     */
    @Override
    public TaskData getNextConfigInfo(String taskInstUuid) {
        TaskData taskData = new TaskData();
        taskData.setUserId(SpringSecurityUtils.getCurrentUserId());
        return getNextConfigInfo(taskInstUuid, taskData);
    }

    /**
     * 根据任务UUID获取下一任务配置信息
     *
     * @param taskInstUuid
     * @param taskData
     * @return
     */
    @Override
    public TaskData getNextConfigInfo(String taskInstUuid, TaskData taskData) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        taskData.setFormUuid(taskInstance.getFormUuid());
        taskData.setDataUuid(taskInstance.getDataUuid());
        Token token = new Token(taskInstance, taskData);
        Transition transition = token.getNode().getLeavingTransition();
        FlowDelegate flowDelegate = token.getFlowDelegate();
        TransitionResolver transitionResolver = TransitionResolverFactory.getResolver(transition);
        return FlowServiceImpl.getTaskConfigData(taskData, token, flowDelegate, transition, transitionResolver);
    }

    /**
     * 获取流程操作日志
     * <p>
     * (non-Javadoc)
     */
    @Override
    public Map<String, List<TaskOperation>> getOperationAsMap(String flowInstUuid) {
        Map<String, List<TaskOperation>> map = new LinkedHashMap<String, List<TaskOperation>>();
        List<TaskOperation> taskOperations = taskOperationService.getByFlowInstUuid(flowInstUuid);
        for (TaskOperation taskOperation : taskOperations) {
            String taskInstUuid = taskOperation.getTaskInstUuid();
            if (!map.containsKey(taskInstUuid)) {
                map.put(taskInstUuid, new ArrayList<TaskOperation>());
            }
            List<TaskOperation> operations = map.get(taskInstUuid);
            operations.add(taskOperation);
        }
        return map;
    }

    /**
     * 获取流程流转日志
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTaskActivities(java.lang.String)
     */
    @Override
    public List<TaskActivity> getTaskActivities(String flowInstUuid) {
        return taskActivityDao.getByFlowInstUuid(flowInstUuid);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public List<String> getPreTaskOperatorIds(String taskInstUuid) {
        String getPreTaskOperatorHql = "select to.assignee from TaskOperation to where exists (select uuid from TaskActivity ta where ta.preTaskInstUuid = to.taskInstUuid and ta.taskInstUuid = :taskInstUuid)";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        List<String> operatorIds = this.taskOperationService.getAssigneeByHQL(getPreTaskOperatorHql, values);
        return operatorIds;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public List<String> getPreTaskOperatorNames(String taskInstUuid) {
        List<String> userNames = new ArrayList<String>();
        List<String> userIds = getPreTaskOperatorIds(taskInstUuid);
        for (String userId : userIds) {
//            userNames.add(orgApiFacade.getUserNameById(userId));
            userNames.add(workflowOrgService.getNameById(userId));
        }
        return userNames;
    }

    /**
     * 工作抄送
     * <p>
     * (non-Javadoc)
     */
    @Override
    public void copyTo(String userId, String taskInstUuid, List<String> rawCopyUsers, String aclRole) {
        this.copyTo(userId, this.taskInstanceService.get(taskInstUuid), rawCopyUsers, aclRole);
    }

    /**
     * 工作抄送
     * <p>
     * (non-Javadoc)
     */
    @Override
    public void copyTo(String userId, TaskInstance taskInstance, List<String> rawCopyUsers, String aclRole) {
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.COPY_TO);
        TaskData taskData = new TaskData();
        Token token = new Token(taskInstance, taskData);
        taskData.setToken(token);
        taskData.setFormUuid(taskInstance.getFormUuid());
        taskData.setDataUuid(taskInstance.getDataUuid());
        taskData.setUserId(userId);
        taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
        String key = taskInstance.getUuid() + userId;
        String action = taskData.getAction(key);
        taskData.setAction(key,
                StringUtils.isNotBlank(action) ? action : WorkFlowOperation.getName(WorkFlowOperation.COPY_TO));
        taskData.setActionType(key, WorkFlowOperation.COPY_TO);
        taskData.setCustomData("copyToUsers", rawCopyUsers);
        taskData.setAclRole(aclRole);
        Param param = new Param(taskInstance, taskData);
        taskExecutor.execute(param);

        taskInstanceService.flushSession();
        taskInstanceService.clearSession();

        flowIndexDocumentService.indexWorkflowDocument(taskData);
    }

    /**
     * 工作催办
     * <p>
     * (non-Javadoc)
     */
    @Override
    public void remind(String taskInstUuid, String opinionName, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles) {
        this.remind(this.taskInstanceService.get(taskInstUuid), opinionName, opinionValue, opinionText, opinionFiles);
    }

    /**
     * 工作催办
     * <p>
     * (non-Javadoc)
     */
    @Override
    public void remind(TaskInstance task, String opinionName, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles) {
        if (task.getEndTime() != null) {
            throw new WorkFlowException("流程已办结，不能进行催办！");
        }

        FlowInstance flowInstance = task.getFlowInstance();

        String remindOpinionText = opinionText;
        if (StringUtils.isBlank(remindOpinionText)) {
            remindOpinionText = "请抓紧办理!";
        }

        FlowInstance parentFlowInstance = flowInstance.getParent();

        String userId = SpringSecurityUtils.getCurrentUserId();
        TaskData taskData = new TaskData();
        taskData.setUserId(userId);
        String key = task.getUuid() + userId;
        taskData.setOpinionFiles(key, opinionFiles);
        if (parentFlowInstance != null) {
            // 发送子流程催办消息
            List<String> userIds = sendSubFlowRemindMessage(task, flowInstance, parentFlowInstance, opinionName,
                    opinionValue, remindOpinionText);

            // 操作记录
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.REMIND),
                    ActionCode.REMIND.getCode(), WorkFlowOperation.REMIND, opinionValue, opinionName, remindOpinionText,
                    userId, userIds, null, null, null, task, flowInstance,
                    taskData);
        } else {
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
            List<String> userIds = getTodoUserIds(task.getUuid());
            /* modified by huanglinchuan 2014.11.1 begin */
            // 提醒也需要进入办理过程，即记录到任务历史记录
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.REMIND),
                    ActionCode.REMIND.getCode(), WorkFlowOperation.REMIND, opinionValue, opinionName, remindOpinionText,
                    userId, userIds, null, null, null, task, flowInstance,
                    taskData);

            List<MessageTemplate> messageTemplates = getRemindMessageTemplates(flowDelegate);
            sendRemindMessage(task, opinionName, opinionValue, flowInstance, remindOpinionText, userIds,
                    messageTemplates);
            /* modified by huanglinchuan 2014.11.1 end */
        }
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param parentFlowInstance
     * @param opinionName
     * @param opinionValue
     * @param opinionText
     * @return
     */
    private List<String> sendSubFlowRemindMessage(TaskInstance taskInstance, FlowInstance flowInstance,
                                                  FlowInstance parentFlowInstance, String opinionName, String opinionValue, String opinionText) {
        String taskInstUuid = taskInstance.getUuid();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentFlowInstance.getFlowDefinition());
        // 子流程催办通知——子流程办理人
        List<MessageTemplate> remindTodoTemplates = flowDelegate.getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND.getType());
        List<String> remindTodoUserIds = null;
        // 子流程催办通知——子流程办理人上级领导
        List<MessageTemplate> remindDoingSuperiorTemplates = flowDelegate.getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_DOING_SUPERIOR.getType());
        List<String> remindDoingSuperiorUserIds = null;
        // 子流程催办通知——子流程督办人员
        List<MessageTemplate> remindSuperviseTemplates = flowDelegate.getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_SUPERVISE.getType());
        List<String> remindSuperviseUserIds = null;
        // 子流程催办通知——子流程跟踪人员
        List<MessageTemplate> remindTracerTemplates = flowDelegate.getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_TRACER.getType());
        List<String> remindTracerUserIds = null;
        // 子流程催办通知——子流程流程管理人员
        List<MessageTemplate> remindAdminTemplates = flowDelegate.getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_ADMIN.getType());
        List<String> remindAdminUserIds = null;

        // 催办人员ID
        Set<String> remindUserIds = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(remindTodoTemplates)) {
            remindTodoUserIds = getTodoUserIds(taskInstUuid);
            remindUserIds.addAll(remindTodoUserIds);
        }
        if (CollectionUtils.isNotEmpty(remindDoingSuperiorTemplates)) {
            remindDoingSuperiorUserIds = getTodoSuperiorUserIds(taskInstUuid);
            remindUserIds.addAll(remindDoingSuperiorUserIds);
        }
        if (CollectionUtils.isNotEmpty(remindSuperviseTemplates)) {
            remindSuperviseUserIds = getSuperviseUserIds(taskInstUuid);
            remindUserIds.addAll(remindSuperviseUserIds);
        }
        if (CollectionUtils.isNotEmpty(remindTracerTemplates)) {
            remindTracerUserIds = getTraceUserIds(taskInstUuid);
            remindUserIds.addAll(remindTracerUserIds);
        }
        if (CollectionUtils.isNotEmpty(remindAdminTemplates)) {
            remindAdminUserIds = getMonitorUserIds(taskInstUuid);
            remindUserIds.addAll(remindAdminUserIds);
        }

        TaskInstance parentTaskInstance = taskInstance.getParent();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String key = parentTaskInstance.getUuid() + userId;
        TaskData msgTaskData = new TaskData();
        Token token = new Token(parentTaskInstance, msgTaskData);
        msgTaskData.setToken(token);
        msgTaskData.setUserId(userId);
        msgTaskData.setFormUuid(parentTaskInstance.getFormUuid());
        msgTaskData.setDataUuid(parentTaskInstance.getDataUuid());
        msgTaskData.setOpinionLabel(key, opinionName);
        msgTaskData.setOpinionValue(key, opinionValue);
        msgTaskData.setOpinionText(key, opinionText);
        msgTaskData.setCustomData("流程实例名称", flowInstance.getTitle());

        // 发送催办消息
        if (CollectionUtils.isNotEmpty(remindTodoUserIds)) {
            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND, remindTodoTemplates,
                    parentTaskInstance, parentFlowInstance, remindTodoUserIds, ParticipantType.TodoUser);
        }
        if (CollectionUtils.isNotEmpty(remindDoingSuperiorUserIds)) {
            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_DOING_SUPERIOR,
                    remindDoingSuperiorTemplates, parentTaskInstance, parentFlowInstance, remindDoingSuperiorUserIds,
                    ParticipantType.TodoUser);
        }
        if (CollectionUtils.isNotEmpty(remindSuperviseUserIds)) {
            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_SUPERVISE,
                    remindSuperviseTemplates, parentTaskInstance, parentFlowInstance, remindSuperviseUserIds,
                    ParticipantType.SuperviseUser);
        }
        if (CollectionUtils.isNotEmpty(remindTracerUserIds)) {
            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_TRACER,
                    remindTracerTemplates, parentTaskInstance, parentFlowInstance, remindTracerUserIds,
                    ParticipantType.SuperviseUser);
        }
        if (CollectionUtils.isNotEmpty(remindAdminUserIds)) {
            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_ADMIN,
                    remindAdminTemplates, parentTaskInstance, parentFlowInstance, remindAdminUserIds,
                    ParticipantType.MonitorUser);
        }

        // 二级分发子流程
        List<TaskInstance> subNormalTaskInstanceList = taskInstanceService.getNormalByParentTaskInstUuid(taskInstUuid);
        if (CollectionUtils.isNotEmpty(subNormalTaskInstanceList)) {
            for (TaskInstance subNormalTaskInstance : subNormalTaskInstanceList) {
                String subNormalTaskInstUuid = subNormalTaskInstance.getUuid();
                if (CollectionUtils.isNotEmpty(remindTodoTemplates)) {
                    remindTodoUserIds = getTodoUserIds(subNormalTaskInstUuid);
                    remindUserIds.addAll(remindTodoUserIds);
                }
                if (CollectionUtils.isNotEmpty(remindDoingSuperiorTemplates)) {
                    remindDoingSuperiorUserIds = getTodoSuperiorUserIds(subNormalTaskInstUuid);
                    remindUserIds.addAll(remindDoingSuperiorUserIds);
                }
                if (CollectionUtils.isNotEmpty(remindSuperviseTemplates)) {
                    remindSuperviseUserIds = getSuperviseUserIds(subNormalTaskInstUuid);
                    remindUserIds.addAll(remindSuperviseUserIds);
                }
                if (CollectionUtils.isNotEmpty(remindTracerTemplates)) {
                    remindTracerUserIds = getTraceUserIds(subNormalTaskInstUuid);
                    remindUserIds.addAll(remindTracerUserIds);
                }
                if (CollectionUtils.isNotEmpty(remindAdminTemplates)) {
                    remindAdminUserIds = getMonitorUserIds(subNormalTaskInstUuid);
                    remindUserIds.addAll(remindAdminUserIds);
                }
                msgTaskData.setCustomData("流程实例名称", subNormalTaskInstance.getFlowInstance().getTitle());
                // 发送催办消息
                if (CollectionUtils.isNotEmpty(remindTodoUserIds)) {
                    MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND,
                            remindTodoTemplates, parentTaskInstance, parentFlowInstance, remindTodoUserIds,
                            ParticipantType.TodoUser);
                }
                if (CollectionUtils.isNotEmpty(remindDoingSuperiorUserIds)) {
                    MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_DOING_SUPERIOR,
                            remindDoingSuperiorTemplates, parentTaskInstance, parentFlowInstance,
                            remindDoingSuperiorUserIds, ParticipantType.TodoUser);
                }
                if (CollectionUtils.isNotEmpty(remindSuperviseUserIds)) {
                    MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_SUPERVISE,
                            remindSuperviseTemplates, parentTaskInstance, parentFlowInstance, remindSuperviseUserIds,
                            ParticipantType.SuperviseUser);
                }
                if (CollectionUtils.isNotEmpty(remindTracerUserIds)) {
                    MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_TRACER,
                            remindTracerTemplates, parentTaskInstance, parentFlowInstance, remindTracerUserIds,
                            ParticipantType.SuperviseUser);
                }
                if (CollectionUtils.isNotEmpty(remindAdminUserIds)) {
                    MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_ADMIN,
                            remindAdminTemplates, parentTaskInstance, parentFlowInstance, remindAdminUserIds,
                            ParticipantType.MonitorUser);
                }
            }
        }

        return Arrays.asList(remindUserIds.toArray(new String[0]));
    }

    private void sendRemindMessage(TaskInstance task, String opinionName, String opinionValue,
                                   FlowInstance flowInstance, String remindOpinionText, List<String> userIds,
                                   List<MessageTemplate> messageTemplates) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String key = task.getUuid() + userId;
        TaskData taskData = new TaskData();
        Token token = new Token(task.getParent() != null ? task.getParent() : task, taskData);
        taskData.setToken(token);
        taskData.setUserId(userId);
        taskData.setFormUuid(task.getFormUuid());
        taskData.setDataUuid(task.getDataUuid());
        taskData.setOpinionLabel(key, opinionName);
        taskData.setOpinionValue(key, opinionValue);
        taskData.setOpinionText(key, remindOpinionText);
        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_REMIND, messageTemplates, task, flowInstance,
                userIds, ParticipantType.TodoUser);
    }

    private List<MessageTemplate> getRemindMessageTemplates(FlowDelegate flowDelegate) {
        List<MessageTemplate> messageTemplates = flowDelegate.getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_REMIND.getType());
        // 催办消息模板若没设置，使用默认模板
        if (messageTemplates == null || messageTemplates.isEmpty()) {
            MessageTemplate remind = new MessageTemplate();
            remind.setId(WorkFlowMessageTemplate.WF_WORK_REMIND.name());
            remind.setTypeName(WorkFlowMessageTemplate.WF_WORK_REMIND.getName());
            remind.setIsSendMsg(MessageTemplate.SEND_MSG);
            if (messageTemplates == null) {
                messageTemplates = new ArrayList<MessageTemplate>();
            }
            messageTemplates.add(remind);
        }
        return messageTemplates;
    }

    /**
     * 工作标记已阅
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#markRead(java.lang.String)
     */
    @Override
    public void markRead(String taskInstUuid) {
        this.markRead(this.taskInstanceService.get(taskInstUuid));
    }

    /**
     * 工作标记已阅
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#markRead(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public void markRead(TaskInstance task) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        // aclTaskService.addPermission(task.getUuid(), AclPermission.FLAG_READ, userId);
        aclTaskService.addFlagReadPermission(userId, PermissionGranularityUtils.getCurrentUserSids(), task.getUuid());
        aclTaskService.removePermission(task.getUuid(), AclPermission.UNREAD, userId);
        readMarkerService.markRead(task.getUuid(), userId);
    }

    /**
     * 工作标记未阅
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#markUnread(java.lang.String)
     */
    @Override
    public void markUnread(String taskInstUuid) {
        this.markUnread(this.taskInstanceService.get(taskInstUuid));
    }

    /**
     * 工作标记未阅
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#markUnread(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public void markUnread(TaskInstance task) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        aclTaskService.addPermission(task.getUuid(), AclPermission.UNREAD, userId);
        aclTaskService.removePermission(task.getUuid(), AclPermission.FLAG_READ, userId);
        readMarkerService.markNew(task.getUuid(), userId);
    }

    /**
     * 工作取消关注
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#unfollow(java.lang.String)
     */
    @Override
    public void unfollow(String taskInstUuid) {
        TaskInstance task = taskInstanceService.get(taskInstUuid);
        this.unfollow(task);
    }

    /**
     * 工作取消关注
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#unfollow(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    public void unfollow(TaskInstance task) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        // 判断是否有关注，如果有就取消关注
        if (aclTaskService.hasPermission(task.getUuid(), AclPermission.ATTENTION, userId)) {
            aclTaskService.removePermission(task.getUuid(), AclPermission.ATTENTION, userId);
        }
    }

    /**
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void handOver(String userId, String taskInstUuid, List<String> userIds, String opinionName, String opinionValue,
                         String opinionText, List<LogicFileInfo> opinionFiles, boolean requiredHandOverPermission) {
        TaskData taskData = new TaskData();
        this.handOver(userId, taskInstUuid, userIds, opinionName, opinionValue, opinionText, opinionFiles, requiredHandOverPermission, taskData);
    }

    @Override
    @Transactional
    public void handOver(String userId, String taskInstUuid, List<String> userIds, String opinionName, String opinionValue
            , String opinionText, List<LogicFileInfo> opinionFiles, boolean requiredHandOverPermission, TaskData taskData) {
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.HAND_OVER);
        TaskInstance taskInstance = this.getTask(taskInstUuid);
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        taskData.setFlowInstUuid(taskInstance.getFlowInstance().getUuid());
        taskData.setUserId(userId);
        taskData.setUserName(userDetails.getUserName());
        taskData.setUserDetails(userDetails);
        // 任务用户
        String key = taskInstUuid + userId;
        // 办理意见
        taskData.setOpinionLabel(key, opinionName);
        taskData.setOpinionValue(key, opinionValue);
        taskData.setOpinionText(key, opinionText);
        taskData.setOpinionFiles(key, opinionFiles);
        // 操作动作
        taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.HAND_OVER));
        taskData.setActionType(key, WorkFlowOperation.HAND_OVER);

        List<String> rawHandOverUser = setTaskUserJobPathsIfRequired(userIds, taskInstance.getId(), taskData);
        HandOverParam param = new HandOverParam(taskInstance, taskData, null, true, requiredHandOverPermission);
        param.setHandOverUser(rawHandOverUser);
        taskExecutor.execute(param);

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        flowIndexDocumentService.indexWorkflowDocument(taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#signOpinion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void signOpinion(String taskInstUuid, String text, String value) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        TaskData taskData = new TaskData();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        taskData.setUserId(userId);
        taskData.setUserName(userName);
        String key = taskInstance.getUuid() + userId;
        taskData.setOpinionText(key, text);
        taskData.setOpinionValue(key, value);
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.SIGN_OPINION),
                ActionCode.SIGN_OPINION.getCode(), WorkFlowOperation.SIGN_OPINION, value, value, text, userId, null,
                null, null, null, taskInstance, flowInstance, taskData);
    }

    /**
     * 获取可跳转的环节，不包含开始环节、当前环节、子环节
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getToTasks(java.lang.String)
     */
    @Override
    public Map<String, Object> getToTasks(String taskInstUuid) {
        TaskInstance task = taskInstanceService.get(taskInstUuid);
        FlowDelegate flowDelegate = new FlowDelegate(task.getFlowDefinition());
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, String>> toTasks = new ArrayList<Map<String, String>>();
        String fromTaskId = task.getId();
        for (Node node : nodes) {
            // 不包含开始环节、子环节
            if (node instanceof StartNode || node instanceof SubTaskNode) {
                continue;
            }
            // 不包含当前环节
            String id = node.getId();
            if (fromTaskId.equals(id) && task.getEndTime() == null) {
                continue;
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("id", id);
            map.put("name", node.getName());

            toTasks.add(map);
        }

        // 结束环节
        if (task.getEndTime() == null) {
            Map<String, String> endMap = new HashMap<String, String>();
            endMap.put("id", FlowDelegate.END_FLOW_ID);
            endMap.put("name", "送结束");
            toTasks.add(endMap);
        }

        result.put("fromTaskId", fromTaskId);
        result.put("toTasks", toTasks);
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#suspend(java.lang.String)
     */
    @Override
    public void suspend(String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        taskInstance.setSuspensionState(SuspensionState.LOGIC_SUSPEND.getState());
        this.taskInstanceService.save(taskInstance);

        // 用户操作日志
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        BusinessOperationLog bussinessLog = new BusinessOperationLog();
        bussinessLog.setModuleId(flowDefinition.getModuleId());
        bussinessLog.setDataDefType(ModuleID.WORKFLOW.getValue());
        bussinessLog.setDataDefId(flowDefinition.getId());
        bussinessLog.setDataDefName(flowDefinition.getName());
        bussinessLog.setOperation(WorkFlowOperation.getName(WorkFlowOperation.SUSPEND));
        bussinessLog.setUserId(SpringSecurityUtils.getCurrentUserId());
        bussinessLog.setUserName(SpringSecurityUtils.getCurrentUserName());
        bussinessLog.setDataId(flowInstance.getUuid());
        bussinessLog.setDataName(flowInstance.getTitle());
        Map<String, Object> details = Maps.newHashMap();
        ContextLogs.sendLogEvent(new LogEvent(bussinessLog, details));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#resume(java.lang.String)
     */
    @Override
    public void resume(String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        taskInstance.setSuspensionState(SuspensionState.NORMAL.getState());
        this.taskInstanceService.save(taskInstance);

        // 用户操作日志
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        BusinessOperationLog bussinessLog = new BusinessOperationLog();
        bussinessLog.setModuleId(flowDefinition.getModuleId());
        bussinessLog.setDataDefType(ModuleID.WORKFLOW.getValue());
        bussinessLog.setDataDefId(flowDefinition.getId());
        bussinessLog.setDataDefName(flowDefinition.getName());
        bussinessLog.setOperation(WorkFlowOperation.getName(WorkFlowOperation.RESUME));
        bussinessLog.setUserId(SpringSecurityUtils.getCurrentUserId());
        bussinessLog.setUserName(SpringSecurityUtils.getCurrentUserName());
        bussinessLog.setDataId(flowInstance.getUuid());
        bussinessLog.setDataName(flowInstance.getTitle());
        Map<String, Object> details = Maps.newHashMap();
        ContextLogs.sendLogEvent(new LogEvent(bussinessLog, details));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#suspendTimer(java.lang.String)
     */
    @Override
    public void suspendTimer(String taskInstUuid) {
        // TaskHandler taskHandler =
        // ApplicationContextHolder.getBean(TaskHandler.class);
        // TaskInstance taskInstance = taskDao.get(taskInstUuid);
        // taskHandler.pauseTimer(null, taskInstance.getFlowInstance(),
        // taskInstance);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#resumeTimer(java.lang.String)
     */
    @Override
    public void resumeTimer(String taskInstUuid) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#reject(java.lang.String)
     */
    @Override
    public void reject(String taskInstUuid) {
        TaskData taskData = new TaskData();
        submit(taskInstUuid, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#startBranchTask(java.lang.String, java.lang.String, boolean, java.util.List, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<String> startBranchTask(String belongToTaskInstUuid, String belongToFlowInstUuid, boolean isMajor,
                                        List<String> taskUsers, String businessType, String businessRole, String actionName) {
        TaskInstance parallelTaskInstance = getTask(belongToTaskInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parallelTaskInstance.getFlowDefinition());
        // 获取配置的分支流向
        String parallelTaskId = parallelTaskInstance.getId();
        List<Direction> branchDirections = getBranchDirections(parallelTaskId, isMajor, flowDelegate);
        if (CollectionUtils.isEmpty(branchDirections)) {
            String errorMsg = "流程分支环节[" + parallelTaskInstance.getName() + "]没有配置对应的动态" + (isMajor ? "主办" : "协办")
                    + "分支！";
            throw new WorkFlowException(errorMsg);
        }

        return doStartBranchTask(parallelTaskInstance, belongToFlowInstUuid, flowDelegate, branchDirections, taskUsers,
                false, actionName);
    }

    /**
     * @param parallelTaskInstance
     * @param belongToFlowInstUuid
     * @param flowDelegate
     * @param branchDirections
     * @param taskUsers
     * @param actionName
     * @return
     */
    private List<String> doStartBranchTask(TaskInstance parallelTaskInstance, String belongToFlowInstUuid,
                                           FlowDelegate flowDelegate, List<Direction> branchDirections, List<String> taskUsers,
                                           boolean useFormFieldUsers, String actionName) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        List<String> branchTaskUuids = Lists.newArrayList();

        boolean isMergedBranchTaskFinished = taskBranchService
                .isBranchTaskCompletedByParallelTaskInstUuid(parallelTaskInstance.getUuid());

        String fromTaskId = parallelTaskInstance.getId();
        String formUuid = parallelTaskInstance.getFormUuid();
        String dataUuid = parallelTaskInstance.getDataUuid();
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        // 分发数据
        TaskData taskData = new TaskData();
        taskData.setUserId(userDetails.getUserId());
        taskData.setUserName(userDetails.getUserName());
        taskData.setFlowInstUuid(belongToFlowInstUuid);
        taskData.setFormUuid(formUuid);
        taskData.setDataUuid(dataUuid);
        taskData.setDyFormData(dataUuid, dyFormData);
        ReservedFieldUtils.setReservedFields(dyFormData, taskData);

        taskData.setTaskForking(fromTaskId, true);

        // 使用表单字段的办理人
        taskData.setUseFormFieldUsers(fromTaskId, useFormFieldUsers);
        // 添加记录日志
        taskData.setLogAddBranchTask(fromTaskId, true);
        taskData.setAddBranchTaskActionName(fromTaskId, actionName);

        Transition transition = flowDelegate.getTaskNode(fromTaskId).getLeavingTransition();
        transition.getTos().clear();
        for (Direction branchDirection : branchDirections) {
            String toTaskId = branchDirection.getToID();
            taskData.addTaskUsers(toTaskId, taskUsers);
            taskData.setPreTaskProperties(toTaskId, FlowConstant.BRANCH.IS_MERGED_BRANCH_TASK_FINISHED,
                    isMergedBranchTaskFinished);
            transition.getTos().add(flowDelegate.getTaskNode(toTaskId));
        }
        Token token = new Token(parallelTaskInstance, taskData);
        ExecutionContext executionContext = new ExecutionContext(token);
        transition.take(executionContext);

        branchTaskUuids.addAll(taskData.getSubmitResult().getTaskInstUUids());
        return branchTaskUuids;
    }

    /**
     * @param parallelTaskId
     * @param isMajor
     * @param flowDelegate
     * @return
     */
    private List<Direction> getBranchDirections(String parallelTaskId, boolean isMajor, FlowDelegate flowDelegate) {
        List<Direction> returnDirections = Lists.newArrayList();
        List<Direction> directions = flowDelegate.getDirections(parallelTaskId);
        for (Direction direction : directions) {
            // 动态多分支
            if (!FlowConstant.BRANCH_MODE.DYNAMIC.equals(direction.getBranchMode())) {
                continue;
            }
            // 主办分支
            if (isMajor) {
                if (FlowConstant.BRANCH_TYPE.MAJOR.equals(direction.getBranchInstanceType())) {
                    returnDirections.add(direction);
                }
            } else {
                // 协办分支
                if (FlowConstant.BRANCH_TYPE.MINOR.equals(direction.getBranchInstanceType())) {
                    returnDirections.add(direction);
                }
            }
        }
        return returnDirections;
    }

    /**
     * @param parallelTaskId
     * @param flowDelegate
     * @return
     */
    private List<Direction> getDynamicBranchDirections(String parallelTaskId, FlowDelegate flowDelegate) {
        List<Direction> returnDirections = Lists.newArrayList();
        List<Direction> directions = flowDelegate.getDirections(parallelTaskId);
        for (Direction direction : directions) {
            // 动态多分支
            if (!FlowConstant.BRANCH_MODE.DYNAMIC.equals(direction.getBranchMode())) {
                continue;
            }
            // 主办分支
            if (FlowConstant.BRANCH_TYPE.MAJOR.equals(direction.getBranchInstanceType())) {
                returnDirections.add(direction);
            }
            // 协办分支
            if (FlowConstant.BRANCH_TYPE.MINOR.equals(direction.getBranchInstanceType())) {
                returnDirections.add(direction);
            }
        }
        return returnDirections;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#startBranchTask(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<String> startBranchTask(String belongToTaskInstUuid, String belongToFlowInstUuid, String businessType,
                                        String businessRole, String actionName) {
        TaskInstance parallelTaskInstance = getTask(belongToTaskInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parallelTaskInstance.getFlowDefinition());
        String parallelTaskId = parallelTaskInstance.getId();
        // 获取配置的分支流向
        List<Direction> branchDirections = getDynamicBranchDirections(parallelTaskId, flowDelegate);
        if (CollectionUtils.isEmpty(branchDirections)) {
            String errorMsg = "流程分支环节[" + parallelTaskInstance.getName() + "]没有配置对应的动态主办或协办分支！";
            throw new WorkFlowException(errorMsg);
        }

        List<String> taskUsers = Collections.emptyList();
        return doStartBranchTask(parallelTaskInstance, belongToFlowInstUuid, flowDelegate, branchDirections, taskUsers,
                true, actionName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#startSubFlow(java.lang.String, java.lang.String, java.lang.String, boolean, java.util.List, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<String> startSubFlow(String parentTaskInstUuid, String parentFlowInstUuid, String newFlowId,
                                     boolean isMajor, List<String> taskUsers, String businessType, String businessRole, String actionName) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        TaskInstance parentTaskInstance = taskInstanceService.get(parentTaskInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentTaskInstance.getFlowDefinition());
        // 获取分发环节
        Node node = flowDelegate.getTaskNode(parentTaskInstance.getId());
        SubTaskNode subTaskNode = (SubTaskNode) node;
        List<NewFlow> newFlows = getNewFlows(newFlowId, isMajor, subTaskNode);
        if (CollectionUtils.isEmpty(newFlows)) {
            String errorMsg = "子流程环节[" + subTaskNode.getName() + "]没有配置对应的" + (isMajor ? "主办" : "协办") + "子流程！";
            throw new WorkFlowException(errorMsg);
        }

        String formUuid = parentTaskInstance.getFormUuid();
        String dataUuid = parentTaskInstance.getDataUuid();
        TaskData taskData = new TaskData();
        taskData.setUserId(userDetails.getUserId());
        taskData.setUserName(userDetails.getUserName());
        taskData.setFormUuid(formUuid);
        taskData.setDataUuid(dataUuid);
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        taskData.setDyFormData(dataUuid, dyFormData);
        for (NewFlow newFlow : newFlows) {
            taskData.addTaskUsers(newFlow.getId(), taskUsers);
        }
        ReservedFieldUtils.setReservedFields(dyFormData, taskData);
        // 添加记录日志
        taskData.setLogAddSubflow(subTaskNode.getId(), true);
        taskData.setAddSubflowActionName(subTaskNode.getId(), actionName);

        Token token = new Token(parentTaskInstance, taskData);
        // bug#58194: hibernate.enable_lazy_load_no_trans设置为true或即时加载流程定义
        // parentTaskInstance.getFlowInstance().getFlowDefinition().getMultiJobFlowType();
        ExecutionContext executionContext = new ExecutionContext(token);
        subTaskNode.setNewFlows(newFlows);
        subTaskNode.setCheckInStage(false);
        executionContext.startSubTasks(subTaskNode);

        return taskData.getSubmitResult().getSubFlowInstUUids();
    }

    /**
     * @param newFlowId
     * @param isMajor
     * @param subTaskNode
     * @return
     */
    private List<NewFlow> getNewFlows(String newFlowId, boolean isMajor, SubTaskNode subTaskNode) {
        List<NewFlow> configNewFlows = subTaskNode.getNewFlows();
        List<NewFlow> returnNewFlows = Lists.newArrayList();
        if (StringUtils.isBlank(newFlowId)) {
            for (NewFlow configNewFlow : configNewFlows) {
                if (isMajor) {
                    if (Boolean.TRUE.equals(configNewFlow.isMajor())) {
                        returnNewFlows.add(configNewFlow);
                    }
                } else {
                    if (Boolean.FALSE.equals(configNewFlow.isMajor())) {
                        returnNewFlows.add(configNewFlow);
                    }
                }
            }
        } else {
            List<String> newFlowIds = Lists.newArrayList(StringUtils.split(newFlowId, Separator.SEMICOLON.getValue()));
            for (NewFlow configNewFlow : configNewFlows) {
                // 发起指定流程
                if (newFlowIds.contains(configNewFlow.getId())) {
                    returnNewFlows.add(configNewFlow);
                }
            }
        }
        return returnNewFlows;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#startSubFlow(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<String> startSubFlow(String parentTaskInstUuid, String parentFlowInstUuid, String businessType,
                                     String businessRole, String actionName) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        TaskInstance parentTaskInstance = taskInstanceService.get(parentTaskInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentTaskInstance.getFlowDefinition());
        // 获取分发环节
        Node node = flowDelegate.getTaskNode(parentTaskInstance.getId());
        SubTaskNode subTaskNode = (SubTaskNode) node;
        String subTaskId = subTaskNode.getId();

        String formUuid = parentTaskInstance.getFormUuid();
        String dataUuid = parentTaskInstance.getDataUuid();
        TaskData taskData = new TaskData();
        taskData.setUserId(userDetails.getUserId());
        taskData.setUserName(userDetails.getUserName());
        taskData.setFormUuid(formUuid);
        taskData.setDataUuid(dataUuid);
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        taskData.setDyFormData(dataUuid, dyFormData);
        ReservedFieldUtils.setReservedFields(dyFormData, taskData);
        // 使用表单字段的办理人
        taskData.setUseFormFieldUsers(subTaskId, true);
        // 添加记录日志
        taskData.setLogAddSubflow(subTaskId, true);
        taskData.setAddSubflowActionName(subTaskId, actionName);

        Token token = new Token(parentTaskInstance, taskData);
        ExecutionContext executionContext = new ExecutionContext(token);
        subTaskNode.setCheckInStage(false);
        executionContext.startSubTasks(subTaskNode);

        return taskData.getSubmitResult().getSubFlowInstUUids();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#resendSubFlow(java.lang.String)
     */
    @Override
    public void resendSubFlow(String parentTaskInstUuid) {
        taskSubFlowDispatchService.resendByParentTaskInstUuid(parentTaskInstUuid);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void stopByParentTaskInstUuid(String parentTaskInstUuid, String action, String actionType,
                                         String opinionLabel, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles) {
        List<TaskSubFlow> alltaskSubFlows = taskSubFlowService.getAllByParentTaskInstUuid(parentTaskInstUuid);
        // 子流程分发
        long dispatchCount = taskSubFlowDispatchService.countDispatchingByParentTaskInstUuid(parentTaskInstUuid);
        if (dispatchCount > 0) {
            throw new WorkFlowException("子流程分发中，不能进行撤回主流程或退回主流程操作！");
        }
        for (TaskSubFlow taskSubFlow : alltaskSubFlows) {
            stopByTaskSubFlow(taskSubFlow, action, actionType, opinionLabel, opinionValue, opinionText, opinionFiles);
        }
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void stopByParentFlowInstUuid(String parentFlowInstUuid, String action, String actionType,
                                         String opinionLabel, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles) {
        List<TaskSubFlow> alltaskSubFlows = taskSubFlowService.getAllByParentFlowInstUuid(parentFlowInstUuid);
        // 子流程分发
        long dispatchCount = taskSubFlowDispatchService.countDispatchingByParentFlowInstUuid(parentFlowInstUuid);
        if (dispatchCount > 0) {
            throw new WorkFlowException("子流程分发中，不能进行撤回主流程或退回主流程操作！");
        }
        for (TaskSubFlow taskSubFlow : alltaskSubFlows) {
            stopByTaskSubFlow(taskSubFlow, action, actionType, opinionLabel, opinionValue, opinionText, opinionFiles);
        }
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void stopByParentFlowInstUuid(String parentFlowInstUuid, String action, String actionType,
                                         String opinionLabel, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles, boolean sendMsg) {
        List<TaskSubFlow> alltaskSubFlows = taskSubFlowService.getAllByParentFlowInstUuid(parentFlowInstUuid);
        for (TaskSubFlow taskSubFlow : alltaskSubFlows) {
            stopByTaskSubFlow(taskSubFlow, action, actionType, opinionLabel, opinionValue, opinionText, opinionFiles, sendMsg);
        }
    }

    /**
     * @param taskSubFlow
     * @param action
     * @param actionType
     * @param opinionLabel
     * @param opinionValue
     * @param opinionText
     */
    @Override
    public void stopByTaskSubFlow(TaskSubFlow taskSubFlow, String action, String actionType, String opinionLabel,
                                  String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles) {
        this.stopByTaskSubFlow(taskSubFlow, action, actionType, opinionLabel, opinionValue, opinionText, opinionFiles, false);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void stopByTaskSubFlow(TaskSubFlow taskSubFlow, String action, String actionType, String opinionLabel,
                                  String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles, boolean sendMsg) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        if (Boolean.TRUE.equals(taskSubFlow.getCompleted())) {
            return;
        }

        taskSubFlow.setCompleted(true);
        if (WorkFlowOperation.CANCEL.equals(actionType)) {
            taskSubFlow.setCompletionState(TaskSubFlow.STATUS_CANCEL);
        } else if (WorkFlowOperation.ROLLBACK_TO_MAIN_FLOW.equals(actionType)) {
            taskSubFlow.setCompletionState(TaskSubFlow.STATUS_ROLLBACK);
        } else {
            taskSubFlow.setCompletionState(TaskSubFlow.STATUS_STOP);
        }
        taskSubFlowService.save(taskSubFlow);
        this.dao.getSession().flush();

        List<TaskInstance> unfinishedSubTasks = getUnfinishedTasks(taskSubFlow.getFlowInstUuid());
        for (TaskInstance unfinishedSubTask : unfinishedSubTasks) {
            // 子流程终止通知——子流程办理人
            List<MessageTemplate> todoMessageTemplates = null;
            List<String> messageTodoUserIds = null;
            // 子流程终止通知——子流程全部已办人员
            List<MessageTemplate> doneMessageTemplates = null;
            List<String> messageDoneUserIds = null;
            // 发送子流程终止消息
            if (sendMsg) {
                TaskInstance parentTaskInstance = unfinishedSubTask.getParent();
                if (parentTaskInstance != null) {
                    FlowDelegate flowDelegate = FlowDelegateUtils
                            .getFlowDelegate(parentTaskInstance.getFlowDefinition());
                    todoMessageTemplates = flowDelegate.getMessageTemplateMap()
                            .get(WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP.getType());
                    doneMessageTemplates = flowDelegate.getMessageTemplateMap()
                            .get(WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP_DONE.getType());
                    if (StringUtils.isNotBlank(unfinishedSubTask.getTodoUserId())) {
                        messageTodoUserIds = Arrays.asList(StringUtils.split(unfinishedSubTask.getTodoUserId()));
                    }
                    if (CollectionUtils.isNotEmpty(doneMessageTemplates)) {
                        messageDoneUserIds = getDoneUserIds(unfinishedSubTask.getUuid());
                    }
                }
            }

            String subTaskKey = unfinishedSubTask.getUuid() + userId;
            TaskData gotoTaskData = new TaskData();
            gotoTaskData.setSilent(unfinishedSubTask.getFlowInstance().getUuid(), true);
            gotoTaskData.setUserId(userId);
            gotoTaskData.setOpinionText(subTaskKey, opinionText);
            gotoTaskData.setOpinionLabel(subTaskKey, opinionLabel);
            gotoTaskData.setOpinionValue(subTaskKey, opinionValue);
            gotoTaskData.setOpinionFiles(subTaskKey, opinionFiles);
            gotoTaskData.setAction(subTaskKey, action);
            gotoTaskData.setActionType(subTaskKey, actionType);
            if (StringUtils.equals(WorkFlowOperation.CANCEL, actionType)) {
                gotoTaskData.setActionCode(unfinishedSubTask.getUuid(), ActionCode.CANCEL.getCode());
            } else if (StringUtils.equals(WorkFlowOperation.ROLLBACK_TO_MAIN_FLOW, actionType)) {
                gotoTaskData.setActionCode(unfinishedSubTask.getUuid(), ActionCode.ROLLBACK.getCode());
            } else if (StringUtils.equals(WorkFlowOperation.STOP, actionType)) {
                gotoTaskData.setActionCode(unfinishedSubTask.getUuid(), ActionCode.STOP.getCode());
            }
            String gotoTaskId = FlowDelegate.END_FLOW_ID;
            GotoTaskParam gotoTaskParam = new GotoTaskParam(unfinishedSubTask, gotoTaskData, null, true, gotoTaskId,
                    false);
            TaskExecutor gotoTaskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.GOTO_TASK);
            gotoTaskExecutor.execute(gotoTaskParam);

            // 发送子流程终止消息
            if (sendMsg) {
                if (CollectionUtils.isNotEmpty(messageTodoUserIds)) {
                    MessageClientUtils.send(gotoTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP,
                            todoMessageTemplates, unfinishedSubTask, unfinishedSubTask.getFlowInstance(),
                            messageTodoUserIds, ParticipantType.TodoUser);
                }
                if (CollectionUtils.isNotEmpty(messageDoneUserIds)) {
                    MessageClientUtils.send(gotoTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REDO_DONE,
                            doneMessageTemplates, unfinishedSubTask, unfinishedSubTask.getFlowInstance(),
                            messageDoneUserIds, ParticipantType.TodoUser);
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#stopBranchTask(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.bpm.engine.support.InteractionTaskData)
     */
    @Override
    public void stopBranchTask(String taskInstUuid, String action, String actionType, List<String> actionObjects,
                               String opinionLabel, String opinionValue, String opinionText, InteractionTaskData interactionTaskData) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String userId = userDetails.getUserId();
        TaskInstance taskInstance = this.getTask(taskInstUuid);
        String taskId = taskInstance.getId();
        String flowInstUuid = getFlowInstUUidByTaskInstUuid(taskInstUuid);

        String branchTaskKey = taskInstUuid + userId;
        TaskData taskData = new TaskData();
        taskData.setSilent(flowInstUuid, false);
        taskData.setUserId(userId);
        taskData.setUserName(userDetails.getUserName());
        taskData.setFormUuid(taskInstance.getFormUuid());
        taskData.setDataUuid(taskInstance.getDataUuid());
        taskData.setOpinionText(branchTaskKey, opinionText);
        taskData.setOpinionLabel(branchTaskKey, opinionLabel);
        taskData.setOpinionValue(branchTaskKey, opinionValue);
        taskData.setAction(branchTaskKey, action);
        taskData.setActionType(branchTaskKey, actionType);
        taskData.put(branchTaskKey + "_actionObjects", actionObjects);
        // 合并交互性数据
        mergeInteractionTaskData(taskInstance, taskData, interactionTaskData);

        // 最后一个分支，提交走
        boolean isTheLastMergeBranch = taskBranchService.isTheLastMergeBranch(taskInstance);
        if (isTheLastMergeBranch) {
            try {
                IgnoreLoginUtils.login(userDetails.getTenantId(), actionObjects.get(0));
                // 删除待办权限
                for (String actionObject : actionObjects) {
                    removeTodoPermission(actionObject, taskInstUuid);
                }
                TaskExecutor todoSubmitTaskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
                Param param = new Param(taskInstance, taskData);
                todoSubmitTaskExecutor.execute(param);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                IgnoreLoginUtils.logout();
            }
        } else {
            // 不是最后一个分支，送结束
            taskData.setGotoTask(taskId, true);
            String gotoTaskId = FlowDelegate.END_FLOW_ID;
            GotoTaskParam gotoTaskParam = new GotoTaskParam(taskInstance, taskData, null, true, gotoTaskId, false);
            TaskExecutor gotoTaskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.GOTO_TASK);
            gotoTaskExecutor.execute(gotoTaskParam);
        }

        // 终止分支
        taskBranchService.stopBranchTaskByCurrentTaskInstUuid(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#stopFlow(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.bpm.engine.support.InteractionTaskData)
     */
    @Override
    public void stopFlow(String flowInstUuid, String action, String actionType, List<String> actionObjects,
                         String opinionLabel, String opinionValue, String opinionText, InteractionTaskData interactionTaskData) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String userId = userDetails.getUserId();
        // 结束子流程关系
        List<TaskSubFlow> alltaskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstUuid);
        for (TaskSubFlow taskSubFlow : alltaskSubFlows) {
            taskSubFlow.setCompleted(true);
            taskSubFlow.setCompletionState(TaskSubFlow.STATUS_STOP);
            taskSubFlowService.save(taskSubFlow);
            this.dao.getSession().flush();
        }
        // 结束流程
        List<TaskInstance> unfinishedSubTasks = getUnfinishedTasks(flowInstUuid);
        for (TaskInstance unfinishedSubTask : unfinishedSubTasks) {
            String subTaskKey = unfinishedSubTask.getUuid() + userId;
            TaskData gotoTaskData = new TaskData();
            gotoTaskData.setGotoTask(unfinishedSubTask.getId(), true);
            gotoTaskData.setSilent(unfinishedSubTask.getFlowInstance().getUuid(), false);
            gotoTaskData.setUserId(userId);
            gotoTaskData.setUserName(userDetails.getUserName());
            gotoTaskData.setOpinionText(subTaskKey, opinionText);
            gotoTaskData.setOpinionLabel(subTaskKey, opinionLabel);
            gotoTaskData.setOpinionValue(subTaskKey, opinionValue);
            gotoTaskData.setAction(subTaskKey, action);
            gotoTaskData.setActionType(subTaskKey, actionType);
            gotoTaskData.setActionCode(unfinishedSubTask.getUuid(), ActionCode.STOP.getCode());
            gotoTaskData.put(subTaskKey + "_actionObjects", actionObjects);
            // 合并交互性数据
            mergeInteractionTaskData(unfinishedSubTask, gotoTaskData, interactionTaskData);
            String gotoTaskId = FlowDelegate.END_FLOW_ID;
            GotoTaskParam gotoTaskParam = new GotoTaskParam(unfinishedSubTask, gotoTaskData, null, true, gotoTaskId,
                    false);
            TaskExecutor gotoTaskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.GOTO_TASK);
            gotoTaskExecutor.execute(gotoTaskParam);
        }
    }

    /**
     * @param taskInstance
     * @param gotoTaskData
     * @param interactionTaskData
     */
    private void mergeInteractionTaskData(TaskInstance taskInstance, TaskData gotoTaskData,
                                          InteractionTaskData interactionTaskData) {
        if (interactionTaskData == null) {
            return;
        }
        String fromTaskId = interactionTaskData.getFromTaskId();
        gotoTaskData.setToDirectionId(fromTaskId, interactionTaskData.getToDirectionId());
        gotoTaskData.setToDirectionIds(interactionTaskData.getToDirectionIds());
        gotoTaskData.setToTaskId(fromTaskId, interactionTaskData.getToTaskId());
        gotoTaskData.setToTaskIds(interactionTaskData.getToTaskIds());
        gotoTaskData.setTaskUsers(interactionTaskData.getTaskUsers());
        gotoTaskData.addTaskCopyUsers(interactionTaskData.getTaskCopyUsers());
        gotoTaskData.addTaskMonitors(interactionTaskData.getTaskMonitors());
        gotoTaskData.setToSubFlowId(interactionTaskData.getToSubFlowId());
        gotoTaskData.setWaitForMerge(interactionTaskData.getWaitForMerge());
        gotoTaskData.setRollbackToTaskId(taskInstance.getUuid(), interactionTaskData.getRollbackToTaskId());
        gotoTaskData.setRollbackToTaskInstUuid(taskInstance.getUuid(), interactionTaskData.getRollbackToTaskInstUuid());
        gotoTaskData.setCustomData("gotoTaskId", interactionTaskData.getGotoTaskId());
        gotoTaskData.setArchiveFolderUuid(taskInstance.getId(), interactionTaskData.getArchiveFolderUuid());
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void distributeInfo(String taskInstUuid, String content, List<String> fileIds) {
        TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
        TaskInfoDistribution distribution = new TaskInfoDistribution();
        distribution.setFlowInstUuid(taskInstance.getFlowInstance().getUuid());
        distribution.setTaskInstUuid(taskInstUuid);
        distribution.setContent(content);
        distribution.setRepoFileIds(StringUtils.join(fileIds, Separator.SEMICOLON.getValue()));
        this.dao.save(distribution);

        // 分发附件
        if (CollectionUtils.isNotEmpty(fileIds)) {
            mongoFileService.pushFilesToFolder(distribution.getUuid(), fileIds, null);
        }

        // 添加环节操作日志
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = new TaskData();
        taskData.setUserId(user.getUserId());
        taskData.setUserName(user.getUserName());
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.DISTRIBUTE_INFO),
                ActionCode.DISTRIBUTE_INFO.getCode(), WorkFlowOperation.DISTRIBUTE_INFO, null, null, content,
                user.getUserId(), getTodoUserIds(taskInstUuid), null, null, null, taskInstance,
                taskInstance.getFlowInstance(), taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getDistributeInfos(java.lang.String)
     */
    @Override
    public List<TaskInfoDistribution> getDistributeInfos(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.nativeDao.namedQuery("taskInfoiDistributionQuery", values, TaskInfoDistribution.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#changeFlowDueTime(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void changeFlowDueTime(String flowInstUuid, String dueTimeString, String opinionText) {
        if (!timerExecutor.hasTimerConfiguration(flowInstUuid)) {
            throw new WorkFlowException("流程没有配置计时系统，不能变更时限！");
        }

        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        if (flowInstance.getEndTime() != null) {
            throw new WorkFlowException("流程已办结，不能变更时限！");
        }

        // 办理时限
        Date dueTime = null;
        // 当前时间
        Date currentDate = null;
        try {
            dueTime = DateUtils.parse(dueTimeString);
            // 当前时间
            currentDate = Calendar.getInstance().getTime();
            String pattern = DateUtils.parsePattern(dueTimeString);
            currentDate = DateUtils.parse(DateUtils.format(currentDate, pattern));
        } catch (ParseException e1) {
            throw new WorkFlowException("办理时限[" + dueTimeString + "]解析出误，不能变更时限！");
        }
        if (!dueTime.after(currentDate)) {
            throw new WorkFlowException("办理时限必须大于当前时间！");
        }

        // 变更时限
        timerExecutor.changeDueTime(dueTime, flowInstUuid);
        // 重启计时器
        timerExecutor.restart(flowInstUuid);

        // 子流程办理时限修改通知——子流程办理人
        List<MessageTemplate> messageTemplates = null;
        FlowInstance parentFlowInstance = flowInstance.getParent();
        if (parentFlowInstance != null) {
            messageTemplates = FlowDelegateUtils.getFlowDelegate(parentFlowInstance.getFlowDefinition())
                    .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_TIMELIMIT_MODIFY.getType());
        }

        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 发送消息、操作日志
        List<TaskInstance> todoTasks = getUnfinishedTasks(flowInstUuid);
        for (TaskInstance taskInstance : todoTasks) {
            TaskInstance parentTaskInstance = taskInstance.getParent();
            if (parentTaskInstance == null) {
                parentTaskInstance = taskInstance;
            }
            TaskData msgTaskData = new TaskData();
            Token token = new Token(parentTaskInstance, msgTaskData);
            msgTaskData.setToken(token);
            msgTaskData.setUserId(user.getUserId());
            msgTaskData.setUserName(user.getUserName());
            msgTaskData.setCustomData("流程实例名称", flowInstance.getTitle());
            if (flowInstance.getDueTime() != null) {
                msgTaskData.setCustomData("调整后办理时限", DateUtils.formatDate(flowInstance.getDueTime()));
            } else {
                msgTaskData.setCustomData("调整后办理时限", dueTimeString);
            }
            // 发送消息
            if (CollectionUtils.isNotEmpty(messageTemplates)) {
                List<String> messageTodoUserIds = Lists.newArrayList();
                // 二级分发子流程
                if (Integer.valueOf(2).equals(taskInstance.getType())) {
                    List<TaskInstance> subNormalTaskInstanceList = taskInstanceService
                            .getNormalByParentTaskInstUuid(taskInstance.getUuid());
                    for (TaskInstance subNormalTaskInstance : subNormalTaskInstanceList) {
                        messageTodoUserIds.addAll(getTodoUserIds(subNormalTaskInstance.getUuid()));
                    }
                } else {
                    messageTodoUserIds = getTodoUserIds(taskInstance.getUuid());
                }
                MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_TIMELIMIT_MODIFY,
                        messageTemplates, parentTaskInstance, parentTaskInstance.getFlowInstance(), messageTodoUserIds,
                        ParticipantType.TodoUser);
            }

            // 添加环节操作日志
            TaskData taskData = new TaskData();
            taskData.setUserId(user.getUserId());
            taskData.setUserName(user.getUserName());
            // 操作日志
            String fullOpinionText = "变更时限：" + DateUtils.formatDate(dueTime) + (StringUtils.isNotBlank(opinionText) ? ("，") + opinionText : StringUtils.EMPTY);
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CHANGE_LIMIT_TIME),
                    ActionCode.CHANGE_LIMIT_TIME.getCode(), WorkFlowOperation.CHANGE_LIMIT_TIME, null, null,
                    fullOpinionText, user.getUserId(), getTodoUserIds(taskInstance.getUuid()),
                    null, null, null, taskInstance, taskInstance.getFlowInstance(), taskData);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#changeTaskDueTime(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void changeTaskDueTime(String taskInstUuid, String flowInstUuid, String dueTimeString, String opinionText) {
        if (!timerExecutor.hasTimerConfiguration(flowInstUuid)) {
            throw new WorkFlowException("流程没有配置计时系统，不能变更时限！");
        }

        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        if (flowInstance.getEndTime() != null) {
            throw new WorkFlowException("流程已办结，不能变更时限！");
        }
        // 办理时限
        Date dueTime = null;
        // 当前时间
        Date currentDate = null;
        try {
            dueTime = DateUtils.parse(dueTimeString);
            // 当前时间
            currentDate = Calendar.getInstance().getTime();
            String pattern = DateUtils.parsePattern(dueTimeString);
            currentDate = DateUtils.parse(DateUtils.format(currentDate, pattern));
        } catch (ParseException e1) {
            throw new WorkFlowException("办理时限[" + dueTimeString + "]解析出误，不能变更时限！");
        }
        if (!dueTime.after(currentDate)) {
            throw new WorkFlowException("办理时限必须大于当前时间！");
        }

        TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
        if (taskInstance.getEndTime() != null) {
            throw new WorkFlowException("流程分支已办结，不能变更时限！");
        }

        if (!timerExecutor.isTaskInstanceInTimer(taskInstance.getUuid())) {
            throw new WorkFlowException("流程分支未配置计时器，不能变更时限！");
        }

        // 变更时限
        timerExecutor.changeDueTime(dueTime, taskInstUuid, flowInstUuid);
        // 重启计时器
        timerExecutor.restart(taskInstUuid, flowInstUuid);

        // 添加环节操作日志
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = new TaskData();
        taskData.setUserId(user.getUserId());
        taskData.setUserName(user.getUserName());
        String fullOpinionText = "变更时限：" + DateUtils.formatDate(dueTime) + (StringUtils.isNotBlank(opinionText) ? ("，") + opinionText : StringUtils.EMPTY);
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.CHANGE_LIMIT_TIME),
                ActionCode.CHANGE_LIMIT_TIME.getCode(), WorkFlowOperation.CHANGE_LIMIT_TIME, null, null,
                fullOpinionText, user.getUserId(), getTodoUserIds(taskInstance.getUuid()), null,
                null, null, taskInstance, taskInstance.getFlowInstance(), taskData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimerWorkTime> listTimerWorkTimeByTaskInstUuid(String taskInstUuid) {
        List<TimerWorkTime> timerWorkTimes = Lists.newArrayList();
        List<TaskTimer> taskTimers = taskTimerService.listByTaskInstUuid(taskInstUuid);
        taskTimers.forEach(taskTimer -> {
            timerWorkTimes.add(timerFacadeService.getTimerWorkTime(taskTimer.getTimerUuid()));
        });
        return timerWorkTimes;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void gotoTask(String taskInstUuid, String gotoTaskId, TaskData taskData) {
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.GOTO_TASK);
        TaskInstance taskInstance = this.getTask(taskInstUuid);
        Boolean isLog = (Boolean) taskData.get("isLog");
        if (isLog == null) {
            isLog = true;
        }
        Param param = new GotoTaskParam(taskInstance, taskData, null, isLog, gotoTaskId, true);
        taskExecutor.execute(param);

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        flowIndexDocumentService.indexWorkflowDocument(taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTodoUserIds(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getTodoUserIds(String taskInstUuid) {
        Set<String> todoUserIds = Sets.newLinkedHashSet();
        List<AclTaskEntry> aclSids = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
        for (AclTaskEntry aclSid : aclSids) {
            todoUserIds.add(aclSid.getSid());
        }
        return Arrays.asList(todoUserIds.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTodoUserNames(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getTodoUserNames(String taskInstUuid) {
        Set<String> todoUsernames = Sets.newLinkedHashSet();
        List<AclTaskEntry> aclTaskEntries = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
        for (AclTaskEntry taskEntry : aclTaskEntries) {
            todoUsernames.add(IdentityResolverStrategy.resolveAsName(taskEntry.getSid()));
        }
        return Arrays.asList(todoUsernames.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTodoSuperiorUserIds(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getTodoSuperiorUserIds(String taskInstUuid) {
        // 在办人员的上级领导
        Set<String> superiros = Sets.newHashSet();
        // 在办人员
        List<String> userIds = getTodoUserIds(taskInstUuid);
        if (CollectionUtils.isNotEmpty(userIds)) {
            for (String u : userIds) {
//                Set<String> ids = orgApiFacade.queryUserMainJobSuperiorLeaderList(u);
                Set<String> ids = workflowOrgService.listUserMainJobSuperiorLeader(u);
                if (CollectionUtils.isNotEmpty(ids)) {
                    superiros.addAll(ids);
                }
            }
        }
        return Arrays.asList(superiros.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getDoneUserIds(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getDoneUserIds(String taskInstUuid) {
        Set<String> doneUserIds = Sets.newLinkedHashSet();
        List<AclTaskEntry> aclTaskEntries = aclTaskService.getSid(taskInstUuid, AclPermission.DONE);
        for (AclTaskEntry aclTaskEntry : aclTaskEntries) {
            String sid = aclTaskEntry.getSid();
            if (sid.startsWith(IdPrefix.USER.getValue())) {
                doneUserIds.add(aclTaskEntry.getSid());
            }
        }
        return Arrays.asList(doneUserIds.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTraceUserIds(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getTraceUserIds(String taskInstUuid) {
        Set<String> userIds = Sets.newLinkedHashSet();
        TaskInstance taskInstance = getTask(taskInstUuid);
        // 流程发起人
        String userId = taskInstance.getFlowInstance().getStartUserId();
        if (StringUtils.isNotBlank(userId)) {
            userIds.add(userId);
        }

        // 前环节办理人
        TaskActivity taskActivity = new TaskActivity();
        taskActivity.setTaskInstUuid(taskInstance.getUuid());
        TaskActivityService taskActivityService = ApplicationContextHolder.getBean(TaskActivityService.class);
        List<TaskActivity> taskActivities = taskActivityService.findByExample(taskActivity);
        if (CollectionUtils.isNotEmpty(taskActivities)) {
            String preTaskInstUuid = taskActivities.get(0).getPreTaskInstUuid();
            if (StringUtils.isNotBlank(preTaskInstUuid)) {
                userIds.addAll(getTaskOwners(preTaskInstUuid));
            }
        }
        return Arrays.asList(userIds.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getSuperviseUserIds(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getSuperviseUserIds(String taskInstUuid) {
        return getSuperviseUserIds(taskInstUuid, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getSuperviseUserIds(java.lang.String, boolean)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getSuperviseUserIds(String taskInstUuid, boolean includeAdmin) {
        Set<String> userIds = Sets.newLinkedHashSet();
        List<AclTaskEntry> aclTaskEntries = aclTaskService.getSid(taskInstUuid, AclPermission.SUPERVISE);
        for (AclTaskEntry aclTaskEntry : aclTaskEntries) {
            userIds.add(aclTaskEntry.getSid());
        }
        if (includeAdmin) {
//            userIds.addAll(orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId()));
            userIds.addAll(workflowOrgService.listCurrentTenantAdminIds());
        }
        return Arrays.asList(userIds.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getMonitorUserIds(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getMonitorUserIds(String taskInstUuid) {
        return getMonitorUserIds(taskInstUuid, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getMonitorUserIds(java.lang.String, boolean)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getMonitorUserIds(String taskInstUuid, boolean includeAdmin) {
        Set<String> userIds = Sets.newLinkedHashSet();
        List<AclTaskEntry> aclSids = aclTaskService.getSid(taskInstUuid, AclPermission.MONITOR);
        for (AclTaskEntry aclSid : aclSids) {
            userIds.add(aclSid.getSid());
        }
        if (includeAdmin) {
            userIds.addAll(workflowOrgService.listCurrentTenantAdminIds());
        }
        return Arrays.asList(userIds.toArray(new String[0]));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getViewerUserIds(String taskInstUuid, boolean includeAdmin) {
        Set<String> userIds = Sets.newLinkedHashSet();
        List<AclTaskEntry> aclSids = aclTaskService.getSid(taskInstUuid, AclPermission.READ);
        for (AclTaskEntry aclSid : aclSids) {
            userIds.add(aclSid.getSid());
        }
        if (includeAdmin) {
            userIds.addAll(workflowOrgService.listCurrentTenantAdminIds());
        }
        return Arrays.asList(userIds.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTask(java.lang.String)
     */
    @Override
    public TaskInstance getTask(String taskInstUuid) {
        return taskInstanceService.get(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTaskType(java.lang.String)
     */
    @Override
    public Integer getTaskType(String taskInstUuid) {
        String hql = "select t.type from TaskInstance t where t.uuid = :taskInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.findUnique(hql, values);
    }

    /**
     * (non-Javadoc)
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#hasPermission(java.lang.String, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    // @Override
    // @Transactional(readOnly = true)
    // public boolean hasPermission(String taskInstUuid, Permission permission,
    // String userId) {
    // return aclServiceWrapper.hasPermission( taskInstUuid,
    // permission, userId);
    // }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getOtherParallelTasks(java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskInstance> getOtherParallelTasks(String taskInstUuid, String parallelTaskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        return taskInstanceService.find(GET_OTHER_PARALLEL_TASK, values);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(UserDetails userDetails, String taskInstUuid, Permission permission) {
        return flowPermissionEvaluator.hasPermission(userDetails, taskInstUuid, permission);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermissionCurrentUser(String taskInstUuid, Integer[] masks) {
        return flowPermissionEvaluator.hasPermission((UserDetails) SpringSecurityUtils.getCurrentUser(), taskInstUuid,
                masks);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#query(java.lang.String, java.util.Map)
     */
    @Override
    public <X> List<X> query(String hql, Map<String, Object> values) {
        return (List<X>) this.taskInstanceService.find(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAttention(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAttention(String userId, String taskInstUuid) {
        return aclTaskService.hasPermission(taskInstUuid, AclPermission.ATTENTION, userId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedCopyTo(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedCopyTo(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = taskData.getTodoRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.CopyTo.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.CopyTo.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedAttention(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedAttention(String userId, String taskInstUuid) {
        if (isAttention(userId, taskInstUuid)) {
            return false;
        }
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = Lists.newArrayList();
        rights.addAll(taskData.getTodoRights());
        rights.addAll(taskData.getDoneRights());
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.Attention.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.Attention.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedUnfollow(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedUnfollow(String userId, String taskInstUuid) {
        if (!isAttention(userId, taskInstUuid)) {
            return false;
        }
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = Lists.newArrayList();
        rights.addAll(taskData.getTodoRights());
        rights.addAll(taskData.getDoneRights());
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.Unfollow.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.Unfollow.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedRemind(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedRemind(String userId, String taskInstUuid) {
        if (!(hasSupervisePermission(userId, taskInstUuid) || hasMonitorPermission(userId, taskInstUuid))) {
            return false;
        }
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = Lists.newArrayList();
        rights.addAll(taskData.getMonitorRights());
        rights.addAll(taskData.getAdminRights());
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.Remind.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.Remind.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedSuspend(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedSuspend(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null || taskInstance.getSuspensionState() == 1) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = Lists.newArrayList();
        rights.addAll(taskData.getTodoRights());
        rights.addAll(taskData.getDoneRights());
        rights.addAll(taskData.getMonitorRights());
        rights.addAll(taskData.getAdminRights());
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.Suspend.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.Suspend.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedResume(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedResume(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null || taskInstance.getSuspensionState() == 0) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = Lists.newArrayList();
        rights.addAll(taskData.getTodoRights());
        rights.addAll(taskData.getDoneRights());
        rights.addAll(taskData.getMonitorRights());
        rights.addAll(taskData.getAdminRights());
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.Resume.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.Resume.getCode())) {
//            return true;
//        }
        return true;
    }

    @Override
    public boolean isAllowedDelete(String userId, String taskInstUuid) {
        // 任务已经结束不允许删除
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        try {
            Date date = DateUtils.parseDate("2015-06-01");
            if (taskInstance.getStartTime().before(date)) {
                FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
                if (flowDelegate.isFirstTaskNode(taskInstance.getId())) {
                    return true;
                }
            }
        } catch (ParseException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        if (taskInstance == null || taskInstance.getEndTime() != null) {
            return false;
        }
        // 子任务不允许删除
        if (taskInstance.getParent() != null) {
            return false;
        }
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 生成任务活动堆栈
        List<TaskActivityQueryItem> allTaskActivities = taskActivityService.getAllActivityByTaskInstUuid(taskInstUuid);
        TaskActivityStack stack = TaskActivityStackFactary.build(taskInstUuid, allTaskActivities);
        // 如果处理第一环节且有待办权限可进行删除
        if (stack.size() == 1
                && aclTaskService.hasPermission(taskInstUuid, AclPermission.TODO, userId)) {
            List<AclTaskEntry> aclSids = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
            // 多人办理不允许删除
            if (aclSids.size() > 1) {
                return false;
            }
            return true;
        } else if ((stack.size() == 1 || user instanceof IgnoreLoginUserDetails) && user.isAdmin()) {
            return true;
        } else if (!stack.isEmpty()) {
            // 退回、撤回、跳转到第一环节
            TaskActivityItem item = stack.pop();
            if (TransferCode.RollBack.getCode().equals(item.getTransferCode())
                    || TransferCode.Cancel.getCode().equals(item.getTransferCode())
                    || TransferCode.GotoTask.getCode().equals(item.getTransferCode())) {
                String taskId = item.getTaskId();
                FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
                if (flowDelegate.isFirstTaskNode(taskId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedHandOver(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAllowedHandOver(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null || taskInstance.getEndTime() != null) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = taskData.getAdminRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.HandOver.getCode())) {
            return false;
        }
        if (!hasMonitorPermission(userId, taskInstUuid)) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.HandOver.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isRequiredHandOverOpinion(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isRequiredHandOverOpinion(String userId, String taskInstUuid) {
        TaskData taskData = getConfigInfo(taskInstUuid);
        List<String> rights = taskData.getMonitorRights();
        rights.addAll(taskData.getAdminRights());
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.RequiredHandOverOpinion.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.RequiredHandOverOpinion.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedGotoTask(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAllowedGotoTask(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null) {
            return false;
        }
        // 办结流程可跳转
        if (taskInstance.getEndTime() != null) {
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            if (flowInstance.getEndTime() == null
                    || !StringUtils.equals(taskInstUuid, getLastTaskInstanceUuidByFlowInstUuid(flowInstance.getUuid()))) {
                return false;
            }
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = taskData.getAdminRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.GotoTask.getCode())) {
            return false;
        }
        if (!hasMonitorPermission(userId, taskInstUuid)) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.GotoTask.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isRequiredGotoTaskOpinion(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isRequiredGotoTaskOpinion(String userId, String taskInstUuid) {
        TaskData taskData = getConfigInfo(taskInstUuid);
        List<String> rights = taskData.getMonitorRights();
        rights.addAll(taskData.getAdminRights());
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.RequiredGotoTaskOpinion.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.RequiredGotoTaskOpinion.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedRollbackToTask(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAllowedRollbackToTask(String userId, String taskInstUuid) {
        // 生成任务活动堆栈
        List<TaskActivityQueryItem> allTaskActivities = taskActivityService.getAllActivityByTaskInstUuid(taskInstUuid);
        TaskActivityStack stack = TaskActivityStackFactary.build(taskInstUuid, allTaskActivities);
        if (!stack.isEmpty()) {
            TaskActivityItem item = stack.pop();
            String preTaskInstUuid = item.getPreTaskInstUuid();
            String parallelTaskInstUuid = item.getParallelTaskInstUuid();
            if (StringUtils.isNotBlank(parallelTaskInstUuid)) {
                // 动态分支的第一个环节不可退回
                boolean isDynamicBranchTask = taskBranchService.isDynamicBranchTask(parallelTaskInstUuid);
                if (StringUtils.equals(preTaskInstUuid, parallelTaskInstUuid)) {
                    if (isDynamicBranchTask) {
                        return false;
                    }
                } else {
                    // 非并行任务退回
                    List<TaskActivityQueryItem> activityItems = taskActivityService
                            .getAllActivityByTaskInstUuid(taskInstUuid);
                    TaskActivityStack branchStack = TaskActivityStackFactary.build(taskInstUuid, activityItems);
                    List<TaskActivityItem> branchItems = branchStack.getAvailableToRollbackTaskActivityItems();
                    List<TaskActivityItem> deletedItems = new ArrayList<TaskActivityItem>();
                    TaskInstance taskInstance = getTask(taskInstUuid);
                    FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
                    for (TaskActivityItem branchItem : branchItems) {
                        // 动态分支只能退回到动态分支内办理的结点
                        if (isDynamicBranchTask && !Boolean.TRUE.equals(branchItem.getIsParallel())) {
                            deletedItems.add(branchItem);
                        } else if (Boolean.TRUE.equals(branchItem.getIsParallel())
                                && !parallelTaskInstUuid.equals(branchItem.getParallelTaskInstUuid())) {
                            deletedItems.add(branchItem);
                        }

                        Node node = flowDelegate.getTaskNode(item.getTaskId());
                        // 去掉子环节、重复的环节
                        if (node instanceof SubTaskNode || node.getId().equals(taskInstance.getId())) {
                            continue;
                        }
                    }
                    branchItems.removeAll(deletedItems);
                    if (CollectionUtils.isEmpty(branchItems)) {
                        return false;
                    }
                }
            }

            if (stack.size() == 0) {
                return StringUtils.equals(item.getCreator(), userId);
            }
            return stack.size() > 0;
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isRequiredRollbackOpinion(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isRequiredRollbackOpinion(String userId, String taskInstUuid) {
        TaskData taskData = getConfigInfo(taskInstUuid);
        List<String> rights = taskData.getTodoRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.RequiredRollbackOpinion.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.RequiredRollbackOpinion.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedDirectRollbackToTask(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedDirectRollbackToTask(String userId, String taskInstUuid) {
        // 生成任务活动堆栈
        List<TaskActivityQueryItem> allTaskActivities = taskActivityService.getAllActivityByTaskInstUuid(taskInstUuid);
        TaskActivityStack stack = TaskActivityStackFactary.build(taskInstUuid, allTaskActivities);
        if (stack.isEmpty()) {
            return false;
        }

        TaskInstance taskInstance = this.get(taskInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        // 退回处理中，流程定义的第一个任务，不支持退回
        if (flowDelegate.isFirstTaskNode(taskInstance.getId())) {
            return false;
        }

        // 权限判断
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = taskData.getTodoRights();
        if (CollectionUtils.isEmpty(rights)) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.DirectRollback.getCode())) {
            return false;
        }
//        if (!securityApiFacade.isGranted(WorkFlowPrivilege.DirectRollback.getCode())) {
//            return false;
//        }

        TaskActivityItem top = stack.pop();
        if (!stack.isEmpty()) {
            TaskActivityItem second = stack.pop();
            if (flowDelegate.isNotRollback(second.getTaskId())) {
                return false;
            }
            if (Boolean.FALSE.equals(top.getIsParallel()) && Boolean.TRUE.equals(second.getIsParallel())) {
                return false;
            }
            // 直接退回的环节已不存在
            if (!flowDelegate.existsTaskNode(second.getTaskId())) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedRollbackToMainFlow(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedRollbackToMainFlow(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        // 判断流程
        if (flowInstance.getParent() == null || flowInstance.getParent().getEndTime() != null) {
            return false;
        }
        // 子流程分发中不允许退回主流程
        long dispatchCount = taskSubFlowDispatchService
                .countDispatchingByParentFlowInstUuid(flowInstance.getParent().getUuid());
        if (dispatchCount > 0) {
            return false;
        }
        // 判断环节
        List<TaskSubFlow> taskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstance.getUuid());
        for (TaskSubFlow taskSubFlow : taskSubFlows) {
            String parentTaskInstUuid = taskSubFlow.getParentTaskInstUuid();
            TaskInstance parentTaskInstance = this.taskInstanceService.get(parentTaskInstUuid);
            if (parentTaskInstance.getEndTime() != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedSubmit(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAllowedSubmit(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null || taskInstance.getEndTime() != null) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = taskData.getTodoRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.Submit.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.Submit.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isRequiredSignOpinion(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isRequiredSignOpinion(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        TaskData taskData = getTaskData(taskInstance);
        // 第一个环节不需要必填意见
        if (taskData.getIsFirstTaskNode(taskInstance.getId())) {
            return false;
        }
        List<String> rights = taskData.getTodoRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.RequiredSignOpinion.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.RequiredSignOpinion.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedCancel(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedCancel(String userId, String taskInstUuid) {
        // if (aclServiceWrapper.hasPermission( taskInstUuid,
        // AclPermission.TODO, userId)) {
        // return false;
        // }
        // 生成任务活动堆栈
        // List<TaskActivity> allTaskActivities =
        // taskActivityService.getAllActivityByTaskInstUuid(taskInstUuid);
        List<TaskActivityQueryItem> allTaskActivities = taskActivityService.getAllActivityByTaskInstUuid(taskInstUuid);
        // 生成任务活动操作堆栈
        List<TaskOperation> allTaskOperations = taskOperationService.getAllTaskOperationByTaskInstUuid(taskInstUuid);
        TaskActivityStack stack = TaskActivityStackFactary.build(taskInstUuid, allTaskActivities, allTaskOperations);
        if (stack.isEmpty()) {
            return false;
        }
        TaskActivityItem topItem = stack.pop();

        // 是否允许撤回办结判断
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        // 判断已办环节在流程定义中是否存在
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        // 本环节不可被撤回
        if (flowDelegate.isNotCancel(taskInstance.getId())) {
            return false;
        }

        if (taskInstance.getEndTime() != null && taskInstance.getFlowInstance().getEndTime() != null) {
            WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
            if (!workFlowSettings.isAllowCancelOver()) {
                return false;
            }
            return isAllowCancelOverForTaskOperation(userId, topItem);
        }

        // 办结撤回不允许再次撤回
        if ("Cancel".equals(taskInstance.getActionType()) && CollectionUtils.isEmpty(topItem.getOperationItems())) {
            return false;
        }

        boolean existsTaskNode = flowDelegate.existsTaskNode(topItem.getPreTaskId());
        // 判断子流程环节是否允许撤回，若存在子流程已办结，则不允需撤回
        if (Integer.valueOf(2).equals(taskInstance.getType()) && !BooleanUtils.isTrue(taskInstance.getIsParallel())) {
            return existsTaskNode && isAllowCancelForTaskSubFlow(taskInstance, taskInstance.getFlowInstance());
        }

        // 前一环节为子流程环节，不可撤回
        if (isSubTaskInstance(topItem.getPreTaskInstUuid())) {
            return false;
        }

        // 环节流转代码
        Integer transferCode = topItem.getTransferCode();
        // 1、提交流转->撤回
        if (TransferCode.Submit.getCode().equals(transferCode)) {
            Boolean isPallel = topItem.getIsParallel();
            // 1、正常提交、退回到下一个环节后撤回
            if (Boolean.FALSE.equals(isPallel)) {
                if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                    return existsTaskNode && true;
                } else if (!topItem.getOperationItems().isEmpty()) {
                    // 环节操作撤回
                    return isAllowCancelForTaskOperation(userId, topItem);
                } else if (!stack.isEmpty()) {
                    // 并行流程撤回操作
                    TaskActivityItem secondItem = stack.pop();
                    if (secondItem.getIsParallel()
                            && isAllowedCancelForOverParallelTask(userId, allTaskActivities, allTaskOperations)) {
                        return true;
                    }
                }
            } else {
                // 并行流程撤回操作
                String parallelTaskInstUuid = topItem.getParallelTaskInstUuid();
                String preTaskInstUuid = topItem.getPreTaskInstUuid();
                // 1、并行分支有一个操作，下一个不可撤回
                if (preTaskInstUuid.equals(parallelTaskInstUuid) && isExistsFirstParallelTaskTaskOperation(userId,
                        parallelTaskInstUuid, allTaskActivities, allTaskOperations)) {
                    return false;
                } else if (preTaskInstUuid.equals(parallelTaskInstUuid) && userId.equals(topItem.getCreator())) {
                    // 2、并行分发提交撤回
                    return existsTaskNode && true;
                } else if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                    // 3、并行分支提交后进入下一环节撤回
                    return existsTaskNode && true;
                } else if (isAllowedCancelForOverParallelTask(userId, allTaskActivities, allTaskOperations)) {
                    // 4、并行分支提交结束后撤回
                    return existsTaskNode && true;
                } else if (!topItem.getOperationItems().isEmpty() && isAllowCancelForTaskOperation(userId, topItem)) {
                    // 5、并行分支环节操作撤回
                    return existsTaskNode && true;
                }
            }
        } else if (TransferCode.RollBack.getCode().equals(transferCode)) {
            // 2、退回流转->撤回
            if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                // 不允许撤回到子流程
                TaskInstance preTaskInstance = this.taskInstanceService.get(topItem.getPreTaskInstUuid());
                if (Integer.valueOf(2).equals(preTaskInstance.getType())) {
                    return false;
                }
                return existsTaskNode && true;
            } else if (!topItem.getOperationItems().isEmpty()) {
                // 环节操作撤回
                return isAllowCancelForTaskOperation(userId, topItem);
            }
        } else if (TransferCode.Cancel.getCode().equals(transferCode)) {
            // 3、撤回流转->撤回
            if (!topItem.getOperationItems().isEmpty()) {
                // 环节操作撤回
                return isAllowCancelForTaskOperation(userId, topItem);
            }
        } else if (TransferCode.GotoTask.getCode().equals(transferCode)) {
            // 4、移交环节流转
        } else if (TransferCode.SkipTask.getCode().equals(transferCode)) {
            // 5、办理人为空自动跳过
            return existsTaskNode && true;
        } else if (TransferCode.TransferSubmit.getCode().equals(transferCode)) {
            // 6、转办提交流转->撤回
            if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                return existsTaskNode && true;
            } else if (!topItem.getOperationItems().isEmpty()) {
                // 环节操作撤回
                return isAllowCancelForTaskOperation(userId, topItem);
            }
        } else if (TransferCode.DelegationSubmit.getCode().equals(transferCode)) {
            Boolean isPallel = topItem.getIsParallel();
            // 7、委托提交流转->撤回
            if (Boolean.FALSE.equals(isPallel)) {
                if (isAllowCancelDelegationSubmit(topItem, userId)) {
                    return existsTaskNode && true;
                } else if (!topItem.getOperationItems().isEmpty()) {
                    // 环节操作撤回
                    return isAllowCancelForTaskOperation(userId, topItem);
                }
            } else {
                // 并行流程撤回操作
                String parallelTaskInstUuid = topItem.getParallelTaskInstUuid();
                String preTaskInstUuid = topItem.getPreTaskInstUuid();
                // 1、并行分发提交撤回
                if (preTaskInstUuid.equals(parallelTaskInstUuid) && userId.equals(topItem.getCreator())) {
                    return existsTaskNode && true;
                } else if (topItem.getOperationItems().isEmpty() && userId.equals(topItem.getCreator())) {
                    // 2、并行分支提交后进入下一环节撤回
                    return existsTaskNode && true;
                } else if (isAllowedCancelForOverParallelTask(userId, allTaskActivities, allTaskOperations)) {
                    // 3、并行分支提交结束后撤回
                    return true;
                } else if (!topItem.getOperationItems().isEmpty() && isAllowCancelForTaskOperation(userId, topItem)) {
                    // 4、并行分支环节操作撤回
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isRequiredCancelOpinion(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isRequiredCancelOpinion(String userId, String taskInstUuid) {
        TaskData taskData = getConfigInfo(taskInstUuid);
        List<String> rights = taskData.getDoneRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.RequiredCancelOpinion.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.RequiredCancelOpinion.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * @param taskInstUuid
     * @return
     */
    private boolean isSubTaskInstance(String taskInstUuid) {
        if (StringUtils.isBlank(taskInstUuid)) {
            return false;
        }
        return Integer.valueOf(2).equals(getTaskType(taskInstUuid));
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
     * 判断子流程环节是否允许撤回，若存在子流程已办结，则不允需撤回
     *
     * @param taskInstance
     * @param flowInstance
     * @return
     */
    private boolean isAllowCancelForTaskSubFlow(TaskInstance taskInstance, FlowInstance flowInstance) {
        String parentTaskInstUuid = taskInstance.getUuid();
        String parentFlowInstUuid = flowInstance.getUuid();
        // 子流程分发中不允许撤回
        long dispatchCount = taskSubFlowDispatchService.countDispatchingByParentTaskInstUuid(parentTaskInstUuid);
        if (dispatchCount > 0) {
            return false;
        }
        TaskSubFlow example = new TaskSubFlow();
        example.setParentTaskInstUuid(parentTaskInstUuid);
        example.setParentFlowInstUuid(parentFlowInstUuid);
        example.setCompleted(true);
        return !(this.dao.countByExample(example) > 0);
    }

    /**
     * @param userId
     * @param allTaskActivities
     * @param allTaskOperations
     * @return
     */
    private boolean isExistsFirstParallelTaskTaskOperation(String userId, String parallelTaskInstUuid,
                                                           List<TaskActivityQueryItem> allTaskActivities, List<TaskOperation> allTaskOperations) {
        List<String> parallelTaskInstUuids = new ArrayList<String>();
        for (TaskActivityQueryItem taskActivityQueryItem : allTaskActivities) {
            if (Boolean.TRUE.equals(taskActivityQueryItem.getIsParallel())
                    && StringUtils.equals(parallelTaskInstUuid, taskActivityQueryItem.getPreTaskInstUuid())) {
                // 子流程环节已办结的忽略掉
                TaskInstance taskInstance = getTask(taskActivityQueryItem.getTaskInstUuid());
                if (Integer.valueOf(2).equals(taskInstance.getType()) && taskInstance.getEndTime() != null) {
                    continue;
                }
                parallelTaskInstUuids.add(taskActivityQueryItem.getTaskInstUuid());
            }
        }
        for (TaskOperation taskOperation : allTaskOperations) {
            if (parallelTaskInstUuids.contains(taskOperation.getTaskInstUuid())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param userId
     * @param allTaskActivities
     * @param allTaskOperations
     */
    private boolean isAllowedCancelForOverParallelTask(String userId, List<TaskActivityQueryItem> allTaskActivities,
                                                       List<TaskOperation> allTaskOperations) {
        List<String> parallelTaskInstUuids = new ArrayList<String>();
        for (TaskActivityQueryItem taskActivityQueryItem : allTaskActivities) {
            if (Boolean.TRUE.equals(taskActivityQueryItem.getIsParallel()) && taskActivityQueryItem.getEndTime() != null
                    && userId.equals(taskActivityQueryItem.getModifier())) {
                parallelTaskInstUuids.add(taskActivityQueryItem.getTaskInstUuid());
            }
        }
        TaskOperation lastSubmitTaskOperation = null;
        Collections.sort(allTaskOperations, new Comparator<TaskOperation>() {

            @Override
            public int compare(TaskOperation o1, TaskOperation o2) {
                return o1.getCreateTime().before(o2.getCreateTime()) ? 1 : -1;
            }
        });
        for (TaskOperation taskOperation : allTaskOperations) {
            if (WorkFlowOperation.isActionCodeOfSubmit(taskOperation.getActionCode())) {
                if (StringUtils.equals(userId, taskOperation.getAssignee())
                        && parallelTaskInstUuids.contains(taskOperation.getTaskInstUuid())) {
                    lastSubmitTaskOperation = taskOperation;
                }
                break;
            }
        }
        if (lastSubmitTaskOperation != null) {
            return true;
        }

        return false;
    }

    /**
     * @param userId
     * @param topItem
     * @return
     */
    private boolean isAllowCancelOverForTaskOperation(String userId, TaskActivityItem topItem) {
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
                    return true;
                } else {
                    return false;
                }
            }
        }
        return CollectionUtils.isEmpty(items) && TransferCode.SkipTask.getCode().equals(topItem.getTransferCode());
    }

    boolean isAllowCancelForTaskOperation(String userId, TaskActivityItem topItem) {
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
                    return true;
                }
                break;
            case 2:
                // 会签提交
                if (identityService.isAllowedRestoreTodoCounterSignSubmit(historyTaskIdentity)) {
                    return true;
                }
                break;
            case 3:
                // 转办提交
                if (identityService.isAllowedRestoreTodoTransferSubmit(historyTaskIdentity) || CollectionUtils
                        .isNotEmpty(identityService.getTodoByTaskInstUuid(historyTaskIdentity.getTaskInstUuid()))) {
                    return true;
                }
                break;
            case 7:
                // 转办
                if (identityService.isAllowedRestoreTransfer(historyTaskIdentity)) {
                    return true;
                }
                break;
            case 8:
                // 会签
                if (identityService.isAllowedRestoreCounterSign(historyTaskIdentity)) {
                    return true;
                }
                break;
            case 27:
                // 委托提交
                if (identityService.isAllowedRestoreTodoDelegationSubmit(historyTaskIdentity)) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getTaskOwners(java.lang.String)
     */
    @Override
    public Set<String> getTaskOwners(String taskInstUuid) {
        Set<String> ids = new LinkedHashSet<String>();
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        String owner = taskInstance.getOwner();
        if (StringUtils.isNotBlank(owner)) {
            ids.addAll(Arrays.asList(StringUtils.split(owner, Separator.SEMICOLON.getValue())));
        }
        return ids;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public List<TaskInstance> getOtherUnfinishedParallelTasks(String taskInstUuid, String parallelTaskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        return this.taskInstanceService.listByHQL(GET_OTHER_UNFINISHED_PARALLEL_TASK, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getLastTaskInstanceByFlowInstUuid(java.lang.String)
     */
    @Override
    public TaskInstance getLastTaskInstanceByFlowInstUuid(String flowInstUuid) {
        String hql = "from TaskInstance t where t.flowInstance.uuid = :flowInstUuid order by t.createTime desc";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        List<TaskInstance> taskInstances = this.taskInstanceService.find(hql, values);
        if (!taskInstances.isEmpty()) {
            return taskInstances.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedTransfer(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAllowedTransfer(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null || taskInstance.getEndTime() != null) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = taskData.getTodoRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.Transfer.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.Transfer.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isRequiredTransferOpinion(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isRequiredTransferOpinion(String userId, String taskInstUuid) {
        TaskData taskData = getConfigInfo(taskInstUuid);
        List<String> rights = taskData.getTodoRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.RequiredTransferOpinion.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.RequiredTransferOpinion.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isAllowedCounterSign(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAllowedCounterSign(String userId, String taskInstUuid) {
        TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
        if (taskInstance == null || taskInstance.getEndTime() != null) {
            return false;
        }
        TaskData taskData = getTaskData(taskInstance);
        List<String> rights = taskData.getTodoRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.CounterSign.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.CounterSign.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#isRequiredCounterSignOpinion(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isRequiredCounterSignOpinion(String userId, String taskInstUuid) {
        TaskData taskData = getConfigInfo(taskInstUuid);
        List<String> rights = taskData.getTodoRights();
        if (rights.isEmpty()) {
            return false;
        }
        if (!rights.contains(WorkFlowPrivilege.RequiredCounterSignOpinion.getCode())) {
            return false;
        }
//        if (securityApiFacade.isGranted(WorkFlowPrivilege.RequiredCounterSignOpinion.getCode())) {
//            return true;
//        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#addTodoPermission(java.lang.String, java.lang.String)
     */
    @Override
    public void addTodoPermission(String sid, String taskInstUuid) {
        aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, sid);
    }

    @Override
    public void addTodoPermission(Set<String> sids, String taskInstUuid) {
        aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, sids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#removeTodoPermission(java.lang.String, java.lang.String)
     */
    @Override
    public void removeTodoPermission(String sid, String taskInstUuid) {
        aclTaskService.removePermission(taskInstUuid, AclPermission.TODO, sid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#addDonePermission(java.lang.String, java.lang.String)
     */
    @Override
    public void addDonePermission(String userId, String taskInstUuid) {
        if (!aclTaskService.hasPermission(taskInstUuid, AclPermission.DONE, userId)) {
            aclTaskService.addPermission(taskInstUuid, AclPermission.DONE, userId);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#removeDonePermission(java.lang.String, java.lang.String)
     */
    @Override
    public void removeDonePermission(String sid, String taskInstUuid) {
        aclTaskService.removePermission(taskInstUuid, AclPermission.DONE, sid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#addUnreadPermission(java.lang.String, java.lang.String)
     */
    @Override
    public void addUnreadPermission(String userId, String taskInstUuid) {
        if (!aclTaskService.hasPermission(taskInstUuid, AclPermission.UNREAD, userId)) {
            aclTaskService.addPermission(taskInstUuid, AclPermission.UNREAD, userId);
        }
    }

    @Override
    public void addUnreadPermission(Set<String> userIds, String taskInstUuid) {
        aclTaskService.addPermission(taskInstUuid, AclPermission.UNREAD, userIds);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#addSupervisePermission(java.lang.String, java.lang.String)
     */
    @Override
    public void addSupervisePermission(String sid, String taskInstUuid) {
        if (!aclTaskService.hasPermission(taskInstUuid, AclPermission.SUPERVISE, sid)) {
            aclTaskService.addPermission(taskInstUuid, AclPermission.SUPERVISE, sid);
        }
    }

    @Override
    public void addSupervisePermission(Set<String> sids, String taskInstUuid) {
        aclTaskService.addPermission(taskInstUuid, AclPermission.SUPERVISE, sids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#addMonitorPermission(java.lang.String, java.lang.String)
     */
    @Override
    public void addMonitorPermission(String sid, String taskInstUuid) {
        if (!aclTaskService.hasPermission(taskInstUuid, AclPermission.MONITOR, sid)) {
            aclTaskService.addPermission(taskInstUuid, AclPermission.MONITOR, sid);
        }
    }

    @Override
    public void addMonitorPermission(Set<String> sids, String taskInstUuid) {
        aclTaskService.addPermission(taskInstUuid, AclPermission.MONITOR, sids);
    }

    @Override
    public void addViewerPermission(Set<String> sids, String taskInstUuid) {
        aclTaskService.addPermission(taskInstUuid, AclPermission.READ, sids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#removeSupervisePermission(java.lang.String, java.lang.String)
     */
    @Override
    public void removeSupervisePermission(String sid, String taskInstUuid) {
        aclTaskService.removePermission(taskInstUuid, AclPermission.SUPERVISE, sid);
    }

    @Override
    public void addConsultPermission(String sid, String taskInstUuid) {
        if (!aclTaskService.hasPermission(taskInstUuid, AclPermission.CONSULT, sid)) {
            aclTaskService.addPermission(taskInstUuid, AclPermission.CONSULT, sid);
        }
    }

    @Override
    public void removeConsultPermission(String sid, String taskInstUuid) {
        aclTaskService.removePermission(taskInstUuid, AclPermission.CONSULT, sid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#hasTodoPermission(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasTodoPermission(String sid, String taskInstUuid) {
        return aclTaskService.hasPermission(taskInstUuid, AclPermission.TODO, sid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#hasTodoPermission(com.wellsoft.pt.security.core.userdetails.UserDetails, java.lang.String)
     */
    @Override
    public boolean hasTodoPermission(UserDetails user, String taskInstUuid) {
        return flowPermissionEvaluator.hasPermission(user, taskInstUuid, AclPermission.TODO);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#hasDonePermission(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasDonePermission(String userId, String taskInstUuid) {
        return aclTaskService.hasPermission(taskInstUuid, AclPermission.DONE, userId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#hasMonitorPermission(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasMonitorPermission(String userId, String taskInstUuid) {
        // 监控权限检查
        if (aclTaskService.hasPermission(taskInstUuid, AclPermission.MONITOR, userId)) {
            return true;
        }

        // 流程管理监控权限检查
        if (flowManagementService.hasPermission(userId, taskInstUuid, ManagementType.MONITOR)) {
            return true;
        }

        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#hasMonitorPermission(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasMonitorPermission(UserDetails user, String taskInstUuid) {
        FlowPermissionEvaluatorContext.setEnableContextHolder(true);
        // 监控权限检查
        if (flowPermissionEvaluator.hasPermission(user, taskInstUuid, AclPermission.MONITOR)) {
            FlowPermissionEvaluatorContext.reset();
            return true;
        }
        if (FlowPermissionEvaluatorContext.getOnlyUseAccessPermissionProvider()) {
            FlowPermissionEvaluatorContext.reset();
            return false;
        }
        FlowPermissionEvaluatorContext.reset();

        // 流程管理监控权限检查
        if (flowManagementService.hasPermission(user.getUserId(), taskInstUuid, ManagementType.MONITOR)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean hasMonitorPermissionCurrentUser(String taskInstUuid) {
        return this.hasMonitorPermission((UserDetails) SpringSecurityUtils.getCurrentUser(), taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#hasSupervisePermission(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasSupervisePermission(String userId, String taskInstUuid) {
        // 监控权限检查
        if (aclTaskService.hasPermission(taskInstUuid, AclPermission.SUPERVISE, userId)) {
            return true;
        }

        // 流程管理监控权限检查
        if (flowManagementService.hasPermission(userId, taskInstUuid, ManagementType.SUPERVISE)) {
            return true;
        }

        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#hasSupervisePermission(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasSupervisePermission(UserDetails user, String taskInstUuid) {
        FlowPermissionEvaluatorContext.setEnableContextHolder(true);
        // 监控权限检查
        if (flowPermissionEvaluator.hasPermission(user, taskInstUuid, AclPermission.SUPERVISE)) {
            FlowPermissionEvaluatorContext.reset();
            return true;
        }
        if (FlowPermissionEvaluatorContext.getOnlyUseAccessPermissionProvider()) {
            FlowPermissionEvaluatorContext.reset();
            return false;
        }
        FlowPermissionEvaluatorContext.reset();
        // 流程管理监控权限检查
        if (flowManagementService.hasPermission(user.getUserId(), taskInstUuid, ManagementType.SUPERVISE)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean hasSupervisePermissionCurrentUser(String taskInstUuid) {
        return this.hasSupervisePermission((UserDetails) SpringSecurityUtils.getCurrentUser(), taskInstUuid);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasViewPermission(UserDetails user, String taskInstUuid) {
        TaskInstance taskInstance = new TaskInstance();
        taskInstance.setUuid(taskInstUuid);
        FlowPermissionEvaluatorContext.setEnableContextHolder(true);
        if (flowPermissionEvaluator.hasAnyPermission(user, taskInstUuid)) {
            FlowPermissionEvaluatorContext.reset();
            return true;
        }
        if (FlowPermissionEvaluatorContext.getOnlyUseAccessPermissionProvider()) {
            FlowPermissionEvaluatorContext.reset();
            return false;
        }
        FlowPermissionEvaluatorContext.reset();
        // 流程管理监控权限检查
        if (flowManagementService.hasPermission(user.getUserId(), taskInstUuid)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean hasViewPermissionCurrentUser(String taskInstUuid) {
        return this.hasViewPermission((UserDetails) SpringSecurityUtils.getCurrentUser(), taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#copyPermissions(java.lang.String, java.lang.String, org.springframework.security.acls.model.Permission, org.springframework.security.acls.model.Permission)
     */
    @Override
    public void copyPermissions(String sourceTaskInstUuid, String targetTaskInstUuid, Permission requiredPermission,
                                Permission ignorePermission) {
        TaskInstance entity = new TaskInstance();
        entity.setUuid(sourceTaskInstUuid);
        List<AclTaskEntry> aclSids = aclTaskService.getSid(entity.getUuid());
        for (AclTaskEntry aclSid : aclSids) {
            String sid = aclSid.getSid();
            TaskInstance e = new TaskInstance();
            e.setUuid(sourceTaskInstUuid);
            List<Permission> permissions = aclTaskService.getPermission(e.getUuid(), sid);
            if (permissions.isEmpty() && requiredPermission != null) {
                permissions.add(requiredPermission);
            }
            for (Permission permission : permissions) {
                if (ignorePermission != null && permission.getMask() == ignorePermission.getMask()) {
                    continue;
                }
                if (!aclTaskService.hasPermission(targetTaskInstUuid, permission, sid)) {
                    aclTaskService.addPermission(targetTaskInstUuid, permission, sid);
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#topping(java.util.Collection)
     */
    @Override
    public void topping(Collection<String> col) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        for (String uuid : col) {
            if (taskInstanceToppingService.countByFlowInstUuid(uuid, userId) == 0) {
                TaskInstanceTopping po = new TaskInstanceTopping();
                po.setUserId(userId);
                po.setTaskInstUuid(uuid);
                po.setIsTopping(1);
                taskInstanceToppingService.save(po);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#untopping(java.util.Collection)
     */
    @Override
    public void untopping(Collection<String> col) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        for (String uuid : col) {
            taskInstanceToppingService.deleteByFlowInstUuid(uuid, userId);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#getFlowInstUUidByTaskInstUuid(java.lang.String)
     */
    @Override
    public String getFlowInstUUidByTaskInstUuid(String taskInstUuid) {
        return taskInstanceService.getFlowInstUUidByTaskInstUuid(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#addCurrentUserLock(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public void addCurrentUserLock(String taskInstUuid) {
        Cache cache = cacheManager.getCache(TASK_LOCK_CACHE_ID);
        if (cache == null) {
            return;
        }
        String cacheKey = getTaskLockCacheKey(taskInstUuid);
        Set<TaskLockInfo> lockInfos = null;
        ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            lockInfos = (Set<TaskLockInfo>) valueWrapper.get();
        }
        if (lockInfos == null) {
            lockInfos = Sets.newHashSet();
        }
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        TaskLockInfo lockInfo = new TaskLockInfo(taskInstUuid, user);
        lockInfos.add(lockInfo);
        cache.put(cacheKey, lockInfos);
    }

    /**
     * @param taskInstUuid
     * @return
     */
    private String getTaskLockCacheKey(String taskInstUuid) {
        return "taskLock_" + taskInstUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#removeCurrentUserLock(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public void removeCurrentUserLock(String taskInstUuid) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        removeUserLock(user, taskInstUuid);
    }

    /**
     * @param user
     * @param taskInstUuid
     */
    @SuppressWarnings("unchecked")
    private void removeUserLock(UserDetails user, String taskInstUuid) {
        Cache cache = cacheManager.getCache(TASK_LOCK_CACHE_ID);
        if (cache == null) {
            return;
        }
        String cacheKey = getTaskLockCacheKey(taskInstUuid);
        Set<TaskLockInfo> lockInfos = null;
        ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            lockInfos = (Set<TaskLockInfo>) valueWrapper.get();
        }
        if (lockInfos == null) {
            return;
        }
        TaskLockInfo lockInfo = new TaskLockInfo(taskInstUuid, user);
        lockInfos.remove(lockInfo);
        if (CollectionUtils.isEmpty(lockInfos)) {
            cache.evict(cacheKey);
        } else {
            cache.put(cacheKey, lockInfos);
        }
    }

    /**
     * (non-Javadoc)
     */
    @Override
    @Transactional(readOnly = true)
    public void removeAllUserLock(UserDetails userDetails) {
        Cache cache = cacheManager.getCache(TASK_LOCK_CACHE_ID);
        if (cache == null) {
            return;
        }
        List<String> taskInstUuids = identityService.listTodoSubmitTaskInstUuidByUserId(userDetails.getUserId());
        for (String taskInstUuid : taskInstUuids) {
            removeUserLock(userDetails, taskInstUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#listTaskLock(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<TaskLockInfo> listTaskLock(String taskInstUuid) {
        Cache cache = cacheManager.getCache(TASK_LOCK_CACHE_ID);
        if (cache == null) {
            return Collections.emptyList();
        }
        String cacheKey = getTaskLockCacheKey(taskInstUuid);
        Set<TaskLockInfo> lockInfos = null;
        ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            lockInfos = (Set<TaskLockInfo>) valueWrapper.get();
        }
        if (lockInfos == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(lockInfos);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#releaseLock(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public void releaseLock(String taskInstUuid) {
        Cache cache = cacheManager.getCache(TASK_LOCK_CACHE_ID);
        if (cache == null) {
            return;
        }
        String cacheKey = getTaskLockCacheKey(taskInstUuid);
        cache.evict(cacheKey);
    }

    @Override
    public Set<Permission> getCurrentUserPermissions(String taskInstUuid, String flowDefUuid) {
        Set<Permission> permissions = Sets.newHashSet();
        if (StringUtils.isNotBlank(taskInstUuid)) {
            TaskInstance taskInstance = new TaskInstance();
            taskInstance.setUuid(taskInstUuid);
            permissions.addAll(aclTaskService.getAllPermissionBySids(taskInstance.getUuid(), PermissionGranularityUtils.getCurrentUserSids()));
            String currentUserId = SpringSecurityUtils.getCurrentUserId();
            String flowDefinitionUuid = flowDefUuid;
            if (StringUtils.isBlank(flowDefinitionUuid)) {
                flowDefinitionUuid = taskInstanceService.getFlowDefUUidByTaskInstUuid(taskInstUuid);
            }
            List<Integer> managerPermissions = flowManagementService.listManagementPermission(currentUserId, flowDefinitionUuid);
            if (managerPermissions.contains(ManagementType.SUPERVISE)) {
                permissions.add(AclPermission.SUPERVISE);
            }
            if (managerPermissions.contains(ManagementType.MONITOR)) {
                permissions.add(AclPermission.MONITOR);
            }
            if (managerPermissions.contains(ManagementType.READ)) {
                permissions.add(AclPermission.READ);
            }
            if (CollectionUtils.isEmpty(permissions)) {
                String flowInstUuid = getFlowInstUUidByTaskInstUuid(taskInstUuid);
                permissions.addAll(flowPermissionEvaluator.getFlowAccessPermission(taskInstUuid, flowInstUuid, flowDefinitionUuid));
            }
        }
        return permissions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getReaderUserIdsByFlowInstUuid(String flowInstUuid) {
        String taskInstUuid = getLastTaskInstanceUuidByFlowInstUuid(flowInstUuid);
        if (StringUtils.isBlank(taskInstUuid)) {
            return Collections.emptyList();
        }
        List<AclTaskEntry> aclTaskEntries = aclTaskService.getSid(taskInstUuid);
        return aclTaskEntries.stream().filter(entry -> {
            return BooleanUtils.isTrue(entry.getReadAuth())
                    || BooleanUtils.isTrue(entry.getTodoAuth())
                    || BooleanUtils.isTrue(entry.getDoneAuth())
                    || BooleanUtils.isTrue(entry.getAttentionAuth())
                    || BooleanUtils.isTrue(entry.getUnReadAuth())
                    || BooleanUtils.isTrue(entry.getFlagReadAuth())
                    || BooleanUtils.isTrue(entry.getSuperviseAuth())
                    || BooleanUtils.isTrue(entry.getMonitorAuth());
        }).map(AclTaskEntry::getSid).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUuid(String taskInstUuid) {
        if (StringUtils.isBlank(taskInstUuid)) {
            return false;
        }

        return taskInstanceService.countByUuid(taskInstUuid) > 0;
    }

}

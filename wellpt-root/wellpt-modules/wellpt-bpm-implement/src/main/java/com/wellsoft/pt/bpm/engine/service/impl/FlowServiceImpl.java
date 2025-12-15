/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotParam.BotFromParam;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.FlowListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.core.*;
import com.wellsoft.pt.bpm.engine.core.handler.AbstractHandler;
import com.wellsoft.pt.bpm.engine.dao.FlowSchemaDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.delegate.FlowInstanceDelegate;
import com.wellsoft.pt.bpm.engine.element.NewFlowElement;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowAclSid;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.TaskExecutor;
import com.wellsoft.pt.bpm.engine.executor.TaskExecutorFactory;
import com.wellsoft.pt.bpm.engine.executor.param.GotoTaskParam;
import com.wellsoft.pt.bpm.engine.node.EndNode;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.StartNode;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.query.*;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.timer.support.TimingState;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.bpm.engine.util.ReservedFieldUtils;
import com.wellsoft.pt.bpm.engine.util.TitleExpressionUtils;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.log.LogEvent;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.facade.service.LogFacadeService;
import com.wellsoft.pt.log.service.UserOperationLogService;
import com.wellsoft.pt.log.support.ContextLogs;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.support.TimerWorkTime;
import com.wellsoft.pt.workflow.enums.WorkFlowFieldMapping;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 流程实例的操作服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-2.1	zhulh		2012-11-2		Create
 * </pre>
 * @date 2012-11-2
 */
@Service
@Transactional
public class FlowServiceImpl extends BaseServiceImpl implements FlowService {

    private static final String COUNT_IS_COMPLETED = "select count(uuid) from FlowInstance t where t.uuid = :flowInstUuid and t.isActive = false and t.endTime is not null";
    private static final String COUNT_IS_COMPLETED_BY_TASK_INST_UUID = "select count(uuid) from FlowInstance t1 where "
            + "exists(select uuid from TaskInstance t2 where t2.uuid = :taskInstUuid and t2.flowInstance.uuid = t1.uuid) "
            + "and t1.isActive = false and t1.endTime is not null";
    private static final String HAS_CREATE_PERMISION_WHERE_HQL = "o.enabled = true and o.uuid = :flowDefUuid";
    private static Logger log = LoggerFactory.getLogger(FlowServiceImpl.class);
    @Autowired
    private FlowDefinitionService flowDefinitionService;
    //    @Autowired
//    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;
    @Autowired
    private AclService aclService;
    @Autowired
    private WorkFlowAclServiceWrapper aclServiceWrapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private FlowSchemaDao flowSchemaDao;
    @Autowired
    private TaskSubFlowService taskSubFlowService;
    @Autowired
    private LogFacadeService logFacadeService;
    @Autowired
    private UserOperationLogService userOperationLogService;
    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;
    @Autowired(required = false)
    private Map<String, FlowListener> listenerMap;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private BotFacadeService botFacadeService;
    @Autowired
    private FlowManagementService flowManagementService;
    @Autowired
    private FlowIndexDocumentService flowIndexDocumentService;
    @Autowired
    @Qualifier("workService")
    private WorkService workService;
    @Autowired
    private TaskTimerService taskTimerService;
    @Autowired
    private TsTimerFacadeService timerFacadeService;
    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    /**
     * 如何描述该方法
     *
     * @param taskData
     * @param token
     * @param flowDelegate
     * @param transition
     * @param transitionResolver
     * @return
     */
    public static TaskData getTaskConfigData(TaskData taskData, Token token, FlowDelegate flowDelegate,
                                             Transition transition, TransitionResolver transitionResolver) {
        List<QueryItem> nodes = new ArrayList<QueryItem>();
        String daiding = StringUtils.defaultIfBlank(AppCodeI18nMessageSource.getMessage("WorkflowWork.taskProcess.nextUnknownTaskName", "pt-app-workflow"), "待定...");
        String userNameDaiding = StringUtils.defaultIfBlank(AppCodeI18nMessageSource.getMessage("WorkflowWork.taskProcess.nextUnknownUserName", "pt-app-workflow"), "下一环节确定");
        String toTaskId = taskData.getToTaskId(transition.getFromId());
        if (StringUtils.isBlank(toTaskId) || StringUtils.equals(FlowConstant.AUTO_SUBMIT, toTaskId)) {
            try {
                // 如果是分支节点
                taskData.setSubmitButtonId(WorkFlowPrivilege.Submit.getCode());
                List<Node> toNodes = Lists.newArrayList();
                if (flowDelegate.isConditionTask(token.getNode()) || flowDelegate.isConditionTask(transition.getFrom())) {
                    transitionResolver.resolveForkTask(transition, token);
                    toNodes = transition.getTos();
                    if (CollectionUtils.size(toNodes) != 1) {
                        taskData.setTaskName(daiding);
                        taskData.setTaskId(StringUtils.EMPTY);
                        taskData.setTaskRawUserNames(userNameDaiding);
                        return taskData;
                    }
                } else {
                    transitionResolver.resolveForkTask(transition, token);
                    toNodes = transition.getTos();
                }
                for (Node toNode : toNodes) {
                    QueryItem node = new QueryItem();
                    node.put("id", toNode.getId());
                    nodes.add(node);
                }
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));

                taskData.setTaskName(daiding);
                taskData.setTaskId(StringUtils.EMPTY);
                taskData.setTaskRawUserNames(userNameDaiding);
                return taskData;
            }
        } else {
            QueryItem node = new QueryItem();
            node.put("id", toTaskId);
            nodes.add(node);
        }
        IdentityResolverStrategy identityResolverStrategy = ApplicationContextHolder
                .getBean(IdentityResolverStrategy.class);
        Iterator<QueryItem> it = nodes.iterator();

        Map<String, String> taskUsers = Maps.newHashMap();
        List<String> taskIds = Lists.newArrayList();
        while (it.hasNext()) {
            StringBuilder taskNames = new StringBuilder();
            StringBuilder taskRawUserNames = new StringBuilder();
            QueryItem item = it.next();
            String nextTaskId = item.get("id").toString();
            Node node = flowDelegate.getTaskNode(nextTaskId);
            if (node instanceof StartNode) {
                taskNames.append(node.getName());
                taskRawUserNames.append(" ");
            } else if (node instanceof EndNode) {
                taskNames.append("结束");
                taskRawUserNames.append(" ");
            } else {
                TaskElement taskElement = flowDelegate.getFlow().getTask(nextTaskId);
                String taskName = taskElement.getName();
                taskIds.add(nextTaskId);
                // 由前一环节办理人指定或指定具体办理人
                if (!taskElement.isSetUser() || taskElement.isSelectAgain()) {
                    // 环节名称
                    taskNames.append(taskName);
                    // 环节办理人
                    taskRawUserNames.append(userNameDaiding);
                } else {
                    // 环节名称
                    taskNames.append(taskName);
                    List<String> rawNames = Lists.newArrayList();
                    boolean isSetUserEmptyToUser = flowDelegate.getIsSetUserEmptyToUser(nextTaskId);
                    try {
                        List<FlowUserSid> userSids = identityResolverStrategy.resolve(node, token, flowDelegate.getTaskUsers(nextTaskId), ParticipantType.TodoUser);
                        if (CollectionUtils.isEmpty(userSids) && isSetUserEmptyToUser) {
                            userSids =
                                    identityResolverStrategy.resolve(node, token, flowDelegate.getTaskEmptyToUsers(nextTaskId), ParticipantType.TodoUser);
                        }

                        if (CollectionUtils.isEmpty(userSids)) {
                            rawNames.add(userNameDaiding);
                        } else {
                            for (FlowUserSid flowUserSid : userSids) {
                                rawNames.add(flowUserSid.getName());
                            }
                        }
                    } catch (Exception e) {
                        taskRawUserNames.append(userNameDaiding);
                    }
                    taskRawUserNames.append(StringUtils.join(rawNames, Separator.SEMICOLON.getValue()));
                }
            }

            taskUsers.put(taskNames.toString(), taskRawUserNames.toString());
        }

        Collection<String> values = taskUsers.values();
        List<String> taskRawUserNames = Lists.newArrayList();
        values.forEach(rawNames -> {
            String rawUserNames = rawNames;
            if (StringUtils.isBlank(rawUserNames) || "null".equals(rawUserNames)
                    || StringUtils.contains(rawUserNames, userNameDaiding)) {
                rawUserNames = userNameDaiding;
            }
            taskRawUserNames.add(rawUserNames);
        });
        taskData.setTaskName(StringUtils.join(taskUsers.keySet().toArray(new String[0]), Separator.COMMA.getValue()));
        taskData.setTaskId(StringUtils.join(taskIds.toArray(new String[]{}), Separator.COMMA.getValue()));
        taskData.setTaskRawUserNames(StringUtils.join(taskRawUserNames, Separator.COMMA.getValue()));
        return taskData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.service.FlowService#getFlowDefinition(java.lang.String)
     */
    @Override
    public FlowDefinition getFlowDefinition(String flowDefUuid) {
        return flowDefinitionService.getOne(flowDefUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#getFlowDefinitionById(java.lang.String)
     */
    @Override
    public FlowDefinition getFlowDefinitionById(String id) {
        return flowDefinitionService.getById(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#saveAsDraftByFlowDefId(java.lang.String, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public FlowInstance saveAsDraftByFlowDefId(String flowDefId, TaskData taskData) {
        FlowDefinition flowDefinition = this.flowDefinitionService.getById(flowDefId);
        return saveAsDraft(flowDefinition.getUuid(), taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.service.TaskService#save(com.wellsoft.pt.workflow.engine.entity.TaskInstance)
     */
    @Override
    public FlowInstance saveAsDraft(String flowDefUuid, TaskData taskData) {
        return saveSubFlowAsDraft(null, null, flowDefUuid, taskData, null);
        // FlowDefinition flowDefinition =
        // this.flowDefinitionService.get(flowDefUuid);
        //
        // String flowDefId = flowDefinition.getId();
        // String flowName = flowDefinition.getName();
        // String title = taskData.getTitle(flowDefId);
        // String startUserId = taskData.getUserId();
        // Date startTime = Calendar.getInstance().getTime();
        // String dataUuid = taskData.getDataUuid();
        // if (StringUtils.isBlank(title)) {
        // title = flowName + "_" + SpringSecurityUtils.getCurrentUserName() +
        // "_"
        // + DateUtils.formatDateTime(startTime);
        // }
        //
        // FlowInstance flowInstance = new FlowInstance();
        // // 流程定义id
        // flowInstance.setId(flowDefId);
        // // 流程实例名称
        // flowInstance.setName(flowName);
        // // 动态表单数据UUID
        // flowInstance.setDataUuid(dataUuid);
        // // 流程定义
        // flowInstance.setFlowDefinition(flowDefinition);
        // // 标题
        // flowInstance.setTitle(title);
        // // 开始时间
        // flowInstance.setStartTime(startTime);
        // // 流程启动者
        // flowInstance.setStartUserId(startUserId);
        // // 当前流程是否处理活动状态
        // flowInstance.setIsActive(true);
        //
        // this.flowInstanceService.save(flowInstance);
        // this.aclServiceWrapper.createAcl(flowInstance, startUserId,
        // AclPermission.DRAFT);
        //
        // // 用户操作日志
        // UserOperationLog log = new UserOperationLog();
        // log.setModuleId(ModuleID.WORKFLOW.getValue());
        // log.setModuleName("工作流程");
        // log.setContent(flowName);
        // log.setOperation("保存草稿");
        // log.setCreatorName(taskData.getUserName());
        // log.setDetails(flowInstance.getTitle());
        // userOperationLogService.save(log);
        //
        // // 保存流程输入参数
        // Set<String> keys = taskData.getCustomDataKeySet();
        // for (String name : keys) {
        // String value = taskData.getCustomData(name);
        // FlowInstanceParameter parameter = new FlowInstanceParameter();
        // parameter.setFlowInstUuid(flowInstance.getUuid());
        // parameter.setName(name);
        // List<FlowInstanceParameter> parameters =
        // flowInstanceParameterService.findByExample(parameter);
        // if (parameters.isEmpty()) {
        // parameter.setValue(value);
        // flowInstanceParameterService.save(parameter);
        // } else {
        // parameters.get(0).setValue(value);
        // flowInstanceParameterService.save(parameters.get(0));
        // }
        // }
        //
        // return flowInstance;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#createEmptySubFlowInstance(com.wellsoft.pt.bpm.engine.entity.FlowInstance, java.lang.String)
     */
    @Override
    public FlowInstance createEmptySubFlowInstance(FlowInstance parent, String subFlowDefId) {
        FlowDefinition flowDefinition = this.flowDefinitionService.getById(subFlowDefId);
        FlowInstance flowInstance = new FlowInstance();
        // 流程定义
        flowInstance.setFlowDefinition(flowDefinition);
        // 当前流程是否处理活动状态
        flowInstance.setIsActive(true);
        // 归属系统ID
        flowInstance.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        // 父流程实例
        flowInstance.setParent(parent);
        // 归属系统及租户
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            flowInstance.setSystem(system);
            flowInstance.setTenant(SpringSecurityUtils.getCurrentTenantId());
        } else {
            flowInstance.setSystem(parent.getSystem());
            flowInstance.setTenant(parent.getTenant());
        }
        this.flowInstanceService.save(flowInstance);
        this.flowInstanceService.flushSession();
        this.flowInstanceService.clearSession();
        return flowInstance;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#updateSubFlowAsDraftByFlowDefId(java.lang.String, com.wellsoft.pt.bpm.engine.entity.FlowInstance, java.lang.String, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public FlowInstance updateSubFlowAsDraftByFlowDefId(String flowInstUuid, FlowInstance parent,
                                                        TaskInstance parentTaskInstance, String subFlowDefId, TaskData taskData, String todoName) {
        taskData.put("updateFlowInstUuid", flowInstUuid);
        FlowDefinition flowDefinition = this.flowDefinitionService.getById(subFlowDefId);
        FlowInstance flowInstance = saveSubFlowAsDraft(parent, parentTaskInstance, flowDefinition.getUuid(), taskData,
                todoName);
        taskData.remove("updateFlowInstUuid");
        return flowInstance;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#saveSubFlowAsDraft(com.wellsoft.pt.bpm.engine.entity.FlowInstance, java.lang.String, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public FlowInstance saveSubFlowAsDraftByFlowDefId(FlowInstance parent, TaskInstance parentTaskInstance,
                                                      String subFlowDefId, TaskData taskData, String todoName) {
        FlowDefinition flowDefinition = this.flowDefinitionService.getById(subFlowDefId);
        return saveSubFlowAsDraft(parent, parentTaskInstance, flowDefinition.getUuid(), taskData, todoName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#saveSubFlowAsDraft(com.wellsoft.pt.bpm.engine.entity.FlowInstance, java.lang.String, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public FlowInstance saveSubFlowAsDraft(FlowInstance parent, TaskInstance parentTaskInstance, String subFlowDefUuid,
                                           TaskData taskData, String todoName) {
        FlowDefinition flowDefinition = this.flowDefinitionService.getOne(subFlowDefUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
        String flowDefId = flowDefinition.getId();
        String flowName = flowDefinition.getName();
        String title = taskData.getTitle(flowDefId);
        String startUserId = taskData.getUserId();
        Date startTime = Calendar.getInstance().getTime();
        String formUuid = taskData.getFormUuid();
        DyFormFormDefinition formFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        String pformUuid = formFormDefinition.doGetPFormUuid();
        // 7.0平台显示单据返回空
        if (StringUtils.isBlank(pformUuid)) {
            pformUuid = formFormDefinition.getUuid();
        }
        String dataUuid = taskData.getDataUuid();
        String ownerId = taskData.getFlowOwnerId();
        String startDepartmentId = taskData.getFlowStartDepartmentId();
        String ownerDepartmentId = taskData.getFlowOwnerDepartmentId();
        String startUnitId = taskData.getFlowStartUnitId();
        String ownerUnitId = taskData.getFlowOwnerUnitId();
        if (parent != null) {
            NewFlowElement subFlowElement = workService.loadSubFlowElement(parent.getUuid(), parent.getUuid(),
                    parentTaskInstance.getId(), flowDefId);
            DyFormData parentFlowDyFormData = dyFormFacade.getDyFormData(parent.getFormUuid(), parent.getDataUuid());
            title = TitleExpressionUtils.generateSubFlowInstanceTitle(subFlowElement, parent.getFlowDefinition(),
                    parent, taskData, parentFlowDyFormData, todoName);

            if (title.length() > 255) {
                title = title.substring(0, 254);
            }
            if (!taskData.getIsAsync(flowDefId) && BooleanUtils.isTrue(parent.getIsActive())) {
                parent.setIsActive(false);
                this.flowInstanceService.save(parent);
            }
        } else {
            if (StringUtils.isBlank(title)) {
                title = flowName + "_" + SpringSecurityUtils.getCurrentUserName() + "_"
                        + DateUtils.formatDateTime(startTime);
            }
        }

        // 要更新的流程实例
        String updateFlowInstUuid = (String) taskData.get("updateFlowInstUuid");
        FlowInstance flowInstance = null;
        if (StringUtils.isNotBlank(updateFlowInstUuid)) {
            flowInstance = flowInstanceService.getOne(updateFlowInstUuid);
        } else {
            flowInstance = new FlowInstance();
        }
        // 流程定义id
        flowInstance.setId(flowDefId);
        // 流程实例名称
        flowInstance.setName(flowName);
        // 动态表单定义UUID
        flowInstance.setFormUuid(pformUuid);
        // 动态表单数据UUID
        flowInstance.setDataUuid(dataUuid);
        // 流程定义
        flowInstance.setFlowDefinition(flowDefinition);
        // 标题
        flowInstance.setTitle(title);
        // 开始时间
        flowInstance.setStartTime(startTime);
        // 流程启动者ID
        flowInstance.setStartUserId(startUserId);
        // 流程所有者ID
        flowInstance.setOwnerId(ownerId);
        // 流程发起部门ID
        flowInstance.setStartDepartmentId(startDepartmentId);
        // 流程所属部门ID
        flowInstance.setOwnerDepartmentId(ownerDepartmentId);
        // 流程发起单位ID
        flowInstance.setStartUnitId(startUnitId);
        // 流程所属单位ID
        flowInstance.setOwnerUnitId(ownerUnitId);
        // 当前流程是否处理活动状态
        flowInstance.setIsActive(true);
        // 归属系统ID
        flowInstance.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        // 父流程实例
        flowInstance.setParent(parent);
        // 计时信息
        flowInstance.setIsTiming(false);
        flowInstance.setTimingState(TimingState.NORMAL);
        flowInstance.setIsOverDue(false);

        // 是否可查看主流程
        flowInstance.setViewMainFlowJson(flowDelegate.getViewMainFlowJson());

        // 设置预留字段
        ReservedFieldUtils.setReservedFields(flowInstance, taskData);

        /* modified by huanglinchuan2014.12.25 begin */
        // 注释掉，将其移至WorkServiceImpl.setFlowInstanceTitle和WorkServiceImpl.save方法中
        /*
         * modified by huanglinchuan2014.10.20 begin String fromTitle =
         * taskData.getTitle(flowInstance.getId());
         * //流程定义增加标题表达式，如果该流程表达式不为空，则以该流程表达式为准，否则以动态表单设定的标题为准 if
         * (!StringUtils.trimToEmpty (flowDefinition.getTitleExpression()).equals("")) {
         * //根据动态表单和流程变量解析流程标题表达式 DyFormData mainData =
         * taskData.getDyFormData(taskData.getDataUuid()); if (mainData == null) {
         * mainData = dyFormApiFacade.getDyFormData(taskData.getFormUuid(),
         * taskData.getDataUuid()); } Map<String, Object> allData =
         * mainData.getFormDataOfMainform(); Calendar cal = Calendar.getInstance(); int
         * year = cal.get(Calendar.YEAR);//获取年份 int month = cal.get(Calendar.MONTH) +
         * 1;//获取月份 int day = cal.get(Calendar.DATE);//获取日 int hour =
         * cal.get(Calendar.HOUR_OF_DAY);//小时 int minute = cal.get(Calendar.MINUTE);//分
         * int second = cal.get(Calendar.SECOND);//秒 allData.put("年",
         * String.valueOf(year)); allData.put("月", DateUtil.getFormatDate(month));
         * allData.put("日", DateUtil.getFormatDate(day)); allData.put("时",
         * DateUtil.getFormatDate(hour)); allData.put("分",
         * DateUtil.getFormatDate(minute)); allData.put("秒",
         * DateUtil.getFormatDate(second)); allData.put("简年",
         * String.valueOf(year).substring(2));
         *
         * allData.put("流程名称", flowDefinition.getName()); allData.put("流程ID",
         * flowDefinition.getId()); allData.put("流程编号", flowDefinition.getCode());
         *
         * UserProfile userProfile =
         * orgApiFacade.getUserProfileByUserId(taskData.getUserId());
         * allData.put("发起人姓名", userProfile.getUserName()); allData.put("发起人所在部门名称",
         * userProfile.getDepartmentName());
         *
         * TemplateEngine templateEngine =
         * TemplateEngineFactory.getDefaultTemplateEngine(); try { flowInstance.setTitle
         * (templateEngine.process(flowInstance.getFlowDefinition
         * ().getTitleExpression(), allData));
         * taskData.setTitle(flowInstance.getFlowDefinition().getId(),
         * flowInstance.getTitle()); } catch (Exception ex) { modified by huanglinchuan
         * 2014.12.15 begin
         * logger.error(flowInstance.getFlowDefinition().getTitleExpression());
         * logger.error(JsonUtils.object2Json(allData)); logger.error("流程标题解析错误：", ex);
         * modified by huanglinchuan 2014.12.15 end } } else { if
         * (StringUtils.isNotBlank(fromTitle)) { flowInstance.setTitle(fromTitle); } }
         */
        /* modified by huanglinchuan2014.10.20 end */
        /* modified by huanglinchuan2014.12.25 end */

        // 设置流程映射字段
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        boolean hasFieldMapping = false;
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            taskData.setDyFormData(dataUuid, dyFormData);
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CURRENT_FLOW_STATE_NAME.getValue())) {
            dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_FLOW_STATE_NAME.getValue(),
                    flowDelegate.getFlowStateName(WorkFlowState.Draft));
            hasFieldMapping = true;
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CURRENT_FLOW_STATE_CODE.getValue())) {
            dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_FLOW_STATE_CODE.getValue(),
                    WorkFlowState.Draft);
            hasFieldMapping = true;
        }
        if (hasFieldMapping) {
            // dyFormData.doForceCover();
            // String updatedDataUuid = dyFormFacade.saveFormData(dyFormData);
            taskData.setDataUuid(dataUuid);
            // taskData.setDyFormData(dataUuid, dyFormFacade.getDyFormData(formUuid,
            // updatedDataUuid));
            taskData.setDyFormData(dataUuid, dyFormData);
            taskData.addUpdatedDyFormData(dataUuid, dyFormData);
        }

        if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(flowDefinition.getMultiJobFlowType())) {
            // 设置职位选择值
            if (StringUtils.isNotBlank(taskData.getJobSelected(startUserId))) {
                flowInstance.setStartJobId(taskData.getJobSelected(startUserId));
            } else if (StringUtils.isNotBlank(flowDefinition.getJobField())) {
                Object jobFieldValue = dyFormData.getFieldValue(flowDefinition.getJobField());
                if (jobFieldValue != null && StringUtils.isNotBlank(jobFieldValue.toString())) {
                    flowInstance.setStartJobId(jobFieldValue.toString());
                }
            }
        } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(flowDefinition.getMultiJobFlowType())) {
            flowInstance.setStartJobId(taskData.getMainJob());
        }

        // 组织版本
        flowInstance.setOrgVersionId(workflowOrgService.getOrgVersionId(flowDelegate));
        // 设置归属的系统
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            flowInstance.setSystem(RequestSystemContextPathResolver.system());
            flowInstance.setTenant(SpringSecurityUtils.getCurrentTenantId());
        } else if (parent != null && StringUtils.isBlank(flowInstance.getSystem())) {
            flowInstance.setSystem(parent.getSystem());
            flowInstance.setTenant(parent.getTenant());
        }

        this.flowInstanceService.save(flowInstance);
        this.flowInstanceService.flushSession();
        this.flowInstanceService.clearSession();

        // 设置任务数据的流程实例UUID
        taskData.setFlowInstUuid(flowInstance.getUuid());

        // 发布流程创建事件
        String[] listeners = flowDelegate.getStartNode().getListeners();
        String rtFlowListener = (String) taskData.getCustomData(CustomRuntimeData.KEY_FLOW_LISTENER);
        if (StringUtils.isNotBlank(rtFlowListener)) {
            listeners = (String[]) ArrayUtils.addAll(listeners, StringUtils.split(rtFlowListener, Separator.SEMICOLON.getValue()));
        }
        ExecutionContext executionContext = null;
        if (MapUtils.isNotEmpty(listenerMap) && ArrayUtils.isNotEmpty(listeners)) {
            Token token = new Token(flowInstance, taskData);
            executionContext = new ExecutionContext(token);
            Event event = AbstractHandler.getEvent(flowDelegate.getStartNode(), Listener.FLOW, executionContext);
            // task#8100: 跳转环节触发环节的监听器
            // if (!WorkFlowOperation.GOTO_TASK.equals(event.getActionType())) {
            for (String listener : listeners) {
                FlowListener flowListener = listenerMap.get(listener);
                if (flowListener == null) {
                    continue;
                }
                try {
                    flowListener.onCreated(event);
                } catch (Exception e) {
                    String errorString = ExceptionUtils.getStackTrace(e);
                    logger.error(errorString);
                    if (e instanceof WorkFlowException) {
                        throw (WorkFlowException) e;
                    } else {
                        throw new WorkFlowException(
                                "流程实例事件监听器" + "[" + flowListener.getName() + "]" + "执行流程实例创建事件处理出现异常: " + errorString);
                    }
                }
            }
            // }
        }

        // 执行事件脚本
        executeEventScript(flowInstance, flowDelegate, taskData, executionContext, Pointcut.CREATED);

        Set<String> rawUserIds = taskData.getTaskUsers(SubTaskNode.DRAFT);
        // 解析用户为实际的参与人
        List<String> userIds = IdentityResolverStrategy
                .resolveUserIds(Arrays.asList(rawUserIds.toArray(new String[0])));
        // 指定的办理人为空，使用当前用户
        if (userIds.isEmpty()) {
            userIds.add(startUserId);
        }
        // 流程草稿只能归属于一个办理人
        if (userIds.size() > 1) {
            throw new WorkFlowException("流程草稿只能归属于一个办理人！");
        }
        for (String userId : userIds) {
            aclService.createAcl(flowInstance, userId, AclPermission.DRAFT);
        }

        // 用户操作日志
        if (taskData.isLogUserOperation() != false) {
            BusinessOperationLog log = new BusinessOperationLog();
            log.setModuleId(flowDefinition.getModuleId());
            // log.setModuleName("工作流程");
            log.setDataDefType(ModuleID.WORKFLOW.getValue());
            log.setDataDefId(flowDefinition.getId());
            log.setDataDefName(flowDefinition.getName());
            log.setOperation(WorkFlowOperation.getName(WorkFlowOperation.SAVE_DRAFT));
            log.setUserId(taskData.getUserId());
            log.setUserName(taskData.getUserName());
            log.setDataId(flowInstance.getUuid());
            log.setDataName(flowInstance.getTitle());
            // userOperationLogService.save(log);
            Map<String, Object> details = Maps.newHashMap();
            // details.put("dyform", dyFormData.cloneDyFormDatasToJson());
            // details.put("flowInstUuid", flowInstance.getUuid());
            ContextLogs.sendLogEvent(new LogEvent(log, details));
        }

        // 保存流程输入参数
        saveOrUpdateFlowInstanceParameter(taskData, flowInstance);

        return flowInstance;
    }

    /**
     * @param taskData
     * @param flowInstance
     */
    private void saveOrUpdateFlowInstanceParameter(TaskData taskData, FlowInstance flowInstance) {
        Set<String> keys = taskData.getCustomDataKeySet();
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        List<FlowInstanceParameter> toSavedParameters = Lists.newArrayList();
        List<FlowInstanceParameter> parameters = flowInstanceParameterService.getByFlowInstanceUuid(flowInstance.getUuid());
        for (String name : keys) {
            Object data = taskData.getCustomData(name);
            String value = data == null ? null : data.toString();

            List<FlowInstanceParameter> existsParameters = parameters.stream().filter(parameter -> StringUtils.equals(parameter.getName(), name)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(existsParameters)) {
                existsParameters.forEach(flowInstanceParameter -> flowInstanceParameter.setValue(value));
                toSavedParameters.addAll(existsParameters);
            } else {
                FlowInstanceParameter parameter = new FlowInstanceParameter();
                parameter.setFlowInstUuid(flowInstance.getUuid());
                parameter.setName(name);
                parameter.setValue(value);
                toSavedParameters.add(parameter);
            }
        }
        flowInstanceParameterService.saveAll(toSavedParameters);
    }

    /**
     * @param flowInstance
     * @param flowDelegate
     * @param taskData
     * @param executionContext
     * @param pointcut
     */
    private void executeEventScript(FlowInstance flowInstance, FlowDelegate flowDelegate, TaskData taskData,
                                    ExecutionContext executionContext, String pointcut) {
        Script script = flowDelegate.getFlowEventScript(pointcut);
        if (script == null) {
            return;
        }
        Token token = new Token(flowInstance, taskData);
        ExecutionContext ec = executionContext;
        if (ec == null) {
            ec = new ExecutionContext(token);
        }
        Event event = AbstractHandler.getEvent(flowDelegate.getStartNode(), Listener.FLOW, ec);
        WorkFlowScriptHelper.executeEventScript(event, script);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.service.FlowService#startFlowInstance(java.lang.String, com.wellsoft.pt.workflow.engine.support.TaskData)
     */
    @Override
    public FlowInstance startFlowInstance(String flowInstUuid, TaskData taskData) {
        // 判断流程是否已经启动
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        if (this.dao.countByNamedQuery("getTaskInstancesByFlowInstUuid", values) > 0) {
            throw new WorkFlowException("该流程已启动或已办结，不能再次启动!");
        }

        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);

        // 保存流程输入参数
        saveOrUpdateFlowInstanceParameter(taskData, flowInstance);

        // 更新身份
        Token token = new Token(flowInstance, taskData);
        updateStartJobIdentity(flowInstance, taskData, token);

        taskData.setStartNewFlow(flowInstUuid, true);
        FlowInstanceDelegate delegate = new FlowInstanceDelegate();
        delegate.start(flowInstance, taskData, token);

        // 删除草稿
        removeDraftPermission(taskData.getUserId(), flowInstance.getUuid());
        this.dao.getSession().flush();
        this.dao.getSession().clear();
        flowInstance.setStartTime(Calendar.getInstance().getTime());
        flowInstanceService.save(flowInstance);
        flowInstanceService.flushSession();
        flowInstanceService.clearSession();
        return flowInstance;
    }

    /**
     * @param flowInstance
     * @param taskData
     * @param token
     */
    private void updateStartJobIdentity(FlowInstance flowInstance, TaskData taskData, Token token) {
        String userId = taskData.getUserId();
        Node node = token.getNode();
        if (node instanceof StartNode) {
            node = token.getFlowDelegate().getTaskNode(node.getToID());
        }
        FlowUserSid flowUserSid = flowUserJobIdentityService.getStartUserSid(userId, taskData, node, token.getFlowDelegate(), token);
        flowInstance.setStartJobId(flowUserSid.getIdentityId());
        if (StringUtils.isBlank(taskData.getJobSelected(userId))) {
            taskData.setJobSelected(userId, flowUserSid.getIdentityId());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#startFlowInstance(java.lang.String, java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public FlowInstance startByFlowDefId(String flowDefId, String startUserId, String toTaskId,
                                         List<String> toTaskUsers, String formUuid, String dataUuid) {
        FlowInstance flowInstance = null;
        TaskData taskData = new TaskData();
        taskData.setUserId(startUserId);
        taskData.setFormUuid(formUuid);
        taskData.setDataUuid(dataUuid);
        // 设置任务办理人
        FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
        String flowDefUuid = flowDefinition.getUuid();
        if (AS_DRAFT.equals(toTaskId)) {// 启动为草稿
            // 工作保存为草稿
            flowInstance = saveAsDraft(flowDefinition.getUuid(), taskData);
        } else if (FlowService.START_TODO.equals(toTaskId)) {
            // 工作保存为草稿
            flowInstance = saveAsDraft(flowDefUuid, taskData);

            String flowInstUuid = flowInstance.getUuid();
            startFlowInstance(flowInstUuid, taskData);
        } else if (AUTO_SUBMIT.equals(toTaskId)) {// 自动提交
            // 工作保存为草稿
            flowInstance = saveAsDraft(flowDefUuid, taskData);

            String flowInstUuid = flowInstance.getUuid();
            startFlowInstance(flowInstUuid, taskData);
            // 提交第一个任务
            List<TaskInstance> taskInstances = taskService.getTodoTasks(startUserId, flowInstUuid);
            for (TaskInstance taskInstance : taskInstances) {
                String fromTaskId = taskInstance.getId();
                // 设置自动提交
                taskData.setAutoSubmit(fromTaskId, true);
                // 设置自动提交的办理人
                taskData.setAutoSubmitUsers(fromTaskId, toTaskUsers);
                taskService.submit(taskInstance, taskData);
            }
        } else {// 提交到指定环节
            // 工作保存为草稿
            flowInstance = saveAsDraft(flowDefUuid, taskData);

            String flowInstUuid = flowInstance.getUuid();
            // 设置流程提交的环节
            Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
            taskUsers.put(toTaskId, toTaskUsers);
            taskData.setTaskUsers(taskUsers);
            taskData.setToTaskId(FlowDelegate.START_FLOW_ID, toTaskId.toString());

            startFlowInstance(flowInstUuid, taskData);
        }
        return flowInstance;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#getFirstTaskData(java.lang.String)
     */
    @Override
    public TaskData getFirstTaskData(String flowDefUuid) {
        FlowDelegate flowDelegate = new FlowDelegate(this.flowDefinitionService.getOne(flowDefUuid));
        return flowDelegate.getFistTaskData();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#getLastTaskData(java.lang.String)
     */
    @Override
    public TaskData getLastTaskData(String flowDefUuid) {
        FlowDelegate flowDelegate = new FlowDelegate(this.flowDefinitionService.getOne(flowDefUuid));
        return flowDelegate.getLastTaskData();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#getFirstTaskProcessInfo(java.lang.String)
     */
    @Override
    public TaskData getFirstTaskProcessInfo(String flowDefUuid) {
        FlowDelegate flowDelegate = new FlowDelegate(this.flowDefinitionService.getOne(flowDefUuid));
        return flowDelegate.getFistTaskProcessInfo();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#getNextTaskProcessInfo(java.lang.String, java.lang.String)
     */
    @Override
    public TaskData getNextTaskProcessInfo(String flowDefUuid, String taskId) {
        TaskData taskData = new TaskData();
        taskData.setUserId(SpringSecurityUtils.getCurrentUserId());
        taskData.setTaskId(taskId);
        FlowInstance flowInstance = new FlowInstance();
        flowInstance.setStartUserId(SpringSecurityUtils.getCurrentUserId());
        flowInstance.setFlowDefinition(this.flowDefinitionService.getOne(flowDefUuid));
        Token token = new Token(flowInstance, taskData);
        FlowDelegate flowDelegate = token.getFlowDelegate();
        taskData.setFormUuid(flowDelegate.getFlow().getProperty().getFormID());
        Transition transition = flowDelegate.getTaskNode(taskId).getLeavingTransition();
        TransitionResolver transitionResolver = TransitionResolverFactory.getResolver(transition);
        return getTaskConfigData(taskData, token, flowDelegate, transition, transitionResolver);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#deleteDraft(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteDraft(String userId, String flowInstUuid) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);

        Map<String, Object> map = new HashMap<String, Object>();
        FlowSchema flowSchema = flowSchemaDao.getOne(flowDefinition.getFlowSchemaUuid());// flowDefinition.getFlowSchema();
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
        FlowInstanceDeleteQueryItem flowInstanceDeleteQueryItem = new FlowInstanceDeleteQueryItem();
        BeanUtils.copyProperties(flowInstance, flowInstanceDeleteQueryItem);
        flowInstanceDeleteQueryItem.setFlowDefUuid(flowDefinition.getUuid());
        if (flowInstance.getParent() != null) {
            flowInstanceDeleteQueryItem.setParentFlowInstUuid(flowInstance.getParent().getUuid());
        }
        List<FlowInstanceParameter> flowInstanceParameters = flowInstanceParameterService
                .getByFlowInstanceUuid(flowInstUuid);
        map.put("flowInstance", flowInstanceDeleteQueryItem);
        map.put("flowInstanceParameters", flowInstanceParameters);
        // ACL权限
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("objectIdIdentity", flowInstance.getUuid());
        List<AclObjectIdentityDeleteQueryItem> aclObjectIdentityDeleteQueryItems = this.nativeDao
                .namedQuery("getAclObjectIdentityQueryItem", values, AclObjectIdentityDeleteQueryItem.class);
        List<AclEntryDeleteQueryItem> aclEntryDeleteQueryItems = this.nativeDao.namedQuery("aclEntryQueryItem", values,
                AclEntryDeleteQueryItem.class);
        map.put("aclObjectIdentities", aclObjectIdentityDeleteQueryItems);
        map.put("aclEntries", aclEntryDeleteQueryItems);

        // 表单定义
        String formUuid = flowInstance.getFormUuid();
        String dataUuid = flowInstance.getDataUuid();
        DyFormFormDefinition dyFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        DyFormDefinitionDeleteQueryItem dyFormDefinitionDeleteQueryItem = new DyFormDefinitionDeleteQueryItem(
                dyFormDefinition);
        // BeanUtils.copyProperties(dyFormDefinition,
        // dyFormDefinitionDeleteQueryItem, new String[] { "jsonHandler" });
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
        String serialNo = "";
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue())) {
            Object tmpSerialNo = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue());
            serialNo = tmpSerialNo == null ? "" : tmpSerialNo.toString();
        }
        // 流程环节
        String taskName = flowDelegate.getTaskNode(flowDelegate.getStartNode().getToID()).getName();
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
        log.setRemark("删除草搞");
        this.dao.save(log);

        // 发布流程删除事件
        TaskData taskData = new TaskData();
        taskData.setUserId(userId);
        taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
        taskData.setFormUuid(formUuid);
        taskData.setDataUuid(dataUuid);
        taskData.setDyFormData(dataUuid, dyFormData);
        String[] listeners = flowDelegate.getStartNode().getListeners();
        String rtFlowListener = (String) taskData.getCustomData(CustomRuntimeData.KEY_FLOW_LISTENER);
        if (StringUtils.isNotBlank(rtFlowListener)) {
            listeners = (String[]) ArrayUtils.addAll(listeners, StringUtils.split(rtFlowListener, Separator.SEMICOLON.getValue()));
        }
        ExecutionContext executionContext = null;
        if (MapUtils.isNotEmpty(listenerMap) && ArrayUtils.isNotEmpty(listeners)) {
            Token token = new Token(flowInstance, taskData);
            executionContext = new ExecutionContext(token);
            Event event = AbstractHandler.getEvent(flowDelegate.getStartNode(), Listener.FLOW, executionContext);
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
            }
            // }
        }

        // 执行事件脚本
        executeEventScript(flowInstance, flowDelegate, taskData, executionContext, Pointcut.DELETED);

        // 1、删除草搞权限
        removeDraftPermission(taskData.getUserId(), flowInstance.getUuid());
        // 2、删除动态表单数据
        dyFormFacade.delFullFormData(formUuid, dataUuid);
        // 3、删除流程数据
        flowInstanceService.remove(flowInstUuid);

        // 删除全文检索ES内容
        taskData.setFlowInstUuid(flowInstance.getUuid());
        flowIndexDocumentService.deleteIndex(taskData);

        // 用户操作日志
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
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#stop(java.lang.String, java.lang.String)
     */
    @Override
    public void stop(String userId, String flowInstUuid) {
        List<TaskInstance> unfinishedSubTasks = taskService.getUnfinishedTasks(flowInstUuid);
        for (TaskInstance unfinishedSubTask : unfinishedSubTasks) {
            String subTaskKey = unfinishedSubTask.getUuid() + userId;
            TaskData gotoTaskData = new TaskData();
            gotoTaskData.setUserId(userId);
            gotoTaskData.setAction(subTaskKey, WorkFlowOperation.getName(WorkFlowOperation.GOTO_TASK));
            gotoTaskData.setActionType(subTaskKey, WorkFlowOperation.GOTO_TASK);
            String gotoTaskId = FlowDelegate.END_FLOW_ID;
            GotoTaskParam gotoTaskParam = new GotoTaskParam(unfinishedSubTask, gotoTaskData, null, true, gotoTaskId,
                    false);
            TaskExecutor gotoTaskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.GOTO_TASK);
            gotoTaskExecutor.execute(gotoTaskParam);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#findFlowInstanceParameter(com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter)
     */
    @Override
    public List<FlowInstanceParameter> findFlowInstanceParameter(FlowInstanceParameter example) {
        return flowInstanceParameterService.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#saveFlowInstanceParameter(com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter)
     */
    @Override
    public void saveFlowInstanceParameter(FlowInstanceParameter parameter) {
        flowInstanceParameterService.save(parameter);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#deleteFlowInstanceParameter(com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter)
     */
    @Override
    public void deleteFlowInstanceParameter(FlowInstanceParameter parameter) {
        flowInstanceParameterService.delete(parameter);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#getFlowInstance(java.lang.String)
     */
    @Override
    public FlowInstance getFlowInstance(String flowInstUuid) {
        return flowInstanceService.get(flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#getFlowInstanceByTaskInstUuid(java.lang.String)
     */
    @Override
    public FlowInstance getFlowInstanceByTaskInstUuid(String taskInstUuid) {
        return flowInstanceService.getByTaskInstUuid(taskInstUuid);
    }

    @Override
    public Map<String, Object> getFormUuidAndDataUuidByTaskInstUuid(String taskInstUuid) {
        TaskInstance t = taskService.get(taskInstUuid);
        if (t != null) {
            return ImmutableMap.of("dataUuid", t.getDataUuid(), "formUuid", t.getFormUuid());
        }
        return null;
    }

    /**
     * 根据表单数据UUID获取流程实例UUID
     *
     * @param dataUuid
     * @return
     */
    @Override
    public String getFlowInstUuidByFormDataUuid(String dataUuid) {
        return flowInstanceService.getUuidByDataUuid(dataUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#isMainFlowInstance(java.lang.String)
     */
    @Override
    public boolean isMainFlowInstance(String flowInstUuid) {
        return flowInstanceService.isMainFlowInstance(flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#getUnfinishedSubFlowInstances(java.lang.String)
     */
    @Override
    public List<FlowInstance> getUnfinishedSubFlowInstances(String flowInstUuid) {
        return flowInstanceService.getUnfinishedSubFlowInstances(flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#saveFlowInstance(com.wellsoft.pt.bpm.engine.entity.FlowInstance)
     */
    @Override
    public void saveFlowInstance(FlowInstance flowInstance) {
        flowInstanceService.save(flowInstance);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#syncSubFlowInstances(com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void syncSubFlowInstances(FlowInstance flowInstance, TaskData taskData) {
        syncSubFlowInstances(flowInstance, taskData, StringUtils.EMPTY);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#syncSubFlowInstancesByParentFlowInstUuid(java.lang.String)
     */
    @Override
    public void syncSubFlowInstancesByParentFlowInstUuid(String parentFlowInstUuid) {
        syncSubFlowInstancesByParentFlowInstUuidAndBotRuleId(parentFlowInstUuid, StringUtils.EMPTY);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#syncSubFlowInstancesByParentFlowInstUuidAndBotRuleId(java.lang.String, java.lang.String)
     */
    @Override
    public void syncSubFlowInstancesByParentFlowInstUuidAndBotRuleId(String parentFlowInstUuid, String botRuleId) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = new TaskData();
        taskData.setUserId(user.getUserId());
        taskData.setUserName(user.getUserName());
        List<FlowInstanceParameter> parameters = flowInstanceParameterService.getByFlowInstanceUuid(parentFlowInstUuid);
        for (FlowInstanceParameter parameter : parameters) {
            taskData.setCustomData(parameter.getName(), parameter.getValue());
        }
        FlowInstance flowInstance = this.getFlowInstance(parentFlowInstUuid);
        // 同步子流程数据
        syncSubFlowInstances(flowInstance, taskData, botRuleId);
    }

    /**
     * @param flowInstance
     * @param taskData
     * @param botRuleId
     */
    private void syncSubFlowInstances(FlowInstance flowInstance, TaskData taskData, String botRuleId) {
        Token token = taskData.getToken();
        if (token == null) {
            token = new Token(flowInstance, taskData);
        }
        // 子环节信息
        List<SubTaskNode> subTaskNodes = token.getFlowDelegate().getAllSubTaskNodes();
        for (SubTaskNode subTaskNode : subTaskNodes) {
            List<NewFlow> newFlows = subTaskNode.getNewFlows();
            for (NewFlow newFlow : newFlows) {
                String id = newFlow.getId();
                String syncBotRuleId = StringUtils.isNotBlank(botRuleId) ? botRuleId : newFlow.getSyncBotRuleId();
                String key = FlowConstant.SUB_FLOW.KEY_SYNC_SUB_FLOW_INST_UUIDS + Separator.UNDERLINE.getValue() + id;
                String syncSubflowInstUuids = ObjectUtils.toString(taskData.getCustomData(key), StringUtils.EMPTY);
                if (StringUtils.isNotBlank(syncBotRuleId) && StringUtils.isNotBlank(syncSubflowInstUuids)) {
                    // 子流程实例信息
                    List<String> subflowInstUuids = Arrays
                            .asList(StringUtils.split(syncSubflowInstUuids, Separator.SEMICOLON.getValue()));
                    // 运行中或正常结束的子流程才同步
                    subflowInstUuids = taskSubFlowService.filterCompletionStatesByFlowInstUuids(subflowInstUuids,
                            TaskSubFlow.STATUS_NORMAL, TaskSubFlow.STATUS_COMPLETED);
                    for (String subflowInstUuid : subflowInstUuids) {
                        FlowInstance subFlowInstance = getFlowInstance(subflowInstUuid);
                        Set<BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
                        BotFromParam botFromParam = new BotFromParam(flowInstance.getDataUuid(),
                                flowInstance.getFormUuid());
                        froms.add(botFromParam);
                        BotParam botParam = new BotParam(syncBotRuleId, froms);
                        botParam.setFroms(froms);
                        botParam.setTargetUuid(subFlowInstance.getDataUuid());
                        try {
                            botFacadeService.startBot(botParam);
                        } catch (Exception e) {
                            throw new RuntimeException("主流程实时同步子流程数据时出错！", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#getBehindFlowInstanceByFrontFlowInstanceUuid(java.lang.String)
     */
    @Override
    public List<FlowInstance> getBehindFlowInstanceByFrontFlowInstanceUuid(String flowInstUuid) {
        return taskSubFlowService.getBehindFlowInstanceByFrontFlowInstanceUuid(flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#query(java.lang.String, java.util.Map)
     */
    @Override
    public <X> List<X> query(String hql, Map<String, Object> values) {
        return this.flowInstanceService.find(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#isCompleted(java.lang.String)
     */
    @Override
    public Boolean isCompleted(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.count(COUNT_IS_COMPLETED, values) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#isCompletedByTaskInstUuid(java.lang.String)
     */
    @Override
    public Boolean isCompletedByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.count(COUNT_IS_COMPLETED_BY_TASK_INST_UUID, values) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskService#hasDraftPermission(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasDraftPermission(String userId, String flowInstUuid) {
        return aclService.hasPermission(FlowInstance.class, flowInstUuid, AclPermission.DRAFT, userId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#removeDraftPermission(java.lang.String, java.lang.String)
     */
    @Override
    public void removeDraftPermission(String userId, String flowInstUuid) {
        aclService.removePermission(FlowInstance.class, flowInstUuid, AclPermission.DRAFT, userId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#addReader(java.lang.String, java.util.List)
     */
    @Override
    public void addReader(String flowDefUuid, List<String> orgIds) {
        flowManagementService.add(ManagementType.READ, orgIds, flowDefUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#addReaderById(java.lang.String, java.util.List)
     */
    @Override
    public void addReaderById(String flowDefId, List<String> orgIds) {
        if (StringUtils.isBlank(flowDefId)) {
            return;
        }
        flowManagementService.addByFlowDefId(ManagementType.READ, orgIds, flowDefId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#removeReader(java.lang.String, java.util.List)
     */
    @Override
    public void removeReader(String flowDefUuid, List<String> orgIds) {
        flowManagementService.remove(ManagementType.READ, orgIds, flowDefUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#removeReaderById(java.lang.String, java.util.List)
     */
    @Override
    public void removeReaderById(String flowDefId, List<String> orgIds) {
        if (StringUtils.isBlank(flowDefId)) {
            return;
        }
        flowManagementService.removeByFlowDefId(ManagementType.READ, orgIds, flowDefId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowService#hasCreatePermission(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasCreatePermission(String userId, String flowDefUuid) {
        QueryInfo<FlowDefinition> aclQueryInfo = new QueryInfo<FlowDefinition>();
        aclQueryInfo.setWhereHql(HAS_CREATE_PERMISION_WHERE_HQL);
        aclQueryInfo.addQueryParams("flowDefUuid", flowDefUuid);
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(BasePermission.CREATE);
//        Set<String> orgIds = orgApiFacade.getUserOrgIds(userId);
        Set<String> orgIds = workflowOrgService.getUserRelatedIds(userId);
        List<String> sids = new ArrayList<String>();
        for (String orgId : orgIds) {
            String sid = orgId;
            if (sid.startsWith(IdPrefix.USER.getValue())) {
                continue;
            }
            sid = WorkFlowAclSid.ROLE_FLOW_CREATOR + "_" + sid;
            sids.add(sid);
        }
        if (!sids.contains(userId)) {
            sids.add(userId);
        }
        sids.add(WorkFlowAclSid.ROLE_FLOW_ALL_CREATOR.name());
        aclService.query(FlowDefinition.class, aclQueryInfo, permissions, sids);
        return aclQueryInfo.getPage().getResult().size() > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimerWorkTime> listTimerWorkTimeByFlowInstUuid(String flowInstUuid) {
        List<TimerWorkTime> timerWorkTimes = Lists.newArrayList();
        List<TaskTimer> taskTimers = taskTimerService.listByTaskInstUuid(flowInstUuid);
        taskTimers.forEach(taskTimer -> {
            timerWorkTimes.add(timerFacadeService.getTimerWorkTime(taskTimer.getTimerUuid()));
        });
        return timerWorkTimes;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getOrgVersionIdsByFlowInstUuid(String flowInstUuid, String flowDefUuid) {
        Token token = null;
        if (StringUtils.isNotBlank(flowInstUuid)) {
            FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
            token = new Token(flowInstance, new TaskData());
        } else {
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefUuid);
            token = new Token(flowDelegate, flowDelegate.getStartNode(), new TaskData());
        }
        return OrgVersionUtils.getAvailableFlowOrgVersionIds(token);
    }

    @Override
    @Transactional
    public void addRuntimeFlowAccessPermissionProvider(String flowInstUuid, String flowAccessPermissionProvider) {
        FlowInstanceParameter parameter = flowInstanceParameterService.getByFlowInstUuidAndName(flowInstUuid, "flowAccessPermissionProvider");
        if (parameter == null) {
            parameter = new FlowInstanceParameter();
            parameter.setFlowInstUuid(flowInstUuid);
            parameter.setName("flowAccessPermissionProvider");
            parameter.setValue(flowAccessPermissionProvider);
        } else {
            String provider = parameter.getValue();
            if (StringUtils.isNotBlank(provider)) {
                Set<String> providers = Sets.newHashSet(StringUtils.split(provider, Separator.SEMICOLON.getValue()));
                providers.add(flowAccessPermissionProvider);
                parameter.setValue(StringUtils.join(providers, Separator.SEMICOLON.getValue()));
            } else {
                parameter.setValue(flowAccessPermissionProvider);
            }
        }
        flowInstanceParameterService.save(parameter);
    }

}

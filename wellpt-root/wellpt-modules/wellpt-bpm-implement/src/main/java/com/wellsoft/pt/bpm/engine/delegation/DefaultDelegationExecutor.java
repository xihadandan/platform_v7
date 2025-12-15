/*
 * @(#)2015-3-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.delegation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.groovy.GroovyUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegation.service.TaskDelegationTakeBackService;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowVariables;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskExecutor;
import com.wellsoft.pt.bpm.engine.executor.TaskExecutorFactory;
import com.wellsoft.pt.bpm.engine.executor.param.DelegationParam;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.bpm.engine.util.TitleExpressionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.task.facade.TaskApiFacade;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import com.wellsoft.pt.xxljob.service.XxlJobService;
import com.xxl.job.core.well.model.TmpJobParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
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
 * 2015-3-25.1	zhulh		2015-3-25		Create
 * </pre>
 * @date 2015-3-25
 */
@Service
@Transactional
public class DefaultDelegationExecutor extends BaseServiceImpl implements DelegationExecutor {

    //    @Autowired
//    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;
    @Autowired
    private TaskApiFacade taskApiFacade;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private TaskDelegationTakeBackService takeBackService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private AclTaskService aclTaskService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FlowService flowService;
    @Autowired
    private FlowDelegationSettingsService flowDelegationSettingsService;
    @Autowired
    private XxlJobService xxlJobService;
    @Autowired
    private TaskDelegationService taskDelegationService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor#checkedAndDelegation(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public TaskDelegation checkedAndPrepareDelegation(final FlowUserSid todoUserId, final Set<String> todoUserIds,
                                                      final TaskInstance taskInstance, final FlowInstance flowInstance, final ExecutionContext executionContext) {
        if (!IdPrefix.startsUser(todoUserId.getId())) {
            return null;
        }
        // 当前任务的环节实例UUID
        String flowInstUuid = flowInstance.getUuid();
        String taskInstUuid = taskInstance.getUuid();
        TaskData taskData = null;
        if (executionContext != null) {
            taskData = executionContext.getToken().getTaskData();
        } else {
            taskData = new TaskData();
            taskData.setUserId(SpringSecurityUtils.getCurrentUserId());
            taskData.setFormUuid(taskInstance.getFormUuid());
            taskData.setDataUuid(taskInstance.getDataUuid());
        }
        // 当前用户ID
        String currentUserId = taskData.getUserId();

        // 获取配置的职务代理人配置列表
        List<FlowDelegationSettings> dutyAgents = getDelegationSettings(currentUserId, taskInstUuid, todoUserIds,
                taskData, ModuleID.WORKFLOW.getValue(), flowInstance.getSystem());
        if (dutyAgents.isEmpty()) {
            return null;
        }

        // 流程定义ID
        String flowDefId = flowInstance.getId();
        // 流程定义分类
        String cagetory = flowInstance.getFlowDefinition().getCategory();
        // 委托人、受托人、内容信息及条件判断过滤
        List<FlowDelegationSettings> returnDutyAgents = filterDelegationSettings(todoUserId, flowDefId, cagetory,
                dutyAgents, flowInstance, taskInstance, taskData);
        if (returnDutyAgents.isEmpty()) {
            return null;
        }

        // 返回受拖人ID列表
        return prepareDelegation(returnDutyAgents, taskInstUuid, flowInstUuid);
    }

    /**
     * @param todoUserIds
     * @param taskData
     * @param value
     * @return
     */
    private List<FlowDelegationSettings> getDelegationSettings(String currentUserId, String taskInstUuid,
                                                               Set<String> todoUserIds, TaskData taskData, String value, String system) {
        // 职务代理人信息存在上下文，直接从上下文取数据
        Map<String, List<FlowDelegationSettings>> delegationSettingsMap = FlowDelegationSettingsContextHolder
                .getFlowDelegationSettings();
        if (delegationSettingsMap == null) {
            delegationSettingsMap = new HashMap<String, List<FlowDelegationSettings>>();
        }
        if (delegationSettingsMap.containsKey(taskInstUuid)) {
            return delegationSettingsMap.get(taskInstUuid);
        }

        // 获取当前有效的职务代理人信息
        List<String> userIds = new ArrayList<String>();
        userIds.addAll(todoUserIds);
        // 当前用户ID不进行工作委托
        userIds.remove(currentUserId);
        if (userIds.isEmpty()) {
            return new ArrayList<FlowDelegationSettings>(0);
        }

        List<FlowDelegationSettings> dutyAgents = flowDelegationSettingsService.getByUserIdsAndSystem(userIds, system);
        Collections.sort(dutyAgents, IdEntityComparators.MODIFY_TIME_DESC);

        // 代理人信息放入上下文环境
        delegationSettingsMap.put(taskInstUuid, dutyAgents);
        FlowDelegationSettingsContextHolder.setFlowDelegationSettings(delegationSettingsMap);

        return dutyAgents;
    }

    /**
     * 委托人、内容信息及条件判断过滤
     *
     * @param todoUserId
     * @param flowDefId
     * @param category
     * @param delegationSettingsList
     * @param flowInstance
     * @param taskData
     * @return
     */
    private List<FlowDelegationSettings> filterDelegationSettings(final FlowUserSid todoUserSid, String flowDefId,
                                                                  String category, List<FlowDelegationSettings> delegationSettingsList, FlowInstance flowInstance,
                                                                  TaskInstance taskInstance, TaskData taskData) {
        List<FlowDelegationSettings> returnDelegationSettings = new ArrayList<FlowDelegationSettings>();
        String todoUserId = todoUserSid.getId();
//        // 检查的所有流程
//        String checkedAllFlow = Separator.SEMICOLON.getValue() + WorkFlowVariables.FLOW_ALL.getName()
//                + Separator.SEMICOLON.getValue();
//        // 检查的流程分类
//        String checkedFlowCategory = Separator.SEMICOLON.getValue() + WorkFlowVariables.FLOW_CATEGORY_PREFIX.getName()
//                + category + Separator.SEMICOLON.getValue();
//        // 检查的流程定义ID
//        String checkedFlowDefId = Separator.SEMICOLON.getValue() + flowDefId + Separator.SEMICOLON.getValue();

        for (FlowDelegationSettings delegationSettings : delegationSettingsList) {
            // 1、委托人不一致，忽略掉
            String consignor = delegationSettings.getConsignor();
            if (!StringUtils.equals(todoUserId, consignor)) {
                continue;
            }

            // 2、受托人非空判断
            String trustee = delegationSettings.getTrustee();
            if (StringUtils.isBlank(trustee)) {
                continue;
            }

            // 3、如果委托内容为空则表示委托所有工作
            DelegationContent delegationContent = new DelegationContent(delegationSettings.getContent());
            if (!delegationContent.matchFlow(WorkFlowVariables.FLOW_CATEGORY_PREFIX.getName() + category, flowDefId, taskInstance.getId())) {
                continue;
            }
//            String dutyContent = delegationSettings.getContent();
//            if (StringUtils.isNotBlank(dutyContent)) {
//                // 根据委托内容获取相应的受托人
//                String compareContent = dutyContent;
//                if (!compareContent.startsWith(Separator.SEMICOLON.getValue())) {
//                    compareContent = Separator.SEMICOLON.getValue() + dutyContent;
//                }
//                if (!compareContent.endsWith(Separator.SEMICOLON.getValue())) {
//                    compareContent = compareContent + Separator.SEMICOLON.getValue();
//                }
//                if (!(compareContent.contains(checkedAllFlow) || compareContent.contains(checkedFlowCategory) || compareContent
//                        .contains(checkedFlowDefId))) {
//                    continue;
//                }
//            }

            // 4、委托时间有效性判断
            if (!checkDelegationTime(delegationSettings)) {
                continue;
            }

            // 5、委托身份
            if (!delegationContent.matchJobIdentities(todoUserSid)) {
                continue;
            }

            // 6、委托条件
            String conditionJson = delegationSettings.getConditionJson();
            if (!evalateDelegationCondition(conditionJson, flowInstance, taskInstance, taskData)) {
                continue;
            }

            // 添加职务代理人信息，只需取最新修改的一条数据
            returnDelegationSettings.add(delegationSettings);
            break;
        }

        return returnDelegationSettings;
    }

    /**
     * @param conditionJson
     * @param flowInstance
     * @param taskInstance
     * @param taskData
     * @return
     */
    private boolean evalateDelegationCondition(String conditionJson, FlowInstance flowInstance, TaskInstance taskInstance, TaskData taskData) {
        if (StringUtils.isBlank(conditionJson)) {
            return true;
        }

        FlowDelegationConditionConfig conditionConfig = JsonUtils.json2Object(conditionJson, FlowDelegationConditionConfig.class);
        boolean evalateResult = false;
        boolean matchAll = FlowDelegationConditionConfig.MATCH_ALL.equals(conditionConfig.getMatch());
        List<FlowDelegationConditionConfig.FlowDelegationCondition> conditions = conditionConfig.getConditions();
        if (matchAll && CollectionUtils.isNotEmpty(conditions)) {
            evalateResult = true;
        }
        for (FlowDelegationConditionConfig.FlowDelegationCondition condition : conditions) {
            boolean result = evalateDelegationCondition(condition, flowInstance, taskInstance, taskData);
            if (matchAll) {
                if (!result) {
                    evalateResult = false;
                    break;
                }
            } else {
                if (result) {
                    evalateResult = true;
                    break;
                }
            }
        }
        return evalateResult;
    }

    /**
     * @param condition
     * @param flowInstance
     * @param taskInstance
     * @param taskData
     * @return
     */
    private boolean evalateDelegationCondition(FlowDelegationConditionConfig.FlowDelegationCondition condition,
                                               FlowInstance flowInstance, TaskInstance taskInstance, TaskData taskData) {
        Map<String, Object> properties = Maps.newHashMap();
        String conditionType = condition.getType();
        DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(taskInstance.getFormUuid(), taskInstance.getDataUuid());
        }

        // 表单变量
        Map<String, Object> dataOfMainform = Maps.newHashMap(dyFormData.getFormDataOfMainform());
        List<String> fieldNames = dyFormData.doGetFieldNames();
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            fieldNames.forEach(fieldName -> {
                if (!dataOfMainform.containsKey(fieldName)) {
                    dataOfMainform.put(fieldName, null);
                }
            });
        }
        properties.put("dyform", dataOfMainform);
        List<DyformFieldDefinition> dyformFieldDefinitions = dyFormFacade.getFieldDefinitions(taskInstance.getFormUuid());
        Map<String, DyformFieldDefinition> dyformFieldDefinitionMap = ConvertUtils.convertElementToMap(dyformFieldDefinitions, "name");
        for (Map.Entry<String, Object> entry : dataOfMainform.entrySet()) {
            DyformFieldDefinition dyformFieldDefinition = dyformFieldDefinitionMap.get(entry.getKey());
            if (dyformFieldDefinition != null) {
                properties.put("表单字段_" + StringUtils.trim(dyformFieldDefinition.getDisplayName()), entry.getValue());
            }
        }

        // 流程变量
        properties.putAll(TitleExpressionUtils.getFlowCommonVariables(flowInstance.getStartUserId(), flowInstance.getFlowDefinition(), flowInstance));
        properties.put("当前环节名称", taskInstance.getName());
        properties.put("当前环节ID", taskInstance.getId());
        properties.put("taskName", taskInstance.getName());
        properties.put("taskId", taskInstance.getId());

        boolean isFormField = FlowDelegationConditionConfig.FlowDelegationCondition.TYPE_FORM_FIELD.equals(conditionType);
        String field = isFormField ? ("dyform." + condition.getFormField()) : condition.getWorkflowField();
        Object fieldValue = properties.get(field);
        if (isFormField) {
            fieldValue = dataOfMainform.get(condition.getFormField());
        }
        String operator = condition.getOperator();
        Object conditionValue = calculateConditionValue(condition.getValue(), properties);
        StringBuilder script = new StringBuilder("return ");
        switch (operator) {
            case "contains":
                String containValue = Objects.toString(fieldValue, StringUtils.EMPTY);
                script.append(StringUtils.isNotBlank(containValue) && StringUtils.contains(containValue, Objects.toString(conditionValue, StringUtils.EMPTY)));
                break;
            case "not contains":
                String notContainValue = Objects.toString(fieldValue, StringUtils.EMPTY);
                script.append(StringUtils.isNotBlank(notContainValue) && !StringUtils.contains(notContainValue, Objects.toString(conditionValue, StringUtils.EMPTY)));
                break;
            default:
                String valueString = Objects.toString(conditionValue, StringUtils.EMPTY);
                if (NumberUtils.isNumber(valueString)) {
                    if (NumberUtils.isNumber(Objects.toString(fieldValue, StringUtils.EMPTY))) {
                        if (isFormField) {
                            dataOfMainform.put(condition.getFormField(), Double.valueOf(fieldValue.toString()));
                        } else {
                            properties.put(field, Double.valueOf(fieldValue.toString()));
                        }
                    }
                    if (StringUtils.equals("==", operator) &&
                            NumberUtils.isNumber(Objects.toString(fieldValue, StringUtils.EMPTY))) {
                        script.append("Double.valueOf(" + conditionValue + ").equals(" + field + ")");
                    } else {
                        script.append(field).append(" ").append(operator).append(" ").append(conditionValue);
                    }
                } else {
                    if (StringUtils.equals("==", operator)) {
                        properties.put("conditionValue", conditionValue);
                        if (conditionValue != null) {
                            script.append("conditionValue.equals(" + field + ")");
                        } else {
                            script.append(field).append(" ").append(operator).append(" ").append("conditionValue");
                        }
                    } else if ((StringUtils.startsWith(valueString, "\"") && StringUtils.endsWith(valueString, "\""))) {
                        script.append(field).append(" ").append(operator).append(" ").append(valueString);
                    } else {
                        script.append(field).append(" ").append(operator).append(" \"").append(valueString).append("\"");
                    }
                }
                break;
        }

        Object result = GroovyUtils.run(script.toString(), properties);
        return Boolean.TRUE.equals(result);
    }

    /**
     * @param value
     * @param properties
     * @return
     */
    private Object calculateConditionValue(String value, Map<String, Object> properties) {
        if (StringUtils.isBlank(value) || !StringUtils.contains(value, "$")) {
            return value;
        }

        // 只有一个变量的情况
        String variable = StringUtils.replace(value, "${", StringUtils.EMPTY);
        variable = StringUtils.replace(variable, "}", StringUtils.EMPTY);
        variable = StringUtils.replace(variable, "表单字段.", "表单字段_");
        if (properties.containsKey(variable)) {
            return properties.get(variable);
        }

        // 变量表达式的情况
        Object result = value;
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            // 按变量重置
            String expression = StringUtils.replace(value, "表单字段.", "表单字段_");
            result = templateEngine.process(expression, properties);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 返回最新修改的工作委托配置信息
     *
     * @param returnDelegationSettings
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    private TaskDelegation prepareDelegation(List<FlowDelegationSettings> returnDelegationSettings,
                                             String taskInstUuid, String flowInstUuid) {
        //循环没有按照顺序，导致取到的委托人错误
        if (returnDelegationSettings != null && returnDelegationSettings.size() > 0) {
            FlowDelegationSettings delegationSettings = returnDelegationSettings.get(0);
//		for (FlowDelegationSettings delegationSettings : returnDelegationSettings) {
            // 保存任务委托实例信息
            TaskDelegation taskDelegation = new TaskDelegation();
            taskDelegation.setTaskInstUuid(taskInstUuid);
            taskDelegation.setFlowInstUuid(flowInstUuid);
            taskDelegation.setDelegationSettingsUuid(delegationSettings.getUuid());
            taskDelegation.setConsignorName(delegationSettings.getConsignorName());
            taskDelegation.setConsignor(delegationSettings.getConsignor());
            taskDelegation.setTrusteeName(delegationSettings.getTrusteeName());
            taskDelegation.setTrustee(delegationSettings.getTrustee());
            taskDelegation.setDueToTakeBackWork(delegationSettings.getDueToTakeBackWork());
            taskDelegation.setDeactiveToTakeBackWork(delegationSettings.getDeactiveToTakeBackWork());
            taskDelegation.setCompletionState(TaskDelegation.STATUS_NORMAL);
            taskDelegation.setFromTime(delegationSettings.getFromTime());
            taskDelegation.setToTime(delegationSettings.getToTime());
            return taskDelegation;
//		}
        }

        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor#delegationCurrentWork(com.wellsoft.pt.org.entity.DutyAgent)
     */
    @Override
    public void delegationCurrentWork(FlowDelegationSettings delegationSettings) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            doDelegationCurrentWork(delegationSettings);
        } else {
            String system = RequestSystemContextPathResolver.system();
            scheduledExecutorService.execute(() -> {
                try {
                    RequestSystemContextPathResolver.setSystem(system);
                    IgnoreLoginUtils.login(userDetails);
                    synchronized (userDetails) {
                        delegationCurrentWork(delegationSettings);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    IgnoreLoginUtils.logout();
                    RequestSystemContextPathResolver.clear();
                }
            });
        }
    }

    /**
     * @param delegationSettings
     */
    public void doDelegationCurrentWork(FlowDelegationSettings delegationSettings) {
        // 1、委托当前工作判断
        Boolean includeCurrentWork = delegationSettings.getIncludeCurrentWork();
        if (!Boolean.TRUE.equals(includeCurrentWork)) {
            return;
        }
        Integer status = delegationSettings.getStatus();
        if (!FlowDelegationSettings.STATUS_ACTIVE.equals(status)) {
            return;
        }

        // 2、委托时间有效性判断
        if (!checkDelegationTime(delegationSettings)) {
            // 启动委托当前工作跟踪任务
            startDelegationCurrentWorkDueHandlerJobIfRequire(delegationSettings);
            return;
        }

        // 委托当前工作
        // 3、获取委托人当前的待办工作
        List<FlowDelegationSettings> delegationSettingsList = new ArrayList<FlowDelegationSettings>();
        delegationSettingsList.add(delegationSettings);
        String consignor = delegationSettings.getConsignor();
        UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
        List<String> consignorSids = null;
        if (StringUtils.equals(consignor, currentUser.getUserId())) {
            consignorSids = PermissionGranularityUtils.getSids(currentUser);
        } else {
            consignorSids = PermissionGranularityUtils.getUserSids(consignor);
        }
        List<TaskIdentity> taskIdentities = identityService.getTodoSubmitByUserSids(consignorSids);
        for (TaskIdentity currentTaskIdentity : taskIdentities) {
            String taskInstUuid = currentTaskIdentity.getTaskInstUuid();
            TaskInstance taskInstance = taskService.getTask(taskInstUuid);
            if (taskInstance == null) {
                continue;
            }
            // 环节状态判断
            int suspensionState = taskInstance.getSuspensionState();
            if (!WorkFlowSuspensionState.Normal.equals(suspensionState)) {
                continue;
            }
            // 结束时间判断
            Date endTime = taskInstance.getEndTime();
            if (endTime != null) {
                continue;
            }

            // 委托待办验证是否允许二次委托
            if (!isAllowSecondaryDelegation(currentTaskIdentity)) {
                continue;
            }

            FlowInstance flowInstance = flowService.getFlowInstanceByTaskInstUuid(taskInstUuid);
            if (flowInstance == null) {
                continue;
            }
            // 流程定义ID
            String flowDefId = flowInstance.getId();
            // 流程定义分类
            String cagetory = flowInstance.getFlowDefinition().getCategory();
            // 任务数据
            String userId = SpringSecurityUtils.getCurrentUserId();
            String userName = SpringSecurityUtils.getCurrentUserName();
            if (!StringUtils.equals(consignor, userId)) {
                userId = consignor;
//                userName = orgApiFacade.getUserNameById(userId);
                userName = workflowOrgService.getNameById(userId);
            }
            TaskData taskData = new TaskData();
            taskData.setUserId(userId);
            taskData.setUserName(userName);
            taskData.setFormUuid(taskInstance.getFormUuid());
            taskData.setDataUuid(taskInstance.getDataUuid());
            taskData.put("publishDelegationEvent", true);
            // 委托人、受托人、内容信息及条件判断过滤
            FlowUserSid consignorUserSid = null;
            if (IdPrefix.startsUser(currentTaskIdentity.getUserId())) {
                consignorUserSid = new FlowUserSid(consignor, delegationSettings.getConsignorName());
            } else {
                Token token = new Token(taskInstance, taskData);
                consignorUserSid = new FlowUserSidInOrg(consignor, consignor, Lists.newArrayList(currentTaskIdentity.getUserId()),
                        OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
            }
            List<FlowDelegationSettings> returnDelegationSettings = filterDelegationSettings(consignorUserSid, flowDefId,
                    cagetory, delegationSettingsList, flowInstance, taskInstance, taskData);
            if (returnDelegationSettings.isEmpty()) {
                continue;
            }
            TaskDelegation taskDelegation = prepareDelegation(returnDelegationSettings, taskInstUuid,
                    flowInstance.getUuid());

            List<String> trustees = Lists.newArrayList(StringUtils.split(taskDelegation.getTrustee(),
                    Separator.SEMICOLON.getValue()));
            if (CollectionUtils.isEmpty(trustees)) {
                continue;
            }

            // 已经委托未办理的不再委托
            List<TaskDelegation> currentDelegations = taskDelegationService.listRunningByConsignorAndTaskInstUuid(userId, taskInstUuid);
            currentDelegations.forEach(currentDelegation -> {
                if (StringUtils.equals(delegationSettings.getUuid(), currentDelegation.getDelegationSettingsUuid())) {
                    trustees.removeAll(Arrays.asList(StringUtils.split(currentDelegation.getTrustee(),
                            Separator.SEMICOLON.getValue())));
                }
            });

            if (CollectionUtils.isEmpty(trustees)) {
                continue;
            }

            // 委托用户粒度大于人员的待办
            if (consignorUserSid instanceof FlowUserSidInOrg) {
                TaskIdentity taskIdentity = new TaskIdentity();
                taskIdentity.setTaskInstUuid(taskInstance.getUuid());
                taskIdentity.setTodoType(WorkFlowTodoType.Submit);
                taskIdentity.setUserId(consignor);
                taskIdentity.setIdentityId(consignorUserSid.getIdentityId());
                taskIdentity.setIdentityIdPath(consignorUserSid.getIdentityIdPath());
                identityService.addTodo(taskIdentity);
                aclTaskService.addDonePermission(consignor, consignorSids, taskInstance.getUuid());
                identityService.flushSession();
                currentTaskIdentity = taskIdentity;
            }

            // 工作委托处理
            doDelegationWork(taskInstance, currentTaskIdentity, taskDelegation, Lists.newArrayList(taskDelegation.getConsignor()), taskData);
        }
    }

    /**
     * @param taskIdentity
     * @return
     */
    private boolean isAllowSecondaryDelegation(TaskIdentity taskIdentity) {
        if (StringUtils.isNotBlank(taskIdentity.getSourceTaskIdentityUuid()) && WorkFlowTodoType.Delegation.equals(taskIdentity.getTodoType())) {
            TaskDelegation taskDelegation = taskDelegationService.getByTaskIdentityUuid(taskIdentity.getUuid());
            if (taskDelegation != null) {
                FlowDelegationSettings flowDelegationSettings = flowDelegationSettingsService.getOne(taskDelegation.getDelegationSettingsUuid());
                if (flowDelegationSettings != null && BooleanUtils.isNotTrue(flowDelegationSettings.getAllowSecondaryDelegation())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param delegationSettings
     */
    private void startDelegationCurrentWorkDueHandlerJobIfRequire(FlowDelegationSettings delegationSettings) {
        Date fromDate = delegationSettings.getFromTime();
        Date toDate = delegationSettings.getToTime();
        // 开始时间与结束时间不为空，开始时间要小于结束时间
        if (fromDate != null && toDate != null) {
            if (fromDate.after(toDate)) {
                return;
            }
        }
        Date currentDate = Calendar.getInstance().getTime();
        // 开始时间不为空，开始时间要小于当前时间
        if (fromDate != null) {
            if (currentDate.before(fromDate)) {
                //xxlJob执行需要的参数
                ExecutionParam executionParam = new ExecutionParam()
                        .setTenantId(SpringSecurityUtils.getCurrentTenantId())
                        .setUserId(delegationSettings.getCreator())
                        .putKeyVal("delegationSettingsUuid", delegationSettings.getUuid());
                TmpJobParam.Builder builder = TmpJobParam.toBuilder()
                        .setJobDesc("工作委托设置_到期委托当前工作_" + delegationSettings.getUuid())
                        .setExecutorHandler(JobHandlerName.Temp.TaskDelegationCurrentWorkDueHandlerJob)
                        .addExecutionTimeParams(fromDate, executionParam.toJson());
                xxlJobService.addTmpStart(builder.build());
                return;
            }
        }
    }

    /**
     * 委托时间有效性判断
     *
     * @param delegationSettings
     */
    private boolean checkDelegationTime(FlowDelegationSettings delegationSettings) {
        Date fromDate = delegationSettings.getFromTime();
        Date toDate = delegationSettings.getToTime();
        // 开始时间与结束时间不为空，开始时间要小于结束时间
        if (fromDate != null && toDate != null) {
            if (fromDate.after(toDate)) {
                return false;
            }
        }
        Date currentDate = Calendar.getInstance().getTime();
        // 开始时间不为空，开始时间要小于当前时间
        if (fromDate != null) {
            if (currentDate.before(fromDate)) {
                return false;
            }
        }
        // 结束时间不为空，结束时间要大于当前时间
        if (toDate != null) {
            if (currentDate.after(toDate)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 工作委托处理
     *
     * @param taskInstance
     * @param taskIdentity
     * @param taskDelegation
     */
    public void delegationWork(TaskInstance taskInstance, TaskIdentity taskIdentity, TaskDelegation taskDelegation) {
        // 委托人列表
        List<String> consignors = Lists.newArrayList(taskDelegation.getConsignor());
        doDelegationWork(taskInstance, taskIdentity, taskDelegation, consignors, null);
    }

    /**
     * @param taskInstance
     * @param taskIdentity
     * @param taskDelegation
     * @param consignors
     */
    private void doDelegationWork(TaskInstance taskInstance, TaskIdentity taskIdentity, TaskDelegation taskDelegation,
                                  List<String> consignors, TaskData delegationTaskData) {
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.DELEGATION);
        TaskData taskData = delegationTaskData;
        if (taskData == null) {
            taskData = new TaskData();
        }
        taskData.setUserId(taskDelegation.getConsignor());
        taskData.setUserName(taskDelegation.getConsignorName());
        taskData.setFormUuid(taskInstance.getFormUuid());
        taskData.setDataUuid(taskInstance.getDataUuid());

        String userId = taskData.getUserId();
        String key = taskInstance.getUuid() + userId;
        taskData.setTaskInstUuid(taskInstance.getUuid());
        taskData.setOpinionLabel(key, StringUtils.EMPTY);
        taskData.setOpinionValue(key, StringUtils.EMPTY);
        taskData.setOpinionText(key, "工作委托");
        taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.DELEGATION));
        taskData.setActionType(key, WorkFlowOperation.DELEGATION);
        Param param = new DelegationParam(taskDelegation, taskInstance, taskData, taskIdentity, true);
        taskExecutor.execute(param);

        // 保存工作委托信息及启动跟踪任务
        this.taskDelegationService.save(taskDelegation);

        // 判断是否允许进行二次委托
        FlowDelegationSettings delegationSettingsEntity = null;
        String delegationSettingsUuid = taskDelegation.getDelegationSettingsUuid();
        Map<String, List<FlowDelegationSettings>> flowDelegationSettings = FlowDelegationSettingsContextHolder.getFlowDelegationSettings();
        if (MapUtils.isNotEmpty(flowDelegationSettings)) {
            List<FlowDelegationSettings> delegationSettings = flowDelegationSettings.get(taskInstance.getUuid());
            if (CollectionUtils.isNotEmpty(delegationSettings)) {
                delegationSettingsEntity = delegationSettings.stream().filter(delegationSetting -> StringUtils.equals(delegationSetting.getUuid(), delegationSettingsUuid)).findFirst().orElse(null);
            }
        }
        if (delegationSettingsEntity == null && StringUtils.isNotBlank(delegationSettingsUuid)) {
            delegationSettingsEntity = flowDelegationSettingsService.getOne(delegationSettingsUuid);
        }
        // 是否允许二次委托
        if (BooleanUtils.isNotTrue(delegationSettingsEntity.getAllowSecondaryDelegation())) {
            return;
        }

        // 二次委托
        FlowDelegationSettingsContextHolder.remove();
        String indirectUserId = taskDelegation.getTrustee();
        Set<String> indirectUsers = Sets.newHashSet();
        indirectUsers.add(indirectUserId);
        FlowUserSid indirectUserSid = new FlowUserSid(indirectUserId, taskDelegation.getTrusteeName());
        TaskDelegation trusteeTaskDelegation = checkedAndPrepareDelegation(indirectUserSid, indirectUsers, taskInstance,
                taskInstance.getFlowInstance(), null);
        // 避免相互委托
        if (trusteeTaskDelegation != null && !consignors.contains(trusteeTaskDelegation.getTrustee())) {
            List<TaskIdentity> trusteeTaskIdentities = identityService.getTodoByTaskInstUuidAndUserIdAndTodoType(
                    taskInstance.getUuid(), indirectUserId, WorkFlowTodoType.Delegation);
            if (CollectionUtils.isNotEmpty(trusteeTaskIdentities)) {
                consignors.add(trusteeTaskDelegation.getConsignor());
                doDelegationWork(taskInstance, trusteeTaskIdentities.get(0), trusteeTaskDelegation, consignors, null);
            }
        }
        FlowDelegationSettingsContextHolder.remove();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor#deactiveToTakeBack(com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings)
     */
    @Override
    public void deactiveToTakeBack(FlowDelegationSettings delegationSettings) {
        takeBackService.deactiveToTakeBack(delegationSettings.getUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor#takeBackCounterSignDelegation(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    public void takeBackCounterSignDelegation(TaskIdentity taskIdentity) {
        takeBackService.takeBackCounterSignDelegation(taskIdentity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor#completeDelegation(com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void completeDelegation(ExecutionContext executionContext, boolean taskComplete) {
        Token token = executionContext.getToken();
        TaskData taskData = token.getTaskData();
        String taskInstUuid = token.getTask().getUuid();
        String userId = taskData.getUserId();
        String key = taskInstUuid + userId;
        String actionType = taskData.getActionType(key);
        taskDelegationService.completeByUserIdAndActionType(userId, actionType, taskInstUuid, taskComplete);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor#cancelDelegationByTaskInstUuid(java.lang.String)
     */
    @Override
    public void cancelDelegationByTaskInstUuid(String taskInstUuid) {
        taskDelegationService.cancelByTaskInstUuid(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor#cancelDelegationByTaskIdentityUuid(java.lang.String)
     */
    @Override
    public void cancelDelegationByTaskIdentityUuid(String taskIdentityUuid) {
        taskDelegationService.cancelByTaskIdentityUuid(taskIdentityUuid);
    }

    @Override
    @Transactional
    public boolean delegationWorkOfUserInSid(List<FlowUserSid> userInSids, TaskInstance taskInstance, FlowInstance flowInstance,
                                             Map<String, TaskDelegation> userTaskDelegationMap, ExecutionContext executionContext) {
        FlowDelegationSettingsContextHolder.remove();
        List<String> orgIds = userInSids.stream().map(FlowUserSid::getId).collect(Collectors.toList());
        String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(executionContext.getToken());
        Map<String, String> userMap = workflowOrgService.getUsersByIds(orgIds, orgVersionIds);
        Set<String> userIds = userMap.keySet();
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        boolean hasDelegation = false;
        for (Map.Entry<String, String> entry : userMap.entrySet()) {
            String userId = entry.getKey();
            // 设置职务代理人，如果当前用户等于待办人员，则忽略
            TaskDelegation userTaskDelegation = userTaskDelegationMap.get(userId);
            if (StringUtils.equals(userId, currentUserId) || userTaskDelegation != null) {
                continue;
            }
            FlowUserSidInOrg userSid = new FlowUserSidInOrg(userId, userId, orgIds, orgVersionIds);
            TaskDelegation taskDelegation = checkedAndPrepareDelegation(userSid, userIds, taskInstance, flowInstance, executionContext);
            if (taskDelegation == null) {
                continue;
            }

            TaskIdentity taskIdentity = new TaskIdentity();
            taskIdentity.setTaskInstUuid(taskInstance.getUuid());
            taskIdentity.setTodoType(WorkFlowTodoType.Submit);
            taskIdentity.setUserId(userId);
            taskIdentity.setIdentityId(userSid.getIdentityId());
            taskIdentity.setIdentityIdPath(userSid.getIdentityIdPath());
            identityService.addTodo(taskIdentity);
            aclTaskService.addDonePermission(userId, PermissionGranularityUtils.getUserSids(userId), taskInstance.getUuid());

            identityService.flushSession();
            delegationWork(taskInstance, taskIdentity, taskDelegation);
            userTaskDelegationMap.put(userId, taskDelegation);
            hasDelegation = true;
        }
        FlowDelegationSettingsContextHolder.remove();
        return hasDelegation;
    }

    /**
     * Description: 如何描述该类
     *
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2015-3-30.1	zhulh		2015-3-30		Create
     * </pre>
     * @date 2015-3-30
     */
    public static final class FlowDelegationSettingsContextHolder {
        private static final ThreadLocal<Map<String, List<FlowDelegationSettings>>> delegationSettingsContextHolder = new NamedThreadLocal<Map<String, List<FlowDelegationSettings>>>(
                "delegation settings context holder");

        /**
         *
         */
        public static void remove() {
            delegationSettingsContextHolder.remove();
        }

        /**
         * Description how to use this method
         */
        public static Map<String, List<FlowDelegationSettings>> getFlowDelegationSettings() {
            return delegationSettingsContextHolder.get();
        }

        /**
         * Description how to use this method
         */
        public static void setFlowDelegationSettings(Map<String, List<FlowDelegationSettings>> delegationSettings) {
            delegationSettingsContextHolder.set(delegationSettings);
        }

    }

    /**
     *
     */
    private static final class DelegationContent extends BaseObject {

        private static final long serialVersionUID = 8007935248380543457L;

        private String content;

        // all全部流程、flow指定流程、task指定环节
        private String type;

        private List<String> values = Lists.newArrayList();

        /// 委托身份
        private List<String> jobIdentities = Lists.newArrayList();

        /**
         * @param content
         */
        public DelegationContent(String content) {
            this.content = content;
            this.parseContent();
        }

        /**
         *
         */
        private void parseContent() {
            if (StringUtils.isBlank(this.content)) {
                this.values.add(WorkFlowVariables.FLOW_ALL.getName());
                this.type = "all";
            } else if (StringUtils.startsWith(this.content, "{")) {
                JSONObject jsonObject = new JSONObject(this.content);
                if (jsonObject.has("values")) {
                    JSONArray valuesArray = jsonObject.getJSONArray("values");
                    for (int index = 0; index < valuesArray.length(); index++) {
                        this.values.add(valuesArray.getString(index));
                    }
                }
                if (jsonObject.has("jobIdentities")) {
                    JSONArray jobIdentityArray = jsonObject.getJSONArray("jobIdentities");
                    for (int index = 0; index < jobIdentityArray.length(); index++) {
                        this.jobIdentities.add(jobIdentityArray.getString(index));
                    }
                }
                if (jsonObject.has("type")) {
                    this.type = jsonObject.getString("type");
                }
            } else {
                this.values.addAll(Arrays.asList(StringUtils.split(this.content, Separator.SEMICOLON.getValue())));
                this.type = "all";
            }
        }

        /**
         * @param categoryUuid
         * @param flowDefId
         * @param taskId
         * @return
         */
        public boolean matchFlow(String categoryUuid, String flowDefId, String taskId) {
            if (this.values.contains(WorkFlowVariables.FLOW_ALL.getName())) {
                return true;
            }
            if (StringUtils.isBlank(this.type)) {
                return false;
            }

            boolean matchFlow = false;
            switch (this.type) {
                case "all":
                    matchFlow = true;
                    break;
                case "flow":
                    matchFlow = this.values.contains(categoryUuid) || this.values.contains(flowDefId);
                    break;
                case "task":
                    matchFlow = this.values.contains(flowDefId + Separator.COLON.getValue() + taskId);
                    break;
                default:
                    break;
            }
            return matchFlow;
        }

        /**
         * @param todoUserSid
         * @return
         */
        public boolean matchJobIdentities(FlowUserSid todoUserSid) {
            if (CollectionUtils.isEmpty(this.jobIdentities) || this.jobIdentities.contains("all")) {
                return true;
            }

            List<OrgUserJobDto> userJobDtos = todoUserSid.getOrgUserJobDtos();
            if (CollectionUtils.isEmpty(userJobDtos)) {
                return true;
            }

            List<String> userJobIdentities = userJobDtos.stream().map(OrgUserJobDto::getJobId).collect(Collectors.toList());
            return CollectionUtils.containsAny(userJobIdentities, this.jobIdentities);
        }

    }

    private static final class FlowUserSidInOrg extends FlowUserSid {
        private List<String> orgIds;
        private String[] orgVersionIds;

        public FlowUserSidInOrg(String id, String name, List<String> orgIds, String[] orgVersionIds) {
            super(id, name);
            this.orgIds = orgIds;
            this.orgVersionIds = orgVersionIds;
        }

        @Override
        public List<OrgUserJobDto> getOrgUserJobDtos() {
            List<OrgUserJobDto> orgUserJobDtos = super.getOrgUserJobDtos();
            if (CollectionUtils.isNotEmpty(orgUserJobDtos) || CollectionUtils.isEmpty(orgIds)) {
                return orgUserJobDtos;
            }
            WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
            orgUserJobDtos = workflowOrgService.listUserJobIdentity(this.getId(), this.orgVersionIds);
            orgUserJobDtos = orgUserJobDtos.stream().filter(orgUserJobDto -> {
                String jobIdPath = orgUserJobDto.getJobIdPath();
                if (StringUtils.isBlank(jobIdPath)) {
                    return false;
                }
                List<String> jobIds = Arrays.asList(StringUtils.split(jobIdPath, Separator.SLASH.getValue()));
                return CollectionUtils.containsAny(jobIds, orgIds);
            }).collect(Collectors.toList());
            super.setOrgUserJobDtos(orgUserJobDtos);
            return orgUserJobDtos;
        }
    }

}

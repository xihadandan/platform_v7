/*
 * @(#)11/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.bpm.engine.dao.WfFlowDefinitionUserDao;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.WfFlowDefinitionUserEntity;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.WfFlowDefinitionUserService;
import com.wellsoft.pt.bpm.engine.support.FlowDefinitionUserModifyParams;
import com.wellsoft.pt.bpm.engine.support.TaskUserExpressionConfig;
import com.wellsoft.pt.bpm.engine.support.TaskUserExpressionConfigJson;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/29/24.1	    zhulh		11/29/24		    Create
 * </pre>
 * @date 11/29/24
 */
@Service
public class WfFlowDefinitionUserServiceImpl extends AbstractJpaServiceImpl<WfFlowDefinitionUserEntity, WfFlowDefinitionUserDao, Long>
        implements WfFlowDefinitionUserService {

    @Autowired
    private WorkflowOrgService workflowOrgService;

    /**
     * @param flowDefinition
     * @param flowElement
     */
    @Override
    @Transactional
    public void sync(FlowDefinition flowDefinition, FlowElement flowElement) {
        String flowDefUuid = flowDefinition.getUuid();
        this.deleteByFlowDefUuid(flowDefUuid);

        List<WfFlowDefinitionUserEntity> entities = Lists.newArrayList();
        // 流程属性
        entities.addAll(getDefinitionUsersOfFlow(flowDefUuid, flowElement));
        // 计时器
        entities.addAll(getDefinitionUsersOfTimer(flowDefUuid, flowElement));
        // 环节属性
        entities.addAll(getDefinitionUsersOfTask(flowDefUuid, flowElement));
        // 消息分发
        entities.addAll(getDefinitionUsersOfMessage(flowDefUuid, flowElement));

        this.dao.saveAll(entities);
    }

    /**
     * @param flowDefUuid
     * @param flowElement
     * @return
     */
    private List<WfFlowDefinitionUserEntity> getDefinitionUsersOfFlow(String flowDefUuid, FlowElement flowElement) {
        List<WfFlowDefinitionUserEntity> entities = Lists.newArrayList();
        PropertyElement propertyElement = flowElement.getProperty();
        // 发起人
        addDefinitionUsers(flowDefUuid, "flow", "发起人", flowDefUuid, "creators", propertyElement.getCreators(), entities);
        // 参与人
        addDefinitionUsers(flowDefUuid, "flow", "参与人", flowDefUuid, "users", propertyElement.getUsers(), entities);
        // 督办人
        addDefinitionUsers(flowDefUuid, "flow", "督办人", flowDefUuid, "monitors", propertyElement.getMonitors(), entities);
        // 监控者
        addDefinitionUsers(flowDefUuid, "flow", "监控者", flowDefUuid, "admins", propertyElement.getAdmins(), entities);
        // 阅读者
        addDefinitionUsers(flowDefUuid, "flow", "查看者", flowDefUuid, "viewers", propertyElement.getViewers(), entities);
        // AB岗
        addDefinitionUsers(flowDefUuid, "flow", "逾期流程代理人", flowDefUuid, "bakUsers", propertyElement.getBakUsers(), entities, 16);

        return entities;
    }

    /**
     * @param flowDefUuid
     * @param nodeType
     * @param nodeName
     * @param nodeId
     * @param nodeUserAttribute
     * @param unitElements
     * @param entities
     */
    private void addDefinitionUsers(String flowDefUuid, String nodeType, String nodeName, String nodeId, String nodeUserAttribute,
                                    List<UserUnitElement> unitElements, List<WfFlowDefinitionUserEntity> entities) {
        addDefinitionUsers(flowDefUuid, nodeType, nodeName, nodeId, nodeUserAttribute, unitElements, entities, 1, 32);
    }

    /**
     * @param flowDefUuid
     * @param nodeType
     * @param nodeName
     * @param nodeId
     * @param nodeUserAttribute
     * @param unitElements
     * @param entities
     * @param allowUserTypes
     */
    private void addDefinitionUsers(String flowDefUuid, String nodeType, String nodeName, String nodeId, String nodeUserAttribute,
                                    List<UserUnitElement> unitElements, List<WfFlowDefinitionUserEntity> entities, Integer... allowUserTypes) {
        if (CollectionUtils.isEmpty(unitElements)) {
            return;
        }

        List<Integer> types = Lists.newArrayList(allowUserTypes);
        List<UserUnitElement> userUnitElements = unitElements.
                stream().filter(userUnitElement -> types.contains(userUnitElement.getType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userUnitElements)) {
            return;
        }

        List<String> userIds = Lists.newArrayList();
        List<String> userNames = Lists.newArrayList();
        List<String> orgIds = Lists.newArrayList();
        for (int index = 0; index < userUnitElements.size(); index++) {
            UserUnitElement unitElement = userUnitElements.get(index);
            String unitValue = unitElement.getValue();
            if (StringUtils.isBlank(unitValue)) {
                continue;
            }

            if (StringUtils.startsWith(unitValue, "{")) {
                TaskUserExpressionConfigJson json = JsonUtils.json2Object(unitValue, TaskUserExpressionConfigJson.class);
                List<TaskUserExpressionConfig> configs = json.getExpressionConfigs();
                if (CollectionUtils.isNotEmpty(configs)) {
                    for (TaskUserExpressionConfig config : configs) {
                        if (StringUtils.equals(config.getUserType(), "Unit")) {
                            userIds.add(config.getUserValue());
                            userNames.add(StringUtils.replace(config.getUserName(), Separator.SEMICOLON.getValue(), "，"));
                        }
                    }
                }
            } else if (StringUtils.equals(nodeUserAttribute, "bakUsers")) {
                // 逾期流程代理人
                String[] aUsers = StringUtils.split(unitValue, "|");
                String[] bUsers = StringUtils.split(unitElement.getArgValue(), "|");
                String userId = "";
                String userName = "";
                if (aUsers != null && aUsers.length == 2) {
                    userId = aUsers[1];
                    userName = "A岗(" + aUsers[0] + ")";
                }
                if (bUsers != null && bUsers.length == 2) {
                    userId += "|" + bUsers[1];
                    userName += "-B岗(" + bUsers[0] + ")";
                }
                userIds.add(userId);
                userNames.add(userName);
            } else {
                String orgName = "";
                if (Integer.valueOf(32).equals(unitElement.getType())) {
                    orgName = "业务组织：";
                    orgIds.add(unitElement.getBizOrgId());
                } else if (StringUtils.isNotBlank(unitElement.getOrgId())) {
                    orgName = "行政组织：";
                    orgIds.add(unitElement.getOrgId());
                }
                userIds.add(unitValue);
                userNames.add(orgName + StringUtils.replace(unitElement.getArgValue(), Separator.SEMICOLON.getValue(), "，"));
            }
        }
        WfFlowDefinitionUserEntity entity = new WfFlowDefinitionUserEntity();
        entity.setFlowDefUuid(flowDefUuid);
        entity.setNodeType(nodeType);
        entity.setNodeName(nodeName);
        entity.setNodeId(nodeId);
        entity.setNodeUserAttribute(nodeUserAttribute);
        entity.setUserType(StringUtils.join(types, Separator.SEMICOLON.getValue()));
        entity.setUserValue(StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
        entity.setUserArgValue(StringUtils.join(userNames, "；"));
        entity.setUserOrgId(StringUtils.join(orgIds, Separator.SEMICOLON.getValue()));
        entity.setSortOrder(entities.size());
        entities.add(entity);
    }

    /**
     * @param flowDefUuid
     * @param flowElement
     * @return
     */
    private List<WfFlowDefinitionUserEntity> getDefinitionUsersOfTimer(String flowDefUuid, FlowElement flowElement) {
        List<WfFlowDefinitionUserEntity> entities = Lists.newArrayList();
        List<TimerElement> timerElements = flowElement.getTimers();
        if (CollectionUtils.isEmpty(timerElements)) {
            return entities;
        }

        timerElements.forEach(timerElement -> {
            String timerId = timerElement.getTimerId();

            // 预警提醒
            List<AlarmElement> alarmElements = timerElement.getAlarmElements();
            if (CollectionUtils.isNotEmpty(alarmElements)) {
                alarmElements.forEach(alarmElement -> {
                    // 其他人员
                    addDefinitionUsers(flowDefUuid, "timer", timerElement.getName(), timerId, "alarmUsers", alarmElement.getAlarmUsers(), entities);
                    // 发起流程其他人员
                    addDefinitionUsers(flowDefUuid, "timer", timerElement.getName(), timerId, "alarmFlowDoingUsers", alarmElement.getAlarmFlowDoingUsers(), entities);
                });
            }

            // 逾期处理
            // 其他人员
            addDefinitionUsers(flowDefUuid, "timer", timerElement.getName(), timerId, "dueUsers", timerElement.getDueUsers(), entities);
            // 移交给其他人员办理
            addDefinitionUsers(flowDefUuid, "timer", timerElement.getName(), timerId, "dueToUsers", timerElement.getDueToUsers(), entities);
            // 发起流程其他人员
            addDefinitionUsers(flowDefUuid, "timer", timerElement.getName(), timerId, "dueFlowDoingUsers", timerElement.getDueFlowDoingUsers(), entities);
        });

        return entities;
    }

    /**
     * @param flowDefUuid
     * @param flowElement
     * @return
     */
    private List<WfFlowDefinitionUserEntity> getDefinitionUsersOfTask(String flowDefUuid, FlowElement flowElement) {
        List<WfFlowDefinitionUserEntity> entities = Lists.newArrayList();
        List<TaskElement> taskElements = flowElement.getTasks();
        if (CollectionUtils.isEmpty(taskElements)) {
            return entities;
        }

        taskElements.forEach(taskElement -> {
            String taskName = taskElement.getName();
            String taskId = taskElement.getId();
            // 办理人
            if (taskElement.isSetUser()) {
                addDefinitionUsers(flowDefUuid, "task", taskName, taskId, "users", taskElement.getUsers(), entities, 1, 16, 32);
            }
            // 转办人
            addDefinitionUsers(flowDefUuid, "task", taskName, taskId, "transferUsers", taskElement.getTransferUsers(), entities);
            // 抄送人
            addDefinitionUsers(flowDefUuid, "task", taskName, taskId, "copyUsers", taskElement.getCopyUsers(), entities);
            // 指定其他办理人
            addDefinitionUsers(flowDefUuid, "task", taskName, taskId, "emptyToUsers", taskElement.getEmptyToUsers(), entities);
            // 督办人
            addDefinitionUsers(flowDefUuid, "task", taskName, taskId, "monitors", taskElement.getMonitors(), entities);
            // 决策人
            addDefinitionUsers(flowDefUuid, "task", taskName, taskId, "decisionMakers", taskElement.getDecisionMakers(), entities);

            // 环节权限配置
            addTaskRightConfigUsers(flowDefUuid, taskElement, entities);

            // 子流程环节
            if (taskElement instanceof SubTaskElement) {
                SubTaskElement subTaskElement = (SubTaskElement) taskElement;
                // 跟进人员
                addDefinitionUsers(flowDefUuid, "subTask", taskName, taskId, "subTaskMonitors", subTaskElement.getSubTaskMonitors(), entities);
            }
        });

        return entities;
    }

    /**
     * @param flowDefUuid
     * @param taskElement
     * @param entities
     */
    private void addTaskRightConfigUsers(String flowDefUuid, TaskElement taskElement, List<WfFlowDefinitionUserEntity> entities) {
        String taskName = taskElement.getName();
        String taskId = taskElement.getId();
        // 发起权限配置
        RightConfigElement startRightConfig = taskElement.getStartRightConfig();
        addTaskRightConfigDefinitionUsers(flowDefUuid, taskName, taskId, "startRightConfig", startRightConfig, entities);
        // 待办权限配置
        RightConfigElement todoRightConfig = taskElement.getTodoRightConfig();
        addTaskRightConfigDefinitionUsers(flowDefUuid, taskName, taskId, "todoRightConfig", todoRightConfig, entities);
        // 已办权限配置
        RightConfigElement doneRightConfig = taskElement.getDoneRightConfig();
        addTaskRightConfigDefinitionUsers(flowDefUuid, taskName, taskId, "doneRightConfig", doneRightConfig, entities);
        // 督办权限配置
        RightConfigElement monitorRightConfig = taskElement.getMonitorRightConfig();
        addTaskRightConfigDefinitionUsers(flowDefUuid, taskName, taskId, "monitorRightConfig", monitorRightConfig, entities);
        // 监控权限配置
        RightConfigElement adminRightConfig = taskElement.getAdminRightConfig();
        addTaskRightConfigDefinitionUsers(flowDefUuid, taskName, taskId, "adminRightConfig", adminRightConfig, entities);
        // 抄送权限配置
        RightConfigElement copyToRightConfig = taskElement.getCopyToRightConfig();
        addTaskRightConfigDefinitionUsers(flowDefUuid, taskName, taskId, "copyToRightConfig", copyToRightConfig, entities);
        // 查看权限配置
        RightConfigElement viewerRightConfig = taskElement.getViewerRightConfig();
        addTaskRightConfigDefinitionUsers(flowDefUuid, taskName, taskId, "viewerRightConfig", viewerRightConfig, entities);
    }


    /**
     * @param flowDefUuid
     * @param taskName
     * @param taskId
     * @param configName
     * @param rightConfigElement
     * @param entities
     */
    private void addTaskRightConfigDefinitionUsers(String flowDefUuid, String taskName, String taskId, String configName,
                                                   RightConfigElement rightConfigElement, List<WfFlowDefinitionUserEntity> entities) {
        if (rightConfigElement == null) {
            return;
        }

        if (StringUtils.equals("1", rightConfigElement.getIsSetTransferUser())) {
            addDefinitionUsers(flowDefUuid, "task", taskName, taskId, configName + "TransferUsers",
                    rightConfigElement.getTransferUsers(), entities);
        }
        if (StringUtils.equals("1", rightConfigElement.getIsSetCounterSignUser())) {
            addDefinitionUsers(flowDefUuid, "task", taskName, taskId, configName + "CounterSignUsers",
                    rightConfigElement.getCounterSignUsers(), entities);
        }
        if (StringUtils.equals("1", rightConfigElement.getIsSetAddSignUser())) {
            addDefinitionUsers(flowDefUuid, "task", taskName, taskId, configName + "AddSignUsers",
                    rightConfigElement.getAddSignUsers(), entities);
        }
        if (StringUtils.equals("1", rightConfigElement.getIsSetCopyUser())) {
            addDefinitionUsers(flowDefUuid, "task", taskName, taskId, configName + "CopyUsers",
                    rightConfigElement.getCopyUsers(), entities);
        }
    }

    /**
     * @param flowDefUuid
     * @param flowElement
     * @return
     */
    private List<WfFlowDefinitionUserEntity> getDefinitionUsersOfMessage(String flowDefUuid, FlowElement flowElement) {
        List<WfFlowDefinitionUserEntity> entities = Lists.newArrayList();
        List<MessageTemplateElement> messageTemplateElements = flowElement.getProperty().getMessageTemplates();
        if (CollectionUtils.isEmpty(messageTemplateElements)) {
            return entities;
        }

        messageTemplateElements.forEach(messageTemplateElement -> {
            // 抄送人员
            addDefinitionUsers(flowDefUuid, "message", messageTemplateElement.getTypeName(), messageTemplateElement.getType(), "copyMsgRecipients", messageTemplateElement.getCopyMsgRecipients(), entities);
        });

        return entities;
    }

    @Override
    @Transactional
    public void deleteByFlowDefUuid(String flowDefUuid) {
        Assert.hasLength(flowDefUuid, "流程定义UUID不能为空！");

        String hql = "delete from WfFlowDefinitionUserEntity t where t.flowDefUuid = :flowDefUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("flowDefUuid", flowDefUuid);
        this.dao.deleteByHQL(hql, params);
    }

    /**
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     */
    @Override
    public void modifyUserOfFlowElement(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement, FlowDefinitionUserModifyParams modifyParams) {
        String userValue = definitionUserEntity.getUserValue();
        String userArgValue = definitionUserEntity.getUserArgValue();
        String oldUserId = modifyParams.getOldUserId();
        String oldUserName = modifyParams.getOldUserName();
        // 按用户ID查找替换
        if (StringUtils.isNotBlank(oldUserId)) {
            if (!StringUtils.contains(userValue, oldUserId)) {
                return;
            }
        } else if (StringUtils.isNotBlank(oldUserName)) {
            // 按用户名查找替换
            if (!StringUtils.contains(userArgValue, oldUserName)) {
                return;
            }
        } else {
            return;
        }

        String nodeType = definitionUserEntity.getNodeType();
        switch (nodeType) {
            case "flow":
                modifyDefinitionUsersOfFlow(definitionUserEntity, flowElement, modifyParams);
                break;
            case "timer":
                modifyDefinitionUsersOfTimer(definitionUserEntity, flowElement, modifyParams);
                break;
            case "task":
            case "subTask":
                modifyDefinitionUsersOfTask(definitionUserEntity, flowElement, modifyParams);
                break;
            case "message":
                modifyDefinitionUsersOfMessage(definitionUserEntity, flowElement, modifyParams);
                break;
        }
    }

    @Override
    public boolean validateBizOrgUser(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement, FlowDefinitionUserModifyParams modifyParams) {
        if (!("replace".equals(modifyParams.getModifyMode()) || "add".equals(modifyParams.getModifyMode()))) {
            return true;
        }

        String userValue = definitionUserEntity.getUserValue();
        String userArgValue = definitionUserEntity.getUserArgValue();
        String oldUserId = modifyParams.getOldUserId();
        String oldUserName = modifyParams.getOldUserName();
        // 按用户ID查找替换
        if (StringUtils.isNotBlank(oldUserId)) {
            if (!StringUtils.contains(userValue, oldUserId)) {
                return false;
            }
        } else if (StringUtils.isNotBlank(oldUserName)) {
            // 按用户名查找替换
            if (!StringUtils.contains(userArgValue, oldUserName)) {
                return false;
            }
        } else {
            return false;
        }

        List<UserUnitElement> unitElements = null;
        String nodeType = definitionUserEntity.getNodeType();
        switch (nodeType) {
            case "flow":
                unitElements = getDefinitionUnitUsersOfFlow(definitionUserEntity, flowElement, modifyParams);
                break;
            case "timer":
                unitElements = getDefinitionUnitUsersOfTimer(definitionUserEntity, flowElement, modifyParams);
                break;
            case "task":
            case "subTask":
                unitElements = getDefinitionUnitUsersOfTask(definitionUserEntity, flowElement, modifyParams);
                break;
            case "message":
                unitElements = getDefinitionUnitUsersOfMessage(definitionUserEntity, flowElement, modifyParams);
                break;
        }

        if (CollectionUtils.isEmpty(unitElements)) {
            return false;
        }

        boolean valid = true;
        List<UserUnitElement> orgUnitElements = unitElements.stream().filter(unitElement -> Integer.valueOf(1).equals(unitElement.getType())).collect(Collectors.toList());
        List<UserUnitElement> bizUnitElements = unitElements.stream().filter(unitElement -> Integer.valueOf(32).equals(unitElement.getType())).collect(Collectors.toList());
        for (UserUnitElement unitElement : orgUnitElements) {
            if (StringUtils.contains(unitElement.getValue(), modifyParams.getOldUserId())
                    && StringUtils.isNotBlank(unitElement.getOrgId())
                    && !StringUtils.startsWith(modifyParams.getNewUserId(), IdPrefix.GROUP.getValue())
                    && !workflowOrgService.isMemberOfOrg(modifyParams.getNewUserId(), unitElement.getOrgId())) {
                valid = false;
                break;
            }
        }
        if (valid) {
            for (UserUnitElement unitElement : bizUnitElements) {
                if (StringUtils.contains(unitElement.getValue(), modifyParams.getOldUserId())
                        && StringUtils.isNotBlank(unitElement.getBizOrgId())
                        && !workflowOrgService.isMemberOfBizOrg(modifyParams.getNewUserId(), unitElement.getBizOrgId())) {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

    /**
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     * @return
     */
    private List<UserUnitElement> getDefinitionUnitUsersOfFlow(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement, FlowDefinitionUserModifyParams modifyParams) {
        PropertyElement propertyElement = flowElement.getProperty();
        String nodeUserAttribute = definitionUserEntity.getNodeUserAttribute();
        List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(propertyElement, nodeUserAttribute);
        List<UserUnitElement> userUnitElements = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(unitElements)) {
            userUnitElements.addAll(unitElements);
        }
        return userUnitElements;
    }

    /**
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     * @return
     */
    private List<UserUnitElement> getDefinitionUnitUsersOfTimer(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement, FlowDefinitionUserModifyParams modifyParams) {
        List<TimerElement> timerElements = flowElement.getTimers();
        if (CollectionUtils.isEmpty(timerElements)) {
            return Collections.emptyList();
        }
        TimerElement timerElement = timerElements.stream().filter(timer -> StringUtils.equals(timer.getTimerId(), definitionUserEntity.getNodeId()))
                .findFirst().orElse(null);
        if (timerElement == null) {
            return Collections.emptyList();
        }

        List<UserUnitElement> userUnitElements = Lists.newArrayList();
        String nodeUserAttribute = definitionUserEntity.getNodeUserAttribute();
        // 预警提醒
        if (StringUtils.equals("alarmUsers", nodeUserAttribute) || StringUtils.equals("alarmFlowDoingUsers", nodeUserAttribute)) {
            List<AlarmElement> alarmElements = timerElement.getAlarmElements();
            if (CollectionUtils.isNotEmpty(alarmElements)) {
                alarmElements.stream().forEach(alarmElement -> {
                    List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(alarmElement, nodeUserAttribute);
                    if (CollectionUtils.isNotEmpty(unitElements)) {
                        userUnitElements.addAll(unitElements);
                    }
                });
            }
        } else {
            // 逾期处理
            List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(timerElement, nodeUserAttribute);
            if (CollectionUtils.isNotEmpty(unitElements)) {
                userUnitElements.addAll(unitElements);
            }
        }
        return userUnitElements;
    }


    /**
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     * @return
     */
    private List<UserUnitElement> getDefinitionUnitUsersOfTask(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement, FlowDefinitionUserModifyParams modifyParams) {
        TaskElement taskElement = flowElement.getTask(definitionUserEntity.getNodeId());
        if (taskElement == null) {
            return Collections.emptyList();
        }

        List<UserUnitElement> userUnitElements = Lists.newArrayList();
        String nodeUserAttribute = definitionUserEntity.getNodeUserAttribute();
        if (StringUtils.contains(nodeUserAttribute, "RightConfig")) {
            String rightConfigProperty = StringUtils.substringBefore(nodeUserAttribute, "RightConfig") + "RightConfig";
            String rightConfigUserProperty = StringUtils.uncapitalize(StringUtils.substringAfter(nodeUserAttribute, "RightConfig"));
            RightConfigElement rightConfigElement = (RightConfigElement) ReflectionUtils.invokeGetterMethod(taskElement, rightConfigProperty);
            List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(rightConfigElement, rightConfigUserProperty);
            if (CollectionUtils.isNotEmpty(unitElements)) {
                userUnitElements.addAll(unitElements);
            }
        } else {
            List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(taskElement, nodeUserAttribute);
            if (CollectionUtils.isNotEmpty(unitElements)) {
                userUnitElements.addAll(unitElements);
            }
        }
        return userUnitElements;
    }

    /**
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     * @return
     */
    private List<UserUnitElement> getDefinitionUnitUsersOfMessage(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement, FlowDefinitionUserModifyParams modifyParams) {
        List<MessageTemplateElement> elements = flowElement.getProperty().getMessageTemplates();
        if (CollectionUtils.isEmpty(elements)) {
            return Collections.emptyList();
        }

        List<UserUnitElement> userUnitElements = Lists.newArrayList();
        List<MessageTemplateElement> messageTemplateElements = elements.stream()
                .filter(element -> StringUtils.equals(element.getType(), definitionUserEntity.getNodeId()))
                .collect(Collectors.toList());
        String nodeUserAttribute = definitionUserEntity.getNodeUserAttribute();
        messageTemplateElements.stream().forEach(messageTemplateElement -> {
            List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(messageTemplateElement, nodeUserAttribute);
            if (CollectionUtils.isNotEmpty(unitElements)) {
                userUnitElements.addAll(unitElements);
            }
        });
        return userUnitElements;
    }

    /**
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     */
    private void modifyDefinitionUsersOfFlow(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement,
                                             FlowDefinitionUserModifyParams modifyParams) {
        PropertyElement propertyElement = flowElement.getProperty();
        String nodeUserAttribute = definitionUserEntity.getNodeUserAttribute();
        List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(propertyElement, nodeUserAttribute);
        modifyDefinitionUsers(unitElements, definitionUserEntity, modifyParams);
    }

    /**
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     */
    private void modifyDefinitionUsersOfTimer(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement,
                                              FlowDefinitionUserModifyParams modifyParams) {
        List<TimerElement> timerElements = flowElement.getTimers();
        if (CollectionUtils.isEmpty(timerElements)) {
            return;
        }
        TimerElement timerElement = timerElements.stream().filter(timer -> StringUtils.equals(timer.getTimerId(), definitionUserEntity.getNodeId()))
                .findFirst().orElse(null);
        if (timerElement == null) {
            return;
        }

        String nodeUserAttribute = definitionUserEntity.getNodeUserAttribute();
        // 预警提醒
        if (StringUtils.equals("alarmUsers", nodeUserAttribute) || StringUtils.equals("alarmFlowDoingUsers", nodeUserAttribute)) {
            List<AlarmElement> alarmElements = timerElement.getAlarmElements();
            if (CollectionUtils.isNotEmpty(alarmElements)) {
                alarmElements.stream().forEach(alarmElement -> {
                    List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(alarmElement, nodeUserAttribute);
                    modifyDefinitionUsers(unitElements, definitionUserEntity, modifyParams);
                });
            }
        } else {
            // 逾期处理
            List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(timerElement, nodeUserAttribute);
            modifyDefinitionUsers(unitElements, definitionUserEntity, modifyParams);
        }
    }

    /**
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     */
    private void modifyDefinitionUsersOfTask(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement,
                                             FlowDefinitionUserModifyParams modifyParams) {
        TaskElement taskElement = flowElement.getTask(definitionUserEntity.getNodeId());
        if (taskElement == null) {
            return;
        }

        String nodeUserAttribute = definitionUserEntity.getNodeUserAttribute();
        if (StringUtils.contains(nodeUserAttribute, "RightConfig")) {
            String rightConfigProperty = StringUtils.substringBefore(nodeUserAttribute, "RightConfig") + "RightConfig";
            String rightConfigUserProperty = StringUtils.uncapitalize(StringUtils.substringAfter(nodeUserAttribute, "RightConfig"));
            RightConfigElement rightConfigElement = (RightConfigElement) ReflectionUtils.invokeGetterMethod(taskElement, rightConfigProperty);
            List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(rightConfigElement, rightConfigUserProperty);
            modifyDefinitionUsers(unitElements, definitionUserEntity, modifyParams);
        } else {
            List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(taskElement, nodeUserAttribute);
            modifyDefinitionUsers(unitElements, definitionUserEntity, modifyParams);
        }
    }

    /**
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     */
    private void modifyDefinitionUsersOfMessage(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement,
                                                FlowDefinitionUserModifyParams modifyParams) {
        List<MessageTemplateElement> elements = flowElement.getProperty().getMessageTemplates();
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }

        List<MessageTemplateElement> messageTemplateElements = elements.stream()
                .filter(element -> StringUtils.equals(element.getType(), definitionUserEntity.getNodeId()))
                .collect(Collectors.toList());
        String nodeUserAttribute = definitionUserEntity.getNodeUserAttribute();
        messageTemplateElements.stream().forEach(messageTemplateElement -> {
            List<UserUnitElement> unitElements = (List<UserUnitElement>) ReflectionUtils.invokeGetterMethod(messageTemplateElement, nodeUserAttribute);
            modifyDefinitionUsers(unitElements, definitionUserEntity, modifyParams);
        });
    }

    /**
     * @param unitElements
     * @param definitionUserEntity
     * @param modifyParams
     */
    private void modifyDefinitionUsers(List<UserUnitElement> unitElements, WfFlowDefinitionUserEntity definitionUserEntity,
                                       FlowDefinitionUserModifyParams modifyParams) {
        if (CollectionUtils.isNotEmpty(unitElements)) {
            List<UserUnitElement> elements = Lists.newArrayList(unitElements);
            List<String> userTypes = Arrays.asList(StringUtils.split(definitionUserEntity.getUserType(), Separator.SEMICOLON.getValue()));
            elements.stream().filter(unitElement -> unitElement.getType() != null && userTypes.contains(unitElement.getType().toString()))
                    .forEach(unitElement -> modifyDefinitionUser(unitElement, definitionUserEntity, modifyParams, unitElements));
        }
    }

    /**
     * @param unitElement
     * @param definitionUserEntity
     * @param modifyParams
     * @param unitElements
     */
    private void modifyDefinitionUser(UserUnitElement unitElement, WfFlowDefinitionUserEntity definitionUserEntity,
                                      FlowDefinitionUserModifyParams modifyParams, List<UserUnitElement> unitElements) {
        String userValue = unitElement.getValue();
        String userArgValue = unitElement.getArgValue();
        String oldUserId = modifyParams.getOldUserId();
        String oldUserName = modifyParams.getOldUserName();
        // 按用户ID查找替换
        if (StringUtils.isNotBlank(oldUserId)) {
            if (StringUtils.contains(userValue, oldUserId)) {
                modifyDefinitionUserByMatchUserId(unitElement, modifyParams, unitElements, definitionUserEntity);
            }
        } else if (StringUtils.isNotBlank(oldUserName)) {
            // 按用户名查找替换
            if (StringUtils.contains(userArgValue, oldUserName)) {
                modifyDefinitionUserByMatchUserName(unitElement, modifyParams, unitElements, definitionUserEntity);
            }
        }
    }

    /**
     * @param unitElement
     * @param modifyParams
     * @param unitElements
     * @param definitionUserEntity
     */
    private void modifyDefinitionUserByMatchUserId(UserUnitElement unitElement, FlowDefinitionUserModifyParams modifyParams,
                                                   List<UserUnitElement> unitElements, WfFlowDefinitionUserEntity definitionUserEntity) {
        String modifyMode = modifyParams.getModifyMode();
        switch (modifyMode) {
            case "replace":
                if (Integer.valueOf(16).equals(unitElement.getType())) {
                    String unitValue = unitElement.getValue();
                    if ("bakUsers".equals(definitionUserEntity.getNodeUserAttribute())) {
                        String value = StringUtils.replace(unitValue, modifyParams.getOldUserId(), modifyParams.getNewUserId());
                        value = StringUtils.replace(value, modifyParams.getOldUserName(), modifyParams.getNewUserName());
                        unitElement.setValue(value);
                        unitElement.setArgValue(StringUtils.replace(unitElement.getArgValue(), modifyParams.getOldUserName(), modifyParams.getNewUserName()));
                    } else if (StringUtils.startsWith(unitValue, "{")) {
                        TaskUserExpressionConfigJson configJson = replaceTaskUserExpressionConfigUserByMatchUserId(unitValue, modifyParams);
                        unitElement.setValue(JsonUtils.object2Json(configJson));
                        unitElement.setArgValue(configJson.getExpressionDisplayName());
                    }
                } else {
                    List<String> userIds = Lists.newArrayList(StringUtils.split(unitElement.getValue(), Separator.SEMICOLON.getValue()));
                    List<String> userNames = Lists.newArrayList(StringUtils.split(unitElement.getArgValue(), Separator.SEMICOLON.getValue()));
                    List<String> userPaths = Lists.newArrayList();
                    if (StringUtils.isNotBlank(unitElement.getValuePath())) {
                        userPaths.addAll(Arrays.asList(StringUtils.split(unitElement.getValuePath(), Separator.SEMICOLON.getValue())));
                    }
                    List<String> newUserIds = Lists.newArrayList();
                    List<String> newUserNames = Lists.newArrayList();
                    List<String> newUserPaths = Lists.newArrayList();
                    for (int index = 0; index < userIds.size(); index++) {
                        if (StringUtils.equals(userIds.get(index), modifyParams.getOldUserId())) {
                            newUserIds.add(modifyParams.getNewUserId());
                            newUserNames.add(modifyParams.getNewUserName());
                            if (CollectionUtils.isNotEmpty(userPaths)) {
                                newUserPaths.add(modifyParams.getNewUserPath());
                            }
                        } else {
                            newUserIds.add(userIds.get(index));
                            newUserNames.add(userNames.get(index));
                            if (CollectionUtils.isNotEmpty(userPaths)) {
                                newUserPaths.add(userPaths.get(index));
                            }
                        }
                    }
                    unitElement.setValue(StringUtils.join(newUserIds, Separator.SEMICOLON.getValue()));
                    unitElement.setArgValue(StringUtils.join(newUserNames, Separator.SEMICOLON.getValue()));
                    unitElement.setValuePath(StringUtils.join(newUserPaths, Separator.SEMICOLON.getValue()));
                }
                break;
            case "add":
                if (Integer.valueOf(16).equals(unitElement.getType())) {
                    String unitValue = unitElement.getValue();
                    if ("bakUsers".equals(definitionUserEntity.getNodeUserAttribute())) {
                        unitElement.setValue(addBackUserByMatchUserId(unitValue, modifyParams));
                        unitElement.setArgValue(addBackUserByMatchUserId(unitElement.getArgValue(), modifyParams));
                    } else if (StringUtils.startsWith(unitValue, "{")) {
                        TaskUserExpressionConfigJson configJson = addTaskUserExpressionConfigUserByMatchUserId(unitValue, modifyParams);
                        unitElement.setValue(JsonUtils.object2Json(configJson));
                        unitElement.setArgValue(configJson.getExpressionDisplayName());
                    }
                } else {
                    List<String> userIds = Lists.newArrayList(StringUtils.split(unitElement.getValue(), Separator.SEMICOLON.getValue()));
                    List<String> userNames = Lists.newArrayList(StringUtils.split(unitElement.getArgValue(), Separator.SEMICOLON.getValue()));
                    List<String> userPaths = Lists.newArrayList();
                    if (StringUtils.isNotBlank(unitElement.getValuePath())) {
                        userPaths.addAll(Arrays.asList(StringUtils.split(unitElement.getValuePath(), Separator.SEMICOLON.getValue())));
                    }
                    userIds.add(modifyParams.getNewUserId());
                    userNames.add(modifyParams.getNewUserName());
                    if (CollectionUtils.isNotEmpty(userPaths)) {
                        userPaths.add(modifyParams.getNewUserPath());
                    }
                    unitElement.setValue(StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
                    unitElement.setArgValue(StringUtils.join(userNames, Separator.SEMICOLON.getValue()));
                    unitElement.setValuePath(StringUtils.join(userPaths, Separator.SEMICOLON.getValue()));
                }
                break;
            case "delete":
                if (Integer.valueOf(16).equals(unitElement.getType())) {
                    String unitValue = unitElement.getValue();
                    if ("bakUsers".equals(definitionUserEntity.getNodeUserAttribute())) {
                        unitElement.setValue(deleteBackUserByMatchUserId(unitValue, modifyParams));
                        unitElement.setArgValue(deleteBackUserByMatchUserId(unitElement.getArgValue(), modifyParams));
                    } else if (StringUtils.startsWith(unitValue, "{")) {
                        TaskUserExpressionConfigJson configJson = deleteTaskUserExpressionConfigUserByMatchUserId(unitValue, modifyParams);
                        unitElement.setValue(JsonUtils.object2Json(configJson));
                        unitElement.setArgValue(configJson.getExpressionDisplayName());
                    }
                } else {
                    List<String> userIds = Lists.newArrayList(StringUtils.split(unitElement.getValue(), Separator.SEMICOLON.getValue()));
                    List<String> userNames = Lists.newArrayList(StringUtils.split(unitElement.getArgValue(), Separator.SEMICOLON.getValue()));
                    List<String> userPaths = Lists.newArrayList();
                    if (StringUtils.isNotBlank(unitElement.getValuePath())) {
                        userPaths.addAll(Arrays.asList(StringUtils.split(unitElement.getValuePath(), Separator.SEMICOLON.getValue())));
                    }
                    List<String> newUserIds = Lists.newArrayList();
                    List<String> newUserNames = Lists.newArrayList();
                    List<String> newUserPaths = Lists.newArrayList();
                    for (int index = 0; index < userIds.size(); index++) {
                        if (StringUtils.equals(userIds.get(index), modifyParams.getOldUserId())) {
                            continue;
                        } else {
                            newUserIds.add(userIds.get(index));
                            newUserNames.add(userNames.get(index));
                            if (CollectionUtils.isNotEmpty(userPaths)) {
                                newUserPaths.add(userPaths.get(index));
                            }
                        }
                    }
                    unitElement.setValue(StringUtils.join(newUserIds, Separator.SEMICOLON.getValue()));
                    unitElement.setArgValue(StringUtils.join(newUserNames, Separator.SEMICOLON.getValue()));
                    unitElement.setValuePath(StringUtils.join(newUserPaths, Separator.SEMICOLON.getValue()));
                }
                break;
        }
    }

    /**
     * @param unitValue
     * @param modifyParams
     * @return
     */
    private TaskUserExpressionConfigJson replaceTaskUserExpressionConfigUserByMatchUserId(String unitValue, FlowDefinitionUserModifyParams modifyParams) {
        TaskUserExpressionConfigJson json = JsonUtils.json2Object(unitValue, TaskUserExpressionConfigJson.class);
        List<TaskUserExpressionConfig> configs = json.getExpressionConfigs();
        if (CollectionUtils.isEmpty(configs)) {
            return json;
        }

        for (TaskUserExpressionConfig config : configs) {
            if (StringUtils.equals(config.getUserType(), "Unit") && StringUtils.isNotBlank(config.getUserValue())
                    && StringUtils.isNotBlank(config.getUserName())) {
                List<String> userNames = Lists.newArrayList(StringUtils.split(config.getUserName(), Separator.SEMICOLON.getValue()));
                List<String> userIds = Lists.newArrayList(StringUtils.split(config.getUserValue(), Separator.SEMICOLON.getValue()));
                List<String> userPaths = Lists.newArrayList();
                if (StringUtils.isNotBlank(config.getValuePath())) {
                    userPaths.addAll(Arrays.asList(StringUtils.split(config.getValuePath(), Separator.SEMICOLON.getValue())));
                }
                List<String> newUserIds = Lists.newArrayList();
                List<String> newUserNames = Lists.newArrayList();
                List<String> newUserPaths = Lists.newArrayList();
                for (int index = 0; index < userIds.size(); index++) {
                    if (StringUtils.equals(userIds.get(index), modifyParams.getOldUserId())) {
                        newUserIds.add(modifyParams.getNewUserId());
                        newUserNames.add(modifyParams.getNewUserName());
                        if (CollectionUtils.isNotEmpty(userPaths)) {
                            newUserPaths.add(modifyParams.getNewUserPath());
                        }
                    } else {
                        newUserIds.add(userIds.get(index));
                        newUserNames.add(userNames.get(index));
                        if (CollectionUtils.isNotEmpty(userPaths)) {
                            newUserPaths.add(userPaths.get(index));
                        }
                    }
                }
                config.setUserName(StringUtils.join(newUserNames, Separator.SEMICOLON.getValue()));
                config.setUserValue(StringUtils.join(newUserIds, Separator.SEMICOLON.getValue()));
                config.setValuePath(StringUtils.join(newUserPaths, Separator.SEMICOLON.getValue()));
            }
        }
        return json;
    }

    /**
     * @param unitValue
     * @param modifyParams
     * @return
     */
    private TaskUserExpressionConfigJson addTaskUserExpressionConfigUserByMatchUserId(String unitValue, FlowDefinitionUserModifyParams modifyParams) {
        TaskUserExpressionConfigJson json = JsonUtils.json2Object(unitValue, TaskUserExpressionConfigJson.class);
        List<TaskUserExpressionConfig> configs = json.getExpressionConfigs();
        if (CollectionUtils.isEmpty(configs)) {
            return json;
        }

        for (TaskUserExpressionConfig config : configs) {
            if (StringUtils.equals(config.getUserType(), "Unit") && StringUtils.isNotBlank(config.getUserValue())
                    && StringUtils.isNotBlank(config.getUserName())) {
                List<String> userNames = Lists.newArrayList(StringUtils.split(config.getUserName(), Separator.SEMICOLON.getValue()));
                List<String> userIds = Lists.newArrayList(StringUtils.split(config.getUserValue(), Separator.SEMICOLON.getValue()));
                List<String> userPaths = Lists.newArrayList();
                if (StringUtils.isNotBlank(config.getValuePath())) {
                    userPaths.addAll(Arrays.asList(StringUtils.split(config.getValuePath(), Separator.SEMICOLON.getValue())));
                }
                userIds.add(modifyParams.getNewUserId());
                userNames.add(modifyParams.getNewUserName());
                if (CollectionUtils.isNotEmpty(userPaths)) {
                    userPaths.add(modifyParams.getNewUserPath());
                }
                config.setUserName(StringUtils.join(userNames, Separator.SEMICOLON.getValue()));
                config.setUserValue(StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
                config.setValuePath(StringUtils.join(userPaths, Separator.SEMICOLON.getValue()));
            }
        }
        return json;
    }

    /**
     * @param unitValue
     * @param modifyParams
     * @return
     */
    private TaskUserExpressionConfigJson deleteTaskUserExpressionConfigUserByMatchUserId(String unitValue, FlowDefinitionUserModifyParams modifyParams) {
        TaskUserExpressionConfigJson json = JsonUtils.json2Object(unitValue, TaskUserExpressionConfigJson.class);
        List<TaskUserExpressionConfig> configs = json.getExpressionConfigs();
        if (CollectionUtils.isEmpty(configs)) {
            return json;
        }

        for (TaskUserExpressionConfig config : configs) {
            if (StringUtils.equals(config.getUserType(), "Unit") && StringUtils.isNotBlank(config.getUserValue())
                    && StringUtils.isNotBlank(config.getUserName())) {
                List<String> userNames = Lists.newArrayList(StringUtils.split(config.getUserName(), Separator.SEMICOLON.getValue()));
                List<String> userIds = Lists.newArrayList(StringUtils.split(config.getUserValue(), Separator.SEMICOLON.getValue()));
                List<String> userPaths = Lists.newArrayList();
                if (StringUtils.isNotBlank(config.getValuePath())) {
                    userPaths.addAll(Arrays.asList(StringUtils.split(config.getValuePath(), Separator.SEMICOLON.getValue())));
                }
                List<String> newUserIds = Lists.newArrayList();
                List<String> newUserNames = Lists.newArrayList();
                List<String> newUserPaths = Lists.newArrayList();
                for (int index = 0; index < userIds.size(); index++) {
                    if (StringUtils.equals(userIds.get(index), modifyParams.getOldUserId())) {
                        continue;
                    } else {
                        newUserIds.add(userIds.get(index));
                        newUserNames.add(userNames.get(index));
                        if (CollectionUtils.isNotEmpty(userPaths)) {
                            newUserPaths.add(userPaths.get(index));
                        }
                    }
                }
                config.setUserName(StringUtils.join(newUserNames, Separator.SEMICOLON.getValue()));
                config.setUserValue(StringUtils.join(newUserIds, Separator.SEMICOLON.getValue()));
                config.setValuePath(StringUtils.join(newUserPaths, Separator.SEMICOLON.getValue()));
            }
        }
        return json;
    }

    /**
     * @param value
     * @param modifyParams
     * @return
     */
    private String addBackUserByMatchUserId(String value, FlowDefinitionUserModifyParams modifyParams) {
        String retValue = value;
        String[] parts = StringUtils.split(value, Separator.VERTICAL.getValue());
        if (parts.length == 2) {
            List<String> names = Lists.newArrayList(StringUtils.split(parts[0], Separator.SEMICOLON.getValue()));
            List<String> ids = Lists.newArrayList(StringUtils.split(parts[1], Separator.SEMICOLON.getValue()));
            int index = ids.indexOf(modifyParams.getOldUserId());
            if (index != -1 && !ids.contains(modifyParams.getNewUserId())) {
                ids.add(modifyParams.getNewUserId());
                names.add(modifyParams.getNewUserName());
                retValue = StringUtils.join(names, Separator.SEMICOLON.getValue()) + Separator.VERTICAL.getValue() + StringUtils.join(ids, Separator.SEMICOLON.getValue());
            }
        }
        return retValue;
    }

    /**
     * @param value
     * @param modifyParams
     * @return
     */
    private String deleteBackUserByMatchUserId(String value, FlowDefinitionUserModifyParams modifyParams) {
        String retValue = value;
        String[] parts = StringUtils.split(value, Separator.VERTICAL.getValue());
        if (parts.length == 2) {
            List<String> names = Lists.newArrayList(StringUtils.split(parts[0], Separator.SEMICOLON.getValue()));
            List<String> ids = Lists.newArrayList(StringUtils.split(parts[1], Separator.SEMICOLON.getValue()));
            int index = ids.indexOf(modifyParams.getOldUserId());
            if (index != -1) {
                ids.remove(index);
                names.remove(index);
                if (CollectionUtils.size(ids) > 0) {
                    retValue = StringUtils.join(names, Separator.SEMICOLON.getValue()) + Separator.VERTICAL.getValue() + StringUtils.join(ids, Separator.SEMICOLON.getValue());
                }
            }
        }
        return retValue;
    }

    /**
     * @param unitElement
     * @param modifyParams
     * @param unitElements
     * @param definitionUserEntity
     */
    private void modifyDefinitionUserByMatchUserName(UserUnitElement unitElement, FlowDefinitionUserModifyParams modifyParams,
                                                     List<UserUnitElement> unitElements, WfFlowDefinitionUserEntity definitionUserEntity) {
        String modifyMode = modifyParams.getModifyMode();
        switch (modifyMode) {
            case "replace":
                unitElement.setValue(StringUtils.replace(unitElement.getValue(), modifyParams.getOldUserId(), modifyParams.getNewUserId()));
                unitElement.setArgValue(StringUtils.replace(unitElement.getArgValue(), modifyParams.getOldUserName(), modifyParams.getNewUserName()));
                break;
            case "add":
                UserUnitElement newUnitElement = new UserUnitElement();
                BeanUtils.copyProperties(unitElement, newUnitElement);
                newUnitElement.setValue(modifyParams.getNewUserId());
                newUnitElement.setArgValue(modifyParams.getNewUserName());
                unitElements.add(newUnitElement);
                break;
            case "delete":
                unitElements.remove(unitElement);
                break;
        }
    }

}

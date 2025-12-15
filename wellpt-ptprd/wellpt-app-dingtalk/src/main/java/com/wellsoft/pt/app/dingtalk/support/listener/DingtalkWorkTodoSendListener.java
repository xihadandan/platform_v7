/*
 * @(#)4/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support.listener;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkTodoTaskEntity;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkWorkRecordEntity;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkConfigFacadeService;
import com.wellsoft.pt.app.dingtalk.model.ActionCardMessage;
import com.wellsoft.pt.app.dingtalk.model.TodoTaskMessage;
import com.wellsoft.pt.app.dingtalk.service.DingtalkTodoTaskService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkUserService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkWorkRecordService;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiV2Utils;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkGroupChatUtils;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.facade.service.MessageTemplateApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.event.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
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
 * 4/23/25.1	    zhulh		4/23/25		    Create
 * </pre>
 * @date 4/23/25
 */
@Component
public class DingtalkWorkTodoSendListener extends WellptTransactionalEventListener<WorkTodoEvent> {

    @Autowired
    private DingtalkConfigFacadeService dingtalkConfigFacadeService;

    @Autowired
    private DingtalkWorkRecordService dingtalkWorkRecordService;

    @Autowired
    private DingtalkTodoTaskService dingtalkTodoTaskService;

    @Autowired
    private DingtalkUserService dingtalkUserService;

    @Autowired
    private MessageTemplateApiFacade messageTemplateApiFacade;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Override
    public void onApplicationEvent(WorkTodoEvent event) {
        FlowInstance flowInstance = event.getFlowInstance();
        TaskInstance taskInstance = event.getTaskInstance();
        // 流程数据未持久化（比如办理人异常）
        if (StringUtils.isBlank(flowInstance.getUuid()) || StringUtils.isBlank(taskInstance.getUuid())) {
            return;
        }
        String system = flowInstance.getSystem();
        String tenant = flowInstance.getTenant();
        DingtalkConfigVo dingtalkConfigVo = dingtalkConfigFacadeService.getVoBySystemAndTenant(system, tenant);
        if (BooleanUtils.isNotTrue(dingtalkConfigVo.getEnabled())) {
            return;
        }
        DingtalkConfigVo.DingtalkConfiguration dingtalkConfiguration = dingtalkConfigVo.getConfiguration();
        if (dingtalkConfiguration == null || (!dingtalkConfiguration.isEnabledTodoTask()
                && !dingtalkConfiguration.isEnabledPushMsg() && !dingtalkConfiguration.getGroupChat().isEnabled())) {
            return;
        }

        TaskData taskData = event.getTaskData();
        List<String> todoUserIds = getTodoUserIds(taskInstance, taskData, event);
        boolean isCancel = taskData.isCancel(taskData.getPreTaskId(taskInstance.getId()));
        boolean isRollback = taskData.isRollback(taskData.getPreTaskId(taskInstance.getId()));
        // 转办、会签、加签、委托、委托回收操作
        if (event instanceof WorkTransferEvent || event instanceof WorkCounterSignEvent || event instanceof WorkAddSignEvent
                || event instanceof WorkDelegationEvent || event instanceof WorkDelegationTakeBackEvent) {
            // 更新钉钉待办任务
            updateTodoTask(taskInstance, flowInstance, event, dingtalkConfigVo);

            List<DingtalkWorkRecordEntity> workRecords = dingtalkWorkRecordService.listByTaskInstUuidAndTypeAndState(taskInstance.getUuid(), DingtalkWorkRecordEntity.Type.SYSTEM, DingtalkWorkRecordEntity.State.Sent);
            if (DingtalkGroupChatUtils.isGroupChat(workRecords)) {
                DingtalkWorkRecordEntity groupChatEntity = DingtalkGroupChatUtils.getGroupChat(workRecords);
                // 加签群聊成员
                addChatMembers(groupChatEntity, taskInstance, flowInstance, event, dingtalkConfigVo);
            } else {
                if (CollectionUtils.size(workRecords) <= 1) {
                    // List<String> todoUserIds = getTodoUserIds(taskInstance, taskData, event);
                    if (!todoUserIds.contains(taskData.getUserId())) {
                        todoUserIds = Lists.newArrayList(todoUserIds.listIterator());
                        todoUserIds.add(taskData.getUserId());
                    }
                    if (DingtalkGroupChatUtils.isMatchGroupChat(todoUserIds, event, dingtalkConfiguration)) {
                        deleteWorkRecords(workRecords);
                        String actionName = Objects.toString(WorkFlowOperation.getName(taskData.getActionType(taskInstance.getUuid() + taskData.getUserId())), StringUtils.EMPTY);
                        String subject = String.format("工作%s通知", actionName);
                        createChat(subject, taskInstance, flowInstance, todoUserIds, dingtalkConfigVo);
                    } else {
                        // 待办消息推送
                        pushMessage(null, taskInstance, flowInstance, Lists.newArrayList(event.getUserIds().iterator()), event, dingtalkConfigVo);
                    }
                } else {
                    // 待办消息推送
                    pushMessage(null, taskInstance, flowInstance, Lists.newArrayList(event.getUserIds().iterator()), event, dingtalkConfigVo);
                }
            }
        } else {
            // 撤回、退回流转，撤回已发送的消息
            if (isCancel || isRollback) {
                String preTaskInstUuid = taskData.getPreTaskInstUuid(taskInstance.getId());
                cancelMessage(preTaskInstUuid, dingtalkConfigVo);
                // 撤回钉钉待办任务
                cancelTodoTask(preTaskInstUuid, dingtalkConfigVo);
            }

            if (!isCancel) {
                String subject = isRollback ? "工作退回通知" : "工作到达通知";
                if (DingtalkGroupChatUtils.isMatchGroupChat(todoUserIds, event, dingtalkConfiguration)) {
                    createChat(subject, taskInstance, flowInstance, todoUserIds, dingtalkConfigVo);
                } else {
                    // 待办消息推送
                    pushMessage(subject, taskInstance, flowInstance, todoUserIds, event, dingtalkConfigVo);
                }
            }

            // 推送钉钉待办任务
            pushTodoTask(taskInstance, flowInstance, todoUserIds, event, dingtalkConfigVo);
        }
    }

    private void addChatMembers(DingtalkWorkRecordEntity workRecord, TaskInstance taskInstance, FlowInstance flowInstance, WorkTodoEvent event, DingtalkConfigVo dingtalkConfigVo) {
        Set<String> userIds = event.getUserIds();
        Map<String, String> userIdMap = dingtalkUserService.listUserIdMapByOaUserIds(Lists.newArrayList(userIds.iterator()), dingtalkConfigVo.getAppId());
        if (MapUtils.isEmpty(userIdMap)) {
            logger.error("dingtalk user is empty，" + StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
            return;
        }
        DingtalkApiV2Utils.addChatMembers(workRecord, userIdMap, dingtalkConfigVo);
        dingtalkWorkRecordService.save(workRecord);
    }

    private void deleteWorkRecords(List<DingtalkWorkRecordEntity> workRecords) {
        if (CollectionUtils.isNotEmpty(workRecords)) {
            workRecords.forEach(feishuWorkRecordEntity -> feishuWorkRecordEntity.setState(DingtalkWorkRecordEntity.State.Deleted));
            dingtalkWorkRecordService.saveAll(workRecords);
        }
    }

    private void createChat(String subject, TaskInstance taskInstance, FlowInstance flowInstance, List<String> todoUserIds, DingtalkConfigVo dingtalkConfigVo) {
        // 创建群，给群发送消息
        Map<String, String> userIdMap = dingtalkUserService.listUserIdMapByOaUserIds(todoUserIds, dingtalkConfigVo.getAppId());
        if (MapUtils.isEmpty(userIdMap)) {
            logger.error("dingtalk user is empty，" + StringUtils.join(todoUserIds, Separator.SEMICOLON.getValue()));
            return;
        }
        DingtalkWorkRecordEntity recordEntity = createChatRecord(taskInstance, flowInstance, userIdMap, dingtalkConfigVo);
        DingtalkApiV2Utils.createChat(recordEntity, dingtalkConfigVo);
        dingtalkWorkRecordService.save(recordEntity);
        DingtalkApiV2Utils.sendCreateChatMessage(subject, recordEntity, dingtalkConfigVo);
    }

    private DingtalkWorkRecordEntity createChatRecord(TaskInstance taskInstance, FlowInstance flowInstance, Map<String, String> userIdMap, DingtalkConfigVo dingtalkConfigVo) {
        String url = DingtalkApiV2Utils.getPcUrl(taskInstance.getUuid(), flowInstance.getUuid(), flowInstance.getSystem(), dingtalkConfigVo.getCorpDomainUri());

        String ownerId = dingtalkUserService.getUserIdByOaUserId(SpringSecurityUtils.getCurrentUserId(), dingtalkConfigVo.getAppId());

        Map<String, String> content = Maps.newHashMap();
        content.put("groupName", "群聊: " + flowInstance.getTitle());

        DingtalkWorkRecordEntity entity = new DingtalkWorkRecordEntity();
        entity.setConfigUuid(dingtalkConfigVo.getUuid());
        entity.setAppId(dingtalkConfigVo.getAppId());
        entity.setTitle(flowInstance.getTitle());
        entity.setFlowInstUuid(flowInstance.getUuid());
        entity.setTaskInstUuid(taskInstance.getUuid());
        entity.setUrl(url);
        entity.setContent(JsonUtils.object2Json(content));
        entity.setOaUserId(StringUtils.join(userIdMap.values(), Separator.SEMICOLON.getValue()));
        entity.setUserId(StringUtils.join(userIdMap.keySet(), Separator.SEMICOLON.getValue()));
        entity.setOwnerId(ownerId);
        entity.setGroupChat(true);
        entity.setType(DingtalkWorkRecordEntity.Type.SYSTEM);
        entity.setState(DingtalkWorkRecordEntity.State.ToSend);
        entity.setSystem(flowInstance.getSystem());
        entity.setTenant(flowInstance.getTenant());
        return entity;
    }

    /**
     * @param taskInstUuid
     * @param dingtalkConfigVo
     */
    private void cancelTodoTask(String taskInstUuid, DingtalkConfigVo dingtalkConfigVo) {
        // 待办任务
        if (dingtalkConfigVo.getConfiguration().isEnabledTodoTask()) {
            List<DingtalkTodoTaskEntity> todoTaskEntities = dingtalkTodoTaskService.listByTaskInstUuidAndState(taskInstUuid, DingtalkTodoTaskEntity.State.Sent);
            if (CollectionUtils.isNotEmpty(todoTaskEntities)) {
                todoTaskEntities.forEach(todoTaskEntity -> {
                    DingtalkApiV2Utils.cancelTodoTask(todoTaskEntity, dingtalkConfigVo);
                });
                dingtalkTodoTaskService.saveAll(todoTaskEntities);
            }
        }
    }

    /**
     * @param taskInstUuid
     * @param dingtalkConfigVo
     */
    private void cancelMessage(String taskInstUuid, DingtalkConfigVo dingtalkConfigVo) {
        // 消息及群聊
        List<DingtalkWorkRecordEntity> workRecordEntities = dingtalkWorkRecordService.listByTaskInstUuidAndTypeAndState(taskInstUuid, DingtalkWorkRecordEntity.Type.SYSTEM, DingtalkWorkRecordEntity.State.Sent);
        if (CollectionUtils.isNotEmpty(workRecordEntities)) {
            if (isGroupChat(workRecordEntities)) {
                DingtalkApiV2Utils.cancelGroupChat(workRecordEntities.get(0), dingtalkConfigVo);
            } else {
                DingtalkApiV2Utils.cancelMessage(workRecordEntities, dingtalkConfigVo);
            }
            dingtalkWorkRecordService.saveAll(workRecordEntities);
        }
    }

    private boolean isGroupChat(List<DingtalkWorkRecordEntity> workRecordEntities) {
        return DingtalkGroupChatUtils.isGroupChat(workRecordEntities);
    }

    private List<String> getTodoUserIds(TaskInstance taskInstance, TaskData taskData, WorkTodoEvent event) {
        List<String> todoUserIds = Lists.newArrayList(taskService.getTodoUserIds(taskInstance.getUuid()).iterator());
        FlowDelegate flowDelegate = taskData.getToken().getFlowDelegate();
        if (TaskNodeType.CollaborationTask.getValue().equals(flowDelegate.getFlow().getTask(taskInstance.getId()).getType())) {
            Set<FlowUserSid> decisionMakerSids = taskData.getTaskDecisionMakerSids(taskInstance.getId());
            if (CollectionUtils.isNotEmpty(decisionMakerSids)) {
                todoUserIds.addAll(decisionMakerSids.stream().map(FlowUserSid::getId).collect(Collectors.toList()));
            }
        }
        if (todoUserIds.stream().filter(todoUserId -> !IdPrefix.startsUser(todoUserId)).findFirst().isPresent()) {
            Map<String, String> userIdMap = workflowOrgService.getUsersByIds(todoUserIds, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(taskData.getToken()));
            todoUserIds = Arrays.asList(userIdMap.keySet().toArray(new String[0]));
        }
        return todoUserIds;
    }

    /**
     * 待办任务
     *
     * @param subject
     * @param taskInstance
     * @param flowInstance
     * @param todoUserIds
     * @param event
     * @param dingtalkConfigVo
     */
    private void pushTodoTask(TaskInstance taskInstance, FlowInstance flowInstance, List<String> todoUserIds, WorkTodoEvent event, DingtalkConfigVo dingtalkConfigVo) {
        if (dingtalkConfigVo.getConfiguration().isEnabledTodoTask()) {
            DingtalkTodoTaskEntity dingtalkTodoTaskEntity = createTodoTask(taskInstance, flowInstance, todoUserIds, event, dingtalkConfigVo);
            if (dingtalkTodoTaskEntity != null) {
                DingtalkApiV2Utils.createTodoTask(dingtalkTodoTaskEntity, dingtalkConfigVo);
                dingtalkTodoTaskService.save(dingtalkTodoTaskEntity);
            } else {
                logger.error("dingtalkTodoTaskEntity is null");
            }
        }
    }

    /**
     * 消息通知
     *
     * @param subject
     * @param taskInstance
     * @param flowInstance
     * @param todoUserIds
     * @param event
     * @param dingtalkConfigVo
     */
    private void pushMessage(String subject, TaskInstance taskInstance, FlowInstance flowInstance, List<String> todoUserIds, WorkTodoEvent event, DingtalkConfigVo dingtalkConfigVo) {
        if (dingtalkConfigVo.getConfiguration().isEnabledPushMsg()) {
            List<DingtalkWorkRecordEntity> entities = createRecords(taskInstance, flowInstance, todoUserIds, event, dingtalkConfigVo);

            DingtalkApiV2Utils.sendTextMessage(StringUtils.isNotBlank(subject) ? subject : "工作到达通知", entities, dingtalkConfigVo);
            dingtalkWorkRecordService.saveAll(entities);
        }
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param todoUserIds
     * @param event
     * @param dingtalkConfigVo
     * @return
     */
    private DingtalkTodoTaskEntity createTodoTask(TaskInstance taskInstance, FlowInstance flowInstance, List<String> todoUserIds, WorkTodoEvent event, DingtalkConfigVo dingtalkConfigVo) {
        Map<String, String> unionIdMap = dingtalkUserService.listUnionIdMapByOaUserIds(todoUserIds, dingtalkConfigVo.getAppId());
        if (MapUtils.isEmpty(unionIdMap)) {
            return null;
        }

        String redirectUri = DingtalkApiV2Utils.getUri(dingtalkConfigVo.getCorpDomainUri());
        String url = DingtalkApiV2Utils.getPcUrl(taskInstance.getUuid(), flowInstance.getUuid(), flowInstance.getSystem(), dingtalkConfigVo.getCorpDomainUri());

        // 消息内容
        TodoTaskMessage message = createTodoTaskMessage(url, redirectUri, flowInstance, taskInstance, event, dingtalkConfigVo);

        String content = JsonUtils.object2Json(message);
        String ownerUnionId = dingtalkUserService.getUnionIdByOaUserId(SpringSecurityUtils.getCurrentUserId(), dingtalkConfigVo.getAppId());

        DingtalkTodoTaskEntity entity = new DingtalkTodoTaskEntity();
        entity.setConfigUuid(dingtalkConfigVo.getUuid());
        entity.setAppId(dingtalkConfigVo.getAppId());
        entity.setTitle(flowInstance.getTitle());
        entity.setFlowInstUuid(flowInstance.getUuid());
        entity.setTaskInstUuid(taskInstance.getUuid());
        entity.setUrl(url);
        entity.setContent(content);
        entity.setOaUserId(StringUtils.join(unionIdMap.values(), Separator.SEMICOLON.getValue()));
        entity.setUserUnionId(StringUtils.join(unionIdMap.keySet(), Separator.SEMICOLON.getValue()));
        entity.setOwnerUnionId(ownerUnionId);
        entity.setState(DingtalkTodoTaskEntity.State.ToSend);
        entity.setSystem(flowInstance.getSystem());
        entity.setTenant(flowInstance.getTenant());
        return entity;
    }

    private TodoTaskMessage createTodoTaskMessage(String url, String redirectUri, FlowInstance flowInstance, TaskInstance taskInstance, WorkTodoEvent event, DingtalkConfigVo dingtalkConfigVo) {
        TodoTaskMessage message = new TodoTaskMessage();
        message.setSubject(flowInstance.getTitle());
        message.setPcUrl(url);
        message.setAppUrl(DingtalkApiV2Utils.getMobileUrlByPcUrl(url, dingtalkConfigVo.getMobileAppUri(), flowInstance.getSystem()));
        LinkedHashMap<String, Object> fieldValues = Maps.newLinkedHashMap();
        fieldValues.put("前办理人", SpringSecurityUtils.getCurrentUserName());
        fieldValues.put("办理环节", taskInstance.getName());
        fieldValues.put("开始时间", DateUtils.formatDateTimeMin(taskInstance.getStartTime()));
        message.setContentFieldList(fieldValues);
        return message;
    }

    private void updateTodoTask(TaskInstance taskInstance, FlowInstance flowInstance, WorkTodoEvent event, DingtalkConfigVo dingtalkConfigVo) {
        if (!dingtalkConfigVo.getConfiguration().isEnabledTodoTask()) {
            return;
        }

        List<DingtalkTodoTaskEntity> todoTaskEntities = dingtalkTodoTaskService.listByTaskInstUuidAndState(taskInstance.getUuid(), DingtalkTodoTaskEntity.State.Sent);
        if (CollectionUtils.isEmpty(todoTaskEntities)) {
            return;
        }

        DingtalkTodoTaskEntity todoTaskEntity = todoTaskEntities.get(0);
        List<String> oaUserIds = getTodoUserIds(taskInstance, event.getTaskData(), event);
        Map<String, String> userIdMap = dingtalkUserService.listUnionIdMapByOaUserIds(oaUserIds, dingtalkConfigVo.getAppId());
        if (MapUtils.isEmpty(userIdMap)) {
            logger.error("dingtalk user is empty，" + StringUtils.join(oaUserIds, Separator.SEMICOLON.getValue()));
            return;
        }

//        List<String> newOaUserIds = com.google.common.collect.Lists.newArrayList(StringUtils.split(todoTaskEntity.getOaUserId(), Separator.SEMICOLON.getValue()));
//        List<String> newUserUnionIds = com.google.common.collect.Lists.newArrayList(StringUtils.split(todoTaskEntity.getUserUnionId(), Separator.SEMICOLON.getValue()));
//        newOaUserIds.addAll(userIdMap.values());
//        newUserUnionIds.addAll(userIdMap.keySet());

        todoTaskEntity.setOaUserId(StringUtils.join(userIdMap.values(), Separator.SEMICOLON.getValue()));
        todoTaskEntity.setUserUnionId(StringUtils.join(userIdMap.keySet(), Separator.SEMICOLON.getValue()));

        DingtalkApiV2Utils.updateTodoTask(todoTaskEntity, dingtalkConfigVo);
        dingtalkTodoTaskService.save(todoTaskEntity);
    }

    private List<DingtalkWorkRecordEntity> createRecords(TaskInstance taskInstance, FlowInstance flowInstance, List<String> todoUserIds, WorkTodoEvent event, DingtalkConfigVo dingtalkConfigVo) {
        Map<String, String> userIdMap = dingtalkUserService.listUserIdMapByOaUserIds(todoUserIds, dingtalkConfigVo.getAppId());
        if (MapUtils.isEmpty(userIdMap)) {
            return Collections.emptyList();
        }

        String redirectUri = DingtalkApiV2Utils.getUri(dingtalkConfigVo.getCorpDomainUri());
        String url = DingtalkApiV2Utils.getPcUrl(taskInstance.getUuid(), flowInstance.getUuid(), flowInstance.getSystem(), dingtalkConfigVo.getCorpDomainUri());

        // 消息内容
        MessageTemplate messageTemplate = getMessageTemplate(event, taskInstance, dingtalkConfigVo);
        ActionCardMessage message = createActionCardMessage("工作到达通知", url, redirectUri, messageTemplate, flowInstance, taskInstance, event, dingtalkConfigVo);

        String content = JsonUtils.object2Json(message);

        List<DingtalkWorkRecordEntity> entities = Lists.newArrayList();
//        userIdMap.forEach((userId, oaUserId) -> {
        DingtalkWorkRecordEntity entity = new DingtalkWorkRecordEntity();
        entity.setConfigUuid(dingtalkConfigVo.getUuid());
        entity.setAppId(dingtalkConfigVo.getAppId());
        entity.setTitle(flowInstance.getTitle());
        entity.setFlowInstUuid(flowInstance.getUuid());
        entity.setTaskInstUuid(taskInstance.getUuid());
        entity.setUrl(url);
        entity.setContent(content);
        entity.setOaUserId(StringUtils.join(userIdMap.values(), Separator.SEMICOLON.getValue()));
        entity.setUserId(StringUtils.join(userIdMap.keySet(), Separator.SEMICOLON.getValue()));
        entity.setGroupChat(false);
        entity.setType(DingtalkWorkRecordEntity.Type.SYSTEM);
        entity.setState(DingtalkWorkRecordEntity.State.ToSend);
        entity.setSystem(flowInstance.getSystem());
        entity.setTenant(flowInstance.getTenant());
        entities.add(entity);
//        });
        return entities;
    }

    private ActionCardMessage createActionCardMessage(String subject, String pcUrl, String redirectUri, MessageTemplate messageTemplate, FlowInstance flowInstance, TaskInstance taskInstance,
                                                      WorkTodoEvent event, DingtalkConfigVo dingtalkConfigVo) {
        ActionCardMessage message = new ActionCardMessage();
        if (StringUtils.isNotBlank(pcUrl)) {
            message.setSingleTitle("查看流程");
            message.setSingleUrl(DingtalkApiV2Utils.getMobileUrlByPcUrl(pcUrl, dingtalkConfigVo.getMobileAppUri(), flowInstance.getSystem()));
        }
        if (messageTemplate == null) {
            message.setSubject(subject);
            message.setMarkdown("### " + flowInstance.getTitle());
            return message;
        }

        try {
            Map<Object, Object> dataMap = MessageClientUtils.getDyformValues(event.getTaskData(), taskInstance, flowInstance);

            dataMap.put("_referer", redirectUri);
            dataMap.put("_refererEncoded", java.net.URLEncoder.encode(redirectUri, Charsets.UTF_8.name()));
            Map<String, Object> extraData = Maps.newHashMap();
            extraData.put("_feishuMsg", true);
            extraData.put("_workUrl", pcUrl);
            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
            List<IdEntity> entities = MessageClientUtils.getIdEntities(taskInstance, flowInstance);
            Map<Object, Object> data = templateEngine.mergeDataAsMap(entities, dataMap, extraData, false, false);
            message.setSubject(messageTemplate.getName());
            StringBuilder markdwon = new StringBuilder();
            if (StringUtils.isNotBlank(messageTemplate.getOnlineSubject())) {
                String title = templateEngine.process(messageTemplate.getOnlineSubject(), data);
                markdwon.append("### ").append(title).append("\n");
            }
            if (StringUtils.isNotBlank(messageTemplate.getOnlineBody())) {
                String body = templateEngine.process(messageTemplate.getOnlineBody(), data);
                markdwon.append(body);
            }
            message.setMarkdown(markdwon.toString());
            return message;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            message.setSubject(subject);
            message.setMarkdown("### " + flowInstance.getTitle());
        }
        return message;
    }

    private MessageTemplate getMessageTemplate(WorkTodoEvent event, TaskInstance taskInstance, DingtalkConfigVo dingtalkConfigVo) {
        DingtalkConfigVo.DingtalkConfiguration configuration = dingtalkConfigVo.getConfiguration();
        String templateType = configuration.getTodoMsgTemplateType();
        String templateId = null;
        if (StringUtils.equals("todo", templateType)) {
            FlowDelegate flowDelegate = DingtalkGroupChatUtils.getFlowDelegate(event, taskInstance);
            List<com.wellsoft.pt.bpm.engine.support.MessageTemplate> messageTemplates = flowDelegate.getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_TODO.getType());
            if (CollectionUtils.isNotEmpty(messageTemplates)) {
                templateId = messageTemplates.get(0).getId();
            }
        } else {
            templateId = configuration.getTodoMsgTemplateId();
        }
        if (StringUtils.isBlank(templateId)) {
            return null;
        }
        return messageTemplateApiFacade.getById(templateId);
    }

}

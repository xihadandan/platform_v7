/*
 * @(#)3/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support.listener;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.feishu.entity.FeishuWorkRecordEntity;
import com.wellsoft.pt.app.feishu.model.TextMessage;
import com.wellsoft.pt.app.feishu.service.FeishuConfigService;
import com.wellsoft.pt.app.feishu.service.FeishuUserService;
import com.wellsoft.pt.app.feishu.service.FeishuWorkRecordService;
import com.wellsoft.pt.app.feishu.utils.FeishuApiUtils;
import com.wellsoft.pt.app.feishu.utils.FeishuGroupChatUtils;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.TaskActivityService;
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
 * 3/21/25.1	    zhulh		3/21/25		    Create
 * </pre>
 * @date 3/21/25
 */
@Component
public class FeishuWorkTodoSendListener extends WellptTransactionalEventListener<WorkTodoEvent> {

    @Autowired
    private FeishuConfigService feishuConfigService;

    @Autowired
    private FeishuUserService feishuUserService;

    @Autowired
    private FeishuWorkRecordService feishuWorkRecordService;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private MessageTemplateApiFacade messageTemplateApiFacade;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    /**
     * @param event
     */
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
        FeishuConfigVo feishuConfigVo = feishuConfigService.getBySystemAndTenant(system, tenant);
        if (BooleanUtils.isNotTrue(feishuConfigVo.getEnabled())) {
            return;
        }
        FeishuConfigVo.FeishuConfiguration feishuConfiguration = feishuConfigVo.getConfiguration();
        if (feishuConfiguration == null ||
                (!feishuConfiguration.isEnabledPushMsg() && !feishuConfiguration.getGroupChat().isEnabled())) {
            return;
        }

        TaskData taskData = event.getTaskData();
//        TaskActivity taskActivity = taskActivityService.getByTaskInstUuid(taskInstance.getUuid());
//        if (taskActivity == null) {
//            try {
//                Thread.sleep(1000 * 1);
//                taskActivity = taskActivityService.getByTaskInstUuid(taskInstance.getUuid());
//            } catch (InterruptedException e) {
//            }
//        }
        boolean isCancel = taskData.isCancel(taskData.getPreTaskId(taskInstance.getId()));
        boolean isRollback = taskData.isRollback(taskData.getPreTaskId(taskInstance.getId()));
        // 转办、会签、加签、委托、委托回收操作
        if (event instanceof WorkTransferEvent || event instanceof WorkCounterSignEvent || event instanceof WorkAddSignEvent
                || event instanceof WorkDelegationEvent || event instanceof WorkDelegationTakeBackEvent) {
            List<FeishuWorkRecordEntity> workRecords = feishuWorkRecordService.listByTaskInstUuidAndTypeAndState(taskInstance.getUuid(), FeishuWorkRecordEntity.Type.SYSTEM, FeishuWorkRecordEntity.State.Sent);
            if (FeishuGroupChatUtils.isGroupChat(workRecords)) {
                FeishuWorkRecordEntity groupChatEntity = FeishuGroupChatUtils.getGroupChat(workRecords);
                // 加签群聊成员
                addChatMembers(groupChatEntity, taskInstance, flowInstance, event, feishuConfigVo);
            } else {
                if (CollectionUtils.size(workRecords) <= 1) {
                    List<String> todoUserIds = getTodoUserIds(taskInstance, taskData, event);
                    if (!todoUserIds.contains(taskData.getUserId())) {
                        todoUserIds = Lists.newArrayList(todoUserIds.listIterator());
                        todoUserIds.add(taskData.getUserId());
                    }
                    if (FeishuGroupChatUtils.isMatchGroupChat(todoUserIds, event, feishuConfiguration)) {
                        deleteWorkRecords(workRecords);
                        String actionName = Objects.toString(WorkFlowOperation.getName(taskData.getActionType(taskInstance.getUuid() + taskData.getUserId())), StringUtils.EMPTY);
                        String subject = String.format("工作%s通知", actionName);
                        createChat(subject, taskInstance, flowInstance, todoUserIds, feishuConfigVo);
                    } else {
                        // 待办消息推送
                        pushMessage(null, taskInstance, flowInstance, Lists.newArrayList(event.getUserIds().iterator()), event, feishuConfigVo);
                    }
                } else {
                    // 待办消息推送
                    pushMessage(null, taskInstance, flowInstance, Lists.newArrayList(event.getUserIds().iterator()), event, feishuConfigVo);
                }
            }
        } else {
            // 撤回、退回流转，撤回已发送的消息
            if (isCancel || isRollback) {
                String preTaskInstUuid = taskData.getPreTaskInstUuid(taskInstance.getId());
                cancelMessage(preTaskInstUuid, feishuConfigVo);
            }

            if (!isCancel) {
                String subject = isRollback ? "工作退回通知" : "工作到达通知";
                List<String> todoUserIds = getTodoUserIds(taskInstance, taskData, event);
                if (FeishuGroupChatUtils.isMatchGroupChat(todoUserIds, event, feishuConfiguration)) {
                    createChat(subject, taskInstance, flowInstance, todoUserIds, feishuConfigVo);
                } else {
                    // 待办消息推送
                    pushMessage(subject, taskInstance, flowInstance, todoUserIds, event, feishuConfigVo);
                }
            }
        }
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

    private void deleteWorkRecords(List<FeishuWorkRecordEntity> workRecords) {
        if (CollectionUtils.isNotEmpty(workRecords)) {
            workRecords.forEach(feishuWorkRecordEntity -> feishuWorkRecordEntity.setState(FeishuWorkRecordEntity.State.Deleted));
            feishuWorkRecordService.saveAll(workRecords);
        }
    }

    /**
     * 添加群聊成员
     *
     * @param workRecord
     * @param taskInstance
     * @param flowInstance
     * @param event
     * @param feishuConfigVo
     */
    private void addChatMembers(FeishuWorkRecordEntity workRecord, TaskInstance taskInstance, FlowInstance flowInstance, WorkTodoEvent event, FeishuConfigVo feishuConfigVo) {
        Set<String> userIds = event.getUserIds();
        Map<String, String> openIdMap = feishuUserService.listOpenIdMapByOaUserId(userIds, feishuConfigVo.getAppId());
        if (MapUtils.isEmpty(openIdMap)) {
            logger.error("fieshu user is empty，" + StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
            return;
        }
        FeishuApiUtils.addChatMembers(workRecord, openIdMap, feishuConfigVo);
        feishuWorkRecordService.save(workRecord);
    }

    private void pushMessage(String subject, TaskInstance taskInstance, FlowInstance flowInstance, List<String> todoUserIds, WorkTodoEvent event, FeishuConfigVo feishuConfigVo) {
        if (feishuConfigVo.getConfiguration().isEnabledPushMsg()) {
            List<FeishuWorkRecordEntity> entities = createRecords(taskInstance, flowInstance, todoUserIds, event, feishuConfigVo);

            // entities.forEach(feishuWorkRecordEntity -> {
            FeishuApiUtils.sendTextMessage(StringUtils.isNotBlank(subject) ? subject : "工作到达通知", entities, feishuConfigVo);
            //});
            feishuWorkRecordService.saveAll(entities);
        }
    }

    /**
     * 创建群
     *
     * @param subject
     * @param taskInstance
     * @param flowInstance
     * @param todoUserIds
     * @param feishuConfigVo
     */
    private void createChat(String subject, TaskInstance taskInstance, FlowInstance flowInstance,
                            List<String> todoUserIds, FeishuConfigVo feishuConfigVo) {
        // 创建群，给群发送消息
        Set<String> userIds = Sets.newHashSet(todoUserIds);// event.getUserIds();
        Map<String, String> openIdMap = feishuUserService.listOpenIdMapByOaUserId(userIds, feishuConfigVo.getAppId());
        if (MapUtils.isEmpty(openIdMap)) {
            logger.error("fieshu user is empty，" + StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
            return;
        }
        FeishuWorkRecordEntity recordEntity = createChatRecord(taskInstance, flowInstance, openIdMap, feishuConfigVo);
        FeishuApiUtils.createChat(recordEntity, feishuConfigVo);
        feishuWorkRecordService.save(recordEntity);
        FeishuApiUtils.sendCreateChatMessage(subject, recordEntity, feishuConfigVo);
    }

//    /**
//     * @param taskInstance
//     * @param event
//     * @return
//     */
//    private boolean isCreateChat(TaskInstance taskInstance, WorkTodoEvent event, FeishuConfigVo.FeishuConfiguration feishuConfiguration) {
//        FeishuConfigVo.FeishuGroupChat groupChat = feishuConfiguration.getGroupChat();
//        if (groupChat == null || !groupChat.isEnabled()) {
//            return false;
//        }
//
//        FlowDelegate flowDelegate = getFlowDelegate(event, taskInstance);
//        TaskElement taskElement = flowDelegate.getFlow().getTask(taskInstance.getId());
//        if ((TaskNodeType.CollaborationTask.getValue().equals(taskElement.getType()) || BooleanUtils.isNotTrue(taskElement.isAnyone()))
//                && CollectionUtils.size(event.getUserIds()) > 1) {
//            return true;
//        }
//        return false;
//    }

    /**
     * @param taskInstUuid
     * @param feishuConfigVo
     */
    private void cancelMessage(String taskInstUuid, FeishuConfigVo feishuConfigVo) {
        List<FeishuWorkRecordEntity> workRecordEntities = feishuWorkRecordService.listByTaskInstUuidAndTypeAndState(taskInstUuid, FeishuWorkRecordEntity.Type.SYSTEM, FeishuWorkRecordEntity.State.Sent);
        if (CollectionUtils.isEmpty(workRecordEntities)) {
            return;
        }

        if (isGroupChat(workRecordEntities)) {
            FeishuApiUtils.cancelGroupChat(workRecordEntities.get(0), feishuConfigVo);
        } else {
            FeishuApiUtils.cancelMessage(workRecordEntities, feishuConfigVo.getAppId(), feishuConfigVo.getAppSecret(), feishuConfigVo.getServiceUri());
        }
        feishuWorkRecordService.saveAll(workRecordEntities);
    }

    private boolean isGroupChat(List<FeishuWorkRecordEntity> workRecordEntities) {
        return FeishuGroupChatUtils.isGroupChat(workRecordEntities);
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param todoUserIds
     * @param event
     * @param feishuConfigVo
     * @return
     */
    private List<FeishuWorkRecordEntity> createRecords(TaskInstance taskInstance, FlowInstance flowInstance,
                                                       List<String> todoUserIds, WorkTodoEvent event, FeishuConfigVo feishuConfigVo) {
        Set<String> userIds = Sets.newHashSet(todoUserIds);
        Map<String, String> openIdMap = feishuUserService.listOpenIdMapByOaUserId(userIds, feishuConfigVo.getAppId());
        if (MapUtils.isEmpty(openIdMap)) {
            return Collections.emptyList();
        }

        String redirectUri = FeishuApiUtils.getUri(feishuConfigVo.getRedirectUri());
//
//        String url = String.format("%s/sys/%s/_/workflow/work/view/work?taskInstUuid=%s&flowInstUuid=%s&_requestCode=%s",
//                redirectUri, flowInstance.getSystem(), taskInstance.getUuid(), flowInstance.getUuid(), System.currentTimeMillis());
        String url = FeishuApiUtils.getPcUrl(taskInstance.getUuid(), flowInstance.getUuid(), flowInstance.getSystem(), feishuConfigVo.getRedirectUri());

        // 消息内容
        MessageTemplate messageTemplate = getMessageTemplate(event, taskInstance, feishuConfigVo);
        TextMessage textMessage = renderMessageContent(url, redirectUri, messageTemplate, flowInstance, taskInstance, event, feishuConfigVo);
        if (textMessage == null) {
            textMessage = new TextMessage();
            textMessage.setSubject("工作到达通知");
            textMessage.setTitle(flowInstance.getTitle());
        }
        String content = JsonUtils.object2Json(textMessage);

        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        List<FeishuWorkRecordEntity> entities = Lists.newArrayList();
        openIdMap.forEach((openId, oaUserId) -> {
//            if (StringUtils.equals(currentUserId, oaUserId)) {
//                return;
//            }
            FeishuWorkRecordEntity entity = new FeishuWorkRecordEntity();
            entity.setConfigUuid(feishuConfigVo.getUuid());
            entity.setAppId(feishuConfigVo.getAppId());
            entity.setTitle(flowInstance.getTitle());
            entity.setFlowInstUuid(flowInstance.getUuid());
            entity.setTaskInstUuid(taskInstance.getUuid());
            entity.setUrl(url);
            entity.setContent(content);
            entity.setOaUserId(oaUserId);
            entity.setOpenId(openId);
            entity.setGroupChat(false);
            entity.setType(FeishuWorkRecordEntity.Type.SYSTEM);
            entity.setState(FeishuWorkRecordEntity.State.ToSend);
            entity.setSystem(flowInstance.getSystem());
            entity.setTenant(flowInstance.getTenant());
            entities.add(entity);
        });
        return entities;
    }

    /**
     * @param pcUrl
     * @param redirectUri
     * @param messageTemplate
     * @param flowInstance
     * @param taskInstance
     * @param event
     * @param feishuConfigVo
     * @return
     */
    private TextMessage renderMessageContent(String pcUrl, String redirectUri, MessageTemplate messageTemplate, FlowInstance flowInstance, TaskInstance taskInstance,
                                             WorkTodoEvent event, FeishuConfigVo feishuConfigVo) {
        if (messageTemplate == null) {
            return null;
        }

        TextMessage textMessage = new TextMessage();
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
            textMessage.setSubject(messageTemplate.getName());
            if (StringUtils.isNotBlank(messageTemplate.getOnlineBody())) {
                String body = templateEngine.process(messageTemplate.getOnlineBody(), data);
                textMessage.setText(body);
            }
            if (StringUtils.isNotBlank(messageTemplate.getOnlineSubject())) {
                String title = templateEngine.process(messageTemplate.getOnlineSubject(), data);
                textMessage.setTitle(title);
            }
            textMessage.setUrl(pcUrl);
            return textMessage;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private MessageTemplate getMessageTemplate(WorkTodoEvent event, TaskInstance taskInstance, FeishuConfigVo feishuConfigVo) {
        FeishuConfigVo.FeishuConfiguration configuration = feishuConfigVo.getConfiguration();
        String templateType = configuration.getTodoMsgTemplateType();
        String templateId = null;
        if (StringUtils.equals("todo", templateType)) {
            FlowDelegate flowDelegate = FeishuGroupChatUtils.getFlowDelegate(event, taskInstance);
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

    private FeishuWorkRecordEntity createChatRecord(TaskInstance taskInstance, FlowInstance flowInstance, Map<String, String> openIdMap, FeishuConfigVo feishuConfigVo) {
//        Set<String> userIds = Sets.newHashSet(todoUserIds);
//        Map<String, String> openIdMap = feishuUserService.listOpenIdMapByOaUserId(userIds, feishuConfigVo.getAppId());
        // String redirectUri = FeishuApiUtils.getUri(feishuConfigVo.getRedirectUri());

        String url = FeishuApiUtils.getPcUrl(taskInstance.getUuid(), flowInstance.getUuid(), flowInstance.getSystem(), feishuConfigVo.getRedirectUri());
//        String url = String.format("%s/sys/%s/_/workflow/work/view/work?taskInstUuid=%s&flowInstUuid=%s&_requestCode=%s",
//                redirectUri, flowInstance.getSystem(), taskInstance.getUuid(), flowInstance.getUuid(), System.currentTimeMillis());

        Map<String, String> content = Maps.newHashMap();
        content.put("groupName", "群聊: " + flowInstance.getTitle());
        FeishuWorkRecordEntity entity = new FeishuWorkRecordEntity();
        entity.setConfigUuid(feishuConfigVo.getUuid());
        entity.setAppId(feishuConfigVo.getAppId());
        entity.setTitle(flowInstance.getTitle());
        entity.setFlowInstUuid(flowInstance.getUuid());
        entity.setTaskInstUuid(taskInstance.getUuid());
        entity.setUrl(url);
        entity.setContent(JsonUtils.object2Json(content));
        entity.setOaUserId(StringUtils.join(openIdMap.values(), Separator.SEMICOLON.getValue()));
        entity.setOpenId(StringUtils.join(openIdMap.keySet(), Separator.SEMICOLON.getValue()));
        entity.setGroupChat(true);
        entity.setType(FeishuWorkRecordEntity.Type.SYSTEM);
        entity.setState(FeishuWorkRecordEntity.State.ToSend);
        entity.setSystem(flowInstance.getSystem());
        entity.setTenant(flowInstance.getTenant());
        return entity;
    }


}

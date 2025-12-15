/*
 * @(#)5/26/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.support.listener;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.weixin.entity.WeixinWorkRecordEntity;
import com.wellsoft.pt.app.weixin.facade.service.WeixinConfigFacadeService;
import com.wellsoft.pt.app.weixin.model.TextCardMessage;
import com.wellsoft.pt.app.weixin.service.WeixinUserService;
import com.wellsoft.pt.app.weixin.service.WeixinWorkRecordService;
import com.wellsoft.pt.app.weixin.utils.WeixinApiUtils;
import com.wellsoft.pt.app.weixin.utils.WeixinGroupChatUtils;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
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
 * 5/26/25.1	    zhulh		5/26/25		    Create
 * </pre>
 * @date 5/26/25
 */
@Component
public class WeixinWorkTodoSendListener extends WellptTransactionalEventListener<WorkTodoEvent> {

    @Autowired
    private WeixinConfigFacadeService weixinConfigFacadeService;

    @Autowired
    private WeixinUserService weixinUserService;

    @Autowired
    private WeixinWorkRecordService weixinWorkRecordService;

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
        // String tenant = flowInstance.getTenant();
        WeixinConfigVo weixinConfigVo = weixinConfigFacadeService.getVoBySystem(system);
        if (BooleanUtils.isNotTrue(weixinConfigVo.getEnabled())) {
            return;
        }
        WeixinConfigVo.WeixinConfiguration weixinConfiguration = weixinConfigVo.getConfiguration();
        if (weixinConfiguration == null ||
                (!weixinConfiguration.isEnabledPushMsg() && !weixinConfiguration.getGroupChat().isEnabled())) {
            return;
        }

        TaskData taskData = event.getTaskData();
        boolean isCancel = taskData.isCancel(taskData.getPreTaskId(taskInstance.getId()));
        boolean isRollback = taskData.isRollback(taskData.getPreTaskId(taskInstance.getId()));
        // 转办、会签、加签、委托、委托回收操作
        if (event instanceof WorkTransferEvent || event instanceof WorkCounterSignEvent || event instanceof WorkAddSignEvent
                || event instanceof WorkDelegationEvent || event instanceof WorkDelegationTakeBackEvent) {
            List<WeixinWorkRecordEntity> workRecords = weixinWorkRecordService.listByTaskInstUuidAndTypeAndState(taskInstance.getUuid(), WeixinWorkRecordEntity.Type.SYSTEM, WeixinWorkRecordEntity.State.Sent);
            if (WeixinGroupChatUtils.isGroupChat(workRecords)) {
                WeixinWorkRecordEntity groupChatEntity = WeixinGroupChatUtils.getGroupChat(workRecords);
                // 加签群聊成员
                addChatMembers(groupChatEntity, taskInstance, flowInstance, event, weixinConfigVo);
            } else {
                if (CollectionUtils.size(workRecords) <= 1) {
                    List<String> todoUserIds = getTodoUserIds(taskInstance, taskData, event);
                    if (!todoUserIds.contains(taskData.getUserId())) {
                        todoUserIds = Lists.newArrayList(todoUserIds.listIterator());
                        todoUserIds.add(taskData.getUserId());
                    }
                    if (WeixinGroupChatUtils.isMatchGroupChat(todoUserIds, event, weixinConfiguration)) {
                        deleteWorkRecords(workRecords);
                        String actionName = Objects.toString(WorkFlowOperation.getName(taskData.getActionType(taskInstance.getUuid() + taskData.getUserId())), StringUtils.EMPTY);
                        String subject = String.format("工作%s通知", actionName);
                        createChat(subject, taskInstance, flowInstance, todoUserIds, weixinConfigVo);
                    } else {
                        // 待办消息推送
                        pushMessage(null, taskInstance, flowInstance, Lists.newArrayList(event.getUserIds().iterator()), event, weixinConfigVo);
                    }
                } else {
                    // 待办消息推送
                    pushMessage(null, taskInstance, flowInstance, Lists.newArrayList(event.getUserIds().iterator()), event, weixinConfigVo);
                }
            }
        } else {
            // 撤回、退回流转，撤回已发送的消息
            if (isCancel || isRollback) {
                String preTaskInstUuid = taskData.getPreTaskInstUuid(taskInstance.getId());
                cancelMessage(preTaskInstUuid, weixinConfigVo);
            }

            if (!isCancel) {
                String subject = isRollback ? "工作退回通知" : "工作到达通知";
                List<String> todoUserIds = getTodoUserIds(taskInstance, taskData, event);
                if (WeixinGroupChatUtils.isMatchGroupChat(todoUserIds, event, weixinConfiguration)) {
                    createChat(subject, taskInstance, flowInstance, todoUserIds, weixinConfigVo);
                } else {
                    // 待办消息推送
                    pushMessage(subject, taskInstance, flowInstance, todoUserIds, event, weixinConfigVo);
                }
            }
        }
    }

    private void deleteWorkRecords(List<WeixinWorkRecordEntity> workRecords) {
        if (CollectionUtils.isNotEmpty(workRecords)) {
            workRecords.forEach(workRecord -> workRecord.setState(WeixinWorkRecordEntity.State.Deleted));
            weixinWorkRecordService.saveAll(workRecords);
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

    private void createChat(String subject, TaskInstance taskInstance, FlowInstance flowInstance, List<String> todoUserIds, WeixinConfigVo weixinConfigVo) {
        // 创建群，给群发送消息
        Map<String, String> userIdMap = weixinUserService.listUserIdMapByOaUserIds(todoUserIds, weixinConfigVo.getCorpId());
        if (MapUtils.isEmpty(userIdMap)) {
            logger.error("weixin user is empty，" + StringUtils.join(todoUserIds, Separator.SEMICOLON.getValue()));
            return;
        }
        WeixinWorkRecordEntity recordEntity = createChatRecord(taskInstance, flowInstance, userIdMap, weixinConfigVo);
        WeixinApiUtils.createChat(recordEntity, weixinConfigVo);
        weixinWorkRecordService.save(recordEntity);
        WeixinApiUtils.sendCreateChatMessage(subject, recordEntity, weixinConfigVo);
    }

    private WeixinWorkRecordEntity createChatRecord(TaskInstance taskInstance, FlowInstance flowInstance, Map<String, String> userIdMap, WeixinConfigVo weixinConfigVo) {
        String url = WeixinApiUtils.getPcUrl(taskInstance.getUuid(), flowInstance.getUuid(), flowInstance.getSystem(), weixinConfigVo.getCorpDomainUri());

        String ownerId = weixinUserService.getUserIdByOaUserIdAndCorpId(SpringSecurityUtils.getCurrentUserId(), weixinConfigVo.getCorpId());

        Map<String, String> content = Maps.newHashMap();
        content.put("groupName", "群聊: " + flowInstance.getTitle());

        WeixinWorkRecordEntity entity = new WeixinWorkRecordEntity();
        entity.setConfigUuid(weixinConfigVo.getUuid());
        entity.setCorpId(weixinConfigVo.getCorpId());
        entity.setAppId(weixinConfigVo.getAppId());
        entity.setTitle(flowInstance.getTitle());
        entity.setFlowInstUuid(flowInstance.getUuid());
        entity.setTaskInstUuid(taskInstance.getUuid());
        entity.setUrl(url);
        entity.setContent(JsonUtils.object2Json(content));
        entity.setOaUserId(StringUtils.join(userIdMap.values(), Separator.SEMICOLON.getValue()));
        entity.setUserId(StringUtils.join(userIdMap.keySet(), Separator.SEMICOLON.getValue()));
        entity.setOwnerId(ownerId);
        entity.setGroupChat(true);
        entity.setType(WeixinWorkRecordEntity.Type.SYSTEM);
        entity.setState(WeixinWorkRecordEntity.State.ToSend);
        entity.setSystem(flowInstance.getSystem());
        entity.setTenant(flowInstance.getTenant());
        return entity;
    }

    private void addChatMembers(WeixinWorkRecordEntity workRecord, TaskInstance taskInstance, FlowInstance flowInstance, WorkTodoEvent event, WeixinConfigVo weixinConfigVo) {
        Set<String> userIds = event.getUserIds();
        Map<String, String> userIdMap = weixinUserService.listUserIdMapByOaUserIds(Lists.newArrayList(userIds.iterator()), weixinConfigVo.getCorpId());
        if (MapUtils.isEmpty(userIdMap)) {
            logger.error("weixin user is empty，" + StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
            return;
        }
        WeixinApiUtils.addChatMembers(workRecord, userIdMap, weixinConfigVo);
        weixinWorkRecordService.save(workRecord);
    }

    private void pushMessage(String subject, TaskInstance taskInstance, FlowInstance flowInstance, List<String> todoUserIds,
                             WorkTodoEvent event, WeixinConfigVo weixinConfigVo) {
        if (weixinConfigVo.getConfiguration().isEnabledPushMsg()) {
            List<WeixinWorkRecordEntity> entities = createRecords(taskInstance, flowInstance, todoUserIds, event, weixinConfigVo);

            WeixinApiUtils.sendTextMessage(StringUtils.isNotBlank(subject) ? subject : "工作到达通知", entities, weixinConfigVo);
            weixinWorkRecordService.saveAll(entities);
        }
    }

    private List<WeixinWorkRecordEntity> createRecords(TaskInstance taskInstance, FlowInstance flowInstance, List<String> todoUserIds, WorkTodoEvent event, WeixinConfigVo weixinConfigVo) {
        Map<String, String> userIdMap = weixinUserService.listUserIdMapByOaUserIds(todoUserIds, weixinConfigVo.getCorpId());
        if (MapUtils.isEmpty(userIdMap)) {
            return Collections.emptyList();
        }

        String redirectUri = WeixinApiUtils.getUri(weixinConfigVo.getCorpDomainUri());
        String url = WeixinApiUtils.getPcUrl(taskInstance.getUuid(), flowInstance.getUuid(), flowInstance.getSystem(), weixinConfigVo.getCorpDomainUri());

        // 消息内容
        MessageTemplate messageTemplate = getMessageTemplate(event, taskInstance, weixinConfigVo);
        TextCardMessage message = createActionCardMessage("工作到达通知", url, redirectUri, messageTemplate, flowInstance, taskInstance, event, weixinConfigVo);

        String content = JsonUtils.object2Json(message);

        List<WeixinWorkRecordEntity> entities = Lists.newArrayList();
        WeixinWorkRecordEntity entity = new WeixinWorkRecordEntity();
        entity.setConfigUuid(weixinConfigVo.getUuid());
        entity.setCorpId(weixinConfigVo.getCorpId());
        entity.setAppId(weixinConfigVo.getAppId());
        entity.setTitle(flowInstance.getTitle());
        entity.setFlowInstUuid(flowInstance.getUuid());
        entity.setTaskInstUuid(taskInstance.getUuid());
        entity.setUrl(url);
        entity.setContent(content);
        entity.setOaUserId(StringUtils.join(userIdMap.values(), Separator.SEMICOLON.getValue()));
        entity.setUserId(StringUtils.join(userIdMap.keySet(), Separator.SEMICOLON.getValue()));
        entity.setGroupChat(false);
        entity.setType(WeixinWorkRecordEntity.Type.SYSTEM);
        entity.setState(WeixinWorkRecordEntity.State.ToSend);
        entity.setSystem(flowInstance.getSystem());
        entity.setTenant(flowInstance.getTenant());
        entities.add(entity);
        return entities;
    }

    private TextCardMessage createActionCardMessage(String subject, String pcUrl, String redirectUri, MessageTemplate messageTemplate, FlowInstance flowInstance, TaskInstance taskInstance,
                                                    WorkTodoEvent event, WeixinConfigVo weixinConfigVo) {
        TextCardMessage message = new TextCardMessage();
        if (StringUtils.isNotBlank(pcUrl)) {
            message.setBtnTxt("查看流程");
            message.setUrl(WeixinApiUtils.getMobileUrlByPcUrl(pcUrl, weixinConfigVo.getMobileAppUri(), flowInstance.getSystem()));
        }
        if (messageTemplate == null) {
            message.setTitle(subject);
            message.setDescription(flowInstance.getTitle());
            return message;
        }

        try {
            Map<Object, Object> dataMap = MessageClientUtils.getDyformValues(event.getTaskData(), taskInstance, flowInstance);

            dataMap.put("_referer", redirectUri);
            dataMap.put("_refererEncoded", java.net.URLEncoder.encode(redirectUri, Charsets.UTF_8.name()));
            Map<String, Object> extraData = Maps.newHashMap();
            extraData.put("_weixinMsg", true);
            extraData.put("_workUrl", pcUrl);
            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
            List<IdEntity> entities = MessageClientUtils.getIdEntities(taskInstance, flowInstance);
            Map<Object, Object> data = templateEngine.mergeDataAsMap(entities, dataMap, extraData, false, false);
            message.setTitle(messageTemplate.getName());
            StringBuilder markdwon = new StringBuilder();
            if (StringUtils.isNotBlank(messageTemplate.getOnlineSubject())) {
                String title = templateEngine.process(messageTemplate.getOnlineSubject(), data);
                markdwon.append(title).append("\n");
            }
            if (StringUtils.isNotBlank(messageTemplate.getOnlineBody())) {
                String body = templateEngine.process(messageTemplate.getOnlineBody(), data);
                markdwon.append(body);
            }
            message.setDescription(markdwon.toString());
            return message;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            message.setTitle(subject);
            message.setDescription(flowInstance.getTitle());
        }
        return message;
    }

    private MessageTemplate getMessageTemplate(WorkTodoEvent event, TaskInstance taskInstance, WeixinConfigVo weixinConfigVo) {
        WeixinConfigVo.WeixinConfiguration configuration = weixinConfigVo.getConfiguration();
        String templateType = configuration.getTodoMsgTemplateType();
        String templateId = null;
        if (StringUtils.equals("todo", templateType)) {
            FlowDelegate flowDelegate = WeixinGroupChatUtils.getFlowDelegate(event, taskInstance);
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

    private void cancelMessage(String taskInstUuid, WeixinConfigVo weixinConfigVo) {
        List<WeixinWorkRecordEntity> workRecordEntities = weixinWorkRecordService.listByTaskInstUuidAndTypeAndState(taskInstUuid, WeixinWorkRecordEntity.Type.SYSTEM, WeixinWorkRecordEntity.State.Sent);
        if (CollectionUtils.isEmpty(workRecordEntities)) {
            return;
        }

        if (WeixinGroupChatUtils.isGroupChat(workRecordEntities)) {
            WeixinApiUtils.cancelGroupChat(workRecordEntities.get(0), weixinConfigVo);
        } else {
            WeixinApiUtils.cancelMessage(workRecordEntities, weixinConfigVo);
        }
        weixinWorkRecordService.saveAll(workRecordEntities);
    }

}

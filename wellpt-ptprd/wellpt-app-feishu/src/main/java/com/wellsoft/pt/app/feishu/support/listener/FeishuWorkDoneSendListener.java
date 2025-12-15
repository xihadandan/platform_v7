/*
 * @(#)3/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support.listener;

import com.google.common.collect.Lists;
import com.lark.oapi.service.im.v1.model.Message;
import com.lark.oapi.service.im.v1.model.Sender;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.app.feishu.entity.FeishuWorkRecordEntity;
import com.wellsoft.pt.app.feishu.service.FeishuConfigService;
import com.wellsoft.pt.app.feishu.service.FeishuUserService;
import com.wellsoft.pt.app.feishu.service.FeishuWorkRecordService;
import com.wellsoft.pt.app.feishu.support.FeishuMessage;
import com.wellsoft.pt.app.feishu.support.FeishuMessageParser;
import com.wellsoft.pt.app.feishu.utils.FeishuApiUtils;
import com.wellsoft.pt.app.feishu.utils.FeishuGroupChatUtils;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.jpa.event.EventListenerPair;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.event.WorkDoneEvent;
import com.wellsoft.pt.workflow.event.WorkTodoEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
 * 3/24/25.1	    zhulh		3/24/25		    Create
 * </pre>
 * @date 3/24/25
 */
@Component
public class FeishuWorkDoneSendListener extends WellptTransactionalEventListener<WorkDoneEvent> {

    @Autowired
    private FeishuConfigService feishuConfigService;

    @Autowired
    private FeishuUserService feishuUserService;

    @Autowired
    private FeishuWorkRecordService feishuWorkRecordService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Override
    public boolean onAddEvent(List<EventListenerPair> eventListenerPairs, ApplicationEvent event) {
        boolean needExecute = true;
        WorkDoneEvent doneEvent = (WorkDoneEvent) event;
        for (EventListenerPair eventListenerPair : eventListenerPairs) {
            if (eventListenerPair.getEvent() instanceof WorkTodoEvent) {
                WorkTodoEvent todoEvent = (WorkTodoEvent) eventListenerPair.getEvent();
                Set<String> userIds = todoEvent.getUserIds();
                TaskInstance taskInstance = todoEvent.getTaskInstance();
                if (userIds == null || taskInstance == null) {
                    continue;
                }
                if (false == StringUtils.equals(doneEvent.getTaskInstUuid(), taskInstance.getUuid())) {
                    continue;
                }
                if (userIds.contains(doneEvent.getUserId())) {
                    needExecute = false;// 当前待办更新事件不执行
                    userIds.remove(doneEvent.getUserId());// 移除待办用户
                    if (userIds.isEmpty()) {
                        // 办理人为空，忽略待办推送事件
                        eventListenerPair.markIgnoreExecute();
                    }
                }
            } else if (eventListenerPair.getEvent() instanceof WorkDoneEvent) {
                WorkDoneEvent doneEvent2 = (WorkDoneEvent) eventListenerPair.getEvent();
                if (doneEvent.equals(doneEvent2) && this.equals(eventListenerPair.getListener())) {
                    needExecute = true;
                    eventListenerPair.markIgnoreExecute();// 忽略重复的事件
                }
            }
        }
        return needExecute;
    }

    @Override
    public void onApplicationEvent(WorkDoneEvent event) {
        String flowInstUuid = event.getFlowInstUuid();
        String taskInstUuid = event.getTaskInstUuid();
        // 流程数据未持久化（比如办理人异常）
        if (StringUtils.isBlank(flowInstUuid) || StringUtils.isBlank(taskInstUuid)) {
            return;
        }
        String system = event.getSystem();
        String tenant = event.getTenant();
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
        List<FeishuWorkRecordEntity> workRecords = feishuWorkRecordService.listByTaskInstUuidAndTypeAndState(taskInstUuid, FeishuWorkRecordEntity.Type.SYSTEM, FeishuWorkRecordEntity.State.Sent);
        if (FeishuGroupChatUtils.isGroupChat(workRecords)) {
            FeishuWorkRecordEntity groupChatEntity = FeishuGroupChatUtils.getGroupChat(workRecords);
//            // 提交环节，发送群聊消息
//            if (WorkFlowOperation.isActionCodeOfSubmit(taskData.getActionCode(taskInstUuid))
//                    || WorkFlowOperation.isActionTypeOfSubmit(taskData.getActionType(taskInstUuid + taskData.getUserId()))) {
            sendChatMessage(groupChatEntity, event, feishuConfigVo);
//            }

            // 群聊结束，获取聊天记录到办理过程中
            List<String> todoUserIds = taskService.getTodoUserIds(taskInstUuid);
            if (CollectionUtils.isEmpty(todoUserIds)) {
                if (FeishuGroupChatUtils.isSaveContent2Flow(feishuConfigVo)
                        && !ActionCode.CANCEL.getCode().equals(taskData.getActionCode(taskInstUuid))) {
                    saveChatMessage2WorkProcess(groupChatEntity, event, feishuConfigVo);
                }
                // 环节结束，解散群聊
                if (FeishuGroupChatUtils.isDeleteChatOnTaskEnd(feishuConfigVo)) {
                    List<FeishuWorkRecordEntity> workRecordEntities = feishuWorkRecordService.listGroupChatByFlowInstUuidAndTypeAndState(flowInstUuid, FeishuWorkRecordEntity.Type.SYSTEM, FeishuWorkRecordEntity.State.Sent);
                    workRecordEntities.forEach(workRecordEntity -> FeishuApiUtils.cancelGroupChat(workRecordEntity, feishuConfigVo));
                    feishuWorkRecordService.saveAll(workRecordEntities);
                }
            }
        }

        // 流程结束，解散群聊
        List<String> taskInstUuids = taskService.getUnfinishedTaskInstanceUuids(flowInstUuid);
        if (CollectionUtils.isEmpty(taskInstUuids)) {
            List<FeishuWorkRecordEntity> workRecordEntities = feishuWorkRecordService.listGroupChatByFlowInstUuidAndTypeAndState(flowInstUuid, FeishuWorkRecordEntity.Type.SYSTEM, FeishuWorkRecordEntity.State.Sent);
            workRecordEntities.forEach(workRecordEntity -> FeishuApiUtils.cancelGroupChat(workRecordEntity, feishuConfigVo));
            feishuWorkRecordService.saveAll(workRecordEntities);
        }
    }

    private void saveChatMessage2WorkProcess(FeishuWorkRecordEntity feishuWorkRecord, WorkDoneEvent event, FeishuConfigVo feishuConfigVo) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String flowInstUuid = event.getFlowInstUuid();
        TaskOperation taskOperation = getUserLastestTaskOperation(userId, feishuWorkRecord.getTaskInstUuid(), feishuWorkRecord.getFlowInstUuid());
        if (taskOperation == null) {
            return;
        }

        List<Message> messages = FeishuApiUtils.listChatMessage(feishuWorkRecord, feishuConfigVo);
        List<FeishuMessage> feishuMessages = convertMessage(messages, feishuConfigVo);
        List<String> fileIds = Lists.newArrayList();
        // 群聊消息转为文件
        if (FeishuGroupChatUtils.isSaveTextContent2Flow(feishuConfigVo)) {
            InputStream inputStream = messages2InputStream(feishuMessages, feishuConfigVo);
            MongoFileEntity mongoFileEntity = mongoFileService.saveFile("群聊: " + feishuWorkRecord.getTitle() + ".txt", inputStream);
            IOUtils.closeQuietly(inputStream);
            fileIds.add(mongoFileEntity.getFileID());
        }
        // 下载会话文件
        if (FeishuGroupChatUtils.isSaveTextAndFileContent2Flow(feishuConfigVo)) {
            fileIds.addAll(FeishuApiUtils.downloadResource(feishuMessages, feishuConfigVo, mongoFileService));
        }
        // 文件放入夹
        if (CollectionUtils.isNotEmpty(fileIds)) {
            mongoFileService.pushFilesToFolder(flowInstUuid, fileIds, taskOperation.getUuid());
        }
    }

    private TaskOperation getUserLastestTaskOperation(String userId, String taskInstUuid, String flowInstUuid) {
        List<TaskOperation> taskOperations = taskOperationService.listByTaskInstUuid(taskInstUuid);
        TaskOperation taskOperation = taskOperations.stream().sorted(IdEntityComparators.CREATE_TIME_DESC).filter(item -> StringUtils.equals(item.getAssignee(), userId))
                .findFirst().orElse(null);
        return taskOperation != null ? taskOperation : taskOperationService.getLastestByUserId(userId, flowInstUuid);
    }

    private List<FeishuMessage> convertMessage(List<Message> messages, FeishuConfigVo feishuConfigVo) {
        Set<String> senderIdSet = messages.stream().map(Message::getSender).map(Sender::getId).collect(Collectors.toSet());
        Map<String, String> userNameMap = feishuUserService.getUserNamesByOpenIds(senderIdSet, feishuConfigVo.getAppId());

        List<FeishuMessage> feishuMessage = Lists.newArrayList();
        FeishuMessageParser messageParser = new FeishuMessageParser();
        messages.forEach(message -> {
            Sender sender = message.getSender();
            if (StringUtils.isBlank(sender.getId())) {
                return;
            }
            feishuMessage.add(messageParser.parse(message, userNameMap, feishuConfigVo));
        });
        return feishuMessage;
    }

    private InputStream messages2InputStream(List<FeishuMessage> messages, FeishuConfigVo feishuConfigVo) {
        StringBuilder sb = new StringBuilder();
        messages.forEach(message -> {
            sb.append(message.getSenderName() + " " + DateUtils.formatDateTime(message.getSendTime()));
            sb.append("\n");
            sb.append(message.getTextMessage());
            sb.append("\n\r");
        });

        return IOUtils.toInputStream(sb.toString());
    }

    private void sendChatMessage(FeishuWorkRecordEntity feishuWorkRecord, WorkDoneEvent event, FeishuConfigVo feishuConfigVo) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = event.getTaskData();
        String key = taskData.getTaskInstUuid() + taskData.getUserId();
        String opinionText = taskData.getOpinionText(key);
        String actionType = taskData.getActionType(key);
        String actionName = WorkFlowOperation.getName(actionType);
        if (StringUtils.isBlank(actionName)) {
            actionName = actionType;
        }
        String actionUserNameKey = StringUtils.substring(actionType, 0, 1).toLowerCase() + StringUtils.substring(actionType, 1) + "UserNames";
        String actionUserNames = Objects.toString(taskData.get(actionUserNameKey), StringUtils.EMPTY);
        String actionWidthUser = StringUtils.isNotBlank(actionUserNames) ? (actionName + " " + actionUserNames) : actionName;
        FeishuApiUtils.sendUserChatMessage(userDetails, actionWidthUser, opinionText, feishuWorkRecord, feishuConfigVo);
    }

}

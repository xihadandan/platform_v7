/*
 * @(#)2013-4-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.util;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.message.MessageSenderDispatcher;
import com.wellsoft.pt.bpm.engine.message.event.WorkMessageSendEvent;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.rule.engine.RuleEngine;
import com.wellsoft.pt.rule.engine.RuleEngineFactory;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
public class MessageClientUtils {

    protected static Logger logger = LoggerFactory.getLogger(MessageClientUtils.class);

    // private static MessageClientApiFacade messageClientApiFacade;

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param taskInstance
     * @param flowInstance
     * @param userId
     */
    public static void send(TaskData taskData, WorkFlowMessageTemplate templateId, List<MessageTemplate> templates,
                            TaskInstance taskInstance, FlowInstance flowInstance, String userId, ParticipantType participantType) {
        // 不发送消息
        if (!taskData.isSendMsg()) {
            return;
        }
        if (templates == null || templates.isEmpty()) {
            // logger.warn("没有为消息类型为[" + templateId.getType() + "]的消息指定消息模板!");
            return;
        }
        /* modified by huanglinchuan 2014.10.21 begin */
        // 自定义消息发送调度器
        Object messageSenderDispatcher = taskData.getCustomData(CustomRuntimeData.KEY_MESSAGE_SENDER_DISPATCHER);
        if (messageSenderDispatcher != null && StringUtils.isNotBlank(messageSenderDispatcher.toString())) {
            for (MessageTemplate template : templates) {
                // 判断消息分发设置中的控制开关是否打开
                if (StringUtils.trimToEmpty(template.getIsSendMsg()).equals("")
                        || StringUtils.trimToEmpty(template.getIsSendMsg()).equals("0")) {
                    continue;
                }
                MessageSenderDispatcher dispatcher = ApplicationContextHolder.getBean(
                        messageSenderDispatcher.toString(), MessageSenderDispatcher.class);
                dispatcher.onSend(flowInstance, taskInstance, taskData, templateId, userId, participantType);

                List<UserUnitElement> msgRecipients = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(template.getMsgRecipients())) {
                    msgRecipients.addAll(template.getMsgRecipients());
                }
                if (CollectionUtils.isNotEmpty(template.getExtraMsgRecipients())) {
                    msgRecipients.addAll(template.getExtraMsgRecipients());
                }

                if (CollectionUtils.isNotEmpty(msgRecipients)) {
                    Node currentTaskNode = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition())
                            .getTaskNode(taskInstance.getId());
                    IdentityResolverStrategy identityResolverStrategy = ApplicationContextHolder
                            .getBean(IdentityResolverStrategy.class);
                    List<FlowUserSid> extraMsgRecipientUserIds = identityResolverStrategy.resolve(currentTaskNode,
                            taskData.getToken(), msgRecipients, participantType);
                    for (FlowUserSid extraUserId : extraMsgRecipientUserIds) {
                        dispatcher.onSend(flowInstance, taskInstance, taskData, templateId, extraUserId.getId(),
                                participantType);
                    }
                }
            }

        } else {
            send(taskData, templateId, templates, taskInstance, flowInstance,
                    StringUtils.isNotBlank(userId) ? Arrays.asList(userId) : null, participantType);
        }

        /* modified by huanglinchuan 2014.10.21 end */
    }

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param taskInstance
     * @param flowInstance
     * @param userIds
     */
    public static void send(TaskData taskData, WorkFlowMessageTemplate templateId, List<MessageTemplate> templates,
                            TaskInstance taskInstance, FlowInstance flowInstance, Collection<String> userIds,
                            ParticipantType participantType) {
        // 不发送消息
        if (!taskData.isSendMsg()) {
            return;
        }
        if (templates == null || templates.isEmpty()) {
            // logger.warn("没有为消息类型为[{}]的消息指定消息模板!", templateId.getType());
            return;
        }

//        if (messageClientApiFacade == null) {
//            initMessageClientApi();
//        }

        List<IdEntity> entities = getIdEntities(taskInstance, flowInstance);
        Map<Object, Object> dyformValues = getDyformValues(taskData, taskInstance, flowInstance);
        TaskInstance msgTaskInstance = null;
        if (CollectionUtils.isNotEmpty(entities) && entities.get(0) instanceof TaskInstance) {
            msgTaskInstance = (TaskInstance) entities.get(0);
        }

        /* modified by huanglinchuan 2014.10.21 begin */
        // 判断消息分发设置中的控制开关是否打开
        for (MessageTemplate template : templates) {
            if (template == null || StringUtils.trimToEmpty(template.getIsSendMsg()).equals("")
                    || StringUtils.trimToEmpty(template.getIsSendMsg()).equals("0")) {
                continue;
            }

            // 约束条件判断
            String condition = template.getCondition();
            if (StringUtils.isNotBlank(condition)) {
                DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
                RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
                ruleEngine.setVariable("dyFormData", dyFormData);
                ruleEngine.setVariable("dyform", dyformValues);
                if (taskInstance != null) {
                    ruleEngine.setVariable("actionType", taskInstance.getActionType());
                    ruleEngine.setVariable("taskId", taskInstance.getId());
                }
                String exp = condition;
                String scriptText = "if (" + exp + "){ set conditionResult = true end } end";
                ruleEngine.execute(scriptText);
                Object conditionResult = ruleEngine.getVariable("conditionResult");
                if (!Boolean.TRUE.equals(conditionResult)) {
                    logger.warn("消息类型为[" + templateId.getType() + "]的消息无法满足约束条件[" + condition + "]!");
                    continue;
                }
            }

            Set<String> toSendUserIds = new LinkedHashSet<String>();
            if (userIds != null) {
                for (String userId : userIds) {
                    if (StringUtils.isNotBlank(userId)) {
                        toSendUserIds.add(userId);
                    }
                }
            }

            // 解析额外设置的消息抄送对象
            if (taskInstance != null && flowInstance != null) {
                List<UserUnitElement> msgRecipUnitElements = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(template.getMsgRecipients())) {// 指定人员
                    msgRecipUnitElements.addAll(template.getMsgRecipients());
                }
                if (CollectionUtils.isNotEmpty(template.getExtraMsgRecipients())) {// 额外抄送人员
                    msgRecipUnitElements.addAll(template.getExtraMsgRecipients());
                }
                if (!msgRecipUnitElements.isEmpty()) {
                    Node currentTaskNode = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition())
                            .getTaskNode(taskInstance.getId());
                    IdentityResolverStrategy identityResolverStrategy = ApplicationContextHolder
                            .getBean(IdentityResolverStrategy.class);
                    List<FlowUserSid> flowUserSidList = identityResolverStrategy.resolve(currentTaskNode,
                            taskData.getToken(), msgRecipUnitElements, ParticipantType.CopyUser);
                    for (int i = 0; flowUserSidList != null && i < flowUserSidList.size(); i++) {
                        toSendUserIds.add(flowUserSidList.get(i).getId());
                    }
                }
            }
            if (toSendUserIds.isEmpty()) {
                continue;
            }

            String dataUuid = flowInstance != null ? flowInstance.getUuid() : null;
            String sendWay = taskData.getMsgSendWay();
            WorkMessageSendEvent workMessageSendEvent = new WorkMessageSendEvent(dataUuid, sendWay, taskInstance, taskData,
                    flowInstance, template, entities, dyformValues, toSendUserIds, RequestSystemContextPathResolver.system());
            ApplicationContextHolder.getApplicationContext().publishEvent(workMessageSendEvent);
            // messageClientApiFacade.send(template.getId(), sendWay, entities, dyformValues, toSendUserIds, msgTaskInstance, dataUuid, null);
        }
        /* modified by huanglinchuan 2014.10.21 end */
    }

    public static List<IdEntity> getIdEntities(TaskInstance taskInstance, FlowInstance flowInstance) {
        List<IdEntity> entities = new ArrayList<IdEntity>();
        TaskInstance msgTaskInstance = null;
        if (taskInstance != null) {
            msgTaskInstance = new TaskInstance();
            BeanUtils.copyProperties(taskInstance, msgTaskInstance);
            entities.add(msgTaskInstance);
        } else {
            // logger.error("Task instance for message template [" + templateId + "] is null!");
        }
        if (flowInstance != null) {
            FlowInstance msgFlowInstance = new FlowInstance();
            BeanUtils.copyProperties(flowInstance, msgFlowInstance);
            entities.add(msgFlowInstance);
            if (msgTaskInstance != null) {
                msgTaskInstance.setFlowInstance(msgFlowInstance);
            }
        } else {
            //logger.error("Flow instance for message template [" + templateId + "] is null!");
        }
        return entities;
    }

    /**
     * @param taskData
     * @param taskInstance
     * @param flowInstance
     * @return
     */
    public static Map<Object, Object> getDyformValues(TaskData taskData, TaskInstance taskInstance, FlowInstance flowInstance) {
        Map<Object, Object> dyformValues = new HashMap<>(0);
        // 加入动态表单数据
        DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
        if (dyFormData == null) {
            DyFormFacade dyFormApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            if (StringUtils.isNotBlank(taskData.getFormUuid()) && StringUtils.isNotBlank(taskData.getDataUuid())) {
                dyFormData = dyFormApiFacade.getDyFormData(taskData.getFormUuid(), taskData.getDataUuid());
            } else if (taskInstance != null) {
                dyFormData = dyFormApiFacade.getDyFormData(taskInstance.getFormUuid(), taskInstance.getDataUuid());
            }
            taskData.setDyFormData(taskData.getDataUuid(), dyFormData);
        }
        Map<String, List<Map<String, Object>>> displayValueMap = dyFormData.getDisplayValuesKeyAsFormId();
        dyformValues.putAll(displayValueMap);
        Map<String, Object> formDataOfMainform = dyFormData.getFormDataOfMainform();
        if (MapUtils.isNotEmpty(formDataOfMainform)) {
            dyformValues.putAll(formDataOfMainform);
        }
        if (taskInstance != null) {
            dyformValues.put("taskInstanceUuid", taskInstance.getUuid());
            String key = taskInstance.getUuid() + taskData.getUserId();
            String preTaskInstUuid = taskData.getPreTaskInstUuid(taskInstance.getId());
            String preKey = preTaskInstUuid + taskData.getUserId();
            String opinionLabel = taskData.getOpinionLabel(key);
            String opinionValue = taskData.getOpinionValue(key);
            String opinionText = taskData.getOpinionText(key);
            List<LogicFileInfo> opinionFiles = taskData.getOpinionFiles(key);
            if (StringUtils.isBlank(opinionLabel) && StringUtils.isBlank(opinionValue)
                    && StringUtils.isBlank(opinionText) && CollectionUtils.isEmpty(opinionFiles)) {
                opinionLabel = taskData.getOpinionLabel(preKey);
                opinionValue = taskData.getOpinionValue(preKey);
                opinionText = taskData.getOpinionText(preKey);
                opinionFiles = taskData.getOpinionFiles(preKey);
            }
            dyformValues.put("opinionLabel", opinionLabel);
            dyformValues.put("opinionValue", opinionValue);
            dyformValues.put("opinionText", opinionText);
            dyformValues.put("opinionFiles", opinionFiles);
            dyformValues.put("oldTodoUserIds", taskData.get("oldTodoUserIds"));
            dyformValues.put("oldTodoUserNames", taskData.get("oldTodoUserNames"));
            dyformValues.put("newTodoUserIds", taskData.get("newTodoUserIds"));
            dyformValues.put("newTodoUserNames", taskData.get("newTodoUserNames"));
            dyformValues.put("limitTime", taskData.get("limitTime"));
        }
        dyformValues.put("flowInstanceUuid", flowInstance.getUuid());
        // 附加数据
        for (String key : taskData.getCustomDataKeySet()) {
            dyformValues.put(key, taskData.getCustomData(key));
        }
        return dyformValues;
    }

    /**
     *
     */
//    private static void initMessageClientApi() {
//        messageClientApiFacade = ApplicationContextHolder.getBean(MessageClientApiFacade.class);
//    }

    /**
     * @param wfWorkDirectionSendMsg
     * @param direction
     * @param userIds
     */
    public static void send(WorkFlowMessageTemplate templateId, List<MessageTemplate> messageTemplates,
                            Direction direction, Node to, Token token, List<FlowUserSid> userIds) {
        // 不发送消息
        if (!token.getTaskData().isSendMsg()) {
            return;
        }
        if (messageTemplates == null || messageTemplates.isEmpty()) {
            // logger.warn("没有为消息类型为[" + templateId.getType() + "]的消息指定消息模板!");
            return;
        }

//        if (messageClientApiFacade == null) {
//            initMessageClientApi();
//        }

        List<IdEntity> entities = new ArrayList<IdEntity>();
        entities.add(direction);
        TaskInstance taskInstance = token.getTask();
        FlowInstance flowInstance = token.getFlowInstance();
        entities.addAll(getIdEntities(taskInstance, flowInstance));

        // 加入动态表单数据
        DyFormData dyFormData = null;
        Map<Object, Object> dyformValues = new HashMap<Object, Object>(0);
        /* modified by huanglinchuan 2014.10.27 begin */
        // 判断消息分发设置中的控制开关是否打开
        for (MessageTemplate template : messageTemplates) {
            if (template == null || StringUtils.trimToEmpty(template.getIsSendMsg()).equals("")
                    || StringUtils.trimToEmpty(template.getIsSendMsg()).equals("0")) {
                continue;
            }

            // 加入动态表单数据
            if (dyformValues.isEmpty()) {
                dyFormData = token.getTaskData().getDyFormData(token.getTaskData().getDataUuid());
                if (dyFormData == null) {
                    DyFormFacade dyFormApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
                    dyFormData = dyFormApiFacade.getDyFormData(token.getTaskData().getFormUuid(), token.getTaskData()
                            .getDataUuid());
                }
                Map<String, Object> mainData = dyFormData.getFormDataOfMainform();
                for (String key : mainData.keySet()) {
                    dyformValues.put(key, mainData.get(key));
                }
            }

            // 约束条件判断
            String condition = template.getCondition();
            if (StringUtils.isNotBlank(condition)) {
                String toTaskId = direction.getToID();
                if (StringUtils.equals(FlowConstant.END_FLOW_ID, toTaskId)) {
                    toTaskId = FlowConstant.END_FLOW;
                }
                RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
                ruleEngine.setVariable("dyFormData", dyFormData);
                ruleEngine.setVariable("node", to);
                ruleEngine.setVariable("token", token);
                ruleEngine.setVariable("directionId", direction.getId());
                ruleEngine.setVariable("fromTaskId", direction.getFromID());
                ruleEngine.setVariable("toTaskId", toTaskId);
                ruleEngine.setVariable("dyform", dyformValues);
                String exp = condition;
                String scriptText = "if (" + exp + "){ set conditionResult = true end } end";
                ruleEngine.execute(scriptText);
                Object conditionResult = ruleEngine.getVariable("conditionResult");
                if (!Boolean.TRUE.equals(conditionResult)) {
                    logger.warn("消息类型为[" + templateId.getType() + "]的消息无法满足约束条件[" + condition + "]!");
                    continue;
                }
            }

            Set<FlowUserSid> toSendUserIds = new LinkedHashSet<FlowUserSid>();
            if (userIds != null) {
                for (FlowUserSid userId : userIds) {
                    toSendUserIds.add(userId);
                }
            }

            List<UserUnitElement> msgRecipUnitElements = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(template.getMsgRecipients())) {
                msgRecipUnitElements.addAll(template.getMsgRecipients());
            }
            if (CollectionUtils.isNotEmpty(template.getExtraMsgRecipients())) {
                msgRecipUnitElements.addAll(template.getExtraMsgRecipients());
            }
            if (!msgRecipUnitElements.isEmpty()) {
                Node currentTaskNode = token.getFlowDelegate().getTaskNode(direction.getFromID());
                IdentityResolverStrategy identityResolverStrategy = ApplicationContextHolder
                        .getBean(IdentityResolverStrategy.class);
                List<FlowUserSid> flowUserSidList = identityResolverStrategy.resolve(currentTaskNode, token,
                        msgRecipUnitElements, ParticipantType.CopyUser);
                for (int i = 0; flowUserSidList != null && i < flowUserSidList.size(); i++) {
                    toSendUserIds.add(flowUserSidList.get(i));
                }
            }

            if (toSendUserIds.isEmpty()) {
                continue;
            }

            // 取出组织ID
            List<String> toSendOrgIds = new ArrayList<String>();
            for (FlowUserSid toSendUserId : toSendUserIds) {
                toSendOrgIds.add(toSendUserId.getId());
            }

            String dataUuid = flowInstance != null ? flowInstance.getUuid() : null;
            String sendWay = token.getTaskData().getMsgSendWay();
            WorkMessageSendEvent workMessageSendEvent = new WorkMessageSendEvent(dataUuid, sendWay, taskInstance, token.getTaskData(),
                    flowInstance, template, entities, dyformValues, toSendOrgIds, RequestSystemContextPathResolver.system());
            ApplicationContextHolder.getApplicationContext().publishEvent(workMessageSendEvent);
            // messageClientApiFacade.send(template.getId(), entities, dyformValues, toSendOrgIds);
            /* modified by huanglinchuan 2014.10.27 end */
        }
    }
}

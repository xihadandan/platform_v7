/*
 * @(#)8/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.message.listener;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.log.model.FlowMessageSendLogModel;
import com.wellsoft.pt.bpm.engine.log.service.FlowLogService;
import com.wellsoft.pt.bpm.engine.message.event.WorkMessageSendEvent;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.facade.service.MessageTemplateApiFacade;
import com.wellsoft.pt.message.support.MessageExtraParm;
import com.wellsoft.pt.message.support.MessageSendResult;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/21/25.1	    zhulh		8/21/25		    Create
 * </pre>
 * @date 8/21/25
 */
@Component
public class WorkMessageSendEventListener extends WellptTransactionalEventListener<WorkMessageSendEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    @Autowired
    private MessageTemplateApiFacade messageTemplateApiFacade;

    @Autowired
    private FlowLogService flowLogService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Override
    public void onApplicationEvent(WorkMessageSendEvent event) {
        String msgId = null;
        try {
            RequestSystemContextPathResolver.setSystem(event.getSystem());
            msgId = sendWorkMessage(event);
        } catch (Exception e) {
            logger.error("发送工作消息失败", e);
        } finally {
            logSendWorkMessage(msgId, event);
            RequestSystemContextPathResolver.clear();
        }
    }

    private String sendWorkMessage(WorkMessageSendEvent event) {
        String dataUuid = event.getDataUuid();
        String sendWay = event.getSendWay();
//        TaskInstance taskInstance = event.getTaskInstance();
//        TaskData taskData = event.getTaskData();
        FlowInstance flowInstance = event.getFlowInstance();
        MessageTemplate template = event.getTemplate();
        List<IdEntity> entities = event.getEntities();
        Map<Object, Object> dyformValues = event.getDyformValues();
        Collection<String> toSendOrgIds = event.getToSendOrgIds();
        String msgId = SnowFlake.getId() + StringUtils.EMPTY;
        MessageExtraParm messageExtraParm = new MessageExtraParm();
        messageExtraParm.setMessageid(msgId);
        messageExtraParm.setSystemid(flowInstance.getSystem());
        messageClientApiFacade.send(template.getId(), sendWay, entities, dyformValues, toSendOrgIds, messageExtraParm, dataUuid, null);
        return msgId;
    }

    private void logSendWorkMessage(String msgId, WorkMessageSendEvent event) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String sendWay = event.getSendWay();
        TaskInstance taskInstance = event.getTaskInstance();
        // TaskData taskData = event.getTaskData();
        FlowInstance flowInstance = event.getFlowInstance();
        MessageTemplate template = event.getTemplate();
        // List<IdEntity> entities = event.getEntities();
        // Map<Object, Object> dyformValues = event.getDyformValues();
        Collection<String> toSendOrgIds = event.getToSendOrgIds();
        Map<String, String> toSendOrgNames = workflowOrgService.getNamesByIds(Lists.newArrayList(toSendOrgIds));

        FlowMessageSendLogModel logModel = new FlowMessageSendLogModel();
        logModel.setTitle(flowInstance.getTitle());
        logModel.setFlowName(flowInstance.getName());
        logModel.setFlowDefId(flowInstance.getId());
        logModel.setFlowInstUuid(flowInstance.getUuid());
        if (taskInstance != null) {
            logModel.setTaskInstUuid(taskInstance.getUuid());
        } else {
            logModel.setTaskInstUuid(taskService.getLastTaskInstanceUuidByFlowInstUuid(flowInstance.getUuid()));
        }
        logModel.setMsgId(msgId);
        logModel.setMsgTypeName(template.getTypeName());
        logModel.setMsgType(template.getId());
        logModel.setSenderName(userDetails.getUserName());
        logModel.setSenderId(userDetails.getUserId());
        logModel.setRecipientName(StringUtils.join(toSendOrgNames.values(), Separator.SEMICOLON.getValue()));
        logModel.setRecipientId(StringUtils.join(toSendOrgNames.keySet(), Separator.SEMICOLON.getValue()));
        if (StringUtils.isNotBlank(sendWay)) {
            logModel.setSendWay(sendWay);
        } else {
            com.wellsoft.pt.message.entity.MessageTemplate messageTemplate = messageTemplateApiFacade.getById(template.getId());
            logModel.setSendWay(messageTemplate.getSendWay());
        }
        logModel.setSendTime(Calendar.getInstance().getTime());
        logModel.setSendResultCode(MessageSendResult.SENDING);
        logModel.setDetails(StringUtils.EMPTY);
        logModel.setCreateTime(Calendar.getInstance().getTime());
        logModel.setSystem(event.getSystem());
        logModel.setTenant(userDetails.getTenantId());

        flowLogService.logMessageSend(logModel);
    }

}

/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.client.impl;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.message.client.MessageClient;
import com.wellsoft.pt.message.client.MessageResolver;
import com.wellsoft.pt.message.entity.MessageOutbox;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.message.service.MessageOutboxService;
import com.wellsoft.pt.message.service.MessageQueueService;
import com.wellsoft.pt.message.service.ScheduleMessageQueueService;
import com.wellsoft.pt.message.support.JmsMessage;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageExtraParm;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.entity.Duty;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.DutyService;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
@Service
@Transactional
public class JmsMessageProducer implements MessageClient {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageInboxService messageInboxService;
    @Autowired
    private MessageOutboxService messageOutboxService;

    @Autowired(required = false)
    private MessageResolver messageResolver;

    @Autowired
    private JobService jobService;

    @Autowired
    private DutyService dutyService;
    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private MessageQueueService messageQueueService;

    @Autowired
    private ScheduleMessageQueueService scheduleMessageQueueService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.client.MessageClient#send(java.lang.String, com.wellsoft.pt.core.entity.IdEntity, java.util.Collection)
     */
    @Override
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> recipients) {
        Collection<ENTITY> entities = new ArrayList<ENTITY>();
        entities.add(entity);
        send(templateId, entities, null, recipients);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.client.MessageClient#send(java.lang.String, com.wellsoft.pt.core.entity.IdEntity, java.util.Map, java.util.Collection)
     */
    @Override
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> recipients) {
        Collection<ENTITY> entities = new ArrayList<ENTITY>();
        entities.add(entity);
        send(templateId, entities, dytableMap, recipients);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.client.MessageClient#send(java.lang.String, java.util.Collection, java.util.Collection)
     */
    @Override
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Collection<String> recipients) {
        send(templateId, entities, null, recipients);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.client.MessageClient#send(java.lang.String, java.util.Collection, java.util.List, java.util.Collection)
     */
    @Override
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> recipients) {
        send(templateId, entities, dytableMap, null, recipients);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.client.MessageClient#send(java.lang.String, java.util.Collection, java.util.Map, java.util.Map, java.util.Collection)
     */
    @Override
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Map<String, Object> root,
                                               Collection<String> recipients) {
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, entities, dytableMap, root,
                    recipients);
            // 发送消息
            if (jmsMessage != null) {
                sendMessage(jmsMessage, recipients, entities, null, templateId);

            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @Parma entity 額外接收參數
     * @see com.wellsoft.pt.message.client.MessageClient#send(java.lang.String, java.util.Collection, java.util.Map, java.util.Map, java.util.Collection)
     * webservice調用
     */
    @Override
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> recipients, ENTITY entity) {
        this.send(templateId, null, entities, dytableMap, recipients, entity, null, null);
    }

    @Override
    public <ENTITY extends IdEntity> void send(String templateId, String sendWays, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap, Collection<String> recipients,
                                               ENTITY entity, String dataUuid, String[] attachmentIds) {
        try {
            List<String> recipientList = new ArrayList<String>();
            for (String recipient : recipients) {
                recipientList.add(recipient);
            }
            try {
                //解析消息的发送来源网页，提供给消息内容解析来源链接
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                String host = request.getHeader("host");
                String referer = request.getHeader("referer").replaceFirst(host, "");
                if (dytableMap == null) {
                    dytableMap = Maps.newHashMap();
                }
                dytableMap.put("_referer", referer);
                dytableMap.put("_refererEncoded", java.net.URLEncoder.encode(referer, Charsets.UTF_8.name()));
            } catch (Exception e) {
                logger.warn("无法获取请求来源地址");
            }
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, entities, dytableMap, null,
                    recipients, entity, dataUuid, attachmentIds, sendWays);
            if (jmsMessage == null) {
                logger.error("JMS Message for template id [" + templateId + "] is null");
                return;
            }
            // 保存发件箱
            String correlationId = StringUtils.isBlank(dataUuid) ? UUID.randomUUID().toString() : dataUuid;
            jmsMessage.setCorrelationId(correlationId);
            // 保存发件箱
            MessageOutbox messageOutbox = null;
            List<Message> messages = new ArrayList<Message>();
            // 发送消息
            for (Message message : jmsMessage.getMessages()) {
                message.setSenderName(SpringSecurityUtils.getCurrentUserName());
                messages.add(message);
                if (messageOutbox == null) {
                    String systemid = null;
                    String messageOutbox_msgid = message.getDataUuid();
                    if (entity instanceof MessageExtraParm) {
                        MessageExtraParm parm = (MessageExtraParm) entity;
                        messageOutbox_msgid = parm.getMessageid();
                        systemid = parm.getSystemid();
                    }
                    String messageAttach = message.getAttach();
                    if (messageAttach != null && !"".equals(messageAttach)) {
                        String[] fileids = messageAttach.split(Separator.COMMA.getValue());
                        // 获取文件的物理ID以便跨租户的获取
                        List<String> phsId = Arrays.asList(fileids);
                        // message.setPhysicFileIds(mongoFileService.getphysicalFileIdByFileId(phsId));
                    }
                    if (!Message.TYPE_ONLINE_CANCEL.equals(message.getType())) {
                        messageOutbox = saveMessageOutbox(message, message.getRecipientNames(),
                                recipientList,
                                messageOutbox_msgid, systemid, correlationId);
                        message.setDataUuid(messageOutbox.getUuid());
                        message.setSystem(messageOutbox.getSystem());
                        message.setTenant(messageOutbox.getTenant());
                        messageOutbox = null;
                    } else {
                        Message msg = (Message) entity;
                        message.setDataUuid(msg.getDataUuid());
                    }
                } else {
                    if (!Message.TYPE_ONLINE_CANCEL.equals(message.getType())) {
                        message.setDataUuid(messageOutbox.getUuid());
                    } else {
                        Message msg = (Message) entity;
                        message.setDataUuid(msg.getDataUuid());
                    }
                }
            }
            if (jmsMessage != null) {
                jmsMessage.setMessages(messages);
                logger.error("jmsMessage " + jmsMessage);
                messageQueueService.send(jmsMessage);
            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }

        } catch (Exception e) {
            // logger.error(ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 保存发送记录
     *
     * @param recipientsTemp
     * @param recipientNames
     * @param content
     * @param businessId
     */
    private MessageOutbox saveMessageOutbox(Message mesg, List<String> recipientNames,
                                            List<String> recipientsTemp,
                                            String businessId, String systemid,
                                            String correlationId) {
        MessageOutbox outbox = new MessageOutbox();
        outbox.setTemplateId(mesg.getTemplateId());
        outbox.setName(mesg.getName());
        outbox.setType(mesg.getType());
        outbox.setSender(mesg.getSender());
        outbox.setRecipient(StringUtils.join(recipientsTemp, Separator.COMMA.getValue()));
        outbox.setRecipientName(StringUtils.join(recipientNames, Separator.COMMA.getValue()));
        outbox.setSubject(mesg.getSubject());
        outbox.setBody(mesg.getBody());
        outbox.setSentTime(mesg.getSentTime());
        outbox.setReceivedTime(new Date());
        outbox.setMessageId(businessId);
        outbox.setSystemid(systemid);
        outbox.setSystem(RequestSystemContextPathResolver.system());
        outbox.setTenant(SpringSecurityUtils.getCurrentTenantId());
        outbox.setRelatedUrl(mesg.getRelatedUrl());
        outbox.setRelatedTitle(mesg.getRelatedTitle());
        outbox.setIscancel(Boolean.FALSE);
        outbox.setCreateTime(new Date());
        outbox.setCreator(mesg.getSender());
        outbox.setCorrelationId(correlationId);
        if (mesg.getMessageLevel() == null) {
            outbox.setMarkFlag("0");
        } else {
            outbox.setMarkFlag(mesg.getMessageLevel());
        }
        if ("system".equals(mesg.getSender())) {
            outbox.setSenderName("系统");
            outbox.setName(mesg.getName());
        } else {
            if (!"unknown".equals(mesg.getSender())) {
                outbox.setSenderName(messageInboxService.getUserNameById(mesg.getSender()));
            }
            outbox.setName("在线消息");
        }
        messageOutboxService.saveMessageOutbox(outbox);

        String messageAttach = mesg.getAttach();
        if (messageAttach != null && !"".equals(
                messageAttach) && messageAttach != null && !"".equals(messageAttach)) {
            String[] fileids = messageAttach.split(Separator.COMMA.getValue());
            List<String> field_list = new ArrayList<String>();
            field_list = Arrays.asList(fileids);
            mongoFileService.pushFilesToFolder(outbox.getUuid(), field_list, "messageAttach");
        }
        return outbox;
    }

    /**
     * @param templateId
     * @param entities
     * @param dytableMap
     * @param recipients
     * @param entity
     * @param dataUuid   业务数据id
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> recipients, ENTITY entity,
                                               String dataUuid) {
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, entities, dytableMap, null,
                    recipients, entity,
                    dataUuid);
            // 发送消息
            if (jmsMessage != null) {
                sendMessage(jmsMessage, recipients, entities, entity, templateId);
            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public <ENTITY extends IdEntity> void cancelMessage(String dataUuid) {
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(dataUuid);
            // 取消消息
            if (jmsMessage != null) {
                messageQueueService.send(jmsMessage);
            } else {
                logger.error("JMS Message for datauuid id [" + dataUuid + "] is null");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> recipients,
                                               String dataUuid) {
        Collection<ENTITY> entities = new ArrayList<ENTITY>();
        entities.add(entity);
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, entities, null, null,
                    recipients, dataUuid);
            // 发送消息
            if (jmsMessage != null) {
                sendMessage(jmsMessage, recipients, entities, entity, templateId);
            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * @param templateId
     * @param entity
     * @param recipients
     * @param attachmentIds
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> recipients,
                                               String[] attachmentIds) {
        Collection<ENTITY> entities = new ArrayList<ENTITY>();
        entities.add(entity);
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, entities, null, null,
                    recipients, null,
                    attachmentIds);
            // 发送消息
            if (jmsMessage != null) {
                sendMessage(jmsMessage, recipients, entities, entity, templateId);
            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * @param templateId
     * @param entity
     * @param recipients
     * @param attachmentIds
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> recipients,
                                               String dataUuid, String[] attachmentIds) {
        Collection<ENTITY> entities = new ArrayList<ENTITY>();
        entities.add(entity);
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, entities, null, null,
                    recipients, dataUuid,
                    attachmentIds);
            // 发送消息
            if (jmsMessage != null) {
                sendMessage(jmsMessage, recipients, entities, entity, templateId);
            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param templateId
     * @param entity
     * @param recipients
     * @param dataUuid
     * @param attachmentIds
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> recipients,
                                               String dataUuid, String[] attachmentIds,
                                               String sendWays) {
        Collection<ENTITY> entities = new ArrayList<ENTITY>();
        entities.add(entity);
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, entities, null, null,
                    recipients, dataUuid,
                    attachmentIds, sendWays);
            // 发送消息
            if (jmsMessage != null) {
                sendMessage(jmsMessage, recipients, entities, entity, templateId);
            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 返回选择的接收者的名称
     *
     * @param orgIds
     * @return
     */
    private void getReceiversNameByOrgIds(List<String> orgIds, HashMap<String, String> users_map,
                                          List<String> receiveNames) {
        String name_temp = "";
        for (String orgId : orgIds) {
            if (orgId.startsWith(IdPrefix.USER.getValue())) {// U 用户
                receiveNames.add(users_map.get(orgId));
            } else if (orgId.startsWith(IdPrefix.DEPARTMENT.getValue())) { // D
                // 部门
                name_temp = getDepartmentName(orgId);
                receiveNames.add(name_temp);
            } else if (orgId.startsWith("J")) {// J 职位
                name_temp = getJobName(orgId);
                receiveNames.add(name_temp);
            } else if (orgId.startsWith(IdPrefix.GROUP.getValue())) {// G 群组
                name_temp = getGroupName(orgId);
                receiveNames.add(name_temp);
            } else if (orgId.startsWith("W")) {// W 职务
                name_temp = getDutyName(orgId);
                receiveNames.add(name_temp);
            }
        }

    }

    /***
     * 返回部门名称
     * @param id
     * @return
     */
    private String getDepartmentName(String id) {
        com.wellsoft.pt.org.entity.Department department = departmentService.getById(id);
        return department.getName();
    }

    /**
     * 取得职务名称
     *
     * @param id
     * @return
     */
    private String getJobName(String id) {
        Job job = jobService.getById(id);
        return job.getName();
    }

    /**
     * 取得职位名称
     *
     * @param id
     * @return
     */
    private String getDutyName(String id) {
        Duty duty = dutyService.getById(id);
        return duty.getName();
    }

    /**
     * 取得群组名称
     *
     * @param id
     * @return
     */
    private String getGroupName(String id) {
        MultiOrgGroup group = orgApiFacade.getGroupById(id);
        return group.getName();
    }

    /**
     * 根据租户进行用户分组
     *
     * @param recipients
     * @return
     * @author linz:
     * @date 创建时间：2015-7-21 上午11:22:53
     * @version 1.0
     * @parameter
     */
    private Map<String, List<String>> getMapTenantId(List<String> recipients) {
        Map<String, List<String>> hasMap = new HashMap<String, List<String>>();
        Set<String> setTenantId = getSetTenantId(recipients);
        List<String> userList = null;
        for (String tenTempId : setTenantId) {
            userList = new ArrayList<String>();
            for (String userId : recipients) {
                String tenantId = getTenantId(userId);
                if (tenantId.equals(tenTempId))
                    userList.add(userId);
            }
            hasMap.put(tenTempId, userList);
        }
        return hasMap;
    }

    private Set<String> getSetTenantId(List<String> recipients) {
        Set<String> setTenantId = new HashSet<String>();
        for (String userId : recipients) {
            String tenantId = getTenantId(userId);
            setTenantId.add(tenantId);
        }
        return setTenantId;
    }

    private String getTenantId(String userId) {
        return userId.substring(0, 4).replace(userId.substring(0, 1), "T");
    }

    private <ENTITY extends IdEntity> void sendMessage(JmsMessage jmsMessage,
                                                       Collection<String> recipients,
                                                       Collection<ENTITY> entities, ENTITY entity,
                                                       String templateId) {
        List<String> recipientList = new ArrayList<String>();
        for (String recipient : recipients) {
            recipientList.add(recipient);
        }
        // 保存发件箱
        String correlationId = StringUtils.isNotBlank(
                jmsMessage.getCorrelationId()) ? jmsMessage.getCorrelationId()
                : UUID.randomUUID().toString();
        jmsMessage.setCorrelationId(correlationId);
        // 保存发件箱
        MessageOutbox messageOutbox = null;
        List<Message> messages = new ArrayList<Message>();
        // 发送消息
        for (Message message : jmsMessage.getMessages()) {
            message.setSenderName(SpringSecurityUtils.getCurrentUserName());
            messages.add(message);
            if (messageOutbox == null) {
                String systemid = null;
                String messageOutbox_msgid = message.getDataUuid();
                if (entity instanceof MessageExtraParm) {
                    MessageExtraParm parm = (MessageExtraParm) entity;
                    messageOutbox_msgid = parm.getMessageid();
                    systemid = parm.getSystemid();
                }
                List<String> recipientNames = new ArrayList<String>();
                if (CollectionUtils.isNotEmpty(entities)) {
                    for (ENTITY entityTemp : entities) {
                        if (entity instanceof Message) {
                            Message message2 = (Message) entityTemp;
                            recipientNames = message2.getRecipientNames();
                        }
                    }
                }

                String messageAttach = message.getAttach();
                if (messageAttach != null && !"".equals(messageAttach)) {
                    String[] fileids = messageAttach.split(Separator.COMMA.getValue());
                    // 获取文件的物理ID以便跨租户的获取
                    List<String> phsId = Arrays.asList(fileids);
                    // message.setPhysicFileIds(mongoFileService.getphysicalFileIdByFileId(phsId));
                }
                if (recipientNames == null || recipientNames.size() == 0) {
                    recipientNames = message.getRecipientNames();
                }
                messageOutbox = saveMessageOutbox(message, recipientNames, recipientList,
                        messageOutbox_msgid,
                        systemid, correlationId);
                message.setDataUuid(messageOutbox.getUuid());

            } else {
                message.setDataUuid(messageOutbox.getUuid());
            }
        }
        if (jmsMessage != null) {
            jmsMessage.setMessages(messages);
            //即时发送的消息类型或者一分钟以内将要发送的消息，保存到实时发送消息队列
            if (MessageTemplate.SEND_TIME_IN_TIME.equals(jmsMessage.getSendTimeType())
                    || DateUtils.addMinutes(new Date(), 1).compareTo(
                    jmsMessage.getSendTime()) >= 0) {
                messageQueueService.send(jmsMessage);
                return;
            }
            //定时发送/工作时间发送的消息模板，保存到定时队列
            scheduleMessageQueueService.send(jmsMessage);

        } else {
            logger.error("JMS Message for template id [" + templateId + "] is null");
        }
    }

    public void send(String templateId, String dataJson, Collection<String> userIds) {
        this.send(templateId, new Gson().fromJson(dataJson, HashMap.class), userIds);
    }

    public void send(String templateId, Map<String, Object> dataMap, Collection<String> userIds) {
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, null, null,
                    dataMap, userIds);
            // 发送消息
            if (jmsMessage != null) {
                sendMessage(jmsMessage, userIds, null, null, templateId);
            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }
        } catch (Exception e) {
            logger.error("发送消息异常：", e);
        }
    }

    public void send(String templateId, Map<Object, Object> dataMap, Set<String> recipientIds,
                     Set<String> sentWays) {
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, null,
                    dataMap, null, recipientIds,
                    dataMap.containsKey("uuid") ? dataMap.get("uuid").toString() : null,
                    dataMap.containsKey("attachmentIds") ? (String[]) dataMap.get(
                            "attachmentIds") : null,
                    StringUtils.join(sentWays, ";"));
            // 发送消息
            if (jmsMessage != null) {
                sendMessage(jmsMessage, recipientIds, null, null, templateId);
            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }
        } catch (Exception e) {
            logger.error("发送消息异常：", e);
        }

    }

    public void send(String templateId, Map<Object, Object> dataMap, Map<String, Object> extraMap, Set<String> recipientIds,
                     Set<String> sentWays) {
        try {
            // 解析生成消息实体
            JmsMessage jmsMessage = messageResolver.resolve(templateId, null,
                    dataMap, extraMap, recipientIds,
                    dataMap.containsKey("uuid") ? dataMap.get("uuid").toString() : null,
                    dataMap.containsKey("attachmentIds") ? (String[]) dataMap.get(
                            "attachmentIds") : null,
                    StringUtils.join(sentWays, ";"));
            // 发送消息
            if (jmsMessage != null) {
                sendMessage(jmsMessage, recipientIds, null, null, templateId);
            } else {
                logger.error("JMS Message for template id [" + templateId + "] is null");
            }
        } catch (Exception e) {
            logger.error("发送消息异常：", e);
        }

    }
}

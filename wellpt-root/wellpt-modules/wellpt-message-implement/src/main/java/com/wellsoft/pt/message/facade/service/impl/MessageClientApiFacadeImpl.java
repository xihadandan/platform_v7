/*
 * @(#)2013-1-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.message.client.impl.JmsMessageProducer;
import com.wellsoft.pt.message.entity.MessageOutbox;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.service.MessageOutboxService;
import com.wellsoft.pt.message.service.ScheduleMessageQueueService;
import com.wellsoft.pt.message.service.ShortMessageService;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageParams;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Description: 消息发送对外接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-28.1	zhulh		2013-1-28		Create
 * </pre>
 * @date 2013-1-28
 */
@Service
public class MessageClientApiFacadeImpl extends AbstractApiFacade implements
        MessageClientApiFacade {
    @Autowired(required = false)
    private JmsMessageProducer jmsMessageProducer;

    @Autowired
    private ScheduleMessageQueueService scheduleMessageQueueService;
    @Autowired
    private ShortMessageService shortMessageService;
    @Autowired
    private MessageOutboxService messageOutboxService;

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entity     实体数据
     * @param recipients 接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> userIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entity, userIds);
        }
    }

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param dataJson   业务json数据
     * @param userIds    接收用户id集合
     */
    public void send(String templateId, String dataJson, Collection<String> userIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, dataJson, userIds);
        }
    }

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entity     实体数据
     * @param userIds    接收用户ID集合
     * @param datauuid
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> userIds,
                                               String datauuid) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entity, userIds, datauuid);
        }
    }

    /**
     * 发送消息
     *
     * @param templateId    模板ID
     * @param entity        实体数据
     * @param userIds       接收用户ID集合
     * @param datauuid
     * @param attachmentIds 附件ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> userIds,
                                               String datauuid, String[] attachmentIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entity, userIds, datauuid, attachmentIds);
        }
    }

    /**
     * 发送消息
     *
     * @param templateId    模板ID
     * @param sendWays      发送方式
     * @param entity        实体数据
     * @param userIds       接收用户ID集合
     * @param datauuid
     * @param attachmentIds 附件IDS
     */
    public <ENTITY extends IdEntity> void send(String templateId, String sendWays, ENTITY entity,
                                               Collection<String> userIds, String datauuid,
                                               String[] attachmentIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            if (sendWays == null || "".equals(sendWays)) {
                sendWays = "NONE";
            }
            jmsMessageProducer.send(templateId, entity, userIds, datauuid, attachmentIds, sendWays);
        }
    }

    /**
     * 发送消息
     *
     * @param templateId    模板ID
     * @param entity        实体数据
     * @param userIds       接收用户ID集合
     * @param attachmentIds 附件IDS
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> userIds,
                                               String[] attachmentIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entity, userIds, attachmentIds);
        }
    }

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entity     业务实体
     * @param dytableMap 表单数据
     * @param userIds    接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> userIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entity, dytableMap, userIds);
        }
    }

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entities   实体数据集合
     * @param userIds    接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Collection<String> userIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entities, userIds);
        }
    }

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entities   实体数据集合
     * @param dytableMap 表单数据
     * @param userIds    接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> userIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entities, dytableMap, userIds);
        }
    }

    @Override
    public <ENTITY extends IdEntity> void send(String templateId, String sendWays, Collection<ENTITY> entities, Map<Object, Object> dytableMap, Collection<String> userIds, String dataUuid, String[] attachmentIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            List<ENTITY> list = Lists.newArrayList(entities);
            ENTITY entity = CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
            jmsMessageProducer.send(templateId, sendWays, entities, dytableMap, userIds, entity, dataUuid, attachmentIds);
        }
    }

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entities   实体数据集合
     * @param dytableMap 表单数据
     * @param userIds    接收用户ID集合
     * @param entity     实体数据
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> userIds, ENTITY entity) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entities, dytableMap, userIds, entity);
        }
    }

    @Override
    public <ENTITY extends IdEntity> void send(String templateId, String sendWays, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap, Collection<String> userIds,
                                               ENTITY entity, String dataUuid, String[] attachmentIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, sendWays, entities, dytableMap, userIds, entity, dataUuid, attachmentIds);
        }
    }

    /**
     * @param templateId 模板ID
     * @param entities
     * @param dytableMap 表单数据
     * @param recipients 接收用户ID集合
     * @param entity     消息实体
     * @param dataUuid
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> recipients, ENTITY entity,
                                               String dataUuid) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entities, dytableMap, recipients, entity, dataUuid);
        }
    }

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entities   实体数据集合
     * @param dytableMap 表单数据
     * @param root       其他数据
     * @param userIds    接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Map<String, Object> root,
                                               Collection<String> userIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.send(templateId, entities, dytableMap, root, userIds);
        }
    }

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给互联网用户
     *
     * @param templateId 模板ID
     * @param entity     实体数据
     * @param extraData  额外数据
     * @param userIds    接收用户ID集合
     */
    @Override
    public <ENTITY extends IdEntity> void sendToInternetUser(String templateId, ENTITY entity, Map<String, Object> extraData,
                                                             Collection<String> userIds) {
        if (StringUtils.isBlank(templateId)) {
            logger.error("消息模板ID为空");
            return;
        }
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            if (extraData == null) {
                extraData = new HashMap<>();
            }
            extraData.put("recipientType", Message.RECIPIENT_TYPE_INTERNET_USER);
            Collection<ENTITY> entities = new ArrayList<>();
            entities.add(entity);
            jmsMessageProducer.send(templateId, entities, null, extraData, userIds);
        }
    }

    /**
     * 发送普通短信，手机号收信人不是系统内数据
     *
     * @param recipients   收信人ID
     * @param mbphones     收信人手机号
     * @param body         短信内容
     * @param businesskey1 业务键1
     * @param businesskey2 业务键2
     * @param businesskey3 业务键3
     * @return 发送结果
     */
    public String sendSmsMessages(String recipients, String mbphones, String body,
                                  String businesskey1,
                                  String businesskey2, String businesskey3) {
        if (StringUtils.isBlank(recipients) || StringUtils.isBlank(mbphones) || StringUtils.isBlank(
                body)) {
            return "fail";
        }
        if (recipients.split(";").length != mbphones.split(";").length) {
            return "fail";
        }
        shortMessageService.sendCommonMessages(recipients, mbphones, body, businesskey1,
                businesskey2, businesskey3);
        return "success";
    }

    /**
     * 发送普通短信，手机号收信人不是系统内数据
     *
     * @param recipients   收信人ID
     * @param mbphones     收信人手机号
     * @param body         短信内容
     * @param businesskey1 业务键1
     * @param businesskey2 业务键2
     * @param businesskey3 业务键3
     * @return 发送结果
     */
    public String sendSmsMessages(String recipients, String mbphones, String body,
                                  String businesskey1,
                                  String businesskey2, String businesskey3, Boolean async) {
        if (StringUtils.isBlank(recipients) || StringUtils.isBlank(mbphones) || StringUtils.isBlank(
                body)) {
            return "fail";
        }
        if (recipients.split(";").length != mbphones.split(";").length) {
            return "fail";
        }
        shortMessageService.sendCommonMessages(recipients, mbphones, body, businesskey1,
                businesskey2, businesskey3,
                async);
        return "success";
    }

    /**
     * 取消发件箱消息
     *
     * @param dataUuid
     */
    public <ENTITY extends IdEntity> void cancelMessage(String dataUuid) {
        if (jmsMessageProducer == null) {
            logger.error("消息服务模块没有开启，无法发送消息！");
        } else {
            jmsMessageProducer.cancelMessage(dataUuid);
        }
    }

    @Override
    public boolean cancelScheduleMessage(String businessId) {
        return scheduleMessageQueueService.deleteByBusinessId(businessId);
    }

    @Override
    public void sendByParams(MessageParams params) {
        if (StringUtils.isNotBlank(params.getTemplateId())) {//按模板发送
            jmsMessageProducer.send(params.getTemplateId(), params.getDataMap(), params.getExtraData(),
                    params.getRecipientIds(), params.getSentWays());
        } else if (CollectionUtils.isNotEmpty(params.getMobilePns())) {
            if (params.getMobilePns().size() != params.getRecipientNames().size()) {
                throw new RuntimeException("收件人名称与收件人手机号码数量不一致");
            }
            shortMessageService.sendCommonMessages(params.getRecipientNames(),
                    params.getMobilePns(), params.getContent(), params.getReservedTexts()[0],
                    params.getReservedTexts()[1], params.getReservedTexts()[2],
                    params.getAsync());
        }
    }

    @Override
    public void deleteByCorrelationId(String correlationId) {
        if (StringUtils.isBlank(correlationId)) {
            return;
        }
        messageOutboxService.deleteByCorrelationId(correlationId);
    }

    @Override
    public MessageOutbox getOutBoxByMessageId(String messageId) {
        if (StringUtils.isBlank(messageId)) {
            return null;
        }
        return messageOutboxService.getByMessageId(messageId);
    }

    @Override
    public Integer getSendResultCodeByMessageId(String messageId, String... sendWays) {
        return messageOutboxService.getSendResultCodeByMessageId(messageId, sendWays);
    }

}

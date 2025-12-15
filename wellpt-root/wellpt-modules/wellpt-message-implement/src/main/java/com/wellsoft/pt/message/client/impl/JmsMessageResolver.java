/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.client.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.message.client.MessageResolver;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.entity.WebServiceParm;
import com.wellsoft.pt.message.service.MessageEventService;
import com.wellsoft.pt.message.service.MessageTemplateService;
import com.wellsoft.pt.message.support.JmsMessage;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageExtraParm;
import com.wellsoft.pt.message.support.MessageWebserviceParm;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2012-11-9.1	zhulh		2012-11-9		Create
 * </pre>
 * @date 2012-11-9
 */
@Service
@Transactional
public class JmsMessageResolver implements MessageResolver {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;
    @Autowired
    private MessageEventService messageEventService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private OrgFacadeService orgFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.client.MessageResolver#resolve(java.lang.String, com.wellsoft.pt.core.entity.IdEntity, java.util.Collection)
     */
    @Override
    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                        Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, String datauuid) {

        // 设置JMS发送的消息
        JmsMessage jmsMessage = resolveCommon(templateId, entities, dataMap, extraData, recipients, null, datauuid,
                null, null);
        return jmsMessage;
    }

    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                        Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, String datauuid,
                                                        String[] attachmentIds) {

        // 设置JMS发送的消息
        JmsMessage jmsMessage = resolveCommon(templateId, entities, dataMap, extraData, recipients, null, datauuid,
                attachmentIds, null);
        return jmsMessage;
    }

    private <ENTITY extends IdEntity> JmsMessage resolveCommon(String templateId, Collection<ENTITY> entities,
                                                               Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity,
                                                               String datauuid, String[] attachmentIds, String sendWays_userdefine) {
        MessageTemplate template = messageTemplateService.getById(templateId);

        if (template == null) {
            logger.error("Template id [" + templateId + "] is not found");
            return null;
        }
        // 消息模板信息
        String name = template.getName(); // 名称 String category =
        // template.getCategory(); //分类标识
        String type = template.getType(); // 类型(系统、用户)

        String sendWay = template.getSendWay(); // 提醒方式
        if (sendWays_userdefine != null && !"".equals(sendWays_userdefine)) {
            sendWay = sendWays_userdefine;
        }
        String sendTimeType = template.getSendTime(); // 发送时间类型
        String mappingRule = template.getMappingRule(); // 映像规则
        // String messageEvent = template.getMessageEvent();//发送消息触发事件
        String isOnlinePopup = template.getIsOnlinePopup();// 在线消息弹窗提醒
        if (StringUtils.isBlank(sendWay)) {
            return null;
        }

        // 在线消息
        // String onlineSubject = template.getOnlineSubject(); //标题
        // String onlineBody = template.getOnlineBody(); //内容

        // 手机短信
        // String smsBody = template.getSmsBody(); //内容

        // 电子邮件
        // String emailSubject = template.getEmailSubject(); //标题
        // String emailBody = template.getEmailBody(); //内容

        String[] sendWays = sendWay.split(",|;"); // 支持逗号和分号分割
        Map<Object, Object> data = new HashMap<Object, Object>(0);
        if (sendWays.length != 0) {
            data = TemplateEngineFactory.getDefaultTemplateEngine().mergeDataAsMap(entities, dataMap, extraData, false,
                    false);
        }
        List<Message> messages = new ArrayList<Message>();
        final Date currentTime = Calendar.getInstance().getTime();
        Date timeToSend = currentTime;
        // 目前设置及时发送
        if (MessageTemplate.SEND_TIME_WORK_TIME.equals(sendTimeType)) {
            if (workTimePlanFacadeService.isWorkHour(null, currentTime)) {
                timeToSend = currentTime;
            } else {
                timeToSend = workTimePlanFacadeService.getWorkDate(null, currentTime, 0.000001d, WorkUnit.WorkingHour);
            }
        } else if (MessageTemplate.SEND_TIME_SCHEDULE_TIME.equals(sendTimeType)) {
            // 定时时间
            timeToSend = resolveSendTime(template, data);
        }

        if (template.getMessageEvent() != null && !"".equals(template.getMessageEvent())) {// 解析消息触发事件
            messageEventService.exeClientEventInstance(template.getMessageEvent(), templateId, entities, dataMap,
                    extraData, recipients, entity);
        }

        for (String way : sendWays) {
            Message msg = new Message();
            // 消息信息
            msg.setTemplateId(templateId); // 消息模板ID
            msg.setName(name); // 名称
            msg.setType(way); // 消息类型(在线、手机短信、邮件、WebService)，使用模板的提醒方式
            if (extraData != null
                    && StringUtils.equals((String) extraData.get("recipientType"), Message.RECIPIENT_TYPE_INTERNET_USER)) {
                //互联网用户消息
                msg.setRecipientType(Message.RECIPIENT_TYPE_INTERNET_USER);
            }
            boolean sender_is_system = true;// 是否要带入系统默认的发送者
            List<String> recipientName = new ArrayList<String>();
            if (entity != null) {// 外部传入的消息发送者
                if (entity instanceof MessageExtraParm) {
                    MessageExtraParm parm = (MessageExtraParm) entity;
                    msg.setSender(parm.getSender());
                    if (StringUtils.isNotBlank(parm.getSender())) {
                        sender_is_system = false;
                    }
                    msg.setExtraParm(entity);// 设置额外参数信息
                } else if (entity instanceof Message) {// 特别处理系统自带发送消息时，传入的数值
                    Message msg_temp = (Message) entity;
                    msg.setMessageLevel(msg_temp.getMessageLevel());
                    msg.setAttach(msg_temp.getAttach());
                    recipientName = msg_temp.getRecipientNames();
                    if (StringUtils.isNotBlank(msg_temp.getType())) {
                        msg.setType(msg_temp.getType());
                    }
                    if (StringUtils.isNotBlank(msg_temp.getDataUuid()))
                        msg.setDataUuid(msg_temp.getDataUuid());
                    // 通过界面传送的接受者名字是有值的，为了防止跨租户无法获取名称的问题新增名称匹配
                    msg.setRecipientNames(recipientName);
                    msg.setPhysicFileIds(msg_temp.getPhysicFileIds());
                    msg.setRelatedUrl(msg_temp.getRelatedUrl());
                    msg.setRelatedTitle(msg_temp.getRelatedTitle());
                } else {
                    msg.setExtraParm(entity);
                }
            }
            if (sender_is_system) {
                msg.setSender(resolveSender(type)); // 根据消息模板的类型(系统、用户)，解析获取发送人信息
            }
            // 附件
            if (attachmentIds != null) {
                msg.setAttach(StringUtils.join(Arrays.asList(attachmentIds), Separator.COMMA.getValue()));
            }
            if (Message.TYPE_ON_LINE.equals(way)) {
                if (!"Y".equals(template.getOnlineAttach())) {
                    msg.setAttach("");
                }
            } else if (Message.TYPE_EMAIL.equals(way) && !"Y".equals(template.getEmailAttach())) {
                msg.setAttach("");
            }
            msg.setRecipients(Arrays.asList(recipients.toArray(new String[0]))); // 接收人
            msg.setSubject(resolveSubject(template, way, data)); // 标题，解析获取标题
            msg.setBody(resolveBody(template, way, data)); // 消息体，解析获取标题
            // 发送时间
            msg.setSentTime(timeToSend);
            msg.setReceivedTime(null); // 接收时间
            msg.setIsread(Boolean.FALSE); // 是否已阅
            msg.setMappingRule(resolveMappingRule(mappingRule, data)); // 映像规则解析成的字符串
            msg.setTenantId(resolveTenantId()); // 租户ID
            msg.setCreator(resolveCreator());// 消息创建人ID
            // 在线消息设置
            msg.setIsOnlinePopup(isOnlinePopup);
            msg.setClassifyUuid(template.getClassifyUuid());
            msg.setClassifyName(template.getClassifyName());
//			msg.setCallbackJson(template.getCallbackJson());
            msg.setReminderType(template.getReminderType());
            msg.setPopupPosition(template.getPopupPosition());
            msg.setPopupSize(template.getPopupSize());
            msg.setPopupWidth(template.getPopupWidth());
            msg.setPopupHeight(template.getPopupHeight());
            msg.setPopupPosition(template.getPopupPosition());
            msg.setDisplayMask(template.getDisplayMask());
            msg.setAutoTimeCloseWin(template.getAutoTimeCloseWin());

            if (null != data.get("forwardDataUuid")) {
                msg.setForwardDataUuid(data.get("forwardDataUuid").toString());
            }

            resolveCallBackJson(template, data, msg);
            resolveRealteUrl(template, sendWay, data, msg);// 设定在线消息关联url
            // webserice消息设置
            resolveWebserviceParm(template, sendWay, data, msg);
            // 设定日程信息
            resolveSchedule(template, sendWay, data, msg);
            // 设定钉钉信息
            resolveDingtalk(template, way, data, msg);
            // 设定业务数据id，供立场决策时使用
            msg.setDataUuid(datauuid);
            msg.setForegroundEvent(template.getForegroundEvent());
            msg.setBackgroundEvent(template.getBackgroundEvent());
            msg.setShowViewpoint(template.getShowViewpoint());
            msg.setAskForSchedule(template.getAskForSchedule());
            msg.setViewpointY(template.getViewpointY());
            msg.setViewpointN(template.getViewpointN());
            msg.setViewpointNone(template.getViewpointNone());
            if (recipientName == null || recipientName.size() == 0) {
                Map<String, String> recipients_all_map = orgFacadeService.getNameByOrgEleIds(msg.getRecipients());
                recipientName = new ArrayList<String>();
                for (String recipient : msg.getRecipients()) {
                    recipientName.add(recipients_all_map.get(recipient));
                }
                msg.setRecipientNames(recipientName);
            }
            messages.add(msg);

        }
        // messageEvent(messageEvent);//发送消息触发事件
        // 设置JMS发送的消息
        JmsMessage jmsMessage = new JmsMessage();
        jmsMessage.setMessages(messages);
        // 默认延时1秒发送
        jmsMessage.setDelay(timeToSend.getTime() - currentTime.getTime() + 1000);
        jmsMessage.setSendTime(timeToSend);
        jmsMessage.setName(template.getName());
        jmsMessage.setTemplateId(templateId);
        jmsMessage.setCorrelationId(StringUtils.isNotBlank(datauuid) ? datauuid : null);
        jmsMessage.setSendTimeType(sendTimeType);
        return jmsMessage;
    }

    private void resolveCallBackJson(MessageTemplate template, Object entity, Message msg) {
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            msg.setCallbackJson(templateEngine.process(template.getCallbackJson(), entity));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void resolveRealteUrl(MessageTemplate template, String sendWay, Object entity, Message msg) {
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            msg.setRelatedTitle(templateEngine.process(template.getRelatedTitle(), entity));
            msg.setRelatedUrl(templateEngine.process(template.getRelatedUrl(), entity));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void resolveSchedule(MessageTemplate template, String sendWay, Object entity, Message msg) {
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            if (sendWay.contains(Message.TYPE_ON_LINE)) {
                msg.setScheduleTitle(templateEngine.process(template.getScheduleTitle(), entity));
                msg.setScheduleDates(templateEngine.process(template.getScheduleDates(), entity));
                msg.setScheduleDatee(templateEngine.process(template.getScheduleDatee(), entity));
                msg.setScheduleAddress(templateEngine.process(template.getScheduleAddress(), entity));
                msg.setReminderTime(templateEngine.process(template.getReminderTime(), entity));
                msg.setScheduleBody(templateEngine.process(template.getScheduleBody(), entity));
                msg.setSrcTitle(templateEngine.process(template.getSrcTitle(), entity));
                msg.setSrcAddress(templateEngine.process(template.getSrcAddress(), entity));
                msg.setRepeatType(templateEngine.process(template.getRepeatType(), entity));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void resolveWebserviceParm(MessageTemplate template, String sendWay, Object entity, Message msg) {
        MessageWebserviceParm parm = new MessageWebserviceParm();
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            if (Message.TYPE_WEB_SERVICE.equals(sendWay)) {
                parm.setWebServiceUrl(template.getWebServiceUrl());
                parm.setUsernameKey(template.getUsernameKey());
                parm.setUsernameValue(templateEngine.process(template.getUsernameValue(), entity));
                parm.setPasswordKey(template.getPasswordKey());
                parm.setPasswordValue(templateEngine.process(template.getPasswordValue(), entity));
                parm.setTenantidKey(template.getTenantidKey());
                parm.setTenantidValue(templateEngine.process(template.getTenantidValue(), entity));
                Set<WebServiceParm> childParm = template.getChildren();
                HashMap<String, Object> jsonMap = new HashMap<String, Object>();
                String parmValue = "";
                for (WebServiceParm child : childParm) {// 替换列表参数信息
                    parmValue = templateEngine.process(child.getParmValue(), entity);
                    jsonMap.put(child.getParmName(), parmValue);
                }
                parm.setJsondata(JsonUtils.object2Json(jsonMap));
                msg.setReservedIdentity(parm);// 设定webservice 发送消息
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param template
     * @param sendWay
     * @param data
     * @param msg
     */
    private void resolveDingtalk(MessageTemplate template, String sendWay, Map<Object, Object> entity, Message msg) {
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            if (Message.TYPE_DINGTALK.equals(sendWay)) {
                msg.setDtMessageType(template.getDtMessageType());
                msg.setDtBtnOrientation(template.getDtBtnOrientation());
                List<Map<String, String>> dtBtnJsonList = Lists.newArrayList();
                if (StringUtils.equals(template.getDtJumpType(), "single")) {
                    Map<String, String> btnObj = Maps.newHashMap();
                    btnObj.put("url", templateEngine.process(template.getDtUri(), entity));
                    btnObj.put("title", templateEngine.process(template.getDtUriTitle(), entity));
                    dtBtnJsonList.add(btnObj);
                } else if (StringUtils.equals(template.getDtJumpType(), "multi")) {
                    JSONArray btnJsonArray = JSONArray.fromObject(template.getDtBtnJsonList());
                    for (int i = 0; i < btnJsonArray.size(); i++) {
                        JSONObject btnJson = btnJsonArray.getJSONObject(i);
                        String url = btnJson.optString("url");
                        String title = btnJson.optString("title");
                        if (StringUtils.isBlank(url) || StringUtils.isBlank(title)) {
                            continue;
                        }
                        Map<String, String> btnObj = Maps.newHashMap();
                        btnObj.put("url", templateEngine.process(url, entity));
                        btnObj.put("title", templateEngine.process(title, entity));
                        dtBtnJsonList.add(btnObj);
                    }
                }
                msg.setDtBtnJsonList(dtBtnJsonList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 解析映射规则
     *
     * @param mappingRule
     * @param entity
     * @return
     */
    private String resolveMappingRule(String mappingRule, Object entity) {
        String result = null;
        try {
            result = TemplateEngineFactory.getDefaultTemplateEngine().process(mappingRule, entity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        return result;
    }

    /**
     * 解析发送消息触发事件
     *
     * @param mappingRule
     * @param entity
     * @return
     */
    private String resolveMessageEvent(String messageEvent, Object entity) {
        String result = null;
        try {
            result = TemplateEngineFactory.getDefaultTemplateEngine().process(messageEvent, entity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        return result;
    }

    /**
     * 获取租户ID
     *
     * @return
     */
    private String resolveTenantId() {
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        return tenantId != null ? tenantId : Config.DEFAULT_TENANT;
    }

    /**
     * 获取消息创建人
     *
     * @return
     */
    private String resolveCreator() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        return userId != null ? userId : Config.DEFAULT_TENANT;
    }

    /**
     * 解析生成模板内容
     *
     * @param template
     * @param way
     * @return
     */
    private String resolveBody(MessageTemplate template, String sendWay, Object entity) {
        String body = null;
        try {
            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
            if (Message.TYPE_ON_LINE.equals(sendWay)) {
                body = templateEngine.process(template.getOnlineBody(), entity);
            } else if (Message.TYPE_EMAIL.equals(sendWay)) {
                body = templateEngine.process(template.getEmailBody(), entity);
            } else if (Message.TYPE_SMS.equals(sendWay)) {
                body = templateEngine.process(template.getSmsBody(), entity);
            } else if (Message.TYPE_DINGTALK.equals(sendWay)) {
                body = templateEngine.process(template.getDtBody(), entity);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            body = e.getMessage();
        }
        return body;
    }

    private Date resolveSendTime(MessageTemplate template, Object entity) {
        try {
            if (StringUtils.isNotBlank(template.getScheduleTime())) {
                TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
                String dateTimeStr = templateEngine.process(template.getScheduleTime(), entity);
                return DateUtils.parseDate(dateTimeStr, "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss");
            }

        } catch (Exception e) {
            logger.error("消息格式解析定时时间异常：", e);
        }
        return null;
    }

    /**
     * 如何描述该方法
     *
     * @param template
     * @param sendWay
     * @return
     */
    private String resolveSubject(MessageTemplate template, String sendWay, Object entity) {
        String subject = null;
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        try {
            if (Message.TYPE_ON_LINE.equals(sendWay)) {
                subject = templateEngine.process(template.getOnlineSubject(), entity);
            } else if (Message.TYPE_EMAIL.equals(sendWay)) {
                subject = templateEngine.process(template.getEmailSubject(), entity);
            } else if (Message.TYPE_DINGTALK.equals(sendWay)) {
                subject = templateEngine.process(template.getDtTitle(), entity);
            } else if (Message.TYPE_SMS.equals(sendWay)) {
                // 短信没有消息模板主题
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            subject = e.getMessage();
        }
        return subject;
    }

    /**
     * 根据消息模板的类型(系统、用户)，解析获取发送人信息
     *
     * @param type
     * @return
     */
    private String resolveSender(String type) {
        if (MessageTemplate.TYPE_SYSTEM.equals(type)) {
            return Message.USER_SYSTEM;
        } else if (MessageTemplate.TYPE_USER.equals(type)) {
            return SpringSecurityUtils.getCurrentUserId();
        }
        return "unknown";
    }

    @Override
    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                        Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity,
                                                        String datauuid) {
        JmsMessage jmsMessage = resolveCommon(templateId, entities, dataMap, extraData, recipients, entity, datauuid,
                null, null);
        return jmsMessage;
    }

    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                        Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity,
                                                        String datauuid, String[] attachmentIds) {
        JmsMessage jmsMessage = resolveCommon(templateId, entities, dataMap, extraData, recipients, entity, datauuid,
                attachmentIds, null);
        return jmsMessage;
    }

    @Override
    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities, Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity, String datauuid, String[] attachmentIds, String sendWays) {
        JmsMessage jmsMessage = resolveCommon(templateId, entities, dataMap, extraData, recipients, entity, datauuid,
                attachmentIds, sendWays);
        return jmsMessage;
    }

    @Override
    public <ENTITY extends IdEntity> JmsMessage resolve(String datauuid) {
        List<Message> messages = new ArrayList<Message>();
        Message message = new Message();
        message.setDataUuid(datauuid);// 要注销的消息id
        message.setTenantId(resolveTenantId()); // 租户ID
        message.setCreator(resolveCreator());// 消息创建人ID
        message.setType(Message.TYPE_CANCEL);// 消息类型
        messages.add(message);
        // 设置JMS发送的消息
        JmsMessage jmsMessage = new JmsMessage();
        jmsMessage.setMessages(messages);
        // 默认延时1秒发送
        jmsMessage.setDelay(1000);
        return jmsMessage;
    }

    @Override
    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                        Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients) {
        // 设置JMS发送的消息
        JmsMessage jmsMessage = resolveCommon(templateId, entities, dataMap, extraData, recipients, null, null, null,
                null);
        return jmsMessage;
    }

    @Override
    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                        Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity) {
        JmsMessage jmsMessage = resolveCommon(templateId, entities, dataMap, extraData, recipients, entity, null, null,
                null);
        return jmsMessage;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.client.MessageResolver#resolve(java.lang.String, java.util.Collection, java.util.Map, java.util.Map, java.util.Collection, java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                        Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, String datauuid,
                                                        String[] attachmentIds, String sendWays) {
        // 设置JMS发送的消息
        JmsMessage jmsMessage = resolveCommon(templateId, entities, dataMap, extraData, recipients, null, datauuid,
                attachmentIds, sendWays);
        return jmsMessage;
    }

}

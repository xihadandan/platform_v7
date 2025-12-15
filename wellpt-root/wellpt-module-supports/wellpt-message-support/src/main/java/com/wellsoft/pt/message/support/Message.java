/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.support;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 消息实体类
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
public class Message extends TenantEntity {
    public static final String TYPE_ON_LINE = "ON_LINE";
    public static final String TYPE_EMAIL = "EMAIL";
    public static final String TYPE_DINGTALK = "DINGTALK";
    public static final String TYPE_SMS = "SMS";
    public static final String TYPE_WEB_SERVICE = "WEB_SERVICE";
    public static final String TYPE_CANCEL = "MESSAGE_CANCEL";
    public static final String TYPE_SCHEDULE = "SCHEDULE";// 增加日程类型 20141010
    public static final String TYPE_UNKNOWN = "UNKNOWN";
    public static final String USER_SYSTEM = "system";
    public static final String TYPE_ONLINE_CANCEL = "cancelMessageOnline";
    public static final String TYPE_INTEFACE = "INTEFACE";
    public static final String RECIPIENT_TYPE_INTERNET_USER = "INTERNET_USER_MESSAGE";
    private static final long serialVersionUID = 3080258719550488161L;
    /**
     * 消息模板ID
     */
    private String templateId;
    /**
     * 名称
     */
    private String name;
    /**
     * 消息类型(在线、手机短信、邮件、WebService)
     */
    private String type;
    /**
     * 发送人
     */
    private String sender;
    /**
     * 接收人
     */
    private List<String> recipients;
    /**
     * 接收人名称
     */
    private List<String> recipientNames;
    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String body;
    /**
     * 发送时间
     */
    private Date sentTime;
    /**
     * 接收时间
     */
    private Date receivedTime;
    /**
     * 是否已阅
     */
    private Boolean isread;
    /**
     * 映像规则解析成的字符串
     */
    private String mappingRule;
    /**
     * 是否弹窗提醒
     */
    private String isOnlinePopup;
    /**
     * 是否征求意见立场
     */
    private String showViewpoint;
    /**
     * 意见立场通过
     */
    private String viewpointY;
    /**
     * 意见立场不通过
     */
    private String viewpointN;
    /**
     * 意见立场不处理
     */
    private String viewpointNone;
    /**
     * 前台js事件处理
     */
    private String foregroundEvent;
    /**
     * 悬着立场后，后台处理
     */
    private String backgroundEvent;

    /**
     * 是否列入日程
     */
    private String askForSchedule;

    /**
     * 消息紧急程度
     */
    private String messageLevel;

    private String relatedTitle;// 在线消息源标题
    private String relatedUrl;// 在线源地址
    private String classifyUuid; //消息分类uuId
    private String classifyName; //消息分类名称
    private String callbackJson; //回调事件json
    private Integer reminderType; //消息提醒方式（1：徽标，2：徽标+弹窗）
    private Integer popupPosition; //弹窗位置：1 浏览器右下角，弹窗在浏览器右下角弹出 2 浏览器中间，弹窗在浏览器中间弹出
    private Integer popupSize; //弹窗大小（1：默认，2：自定义）
    private String popupWidth; //弹窗宽度
    private String popupHeight; //弹窗高度
    private Integer displayMask;//显示遮罩 0否 1是
    private Integer autoTimeCloseWin;//自动计时关闭弹窗  0否 1是
    private String forwardDataUuid; // 跳转的数据的uuid

    /**
     * 日程
     */
    private String scheduleTitle;// 标题
    private String scheduleDates;// 开始日期
    private String scheduleDatee;// 结束日期
    private String scheduleAddress;// 地址
    private String reminderTime;// 提醒时间
    private String repeatType;// 提醒时间
    private String scheduleBody;// 内容
    private String srcTitle;// 源标题
    private String srcAddress;// 源地址

    /**
     * dingtalk
     */
    private String dtMessageType;// 消息类型:ActionCard
    private String dtBtnOrientation;// 按钮排列方式:竖直排列(0)，横向排列(1)
    private List<Map<String, String>> dtBtnJsonList;// 附加链接JSONArray[{"title":"","url":""}]

    /**
     * 业务数据id
     */
    private String dataUuid;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 收信人手机号
     */
    private Set<String> recipientMbPhones;
    /**
     * 短信存储,异步发送
     */
    private Boolean async;
    /**
     * 额外参数信息
     */
    private IdEntity extraParm;
    /**
     * 预留参数
     */
    private IdEntity reservedIdentity;
    /**
     * 预留字段1
     */
    private String reservedText1;
    /**
     * 预留字段2
     */
    private String reservedText2;
    /**
     * 预留字段3
     */
    private String reservedText3;
    /**
     * 发件人名称
     */
    private String senderName;

    /**
     * 接收人类型
     */
    private String recipientType;

    @JsonIgnore
    private List<MongoFileEntity> mongoFileEntities;
    private List<String> physicFileIds;

    private String system;
    private String tenant;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public List<String> getPhysicFileIds() {
        return physicFileIds;
    }

    public void setPhysicFileIds(List<String> physicFileIds) {
        this.physicFileIds = physicFileIds;
    }

    public List<String> getRecipientNames() {
        return recipientNames;
    }

    public void setRecipientNames(List<String> recipientNames) {
        this.recipientNames = recipientNames;
    }

    public List<MongoFileEntity> getMongoFileEntities() {
        return mongoFileEntities;
    }

    public void setMongoFileEntities(List<MongoFileEntity> mongoFileEntities) {
        this.mongoFileEntities = mongoFileEntities;
    }

    /**
     * @return the templateId
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId 要设置的templateId
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender 要设置的sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return the recipients
     */
    @Transient
    public List<String> getRecipients() {
        return recipients;
    }

    /**
     * @param recipients 要设置的recipients
     */
    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject 要设置的subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body 要设置的body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the sentTime
     */
    public Date getSentTime() {
        return sentTime;
    }

    /**
     * @param sentTime 要设置的sentTime
     */
    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    /**
     * @return the receivedTime
     */
    public Date getReceivedTime() {
        return receivedTime;
    }

    /**
     * @param receivedTime 要设置的receivedTime
     */
    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    /**
     * @return the isread
     */
    public Boolean getIsread() {
        return isread;
    }

    /**
     * @param isread 要设置的isread
     */
    public void setIsread(Boolean isread) {
        this.isread = isread;
    }

    /**
     * @return the mappingRule
     */
    public String getMappingRule() {
        return mappingRule;
    }

    /**
     * @param mappingRule 要设置的mappingRule
     */
    public void setMappingRule(String mappingRule) {
        this.mappingRule = mappingRule;
    }

    public String getIsOnlinePopup() {
        return isOnlinePopup;
    }

    public void setIsOnlinePopup(String isOnlinePopup) {
        this.isOnlinePopup = isOnlinePopup;
    }

    public String getShowViewpoint() {
        return showViewpoint;
    }

    public void setShowViewpoint(String showViewpoint) {
        this.showViewpoint = showViewpoint;
    }

    public String getViewpointY() {
        return viewpointY;
    }

    public void setViewpointY(String viewpointY) {
        this.viewpointY = viewpointY;
    }

    public String getViewpointN() {
        return viewpointN;
    }

    public void setViewpointN(String viewpointN) {
        this.viewpointN = viewpointN;
    }

    public String getViewpointNone() {
        return viewpointNone;
    }

    public void setViewpointNone(String viewpointNone) {
        this.viewpointNone = viewpointNone;
    }

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId 要设置的tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public Set<String> getRecipientMbPhones() {
        return recipientMbPhones;
    }

    public void setRecipientMbPhones(Set<String> recipientMbPhones) {
        this.recipientMbPhones = recipientMbPhones;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    /**
     * @return the reservedText1
     */
    public String getReservedText1() {
        return reservedText1;
    }

    /**
     * @param reservedText1 要设置的reservedText1
     */
    public void setReservedText1(String reservedText1) {
        this.reservedText1 = reservedText1;
    }

    /**
     * @return the reservedText2
     */
    public String getReservedText2() {
        return reservedText2;
    }

    /**
     * @param reservedText2 要设置的reservedText2
     */
    public void setReservedText2(String reservedText2) {
        this.reservedText2 = reservedText2;
    }

    /**
     * @return the reservedText3
     */
    public String getReservedText3() {
        return reservedText3;
    }

    /**
     * @param reservedText3 要设置的reservedText3
     */
    public void setReservedText3(String reservedText3) {
        this.reservedText3 = reservedText3;
    }

    public String getBackgroundEvent() {
        return backgroundEvent;
    }

    public void setBackgroundEvent(String backgroundEvent) {
        this.backgroundEvent = backgroundEvent;
    }

    public String getAskForSchedule() {
        return askForSchedule;
    }

    public void setAskForSchedule(String askForSchedule) {
        this.askForSchedule = askForSchedule;
    }

    public IdEntity getExtraParm() {
        return extraParm;
    }

    public void setExtraParm(IdEntity extraParm) {
        this.extraParm = extraParm;
    }

    public IdEntity getReservedIdentity() {
        return reservedIdentity;
    }

    public void setReservedIdentity(IdEntity reservedIdentity) {
        this.reservedIdentity = reservedIdentity;
    }

    public String getForegroundEvent() {
        return foregroundEvent;
    }

    public void setForegroundEvent(String foregroundEvent) {
        this.foregroundEvent = foregroundEvent;
    }

    public String getMessageLevel() {
        return messageLevel;
    }

    public void setMessageLevel(String messageLevel) {
        this.messageLevel = messageLevel;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public String getScheduleDates() {
        return scheduleDates;
    }

    public void setScheduleDates(String scheduleDates) {
        this.scheduleDates = scheduleDates;
    }

    public String getScheduleDatee() {
        return scheduleDatee;
    }

    public void setScheduleDatee(String scheduleDatee) {
        this.scheduleDatee = scheduleDatee;
    }

    public String getScheduleAddress() {
        return scheduleAddress;
    }

    public void setScheduleAddress(String scheduleAddress) {
        this.scheduleAddress = scheduleAddress;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getScheduleBody() {
        return scheduleBody;
    }

    public void setScheduleBody(String scheduleBody) {
        this.scheduleBody = scheduleBody;
    }

    public String getSrcTitle() {
        return srcTitle;
    }

    public void setSrcTitle(String srcTitle) {
        this.srcTitle = srcTitle;
    }

    public String getSrcAddress() {
        return srcAddress;
    }

    public void setSrcAddress(String srcAddress) {
        this.srcAddress = srcAddress;
    }

    public String getRelatedTitle() {
        return relatedTitle;
    }

    public void setRelatedTitle(String relatedTitle) {
        this.relatedTitle = relatedTitle;
    }

    public String getRelatedUrl() {
        return relatedUrl;
    }

    public void setRelatedUrl(String relatedUrl) {
        this.relatedUrl = relatedUrl;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getClassifyUuid() {
        return classifyUuid;
    }

    public void setClassifyUuid(String classifyUuid) {
        this.classifyUuid = classifyUuid;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public String getCallbackJson() {
        return callbackJson;
    }

    public void setCallbackJson(String callbackJson) {
        this.callbackJson = callbackJson;
    }

    public Integer getReminderType() {
        return reminderType;
    }

    public void setReminderType(Integer reminderType) {
        this.reminderType = reminderType;
    }

    public Integer getPopupPosition() {
        return popupPosition;
    }

    public void setPopupPosition(Integer popupPosition) {
        this.popupPosition = popupPosition;
    }

    public Integer getDisplayMask() {
        return displayMask;
    }

    public void setDisplayMask(Integer displayMask) {
        this.displayMask = displayMask;
    }

    public Integer getAutoTimeCloseWin() {
        return autoTimeCloseWin;
    }

    public void setAutoTimeCloseWin(Integer autoTimeCloseWin) {
        this.autoTimeCloseWin = autoTimeCloseWin;
    }

    public String getPopupWidth() {
        return popupWidth;
    }

    public void setPopupWidth(String popupWidth) {
        this.popupWidth = popupWidth;
    }

    public String getPopupHeight() {
        return popupHeight;
    }

    public void setPopupHeight(String popupHeight) {
        this.popupHeight = popupHeight;
    }

    public Integer getPopupSize() {
        return popupSize;
    }

    public void setPopupSize(Integer popupSize) {
        this.popupSize = popupSize;
    }

    public String getDtMessageType() {
        return dtMessageType;
    }

    public void setDtMessageType(String dtMessageType) {
        this.dtMessageType = dtMessageType;
    }

    public String getDtBtnOrientation() {
        return dtBtnOrientation;
    }

    public void setDtBtnOrientation(String dtBtnOrientation) {
        this.dtBtnOrientation = dtBtnOrientation;
    }

    public String getForwardDataUuid() {
        return forwardDataUuid;
    }

    public void setForwardDataUuid(String forwardDataUuid) {
        this.forwardDataUuid = forwardDataUuid;
    }

    public List<Map<String, String>> getDtBtnJsonList() {
        return dtBtnJsonList;
    }

    public void setDtBtnJsonList(List<Map<String, String>> dtBtnJsonList) {
        this.dtBtnJsonList = dtBtnJsonList;
    }
}

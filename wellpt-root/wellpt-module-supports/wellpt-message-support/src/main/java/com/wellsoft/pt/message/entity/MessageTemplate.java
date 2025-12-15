/*
 * @(#)2012-11-7 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.validator.MaxLength;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: 消息格式实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-7.1	zhulh		2012-11-7		Create
 * </pre>
 * @date 2012-11-7
 */
@Entity
@Table(name = "msg_message_template")
@DynamicUpdate
@DynamicInsert
public class MessageTemplate extends TenantEntity {
    public static final String TYPE_SYSTEM = "SYSTEM";
    public static final String TYPE_USER = "USER";
    public static final String SEND_TIME_IN_TIME = "IN_TIME";
    public static final String SEND_TIME_WORK_TIME = "WORK_TIME";
    public static final String SEND_TIME_SCHEDULE_TIME = "SCHEDULE_TIME";
    public static final String SYSTEM_DEFAULT_MESSAGE = "systemFormat";
    public static final String DATA_EXPORT_MESSAGE = "MSG_EXPORT";
    public static final String DATA_IMPORT_MESSAGE = "MSG_IMPORT";
    private static final long serialVersionUID = 6699625435646722048L;
    /**
     * 名称
     */
    @NotBlank
    private String name;
    /**
     * ID
     */
    @NotBlank
    private String id;
    /**
     * 编号
     */
    private String code;

    /**
     * 分类标识
     */
    private String category;
    /**
     * 类型(系统、用户)
     */
    private String type;
    /**
     * 提醒方式
     */
    private String sendWay;
    /**
     * 发送时间
     */
    private String sendTime;
    /**
     * 定时时间
     */
    private String scheduleTime;
    /**
     * 映像规则
     */
    private String mappingRule;
    /**
     * 消息触发事件
     */
    private String messageEvent;
    /**
     * 消息触发事件文本说明
     */
    private String messageEventText;
    /**
     * 消息触发后台接口
     **/
    private String messageInteface;
    /**
     * 消息触发后台接口名称
     **/
    private String messageIntefaceText;
    /**
     * 在线消息
     */
    private String onlineSubject;// 标题
    @MaxLength(max = 1024)
    private String onlineBody;// 内容
    private String isOnlinePopup;// 是否弹窗提醒
    private String showViewpoint;// 是否征求意见立场
    private String viewpointY;// 意见立场通过
    private String viewpointN;// 意见立场不通过
    private String viewpointNone;// 意见立场不处理
    private String askForSchedule;// 咨询是否列入日程
    private String foregroundEvent;// 前台事件处理
    private String foregroundEventText;// 前台事件处理文本说明
    private String backgroundEvent;// 后台事件处理
    private String backgroundEventText;// 后台事件处理文本说明
    private String relatedTitle;// 源标题
    private String relatedUrl;// 源地址
    private String onlineAttach;// 是否发送附件

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

    /**
     * 手机短信
     */
    private Integer breakWord; // 短信启用分词
    private String smsBody;// 内容

    /**
     * 电子邮件
     */
    private String emailSubject;// 标题
    private String emailBody;// 内容
    private String emailAttach;// 是否发送附件
    /**
     * 日程
     */
    private String scheduleTitle;// 标题
    private String scheduleDates;// 开始日期
    private String scheduleDatee;// 结束日期
    private String scheduleAddress;// 地址
    private String reminderTime;// 提醒时间
    private String repeatType;// 重复方式
    private String scheduleBody;// 内容
    private String srcTitle;// 源标题
    private String srcAddress;// 源地址

    /**
     * webservice
     */
    private String webServiceUrl;// 标题
    private String usernameKey;// 用户
    private String usernameValue;// 用户
    private String passwordKey;// 标题
    private String passwordValue;// 标题
    private String tenantidKey;// 标题
    private String tenantidValue;// 标题

    /**
     * dingtalk
     */
    private String dtMessageType;// 消息类型:ActionCard
    private String dtJumpType;// 跳转方式:（整体跳转）single、（独立跳转）multi
    private String dtTitle;// 标题
    private String dtBody;// 内容
    private String dtUri;// 源地址
    private String dtUriTitle;// 源标题
    private String dtBtnOrientation;// 按钮排列方式:竖直排列(0)，横向排列(1)
    private String dtBtnJsonList;// 附加链接JSONArray[{"title":"","url":""}]

    private String moduleId;

    @UnCloneable
    private Set<WebServiceParm> children = new HashSet<WebServiceParm>();

    public String getMessageInteface() {
        return messageInteface;
    }

    public void setMessageInteface(String messageInteface) {
        this.messageInteface = messageInteface;
    }

    public String getMessageIntefaceText() {
        return messageIntefaceText;
    }

    public void setMessageIntefaceText(String messageIntefaceText) {
        this.messageIntefaceText = messageIntefaceText;
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
     * @return the id
     */
    @Column(nullable = false, unique = true)
    @NotBlank
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category 要设置的category
     */
    public void setCategory(String category) {
        this.category = category;
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
     * @return the sendWay
     */
    public String getSendWay() {
        return sendWay;
    }

    /**
     * @param sendWay 要设置的sendWay
     */
    public void setSendWay(String sendWay) {
        this.sendWay = sendWay;
    }

    /**
     * @return the sendTime
     */
    public String getSendTime() {
        return sendTime;
    }

    /**
     * @param sendTime 要设置的sendTime
     */
    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
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

    public String getMessageEvent() {
        return messageEvent;
    }

    public void setMessageEvent(String messageEvent) {
        this.messageEvent = messageEvent;
    }

    /**
     * @return the onlineSubject
     */
    public String getOnlineSubject() {
        return onlineSubject;
    }

    /**
     * @param onlineSubject 要设置的onlineSubject
     */
    public void setOnlineSubject(String onlineSubject) {
        this.onlineSubject = onlineSubject;
    }

    /**
     * @return the onlineBody
     */
    public String getOnlineBody() {
        return onlineBody;
    }

    /**
     * @param onlineBody 要设置的onlineBody
     */
    public void setOnlineBody(String onlineBody) {
        this.onlineBody = onlineBody;
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

    public Integer getBreakWord() {
        return breakWord;
    }

    public void setBreakWord(Integer breakWord) {
        this.breakWord = breakWord;
    }

    /**
     * @return the smsBody
     */
    public String getSmsBody() {
        return smsBody;
    }

    /**
     * @param smsBody 要设置的smsBody
     */
    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }

    /**
     * @return the emailSubject
     */
    public String getEmailSubject() {
        return emailSubject;
    }

    /**
     * @param emailSubject 要设置的emailSubject
     */
    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    /**
     * @return the emailBody
     */
    public String getEmailBody() {
        return emailBody;
    }

    /**
     * @param emailBody 要设置的emailBody
     */
    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "messageTemplate")
    @Cascade(value = {CascadeType.ALL})
    @OrderBy(value = "id")
    public Set<WebServiceParm> getChildren() {
        return children;
    }

    public void setChildren(Set<WebServiceParm> children) {
        this.children = children;
    }

    public String getWebServiceUrl() {
        return webServiceUrl;
    }

    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    public String getUsernameKey() {
        return usernameKey;
    }

    public void setUsernameKey(String usernameKey) {
        this.usernameKey = usernameKey;
    }

    public String getUsernameValue() {
        return usernameValue;
    }

    public void setUsernameValue(String usernameValue) {
        this.usernameValue = usernameValue;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    public String getPasswordValue() {
        return passwordValue;
    }

    public void setPasswordValue(String passwordValue) {
        this.passwordValue = passwordValue;
    }

    public String getTenantidKey() {
        return tenantidKey;
    }

    public void setTenantidKey(String tenantidKey) {
        this.tenantidKey = tenantidKey;
    }

    public String getTenantidValue() {
        return tenantidValue;
    }

    public void setTenantidValue(String tenantidValue) {
        this.tenantidValue = tenantidValue;
    }

    public String getMessageEventText() {
        return messageEventText;
    }

    public void setMessageEventText(String messageEventText) {
        this.messageEventText = messageEventText;
    }

    public String getAskForSchedule() {
        return askForSchedule;
    }

    public void setAskForSchedule(String askForSchedule) {
        this.askForSchedule = askForSchedule;
    }

    public String getForegroundEvent() {
        return foregroundEvent;
    }

    public void setForegroundEvent(String foregroundEvent) {
        this.foregroundEvent = foregroundEvent;
    }

    public String getForegroundEventText() {
        return foregroundEventText;
    }

    public void setForegroundEventText(String foregroundEventText) {
        this.foregroundEventText = foregroundEventText;
    }

    public String getBackgroundEvent() {
        return backgroundEvent;
    }

    public void setBackgroundEvent(String backgroundEvent) {
        this.backgroundEvent = backgroundEvent;
    }

    public String getBackgroundEventText() {
        return backgroundEventText;
    }

    public void setBackgroundEventText(String backgroundEventText) {
        this.backgroundEventText = backgroundEventText;
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

    public String getOnlineAttach() {
        return onlineAttach;
    }

    public void setOnlineAttach(String onlineAttach) {
        this.onlineAttach = onlineAttach;
    }

    public String getEmailAttach() {
        return emailAttach;
    }

    public void setEmailAttach(String emailAttach) {
        this.emailAttach = emailAttach;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
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

    public String getDtJumpType() {
        return dtJumpType;
    }

    public void setDtJumpType(String dtJumpType) {
        this.dtJumpType = dtJumpType;
    }

    public String getDtTitle() {
        return dtTitle;
    }

    public void setDtTitle(String dtTitle) {
        this.dtTitle = dtTitle;
    }

    public String getDtBody() {
        return dtBody;
    }

    public void setDtBody(String dtBody) {
        this.dtBody = dtBody;
    }

    public String getDtUri() {
        return dtUri;
    }

    public void setDtUri(String dtUri) {
        this.dtUri = dtUri;
    }

    public String getDtUriTitle() {
        return dtUriTitle;
    }

    public void setDtUriTitle(String dtUriTitle) {
        this.dtUriTitle = dtUriTitle;
    }

    public String getDtBtnOrientation() {
        return dtBtnOrientation;
    }

    public void setDtBtnOrientation(String dtBtnOrientation) {
        this.dtBtnOrientation = dtBtnOrientation;
    }

    public String getDtBtnJsonList() {
        return dtBtnJsonList;
    }

    public void setDtBtnJsonList(String dtBtnJsonList) {
        this.dtBtnJsonList = dtBtnJsonList;
    }

}

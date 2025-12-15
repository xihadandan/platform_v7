package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Clob;
import java.util.Date;

/**
 * Description: 文档交换记录
 *
 * @author chenq
 * @date 2018/5/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/15    chenq		2018/5/15		Create
 * </pre>
 */
@Entity
@Table(name = "DMS_DOC_EXCHANGE_RECORD")
@DynamicInsert
@DynamicUpdate
public class DmsDocExchangeRecordEntity extends TenantEntity {

    private static final long serialVersionUID = -6192586717118683558L;

    private String userId;//归属用户ID

    private Integer exchangeType;

    private String dyformUuid;

    private String dataUuid;

    private String fileUuids;

    private String fileNames;

    private Date feedbackTimeLimit;

    private Date signTimeLimit;

    private Integer docEncryptionLevel;

    private Integer docUrgeLevel;

    private Boolean isSmsNotify;

    private Boolean isImNotify;

    private Boolean isMailNotify;

    private Boolean isNeedSign;

    private Boolean isNeedFeedback;

    private Integer recordStatus;

    private String fromRecordDetailUuid;

    private String fromUserId;

    private String fromUnitId;

    @JsonIgnore
    private Clob toUserIds;

    @JsonIgnore
    private Clob toUserNames;

    private String flowUuid;

    private String docTitle;

    private Clob configurationJson;//配置项json

    private Integer overtimeLevel;//紧要性，根据反馈/签收时限与工作日的差距判断，1 一般 2 重要 3 紧急

    private Integer refuseToView; //拒绝查看

    private Integer noReminders; //不再提醒

    private String configUuid;//配置项uuid

    private Date sendTime;//发件时间

    public Integer getRefuseToView() {
        return refuseToView;
    }

    public void setRefuseToView(Integer refuseToView) {
        this.refuseToView = refuseToView;
    }

    public Integer getNoReminders() {
        return noReminders;
    }

    public void setNoReminders(Integer noReminders) {
        this.noReminders = noReminders;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(Integer exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getDyformUuid() {
        return dyformUuid;
    }

    public void setDyformUuid(String dyformUuid) {
        this.dyformUuid = dyformUuid;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public Date getFeedbackTimeLimit() {
        return feedbackTimeLimit;
    }

    public void setFeedbackTimeLimit(Date feedbackTimeLimit) {
        this.feedbackTimeLimit = feedbackTimeLimit;
    }

    public Date getSignTimeLimit() {
        return signTimeLimit;
    }

    public void setSignTimeLimit(Date signTimeLimit) {
        this.signTimeLimit = signTimeLimit;
    }

    public Integer getDocEncryptionLevel() {
        return docEncryptionLevel;
    }

    public void setDocEncryptionLevel(Integer docEncryptionLevel) {
        this.docEncryptionLevel = docEncryptionLevel;
    }

    public Integer getDocUrgeLevel() {
        return docUrgeLevel;
    }

    public void setDocUrgeLevel(Integer docUrgeLevel) {
        this.docUrgeLevel = docUrgeLevel;
    }

    public Boolean getIsSmsNotify() {
        return isSmsNotify;
    }

    public void setIsSmsNotify(Boolean smsNotify) {
        isSmsNotify = smsNotify;
    }

    public Boolean getIsImNotify() {
        return isImNotify;
    }

    public void setIsImNotify(Boolean imNotify) {
        isImNotify = imNotify;
    }

    public Boolean getIsMailNotify() {
        return isMailNotify;
    }

    public void setIsMailNotify(Boolean mailNotify) {
        isMailNotify = mailNotify;
    }

    public Integer getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(Integer recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getFromRecordDetailUuid() {
        return fromRecordDetailUuid;
    }

    public void setFromRecordDetailUuid(String fromRecordDetailUuid) {
        this.fromRecordDetailUuid = fromRecordDetailUuid;
    }

    public Clob getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(Clob toUserIds) {
        this.toUserIds = toUserIds;
    }

    public Clob getToUserNames() {
        return toUserNames;
    }

    public void setToUserNames(Clob toUserNames) {
        this.toUserNames = toUserNames;
    }


    public Boolean getIsNeedSign() {
        return isNeedSign;
    }

    public void setIsNeedSign(Boolean needSign) {
        isNeedSign = needSign;
    }

    public Boolean getIsNeedFeedback() {
        return isNeedFeedback;
    }

    public void setIsNeedFeedback(Boolean needFeedback) {
        isNeedFeedback = needFeedback;
    }

    public String getFileUuids() {
        return fileUuids;
    }

    public void setFileUuids(String fileUuids) {
        this.fileUuids = fileUuids;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFlowUuid() {
        return flowUuid;
    }

    public void setFlowUuid(String flowUuid) {
        this.flowUuid = flowUuid;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public Clob getConfigurationJson() {
        return configurationJson;
    }

    public void setConfigurationJson(Clob configurationJson) {
        this.configurationJson = configurationJson;
    }

    public Integer getOvertimeLevel() {
        return overtimeLevel;
    }

    public void setOvertimeLevel(Integer overtimeLevel) {
        this.overtimeLevel = overtimeLevel;
    }

    public String getConfigUuid() {
        return configUuid;
    }

    public void setConfigUuid(String configUuid) {
        this.configUuid = configUuid;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getFromUnitId() {
        return fromUnitId;
    }

    public void setFromUnitId(String fromUnitId) {
        this.fromUnitId = fromUnitId;
    }
}

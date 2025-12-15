package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Clob;
import java.util.Date;

/**
 * Description: 文档交换-转发
 *
 * @author chenq
 * @date 2018/5/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/21    chenq		2018/5/21		Create
 * </pre>
 */
@Table(name = "DMS_DOC_EXC_RECORD_FORWARD")
@Entity
@DynamicUpdate
@DynamicInsert
public class DmsDocExchangeForwardEntity extends TenantEntity {
    private static final long serialVersionUID = -1731179676146286631L;

    private String docExchangeRecordUuid;

    private Clob toUserIds;

    private String fromUserId;

    private String fromUnitId;

    private Clob toUserNames;

    private Date feedbackTimeLimit;

    private Date signTimeLimit;

    private Boolean isImNotify;

    private Boolean isSmsNotify;

    private Boolean isMailNotify;

    private DocExchangeRecordStatusEnum forwardStatus;

    private String remark;

    private String fileUuids;

    private String fileNames;

    public DmsDocExchangeForwardEntity() {

    }

    public DmsDocExchangeForwardEntity(String docExchangeRecordUuid, Clob toUserIds,
                                       Clob toUserNames,
                                       Date feedbackTimeLimit, Date signTimeLimit,
                                       Boolean isImNotify,
                                       Boolean isSmsNotify, Boolean isMailNotify,
                                       DocExchangeRecordStatusEnum forwardStatus, String remark,
                                       String fileUuids, String fileNames, String fromUserId, String fromUnitId) {
        this.docExchangeRecordUuid = docExchangeRecordUuid;
        this.toUserIds = toUserIds;
        this.toUserNames = toUserNames;
        this.feedbackTimeLimit = feedbackTimeLimit;
        this.signTimeLimit = signTimeLimit;
        this.isImNotify = isImNotify;
        this.isSmsNotify = isSmsNotify;
        this.isMailNotify = isMailNotify;
        this.forwardStatus = forwardStatus;
        this.remark = remark;
        this.fileUuids = fileUuids;
        this.fileNames = fileNames;
        this.fromUserId = fromUserId;
        this.fromUnitId = fromUnitId;
    }

    public String getFromUnitId() {
        return fromUnitId;
    }

    public void setFromUnitId(String fromUnitId) {
        this.fromUnitId = fromUnitId;
    }

    public String getDocExchangeRecordUuid() {
        return docExchangeRecordUuid;
    }

    public void setDocExchangeRecordUuid(String docExchangeRecordUuid) {
        this.docExchangeRecordUuid = docExchangeRecordUuid;
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

    public Boolean getIsImNotify() {
        return isImNotify;
    }

    public void setIsImNotify(Boolean imNotify) {
        isImNotify = imNotify;
    }

    public Boolean getIsSmsNotify() {
        return isSmsNotify;
    }

    public void setIsSmsNotify(Boolean smsNotify) {
        isSmsNotify = smsNotify;
    }

    public Boolean getIsMailNotify() {
        return isMailNotify;
    }

    public void setIsMailNotify(Boolean mailNotify) {
        isMailNotify = mailNotify;
    }


    public DocExchangeRecordStatusEnum getForwardStatus() {
        return forwardStatus;
    }

    public void setForwardStatus(DocExchangeRecordStatusEnum forwardStatus) {
        this.forwardStatus = forwardStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}

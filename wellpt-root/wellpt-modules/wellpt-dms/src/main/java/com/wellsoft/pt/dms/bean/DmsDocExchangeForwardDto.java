package com.wellsoft.pt.dms.bean;

import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/22
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/22    chenq		2018/5/22		Create
 * </pre>
 */
public class DmsDocExchangeForwardDto implements Serializable {

    private String uuid;

    private String docExchangeRecordUuid;

    private String toUserIds;

    private String fromUserId;

    private String fromUserName;

    private String fromUserUnitName;

    private String toUserNames;

    private Date feedbackTimeLimit;

    private Date signTimeLimit;

    private Boolean isImNotify;

    private Boolean isSmsNotify;

    private Boolean isMailNotify;

    private DocExchangeRecordStatusEnum forwardStatus;

    private String remark;

    private String fileUuids;

    private String fileNames;

    private Date createTime;


    public String getDocExchangeRecordUuid() {
        return docExchangeRecordUuid;
    }

    public void setDocExchangeRecordUuid(String docExchangeRecordUuid) {
        this.docExchangeRecordUuid = docExchangeRecordUuid;
    }

    public String getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(String toUserIds) {
        this.toUserIds = toUserIds;
    }

    public String getToUserNames() {
        return toUserNames;
    }

    public void setToUserNames(String toUserNames) {
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


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserUnitName() {
        return fromUserUnitName;
    }

    public void setFromUserUnitName(String fromUserUnitName) {
        this.fromUserUnitName = fromUserUnitName;
    }
}

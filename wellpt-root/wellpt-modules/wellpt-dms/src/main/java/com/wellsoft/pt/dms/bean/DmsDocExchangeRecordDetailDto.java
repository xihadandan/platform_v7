package com.wellsoft.pt.dms.bean;

import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 文档交换接收详情
 *
 * @author chenq
 * @date 2018/5/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/16    chenq		2018/5/16		Create
 * </pre>
 */


public class DmsDocExchangeRecordDetailDto implements Serializable {

    private static final long serialVersionUID = 2486412736507893810L;

    private String uuid;

    private String docExchangeRecordUuid;

    private String toUserId;

    private String toUserName;

    private DocExchangeRecordStatusEnum signStatus; // 6 待签收 4 已签收 3 已退回

    private String signStatusName;

    private Date signTime;

    private String signUserName;

    private String signUserId;//签收人ID


    private Boolean isFeedback;


    private Boolean isRevoked;

    private String revokeReason;

    private String returnReason;

    private Integer type;

    private String extraSendUuid;

    private Date feedbackTimeLimit;

    private Date signTimeLimit;


    private boolean isSignNearDeadline;//签收是否快到截止日期，差一个工作日进行提醒

    private boolean isOverdue;//是否逾期

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }

    public String getDocExchangeRecordUuid() {
        return docExchangeRecordUuid;
    }

    public void setDocExchangeRecordUuid(String docExchangeRecordUuid) {
        this.docExchangeRecordUuid = docExchangeRecordUuid;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }


    public Boolean getIsFeedback() {
        return isFeedback;
    }

    public void setIsFeedback(Boolean feedback) {
        isFeedback = feedback;
    }


    public Boolean getIsRevoked() {
        return isRevoked;
    }

    public void setIsRevoked(Boolean revoked) {
        isRevoked = revoked;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }


    public DocExchangeRecordStatusEnum getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(DocExchangeRecordStatusEnum signStatus) {
        this.signStatus = signStatus;
        this.signStatusName = signStatus.getName();
    }

    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getSignStatusName() {
        return signStatusName;
    }

    public void setSignStatusName(String signStatusName) {
        this.signStatusName = signStatusName;
    }

    public String getExtraSendUuid() {
        return extraSendUuid;
    }

    public void setExtraSendUuid(String extraSendUuid) {
        this.extraSendUuid = extraSendUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
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

    public boolean isSignNearDeadline() {
        return isSignNearDeadline;
    }

    public void setSignNearDeadline(boolean signNearDeadline) {
        isSignNearDeadline = signNearDeadline;
    }

    public String getSignUserName() {
        return signUserName;
    }

    public void setSignUserName(String signUserName) {
        this.signUserName = signUserName;
    }

    public String getSignUserId() {
        return signUserId;
    }

    public void setSignUserId(String signUserId) {
        this.signUserId = signUserId;
    }
}

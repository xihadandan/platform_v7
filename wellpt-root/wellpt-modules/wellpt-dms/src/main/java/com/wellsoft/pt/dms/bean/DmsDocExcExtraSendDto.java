package com.wellsoft.pt.dms.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 文档交换补充发送记录
 *
 * @author chenq
 * @date 2018/5/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/18    chenq		2018/5/18		Create
 * </pre>
 */

public class DmsDocExcExtraSendDto implements Serializable {

    private static final long serialVersionUID = -1671678412333900351L;
    private String docExchangeRecordUuid;

    private String toUserIds;

    private String toUserNames;

    private Date feedbackTimeLimit;

    private Date signTimeLimit;

    private Boolean isImNotify;

    private Boolean isSmsNotify;

    private Boolean isMailNotify;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

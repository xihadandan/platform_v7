package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Clob;
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
@Table(name = "DMS_DOC_EXC_EXTRA_SEND_DETAIL")
@Entity
@DynamicInsert
@DynamicUpdate
public class DmsDocExcExtraSendEntity extends TenantEntity {

    private static final long serialVersionUID = 5129028624583751941L;
    private String docExchangeRecordUuid;

    private Clob toUserIds;

    private Clob toUserNames;

    private Date feedbackTimeLimit;

    private Date signTimeLimit;

    private Boolean isImNotify;

    private Boolean isSmsNotify;

    private Boolean isMailNotify;

    public DmsDocExcExtraSendEntity() {

    }

    public DmsDocExcExtraSendEntity(String docExchangeRecordUuid, Clob toUserIds, Clob toUserNames, Date feedbackTimeLimit, Date signTimeLimit, Boolean isImNotify, Boolean isSmsNotify, Boolean isMailNotify) {
        this.docExchangeRecordUuid = docExchangeRecordUuid;
        this.toUserIds = toUserIds;
        this.toUserNames = toUserNames;
        this.feedbackTimeLimit = feedbackTimeLimit;
        this.signTimeLimit = signTimeLimit;
        this.isImNotify = isImNotify;
        this.isSmsNotify = isSmsNotify;
        this.isMailNotify = isMailNotify;
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
}

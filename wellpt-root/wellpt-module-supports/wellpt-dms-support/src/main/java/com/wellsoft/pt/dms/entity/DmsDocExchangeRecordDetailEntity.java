package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
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

@Entity
@Table(name = "DMS_DOC_EXCHANGE_RECORD_DETAIL")
@DynamicUpdate
@DynamicInsert
public class DmsDocExchangeRecordDetailEntity extends TenantEntity {
    private static final long serialVersionUID = 9075816023331547893L;

    private String docExchangeRecordUuid;

    private String toUserId;

    private Boolean isFeedback; //是否已经反馈了

    private DocExchangeRecordStatusEnum signStatus; // 6 待签收 4 已签收 3 已退回

    private Date signTime;//签收/退回时间

    private String signUserId;//签收人ID

    private Boolean isRevoked;//是否已经撤回

    private String revokeReason;//撤回理由

    private String returnReason;//退回理由

    private String extraSendUuid;//补发的UUID

    private Integer type; // 0 正常发送  1 补充发送

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


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    @Enumerated
    public DocExchangeRecordStatusEnum getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(DocExchangeRecordStatusEnum signStatus) {
        this.signStatus = signStatus;
    }

    public String getExtraSendUuid() {
        return extraSendUuid;
    }

    public void setExtraSendUuid(String extraSendUuid) {
        this.extraSendUuid = extraSendUuid;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public String getSignUserId() {
        return signUserId;
    }

    public void setSignUserId(String signUserId) {
        this.signUserId = signUserId;
    }
}

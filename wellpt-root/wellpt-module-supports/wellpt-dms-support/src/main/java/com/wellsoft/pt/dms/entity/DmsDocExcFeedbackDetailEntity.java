package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.dms.enums.DocExchangeFeedbackTypeEnum;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 反馈详情
 *
 * @author chenq
 * @date 2018/5/17
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/17    chenq		2018/5/17		Create
 * </pre>
 */
@Table(name = "DMS_DOC_EXC_FEEDBACK_DETAIL")
@Entity
@DynamicUpdate
@DynamicInsert
public class DmsDocExcFeedbackDetailEntity extends TenantEntity {
    private static final long serialVersionUID = 1949630297753313703L;

    private String docExchangeRecordUuid;


    private String toUserId;

    private String fromUserId;

    private String fromUnitId;

    private String toFeedbackDetailUuid;

    private String fromFeedbackDetailUuid;

    private String content;

    private Date feedbackTime;

    private String fileUuids;

    private String fileNames;

    private DocExchangeFeedbackTypeEnum feedbackType;

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

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToFeedbackDetailUuid() {
        return toFeedbackDetailUuid;
    }

    public void setToFeedbackDetailUuid(String toFeedbackDetailUuid) {
        this.toFeedbackDetailUuid = toFeedbackDetailUuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(Date feedbackTime) {
        this.feedbackTime = feedbackTime;
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

    public DocExchangeFeedbackTypeEnum getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(DocExchangeFeedbackTypeEnum feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getFromFeedbackDetailUuid() {
        return fromFeedbackDetailUuid;
    }

    public void setFromFeedbackDetailUuid(String fromFeedbackDetailUuid) {
        this.fromFeedbackDetailUuid = fromFeedbackDetailUuid;
    }


}

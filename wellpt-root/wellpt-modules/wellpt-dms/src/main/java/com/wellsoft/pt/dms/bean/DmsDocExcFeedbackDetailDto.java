package com.wellsoft.pt.dms.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 反馈详情DTo
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
public class DmsDocExcFeedbackDetailDto implements Serializable {

    private static final long serialVersionUID = 6917155056331964818L;

    private String uuid;

    private String docExchangeRecordUuid;

    private String toUserId;

    private String fromUserName;

    private String fromUserId;

    private String fromUnitId;

    private String fromUserUnitName;

    private String toFeedbackDetailUuid;

    private String fromFeedbackDetailUuid;

    private String content;

    private Date feedbackTime;

    private boolean isOverdue;//是否逾期

    private String fileUuids;

    private String fileNames;

    private String answerContent;//回执内容

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getFromUserUnitName() {
        return fromUserUnitName;
    }

    public void setFromUserUnitName(String fromUserUnitName) {
        this.fromUserUnitName = fromUserUnitName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public String getFromFeedbackDetailUuid() {
        return fromFeedbackDetailUuid;
    }

    public void setFromFeedbackDetailUuid(String fromFeedbackDetailUuid) {
        this.fromFeedbackDetailUuid = fromFeedbackDetailUuid;
    }
}

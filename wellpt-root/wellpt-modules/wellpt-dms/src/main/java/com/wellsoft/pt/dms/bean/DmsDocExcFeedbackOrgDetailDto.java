package com.wellsoft.pt.dms.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: yt
 * @Date: 2021/7/21 22:16
 * @Description:
 */
public class DmsDocExcFeedbackOrgDetailDto extends DmsDocExcFeedbackDetailDto implements Serializable {
    private static final long serialVersionUID = -2132973537301456571L;

    //文档交换-记录明细uuid
    private String recordDetailUuid;

    private String unitId; //单位Id

    private String unitName; //单位名称

    private Boolean isFeedback; //是否已经反馈了

    private Date feedbackTimeLimit; //反馈截止时间

    private boolean isFeedbackNearDeadline;//是否反馈快到截止日期，差一个工作日进行提醒

    public String getRecordDetailUuid() {
        return recordDetailUuid;
    }

    public void setRecordDetailUuid(String recordDetailUuid) {
        this.recordDetailUuid = recordDetailUuid;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Boolean getFeedback() {
        return isFeedback;
    }

    public void setFeedback(Boolean feedback) {
        isFeedback = feedback;
    }

    public Date getFeedbackTimeLimit() {
        return feedbackTimeLimit;
    }

    public void setFeedbackTimeLimit(Date feedbackTimeLimit) {
        this.feedbackTimeLimit = feedbackTimeLimit;
    }

    public boolean isFeedbackNearDeadline() {
        return isFeedbackNearDeadline;
    }

    public void setFeedbackNearDeadline(boolean feedbackNearDeadline) {
        isFeedbackNearDeadline = feedbackNearDeadline;
    }

}

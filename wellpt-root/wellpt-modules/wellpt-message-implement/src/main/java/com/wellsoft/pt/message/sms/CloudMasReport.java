package com.wellsoft.pt.message.sms;

public class CloudMasReport {

    /**
     * 状态报告的值
     */
    private String reportStatus;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * yyyyMMddHHmmss（20170518224800）
     */
    private String submitDate;
    /**
     * yyyyMMddHHmmss（20170518224800）
     */
    private String receiveDate;
    /**
     * 未发送成功错误编码
     */
    private String errorCode;
    /**
     * 消息批次号
     */
    private String msgGroup;

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsgGroup() {
        return msgGroup;
    }

    public void setMsgGroup(String msgGroup) {
        this.msgGroup = msgGroup;
    }

}

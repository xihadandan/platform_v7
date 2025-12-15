package com.wellsoft.pt.ei.dto.mail;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2021/9/23 15:15
 * @Description:
 */
public class SendReceiveInfo implements Serializable {
    private static final long serialVersionUID = -3794247369130053742L;

    @FieldType(desc = "发送人名称", required = true)
    private String fromUserName;
    @FieldType(desc = "发送人邮箱地址", required = true)
    private String fromMailAddress;
    @FieldType(desc = "接收人名称，多个以分号隔开", required = true)
    private String toUserName;
    @FieldType(desc = "接收人邮箱地址/部门、职位、用户、群组id，多个以分号隔开", required = true)
    private String toMailAddress;
    @FieldType(desc = "抄送人名称，多个以分号隔开")
    private String ccUserName;
    @FieldType(desc = "抄送人邮箱地址/部门、职位、用户、群组id，多个以分号隔开")
    private String ccMailAddress;
    @FieldType(desc = "密送人名称，多个以分号隔开，多个以分号隔开")
    private String bccUserName;
    @FieldType(desc = "密送人邮箱地址/部门、职位、用户、群组id，多个以分号隔开")
    private String bccMailAddress;
    @FieldType(desc = "发送状态", type = ExportFieldTypeEnum.INTEGER, dictValue = "0：待发送，1：已发送，2：发送失败")
    private Integer sendStatus;
    @FieldType(desc = "阅读回执状态", type = ExportFieldTypeEnum.INTEGER, dictValue = "0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执")
    private Integer readReceiptStatus;
    @FieldType(desc = "真实接收人")
    private String actualToMailAddress;
    @FieldType(desc = "真实抄送人")
    private String actualCcMailAddress;
    @FieldType(desc = "真实密送人")
    private String actualBccMailAddress;

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromMailAddress() {
        return fromMailAddress;
    }

    public void setFromMailAddress(String fromMailAddress) {
        this.fromMailAddress = fromMailAddress;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getToMailAddress() {
        return toMailAddress;
    }

    public void setToMailAddress(String toMailAddress) {
        this.toMailAddress = toMailAddress;
    }

    public String getCcUserName() {
        return ccUserName;
    }

    public void setCcUserName(String ccUserName) {
        this.ccUserName = ccUserName;
    }

    public String getCcMailAddress() {
        return ccMailAddress;
    }

    public void setCcMailAddress(String ccMailAddress) {
        this.ccMailAddress = ccMailAddress;
    }

    public String getBccUserName() {
        return bccUserName;
    }

    public void setBccUserName(String bccUserName) {
        this.bccUserName = bccUserName;
    }

    public String getBccMailAddress() {
        return bccMailAddress;
    }

    public void setBccMailAddress(String bccMailAddress) {
        this.bccMailAddress = bccMailAddress;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getReadReceiptStatus() {
        return readReceiptStatus;
    }

    public void setReadReceiptStatus(Integer readReceiptStatus) {
        this.readReceiptStatus = readReceiptStatus;
    }

    public String getActualToMailAddress() {
        return actualToMailAddress;
    }

    public void setActualToMailAddress(String actualToMailAddress) {
        this.actualToMailAddress = actualToMailAddress;
    }

    public String getActualCcMailAddress() {
        return actualCcMailAddress;
    }

    public void setActualCcMailAddress(String actualCcMailAddress) {
        this.actualCcMailAddress = actualCcMailAddress;
    }

    public String getActualBccMailAddress() {
        return actualBccMailAddress;
    }

    public void setActualBccMailAddress(String actualBccMailAddress) {
        this.actualBccMailAddress = actualBccMailAddress;
    }
}

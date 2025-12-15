package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Auther: yt
 * @Date: 2022/2/12 11:37
 * @Description: 邮件信息表
 */
@Entity
@Table(name = "WM_MAILBOX_INFO")
@DynamicUpdate
@DynamicInsert
public class WmMailboxInfo extends IdEntity implements UUIDGeneratorIndicate {
    private static final long serialVersionUID = 1158760553812905807L;
    /**
     * 发送人名称
     */
    private String fromUserName;
    /**
     * 发送人邮箱地址
     */
    private String fromMailAddress;
    /**
     * 接收人名称，多个以分号隔开
     */
    private String toUserName;
    /**
     * 接收人邮箱地址/部门、职位、用户、群组id
     */
    private String toMailAddress;
    /**
     * 抄送人名称，多个以分号隔开
     */
    private String ccUserName;
    /**
     * 抄送人邮箱地址/部门、职位、用户、群组id
     */
    private String ccMailAddress;
    /**
     * 密送人名称，多个以分号隔开
     */
    private String bccUserName;
    /**
     * 密送人邮箱地址/部门、职位、用户、群组id
     */
    private String bccMailAddress;
    /**
     * 实际接收人邮件地址
     */
    private String actualToMailAddress;
    /**
     * 实际抄送人邮件地址
     */
    private String actualCcMailAddress;
    /**
     * 实际密送人邮件地址
     */
    private String actualBccMailAddress;

    /**
     * 主题
     */
    private String subject;
    /**
     * 邮件文本内容
     */
    private String content;
    /**
     * mogodb附件名称，多个以分隔开
     */
    private String repoFileNames;
    /**
     * mogodb附件UUID，多个以分隔开
     */
    private String repoFileUuids;
    /**
     * 邮件大小
     */
    private Long mailSize;
    /**
     * 阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）
     */
    private Integer readReceiptStatus;
    /**
     * 优先级（1：最高，2：高，3: 正常 默认值，4：低，5：最低）
     */
    private Integer priority;
    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 是否公网邮箱 0：否，1：是
     */
    private Boolean isPublicEmail;

    /**
     * 是否在服务器保留备份
     */
    private Boolean keepOnServer;
    /**
     * 是否自动发送回执 0：否，1：是
     */
    private Boolean sendReceipt;


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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRepoFileNames() {
        return repoFileNames;
    }

    public void setRepoFileNames(String repoFileNames) {
        this.repoFileNames = repoFileNames;
    }

    public String getRepoFileUuids() {
        return repoFileUuids;
    }

    public void setRepoFileUuids(String repoFileUuids) {
        this.repoFileUuids = repoFileUuids;
    }

    public Long getMailSize() {
        return mailSize;
    }

    public void setMailSize(Long mailSize) {
        this.mailSize = mailSize;
    }

    public Integer getReadReceiptStatus() {
        return readReceiptStatus;
    }

    public void setReadReceiptStatus(Integer readReceiptStatus) {
        this.readReceiptStatus = readReceiptStatus;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
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

    public Boolean getIsPublicEmail() {
        return isPublicEmail;
    }

    public void setIsPublicEmail(Boolean publicEmail) {
        isPublicEmail = publicEmail;
    }

    public Boolean getKeepOnServer() {
        return keepOnServer;
    }

    public void setKeepOnServer(Boolean keepOnServer) {
        this.keepOnServer = keepOnServer;
    }

    public Boolean getSendReceipt() {
        return sendReceipt;
    }

    public void setSendReceipt(Boolean sendReceipt) {
        this.sendReceipt = sendReceipt;
    }
}

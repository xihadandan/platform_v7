package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import com.wellsoft.pt.webmail.enums.WmMailBoxStatus;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Auther: yt
 * @Date: 2022/2/12 11:43
 * @Description: 邮件信息关联用户表
 */
@Entity
@Table(name = "WM_MAILBOX_INFO_USER")
@DynamicUpdate
@DynamicInsert
public class WmMailboxInfoUser extends IdEntity implements UUIDGeneratorIndicate {
    private static final long serialVersionUID = -5775238520331435964L;


    /**
     * 用户Id
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 邮箱地址
     */
    private String mailAddress;

    /**
     * 阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）
     */
    private Integer readReceiptStatus;
    /**
     * 旧状态 （0：待解析，1：已发送，2：解析失败，3,实际邮件发送失败）
     * 发送状态（0：待解析，1：发送成功（全部发送成功）2：解析失败（具体报错原因存入failMsg），3：发送失败（全部失败，或部分失败），4：解析成功（投递中））
     */
    private Integer sendStatus;
    /**
     * 执行发送次数
     */
    private Integer sendCount;
    /**
     * 发送失败信息
     */
    private String failMsg;
    /**
     * 下次执行发送时间
     */
    private Date nextSendTime;

    /**
     * 邮件Message-ID，从邮件服务器上取到的邮件唯一标识
     */
    private Integer mid;
    /**
     * 邮件P-ID，从邮件服务器上取到的邮件唯一标识
     */
    private String pid;
    /**
     * 邮件文件夹 系统文件夹：（INBOX：收件箱，OUTBOX：发件箱，DRAFT：草稿箱，RECYCLE：回收站 ），其他值代表 其他文件夹
     */
    private String mailboxName;
    /**
     * 是否已读 0=未读 1=已读
     */
    private Integer isRead;
    /**
     * 阅读时间
     */
    private Date readTime;
    /**
     * 邮件状态 0草稿 1发送成功 2接收成功 3接收失败（空间不足） -1删除 -2彻底删除
     */
    private Integer status;
    /**
     * 撤回状态：0 撤回失败 1 撤回成功 2 部分撤回成功
     */
    private Integer revokeStatus;
    /**
     * 系统单位Id
     */
    private String systemUnitId;
    /**
     * 邮件信息uuid
     */
    private String mailInfoUuid;

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public Integer getReadReceiptStatus() {
        return readReceiptStatus;
    }

    public void setReadReceiptStatus(Integer readReceiptStatus) {
        this.readReceiptStatus = readReceiptStatus;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getMailboxName() {
        return mailboxName;
    }

    public void setMailboxName(String mailboxName) {
        this.mailboxName = mailboxName;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRevokeStatus() {
        return revokeStatus;
    }

    public void setRevokeStatus(Integer revokeStatus) {
        this.revokeStatus = revokeStatus;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getMailInfoUuid() {
        return mailInfoUuid;
    }

    public void setMailInfoUuid(String mailInfoUuid) {
        this.mailInfoUuid = mailInfoUuid;
    }

    public Integer getSendCount() {
        return sendCount;
    }

    public void setSendCount(Integer sendCount) {
        this.sendCount = sendCount;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    public Date getNextSendTime() {
        return nextSendTime;
    }

    public void setNextSendTime(Date nextSendTime) {
        this.nextSendTime = nextSendTime;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    /**
     * 真实信箱
     *
     * @return
     */
    public String translateMailbox() {
        if (WmMailBoxStatus.DRAFT.getCode() ==
                this.getStatus()) {//草稿箱
            return WmWebmailConstants.DRAFT;
        }
        if (WmMailBoxStatus.LOGICAL_DELETE.getCode() ==
                this.getStatus()) {//回收站
            return WmWebmailConstants.RECYCLE;
        }
        if (this.getMailboxName().startsWith(WmMailFolderEntity.FOLDER_CODE_PREFIX)) {//自定义文件夹名
            return this.getMailboxName();
        }
        return this.getMailboxName();
    }
}

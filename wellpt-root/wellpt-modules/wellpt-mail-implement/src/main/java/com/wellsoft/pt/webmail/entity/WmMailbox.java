/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.webmail.enums.WmMailBoxStatus;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 邮件实体类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
@Entity
@Table(name = "WM_MAILBOX")
@DynamicUpdate
@DynamicInsert
public class WmMailbox extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1464933679830L;

    /**
     * 用户Id
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 主题
     */
    private String subject;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 邮件大小
     */
    private Long mailSize;
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
     * 阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）
     */
    private Integer readReceiptStatus;
    /**
     * 优先级（1：最高，2：高，3: 正常 默认值，4：低，5：最低）
     */
    private Integer priority;
    /**
     * 实际发送状态
     *
     * @see com.wellsoft.pt.webmail.enums.WmMailBoxActualStatus
     */
    private String actualToStatus;
    /**
     * 实际抄送状态
     *
     * @see com.wellsoft.pt.webmail.enums.WmMailBoxActualStatus
     */
    private String actualCcStatus;
    /**
     * 实际密送状态
     *
     * @see com.wellsoft.pt.webmail.enums.WmMailBoxActualStatus
     */
    private String actualBccStatus;
    /**
     * 发送状态（0：待发送，1：已发送，2：发送失败）
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
     * 邮件Message-ID，从邮件服务器上取到的邮件唯一标识
     */
    private String mid;
    /**
     * 邮件文件夹 系统文件夹：（INBOX：收件箱，OUTBOX：发件箱，DRAFT：草稿箱，RECYCLE：回收站 ），其他值代表 其他文件夹
     */
    private String mailboxName;
    /**
     * 是否已读 0=未读 1=已读
     */
    private String isRead;
    /**
     * 邮件状态 0草稿 1发送成功 2接收成功 -1删除 -2彻底删除
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
     * 来源邮件uuid
     */
    private String fromMailUuid;


    /**
     * 获取 用户id
     *
     * @return the userId
     */
    @Column(name = "USER_ID")
    public String getUserId() {
        return this.userId;
    }

    /**
     * 设置 用户Id
     *
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取 用户名称
     *
     * @return the userName
     */
    @Column(name = "USER_NAME")
    public String getUserName() {
        return this.userName;
    }

    /**
     * 设置 用户名称
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取 主题
     *
     * @return the subject
     */
    @Column(name = "SUBJECT")
    public String getSubject() {
        return this.subject;
    }

    /**
     * 设置 主题
     *
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * 获取 发送时间
     *
     * @return the sendTime
     */
    @Column(name = "SEND_TIME")
    public Date getSendTime() {
        return this.sendTime;
    }

    /**
     * 设置 发送时间
     *
     * @param sendTime
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * 获取 邮件大小
     *
     * @return the mailSize
     */
    @Column(name = "MAIL_SIZE")
    public Long getMailSize() {
        return this.mailSize;
    }

    /**
     * 设置 邮件大小
     *
     * @param mailSize
     */
    public void setMailSize(Long mailSize) {
        this.mailSize = mailSize;
    }

    /**
     * 获取 发送人名称
     *
     * @return the fromUserName
     */
    @Column(name = "FROM_USER_NAME")
    public String getFromUserName() {
        return this.fromUserName;
    }

    /**
     * 设置 发送人名称
     *
     * @param fromUserName
     */
    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    /**
     * 获取 发送人邮箱地址
     *
     * @return the fromMailAddress
     */
    @Column(name = "FROM_MAIL_ADDRESS")
    public String getFromMailAddress() {
        return this.fromMailAddress;
    }

    /**
     * 设置 发送人邮箱地址
     *
     * @param fromMailAddress
     */
    public void setFromMailAddress(String fromMailAddress) {
        this.fromMailAddress = fromMailAddress;
    }

    /**
     * 获取 接收人邮箱地址/部门、职位、用户、群组id
     *
     * @return the toUserName
     */
    @Column(name = "TO_USER_NAME")
    public String getToUserName() {
        return this.toUserName;
    }

    /**
     * 设置 接收人邮箱地址/部门、职位、用户、群组id
     *
     * @param toUserName
     */
    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    /**
     * 获取 接收人邮箱地址/部门、职位、用户、群组id
     *
     * @return the toMailAddress
     */
    @Column(name = "TO_MAIL_ADDRESS")
    public String getToMailAddress() {
        return this.toMailAddress;
    }

    /**
     * 设置 接收人邮箱地址/部门、职位、用户、群组id
     *
     * @param toMailAddress
     */
    public void setToMailAddress(String toMailAddress) {
        this.toMailAddress = toMailAddress;
    }

    /**
     * 获取 抄送人名称，多个以分号隔开
     *
     * @return the ccUserName
     */
    @Column(name = "CC_USER_NAME")
    public String getCcUserName() {
        return this.ccUserName;
    }

    /**
     * 设置 抄送人名称，多个以分号隔开
     *
     * @param ccUserName
     */
    public void setCcUserName(String ccUserName) {
        this.ccUserName = ccUserName;
    }

    /**
     * 获取 抄送人邮箱地址/部门、职位、用户、群组id
     *
     * @return the ccMailAddress
     */
    @Column(name = "CC_MAIL_ADDRESS")
    public String getCcMailAddress() {
        return this.ccMailAddress;
    }

    /**
     * 设置 抄送人邮箱地址/部门、职位、用户、群组id
     *
     * @param ccMailAddress
     */
    public void setCcMailAddress(String ccMailAddress) {
        this.ccMailAddress = ccMailAddress;
    }

    /**
     * 获取 密送人名称，多个以分号隔开
     *
     * @return the bccUserName
     */
    @Column(name = "BCC_USER_NAME")
    public String getBccUserName() {
        return this.bccUserName;
    }

    /**
     * 设置 密送人名称，多个以分号隔开
     *
     * @param bccUserName
     */
    public void setBccUserName(String bccUserName) {
        this.bccUserName = bccUserName;
    }

    /**
     * 获取 密送人邮箱地址/部门、职位、用户、群组id
     *
     * @return the bccMailAddress
     */
    @Column(name = "BCC_MAIL_ADDRESS")
    public String getBccMailAddress() {
        return this.bccMailAddress;
    }

    /**
     * 设置 密送人邮箱地址/部门、职位、用户、群组id
     *
     * @param bccMailAddress
     */
    public void setBccMailAddress(String bccMailAddress) {
        this.bccMailAddress = bccMailAddress;
    }

    /**
     * 获取 邮件文本内容
     *
     * @return the content
     */
    @Column(name = "CONTENT")
    public String getContent() {
        return this.content;
    }

    /**
     * 设置 邮件文本内容
     *
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取 mogodb附件名称，多个以分隔开
     *
     * @return the repoFileNames
     */
    @Column(name = "REPO_FILE_NAMES")
    public String getRepoFileNames() {
        return this.repoFileNames;
    }

    /**
     * 设置 mogodb附件名称，多个以分隔开
     *
     * @param repoFileNames
     */
    public void setRepoFileNames(String repoFileNames) {
        this.repoFileNames = repoFileNames;
    }

    /**
     * 获取 mogodb附件UUID，多个以分隔开
     *
     * @return the repoFileUuids
     */
    @Column(name = "REPO_FILE_UUIDS")
    public String getRepoFileUuids() {
        return this.repoFileUuids;
    }

    /**
     * 设置 mogodb附件UUID，多个以分隔开
     *
     * @param repoFileUuids
     */
    public void setRepoFileUuids(String repoFileUuids) {
        this.repoFileUuids = repoFileUuids;
    }

    /**
     * 获取 邮件Message-ID，从邮件服务器上取到的邮件唯一标识
     *
     * @return the mid
     */
    @Column(name = "MID")
    public String getMid() {
        return this.mid;
    }

    /**
     * 设置 邮件Message-ID，从邮件服务器上取到的邮件唯一标识
     *
     * @param mid
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     * 获取 邮件文件夹 系统文件夹：（INBOX：收件箱，OUTBOX：发件箱，DRAFT：草稿箱，RECYCLE：回收站 ），其他值代表 其他文件夹
     *
     * @return the mailboxName
     */
    @Column(name = "MAILBOX_NAME")
    public String getMailboxName() {
        return this.mailboxName;
    }

    /**
     * 设置 邮件文件夹 系统文件夹：（INBOX：收件箱，OUTBOX：发件箱，DRAFT：草稿箱，RECYCLE：回收站 ），其他值代表 其他文件夹
     *
     * @param mailboxName
     */
    public void setMailboxName(String mailboxName) {
        this.mailboxName = mailboxName;
    }

    /**
     * 获取 是否已读 0=未读 1=已读
     *
     * @return the isRead
     */
    public String getIsRead() {
        return isRead;
    }

    /**
     * 设置 是否已读 0=未读 1=已读
     *
     * @param isRead 要设置的isRead
     */
    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    /**
     * 获取 邮件状态 0草稿 1发送成功 2接收成功 -1删除 -2彻底删除
     *
     * @return the status
     */
    @Column(name = "STATUS")
    public Integer getStatus() {
        return this.status;
    }

    /**
     * 设置 邮件状态 0草稿 1发送成功 2接收成功 -1删除 -2彻底删除
     *
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取 撤回状态：0 撤回失败 1 撤回成功 2 部分撤回成功
     *
     * @return the revokeStatus
     */
    public Integer getRevokeStatus() {
        return revokeStatus;
    }

    /**
     * 设置 撤回状态：0 撤回失败 1 撤回成功 2 部分撤回成功
     *
     * @param revokeStatus 要设置的revokeStatus
     */
    public void setRevokeStatus(Integer revokeStatus) {
        this.revokeStatus = revokeStatus;
    }

    /**
     * 获取 系统单位Id
     *
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * 设置 系统单位Id
     *
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
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

    /**
     * 获取 来源邮件uuid
     *
     * @return
     */
    public String getFromMailUuid() {
        return fromMailUuid;
    }

    /**
     * 设置 来源邮件uuid
     *
     * @param fromMailUuid
     */
    public void setFromMailUuid(String fromMailUuid) {
        this.fromMailUuid = fromMailUuid;
    }

    /**
     * 获取 实际接收人邮件地址
     *
     * @return
     */
    public String getActualToMailAddress() {
        return actualToMailAddress;
    }

    /**
     * 设置 实际接收人邮件地址
     *
     * @param actualToMailAddress
     */
    public void setActualToMailAddress(String actualToMailAddress) {
        this.actualToMailAddress = actualToMailAddress;
    }

    /**
     * 获取 实际抄送人邮件地址
     *
     * @return
     */
    public String getActualCcMailAddress() {
        return actualCcMailAddress;
    }

    /**
     * 设置 实际抄送人邮件地址
     *
     * @param actualCcMailAddress
     */
    public void setActualCcMailAddress(String actualCcMailAddress) {
        this.actualCcMailAddress = actualCcMailAddress;
    }

    /**
     * 获取 实际密送人邮件地址
     *
     * @return
     */
    public String getActualBccMailAddress() {
        return actualBccMailAddress;
    }

    /**
     * 设置 实际密送人邮件地址
     *
     * @param actualBccMailAddress
     */
    public void setActualBccMailAddress(String actualBccMailAddress) {
        this.actualBccMailAddress = actualBccMailAddress;
    }

    /**
     * 获取 阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）
     *
     * @return
     */
    public Integer getReadReceiptStatus() {
        return readReceiptStatus;
    }

    /**
     * 设置 阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）
     *
     * @param readReceiptStatus
     */
    public void setReadReceiptStatus(Integer readReceiptStatus) {
        this.readReceiptStatus = readReceiptStatus;
    }

    /**
     * 获取 优先级（1：最高，2：高，3: 正常 默认值，4：低，5：最低）
     *
     * @return
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * 设置 优先级（1：最高，2：高，3: 正常 默认值，4：低，5：最低）
     *
     * @param priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * 获取 实际发送状态
     *
     * @return
     * @see com.wellsoft.pt.webmail.enums.WmMailBoxActualStatus
     */
    public String getActualToStatus() {
        return actualToStatus;
    }

    /**
     * 设置 实际发送状态
     *
     * @param actualToStatus
     * @see com.wellsoft.pt.webmail.enums.WmMailBoxActualStatus
     */
    public void setActualToStatus(String actualToStatus) {
        this.actualToStatus = actualToStatus;
    }

    /**
     * 获取 实际抄送状态
     *
     * @return
     * @see com.wellsoft.pt.webmail.enums.WmMailBoxActualStatus
     */
    public String getActualCcStatus() {
        return actualCcStatus;
    }

    /**
     * 设置 实际抄送状态
     *
     * @param actualCcStatus
     * @see com.wellsoft.pt.webmail.enums.WmMailBoxActualStatus
     */
    public void setActualCcStatus(String actualCcStatus) {
        this.actualCcStatus = actualCcStatus;
    }

    /**
     * 获取 实际密送状态
     *
     * @return
     * @see com.wellsoft.pt.webmail.enums.WmMailBoxActualStatus
     */
    public String getActualBccStatus() {
        return actualBccStatus;
    }

    /**
     * 设置 实际密送状态
     *
     * @param actualBccStatus
     * @see com.wellsoft.pt.webmail.enums.WmMailBoxActualStatus
     */
    public void setActualBccStatus(String actualBccStatus) {
        this.actualBccStatus = actualBccStatus;
    }

    /**
     * 获取 发送状态（0：待发送，1：已发送，2：发送失败）
     *
     * @return
     */
    public Integer getSendStatus() {
        return sendStatus;
    }

    /**
     * 设置 发送状态（0：待发送，1：已发送，2：发送失败）
     *
     * @param sendStatus
     */
    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    /**
     * 获取 执行发送次数
     *
     * @return
     */
    public Integer getSendCount() {
        return sendCount;
    }

    /**
     * 设置 执行发送次数
     *
     * @param sendCount
     */
    public void setSendCount(Integer sendCount) {
        this.sendCount = sendCount;
    }

    /**
     * 获取 发送失败信息
     *
     * @return
     */
    public String getFailMsg() {
        return failMsg;
    }

    /**
     * 设置 发送失败信息
     *
     * @param failMsg
     */
    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    /**
     * 获取 下次执行发送时间
     *
     * @return
     */
    public Date getNextSendTime() {
        return nextSendTime;
    }

    /**
     * 设置 下次执行发送时间
     *
     * @param nextSendTime
     */
    public void setNextSendTime(Date nextSendTime) {
        this.nextSendTime = nextSendTime;
    }
}

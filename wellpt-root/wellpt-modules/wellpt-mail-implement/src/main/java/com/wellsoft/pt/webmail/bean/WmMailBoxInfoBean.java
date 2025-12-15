package com.wellsoft.pt.webmail.bean;

import com.wellsoft.pt.webmail.entity.WmMailboxInfo;

import java.util.Date;

/**
 * Description: 邮箱实体Bean
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022/2/16.1	liuyz		2022/2/16		Create
 * </pre>
 * @date 2022/2/16
 */
public class WmMailBoxInfoBean extends WmMailboxInfo {

    /**
     * WmMailBoxInfoUser 字段信息
     */
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）
     */
    private Integer userReadReceiptStatus;
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
    private Integer isRead;
    /**
     * 阅读时间
     */
    private Date readTime;
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
     * 邮件信息uuid
     */
    private String mailInfoUuid;

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

    public Integer getUserReadReceiptStatus() {
        return userReadReceiptStatus;
    }

    public void setUserReadReceiptStatus(Integer userReadReceiptStatus) {
        this.userReadReceiptStatus = userReadReceiptStatus;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
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

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }
}

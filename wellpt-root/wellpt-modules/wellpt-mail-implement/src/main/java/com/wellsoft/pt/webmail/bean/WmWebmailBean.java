/*
 * @(#)2016年6月3日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import com.wellsoft.pt.repository.entity.LogicFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * Description: 邮件bean
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月3日.1	zhulh		2016年6月3日		Create
 * </pre>
 * @date 2016年6月3日
 */
@ApiModel(value = "邮件对象")
public class WmWebmailBean {

    @ApiModelProperty("发送人邮件地址")
    private List<WmMailUserDto> fromMailAddresses;
    @ApiModelProperty("邮箱数据UUID")
    private String mailboxUuid;
    @ApiModelProperty("源邮箱数据UUID")
    private String fromMailUuid;
    @ApiModelProperty("邮件服务器的邮件id")
    private String mid;
    @ApiModelProperty("发送人名称")
    private String fromUserName;
    @ApiModelProperty("发送邮件地址")
    private String fromMailAddress;
    @ApiModelProperty("收件人名称")
    private String toUserName;
    @ApiModelProperty("收件人智能名称")
    private String toSmartUserName;
    @ApiModelProperty("收件人地址")
    private String toMailAddress;
    @ApiModelProperty("抄送人名称")
    private String ccUserName;
    @ApiModelProperty("抄送人智能名称")
    private String ccSmartUserName;
    @ApiModelProperty("抄送人地址")
    private String ccMailAddress;
    @ApiModelProperty("密送人名称")
    private String bccUserName;
    @ApiModelProperty("密送人智能名称")
    private String bccSmartUserName;
    @ApiModelProperty("密送人地址")
    private String bccMailAddress;
    @ApiModelProperty("主题")
    private String subject;
    @ApiModelProperty("0草稿 1发送成功 2接收成功 -1删除 -2彻底删除")
    private Integer status;
    @ApiModelProperty("发送时间")
    private Date sendTime;
    @ApiModelProperty("内容")
    private String content;
    @ApiModelProperty("MONGODB附件名称")
    private String repoFileNames;
    @ApiModelProperty("MONGODB附件UUID")
    private String repoFileUuids;
    @ApiModelProperty("MONGODB附件")
    private List<LogicFileInfo> repoFiles;
    @ApiModelProperty("设置回复人名称")
    private String replyToUserName;
    @ApiModelProperty("设置回复人地址")
    private String replyToMailAddress;
    @ApiModelProperty("阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）")
    private Integer readReceiptStatus;
    @ApiModelProperty("优先级（1：最高，2：高，3: 正常 默认值，4：低，5：最低）")
    private Integer priority;
    @ApiModelProperty("发送状态（0：待发送，1：已发送，2：发送失败）")
    private Integer sendStatus;
    @ApiModelProperty("发送失败信息")
    private String failMsg;
    @ApiModelProperty("是否转发")
    private boolean transfer;
    @ApiModelProperty("是否回复")
    private boolean reply;
    @ApiModelProperty("是否回复全部")
    private boolean replyAll;
    @ApiModelProperty("是否再次编辑")
    private boolean editAgain;
    @ApiModelProperty("INBOX收件箱、OUTBOX发件箱")
    private String mailboxName;
    @ApiModelProperty("撤回状态：0 撤回失败 1 撤回成功 2 部分撤回成功")
    private Integer revokeStatus;
    @ApiModelProperty("是否已读：0 未读 1 已读")
    private String isRead;
    @ApiModelProperty("userId")
    private String userId;

    /**
     * @return the fromMailAddresses
     */
    public List<WmMailUserDto> getFromMailAddresses() {
        return fromMailAddresses;
    }

    /**
     * @param fromMailAddresses 要设置的fromMailAddresses
     */
    public void setFromMailAddresses(List<WmMailUserDto> fromMailAddresses) {
        this.fromMailAddresses = fromMailAddresses;
    }

    /**
     * @return the mailboxUuid
     */
    public String getMailboxUuid() {
        return mailboxUuid;
    }

    /**
     * @param mailboxUuid 要设置的mailboxUuid
     */
    public void setMailboxUuid(String mailboxUuid) {
        this.mailboxUuid = mailboxUuid;
    }

    /**
     * @return the fromUserName
     */
    public String getFromUserName() {
        return fromUserName;
    }

    /**
     * @param fromUserName 要设置的fromUserName
     */
    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    /**
     * @return the fromMailAddress
     */
    public String getFromMailAddress() {
        return fromMailAddress;
    }

    /**
     * @param fromMailAddress 要设置的fromMailAddress
     */
    public void setFromMailAddress(String fromMailAddress) {
        this.fromMailAddress = fromMailAddress;
    }

    /**
     * @return the toUserName
     */
    public String getToUserName() {
        return toUserName;
    }

    /**
     * @param toUserName 要设置的toUserName
     */
    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    /**
     * @return the toMailAddress
     */
    public String getToMailAddress() {
        return toMailAddress;
    }

    /**
     * @param toMailAddress 要设置的toMailAddress
     */
    public void setToMailAddress(String toMailAddress) {
        this.toMailAddress = toMailAddress;
    }

    /**
     * @return the ccUserName
     */
    public String getCcUserName() {
        return ccUserName;
    }

    /**
     * @param ccUserName 要设置的ccUserName
     */
    public void setCcUserName(String ccUserName) {
        this.ccUserName = ccUserName;
    }

    /**
     * @return the ccMailAddress
     */
    public String getCcMailAddress() {
        return ccMailAddress;
    }

    /**
     * @param ccMailAddress 要设置的ccMailAddress
     */
    public void setCcMailAddress(String ccMailAddress) {
        this.ccMailAddress = ccMailAddress;
    }

    /**
     * @return the bccUserName
     */
    public String getBccUserName() {
        return bccUserName;
    }

    /**
     * @param bccUserName 要设置的bccUserName
     */
    public void setBccUserName(String bccUserName) {
        this.bccUserName = bccUserName;
    }

    /**
     * @return the bccMailAddress
     */
    public String getBccMailAddress() {
        return bccMailAddress;
    }

    /**
     * @param bccMailAddress 要设置的bccMailAddress
     */
    public void setBccMailAddress(String bccMailAddress) {
        this.bccMailAddress = bccMailAddress;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject 要设置的subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the sendTime
     */
    public Date getSendTime() {
        return sendTime;
    }

    /**
     * @param sendTime 要设置的sendTime
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the repoFileNames
     */
    public String getRepoFileNames() {
        return repoFileNames;
    }

    /**
     * @param repoFileNames 要设置的repoFileNames
     */
    public void setRepoFileNames(String repoFileNames) {
        this.repoFileNames = repoFileNames;
    }

    /**
     * @return the repoFileUuids
     */
    public String getRepoFileUuids() {
        return repoFileUuids;
    }

    /**
     * @param repoFileUuids 要设置的repoFileUuids
     */
    public void setRepoFileUuids(String repoFileUuids) {
        this.repoFileUuids = repoFileUuids;
    }

    /**
     * @return the repoFiles
     */
    public List<LogicFileInfo> getRepoFiles() {
        return repoFiles;
    }

    /**
     * @param repoFiles 要设置的repoFiles
     */
    public void setRepoFiles(List<LogicFileInfo> repoFiles) {
        this.repoFiles = repoFiles;
    }

    /**
     * @return the replyToUserName
     */
    public String getReplyToUserName() {
        return replyToUserName;
    }

    /**
     * @param replyToUserName 要设置的replyToUserName
     */
    public void setReplyToUserName(String replyToUserName) {
        this.replyToUserName = replyToUserName;
    }

    /**
     * @return the replyToMailAddress
     */
    public String getReplyToMailAddress() {
        return replyToMailAddress;
    }

    /**
     * @param replyToMailAddress 要设置的replyToMailAddress
     */
    public void setReplyToMailAddress(String replyToMailAddress) {
        this.replyToMailAddress = replyToMailAddress;
    }

    /**
     * @return the transfer
     */
    public boolean isTransfer() {
        return transfer;
    }

    /**
     * @param transfer 要设置的transfer
     */
    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

    /**
     * @return the reply
     */
    public boolean isReply() {
        return reply;
    }

    /**
     * @param reply 要设置的reply
     */
    public void setReply(boolean reply) {
        this.reply = reply;
    }

    /**
     * @return the replyAll
     */
    public boolean isReplyAll() {
        return replyAll;
    }

    /**
     * @param replyAll 要设置的replyAll
     */
    public void setReplyAll(boolean replyAll) {
        this.replyAll = replyAll;
    }

    /**
     * @return the mailboxName
     */
    public String getMailboxName() {
        return mailboxName;
    }

    /**
     * @param mailboxName 要设置的mailboxName
     */
    public void setMailboxName(String mailboxName) {
        this.mailboxName = mailboxName;
    }

    /**
     * @return the revokeStatus
     */
    public Integer getRevokeStatus() {
        return revokeStatus;
    }

    /**
     * @param revokeStatus 要设置的revokeStatus
     */
    public void setRevokeStatus(Integer revokeStatus) {
        this.revokeStatus = revokeStatus;
    }

    /**
     * @return the mid
     */
    public String getMid() {
        return mid;
    }

    /**
     * @param mid 要设置的mid
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     * @return the isRead
     */
    public String getIsRead() {
        return isRead;
    }

    /**
     * @param isRead 要设置的isRead
     */
    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFromMailUuid() {
        return fromMailUuid;
    }

    public void setFromMailUuid(String fromMailUuid) {
        this.fromMailUuid = fromMailUuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToSmartUserName() {
        return toSmartUserName;
    }

    public void setToSmartUserName(String toSmartUserName) {
        this.toSmartUserName = toSmartUserName;
    }

    public String getCcSmartUserName() {
        return ccSmartUserName;
    }

    public void setCcSmartUserName(String ccSmartUserName) {
        this.ccSmartUserName = ccSmartUserName;
    }

    public String getBccSmartUserName() {
        return bccSmartUserName;
    }

    public void setBccSmartUserName(String bccSmartUserName) {
        this.bccSmartUserName = bccSmartUserName;
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

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    public boolean isEditAgain() {
        return editAgain;
    }

    public void setEditAgain(boolean editAgain) {
        this.editAgain = editAgain;
    }
}

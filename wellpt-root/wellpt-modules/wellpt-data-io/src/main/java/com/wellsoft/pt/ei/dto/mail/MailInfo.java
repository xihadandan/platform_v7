package com.wellsoft.pt.ei.dto.mail;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Auther: yt
 * @Date: 2021/9/23 15:15
 * @Description:
 */
public class MailInfo implements Serializable {
    private static final long serialVersionUID = 2310341099788010188L;

    @FieldType(desc = "主键uuid", required = true)
    private String uuid;
    @FieldType(desc = "用户ID", required = true)
    private String userId;
    @FieldType(desc = "用户名称")
    private String userName;
    @FieldType(desc = "关联文件夹", dictValue = "系统文件夹：（INBOX：收件箱，OUTBOX：发件箱，DRAFT：草稿箱，RECYCLE：回收站 ），其他值代表 其他文件夹 ", required = true)
    private String mailboxName;
    @FieldType(desc = "数据标签", dictValue = "标签uuid集合")
    private List<String> tags;
    @FieldType(desc = "主题", required = true)
    private String subject;
    @FieldType(desc = "发送时间", type = ExportFieldTypeEnum.DATE, required = true)
    private Date sendTime;
    @FieldType(desc = "邮件文本内容", type = ExportFieldTypeEnum.CLOB)
    private String content;
    @FieldType(desc = "附件", type = ExportFieldTypeEnum.FILE)
    private List<String> repoFile;
    @FieldType(desc = "阅读回执状态", type = ExportFieldTypeEnum.INTEGER, dictValue = "0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执")
    private Integer readReceiptStatus;
    @FieldType(desc = "优先级", type = ExportFieldTypeEnum.INTEGER, dictValue = "1：最高，2：高，3: 正常 默认值，4：低，5：最低")
    private Integer priority;
    @FieldType(desc = "是否已读", dictValue = "0：未读， 1：已读")
    private String isRead;
    @FieldType(desc = "邮件类型", dictValue = "0：草稿，1：发件，2：收件，-1：回收站")
    private Integer mailType;
    @FieldType(desc = "撤回状态", dictValue = "0：撤回失败，1：撤回成功，2：部分撤回成功")
    private Integer revokeStatus;
    @FieldType(desc = "来源邮件uuid")
    private String fromMailUuid;
    @FieldType(desc = "是否公网邮箱 0：否，1：是")
    private Integer isPublicEmail;
    @FieldType(desc = "发送次数")
    private Integer sendCount;
    @FieldType(desc = "失败原因")
    private String failMsg;
    @FieldType(desc = "下次执行发送时间", type = ExportFieldTypeEnum.DATE)
    private Date nextSendTime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getMailboxName() {
        return mailboxName;
    }

    public void setMailboxName(String mailboxName) {
        this.mailboxName = mailboxName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getRepoFile() {
        return repoFile;
    }

    public void setRepoFile(List<String> repoFile) {
        this.repoFile = repoFile;
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

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getFromMailUuid() {
        return fromMailUuid;
    }

    public void setFromMailUuid(String fromMailUuid) {
        this.fromMailUuid = fromMailUuid;
    }

    public Integer getMailType() {
        return mailType;
    }

    public void setMailType(Integer mailType) {
        this.mailType = mailType;
    }

    public Integer getRevokeStatus() {
        return revokeStatus;
    }

    public void setRevokeStatus(Integer revokeStatus) {
        this.revokeStatus = revokeStatus;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getIsPublicEmail() {
        return isPublicEmail;
    }

    public void setIsPublicEmail(Integer isPublicEmail) {
        this.isPublicEmail = isPublicEmail;
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
}

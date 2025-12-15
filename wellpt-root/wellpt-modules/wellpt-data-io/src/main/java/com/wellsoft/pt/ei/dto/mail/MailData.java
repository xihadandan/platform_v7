package com.wellsoft.pt.ei.dto.mail;

import com.wellsoft.pt.ei.annotate.FieldType;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2021/9/23 11:11
 * @Description:
 */
public class MailData implements Serializable {
    private static final long serialVersionUID = -7365138259878901302L;

    @FieldType(desc = "邮件信息", isGroup = true)
    private MailInfo mailInfo;

    @FieldType(desc = "收发信息", isGroup = true)
    private SendReceiveInfo sendReceiveInfo;

    public MailInfo getMailInfo() {
        return mailInfo;
    }

    public void setMailInfo(MailInfo mailInfo) {
        this.mailInfo = mailInfo;
    }

    public SendReceiveInfo getSendReceiveInfo() {
        return sendReceiveInfo;
    }

    public void setSendReceiveInfo(SendReceiveInfo sendReceiveInfo) {
        this.sendReceiveInfo = sendReceiveInfo;
    }
}

/*
 * @(#)2015年8月6日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.support;

/**
 * Description: james3邮件Id
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月6日.1	zhulh		2015年8月6日		Create
 * </pre>
 * @date 2015年8月6日
 */
public class James3MailId {
    private Long mailboxId;
    private Long mailUid;

    /**
     * @param mailboxId
     * @param mailUid
     */
    public James3MailId(Long mailboxId, Long mailUid) {
        super();
        this.mailboxId = mailboxId;
        this.mailUid = mailUid;
    }

    /**
     * @return the mailboxId
     */
    public Long getMailboxId() {
        return mailboxId;
    }

    /**
     * @param mailboxId 要设置的mailboxId
     */
    public void setMailboxId(Long mailboxId) {
        this.mailboxId = mailboxId;
    }

    /**
     * @return the mailUid
     */
    public Long getMailUid() {
        return mailUid;
    }

    /**
     * @param mailUid 要设置的mailUid
     */
    public void setMailUid(Long mailUid) {
        this.mailUid = mailUid;
    }

}

/*
 * @(#)2015年8月6日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: james3邮件异步
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
@Entity
@Table(name = "JAMES_MAIL_ASYNC")
@DynamicUpdate
@DynamicInsert
public class James3MailAsync extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3440180678305310508L;

    // mailboxId
    private Long mailboxId;
    // mailUid
    private Long mailUid;

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

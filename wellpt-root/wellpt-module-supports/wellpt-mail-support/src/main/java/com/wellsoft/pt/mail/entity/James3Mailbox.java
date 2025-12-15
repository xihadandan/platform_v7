/*
 * @(#)2015-08-06 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: James3邮箱
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-08-06.1	zhulh		2015-08-06		Create
 * </pre>
 * @date 2015-08-06
 */
@Entity
@CommonEntity
@Table(name = "JAMES_MAILBOX")
@DynamicUpdate
@DynamicInsert
public class James3Mailbox extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1438858341492L;

    // mailboxId
    private Integer mailboxId;
    // mailboxHighestModseq
    private Integer mailboxHighestModseq;
    // mailboxLastUid
    private Integer mailboxLastUid;
    // mailboxName
    private String mailboxName;
    // mailboxNamespace
    private String mailboxNamespace;
    // mailboxUidValidity
    private Integer mailboxUidValidity;
    // userName
    private String userName;

    /**
     * @return the mailboxId
     */
    public Integer getMailboxId() {
        return this.mailboxId;
    }

    /**
     * @param mailboxId
     */
    public Integer setMailboxId(Integer mailboxId) {
        return this.mailboxId = mailboxId;
    }

    /**
     * @return the mailboxHighestModseq
     */
    public Integer getMailboxHighestModseq() {
        return this.mailboxHighestModseq;
    }

    /**
     * @param mailboxHighestModseq
     */
    public Integer setMailboxHighestModseq(Integer mailboxHighestModseq) {
        return this.mailboxHighestModseq = mailboxHighestModseq;
    }

    /**
     * @return the mailboxLastUid
     */
    public Integer getMailboxLastUid() {
        return this.mailboxLastUid;
    }

    /**
     * @param mailboxLastUid
     */
    public Integer setMailboxLastUid(Integer mailboxLastUid) {
        return this.mailboxLastUid = mailboxLastUid;
    }

    /**
     * @return the mailboxName
     */
    public String getMailboxName() {
        return this.mailboxName;
    }

    /**
     * @param mailboxName
     */
    public String setMailboxName(String mailboxName) {
        return this.mailboxName = mailboxName;
    }

    /**
     * @return the mailboxNamespace
     */
    public String getMailboxNamespace() {
        return this.mailboxNamespace;
    }

    /**
     * @param mailboxNamespace
     */
    public String setMailboxNamespace(String mailboxNamespace) {
        return this.mailboxNamespace = mailboxNamespace;
    }

    /**
     * @return the mailboxUidValidity
     */
    public Integer getMailboxUidValidity() {
        return this.mailboxUidValidity;
    }

    /**
     * @param mailboxUidValidity
     */
    public Integer setMailboxUidValidity(Integer mailboxUidValidity) {
        return this.mailboxUidValidity = mailboxUidValidity;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * @param userName
     */
    public String setUserName(String userName) {
        return this.userName = userName;
    }

}

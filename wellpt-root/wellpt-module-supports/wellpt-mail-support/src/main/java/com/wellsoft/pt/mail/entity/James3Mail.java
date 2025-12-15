/*
 * @(#)2015-08-06 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.Date;

/**
 * Description: james3邮件
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
@Table(name = "JAMES_MAIL")
@DynamicUpdate
@DynamicInsert
public class James3Mail extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1438858325718L;

    // mailboxId
    private Integer mailboxId;
    // mailUid
    private Integer mailUid;
    // mailIsAnswered
    private Integer mailIsAnswered;
    // mailBodyStartOctet
    private Integer mailBodyStartOctet;
    // mailContentOctetsCount
    private Integer mailContentOctetsCount;
    // mailIsDeleted
    private Integer mailIsDeleted;
    // mailIsDraft
    private Integer mailIsDraft;
    // mailIsFlagged
    private Integer mailIsFlagged;
    // mailDate
    private Date mailDate;
    // mailMimeType
    private String mailMimeType;
    // mailModseq
    private Integer mailModseq;
    // mailIsRecent
    private Integer mailIsRecent;
    // mailIsSeen
    private Integer mailIsSeen;
    // mailMimeSubtype
    private String mailMimeSubtype;
    // mailTextualLineCount
    private Integer mailTextualLineCount;
    // mailBytes
    private Blob mailBytes;
    // headerBytes
    private Blob headerBytes;

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
     * @return the mailUid
     */
    public Integer getMailUid() {
        return this.mailUid;
    }

    /**
     * @param mailUid
     */
    public Integer setMailUid(Integer mailUid) {
        return this.mailUid = mailUid;
    }

    /**
     * @return the mailIsAnswered
     */
    public Integer getMailIsAnswered() {
        return this.mailIsAnswered;
    }

    /**
     * @param mailIsAnswered
     */
    public Integer setMailIsAnswered(Integer mailIsAnswered) {
        return this.mailIsAnswered = mailIsAnswered;
    }

    /**
     * @return the mailBodyStartOctet
     */
    public Integer getMailBodyStartOctet() {
        return this.mailBodyStartOctet;
    }

    /**
     * @param mailBodyStartOctet
     */
    public Integer setMailBodyStartOctet(Integer mailBodyStartOctet) {
        return this.mailBodyStartOctet = mailBodyStartOctet;
    }

    /**
     * @return the mailContentOctetsCount
     */
    public Integer getMailContentOctetsCount() {
        return this.mailContentOctetsCount;
    }

    /**
     * @param mailContentOctetsCount
     */
    public Integer setMailContentOctetsCount(Integer mailContentOctetsCount) {
        return this.mailContentOctetsCount = mailContentOctetsCount;
    }

    /**
     * @return the mailIsDeleted
     */
    public Integer getMailIsDeleted() {
        return this.mailIsDeleted;
    }

    /**
     * @param mailIsDeleted
     */
    public Integer setMailIsDeleted(Integer mailIsDeleted) {
        return this.mailIsDeleted = mailIsDeleted;
    }

    /**
     * @return the mailIsDraft
     */
    public Integer getMailIsDraft() {
        return this.mailIsDraft;
    }

    /**
     * @param mailIsDraft
     */
    public Integer setMailIsDraft(Integer mailIsDraft) {
        return this.mailIsDraft = mailIsDraft;
    }

    /**
     * @return the mailIsFlagged
     */
    public Integer getMailIsFlagged() {
        return this.mailIsFlagged;
    }

    /**
     * @param mailIsFlagged
     */
    public Integer setMailIsFlagged(Integer mailIsFlagged) {
        return this.mailIsFlagged = mailIsFlagged;
    }

    /**
     * @return the mailDate
     */
    public Date getMailDate() {
        return this.mailDate;
    }

    /**
     * @param mailDate
     */
    public Date setMailDate(Date mailDate) {
        return this.mailDate = mailDate;
    }

    /**
     * @return the mailMimeType
     */
    public String getMailMimeType() {
        return this.mailMimeType;
    }

    /**
     * @param mailMimeType
     */
    public String setMailMimeType(String mailMimeType) {
        return this.mailMimeType = mailMimeType;
    }

    /**
     * @return the mailModseq
     */
    public Integer getMailModseq() {
        return this.mailModseq;
    }

    /**
     * @param mailModseq
     */
    public Integer setMailModseq(Integer mailModseq) {
        return this.mailModseq = mailModseq;
    }

    /**
     * @return the mailIsRecent
     */
    public Integer getMailIsRecent() {
        return this.mailIsRecent;
    }

    /**
     * @param mailIsRecent
     */
    public Integer setMailIsRecent(Integer mailIsRecent) {
        return this.mailIsRecent = mailIsRecent;
    }

    /**
     * @return the mailIsSeen
     */
    public Integer getMailIsSeen() {
        return this.mailIsSeen;
    }

    /**
     * @param mailIsSeen
     */
    public Integer setMailIsSeen(Integer mailIsSeen) {
        return this.mailIsSeen = mailIsSeen;
    }

    /**
     * @return the mailMimeSubtype
     */
    public String getMailMimeSubtype() {
        return this.mailMimeSubtype;
    }

    /**
     * @param mailMimeSubtype
     */
    public String setMailMimeSubtype(String mailMimeSubtype) {
        return this.mailMimeSubtype = mailMimeSubtype;
    }

    /**
     * @return the mailTextualLineCount
     */
    public Integer getMailTextualLineCount() {
        return this.mailTextualLineCount;
    }

    /**
     * @param mailTextualLineCount
     */
    public Integer setMailTextualLineCount(Integer mailTextualLineCount) {
        return this.mailTextualLineCount = mailTextualLineCount;
    }

    /**
     * @return the mailBytes
     */
    public Blob getMailBytes() {
        return this.mailBytes;
    }

    /**
     * @param mailBytes
     */
    public Blob setMailBytes(Blob mailBytes) {
        return this.mailBytes = mailBytes;
    }

    /**
     * @return the headerBytes
     */
    public Blob getHeaderBytes() {
        return this.headerBytes;
    }

    /**
     * @param headerBytes
     */
    public Blob setHeaderBytes(Blob headerBytes) {
        return this.headerBytes = headerBytes;
    }

}

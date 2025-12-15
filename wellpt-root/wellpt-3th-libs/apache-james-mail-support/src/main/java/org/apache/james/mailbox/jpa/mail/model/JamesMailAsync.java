package org.apache.james.mailbox.jpa.mail.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 邮件同步记录
 *
 * @author chenq
 */
@Entity(name = "JamesMailAsync")
@Table(name = "JAMES_MAIL_ASYNC", uniqueConstraints = {@UniqueConstraint(columnNames = {"MAILBOX_ID", "MAIL_UID"})})
public class JamesMailAsync implements Serializable {

    private static final long serialVersionUID = 3373165107366808414L;


    @Id
    @Column(name = "UUID")
    private String uuid;

    @Column(name = "MAILBOX_ID", nullable = false)
    private String mailboxId;

    @Column(name = "MAIL_UID", nullable = false)
    private String mailUid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMailboxId() {
        return mailboxId;
    }

    public void setMailboxId(String mailboxId) {
        this.mailboxId = mailboxId;
    }

    public String getMailUid() {
        return mailUid;
    }

    public void setMailUid(String mailUid) {
        this.mailUid = mailUid;
    }

    public JamesMailAsync() {
    }
}

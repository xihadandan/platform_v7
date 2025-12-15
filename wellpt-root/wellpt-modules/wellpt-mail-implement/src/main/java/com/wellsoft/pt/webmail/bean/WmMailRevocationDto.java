/*
 * @(#)2018年3月15日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import java.io.Serializable;

/**
 * Description: 邮件撤回dto
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月15日.1	chenqiong		2018年3月15日		Create
 * </pre>
 * @date 2018年3月15日
 */
public class WmMailRevocationDto implements Serializable {

    private static final long serialVersionUID = -5619683742300133757L;


    private String toMailAddress;

    private String mailboxUuid;

    private Boolean isRevokeSuccess;// 是否撤回成功

    public WmMailRevocationDto() {
        super();
    }

    public WmMailRevocationDto(String toMailAddress, String mailboxUuid,
                               Boolean isRevokeSuccess) {
        this.toMailAddress = toMailAddress;
        this.mailboxUuid = mailboxUuid;
        this.isRevokeSuccess = isRevokeSuccess;
    }

    public String getToMailAddress() {
        return toMailAddress;
    }

    public void setToMailAddress(String toMailAddress) {
        this.toMailAddress = toMailAddress;
    }

    public String getMailboxUuid() {
        return mailboxUuid;
    }

    public void setMailboxUuid(String mailboxUuid) {
        this.mailboxUuid = mailboxUuid;
    }

    public Boolean getRevokeSuccess() {
        return isRevokeSuccess;
    }

    public void setRevokeSuccess(Boolean revokeSuccess) {
        isRevokeSuccess = revokeSuccess;
    }
}

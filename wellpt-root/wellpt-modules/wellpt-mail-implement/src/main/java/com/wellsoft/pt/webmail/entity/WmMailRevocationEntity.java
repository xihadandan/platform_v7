/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 邮件撤回记录
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Entity
@Table(name = "WM_MAIL_REVOCATION")
@DynamicUpdate
@DynamicInsert
public class WmMailRevocationEntity extends IdEntity {

    private static final long serialVersionUID = -3978664196227788920L;

    /**
     * 系统单位Id
     */
    private String systemUnitId;

    /**
     * 接收人邮件地址
     */
    private String toMailAddress;

    /**
     * 是否撤回成功
     */
    private Boolean isRevokeSuccess;

    /**
     * 邮件uuid
     */
    private String mailboxUuid;

    /**
     * 获取 系统单位Id
     *
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * 设置 系统单位Id
     *
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }


    /**
     * 获取 接收人邮件地址
     *
     * @return the toMailAddress
     */
    public String getToMailAddress() {
        return toMailAddress;
    }

    /**
     * 设置 接收人邮件地址
     *
     * @param toMailAddress 要设置的toMailAddress
     */
    public void setToMailAddress(String toMailAddress) {
        this.toMailAddress = toMailAddress;
    }


    /**
     * 获取 是否撤回成功
     *
     * @return the isRevokeSuccess
     */
    public Boolean getIsRevokeSuccess() {
        return isRevokeSuccess;
    }

    /**
     * 设置 是否撤回成功
     *
     * @param isRevokeSuccess 要设置的isRevokeSuccess
     */
    public void setIsRevokeSuccess(Boolean isRevokeSuccess) {
        this.isRevokeSuccess = isRevokeSuccess;
    }

    /**
     * 获取 邮件uuid
     *
     * @return
     */
    public String getMailboxUuid() {
        return mailboxUuid;
    }

    /**
     * 设置 邮件uuid
     *
     * @param mailboxUuid
     */
    public void setMailboxUuid(String mailboxUuid) {
        this.mailboxUuid = mailboxUuid;
    }
}

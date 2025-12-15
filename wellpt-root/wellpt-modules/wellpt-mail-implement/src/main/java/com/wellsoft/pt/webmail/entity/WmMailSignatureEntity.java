/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import java.sql.Clob;

/**
 * Description: 邮件签名
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
@Table(name = "WM_MAIL_SIGNATURE")
@DynamicUpdate
@DynamicInsert
public class WmMailSignatureEntity extends IdEntity {

    private static final long serialVersionUID = -496795034127995371L;

    /**
     * 是否默认
     */
    private Boolean isDefault;

    /**
     * 签名名称
     */
    private String signatureName;

    /**
     * 签名内容
     */
    private Clob signatureContent;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 系统单位Id
     */
    private String systemUnitId;

    /**
     * 获取 是否默认
     *
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * 设置 是否默认
     *
     * @param isDefault 要设置的isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * 获取 签名名称
     *
     * @return the signatureName
     */
    public String getSignatureName() {
        return signatureName;
    }

    /**
     * 设置 签名名称
     *
     * @param signatureName 要设置的signatureName
     */
    public void setSignatureName(String signatureName) {
        this.signatureName = signatureName;
    }

    /**
     * 获取 签名内容
     *
     * @return the signatureContent
     */
    @Basic(fetch = FetchType.LAZY)
    public Clob getSignatureContent() {
        return signatureContent;
    }

    /**
     * 设置 签名内容
     *
     * @param signatureContent 要设置的signatureContent
     */
    public void setSignatureContent(Clob signatureContent) {
        this.signatureContent = signatureContent;
    }

    /**
     * 获取 用户Id
     *
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置 用户Id
     *
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

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

}

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
 * Description: 写信模板
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
@Table(name = "WM_MAIL_TEMPLATE")
@DynamicUpdate
@DynamicInsert
public class WmMailTemplateEntity extends IdEntity {

    private static final long serialVersionUID = 7058864415834747793L;

    private Boolean isDefault;

    private String templateName;

    private Clob templateContent;

    private String userId;

    private String systemUnitId;

    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault 要设置的isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @param templateName 要设置的templateName
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * @return the templateContent
     */
    @Basic(fetch = FetchType.LAZY)
    public Clob getTemplateContent() {
        return templateContent;
    }

    /**
     * @param templateContent 要设置的templateContent
     */
    public void setTemplateContent(Clob templateContent) {
        this.templateContent = templateContent;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

}

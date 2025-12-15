/*
 * @(#)2018年3月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import java.io.Serializable;

/**
 * Description: 写信模板dto
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月12日.1	chenqiong		2018年3月12日		Create
 * </pre>
 * @date 2018年3月12日
 */
public class WmMailTemplateDto implements Serializable {

    private static final long serialVersionUID = 8264497481009223692L;

    private String userId;

    private String uuid;

    private String systemUnitId;

    private String templateName;

    private String templateContent;

    private String contentRendered;// 渲染后的值

    private Boolean isDefault;

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
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
    public String getTemplateContent() {
        return templateContent;
    }

    /**
     * @param templateContent 要设置的templateContent
     */
    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

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
     * @return the contentRendered
     */
    public String getContentRendered() {
        return contentRendered;
    }

    /**
     * @param contentRendered 要设置的contentRendered
     */
    public void setContentRendered(String contentRendered) {
        this.contentRendered = contentRendered;
    }

}

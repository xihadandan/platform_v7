/*
 * @(#)2018年3月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import java.io.Serializable;

/**
 * Description: 邮件签名dto
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月9日.1	chenqiong		2018年3月9日		Create
 * </pre>
 * @date 2018年3月9日
 */
public class WmMailSignatureDto implements Serializable {

    private static final long serialVersionUID = 1769834045363328808L;

    private Boolean isDefault;

    private String signatureName;

    private String signatureContent;

    private String uuid;

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
     * @return the signatureName
     */
    public String getSignatureName() {
        return signatureName;
    }

    /**
     * @param signatureName 要设置的signatureName
     */
    public void setSignatureName(String signatureName) {
        this.signatureName = signatureName;
    }

    /**
     * @return the signatureContent
     */
    public String getSignatureContent() {
        return signatureContent;
    }

    /**
     * @param signatureContent 要设置的signatureContent
     */
    public void setSignatureContent(String signatureContent) {
        this.signatureContent = signatureContent;
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

}

/*
 * @(#)10/14/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/14/22.1	zhulh		10/14/22		Create
 * </pre>
 * @date 10/14/22
 */
public class ItemMaterialQueryItem implements BaseQueryItem {
    private static final long serialVersionUID = 4438191748241123238L;

    private String materialName;

    private String materialCode;

    private String materialCodeField;

    private String materialRequired;

    /**
     * @return the materialName
     */
    public String getMaterialName() {
        return materialName;
    }

    /**
     * @param materialName 要设置的materialName
     */
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    /**
     * @return the materialCode
     */
    public String getMaterialCode() {
        return materialCode;
    }

    /**
     * @param materialCode 要设置的materialCode
     */
    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    /**
     * @return the materialCodeField
     */
    public String getMaterialCodeField() {
        return materialCodeField;
    }

    /**
     * @param materialCodeField 要设置的materialCodeField
     */
    public void setMaterialCodeField(String materialCodeField) {
        this.materialCodeField = materialCodeField;
    }

    /**
     * @return the materialRequired
     */
    public String getMaterialRequired() {
        return materialRequired;
    }

    /**
     * @param materialRequired 要设置的materialRequired
     */
    public void setMaterialRequired(String materialRequired) {
        this.materialRequired = materialRequired;
    }
}

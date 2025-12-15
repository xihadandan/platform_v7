/*
 * @(#)2018年9月5日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.bean;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author {zhongwd}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月5日.1	{zhongwd}		2018年9月5日		Create
 * </pre>
 * @date 2018年9月5日
 */
public class DmsDataImportFiledDefaultsBean implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7815544685078589465L;
    //默认类型ID
    private String defaultTypeId;
    //默认类型名称
    private String defaultTypeText;
    //默认值
    private String defaultValue;

    /**
     * @return the defaultTypeId
     */
    public String getDefaultTypeId() {
        return defaultTypeId;
    }

    /**
     * @param defaultTypeId 要设置的defaultTypeId
     */
    public void setDefaultTypeId(String defaultTypeId) {
        this.defaultTypeId = defaultTypeId;
    }

    /**
     * @return the defaultTypeText
     */
    public String getDefaultTypeText() {
        return defaultTypeText;
    }

    /**
     * @param defaultTypeText 要设置的defaultTypeText
     */
    public void setDefaultTypeText(String defaultTypeText) {
        this.defaultTypeText = defaultTypeText;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue 要设置的defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}

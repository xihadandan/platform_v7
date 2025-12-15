/*
 * @(#)2016年8月7日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月7日.1	zhulh		2016年8月7日		Create
 * </pre>
 * @date 2016年8月7日
 */
public class IexportDataDifferenceDetail {
    // 字段名
    private String fieldName;
    // 上传的数据
    private String controlValue;
    // 数据库的数据
    private String testValue;
    // 是否不一样
    private Boolean isDifference;

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName 要设置的fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the controlValue
     */
    public String getControlValue() {
        return controlValue;
    }

    /**
     * @param controlValue 要设置的controlValue
     */
    public void setControlValue(String controlValue) {
        this.controlValue = controlValue;
    }

    /**
     * @return the testValue
     */
    public String getTestValue() {
        return testValue;
    }

    /**
     * @param testValue 要设置的testValue
     */
    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }

    /**
     * @return the isDifference
     */
    public Boolean getIsDifference() {
        if (isDifference == null) {
            isDifference = !StringUtils.equals(this.controlValue, this.testValue);
        }
        return isDifference;
    }

    /**
     * @param isDifference 要设置的isDifference
     */
    public void setIsDifference(Boolean isDifference) {
        this.isDifference = isDifference;
    }

}

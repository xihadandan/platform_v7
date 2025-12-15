/*
 * @(#)2019年4月10日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.enums.Separator;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月10日.1	zhulh		2019年4月10日		Create
 * </pre>
 * @date 2019年4月10日
 */
public class IexportDataIdentifier {

    private String propertyColumnName;

    private String propertyColumnValue;

    /**
     *
     */
    public IexportDataIdentifier() {
        super();
    }

    /**
     * @param propertyColumnName
     * @param propertyColumnValue
     */
    public IexportDataIdentifier(String propertyColumnName, String propertyColumnValue) {
        super();
        this.propertyColumnName = propertyColumnName;
        this.propertyColumnValue = propertyColumnValue;
    }

    /**
     * @return the propertyColumnName
     */
    public String getPropertyColumnName() {
        return propertyColumnName;
    }

    /**
     * @param propertyColumnName 要设置的propertyColumnName
     */
    public void setPropertyColumnName(String propertyColumnName) {
        this.propertyColumnName = propertyColumnName;
    }

    /**
     * @return the propertyColumnValue
     */
    public String getPropertyColumnValue() {
        return propertyColumnValue;
    }

    /**
     * @param propertyColumnValue 要设置的propertyColumnValue
     */
    public void setPropertyColumnValue(String propertyColumnValue) {
        this.propertyColumnValue = propertyColumnValue;
    }

    /**
     * @return the compositeKey
     */
    public boolean isCompositeKey() {
        return StringUtils.contains(propertyColumnName, Separator.SEMICOLON.getValue());
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getPropertyColumnNameValueMap() {
        Map<String, String> nameValueMap = new CaseInsensitiveMap();
        String[] names = StringUtils.split(propertyColumnName, Separator.SEMICOLON.getValue());
        String[] values = StringUtils.split(propertyColumnValue, Separator.SEMICOLON.getValue());
        for (int i = 0; i < names.length; i++) {
            nameValueMap.put(names[i], values[i]);
        }
        return nameValueMap;
    }

}

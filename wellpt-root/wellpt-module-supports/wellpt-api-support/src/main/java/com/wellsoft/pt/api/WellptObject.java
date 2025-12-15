/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class WellptObject implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1042607207344400790L;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getClass().getName());
        result.append("{");

        BeanWrapper beanWrapper = new BeanWrapperImpl(this);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            if ("class".equals(propertyName)) {
                continue;
            }
            if (!beanWrapper.isReadableProperty(propertyName)) {
                continue;
            }
            result.append(propertyName);
            result.append("=");
            Object propertyValue = beanWrapper.getPropertyValue(propertyName);
            if (propertyValue instanceof String) {
                propertyValue = "\"" + propertyValue + "\"";
            } else if (propertyValue instanceof Date) {
                propertyValue = propertyValue.toString();
            }
            result.append(propertyValue);
            result.append(", ");
        }

        if (propertyDescriptors.length > 0) {
            result.setLength(result.length() - 2);
        }

        result.append("}");
        return result.toString();
    }
}

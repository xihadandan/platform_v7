/*
 * @(#)PrimitiveTypeUtils.java 2012-10-17 1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyValue;

import java.beans.PropertyDescriptor;

/**
 * Description: 基本数据类型包装类初始化工具类
 *
 * @author zhulh
 * @date 2012-10-17
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-17.1	zhulh		2012-10-17		Create
 * </pre>
 */
public class PrimitiveTypeWrapperUtils {

    /**
     * 初始化对象的基本类型包装类字段的初始值，若已经有值则忽略
     *
     * @param object
     * @return
     */
    public static Object init(final Object object) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(object);
        PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            Class<?> cls = descriptor.getPropertyType();
            String attributeName = descriptor.getName();
            if (wrapper.getPropertyValue(attributeName) == null) {
                if (cls.isAssignableFrom(Boolean.class)) {
                    wrapper.setPropertyValue(new PropertyValue(attributeName, Boolean.valueOf(false)));
                } else if (cls.isAssignableFrom(Character.class)) {
                    wrapper.setPropertyValue(new PropertyValue(attributeName, Character.valueOf('0')));
                } else if (cls.isAssignableFrom(Byte.class)) {
                    wrapper.setPropertyValue(new PropertyValue(attributeName, Byte.valueOf((byte) 0)));
                } else if (cls.isAssignableFrom(Short.class)) {
                    wrapper.setPropertyValue(new PropertyValue(attributeName, Short.valueOf((short) 0)));
                } else if (cls.isAssignableFrom(Integer.class)) {
                    wrapper.setPropertyValue(new PropertyValue(attributeName, Integer.valueOf(0)));
                } else if (cls.isAssignableFrom(Long.class)) {
                    wrapper.setPropertyValue(new PropertyValue(attributeName, Long.valueOf(0l)));
                } else if (cls.isAssignableFrom(Float.class)) {
                    wrapper.setPropertyValue(new PropertyValue(attributeName, Float.valueOf(0)));
                } else if (cls.isAssignableFrom(Double.class)) {
                    wrapper.setPropertyValue(new PropertyValue(attributeName, Double.valueOf(0)));
                }
            }
        }
        return object;
    }
}

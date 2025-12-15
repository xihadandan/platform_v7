/*
 * @(#)2020年2月14日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.enumtool;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年2月14日.1	zhulh		2020年2月14日		Create
 * </pre>
 * @date 2020年2月14日
 */
public class EnumClassUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(EnumClassUtils.class);

    /**
     *
     */
    private EnumClassUtils() {
    }

    /**
     * 获取枚举类属性
     *
     * @param className
     * @return
     */
    public static List<String> getPropertyNames(String className) {
        if (StringUtils.isBlank(className)) {
            return Collections.emptyList();
        }
        return getPropertyNames(getEnumClass(className));

    }

    /**
     * 如何描述该方法
     *
     * @param className
     * @return
     */
    private static Class<?> getEnumClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取枚举类属性
     *
     * @param enumClass
     * @return
     */
    public static List<String> getPropertyNames(Class<?> enumClass) {
        List<String> propertyNames = Lists.newArrayList();
        try {
            Object[] enumObjects = (Object[]) enumClass.getMethod("values").invoke(null);
            if (ArrayUtils.isNotEmpty(enumObjects)) {
                Object object = enumObjects[0];
                BeanWrapper beanWrapper = new BeanWrapperImpl(object);
                PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String propertyName = propertyDescriptor.getName();
                    if (StringUtils.equals("class", propertyName) || StringUtils.equals("declaringClass", propertyName)) {
                        continue;
                    }

                    propertyNames.add(propertyName);
                }
            }
            // 枚举名
            if (CollectionUtils.isEmpty(propertyNames)) {
                propertyNames.add("name");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return propertyNames;
    }

    /**
     * @param className
     * @param valueField
     * @param value
     * @param textField
     * @return
     */
    public static Object getTextFieldValueWithValueField(String className, String valueField, Object value, String textField) {
        if (StringUtils.isBlank(className) || StringUtils.isBlank(valueField) || StringUtils.isBlank(textField)) {
            return null;
        }
        try {
            Object[] enumObjects = getEnumObjects(getEnumClass(className));
            for (Object object : enumObjects) {
                BeanWrapper beanWrapper = new BeanWrapperImpl(object);
                Object propertyValue = beanWrapper.getPropertyValue(valueField);
                if (propertyValue == value
                        || StringUtils.equals(ObjectUtils.toString(propertyValue), ObjectUtils.toString(value))) {
                    return beanWrapper.getPropertyValue(textField);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param enumClass
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object[] getEnumObjects(Class<?> enumClass) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        return (Object[]) enumClass.getMethod("values").invoke(null);
    }

}

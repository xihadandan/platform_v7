/*
 * @(#)Feb 21, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.proxy;

import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dms.core.convert.ConversionService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 21, 2017.1	zhulh		Feb 21, 2017		Create
 * </pre>
 * @date Feb 21, 2017
 */
public class DyFormDataProxy implements MethodInterceptor {

    private static String METHOD_GET_DATA_UUID = "getDataUuid";

    // 表单内置字段
    private static Set<String> SYSTEM_FIELD_SET = Sets.newHashSet();

    static {
        EnumSystemField[] values = EnumSystemField.values();
        for (EnumSystemField enumSystemField : values) {
            SYSTEM_FIELD_SET.add(enumSystemField.getColumn());
        }
    }

    private DyFormData dyFormData;
    private ImprovedNamingStrategy improvedNamingStrategy = new ImprovedNamingStrategy();

    public DyFormDataProxy(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
    }

    /**
     * @return the dyFormData
     */
    public DyFormData getDyFormData() {
        return dyFormData;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cglib.proxy.MethodInterceptor#intercept(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], org.springframework.cglib.proxy.MethodProxy)
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (isGetter(obj, method)) {
            // 1、调用字段GET方法
            String fieldName = getFieldName(method);
            if (this.dyFormData.isFieldExist(fieldName)) {
                Object retVal = this.dyFormData.getFieldValue(fieldName);
                Class<?> retType = method.getReturnType();
                // 返回对象类型兼容性转化
                ConversionService conversionService = ApplicationContextHolder.getBean(ConversionService.class);
                if (conversionService.canConvert(retVal != null ? TypeDescriptor.valueOf(retVal.getClass()) : null,
                        TypeDescriptor.valueOf(retType))) {
                    retVal = conversionService.convert(retVal, retType);
                }
                return retVal;
            }
            // 2、调用dyFormData类方法
            Method dyformDataGetMethod = null;
            if (IdEntity.UUID.equals(fieldName)) {
                dyformDataGetMethod = this.dyFormData.getClass().getMethod(METHOD_GET_DATA_UUID,
                        method.getParameterTypes());
            } else if (SYSTEM_FIELD_SET.contains(fieldName)) {
                Map<String, Object> formDataOfMainform = this.dyFormData.getFormDataOfMainform();
                return formDataOfMainform != null ? formDataOfMainform.get(fieldName) : null;
            } else {
                dyformDataGetMethod = this.dyFormData.getClass()
                        .getMethod(method.getName(), method.getParameterTypes());
            }
            return dyformDataGetMethod.invoke(this.dyFormData, args);
        } else if (isSetter(obj, method)) {
            String fieldName = getFieldName(method);
            if (SYSTEM_FIELD_SET.contains(fieldName)) {
                return null;
            }
            this.dyFormData.setFieldValue(fieldName, args[0]);
            return proxy.invokeSuper(obj, args);
        }
        return proxy.invokeSuper(obj, args);
    }

    /**
     * @param method
     * @return
     */
    private boolean isSetter(Object obj, Method method) {
        return method.getName().startsWith("set");
    }

    /**
     * @param method
     * @return
     */
    private boolean isGetter(Object obj, Method method) {
        return method.getName().startsWith("get");
    }

    /**
     * @param obj
     * @param method
     * @return
     */
    private String getFieldName(Method method) {
        String propertyName = method.getName().substring(3);
        return improvedNamingStrategy.propertyToColumnName(propertyName);
    }

}

/*
 * @(#)2013-6-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache;

import com.wellsoft.pt.security.util.TenantContextHolder;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-22.1	rzhu		2013-6-22		Create
 * </pre>
 * @date 2013-6-22
 */
public class CustomKeyGenerator implements KeyGenerator {
    public static final int NO_PARAM_KEY = 0;
    public static final int NULL_PARAM_KEY = 53;

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.interceptor.KeyGenerator#generate(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object generate(Object target, Method method, Object... params) {
        String prefix = TenantContextHolder.getTenantId() + ClassUtils.getUserClass(target.getClass()).getName()
                + method.getName();
        if (params.length == 1) {
            return (params[0] == null ? prefix + NULL_PARAM_KEY : prefix + params[0]);
        }
        if (params.length == 0) {
            return prefix + NO_PARAM_KEY;
        }
        int hashCode = 17;
        for (Object object : params) {
            hashCode = 31 * hashCode + (object == null ? NULL_PARAM_KEY : object.hashCode());
        }
        return prefix + Integer.valueOf(hashCode);
    }

}

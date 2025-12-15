/*
 * @(#)2017年10月12日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support.factory;

import com.wellsoft.context.util.encode.JsonBinder;
import com.wellsoft.pt.basicdata.mapper.DataFactory;
import com.wellsoft.pt.basicdata.mapper.support.RequestDataFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;

/**
 * Description:
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月12日.1	zhongzh		2017年10月12日		Create
 * </pre>
 * @date 2017年10月12日
 */
public class RequestParameterDataFactory extends RequestDataFactory implements DataFactory {

    /**
     * source为ServletRequest,getParameter(targetBeanId)返回指定参数,targetBeanId为空返回getParameterMap
     * <p>
     * (non-Javadoc)
     *
     * @see org.dozer.BeanFactory#createBean(java.lang.Object, java.lang.Class, java.lang.String)
     */
    @Override
    public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {
        super.createBean(source, sourceClass, targetBeanId);
        Object object = null;
        if (StringUtils.isBlank(targetBeanId)) {
            object = ((ServletRequest) source).getParameterMap();
        } else {
            object = ((ServletRequest) source).getParameter(targetBeanId);
        }
        return object;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.DataFactory#createData(java.lang.Object, java.lang.Class, java.lang.String)
     */
    @Override
    public Object createData(Object dest, Class<?> destClass, String targetDataId) {
        Object object = null;
        if (dest != null && dest.getClass().isAssignableFrom(destClass)) {
            object = dest;
        } else if (dest != null && dest instanceof String) {
            object = JsonBinder.buildNormalBinder().fromJson((String) dest, destClass);
        }
        return object;
    }
}

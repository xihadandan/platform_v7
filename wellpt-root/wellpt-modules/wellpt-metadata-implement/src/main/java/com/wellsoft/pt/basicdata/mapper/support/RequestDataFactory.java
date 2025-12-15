/*
 * @(#)2017年10月12日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support;

import com.wellsoft.pt.basicdata.mapper.DataFactory;
import com.wellsoft.pt.basicdata.mapper.MapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 如何描述该类
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
public abstract class RequestDataFactory implements DataFactory {

    protected static Logger logger = LoggerFactory.getLogger(RequestDataFactory.class);

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see org.dozer.BeanFactory#createBean(java.lang.Object, java.lang.Class, java.lang.String)
     */
    @Override
    public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {
        if (source == null) {
            throw new MapperException(getClass() + " source is not null");
        }
        return null;
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
        }
        return object;
    }
}

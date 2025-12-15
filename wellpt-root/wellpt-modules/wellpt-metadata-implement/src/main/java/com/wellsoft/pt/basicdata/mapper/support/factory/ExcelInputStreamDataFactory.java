/*
 * @(#)2017年10月12日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support.factory;

import com.wellsoft.pt.basicdata.mapper.DataFactory;

/**
 * Description: 获取Excel格式化数据
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
public class ExcelInputStreamDataFactory extends RequestInputStreamDataFactory implements DataFactory {

    /**
     * (non-Javadoc)
     *
     * @see org.dozer.BeanFactory#createBean(java.lang.Object, java.lang.Class, java.lang.String)
     */
    @Override
    public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {
        return super.createBean(source, sourceClass, targetBeanId);
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
        return super.createData(dest, destClass, targetDataId);
    }
}

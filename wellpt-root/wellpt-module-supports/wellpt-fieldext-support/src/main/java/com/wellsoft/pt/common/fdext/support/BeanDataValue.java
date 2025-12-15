/*
 * @(#)2016年3月20日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月20日.1	zhongzh		2016年3月20日		Create
 * </pre>
 * @date 2016年3月20日
 */
public class BeanDataValue implements ICdDataValue<ICdFieldValue> {

    private ICdFieldValue values;

    /**
     * 如何描述该构造方法
     */
    public BeanDataValue() {
    }

    /**
     * 如何描述该构造方法
     *
     * @param values
     */
    public BeanDataValue(ICdFieldValue values) {
        this.values = values;
    }

    @Override
    public Object getValue(String fieldName) {
        return values == null ? null : ValueFieldEnumAbs.valueOf(fieldName).getValue(values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdDataValue#getData()
     */
    @Override
    public ICdFieldValue getData() {
        return this.values;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdDataValue#setData(java.lang.Object)
     */
    @Override
    public void setData(ICdFieldValue data) {
        this.values = data;
    }

}

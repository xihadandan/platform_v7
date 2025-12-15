/*
 * @(#)2016年3月20日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import java.util.HashMap;
import java.util.Map;

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
public class MapDataValue implements ICdDataValue<Map<String, Object>> {

    private Map<String, Object> values;

    /**
     * 如何描述该构造方法
     */
    public MapDataValue() {
    }

    /**
     * 如何描述该构造方法
     *
     * @param values
     */
    public MapDataValue(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public Object getValue(String fieldName) {
        if (values == null) {
            values = new HashMap<String, Object>();
            values.put("fdext_003", "fdext_003");
        }
        return values.get(fieldName);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdDataValue#getData()
     */
    @Override
    public Map<String, Object> getData() {
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
    public void setData(Map<String, Object> data) {
        this.values = data;
    }

}

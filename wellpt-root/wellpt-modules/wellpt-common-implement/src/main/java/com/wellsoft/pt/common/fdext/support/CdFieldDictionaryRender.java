/*
 * @(#)2016年3月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import com.wellsoft.pt.basicdata.selective.facade.SelectiveDatas;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import com.wellsoft.pt.basicdata.selective.support.SelectiveData;

import java.util.Collection;
import java.util.Collections;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月17日.1	zhongzh		2016年3月17日		Create
 * </pre>
 * @date 2016年3月17日
 */
public class CdFieldDictionaryRender extends AbstractCdFieldRender {

    protected Collection<DataItem> dataItems;

    /**
     * 如何描述该构造方法
     *
     * @param define
     */
    protected CdFieldDictionaryRender(ICdFieldDefinition define) {
        super(define);
    }

    protected boolean contains(Object data, Object value) {
        if (data == null || value == null) {
            return false;
        }
        return ((String) data).contains((String) value);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.AbstractCdFieldRender#getValue(java.lang.String)
     */
    @Override
    public String getValue(Object data, Object value) {
        return contains(data, value) ? DEATULT_VALUE_CHECKED : "";
    }

    /**
     * 获取数据字典项,在freemarker（assign）中尽量将变量引用存起来，防止多次调用
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.AbstractCdFieldRender#getDataItems()
     */
    @SuppressWarnings("unchecked")
    final public Collection<DataItem> getDataItems() {
        SelectiveData selectData = SelectiveDatas.get(getCfgKey());
        if (selectData == null || selectData.getItems() == null) {
            return Collections.emptyList();
        }
        Object items = selectData.getItems();
        if (items != null && items instanceof Collection) {
            // Dict DataItem
            return (Collection<DataItem>) items;
        }
        return Collections.emptyList();
    }
}

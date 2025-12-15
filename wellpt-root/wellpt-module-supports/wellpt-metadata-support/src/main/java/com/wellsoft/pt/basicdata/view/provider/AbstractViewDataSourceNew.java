/*
 * @(#)2013-4-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.provider;

import java.util.Collection;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-18.1	wubin		2013-4-18		Create
 * </pre>
 * @date 2013-4-18
 */
public abstract class AbstractViewDataSourceNew implements ViewDataSourceNew {
    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#count(java.util.Collection, java.lang.String, java.util.Map)
     */
    @Override
    public Long count(Collection<ViewColumnNew> viewColumnNews, String whereHql, Map<String, Object> queryParams) {
        return -1l;
    }
}

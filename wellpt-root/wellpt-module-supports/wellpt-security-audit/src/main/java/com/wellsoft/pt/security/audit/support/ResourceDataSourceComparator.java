/*
 * @(#)2015-9-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.support;

import java.util.Comparator;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-24.1	zhulh		2015-9-24		Create
 * </pre>
 * @date 2015-9-24
 */
public class ResourceDataSourceComparator implements Comparator<ResourceDataSource> {

    /**
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(ResourceDataSource o1, ResourceDataSource o2) {
        return Integer.valueOf(o1.getOrder()).compareTo(o2.getOrder());
    }

}

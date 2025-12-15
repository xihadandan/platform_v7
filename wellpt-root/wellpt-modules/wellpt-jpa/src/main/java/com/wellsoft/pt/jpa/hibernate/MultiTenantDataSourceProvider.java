/*
 * @(#)2013-4-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import javax.sql.DataSource;

/**
 * Description: jq
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-19.1	zhulh		2013-4-19		Create
 * </pre>
 * @date 2013-4-19
 */
public interface MultiTenantDataSourceProvider {

    /**
     * 如何描述该方法
     *
     * @param tenantIdentifier
     */
    public DataSource removeDataSource(String tenantIdentifier);

    /**
     * 如何描述该方法
     *
     * @param tenantIdentifier
     */
    public DataSource getDataSource(String tenantIdentifier);

}

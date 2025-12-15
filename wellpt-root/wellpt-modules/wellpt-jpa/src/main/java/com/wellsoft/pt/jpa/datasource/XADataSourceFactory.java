/*
 * @(#)2013-12-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.datasource;

import com.wellsoft.pt.mt.entity.Tenant;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-4.1	zhulh		2013-12-4		Create
 * </pre>
 * @date 2013-12-4
 */
public class XADataSourceFactory {

    public static XADataSource getXADataSource(String name) {
        DatabaseType databaseType = Enum.valueOf(DatabaseType.class, name);
        XADataSource xaDataSource = null;
        switch (databaseType) {
            case SQLServer2008:
                xaDataSource = new SQLServer2008XADataSource();
                break;
            case Oracle11g:
                xaDataSource = new Oracle11gXADataSource();
                break;
            case Oracle10g:
                xaDataSource = new Oracle10gXADataSource();
                break;
            case MySQL5:
                xaDataSource = new MySQL5XADataSource();
                break;
            case Oracle12c:
                xaDataSource = new Oracle12cXADataSource();
                break;
            case DM:
                xaDataSource = new DamengXADataSource();
                break;
            case KB:
                xaDataSource = new KingbaseXADataSource();
                break;
            default:
                break;
        }
        return xaDataSource;
    }

    /**
     * @param tenant
     * @return
     */
    public static XADataSource build(Tenant tenant) {
        String dabaseType = tenant.getJdbcType();
        XADataSource xaDataSource = getXADataSource(dabaseType);
        return xaDataSource.buildInternal(tenant);
    }

}

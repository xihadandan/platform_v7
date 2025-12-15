/*
 * @(#)2013-5-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.provider;

import com.wellsoft.pt.jpa.enums.Database;
import com.wellsoft.pt.security.superadmin.provider.impl.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-29.1	zhulh		2013-5-29		Create
 * </pre>
 * @date 2013-5-29
 */
public class DatabaseConfigProviderFactory {

    public static DatabaseConfigProvider getConfigProvider(String databaseType) {
        Database database = Enum.valueOf(Database.class, databaseType);
        DatabaseConfigProvider databaseConfigProvider = null;
        switch (database) {
            case Oracle: // Oracle (any version)
                databaseConfigProvider = new OracleConfigProvider();
                break;
            case Oracle9i: // Oracle 9i
                databaseConfigProvider = new Oracle9iConfigProvider();
                break;
            case Oracle10g: // Oracle 10g
                databaseConfigProvider = new Oracle10gConfigProvider();
                break;
            case Oracle11g: // Oracle 11g
                databaseConfigProvider = new Oracle11gConfigProvider();
                break;
            case SQLServer2000: // Microsoft SQL Server 2000
                databaseConfigProvider = new SQLServer2000ConfigProvider();
                break;
            case SQLServer2005: // Microsoft SQL Server 2005
                databaseConfigProvider = new SQLServer2005ConfigProvider();
                break;
            case SQLServer2008: // Microsoft SQL Server 2008
                databaseConfigProvider = new SQLServer2008ConfigProvider();
                break;
            case H2: // H2 Database
                databaseConfigProvider = new H2ConfigProvider();
                break;
            default:
                break;
        }
        return databaseConfigProvider;
    }
}

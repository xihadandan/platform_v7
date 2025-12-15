/*
 * @(#)2012-10-22 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-22.1	lilin		2012-10-22		Create
 * </pre>
 * @date 2012-10-22
 */
@Component
public class ConnectionProviderBuilder {
    public static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    //	@Autowired
    //	private TenantService tenantService;
    public static final String URL = "jdbc:sqlserver://192.168.0.250:1433;databaseName=%s";
    public static final String USER = "oa_dev";
    public static final String PASS = "oa_dev";
    private static final String COMMON_DB = "oa_dev";

    public static Properties getConnectionProviderProperties(String tenantaccount) {
        Properties props = new Properties(null);
        //		Tenant tenant = tenantService.getByAccount(tenantaccount);
        //		props.put(Environment.DRIVER, tenant.getDriver());
        //		props.put(Environment.URL, String.format(URL, tenant.getUrl()));
        //		props.put(Environment.USER, tenant.getDbUser());
        //		props.put(Environment.PASS, tenant.getDbPass());

        props.put(Environment.DRIVER, DRIVER);
        props.put(Environment.URL, String.format(URL, tenantaccount));
        props.put(Environment.USER, USER);
        props.put(Environment.PASS, PASS);
        return props;
    }

    //	public Properties getCommonProviderProperties() {
    //		Properties props = new Properties(null);
    //		props.put(Environment.DRIVER, DRIVER);
    //		props.put(Environment.URL, URL);
    //		props.put(Environment.USER, USER);
    //		props.put(Environment.PASS, PASS);
    //		return props;
    //	}

    public static Properties getConnectionProviderProperties() {
        return getConnectionProviderProperties(COMMON_DB);
    }

    public static DriverManagerConnectionProviderImpl buildConnectionProvider() {
        return buildConnectionProvider(false);
    }

    public static DriverManagerConnectionProviderImpl buildConnectionProvider(String tenantaccount) {
        return buildConnectionProvider(getConnectionProviderProperties(tenantaccount), false);
    }

    public static DriverManagerConnectionProviderImpl buildConnectionProvider(final boolean allowAggressiveRelease) {
        return buildConnectionProvider(getConnectionProviderProperties(COMMON_DB), allowAggressiveRelease);
    }

    private static DriverManagerConnectionProviderImpl buildConnectionProvider(Properties props,
                                                                               final boolean allowAggressiveRelease) {
        DriverManagerConnectionProviderImpl connectionProvider = new DriverManagerConnectionProviderImpl() {
            public boolean supportsAggressiveRelease() {
                return allowAggressiveRelease;
            }
        };
        connectionProvider.configure(props);
        return connectionProvider;
    }

    public static Dialect getCorrespondingDialect() {
        return new SQLServer2008Dialect();
    }

    /**
     * 获取相应的hibernate dialet 默认返回sqlserver2008
     *
     * @param tenantaccount
     * @return
     */
    public Dialect getCorrespondingDialect(String tenantaccount) {
        //		Tenant tenant = tenantService.getByAccount(tenantaccount);
        //		try {
        //			Class dialet = Class.forName(tenant.getDialect());
        //			return (Dialect) dialet.newInstance();
        //		} catch (ClassNotFoundException e) {
        //			// TODO Auto-generated catch block
        //			e.printStackTrace();
        //		} catch (InstantiationException e) {
        //			// TODO Auto-generated catch block
        //			e.printStackTrace();
        //		} catch (IllegalAccessException e) {
        //			// TODO Auto-generated catch block
        //			e.printStackTrace();
        //		}
        return new SQLServer2008Dialect();
    }
}

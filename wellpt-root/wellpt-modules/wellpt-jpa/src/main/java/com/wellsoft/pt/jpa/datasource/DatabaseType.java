/*
 * @(#)2013-12-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.datasource;

import com.google.common.collect.Lists;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
public enum DatabaseType {

    // SQLServer2008
    SQLServer2008("SQLServer2008", "Microsoft SQL Server 2008"),

    // Oracle11g
    Oracle11g("Oracle11g", "Oracle 11g"),

    // Oracle 10g
    Oracle10g("Oracle10g", "Oracle 10g"),

    Oracle12c("Oracle12c", "Oracle 12c"),

    MySQL5("MySQL5", "MySQL 5.7.25 Community Server(GPL)"),

    DM("Dameng7", "Dameng7"),

    KB("Kingbase8", "Kingbase8");

    public static final List<String> SUPPORTS = Lists.newArrayList("mysql", "oracle", "dm", "kb");
    protected static final Map<String, String> databaseTypes = new LinkedHashMap<String, String>();

    static {
        databaseTypes.put(SQLServer2008.getName(), SQLServer2008.getValue());
        databaseTypes.put("SQLServer2005", "Microsoft SQL Server 2005");
        databaseTypes.put("SQLServer2000", "Microsoft SQL Server 2000");
        databaseTypes.put(Oracle11g.getName(), Oracle11g.getValue());
        databaseTypes.put(Oracle10g.getName(), Oracle10g.getValue());
        databaseTypes.put("Oracle9i", "Oracle 9i");
        databaseTypes.put("Oracle", "Oracle (any version)");
        databaseTypes.put("MySQL5", "MySQL5");
        databaseTypes.put("MySQL5InnoDB", "MySQL5 with InnoDB");
        databaseTypes.put("MySQLMyISAM", "MySQL with MyISAM");
        databaseTypes.put("DB2", "DB2");
        databaseTypes.put("DB2400", "DB2 AS/400");
        databaseTypes.put("PostgreSQL81", "PostgreSQL 8.1");
        databaseTypes.put("PostgreSQL82", "PostgreSQL 8.2 and later");
        databaseTypes.put("SybaseASE15", "Sybase ASE 15.5");
        databaseTypes.put("SybaseASE157", "Sybase ASE 15.7");
        databaseTypes.put("SybaseAnywhere", "Sybase Anywhere");
        databaseTypes.put("SAPDB", "SAP DB");
        databaseTypes.put("Informix", "Informix");
        databaseTypes.put("HSQL", "HypersonicSQL");
        databaseTypes.put("H2", "H2 Database");
        databaseTypes.put("Ingres", "Ingres");
        databaseTypes.put("Progress", "Progress");
        databaseTypes.put("Mckoi", "Mckoi SQL");
        databaseTypes.put("Interbase", "Interbase");
        databaseTypes.put("Pointbase", "Pointbase");
        databaseTypes.put("FrontBase", "Frontbase");
        databaseTypes.put("Firebird", "Firebird");
    }

    private String name;
    private String value;

    private DatabaseType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static Map<String, String> getDatabaseTypes() {
        return databaseTypes;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }
}

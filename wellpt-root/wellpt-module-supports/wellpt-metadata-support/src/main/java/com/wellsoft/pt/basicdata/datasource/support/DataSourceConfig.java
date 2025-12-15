/*
 * @(#)2014-8-15 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.support;

/**
 * Description: 数据源常量类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-15.1	wubin		2014-8-15		Create
 * </pre>
 * @date 2014-8-15
 */
public class DataSourceConfig {
    /******数据源类型******/
    //内部数据源
    public static final String DATA_SOURCE_TYPE_IN = "1";
    //外部数据源
    public static final String DATA_SOURCE_TYPE_OUT = "2";
    //数据接口
    public static final String DATA_SOURCE_TYPE_PROVIDER = "3";

    /******内部数据源的类别******/
    //数据库表
    public static final String IN_DATA_SCOPE_TABLE = "1";
    //实体类
    public static final String IN_DATA_SCOPE_ENTITY = "2";
    //数据视图
    public static final String IN_DATA_SCOPE_VIEW = "3";
    //SQL
    public static final String IN_DATA_SCOPE_SQL = "4";
    //HQL
    public static final String IN_DATA_SCOPE_HQL = "5";
    //带acl权限的hql
    public static final String IN_DATA_SCOPE_ACLHQL = "6";
    //带acl权限的实体类
    public static final String IN_DATA_SCOPE_ENTITYBYACL = "7";
    /******外部数据源的类别******/
    //数据库表
    public static final String OUT_DATA_SCOPE_TABLE = "1";
    //数据视图
    public static final String OUT_DATA_SCOPE_VIEW = "2";
    //sql
    public static final String OUT_DATA_SCOPE_SQL = "3";
    //作为输出列
    public static final String DATA_SOURCE_COLUMN_ISEXPORT = "true";
    //不作为输出列
    public static final String DATA_SOURCE_COLUMN_ISNOTEXPORT = "false";
}

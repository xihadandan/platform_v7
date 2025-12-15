/*
 * @(#)2014-7-31 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.bean.DataSourceDefinitionBean;
import com.wellsoft.pt.basicdata.view.entity.ColumnDefinitionNew;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;
import com.wellsoft.pt.security.acl.support.QueryInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 数据源的服务类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-31.1	wubin		2014-7-31		Create
 * </pre>
 * @date 2014-7-31
 */
public interface DataSourceDataService {

    //获得内部系统的所有数据表
    public List getDataBaseTable(String s, String id);

    //获得内部系统的所有数据视图
    public List getDataBaseView(String s, String id);

    //获得内部一张系统表的所有列
    public List getColumnsByTable(String tableName);

    //获得外部数据源的数据库信息
    public List getDataBaseInfoByOut(String s, String id, String sourceType);

    //根据sql获得外部数据源的信息
    public List<QueryItem> getDataByOut(String id, String sourceType, String sqlText, Map<String, Object> queryParams,
                                        PagingInfo pagingInfo);

    //获得外部一张系统表的所有列
    public List getColumnsByOutTable(String id, String tableName, String sourceType);

    //获得所有的数据接口(旧版)
    public List getDataSourceList(String s, String id);

    //获得所有的数据接口(新版)
    public List getSourceList(String s, String id);

    //获得一个数据接口的所有列
    public List getSourceColumns(String id);

    //获得数据接口的数据
    public List<QueryItem> getDataBySource(DataSourceDefinitionBean bean, String whereClause,
                                           Map<String, Object> queryParams, String orderBy, PagingInfo pagingInfo);

    //数据源解析器(解析不同类型的数据源，返回结果集)
    public List<QueryItem> dataSourceInterpreter(String dataSourceDefId);

    //执行sql获得数据
    public List<QueryItem> query(String sql, Map<String, Object> selectionArgs, int firstResult, int maxResults);

    //执行sql验证
    public List queryByValidate(String dataComeType, String dataComeId, String sql, Map<String, Object> selectionArgs);

    //执行sql查询表的总条数
    public Long queryTotal(String tableName, String whereSql, Map<String, Object> queryParams);

    //执行hql获得数据
    public List<QueryItem> queryByHql(String hql);

    public List<QueryItem> query(String dataSourceDefId, String whereClause, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo);

    public List<QueryItem> queryForAcl(String dataSourceDefId, QueryInfo aclQueryInfo, PagingInfo pagingInfo);

    public Long queryByTotal(String dataSourceDefId);

    public Long getTotalByOut(String id, String sourceType, String sqlText, Map<String, Object> queryParams,
                              PagingInfo pagingInfo);

    //查询获得带视图的条件的总数
    public Long queryTotalByView(String dataSourceDefId, String whereSql, Map<String, Object> queryParams);

    //查询获得带查询条件的总数
    public Long queryTotalBySelect(String dataSourceDefId, String whereSql, DyViewQueryInfoNew dyViewQueryInfoNew,
                                   Set<ColumnDefinitionNew> columnDefinitionNews);

    public Object[] custom(String dataSourceDefId, Object[] obj);
}

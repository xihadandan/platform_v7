/*
 * @(#)2014-8-15 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.provider;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
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
public interface DataSourceProvider {

    /**
     * 查询数据源的数据
     *
     * @param dataSourceColumns
     * @param whereSql
     * @param queryParams
     * @param orderBy
     * @param pagingInfo
     * @return
     */
    public List<QueryItem> query(Set<DataSourceColumn> dataSourceColumns, String whereSql,
                                 Map<String, Object> queryParams, String orderBy, PagingInfo pagingInfo);

    /**
     * 获取数据源的定义列
     *
     * @return
     */
    Collection<DataSourceColumn> getAllDataSourceColumns();

    /**
     * 获取模块ID
     *
     * @return
     */
    String getModuleId();

    /**
     * 获取模块的名字
     *
     * @return
     */
    String getModuleName();

    /**
     * 查询总条数
     *
     * @param viewColumns
     * @param whereHql
     * @param queryParams
     * @return
     */
    public Long count(Set<DataSourceColumn> dataSourceColumns, String whereHql, Map<String, Object> queryParams);

    /**
     * where查询条件
     *
     * @param dyViewQueryInfoNew
     * @return
     */
    public String getWhereSql(DyViewQueryInfoNew dyViewQueryInfoNew);

    public String getWhereSqlForAll(DyViewQueryInfoNew dyViewQueryInfoNew, String whereSql);

    public Object[] custom(Object[] obj);
}

/*
 * @(#)2013-4-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.provider;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 模块数据提供接口
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-17.1	wubin		2013-4-17		Create
 * </pre>
 * @date 2013-4-17
 */
public interface ViewDataSourceNew {

    /**
     * 查询数据
     *
     * @param viewColumnNews 要查询返回的列
     * @param whereHql       额外的查询语句
     * @param queryParams    查询参数
     * @param orderBy        排序，参name1 asc, name2 desc的形式传入
     * @param pageNo         第几页
     * @param pageSize       每页条数,pageSize=0时,不分页查全部用
     * @return
     */
    public List<QueryItem> query(Collection<ViewColumnNew> viewColumnNews, String whereHql, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo);

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
     * 查询总条数
     *
     * @param viewColumnNews
     * @param whereHql
     * @param queryParams
     * @return
     */
    public Long count(Collection<ViewColumnNew> viewColumnNews, String whereHql, Map<String, Object> queryParams);

    /**
     * 获取视图列，要求返回一个集合，里面存放列查询属性名、列查询别名、列显示名
     *
     * @return
     */
    Collection<ViewColumnNew> getAllViewColumns();

    /**
     * 获取数据源的定义列
     *
     * @return
     */
    Set<DataSourceColumn> getAllDataSourceColumns();

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

}

/*
 * @(#)2014-8-20 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.facade.service;


import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceDefinition;
import com.wellsoft.pt.basicdata.view.entity.ColumnDefinitionNew;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Description: 数据源的接口api
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-20.1	wubin		2014-8-20		Create
 * </pre>
 * @date 2014-8-20
 */

public interface DataSourceApiFacade {


    /**
     * 获得所有的数据源定义
     *
     * @return
     */
    public List<DataSourceDefinition> getAll();

    /**
     * 获得一个数据源的所有列,根据传入的数据源ID
     *
     * @param dataSourceId
     * @return
     */
    public List<DataSourceColumn> getDataSourceFieldsById(String dataSourceDefId);

    /**
     * 获得一个数据源的所有显示标题不为空的列,根据传入的数据源ID
     *
     * @param dataSourceDefId
     * @return
     */
    public List<DataSourceColumn> getDataSourceShowTitleNameFieldsById(String dataSourceDefId);

    /**
     * 查询数据源的数据
     *
     * @param dataSourceDefId
     * @param whereClause
     * @param queryParams
     * @param orderBy
     * @return
     */
    public List<QueryItem> query(String dataSourceDefId, String whereClause, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo);

    /**
     * 查询数据的总数
     *
     * @param dataSourceDefId
     * @param whereClause
     * @param queryParams
     * @return
     */
    public Long count(String dataSourceDefId, String whereClause, Map<String, Object> queryParams);

    /**
     * 查询数据的总数
     *
     * @param dataSourceDefId
     * @param whereClause
     * @param queryParams
     * @return
     */
    public Long countForView(String dataSourceDefId, String whereClause, Map<String, Object> queryParams);

    /**
     * 查询数据的总数
     *
     * @param dataSourceDefId
     * @param whereClause
     * @param queryParams
     * @return
     */
    public Long countForSelect(String dataSourceDefId, String whereClause, DyViewQueryInfoNew dyViewQueryInfoNew,
                               Map<String, Object> queryParams, Set<ColumnDefinitionNew> columnDefinitionNews);

    public Object[] custom(String dataSourceDefId, Object[] obj);

}

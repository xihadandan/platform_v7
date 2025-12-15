/*
* @(#)${createDate} V1.0
*
* Copyright 2015 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.provider.AbstractDataSourceProvider;
import com.wellsoft.pt.core.support.PagingInfo;
import com.wellsoft.pt.core.support.QueryItem;

/**
* Description: 如何描述该类
*
* @author ${author}
* @date ${createDate}
* @version 1.0
*
*
<pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * ${createDate}.1	${author}        ${createDate}		Create
 * </pre>
*
*/
@Component
public class ${entity}ViewMaintainSourceProvider extends AbstractDataSourceProvider {

/**
* 如何描述该方法
*
* (non-Javadoc)
* @see com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider#query(java.util.Set, java.lang.String, java.util.Map, java.lang.String, com.wellsoft.pt.core.support.PagingInfo)
*/
@Override
public List
<QueryItem> query(Set
    <DataSourceColumn> dataSourceColumns, String whereSql,
        Map
        <String
        , Object> queryParams, String orderBy, PagingInfo pagingInfo) {
        // TODO Auto-generated method stub
        return null;
        }

        /**
        * 如何描述该方法
        *
        * (non-Javadoc)
        * @see com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider#getAllDataSourceColumns()
        */
        @Override
        public Collection
        <DataSourceColumn> getAllDataSourceColumns() {
            // TODO Auto-generated method stub
            return null;
            }

            /**
            * 如何描述该方法
            *
            * (non-Javadoc)
            * @see com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider#getModuleId()
            */
            @Override
            public String getModuleId() {
            // TODO Auto-generated method stub
            return null;
            }

            /**
            * 如何描述该方法
            *
            * (non-Javadoc)
            * @see com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider#getModuleName()
            */
            @Override
            public String getModuleName() {
            // TODO Auto-generated method stub
            return null;
            }

            }

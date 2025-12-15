/*
 * @(#)2019年8月28日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.usertable.support;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreColumn;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConditionConverter;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQuery;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.DataType;
import com.wellsoft.pt.jpa.criteria.TableCriteria;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月28日.1	zhulh		2019年8月28日		Create
 * </pre>
 * @date 2019年8月28日
 */
public class UserTableFormDataQueryImpl extends AbstractQuery<FormDataQuery, QueryItem> implements FormDataQuery {

    private TableCriteria tableCriteria;

    /**
     * @param formId
     */
    public UserTableFormDataQueryImpl(UserTableFormDataQueryInfo queryInfo, NativeDao nativeDao) {
        super();
        this.tableCriteria = new TableCriteria(nativeDao, queryInfo.getTableName());
        setTableCriteriaQueryInfo(queryInfo);
    }

    /**
     * @param queryInfo
     */
    private void setTableCriteriaQueryInfo(FormDataQueryInfo queryInfo) {
        Map<String, DataStoreColumn> columnMap = getColumnMap(tableCriteria.getCriteriaMetadata());
        // 去重
        tableCriteria.setDistinct(queryInfo.isDistinct());
        // 查询列
        tableCriteria.setProjection(queryInfo.getProjection());
        // 查询条件
        List<Condition> conditions = queryInfo.getConditions();
        for (Condition condition : conditions) {
            tableCriteria.addCriterion(DataStoreConditionConverter.covertCriterion(condition, columnMap));
        }
        // 查询参数
        Map<String, Object> queryParams = queryInfo.getQueryParams();
        for (Entry<String, Object> entry : queryParams.entrySet()) {
            tableCriteria.addQueryParams(entry.getKey(), entry.getValue());
        }
        // 分组
        tableCriteria.setGroupBy(queryInfo.getGroupBy());
        tableCriteria.setHaving(queryInfo.getHaving());
        // 排序
        tableCriteria.addOrders(queryInfo.getOrders());
        // 分页
        tableCriteria.setPagingInfo(queryInfo.getPagingInfo());
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    private Map<String, DataStoreColumn> getColumnMap(CriteriaMetadata criteriaMetadata) {
        Map<String, DataStoreColumn> values = new LinkedCaseInsensitiveMap<DataStoreColumn>();
        String[] columnIndexs = criteriaMetadata.getColumnIndexs();
        for (int index = 0; index < columnIndexs.length; index++) {
            String columnIndex = columnIndexs[index];
            DataType dataType = criteriaMetadata.getDataType(index);
            DataStoreColumn dataStoreColumn = new DataStoreColumn();
            dataStoreColumn.setColumnIndex(columnIndex);
            dataStoreColumn.setDataType(dataType.getType());
            dataStoreColumn.setColumnName(columnIndex);
            values.put(columnIndex, dataStoreColumn);
        }
        return values;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.query.Query#count()
     */
    @Override
    public long count() {
        return tableCriteria.count();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.query.Query#uniqueResult()
     */
    @Override
    public QueryItem uniqueResult() {
        List<QueryItem> queryItems = tableCriteria.list(QueryItem.class);
        if (CollectionUtils.isEmpty(queryItems)) {
            return null;
        }
        if (CollectionUtils.size(queryItems) > 1) {
            throw new RuntimeException(String.format("uniqueResult return %d result", CollectionUtils.size(queryItems)));
        }
        return queryItems.get(0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.query.Query#list()
     */
    @Override
    public List<QueryItem> list() {
        return tableCriteria.list(QueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.query.Query#list(java.lang.Class)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        return tableCriteria.list(itemClass);
    }

}

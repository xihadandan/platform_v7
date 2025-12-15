/*
 * @(#)2017年5月2日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criterion.Conjunction;
import com.wellsoft.pt.jpa.criterion.Restrictions;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月2日.1	zhulh		2017年5月2日		Create
 * </pre>
 * @date 2017年5月2日
 */
public class DataStoreQueryImpl extends AbstractDataStoreQuery {
    private Criteria criteria;
    private NativeDao dao;
    private DataStoreConfiguration dataStoreConfiguration;
    private boolean distinct;
    private Set<String> columnIndexes;
    private List<Condition> conditions;
    private String defaultCondition;
    private List<DataStoreOrder> orders;
    private List<DataStoreOrder> defaultOrders;
    private PagingInfo pagingInfo;

    /**
     * @param dataStoreConfiguration
     */
    public DataStoreQueryImpl(NativeDao dao, UniversalDao universalDao, DataStoreConfiguration dataStoreConfiguration) {
        this.dao = dao;
        this.dataStoreConfiguration = dataStoreConfiguration;
        this.criteria = new DataStoreCriteria(dao, universalDao, this.dataStoreConfiguration);
        this.defaultCondition = dataStoreConfiguration.getDefaultCondition();
        if (StringUtils.isNotBlank(this.defaultCondition)) {
            this.criteria.addCriterion(Restrictions.sql(this.defaultCondition));
        }
        if (StringUtils.isNotBlank(dataStoreConfiguration.getDefaultOrder())) {
            this.defaultOrders = DataStoreOrderConverter.covertOrders(dataStoreConfiguration.getDefaultOrder());
        }
    }

    /**
     * @return the dataStoreConfiguration
     */
    public DataStoreConfiguration getDataStoreConfiguration() {
        return dataStoreConfiguration;
    }

    /**
     * @param dataStoreConfiguration 要设置的dataStoreConfiguration
     */
    public DataStoreQuery setDataStoreConfiguration(DataStoreConfiguration dataStoreConfiguration) {
        this.dataStoreConfiguration = dataStoreConfiguration;
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQuery#setDistinct(boolean)
     */
    @Override
    public DataStoreQuery setDistinct(boolean distinct) {
        this.distinct = distinct;
        this.criteria.setDistinct(distinct);
        return this;
    }

    /**
     * @return the columnIndexes
     */
    public Set<String> getColumnIndexes() {
        return columnIndexes;
    }

    /**
     * @param columnIndexes 要设置的columnIndexes
     */
    public DataStoreQuery setColumnIndexes(Set<String> columnIndexes) {
        this.columnIndexes = columnIndexes;
        this.criteria.setProjection(columnIndexes);
        return this;
    }

    /**
     * @return the conditions
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * @param conditions 要设置的conditions
     */
    public DataStoreQuery setConditions(List<Condition> conditions) {
        this.conditions = conditions;
        for (Condition condition : conditions) {
            this.criteria.addCriterion(DataStoreConditionConverter.covertCriterion(condition,
                    this.dataStoreConfiguration.getColumnMap()));
        }
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQuery#setProperties(java.util.Map)
     */
    @Override
    public void setProperties(Map<String, Object> values) {
        if (values != null) {
            for (Entry<String, Object> entry : values.entrySet()) {
                this.criteria.addQueryParams(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * @return the orders
     */
    public List<DataStoreOrder> getOrders() {
        return orders;
    }

    /**
     * @param orders 要设置的orders
     *               添加处理默认排序
     */
    public DataStoreQuery setOrders(List<DataStoreOrder> orders) {
        this.orders = orders;
        // 定义条件有排序使用定义条件排序，没有再使用默认定义排序
        if (!CollectionUtils.isEmpty(orders)) {
            Iterator<DataStoreOrder> it = orders.iterator();
            while (it.hasNext()) {
                this.criteria.addOrder(it.next().toOrder());
            }
        } else if (!CollectionUtils.isEmpty(defaultOrders)) {
            Iterator<DataStoreOrder> it = defaultOrders.iterator();
            while (it.hasNext()) {
                this.criteria.addOrder(it.next().toOrder());
            }
        }
        return this;
    }

    /**
     * @return the pagingInfo
     */
    public PagingInfo getPagingInfo() {
        return pagingInfo;
    }

    /**
     * @param pagingInfo 要设置的pagingInfo
     */
    public DataStoreQuery setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
        this.criteria.setPagingInfo(pagingInfo);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQuery#list()
     */
    @SuppressWarnings("unchecked")
    @Override
    public <ITEM extends Serializable> List<ITEM> list() {
        return (List<ITEM>) this.criteria.list(QueryItem.class);
    }

    /**
     * @return
     */
    private String getSQLQueryString() {
        StringBuilder sql = new StringBuilder();
        sql.append(getSelectClause()).append(getFromClause()).append(getWhereClause()).append(getOrderByClause());
        return sql.toString();
    }

    /**
     * @return
     */
    private Object getSelectClause() {
        StringBuilder selection = new StringBuilder(" select ");
        if (distinct) {
            selection.append(" distinct ");
        }
        if (CollectionUtils.isEmpty(this.columnIndexes)) {
            selection.append(criteria.getAlais() + ".* ");
            return selection.toString();
        }

        Iterator<String> it = columnIndexes.iterator();
        while (it.hasNext()) {
            String columnIndex = it.next();
            selection.append(this.criteria.getColumnName(columnIndex));
            if (it.hasNext()) {
                selection.append(", ");
            }
        }
        return selection.toString();
    }

    /**
     * @return
     */
    private Object getFromClause() {
        return " from " + this.dataStoreConfiguration.getTableName() + " " + criteria.getAlais() + " ";
    }

    /**
     * @return
     */
    private Object getWhereClause() {
        Conjunction conjunction = Restrictions.conjunction();
        if (conditions != null) {
            for (Condition condition : conditions) {
                conjunction.add(DataStoreConditionConverter.covertCriterion(condition,
                        this.dataStoreConfiguration.getColumnMap()));
            }
        }
        return " where " + conjunction.toSqlString(criteria);
    }

    /**
     * @return
     */
    private Object getOrderByClause() {
        List<DataStoreOrder> dataStoreOrders = Lists.newArrayListWithCapacity(0);
        if (CollectionUtils.isNotEmpty(orders)) {
            dataStoreOrders.addAll(orders);
        }
        if (CollectionUtils.isNotEmpty(defaultOrders)) {
            dataStoreOrders.addAll(defaultOrders);
        }
        if (CollectionUtils.isEmpty(dataStoreOrders)) {
            return StringUtils.EMPTY;
        }
        StringBuilder orderBy = new StringBuilder(" order by ");
        Iterator<DataStoreOrder> it = dataStoreOrders.iterator();
        while (it.hasNext()) {
            orderBy.append(it.next().toOrder().toSqlString(this.criteria));
            if (it.hasNext()) {
                orderBy.append(", ");
            }
        }
        return orderBy.toString();
    }

}

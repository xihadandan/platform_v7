/*
 * @(#)2017年5月2日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;

import java.util.*;

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
public class DataStoreQueryInfo extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7363138655928794612L;

    private boolean distinct;

    private String dataStoreId;

    private Set<String> projection;

    private List<Condition> conditions = new ArrayList<Condition>(0);

    private Map<String, Object> queryParams = new HashMap<String, Object>();

    private List<DataStoreOrder> orders = new ArrayList<DataStoreOrder>(0);

    private PagingInfo pagingInfo;

    /**
     * @return the distinct
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * @param distinct 要设置的distinct
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * @return the dataStoreId
     */
    public String getDataStoreId() {
        return dataStoreId;
    }

    /**
     * @param dataStoreId 要设置的dataStoreId
     */
    public void setDataStoreId(String dataStoreId) {
        this.dataStoreId = dataStoreId;
    }

    /**
     * @return the projection
     */
    public Set<String> getProjection() {
        return projection;
    }

    /**
     * @param projection 要设置的projection
     */
    public void setProjection(Set<String> projection) {
        this.projection = projection;
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
    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    /**
     * @param condition
     */
    public DataStoreQueryInfo addCondition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    /**
     * @return the queryParams
     */
    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    /**
     * @param queryParams 要设置的queryParams
     */
    public void setQueryParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * @return the orders
     */
    public List<DataStoreOrder> getOrders() {
        return orders;
    }

    /**
     * @param orders 要设置的orders
     */
    public void setOrders(List<DataStoreOrder> orders) {
        this.orders = orders;
    }

    /**
     * @param order
     * @return
     */
    public DataStoreQueryInfo addOrder(DataStoreOrder order) {
        this.orders.add(order);
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
    public void setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

}

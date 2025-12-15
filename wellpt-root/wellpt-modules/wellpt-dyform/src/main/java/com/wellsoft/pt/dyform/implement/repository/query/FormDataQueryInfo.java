/*
 * @(#)2019年8月23日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.jpa.criterion.Order;

import java.util.List;
import java.util.Map;
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
 * 2019年8月23日.1	zhulh		2019年8月23日		Create
 * </pre>
 * @date 2019年8月23日
 */
public class FormDataQueryInfo extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8525981085574941667L;

    private String formId;

    private boolean distinct;

    private Set<String> projection;

    private List<Condition> conditions = Lists.newArrayListWithCapacity(0);

    private Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(0);

    private String groupBy;

    private String having;

    private List<Order> orders = Lists.newArrayListWithCapacity(0);

    private PagingInfo pagingInfo;

    /**
     * @param formId
     */
    public FormDataQueryInfo(String formId) {
        super();
        this.formId = formId;
    }

    /**
     * @return the formId
     */
    public String getFormId() {
        return formId;
    }

    /**
     * @param formId 要设置的formId
     */
    public void setFormId(String formId) {
        this.formId = formId;
    }

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
     * @return the groupBy
     */
    public String getGroupBy() {
        return groupBy;
    }

    /**
     * @param groupBy 要设置的groupBy
     */
    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    /**
     * @return the having
     */
    public String getHaving() {
        return having;
    }

    /**
     * @param having 要设置的having
     */
    public void setHaving(String having) {
        this.having = having;
    }

    /**
     * @return the orders
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * @param orders 要设置的orders
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
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

/*
 * @(#)2019年8月26日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.criterion.Order;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

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
 * 2019年8月26日.1	zhulh		2019年8月26日		Create
 * </pre>
 * @date 2019年8月26日
 */
public class FormDataQueryInfoBuilder {

    private String formId;

    private boolean distinct;

    private Set<String> projection;

    private List<Condition> conditions = Lists.newArrayList();

    private Map<String, Object> queryParams = Maps.newHashMap();

    private String groupBy;

    private String having;

    private String orderBy;

    private Integer firstResult;

    private Integer maxResults;

    /**
     * @param queryInfo
     */
    public FormDataQueryInfoBuilder(String formId) {
        super();
        this.formId = formId;
    }

    /**
     * @param distinct
     * @return
     */
    public FormDataQueryInfoBuilder distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    /**
     * @param projection
     * @return
     */
    public FormDataQueryInfoBuilder projection(String[] projection) {
        if (projection != null) {
            this.projection = Sets.newHashSet(projection);
        }
        return this;
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @param operator
     * @return
     */
    public FormDataQueryInfoBuilder condition(String fieldName, String fieldValue, CriterionOperator operator) {
        conditions.add(new Condition(fieldName, fieldValue, operator));
        queryParams.put(fieldName, fieldValue);
        return this;
    }

    /**
     * @param selection
     * @return
     */
    public FormDataQueryInfoBuilder selection(String selection) {
        if (StringUtils.isNotBlank(selection)) {
            Condition sqlConditon = new Condition();
            sqlConditon.setSql(selection);
            conditions.add(sqlConditon);
        }
        return this;
    }

    /**
     * @param selectionArgs
     * @return
     */
    public FormDataQueryInfoBuilder selectionArgs(Map<String, Object> selectionArgs) {
        if (selectionArgs != null) {
            this.queryParams.putAll(selectionArgs);
        }
        return this;
    }

    /**
     * @param groupBy
     * @return
     */
    public FormDataQueryInfoBuilder groupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    /**
     * @param having
     * @return
     */
    public FormDataQueryInfoBuilder having(String having) {
        this.having = having;
        return this;
    }

    /**
     * @param orderBy
     * @return
     */
    public FormDataQueryInfoBuilder orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    /**
     * @param firstResult
     * @return
     */
    public FormDataQueryInfoBuilder firstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    /**
     * @param maxResults
     * @return
     */
    public FormDataQueryInfoBuilder maxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    /**
     * @return
     */
    public FormDataQueryInfo build() {
        FormDataQueryInfo queryInfo = new FormDataQueryInfo(formId);
        // 去重
        if (distinct) {
            queryInfo.setDistinct(distinct);
        }
        // 查询列
        if (CollectionUtils.isNotEmpty(projection)) {
            queryInfo.setProjection(projection);
        }
        // 查询条件
        if (CollectionUtils.isNotEmpty(conditions)) {
            queryInfo.setConditions(conditions);
        }
        // 查询参数
        if (MapUtils.isNotEmpty(queryParams)) {
            queryInfo.setQueryParams(queryParams);
        }
        // 分组查询
        if (StringUtils.isNotBlank(groupBy)) {
            queryInfo.setGroupBy(groupBy);
        }
        if (StringUtils.isNotBlank(having)) {
            queryInfo.setHaving(having);
        }
        // 分页
        if (firstResult != null && maxResults != null && firstResult >= 0 && maxResults > 0) {
            queryInfo.setPagingInfo(new PagingInfo((firstResult / maxResults) + 1, maxResults, false));
        }
        // 排序
        if (StringUtils.isNotBlank(orderBy)) {
            List<Order> orders = Lists.newArrayList();
            String[] orderBys = StringUtils.split(orderBy, Separator.COMMA.getValue());
            for (String order : orderBys) {
                String[] sorts = StringUtils.split(order, Separator.SPACE.getValue());
                Order dataStoreOrder = null;
                if (sorts.length == 1) {
                    dataStoreOrder = Order.asc(sorts[0]);
                } else {
                    dataStoreOrder = StringUtils.equalsIgnoreCase("asc", sorts[1]) ? Order.asc(sorts[0]) : Order
                            .desc(sorts[0]);
                }
                orders.add(dataStoreOrder);
            }
            queryInfo.setOrders(orders);
        }
        return queryInfo;
    }

}

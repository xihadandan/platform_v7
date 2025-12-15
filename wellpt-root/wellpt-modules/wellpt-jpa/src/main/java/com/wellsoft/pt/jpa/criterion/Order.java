/*
 * @(#)2016年10月26日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criterion;

import com.wellsoft.pt.jpa.criteria.Criteria;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月26日.1	xiem		2016年10月26日		Create
 * </pre>
 * @date 2016年10月26日
 */
public class Order {
    private boolean ascending;
    private boolean ignoreCase;
    private String columnIndex;

    protected Order(String columnIndex, boolean ascending) {
        this.columnIndex = columnIndex;
        this.ascending = ascending;
    }

    public static Order asc(String columnIndex) {
        return new Order(columnIndex, true);
    }

    public static Order desc(String columnIndex) {
        return new Order(columnIndex, false);
    }

    public Order ignoreCase() {
        ignoreCase = true;
        return this;
    }

    public String getColumnIndex() {
        return columnIndex;
    }

    public boolean isAscending() {
        return ascending;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public String toSqlString(Criteria criteria) {
        String sql = "";
        if (ignoreCase) {
            sql += " lower(";
        }
        sql += criteria.getColumnName(columnIndex);
        if (ignoreCase) {
            sql += " )";
        }
        sql += " ";
        sql += ascending ? "asc" : "desc";
        return sql;
    }

    public org.hibernate.criterion.Order toHibernateOrder() {
        org.hibernate.criterion.Order order = null;
        if (ascending) {
            order = org.hibernate.criterion.Order.asc(columnIndex);
        } else {
            order = org.hibernate.criterion.Order.desc(columnIndex);
        }
        if (ignoreCase) {
            order.ignoreCase();
        }
        return order;
    }
}

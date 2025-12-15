/*
 * @(#)2016年10月25日 V1.0
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
 * 2016年10月25日.1	xiem		2016年10月25日		Create
 * </pre>
 * @date 2016年10月25日
 */
public class SimpleExpression implements Criterion {
    private final String columnIndex;
    private final Object value;
    private final String op;
    private boolean ignoreCase;

    protected SimpleExpression(String columnIndex, Object value, String op) {
        this.columnIndex = columnIndex;
        this.value = value;
        this.op = op;
    }

    protected SimpleExpression(String columnIndex, Object value, String op, boolean ignoreCase) {
        this.columnIndex = columnIndex;
        this.value = value;
        this.ignoreCase = ignoreCase;
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    public String getColumnIndex() {
        return columnIndex;
    }

    public Object getValue() {
        return value;
    }

    /**
     * Make case insensitive.  No effect for non-String values
     *
     * @return {@code this}, for method chaining
     */
    public SimpleExpression ignoreCase() {
        ignoreCase = true;
        return this;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criterion.Criterion#toSqlString(com.wellsoft.pt.core.criteria.Criteria)
     */
    @Override
    public String toSqlString(Criteria criteria) {
        String paramsName = criteria.generateParamsName(columnIndex);
        criteria.addQueryParams(columnIndex, paramsName, value);
        if (ignoreCase) {
            return "lower(" + criteria.getColumnName(columnIndex) + ") " + getOp() + "  :" + paramsName;
        }
        return criteria.getColumnName(columnIndex) + getOp() + "  :" + paramsName;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criterion.Criterion#toHibernateCriterion()
     */
    @Override
    public org.hibernate.criterion.Criterion toHibernateCriterion() {
        if (op.equals("=")) {
            return org.hibernate.criterion.Restrictions.eq(columnIndex, value);
        }
        if (op.equals("<>")) {
            return org.hibernate.criterion.Restrictions.ne(columnIndex, value);
        }
        if (op.equals("<")) {
            return org.hibernate.criterion.Restrictions.lt(columnIndex, value);
        }
        if (op.equals("<=")) {
            return org.hibernate.criterion.Restrictions.le(columnIndex, value);
        }
        if (op.equals(">")) {
            return org.hibernate.criterion.Restrictions.gt(columnIndex, value);
        }
        if (op.equals(">=")) {
            return org.hibernate.criterion.Restrictions.ge(columnIndex, value);
        }
        return null;
    }

}

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
public class NotLikeExpression implements Criterion {
    private final String columnIndex;
    private final String value;
    private final boolean ignoreCase;

    protected NotLikeExpression(String columnIndex, String value, MatchMode matchMode, boolean ignoreCase) {
        this(columnIndex, matchMode.toMatchString(value), ignoreCase);
    }

    protected NotLikeExpression(String columnIndex, String value, MatchMode matchMode) {
        this(columnIndex, matchMode.toMatchString(value), false);
    }

    protected NotLikeExpression(String columnIndex, String value, boolean ignoreCase) {
        this.columnIndex = columnIndex;
        this.value = value;
        this.ignoreCase = ignoreCase;
    }

    protected NotLikeExpression(String columnIndex, String value) {
        this(columnIndex, value, false);
    }

    /**
     * @return the columnIndex
     */
    public String getColumnIndex() {
        return columnIndex;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
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
            return "lower(" + criteria.getColumnName(columnIndex) + ") not like :" + paramsName;
        }
        return criteria.getColumnName(columnIndex) + " not like :" + paramsName;
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
        org.hibernate.criterion.SimpleExpression likeExpression = org.hibernate.criterion.Restrictions.like(
                columnIndex, value);
        if (ignoreCase) {
            likeExpression.ignoreCase();
        }
        return org.hibernate.criterion.Restrictions.not(likeExpression);
    }
}

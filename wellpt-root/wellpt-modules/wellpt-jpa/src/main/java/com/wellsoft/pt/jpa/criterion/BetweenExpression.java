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
public class BetweenExpression implements Criterion {
    private final String columnIndex;
    private final Object lo;
    private final Object hi;

    protected BetweenExpression(String columnIndex, Object lo, Object hi) {
        this.columnIndex = columnIndex;
        this.lo = lo;
        this.hi = hi;
    }

    /**
     * @return the columnIndex
     */
    public String getColumnIndex() {
        return columnIndex;
    }

    /**
     * @return the lo
     */
    public Object getLo() {
        return lo;
    }

    /**
     * @return the hi
     */
    public Object getHi() {
        return hi;
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
        String loParamsName = criteria.generateParamsName(columnIndex);
        String hiParamsName = criteria.generateParamsName(columnIndex) + "_END";
        criteria.addQueryParams(columnIndex, loParamsName, lo);
        criteria.addQueryParams(columnIndex, hiParamsName, hi);
        return criteria.getColumnName(columnIndex) + " between :" + loParamsName + " and :" + hiParamsName;
    }

    @Override
    public String toString() {
        return columnIndex + " between " + lo + " and " + hi;
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
        return org.hibernate.criterion.Restrictions.between(columnIndex, lo, hi);
    }
}

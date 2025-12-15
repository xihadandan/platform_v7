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
public class InExpression implements Criterion {
    private final String columnIndex;
    private final Object[] values;

    protected InExpression(String columnIndex, Object[] values) {
        this.columnIndex = columnIndex;
        this.values = values;
    }

    /**
     * @return the columnIndex
     */
    public String getColumnIndex() {
        return columnIndex;
    }

    /**
     * @return the values
     */
    public Object[] getValues() {
        return values;
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
        criteria.addQueryParams(columnIndex, paramsName, values);
        return criteria.getColumnName(columnIndex) + " in (:" + paramsName + ")";
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
        return org.hibernate.criterion.Restrictions.in(columnIndex, values);
    }
}

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
public class PropertyExpression implements Criterion {
    private final String columnIndex;
    private final String otherColumnIndex;
    private final String op;

    protected PropertyExpression(String columnIndex, String otherColumnIndex, String op) {
        this.columnIndex = columnIndex;
        this.otherColumnIndex = otherColumnIndex;
        this.op = op;
    }

    public String getOp() {
        return op;
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
        return criteria.getColumnName(columnIndex) + getOp() + criteria.getColumnName(otherColumnIndex);
    }

    @Override
    public String toString() {
        return columnIndex + getOp() + otherColumnIndex;
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
            return org.hibernate.criterion.Restrictions.eqProperty(columnIndex, otherColumnIndex);
        }
        if (op.equals("<>")) {
            return org.hibernate.criterion.Restrictions.neProperty(columnIndex, otherColumnIndex);
        }
        if (op.equals("<")) {
            return org.hibernate.criterion.Restrictions.ltProperty(columnIndex, otherColumnIndex);
        }
        if (op.equals("<=")) {
            return org.hibernate.criterion.Restrictions.leProperty(columnIndex, otherColumnIndex);
        }
        if (op.equals(">")) {
            return org.hibernate.criterion.Restrictions.gtProperty(columnIndex, otherColumnIndex);
        }
        if (op.equals(">=")) {
            return org.hibernate.criterion.Restrictions.geProperty(columnIndex, otherColumnIndex);
        }
        return null;
    }
}

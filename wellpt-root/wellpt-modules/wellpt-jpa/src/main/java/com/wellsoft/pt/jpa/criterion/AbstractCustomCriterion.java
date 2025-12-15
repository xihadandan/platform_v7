/*
 * @(#)2017年5月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criterion;

import com.wellsoft.pt.jpa.criteria.Criteria;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月18日.1	zhulh		2017年5月18日		Create
 * </pre>
 * @date 2017年5月18日
 */
public abstract class AbstractCustomCriterion implements Criterion {
    protected String columnIndex;
    protected Object value;
    protected boolean ignoreCase;
    protected String op;

    /**
     *
     */
    public AbstractCustomCriterion() {
        super();
    }

    /**
     * @param columnIndex
     * @param value
     * @param op
     */
    public AbstractCustomCriterion(String columnIndex, Object value, String op) {
        this(columnIndex, value, op, true);
    }

    /**
     * @param columnIndex
     * @param value
     * @param op
     * @param ignoreCase
     */
    public AbstractCustomCriterion(String columnIndex, Object value, String op, boolean ignoreCase) {
        this.columnIndex = columnIndex;
        this.value = value;
        this.op = op;
        this.ignoreCase = ignoreCase;
    }

    /**
     * @return the ignoreCase
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    /**
     * @param ignoreCase 要设置的ignoreCase
     */
    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
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
    public Object getValue() {
        return value;
    }

    /**
     * @return the op
     */
    public String getOp() {
        return op;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criterion.Criterion#toSqlString(com.wellsoft.pt.core.criteria.Criteria)
     */
    @Override
    public String toSqlString(Criteria criteria) {
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criterion.Criterion#toHibernateCriterion()
     */
    @Override
    public org.hibernate.criterion.Criterion toHibernateCriterion() {
        return null;
    }

}
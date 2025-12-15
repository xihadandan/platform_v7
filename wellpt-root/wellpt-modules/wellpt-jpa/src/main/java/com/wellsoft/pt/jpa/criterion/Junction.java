/*
 * @(#)2016年10月25日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criterion;

import com.wellsoft.pt.jpa.criteria.Criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
public class Junction implements Criterion {
    private final Nature nature;
    private final List<Criterion> conditions = new ArrayList<Criterion>();

    protected Junction(Nature nature) {
        this.nature = nature;
    }

    protected Junction(Nature nature, Criterion... criterion) {
        this(nature);
        Collections.addAll(conditions, criterion);
    }

    public Junction add(Criterion criterion) {
        if (criterion != null) {
            conditions.add(criterion);
        }
        return this;
    }

    public Nature getNature() {
        return nature;
    }

    /**
     * @return the conditions
     */
    public List<Criterion> getConditions() {
        return conditions;
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
        if (conditions.size() == 0) {
            return "1=1";
        }

        final StringBuilder buffer = new StringBuilder().append('(');
        final Iterator<Criterion> itr = conditions.iterator();
        while (itr.hasNext()) {
            buffer.append(itr.next().toSqlString(criteria));
            if (itr.hasNext()) {
                buffer.append(' ').append(nature.getOperator()).append(' ');
            }
        }
        return buffer.append(')').toString();
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
        org.hibernate.criterion.Criterion[] conds = new org.hibernate.criterion.Criterion[conditions.size()];
        for (int i = 0; i < conditions.size(); i++) {
            conds[i] = conditions.get(i).toHibernateCriterion();
        }
        if (this.getNature().equals(Nature.OR)) {
            return org.hibernate.criterion.Restrictions.or(conds);
        }
        return org.hibernate.criterion.Restrictions.and(conds);
    }

    /**
     * The type of junction
     */
    public static enum Nature {
        /**
         * An AND
         */
        AND,
        /**
         * An OR
         */
        OR;

        /**
         * The corresponding SQL operator
         *
         * @return SQL operator
         */
        public String getOperator() {
            return name().toLowerCase();
        }
    }
}

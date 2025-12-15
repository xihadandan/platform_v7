/*
 * @(#)2016年10月25日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criterion;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.criteria.Criteria;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
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
public class IsExpression implements Criterion {
    private final String columnIndex;
    private final String value;

    protected IsExpression(String columnIndex, String value) {
        this.columnIndex = columnIndex;
        this.value = value;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criterion.Criterion#toSqlString(com.wellsoft.pt.core.criteria.Criteria)
     */
    @Override
    public String toSqlString(Criteria criteria) {
        String paramsName = criteria.generateParamsName(columnIndex);
        criteria.addQueryParams(columnIndex, paramsName, value);
        List<String> values = Arrays.asList(StringUtils.split(value, Separator.SEMICOLON.getValue()));
        String columnName = criteria.getColumnName(columnIndex);
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = values.iterator();
        sb.append("(");
        while (it.hasNext()) {
            sb.append(columnName);
            sb.append(" is ");
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(" or ");
            }
        }
        sb.append(")");
        return sb.toString();
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

/*
 * @(#)2017年5月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.support.criterion;

import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.TableCriteria;
import com.wellsoft.pt.jpa.criterion.AbstractCustomCriterion;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;

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
public abstract class AbstractUnitTreeCriterion extends AbstractCustomCriterion {

    /**
     *
     */
    public AbstractUnitTreeCriterion() {
        super();
    }

    /**
     * @param columnIndex
     * @param value
     * @param op
     * @param ignoreCase
     */
    public AbstractUnitTreeCriterion(String columnIndex, Object value, String op, boolean ignoreCase) {
        super(columnIndex, value, op, ignoreCase);
    }

    /**
     * @param columnIndex
     * @param value
     * @param op
     */
    public AbstractUnitTreeCriterion(String columnIndex, Object value, String op) {
        super(columnIndex, value, op);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criterion.AbstractCustomCriterion#toSqlString(com.wellsoft.pt.core.criteria.Criteria)
     */
    @Override
    public String toSqlString(Criteria criteria) {
        // 组织查询
        criteria.addQueryParams(columnIndex, "unit_in_expression_org_id", value);
        String tpl = getTemplateString();
        TableCriteria tableCriteria = (TableCriteria) criteria;
        String tableName = tableCriteria.getTableName();
        StringBuilder sb = new StringBuilder();
        sb.append("exists (select distinct a_.uuid from " + tableName + " a_ inner join (" + tpl + ")");
        sb.append(" u_ on a_." + columnIndex + " " + getOp());
        if (CriterionOperator.like.getOperator().equals(getOp())) {
            sb.append(" '%' || u_.org_id || '%'");
        } else {
            sb.append(" u_.org_id ");
        }
        sb.append(" where a_.uuid = " + criteria.getColumnName("uuid") + ")");
        return sb.toString();
    }

    /**
     * @return
     */
    public String getTemplateString() {
        String templateName = "ftl/unit_in_expression_" + getOrgType().toLowerCase() + ".ftl";
        String tpl = TemplateEngineFactory.getDefaultTemplateEngine().getTemplateAsString(
                AbstractUnitTreeCriterion.class, templateName);
        return tpl;
    }

    /**
     * @return
     */
    public abstract String getOrgType();

}

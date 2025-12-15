/*
 * @(#)2016年10月26日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.criterion.Conjunction;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.criterion.Order;
import com.wellsoft.pt.jpa.criterion.Restrictions;
import com.wellsoft.pt.jpa.dao.impl.BaseDaoImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
public abstract class AbstractCriteria extends BaseDaoImpl implements Criteria {
    protected final Conjunction conjunction = Restrictions.conjunction();
    protected final Map<String, Object> queryParams = new HashMap<String, Object>();
    protected final List<Order> orders = new ArrayList<Order>();
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected boolean distinct;
    protected Set<String> projection;
    protected String groupBy;
    protected String having;
    protected int maxResults = Integer.MAX_VALUE;
    protected int firstResult = 1;
    protected PagingInfo pagingInfo;
    private CriteriaMetadata criteriaMetadata;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#setDistinct(boolean)
     */
    @Override
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#setProjection(java.util.Set)
     */
    @Override
    public void setProjection(Set<String> projection) {
        this.projection = projection;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#addCriterion(com.wellsoft.pt.core.criterion.Criterion)
     */
    @Override
    public void addCriterion(Criterion criterion) {
        if (criterion != null) {
            conjunction.add(criterion);
        }
    }

    /**
     * @return
     */
    public String getAlais() {
        return "this_";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getColumnName(java.lang.String)
     */
    @Override
    public String getColumnName(String columnIndex) {
        /**
         * modify by chenqiong
         * modify time : 2017/9/13 14:31
         * reason : getAlais() 被SqlCriteria重写为空字符串，如果是空字符串就不需要拼 “别名.字段”
         */
        String columnName = getCriteriaMetadata().getMapColumnIndex(columnIndex);
        return StringUtils.isBlank(getAlais()) ? columnName : getAlais() + "." + columnName;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getQueryParams()
     */
    @Override
    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#addQueryParams(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public Criteria addQueryParams(String columnIndex, String paramName, Object value) {
        return addQueryParams(paramName, value);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#addQueryParams(java.lang.String, java.lang.Object)
     */
    @Override
    public Criteria addQueryParams(String paramName, Object value) {
        queryParams.put(paramName, value);
        return this;
    }

    @Override
    public String generateParamsName(String columnIndex) {
        int i = 0;
        while (true) {
            String paramsName = columnIndex + "_" + (++i);
            if (!queryParams.containsKey(columnIndex)) {
                return columnIndex;
            }
            if (!queryParams.containsKey(paramsName)) {
                return paramsName;
            }
        }
    }

    /**
     * @return the groupBy
     */
    public String getGroupBy() {
        return groupBy;
    }

    /**
     * @param groupBy 要设置的groupBy
     */
    public Criteria setGroupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    /**
     * @return the having
     */
    public String getHaving() {
        return having;
    }

    /**
     * @param having 要设置的having
     */
    public Criteria setHaving(String having) {
        this.having = having;
        return this;
    }

    @Override
    public Criteria setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
        return this;
    }

    @Override
    public PagingInfo getPagingInfo() {
        return this.pagingInfo;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#addOrder(com.wellsoft.pt.core.criterion.Order)
     */
    @Override
    public Criteria addOrder(Order order) {
        orders.add(order);
        return this;
    }

    @Override
    public Criteria addOrders(Collection<Order> orders) {
        this.orders.addAll(orders);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getOrders()
     */
    @Override
    public List<Order> getOrders() {
        return this.orders;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getCriteriaMetadata()
     */
    @Override
    public CriteriaMetadata getCriteriaMetadata() {
        if (criteriaMetadata == null) {
            criteriaMetadata = initCriteriaMetadata();
        }
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.Criteria#setCriteriaMetadata(com.wellsoft.pt.jpa.criteria.CriteriaMetadata)
     */
    @Override
    public void setCriteriaMetadata(CriteriaMetadata criteriaMetadata) {
        this.criteriaMetadata = criteriaMetadata;
    }

    protected abstract CriteriaMetadata initCriteriaMetadata();

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getCriterion()
     */
    @Override
    public Criterion getCriterion() {
        return conjunction;
    }

    protected String getQuerySql() {
        StringBuilder sBuffer = new StringBuilder();
        sBuffer.append(getSelectSql()).append(getFromSql()).append(getWhereSql()).append(getGroupBySql()).append(getHavingSql()).append(getOrderSql());
        return sBuffer.toString();
    }

    protected String getCountSql() {
        StringBuilder sBuffer = new StringBuilder("select count(1) ");
        sBuffer.append(getFromSql()).append(getWhereSql());
        return sBuffer.toString();

    }

    protected String getSelectSql() {
        StringBuilder selection = new StringBuilder(" select ");
        if (distinct) {
            selection.append(" distinct ");
        }
        if (CollectionUtils.isEmpty(this.projection)) {
            String alais = this.getAlais();
            if (StringUtils.isBlank(alais)) {
                selection.append("* ");
            } else {
                selection.append(alais + ".* ");
            }
            return selection.toString();
        }

        Iterator<String> it = this.projection.iterator();
        while (it.hasNext()) {
            String columnIndex = it.next();
            selection.append(this.getColumnName(columnIndex));
            if (it.hasNext()) {
                selection.append(", ");
            }
        }
        return selection.toString();
    }

    protected String getFromSql() {
        return "from dual";
    }

    ;

    protected String getWhereSql() {
        return " where " + conjunction.toSqlString(this);
    }

    /**
     * @return
     */
    protected String getGroupBySql() {
        if (StringUtils.isBlank(groupBy)) {
            return StringUtils.EMPTY;
        }
        return " group by " + groupBy;
    }

    /**
     * @return
     */
    protected String getHavingSql() {
        if (StringUtils.isBlank(having)) {
            return StringUtils.EMPTY;
        }
        return " having " + having;
    }

    protected String getOrderSql() {
        if (CollectionUtils.isEmpty(orders)) {
            return "";
        }
        StringBuilder sBuffer = new StringBuilder(" order by ");
        for (Order order : orders) {
            sBuffer.append(order.toSqlString(this));
            if (sBuffer.length() > 0) {
                sBuffer.append(",");
            }
        }
        return sBuffer.substring(0, sBuffer.length() - 1);
    }

}

/*
 * @(#)2017年5月2日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.criterion.Order;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月2日.1	zhulh		2017年5月2日		Create
 * </pre>
 * @date 2017年5月2日
 */
public class DataStoreCriteria implements Criteria {

    private Criteria criteria;

    public DataStoreCriteria(NativeDao dao, UniversalDao universalDao, DataStoreConfiguration dataStoreConfiguration) {
        criteria = DataStoreCriteriaFactory.getCriteria(dao, universalDao, dataStoreConfiguration);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#setDistinct(boolean)
     */
    @Override
    public void setDistinct(boolean distinct) {
        criteria.setDistinct(distinct);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#setProjection(java.util.Set)
     */
    @Override
    public void setProjection(Set<String> projection) {
        criteria.setProjection(projection);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#addCriterion(com.wellsoft.pt.core.criterion.Criterion)
     */
    @Override
    public void addCriterion(Criterion criterion) {
        if (criterion != null) {
            criteria.addCriterion(criterion);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#addOrder(com.wellsoft.pt.core.criterion.Order)
     */
    @Override
    public Criteria addOrder(Order order) {
        return criteria.addOrder(order);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#addOrders(java.util.Collection)
     */
    @Override
    public Criteria addOrders(Collection<Order> orders) {
        return criteria.addOrders(orders);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#addQueryParams(java.lang.String, java.lang.Object)
     */
    @Override
    public Criteria addQueryParams(String paramName, Object value) {
        return criteria.addQueryParams(paramName, value);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#addQueryParams(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public Criteria addQueryParams(String columnIndex, String paramName, Object value) {
        return criteria.addQueryParams(columnIndex, paramName, value);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#count()
     */
    @Override
    public long count() {
        return criteria.count();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#generateParamsName(java.lang.String)
     */
    @Override
    public String generateParamsName(String columnIndex) {
        return criteria.generateParamsName(columnIndex);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getAlais()
     */
    @Override
    public String getAlais() {
        return criteria.getAlais();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getColumnName(java.lang.String)
     */
    @Override
    public String getColumnName(String columnIndex) {
        return criteria.getColumnName(columnIndex);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getCriteriaMetadata()
     */
    @Override
    public CriteriaMetadata getCriteriaMetadata() {
        return criteria.getCriteriaMetadata();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.Criteria#setCriteriaMetadata(com.wellsoft.pt.jpa.criteria.CriteriaMetadata)
     */
    @Override
    public void setCriteriaMetadata(CriteriaMetadata criteriaMetadata) {
        criteria.setCriteriaMetadata(criteriaMetadata);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getCriterion()
     */
    @Override
    public Criterion getCriterion() {
        return criteria.getCriterion();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getOrders()
     */
    @Override
    public List<Order> getOrders() {
        return criteria.getOrders();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getPagingInfo()
     */
    @Override
    public PagingInfo getPagingInfo() {
        return criteria.getPagingInfo();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getQueryParams()
     */
    @Override
    public Map<String, Object> getQueryParams() {
        return criteria.getQueryParams();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getSession()
     */
    @Override
    public Session getSession() {
        return criteria.getSession();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getSessionFactory()
     */
    @Override
    public SessionFactory getSessionFactory() {
        return criteria.getSessionFactory();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#list()
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list() {
        return criteria.list();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#list(java.lang.Class)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        return criteria.list(itemClass);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#setPagingInfo(com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public Criteria setPagingInfo(PagingInfo pagingInfo) {
        return criteria.setPagingInfo(pagingInfo);
    }

}

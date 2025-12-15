/*
 * @(#)2014-7-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.criteria.*;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryRegistrar;
import com.wellsoft.pt.jpa.support.NativeAliasToLowerCaseEntityMapResultTransformer;
import com.wellsoft.pt.jpa.support.NativeQueryItemResultTransformer;
import org.apache.commons.collections.MapUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.ResultTransformer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 租户库原生HQL的DAO接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-7.1	zhulh		2014-7-7		Create
 * </pre>
 * @date 2014-7-7
 */
@Repository
@Scope(value = "prototype")
public class NativeDaoImpl extends BaseDaoImpl implements NativeDao {

    private SessionFactory sessionFactory;

    private String multiTenancyStrategy;

    private static String prepareCountHql(String orgHql) {
        String fromHql = orgHql;
        // select子句与order by子句会影响count查询,进行简单的排除.
        // fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
        // fromHql = StringUtils.substringBefore(fromHql, "order by");
        // String countHql = "select count(*) " + fromHql;
        String countHql = "select count(*) from (" + fromHql + ") t";
        return countHql;
    }

    /**
     *
     */
    @PostConstruct
    public void init() {
        sessionFactory = (SessionFactory) ApplicationContextHolder.getBean(
                Config.TENANT_SESSION_FACTORY_BEAN_NAME);
        multiTenancyStrategy = Config.getValue(Config.KEY_MULTI_TENANCY_STRATEGY);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#setSessionFactory(org.hibernate.SessionFactory)
     */
    @Override
    public void setSessionFactory(SessionFactory sessionFactory, String multiTenancyStrategy) {
        this.sessionFactory = sessionFactory;
        this.multiTenancyStrategy = multiTenancyStrategy;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#findUnique(java.lang.String, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U extends Serializable> U findUnique(String sql, Map<String, Object> values) {
        return (U) createSQLQuery(sql, values).uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#findUnique(java.lang.String, java.util.Map, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U extends Serializable> U findUnique(String sql, Map<String, Object> values,
                                                 Class<U> itemClass) {
        return (U) createSQLQuery(sql, values).setResultTransformer(
                getTtemTransformerMap(itemClass)).uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#findUniqueByNamedQuery(java.lang.String, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U extends Serializable> U findUniqueByNamedQuery(String queryName,
                                                             Map<String, Object> values) {
        return (U) createSQLQuery(getDynamicNamedQueryString(sessionFactory, queryName, values),
                values).uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#findUniqueByNamedQuery(java.lang.String, java.util.Map, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U extends Serializable> U findUniqueByNamedQuery(String queryName,
                                                             Map<String, Object> values,
                                                             Class<U> itemClass) {
        return (U) createSQLQuery(getDynamicNamedQueryString(sessionFactory, queryName, values),
                values).setResultTransformer(getTtemTransformerMap(itemClass))
                .uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#query(java.lang.String, java.util.Map)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> query(String sql, Map<String, Object> values) {
        return this.query(sql, values, null, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#query(java.lang.String, java.util.Map, java.lang.Class)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> query(String sql, Map<String, Object> values,
                                                        Class<ITEM> itemClass) {
        return this.query(sql, values, itemClass, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#query(java.lang.String, java.util.Map, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> query(String sql, Map<String, Object> values,
                                                        PagingInfo pagingInfo) {
        return this.query(sql, values, null, pagingInfo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ITEM extends Serializable> List<ITEM> query(String sql,
                                                        final Map<String, Object> values,
                                                        Class<ITEM> itemClass,
                                                        PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        SQLQuery query = getSession().createSQLQuery(sql);
        if (MapUtils.isNotEmpty(values)) {
            query.setProperties(values);
        }

        // 分页
        if (pagingInfo != null) {
            if (pagingInfo.isAutoCount()) {
                long totalCount = countHqlResult(sql, values);
                pagingInfo.setTotalCount(totalCount);
            }
            // hibernate的firstResult的序号从0开始
            query.setFirstResult(pagingInfo.getFirst());
            query.setMaxResults(pagingInfo.getPageSize());
        }

        // 结果类型转换
        if (itemClass == null) {
            // hibernate默认处理
        } else if (QueryItem.class.isAssignableFrom(itemClass)) {
            query.setResultTransformer(NativeQueryItemResultTransformer.INSTANCE);
        } else if (JpaEntity.class.isAssignableFrom(itemClass)) {
            // 转换为实体
            query.setResultTransformer(getTtemTransformerMap(itemClass));
        } else if (Map.class.isAssignableFrom(itemClass)) {
            // 转换为MAP
            query.setResultTransformer(NativeAliasToLowerCaseEntityMapResultTransformer.INSTANCE);
        } else {
            query.setResultTransformer(getTtemTransformerMap(itemClass));
        }

        return query.list();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#namedQuery(java.lang.String, java.util.Map)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName,
                                                             Map<String, Object> values) {
        return this.namedQuery(queryName, values, null, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#namedQuery(java.lang.String, java.util.Map, java.lang.Class)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName,
                                                             Map<String, Object> values,
                                                             Class<ITEM> itemClass) {
        return this.namedQuery(queryName, values, itemClass, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#namedQuery(java.lang.String, java.util.Map, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName,
                                                             Map<String, Object> values,
                                                             PagingInfo pagingInfo) {
        return this.namedQuery(queryName, values, null, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#namedQuery(java.lang.String, java.util.Map, java.lang.Class, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName,
                                                             Map<String, Object> values,
                                                             Class<ITEM> itemClass,
                                                             PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        String hql = getDynamicNamedQueryString(this.sessionFactory, queryName, values);
        return this.query(hql, values, itemClass, pagingInfo);
    }

    @SuppressWarnings("unchecked")
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName,
                                                             final Map<String, Object> values,
                                                             ResultTransformer transformer) {
        // 条件与排序信息在HQL中
        String hql = getDynamicNamedQueryString(this.sessionFactory, queryName, values);
        SQLQuery query = getSession().createSQLQuery(hql);
        query.setProperties(values);
        // 结果类型转换
        query.setResultTransformer(transformer);
        return query.list();
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数.
     * <p>
     * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
     */
    protected long countHqlResult(final String hql, final Map<String, Object> values) {
        String countHql = prepareCountHql(hql);

        try {
            Long count = ((Number) createSQLQuery(countHql, values).uniqueResult()).longValue();
            return count;
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
        }
    }

    /**
     * 根据查询HQL与参数列表创建Query对象. 与find()函数可进行更加灵活的操作.
     *
     * @param values 命名参数,按名称绑定.
     */
    public Query createSQLQuery(final String queryString, final Map<String, ?> values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = getSession().createSQLQuery(queryString);
        if (values != null) {
            query.setProperties(values);
        }
        return query;
    }

    /**
     * @return
     */
    public <ENTITY extends Serializable> Session getSession() {
        if (Config.MULTI_TENANCY_STRATEGY_SESSION_FACTORY.equals(multiTenancyStrategy)) {
            SessionFactory sf = SessionFactoryRegistrar.getCurrentTenantSessionFactory();
            if (sf != null) {
                return sf.getCurrentSession();
            }
        }
        return sessionFactory.getCurrentSession();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#countByNamedQuery(java.lang.String, java.util.Map)
     */
    @Override
    public Long countByNamedQuery(String queryName, Map<String, Object> values) {
        // 条件与排序信息在HQL中
        String sql = getDynamicNamedQueryString(this.sessionFactory, queryName, values);
        return countHqlResult(sql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#batchExecute(java.lang.String, java.util.Map)
     */
    @Override
    public int batchExecute(String sql, Map<String, Object> values) {
        Query query = getSession().createSQLQuery(sql);
        if (values != null) {
            query.setProperties(values);
        }
        return query.executeUpdate();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#update(java.lang.String,
     * java.util.Map)
     */
    @Override
    public int update(String sql, Map<String, Object> values) {
        return batchExecute(sql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#executeSql(java.lang.String)
     */
    @Override
    public void executeSql(String sql) {
        batchExecute(sql, null);
    }

    @Override
    public int namedExecute(String executeName, Map<String, Object> values) {
        // 条件与排序信息在HQL中
        String sql = getDynamicNamedQueryString(this.sessionFactory, executeName, values);
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        if (values != null) {
            query.setProperties(values);
        }
        return query.executeUpdate();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.NativeDao#createTableCriteria(java.lang.String)
     */
    @Override
    public Criteria createTableCriteria(String tableName) {
        return new TableCriteria(this, tableName);
    }

    @Override
    public Criteria createSqlCriteria(String sql) {
        return new SqlCriteria(this, sql);
    }

    @Override
    public Criteria createInterfaceCriteria(QueryInterface query, String dataInterfaceParam) {
        return new InterfaceCriteria(this, query, dataInterfaceParam);
    }

    @Override
    public Criteria createNamedQueryCriteria(String sqlName) {
        return new NamedQueryCriteria(this, sqlName);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}

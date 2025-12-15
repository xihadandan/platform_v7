package com.wellsoft.pt.jpa.hibernate;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.BaseEntity;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.PropertyFilter.MatchType;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.*;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.*;

/**
 * 封装扩展功能的Hibernat DAO泛型基类.
 * <p>
 * 扩展功能包括分页查询,按属性过滤条件列表查询. 可在Service层直接使用,也可以扩展泛型DAO子类使用,见两个构造函数的注释.
 *
 * @param <T>  DAO操作的对象类型
 * @param <PK> 主键类型
 * @author lilin
 */
public class HibernateDao<T, PK extends Serializable> extends SimpleHibernateDao<T, PK> {


    private static final Map<String, ResultTransformer> itemTransformerMap = new HashMap<String, ResultTransformer>();

    /**
     * 用于省略Dao层, Service层直接使用通用HibernateDao的构造函数. 在构造函数中定义对象类型Class. eg.
     * HibernateDao<User, Long> userDao = new HibernateDao<User,
     * Long>(sessionFactory, User.class);
     */
    // public HibernateDao(final SessionFactory sessionFactory, final Class<T>
    // entityClass) {
    // super(sessionFactory, entityClass);
    // }

    // -- 分页查询函数 --//

    /**
     * 用于Dao层子类的构造函数. 通过子类的泛型定义取得对象类型Class. eg. public class UserDao extends
     * HibernateDao<User, Long>{ }
     */
    public HibernateDao() {
        super();
    }

    /**
     * 分页获取全部对象.
     */
    public Page<T> getAll(final Page<T> page) {
        return findPage(page);
    }

    /**
     * 按HQL分页查询.
     *
     * @param page   分页参数.
     * @param hql    hql语句.
     * @param values 数量可变的查询参数,按顺序绑定.
     * @return 分页查询结果, 附带结果列表及所有查询输入参数.
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public Page<T> findPage(final Page<T> page, final String hql, final Object... values) {
        Assert.notNull(page, "page不能为空");
        String orderhql = getOrderHql(page, hql);
        Query q = createQuery(orderhql, values);

        if (page.isAutoCount()) {
            long totalCount = countHqlResult(orderhql, values);
            page.setTotalCount(totalCount);
        }

        setPageParameterToQuery(q, page);

        List result = q.list();
        page.setResult(result);
        return page;
    }

    /**
     * 按HQL分页查询.
     *
     * @param page   分页参数. 如果要做排序查询，注意page中设置的orderBy属性要和对应的hql一致.
     * @param hql    hql语句.
     * @param values 命名参数,按名称绑定.
     * @return 分页查询结果, 附带结果列表及所有查询输入参数.
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public Page<T> findPage(final Page<T> page, final String hql, final Map<String, ?> values) {
        Assert.notNull(page, "page不能为空");
        String orderhql = getOrderHql(page, hql);
        Query q = createQuery(orderhql, values);

        if (page.isAutoCount()) {
            long totalCount = countHqlResult(orderhql, values);
            page.setTotalCount(totalCount);
        }

        setPageParameterToQuery(q, page);

        List result = q.list();
        page.setResult(result);
        return page;
    }

    /**
     * 获取相应的根据page中设置的orderby，order属性拼装相应的hql查询语句，注意参数的对应要一致 如hql为from User u
     * oderby属性要设置为u.uuid等
     *
     * @param page
     * @param hql
     * @return
     */
    private String getOrderHql(final Page<T> page, String hql) {
        String orderhql = hql;
        if (page.isOrderBySetted()) {
            String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
            String[] orderArray = StringUtils.split(page.getOrder(), ',');

            Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

            orderhql += " order by ";
            for (int i = 0; i < orderByArray.length; i++) {

                if (i == orderByArray.length - 1) {
                    orderhql += orderByArray[i] + " " + orderArray[i];
                } else {
                    orderhql += orderByArray[i] + " " + orderArray[i] + ", ";
                }

            }
            // orderhql = orderhql.substring(0, orderhql.length() - 2);
        }
        return orderhql;
    }

    /**
     * 按Criteria分页查询.
     *
     * @param page       分页参数.
     * @param criterions 数量可变的Criterion.
     * @return 分页查询结果.附带结果列表及所有查询输入参数.
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public Page<T> findPage(final Page<T> page, final Criterion... criterions) {
        Assert.notNull(page, "page不能为空");

        Criteria c = createCriteria(criterions);

        if (page.isAutoCount()) {
            long totalCount = countCriteriaResult(c);
            page.setTotalCount(totalCount);
        }
        setPageParameterToCriteria(c, page);

        List result = c.list();
        page.setResult(result);
        return page;
    }

    /**
     * 设置分页参数到Query对象,辅助函数.
     */
    protected Query setPageParameterToQuery(final Query q, final Page<T> page) {

        Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");

        // hibernate的firstResult的序号从0开始
        q.setFirstResult(page.getFirst() - 1);
        q.setMaxResults(page.getPageSize());
        return q;
    }

    /**
     * 设置分页参数到Criteria对象,辅助函数.
     */
    protected Criteria setPageParameterToCriteria(final Criteria c, final Page<T> page) {

        Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");

        // hibernate的firstResult的序号从0开始
        c.setFirstResult(page.getFirst() - 1);
        c.setMaxResults(page.getPageSize());

        if (page.isOrderBySetted()) {
            String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
            String[] orderArray = StringUtils.split(page.getOrder(), ',');

            Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

            for (int i = 0; i < orderByArray.length; i++) {
                if (Page.ASC.equals(orderArray[i])) {
                    c.addOrder(Order.asc(orderByArray[i]));
                } else {
                    c.addOrder(Order.desc(orderByArray[i]));
                }
            }
        }
        return c;
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数.
     * <p>
     * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
     */
    protected long countHqlResult(final String hql, final Object... values) {
        String countHql = prepareCountHql(hql);

        try {
            Long count = findUnique(countHql, values);
            return count;
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
        }
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数.
     * <p>
     * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
     */
    protected long countHqlResult(final String hql, final Map<String, ?> values) {
        String countHql = prepareCountHql(hql);

        try {
            Long count = findUnique(countHql, values);
            return count;
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
        }
    }

    private String prepareCountHql(String orgHql) {
        String fromHql = orgHql;
        // select子句与order by子句会影响count查询,进行简单的排除.
        fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
        fromHql = StringUtils.substringBefore(fromHql, "order by");
        String countHql = "select count(*) " + fromHql;
        return countHql;
    }

    // -- 属性过滤条件(PropertyFilter)查询函数 --//

    /**
     * 执行count查询获得本次Criteria查询所能获得的对象总数.
     */
    @SuppressWarnings("unchecked")
    protected long countCriteriaResult(final Criteria c) {
        CriteriaImpl impl = (CriteriaImpl) c;

        // 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
        Projection projection = impl.getProjection();
        ResultTransformer transformer = impl.getResultTransformer();

        List<CriteriaImpl.OrderEntry> orderEntries = null;
        try {
            orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");
            ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        // 执行Count查询
        Long totalCountObject = (Long) c.setProjection(Projections.rowCount()).uniqueResult();
        long totalCount = (totalCountObject != null) ? totalCountObject : 0;

        // 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
        c.setProjection(projection);

        if (projection == null) {
            c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        }
        if (transformer != null) {
            c.setResultTransformer(transformer);
        }
        try {
            ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return totalCount;
    }

    /**
     * 按属性查找对象列表,支持多种匹配方式.
     *
     * @param matchType 匹配方式,目前支持的取值见PropertyFilter的MatcheType enum.
     */
    public List<T> findBy(final String propertyName, final Object value,
                          final MatchType matchType) {
        Criterion criterion = buildCriterion(propertyName, value, matchType);
        return find(criterion);
    }

    /**
     * 按属性过滤条件列表查找对象列表.
     */
    public List<T> find(List<PropertyFilter> filters) {
        Criterion[] criterions = buildCriterionByPropertyFilter(filters);
        return find(criterions);
    }

    /**
     * 按属性过滤条件列表分页查找对象.
     */
    public Page<T> findPage(final Page<T> page, final List<PropertyFilter> filters) {
        Criterion[] criterions = buildCriterionByPropertyFilter(filters);
        return findPage(page, criterions);
    }

    /**
     * 按属性条件参数创建Criterion,辅助函数.
     */
    protected Criterion buildCriterion(final String propertyName, final Object propertyValue,
                                       final MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");
        SimpleExpression criterion = null;
        // 根据MatchType构造criterion
        switch (matchType) {
            case EQ:
                criterion = Restrictions.eq(propertyName, propertyValue);
                break;
            case LIKE:
                criterion = Restrictions.like(propertyName, (String) propertyValue,
                        MatchMode.ANYWHERE);
                break;

            case LE:
                criterion = Restrictions.le(propertyName, propertyValue);
                break;
            case LT:
                criterion = Restrictions.lt(propertyName, propertyValue);
                break;
            case GE:
                criterion = Restrictions.ge(propertyName, propertyValue);
                break;
            case GT:
                criterion = Restrictions.gt(propertyName, propertyValue);
        }
        if (propertyValue != null && String.class.isAssignableFrom(propertyValue.getClass())) {
            criterion.ignoreCase();
        }
        return criterion;
    }

    /**
     * 按属性条件列表创建Criterion数组,辅助函数.
     */
    protected Criterion[] buildCriterionByPropertyFilter(final List<PropertyFilter> filters) {
        List<Criterion> criterionList = new ArrayList<Criterion>();
        for (PropertyFilter filter : filters) {
            if (!filter.hasMultiProperties()) { // 只有一个属性需要比较的情况.
                Criterion criterion = buildCriterion(filter.getPropertyName(),
                        filter.getMatchValue(),
                        filter.getMatchType());
                criterionList.add(criterion);
            } else {// 包含多个属性需要比较的情况,进行or处理.
                Disjunction disjunction = Restrictions.disjunction();
                for (String param : filter.getPropertyNames()) {
                    Criterion criterion = buildCriterion(param, filter.getMatchValue(),
                            filter.getMatchType());
                    disjunction.add(criterion);
                }
                criterionList.add(disjunction);
            }
        }
        return criterionList.toArray(new Criterion[criterionList.size()]);
    }

    /**
     * 按属性条件列表创建Criterion数组,辅助函数.
     */
    protected Criterion[] buildCriterionByPropertyFilter(final List<PropertyFilter> filters,
                                                         boolean queryOr) {
        if (!queryOr) {
            return buildCriterionByPropertyFilter(filters);
        }
        List<Criterion> criterionList = new ArrayList<Criterion>();
        // 包含多个属性需要比较的情况,进行or处理.
        Disjunction disjunction = Restrictions.disjunction();
        for (PropertyFilter filter : filters) {
            if (!filter.hasMultiProperties()) { // 只有一个属性需要比较的情况.
                Criterion criterion = buildCriterion(filter.getPropertyName(),
                        filter.getMatchValue(),
                        filter.getMatchType());
                disjunction.add(criterion);
                criterionList.add(disjunction);
            } else {
                for (String param : filter.getPropertyNames()) {
                    Criterion criterion = buildCriterion(param, filter.getMatchValue(),
                            filter.getMatchType());
                    disjunction.add(criterion);
                }
                criterionList.add(disjunction);
            }
        }
        return criterionList.toArray(new Criterion[criterionList.size()]);
    }

    public <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example) {
        return this.findByExample(example, null, null);
    }

    public <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example,
                                                                    String orderByProperty) {
        return this.findByExample(example, orderByProperty, null);
    }

    public <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example,
                                                                    String orderByProperty,
                                                                    PagingInfo pagingInfo) {
        return this.findByExample(example, null, orderByProperty, pagingInfo);
    }

    @SuppressWarnings("unchecked")
    public <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example,
                                                                    List<PropertyFilter> propertyFilters,
                                                                    String orderByProperty,
                                                                    PagingInfo pagingInfo) {
        Criteria criteria = getSession().createCriteria(ClassUtils.getUserClass(example)).add(
                Example.create(example));

        // 查询字段
        if (propertyFilters != null) {
            boolean queryOr = false;
            if (!propertyFilters.isEmpty()) {
                queryOr = propertyFilters.get(0).isQueryOr();
            }
            Criterion[] criterions = buildCriterionByPropertyFilter(propertyFilters, queryOr);
            for (Criterion criterion : criterions) {
                criteria.add(criterion);
            }
        }
        // 排序
        if (orderByProperty != null) {
            String[] orderBys = StringUtils.split(orderByProperty, ',');
            for (int i = 0; i < orderBys.length; i++) {
                String[] orderBy = StringUtils.split(orderBys[i], ' ');
                String propertyName = orderBy[0];
                if (orderBy.length == 1) {
                    criteria.addOrder(Order.asc(propertyName));
                } else if (orderBy.length == 2) {
                    String order = orderBy[1];
                    if (Page.ASC.equalsIgnoreCase(order)) {
                        criteria.addOrder(Order.asc(propertyName));
                    } else if (Page.DESC.equalsIgnoreCase(order)) {
                        criteria.addOrder(Order.desc(propertyName));
                    } else {
                        throw new RuntimeException("Unknow order [" + order + "]");
                    }
                } else {
                    throw new RuntimeException("Unknow order by [" + orderBys[i] + "]");
                }
            }
        }

        // 分页
        if (pagingInfo != null) {
            if (pagingInfo.isAutoCount()) {
                pagingInfo.setTotalCount(countCriteriaResult(criteria));
                // criteria.setProjection(Projections.rowCount());
                // pagingInfo.setTotalCount((Long) criteria.uniqueResult());
                // criteria.setProjection(null);
            }
            // hibernate的firstResult的序号从0开始
            criteria.setFirstResult(pagingInfo.getFirst());
            criteria.setMaxResults(pagingInfo.getPageSize());
        }
        return criteria.list();
    }

    /**
     * HQL命名查询
     *
     * @param bean
     * @param itemClass
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> namedQuery(final Map<String, ?> values,
                                                             Class<ITEM> itemClass) {
        return this.namedQuery(values, itemClass, null);
    }

    /**
     * HQL命名查询
     *
     * @param bean
     * @param itemClass
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> namedQuery(Object bean, Class<ITEM> itemClass) {
        return this.namedQuery(convertMap(bean), itemClass, null);
    }

    /**
     * HQL命名查询
     *
     * @param bean
     * @param itemClass
     * @param pagingInfo
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> namedQuery(final Map<String, ?> values,
                                                             Class<ITEM> itemClass,
                                                             PagingInfo pagingInfo) {
        // "UserQueryItem", "userQuery"
        String itemName = itemClass.getSimpleName();
        String queryName = Introspector.decapitalize(itemName.substring(0, itemName.length() - 4));
        return this.namedQuery(queryName, values, itemClass, pagingInfo);
    }

    /**
     * HQL命名查询
     *
     * @param bean
     * @param itemClass
     * @param pagingInfo
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> namedQuery(Object bean, Class<ITEM> itemClass,
                                                             PagingInfo pagingInfo) {
        return this.namedQuery(convertMap(bean), itemClass, pagingInfo);
    }

    /**
     * HQL命名查询
     *
     * @param queryName
     * @param bean
     * @param itemClass
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName,
                                                             final Map<String, ?> values) {
        return this.namedQuery(queryName, values, null, null);
    }

    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName,
                                                             final Map<String, ?> values,
                                                             Class<ITEM> itemClass) {
        return this.namedQuery(queryName, values, itemClass, null);
    }

    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, final Object bean,
                                                             Class<ITEM> itemClass) {
        return this.namedQuery(queryName, convertMap(bean), itemClass);
    }

    /**
     * HQL命名查询
     *
     * @param queryName
     * @param bean
     * @param itemClass
     * @param pagingInfo
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName,
                                                             final Map<String, ?> values,
                                                             Class<ITEM> itemClass,
                                                             PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        Map<String, Object> params = Maps.newHashMap();
        params.putAll(values);
        String hql = getNamedHql(queryName, params);

        // FreeMarker模板编译
        try {
            hql = TemplateEngineFactory.getDefaultTemplateEngine().process(hql, values);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return this.query(hql, values, itemClass, pagingInfo);
    }

    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, final Object bean,
                                                             Class<ITEM> itemClass,
                                                             PagingInfo pagingInfo) {
        return this.namedQuery(queryName, convertMap(bean), itemClass, pagingInfo);
    }

    public <ITEM extends Serializable> List<ITEM> query(String hql, Object bean,
                                                        Class<ITEM> itemClass) {
        return this.query(hql, convertMap(bean), itemClass);
    }

    public <ITEM extends Serializable> List<ITEM> query(String hql, Object bean,
                                                        Class<ITEM> itemClass,
                                                        PagingInfo pagingInfo) {
        return this.query(hql, convertMap(bean), itemClass, pagingInfo);
    }

    public <ITEM extends Serializable> List<ITEM> query(String hql, final Map<String, ?> values,
                                                        Class<ITEM> itemClass) {
        return this.query(hql, values, itemClass, null);
    }

    @SuppressWarnings("unchecked")
    public <ITEM extends Serializable> List<ITEM> query(String hql, final Map<String, ?> values,
                                                        Class<ITEM> itemClass,
                                                        PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        Query query = this.getSession().createQuery(hql);
        query.setProperties(values);

        // 分页
        if (pagingInfo != null) {
            if (pagingInfo.isAutoCount()) {
                long totalCount = countHqlResult(hql, values);
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
            query.setResultTransformer(QueryItemResultTransformer.INSTANCE);
        } else if (BaseEntity.class.isAssignableFrom(itemClass)) {
            // 别名查询转换为实体
            if (hql.trim().substring(0, 6).toLowerCase().startsWith("select")) {
                Type[] types = query.getReturnTypes();
                if (types.length == 1) {
                    Type type = types[0];
                    if (itemClass.equals(type.getReturnedClass())) {
                    } else {
                        query.setResultTransformer(getTtemTransformerMap(itemClass, hql));
                    }
                } else {
                    query.setResultTransformer(getTtemTransformerMap(itemClass, hql));
                }
            }
        } else {
            query.setResultTransformer(getTtemTransformerMap(itemClass, hql));
        }

        return query.list();
    }

    private ResultTransformer getTtemTransformerMap(Class<?> itemClass, String queryLang) {
        String key = entityClass.getCanonicalName() + "_" + StringUtils.substringBefore(queryLang, "from");
        if (!itemTransformerMap.containsKey(key)) {
            itemTransformerMap.put(key, new AliasToBeanResultTransformer(itemClass));
        }
        return itemTransformerMap.get(key);
    }

    /**
     * @param bean
     */
    private Map<String, Object> convertMap(Object bean) {
        Map<String, Object> map = new HashMap<String, Object>();
        BeanWrapper wrapper = new BeanWrapperImpl(bean);
        PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            String propertyName = descriptor.getName();
            map.put(propertyName, wrapper.getPropertyValue(propertyName));
        }
        return map;
    }

    public String getNamedHql(String queryName, Map<String, Object> params) {
        SessionFactoryImpl factory = (SessionFactoryImpl) this.getSessionFactory();
        return NamedQueryScriptLoader.generateDynamicNamedQueryString(factory, queryName, params);

    }

    public Long count() {
        return (Long) getSession().createQuery(
                "select count(*) from " + entityClass.getCanonicalName()).uniqueResult();
    }

    public Date getSysDate() {
        SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor) this.getSessionFactory();
        String currentTimestampSelectString = sessionFactoryImplementor.getDialect().getCurrentTimestampSelectString();
        SQLQuery queryObject = getSession().createSQLQuery(currentTimestampSelectString);
        return (Date) queryObject.uniqueResult();
    }
}
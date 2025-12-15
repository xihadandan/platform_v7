package com.wellsoft.pt.jpa.hibernate;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.entity.BaseEntity;
import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.jpa.support.SupportMultiDatabaseConfigurationBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.*;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 封装Hibernate原生API的DAO泛型基类.
 * <p>
 * 可在Service层直接使用, 也可以扩展泛型DAO子类使用, 见两个构造函数的注释. 取消了HibernateTemplate,
 * 直接使用Hibernate原生API.
 *
 * @param <T>  DAO操作的对象类型
 * @param <PK> 主键类型
 * @author lilin
 */
@SuppressWarnings("unchecked")
@Component
public class SimpleHibernateDao<T, PK extends Serializable> {

    protected static String DATABASE_TYPE_SUFFIX = "";//命名查询前缀：特定数据库的查询
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected Class<T> entityClass;
    private SessionFactory sessionFactory;
    private String multiTenancyStrategy;

    // @Autowired
    // private SchemaBasedMultiTenancy schemaBasedMultiTenancy;
    // @Autowired
    // private SessionContextHolder sessionContextHolder;

    /**
     * 用于Dao层子类使用的构造函数. 通过子类的泛型定义取得对象类型Class. eg. public class UserDao extends
     * SimpleHibernateDao<User, Long>
     */
    public SimpleHibernateDao() {
        if (ClassUtils.isCglibProxyClass(getClass())) {
            this.entityClass = (Class<T>) Object.class;
        } else {
            this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
        }
    }

    @PostConstruct
    public void init() {
        if (Config.MULTI_TENANCY) {
            CommonEntity commonEntity = this.entityClass.getAnnotation(CommonEntity.class);
            if (commonEntity != null) {
                try {
                    setSessionFactory((SessionFactory) ApplicationContextHolder
                            .getBean(Config.COMMON_SESSION_FACTORY_BEAN_NAME));
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                    setSessionFactory((SessionFactory) ApplicationContextHolder
                            .getBean(Config.TENANT_SESSION_FACTORY_BEAN_NAME));
                }
            } else {
                setSessionFactory((SessionFactory) ApplicationContextHolder
                        .getBean(Config.TENANT_SESSION_FACTORY_BEAN_NAME));
                multiTenancyStrategy = Config.getValue(Config.KEY_MULTI_TENANCY_STRATEGY);
            }
        } else {
            setSessionFactory((SessionFactory) ApplicationContextHolder
                    .getBean(Config.TENANT_SESSION_FACTORY_BEAN_NAME));
        }

        //数据库类型判断
        DATABASE_TYPE_SUFFIX = SupportMultiDatabaseConfigurationBuilder.resolveDatabaseType(
                ((SessionFactoryImpl) sessionFactory).getDialect());

    }

    // /**
    // * 用于省略Dao层, 在Service层直接使用通用SimpleHibernateDao的构造函数.
    // * 在构造函数中定义对象类型Class.
    // * eg.
    // * SimpleHibernateDao<User, Long> userDao = new SimpleHibernateDao<User,
    // Long>(sessionFactory, User.class);
    // */
    // protected SimpleHibernateDao(final SessionFactory sessionFactory, final
    // Class<T> entityClass) {
    // this.sessionFactory = sessionFactory;
    // this.entityClass = entityClass;
    // }

    /**
     * 取得sessionFactory.
     */
    public SessionFactory getSessionFactory() {
        if (Config.MULTI_TENANCY_STRATEGY_SESSION_FACTORY.equals(multiTenancyStrategy)) {
            SessionFactory sf = SessionFactoryRegistrar.getCurrentTenantSessionFactory();
            if (sf != null) {
                return sf;
            }
        }
        return sessionFactory;
    }

    /**
     * 采用@Autowired按类型注入SessionFactory, 当有多个SesionFactory的时候在子类重载本函数.
     */
    protected void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 取得当前Session.
     */
    public Session getSession() {
        if (Config.MULTI_TENANCY_STRATEGY_SESSION_FACTORY.equals(multiTenancyStrategy)) {
            SessionFactory sf = SessionFactoryRegistrar.getCurrentTenantSessionFactory();
            if (sf != null) {
                return sf.getCurrentSession();
            }
        }
        return sessionFactory.getCurrentSession();

    }

    /**
     * 保存新增或修改的对象. 当uuid为"" 时 设置为null
     */
    public void save(final T entity) {
        Assert.notNull(entity, "entity不能为空");
        if (entity instanceof BaseEntity) {
            BaseEntity identity = (BaseEntity) entity;
            if (StringUtils.isBlank(identity.getUuid())) {
                identity.setUuid(null);
            }
        }
        getSession().saveOrUpdate(entity);
        logger.info("save entity: {}", entity);
    }

    /**
     * 保存全部
     *
     * @param entities
     */
    public void saveAll(final Collection<T> entities) {
        for (T entity : entities) {
            getSession().saveOrUpdate(entity);
        }
    }

    /**
     * @param entity
     */
    public void merge(final T entity) {
        getSession().merge(entity);
    }

    /**
     * 保存新增或修改的对象. 当uuid为"" 时 设置为null
     */
    public void persist(final T entity) {
        Assert.notNull(entity, "entity不能为空");
        getSession().persist(entity);
        logger.info("save entity: {}", entity);
    }

    /**
     * 删除对象.
     *
     * @param entity 对象必须是session中的对象或含uuid属性的transient对象.
     */
    public void delete(final T entity) {
        Assert.notNull(entity, "entity不能为空");
        getSession().delete(entity);
        logger.debug("delete entity: {}", entity);
    }

    /**
     * @param entities
     */
    public void deleteAll(final Collection<T> entities) {
        for (T entity : entities) {
            getSession().delete(entity);
        }
    }

    /**
     * 按uuid删除对象.
     */
    public void delete(final PK uuid) {
        Assert.notNull(uuid, "id不能为空");
        getSession().delete(get(uuid));
        logger.debug("delete entity {},uuid is {}", entityClass.getSimpleName(), uuid);
    }

    /**
     * @param uuid
     */
    public void deleteAllByPk(final Collection<PK> uuids) {
        for (PK uuid : uuids) {
            getSession().delete(get(uuid));
        }
    }

    /**
     * 按uuid获取对象.
     */
    public T get(final PK uuid) {
        Assert.notNull(uuid, "uuid不能为空");
        return (T) getSession().get(entityClass, uuid);
    }

    /**
     * 按id列表获取对象列表.
     */
    public List<T> get(final Collection<PK> uuids) {
        return find(Restrictions.in(getIdName(), uuids));
    }

    /**
     * 获取全部对象.
     */
    public List<T> getAll() {
        return find();
    }

    /**
     * 获取全部对象, 支持按属性行序.
     */
    public List<T> getAll(String orderByProperty, boolean isAsc) {
        Criteria c = createCriteria();
        if (isAsc) {
            c.addOrder(Order.asc(orderByProperty));
        } else {
            c.addOrder(Order.desc(orderByProperty));
        }
        return c.list();
    }

    /**
     * 按属性查找对象列表, 匹配方式为相等.
     */
    public List<T> findBy(final String propertyName, final Object value) {
        Assert.hasText(propertyName, "propertyName不能为空");
        Criterion criterion = Restrictions.eq(propertyName, value);
        return find(criterion);
    }

    /**
     * 按属性查找唯一对象, 匹配方式为相等.
     */
    public T findUniqueBy(final String propertyName, final Object value) {
        Assert.hasText(propertyName, "propertyName不能为空");
        Criterion criterion = Restrictions.eq(propertyName, value);
        return (T) createCriteria(criterion).uniqueResult();
    }

    /**
     * 按HQL查询对象列表.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    @Deprecated
    public <X> List<X> find(final String hql, final Object... values) {
        return createQuery(hql, values).list();
    }

    /**
     * 按HQL查询对象列表.
     *
     * @param values 命名参数,按名称绑定.
     */
    public <X> List<X> find(final String hql, final Map<String, ?> values) {
        return createQuery(hql, values).list();
    }

    /**
     * 按HQL查询唯一对象.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    @Deprecated
    public <X> X findUnique(final String hql, final Object... values) {
        return (X) createQuery(hql, values).uniqueResult();
    }

    /**
     * 按HQL查询唯一对象.
     *
     * @param values 命名参数,按名称绑定.
     */
    public <X> X findUnique(final String hql, final Map<String, ?> values) {
        return (X) createQuery(hql, values).uniqueResult();
    }

    /**
     * 执行HQL进行批量修改/删除操作.
     *
     * @param values 数量可变的参数,按顺序绑定.
     * @return 更新记录数.
     */
    @Deprecated
    public int batchExecute(final String hql, final Object... values) {
        return createQuery(hql, values).executeUpdate();
    }

    /**
     * 执行HQL进行批量修改/删除操作.
     *
     * @param values 命名参数,按名称绑定.
     * @return 更新记录数.
     */
    public int batchExecute(final String hql, final Map<String, ?> values) {
        return createQuery(hql, values).executeUpdate();
    }

    /**
     * 根据查询HQL与参数列表创建Query对象. 与find()函数可进行更加灵活的操作.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    @Deprecated
    public Query createQuery(final String queryString, final Object... values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = getSession().createQuery(queryString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
        return query;
    }

    /**
     * 根据查询HQL与参数列表创建Query对象. 与find()函数可进行更加灵活的操作.
     *
     * @param values 命名参数,按名称绑定.
     */
    public Query createQuery(final String queryString, final Map<String, ?> values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = getSession().createQuery(queryString);
         if (values != null) {
            query.setProperties(values);
        }
        return query;
    }

    /**
     * 按Criteria查询对象列表.
     *
     * @param criterions 数量可变的Criterion.
     */
    public List<T> find(final Criterion... criterions) {
        return createCriteria(criterions).list();
    }

    /**
     * 按Criteria查询唯一对象.
     *
     * @param criterions 数量可变的Criterion.
     */
    public T findUnique(final Criterion... criterions) {
        return (T) createCriteria(criterions).uniqueResult();
    }

    /**
     * 根据Criterion条件创建Criteria. 与find()函数可进行更加灵活的操作.
     *
     * @param criterions 数量可变的Criterion.
     */
    public Criteria createCriteria(final Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(entityClass);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    /**
     * 初始化对象. 使用load()方法得到的仅是对象Proxy, 在传到View层前需要进行初始化. 如果传入entity,
     * 则只初始化entity的直接属性,但不会初始化延迟加载的关联集合和属性. 如需初始化关联属性,需执行:
     * Hibernate.initialize(user.getRoles())，初始化User的直接属性和关联集合.
     * Hibernate.initialize
     * (user.getDescription())，初始化User的直接属性和延迟加载的Description属性.
     */
    public void initProxyObject(Object proxy) {
        Hibernate.initialize(proxy);
    }

    /**
     * Flush当前Session.
     */
    public void flush() {
        getSession().flush();
    }

    /**
     * 为Query添加distinct transformer. 预加载关联对象的HQL会引起主对象重复, 需要进行distinct处理.
     */
    public Query distinct(Query query) {
        query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return query;
    }

    /**
     * 为Criteria添加distinct transformer. 预加载关联对象的HQL会引起主对象重复, 需要进行distinct处理.
     */
    public Criteria distinct(Criteria criteria) {
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria;
    }

    /**
     * 取得对象的主键名.
     */
    public String getIdName() {
        ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
        return meta.getIdentifierPropertyName();
    }

    /**
     * 判断对象的属性值在数据库内是否唯一.
     * <p>
     * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
     */
    public boolean isPropertyUnique(final String propertyName, final Object newValue,
                                    final Object oldValue) {
        if (newValue == null || newValue.equals(oldValue)) {
            return true;
        }
        Object object = findUniqueBy(propertyName, newValue);
        return (object == null);
    }
}
package com.wellsoft.pt.jpa.hibernate4;

import com.alibaba.druid.pool.DruidDataSource;
import com.wellsoft.context.util.PropertiesUtils;
import com.wellsoft.pt.jpa.dao.SessionOperationHibernateDao;
import com.wellsoft.pt.jpa.support.CommonEntityAnnotatedClassesFactoryBean;
import com.wellsoft.pt.jpa.support.HibernateMappingLocationsFactoryBean;
import com.wellsoft.pt.jpa.support.SupportMultiDatabaseLocalSessionFactoryBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Description:代码动态构建hibernateSessionFactory
 *
 * @author chenq
 * @date 2019/7/24
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/24    chenq		2019/7/24		Create
 * 6.2          liuxj		2022/1/6		去除分布式事务，改成本地事务
 * </pre>
 */
public class DynamicHibernateSessionFactoryRegistry {

    public final static String URL = "url";
    public final static String DYNAMIC_TRANSACTIONMANAGER_PREFIX = "DynamicTransactionManager_";
    private final static Logger LOGGER = LoggerFactory.getLogger(
            DynamicHibernateSessionFactoryRegistry.class);
    private final static ReentrantLock lock = new ReentrantLock();
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String HIBERNATEPROPERTIES = "hibernateProperties";

    //===========druid数据源配置====================
    /**
     * 配置初始化大小、最小、最大
     */
    @Value("${dynamic.jdbc.initSize:5}")
    private static int jdbcInitSize = 5;

    @Value("${dynamic.jdbc.minIdel:5}")
    private static int minIdel = 5;

    @Value("${dynamic.jdbc.maxActive:50}")
    private static int maxActive = 50;

    /**
     * 配置获取连接等待超时的时间
     */
    @Value("${dynamic.jdbc.maxWait:60000}")
    private static long maxWait = 60000;
    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    @Value("${dynamic.jdbc.timeBetweenEvictionRunsMillis:60000}")
    private static long timeBetweenEvictionRunsMillis = 60000;
    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    @Value("${dynamic.jdbc.minEvictableIdleTimeMillis:300000}")
    private static long minEvictableIdleTimeMillis = 300000;

    @Value("${dynamic.jdbc.validationQuery: select 1 from dual }")
    private static String validationQuery;

    @Value("${dynamic.jdbc.testOnBorrow:false}")
    private static boolean testOnBorrow = false;

    @Value("${dynamic.jdbc.testOnReturn:false}")
    private static boolean testOnReturn = false;

    @Value("${dynamic.jdbc.testWhileIdle:true}")
    private static boolean testWhileIdle = true;

    @Value("${dynamic.jdbc.poolPreparedStatements:true}")
    private static boolean poolPreparedStatements = true;

    @Value("${dynamic.jdbc.maxPoolPreparedStatementPerConnectionSize:50}")
    private static int maxPoolPreparedStatementPerConnectionSize = 50;


    /**
     * 读取配置文件，如果没有配使用上面的默认配置
     */
    static {
        try {
            Properties properties = PropertiesUtils.loadProperties("classpath:system-jdbc.properties");
            String initSize = properties.getProperty("dynamic.jdbc.initSize");
            if (!StringUtils.isEmpty(initSize)) {
                jdbcInitSize = Integer.parseInt(initSize);
            }
            String strMinIdel = properties.getProperty("dynamic.jdbc.minIdel");
            if (!StringUtils.isEmpty(strMinIdel)) {
                minIdel = Integer.parseInt(strMinIdel);
            }

            String strMaxActive = properties.getProperty("dynamic.jdbc.maxActive");
            if (!StringUtils.isEmpty(strMaxActive)) {
                maxActive = Integer.parseInt(strMaxActive);
            }

        } catch (IOException e) {
            LOGGER.error("动态数据加载配置文件system-jdbc.properties出错", e);
        }

    }

    public static String registry(ConfigurableListableBeanFactory configurableListableBeanFactory,
                                  Map<String, Object> jdbcPropertyValues) {
        String sessionFactoryId = null;
        try {
            lock.lock();
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
            String url = jdbcPropertyValues.get(URL).toString();
            String user = jdbcPropertyValues.get(USERNAME).toString();
            String password = jdbcPropertyValues.get(PASSWORD).toString();

            //构建数据源
            String dataSourceId = buildDataSource(defaultListableBeanFactory, url, user, password,
                    jdbcPropertyValues);

            sessionFactoryId = "DynamicSessionFactoryOf" + dataSourceId;

            if (defaultListableBeanFactory.containsBean(sessionFactoryId)) {
                //已经存在的动态数据源，获取其绑定的sessionFactory，重新构造一个新的sessionFactory
                SupportMultiDatabaseLocalSessionFactoryBean existSessionFactoryBean = defaultListableBeanFactory.getBean(
                        "&" + sessionFactoryId, SupportMultiDatabaseLocalSessionFactoryBean.class);
                Configuration configuration = existSessionFactoryBean.getConfiguration();
                configuration.addXML(jdbcPropertyValues.get("mappingXML").toString());
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                        configuration.getProperties()).build();
                SessionFactory newSessionFactory = configuration.buildSessionFactory(
                        serviceRegistry);
                sessionFactoryId = DigestUtils.md5Hex(url + user + System.currentTimeMillis());
                defaultListableBeanFactory.registerSingleton(sessionFactoryId, newSessionFactory);
                buildSessionDao(defaultListableBeanFactory, sessionFactoryId);
                return sessionFactoryId;
            }

            //构建session工厂
            BeanDefinitionBuilder localSessionFactoryBean = BeanDefinitionBuilder.genericBeanDefinition(
                    SupportMultiDatabaseLocalSessionFactoryBean.class);
            localSessionFactoryBean.addPropertyReference("dataSource", dataSourceId);
            localSessionFactoryBean.addPropertyValue("namingStrategy",
                    new CustomeImprovedNamingStrategy());
            if (jdbcPropertyValues.get("mappingXML") != null) {
                //动态的xml映射
                localSessionFactoryBean.addPropertyValue("mappingXMLs",
                        new String[]{jdbcPropertyValues.get("mappingXML").toString()});

            }

            if (!jdbcPropertyValues.containsKey("ignnoreEntity")) {
                try {
                    //实体类注解
                    CommonEntityAnnotatedClassesFactoryBean annotatedClassesFactoryBean = new CommonEntityAnnotatedClassesFactoryBean();
                    annotatedClassesFactoryBean.setPackagesToScan("com.wellsoft.***.**");
                    localSessionFactoryBean.addPropertyValue("annotatedClasses",
                            annotatedClassesFactoryBean.getObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!jdbcPropertyValues.containsKey("ignnoreMapping")) {
                try {
                    //mapping路径
                    HibernateMappingLocationsFactoryBean mappingLocations = new HibernateMappingLocationsFactoryBean();
                    mappingLocations.setIgnoreUrlResource(true);
                    localSessionFactoryBean.addPropertyValue("mappingLocations",
                            mappingLocations.getObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //hibernate 属性
            localSessionFactoryBean.addPropertyValue("hibernateProperties",
                    hibernateProperties(jdbcPropertyValues,
                            "jdbc/" + dataSourceId));
            localSessionFactoryBean.setScope(SCOPE_SINGLETON);
            defaultListableBeanFactory.registerBeanDefinition(sessionFactoryId,
                    localSessionFactoryBean.getRawBeanDefinition());
            buildTransactionManager(defaultListableBeanFactory, sessionFactoryId);
            buildSessionDao(defaultListableBeanFactory, sessionFactoryId);


        } catch (Exception e) {
            LOGGER.error("代码级构建hibernate sessionFactory异常：", e);
        } finally {
            lock.unlock();
        }
        return sessionFactoryId;
    }

    private static void buildSessionDao(
            DefaultListableBeanFactory defaultListableBeanFactory,
            String sessionFactoryId) {
        BeanDefinitionBuilder daoBean = BeanDefinitionBuilder.genericBeanDefinition(
                SessionOperationHibernateDao.class);
        daoBean.addPropertyReference("sessionFactory", sessionFactoryId);
        daoBean.setScope(SCOPE_SINGLETON);
        defaultListableBeanFactory.registerBeanDefinition("DynamicDAO_" + sessionFactoryId,
                daoBean.getRawBeanDefinition());

    }

    /**
     * 动态构建事务管理器
     *
     * @param defaultListableBeanFactory
     * @param sessionFactoryId
     */
    private static void buildTransactionManager(
            DefaultListableBeanFactory defaultListableBeanFactory,
            String sessionFactoryId) {
        BeanDefinitionBuilder transactionManagerBean = BeanDefinitionBuilder.genericBeanDefinition(
                HibernateTransactionManager.class);
        transactionManagerBean.addPropertyReference("sessionFactory", sessionFactoryId);
        transactionManagerBean.setScope(SCOPE_SINGLETON);
        defaultListableBeanFactory.registerBeanDefinition(DYNAMIC_TRANSACTIONMANAGER_PREFIX + sessionFactoryId,
                transactionManagerBean.getRawBeanDefinition());

    }


    /**
     * 构建数据
     *
     * @param defaultListableBeanFactory bean工厂
     * @param url                        数据库连接url
     * @param user                       用户名
     * @param password                   密码
     * @param dsProperties               属性配置
     * @return
     */
    public synchronized static String buildDataSource(
            DefaultListableBeanFactory defaultListableBeanFactory,
            String url, String user, String password, Map<String, Object> dsProperties) {
        String dataSourceUniqId = "DynamicDataSource_" + DigestUtils.md5Hex(
                url + user + System.currentTimeMillis());
        if (defaultListableBeanFactory.containsBean(dataSourceUniqId)) {
            return dataSourceUniqId;//已经构建过的数据源，不重新使用
        }
        //构建数据源
        BeanDefinitionBuilder druidDataSourceFactoryBean = BeanDefinitionBuilder.genericBeanDefinition(
                DruidDataSource.class);
        druidDataSourceFactoryBean.setScope(SCOPE_SINGLETON);
        druidDataSourceFactoryBean.addPropertyValue("url", url);
        druidDataSourceFactoryBean.addPropertyValue("username", user);
        druidDataSourceFactoryBean.addPropertyValue("password", password);
        druidDataSourceFactoryBean.addPropertyValue("initialSize", jdbcInitSize);
        druidDataSourceFactoryBean.addPropertyValue("minIdle", minIdel);
        druidDataSourceFactoryBean.addPropertyValue("maxActive", maxActive);
        druidDataSourceFactoryBean.addPropertyValue("maxWait", maxWait);
        druidDataSourceFactoryBean.addPropertyValue("timeBetweenEvictionRunsMillis", timeBetweenEvictionRunsMillis);
        druidDataSourceFactoryBean.addPropertyValue("minEvictableIdleTimeMillis", minEvictableIdleTimeMillis);
        druidDataSourceFactoryBean.addPropertyValue("testOnBorrow", testOnBorrow);
        druidDataSourceFactoryBean.addPropertyValue("testOnReturn", testOnReturn);
        druidDataSourceFactoryBean.addPropertyValue("testWhileIdle", testWhileIdle);
        druidDataSourceFactoryBean.addPropertyValue("poolPreparedStatements", poolPreparedStatements);
        druidDataSourceFactoryBean.addPropertyValue("maxPoolPreparedStatementPerConnectionSize", maxPoolPreparedStatementPerConnectionSize);
        defaultListableBeanFactory.registerBeanDefinition(dataSourceUniqId,
                druidDataSourceFactoryBean.getRawBeanDefinition());
        return dataSourceUniqId;
    }


    private static Properties hibernateProperties(Map<String, Object> jdbcPropertyValues,
                                                  String jdbcUniqName) {
        Properties properties = new Properties();
        properties.put("hibernate.jdbc.batch_size", "20");
        properties.put("hibernate.jdbc.fetch_size", "50");
        properties.put("hibernate.cache.use_second_level_cache", "false");
        properties.put("hibernate.cache.use_query_cache", "false");
        properties.put("hibernate.connection.release_mode", "after_statement");
        properties.put("hibernate.current_session_context_class",
                "com.wellsoft.pt.jpa.hibernate4.LocalSpringSessionContext");
        properties.put("hibernate.enable_lazy_load_no_trans", true);
        //属性覆盖
        Map<String, Object> hibernateProperties = (Map<String, Object>) jdbcPropertyValues.get(
                HIBERNATEPROPERTIES);
        if (MapUtils.isNotEmpty(hibernateProperties)) {
            properties.putAll(hibernateProperties);
        }
        return properties;
    }


}

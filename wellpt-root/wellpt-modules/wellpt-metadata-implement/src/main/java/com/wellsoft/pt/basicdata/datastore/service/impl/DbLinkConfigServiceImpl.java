package com.wellsoft.pt.basicdata.datastore.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datasource.factory.MongoFileJarClassLoader;
import com.wellsoft.pt.basicdata.datastore.dao.DbLinkConfigDao;
import com.wellsoft.pt.basicdata.datastore.entity.DbLinkConfigEntity;
import com.wellsoft.pt.basicdata.datastore.service.DbLinkConfigService;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.datasource.DataSourceContextHolder;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.support.CustomDm7DBDialect;
import com.wellsoft.pt.jpa.support.CustomMySQL5InnoDBDialect;
import com.wellsoft.pt.jpa.support.CustomOracle10gDialect;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Kingbase8Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.springframework.beans.BeanUtils;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月10日   chenq	 Create
 * </pre>
 */
@Service
public class DbLinkConfigServiceImpl extends AbstractJpaServiceImpl<DbLinkConfigEntity, DbLinkConfigDao, Long> implements DbLinkConfigService {


    Map<Long, SessionFactory> sessionFactoryMap = Maps.newConcurrentMap();

    @Override
    @Transactional
    public DbLinkConfigEntity saveDbLinkConfig(DbLinkConfigEntity temp) {
        DbLinkConfigEntity entity = temp.getUuid() == null ? new DbLinkConfigEntity() : getOne(temp.getUuid());
        if (entity.getUuid() == null) {
            BeanUtils.copyProperties(temp, entity);
            entity.setSystem(RequestSystemContextPathResolver.system());
            entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        } else {
            BeanUtils.copyProperties(temp, entity, ArrayUtils.addAll(entity.BASE_FIELDS, "system", "tenant"));
        }
        save(entity);
        sessionFactoryMap.remove(entity.getUuid());
        // FIXME: 是否提前创建
//        this.createLocalSessionFactory(entity.getUuid());
        return entity;
    }

    public static class MyDialectResolver implements DialectResolver {
        @Override
        public Dialect resolveDialect(DialectResolutionInfo dialectResolutionInfo) {
            return new CustomOracle10gDialect();
        }
    }

    @Override
    public NativeDao getSwitchNativeDao(NativeDao currentNativeDao) {
        NativeDao nativeDao = currentNativeDao;
        //需要切换数据源
        if (StrUtil.isNotBlank(DataSourceContextHolder.getDataSource())) {
            // 通过数据库连接创建
            SessionFactory sessionFactory = createLocalSessionFactory(Convert.toLong(DataSourceContextHolder.getDataSource()));
            nativeDao = ApplicationContextHolder.getBean(NativeDao.class);
            nativeDao.setSessionFactory(sessionFactory, null);
        }
        return nativeDao;
    }


    @Override
    public SessionFactory createLocalSessionFactory(Long dbLinkConfUuid) {
        if (sessionFactoryMap.containsKey(dbLinkConfUuid)) {
            return sessionFactoryMap.get(dbLinkConfUuid);
        }
        DbLinkConfigEntity entity = getOne(dbLinkConfUuid);
        if (entity != null && entity.isSQLDatabase()) {
            DataSource dataSource = buildSQLDataSource(entity);
            LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource);
            Properties hibernateProps = new Properties();
//            hibernateProps.put(Environment.DIALECT_RESOLVERS, MyDialectResolver.class.getName());
            if (DbLinkConfigEntity.DbType.dameng.name().equals(entity.getDbType())) {
                hibernateProps.put(Environment.DIALECT, CustomDm7DBDialect.class.getName());
            } else if (DbLinkConfigEntity.DbType.kingbase.name().equals(entity.getDbType())) {
                hibernateProps.put(Environment.DIALECT, Kingbase8Dialect.class.getName());
            } else if (DbLinkConfigEntity.DbType.mysql.name().equals(entity.getDbType())) {
                hibernateProps.put(Environment.DIALECT, CustomMySQL5InnoDBDialect.class.getName());

            } else {
                hibernateProps.put(Environment.DIALECT, CustomOracle10gDialect.class.getName());

            }
            if (MapUtils.isNotEmpty(entity.getParamProperties())) {
                for (Object key : entity.getParamProperties().keySet()) {
                    if (key.toString().startsWith("hibernate.")) {
                        hibernateProps.put(key, entity.getParamProperties().getProperty(key.toString()));
                    }
                }
            }
            if (entity.getDbType().equalsIgnoreCase(DbLinkConfigEntity.DbType.mysql.name())) {
                if (entity.getLinkType().equalsIgnoreCase(DbLinkConfigEntity.LinkType.host.name())) {
                    hibernateProps.put("tableSchema", entity.getSname());
                } else {
                    int flag = entity.getUrl().indexOf("?");
                    if (flag != -1) {
                        hibernateProps.put("tableSchema", entity.getUrl().substring(entity.getUrl().lastIndexOf("/") + 1, flag));
                    } else {
                        hibernateProps.put("tableSchema", entity.getUrl().substring(entity.getUrl().lastIndexOf("/") + 1));
                    }
                }

            }
            builder.addProperties(hibernateProps);
            SessionFactory sessionFactory = builder.buildSessionFactory();
            sessionFactoryMap.put(dbLinkConfUuid, sessionFactory);
            return sessionFactory;
        }
        return null;
    }

    @Override
    public DataSource buildSQLDataSource(Long dbLinkConfUuid) {
        DbLinkConfigEntity entity = getOne(dbLinkConfUuid);
        if (entity != null && entity.isSQLDatabase()) {
            DataSource dataSource = buildSQLDataSource(entity);
            return dataSource;
        }
        return null;
    }

    private DataSource buildSQLDataSource(DbLinkConfigEntity entity) {
        DruidDataSource dataSource = new DruidDataSource();
        if (StringUtils.isNotBlank(entity.getUrl()) && DbLinkConfigEntity.LinkType.url.name().equals(entity.getLinkType())) {
            dataSource.setUrl(entity.getUrl());
        } else {
            if (DbLinkConfigEntity.DbType.oracle.name().equals(entity.getDbType())) {
                dataSource.setUrl(
                        DbLinkConfigEntity.LinkType.host.name().equals(entity.getLinkType()) ?
                                (DbLinkConfigEntity.ConnectStype.sid.name().equals(entity.getConnectStype()) ?
                                        new StringBuilder("jdbc:oracle:thin:@").append(entity.getHost()).append(":").append(entity.getPort()).append(":").append(entity.getSid()).toString() :
                                        new StringBuilder("jdbc:oracle:thin:@//").append(entity.getHost()).append(":").append(entity.getPort()).append("/").append(entity.getSname()).toString()) :
                                entity.getUrl());
            } else if (DbLinkConfigEntity.DbType.mysql.name().equals(entity.getDbType())) {
                dataSource.setUrl(
                        DbLinkConfigEntity.LinkType.host.name().equals(entity.getLinkType()) ?
                                new StringBuilder("jdbc:mysql://").append(entity.getHost()).append(":").append(entity.getPort()).append("/").append(entity.getSname()).toString()
                                : entity.getUrl()
                );
            } else if (DbLinkConfigEntity.DbType.dameng.name().equals(entity.getDbType())) {
                dataSource.setUrl(
                        DbLinkConfigEntity.LinkType.host.name().equals(entity.getLinkType()) ?
                                new StringBuilder("jdbc:dm://").append(entity.getHost()).append(":").append(entity.getPort()).append("/").append(entity.getSname()).toString() : entity.getUrl());
            }
        }
        dataSource.setUsername(entity.getUserName());
        dataSource.setPassword(entity.getPassword());
        dataSource.setMaxWait(10000);
        dataSource.setConnectionErrorRetryAttempts(5);
        dataSource.setName(entity.getName());
        dataSource.setLoginTimeout(10);
        dataSource.setFailFast(true);
        Properties properties = entity.getParamProperties();
        if (MapUtils.isNotEmpty(properties)) {
            try {
                dataSource.setConnectProperties(properties);
            } catch (Exception e) {
                logger.error("数据源连接加载配置异常: ", e);
            }
        }
        if (StringUtils.isNotBlank(entity.getDriverClass())) {
            dataSource.setDriverClassName(entity.getDriverClass());
        }
        if (StringUtils.isNotBlank(entity.getDriverJarFile())) {
            try {
                dataSource.setDriverClassLoader(MongoFileJarClassLoader.load(entity.getDriverJarFile()));
            } catch (Exception e) {
                logger.error("驱动程序加载异常: ", e);
            }
        }
        if (StringUtils.isNotBlank(entity.getDriverClass())) {
            dataSource.setDriverClassName(entity.getDriverClass());
        }

        return dataSource;
    }

    @Override
    public List<DbLinkConfigEntity> getDbLinksBySystem(String system) {
        DbLinkConfigEntity example = new DbLinkConfigEntity();
        if (StringUtils.isNotBlank(system)) {
            example.setSystem(system);
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        return dao.listByEntity(example);
    }

    @Override
    public DatabaseMetaData testConnection(DbLinkConfigEntity entity) {
        DruidDataSource dataSource = (DruidDataSource) buildSQLDataSource(entity);
        try {
            dataSource.setBreakAfterAcquireFailure(true);
            dataSource.setConnectionErrorRetryAttempts(0);
            Connection connection = dataSource.getConnection();
            return connection.getMetaData();
        } catch (Exception e) {
            logger.error("测试连接异常", e);
            throw new RuntimeException(e);
        }
    }


}

/*
 * @(#)2012-10-22 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-22.1	lilin		2012-10-22		Create
 * </pre>
 * @date 2012-10-22
 */
public class SchemaBasedMultiTenancy {
    private static final TypeFilter[] ENTITY_TYPE_FILTERS = new TypeFilter[]{
            new AnnotationTypeFilter(Entity.class, false), new AnnotationTypeFilter(Embeddable.class, false),
            new AnnotationTypeFilter(MappedSuperclass.class, false)};
    private static final Method addAnnotatedClassMethod = ClassUtils.getMethod(Configuration.class,
            "addAnnotatedClass", Class.class);
    protected SessionFactoryImplementor sessionFactory;

    //	private DriverManagerConnectionProviderImpl acmeProvider;
    //	private DriverManagerConnectionProviderImpl jbossProvider;
    //	private DriverManagerConnectionProviderImpl oaProvider;

    //	private static SchemaBasedMultiTenancy schemaBasedMultiTenancy = new SchemaBasedMultiTenancy();
    private ServiceRegistryImplementor serviceRegistry;

    public SchemaBasedMultiTenancy() {
        //		AbstractMultiTenantConnectionProvider multiTenantConnectionProvider = buildMultiTenantConnectionProvider();
        //		Configuration cfg = buildConfiguration();
        //
        //		serviceRegistry = (ServiceRegistryImplementor) new ServiceRegistryBuilder().applySettings(cfg.getProperties())
        //				.addService(MultiTenantConnectionProvider.class, multiTenantConnectionProvider).buildServiceRegistry();
        //
        //		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory(serviceRegistry);
    }

    //	public static SchemaBasedMultiTenancy getInstance() {
    //		return schemaBasedMultiTenancy;
    //	}

    @PostConstruct
    public void init() {
        AbstractMultiTenantConnectionProvider multiTenantConnectionProvider = buildMultiTenantConnectionProvider();
        Configuration cfg = buildConfiguration();

        serviceRegistry = (ServiceRegistryImplementor) new ServiceRegistryBuilder().applySettings(cfg.getProperties())
                .addService(MultiTenantConnectionProvider.class, multiTenantConnectionProvider).build();

        sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory(serviceRegistry);
    }

    protected Configuration buildConfiguration() {
        Configuration cfg = new Configuration();
        cfg.getProperties().put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        //		cfg.setProperty(Environment.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName());
        cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
        cfg.setProperty(Environment.USE_QUERY_CACHE, "true");
        cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
        cfg.setProperty(Environment.CACHE_REGION_FACTORY, "org.hibernate.cache.ehcache.EhCacheRegionFactory");

        //		cfg.addAnnotatedClass( Customer.class );
        //设置map模式
        //		cfg.setProperty(Environment.DEFAULT_ENTITY_MODE, EntityMode.MAP.toString());
        cfg.setNamingStrategy(new ImprovedNamingStrategy());

        scanPackages(cfg, "com.wellsoft");

        //		cfg.addPackage("com.wellsoft");
        //		cfg.addAnnotatedClass(IdEntity.class);
        //		cfg.addAnnotatedClass(User.class);
        //		cfg.addAnnotatedClass(UserDetail.class);
        //		cfg.addAnnotatedClass(Role.class);
        //		cfg.addAnnotatedClass(Permission.class);
        //		cfg.addAnnotatedClass(Resource.class);
        //		cfg.addAnnotatedClass(UserProperty.class);
        cfg.setProperty(Environment.DIALECT, "org.hibernate.dialect.SQLServer2008Dialect");
        cfg.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        //缓存配置
        cfg.setProperty(Environment.USE_QUERY_CACHE, "true");
        cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
        cfg.setProperty(Environment.CACHE_REGION_FACTORY, "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        cfg.buildMappings();

        //		RootClass meta = (RootClass) cfg.getClassMapping( Customer.class.getName() );
        //		meta.setCacheConcurrencyStrategy( "read-write" );

        return cfg;
    }

    private AbstractMultiTenantConnectionProvider buildMultiTenantConnectionProvider() {
        //		acmeProvider = ConnectionProviderBuilder.buildConnectionProvider("acme");
        //		jbossProvider = ConnectionProviderBuilder.buildConnectionProvider("jboss");
        //		oaProvider = ConnectionProviderBuilder.buildConnectionProvider("oa");
        return new AbstractMultiTenantConnectionProvider() {
            @Override
            protected ConnectionProvider getAnyConnectionProvider() {
                return ConnectionProviderBuilder.buildConnectionProvider();
            }

            @Override
            protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
                return ConnectionProviderBuilder.buildConnectionProvider(tenantIdentifier);

                //				throw new HibernateException("Unknown tenant identifier");
            }
        };
    }

    /**
     * @return the sessionFactory
     */
    public SessionFactoryImplementor getSessionFactory() {
        return sessionFactory;
    }

    /**
     * @param sessionFactory 要设置的sessionFactory
     */
    public void setSessionFactory(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getNewSession() {
        return sessionFactory.withOptions().openSession();
    }

    public Session getNewSession(String tenant) {
        sessionFactory.getCurrentSession();
        return sessionFactory.withOptions().tenantIdentifier(tenant).openSession();
    }

    public void scanPackages(Configuration cfg, String... packagesToScan) throws HibernateException {
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils
                .getResourcePatternResolver(new PathMatchingResourcePatternResolver());
        String RESOURCE_PATTERN = "/**/*.class";
        try {
            for (String pkg : packagesToScan) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
                Resource[] resources = resourcePatternResolver.getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        String className = reader.getClassMetadata().getClassName();
                        if (matchesFilter(reader, readerFactory)) {
                            addAnnotatedClasses(cfg, resourcePatternResolver.getClassLoader().loadClass(className));
                        }
                    }
                }
            }

        } catch (IOException ex) {
            throw new MappingException("Failed to scan classpath for unlisted classes", ex);
        } catch (ClassNotFoundException ex) {
            throw new MappingException("Failed to load annotated classes from classpath", ex);
        }
    }

    private boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        for (TypeFilter filter : ENTITY_TYPE_FILTERS) {
            if (filter.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }

    public void addAnnotatedClasses(Configuration cfg, Class<?>... annotatedClasses) {
        for (Class<?> annotatedClass : annotatedClasses) {
            //			ReflectionUtils.invokeMethod(addAnnotatedClassMethod, this, annotatedClass);
            cfg.addAnnotatedClass(annotatedClass);
        }
    }
}

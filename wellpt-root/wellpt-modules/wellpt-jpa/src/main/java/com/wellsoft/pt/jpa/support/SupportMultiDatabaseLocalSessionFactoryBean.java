package com.wellsoft.pt.jpa.support;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/3/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/3/7    chenq		2019/3/7		Create
 * </pre>
 */
public class SupportMultiDatabaseLocalSessionFactoryBean extends
        HibernateExceptionTranslator implements FactoryBean<SessionFactory>, ResourceLoaderAware,
        InitializingBean, DisposableBean {


    private DataSource dataSource;
    private Resource[] configLocations;
    private String[] mappingResources;
    private Resource[] mappingLocations;
    private Resource[] cacheableMappingLocations;
    private Resource[] mappingJarLocations;
    private Resource[] mappingDirectoryLocations;
    private Interceptor entityInterceptor;
    private NamingStrategy namingStrategy;
    private Properties hibernateProperties;
    private Class<?>[] annotatedClasses;
    private String[] annotatedPackages;
    private String[] packagesToScan;
    private Object jtaTransactionManager;
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private Configuration configuration;
    private SessionFactory sessionFactory;

    public SupportMultiDatabaseLocalSessionFactoryBean() {
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setConfigLocation(Resource configLocation) {
        this.configLocations = new Resource[]{configLocation};
    }

    public void setConfigLocations(Resource[] configLocations) {
        this.configLocations = configLocations;
    }

    public void setMappingResources(String[] mappingResources) {
        this.mappingResources = mappingResources;
    }

    public void setMappingLocations(Resource[] mappingLocations) {
        this.mappingLocations = mappingLocations;
    }

    public void setCacheableMappingLocations(Resource[] cacheableMappingLocations) {
        this.cacheableMappingLocations = cacheableMappingLocations;
    }

    public void setMappingJarLocations(Resource[] mappingJarLocations) {
        this.mappingJarLocations = mappingJarLocations;
    }

    public void setMappingDirectoryLocations(Resource[] mappingDirectoryLocations) {
        this.mappingDirectoryLocations = mappingDirectoryLocations;
    }

    public void setEntityInterceptor(Interceptor entityInterceptor) {
        this.entityInterceptor = entityInterceptor;
    }

    public void setNamingStrategy(NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public Properties getHibernateProperties() {
        if (this.hibernateProperties == null) {
            this.hibernateProperties = new Properties();
        }

        return this.hibernateProperties;
    }

    public void setHibernateProperties(Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

    public void setAnnotatedClasses(Class<?>[] annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    public void setAnnotatedPackages(String[] annotatedPackages) {
        this.annotatedPackages = annotatedPackages;
    }

    public void setPackagesToScan(String... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setJtaTransactionManager(Object jtaTransactionManager) {
        this.jtaTransactionManager = jtaTransactionManager;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(
                resourceLoader);
    }

    public void afterPropertiesSet() throws IOException {
        SupportMultiDatabaseConfigurationBuilder sfb = new SupportMultiDatabaseConfigurationBuilder(
                this.dataSource,
                this.resourcePatternResolver);
        Resource[] arr = null;
        int len = 0;
        int i = 0;
        Resource resource = null;
        if (this.configLocations != null) {
            arr = this.configLocations;
            len = arr.length;

            for (i = 0; i < len; ++i) {
                resource = arr[i];
                sfb.configure(resource.getURL());
            }
        }

        if (this.mappingResources != null) {
            String[] arrStr = this.mappingResources;
            len = arrStr.length;

            for (i = 0; i < len; ++i) {
                String mapping = arrStr[i];
                Resource mr = new ClassPathResource(mapping.trim(),
                        this.resourcePatternResolver.getClassLoader());
                sfb.addInputStream(mr.getInputStream());
            }
        }

        if (this.mappingLocations != null) {
            arr = this.mappingLocations;
            len = arr.length;

            for (i = 0; i < len; ++i) {
                resource = arr[i];
                sfb.currentResource(resource);
                sfb.addInputStream(resource.getInputStream());
            }
        }

        if (this.cacheableMappingLocations != null) {
            arr = this.cacheableMappingLocations;
            len = arr.length;

            for (i = 0; i < len; ++i) {
                resource = arr[i];
                sfb.addCacheableFile(resource.getFile());
            }
        }

        if (this.mappingJarLocations != null) {
            arr = this.mappingJarLocations;
            len = arr.length;

            for (i = 0; i < len; ++i) {
                resource = arr[i];
                sfb.addJar(resource.getFile());
            }
        }

        if (this.mappingDirectoryLocations != null) {
            arr = this.mappingDirectoryLocations;
            len = arr.length;

            for (i = 0; i < len; ++i) {
                resource = arr[i];
                File file = resource.getFile();
                if (!file.isDirectory()) {
                    throw new IllegalArgumentException(
                            "Mapping directory location [" + resource + "] does not denote a directory");
                }

                sfb.addDirectory(file);
            }
        }

        if (this.entityInterceptor != null) {
            sfb.setInterceptor(this.entityInterceptor);
        }

        if (this.namingStrategy != null) {
            sfb.setNamingStrategy(this.namingStrategy);
        }

        if (this.hibernateProperties != null) {
            sfb.addProperties(this.hibernateProperties);
        }

        if (this.annotatedClasses != null) {
            sfb.addAnnotatedClasses(this.annotatedClasses);
        }

        if (this.annotatedPackages != null) {
            sfb.addPackages(this.annotatedPackages);
        }

        if (this.packagesToScan != null) {
            sfb.scanPackages(this.packagesToScan);
        }

 /*       if (this.jtaTransactionManager != null) {
            sfb.setJtaTransactionManager(this.jtaTransactionManager);
        }*/

        this.configuration = sfb;
        /*if (this.dataSource instanceof DruidDataSource) {
            this.configuration.setInterceptor(new TenantSchemaInterceptor(((DruidDataSource) this.dataSource).getUsername()));
        }*/
        this.sessionFactory = this.buildSessionFactory(sfb);
    }

    protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
        return sfb.buildSessionFactory();
    }

    public final Configuration getConfiguration() {
        if (this.configuration == null) {
            throw new IllegalStateException("Configuration not initialized yet");
        } else {
            return this.configuration;
        }
    }

    public SessionFactory getObject() {
        return this.sessionFactory;
    }

    public Class<?> getObjectType() {
        return this.sessionFactory != null ? this.sessionFactory.getClass() : SessionFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        this.sessionFactory.close();
    }
}

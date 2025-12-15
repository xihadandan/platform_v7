/*
 * @(#)2013-2-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.support;

import com.wellsoft.context.Context;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-3.1	zhulh		2013-2-3		Create
 * </pre>
 * @date 2013-2-3
 */
public class HibernateMappingLocationsFactoryBean implements FactoryBean<Collection<Resource>> {

    public static final String KEY_HIBERNATE_NAMED_SQL_RELOADABLE = "hibernate.namedSQL.reloadable";
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static Map<String, Resource> fileSystemResourceMap = new HashMap<String, Resource>();
    private static Collection<Resource> resources = null;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private boolean ignoreUrlResource = false;

    /**
     * @param queryName
     * @return
     */
    public static Resource getResource(String queryName) {
        return fileSystemResourceMap.get(queryName);
    }

    /**
     * @return the ignoreUrlResource
     */
    public boolean isIgnoreUrlResource() {
        return ignoreUrlResource;
    }

    /**
     * @param ignoreUrlResource 要设置的ignoreUrlResource
     */
    public void setIgnoreUrlResource(boolean ignoreUrlResource) {
        this.ignoreUrlResource = ignoreUrlResource;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public Collection<Resource> getObject() throws Exception {
        List<Resource> result = new ArrayList<Resource>();
        result.addAll(loadObject("hbm.xml"));
        return result;
    }

    private Collection<Resource> loadObject(String fileSuffix) throws Exception {
        if (HibernateMappingLocationsFactoryBean.resources != null) {
            return HibernateMappingLocationsFactoryBean.resources;
        }
        synchronized (HibernateMappingLocationsFactoryBean.class) {
            if (HibernateMappingLocationsFactoryBean.resources == null) {
                HibernateMappingLocationsFactoryBean.resources = new ArrayList<Resource>();
                String[] basePackages = StringUtils.split(Config.BASE_PACKAGES, Separator.COMMA.getValue());
                for (String basePackage : basePackages) {
                    String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                            + org.springframework.util.ClassUtils.convertClassNameToResourcePath(basePackage)
                            + "/**/*.hbm.xml";
                    Resource[] resources = resourcePatternResolver.getResources(searchPath);
                    HibernateMappingLocationsFactoryBean.resources.addAll(filterUrlResource(resources));
                }
            }
        }
        return HibernateMappingLocationsFactoryBean.resources;
    }

    /**
     * 只加载UrlResource
     *
     * @param resources
     * @return
     */
    private List<Resource> filterUrlResource(Resource[] resources) {
        Arrays.sort(resources, new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                if (o1 instanceof FileSystemResource && false == (o2 instanceof FileSystemResource)) {
                    return 1;
                } else if (false == (o1 instanceof FileSystemResource) && (o2 instanceof FileSystemResource)) {
                    return -1;
                }
                return 0;
            }
        });
        boolean namedSqlReloadable = Boolean.TRUE.toString()
                .equals(Config.getValue(KEY_HIBERNATE_NAMED_SQL_RELOADABLE));
        List<Resource> resources2 = new ArrayList<Resource>();
        for (Resource resource : resources) {
            if ((resource instanceof UrlResource)) {
                resources2.add(resource);
            }
            if (/*resource instanceof FileSystemResource &&*/(namedSqlReloadable || Context.isDebug())) {
                try {
                    Configuration cfg = new Configuration();
                    cfg.addInputStream(resource.getInputStream());
                    cfg.buildMappings();
                    Map<?, ?> sqlQueries = cfg.getNamedSQLQueries();
                    String databaseType = SupportMultiDatabaseConfigurationBuilder.resolveDatabaseType(resource);
                    for (Object key : sqlQueries.keySet()) {
                        fileSystemResourceMap.put(databaseType + key.toString(), resource);
                    }
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
            }
        }
        return resources2;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType() {
        return new Resource[]{}.getClass();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

}

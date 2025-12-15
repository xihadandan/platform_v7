/*
 * @(#)2013-1-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.support;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-6.1	zhulh		2013-1-6		Create
 * </pre>
 * @date 2013-1-6
 */
public class CommonEntityAnnotatedClassesFactoryBean implements FactoryBean<Collection<Class<?>>> {
    protected static final TypeFilter COMMON_TYPE_FILTER = new AnnotationTypeFilter(CommonEntity.class, false);
    protected static final TypeFilter ENTITY_TYPE_FILTER = new AnnotationTypeFilter(Entity.class, false);

    private static final ConcurrentMap<String, Collection<Class<?>>> objectMap = new ConcurrentHashMap<String, Collection<Class<?>>>();

    private static final String RESOURCE_PATTERN = "/**/*.class";
    private String packagesToScan;

    @Autowired
    private ResourceLoader resourceLoader;

    // private ResourcePatternResolver resourcePatternResolver;

    /**
     * @return the packagesToScan
     */
    public String getPackagesToScan() {
        return packagesToScan;
    }

    /**
     * @param packagesToScan 要设置的packagesToScan
     */
    public void setPackagesToScan(String packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public Collection<Class<?>> getObject() throws Exception {
        String cacheKey = getClass().getSimpleName() + ":" + packagesToScan;
        if (objectMap.containsKey(cacheKey)) {
            return objectMap.get(cacheKey);
        }
        synchronized (objectMap) {
            if (objectMap.containsKey(cacheKey) == false) {
                ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils
                        .getResourcePatternResolver(resourceLoader);
                Collection<Class<?>> annotatedClasses = new ArrayList<Class<?>>();
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(packagesToScan) + RESOURCE_PATTERN;
                Resource[] resources = resourcePatternResolver.getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        String className = reader.getClassMetadata().getClassName();
                        if (matchesFilter(reader, readerFactory)) {
                            annotatedClasses.add(resourcePatternResolver.getClassLoader().loadClass(className));
                        }
                    }
                }
                objectMap.put(cacheKey, annotatedClasses);
            }
        }
        return objectMap.get(cacheKey);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType() {
        return new Class[]{}.getClass();
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

    protected boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        if (COMMON_TYPE_FILTER.match(reader, readerFactory) || ENTITY_TYPE_FILTER.match(reader, readerFactory)) {
            return true;
        }
        return false;
    }
}

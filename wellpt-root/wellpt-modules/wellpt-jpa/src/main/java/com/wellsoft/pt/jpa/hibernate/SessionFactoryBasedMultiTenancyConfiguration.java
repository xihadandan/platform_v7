/*
 * @(#)2015年11月27日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import org.hibernate.cfg.NamingStrategy;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年11月27日.1	zhulh		2015年11月27日		Create
 * </pre>
 * @date 2015年11月27日
 */
public class SessionFactoryBasedMultiTenancyConfiguration {

    private NamingStrategy namingStrategy;

    private Class<?>[] annotatedClasses;

    private Resource[] mappingLocations;

    private Properties hibernateProperties;

    /**
     * @return the namingStrategy
     */
    public NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }

    /**
     * @param namingStrategy 要设置的namingStrategy
     */
    public void setNamingStrategy(NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    /**
     * @return the annotatedClasses
     */
    public Class<?>[] getAnnotatedClasses() {
        return annotatedClasses;
    }

    /**
     * @param annotatedClasses 要设置的annotatedClasses
     */
    public void setAnnotatedClasses(Class<?>[] annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    /**
     * @return the mappingLocations
     */
    public Resource[] getMappingLocations() {
        return mappingLocations;
    }

    /**
     * @param mappingLocations 要设置的mappingLocations
     */
    public void setMappingLocations(Resource[] mappingLocations) {
        this.mappingLocations = mappingLocations;
    }

    /**
     * @return the hibernateProperties
     */
    public Properties getHibernateProperties() {
        return hibernateProperties;
    }

    /**
     * @param hibernateProperties 要设置的hibernateProperties
     */
    public void setHibernateProperties(Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

}

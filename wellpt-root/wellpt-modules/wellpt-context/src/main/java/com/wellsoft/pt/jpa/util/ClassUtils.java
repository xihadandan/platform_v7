/*
 * @(#)2016年2月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.util;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import javax.annotation.Nullable;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
 * 2016年2月1日.1	zhulh		2016年2月1日		Create
 * </pre>
 * @date 2016年2月1日
 */
public class ClassUtils {
    private static Logger logger = LoggerFactory.getLogger(ClassUtils.class);
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
            resourcePatternResolver);

    private static Object LOCKED = new Object();

    private static Map<String, Class<?>> entityClasses;
    private static Map<String, Class<?>> itemClasses;
    private static Map<String, Class<?>> jobClasses;
    private static Map<String, String> classMethodDescriptions;
    private static Map<String, Class<?>> enumClasses;

    static {
        try {
            initEntityClasses();
            initQueryItemClasses();
            initJobClasses();
            initClassMethodDescriptions();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private static void initJobClasses() throws Exception {
        if (jobClasses != null) {
            return;
        }
        jobClasses = new HashMap<String, Class<?>>();
        String[] basePackages = StringUtils.split(Config.BASE_PACKAGES, Separator.COMMA.getValue());
        for (String basePackage : basePackages) {
            String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils.convertClassNameToResourcePath(
                    basePackage)
                    + "/**/job/**/*Job.class";
            Resource[] resources = resourcePatternResolver.getResources(searchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> mappedClass = Class.forName(className);
                if (org.quartz.Job.class.isAssignableFrom(mappedClass)
                        && !Modifier.isAbstract(mappedClass.getModifiers())) {
                    String simpleName = mappedClass.getSimpleName();
                    jobClasses.put(simpleName, mappedClass);
                }
            }
        }
    }

    private static void loadClassMethodDescription(String classResoucePath) throws Exception {
        String[] basePackages = StringUtils.split(Config.BASE_PACKAGES, Separator.COMMA.getValue());
        for (String basePackage : basePackages) {
            String descProperties = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + "/META-INF/com/wellsoft/**/description.properties";
            Resource[] resources = resourcePatternResolver.getResources(descProperties);
            Map<Object, Object> allDescriptions = Maps.newHashMap();
            for (Resource resource : resources) {
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                allDescriptions.putAll(properties);
            }

            String classSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils.convertClassNameToResourcePath(
                    basePackage)
                    + classResoucePath;
            resources = resourcePatternResolver.getResources(classSearchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> mappedClass = Class.forName(className);
                putClassMethodDecription(allDescriptions, mappedClass, null,
                        0);
                //方法的注解说明
                Method[] methods = mappedClass.getDeclaredMethods();
                //按同名方法分组
                ImmutableListMultimap<String, Method> methodMap = Multimaps.index(
                        Lists.newArrayList(methods), new Function<Method, String>() {
                            @Nullable
                            @Override
                            public String apply(@Nullable Method method) {
                                return method.getName();
                            }
                        });

                Map<String, Collection<Method>> methodGroupMap = methodMap.asMap();
                Set<String> methodKeys = methodGroupMap.keySet();
                for (String m : methodKeys) {
                    List<Method> methodCollection = Lists.newArrayList(methodGroupMap.get(m));
                    if (methodCollection.size() > 1) {
                        for (int i = 1; i <= methodCollection.size(); i++) {
                            putClassMethodDecription(allDescriptions, mappedClass,
                                    methodCollection.get(i - 1),
                                    i);
                        }
                        continue;
                    }
                    putClassMethodDecription(allDescriptions, mappedClass, methodCollection.get(0),
                            0);

                }

            }
        }
    }

    public static void initClassMethodDescriptions() throws Exception {
        if (classMethodDescriptions != null) {
            return;
        }
        classMethodDescriptions = Maps.newHashMap();
        loadClassMethodDescription("/**/facade/service/**/*Service.class");
        loadClassMethodDescription("/**/web/**/*Controller.class");


    }

    private static void putClassMethodDecription(Map<Object, Object> descriptionProperties,
                                                 Class targetClass, Method targetMethod,
                                                 int methodOrder) {
        if (targetMethod != null) {//注解方法
            String descriptText = "";
            Object propText = descriptionProperties.get(
                    targetClass.getCanonicalName() + "." + targetMethod.getName() + (methodOrder == 0 ? "" : "#" + methodOrder));
            if (propText != null) {
                descriptText = propText.toString();
            }
            boolean hasDecription = targetMethod.isAnnotationPresent(
                    Description.class);
            if (hasDecription) {
                Description annotation = (Description) AnnotationUtils.getAnnotation(
                        targetMethod, Description.class);
                descriptText = annotation.value();
            }
            if (StringUtils.isNotBlank(descriptText)) {
                classMethodDescriptions.put(
                        targetClass.getCanonicalName() + "." + targetMethod.getName() + (methodOrder == 0 ? "" : "#" + methodOrder),
                        descriptText);
            }
        } else if (targetClass != null) {//注解类
            String canonicalName = targetClass.getCanonicalName();
            String classDescription = "";
            Object classPropDescription = descriptionProperties.get(targetClass.getCanonicalName());
            if (classPropDescription != null) {
                classDescription = classPropDescription.toString();
            }
            if (targetClass.isAnnotationPresent(Description.class)) {//注解说明优先
                Description description = (Description) targetClass.getAnnotation(
                        Description.class);
                classDescription = description.value();
            }
            if (StringUtils.isNotBlank(classDescription)) {
                classMethodDescriptions.put(canonicalName, classDescription);
            }
        }
    }


    /**
     *
     */
    private static void initEntityClasses() {
        if (entityClasses != null) {
            return;
        }
        Set<SessionFactory> sessionFactories = new HashSet<SessionFactory>();
        SessionFactory sessionFactory = null;
        try {
            sessionFactory = ApplicationContextHolder.getBean(
                    Config.COMMON_SESSION_FACTORY_BEAN_NAME,
                    SessionFactory.class);
            if (sessionFactory != null) {
                sessionFactories.add(sessionFactory);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        sessionFactory = ApplicationContextHolder
                .getBean(Config.TENANT_SESSION_FACTORY_BEAN_NAME, SessionFactory.class);
        if (sessionFactory != null) {
            sessionFactories.add(sessionFactory);
        }
        entityClasses = new HashMap<String, Class<?>>();
        for (SessionFactory sessionFactoryImpl : sessionFactories) {
            Map<String, ClassMetadata> eMap = sessionFactoryImpl.getAllClassMetadata();
            for (ClassMetadata classMetadata : eMap.values()) {
                Class<?> mappedClass = classMetadata.getMappedClass();
                if (mappedClass != null) {
                    String simpleName = mappedClass.getSimpleName();
                    entityClasses.put(Introspector.decapitalize(simpleName), mappedClass);
                }
            }
        }
    }

    /**
     *
     */
    private static void initEnumClasses() {
        if (enumClasses != null) {
            return;
        }
        try {
            enumClasses = new HashMap<String, Class<?>>();
            String[] basePackages = StringUtils.split(Config.BASE_PACKAGES, Separator.COMMA.getValue());
            for (String basePackage : basePackages) {
                String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + org.springframework.util.ClassUtils.convertClassNameToResourcePath(
                        basePackage)
                        + "/**/*.class";
                Resource[] resources = resourcePatternResolver.getResources(searchPath);
                for (Resource resource : resources) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    org.springframework.core.type.ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    try {
                        if (classMetadata.isFinal()) {
                            String className = metadataReader.getClassMetadata().getClassName();
                            Class<?> mappedClass = Class.forName(className);
                            if (Enum.class.isAssignableFrom(mappedClass)) {
                                enumClasses.put(className, mappedClass);
                            }
                        }
                    } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @return the entityClasses
     */
    public static Map<String, Class<?>> getEntityClasses() {
        return entityClasses;
    }

    /**
     * @return the itemClasses
     */
    public static Map<String, Class<?>> getItemClasses() {
        return itemClasses;
    }

    public static void initQueryItemClasses() throws Exception {
        if (itemClasses != null) {
            return;
        }
        itemClasses = new HashMap<String, Class<?>>();
        String[] basePackages = StringUtils.split(Config.BASE_PACKAGES, Separator.COMMA.getValue());
        for (String basePackage : basePackages) {
            String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils.convertClassNameToResourcePath(
                    basePackage)
                    + "/**/query/**/*QueryItem.class";
            Resource[] resources = resourcePatternResolver.getResources(searchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> mappedClass = Class.forName(className);
                String simpleName = mappedClass.getSimpleName();
                itemClasses.put(Introspector.decapitalize(simpleName), mappedClass);
            }
        }
    }

    public static Map<String, Class<?>> getJobClasses() {
        return jobClasses;
    }


    public static Map<String, String> getClassMethodDescriptions() {
        return classMethodDescriptions;
    }

    public static Map<String, Class<?>> getEnumClasses() {
        if (enumClasses != null) {
            return enumClasses;
        }
        synchronized (LOCKED) {
            if (enumClasses != null) {
                return enumClasses;
            }
            initEnumClasses();
        }
        return enumClasses;
    }

}

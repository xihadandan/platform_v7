/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wellsoft.pt.di.support;

import com.google.common.collect.Maps;
import com.wellsoft.pt.di.component.AbstractDIComponent;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.NoFactoryAvailableException;
import org.apache.camel.impl.DefaultComponentResolver;
import org.apache.camel.spi.ComponentResolver;
import org.apache.camel.spi.FactoryFinder;
import org.apache.camel.util.CamelContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentMap;

/**
 * The default implementation of {@link ComponentResolver} which tries to find
 * components by using the URI scheme prefix and searching for a file of the URI
 * scheme name in the <b>META-INF/services/org/apache/camel/component/</b>
 * directory on the classpath.
 */
public class CustomComponentResolver extends DefaultComponentResolver {


    public static final String RESOURCE_PATH = "META-INF/services/org/apache/camel/component/";
    private static final Logger LOG = LoggerFactory.getLogger(CustomComponentResolver.class);
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
            resourcePatternResolver);
    private static ConcurrentMap<String, Class> customComponetClass = Maps.newConcurrentMap();//自定义的组件类

    static {
        try {
            String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + "com/wellsoft/pt/**/component/**/*DIComponent.class";
            Resource[] resources = resourcePatternResolver.getResources(searchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(
                        resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> mappedClass = Class.forName(className);
                if (!Modifier.isAbstract(mappedClass.getModifiers())//非抽象类的组件定义
                        && AbstractDIComponent.class.isAssignableFrom(mappedClass)) {
                    try {
                        AbstractDIComponent component = (AbstractDIComponent) mappedClass.newInstance();
                        AbstractEndpoint endpoint = (AbstractEndpoint) component.endpointClass.newInstance();
                        customComponetClass.put(endpoint.endpointPrefix(), mappedClass);
                    } catch (Exception e) {
                        LOG.error("无法识别数据交换类[ {} ]的端点信息", resource.getURL(), e);
                    }

                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FactoryFinder factoryFinder;

    public Component resolveComponent(String name, CamelContext context) {
        // lookup in registry first
        Object bean = null;
        try {
            bean = context.getRegistry().lookupByName(name);
            getLog().debug("Found component: {} in registry: {}", name, bean);
        } catch (Exception e) {
            getLog().debug("Ignored error looking up bean: " + name, e);
        }
        if (bean != null) {
            if (bean instanceof Component) {
                return (Component) bean;
            } else {
                // lets use Camel's type conversion mechanism to convert things like CamelContext
                // and other types into a valid Component
                Component component = CamelContextHelper.convertTo(context, Component.class, bean);
                if (component != null) {
                    return component;
                }
            }
            // we do not throw the exception here and try to auto create a component
        }

        // not in registry then use component factory
        Class<?> type;

        type = customComponetClass.get(name);//优先从自定义组件类加载

        if (type == null) {
            try {
                type = findComponent(name, context);
                if (type == null) {
                    // not found
                    return null;
                }
            } catch (NoFactoryAvailableException e) {
                return null;
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Invalid URI, no Component registered for scheme: " + name, e);
            }

        }


        if (getLog().isDebugEnabled()) {
            getLog().debug("Found component: {} via type: {} via: {}{}",
                    new Object[]{name, type.getName(), factoryFinder.getResourcePath(), name});
        }

        // create the component
        if (Component.class.isAssignableFrom(type)) {
            return (Component) context.getInjector().newInstance(type);
        } else {
            throw new IllegalArgumentException(
                    "Type is not a Component implementation. Found: " + type.getName());
        }
    }

    private Class<?> findComponent(String name,
                                   CamelContext context) throws ClassNotFoundException, IOException {
        if (factoryFinder == null) {
            factoryFinder = context.getFactoryFinder(RESOURCE_PATH);
        }
        return factoryFinder.findClass(name);
    }

    protected Logger getLog() {
        return LOG;
    }

}

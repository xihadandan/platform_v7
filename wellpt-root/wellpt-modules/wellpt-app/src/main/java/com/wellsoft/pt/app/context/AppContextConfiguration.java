/*
 * @(#)2017-01-24 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.context.config.ContainerComponentRegistry;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;
import com.wellsoft.pt.app.context.support.OrderedProperties;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.support.ContainerComponentConfiguration;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import com.wellsoft.pt.app.theme.Theme;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-24.1	zhulh		2017-01-24		Create
 * </pre>
 * @date 2017-01-24
 */
@Component
public class AppContextConfiguration implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7173966306807647996L;

    private static final String CLASSPATH = "classpath";

    private Logger logger = LoggerFactory.getLogger(AppContextParser.class);

    @Autowired
    private List<AppContextConfigurerAdapter> configurers;

    @Autowired(required = false)
    private List<AppContextParser> parsers;

    private AppParserContext parserContext = new AppParserContext();

    /**
     *
     */
    public void configure() {
        if (CollectionUtils.isEmpty(parsers)) {
            return;
        }
        for (AppContextConfigurerAdapter appContextConfigurer : configurers) {
            // 加载属性配置
            PropertiesRegistry propertiesRegistry = new PropertiesRegistry();
            appContextConfigurer.addProperties(propertiesRegistry);
            List<AppContextPropertiesConfigurationSupport> propertiesConfigurationSupports = prepare(
                    appContextConfigurer, propertiesRegistry);
            for (AppContextPropertiesConfigurationSupport configurationSupport : propertiesConfigurationSupports) {
                for (AppContextParser parser : parsers) {
                    parser.parse(configurationSupport, parserContext);
                }
            }

            // 注册容器组件
            ContainerComponentRegistry componentRegistry = new ContainerComponentRegistry();
            appContextConfigurer.addContainerComponent(componentRegistry);
            buildContainerComponentConfiguration(componentRegistry);
        }
    }

    /**
     * @param componentRegistry
     */
    private void buildContainerComponentConfiguration(ContainerComponentRegistry componentRegistry) {
        ContainerComponentConfiguration configuration = ApplicationContextHolder
                .getBean(ContainerComponentConfiguration.class);
        configuration.addContainerComponent(componentRegistry.getContainerComponentMap());
    }

    /**
     * @param appContextConfigurer
     */
    private List<AppContextPropertiesConfigurationSupport> prepare(AppContextConfigurerAdapter appContextConfigurer,
                                                                   PropertiesRegistry propertiesRegistry) {
        List<String> propertiesFiles = propertiesRegistry.getPropertiesFiles();
        List<OrderedProperties> orderedProperties = new ArrayList<OrderedProperties>();
        List<AppContextPropertiesConfigurationSupport> configurationSupports = new ArrayList<AppContextPropertiesConfigurationSupport>();
        for (String propertiesFile : propertiesFiles) {
            try {
                Resource[] resources = getResources(appContextConfigurer, propertiesFile);
                for (Resource resource : resources) {
                    InputStream fis = resource.getInputStream();
                    OrderedProperties config = new OrderedProperties();
                    config.load(fis);
                    fis.close();
                    orderedProperties.add(config);
                    AppContextPropertiesConfigurationSupport propertiesConfigurationSupport = new AppContextPropertiesConfigurationSupport(
                            appContextConfigurer, propertiesFile, config);
                    configurationSupports.add(propertiesConfigurationSupport);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        parserContext.addAll(orderedProperties);
        return configurationSupports;
    }

    /**
     * @param properties
     * @return
     * @throws IOException
     */
    private Resource[] getResources(AppContextConfigurerAdapter appContextConfigurer, String properties)
            throws IOException {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils
                .getResourcePatternResolver(applicationContext);
        Resource[] resources = new Resource[0];
        if (properties.startsWith(CLASSPATH)) {
            resources = resourcePatternResolver.getResources(properties);
        } else {
            Class<?> configurerClass = appContextConfigurer.getClass();
            URL url = configurerClass.getResource(properties);
            if (null != url) {
                List<Resource> tmpResources = new ArrayList<Resource>();
                tmpResources.add(new UrlResource(url));
                resources = tmpResources.toArray(resources);
            } else {
                logger.error("URL of " + properties + " is null");
            }
        }
        return resources;
    }

    /**
     * @return
     */
    public List<CssFile> getAllCssFile() {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.addAll(parserContext.getCssFileMap().values());
        return cssFiles;
    }

    /**
     * @return
     */
    public List<JavaScriptModule> getAllJavaScriptModules() {
        List<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
        javaScriptModules.addAll(parserContext.getJavaScriptModuleMap().values());
        return javaScriptModules;
    }

    /**
     * @return
     */
    public List<? extends JavaScriptTemplate> getAllJavaScriptTemplats() {
        List<JavaScriptTemplate> javaScriptTemplates = new ArrayList<JavaScriptTemplate>();
        javaScriptTemplates.addAll(parserContext.getJavaScriptTemplateMap().values());
        return javaScriptTemplates;
    }

    /**
     * @return
     */
    public List<Theme> getAllThemes() {
        List<Theme> themes = new ArrayList<Theme>();
        themes.addAll(parserContext.getThemeMap().values());
        return themes;
    }

}

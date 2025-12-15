/*
 * @(#)3/16/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import javax.servlet.ServletContext;

import static com.wellsoft.context.servlet.ServletContextClassLoader.cdbLocationPattern;
import static com.wellsoft.context.servlet.ServletContextClassLoader.mdbLocationPattern;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 3/16/23.1	zhulh		3/16/23		Create
 * </pre>
 * @date 3/16/23
 */
public class ContextLoader {
    private static Logger LOG = LoggerFactory.getLogger(ContextLoader.class);

    public static void load(ServletContext servletContext) {
        ResourcePatternResolver resourcePatternResolver = new ServletContextResourcePatternResolver(servletContext);
        try {
            Resource[] mdbResources = resourcePatternResolver.getResources(mdbLocationPattern);
            Resource[] cdbResources = resourcePatternResolver.getResources(cdbLocationPattern);
            if (mdbResources.length > 0 && cdbResources.length > 0) {
                ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
                ClassLoader classLoader = new ServletContextClassLoader(currentClassLoader, mdbResources, cdbResources);
                Thread.currentThread().setContextClassLoader(classLoader);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}

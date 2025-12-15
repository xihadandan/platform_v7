/*
 * @(#)2019年5月14日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.servlet;

import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Description: 密码器类加载器，设置为上下文的类加载器，解密class文件，后续考虑通过Instrumentation或JVMTI替换解密功能
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月14日.1	zhulh		2019年5月14日		Create
 * </pre>
 * @date 2019年5月14日
 */
@Order(value = 0)
public class ServletContextClassLoaderInitializer implements WebApplicationInitializer {

    /**
     * (non-Javadoc)
     *
     * @see WebApplicationInitializer#onStartup(ServletContext)
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ContextLoader.load(servletContext);
    }

}

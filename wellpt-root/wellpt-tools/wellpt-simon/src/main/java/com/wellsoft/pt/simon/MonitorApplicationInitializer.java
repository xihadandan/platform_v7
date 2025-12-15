/*
 * @(#)2019年11月23日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

/**
 * Description: 在DefaultWebApplicationInitializer之前，不需要权限过滤
 *  
 * @author zhongzh
 * @date 2019年11月23日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月23日.1	zhongzh		2019年11月23日		Create
 * </pre>
 *
 */
@Order(1)
public class MonitorApplicationInitializer implements WebApplicationInitializer {

    public void onStartup(javax.servlet.ServletContext servletContext) throws javax.servlet.ServletException {
        String filterName = "simon-console-filter";
        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("url-prefix", "/simon-console");
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD,
                DispatcherType.INCLUDE);
        // 初始化SimonConsoleFilter
        FilterRegistration.Dynamic filter = servletContext.addFilter(filterName,
                org.javasimon.console.SimonConsoleFilter.class);
        filter.setInitParameters(initParameters);
        filter.addMappingForUrlPatterns(dispatcherTypes, false, "/*");
    };

}

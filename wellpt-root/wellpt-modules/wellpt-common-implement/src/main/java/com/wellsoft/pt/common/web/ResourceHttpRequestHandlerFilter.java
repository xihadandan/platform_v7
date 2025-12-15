/*
 * @(#)2013-12-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web;

import com.wellsoft.pt.app.support.AppCacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: js请求混淆路径反解析过滤器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-17.1	zhulh		2013-12-17		Create
 * </pre>
 * @date 2013-12-17
 */
public class ResourceHttpRequestHandlerFilter extends GenericFilterBean {
    private ResourceHttpRequestHandler delegate;
    private String location;
    private String[] resourceLocations;

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location 要设置的location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * (non-Javadoc)
     *
     * @see GenericFilterBean#initFilterBean()
     */
    @Override
    protected void initFilterBean() throws ServletException {
        delegate = new ResourceHttpRequestHandler();
        resourceLocations = StringUtils.split(location);
        List<Resource> locations = new ArrayList<Resource>();
        for (String resourceLocation : resourceLocations) {
            ServletContextResource servletContextResource = new ServletContextResource(
                    getServletContext(),
                    resourceLocation);
            locations.add(servletContextResource);
        }
        delegate.setLocations(locations);
        delegate.setApplicationContext(
                WebApplicationContextUtils.getWebApplicationContext(getServletContext()));

        super.initFilterBean();
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestUri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        for (String resourceLocation : resourceLocations) {
            String prefix = contextPath + resourceLocation;
            if (requestUri.startsWith(prefix)) {
                String path = requestUri.substring(prefix.length());
                String realPath = AppCacheUtils.getRealPath(path);
                if (StringUtils.isNotBlank(realPath)) {
                    /*path = realPath.substring(resourceLocation.length());
                    request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE,
                            path);*/
                    request.setAttribute(WebUtils.INCLUDE_SERVLET_PATH_ATTRIBUTE, realPath);
                    chain.doFilter(request, response);
                    break;
                }

            }
        }

        chain.doFilter(request, response);
    }

}

/*
 * @(#)2016年8月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web.interceptor;

import com.wellsoft.pt.app.support.AppConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月13日.1	zhulh		2016年8月13日		Create
 * </pre>
 * @date 2016年8月13日
 */
public class WebAppFacadeInterceptor extends HandlerInterceptorAdapter {

    public static final String PI_PATH_REQUEST_ATTRIBUTE_NAME = "APP_PI_PATH";
    //	private static final String SEPARATOR_SLASH = "/";
    private static final String SEPARATOR_DOT = ".";
    private static final UrlPathHelper urlPathHelper = new UrlPathHelper();

    //	public static final String SYSTEM_REQUEST_ATTRIBUTE_NAME = "APP_SYSTEM_ID";
    //	public static final String MODULE_REQUEST_ATTRIBUTE_NAME = "APP_MODULE_ID";
    //	public static final String APPLICATION_REQUEST_ATTRIBUTE_NAME = "APP_APPLICATION_ID";

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        resolveRequestAppData(request, response);
        return super.preHandle(request, response, handler);
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * @param request
     * @param response
     */
    private void resolveRequestAppData(HttpServletRequest request, HttpServletResponse response) {
        String requestUri = urlPathHelper.getRequestUri(request);
        String basePath = requestUri;
        int end = StringUtils.lastIndexOf(requestUri, SEPARATOR_DOT);
        if (end > 0) {
            basePath = requestUri.substring(0, end);
        }
        String prefix = request.getContextPath() + AppConstants.WEB_APP_PATH;
        String appPath = StringUtils.substring(basePath, prefix.length());
        //		String[] piInfos = StringUtils.split(appPath, SEPARATOR_SLASH);
        //		String sysId = null;
        //		String moduleId = null;
        //		String appId = null;
        //		if (piInfos.length > 0) {
        //			sysId = piInfos[0];
        //		}
        //		if (piInfos.length == 2) {
        //			moduleId = piInfos[1];
        //			appId = piInfos[1];
        //		}
        //		if (piInfos.length > 2) {
        //			moduleId = piInfos[piInfos.length - 2];
        //			appId = piInfos[piInfos.length - 1];
        //		}
        request.setAttribute(PI_PATH_REQUEST_ATTRIBUTE_NAME, appPath);
        //		request.setAttribute(SYSTEM_REQUEST_ATTRIBUTE_NAME, sysId);
        //		request.setAttribute(MODULE_REQUEST_ATTRIBUTE_NAME, moduleId);
        //		request.setAttribute(APPLICATION_REQUEST_ATTRIBUTE_NAME, appId);
    }
}

/*
 * @(#)2014-11-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.web;

import org.springframework.core.NamedThreadLocal;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
 * 2014-11-24.1	zhulh		2014-11-24		Create
 * </pre>
 * @date 2014-11-24
 */
public class JsonDataServicesContextHolder {
    private static final ThreadLocal<HttpServletRequest> requestHolder = new NamedThreadLocal<HttpServletRequest>(
            "Http Servlet Request");

    private static final ThreadLocal<HttpServletResponse> responseHolder = new NamedThreadLocal<HttpServletResponse>(
            "Http Servlet Response");

    public static void setRequestResponse(HttpServletRequest request, HttpServletResponse response) {
        requestHolder.set(request);
        responseHolder.set(response);
    }

    public static void remove() {
        requestHolder.remove();
        responseHolder.remove();
    }


    public static HttpServletRequest getRequest() {
        HttpServletRequest request = requestHolder.get();
        if (request == null && RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            request = attr.getRequest();
        }
        return request;
    }

    public static boolean isMobileRequest() {
        HttpServletRequest request = requestHolder.get();
        if (request == null && RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            request = attr.getRequest();
        }
        boolean result = request != null && ("true".equalsIgnoreCase(request.getHeader("isMobileApp")) || "true".equalsIgnoreCase(request.getParameter("isMobileApp")));
        if (result) {
            return result;
        } else {
            if (null == RequestContextHolder.getRequestAttributes()) {
                return result;
            } else {
                request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                return request != null && ("true".equalsIgnoreCase(request.getHeader("isMobileApp")) || "true".equalsIgnoreCase(request.getParameter("isMobileApp")));
            }
        }
    }

    public static HttpServletResponse getResponse() {
        HttpServletResponse response = responseHolder.get();
        return response;
    }
}

/*
 * @(#)Mar 3, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.interceptor;

import com.google.gson.JsonElement;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.web.action.ActionInvocation;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

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
 * Mar 3, 2017.1	zhulh		Mar 3, 2017		Create
 * </pre>
 * @date Mar 3, 2017
 */
@Component
public class Test1ActionHandlerInterceptor extends ActionHandlerInterceptorAdapter {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptor#getName()
     */
    @Override
    public String getName() {
        return "操作处理拦截器测试1";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.wellsoft.pt.dms.core.context.ActionContext, com.wellsoft.pt.dms.core.web.action.ActionInvocation)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             ActionContext actionContext,
                             ActionInvocation actionInvocation,
                             JsonElement jsonParams) throws Exception {
        return super.preHandle(request, response, actionContext, actionInvocation, jsonParams);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.wellsoft.pt.dms.core.context.ActionContext, com.wellsoft.pt.dms.core.web.action.ActionInvocation, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           ActionContext actionContext,
                           ActionInvocation actionInvocation, ModelAndView modelAndView,
                           JsonElement jsonParams) throws Exception {
        super.postHandle(request, response, actionContext, actionInvocation, modelAndView,
                jsonParams);
    }

}

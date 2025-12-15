/*
 * @(#)2019年12月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.interceptor;

import com.google.gson.JsonElement;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.event.DmsActionEvent;
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
 * 2019年12月19日.1	zhulh		2019年12月19日		Create
 * </pre>
 * @date 2019年12月19日
 */
@Component
public class DmsPublishActionEventHandlerInterceptor extends ActionHandlerInterceptorAdapter {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptor#getName()
     */
    @Override
    public String getName() {
        return "数据管理_数据管理操作后事件发布拦截器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.wellsoft.pt.dms.core.context.ActionContext, com.wellsoft.pt.dms.core.web.action.ActionInvocation, org.springframework.web.servlet.ModelAndView, com.google.gson.JsonElement)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext,
                           ActionInvocation actionInvocation, ModelAndView modelAndView, JsonElement jsonParams) throws Exception {
        ApplicationContextHolder.getApplicationContext().publishEvent(
                new DmsActionEvent(actionContext, actionInvocation, jsonParams));
    }

}

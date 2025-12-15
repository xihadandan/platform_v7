package com.wellsoft.pt.demo.web.interceptor;

import com.google.gson.JsonElement;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.web.action.ActionInvocation;
import com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class StudentDyformFormActionHandlerInterceptorAdapter extends ActionHandlerInterceptorAdapter {

    @Override
    public String getName() {
        return "平台二开示例--数据管理_学生表单操作_拦截器";
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext,
                             ActionInvocation actionInvocation, JsonElement jsonParams) throws Exception {
        System.out.println("preHandle");
        return super.preHandle(request, response, actionContext, actionInvocation, jsonParams);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext,
                           ActionInvocation actionInvocation, ModelAndView modelAndView, JsonElement jsonParams) throws Exception {
        System.out.println("postHandle");
        super.postHandle(request, response, actionContext, actionInvocation, modelAndView, jsonParams);
    }


}

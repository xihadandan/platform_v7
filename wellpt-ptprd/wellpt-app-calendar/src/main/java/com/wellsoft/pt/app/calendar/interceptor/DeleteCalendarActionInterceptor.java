/*
 * @(#)2018年4月28日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.interceptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wellsoft.pt.app.calendar.service.MyCalendarEventService;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.web.action.ActionInvocation;
import com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptorAdapter;
import com.wellsoft.pt.dms.ext.dyform.web.action.DyFormActions;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月28日.1	zyguo		2018年4月28日		Create
 * </pre>
 * @date 2018年4月28日
 */
@Component
public class DeleteCalendarActionInterceptor extends ActionHandlerInterceptorAdapter {

    @Autowired
    private MyCalendarEventService myCalendarEventService;

    @Override
    public String getName() {
        return "我的日历本_删除操作_拦截器";
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext,
                             ActionInvocation actionInvocation, JsonElement jsonElement) throws Exception {
        return super.preHandle(request, response, actionContext, actionInvocation, jsonElement);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext,
                           ActionInvocation actionInvocation, ModelAndView modelAndView, JsonElement jsonElement) throws Exception {
        String acId = actionInvocation.getAction().getId();
        if (DyFormActions.ACTION_DELETE.equals(acId)) {
            JsonObject jsonObj = jsonElement == null ? null : jsonElement.getAsJsonObject();
            if (jsonObj != null) {
                String calendarUuid = jsonObj.get("dataUuid").getAsString();
                if (StringUtils.isNotBlank(calendarUuid)) {
                    // 删除该日历本下的所有事项
                    myCalendarEventService.deleteEventByCalendarUuid(calendarUuid);
                } else {
                    throw new RuntimeException("找不到对应的数据");
                }
            }
        }
        super.postHandle(request, response, actionContext, actionInvocation, modelAndView, jsonElement);
    }

}

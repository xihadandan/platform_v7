/*
 * @(#)2013-7-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.web.interceptor;

import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.util.web.AjaxUtils;
import com.wellsoft.context.web.controller.FaultMessage;
import com.wellsoft.pt.security.access.CustomLogoutFilter;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.support.CasLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-7-2.1	rzhu		2013-7-2		Create
 * </pre>
 * @date 2013-7-2
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails != null) {
            return true;
        }

        if (AjaxUtils.isAjaxRequest(request)) {
            // Ajax的请求登录超时处理及JDS的独立处理
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(Encoding.UTF8.getValue());
            FaultMessage msg = new FaultMessage("登录超时，请重新登录!");
            msg.setErrorCode(JsonDataErrorCode.SessionExpired.name());
            msg.setData(CustomLogoutFilter.DEFAULT_LOGOUT_URL);
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            Writer writer = response.getWriter();
            objectMapper.writeValue(writer, msg);
        } else {
            if (CasLoginUtils.isUseCas()) {
                // 启用cas时跳转的链接
                response.sendRedirect(request.getContextPath() + CustomLogoutFilter.CAS_LOGOUT_URL);
            } else {
                response.sendRedirect(request.getContextPath() + CustomLogoutFilter.DEFAULT_LOGOUT_URL);
            }
        }
        return false;
    }

}

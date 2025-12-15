/*
 * @(#)2019年8月9日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.api.authentication;

import com.google.common.collect.Maps;
import com.wellsoft.pt.api.AbstractWellptApi;
import com.wellsoft.pt.api.annotation.ApiComponent;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月9日.1	zhulh		2019年8月9日		Create
 * </pre>
 * @date 2019年8月9日
 */
@ApiComponent(name = "authentication")
public class AuthenticationApi extends AbstractWellptApi {

    @Autowired
    private UserDetailsServiceProvider userDetailsServiceProvider;

    @Autowired(required = false)
    private List<CompositeSessionAuthenticationStrategy> sessionAuthenticationStrategies;

    @Autowired(required = false)
    private List<LogoutHandler> logoutHandlers;

    /**
     * 登录
     *
     * @param username
     */
    public ApiResponse login(AuthenticationApiRequest request) {
        UserDetails userDetails = userDetailsServiceProvider.getUserDetails(request.getUsername());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
                StringUtils.EMPTY, userDetails.getAuthorities());
        auth.setDetails(userDetails.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        //        for (SessionAuthenticationStrategy sessionAuthenticationStrategy : sessionAuthenticationStrategies) {
        //            sessionAuthenticationStrategy.onAuthentication(auth, request, response);
        //        }
        return new ApiResponse();
    }

    /**
     * 登出
     */
    public ApiResponse logout(AuthenticationApiRequest request) {
        SecurityContext context = SecurityContextHolder.getContext();
        //        Authentication authentication = context.getAuthentication();
        //        for (LogoutHandler logoutHandler : logoutHandlers) {
        //            logoutHandler.logout(request, response, authentication);
        //        }
        context.setAuthentication(null);
        SecurityContextHolder.clearContext();
        if (RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest httpServletRequest = attr.getRequest();
            HttpSession session = httpServletRequest.getSession(false);
            if (session != null) {
                LOGGER.info("Invalidating session: {}", session.getId());
                session.invalidate();
            }
        }
        return new ApiResponse();
    }

    /**
     * 有效性验证
     *
     * @param username
     * @return
     */
    public ApiResponse validate(AuthenticationApiRequest request) {
        boolean valid = false;
        try {
            UserDetails userDetails = userDetailsServiceProvider.getUserDetails(request.getUsername());
            LOGGER.info("userDetails: {}" + userDetails);
            valid = true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> data = Maps.newHashMap();
        data.put("valid", valid);
        apiResponse.setData(data);
        return apiResponse;
    }

}

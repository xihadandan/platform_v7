/*
 * @(#)2014-1-15 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.org.entity.UserLoginLog;
import com.wellsoft.pt.org.service.UserLoginLogService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-15.1	zhulh		2014-1-15		Create
 * </pre>
 * @date 2014-1-15
 */
public class CasLoginAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserLoginLogService userLoginLogService = ApplicationContextHolder.getBean(UserLoginLogService.class);

            // 增加登录日记
            UserLoginLog loginLog = new UserLoginLog();
            loginLog.setUserUuid(userDetails.getUserUuid());
            loginLog.setLoginTime(Calendar.getInstance().getTime());
            loginLog.setLoginIp(ServletUtils.getRemoteAddr(request));
            userLoginLogService.saveBean(loginLog);

            // 更新上次登录时间
            UserService userService = ApplicationContextHolder.getBean(UserService.class);
            userService.updateLastLoginTime(userDetails.getUserUuid());
        }
    }
}

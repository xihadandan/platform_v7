/*
 * @(#)2014-1-15 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.NetUtils;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.org.entity.UserLoginLog;
import com.wellsoft.pt.org.service.UserLoginLogService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class LoginAuthenticationSuccessHandler implements
        com.wellsoft.pt.security.core.authentication.AuthenticationSuccessHandler {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserLoginLogService userLoginLogService = ApplicationContextHolder.getBean(
                    UserLoginLogService.class);

            // 增加登录日记
            UserLoginLog loginLog = new UserLoginLog();
            loginLog.setUserUuid(userDetails.getUserUuid());
            loginLog.setLoginTime(Calendar.getInstance().getTime());
            loginLog.setLoginIp(NetUtils.getRequestIp());
            userLoginLogService.saveBean(loginLog);

            // 更新上次登录时间
            if (userDetails.getUserUuid() != null) {
                MultiOrgUserService userService = ApplicationContextHolder.getBean(
                        MultiOrgUserService.class);
                userService.updateLastLoginTime(userDetails.getUserUuid());
            }
        }
    }

}

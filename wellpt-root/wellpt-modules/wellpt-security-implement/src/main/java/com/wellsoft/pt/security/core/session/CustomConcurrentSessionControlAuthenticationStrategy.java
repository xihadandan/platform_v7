/*
 * @(#)2014-1-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.session;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-17.1	zhulh		2014-1-17		Create
 * </pre>
 * @date 2014-1-17
 */
public class CustomConcurrentSessionControlAuthenticationStrategy extends
        ConcurrentSessionControlAuthenticationStrategy {

    protected Logger logger = LoggerFactory.getLogger(
            CustomConcurrentSessionControlAuthenticationStrategy.class);

    private volatile boolean licensePermit;

    private volatile boolean isCheckedLicense;

    private SessionRegistry sessionRegistry;

    /**
     * @param sessionRegistry
     */
    public CustomConcurrentSessionControlAuthenticationStrategy(SessionRegistry sessionRegistry) {
        super(sessionRegistry);
        this.sessionRegistry = sessionRegistry;
    }

    public CustomConcurrentSessionControlAuthenticationStrategy(
            SessionRegistry sessionRegistry, int maximumSessions,
            boolean exceptionIfMaximumExceeded) {
        super(sessionRegistry);
        this.sessionRegistry = sessionRegistry;
        this.setMaximumSessions(maximumSessions);
        this.setExceptionIfMaximumExceeded(exceptionIfMaximumExceeded);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy#getMaximumSessionsForThisUser(org.springframework.security.core.Authentication)
     */
    @Override
    protected int getMaximumSessionsForThisUser(Authentication authentication) {
        return -1;
        //		if (authentication.getPrincipal() instanceof UserDetails) {
        //			if (((UserDetails) authentication.getPrincipal()).isAdmin()) {
        //				return -1;
        //			}
        //		}
        //		return super.getMaximumSessionsForThisUser(authentication);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy#onAuthentication(org.springframework.security.core.Authentication, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.onAuthentication(authentication, request, response);

        // 检测许可证并强制退出超出的会话
        checkLicenseAndExpireSessionsExceededIfRequire();
    }

    /**
     * 检测许可证并强制退出超出的会话
     */
    private void checkLicenseAndExpireSessionsExceededIfRequire() {
        // logger.info("当前系统运行环境：{} -> 校验许可证 ", Config.getAppEnv());
        boolean isProduction = false;
        try {
            // 产品版本需要校验license
            Class.forName("com.wellsoft.pt.servlet.ProductionServletInitializer");
            isProduction = true;
            // 强制退出超出的会话
            // expireSessionsExceededIfRequire(3);
        } catch (Exception e) {
        }
        if (isProduction) {
            try {
                checkLicense();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 强制退出超出的会话
     *
     * @param allowableSessions
     */
    private void expireSessionsExceededIfRequire(int allowableSessions) {
        List<Object> principals = sessionRegistry.getAllPrincipals();
        if (principals.size() < allowableSessions) {
            return;
        }
        throw new SessionAuthenticationException(messages.getMessage(
                "ConcurrentSessionControlStrategy.exceededMaxSessions",
                new Object[]{Integer.valueOf(allowableSessions)},
                "Maximum sessions of {0} exceeded"));
    }

    /**
     * @return
     */
    private synchronized void checkLicense() {
        if (!isCheckedLicense) {
            Stopwatch timer = Stopwatch.createStarted();
            try {
                logger.info("开始校验授权证书");
                Thread.currentThread().getContextClassLoader()
                        .loadClass("com.wellsoft.pt.servlet.License").newInstance();
                isCheckedLicense = true;
            } catch (Exception e) {
                isCheckedLicense = false;
                throw new SessionAuthenticationException(messages.getMessage("spring.security.license.error", "无效授权证书"));
            } finally {
                logger.info("结束校验授权证书，耗时：{}", timer.stop());
            }
        }
    }

}

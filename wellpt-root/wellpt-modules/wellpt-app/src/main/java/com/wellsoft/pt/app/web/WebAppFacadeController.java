/*
 * @(#)2016年3月25日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.context.AppContextRepository;
import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.service.AppSystemService;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.URLEncoder;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期            修改内容
 * 2016年3月25日.1 zhulh       2016年3月25日      Create
 * </pre>
 * @date 2016年3月25日
 */
public class WebAppFacadeController extends AbstractController {

    @Autowired
    private AppPageDefinitionService appPageDefinitionService;

    @Autowired
    private AppSystemService appSystemService;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private AppContextRepository appContextRepository;

    public static View view(AppContext appContext, HttpServletRequest request) {
        return getUserPage(appContext, request);
    }

    /**
     * 获取用户页面
     *
     * @param appContext
     * @return
     */
    @SuppressWarnings("unchecked")
    private static View getUserPage(AppContext appContext, HttpServletRequest request) {
        UserAppData userAppData = appContext.getCurrentUserAppData();
        PiSystem piSystem = userAppData.getSystem();
        PiModule piModule = userAppData.getModule();
        PiApplication piApplication = userAppData.getApplication();
        String sysId = piSystem.getId();
        AppSystem appSystem = AppCacheUtils.getAppSystemById(sysId);
        AppPageDefinition appPageDefinition = AppCacheUtils.getUserAppPageDefinition(
                SpringSecurityUtils.getCurrentUserId(), userAppData.getAppPath(), userAppData.getPageUuid());
        if (appPageDefinition instanceof UnauthorizePageDefinition) {
            throw new AccessDeniedException("无权限访问页面");
        } else if (appPageDefinition instanceof NoPageDefinition) {
            throw new NotFoundException("尚未配置或指定默认页面，请先配置页面");
        }
        String pageUuid = appPageDefinition.getUuid();
        String id = appPageDefinition.getId();

        String title = piApplication != null ? piApplication.getTitle() : (piModule != null ? piModule.getTitle()
                : piSystem.getTitle());
        String theme = appSystem.getTheme();
        // 用户页面主题
        String portalTheme = appPageDefinition.getTheme();
        // 门户页面名称
        if (StringUtils.equals(SpringSecurityUtils.getCurrentUserId(), appPageDefinition.getUserId())) {
            title = appPageDefinition.getName();
        }
        // 页面指定的主题
        String sessionPortalTheme = ObjectUtils.toString(WebUtils.getSessionAttribute(request, "portalTheme_"
                + pageUuid));
        if (StringUtils.isBlank(sessionPortalTheme) && StringUtils.isNotBlank(appPageDefinition.getUserId())
                && StringUtils.isNotBlank(portalTheme)) {
            appContext.getCurrentUserAppData().setTheme(appContext.getTheme(portalTheme));
            WebUtils.setSessionAttribute(request, "portalTheme_" + pageUuid, portalTheme);
            theme = portalTheme;
        }
        WebUtils.setSessionAttribute(request, "portalPageUuid", pageUuid);

        String jsModule = appSystem.getJsModule();
        String definitionJson = appPageDefinition.getDefinitionJson();
        String wtype = appPageDefinition.getWtype();

        Class<?> viewClass = appContext.getComponent(wtype).getViewClass();
        Class<?>[] parameterTypes = new Class[]{String.class, String.class, String.class, String.class, String.class};
        try {
            Constructor<View> constructor = (Constructor<View>) viewClass.getConstructor(parameterTypes);
            View pageView = constructor.newInstance(id, title, theme, jsModule, definitionJson);
            return pageView;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (Config.isBackendServer()) {
            throw new InternalAuthenticationServiceException("");
        }
        AppContext contextBeforeGetPage = appContextRepository.loadContext(request, response);
        try {
            AppContextHolder.setContext(contextBeforeGetPage);

            String responseBody = null;
            UserAppData userAppData = contextBeforeGetPage.getCurrentUserAppData();
            if (AppCacheUtils.isCachedAppPage(userAppData)) {
                responseBody = AppCacheUtils.getAppPage(userAppData);
            }

            if (StringUtils.isBlank(responseBody)) {
                responseBody = getUserPage(contextBeforeGetPage, request).render(contextBeforeGetPage, request,
                        response);
                userAppData.setPageUuid(ObjectUtils.toString(WebUtils.getSessionAttribute(request, "portalPageUuid")));
            }

            // X-Frame-Options 响应头
            response.addHeader("X-Frame-Options", SystemParams.getValue("security.x.frame.options", "SAMEORIGIN"));

            response.setCharacterEncoding(Encoding.UTF8.getValue());
            response.setContentType(MediaType.TEXT_HTML_VALUE);
            Cookie currentUserId = new Cookie("cookie.current.userId", SpringSecurityUtils.getCurrentUserId());
            currentUserId.setPath(request.getContextPath().length() > 0 ? request.getContextPath() : "/");
            Cookie currentUsername = new Cookie("cookie.current.username", URLEncoder.encode(
                    SpringSecurityUtils.getCurrentLoginName(), "UTF-8"));
            currentUsername.setPath(request.getContextPath().length() > 0 ? request.getContextPath() : "/");
            response.addCookie(currentUserId);
            response.addCookie(currentUsername);
            Writer writer = response.getWriter();
            writer.write(responseBody);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            logger.error("页面渲染异常：", ex);
            ModelAndView view = new ModelAndView();
            if (ex instanceof AccessDeniedException) {
                view.setViewName("/pt/common/403");
            } else if (ex instanceof NotFoundException) {
                view.setViewName("/pt/common/404");
                return view;
            } else {
                view.setViewName("/pt/common/500");
            }
            return view;
        } finally {
            AppContext contextAftergetPage = AppContextHolder.getContext();
            AppContextHolder.clearContext();
            appContextRepository.saveContext(contextAftergetPage, request, response);
        }
        return null;
    }

}

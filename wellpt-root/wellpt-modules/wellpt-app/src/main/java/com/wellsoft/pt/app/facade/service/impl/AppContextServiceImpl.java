/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.google.common.base.Throwables;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.pt.app.bean.AppWidgetDefinitionBean;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.theme.Theme;
import com.wellsoft.pt.app.ui.*;
import com.wellsoft.pt.app.ui.client.WebAppPageDefinitionProxyPage;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author wujx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-08-19.1	t		2016-08-19		Create
 * </pre>
 * @date 2016-08-19
 */
@Service
public class AppContextServiceImpl implements AppContextService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppContext appContext;

    @Autowired
    private SecurityAuditFacadeService securityApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppContextService#getComponent(java.lang.String)
     */
    @Override
    public Component getComponent(String wtype) {
        return appContext.getComponent(wtype);
    }

    /**
     * 获取所有主题
     *
     * @param uuid
     * @return
     */
    public List<Theme> getAllThemes() {
        return appContext.getAllThemes();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppContextService#setThemeOfPage(java.lang.String, java.lang.String)
     */
    @Override
    public void setThemeOfPage(String themeId, String pageUuid) {
        // 设置门户页面主题
        WebUtils.setSessionAttribute(JsonDataServicesContextHolder.getRequest(), "portalTheme_" + pageUuid, themeId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppContextService#getPiItemByPiUuid(java.lang.String)
     */
    @Override
    public PiItem getPiItemByPiUuid(String piUuid) {
        return AppCacheUtils.getPiItem(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppContextService#getFunction(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public PiFunction getFunctionByPath(String piPath) {
        String piUuid = AppCacheUtils.getPiUuidByPath(piPath);
        PiFunction piFunction = appContext.getFunction(piUuid);
        if (piFunction == null) {
            return null;
        }
        // 功能类型
        // URL功能是为页面跳转，直接返回应用信息，跳转的URL权限由前台跳转控制，若有关联功能，立即进行权限判断
        if (isDebug() || isUrlFunction(piFunction)) {
            return piFunction;
        }

        if (!securityApiFacade.isGranted(piUuid, AppFunctionType.AppProductIntegration)) {
            PiFunction unAuthenticated = new PiFunction();
            BeanUtils.copyProperties(piFunction, unAuthenticated);
            unAuthenticated.setAuthenticated(false);//设置为无权限访问
            piFunction = unAuthenticated;
        }
        return piFunction;
    }

    private boolean isDebug() {
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes().getAttribute(
                    RequestContextListener.class.getName()
                            + ".REQUEST_ATTRIBUTES", 0);
            HttpServletRequest request = servletRequestAttributes.getRequest();
            return "true".equalsIgnoreCase(request.getHeader("debug"));
        } catch (Exception e) {
            logger.error("判断是否前端调试模式一次：", e);
        }
        return false;
    }

    /**
     * @param piFunction
     * @return
     */
    private boolean isUrlFunction(PiFunction piFunction) {
        // 菜单、URL请求、Web控制器
        String functionType = piFunction.getType();
        return AppFunctionType.MENU.equals(functionType) || AppFunctionType.URL.equals(functionType)
                || AppFunctionType.Controller.equals(functionType);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppContextService#getApplicationByPath(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public PiApplication getApplicationByPath(String piPath) {
        String piUuid = AppCacheUtils.getPiUuidByPath(piPath);
        // 应用不关联功能是为页面跳转，直接返回应用信息，跳转的URL权限由前台跳转控制，若有关联功能，立即进行权限判断
        PiApplication piApp = appContext.getApplication(piUuid);
        if (StringUtils.isNotBlank(piApp.getCfUuid())) {
            if (!securityApiFacade.isGranted(piUuid, AppFunctionType.AppProductIntegration)) {
                PiApplication unAuthenticated = new PiApplication();
                BeanUtils.copyProperties(piApp, unAuthenticated);
                unAuthenticated.setAuthenticated(false);//设置为未授权
                return unAuthenticated;
            }
            AppFunction appFunction = AppCacheUtils.getAppFunction(piApp.getCfUuid());
            if (appFunction != null) {
                PiFunction piFunction = new PiFunction();
                BeanUtils.copyProperties(appFunction, piFunction);
                piApp.setCfFunction(piFunction);
            }
        }
        return piApp;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppContextService#getAppWidgetDefinitionById(java.lang.String)
     */
    @Override
    public AppWidgetDefinition getAppWidgetDefinitionById(String appWidgetDefId, Boolean cloneable) {
        AppWidgetDefinition appWidgetDefinition = AppCacheUtils.getAppWidgetDefinitionById(appWidgetDefId);
        if (appWidgetDefinition == null) {
            return appWidgetDefinition;
        }

        AppWidgetDefinitionBean returnWidget = new AppWidgetDefinitionBean();
        BeanUtils.copyProperties(appWidgetDefinition, returnWidget);
        // 获取所有组件类型wtype、权限过滤后的JSON定义及组件动态渲染
        try {
            HttpServletRequest request = JsonDataServicesContextHolder.getRequest();
            HttpServletResponse response = JsonDataServicesContextHolder.getResponse();
            Widget widget = getWidget(appWidgetDefinition);
            // 页面定义组件的JS模块、组件解析依赖的JS模块
            List<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
            List<Widget> views = WidgetDefinitionUtils.extractWidgets(widget);
            for (View view : views) {
                String wtype = ((Widget) view).getWtype();
                // 页面定义组件的JS模块
                String wtypeJavaScriptModule = WidgetDefinitionUtils.getJavaScriptModuleByWtype(wtype);
                JavaScriptModule javaScriptModule = appContext.getJavaScriptModule(wtypeJavaScriptModule);
                // 组件解析依赖的JS模块
                UIDesignComponent component = appContext.getComponent(wtype);
                if (component != null) {
                    List<JavaScriptModule> dependencies = component.getExplainJavaScriptModules(appContext, view,
                            request, response);
                    if (CollectionUtils.isNotEmpty(dependencies)) {
                        javaScriptModule = RequireJsHelper.clone(javaScriptModule);
                        javaScriptModule.getDependencies().addAll(RequireJsHelper.getJavaScriptModuleIds(dependencies));
                        javaScriptModules.addAll(dependencies);
                    }
                }
                javaScriptModules.add(javaScriptModule);
            }
            Set<String> requireJavaScriptModules = new HashSet<String>();
            for (JavaScriptModule javaScriptModule : javaScriptModules) {
                requireJavaScriptModules.add(javaScriptModule.getId());
            }
            returnWidget.setWtype(StringUtils.join(requireJavaScriptModules, Separator.COMMA.getValue()));
            returnWidget.setRequireJavaScriptModules(requireJavaScriptModules.toArray(new String[]{}));
            returnWidget.setConfigScript(RequireJsHelper.getConfigScript(request, javaScriptModules));

            Page page = getProxyPage(widget);
            String template = appWidgetDefinition.getHtml();
            returnWidget.setDefinitionJson(page.getDefinitionJson());
            returnWidget.setHtml(page.renderViews(template, appContext, request, response));
            // 组件复制
            if (Boolean.TRUE.equals(cloneable)) {
                ModifiableWidgetDefinitionView modifiableWidgetDefinitionView = WidgetDefinitionUtils.copyWidget(
                        returnWidget.getDefinitionJson(), returnWidget.getHtml());
                returnWidget.setId(modifiableWidgetDefinitionView.getId());
                returnWidget.setHtml(modifiableWidgetDefinitionView.getAttribute(AppConstants.KEY_HTML));
                modifiableWidgetDefinitionView.removeAttribute(AppConstants.KEY_HTML);
                returnWidget.setDefinitionJson(modifiableWidgetDefinitionView.getDefinitionJson());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return returnWidget;
    }

    /**
     * 根据组件定义ID获取移动端定义信息
     *
     * @param appWidgetDefId
     * @return
     */
    @Override
    public AppWidgetDefinition getUniAppWidgetDefinitionById(String appWidgetDefId) {
        AppWidgetDefinition appWidgetDefinition = AppCacheUtils.getAppWidgetDefinitionById(appWidgetDefId);
        if (appWidgetDefinition == null) {
            return appWidgetDefinition;
        }
        AppPageDefinitionService appPageDefinitionService = ApplicationContextHolder.getBean(AppPageDefinitionService.class);
        AppPageDefinition appPageDefinition = appPageDefinitionService.getOne(appWidgetDefinition.getAppPageUuid());
        if (appPageDefinition == null) {
            return appWidgetDefinition;
        }
        String uniAppDefinitionJson = appPageDefinition.getUniAppDefinitionJson();
        if (StringUtils.isBlank(uniAppDefinitionJson)) {
            return appWidgetDefinition;
        }

        try {
            Widget widget = WidgetDefinitionUtils.parseWidget(uniAppDefinitionJson, ReadonlyWidgetDefinitionView.class);
            List<Widget> widgets = WidgetDefinitionUtils.extractWidgets(widget);
            widgets = widgets.stream().filter(w -> w.getId().equals(appWidgetDefId)).collect(Collectors.toList());
            if (CollectionUtils.size(widgets) == 1) {
                AppWidgetDefinition returnWidget = new AppWidgetDefinition();
                BeanUtils.copyProperties(appWidgetDefinition, returnWidget);
                returnWidget.setDefinitionJson(widgets.get(0).getDefinitionJson());
                return returnWidget;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return appWidgetDefinition;
    }

    /**
     * @param widget
     * @return
     * @throws Exception
     */
    private WebAppPageDefinitionProxyPage getProxyPage(Widget widget) throws Exception {
        if (widget instanceof WebAppPageDefinitionProxyPage) {
            return (WebAppPageDefinitionProxyPage) widget;
        }
        String id = widget.getId();
        String title = widget.getTitle();
        String theme = null;
        String jsModule = null;
        String definitionJson = widget.getDefinitionJson();
        WebAppPageDefinitionProxyPage proxyPage = new WebAppPageDefinitionProxyPage(id, title, theme, jsModule,
                definitionJson);
        return proxyPage;
    }

    /**
     * @param appWidgetDefinition
     * @return
     */
    @SuppressWarnings("unchecked")
    private Widget getWidget(AppWidgetDefinition appWidgetDefinition) {
        Class<?> viewClass = appContext.getComponent(appWidgetDefinition.getWtype()).getViewClass();
        Class<?>[] parameterTypes = new Class[]{String.class};
        try {
            Constructor<View> constructor = (Constructor<View>) viewClass.getConstructor(parameterTypes);
            View view = constructor.newInstance(appWidgetDefinition.getDefinitionJson());
            return (Widget) view;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppContextService#getJavaScriptModuleConfigScript(java.util.List)
     */
    @Override
    public String getJavaScriptModuleConfigScript(List<String> modules) {
        List<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
        for (String module : modules) {
            JavaScriptModule javaScriptModule = appContext.getJavaScriptModule(module);
            if (javaScriptModule != null) {
                javaScriptModules.add(javaScriptModule);
            }
        }
        HttpServletRequest request = JsonDataServicesContextHolder.getRequest();
        String isMobileAppString = request.getParameter(AppConstants.KEY_IS_MOBILE_APP);
        if (Config.TRUE.equalsIgnoreCase(isMobileAppString)) {
            appContext.setMobileApp(true);
        }
        String configScript = null;
        try {
            configScript = RequireJsHelper.getConfigScript(request, javaScriptModules, true);
        } catch (Exception e) {
            logger.error("加载脚本{}异常：{}", modules, Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e);
        } finally {
            appContext.setMobileApp(false);
        }
        return configScript;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppContextService#getJavaScriptTemplateById(java.lang.String)
     */
    @Override
    public JavaScriptTemplate getJavaScriptTemplateById(String templateId) {
        return appContext.getJavaScriptTemplate(templateId);
    }

}

/*
 * @(#)2016年5月7日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.web;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.bean.AppPageDefinitionBean;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.context.AppContextRepository;
import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.design.container.AbstractContainer;
import com.wellsoft.pt.app.design.container.Container;
import com.wellsoft.pt.app.design.container.DefaultPageContainer;
import com.wellsoft.pt.app.design.container.LayoutitPageContainer;
import com.wellsoft.pt.app.design.support.ComponentUsageFrequencyComparator;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.service.AppSystemService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.ui.*;
import com.wellsoft.pt.app.web.interceptor.WebAppFacadeInterceptor;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 页面设计器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月7日.1	zhulh		2016年5月7日		Create
 * </pre>
 * @date 2016年5月7日
 */
@Controller
@RequestMapping("/web/app/page")
public class WebAppDesignerController extends BaseController {

    // 组件按使用频率排序开关
    private static final String KEY_APP_DESIGNER_COMPONENT_ORDER_BY_USAGE_FREQUENCY = "app.designer.component.order_by.usage_frequency";

    private static final String PI_PATH_REQUEST_ATTRIBUTE_NAME = WebAppFacadeInterceptor.PI_PATH_REQUEST_ATTRIBUTE_NAME;
    private final String[] PAGE_TYPES = new String[]{"jsp", "html"};
    @Autowired
    private AppPageDefinitionMgr appPageDefinitionMgr;
    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;
    @Autowired
    private AppProductIntegrationService appProductIntegrationService;
    @Autowired
    private AppSystemService appSystemService;
    @Autowired
    private AppContextRepository appContextRepository;
    @Autowired
    private List<AbstractContainer> pageContainers;

    /**
     * 打开前台主页应用页面
     *
     * @return
     */
    @RequestMapping(value = "/config/{piUuid}")
    public String config(@PathVariable("piUuid") String piUuid,
                         @RequestParam(value = "pageUuid", required = false) String pageUuid,
                         @RequestParam(value = "pageWtype", defaultValue = "") String pageWtype, HttpServletRequest request,
                         HttpServletResponse response, Model model) {
        AppProductIntegration appProductIntegration = appProductIntegrationService.get(piUuid);
        String appPageUuid = pageUuid;
        if (StringUtils.isBlank(appPageUuid)) {
            appPageUuid = appProductIntegration.getAppPageUuid();
        }
        if (appProductIntegration == null) {
            return forward("/pt/common/404");
        }
        if (Boolean.TRUE.equals(appProductIntegration.getAppPageReference())) {
            throw new RuntimeException("不能对引用的页面进行配置！");
        }
        if (StringUtils.isNotBlank(pageUuid)) {
            AppPageDefinitionBean bean = appPageDefinitionMgr.getBean(pageUuid);
            String wtype = bean.getWtype();
            if (StringUtils.isNotBlank(wtype)) {
                pageWtype = wtype;
            }
        }
        if (StringUtils.isNotBlank(pageWtype) && LayoutitPageContainer.PAGE_TYPE.equals(pageWtype)) {
            return layoutitContainer(piUuid, appPageUuid, pageWtype, request, response, model);
        }
        return defaultContainer(piUuid, appPageUuid, pageWtype, request, response, model);
    }

    /**
     * 打开前台默认主页应用页面
     *
     * @return
     */
    @RequestMapping(value = "/defaultContainer/{piUuid}")
    public String defaultContainer(@PathVariable("piUuid") String piUuid,
                                   @RequestParam(value = "pageUuid", required = false) String pageUuid,
                                   @RequestParam(value = "pageWtype", defaultValue = "") String pageWtype, HttpServletRequest request,
                                   HttpServletResponse response, Model model) {
        String containerWtype = pageWtype;
        AppProductIntegration appProductIntegration = appProductIntegrationService.get(piUuid);
        String productUuid = appProductIntegration.getAppProductUuid();
        String piPath = appProductIntegration.getDataPath();
        String appPageUuid = pageUuid;
        if (StringUtils.isBlank(appPageUuid)) {
            appPageUuid = appProductIntegration.getAppPageUuid();
        }
        if (Boolean.TRUE.equals(appProductIntegration.getAppPageReference())) {
            throw new RuntimeException("不能对引用的页面进行配置！");
        }
        // 获取页面容器
        AbstractContainer container = null;
        AppPageDefinitionBean pageDefinitionBean = null;
        if (StringUtils.isNotBlank(appPageUuid)) {
            pageDefinitionBean = appPageDefinitionMgr.getBean(appPageUuid);
            String wtype = pageDefinitionBean.getWtype();
            if (StringUtils.isNotBlank(wtype)) {
                containerWtype = wtype;
            }
            container = getPageContainer(containerWtype);
        } else if (StringUtils.isNotBlank(containerWtype)) {
            container = getPageContainer(containerWtype);
        } else {
            container = getPageContainer(null);
        }

        // 页面容器定义JS模块
        List<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
        javaScriptModules.add(container.getJavaScriptModule());

        // 页面容器定义CSS
        List<CssFile> cssFiles = container.getDefineCssFiles();

        // 获取页面容器要加载的组件并分组
        Map<String, UIDesignComponent> uiDesignComponentMap = getContainerComponents(pageDefinitionBean, container);
        Map<ComponentCategory, List<UIDesignComponent>> componentMap = new LinkedHashMap<ComponentCategory, List<UIDesignComponent>>();
        Map<String, Object> defaults = new HashMap<String, Object>();
        for (Entry<String, UIDesignComponent> entry : uiDesignComponentMap.entrySet()) {
            UIDesignComponent component = entry.getValue();
            ComponentCategory category = component.getCategory();
            if (category == null) {
                continue;
            }
            if (!componentMap.containsKey(category)) {
                componentMap.put(category, new ArrayList<UIDesignComponent>());
            }
            componentMap.get(category).add(component);
            defaults.put(component.getType(), component.getDefaultOptions());
            // 组件定义JS模块
            javaScriptModules.add(component.getJavaScriptModule());

            // 组件解析的JS模块
            String wtypeJcriptModule = WidgetDefinitionUtils.getJavaScriptModuleByWtype(component.getType());
            // 支持组件所见即所得，加载相应的组件解析JS模块
            if (container.supportsWysiwyg()) {
                javaScriptModules.add(AppContextHolder.getContext().getJavaScriptModule(wtypeJcriptModule));
            }

            // 组件所需CSS
            cssFiles.addAll(component.getDefineCssFiles());
        }

        // 加载解析的JS模块
        if (pageDefinitionBean != null && StringUtils.isNotBlank(pageDefinitionBean.getDefinitionJson())
                && container.supportsWysiwyg()) {
            Page page = getWysiwygPage(containerWtype, pageDefinitionBean);
            List<View> views = page.getViews();
            for (View view : views) {
                String wtype = ((Widget) view).getWtype();
                UIDesignComponent component = AppContextHolder.getContext().getComponent(wtype);
                if (component != null) {
                    javaScriptModules.addAll(component.getExplainJavaScriptModules(AppContextHolder.getContext(), view,
                            request, response));
                }
            }
        }

        // 组件排序
        boolean useComponentUsageFrequency = Boolean.valueOf(SystemParams.getValue(
                KEY_APP_DESIGNER_COMPONENT_ORDER_BY_USAGE_FREQUENCY, Boolean.TRUE.toString()));
        if (useComponentUsageFrequency) {
            // 按组件使用的频率(组件定义的总数)排序
            Map<String, Long> widgetUsageFrequency = appWidgetDefinitionService.getWidgetUsageFrequency();
            ComponentUsageFrequencyComparator componentUsageFrequencyComparator = new ComponentUsageFrequencyComparator(
                    widgetUsageFrequency);
            for (Entry<ComponentCategory, List<UIDesignComponent>> entry : componentMap.entrySet()) {
                Collections.sort(entry.getValue(), componentUsageFrequencyComparator);

            }
        } else {
            // 按组件定义的序号排序
            for (Entry<ComponentCategory, List<UIDesignComponent>> entry : componentMap.entrySet()) {
                OrderComparator.sort(entry.getValue());
            }
        }

        // 组件分类排序
        List<ComponentCategory> keys = Arrays.asList(componentMap.keySet().toArray(new ComponentCategory[0]));
        OrderComparator.sort(keys);
        Map<ComponentCategory, List<UIDesignComponent>> tmpMap = new LinkedHashMap<ComponentCategory, List<UIDesignComponent>>();
        for (ComponentCategory componentCategory : keys) {
            tmpMap.put(componentCategory, componentMap.get(componentCategory));
        }
        componentMap = tmpMap;

        // 加载当前用户上下文数据
        request.setAttribute(PI_PATH_REQUEST_ATTRIBUTE_NAME, piPath);
        AppContext appContext = appContextRepository.loadContext(request, response);
        UserAppData userAppData = null;
        try {
            userAppData = appContext.getCurrentUserAppData();
        } finally {
            AppContextHolder.clearContext();
        }
        String ctx = request.getContextPath();
        StringBuilder webAppScript = new StringBuilder();
        webAppScript.append("window.ctx = \"" + ctx + "\";");
        webAppScript.append("var WebApp = WebApp || {};");
        webAppScript.append("WebApp.currentUserAppData = " + userAppData.toJsonString() + ";");

        StringBuilder callbackScript = new StringBuilder();// "require([ \"/resources/pt/js/app/app.js\" ]);";
        callbackScript.append("appContext.init(WebApp.currentUserAppData);");
        JavaScriptModule clientConfigJavaScriptModule = getClientConfigJavaScriptModule(container, appContext);
        callbackScript.append("requirejs([ '" + ctx + clientConfigJavaScriptModule.getPath() + ".js' ]);");

        // 页面设计器JS模块
        javaScriptModules.add(getAppDesignerJavaScriptModule(container, appContext));
        // 配置的JS模块
        List<JavaScriptModule> configScriptModules = new ArrayList<JavaScriptModule>();
        configScriptModules.addAll(javaScriptModules);
        // 支持组件所见即所得，加载系统下配置的JS模块
        if (container.supportsWysiwyg()) {
            PiSystem piSystem = appContext.getCurrentUserAppData().getSystem();
            configScriptModules.addAll(RequireJsHelper.getJavaScriptModules(AppCacheUtils
                    .getJavaScriptModuleFunctionBySystem(piSystem)));
        }
        String configScript = RequireJsHelper.getConfigScript(request, configScriptModules);
        String requireScript = RequireJsHelper.getRequireScript(javaScriptModules, callbackScript.toString());

        String componentCss = CssHelper.getCssImport(request, cssFiles);
        String appPageConfigCss = "<link href=\"" + request.getContextPath()
                + "/resources/pt/css/app/design/app_page_config.css\" rel=\"stylesheet\">";

        model.addAttribute("configScript", configScript);
        model.addAttribute("webAppScript", webAppScript);
        model.addAttribute("requireScript", requireScript);
        model.addAttribute("javaScriptModules", javaScriptModules);

        model.addAttribute("componentCss", componentCss);
        model.addAttribute("appPageConfigCss", appPageConfigCss);

        model.addAttribute("container", container);
        model.addAttribute("containerDefaultOptions", JsonUtils.object2Json(container.getDefaultOptions()));
        model.addAttribute("containerJsModule", container.getJavaScriptModule().getId());
        model.addAttribute("containerSupportsWysiwyg", container.supportsWysiwyg());
        model.addAttribute("componentMap", componentMap);
        model.addAttribute("productUuid", productUuid);
        model.addAttribute("piUuid", piUuid);
        model.addAttribute("pageUuid", appPageUuid);
        model.addAttribute("title", appProductIntegration.getDataName());
        model.addAttribute("defaultOptions", JsonUtils.object2Json(defaults));
        return "app/design/app_page_config";
    }

    /**
     * @param container
     * @param appContext
     * @return
     */
    protected JavaScriptModule getAppDesignerJavaScriptModule(AbstractContainer container, AppContext appContext) {
        return appContext.getJavaScriptModule("AppPageDesigner");
    }

    /**
     * @param container
     * @param appContext
     * @return
     */
    protected JavaScriptModule getClientConfigJavaScriptModule(Container container, AppContext appContext) {
        return appContext.getJavaScriptModule("app_page_config");
    }

    /**
     * 如何描述该方法
     *
     * @param pageDefinitionBean
     * @return
     */
    private Page getWysiwygPage(String wtype, AppPageDefinitionBean pageDefinitionBean) {
        String componentType = wtype;
        if (StringUtils.isBlank(componentType)) {
            componentType = pageDefinitionBean.getWtype();
        }
        Class<?> viewClass = AppContextHolder.getContext().getComponent(componentType).getViewClass();
        Class<?>[] parameterTypes = new Class[]{String.class, String.class, String.class, String.class, String.class};
        try {
            String id = pageDefinitionBean.getId();

            String title = StringUtils.EMPTY;
            String theme = StringUtils.EMPTY;
            String jsModule = StringUtils.EMPTY;
            String definitionJson = pageDefinitionBean.getDefinitionJson();
            Constructor<View> constructor = (Constructor<View>) viewClass.getConstructor(parameterTypes);
            View pageView = constructor.newInstance(id, title, theme, jsModule, definitionJson);
            return (Page) pageView;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param container
     * @return
     */
    private Map<String, UIDesignComponent> getContainerComponents(AppPageDefinitionBean pageDefinitionBean,
                                                                  AbstractContainer container) {
        Map<String, UIDesignComponent> uiDesignComponentMap = ApplicationContextHolder.getApplicationContext()
                .getBeansOfType(UIDesignComponent.class);
        List<UIDesignComponent> pageComponents = new ArrayList<UIDesignComponent>();
        // 获取页面定义JSON中已经定义的组件
        if (pageDefinitionBean != null && StringUtils.isNotBlank(pageDefinitionBean.getDefinitionJson())) {
            String definitionJson = pageDefinitionBean.getDefinitionJson();
            List<Widget> widgets = WidgetDefinitionUtils.extractWidgets(WidgetDefinitionUtils.parseWidget(
                    definitionJson, ReadonlyWidgetDefinitionView.class));
            for (Widget widget : widgets) {
                UIDesignComponent pageComponent = AppContextHolder.getContext().getComponent(widget.getWtype());
                if (pageComponent != null) {
                    pageComponents.add(pageComponent);
                }
            }
        }

        List<UIDesignComponent> components = container.getComponents();
        pageComponents.addAll(components);
        if (pageComponents.isEmpty()) {
            return uiDesignComponentMap;
        }

        Map<String, UIDesignComponent> returnComponentMap = new HashMap<String, UIDesignComponent>();
        for (Entry<String, UIDesignComponent> entry : uiDesignComponentMap.entrySet()) {
            UIDesignComponent component = entry.getValue();
            if (pageComponents.contains(component)) {
                returnComponentMap.put(entry.getKey(), component);
            }
        }

        if (returnComponentMap.isEmpty()) {
            return uiDesignComponentMap;
        }
        return returnComponentMap;
    }

    /**
     * 打开前台主页应用页面
     *
     * @return
     */
    @RequestMapping(value = "/layoutitContainer/{piUuid}")
    public String layoutitContainer(@PathVariable("piUuid") String piUuid,
                                    @RequestParam(value = "pageUuid", required = false) String pageUuid,
                                    @RequestParam(value = "pageWtype", defaultValue = "") String pageWtype, HttpServletRequest request,
                                    HttpServletResponse response, Model model) {
        defaultContainer(piUuid, pageUuid, pageWtype, request, response, model);

        // 页面容器定义JS模块
        Map<String, Object> lmodel = (Map<String, Object>) model.asMap();
        @SuppressWarnings("unchecked")
        List<JavaScriptModule> javaScriptModules = (List<JavaScriptModule>) lmodel.get("javaScriptModules");
        String ctx = request.getContextPath();
        StringBuilder callbackScript = new StringBuilder();// "require([ \"/resources/pt/js/app/app.js\" ]);";
        callbackScript.append("appContext.init(WebApp.currentUserAppData);");
        callbackScript.append("requirejs([ '" + ctx + "/resources/pt/js/app/design/app_config.js' ]);");
        String requireScript = RequireJsHelper.getRequireScript(javaScriptModules, callbackScript.toString());
        model.addAttribute("requireScript", requireScript);

        String appPageConfigCss = "<link href=\"" + request.getContextPath()
                + "/resources/pt/css/app/design/app_config.css\" rel=\"stylesheet\">";
        model.addAttribute("appPageConfigCss", appPageConfigCss);
        return "app/design/app_config";
    }

    /**
     * @param wtype
     * @return
     */
    private AbstractContainer getPageContainer(String wtype) {
        if (StringUtils.isBlank(wtype)) {
            return ApplicationContextHolder.getBean(DefaultPageContainer.class);
        }
        for (AbstractContainer abstractContainer : pageContainers) {
            if (StringUtils.equals(abstractContainer.getType(), wtype)) {
                return abstractContainer;
            }
        }
        return null;
    }

    /**
     * 预览
     *
     * @return
     */
    @RequestMapping(value = "/preview/{piUuid}")
    public String preview(@PathVariable("piUuid") String piUuid,
                          @RequestParam(value = "pageUuid", required = false) String pageUuid, HttpServletRequest request,
                          HttpServletResponse response, Model model) {
        AppProductIntegration appProductIntegration = appProductIntegrationService.get(piUuid);
        String dataPath = appProductIntegration.getDataPath();
        String redirectUrl = AppConstants.WEB_APP_PATH + dataPath + AppConstants.DOT_HTML;
        if (StringUtils.isNotBlank(pageUuid)) {
            redirectUrl += "?pageUuid=" + pageUuid;
        }
        return redirect(redirectUrl);
    }

    /**
     * 打开配置页
     *
     * @param conponentType
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/configurer/{conponentType}")
    public String configurer(@PathVariable("") String conponentType, HttpServletRequest request,
                             HttpServletResponse response, Model model) {
        return "/app/design/component/" + conponentType;
    }

    @RequestMapping(value = "/configurer/{category}/{componentType}")
    public String categoryConfigurer(@PathVariable String category, @PathVariable String componentType,
                                     HttpServletRequest request, HttpServletResponse response, Model model) {

        return "/app/design/component/" + category + "/" + componentType;
    }

}

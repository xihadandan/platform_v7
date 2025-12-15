package com.wellsoft.pt.app.design.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ApiCodeEnum;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.bean.AppPageDefinitionPathBean;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.context.AppContextRepository;
import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.app.dto.AppSystemAuthPageDto;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.entity.AppSystemPageSettingEntity;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.AppFunctionSourceManager;
import com.wellsoft.pt.app.function.ext.JavaScriptModuleAppFunctionSourceLoader;
import com.wellsoft.pt.app.js.RequireJSJavaScriptModule;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.web.interceptor.WebAppFacadeInterceptor;
import com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService;
import com.wellsoft.pt.security.core.userdetails.InternetUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年05月07日   chenq	 Create
 * </pre>
 */
@RestController
@RequestMapping("/webapp")
public class WebAppPageController extends BaseController {

    @Autowired
    AppContextRepository appContextRepository;

    @Autowired
    AppContextService appContextService;

    @Autowired
    AppContext appContext;

    @Autowired
    JavaScriptModuleAppFunctionSourceLoader javaScriptModuleAppFunctionSourceLoader;
    @Autowired
    AppFunctionSourceManager appFunctionSourceManager;
    @Autowired
    AppProductIntegrationService appProductIntegrationService;
    @Autowired
    AppFunctionService appFunctionService;
    @Autowired
    AppPageDefinitionMgr appPageDefinitionMgr;
    @Autowired
    SecurityApiFacade securityApiFacade;
    @Autowired
    AppProdVersionService appProdVersionService;
    @Autowired
    AppSystemInfoService appSystemInfoService;
    @Autowired
    AppPageDefinitionService appPageDefinitionService;

    @Autowired
    CdUserPreferencesFacadeService cdUserPreferencesFacadeService;

    @Autowired
    SecurityMetadataSourceService securityMetadataSourceService;

    @PostMapping("/registerJavascriptFunction")
    public @ResponseBody
    AppFunctionSource registerJavascriptAppFucntion(@RequestBody RequireJSJavaScriptModule jsModule) {
        AppFunctionSource appFunctionSource = javaScriptModuleAppFunctionSourceLoader.convert2AppFunctionSource(jsModule);
        appFunctionSourceManager.saveConvert2AppFunction(appFunctionSource);
        /*if(StringUtils.isNotBlank(piUuid)) {
            appProductIntegrationService.addAppFunction(piUuid, appFunctionService.getOne(appFunctionSource.getUuid()), false);
        }*/
        return appFunctionSource;
    }


    @GetMapping("resolveWidgetView")
    public @ResponseBody
    AppWidgetDefinition resolveWidgetView(@RequestParam String id, @RequestParam(required = false) Boolean isClone, HttpServletRequest request, HttpServletResponse response) {
        try {
            AppWidgetDefinition appWidgetDefinition = AppCacheUtils.getAppWidgetDefinitionById(id);
            if (appWidgetDefinition == null) {
                return null;
            }
            //TODO: isClone
            return appWidgetDefinition;
        } catch (Exception e) {
            logger.error("组件视图解析异常：", e);
        }
        return null;
    }

    @GetMapping(value = "/getUserAppData")
    public ResultMessage getUserAppData(@RequestParam("appPiPath") String appPiPath, HttpServletRequest request, HttpServletResponse response) {
        ResultMessage message = new ResultMessage();
        message.setCode(200);
        Map<String, Object> data = Maps.newHashMap();
        message.setData(data);
        UserAppData userAppData = null;
        AppContext appContext = null;
        try {
            request.setAttribute(WebAppFacadeInterceptor.PI_PATH_REQUEST_ATTRIBUTE_NAME, appPiPath);
            appContext = appContextRepository.loadContext(request, response);
            userAppData = appContext.getCurrentUserAppData();
            data.put("currentUserAppData", userAppData);
        } catch (Exception e) {
            //TODO:
        } finally {
            AppContextHolder.clearContext();
        }
        return message;
    }


    @GetMapping(value = "/get")
    public ResultMessage page(@RequestParam("appPiPath") String appPiPath, @RequestParam(value = "pageUuid", required = false) String pageUuid,
                              @RequestParam(value = "uniApp", required = false, defaultValue = "false") Boolean uniApp, HttpServletRequest request, HttpServletResponse response) {
        ResultMessage message = new ResultMessage();
        message.setCode(200);
        Map<String, Object> data = Maps.newHashMap();
        message.setData(data);
        try {
            request.setAttribute(WebAppFacadeInterceptor.PI_PATH_REQUEST_ATTRIBUTE_NAME, appPiPath);
            AppContext appContext = appContextRepository.loadContext(request, response);
            UserAppData userAppData = appContext.getCurrentUserAppData();
            data.put("currentUserAppData", userAppData);


            PiSystem piSystem = userAppData.getSystem();
            PiModule piModule = userAppData.getModule();
            PiApplication piApplication = userAppData.getApplication();
            AppPageDefinition appPageDefinition = AppCacheUtils.getUserAppPageDefinition(
                    SpringSecurityUtils.getCurrentUserId(), userAppData.getAppPath(), userAppData.getPageUuid());
            if (appPageDefinition instanceof UnauthorizePageDefinition) {
                throw new AccessDeniedException("无权限访问页面");
            } else if (appPageDefinition instanceof NoPageDefinition) {
                throw new NotFoundException("尚未配置或指定默认页面，请先配置页面");
            }
            String id = appPageDefinition.getId();

            String title = piApplication != null ? piApplication.getTitle() : (piModule != null ? piModule.getTitle()
                    : piSystem.getTitle());
            // 门户页面名称
            if (StringUtils.equals(SpringSecurityUtils.getCurrentUserId(), appPageDefinition.getUserId())) {
                title = appPageDefinition.getName();
            }

            String definitionJson = appPageDefinition.getDefinitionJson();
            // 旧的手机端页面取转uni-app页面定义json
            if (BooleanUtils.isTrue(uniApp) && StringUtils.equals(appPageDefinition.getWtype(), "wMobilePage")) {
                definitionJson = appPageDefinition.getUniAppDefinitionJson();
            }

            JSONObject jsonObject = new JSONObject(definitionJson);// 计算视图下各组件的json
            data.put("html", appPageDefinition.getHtml());
            data.put("wtype", appPageDefinition.getWtype());
            jsonObject.remove(AppConstants.KEY_HTML);
            data.put("definitionJson", definitionJson);
            data.put("id", jsonObject.getString(AppConstants.KEY_ID));
            data.put("title", title);
            data.put("system", userAppData.getSystem().getId());// 返回系统ID

            if (appContext.isMobileApp() || "wMobilePage".equalsIgnoreCase(appPageDefinition.getWtype())) {// 手机端为单页应用，需要加载系统应用下配置的所有脚本
                piSystem = appContext.getCurrentUserAppData().getSystem();
                List<PiFunction> piFunctions = AppCacheUtils.getJavaScriptModuleFunctionBySystem(piSystem);
                Set<String> js = Sets.newHashSet();
                if (CollectionUtils.isNotEmpty(piFunctions)) {
                    for (PiFunction pi : piFunctions) {
                        js.add(pi.getId());
                    }
                    jsonObject.put("javascriptModules", js);
                }

                /*List<JavaScriptTemplate> javaScriptTemplates = RequireJsHelper.getJavaScriptTemplates(AppCacheUtils
                        .getJavaScriptTemplateFunctionBySystem(piSystem));
                Set<String> jsTmps = Sets.newHashSet();
                if(CollectionUtils.isNotEmpty(javaScriptTemplates)){
                    for(JavaScriptTemplate t:javaScriptTemplates){
                        jsTmps.add(t.getId());
                    }
                    jsonObject.put("javaScriptTemplates",jsTmps);
                }*/
            }

        } catch (Exception e) {
            logger.error("页面json渲染异常：", e);
            if (e instanceof AccessDeniedException) {
                message.setCode(403);
            } else if (e instanceof NotFoundException) {
                message.setCode(404);
            } else {
                message.setCode(400);
            }

        } finally {
            AppContextHolder.clearContext();
        }
        return message;
    }


    @RequestMapping("getLoginUserGrantedPageUrl")
    public @ResponseBody
    String getLoginUserGrantedPageUrl() {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (user == null) {
            return null;
        }
        // 互联网用户登录
        if (SpringSecurityUtils.getCurrentUser() instanceof InternetUserDetails) {
            Iterator iterator = user.getAuthorities().iterator();
            Set<String> roles = Sets.newHashSet();
            while (iterator.hasNext()) {
                roles.add(((GrantedAuthority) iterator.next()).getAuthority());
            }
            List<AppPageDefinition> appPageDefinitions = appPageDefinitionMgr.queryByRoleIds(roles);
            AppProductIntegration appProductIntegration = appProductIntegrationService.get(appPageDefinitions.get(0).getAppPiUuid());
            String url = appProductIntegration.getDataPath() + AppConstants.DOT_HTML + "?pageUuid=" + appPageDefinitions.get(0).getUuid();
            return url.startsWith("/") ? url.substring(1) : url;
        }
        List<AppPageDefinitionPathBean> appPageDefinitionList = appPageDefinitionMgr.listFacade();
        if (appPageDefinitionList.size() > 0) {
            AppPageDefinitionPathBean item = appPageDefinitionList.get(0);
            return item.getUrl();
        }
        return null;
    }


    @GetMapping("/authenticatePage")
    public ApiResult authenticatePage(@RequestParam(required = false) String uuid, @RequestParam(required = false) String id) {
        if (StringUtils.isBlank(uuid) && StringUtils.isBlank(id)) {
            return ApiResult.fail(ApiCodeEnum.ARGUMENTS_ERROR);
        }
        try {
            AppPageDefinition appPageDefinition = appPageDefinitionMgr.authenticatePage(uuid, id);
            return ApiResult.success(appPageDefinition);
        } catch (InsufficientAuthenticationException e) {
            logger.error("查询授权页面异常: ", e);
            return ApiResult.build(ApiCodeEnum.RESOURCE_INSUFFICIENT_AUTHENTICATED.getCode(), e.getMessage(), null);
        } catch (RuntimeException runtimeException) {
            logger.error("查询授权页面异常: ", runtimeException);
            return ApiResult.build(ApiCodeEnum.API_SYSTEM_ERROR.getCode(), runtimeException.getMessage(), null);
        }
    }

    @GetMapping("/getAnonymousPage")
    public ApiResult getAnonymousPage(@RequestParam(required = false) String uuid, @RequestParam(required = false) String id) {
        if (StringUtils.isBlank(uuid) && StringUtils.isBlank(id)) {
            return ApiResult.fail(ApiCodeEnum.ARGUMENTS_ERROR);
        }
        try {
            AppPageDefinition page = null;
            if (StringUtils.isNotBlank(uuid)) {
                page = appPageDefinitionService.getOne(uuid);
            } else if (StringUtils.isNotBlank(id)) {
                page = appPageDefinitionService.getLatestPageDefinition(id);
            }
            if (page == null) {
                return ApiResult.build(ApiCodeEnum.API_RESOURCE_NOT_EXIST.getCode(), "页面不存在", null);
            }

            if (page.getIsAnonymous()) {
                AppSystemAuthPageDto dto = new AppSystemAuthPageDto();
                dto.setPageUuid(page.getUuid());
                dto.setPageId(page.getId());
                dto.setTitle(page.getTitle());
                dto.setPageName(page.getName());
                dto.setDefinitionJson(page.getDefinitionJson());
                if (StringUtils.isNotBlank(page.getAppId())) {
                    dto.setPageSetting(new AppSystemPageSettingEntity());
                    dto.setSystem(page.getAppId());
                    dto.setTheme(appSystemInfoService.getSystemPageTheme(page.getId(), page.getTenant(), page.getAppId()));
                    dto.setPageSetting(appSystemInfoService.getSystemPageSetting(Config.DEFAULT_TENANT, page.getAppId()));
                }

                return ApiResult.success(dto);
            } else {
                return ApiResult.build(ApiCodeEnum.RESOURCE_INSUFFICIENT_AUTHENTICATED.getCode(), "无权限", null);
            }
        } catch (InsufficientAuthenticationException e) {
            logger.error("查询授权页面异常: ", e);
            return ApiResult.build(ApiCodeEnum.RESOURCE_INSUFFICIENT_AUTHENTICATED.getCode(), e.getMessage(), null);
        } catch (RuntimeException runtimeException) {
            logger.error("查询授权页面异常: ", runtimeException);
            return ApiResult.build(ApiCodeEnum.API_SYSTEM_ERROR.getCode(), runtimeException.getMessage(), null);
        }
    }

    @GetMapping("/prodVersionAuthenticatePage")
    public ApiResult<List<String>> prodVersionAuthenticatePage(@RequestParam(required = false) Long prodVersionUuid, @RequestParam String prodId) {
        return ApiResult.success(appProdVersionService.getProdVersionAuthenticatePageUuids(prodVersionUuid, prodId));
    }

    @GetMapping("/systemAuthenticatePageInfos")
    public ApiResult<List<AppPageDefinition>> systemAuthenticatePageInfos(@RequestParam(required = false) String tenant, @RequestParam String system) {
        return ApiResult.success(appSystemInfoService.systemAuthenticatePage(system, StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId())));
    }

    @GetMapping("/systemAuthenticatePage")
    public ApiResult<AppSystemAuthPageDto> systemAuthenticatePage(@RequestParam(required = false) String tenant, @RequestParam String system) {
        List<AppPageDefinition> pageDefinitions = appSystemInfoService.systemAuthenticatePage(system, StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()));
        if (CollectionUtils.isNotEmpty(pageDefinitions)) {
            String pageId = cdUserPreferencesFacadeService.getDataValue(StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()) + ":" + system
                    , StringUtils.EMPTY, SpringSecurityUtils.getCurrentUserId(), AppConstants.WORKBENCH);
            AppSystemAuthPageDto dto = new AppSystemAuthPageDto();
            dto.setPageSetting(appSystemInfoService.getSystemPageSetting(StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()), system));
            List<AppPageDefinition> filtered = Lists.newArrayList();
            for (AppPageDefinition page : pageDefinitions) {
                if (page.getId().equals(pageId)) {
                    dto.setPageUuid(page.getUuid());
                    dto.setPageId(page.getId());
                    dto.setTitle(page.getTitle());
                    dto.setPageName(page.getName());
                    AppPageDefinition appPageDefinition = appPageDefinitionService.getOne(page.getUuid());
                    dto.setDefinitionJson(appPageDefinition.getDefinitionJson());
                    dto.setTheme(appSystemInfoService.getSystemPageTheme(appPageDefinition.getId(), StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()), system));
                    dto.getAllAuthPages().add(0, new AppSystemAuthPageDto(page.getId(), page.getUuid(), page.getName()));
                    List<String> unauthorizedResource = appPageDefinitionMgr.getUnauthorizedAppPageResource(dto.getPageUuid());
                    dto.setUnauthorizedResource(unauthorizedResource);
                } else {
                    dto.getAllAuthPages().add(new AppSystemAuthPageDto(page.getId(), page.getUuid(), page.getName()));
                }
                filtered.add(page);
            }


            if (StringUtils.isBlank(dto.getPageId()) && !filtered.isEmpty()) {
                AppPageDefinition appPageDefinition = appPageDefinitionService.getOne(filtered.get(0).getUuid());
                dto.setDefinitionJson(appPageDefinition.getDefinitionJson());
                dto.setPageId(appPageDefinition.getId());
                dto.setPageName(appPageDefinition.getName());
                dto.setPageUuid(appPageDefinition.getUuid());
                dto.setTitle(appPageDefinition.getTitle());
                dto.setTheme(appSystemInfoService.getSystemPageTheme(appPageDefinition.getId(), StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()), system));
                List<String> unauthorizedResource = appPageDefinitionMgr.getUnauthorizedAppPageResource(dto.getPageUuid());
                dto.setUnauthorizedResource(unauthorizedResource);
                Set<String> roles = Sets.newHashSet();
                Collection<ConfigAttribute> configAttributes = securityMetadataSourceService.getAttributes(appPageDefinition.getId(), AppFunctionType.AppPageDefinition);
                if (CollectionUtils.isNotEmpty(configAttributes)) {
                    for (ConfigAttribute ca : configAttributes) {
                        roles.add(ca.getAttribute());
                    }
                    dto.setEffectiveRoles(roles);
                }
            }
            return ApiResult.success(dto);
        }
        return ApiResult.success(null);
    }
}

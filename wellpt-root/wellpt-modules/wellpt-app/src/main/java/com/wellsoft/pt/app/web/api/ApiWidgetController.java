package com.wellsoft.pt.app.web.api;

import com.google.common.collect.Maps;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年08月12日   chenq	 Create
 * </pre>
 */
@RestController
@RequestMapping("/api/app/widget")
public class ApiWidgetController {

    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @Autowired
    private AppPageDefinitionMgr appPageDefinitionMgr;

    @GetMapping("/getWidgetsByAppId")
    public ApiResult<List<AppWidgetDefinition>> getAppWidgetById(@RequestParam String appId,
                                                                 @RequestParam(required = false) String wtype,
                                                                 @RequestParam(required = false) Boolean main) {
        List<AppWidgetDefinition> list = appWidgetDefinitionService.getWidgetsByAppId(appId, wtype, main);
        return ApiResult.success(list);
    }

    @GetMapping("/getAuthorizedWidgetsByAppId")
    public ApiResult<List<AppWidgetDefinition>> getAuthorizedWidgetsByAppId(@RequestParam String appId,
                                                                            @RequestParam(required = false) String wtype,
                                                                            @RequestParam(required = false) Boolean main) {
        List<AppWidgetDefinition> list = appWidgetDefinitionService.getWidgetsByAppId(appId, wtype, main);
        if (SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_ADMIN.name(), BuildInRole.ROLE_TENANT_ADMIN.name()// 超级管理员
                , BuildInRole.ROLE_TRUSTED_CLIENT.name() // 匿名访问页面的请求会经过客户端token认证
        )) {
            return ApiResult.success(list);
        }
        Map<String, List<String>> pageUnauthorizedResource = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(list)) {
            for (AppWidgetDefinition definition : list) {
                if (StringUtils.isNotBlank(definition.getAppPageUuid())) {
                    if (!pageUnauthorizedResource.containsKey(definition.getAppPageUuid())) {
                        List<String> unauthorizedResource = appPageDefinitionMgr.getUnauthorizedAppPageResource(definition.getAppPageUuid());
                        definition.setUnauthorizedResource(unauthorizedResource);
                        pageUnauthorizedResource.put(definition.getAppPageUuid(), definition.getUnauthorizedResource());
                    } else {
                        definition.setUnauthorizedResource(pageUnauthorizedResource.get(definition.getAppPageUuid()));
                    }
                }
            }
        }
        return ApiResult.success(list);

    }


    @GetMapping("/getWidgetById")
    public ApiResult<AppWidgetDefinition> getWidgetById(@RequestParam String id, @RequestParam(required = false)
            String appPageId, @RequestParam(required = false) String appPageUuid) {
        return ApiResult.success(appWidgetDefinitionService.getWidgetByIdAndAppPage(id, appPageId, appPageUuid));
    }

    @GetMapping("/getDefElementI18nsByDefId")
    public ApiResult<List<AppDefElementI18nEntity>> getDefElementI18nsByDefId(@RequestParam String defId,
                                                                              @RequestParam(required = false) BigDecimal version,
                                                                              @RequestParam(required = false) String applyTo,
                                                                              @RequestParam(required = false) String locale) {
        return ApiResult.success(appDefElementI18nService.getI18ns(defId, null, version, applyTo, locale));
    }

    @GetMapping("/getWidgetI18nsByWidgetUuid")
    public ApiResult<List<AppDefElementI18nEntity>> getWidgetI18nsByWidgetUuid(@RequestParam String widgetUuid,
                                                                               @RequestParam(required = false) String locale) {
        AppWidgetDefinition appWidgetDefinition = appWidgetDefinitionService.getOne(widgetUuid);
        return ApiResult.success(appWidgetDefinition != null ? appDefElementI18nService.getI18ns(appWidgetDefinition.getAppPageId(), appWidgetDefinition.getId(),
                appWidgetDefinition.getVersion(), null, locale) : null);
    }


    @PostMapping("/saveWidgetI18ns")
    public ApiResult<Void> saveWidgetI18ns(@RequestBody List<AppDefElementI18nEntity> list) {
        appDefElementI18nService.saveAll(list);
        return ApiResult.success();
    }

    @GetMapping("/copyWidgetDefinitionAsNew")
    public ApiResult<Boolean> copyWidgetDefinitionAsNew(@RequestParam String fromPageUuid,
                                                        @RequestParam String toPageId,
                                                        @RequestParam String toPageUuid,
                                                        @RequestParam(required = false) String toAppId,
                                                        @RequestParam(required = false) String version) {
        appWidgetDefinitionService.copyWidgetDefinitionAsNew(fromPageUuid, toPageId, toPageUuid, toAppId, version);
        return ApiResult.success(true);
    }
}

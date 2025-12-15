package com.wellsoft.pt.app.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.app.bean.AppPageDefinitionParamDto;
import com.wellsoft.pt.app.dto.AppPageResourceDto;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppPageResourceEntity;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.service.AppPageResourceService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.app.web.api.request.PageProtectUpdateRequest;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Description: 页面定义API控制层
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月15日   chenq	 Create
 * </pre>
 */
@Api(tags = "页面定义")
@RestController
@RequestMapping("/api/webapp/page/definition")
public class AppPageDefinitionController {


    @Autowired
    AppPageDefinitionMgr appPageDefinitionMgr;

    @Autowired
    AppPageDefinitionService appPageDefinitionService;

    @Autowired
    AppPageResourceService appPageResourceService;

    @Autowired
    AppWidgetDefinitionService appWidgetDefinitionService;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    @ApiOperation(value = "保存页面定义", notes = "保存页面定义")
    @PostMapping("/savePageDefinition")
    public ApiResult savePageDefinition(@RequestBody AppPageDefinitionParamDto params) {
        String uuid = appPageDefinitionMgr.savePageDefinition(params);
        return ApiResult.success(uuid);
    }

    @PostMapping("/saveWidgetDefinitions")
    public ApiResult<Boolean> saveWidgetDefinitions(@RequestBody AppPageDefinitionParamDto params) {
        appWidgetDefinitionService.saveWidgetDefinitions(params);
        return ApiResult.success(true);
    }

    @GetMapping("/existId")
    public ApiResult<Boolean> existId(@RequestParam String id) {
        return ApiResult.success(appPageDefinitionService.existId(id));
    }

    @ApiOperation(value = "保存页面定义基本信息", notes = "保存页面定义基本信息")
    @PostMapping("/updateBasicInfo")
    public ApiResult<Boolean> updateBasicInfo(@RequestBody AppPageDefinitionParamDto params) {
        appPageDefinitionService.updateBasicInfo(params);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "更新页面主题设置", notes = "更新页面主题设置")
    @PostMapping("/updatePageTheme")
    public ApiResult<Boolean> updatePageTheme(@RequestBody List<AppPageDefinitionParamDto> dtos) {
        appPageDefinitionService.updatePageTheme(dtos);
        return ApiResult.success(true);
    }


    @ApiOperation(value = "更新页面定义是否可设计", notes = "更新页面定义是否可设计")
    @GetMapping("/updateDesignable")
    public ApiResult<Boolean> updateDesignable(@RequestParam String uuid, @RequestParam Boolean designable) {
        appPageDefinitionService.updateDesignable(uuid, designable);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "更新页面定义是否启用", notes = "更新页面定义是否启用")
    @GetMapping("/updateEnabled")
    public ApiResult<Boolean> updateEnabled(@RequestParam String uuid, @RequestParam Boolean enabled) {
        appPageDefinitionService.updateEnabled(uuid, enabled);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "更新页面定义是否匿名", notes = "更新页面定义是否匿名")
    @GetMapping("/updateAnonymous")
    public ApiResult<Boolean> updateAnonymous(@RequestParam String uuid, @RequestParam Boolean anonymous) {
        appPageDefinitionService.updateAnonymous(uuid, anonymous);
        return ApiResult.success(true);
    }

    @GetMapping("/deletePageById/{id}")
    public ApiResult<Boolean> deletePageById(@PathVariable String id) {
        appPageDefinitionService.deleteById(id);
        return ApiResult.success(true);
    }

    @PostMapping("/deletePageByIds")
    public ApiResult<Boolean> deletePageByIds(@RequestBody List<String> ids) {
        appPageDefinitionService.deleteByIds(ids);
        return ApiResult.success(true);
    }

    @GetMapping("/queryPageDefinitionByAppId")
    public ApiResult<List<AppPageDefinition>> queryPageDefinitionByAppId(@RequestParam String appId) {
        return ApiResult.success(appPageDefinitionService.listByAppId(appId));
    }

    @PostMapping("/queryLatestPageDefinitionByAppIds")
    public ApiResult<List<AppPageDefinition>> queryLatestPageDefinitionByAppIds(@RequestBody List<String> appIds) {
        return ApiResult.success(appPageDefinitionService.listLatestVersionPageByAppIds(appIds));
    }

    @PostMapping("/queryLatestPageDefinitionByCurrentSystem")
    public ApiResult<List<AppPageDefinition>> queryLatestPageDefinitionByCurrentSystem() {
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isBlank(system)) {
            return ApiResult.success(Collections.emptyList());
        }
        return ApiResult.success(appPageDefinitionService.listLatestVersionPageBySystem(system));
    }

    @GetMapping("/getLatestPageDefinition/{id}")
    public ApiResult<AppPageDefinition> getLatestPageDefinition(@PathVariable String id) {
        return ApiResult.success(appPageDefinitionService.getLatestPageDefinition(id));
    }

    @GetMapping("/getDefaultPageDefinition")
    public ApiResult<AppPageDefinition> getDefaultPageDefinition(@RequestParam String appId, @RequestParam(required = false) Boolean isPc) {
        return ApiResult.success(appPageDefinitionService.getDefaultPageDefinition(appId, isPc));
    }

    @GetMapping("/getAppPageResourceFunction/{uuid}")
    public ApiResult<List<AppPageResourceDto>> getAppPageResourceFunction(@PathVariable String uuid) {
        List<AppPageResourceDto> dtos = appPageResourceService.getAppPageResourcesAndFunction(uuid);
        return ApiResult.success(dtos);
    }

    @PostMapping("/getAppPageResources")
    public ApiResult<List<AppPageResourceEntity>> getAllPageRelaAppPageResource(@RequestBody List<String> appPageUuids) {
        List<AppPageResourceEntity> list = appPageResourceService.getAppPageResourcesByPageUuid(appPageUuids);
        return ApiResult.success(list);
    }


    @PostMapping("/updatePageProtected")
    public ApiResult<Boolean> updatePageProtectedResources(@RequestBody PageProtectUpdateRequest request) {
        appPageDefinitionService.updatePageProtected(request.getPageDefinitionUuid(), request.getIsProtected(), request.getResourceDtos());
        return ApiResult.success(true);
    }


}

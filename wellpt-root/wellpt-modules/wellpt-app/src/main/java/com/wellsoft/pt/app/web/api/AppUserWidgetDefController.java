package com.wellsoft.pt.app.web.api;

import com.google.common.collect.Maps;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.entity.AppUserWidgetDefEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.app.service.AppUserWidgetDefService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.app.web.api.request.DefWidgetQueryRequest;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author yt
 * @title: AppUserWidgetDefController
 * @date 2020/11/10 16:02
 */
@Api(tags = "用户自定义列项")
@RestController
@RequestMapping("/api/user/widgetDef")
public class AppUserWidgetDefController extends BaseController {

    @Autowired
    private AppUserWidgetDefService appUserWidgetDefService;

    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    //    @ApiOperation(value = "保存用户自定义组件", notes = "保存用户自定义组件")
    @PostMapping("/save")
    @Deprecated
    public ApiResult save(@ApiParam(value = "组件Id", required = false) @RequestParam("widgetId") String widgetId,
                          @ApiParam(value = "配置json字符串", required = true) @RequestParam("configuraion") String configuraion) {
        appUserWidgetDefService.saveUserWidgetDef(SpringSecurityUtils.getCurrentUserId(), widgetId, configuraion, AppUserWidgetDefEntity.Type.WIDGET);
        return ApiResult.success();
    }

    @ApiOperation(value = "保存用户自定义组件", notes = "保存用户自定义组件")
    @PostMapping("/saveUserDefWidget")
    public ApiResult saveUserDefWidget(@RequestBody AppUserWidgetDefEntity body) {
        appUserWidgetDefService.saveUserWidgetDef(body);
        return ApiResult.success();
    }

    @GetMapping("/getDetail")
    public ApiResult<AppUserWidgetDefEntity> getByUuid(@RequestParam String uuid) {
        AppUserWidgetDefEntity entity = appUserWidgetDefService.getOne(uuid);
        entity.setI18ns(appDefElementI18nService.getI18ns(uuid, null, new BigDecimal("1.0"), IexportType.AppUserWidgetDef, LocaleContextHolder.getLocale().toString()));
        return ApiResult.success(entity);
    }


    @ApiOperation(value = "查询自定义列项", notes = "返回 配置json字符串")
    @GetMapping("/get")
    public ApiResult<String> get(@ApiParam(value = "组件Id", required = true) @RequestParam("widgetId") String widgetId) {
        String configuraion = appUserWidgetDefService.getUserWidgetDefition(SpringSecurityUtils.getCurrentUserId(), widgetId);
        return ApiResult.success(configuraion);
    }

    @ApiOperation(value = "根据类型获取用户组件", notes = "根据类型获取用户组件")
    @GetMapping("/getUserWidgetByType")
    public ApiResult<List<AppUserWidgetDefEntity>> getUserWidgetByType(@ApiParam(value = "类型", required = true) @RequestParam("type") AppUserWidgetDefEntity.Type type) {
        List<AppUserWidgetDefEntity> list = appUserWidgetDefService.getUserWidgetDefition(SpringSecurityUtils.getCurrentUserId(), type);
        return ApiResult.success(list);
    }

    @ApiOperation(value = "根据类型获取组件", notes = "根据类型获取组件")
    @GetMapping("/getWidgetByType")
    public ApiResult<List<AppUserWidgetDefEntity>> getWidgetByType(@ApiParam(value = "类型", required = true) @RequestParam("type") AppUserWidgetDefEntity.Type type
    ) {
        List<AppUserWidgetDefEntity> list = appUserWidgetDefService.getWidgetDefinitionByType(type);
        return ApiResult.success(list);
    }

    @PostMapping("/query")
    public ApiResult<Map> query(@RequestBody DefWidgetQueryRequest request) {
        List<AppUserWidgetDefEntity> list = appUserWidgetDefService.pageQueryDefTypeAppWidgets(request.getKeyword(), request.getType(), request.getAppId(), request.getPage());
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", list);
        result.put("page", request.getPage());
        return ApiResult.success(result);
    }

    @ApiOperation(value = "删除自定义组件", notes = "删除自定义组件")
    @GetMapping("/delete/{uuid}")
    public ApiResult<Boolean> deleteSelfWidget(@PathVariable String uuid) {
        appUserWidgetDefService.delete(uuid);
        return ApiResult.success(true);
    }

    @GetMapping("/enabled/{uuid}")
    public ApiResult<Boolean> updateEnabled(@PathVariable String uuid, @RequestParam(required = false) Boolean enabled) {
        appUserWidgetDefService.updateEnabled(uuid, BooleanUtils.isTrue(enabled));
        return ApiResult.success(true);
    }

}

package com.wellsoft.pt.app.web.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.app.support.PiFunction;
import com.wellsoft.pt.app.theme.Theme;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description:
 *
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/2   Create
 * </pre>
 */
@Api(tags = "应用上下文")
@RestController
@RequestMapping("api/app/context")
public class AppContextController {

    @Autowired
    AppContextService appContextService;

    @ApiOperation(value = "根据功能集成信息路径，获取功能信息", notes = "根据功能集成信息路径，获取功能信息")
    @ApiImplicitParam(name = "piPath", value = "功能集成信息路径", dataType = "String", required = true)
    @ApiOperationSupport(order = 10)
    @GetMapping("/getFunctionByPath")
    public ApiResult<PiFunction> getFunctionByPath(@RequestParam String piPath) {
        return ApiResult.success(appContextService.getFunctionByPath(piPath));
    }

    @ApiOperation(value = "根据组件定义ID获取定义信息", notes = "根据组件定义ID获取定义信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appWidgetDefId", value = "组件定义ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "cloneable", value = "是否组件复制", paramType = "query", dataType = "Boolean", required = false)})
    @ApiOperationSupport(order = 20)
    @GetMapping("/getAppWidgetDefinitionById")
    public ApiResult<AppWidgetDefinition> getAppWidgetDefinitionById(@RequestParam String appWidgetDefId, @RequestParam(required = false) Boolean cloneable) {
        return ApiResult.success(appContextService.getAppWidgetDefinitionById(appWidgetDefId, cloneable));
    }

    @ApiOperation(value = "获取所有主题", notes = "获取所有主题")
    @ApiOperationSupport(order = 30)
    @GetMapping("/getAllThemes")
    public ApiResult<List<Theme>> getAllThemes() {
        return ApiResult.success(appContextService.getAllThemes());
    }
}

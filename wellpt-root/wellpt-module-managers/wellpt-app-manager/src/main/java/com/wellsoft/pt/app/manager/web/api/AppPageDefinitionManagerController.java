package com.wellsoft.pt.app.manager.web.api;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 产品集成接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/10/20.1	    zenghw		2021/10/20		    Create
 * </pre>
 * @date 2021/10/20
 */
@Api(tags = "产品集成接口")
@RestController
@RequestMapping("/api/page/definition/manager/")
public class AppPageDefinitionManagerController extends BaseController {

    @Autowired
    private AppPageDefinitionManager appPageDefinitionManager;
    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;

    @ApiOperation(value = "保存工作台 使用者", notes = "保存工作台 使用者")
    @PostMapping("/saveEleIds")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appPiUuid", value = "归属产品集成UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "uuid", value = "页面定义uuid", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "eleIds", value = "节点id（组织节点，用户 角色uuid）", paramType = "query", dataType = "Array", required = true)})
    public ApiResult saveEleIds(@RequestParam(name = "appPiUuid", required = true) String appPiUuid,
                                @RequestParam(name = "uuid", required = true) String uuid,
                                @RequestParam(name = "eleIds", required = true) List<String> eleIds) {
        try {
            appPageDefinitionManager.saveEleIds(appPiUuid, uuid, eleIds);
            return ApiResult.success();
        } catch (Exception e) {
            logger.error("保存工作台 使用者异常：", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "查询工作台使用者", notes = "查询工作台使用者")
    @GetMapping("/getEleIds")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appPiUuid", value = "归属产品集成UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "uuid", value = "页面定义uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<OrgNode>> getEleIds(@RequestParam(name = "appPiUuid", required = true) String appPiUuid,
                                              @RequestParam(name = "uuid", required = true) String uuid) {
        try {
            return ApiResult.success(appPageDefinitionManager.getEleIds(appPiUuid, uuid));
        } catch (Exception e) {
            logger.error("查询工作台使用者异常：", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "根据组件定义ID获取组件定义", notes = "根据组件定义ID获取组件定义")
    @GetMapping("/getById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "组件定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<AppWidgetDefinition> getById(@RequestParam(name = "id", required = true) String id) {
        try {
            return ApiResult.success(appWidgetDefinitionService.getById(id));
        } catch (Exception e) {
            logger.error("根据组件定义ID获取组件定义异常：", e);
            return ApiResult.fail(e.getMessage());
        }
    }
}

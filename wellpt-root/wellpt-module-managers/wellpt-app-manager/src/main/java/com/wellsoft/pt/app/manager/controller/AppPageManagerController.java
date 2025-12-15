package com.wellsoft.pt.app.manager.controller;

import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.app.bean.AppFunctionBean;
import com.wellsoft.pt.app.facade.service.AppFunctionMgr;
import com.wellsoft.pt.app.manager.dto.AppPageDefinitionDto;
import com.wellsoft.pt.app.manager.dto.AppPageResourceDto;
import com.wellsoft.pt.app.manager.facade.service.AppPageDefinitionManager;
import com.wellsoft.pt.app.manager.facade.service.AppPageResourceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 * 应用页面管理控制
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/2   Create
 * </pre>
 */
@Api(tags = "应用页面管理")
@RestController
@RequestMapping("api/app/pagemanager")
public class AppPageManagerController {

    @Autowired
    AppPageDefinitionManager pageDefinitionManager;

    @Autowired
    AppPageResourceManager pageResourceManager;

    @Autowired
    AppFunctionMgr appFunctionMgr;

    @ApiOperation(value = "应用页面保存", notes = "应用页面保存")
    @ApiOperationSupport(order = 10)
    @PostMapping("/savePage")
    public ApiResult savePage(@RequestBody AppPageDefinitionDto appPageDefinitionDto) {
        pageDefinitionManager.saveDto(appPageDefinitionDto);
        return ApiResult.success();
    }


    @ApiOperation(value = "应用页面删除", notes = "应用页面删除")
    @ApiOperationSupport(order = 20)
    @DeleteMapping("/deletePage/{uuid}")
    public ApiResult deletePage(@PathVariable String uuid) {
        pageDefinitionManager.remove(uuid);
        return ApiResult.success();
    }

    @ApiOperation(value = "应用页面资源删除", notes = "应用页面资源删除")
    @ApiOperationSupport(order = 30)
    @PostMapping("/removeResource")
    public ApiResult removeResource(@RequestBody List<AppPageResourceDto> appPageResourceDtos) {
        pageResourceManager.remove(appPageResourceDtos);
        return ApiResult.success();
    }

    @ApiOperation(value = "根据uuid获取应用功能", notes = "根据uuid获取应用功能")
    @ApiImplicitParam(name = "uuid", value = "功能uuid", dataType = "String", required = true)
    @ApiOperationSupport(order = 40)
    @GetMapping("/getFunctionBean/{uuid}")
    public ApiResult<AppFunctionBean> getFunctionBeanByUuid(@PathVariable String uuid) {
        return ApiResult.success(appFunctionMgr.getBean(uuid));
    }

    @ApiOperation(value = "根据uuid获取页面定义", notes = "根据uuid获取页面定义")
    @ApiImplicitParam(name = "uuid", value = "页面uuid", dataType = "String", required = true)
    @ApiOperationSupport(order = 50)
    @GetMapping("/getPageDefinition/{uuid}")
    public ApiResult<AppPageDefinitionDto> getPageDefinition(@PathVariable String uuid) {
        return ApiResult.success(pageDefinitionManager.getDto(uuid));
    }

    @ApiOperation(value = "获取页面设计器列表", notes = "获取页面设计器列表")
    @ApiOperationSupport(order = 60)
    @GetMapping("/getPageDesignerList")
    public ApiResult<List<DataItem>> getPageDesignerList() {
        return ApiResult.success(pageDefinitionManager.getPageDesignerList());
    }

}

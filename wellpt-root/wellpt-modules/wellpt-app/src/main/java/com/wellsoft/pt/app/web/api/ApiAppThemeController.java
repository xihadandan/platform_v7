/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.dto.AppThemeCheckUpdateDto;
import com.wellsoft.pt.app.dto.AppThemeDefinitionDto;
import com.wellsoft.pt.app.facade.service.AppThemeDefinitionFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 应用主题API接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2/27/23.1	zhulh		2/27/23		Create
 * </pre>
 * @date 2/27/23
 */
@Api(tags = "应用主题API接口")
@RestController
@RequestMapping("/api/app/theme")
public class ApiAppThemeController extends BaseController {

    @Autowired
    private AppThemeDefinitionFacadeService themeDefinitionFacadeService;

    /**
     * 获取主题定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/definition/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取主题定义", notes = "获取主题定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "主题定义UUID", paramType = "query", dataType = "String", required = true)})

    public ApiResult<AppThemeDefinitionDto> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(themeDefinitionFacadeService.getDto(uuid));
    }

    /**
     * 获取主题定义JSON
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getDefinitionJson", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取主题定义JSON", notes = "获取主题定义JSON")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "主题定义UUID", paramType = "query", dataType = "String", required = true)})

    public ApiResult<String> getDefinitionJson(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(themeDefinitionFacadeService.getDefinitionJsonByUuid(uuid));
    }

    /**
     * 保存主题定义
     *
     * @param themeDefinitionDto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/definition/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存主题定义", notes = "保存主题定义")
    public ApiResult<Void> save(@RequestBody AppThemeDefinitionDto themeDefinitionDto) {
        themeDefinitionFacadeService.saveDto(themeDefinitionDto);
        return ApiResult.success();
    }

    /**
     * 保存主题定义JSON
     *
     * @param uuid
     * @param definitionJson
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveDefinitionJson", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存主题定义", notes = "保存主题定义")
    public ApiResult<Void> save(@RequestParam(name = "uuid", required = true) String uuid, @RequestBody String definitionJson) {
        themeDefinitionFacadeService.saveDefinitionJson(uuid, definitionJson);
        return ApiResult.success();
    }

    /**
     * 删除主题定义
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/definition/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除主题定义", notes = "删除主题定义")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        themeDefinitionFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 根据应用于列出主题
     *
     * @param applyTo
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listByApplyTo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据应用于列出主题", notes = "根据应用于列出主题")
    public ApiResult<List<AppThemeDefinitionDto>> listByApplyTo(@RequestParam(name = "applyTo", required = true) String applyTo) {
        List<AppThemeDefinitionDto> themeDefinitionDtos = themeDefinitionFacadeService.listByApplyTo(applyTo);
        return ApiResult.success(themeDefinitionDtos);
    }


    /**
     * 获取更新的主题
     *
     * @param applyTo
     * @param checkUpdateDtos
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/listByApplyToWithUpdatedTheme", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取更新的主题", notes = "获取更新的主题")
    public ApiResult<List<AppThemeDefinitionDto>> listUpdatedTheme(@RequestParam("applyTo") String applyTo, @RequestBody List<AppThemeCheckUpdateDto> checkUpdateDtos) {
        List<AppThemeDefinitionDto> themeDefinitionDtos = themeDefinitionFacadeService.listByApplyToWithUpdatedTheme(applyTo, checkUpdateDtos);
        return ApiResult.success(themeDefinitionDtos);
    }

}

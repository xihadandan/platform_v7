/*
 * @(#)4/28/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.material.dto.CdMaterialDefinitionDto;
import com.wellsoft.pt.basicdata.material.facade.service.CdMaterialDefinitionFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/28/23.1	zhulh		4/28/23		Create
 * </pre>
 * @date 4/28/23
 */
@Api(tags = "材料定义控制层")
@RestController
@RequestMapping("/api/material/definition")
public class ApiMaterialDefinitionController extends BaseController {

    @Autowired
    private CdMaterialDefinitionFacadeService materialDefinitionFacadeService;

    /**
     * 获取材料定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取材料定义", notes = "获取材料定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "材料定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<CdMaterialDefinitionDto> getDefinition(@RequestParam(name = "uuid", required = true) Long uuid) {
        return ApiResult.success(materialDefinitionFacadeService.getDto(uuid));
    }

    /**
     * 获取材料定义
     *
     * @param codes
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listFormatByCodes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取材料定义", notes = "获取材料定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "codes", value = "材料定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<String>> listFormatByCodes(@RequestParam(name = "codes", required = true) List<String> codes) {
        return ApiResult.success(materialDefinitionFacadeService.listFormatByCodes(codes));
    }

    /**
     * 保存材料定义
     *
     * @param dto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存材料定义", notes = "保存材料定义")
    public ApiResult<Void> saveDefinition(@RequestBody CdMaterialDefinitionDto dto) {
        materialDefinitionFacadeService.saveDto(dto);
        return ApiResult.success();
    }


    /**
     * 根据UUID，删除材料定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除材料定义", notes = "删除材料定义")
    public ApiResult<Void> delete(@RequestParam(name = "uuid", required = true) Long uuid) {
        materialDefinitionFacadeService.deleteByUuid(uuid);
        return ApiResult.success();
    }

}

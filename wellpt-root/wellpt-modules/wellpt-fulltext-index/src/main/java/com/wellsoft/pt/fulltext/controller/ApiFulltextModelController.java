/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.fulltext.dto.FulltextModelDto;
import com.wellsoft.pt.fulltext.facade.service.FulltextModelFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/13/25.1	    zhulh		6/13/25		    Create
 * </pre>
 * @date 6/13/25
 */
@Api(tags = "全文检索模型管理")
@RestController
@RequestMapping("/api/fulltext/model")
public class ApiFulltextModelController extends BaseController {

    @Autowired
    private FulltextModelFacadeService fulltextModelFacadeService;

    @PostMapping("/save")
    @ApiOperation(value = "保存全文检索数据模型", notes = "保存全文检索数据模型")
    public ApiResult<Long> save(@RequestBody FulltextModelDto dto) {
        return ApiResult.success(fulltextModelFacadeService.saveDto(dto));
    }

    @PostMapping("/saveAll")
    @ApiOperation(value = "保存所有全文检索数据模型", notes = "保存所有全文检索数据模型")
    public ApiResult<Void> saveAll(@RequestBody List<FulltextModelDto> dtos) {
        fulltextModelFacadeService.saveAllDto(dtos);
        return ApiResult.success();
    }

    @GetMapping("/get/{uuid}")
    @ApiOperation(value = "获取全文检索数据模型", notes = "获取全文检索数据模型")
    public ApiResult<FulltextModelDto> get(@PathVariable(name = "uuid") Long uuid) {
        return ApiResult.success(fulltextModelFacadeService.getDto(uuid));
    }

    @GetMapping("/getByDataModeUuid/{dataModeUuid}")
    @ApiOperation(value = "根据数据模型UUID获取全文检索数据模型", notes = "根据数据模型UUID获取全文检索数据模型")
    public ApiResult<FulltextModelDto> getByDataModeUuid(@PathVariable(name = "dataModeUuid") Long dataModeUuid) {
        return ApiResult.success(fulltextModelFacadeService.getByDataModelUuidAndSystem(dataModeUuid, RequestSystemContextPathResolver.system()));
    }

    @GetMapping("/listByModuleId")
    @ApiOperation(value = "根据模块ID获取全文检索数据模型", notes = "根据模块ID获取全文检索数据模型")
    public ApiResult<List<FulltextModelDto>> listByModuleId(@RequestParam(name = "moduleId") String moduleId) {
        return ApiResult.success(fulltextModelFacadeService.listByModuleId(moduleId));
    }

    @GetMapping("/listByCategoryUuid")
    @ApiOperation(value = "根据分类UUID获取全文检索数据模型", notes = "根据分类UUID获取全文检索数据模型")
    public ApiResult<List<FulltextModelDto>> listByCategoryUuid(@RequestParam(name = "categoryUuid") Long categoryUuid) {
        return ApiResult.success(fulltextModelFacadeService.listByCategoryUuid(categoryUuid));
    }

    @DeleteMapping("/delete/{uuid}")
    @ApiOperation(value = "根据UUID删除全文检索数据模型", notes = "根据UUID删除全文检索数据模型")
    public ApiResult<Void> delete(@RequestParam(name = "delete") Long uuid) {
        fulltextModelFacadeService.deleteByUuid(uuid);
        return ApiResult.success();
    }

}

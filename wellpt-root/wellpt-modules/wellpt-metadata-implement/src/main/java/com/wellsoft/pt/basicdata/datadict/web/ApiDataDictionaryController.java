/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryCategoryDto;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryDto;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datadict.enums.EnumDictionaryCategoryType;
import com.wellsoft.pt.basicdata.datadict.facade.service.CdDataDictionaryFacadeService;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/9/23.1	zhulh		8/9/23		Create
 * </pre>
 * @date 8/9/23
 */
@Api(tags = "数据字典")
@RestController
@RequestMapping("/api/datadict")
public class ApiDataDictionaryController extends BaseController {

    @Autowired
    private CdDataDictionaryFacadeService dataDictionaryFacadeService;


    @ApiOperation(value = "保存字典分类", notes = "保存字典分类")
    @PostMapping("/category/save")
    @ApiOperationSupport(order = 10)
    public ApiResult<String> saveCategory(@RequestBody CdDataDictionaryCategoryDto categoryDto) {
        Long uuid = dataDictionaryFacadeService.saveDataDictionaryCategory(categoryDto);
        return ApiResult.success(uuid.toString());
    }

    @ApiOperation(value = "获取字典分类", notes = "获取字典分类")
    @GetMapping("/category/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "模块ID", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 11)
    public ApiResult<List<CdDataDictionaryCategoryDto>> listCategory(@RequestParam(name = "moduleId") String moduleId) {
        List<CdDataDictionaryCategoryDto> categoryDtos = null;
        if (StringUtils.isNotBlank(moduleId)) {
            categoryDtos = dataDictionaryFacadeService.listDataDictionaryCategoryByModuleId(moduleId);
        } else {
            categoryDtos = dataDictionaryFacadeService.listDataDictionaryCategoryByType(EnumDictionaryCategoryType.module);
        }
        return ApiResult.success(categoryDtos);
    }

    @ApiOperation(value = "获取字典分类", notes = "获取字典分类")
    @GetMapping("/category/listByType/{type}")
    @ApiOperationSupport(order = 12)
    public ApiResult<List<CdDataDictionaryCategoryDto>> listByType(@PathVariable(name = "type") EnumDictionaryCategoryType categoryType) {
        List<CdDataDictionaryCategoryDto> categoryDtos = dataDictionaryFacadeService.listDataDictionaryCategoryByType(categoryType);
        return ApiResult.success(categoryDtos);
    }

    @ApiOperation(value = "删除字典分类", notes = "删除字典分类")
    @PostMapping("/category/delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "字典分类UUID", paramType = "query", dataType = "Long", required = true)})
    @ApiOperationSupport(order = 13)
    public ApiResult<Void> deleteCategory(@RequestParam(name = "uuid") Long uuid) {
        dataDictionaryFacadeService.deleteDataDictionaryCategoryByUuid(uuid);
        return ApiResult.success();
    }

    @ApiOperation(value = "保存数据字典", notes = "保存数据字典")
    @PostMapping("/save")
    public ApiResult<String> save(@RequestBody CdDataDictionaryDto dataDictionaryDto) {
        Long uuid = dataDictionaryFacadeService.saveDataDictionary(dataDictionaryDto);
        return ApiResult.success(uuid.toString());
    }

    @ApiOperation(value = "根据字典UUID获取数据字典", notes = "根据字典UUID获取数据字典")
    @GetMapping("/get/{uuid}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "字典UUID", paramType = "query", dataType = "Long", required = true)})
    @ApiOperationSupport(order = 20)
    public ApiResult<CdDataDictionaryDto> getByUuid(@PathVariable(name = "uuid") Long uuid) {
        CdDataDictionaryDto dataDictionaryDto = dataDictionaryFacadeService.getDataDictionaryByUuid(uuid);
        return ApiResult.success(dataDictionaryDto);
    }

    @ApiOperation(value = "根据字典编码获取数据字典", notes = "根据字典编码获取数据字典")
    @GetMapping("/getByCode/{code}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "字典编码", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 21)
    public ApiResult<CdDataDictionaryDto> getByCode(@PathVariable(name = "code") String code,
                                                    @RequestParam(required = false) Boolean locale) {
        CdDataDictionaryDto dataDictionaryDto = dataDictionaryFacadeService.getLocaleDataDictionaryByCode(code, BooleanUtils.isTrue(locale) ? LocaleContextHolder.getLocale().toString() : null);
        return ApiResult.success(dataDictionaryDto);
    }

    @ApiOperation(value = "获取模块数据字典数量", notes = "获取模块数据字典数量")
    @GetMapping("/count")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "模块ID", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 22)
    public ApiResult<Long> count(@RequestParam(name = "moduleId") String moduleId) {
        Long count = dataDictionaryFacadeService.countDataDictionaryByModuleId(moduleId);
        return ApiResult.success(count);
    }

    @ApiOperation(value = "删除数据字典", notes = "删除数据字典")
    @PostMapping("/deleteAll")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "字典UUID列表", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 23)
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids") List<Long> uuids) {
        dataDictionaryFacadeService.deleteDataDictionaryByUuids(uuids);
        return ApiResult.success();
    }

    @ApiOperation(value = "保存字典项", notes = "保存字典项")
    @PostMapping("/saveItem")
    @ApiOperationSupport(order = 30)
    public ApiResult<String> saveItem(@RequestBody CdDataDictionaryItemDto dictionaryItemDto) {
        Long uuid = dataDictionaryFacadeService.saveItem(dictionaryItemDto);
        return ApiResult.success(uuid.toString());
    }

    @ApiOperation(value = "更新字典项排序", notes = "更新字典项排序")
    @PostMapping("/updateItemSortOrder")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sortOrderMap", value = "字典项排序Map<字典项UUID, 序号>", paramType = "query", dataType = "Map", required = true)})
    @ApiOperationSupport(order = 31)
    public ApiResult<Void> updateItemSortOrder(@RequestBody Map<Long, Integer> sortOrderMap) {
        dataDictionaryFacadeService.updateItemSortOrder(sortOrderMap);
        return ApiResult.success();
    }

    @ApiOperation(value = "根据字典项UUID删除字典项", notes = "根据字典项UUID删除字典项")
    @PostMapping("/deleteItem/{uuid}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "字典项UUID", paramType = "query", dataType = "Long", required = true)})
    @ApiOperationSupport(order = 32)
    public ApiResult<Void> deleteItem(@PathVariable(name = "uuid") Long uuid) {
        dataDictionaryFacadeService.deleteItemByItemUuid(uuid);
        return ApiResult.success();
    }


    @GetMapping("/translateAll")
    public ApiResult<Void> translateAllDataDic(@RequestParam Long uuid, @RequestParam Boolean onlyTranslateEmpty) {
        dataDictionaryFacadeService.translateAllDataDic(uuid, onlyTranslateEmpty);
        return ApiResult.success();
    }

    @PostMapping("/translateAll")
    public ApiResult<Void> translateAllDataDic(@RequestBody Map<String, Object> requestMap) {
        List<String> uuids = (List<String>) requestMap.get("uuids");
        Boolean onlyTranslateEmpty = BooleanUtils.isTrue((Boolean) requestMap.get("onlyTranslateEmpty"));
        if (CollectionUtils.isNotEmpty(uuids)) {
            for (String id : uuids) {
                dataDictionaryFacadeService.translateAllDataDic(Long.parseLong(id), onlyTranslateEmpty);
            }

        }
        return ApiResult.success();
    }

    @ApiOperation(value = "批量保存数据字典项", notes = "批量保存数据字典项")
    @PostMapping("/batchSaveDataDicItems")
    @ApiOperationSupport(order = 33)
    public ApiResult<Void> batchSaveDataDicItems(@RequestBody CdDataDictionaryDto dataDictionaryDto) {
        dataDictionaryFacadeService.batchSaveDataDicItems(dataDictionaryDto.getUuid(), dataDictionaryDto.getItems());
        return ApiResult.success();
    }

}

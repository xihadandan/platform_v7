/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.google.common.collect.Maps;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.biz.dto.BizItemDefinitionDto;
import com.wellsoft.pt.biz.dto.BizProcessDefinitionItemIncludeItemDto;
import com.wellsoft.pt.biz.facade.service.BizItemDefinitionFacadeService;
import com.wellsoft.pt.biz.support.ItemMaterial;
import com.wellsoft.pt.biz.support.ItemTimeLimit;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@RestController
@RequestMapping("/api/biz/item/definition")
public class ApiBizItemDefinitionController extends BaseController {

    @Autowired
    private BizItemDefinitionFacadeService bizItemDefinitionFacadeService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * 获取单个事项定义
     *
     * @param uuid
     * @return
     */
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取单个事项定义", notes = "获取单个事项定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "事项定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<BizItemDefinitionDto> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(bizItemDefinitionFacadeService.getDto(uuid));
    }

    /**
     * 获取事项定义
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取事项定义", notes = "获取事项定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "事项定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<BizItemDefinitionDto> getById(@RequestParam(name = "id", required = true) String id) {
        return ApiResult.success(bizItemDefinitionFacadeService.getDtoById(id));
    }

    /**
     * 根据业务ID获取事项定义
     *
     * @param businessId
     * @return
     */
    @GetMapping(value = "/listByBusinessId", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务ID获取事项定义", notes = "根据业务ID获取事项定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessId", value = "业务ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<BizItemDefinitionDto>> listByBusinessId(@RequestParam(name = "businessId", required = true) String businessId) {
        return ApiResult.success(bizItemDefinitionFacadeService.listByBusinessId(businessId));
    }

    /**
     * 保存单个事项定义
     *
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存单个事项定义", notes = "保存单个事项定义")
    public ApiResult<Void> save(@RequestBody BizItemDefinitionDto dto) {
        bizItemDefinitionFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    /**
     * 删除单个事项定义
     *
     * @param uuids
     * @return
     */
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除单个事项定义", notes = "删除单个事项定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "单个事项定义UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        bizItemDefinitionFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 根据表单定义ID获取表单定义JSON
     *
     * @return
     */
    @GetMapping(value = "/getFormDefinitionByFormId/{formId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据表单定义ID获取表单定义JSON", notes = "根据表单定义ID获取表单定义JSON")
    public ApiResult<String> getFormDefinitionByFormId(@PathVariable(name = "formId") String formId) {
        String definitionJson = StringUtils.EMPTY;
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinitionById(formId);
        if (dyFormFormDefinition != null) {
            definitionJson = dyFormFormDefinition.getDefinitionJson();
        }
        return ApiResult.success(definitionJson);
    }

    /**
     * 根据事项编码获取事项包含的事项
     *
     * @param id
     * @param itemCode
     * @return
     */
    @GetMapping(value = "/listIncludeItemDataByItemCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据事项编码获取事项包含的事项", notes = "根据事项编码获取事项包含的事项")
    public ApiResult<List<BizProcessDefinitionItemIncludeItemDto>> listIncludeItemDataByItemCode(@RequestParam(name = "id") String id, @RequestParam(name = "itemCode") String itemCode) {
        List<BizProcessDefinitionItemIncludeItemDto> itemIncludeItems = bizItemDefinitionFacadeService.listIncludeItemDataByItemCode(id, itemCode);
        return ApiResult.success(itemIncludeItems);
    }

    /**
     * 根据事项编码获取事项办理时限及材料配置
     *
     * @param id
     * @param itemCode
     * @return
     */
    @GetMapping(value = "/listTimeLimitAndMaterialByItemCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据事项编码获取事项办理时限及材料配置", notes = "根据事项编码获取事项办理时限及材料配置")
    public ApiResult<Map<String, Object>> listTimeLimitAndMaterialByItemCode(@RequestParam(name = "id") String id, @RequestParam(name = "itemCode") String itemCode) {
        Map<String, Object> map = Maps.newHashMap();
        List<ItemTimeLimit> itemTimeLimits = bizItemDefinitionFacadeService.listTimeLimitDataByItemCode(id, itemCode);
        List<ItemMaterial> itemMaterials = bizItemDefinitionFacadeService.listMaterialDataByItemCode(id, itemCode);
        map.put("timeLimits", itemTimeLimits);
        map.put("materials", itemMaterials);
        return ApiResult.success(map);
    }

}

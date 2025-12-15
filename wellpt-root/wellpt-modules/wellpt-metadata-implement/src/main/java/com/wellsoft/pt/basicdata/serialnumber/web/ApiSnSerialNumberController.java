/*
 * @(#)7/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.web;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberCategoryDto;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberDefinitionDto;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberMaintainDto;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberCategoryFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberDefinitionFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberMaintainFacadeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 流水号api接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/11/22.1	zhulh		7/11/22		Create
 * </pre>
 * @date 7/11/22
 */
@Api(tags = "流水号")
@Controller
@RequestMapping("/api/sn/serial/number")
public class ApiSnSerialNumberController extends BaseController {

    @Autowired
    private SnSerialNumberCategoryFacadeService snSerialNumberCategoryFacadeService;

    @Autowired
    private SnSerialNumberDefinitionFacadeService snSerialNumberDefinitionFacadeService;

    @Autowired
    private SnSerialNumberMaintainFacadeService serialNumberMaintainFacadeService;

    /**
     * 获取流水号分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/category/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流水号分类", notes = "获取流水号分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流水号分类UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<SnSerialNumberCategoryDto> getCategory(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(snSerialNumberCategoryFacadeService.getDto(uuid));
    }

    /**
     * 流水号分类查询
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/category/getAllBySystemUnitIdsLikeName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "流水号分类按系统单位及名称查询", notes = "流水号分类按系统单位及名称查询")
    public ApiResult<List<SnSerialNumberCategoryDto>> getAllBySystemUnitIdsLikeName(@RequestBody SnSerialNumberCategoryGetRequest request) {
        return ApiResult.success(snSerialNumberCategoryFacadeService.getAllBySystemUnitIdsLikeName(request.getName()));
    }

    /**
     * 保存流水号分类
     *
     * @param dto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/category/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存流水号分类", notes = "保存流水号分类")
    public ApiResult<Void> saveCategory(@RequestBody SnSerialNumberCategoryDto dto) {
        snSerialNumberCategoryFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    /**
     * 删除没用的流水号分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/category/deleteWhenNotUsed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除没用的流水号分类", notes = "删除没用的流水号分类")
    public ApiResult<Integer> categoryDeleteWhenNotUsed(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(snSerialNumberCategoryFacadeService.deleteWhenNotUsed(uuid));
    }

    /**
     * 获取流水号定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/definition/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流水号定义", notes = "获取流水号定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流水号定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<SnSerialNumberDefinitionDto> getDefinition(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(snSerialNumberDefinitionFacadeService.getDto(uuid));
    }

    /**
     * 保存流水号定义
     *
     * @param dto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/definition/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存流水号定义", notes = "保存流水号定义")
    public ApiResult<Void> saveDefinition(@RequestBody SnSerialNumberDefinitionDto dto) {
        snSerialNumberDefinitionFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    /**
     * 删除没用的流水号定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/definition/deleteWhenNotUsed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除没用的流水号定义", notes = "删除没用的流水号定义")
    public ApiResult<Integer> definitionDeleteWhenNotUsed(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(snSerialNumberDefinitionFacadeService.deleteWhenNotUsed(uuid));
    }


    @ApiModel("流水号分类按系统单位及名称查询请求数据")
    private static class SnSerialNumberCategoryGetRequest extends BaseObject {
        private static final long serialVersionUID = -1860771671911297747L;
        @ApiModelProperty("名称")
        private String name;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 根据流水号定义UUID获取流水号维护
     *
     * @param serialNumberDefUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/maintain/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流水号定义UUID获取流水号维护", notes = "根据流水号定义UUID获取流水号维护")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serialNumberDefUuid", value = "流水号定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<SnSerialNumberMaintainDto>> listMaintainBySerialNumberDefUuid(@RequestParam(name = "serialNumberDefUuid", required = true) String serialNumberDefUuid) {
        return ApiResult.success(serialNumberMaintainFacadeService.listBySerialNumberDefUuid(serialNumberDefUuid));
    }

}

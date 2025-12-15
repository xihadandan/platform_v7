/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.biz.dto.BizTagDto;
import com.wellsoft.pt.biz.facade.service.BizTagFacadeService;
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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@RestController
@RequestMapping("/api/biz/tag")
public class ApiBizTagController extends BaseController {

    @Autowired
    private BizTagFacadeService bizTagFacadeService;

    /**
     * 获取业务标签
     *
     * @param uuid
     * @return
     */
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务标签", notes = "获取业务标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "业务标签UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<BizTagDto> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(bizTagFacadeService.getDto(uuid));
    }

    /**
     * 保存业务标签
     *
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存业务标签", notes = "保存业务标签")
    public ApiResult<Void> save(@RequestBody BizTagDto dto) {
        bizTagFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    /**
     * 删除业务标签
     *
     * @param uuids
     * @return
     */
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除业务标签", notes = "删除业务标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "业务标签UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        bizTagFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

}

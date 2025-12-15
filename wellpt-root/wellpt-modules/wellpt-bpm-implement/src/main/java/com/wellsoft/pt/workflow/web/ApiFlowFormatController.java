/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import com.wellsoft.pt.workflow.service.FlowFormatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 流程信息格式API接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@Api(tags = "流程信息格式")
@Controller
@RequestMapping("/api/workflow/format")
public class ApiFlowFormatController extends BaseController {

    @Autowired
    private FlowFormatService flowFormatService;

    /**
     * 获取信息格式
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取信息格式", notes = "获取信息格式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "信息格式UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<FlowFormat> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(flowFormatService.get(uuid));
    }

    /**
     * 保存信息格式
     *
     * @param entity
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存信息格式", notes = "保存信息格式")
    public ApiResult<Void> save(@RequestBody FlowFormat entity) {
        flowFormatService.save(entity);
        return ApiResult.success();
    }

    /**
     * 删除信息格式
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除信息格式", notes = "删除信息格式")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        flowFormatService.removeAllByPk(uuids);
        return ApiResult.success();
    }

}

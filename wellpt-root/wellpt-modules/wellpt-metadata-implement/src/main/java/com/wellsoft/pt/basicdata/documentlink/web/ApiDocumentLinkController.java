/*
 * @(#)Mar 14, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.documentlink.dto.CdDocumentLinkDto;
import com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 14, 2022.1	zhulh		Mar 14, 2022		Create
 * </pre>
 * @date Mar 14, 2022
 */
@Api(tags = "文档链接关系")
@RestController
@RequestMapping("/api/document/link")
public class ApiDocumentLinkController extends BaseController {

    @Autowired
    private CdDocumentLinkFacadeService cdDocumentLinkFacadeService;

    /**
     * 获取文档链接关系
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取文档链接关系", notes = "获取文档链接关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "文档链接关系uuid", paramType = "query", dataType = "String", required = false)})
    public ApiResult<CdDocumentLinkDto> get(@PathVariable(name = "uuid", required = false) String uuid) {
        CdDocumentLinkDto documentLinkDto = cdDocumentLinkFacadeService.getDto(uuid);
        return ApiResult.success(documentLinkDto);
    }

    /**
     * 检验并获取目标链接URL
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/checkAndGetTargetUrl/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "检验并获取目标链接URL", notes = "检验并获取目标链接URL")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "文档链接关系uuid", paramType = "query", dataType = "String", required = false)})
    public ApiResult<String> checkAndGetTargetUrl(@PathVariable(name = "uuid", required = false) String uuid) {
        CdDocumentLinkDto documentLinkDto = cdDocumentLinkFacadeService.getDto(uuid);
        if (StringUtils.isBlank(documentLinkDto.getTargetUrl())) {
            return ApiResult.success(null);
        }
        // 检验数据
        if (cdDocumentLinkFacadeService.check(documentLinkDto)) {
            return ApiResult.success(documentLinkDto.getTargetUrl());
        }
        return ApiResult.success(null);
    }

    /**
     * 检验源数据
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/checkSourceDataByUuid/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "检验源数据", notes = "检验源数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "文档链接关系uuid", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Boolean> checkSourceDataByUuid(@PathVariable(name = "uuid", required = false) String uuid) {
        // 检验源数据
        return ApiResult.success(cdDocumentLinkFacadeService.checkSourceDataByUuid(uuid));
    }

    /**
     * 检验目标数据
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/checkTargetDataByUuid/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "检验目标数据", notes = "检验目标数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "文档链接关系uuid", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Boolean> checkTargetDataByUuid(@PathVariable(name = "uuid", required = false) String uuid) {
        // 检验目标数据
        return ApiResult.success(cdDocumentLinkFacadeService.checkTargetDataByUuid(uuid));
    }

}

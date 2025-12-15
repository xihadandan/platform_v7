/*
 * @(#)11/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.biz.dto.BizDefinitionTemplateDto;
import com.wellsoft.pt.biz.facade.service.BizDefinitionTemplateFacadeService;
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
 * 11/22/22.1	zhulh		11/22/22		Create
 * </pre>
 * @date 11/22/22
 */
@Api(tags = "业务流程配置项模板")
@RestController
@RequestMapping("/api/biz/definition/template")
public class ApiBizDefinitionTemplateController extends BaseController {

    @Autowired
    private BizDefinitionTemplateFacadeService definitionTemplateFacadeService;

    /**
     * 获取业务流程配置项模板
     *
     * @return
     */
    @GetMapping(value = "/get/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程配置项模板", notes = "获取业务流程配置项模板")
    public ApiResult<BizDefinitionTemplateDto> get(@PathVariable("uuid") String uuid) {
        BizDefinitionTemplateDto dto = definitionTemplateFacadeService.getDto(uuid);
        return ApiResult.success(dto);
    }

    /**
     * 保存业务流程配置项模板
     *
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存业务流程配置项模板", notes = "保存业务流程配置项模板")
    public ApiResult<Void> save(@RequestBody BizDefinitionTemplateDto dto) {
        definitionTemplateFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    /**
     * 删除业务流程配置项模板
     *
     * @param uuids
     * @return
     */
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除业务流程配置项模板", notes = "删除业务流程配置项模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "业务流程配置项模板UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        definitionTemplateFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 删除业务流程配置项模板
     *
     * @param id
     * @param type
     * @param processDefUuid
     * @return
     */
    @PostMapping(value = "/deleteByIdAndTypeAndProcessDefUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除业务流程配置项模板", notes = "删除业务流程配置项模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "事项或阶段ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "事项或阶段", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "processDefUuid", value = "业务流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deleteByIdAndTypeAndProcessDefUuid(@RequestParam("id") String id,
                                                              @RequestParam("type") String type,
                                                              @RequestParam("processDefUuid") String processDefUuid) {
        definitionTemplateFacadeService.deleteByIdAndTypeAndProcessDefUuid(id, type, processDefUuid);
        return ApiResult.success();
    }

}

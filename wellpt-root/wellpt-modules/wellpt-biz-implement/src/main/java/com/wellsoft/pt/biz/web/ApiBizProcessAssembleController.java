/*
 * @(#)2/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.biz.dto.BizProcessAssembleDto;
import com.wellsoft.pt.biz.facade.service.BizProcessAssembleFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * 2/28/24.1	zhulh		2/28/24		Create
 * </pre>
 * @date 2/28/24
 */
@Api(tags = "业务流程装配")
@RestController
@RequestMapping("/api/biz/process/assemble")
public class ApiBizProcessAssembleController {

    @Autowired
    private BizProcessAssembleFacadeService processAssembleFacadeService;

    /**
     * 获取业务流程定义
     *
     * @return
     */
    @GetMapping(value = "/getByProcessDefUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程装配", notes = "获取业务流程装配")
    public ApiResult<BizProcessAssembleDto> getByProcessDefUuid(@RequestParam("processDefUuid") String processDefUuid) {
        BizProcessAssembleDto dto = processAssembleFacadeService.getByProcessDefUuid(processDefUuid);
        return ApiResult.success(dto);
    }

    /**
     * 获取业务流程定义
     *
     * @return
     */
    @PostMapping(value = "/saveDefinitionJson", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存业务流程装配", notes = "保存业务流程装配")
    public ApiResult<Long> saveDefinitionJson(@RequestParam("processDefUuid") String processDefUuid, @RequestBody String definitionJson) {
        Long uuid = processAssembleFacadeService.saveDefinitionJson(processDefUuid, definitionJson);
        return ApiResult.success(uuid);
    }


}

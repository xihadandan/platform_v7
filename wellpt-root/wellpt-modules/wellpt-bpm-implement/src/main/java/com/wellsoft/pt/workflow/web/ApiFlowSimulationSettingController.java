/*
 * @(#)9/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.workflow.dto.WfFlowSimulationSettingDto;
import com.wellsoft.pt.workflow.facade.service.WfFlowSimulationSettingFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 9/29/24.1	    zhulh		9/29/24		    Create
 * </pre>
 * @date 9/29/24
 */
@Api(tags = "流程设置")
@Controller
@RequestMapping("/api/workflow/simulation/setting")
public class ApiFlowSimulationSettingController {

    @Autowired
    private WfFlowSimulationSettingFacadeService flowSimulationSettingFacadeService;

    /**
     * 获取流程仿真设置
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getByFlowDefUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程仿真设置", notes = "获取流程仿真设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefUuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WfFlowSimulationSettingDto> getByFlowDefUuid(@RequestParam(name = "flowDefUuid", required = true) String flowDefUuid) {
        return ApiResult.success(flowSimulationSettingFacadeService.getDtoByFlowDefUuid(flowDefUuid));
    }

    /**
     * 保存流程仿真设置
     *
     * @param dto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存流程仿真设置", notes = "保存流程仿真设置")
    public ApiResult<Void> save(@RequestBody WfFlowSimulationSettingDto dto) {
        flowSimulationSettingFacadeService.saveDto(dto);
        return ApiResult.success();
    }

}

/*
 * @(#)2013-10-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.log.dto.LogManageOperationDto;
import com.wellsoft.pt.log.entity.UserBehaviorLogEntity;
import com.wellsoft.pt.log.facade.service.LogManageOperationFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 管理日志
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-06-28.1	zenghw		2021-06-28		Create
 * </pre>
 * @date 2021-06-28
 */
@Api(tags = "管理日志接口")
@RestController
@RequestMapping("/api/log/manage/operation")
public class LogManageOperationController extends BaseController {

    @Autowired
    private LogManageOperationFacadeService logManageOperationFacadeService;

    /**
     * 获取管理日志详情
     *
     * @param logManageOperationUuid
     * @return com.wellsoft.pt.log.dto.LogManageOperationDto
     **/
    @ApiOperation(value = "获取管理日志详情", notes = "获取管理日志详情")
    @GetMapping(value = "/getLogManageOperation")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logManageOperationUuid", value = "管理日志详情uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<LogManageOperationDto> getLogManageOperation(
            @RequestParam(name = "logManageOperationUuid", required = true) String logManageOperationUuid) {

        return ApiResult.success(logManageOperationFacadeService.getLogManageOperation(logManageOperationUuid));
    }

    /**
     * 保存流程清单导出-管理日志
     *
     * @param
     * @return com.wellsoft.pt.log.dto.LogManageOperationDto
     **/
    @ApiOperation(value = "保存流程清单导出-管理日志", notes = "保存流程清单导出-管理日志")
    @GetMapping(value = "/saveFlowListExportLogManageOperation")
    public ApiResult saveFlowListExportLogManageOperation() {
        logManageOperationFacadeService.saveFlowListExportLogManageOperation();
        return ApiResult.success();
    }

    @ApiOperation(value = "保存用户前端行为日志", notes = "保存用户前端行为日志")
    @PostMapping(value = "/saveUserBehaviorLog")
    public ApiResult saveUserBehaviorLog(@RequestBody UserBehaviorLogEntity entity) {
        logManageOperationFacadeService.saveUserBehaviorLog(entity);
        return ApiResult.success(true);
    }

}

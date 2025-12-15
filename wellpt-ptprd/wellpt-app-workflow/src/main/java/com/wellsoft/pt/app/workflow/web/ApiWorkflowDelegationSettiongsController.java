/*
 * @(#)2021年3月31日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.workflow.dto.FlowDelegationSettingsDto;
import com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService;
import com.wellsoft.pt.bpm.engine.dto.WfCommonDelegationSettingDto;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 工作委托接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年3月31日.1	zhulh		2021年3月31日		Create
 * </pre>
 * @date 2021年3月31日
 */
@Api(tags = "流程工作委托")
@RestController
@RequestMapping("/api/workflow/delegation/settiongs")
public class ApiWorkflowDelegationSettiongsController extends BaseController {

    @Autowired
    private WorkflowDelegationSettiongsService workflowDelegationSettiongsService;

    /**
     * 获取工作委托设置
     *
     * @param uuid
     * @return
     */
    @ApiOperation(value = "获取工作委托设置", notes = "获取工作委托设置")
    @GetMapping("/get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "工作委托设置UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<FlowDelegationSettingsDto> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(workflowDelegationSettiongsService.getBean(uuid));
    }

    /**
     * 保存工作委托设置
     *
     * @param delegationSettingsDto
     * @return
     */
    @ApiOperation(value = "保存工作委托设置", notes = "保存工作委托设置")
    @PostMapping("/save")
    public ResultMessage save(@RequestBody FlowDelegationSettingsDto delegationSettingsDto) {
        return workflowDelegationSettiongsService.saveBean(delegationSettingsDto);
    }

    /**
     * 工作委托设置激活
     *
     * @param uuids
     * @return
     */
    @ApiOperation(value = "工作委托设置激活", notes = "工作委托设置激活")
    @PostMapping("/activeAll")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "工作委托设置UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> activeAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        workflowDelegationSettiongsService.active(uuids);
        return ApiResult.success();
    }

    /**
     * 工作委托设置终止
     *
     * @param uuids
     * @return
     */
    @ApiOperation(value = "工作委托设置终止", notes = "工作委托设置终止")
    @PostMapping("/deactiveAll")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "工作委托设置UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deactiveAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        workflowDelegationSettiongsService.deactive(uuids);
        return ApiResult.success();
    }

    /**
     * 工作委托设置删除
     *
     * @param uuids
     * @return
     */
    @ApiOperation(value = "工作委托设置删除", notes = "工作委托设置删除")
    @PostMapping("/deleteAll")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "工作委托设置UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        workflowDelegationSettiongsService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 工作委托生效
     *
     * @param uuid
     * @return
     */
    @ApiOperation(value = "工作委托生效", notes = "工作委托生效")
    @PostMapping("/delegationActive")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "工作委托设置UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> delegationActive(@RequestParam(name = "uuid", required = true) String uuid) {
        workflowDelegationSettiongsService.delegationActive(uuid);
        return ApiResult.success();
    }

    /**
     * 工作委托拒绝
     *
     * @param uuid
     * @return
     */
    @ApiOperation(value = "工作委托拒绝", notes = "工作委托拒绝")
    @PostMapping("/delegationRefuse")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "工作委托设置UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> delegationRefuse(@RequestParam(name = "uuid", required = true) String uuid) {
        workflowDelegationSettiongsService.delegationRefuse(uuid);
        return ApiResult.success();
    }

    /**
     * 保存常用工作委托设置
     *
     * @param commonDelegationSettingDto
     * @return
     */
    @ApiOperation(value = "保存常用工作委托设置", notes = "保存常用工作委托设置")
    @PostMapping("/saveCommon")
    public ApiResult<Void> saveCommon(@RequestBody WfCommonDelegationSettingDto commonDelegationSettingDto) {
        workflowDelegationSettiongsService.saveCommonBean(commonDelegationSettingDto);
        return ApiResult.success();
    }

    /**
     * 获取当前用户常用工作委托设置
     *
     * @return
     */
    @ApiOperation(value = "获取当前用户常用工作委托设置", notes = "获取当前用户常用工作委托设置")
    @PostMapping("/listCurrentUserCommon")
    public ApiResult<List<WfCommonDelegationSettingDto>> listCurrentUserCommon(@RequestBody PagingInfo pagingInfo) {
        List<WfCommonDelegationSettingDto> dtos = workflowDelegationSettiongsService.listCommonByUserId(SpringSecurityUtils.getCurrentUserId(), pagingInfo);
        return ApiResult.success(dtos);
    }

    /**
     * 删除常用工作委托设置
     *
     * @param uuid
     * @return
     */
    @ApiOperation(value = "删除常用工作委托设置", notes = "删除常用工作委托设置")
    @PostMapping("/deleteCommon")
    public ApiResult<Void> deleteCommon(@RequestParam(name = "uuid", required = true) Long uuid) {
        workflowDelegationSettiongsService.deleteCommon(uuid);
        return ApiResult.success();
    }

}

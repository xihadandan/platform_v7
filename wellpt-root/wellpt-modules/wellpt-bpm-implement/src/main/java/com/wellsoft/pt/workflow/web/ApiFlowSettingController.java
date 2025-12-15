/*
 * @(#)4/24/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dto.WfFlowSettingDto;
import com.wellsoft.pt.workflow.entity.WfFlowSettingEntity;
import com.wellsoft.pt.workflow.facade.service.WfFlowSettingFacadeService;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
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
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/24/24.1	zhulh		4/24/24		Create
 * </pre>
 * @date 4/24/24
 */
@Api(tags = "流程设置")
@Controller
@RequestMapping("/api/workflow/setting")
public class ApiFlowSettingController extends BaseController {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private WfFlowSettingFacadeService flowSettingFacadeService;

    /**
     * 初始系统流程设置
     *
     * @param system
     * @param tenant
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "初始系统流程设置", notes = "初始系统流程设置")
    public ApiResult<Void> init(@RequestParam(name = "system", required = true) String system,
                                @RequestParam(name = "tenant", required = true) String tenant) {
        flowSettingFacadeService.initBySystemAndTenant(system, tenant);
        return ApiResult.success();
    }

    /**
     * 获取流程设置
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程设置", notes = "获取流程设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程设置UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WfFlowSettingDto> get(@RequestParam(name = "uuid", required = true) Long uuid) {
        return ApiResult.success(flowSettingFacadeService.getDto(uuid));
    }

    /**
     * 获取流程设置
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getByKey", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程设置", notes = "获取流程设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "设置键", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WfFlowSettingDto> getByKey(@RequestParam(name = "key", required = true) String key) {
        return ApiResult.success(flowSettingFacadeService.getDtoByAttrKeyAndSystemAndTenant(key, RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId()));
    }

    /**
     * 保存流程设置
     *
     * @param dto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存流程设置", notes = "保存流程设置")
    public ApiResult<Void> save(@RequestBody WfFlowSettingDto dto) {
        flowSettingFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    /**
     * 批量保存流程设置
     *
     * @param dtos
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量保存流程设置", notes = "批量保存流程设置")
    public ApiResult<Void> saveAll(@RequestBody List<WfFlowSettingDto> dtos) {
        flowSettingFacadeService.saveDtos(dtos);
        return ApiResult.success();
    }

    /**
     * 根据系统及租户获取流程设置
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据系统及租户获取流程设置", notes = "根据系统及租户获取流程设置")
    public ApiResult<List<WfFlowSettingEntity>> listBySystemAndTenant() {
        String system = RequestSystemContextPathResolver.system();
        String tenant = SpringSecurityUtils.getCurrentTenantId();
        return ApiResult.success(flowSettingService.listBySystemAndTenant(system, tenant));
    }

    /**
     * 签署意见时添加附件
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/isEnabledOpinionFile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "签署意见时添加附件", notes = "签署意见时添加附件")
    public ApiResult<Boolean> isEnabledOpinionFile() {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        return ApiResult.success(workFlowSettings.isEnabledOpinionFile());
    }

}

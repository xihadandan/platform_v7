/*
 * @(#)12/20/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.google.common.collect.Lists;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.biz.dto.BizBusinessEntityLifecycleDto;
import com.wellsoft.pt.biz.dto.BizProcessInstanceDto;
import com.wellsoft.pt.biz.dto.BizProcessItemInstanceDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeInstanceDto;
import com.wellsoft.pt.biz.facade.service.BizBusinessEntityFacadeService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 业务主体相关接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/20/23.1	zhulh		12/20/23		Create
 * </pre>
 * @date 12/20/23
 */
@Api(tags = "业务实体")
@RestController
@RequestMapping("/api/biz/process/entity")
public class ApiBizBusinessEntityController extends BaseController {

    @Autowired
    private BizBusinessEntityFacadeService businessEntityFacadeService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * 获取办理过的业务流程定义列表
     *
     * @return
     */
    @GetMapping(value = "/listProcessDefinition", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程定义", notes = "获取业务流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "业务主体表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "entityId", value = "业务主体ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<ProcessDefinitionJson>> listProcessDefinition(@RequestParam("formUuid") String formUuid, @RequestParam("entityId") String entityId) {
        String formId = dyFormFacade.getFormIdByFormUuid(formUuid);
        return ApiResult.success(businessEntityFacadeService.listProcessDefinitionByEntityFormIdAndEntityId(formId, entityId));
    }

    /**
     * 获取办理过的业务流程实例列表
     *
     * @return
     */
    @GetMapping(value = "/listProcessInstance", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程实例", notes = "获取业务流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "业务主体表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "entityId", value = "业务主体ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<BizProcessInstanceDto>> listProcessInstance(@RequestParam("formUuid") String formUuid, @RequestParam("entityId") String entityId) {
        String formId = dyFormFacade.getFormIdByFormUuid(formUuid);
        return ApiResult.success(businessEntityFacadeService.listProcessInstanceByEntityFormIdAndEntityId(formId, entityId));
    }

    /**
     * 获取办理过的过程节点列表
     *
     * @return
     */
    @GetMapping(value = "/listProcessNodeInstanceByProcessDefId", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取办理过的过程节点列表", notes = "获取办理过的过程节点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefId", value = "业务流程定义ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "entityId", value = "业务主体ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<BizProcessNodeInstanceDto>> listProcessNodeInstanceByProcessDefId(@RequestParam("processDefId") String processDefId, @RequestParam("entityId") String entityId) {
        return ApiResult.success(businessEntityFacadeService.listProcessNodeInstanceByProcessDefIdAndEntityId(processDefId, entityId));
    }

    /**
     * 获取办理过的过程节点列表
     *
     * @return
     */
    @GetMapping(value = "/listProcessNodeInstanceByProcessInstUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取办理过的过程节点列表", notes = "获取办理过的过程节点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstUuid", value = "业务流程实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<BizProcessNodeInstanceDto>> listProcessNodeInstanceByProcessInstUuid(@RequestParam("processInstUuid") String processInstUuid) {
        return ApiResult.success(businessEntityFacadeService.listProcessNodeInstanceByProcessInstUuids(Lists.newArrayList(processInstUuid)));
    }

    /**
     * 获取办理过的业务事项列表
     *
     * @return
     */
    @GetMapping(value = "/listProcessItemInstanceByProcessNodeInstUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取过程节点下的业务事项办件", notes = "获取过程节点下的业务事项办件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processNodeInstUuid", value = "过程节点实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<BizProcessItemInstanceDto>> listProcessItemInstanceByProcessNodeInstUuid(@RequestParam("processNodeInstUuid") String processNodeInstUuid) {
        return ApiResult.success(businessEntityFacadeService.listProcessItemInstanceByProcessNodeInstUuid(processNodeInstUuid));
    }

    /**
     * 获取办理过的过程节点列表
     *
     * @return
     */
    @GetMapping(value = "/listProcessItemInstanceByProcessInstUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程下的业务事项办件", notes = "获取业务流程下的业务事项办件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstUuid", value = "业务流程实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<BizProcessItemInstanceDto>> listProcessItemInstanceByProcessInstUuid(@RequestParam("processInstUuid") String processInstUuid) {
        return ApiResult.success(businessEntityFacadeService.listProcessItemInstanceByProcessInstUuids(Lists.newArrayList(processInstUuid)));
    }

    /**
     * 获取业务主体生命周期数据
     *
     * @return
     */
    @GetMapping(value = "/getLifecycle", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务主体生命周期数据", notes = "获取业务主体生命周期数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "业务主体表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "entityId", value = "业务主体ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<BizBusinessEntityLifecycleDto> getLifecycle(@RequestParam("formUuid") String formUuid, @RequestParam("entityId") String entityId) {
        String formId = dyFormFacade.getFormIdByFormUuid(formUuid);
        return ApiResult.success(businessEntityFacadeService.getLifecycleByEntityFormIdAndEntityId(formId, entityId));
    }

}
